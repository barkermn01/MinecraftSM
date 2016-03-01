package MCWebAdmin.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import MCWebAdmin.Config.Serializable.Backups;
import MCWebAdmin.Config.Serializable.Global;
import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Config.Serializable.Servers;
import MCWebAdmin.Instance.InstanceManager;
import MCWebAdmin.Util.Exceptions.PortInUse;
import MCWebAdmin.Util.Exceptions.ServerDoesNotExist;
import MCWebAdmin.Util.Exceptions.ServerIsRunning;
import MCWebAdmin.Util.Exceptions.ServerNameInUse;
import MCWebAdmin.WebServer.AdminWebServer;
import MCWebAdmin.WebServer.InstanceWebServer;

public class CommandHelper {
	private BufferedReader consoleIn;
	public CommandHelper(BufferedReader in)
	{
		consoleIn = in;
	}
	
	private boolean confirm(){
		try{
			boolean test = false;
			String sure = consoleIn.readLine();
			if(sure.toLowerCase().equals("y") || sure.toLowerCase().equals("n"))
			{
				test = true;
			}
			while(!test){
				System.out.print("Response can only be [Y/N]:");
				sure = consoleIn.readLine();
				if(sure.toLowerCase().equals("y") || sure.toLowerCase().equals("n"))
				{
					test = true;
				}
			}
			return test;
		}catch(IOException e){}
		return false;
	}
	
	public boolean exit(){
		System.out.print("Are you sure you want to exit settings are not saved automaticly [Y/N]:");
		return confirm();
	}
	
	public void help()
	{
		System.out.println("Help text - this is the list of commands you can use!  ");
		System.out.println("Shutdown|Exit		- Stops this server running");
		System.out.println("SaveConfig|save		- Forces a save of all config files");
		System.out.println("AdminPort		- Change your admin web access port");
		System.out.println("InstancePort		- Change the instance web access port");
		System.out.println("Create			- Create a new instance of minecraft server");
		System.out.println("Start			- Start a created instance");
		System.out.println("Stop			- Stop a started instance");
		System.out.println("Restart			- Restart a started instance");
		System.out.println("Delete			- Delete a created instance");
		System.out.println("Backup			- Create a backup of an instance");
		System.out.println("ListBackups		- List all the backups saved");
		System.out.println("ListInstances		- Lists all the instances current created");
		System.out.println("Restore			- Restores a instance from a backup");
		System.out.println("Instance			- Allows Instance Commands");
		System.out.println("Help			- Shows this help information");
	}
	
	public void restore()
	{
		System.out.print("Please enter backup filename: ");
		try {
			String bkName = consoleIn.readLine();
			System.out.print("Are you sure you want to restore this will stop instance[Y/N]: ");
			if(confirm()){
				InstanceManager.GetInstance().RestoreInstance(bkName);
			}
		} catch (NumberFormatException e) {
			System.out.println("Port number is invalid");
		} catch (ServerDoesNotExist e) {
			System.out.println("This backup was made for a server that no longer exists");
		} catch (IOException e) {
		}		
	}
	
	public void listInstances()
	{

		System.out.println("List of current instances:");
		Set<String> servers = Servers.GetInstance().GetServers().keySet();
		for(String s : servers)
		{
			System.out.println(s);
		}
		System.out.println("List completed. Total of "+servers.size()+" servers");
	}
	
	public void listBackups()
	{

		System.out.println("List of current backups:");
		System.out.println("Backups are named as ServerName_BackupName_DateOfBackup");
		ArrayList<String> data = Backups.getInstance().GetBackups();
		for(String bk : data){
			System.out.println(bk);
		}
		System.out.println("List completed. Total of "+data.size()+" backups");
	}
	
	public void backup()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			System.out.print("Please enter backup name: ");
			String bk = consoleIn.readLine();
			InstanceManager.GetInstance().BackupInstance(name, bk);
		} catch (IOException e) {
		} catch (ServerDoesNotExist e) {
			System.out.println("Instance does not exist");
		}
	}
	
	public void delete()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			System.out.print("Are you sure you want to delete this instance[Y/N]: ");
			if(confirm()){
				InstanceManager.GetInstance().RemoveInstance(name);
			}
		} catch (IOException e) {
		} catch (ServerDoesNotExist e) {
			System.out.println("Instance does not exist");
		} catch (ServerIsRunning e) {
			System.out.println("Instance is currently running");
		}
	}
	
	public void restart()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();System.out.print("Are you sure you want to restart this instance[Y/N]: ");
			if(confirm()){
				InstanceManager.GetInstance().RestartInstance(name);
			}
		} catch (IOException e) {
		} catch (ServerDoesNotExist e) {
			System.out.println("Instance does not exist");
		}
	}
	
	public void stop()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			System.out.print("Are you sure you want to stop this instance[Y/N]: ");
			if(confirm()){
				InstanceManager.GetInstance().StopInstance(name);
			}
		} catch (IOException e) {
		} catch (ServerDoesNotExist e) {
			System.out.println("Instance does not exist");
		} 
	}
	
	public void start()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			System.out.println("Starting instance: "+name);
			InstanceManager.GetInstance().StartInstance(name);
		} catch (IOException e) {
		} catch (ServerDoesNotExist e) {
			System.out.println("Instance does not exist");
		} catch (ServerIsRunning e) {
			System.out.println("Instance is already running");
		}
	}
	
	public void create()
	{
		System.out.print("Please enter new instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			InstanceManager.GetInstance().CreateInstance(name);
		} catch (IOException e) {
		} catch (ServerNameInUse e) {
			System.out.println("Sorry instance name is in use");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void instancePort()
	{
		System.out.print("Please enter new port: ");
		try {
			int port = Integer.parseInt(consoleIn.readLine());
			if(!InstanceManager.GetInstance().CheckPortIsFree(port)){
				System.out.println("Port is in use!");
				return;
			}
			System.out.print("are you sure this will disconnect anyone connected to the service[Y/N]: ");
			if(confirm()){
				Global.GetInstance().InstancePort = port;
				InstanceWebServer.GetInstance().stop();
				InstanceWebServer.GetInstance().start();
			}
		} catch (NumberFormatException e) {
			System.out.println("Port number is invalid");
		} catch (IOException e) {
		}
	}
	
	@SuppressWarnings("deprecation")
	public void adminPort()
	{
		System.out.print("Please enter new port: ");
		try {
			int port = Integer.parseInt(consoleIn.readLine());
			if(!InstanceManager.GetInstance().CheckPortIsFree(port)){
				System.out.println("Port is in use!");
				return;
			}
			System.out.print("are you sure this will disconnect anyone connected to the service[Y/N]: ");
			if(confirm()){
				Global.GetInstance().AdminPort = port;
				AdminWebServer.GetInstance().stop();
				AdminWebServer.GetInstance().start();
			}
		} catch (NumberFormatException e) {
			System.out.println("Port number is invalid");
		} catch (IOException e) {
		}
	}
	
	public void instance()
	{
		System.out.print("Please enter instance name: ");
		String name;
		try {
			name = consoleIn.readLine();
			if(!InstanceManager.GetInstance().doesServerExsit(name))
			{
				System.out.println("Server dose not exist");
			}
			boolean exit = false;
			while(!exit){
				try {
					System.out.print(name+"#");
					String cmd = consoleIn.readLine().toLowerCase();
					switch(cmd){
						case "exit":{
							exit = true;
							break;
						}
						case "stop":{
							try {
								InstanceManager.GetInstance().StopInstance(name);
							} catch (ServerDoesNotExist e) { }
							break;
						}
						case "start":{
							try {
								InstanceManager.GetInstance().StartInstance(name);
							} catch (ServerDoesNotExist | ServerIsRunning e) { }
							break;
						}
						case "restart":
						{
							try {
								InstanceManager.GetInstance().RestartInstance(name);
							} catch (ServerDoesNotExist e) {
							}
							break;
						}
						case "max-memory":
						{
							if(InstanceManager.GetInstance().isInstanceRunning(name)){
								System.out.println("Server is running please stop it first!");
								return;
							}
							System.out.println("Memory sizes must be in java param form E.G");
							System.out.println("100 MB of Memory is 100M");
							System.out.println("1 GB of Memory is 1G");
							System.out.print("Enter new Maximum Memory Size: ");
							Server.GetServerInstance(name).MemoryMax = consoleIn.readLine();
							break;
						}

						case "min-memory":
						{
							if(InstanceManager.GetInstance().isInstanceRunning(name)){
								System.out.println("Server is running please stop it first!");
								return;
							}
							System.out.println("Memory sizes must be in java param form E.G");
							System.out.println("100 MB of Memory is 100M");
							System.out.println("1 GB of Memory is 1G");
							System.out.print("Enter new Minimum Memory Size: ");
							Server.GetServerInstance(name).MemoryMin = consoleIn.readLine();
							break;
						}
						case "port":
						{
							inst_port(name);
							break;
						}
						case "type":{
							inst_type(name);
							break;
						}
						case "help":{
							inst_help();
							break;
						}
					}
				} catch (IOException e) {
				}
			}
		} catch (IOException e) {
			
		}
	}
	
	public void inst_port(String name) throws IOException {
		if(InstanceManager.GetInstance().isInstanceRunning(name)){
			System.out.println("Server is running please stop it first!");
			return;
		}
		System.out.print("Are you sure this will mean users will have to change information to connect [Y/N]:");
		if(confirm()){
			System.out.print("Please enter new port:");
			String in = consoleIn.readLine();
			try{
				int port = Integer.parseInt(in);
				InstanceManager.GetInstance().ChangePort(name, port);
			}catch(NumberFormatException e){
				System.out.println("port number can only be numerical");
			}catch(PortInUse e){
				System.out.println("port is used by another instance or MCWebAdmin web servers");
			}
		}		
	}
	
	public void inst_help(){
		System.out.println("Help text - this is the list of commands you can use!  ");
		System.out.println("Shutdown|Exit		- exits the instance control");
		System.out.println("Port			- change the port of this instance");
		System.out.println("Start			- Start a created instance");
		System.out.println("Stop			- Stop a started instance");
		System.out.println("Restart			- Restart a started instance");
		System.out.println("Max-Memory			- Sets the maximum memory instance can use");
		System.out.println("Min-Memory			- Sets the minimum memory instance can use");
	}

	public void inst_type(String name)
	{
		try{
			if(InstanceManager.GetInstance().isInstanceRunning(name)){
				System.out.println("Server is running please stop it first!");
				return;
			}
			System.out.print("Changing type will delete all data on instance are you sure [Y/N]: ");
			if(confirm()){
				System.out.print("Please enter new server type: ");
				String serverType = consoleIn.readLine();
				System.out.print("Please enter new server version: ");
				String serverVer = consoleIn.readLine();
				String serverPath = serverType+"_"+serverVer+"/";
				System.out.println("checking for server files at path "+serverPath);
				if(new File("servers/"+serverPath).exists()){
					System.out.print("please enter name of server jar file: ");
					String serverJar = consoleIn.readLine();
					if(serverJar.lastIndexOf('.') != -1){
						String extTest = serverJar.substring(serverJar.lastIndexOf('.'), serverJar.length());
						if(!extTest.equals(".jar")){
							serverJar += ".jar";
						}
					}else{
						serverJar += ".jar";
					}
					System.out.println("Checking for server jar: servers/"+serverPath+serverJar);
					if(new File("servers/"+serverPath+serverJar).exists()){
						Server.GetServerInstance(name).serverOriginPath = serverPath;
						Server.GetServerInstance(name).jarName = serverJar;
						System.out.println("Server config changed");
						try {
							InstanceManager.GetInstance().UninstallFiles(name);
						} catch (ServerDoesNotExist | ServerIsRunning e) {
						}
						return;
					}else{
						System.out.println("Server jar not found");
						System.out.println("Changing type Aborted!");
						return;
					}
				}else{
					System.out.println("Server files not found");
					System.out.println("Changing type Aborted!");
					return;
				}
			}
			System.out.println("Changing type Aborted!");
			return;
		}catch(IOException e){
			
		}
	}
	
	public void saveConfig()
	{
		Global.GetInstance().SaveConfig();
		Servers.GetInstance().SaveConfig();
		Server.SaveAllConfigs();
		Backups.getInstance().SaveConfig();
	}
}
