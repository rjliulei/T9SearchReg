package com.t9search.util;

import java.util.ArrayList;
import java.util.List;

import com.t9search.model.ContactItem;
import com.t9search.model.PinyinBaseUnit;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {

	private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

	public static void main(String[] args) {
		ContactItem item = new ContactItem();
		item.setName("Hi单仇Hi费");
		item.setPhoneNum("1111111");

		chineseStringToPinyinUnit(item);
	}

	/**
	 * Convert from Chinese string to a series of PinyinBaseUnit
	 * 
	 * @param chineseString
	 * @param pinyinUnit
	 */
	public static void chineseStringToPinyinUnit(ContactItem item) {

		String chineseString = item.getName();

		if ((null == chineseString) || (null == item)) {
			return;
		}

		item.setFullSpellings(new ArrayList<String>());
		item.setSimpleSpellings(new ArrayList<String>());

		String chineseStr = chineseString.toLowerCase();

		if (null == format) {
			format = new HanyuPinyinOutputFormat();
		}

		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

		int chineseStringLength = chineseStr.length();
		StringBuffer nonPinyinString = new StringBuffer();
		PinyinBaseUnit pyUnit = null;
		List<PinyinBaseUnit> pinyinUnit = new ArrayList<PinyinBaseUnit>();

		String originalString = null;
		String[] pinyinStr = null;
		boolean lastChineseCharacters = true;
		boolean findFirstName = false;
		int startPosition = -1;

		for (int i = 0; i < chineseStringLength; i++) {
			char ch = chineseStr.charAt(i);
			try {
				pinyinStr = PinyinHelper.toHanyuPinyinStringArray(ch, format);
			} catch (BadHanyuPinyinOutputFormatCombination e) {

				e.printStackTrace();
			}

			if (null == pinyinStr) {
				if (lastChineseCharacters) {// 非汉字
					pyUnit = new PinyinBaseUnit();
					lastChineseCharacters = false;
					startPosition = i;
					nonPinyinString.delete(0, nonPinyinString.length());
				}

				nonPinyinString.append(ch);
			} else {
				if (!lastChineseCharacters) {
					// add continuous non-kanji characters to PinyinBaseUnit
					originalString = nonPinyinString.toString();
					String[] str = { nonPinyinString.toString() };
					addPinyinUnit(pinyinUnit, pyUnit, false, originalString,
							str, startPosition, item);
					nonPinyinString.delete(0, nonPinyinString.length());
					lastChineseCharacters = true;
				}

				// add single Chinese characters Pinyin(include Multiple Pinyin)
				// to PinyinBaseUnit
				pyUnit = new PinyinBaseUnit();
				startPosition = i;
				originalString = String.valueOf(ch);

				if (!findFirstName) {
					findFirstName = true;
					pyUnit.setFirstName(true);
				}
				addPinyinUnit(pinyinUnit, pyUnit, true, originalString,
						pinyinStr, startPosition, item);

			}
		}

		if (false == lastChineseCharacters) {
			// add continuous non-kanji characters to PinyinBaseUnit
			originalString = nonPinyinString.toString();
			String[] str = { nonPinyinString.toString() };
			addPinyinUnit(pinyinUnit, pyUnit, false, originalString, str,
					startPosition, item);
			nonPinyinString.delete(0, nonPinyinString.length());
			lastChineseCharacters = true;
		}

		item.setChineses(pinyinUnit);

		// for debug
		List<String> fullSpellingList = item.getFullSpellings();
		List<String> simSpellingList = item.getSimpleSpellings();
		for (int index = 0; index < fullSpellingList.size(); ++index) {
			System.out.println("contact:" + fullSpellingList.get(index) + "__"
					+ simSpellingList.get(index));
		}
	}

	private static void addPinyinUnit(List<PinyinBaseUnit> pinyinUnit,
			PinyinBaseUnit pyUnit, boolean pinyin, String originalString,
			String[] string, int startPosition, ContactItem item) {
		if ((null == pinyinUnit) || (null == pyUnit)
				|| (null == originalString) || (null == string)) {
			return;
		}

		initPinyinUnit(pyUnit, pinyin, originalString, string, startPosition);

		// compose full and simple spellings
		List<String> fullSpellings = item.getFullSpellings();
		List<String> simSpellings = item.getSimpleSpellings();

		List<String> pinyinStrings = pyUnit.getPinyin();
		List<String> alphaStrings = pyUnit.getAlpha();

		if (pyUnit.isFirstName()) {

			StringBuilder headFull = new StringBuilder();
			StringBuilder headSim = new StringBuilder();
			// full spelling
			if (fullSpellings.size() > 0) {
				headFull.append(fullSpellings.get(0));
			}

			// simple spelling
			if (simSpellings.size() > 0) {
				headSim.append(simSpellings.get(0));
			}

			fullSpellings.clear();
			simSpellings.clear();
			for (int index = 0; index < pinyinStrings.size(); ++index) {

				StringBuilder fullBuilder = new StringBuilder();
				fullBuilder.append(headFull).append(pinyinStrings.get(index));
				fullSpellings.add(fullBuilder.toString());

				StringBuilder simBuilder = new StringBuilder();
				simBuilder.append(headSim).append(alphaStrings.get(index));
				simSpellings.add(simBuilder.toString());
			}
		} else {

			int size = fullSpellings.size();

			if (0 == size) {
				fullSpellings.add(pyUnit.getPinyin().get(0));
				simSpellings.add(pyUnit.getAlpha().get(0));
			} else {

				for (int index = 0; index < size; ++index) {
					StringBuilder fullBuilder = new StringBuilder(
							fullSpellings.get(index));
					fullBuilder.append(pyUnit.getPinyin().get(0));
					fullSpellings.set(index, fullBuilder.toString());

					StringBuilder simBuilder = new StringBuilder(
							simSpellings.get(index));
					simBuilder.append(pyUnit.getAlpha().get(0));
					simSpellings.set(index, simBuilder.toString());
				}
			}
		}

		// add pinyin of Chinese character
		pinyinUnit.add(pyUnit);

		return;

	}

	private static void initPinyinUnit(PinyinBaseUnit pinyinUnit,
			boolean pinyin, String originalString, String[] string,
			int startPosition) {
		if ((null == pinyinUnit) || (null == originalString)
				|| (null == string)) {
			return;
		}

		// alpha/pinyin
		int i = 0;
		int j = 0;
		int k = 0;
		int strLength = string.length;
		pinyinUnit.setPinyin(pinyin);
		pinyinUnit.setStartPosition(startPosition);
		pinyinUnit.setOriginalString(originalString);

		ArrayList<String> pinyinList = new ArrayList<String>();
		ArrayList<String> alphaList = new ArrayList<String>();

		if (!pinyin || strLength <= 1) {// no more than one pinyin
			for (String pinyinTemp : string) {
				pinyinList.add(captureName(pinyinTemp));
				alphaList.add(String.valueOf(pinyinTemp.charAt(0)));
			}
		} else { // more than one pinyin.//we must delete the same pinyin
					// string,because pinyin without tone.

			if (pinyinUnit.isFirstName()) {

				pinyinList.add(captureName(string[0]));
				alphaList.add(String.valueOf(string[0].charAt(0)));

				for (j = 1; j < strLength; j++) {
					int curStringIndexlength = pinyinList.size();
					for (k = 0; k < curStringIndexlength; k++) {
						if (pinyinList.get(k).equals(string[j])) {
							break;
						}
					}

					if (k == curStringIndexlength) {
						pinyinList.add(captureName(string[j]));
						alphaList.add(String.valueOf(string[j].charAt(0)));
					}
				}

			} else {
				// no need to get multiple pinyin
				pinyinList.add(captureName(string[0]));
				alphaList.add(String.valueOf(string[0].charAt(0)));
			}

		}

		pinyinUnit.setPinyin(pinyinList);
		pinyinUnit.setAlpha(alphaList);
	}

	/**
	 * capture the first alphabet
	 * 
	 * @author liulei
	 * @date 2015-4-22
	 * @param name
	 * @return String
	 */
	public static String captureName(String name) {
		// name = name.substring(0, 1).toUpperCase() + name.substring(1);
		// return name;
		char[] cs = name.toCharArray();

		if (cs[0] >= 97 && cs[0] <= 122) {
			cs[0] -= 32;
		}
		return String.valueOf(cs);

	}
}
