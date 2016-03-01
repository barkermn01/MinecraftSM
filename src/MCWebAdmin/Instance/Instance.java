package MCWebAdmin.Instance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import MCWebAdmin.Config.Serializable.Server;

public class Instance extends Thread {
	private Process proc;
	private ArrayList<String> players;
	private int StreamStart = -1; 
	public int StreamCurrnt = 0;
	private String[] outputStream;
	private String server;
	private BufferedWriter out;
	private BufferedReader in;
	private boolean shuttingDown = false;
	
	public Instance(Server serv){
		server = serv.name;
		players = new ArrayList<>();
		outputStream = new String[512];
		if(Server.GetServerInstance(server).AutoStart){
			super.start();
		}
	}
	
	public void run(){		
		Start();
	}
		
	public void Start()
	{
		try{
			ProcessBuilder procBuild = new ProcessBuilder(new String[] { 
				Server.GetServerInstance(server).JavaPath, 
				"-Xmx" + Server.GetServerInstance(server).MemoryMax, 
				"-Xms" + Server.GetServerInstance(server).MemoryMin, 
				"-jar",
				Server.GetServerInstance(server).jarName,
				"nogui", 
				"-nojline", 
				"2>&1" 
			});
			procBuild.directory(new File(Server.GetServerInstance(server).getBaseDir()));
			procBuild.redirectErrorStream(true);
			this.proc = procBuild.start();
	
			in = new BufferedReader(new InputStreamReader(this.proc.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(this.proc.getOutputStream()));
			String line = "";
			while ((line = in.readLine()) != null && !shuttingDown)
			{
				AddToStream(line);
			}
			if(line == null && !shuttingDown && Server.GetServerInstance(server).AutoRestart){
				InstanceManager.GetInstance().RestartInstance(Server.GetServerInstance(server).name);
			}
		}catch(Exception e)
		{
			
		}
	}
	
	private void CheckForPlayerJoined(String line, String[] parts)
	{
		for(int i = 0; i < parts.length; i++){
			if("joined".equals(parts[i])){
				int startLoc = line.indexOf(parts[i-1]);
				int endLoc = line.indexOf(parts[i]);
				players.add(line.substring(startLoc, endLoc).trim());
			}
		}
	}
	
	private void CheckForPlayerLeft(String line, String[] parts)
	{
		for(int i = 0; i < parts.length; i++){
			if("left".equals(parts[i])){
				int startLoc = line.indexOf(parts[i-1]);
				int endLoc = line.indexOf(parts[i]);
				players.remove(line.substring(startLoc, endLoc).trim());
			}
		}
	}
	
	private void AddToStream(String line)
	{
		String[] parts = line.split(" ");
		CheckForPlayerJoined(line, parts);
		CheckForPlayerLeft(line, parts);
		if(StreamCurrnt < outputStream.length)
		{
			outputStream[StreamCurrnt] = line;
		}else{
			StreamStart++;
			if(StreamStart > outputStream.length){
				StreamStart = 0;
			}
			outputStream[StreamStart] = line;
		} 
		StreamCurrnt++;
	}
	
	public String[] GetStream()
	{
		String[] ret = new String[outputStream.length];
		int max = StreamCurrnt;
		for(int i = 0; i < max; i++)
		{
			if(StreamStart < 0)
			{
				ret[i] = outputStream[i];
			}else{
				int textPos = i + StreamStart;
				if(textPos > outputStream.length) textPos -= outputStream.length;
				ret[i] = outputStream[textPos]; 
			}
		}
		return ret;
	}
	
	public String[] GetPlayers()
	{
		String[] plays = new String[players.size()];
		plays = players.toArray(plays);
		return plays;
	}
	
	public void Stop()
	{
		shuttingDown = true;
		if(proc != null){
			proc.destroy();
			System.out.println("Stopping instance: "+Server.GetServerInstance(server).name);
		}
	}
	
	public boolean isRunning()
	{
		if(proc != null){
			return proc.isAlive();
		}
		return false;
	}
	
	public void Restart()
	{
		proc.destroy();
		start();
	}
	
	public void sendInput(String input)
	{
		try
		{
			input = input.trim() + "\r\n";
			out.write(input, 0, input.length());
			out.flush();
		}
		catch (Exception ex) {}
	}
	
	public void ForceStop()
	{
		if(proc != null && proc.isAlive()){
			proc.destroyForcibly();
		}
	}
}
