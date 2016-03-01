package MCWebAdmin.Util.Exceptions;

public class ServerIsNotRunning extends Exception {
	
	public ServerIsNotRunning(String name){
		super("Server '"+name+"' is runnning");
	}
}
