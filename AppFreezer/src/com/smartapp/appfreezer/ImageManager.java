package com.smartapp.appfreezer;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;

/**
 * ͼƬ������
 */
public class ImageManager {
	/**
	 * ����
	 */
	private volatile static ImageManager sInstance = null;
	/**
	 * ͬ�����̵߳�Handler
	 */
	private Handler mHandler = new Handler();
	/**
	 * ͼƬ�����ã�������key
	 */
	private Map<String, SoftReference<Drawable>> mImageCache = new HashMap<String, SoftReference<Drawable>>();

	private ExecutorService mPool = Executors.newFixedThreadPool(2);

	private PackageManager mPm = null;

	private Context mContext;

	/**
	 * ���캯��
	 */
	private ImageManager(Context context) {
		mContext = context;
		mPm = context.getPackageManager();
	}

	/**
	 * ��ȡ����
	 */
	public static synchronized ImageManager getInstance(Context context) {
		if (sInstance == null) {
			synchronized (ImageManager.class) {
				if (sInstance == null) {
					sInstance = new ImageManager(context);
				}
			}
		}
		return sInstance;
	}

	public Drawable loadDrawable(final ApplicationInfo appInfo,
			final String pkName, final ImageCallback callback) {
		if (mImageCache.get(pkName) != null
				&& mImageCache.get(pkName).get() != null) {
			return mImageCache.get(pkName).get();
		}
		mPool.execute(new Runnable() {

			@Override
			public void run() {
				final Drawable drawable = appInfo.loadIcon(mPm);
				mImageCache.put(pkName, new SoftReference<Drawable>(drawable));
				if (callback != null) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							callback.imageLoad(pkName, drawable);
						}
					});
				}
			}
		});
		return null;
	}

	public static interface ImageCallback {
		public void imageLoad(String pkName, Drawable drawable);
	}

}
