package MCWebAdmin.Config.Serializable;

import java.io.Serializable;
import java.util.HashMap;

import MCWebAdmin.Config.ConfigReader;


public class Server implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// holds users and passwords
	public HashMap<String, String> Users;
	
	// holds the servers name
	public String name = "Default"; // Defaults to 'Default'
	
	// holds the path to copy files from
	public String serverOriginPath = "minecraft_1.8.3";
	
	// holds the name of the jar file to load the server from
	public String jarName = "minecraft_server.1.8.3.jar"; // Defaults to 'native.jar'
	
	// holds the directory to run the server from for unique users
	private String baseDir = ""; // Defaults to Global + serverName
	
	// holds the -Xms value for this server
	public String MemoryMin = "256M"; // Defaults to '256M'
	
	// holds the -Xmx value for this server
	public String MemoryMax = "512M"; // Defaults to '512M'
	
	// holds the path to the java path so some servers can use older versions of java
	public String JavaPath = "java"; // Defaults to Path java based java
	
	// holds the name of the theme to be loaded for this servers control panel
	public String ThemeName = "terminal"; // defaults to 'terminal'
	
	// holds the auto start information should this server start when MCWebAdmin starts?
	public boolean AutoStart = false;
	
	// holds if the server should auto restart on a crash
	public boolean AutoRestart = true;
	
	// holds the servers port
	public int Port = 52168;
	
	public String getBaseDir(){
		baseDir = Global.GetInstance().InstancesPath+name+"/"+serverOriginPath+"/";
		return baseDir;
	}
	
	private static HashMap<String,Server> _inst = new HashMap<>();
	public static Server GetServerInstance(String name)
	{
		Server eval = null;
		boolean cfgExists = ConfigReader.GetInstance().ConfigExists("Instances/"+name+".cfg");
		if(!_inst.containsKey(name) && !cfgExists){
			_inst.put(name, new Server());
		}else if(cfgExists){
			_inst.put(name, ConfigReader.GetInstance().Read(eval, "Instances/"+name+".cfg"));
		}
		return _inst.get(name);
	}
	
	public static void SaveAllConfigs()
	{
		for(Server srv : _inst.values())
		{
			srv.SaveConfig();
		}
	}
	
	public void SaveConfig()
	{
		ConfigReader.GetInstance().Write(this, "Instances/"+name+".cfg");
	}
	
	public void loadServerFiles(){
		
	}
	
	private Server(){
		baseDir = Global.GetInstance().InstancesPath+name+"/";
		if(Users == null){
			Users = new HashMap<>();
			// if this is a new server add admin support
			Users.put("Admin", "admin");
		}
	}
}
