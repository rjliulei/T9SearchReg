package com.finalmapservices.view;

import com.t9searchreg.sample.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * T9键盘
 * 
 * @author liulei
 */
public class DialKeyboard extends LinearLayout implements OnClickListener {

	private Context context;
	private KeyboardClickListener keyboardListener;

	/**
	 * Interface definition for a callback to be invoked when a button on the keyboard is clicked.
	 * 
	 * @author liulei
	 * @date 2015-4-17 下午2:48:41
	 * @version 1.0
	 */
	public interface KeyboardClickListener {
		public void deleteChar();

		public void addChar(String c);

		public void hideKeyboard();
	}

	public DialKeyboard(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		this.context = context;
		initView();
	}

	public DialKeyboard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

		this.context = context;
		initView();
	}

	public DialKeyboard(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 初始化相关控件
	 */
	private void initView() {

		// 便于xml预览
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.kc_input_keyboard, null);

		view.findViewById(R.id.DigitDeleteButton).setOnClickListener(this);
		view.findViewById(R.id.DigitEightButton).setOnClickListener(this);
		view.findViewById(R.id.DigitFiveButton).setOnClickListener(this);
		view.findViewById(R.id.DigitFourButton).setOnClickListener(this);
		view.findViewById(R.id.DigitHideButton).setOnClickListener(this);
		view.findViewById(R.id.DigitNineButton).setOnClickListener(this);
		view.findViewById(R.id.DigitOneButton).setOnClickListener(this);
		view.findViewById(R.id.DigitSevenButton).setOnClickListener(this);
		view.findViewById(R.id.DigitSixButton).setOnClickListener(this);
		view.findViewById(R.id.DigitThreeButton).setOnClickListener(this);
		view.findViewById(R.id.DigitTwoButton).setOnClickListener(this);
		view.findViewById(R.id.DigitZeroButton).setOnClickListener(this);
		this.addView(view);
	}

	public void setKeyboardClickListener(KeyboardClickListener l) {
		keyboardListener = l;
	}

	private String getCharById(int resId) {

		int index = 0;
		String[] btnNames = getResources().getStringArray(R.array.keyboard_btn);

		switch (resId) {
		case R.id.DigitEightButton:
			index = 8;
			break;
		case R.id.DigitFiveButton:
			index = 5;
			break;
		case R.id.DigitFourButton:
			index = 4;
			break;
		case R.id.DigitNineButton:
			index = 9;
			break;
		case R.id.DigitOneButton:
			index = 1;
			break;
		case R.id.DigitSevenButton:
			index = 7;
			break;
		case R.id.DigitSixButton:
			index = 6;
			break;
		case R.id.DigitThreeButton:
			index = 3;
			break;
		case R.id.DigitTwoButton:
			index = 2;
			break;
		case R.id.DigitZeroButton:
			index = 0;
			break;

		default:
			break;
		}

		return btnNames[index];
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (null == keyboardListener) {
			return;
		}

		switch (v.getId()) {
		case R.id.DigitDeleteButton:
			keyboardListener.deleteChar();
			break;

		case R.id.DigitHideButton:
			keyboardListener.hideKeyboard();
			break;
		default:
			keyboardListener.addChar(getCharById(v.getId()));
			break;
		}
	}

}
