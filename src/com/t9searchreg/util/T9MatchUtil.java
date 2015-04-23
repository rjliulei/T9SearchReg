package com.t9searchreg.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.os.IInterface;

import com.hp.hpl.sparta.Document.Index;
import com.t9searchreg.model.ContactItem;

/**
 * 
 * @author liulei
 * @date 2015-4-22 下午3:31:57
 * @version 1.0
 */
public class T9MatchUtil {

	/**
	 * regular expression of ignoring characters
	 */
	private static final String RE_IGNORE = "0|1|#|(\\*)";

	/**
	 * regular expression to split characters
	 */
	private static final String RE_SPLIT = "2|3|4|5|6|7|8|9";

	// highlight
	protected static final String PRE_TAG = "<font color='red'>";
	protected static final String POST_TAG = "</font>";

	private static final int MAX_MATCH_COUNT = 20;

	public static void main(String[] args) {
		ArrayList<ContactItem> target = new ArrayList<ContactItem>();
		ContactItem item = new ContactItem();
		item.setName("Hi柳磊");
		item.setPhoneNum("150067923");
		PinyinUtil.chineseStringToPinyinUnit(item);
		target.add(item);

		item = new ContactItem();
		item.setName("仇学费");
		item.setPhoneNum("242345545");
		PinyinUtil.chineseStringToPinyinUnit(item);
		target.add(item);

		ArrayList<ContactItem> result = new ArrayList<ContactItem>();
		matchPinyinOrNumber("55", target, result);
		for (ContactItem temp : result) {
			System.out.println(temp.getHighlightPinyin() + "-"
					+ temp.getHighlightNumber());
		}
	}

	/**
	 * 1.分三层匹配：声母、全拼（包括非汉字）、电话号码 2.优先级：声母>全拼>电话号码，即有一种匹配成功即不再进行其他匹配
	 * 
	 * @author liulei
	 * @date 2015-4-22
	 * @param keyword
	 * @param target
	 * @param result
	 *            匹配结果
	 * @return boolean
	 */
	public static boolean matchPinyinOrNumber(String keyword,
			final List<? extends ContactItem> target, List<ContactItem> result) {

		if (null == keyword || null == target || null == result)
			return false;

		result.clear();

		int count = 0;
		String[] matches = getRegularExpression(keyword);
		Pattern pattern = null;
		if (null != matches[1]) {

			pattern = Pattern.compile(matches[1]);
		}

		boolean isMatched = false;

		for (ContactItem item : target) {

			// if (count >= MAX_MATCH_COUNT) {
			//
			// break;
			// }

			if (null == matches[1]) {
				// full spelling match
				isMatched = matchFullSpelling(item, keyword);

			} else {
				// simple spelling match
				isMatched = matchSimpleSpellingReg(item, pattern);

				// 关键字只有一个时不进行全拼匹配，匹配成功后通过判断首字母大写
				if (keyword.length() > 1) {
					if (!isMatched) {
						// full spelling match
						isMatched = matchFullSpellingReg(item, pattern);
					} else {

						result.add(item);
						count++;
						isMatched = false;
						continue;
					}
				}

			}

			// phone number match
			if (!isMatched) {

				// phone number match
				if (matchPhoneNumber(item, keyword)) {
					result.add(item);
					count++;
				}
			} else {

				result.add(item);
				count++;
				isMatched = false;
				continue;
			}

		}

		// sort list
		Collections.sort(result, ContactItem.COMPARATOR);

		return count > 0;
	}

	/**
	 * match simple spelling by regular expression
	 * 
	 * @author liulei
	 * @date 2015-4-22
	 * @param item
	 * @param pattern
	 * @return boolean
	 */
	private static boolean matchSimpleSpellingReg(ContactItem item,
			Pattern pattern) {

		boolean isMatched = false;
		Matcher matcher = null;

		List<String> simSpellings = item.getSimpleSpellings();
		String match = null;
		int index = 0;
		for (; index < simSpellings.size(); ++index) {

			String sim = simSpellings.get(index);
			matcher = pattern.matcher(sim);

			if (matcher.find()) {
				match = matcher.group().toUpperCase();
				isMatched = true;
				break;
			}
		}

		if (isMatched) {

			String pinyin = item.getFullSpellings().get(index);

			// highlight
			StringBuilder stringBuilder = new StringBuilder(pinyin.length()
					+ match.length()
					* (1 + PRE_TAG.length() + POST_TAG.length()));
			for (int i = 0, j = 0; i < pinyin.length(); i++) {// 循环高亮首字母
				char c = pinyin.charAt(i);
				if (j < match.length()) {
					if (c != match.charAt(j)) {
						stringBuilder.append(c);
					} else {
						stringBuilder.append(PRE_TAG).append(c)
								.append(POST_TAG);

						if (0 == j) {
							item.setMatchStartIndex(i);
							item.setHighlightNumber(item.getPhoneNum());
						}

						j++;
					}
				} else {
					stringBuilder.append(pinyin.substring(i));
					break;
				}
			}

			item.setHighlightPinyin(stringBuilder.toString());
		} else {
			item.setHighlightPinyin(item.getFullSpellings().get(0));
		}

		return isMatched;
	}

	/**
	 * match full spelling by regular expression
	 * 
	 * @author liulei
	 * @date 2015-4-22
	 * @param item
	 * @param pattern
	 * @return boolean
	 */
	private static boolean matchFullSpellingReg(ContactItem item,
			Pattern pattern) {

		boolean isMatched = false;
		Matcher matcher = null;

		List<String> fullSpellings = item.getFullSpellings();
		String match = null;
		int start = 0;
		for (String full : fullSpellings) {

			matcher = pattern.matcher(full.toLowerCase());

			if (matcher.find()) {
				match = matcher.group();
				start = matcher.start();

				// 首字母不为大写，则匹配失败
				if (!Character.isUpperCase(full.charAt(start))) {
					continue;
				}

				item.setMatchStartIndex(start);
				item.setHighlightNumber(item.getPhoneNum());

				// highlight
				StringBuilder stringBuilder = new StringBuilder(full.length()
						+ (1 + PRE_TAG.length() + POST_TAG.length()));

				if (0 != start) {
					stringBuilder.append(full.substring(0, start));
				}
				stringBuilder.append(PRE_TAG);
				stringBuilder.append(full.substring(start,
						start + match.length()));
				stringBuilder.append(POST_TAG);
				stringBuilder.append(full.substring(start + match.length()));

				item.setHighlightPinyin(stringBuilder.toString());

				isMatched = true;
				break;
			}
		}

		if (!isMatched) {
			item.setHighlightPinyin(fullSpellings.get(0));
		}

		return isMatched;
	}

	/**
	 * match full spelling by keyword
	 * 
	 * @author liulei
	 * @date 2015-4-22
	 * @param item
	 * @param keyword
	 * @return boolean
	 */
	private static boolean matchFullSpelling(ContactItem item, String keyword) {

		boolean isMatched = false;
		List<String> fullSpellings = item.getFullSpellings();

		for (String full : fullSpellings) {
			int index = full.toLowerCase().indexOf(keyword);
			if (-1 != index) {

				item.setMatchStartIndex(index);
				item.setHighlightNumber(item.getPhoneNum());

				StringBuilder builder = (0 == index ? new StringBuilder()
						: new StringBuilder(full.substring(0, index)));

				builder.append(PRE_TAG + keyword + POST_TAG
						+ full.substring(index + keyword.length()));

				item.setHighlightPinyin(builder.toString());
				isMatched = true;
				break;
			}
		}

		if (!isMatched) {

			item.setHighlightPinyin(fullSpellings.get(0));
		}

		return isMatched;
	}

	/**
	 * match phone number by keyword
	 * 
	 * @author liulei
	 * @date 2015-4-22
	 * @param item
	 * @param keyword
	 * @return boolean
	 */
	private static boolean matchPhoneNumber(ContactItem item, String keyword) {

		boolean isMatched = false;
		String phoneNum = item.getPhoneNum();
		int index = item.getPhoneNum().indexOf(keyword);

		if (-1 != index) {

			item.setMatchStartIndex(ContactItem.MATCH_WEIGHT_PHONE_NUMBER
					+ index);
			isMatched = true;
			// highlight
			StringBuilder builder = (0 == index ? new StringBuilder()
					: new StringBuilder(phoneNum.substring(0, index)));

			builder.append(PRE_TAG + keyword + POST_TAG
					+ phoneNum.substring(index + keyword.length()));
			item.setHighlightNumber(builder.toString());
		} else {
			item.setHighlightNumber(item.getPhoneNum());
		}

		return isMatched;
	}

	/**
	 * convert valid keyword to regular expression or not
	 * 
	 * @author liulei
	 * @date 2015-4-22
	 * @param keyword
	 * @return String[2] 0:original keyword 1:regular expression
	 */
	private static String[] getRegularExpression(String keyword) {

		if (null == keyword)
			return null;

		String[] result = new String[2];
		result[0] = keyword;

		// check keyword
		Pattern pattern = Pattern.compile(RE_IGNORE);
		Matcher matcher = pattern.matcher(keyword);

		if (matcher.find()) {
			return result;
		}

		StringBuilder reg = new StringBuilder();
		pattern = Pattern.compile(RE_SPLIT);
		matcher = pattern.matcher(keyword);
		while (matcher.find()) {

			int index = Integer.valueOf(matcher.group());
			switch (index) {
			case 2:
				reg.append("[2abc]");
				break;
			case 3:
				reg.append("[3def]");
				break;
			case 4:
				reg.append("[4ghi]");
				break;
			case 5:
				reg.append("[5jkl]");
				break;
			case 6:
				reg.append("[6mno]");
				break;
			case 7:
				reg.append("[7pqrs]");
				break;
			case 8:
				reg.append("[8tuv]");
				break;
			case 9:
			default:
				reg.append("[9wxyz]");
				break;
			}
		}

		result[1] = reg.toString();

		return result;
	}
}
