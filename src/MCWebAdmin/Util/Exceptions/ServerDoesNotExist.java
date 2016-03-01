package MCWebAdmin.Util.Exceptions;

public class ServerDoesNotExist extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServerDoesNotExist() {
        super("There is no server by that name saved");
    }
}
