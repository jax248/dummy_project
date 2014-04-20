package com.pracify.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Class to filter files which are having .3gp extension
 * */
public class FileExtensionFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {
		return (name.endsWith(".3gp"));
	}
}
