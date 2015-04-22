package com.t9search.model;

import java.util.List;

/**
 * contact item
 * 
 * @author liulei
 * @date 2015-4-22 上午9:41:27
 * @version 1.0
 */
public class ContactItem {

	private List<PinyinBaseUnit> chineses;// pinyin and Chinese characters
	private String name;
	private String phoneNum;
	private String highlightPinyin;// after t9 matching,the highlight string of pinyin
	private String highlightNumber;// after t9 matching,the highlight string of phone number
	private List<String> fullSpellings;// the list of full spelling
	private List<String> simpleSpellings;// the list of simple spelling

	public String getHighlightNumber() {
		return highlightNumber;
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

}
