package MCWebAdmin.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ConfigReader {
	private static ConfigReader _inst;
	
	public static ConfigReader GetInstance(){
		if(_inst == null){
			_inst = new ConfigReader();
		}
		return _inst;
	}
	
	private String basePath =  "Config/";
	
	public <T extends Serializable> void Write(T in, String filename)
	{
		String path = basePath+filename+".bin";
		try
		{	
			File f = new File(path);
			f.getParentFile().mkdirs();
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(in);
			out.close();
			fileOut.close();
			System.out.println("Saved config file '"+path+"'");
		}catch(IOException i)
		{
			System.out.println("Failed to create config file '"+path+"'");
		}
	}
	
	public boolean ConfigExists(String filename)
	{
		String path = basePath+filename+".bin";
		File finfo = new File(path);
		return finfo.exists();
	}
	
	public <T extends Serializable> T Read(T readin, String filename)
	{
		String path = basePath+filename+".bin";
		try
		{
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			readin = (T) in.readObject();
			in.close();
			fileIn.close();
			return readin;
		}catch(IOException i)
		{
			System.out.println("Failed to read '"+path+"'");
			return null;
		}catch(ClassNotFoundException c)
		{
			System.out.println("Failed to unserialize '"+path+"'");
		    c.printStackTrace();
		    return null;
		}
	}
}
