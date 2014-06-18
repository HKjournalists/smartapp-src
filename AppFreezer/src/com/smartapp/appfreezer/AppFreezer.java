package com.smartapp.appfreezer;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * ������
 */
public class AppFreezer {

	public static boolean isCollectionEmpty(Collection<?> collection) {
		return collection == null || collection.size() <= 0;
	}

	/**
	 * ��ȡ�����õ�Ӧ���б�
	 */
	public static List<PackageInfo> getEnabledPackagesByApi(Context context) {
		List<PackageInfo> packageInfos = context.getPackageManager()
				.getInstalledPackages(0);
		if (isCollectionEmpty(packageInfos)) {
			return null;
		}
		List<PackageInfo> enabledPackages = new ArrayList<PackageInfo>();
		PackageManager pm = context.getPackageManager();
		for (PackageInfo info : packageInfos) {
			String packageName = info.packageName;
			int state = pm.getApplicationEnabledSetting(packageName);
			if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
					&& state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
				Log.e("TEST", "enable : " + packageName);
				enabledPackages.add(info);
			}
		}
		return !isCollectionEmpty(enabledPackages) ? enabledPackages : null;
	}

	/**
	 * ��ȡ�ѽ��õ�Ӧ���б�
	 */
	public static List<PackageInfo> getDisabledPackagesByApi(Context context) {
		List<PackageInfo> packageInfos = context.getPackageManager()
				.getInstalledPackages(0);
		if (isCollectionEmpty(packageInfos)) {
			return null;
		}
		List<PackageInfo> enabledPackages = new ArrayList<PackageInfo>();
		PackageManager pm = context.getPackageManager();
		for (PackageInfo info : packageInfos) {
			String packageName = info.packageName;
			int state = pm.getApplicationEnabledSetting(packageName);
			if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
					|| state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
				Log.e("TEST", "disable : " + packageName);
				enabledPackages.add(info);
			}
		}
		return !isCollectionEmpty(enabledPackages) ? enabledPackages : null;
	}

	/**
	 * ��ȡ���������ú��ѽ��õ�Ӧ��
	 */
	public static void getEnableAndDisableAppByApi(Context context,
			List<ListDataBean> enableList, List<ListDataBean> disableList) {
		if (enableList == null || disableList == null) {
			return;
		}

		enableList.clear();
		disableList.clear();

		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		if (isCollectionEmpty(packageInfos)) {
			return;
		}

		for (PackageInfo info : packageInfos) {
			ListDataBean bean = new ListDataBean();
			bean.mInfo = info;
			bean.mIsSelect = false;
			// �ж��Ƿ�ϵͳӦ��
			ApplicationInfo app = info.applicationInfo;
			if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				bean.mIsSystemApp = false;
			} else {
				bean.mIsSystemApp = true;
			}
			// Ӧ������
			bean.mAppName = bean.mInfo.applicationInfo.loadLabel(pm).toString()
					.trim();

			String packageName = info.packageName;
			int state = pm.getApplicationEnabledSetting(packageName);
			if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED
					&& state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
				enableList.add(bean);
			} else {
				disableList.add(bean);
			}
		}

		Collections.sort(enableList, new IComparator());
		Collections.sort(disableList, new IComparator());
	}

	/**
	 * �Ƚ���
	 * 
	 * @author xiedezhi
	 * 
	 */
	public static class IComparator implements Comparator<ListDataBean> {

		private final Collator sCollator = Collator.getInstance();

		@Override
		public final int compare(ListDataBean aa, ListDataBean ab) {
			CharSequence sa = aa.mAppName;
			if (sa == null) {
				sa = "";
			}
			CharSequence sb = ab.mAppName;
			if (sb == null) {
				sb = "";
			}
			return sCollator.compare(sa.toString(), sb.toString());
		}
	}

}
