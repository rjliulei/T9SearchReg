package com.t9searchreg.model;

import java.util.List;

/**
 * pinyin base class
 * 
 * @author liulei
 * @date 2015-4-22 上午9:41:13
 * @version 1.0
 */
public class PinyinBaseUnit {
	private String originalString;// original Chinese string or other
	private List<String> pinyin;// one Chinese character maybe multiple pinyin
	private List<String> alpha;// alphabets of multiple pinyin

	private boolean isPinyin;// is pinyin or not
	private int startPosition;// position in the name

	private boolean isFirstName;// is first name or not

	public String getOriginalString() {
		return originalString;
	}

	public boolean isFirstName() {
		return isFirstName;
	}

	public void setFirstName(boolean isFirstName) {
		this.isFirstName = isFirstName;
	}

	public void setOriginalString(String originalString) {
		this.originalString = originalString;
	}

	public List<String> getPinyin() {
		return pinyin;
	}

	public void setPinyin(List<String> pinyin) {
		this.pinyin = pinyin;
	}

	public List<String> getAlpha() {
		return alpha;
	}

	public void setAlpha(List<String> alpha) {
		this.alpha = alpha;
	}

	public boolean isPinyin() {
		return isPinyin;
	}

	public void setPinyin(boolean isPinyin) {
		this.isPinyin = isPinyin;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

}
