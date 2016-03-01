package MCWebAdmin.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import MCWebAdmin.Util.Exceptions.FileNotFound;
import MCWebAdmin.Util.Exceptions.UnableToReadFile;

public class FileReader {

	private static FileReader _inst;
	private FileReader(){}
	
	public static FileReader GetInstance() {
		if(_inst == null)
			_inst = new FileReader();
		return _inst;
	}

	public byte[] readFile(String localFilePath) throws FileNotFound, UnableToReadFile{
		int totalBytesRead = 0;
		File file = new File(localFilePath);
		if(!file.exists()){
			throw new FileNotFound(localFilePath);
		}
		try{
			byte[] result = new byte[(int)file.length()];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			while (totalBytesRead < result.length)
			{
				int bytesRemaining = result.length - totalBytesRead;/* 149:    */         
				int bytesRead = bis.read(result, totalBytesRead, bytesRemaining);
				if (bytesRead > 0) {
					totalBytesRead += bytesRead;
				}
			}
			bis.close();
			return result;
		}catch(Exception e){
			throw new UnableToReadFile(localFilePath);
		}
	}
	
}
