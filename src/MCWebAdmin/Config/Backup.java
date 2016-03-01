package MCWebAdmin.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import MCWebAdmin.Config.Serializable.Backups;
import MCWebAdmin.Config.Serializable.Global;
import MCWebAdmin.Config.Serializable.Server;
import MCWebAdmin.Instance.InstanceManager;
import MCWebAdmin.Util.Exceptions.ServerDoesNotExist;
import MCWebAdmin.Util.Exceptions.ServerIsNotRunning;
import MCWebAdmin.Util.Exceptions.ServerIsRunning;

public class Backup {
	private String serverName, backupName, created;
	
	public Backup(String server, String backup){
		serverName = server;
		backupName = backup;
	}
	
	public Backup(String filename){
		String[] file = filename.split("_");
		serverName = file[0];
		backupName = file[1];
		created = file[2];
	}
	
	public String GetPath()
	{
		return Global.GetInstance().InstanceBackupPath+serverName+"_"+backupName+"_"+created+".zip";
	}
	
	public String GetName()
	{
		return serverName+"_"+backupName+"_"+created;
	}
	
	private void addDir(File dirObj, ZipOutputStream out) throws IOException {
	    File[] files = dirObj.listFiles();
	    byte[] tmpBuf = new byte[1024];

	    for (int i = 0; i < files.length; i++) {
	      if (files[i].isDirectory()) {
	        addDir(files[i], out);
	        continue;
	      }
	      String DirPathToExclude = (System.getProperty("user.dir")+"/"+
	    		  Global.GetInstance().InstancesPath+
	    		  Server.GetServerInstance(serverName).name+"/").replace("/", "\\");
	      FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
	      String zipPath = files[i].getAbsolutePath().replace(DirPathToExclude, "");
	      out.putNextEntry(new ZipEntry(zipPath));
	      int len;
	      while ((len = in.read(tmpBuf)) > 0) {
	        out.write(tmpBuf, 0, len);
	      }
	      out.closeEntry();
	      in.close();
	    }
	  }
	
	public void Create()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		created = dateFormat.format(date);
		String dir = Global.GetInstance().InstancesPath+Server.GetServerInstance(serverName).name+"/";
		File dirObj = new File(dir);
		ZipOutputStream out;
		File f = new File(GetPath().replace(GetName()+".zip", ""));
		f.mkdirs();
		try {
			out = new ZipOutputStream(new FileOutputStream(GetPath()));
		    addDir(dirObj, out);
		    out.close();
		    Backups.getInstance().AddBackup(this);
		    System.out.println("Created backup '"+GetPath()+"'");
		} catch (Exception e) {
			System.out.println("Failed to create backup '"+GetPath()+"'");
		}
		
	}
	
	public void Delete()
	{
		File f = new File(GetPath());
		f.delete();
	}
	
	public void Restore()
	{
		try {
			if(InstanceManager.GetInstance().isInstanceRunning(serverName)){
				InstanceManager.GetInstance().StopInstance(serverName);
			}
			InstanceManager.GetInstance().UninstallFiles(serverName);
		} catch (ServerDoesNotExist | ServerIsRunning e) {
		}
		String outDir = Global.GetInstance().InstancesPath+Server.GetServerInstance(serverName).name+"/";
		try{
		byte[] buf = new byte[1024];
	    ZipInputStream zipinputstream = null;
	    ZipEntry zipentry;
	    zipinputstream = new ZipInputStream(new FileInputStream(GetPath()));
	    zipentry = zipinputstream.getNextEntry();
	    while (zipentry != null) {
	      String entryName = zipentry.getName();
	      FileOutputStream fileoutputstream;
	      File newFile = new File(entryName);
	      String directory = newFile.getParent();

	      if (directory == null) {
	        if (newFile.isDirectory())
	          break;
	      }
	      fileoutputstream = new FileOutputStream(outDir + entryName);
	      int n;
	      while ((n = zipinputstream.read(buf, 0, 1024)) > -1){
	        fileoutputstream.write(buf, 0, n);
	      }
	      fileoutputstream.close();
	      zipinputstream.closeEntry();
	      zipentry = zipinputstream.getNextEntry();
	    }
	    zipinputstream.close();
		}catch(Exception e){
			System.out.println("Restoration of backup '"+GetPath()+"' failed!");
		}
	}
}
