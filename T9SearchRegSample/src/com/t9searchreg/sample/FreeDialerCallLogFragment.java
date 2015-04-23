package com.t9searchreg.sample;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.finalmapservices.adapter.base.BaseAdapterHelper;
import com.finalmapservices.adapter.base.QuickAdapter;
import com.finalmapservices.bean.CallLogBean;
import com.finalmapservices.view.DialKeyboard;
import com.finalmapservices.view.DialKeyboard.KeyboardClickListener;
import com.t9searchreg.sample.FreeDialerActivity.RefreshMatchListView;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FreeDialerCallLogFragment extends Fragment implements
		OnClickListener, KeyboardClickListener, RefreshMatchListView {

	private Activity context;
	private View rootView = null;

	private ImageView ivShowKeyboard;
	private DialKeyboard vKeyboard;

	private ListView lvCallLog;
	private QuickAdapter<CallLogBean> adapterCallLog;
	private AsyncQueryHandler asyncQueryCallLog;
	private String title;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context = getActivity();
		rootView = inflater.inflate(R.layout.fragment_dial, container, false);
		initView();
		initCallLog();
		return rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub

		if (!hidden) {

			((FreeDialerActivity) context).setTitle(title);
		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		((FreeDialerActivity) context).setTitle(title);
		super.onResume();
	}

	private void initView() {

		title = getString(R.string.title_free_dial_call_log);

		vKeyboard = (DialKeyboard) rootView.findViewById(R.id.keyboard);
		vKeyboard.setVisibility(View.GONE);
		vKeyboard.setKeyboardClickListener(this);

		ivShowKeyboard = (ImageView) rootView
				.findViewById(R.id.iv_keyboard_open_or_close);
		ivShowKeyboard.setVisibility(View.VISIBLE);
		ivShowKeyboard.setOnClickListener(this);

		lvCallLog = (ListView) rootView.findViewById(R.id.lv_calllog);

		asyncQueryCallLog = new AsyncQueryHandlerCallLog(
				context.getContentResolver());

		adapterCallLog = new QuickAdapter<CallLogBean>(context,
				R.layout.item_list_calllog) {

			@Override
			protected void convert(BaseAdapterHelper helper, CallLogBean item) {
				// TODO Auto-generated method stub

				((TextView) helper.getView(R.id.tv_name)).setText(item
						.getName());
				((TextView) helper.getView(R.id.tv_phone)).setText(item
						.getNumber());
				((TextView) helper.getView(R.id.tv_time)).setText(item
						.getDate());

				ImageView ivCallType = (ImageView) helper
						.getView(R.id.iv_call_type);
				switch (item.getType()) {
				case 1:
					ivCallType
							.setBackgroundResource(R.drawable.ic_calllog_outgoing_nomal);
					break;
				case 2:
					ivCallType
							.setBackgroundResource(R.drawable.ic_calllog_incomming_normal);
					break;
				case 3:
					ivCallType
							.setBackgroundResource(R.drawable.ic_calllog_missed_normal);
					break;
				}
			}
		};

		lvCallLog.setAdapter(adapterCallLog);
	}

	private void initCallLog() {

		Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
		// 查询的列
		String[] projection = { CallLog.Calls.DATE, // 日期
				CallLog.Calls.NUMBER, // 号码
				CallLog.Calls.TYPE, // 类型
				CallLog.Calls.CACHED_NAME, // 名字
				CallLog.Calls._ID, // id
		};

		asyncQueryCallLog.startQuery(0, null, uri, null, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER + " LIMIT 50");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_keyboard_open_or_close:
			ivShowKeyboard.setVisibility(View.GONE);
			vKeyboard.setVisibility(View.VISIBLE);
			vKeyboard.startAnimation(AnimationUtils.loadAnimation(context,
					R.anim.activity_pull_down_in));
			break;

		default:
			break;
		}
	}

	@Override
	public void deleteChar() {
		// TODO Auto-generated method stub

		if (title.contains(getString(R.string.title_free_dial_call_log))) {
			return;
		}

		if (title.length() > 1) {
			title = title.substring(0, title.length() - 1);
			((FreeDialerActivity) context).setTitle(title);
			((FreeDialerActivity) context).search(title);
		} else if (title.length() == 1) {
			title = getString(R.string.title_free_dial_call_log);
			((FreeDialerActivity) context).setTitle(title);
			lvCallLog.setAdapter(adapterCallLog);
		}
	}

	@Override
	public void addChar(String c) {
		// TODO Auto-generated method stub

		if (title.contains(getString(R.string.title_free_dial_call_log))) {
			title = "";
		}

		c = c.toLowerCase(Locale.CHINA);
		title += String.valueOf(c.charAt(0));
		((FreeDialerActivity) context).setTitle(title);
		((FreeDialerActivity) context).search(title);
	}

	@Override
	public void hideKeyboard() {
		// TODO Auto-generated method stub

		Animation animation = AnimationUtils.loadAnimation(context,
				R.anim.activity_push_down_out);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				vKeyboard.setVisibility(View.GONE);
				ivShowKeyboard.setVisibility(View.VISIBLE);
			}
		});
		vKeyboard.startAnimation(animation);
	}

	private class AsyncQueryHandlerCallLog extends AsyncQueryHandler {

		public AsyncQueryHandlerCallLog(ContentResolver cr) {
			super(cr);
		}
		
		@Override
		public void startQuery(int token, Object cookie, Uri uri,
				String[] projection, String selection, String[] selectionArgs,
				String orderBy) {
			// TODO Auto-generated method stub
			super.startQuery(token, cookie, uri, projection, selection, selectionArgs,
					orderBy);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				ArrayList<CallLogBean> callLogs = new ArrayList<CallLogBean>();
				SimpleDateFormat sfd = new SimpleDateFormat("MM-dd hh:mm");
				Date date;
				cursor.moveToFirst(); // 游标移动到第一项
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					date = new Date(cursor.getLong(cursor
							.getColumnIndex(CallLog.Calls.DATE)));
					String number = cursor.getString(cursor
							.getColumnIndex(CallLog.Calls.NUMBER));
					int type = cursor.getInt(cursor
							.getColumnIndex(CallLog.Calls.TYPE));
					String cachedName = cursor.getString(cursor
							.getColumnIndex(CallLog.Calls.CACHED_NAME));// 缓存的名称与电话号码，如果它的存在
					int id = cursor.getInt(cursor
							.getColumnIndex(CallLog.Calls._ID));

					CallLogBean callLogBean = new CallLogBean();
					callLogBean.setId(id);
					callLogBean.setNumber(number);
					callLogBean.setName(cachedName);
					if (null == cachedName || "".equals(cachedName)) {
						callLogBean.setName(number);
					}
					callLogBean.setType(type);
					callLogBean.setDate(sfd.format(date));

					callLogs.add(callLogBean);
				}
				if (callLogs.size() > 0) {
					adapterCallLog.addAll(callLogs);
				}
			}
			super.onQueryComplete(token, cookie, cursor);
		}
	}

	@Override
	public void refreshMatchListView(QuickAdapter<?> adapter) {
		// TODO Auto-generated method stub
		lvCallLog.setAdapter(adapter);
	}

}
