/*
 * Copyright (C) 2013  WhiteCat 白猫 (www.thinkandroid.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ta.util.netstate;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.ta.TAApplication;
import com.ta.mvc.common.TAIResponseListener;
import com.ta.mvc.common.TARequest;
import com.ta.mvc.common.TAResponse;
import com.ta.util.TALogger;
import com.ta.util.netstate.TANetWorkUtil.netType;

/**
 * @Title NetworkStateReceiver
 * @Package com.ta.util.netstate
 * @Description 是一个检测网络状态改变的，需要配置 <receiver
 *              android:name="com.ta.util.netstate.TANetworkStateReceiver" >
 *              <intent-filter> <action
 *              android:name="android.net.conn.CONNECTIVITY_CHANGE" /> <action
 *              android:name="android.gzcpc.conn.CONNECTIVITY_CHANGE" />
 *              </intent-filter> </receiver>
 * 
 *              需要开启权限 <uses-permission
 *              android:name="android.permission.CHANGE_NETWORK_STATE" />
 *              <uses-permission
 *              android:name="android.permission.CHANGE_WIFI_STATE" />
 *              <uses-permission
 *              android:name="android.permission.ACCESS_NETWORK_STATE" />
 *              <uses-permission
 *              android:name="android.permission.ACCESS_WIFI_STATE" />
 * @author 白猫
 * @date 2013-5-5 下午 22:47
 * @version V1.2
 */
public class TANetworkStateReceiver extends BroadcastReceiver {
	private static Boolean networkAvailable = false;
	private static Boolean isUnload = true;// 设置是否可上传
	private static Boolean isAllUnload = true;// 设置是否可上传
	private static netType netType;
	private static ArrayList<TANetChangeObserver> netChangeObserverArrayList = new ArrayList<TANetChangeObserver>();
	private final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public final static String TA_ANDROID_NET_CHANGE_ACTION = "ta.android.net.conn.CONNECTIVITY_CHANGE";
	private static BroadcastReceiver receiver;

	private static BroadcastReceiver getReceiver() {
		if (receiver == null) {
			receiver = new TANetworkStateReceiver();
		}
		return receiver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		receiver = TANetworkStateReceiver.this;
		if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)
				|| intent.getAction().equalsIgnoreCase(
						TA_ANDROID_NET_CHANGE_ACTION)) {
			TALogger.i(TANetworkStateReceiver.this, "网络状态改变.");
			if (!TANetWorkUtil.isNetworkAvailable(context)) {
				TALogger.i(TANetworkStateReceiver.this, "没有网络连接.");
				networkAvailable = false;
			} else {
				TALogger.i(TANetworkStateReceiver.this, "网络连接成功.");
				netType = TANetWorkUtil.getAPNType(context);
				networkAvailable = true;
			}
			notifyObserver();
		}		

		/**
		 * 插件部分 查询数据库有无数据，有则执行上传
		 */

		if (TANetWorkUtil.isNetworkAvailable(context)) {
			Log.e("TANetworkStateReceiver", "网络可用");
			if (isUnload) {
				isUnload = false;
				Log.e("TANetworkStateReceiver", "执行上传");
				//registerUserInfo(TAApplication.getApplication());

			}

		}

	}

	/**
	 * 插件上传数据到服务端
	 */
	private void gotoUnloadData() {

		// 首次进入调用激活应用接口

		// TODO Auto-generated method stub
		// app下载成功量统计
//		AppDownloadCountDao downloadDao = new AppDownloadCountDao(
//				TAApplication.getApplication());
//		List<AppDownloadCount> downloadList = downloadDao.getSpkData();
//		if (downloadList != null && downloadList.size() > 0) {
//			for (int i = 0; i < downloadList.size(); i++) {
//				AppDownloadCount downloadBean = downloadList.get(i);
//				UnloadDownloadData(downloadBean, TAApplication.getApplication());
//			}
//		}

	}

	/**
	 * 注册网络状态广播
	 * 
	 * @param mContext
	 */
	public static void registerNetworkStateReceiver(Context mContext) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(TA_ANDROID_NET_CHANGE_ACTION);
		filter.addAction(ANDROID_NET_CHANGE_ACTION);
		mContext.getApplicationContext()
				.registerReceiver(getReceiver(), filter);
	}

	/**
	 * 检查网络状态
	 * 
	 * @param mContext
	 */
	public static void checkNetworkState(Context mContext) {
		Intent intent = new Intent();
		intent.setAction(TA_ANDROID_NET_CHANGE_ACTION);
		mContext.sendBroadcast(intent);
	}

	/**
	 * 注销网络状态广播
	 * 
	 * @param mContext
	 */
	public static void unRegisterNetworkStateReceiver(Context mContext) {
		if (receiver != null) {
			try {
				mContext.getApplicationContext().unregisterReceiver(receiver);
			} catch (Exception e) {
				// TODO: handle exception
				TALogger.d("TANetworkStateReceiver", e.getMessage());
			}
		}

	}

	/**
	 * 获取当前网络状态，true为网络连接成功，否则网络连接失败
	 * 
	 * @return
	 */
	public static Boolean isNetworkAvailable() {
		return networkAvailable;
	}

	public static netType getAPNType() {
		return netType;
	}

	private void notifyObserver() {

		for (int i = 0; i < netChangeObserverArrayList.size(); i++) {
			TANetChangeObserver observer = netChangeObserverArrayList.get(i);
			if (observer != null) {
				if (isNetworkAvailable()) {
					observer.onConnect(netType);
				} else {
					observer.onDisConnect();
				}
			}
		}

	}

	/**
	 * 注册网络连接观察者
	 * 
	 * @param observerKey
	 *            observerKey
	 */
	public static void registerObserver(TANetChangeObserver observer) {
		if (netChangeObserverArrayList == null) {
			netChangeObserverArrayList = new ArrayList<TANetChangeObserver>();
		}
		netChangeObserverArrayList.add(observer);
	}

	/**
	 * 注销网络连接观察者
	 * 
	 * @param resID
	 *            observerKey
	 */
	public static void removeRegisterObserver(TANetChangeObserver observer) {
		if (netChangeObserverArrayList != null) {
			netChangeObserverArrayList.remove(observer);
		}
	}

	/**
	 * 上传app下载速度统计数据
	 * 
	 * @param speedList
	 * @param context
	 */
//	private void UnloadDownloadSpeed(List<DownloadSpeed> speedList,
//			Context context) {
//		TAApplication.getApplication()
//				.doCommand(
//						context.getString(R.string.downloadspeedcontroller),
//						new TARequest(DownloadSpeedController.DOWNLOAD_SPEED,
//								speedList), new TAIResponseListener() {
//
//							@Override
//							public void onSuccess(TAResponse response) {
//
//							}
//
//							@Override
//							public void onStart() {
//
//							}
//
//							@Override
//							public void onRuning(TAResponse response) {
//
//							}
//
//							@Override
//							public void onFinish() {
//
//							}
//
//							@Override
//							public void onFailure(TAResponse response) {
//
//							}
//						}, true, false);
//
//	}




}