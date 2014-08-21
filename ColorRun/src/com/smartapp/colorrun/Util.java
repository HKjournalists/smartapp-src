package com.smartapp.colorrun;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class Util {

	public static final Random sRandom = new Random();

	/**
	 * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(mDisplayMetrics);
		return mDisplayMetrics.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(mDisplayMetrics);
		return mDisplayMetrics.heightPixels;
	}

}
