package fileIO;

import java.io.File;

public class DirectoryFactory {
	public static boolean createDirectory(String dirname){
		File dir = new File(dirname);
		if(dir.exists()){
			System.out.println(dirname + " already exists.");
			return false;
		}
		return dir.mkdir();
	}
}
