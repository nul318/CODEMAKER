package com.example.codemaker;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FileManager {

	public static String getFileName(URL url) {
		String pathOfUrl = url.getPath();
		return pathOfUrl.substring(pathOfUrl.lastIndexOf('/') + 1);
	}

	public static List<String> getFileNamesIn(String strDirectory) {
		File filePath = new File(strDirectory);
		
		if (!filePath.exists()) {
			return new LinkedList<String>();
		}
		
		return Arrays.asList(filePath.list());
	}

}
