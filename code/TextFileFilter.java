package code;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/*
 * This class is used to limit the choice of files that can be selected to run on
 * the simulator to text files.
 */
public class TextFileFilter extends FileFilter {
	
	

	@Override
	public boolean accept(File f) {
		String extension = this.getExtension(f);
		
		if (extension.equals("txt") || f.isDirectory()) { //Accept only txt files or directories (to allow navigation)
			return true;
		}

		return false;
	}

	@Override
	public String getDescription() { //To enable description at the bottom of file chooser
		return "Text Files";
	}
	
	
	private String getExtension(File f) {
		String fileName = f.getName();
		String extension = "";
		
		int i = fileName.lastIndexOf('.'); //Get index of the last "." in the file name
		if (i > 0 && i < fileName.length() - 1) {
			extension = fileName.substring(i+1).toLowerCase(); //Extension formed from all characters following "."
		}
		
		return extension;
	}
	
}
