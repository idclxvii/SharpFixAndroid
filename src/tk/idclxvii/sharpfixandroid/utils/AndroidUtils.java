package tk.idclxvii.sharpfixandroid.utils;


import android.os.*;
import android.util.Log;

public class AndroidUtils {
	
	
	public static enum API_CODE_NAME{
		BASE,
		BASE_1_1,
		CUPCAKE,
		DONUT,
		ECLAIR,
		ECLAIR_0_1,
		ECLAIR_MR1,
		FROYO,
		GINGERBREAD,
		GINGERBREAD_MR1,
		HONEYCOMB,
		HONEYCOMB_MR1,
		HONEYCOMB_MR2,
		ICE_CREAM_SANDWICH,
		ICE_CREAM_SANDWICH_MR1,
		JELLY_BEAN,
		JELLY_BEAN_MR1,
		JELLY_BEAN_MR2,
		KITKAT,
		CUR_DEVELOPMENT,
	};
	
	public static String[] API_NAME = {
		"No Code Name",
		"Petit Four",
		"Cupcake",
		"Donut",
		"Eclair",
		"Eclair",
		"Eclair Minor Revision 1",
		"Froyo",
		"Gingerbread",
		"Gingerbread Minor Revision 1",
		"Honeycomb",
		"Honeycomb Minor Revision 1",
		"Honeycomb Minor Revision 2",
		"Ice Cream Sandwich",
		"Ice Cream Sandwich Minor Revision 1",
		"Jellybean",
		"Jellybean Minor Revision 1",
		"Jellybean Minor Revision 2",
		"KitKat",
		"Current Development Build",
	};
	
	public static String getCurrentAndroidVersionInfo(){
		
		return("Detected Full Android Version Information:\nAndroid " + android.os.Build.VERSION.RELEASE + " (" +
				API_CODE_NAME.values()[android.os.Build.VERSION.SDK_INT - 1].toString() + ")\n" +
				"\tAndroid Build Release: Android " + android.os.Build.VERSION.RELEASE + "\n" +
				 "\tAPI level " + android.os.Build.VERSION.SDK_INT + " " +API_NAME[android.os.Build.VERSION.SDK_INT - 1] 
				);
	}
	
	
	
}
