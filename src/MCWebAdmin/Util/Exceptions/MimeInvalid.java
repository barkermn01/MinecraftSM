package MCWebAdmin.Util.Exceptions;

public class MimeInvalid extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MimeInvalid(String ext){
		super("the file extension '"+ext+"' is invalid it must start with a '.'");
	}
}
