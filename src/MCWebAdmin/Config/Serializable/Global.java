package MCWebAdmin.Config.Serializable;

import java.io.Serializable;

import MCWebAdmin.Config.ConfigReader;

public class Global implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Global(){}
	
	// holds Admin Control Panel Port;
	public int AdminPort = 8080; // defaults to '8080'
	// holds Admin Control Panel Hostname
	public String AdminHostname = "*"; // defaults to '*' for all hostnames
	// holds the theme dir name
	public String AdminTheme = "default";
	
	// holds Minecraft Server Control Panel Port
	public int InstancePort = 8081; // defailts to '8081'
	// holds Minecraft Server Control Panel Hostname
	public String InstanceHostname = "*"; // defaults to '*' for all hostnames
	// holds the theme dir name
	public String InstanceTheme = "default";
	// holds the dir where all servers should be saved
	public String InstancesPath = "Instances/";
	// holds the backups dir
	public String InstanceBackupPath = "Instances/Backups/";
	
	public void SaveConfig(){
		ConfigReader.GetInstance().Write(this, "Global.cfg");
	}

	private static Global _inst;
	public static Global GetInstance() {
		boolean cfgExists = ConfigReader.GetInstance().ConfigExists("Global.cfg");
		if(_inst == null && !cfgExists){
			_inst = new Global();
		}else if(cfgExists){
			_inst = ConfigReader.GetInstance().Read(_inst, "Global.cfg");
		}
		return _inst;
	}
}
