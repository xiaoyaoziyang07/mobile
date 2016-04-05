package com.wlan.diversionWlan.other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MyFileOutputStream extends FileOutputStream {
	
	private File file;
	
	public MyFileOutputStream(File file) throws FileNotFoundException {
		super(file);
		this.file = file;
	}
	
	public MyFileOutputStream(File file, boolean b) throws FileNotFoundException{
		super(file, b);
		this.file = file;
	}
	
	public File getFile(){
		return file;
	}
}
