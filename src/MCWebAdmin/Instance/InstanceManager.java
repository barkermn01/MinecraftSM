package MCWebAdmin.Instance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import MCWebAdmin.Config.Backup;
import MCWebAdmin.Config.Serializable.Backups;
import MCWebAdmin.Config.Serializable.Global;
import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Config.Serializable.Servers;
import MCWebAdmin.Util.Exceptions.PortInUse;
import MCWebAdmin.Util.Exceptions.ServerDoesNotExist;
import MCWebAdmin.Util.Exceptions.ServerIsNotRunning;
import MCWebAdmin.Util.Exceptions.ServerIsRunning;
import MCWebAdmin.Util.Exceptions.ServerNameInUse;
import MCWebAdmin.WebServer.AdminWebServer;

public class InstanceManager {
	private Map<String, Instance> servers;
	private static InstanceManager _inst;
	
	public static InstanceManager GetInstance()
	{
		if(_inst == null){
			_inst = new InstanceManager();
		}
		return _inst;
	}
	
	private InstanceManager()
	{
		servers = new HashMap<>();
		// load all servers
		if(Servers.GetInstance().GetServers().size() > 0){
			for(String srvKey : Servers.GetInstance().GetServers().keySet()){
				servers.put(srvKey, new Instance(Server.GetServerInstance(srvKey)));
			}
		}
	}
	
	private void InstallFiles(String name){
		File copyFrom = new File("servers/"+Server.GetServerInstance(name).serverOriginPath+"/");
		File copyTo = new File(Global.GetInstance().InstancesPath+Server.GetServerInstance(name).name+"/");
		if(!copyTo.exists()){
			copyTo.getParentFile().mkdirs();
		}
		try {
			Properties properties = new Properties();
			Integer port = Server.GetServerInstance(name).Port;
			FileUtils.copyDirectoryToDirectory(copyFrom, copyTo);
			properties.load(new FileInputStream(Server.GetServerInstance(name).getBaseDir()+"server.properties"));
			properties.setProperty("server-port", port.toString());
			properties.store(new FileOutputStream(Server.GetServerInstance(name).getBaseDir()+"server.properties"), null);
		} catch (IOException e) {
		}
	}
	
	private boolean CheckForInstall(String name){
		File installDir = new File(
			Global.GetInstance().InstancesPath+
			Server.GetServerInstance(name).name+
			"/"+
			Server.GetServerInstance(name).serverOriginPath+
			"/"
			);
		return installDir.exists();
	}
	
	public void UninstallFiles(String name)throws ServerDoesNotExist, ServerIsRunning
	{
		if(!Servers.GetInstance().ServerExists(name)){
			throw new ServerDoesNotExist();
		}
		if(servers.get(name).isRunning()){
			throw new ServerIsRunning(name);
		}
		File installDir = new File(Global.GetInstance().InstancesPath+Server.GetServerInstance(name).name+"/");
		installDir.delete();
	}
	
	public void ReinstallInstance(String name) throws ServerDoesNotExist, ServerIsRunning
	{
		if(!Servers.GetInstance().ServerExists(name)){
			throw new ServerDoesNotExist();
		}
		if(CheckForInstall(name)){
			UninstallFiles(name);
		}
		InstallFiles(name);
	}
	
	public void CreateInstance(String name) throws ServerNameInUse
	{
		if(Servers.GetInstance().ServerExists(name)){
			throw new ServerNameInUse(name);
		}
		Server.GetServerInstance(name).name = name;
		Servers.GetInstance().AddServer(name);
		servers.put(name, new Instance(Server.GetServerInstance(name)));

		if(!CheckForInstall(name)){
			InstallFiles(name);
		}
	}
	
	public void RemoveInstance(String name) throws ServerDoesNotExist, ServerIsRunning
	{
		if(!servers.containsKey(name)){
			throw new ServerDoesNotExist();
		}
		if(servers.get(name).isRunning()){
			throw new ServerIsRunning(name);
		}
		Servers.GetInstance().RemoveServer(name);
	}
	
	@SuppressWarnings("deprecation")
	public void StopInstance(String name) throws ServerDoesNotExist
	{
		if(!servers.containsKey(name)){
			throw new ServerDoesNotExist();
		}
		servers.get(name).Stop();
		servers.remove(name);
		servers.put(name, new Instance(Server.GetServerInstance(name)));		
	}
	
	public void StartInstance(String name) throws ServerDoesNotExist, ServerIsRunning
	{
		if(!servers.containsKey(name)){
			throw new ServerDoesNotExist();
		}
		if(servers.get(name).isRunning() || servers.get(name).isAlive()){
			throw new ServerIsRunning(name);
		}
		servers.get(name).start();
		
	}
	
	public void StopAllInstances()
	{
		for(Instance i : servers.values())
		{
			i.Stop();
		}
	}
	
	public void ForceStopAllInstances()
	{
		for(Instance i : servers.values())
		{
			i.ForceStop();
		}
	}
	
	public void RestartInstance(String name) throws ServerDoesNotExist
	{
		if(!servers.containsKey(name)){
			throw new ServerDoesNotExist();
		}
		if(servers.get(name).isRunning()){
			StopInstance(name);
		}
		try {
			StartInstance(name);
		} catch (ServerIsRunning e) {
		}
	}
	
	public boolean isInstanceRunning(String name)
	{
		return servers.get(name).isRunning();
	}
	
	public void SendInstanceNotice(String name, String notice) throws ServerDoesNotExist, ServerIsNotRunning
	{
		if(!servers.containsKey(name)){
			throw new ServerDoesNotExist();
		}
		if(!servers.get(name).isRunning()){
			throw new ServerIsNotRunning(name);
		}
		servers.get(name).sendInput("say [Instance] "+notice.trim()+"\r\n");
	}
	
	public void SendWideNotice(String notice)
	{
		for(Instance inst : servers.values())
		{
			if(inst.isRunning()){
				inst.sendInput("say [Server] "+notice);
			}
		}
	}
	
	public void BackupInstance(String name, String BackupName) throws ServerDoesNotExist
	{
		Backup bk = new Backup(name, BackupName);
		bk.Create();
	}
	
	public void RestoreInstance(String backupName) throws ServerDoesNotExist
	{
		Backup bk = new Backup(backupName);
		bk.Restore();
	}
	
	public void DeleteBackup(String backupName) throws ServerDoesNotExist
	{
		Backup bk = new Backup(backupName);
		bk.Delete();
	}
	
	public String[] GetInstancePlayers(String name)
	{
		return servers.get(name).GetPlayers();
	}
	
	public String[] GetStream(String name)
	{
		return servers.get(name).GetStream();
	}
	
	public int GetStreamCount(String name)
	{
		return servers.get(name).StreamCurrnt;
	}
	
	public boolean doesServerExsit(String name)
	{
		return servers.containsKey(name);
	}


	public void ChangePort(String name, Integer port) throws PortInUse {
		if(!CheckPortIsFree(port)){
			throw new PortInUse(port);
		}
		Server.GetServerInstance(name).Port = port;
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(Server.GetServerInstance(name).getBaseDir()+"server.properties"));
			properties.setProperty("server-port", port.toString());
			properties.store(new FileOutputStream(Server.GetServerInstance(name).getBaseDir()+"server.properties"), null);
		} catch (IOException e) {
		}
	}
	
	public boolean CheckPortIsFree(int port){
		if(Global.GetInstance().AdminPort == port){
			return false;
		}
		if(Global.GetInstance().InstancePort == port){
			return false;
		}
		
		for(String name : servers.keySet()){
			if(Server.GetServerInstance(name).Port == port){
				return false;
			}
		}
		return true;
	}
}
