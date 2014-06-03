package com.pracify.util;

import android.os.Environment;

public class PracifyConstants {

	public static String basServer = "bluntingthorns.com";

	public static String basURL = "http://bluntingthorns.com/pracify/PHPScripts";

	public static String filePathIntent = "FilePath";
	public static String fileID = "FileID";

	public static String loginURL = basURL + "/login.php";
	public static String registerURL = basURL + "/register.php";
	public static String resetpwdURL = basURL + "/resetpwd.php";
	public static String fileListURL = basURL + "/get_files.php";
	public static String fileUploadURL = basURL + "/upload_files.php";
	public static String fileDownloadURL = basURL + "/download_files.php";

	public static String recordingPath = "/Pracify";
	public static String externalStoragePath = Environment
			.getExternalStorageDirectory().getAbsolutePath() + recordingPath;
	public static boolean isSDPresent = android.os.Environment
			.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
	
	public static String musicFileExtension = ".mp4";

}
