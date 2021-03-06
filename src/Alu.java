import java.util.ArrayList;
/**
 * The <code>Alu</code> class implements the arithmetic logic unit of a RAM machine.
 * 
 * 
 * @author Isaac Aimán
 */
public class Alu {
	
	private Integer ip;
	private Tape tapeIn;
	private Tape tapeOut;
	private ProgramMemory programMemory;
	private DataMemory dataMemory;
	private ArrayList<Tag> tagList;
	private Integer numberInstructionsExecuted;
	private Boolean debug;
	
	/**
	 *Initializes a newly created <code>Alu</code> object that represents a RAM Machine's ALU.
	 * 
	 */
	public Alu(){

		ip = 0;
		tapeIn = new Tape();
		tapeOut = new Tape();
		programMemory = new ProgramMemory();
		dataMemory = new DataMemory();
		tagList = new ArrayList<Tag>();
		debug = new Boolean(false); 
		
	}
	/**
	 *Constructs a new <code>Alu</code> given all the specified components.
	 * 
	 * @param tapeIn input tape.
	 * @param tapeOut output tape.
	 * @param programMemory program memory used by the ALU.
	 * @param dataMemory data memory used by the ALU.
	 * @param tagList contains all the tags of the current program.
	 * @param debug specifies the debug mode if it's <code>true</code>, otherwise debug mode will be disabled.
	 * 
	 */
	public Alu(Tape tapeIn, Tape tapeOut, ProgramMemory programMemory, DataMemory dataMemory, ArrayList<Tag> tagList, Boolean debug){

		ip = 0;
		this.tapeIn = tapeIn;
		this.tapeOut = tapeOut;
		this.programMemory = programMemory;
		this.dataMemory = dataMemory;
		this.tagList = tagList;
		this.debug = debug;
		
	}
	
	/**
	 * Initializes a newly created <code>Alu</code> object that represents 
	 * a copy of the given <code>Alu</code> object.
	 * 
	 */
	public Alu(Alu other){
		
		ip = other.getIp();
		this.tapeIn = other.getTapeIn();
		this.tapeOut = other.getTapeOut();
		this.programMemory = other.getProgramMemory();
		this.dataMemory = other.getDataMemory();
		this.tagList = other.getTagList();
		this.debug = other.getDebug();
		
	}
	
	/**
	 * Starts executing all the instructions of the data program.
	 * 
	 */
	public void start() throws Exception{
		
		Boolean boolHalt = false;
		numberInstructionsExecuted = new Integer(0);
		
		while(getIp()<programMemory.size() && !boolHalt) {
			setIp(getIp() + 1);
			switch(programMemory.get(getIp() - 1).getName().toLowerCase()){
				case "read":
					read(programMemory.get(getIp() - 1).getArgument());
					break;
				case "load":	
					load(programMemory.get(getIp() - 1).getArgument());
					break;
				case "store":
					store(programMemory.get(getIp() - 1).getArgument());
					break;
				case "jzero": 
					jzero(programMemory.get(getIp() - 1).getArgument());
					break;
				case "write":
					write(programMemory.get(getIp() - 1).getArgument());	
					break;
				case "add":
					add(programMemory.get(getIp() - 1).getArgument());
					break;
				case "sub":
					sub(programMemory.get(getIp() - 1).getArgument());
					break;
				case "mul":
					mul(programMemory.get(getIp() - 1).getArgument());
					break;
				case "div":
					div(programMemory.get(getIp() - 1).getArgument());
					break;
				case "jgtz":
					jgtz(programMemory.get(getIp() - 1).getArgument());
					break;
				case "jump":
					jump(programMemory.get(getIp() - 1).getArgument());
					break;
				case "halt":
					boolHalt = true;
					break;
				default:
					throw new Exception("Error " + mensajeError());
				}
			
			setNumberInstructionsExecuted(numberInstructionsExecuted + 1);
			
			if (debug){
				debugMode();
			}
				
			
			
		}
	}
	
	public void read(String argument) throws Exception{
		
		if(getType(argument).equals("Constante")){
			throw new Exception("Error. Constante no permitida " + mensajeError());
		}
		
		getDataMemory().add(getRegisterNumber(argument), getTapeIn().read());
	}
	
	public void load(String argument) throws Exception{
		
		
		getDataMemory().add(0, getValue(argument));
		
		
	}
	
	public void jzero(String argument) throws Exception{
		
		Boolean found = true;
		if (getDataMemory().getValue(0).equals(new Integer(0))){
			found = false;
			for (int i = 0; i<tagList.size() && !found; i++){
				if(tagList.get(i).getTagName().startsWith(argument)){
					found = true;
					setIp(tagList.get(i).getInstructionNumber());
				}
			}
		}
		
		if (!found){
			throw new Exception("Etiqueta no encontrada " + mensajeError());
		}
	}
	
	public void write(String argument) throws Exception{

		if(!getType(argument).equals("Constante")){
			
			if (getRegisterNumber(argument).equals(0)){
				throw new Exception("Intentando escribir en R0 " + mensajeError());
			}
			
		}

		
		getTapeOut().add(getValue(argument));


	}
	
	public void jump(String argument) throws Exception{
		
		Boolean found = false;
		for (int i = 0; i<tagList.size() && !found; i++){

			if(tagList.get(i).getTagName().startsWith(argument)){
				found = true;
				setIp(tagList.get(i).getInstructionNumber());
			}
		}
		
		if (!found){
			throw new Exception("Etiqueta no encontrada " + mensajeError());
		}
	}
	
	public void store(String argument) throws Exception{
		
		if(getType(argument).equals("Constante")){
			throw new Exception("Error. Constante no permitida " + mensajeError());
		}
		
		getDataMemory().add(getRegisterNumber(argument), getDataMemory().getValue(0));

		
	}
	
	public void add(String argument) throws Exception{
		
		getDataMemory().add(0, getDataMemory().getValue(0) + getValue(argument));
	}
	
	public void sub(String argument) throws Exception{
		
		getDataMemory().add(0, getDataMemory().getValue(0) - getValue(argument));	
		
	}
	
	public void mul(String argument) throws Exception{
		
		getDataMemory().add(0, getDataMemory().getValue(0) * getValue(argument));	
		
	}
	
	public void div(String argument) throws Exception{
		
		getDataMemory().add(0, getDataMemory().getValue(0) / getValue(argument));	
		
	}
	
	public void jgtz(String argument) throws Exception{
		
		Boolean found = true;
		if (getDataMemory().getValue(0).intValue() > 0){
			found = false;
			for (int i = 0; i<tagList.size() && !found; i++){
				if(tagList.get(i).getTagName().startsWith(argument)){
					found = true;
					setIp(tagList.get(i).getInstructionNumber());
				}
			}
		}
		
		if (!found){
			throw new Exception("Etiqueta no encontrada " + mensajeError());
		}
		
	}
	
	public Integer getValue(String argument) throws Exception{
		if(argument.startsWith("=")){
			try{
				new Integer(argument.replaceFirst("=", ""));
			}
			catch(NumberFormatException e){
				throw new Exception("Error de formato del operando " + mensajeError());
	 
			}
			return new Integer(argument.replaceFirst("=", ""));
		}
		else{
			if(argument.startsWith("*")){
				try{
					new Integer(argument.replaceFirst("\\*", ""));
				}
				catch(NumberFormatException e){
					throw new Exception("Error de formato del operando " + mensajeError());
		 
				}
				
				return getDataMemory().getValue(getDataMemory().getValue(
						new Integer(argument.replaceFirst("\\*", ""))));
			}
		}
		
		try{
			new Integer(argument);
		}
		catch(NumberFormatException e){
			throw new Exception("Error de formato del operando " + mensajeError());
 
		}
		
		return (getDataMemory().getValue(new Integer(argument)));
		
		
	}

	public Integer getRegisterNumber(String argument) throws Exception{

		if(argument.startsWith("*")){
			try{
				new Integer(argument.replaceFirst("\\*", ""));
			}
			catch(NumberFormatException e){
				throw new Exception("Error de formato del operando " + mensajeError());
	 
			}
			
			return getDataMemory().getValue(new Integer(argument.replaceFirst("\\*", "")));
		}
		
		try{
			new Integer(argument);
		}
		catch(NumberFormatException e){
			throw new Exception("Error de formato del operando " + mensajeError());
 
		}
		return (new Integer(argument));
		
		
		
	}
	
	public String getType(String argument){
		
		if(argument.startsWith("=")){
			return "Constante";
		}
		
		if(argument.startsWith("*")){
			return "Direccionamiento indirecto";
		}
		
		return "Direccionamiento directo";
	}
	
	public Integer getIp() {
		return ip;
	}

	public void setIp(Integer ip) {
		this.ip = ip;
	}

	public Tape getTapeIn() {
		return tapeIn;
	}

	public void setTapeIn(Tape tapeIn) {
		this.tapeIn = tapeIn;
	}

	public Tape getTapeOut() {
		return tapeOut;
	}

	public void setTapeOut(Tape tapeOut) {
		this.tapeOut = tapeOut;
	}

	public ProgramMemory getProgramMemory() {
		return programMemory;
	}

	public void setProgramMemory(ProgramMemory programMemory) {
		this.programMemory = programMemory;
	}

	public DataMemory getDataMemory() {
		return dataMemory;
	}

	public void setDataMemory(DataMemory dataMemory) {
		this.dataMemory = dataMemory;
	}

	public ArrayList<Tag> getTagList() {
		return tagList;
	}

	public void setTagList(ArrayList<Tag> tagList) {
		this.tagList = tagList;
	}
	
	public String mensajeError(){
		return (("en la instrucción: '" + programMemory.get(getIp() - 1).getName() +
				"' de la línea: " + programMemory.get(getIp() - 1).getLine()));
	}

	
	public Integer getNumberInstructionsExecuted() {
		return numberInstructionsExecuted;
	}

	
	public void setNumberInstructionsExecuted(Integer numberInstructionsExecuted) {
		this.numberInstructionsExecuted = numberInstructionsExecuted;
	}

	
	public Boolean getDebug() {
		return debug;
	}

	
	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

	public void debugMode(){
		
		System.out.println("=============================================");
		System.out.println("Memoria de datos:" + "\n" + getDataMemory());
		System.out.println("Memoria de programa:" + "\n" + getProgramMemory());
		System.out.println("IP: " + getIp());
		System.out.println("Cinta de entrada: " + getTapeIn());
		System.out.println("Cinta de salida: " + getTapeOut());
		System.out.println("Número de instrucciones ejecutadas: " + getNumberInstructionsExecuted());
		System.out.println("=============================================\n\n");

	}

}
