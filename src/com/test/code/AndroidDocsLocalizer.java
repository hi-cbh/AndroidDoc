package com.test.code;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * The code is refactored from:
 * 1. http://blog.csdn.net/baggio1006/article/details/8272229
 * 2. http://blog.csdn.net/liu1164316159/article/details/32335743
 * 这个更有效
 */
public class AndroidDocsLocalizer {
	private static final String ANDROID_SDK_HOME = "ANDROID_HOME";
	private static String androidSDKHome;
	
	private static final String REMOVE_START = "<!--";
	private static final String REMOVE_END = "-->";
	
	private static final String JSAPI_RAW = "<script src=\"http://www.google.com/jsapi\" type=\"text/javascript\"></script>";
	private static final String JSAPI_RAW_PATTERN = "<script src=\"http://www\\.google\\.com/jsapi\" type=\"text/javascript\"></script>";
	private static final String JSAPI_REMOVED_RAW = REMOVE_START + JSAPI_RAW + REMOVE_END;
	
	private static final String FONTS_RAW = "<link rel=\"stylesheet\"\nhref=\"http://fonts.googleapis.com/css?family=Roboto:regular,medium,thin,italic,mediumitalic,bold\" title=\"roboto\">";
	private static final String FONTS_RAW_PATTERN = "<link rel=\"stylesheet\"\nhref=\"http://fonts\\.googleapis\\.com/css\\?family=Roboto:regular,medium,thin,italic,mediumitalic,bold\" title=\"roboto\">";
	private static final String FONTS_REMOVED_RAW = REMOVE_START + FONTS_RAW + REMOVE_END;
	
	private static String docsPath;

	
	public static void main(String[] args) {
		Map<String, String> map = System.getenv();
		androidSDKHome = map.get(ANDROID_SDK_HOME);
		docsPath = androidSDKHome + "\\docs";
		File docs = new File(docsPath);
		if (!docs.exists()) {
			System.out.println(docsPath + " not a valid directory!");
			return;
		}
		
		System.out.println("It will take several minutes, please wait...");
		traverse(docs);
		System.out.println("DONE!");
	}

	private static void traverse(File dir) {
		if (!dir.isDirectory()) return;
		
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				}
				
				return isHtmlFile(file);
			}

		});

		for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("Scanning Folder: "
						+ file.getAbsolutePath());
				traverse(file);
			} else {
				try {
					removeJscript(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static boolean isHtmlFile(File f) {
		return f.isFile() && f.getName().endsWith(".html");
	}

	/**
	 * 
	 * @param file
	 * @throws IOException 
	 */
	private static void removeJscript(File file) throws IOException {
		String content = read(file);
		boolean changed = false;
		
		if (content.indexOf(JSAPI_REMOVED_RAW) == -1) {
			System.out.println("jsapi will be removed.");
			changed = true;
			content = content.replaceFirst(JSAPI_RAW_PATTERN, JSAPI_REMOVED_RAW);
		} else {
			System.out.println("jsapi has beed removed");
		}
		
		if (content.indexOf(FONTS_REMOVED_RAW) == -1) {
			System.out.println("fonts will be removed.");
			changed = true;
			content = content.replaceFirst(FONTS_RAW_PATTERN, FONTS_REMOVED_RAW);
		} else {
			System.out.println("fonts has been removed.");
		}
		
		if (changed) {
	        write(content, file);
			
			System.out.println("Changed: " + file.getAbsolutePath());
		} else {
			System.out.println("Unchanged: " + file.getAbsolutePath());
		}
		
	}
  
    public static String read(File src) {  
        StringBuffer res = new StringBuffer();  
        String line = null;  
        try {  
            BufferedReader reader = new BufferedReader(new FileReader(src));  
            int i = 0;  
            while ((line = reader.readLine()) != null) {  
                if (i != 0) {  
                    res.append('\n');  
                }  
                res.append(line);  
                i++;  
            }  
            
            res.append('\n');  // the last line of the file
            reader.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return res.toString();  
    }  
  
    public static boolean write(String cont, File dist) {  
        try {  
            BufferedWriter writer = new BufferedWriter(new FileWriter(dist));  
            writer.write(cont);  
            writer.flush();  
            writer.close();  
            return true;  
        } catch (IOException e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
}
