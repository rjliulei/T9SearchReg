package com.t9searchreg.model;

import java.util.Comparator;
import java.util.List;

import android.R.integer;

/**
 * contact item
 * 
 * @author liulei
 * @date 2015-4-22 上午9:41:27
 * @version 1.0
 */
public class ContactItem {

	private List<PinyinBaseUnit> chineses;// pinyin and Chinese characters
	protected String name;
	protected String phoneNum;
	private String highlightPinyin;// after t9 matching,the highlight string of pinyin
	private String highlightNumber;// after t9 matching,the highlight string of phone number
	private List<String> fullSpellings;// the list of full spelling
	private List<String> simpleSpellings;// the list of simple spelling

	private int matchStartIndex;// start index of matched sub string,weight that matched by pinyin is more than weight
								// that matched by phone number
	public static final int MATCH_WEIGHT_PINYIN = 1 << 0;
	public static final int MATCH_WEIGHT_PHONE_NUMBER = 1 << 7;

	public String getHighlightNumber() {
		return highlightNumber;
	}

	public int getMatchStartIndex() {
		return matchStartIndex;
	}

	public void setMatchStartIndex(int matchStartIndex) {
		this.matchStartIndex = matchStartIndex;
	}

	public void setHighlightNumber(String highlightNumber) {
		this.highlightNumber = highlightNumber;
	}

	public List<PinyinBaseUnit> getChineses() {
		return chineses;
	}

	public void setChineses(List<PinyinBaseUnit> chineses) {
		this.chineses = chineses;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;

		this.highlightNumber = phoneNum;
	}

	public String getHighlightPinyin() {
		return highlightPinyin;
	}

	public void setHighlightPinyin(String highlightPinyin) {
		this.highlightPinyin = highlightPinyin;
	}

	public List<String> getFullSpellings() {
		return fullSpellings;
	}

	public void setFullSpellings(List<String> fullSpellings) {
		this.fullSpellings = fullSpellings;
	}

	public List<String> getSimpleSpellings() {
		return simpleSpellings;
	}

	public void setSimpleSpellings(List<String> simpleSpellings) {
		this.simpleSpellings = simpleSpellings;
	}

	public static Comparator<ContactItem> COMPARATOR = new Comparator<ContactItem>() {

		@Override
		public int compare(ContactItem lhs, ContactItem rhs) {

			return (lhs.matchStartIndex - rhs.matchStartIndex);
		}
	};
}
