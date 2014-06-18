package com.smartapp.appfreezer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.smartapp.appfreezer.ui.GoBackupTabsBar;
import com.smartapp.appfreezer.ui.ScreenScroller;
import com.smartapp.appfreezer.ui.ScreenScrollerListener;
import com.smartapp.appfreezer.ui.ScrollerViewGroup;

public class MainActivity extends Activity implements ScreenScrollerListener {

	// TODO ��������װ�¼�
	// TODO ˢ�»�����Σ�
	// TODO �ս�ȥ����ȡrootȨ�ޣ�չʾ�Ƕ���Ӧ�úͶ���Ӧ�ã����ж���/�ⶳʱ������rootȨ��
	// TODO �������GO���ݺ�App Quarantine
	// TODO ���Ͻ��и��˵���ť����ɸѡ��ˢ�¡����������֡���������Ӧ��
	// TODO ����crash report
	// TODO �����
	// TODO ��ͼ��

	private ScrollerViewGroup mScrollerViewGroup;
	/**
	 * ����tab��
	 */
	private GoBackupTabsBar mTabsBar = null;

	/**
	 * ������listview
	 */
	private ListView mEnableListView = null;
	private IAdapter mEnableAdapter = null;

	/**
	 * �ѽ���listview
	 */
	private ListView mDisableListView = null;
	private IAdapter mDisableAdapter = null;

	private Button mFreezeBtn = null;
	/**
	 * ���ᰴť
	 */
	private ViewGroup mFreezeBtnFrame = null;

	private Button mUnFreezeBtn = null;
	/**
	 * �ⶳ��ť
	 */
	private ViewGroup mUnFreezeBtnFrame = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		initScrollView();
		initTabView();

		mEnableListView = (ListView) findViewById(R.id.enable_listview);
		mEnableAdapter = new IAdapter(MainActivity.this,
				new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						refreshBtn();
					}
				});
		mEnableListView.setAdapter(mEnableAdapter);
		mEnableListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ��������ı�checkbox
				ListDataBean bean = (ListDataBean) view.getTag();
				bean.mIsSelect = !bean.mIsSelect;
				mEnableAdapter.notifyDataSetChanged();
			}
		});

		mDisableListView = (ListView) findViewById(R.id.disable_listview);
		mDisableAdapter = new IAdapter(MainActivity.this,
				new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						refreshBtn();
					}
				});
		mDisableListView.setAdapter(mDisableAdapter);
		mDisableListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ��������ı�checkbox
				ListDataBean bean = (ListDataBean) view.getTag();
				bean.mIsSelect = !bean.mIsSelect;
				mDisableAdapter.notifyDataSetChanged();
			}
		});
		// ˢ�������ú��ѽ����б�
		refresh();

		mFreezeBtn = (Button) findViewById(R.id.freeze_btn);
		mFreezeBtn.setClickable(false);
		mUnFreezeBtn = (Button) findViewById(R.id.unfreeze_btn);
		mUnFreezeBtn.setClickable(false);

		mFreezeBtnFrame = (ViewGroup) findViewById(R.id.freeze_btn_frame);
		mUnFreezeBtnFrame = (ViewGroup) findViewById(R.id.unfreeze_btn_frame);

		mFreezeBtnFrame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO ����Ӧ��
			}
		});

		mUnFreezeBtnFrame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO �ⶳӦ��
			}
		});
		// ���¶���/�ⶳ��ť״̬
		refreshBtn();
	}

	/**
	 * ���¶���/�ⶳ��ť״̬
	 */
	private void refreshBtn() {
		int freezeCount = mEnableAdapter.getSelectCount();
		int unfreezeCount = mDisableAdapter.getSelectCount();

		if (freezeCount <= 0) {
			mFreezeBtn.setTextColor(0xFF909090);
			mFreezeBtnFrame.setEnabled(false);
		} else {
			mFreezeBtn.setTextColor(0xffffffff);
			mFreezeBtnFrame.setEnabled(true);
		}
		mFreezeBtn
				.setText(getString(R.string.freeze) + "(" + freezeCount + ")");

		if (unfreezeCount <= 0) {
			mUnFreezeBtn.setTextColor(0xFF909090);
			mUnFreezeBtnFrame.setEnabled(false);
		} else {
			mUnFreezeBtn.setTextColor(0xffffffff);
			mUnFreezeBtnFrame.setEnabled(true);
		}
		mUnFreezeBtn.setText(getString(R.string.unfreeze) + "(" + unfreezeCount
				+ ")");
	}

	/**
	 * ˢ�������ú��ѽ����б�
	 */
	private void refresh() {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���÷��ΪԲ�ν�����
		dialog.setMessage(getString(R.string.wait));
		dialog.setCancelable(false);// ���ý������Ƿ���԰��˻ؼ�ȡ��
		dialog.show();

		Thread thread = new Thread() {
			@Override
			public void run() {
				final List<ListDataBean> enableList = new ArrayList<ListDataBean>();
				final List<ListDataBean> disableList = new ArrayList<ListDataBean>();
				AppFreezer.getEnableAndDisableAppByApi(MainActivity.this,
						enableList, disableList);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dialog.dismiss();
						mEnableAdapter.update(enableList);
						mDisableAdapter.update(disableList);
					}
				});

			}
		};
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	/**
	 * ��ʼ����������ҳ
	 */
	private void initScrollView() {
		mScrollerViewGroup = (ScrollerViewGroup) findViewById(R.id.scrollerPageView);
		mScrollerViewGroup.setScreenCount(mScrollerViewGroup.getChildCount());
		mScrollerViewGroup.setScreenScrollerListener(this);
		mScrollerViewGroup.gotoViewByIndex(0);

	}

	/**
	 * ��ʼ��tabҳ
	 */
	private void initTabView() {
		FrameLayout tabFrame = (FrameLayout) findViewById(R.id.tabbar_frame);
		mTabsBar = new GoBackupTabsBar(MainActivity.this,
				new GoBackupTabsBar.TabObserver() {

					@Override
					public void handleChangeTab(int tabIndex) {
						// ����scrollerViewGroup��ת
						mScrollerViewGroup.gotoViewByIndex(tabIndex);
					}
				});
		tabFrame.addView(mTabsBar);
		mTabsBar.cleanData();
		List<String> titles = new ArrayList<String>();
		titles.add(MainActivity.this.getResources().getString(R.string.enable));
		titles.add(MainActivity.this.getResources().getString(R.string.disable));
		mTabsBar.initTabsBar(titles);
		mTabsBar.setButtonSelected(0, false);

		mTabsBar.setBackgroundColor(0xFF202020);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ɱ������
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public ScreenScroller getScreenScroller() {
		return null;
	}

	@Override
	public void setScreenScroller(ScreenScroller scroller) {

	}

	@Override
	public void onFlingIntercepted() {

	}

	@Override
	public void onScrollStart() {

	}

	@Override
	public void onFlingStart() {

	}

	@Override
	public void onScrollChanged(int newScroll, int oldScroll) {

	}

	@Override
	public void onScreenChanged(int newScreen, int oldScreen) {
		mTabsBar.setButtonSelected(newScreen, true);
	}

	@Override
	public void onScrollFinish(int currentScreen) {

	}

	@Override
	public void postInvalidate() {

	}

	@Override
	public void scrollBy(int x, int y) {

	}

	@Override
	public int getScrollX() {
		return 0;
	}

	@Override
	public int getScrollY() {
		return 0;
	}

}
