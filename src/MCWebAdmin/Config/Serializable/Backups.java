package MCWebAdmin.Config.Serializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import MCWebAdmin.Config.Backup;
import MCWebAdmin.Config.ConfigReader;

public class Backups implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> backups;
	private static Backups _inst;
	
	private Backups()
	{
		backups = new ArrayList<>();
	}
	
	public static Backups getInstance()
	{
		boolean cfgExists = ConfigReader.GetInstance().ConfigExists("Backups.cfg");
		if(_inst == null && !cfgExists){
			_inst = new Backups();
		}else if(cfgExists){
			_inst = ConfigReader.GetInstance().Read(_inst, "Backups.cfg");
		}
		return _inst;
	}
	
	public void SaveConfig(){
		ConfigReader.GetInstance().Write(this, "Backups.cfg");
	}
	
	public void AddBackup(Backup bk)
	{
		backups.add(bk.GetName());
	}
	
	public void RemoveBackup(Backup bk)
	{
		backups.remove(bk.GetName());
	}
	
	public ArrayList<String> GetBackups()
	{
		return backups;
	}
}
