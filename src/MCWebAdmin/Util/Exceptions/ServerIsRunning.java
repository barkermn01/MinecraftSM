package MCWebAdmin.Util.Exceptions;

public class ServerIsRunning extends Exception {
	
	public ServerIsRunning(String name){
		super("Server '"+name+"' is not runnning");
	}
}
