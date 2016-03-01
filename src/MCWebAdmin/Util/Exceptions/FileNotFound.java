package MCWebAdmin.Util.Exceptions;

public class FileNotFound extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileNotFound(String filePath){
		super("Could not find '"+filePath+"'");
	}
}
