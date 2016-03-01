package MCWebAdmin.Util.Exceptions;

public class ServerNameInUse extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		public ServerNameInUse(String name){
			super("The server name '"+name+"' is used");
		}
}
