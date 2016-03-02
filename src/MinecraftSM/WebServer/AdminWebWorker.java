package MinecraftSM.WebServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import MinecraftSM.Config.Serializable.Global;
import MinecraftSM.Util.FileReader;
import MinecraftSM.Util.SupportedMimeTypes;
import MinecraftSM.Util.Exceptions.FileNotFound;

public class AdminWebWorker extends Thread implements Runnable {
	private Socket soc;
	private BufferedOutputStream bos;
	private BufferedReader br;
	private ServerRequest sr;
	private String basePath = "Admin_Web/"+Global.GetInstance().AdminTheme;
	
	public AdminWebWorker(Socket socket) {
		soc = socket;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bos = new BufferedOutputStream(socket.getOutputStream());
			sr = new ServerRequest();
			sr.parseInput(br);
			
			// are we handling a JSON RPC Request
			if("/rpc/".equals(sr.requestPath) || "rpc".equals(sr.requestPage) && "/".equals(sr.requestPath)){
				WriteLine("HTTP/1.0 200");
				WriteLine("Server: MinecraftSM/3.0.0");
				WriteLine("Content-Type: application/json");
				WriteLine("");
				WriteLine(sr.rawPost);
				bos.flush();
				bos.close();
				soc.close();
				return;
			}else{
				String path = basePath+sr.requestPath+sr.requestPage;
				
				String fileExt;
				if(sr.requestPage.lastIndexOf('.') > 0){
					int extAt = sr.requestPage.lastIndexOf('.');
					int length = sr.requestPage.length();
					fileExt = sr.requestPage.substring(extAt, length);
				}else{
					fileExt = ""; 
				}
				String mimeType = SupportedMimeTypes.GetInstance().getMimeType(fileExt);
				try{
					byte[] read = FileReader.GetInstance().readFile(path);
					WriteLine("HTTP/1.0 200");
					WriteLine("Content-Type: " + mimeType);
					WriteLine("");
					bos.write(read, 0, read.length);
					bos.flush();
					bos.close();
					soc.close();
					return;
				}
				catch(FileNotFound fnf){
					WriteLine("HTTP/1.0 404");
					WriteLine("Content-Type: text/html");
					WriteLine("");
					WriteLine("<!DOCTYPE html><html><body><h1>404 File Not Found</h1></body></html>");
					bos.flush();
					bos.close();
					soc.close();
					return;
				}
				catch(Exception e){
					show500Error();
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void show500Error(){
		WriteLine("HTTP/1.0 500");
		WriteLine("Content-Type: text/html");
		WriteLine("");
		WriteLine("<!DOCTYPE html><html><body><h1>500 Internal Server Error</h1></body></html>");
		try {
			bos.flush();
			bos.close();
			bos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void WriteLine(String s)
	{
		byte[] bytes = (s + "\r\n").getBytes();
		try
		{
			bos.write(bytes, 0, bytes.length);
		}
		catch (IOException ex) {}
	}
	
	@Override
	public void run(){
		
	}
}
