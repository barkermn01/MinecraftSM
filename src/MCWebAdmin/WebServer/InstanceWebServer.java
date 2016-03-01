package MCWebAdmin.WebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import MCWebAdmin.Config.Serializable.Global;

public class InstanceWebServer extends WebServer {
	private static InstanceWebServer _inst;
	
	public static InstanceWebServer GetInstance(){
		if(_inst == null){
			_inst = new InstanceWebServer();
		}
		return _inst;
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(Global.GetInstance().InstancePort);
			while(!this.shutdown){
				// TODO Auto-generated method stub
				try {
					Socket socket = server.accept();
					InstanceWebWorker worker = new InstanceWebWorker(socket);
					worker.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
