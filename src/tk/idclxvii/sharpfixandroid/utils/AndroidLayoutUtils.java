package tk.idclxvii.sharpfixandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class AndroidLayoutUtils {

	
	
	public static CheckBox fixCheckBoxPaddingLeft(Context a, CheckBox cb, float dp){
		final float scale = a.getResources().getDisplayMetrics().density;
		cb.setPadding(cb.getPaddingLeft() + (int)(dp * scale + 0.5f),
		        cb.getPaddingTop(),
		        cb.getPaddingRight(),
		        cb.getPaddingBottom());
		return cb;
	}
	
	public static RadioButton fixRadioButtonPaddingLeft(Context a, RadioButton rb, float dp){
		final float scale = a.getResources().getDisplayMetrics().density;
		rb.setPadding(rb.getPaddingLeft() + (int)(dp * scale + 0.5f),
				rb.getPaddingTop(),
				rb.getPaddingRight(),
				rb.getPaddingBottom());
		return rb;
	}
	
}
