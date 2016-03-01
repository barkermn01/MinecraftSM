package MCWebAdmin.Util.Exceptions;

public class UnableToReadFile extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnableToReadFile(String filePath){
		super("Unable to read file'"+filePath+"'");
	}
}
