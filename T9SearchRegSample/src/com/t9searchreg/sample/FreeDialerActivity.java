package com.t9searchreg.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import com.finalmapservices.adapter.base.BaseAdapterHelper;
import com.finalmapservices.adapter.base.QuickAdapter;
import com.finalmapservices.bean.ContactBean;
import com.finalmapservices.view.FragmentTabHost;
import com.t9searchreg.model.ContactItem;
import com.t9searchreg.util.PinyinUtil;
import com.t9searchreg.util.T9MatchUtil;

/**
 * 使用fragmentTabHost的Activity
 * 
 * @author bl_sun
 * 
 */
public class FreeDialerActivity extends FragmentActivity {

	// 界面控件
	private FragmentTabHost mTabHost = null;

	// 常量
	/** 要显示的Fragment列表 */
	private Class<?> fragmentArray[] = { FreeDialerCallLogFragment.class,
			FreeDialerContactsFragment.class };
	/** 要显示的tabs图片列表 */
	private int[] mImageView = new int[] { R.drawable.tab_dial_selector,
			R.drawable.tab_contact_selector };
	/** tabs内容列表 */
	private String mTextviewArray[] = { "拨打", "联系人" };

	private RelativeLayout rlCall;
	private TextView tvTitle;
	private TextView tvProgressing;

	private QuickAdapter<ContactItem> adapterSearch;

	private List<ContactBean> contacts = new ArrayList<ContactBean>();
	private List<ContactItem> matchResults;

	private AsyncQueryHandler asyncQueryHandler; // 异步查询数据库类对象
	private HashMap<String, Integer> alphaIndexer; // 字母索引
	private Map<Integer, ContactBean> contactIdMap = null;

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.activity_free_dialer);
		initView();
		initContacts();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {

		// 实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(),
				R.id.fl_tabhost_realcontent);
		// 将图片按钮中间的线去掉，设置为透明
		mTabHost.getTabWidget().setDividerDrawable(android.R.color.transparent);
		// 得到fragment的个数
		int count = fragmentArray.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
					.setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
		}

		tvTitle = (TextView) findViewById(R.id.tv_title);
		rlCall = (RelativeLayout) findViewById(R.id.rl_mybottom);
		rlCall.setVisibility(View.GONE);
		
		tvProgressing = (TextView)findViewById(R.id.tv_progressing);

		// 实例化
		asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());

		adapterSearch = new QuickAdapter<ContactItem>(this,
				R.layout.item_list_contact) {

			@Override
			protected void convert(BaseAdapterHelper helper, ContactItem item) {
				// TODO Auto-generated method stub

				StringBuilder nameBuilder = new StringBuilder(item.getName());
				nameBuilder.append(" ");
				nameBuilder.append(item.getHighlightPinyin());

				((TextView) helper.getView(R.id.tv_name)).setText(Html
						.fromHtml(nameBuilder.toString()));
				((TextView) helper.getView(R.id.tv_phone)).setText(Html
						.fromHtml(item.getHighlightNumber()));

			}
		};
	}

	private void initContacts() {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；
		// 查询的字段
		String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };
		// 按照sort_key升序查詢
		asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");
	}

	/**
	 * 匹配方法
	 * 
	 * @author liulei
	 * @date 2015-4-18
	 * @param query
	 *            void
	 */
	public void search(String query) {

		List<ContactItem> result = null;

		if (null == matchResults) {

			matchResults = new ArrayList<ContactItem>();

			T9MatchUtil.matchPinyinOrNumber(query, contacts, matchResults);

			result = matchResults;
		} else {

			result = new ArrayList<ContactItem>();
			T9MatchUtil.matchPinyinOrNumber(query, matchResults, result);
		}

		adapterSearch.clear();
		adapterSearch.addAll(result);
		Fragment currentFragment = mTabHost.getCurrentFragment();
		if (currentFragment instanceof RefreshMatchListView) {
			((RefreshMatchListView) currentFragment)
					.refreshMatchListView(adapterSearch);
		}
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public String getFragmentTitle() {
		return tvTitle.getText().toString();
	}

	public void showCallBtn() {
	}

	public void hideCallBtn() {
	}

	public List<ContactBean> getContacts() {
		return contacts;
	}

	public HashMap<String, Integer> getAlphaIndex() {
		return alphaIndexer;
	}

	/**
	 * 给Tab按钮设置图标
	 */
	private View getTabItemView(int index) {

		RelativeLayout tab = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.layout_tab_dial, null);

		ImageView imageView = (ImageView) tab.findViewById(R.id.iv_icon);
		imageView.setImageResource(mImageView[index]);
		TextView tv = (TextView) tab.findViewById(R.id.tv_name);
		tv.setText(mTextviewArray[index]);
		return tab;
	}

	public FragmentTabHost getmTabHost() {
		return mTabHost;
	}

	/**
	 * 筛选结果刷新接口
	 * 
	 * @author liulei
	 * @date 2015-4-18 上午11:24:00
	 * @version 1.0
	 */
	public interface RefreshMatchListView {
		/**
		 * 刷新列表显示筛选结果
		 * 
		 * @author liulei
		 * @date 2015-4-18 void
		 */
		public void refreshMatchListView(QuickAdapter<?> adapter);
	}

	/**
	 * 获取首字母
	 * 
	 * @param str
	 * @return
	 */
	public static String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式匹配
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 将小写字母转换为大写
		} else {
			return "#";
		}
	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		public void startQuery(int token, Object cookie, Uri uri,
				String[] projection, String selection, String[] selectionArgs,
				String orderBy) {
			// TODO Auto-generated method stub
			tvProgressing.setVisibility(View.VISIBLE);
			super.startQuery(token, cookie, uri, projection, selection,
					selectionArgs, orderBy);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

			tvProgressing.setVisibility(View.GONE);
			if (cursor != null && cursor.getCount() > 0) {

				contacts.clear();
				contactIdMap = new HashMap<Integer, ContactBean>();

				cursor.moveToFirst(); // 游标移动到第一项
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					int contactId = cursor.getInt(4);
					Long photoId = cursor.getLong(5);
					String lookUpKey = cursor.getString(6);

					if (contactIdMap.containsKey(contactId)) {
						// 无操作
					} else {
						// 创建联系人对象
						ContactBean item = new ContactBean();
						item.setName(name);
						item.setPhoneNum(number);
						item.setSortKey(sortKey);
						item.setPhotoId(photoId);
						item.setLookUpKey(lookUpKey);

						PinyinUtil.chineseStringToPinyinUnit(item);
						contacts.add(item);

						contactIdMap.put(contactId, item);
					}
				}
				if (contacts.size() > 0) {

					alphaIndexer = new HashMap<String, Integer>();
					for (int i = 0; i < contacts.size(); i++) {
						// 得到字母
						String name = getAlpha(contacts.get(i).getSortKey());
						if (!alphaIndexer.containsKey(name)) {
							alphaIndexer.put(name, i);
						}
					}

				}
			}

			super.onQueryComplete(token, cookie, cursor);
		}

	}
}
