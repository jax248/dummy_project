package com.pracify.util;

import android.os.Environment;

public class PracifyConstants {

	public static String basServer = "bluntingthorns.com";

	public static String basURL = "http://bluntingthorns.com/pracify/PHPScripts";

	public static String loginURL = basURL + "/login.php";
	public static String registerURL = basURL + "/register.php";
	
	public static String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pracify";

}
