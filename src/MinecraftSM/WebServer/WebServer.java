package MinecraftSM.WebServer;

import java.net.ServerSocket;

public abstract class WebServer extends Thread implements Runnable{
	protected boolean shutdown = false;
	protected ServerSocket server;
	
	@Override
	public abstract void run();
}
