package MCWebAdmin.WebServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

import MCWebAdmin.Config.Serializable.Global;
import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Instance.InstanceManager;
import MCWebAdmin.Util.FileReader;
import MCWebAdmin.Util.SupportedMimeTypes;
import MCWebAdmin.Util.Exceptions.FileNotFound;

public class InstanceWebWorker extends Thread implements Runnable {
	private Socket soc;
	private BufferedOutputStream bos;
	private BufferedReader br;
	private ServerRequest sr;
	private String basePath = "Instance_Web/"+Global.GetInstance().InstanceTheme + "/";
	
	public InstanceWebWorker(Socket socket) {
		soc = socket;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bos = new BufferedOutputStream(socket.getOutputStream());
			sr = new ServerRequest();
			sr.parseInput(br);
			
			// are we handling a JSON RPC Request
			if("/rpc/".equals(sr.requestPath) || "rpc".equals(sr.requestPage) && "/".equals(sr.requestPath)){
				WriteLine("HTTP/1.0 200");
				WriteLine("Server: MCWebAdmin/3.0.0");
				WriteLine("Content-Type: application/json");
				WriteLine("");
				WriteLine(sr.rawPost);
				bos.flush();
				bos.close();
				soc.close();
				return;
			}else if("/stream/".equals(sr.requestPath))
			{
				if(sr.get.containsKey("instance")){
					String inst = sr.get.get("instance");
					WriteLine("HTTP/1.0 200");
					WriteLine("Server: MCWebAdmin/3.0.0");
					WriteLine("Content-Type: text/plain");
					WriteLine("");
					String[] stream = InstanceManager.GetInstance().GetStream(inst);
					for(String s : stream)
					{
						if(s != null)
							WriteLine(s);
					}
					bos.flush();
					bos.close();
					soc.close();
				}else{
					show404();
				}
				
			}
			else{
				String path = basePath+sr.requestPath+sr.requestPage;
				File f = new File(basePath+sr.requestPath+sr.requestPage);
				if(f.isDirectory()){
					sr.requestPath += "index.html";
				}
				String fileExt;
				if(sr.requestPage.lastIndexOf('.') > 0){
					 fileExt = sr.requestPage.substring(sr.requestPage.lastIndexOf('.'), sr.requestPage.length());
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
					show404();
					return;
				}
				catch(Exception e){
					//show500Error();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					show500DebugError(sw.toString());//getStackTrace(e));
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
	
	private void show500DebugError(String err){
		WriteLine("HTTP/1.0 500");
		WriteLine("Content-Type: text/html");
		WriteLine("");
		WriteLine("<!DOCTYPE html><html><body><h1>500 Internal Server Error</h1><!--"+err+"--></body></html>");
		try {
			bos.flush();
			bos.close();
			bos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void show404()
	{
		WriteLine("HTTP/1.0 404");
		WriteLine("Content-Type: text/html");
		WriteLine("");
		WriteLine("<!DOCTYPE html><html><body><h1>404 File Not Found</h1></body></html>");
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
	
	public void run(){
		
	}
}
