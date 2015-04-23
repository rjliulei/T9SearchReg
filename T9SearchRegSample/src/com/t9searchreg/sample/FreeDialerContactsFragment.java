package com.t9searchreg.sample;

import java.util.Locale;

import com.finalmapservices.adapter.base.BaseAdapterHelper;
import com.finalmapservices.adapter.base.QuickAdapter;
import com.finalmapservices.bean.ContactBean;
import com.finalmapservices.view.DialKeyboard;
import com.finalmapservices.view.DialKeyboard.KeyboardClickListener;
import com.finalmapservices.view.QuickAlphabeticBar;
import com.t9searchreg.sample.FreeDialerActivity.RefreshMatchListView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

public class FreeDialerContactsFragment extends Fragment implements
		OnClickListener, KeyboardClickListener, RefreshMatchListView {

	private Activity context;
	private View rootView = null;

	private TextView etSearch;
	private String keyword;
	private DialKeyboard vKeyboard;
	private ListView lvContacts;
	private QuickAdapter<ContactBean> adapterContacts;

	private QuickAlphabeticBar alphabeticBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context = getActivity();
		rootView = inflater.inflate(R.layout.fragment_contacts_list, container,
				false);
		initView();
		initContact();
		return rootView;
	}

	private void initView() {

		etSearch = (TextView) rootView.findViewById(R.id.et_keyword);
		etSearch.setOnClickListener(this);

		vKeyboard = (DialKeyboard) rootView.findViewById(R.id.keyboard);
		vKeyboard.setVisibility(View.GONE);
		vKeyboard.setKeyboardClickListener(this);

		lvContacts = (ListView) rootView.findViewById(R.id.lv_contacts);

		adapterContacts = new QuickAdapter<ContactBean>(context,
				R.layout.item_list_contact) {

			@Override
			protected void convert(BaseAdapterHelper helper, ContactBean item) {
				// TODO Auto-generated method stub
				String name = item.getName();
				String number = item.getPhoneNum();
				((TextView) helper.getView(R.id.tv_name)).setText(name);
				((TextView) helper.getView(R.id.tv_phone)).setText(number);
				// holder.quickContactBadge.assignContactUri(Contacts.getLookupUri(
				// item.getContactId(), item.getLookUpKey()));
				// if (0 == item.getPhotoId()) {
				// holder.quickContactBadge.setImageResource(R.drawable.touxiang);
				// } else {
				// Uri uri = ContentUris.withAppendedId(
				// ContactsContract.Contacts.CONTENT_URI,
				// item.getContactId());
				// InputStream input = ContactsContract.Contacts
				// .openContactPhotoInputStream(ctx.getContentResolver(), uri);
				// Bitmap contactPhoto = BitmapFactory.decodeStream(input);
				// holder.quickContactBadge.setImageBitmap(contactPhoto);
				// }
				// 当前字母
				String currentStr = FreeDialerActivity.getAlpha(item
						.getSortKey());
				int position = helper.getPosition();
				// 前面的字母
				String previewStr = (position - 1) >= 0 ? FreeDialerActivity
						.getAlpha(getItem(position - 1).getSortKey()) : " ";

				TextView alpha = ((TextView) helper.getView(R.id.tv_alpha));
				if (!previewStr.equals(currentStr)) {
					alpha.setVisibility(View.VISIBLE);
					alpha.setText(currentStr);
				} else {
					alpha.setVisibility(View.GONE);
				}
			}

		};
		lvContacts.setAdapter(adapterContacts);

		alphabeticBar = (QuickAlphabeticBar) rootView
				.findViewById(R.id.fast_scroller);
		alphabeticBar.init(rootView);
		alphabeticBar.setListView(lvContacts);
		alphabeticBar.setVisibility(View.VISIBLE);

	}

	private void initContact() {
		adapterContacts.clear();
		adapterContacts.addAll(((FreeDialerActivity) context).getContacts());

		alphabeticBar.setAlphaIndexer(((FreeDialerActivity) context)
				.getAlphaIndex());
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		((FreeDialerActivity) context)
				.setTitle(getString(R.string.title_free_dial_contacts));
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.et_keyword:

			if (vKeyboard.getVisibility() == View.GONE) {
				vKeyboard.setVisibility(View.VISIBLE);
				vKeyboard.startAnimation(AnimationUtils.loadAnimation(context,
						R.anim.activity_pull_down_in));
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub

		if (!hidden) {

			((FreeDialerActivity) context)
					.setTitle(getString(R.string.title_free_dial_contacts));

		}
		super.onHiddenChanged(hidden);
	}

	@Override
	public void deleteChar() {
		// TODO Auto-generated method stub

		if (null == keyword || keyword.isEmpty()) {
			return;
		}

		if (keyword.length() > 1) {
			keyword = keyword.substring(0, keyword.length() - 1);
			((FreeDialerActivity) context).search(keyword);
		} else if (keyword.length() == 1) {
			keyword = "";
			lvContacts.setAdapter(adapterContacts);
			alphabeticBar.setEnabled(true);
		}

		etSearch.setText(keyword);
	}

	@Override
	public void addChar(String c) {
		// TODO Auto-generated method stub

		c = c.toLowerCase(Locale.CHINA);
		if (null == keyword) {
			keyword = String.valueOf(c.charAt(0));
		} else {
			keyword += String.valueOf(c.charAt(0));
		}

		etSearch.setText(keyword);
		((FreeDialerActivity) context).search(keyword);
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

			}
		});
		vKeyboard.startAnimation(animation);
	}

	@Override
	public void refreshMatchListView(QuickAdapter<?> adapter) {
		// TODO Auto-generated method stub
		alphabeticBar.setEnabled(false);
		lvContacts.setAdapter(adapter);
	}
}
