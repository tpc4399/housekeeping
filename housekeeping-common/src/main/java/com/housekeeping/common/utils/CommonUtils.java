package com.housekeeping.common.utils;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 校验对象是否为空的工具类
 */
@SuppressWarnings("rawtypes")
public class CommonUtils {

	public static String toString(Object object) {
		return object == null ? "" : object.toString();
	}

	public static boolean isEmpty(Collection collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isEmpty(Map map) {
		return map == null || map.isEmpty();
	}

	public static boolean isEmpty(String string) {
		return toString(string).isEmpty();
	}

	public static boolean isNotEmpty(String string) {
		return !toString(string).isEmpty();
	}

	public static boolean isEmptyTrim(String string) {
		return toString(string).trim().isEmpty();
	}

	public static boolean isNotEmptyTrim(String string) {
		return !toString(string).trim().isEmpty();
	}

	public static boolean isEmpty(Object object) {
		return toString(object).isEmpty();
	}

	public static boolean isNotEmpty(Object object) {
		return !toString(object).isEmpty();
	}

	public static boolean isEmptyTrim(Object object) {
		return toString(object).trim().isEmpty();
	}

	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}
	public static  String getTimeYYMMDD(LocalDateTime time) {
		return time.toString().substring(0,10);
	}
	public static  String getTimeYYMMDDhhmmss(LocalDateTime time) {
		return time.toString().substring(0,19).replace("T"," ");
	}

	public static  Object getMaxId(String table, IService context) {
		QueryWrapper wr = new QueryWrapper<>();
		wr.inSql("id", "select MAX(id) from "+table);
		System.out.println(table);
		System.out.println(context);
		Object maxIepId = context.getOne(wr);
		return maxIepId;
	}

	/***
	 *
	 * @param list 获取到的List
	 * @param str1 list为空返回的消息
	 * @param str2 list只有一条返回的消息
	 * @param str3 list有多条返回的消息
	 * @return 消息
	 */
	public static R selectOneHandle(List list, String str1, String str2, String str3){
		if (list.size() == 0){
			return R.failed(str1);
		}else if (list.size() == 1){
			return R.ok(list.get(0), str2);
		}else {
			return R.failed(str3);
		}
	}

	/***
	 * 生成六位隨機驗證碼
	 * @return
	 */
	public static String getRandomSixCode(){
		Random dom = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			Integer s = dom.nextInt(10);
			sb.append(s);
		}
		System.out.printf(sb.toString());
		return sb.toString();
	}

	/***
	 * 由手机号前缀获取区号 如 "+886" => "886"
	 * @param phonePrefix "+886"&"886"
	 * @return String "886"
	 */
	public static String getPhonePrefix(String phonePrefix){
		if (phonePrefix.startsWith("+")){
			return phonePrefix.substring(1, phonePrefix.length());
		}else {
			return phonePrefix;
		}
	}

	/**
	 * 获取神秘代码
	 * @return
	 */
	public static String getMysteriousCode(){
		StringBuilder sb = new StringBuilder("");
		Random random = new Random();
		for (int i = 0; i < 12; i++) {
			int index = random.nextInt(100)%CommonConstants.emn.length;
			sb.append(CommonConstants.emn[index]);
		}
		return new String(sb);
	}

	public static void main(String[] args) {
		String s = getMysteriousCode();
		System.out.println(s);
	}
}

