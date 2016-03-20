package com.wlan.diversionWlan.other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MyFileOutputStream extends FileOutputStream {
	
	private File file;
	
	public MyFileOutputStream(File file) throws FileNotFoundException {
		super(file);
	}
	
	public MyFileOutputStream(File file, boolean b) throws FileNotFoundException{
		super(file, b);
	}
	
	public File getFile(){
		return file;
	}
}
