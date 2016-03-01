package MCWebAdmin.Config.Serializable;

import java.io.Serializable;
import java.util.HashMap;

import MCWebAdmin.Config.ConfigReader;
import MCWebAdmin.Util.Exceptions.ServerDoesNotExist;

public class Servers implements Serializable {

	
	// holds server name and the path to there config files
	private HashMap<String, String> Servers;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Servers(){		
		Servers = new HashMap<>();
	}
	
	public void SaveConfig(){
		ConfigReader.GetInstance().Write(this, "Servers.cfg");
	}

	private static Servers _inst;
	public static Servers GetInstance() {
		boolean cfgExists = ConfigReader.GetInstance().ConfigExists("Servers.cfg");
		if(_inst == null && !cfgExists){
			_inst = new Servers();
		}else if(cfgExists){
			_inst = ConfigReader.GetInstance().Read(_inst, "Servers.cfg");
		}
		return _inst;
	}
	
	public boolean ServerExists(String name){
		return Servers.containsKey(name);
	}
	
	public HashMap<String, String> GetServers(){
		return Servers;
	}
	
	public String getConfigFilePath(String name) throws ServerDoesNotExist{
		if(Servers.containsKey(name)){
			return Servers.get(name);
		}else{
			throw new ServerDoesNotExist();
		}
	}
	
	public void AddServer(String serverName)
	{
		String path = "Config/Servers/"+serverName+".cfg.bin";
		Servers.put(serverName, path);
	}
	
	public void RemoveServer(String serverName)
	{
		Servers.remove(serverName);
	}
}
