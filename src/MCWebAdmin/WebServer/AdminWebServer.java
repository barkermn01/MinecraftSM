package MCWebAdmin.WebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import MCWebAdmin.Config.Serializable.Global;

public class AdminWebServer extends WebServer {
	private static AdminWebServer _inst;
	
	public static AdminWebServer GetInstance(){
		if(_inst == null){
			_inst = new AdminWebServer();
		}
		return _inst;
	}
	
	@Override
	public void run() {
		try {
			server = new ServerSocket(Global.GetInstance().AdminPort);
			while(!this.shutdown){
				// TODO Auto-generated method stub
				try {
					Socket socket = server.accept();
					AdminWebWorker worker = new AdminWebWorker(socket);
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
