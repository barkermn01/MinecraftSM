package MCWebAdmin.WebServer;

import java.net.ServerSocket;

public abstract class WebServer extends Thread implements Runnable{
	protected boolean shutdown = false;
	protected ServerSocket server;
	
	public abstract void run();
}
