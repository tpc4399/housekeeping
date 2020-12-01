package com.housekeeping.common.utils;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.SysOrderPlanDTO;
import com.housekeeping.admin.entity.SysOrderPlan;
import com.housekeeping.admin.vo.RulesMonthlyVo;
import com.housekeeping.common.entity.PeriodOfTime;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.tomcat.jni.Local;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.*;

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

	public static Object getMaxId(String table, IService context) {
		QueryWrapper wr = new QueryWrapper<>();
		wr.inSql("id", "select MAX(id) from "+table);
		System.out.println(table);
		System.out.println(context);
		Object maxObj = context.getOne(wr);
		return maxObj;
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

	public static String getRequestPrefix() throws UnknownHostException {
		String host = InetAddress.getLocalHost().getHostAddress();
		return "http://"+ host +":10010/api";
	}

	/***
	 * 时间段查重，相邻也不重复
	 * start1===========end1
	 * start2===========end2
	 * 重复的条件: start2∈(start1, end1) || end2∈(start1, end1)
	 * 		  || start1∈(start2, end2) || end1∈(start2, end2)
	 * 		  || (start1 = start2 && end1 = end2)
	 * @param existing
	 * @param target
	 * @return true表示重复，false表示不重复
	 */
	public static Boolean doRechecking(PeriodOfTime existing, PeriodOfTime target){
		LocalTime start1 = existing.getTimeSlotStart();
		Float length1 = existing.getTimeSlotLength();
		LocalTime end1 = start1.plusHours((int) (length1/1)).plusMinutes((long) ((length1%1)* 60));

		LocalTime start2 = target.getTimeSlotStart();
		Float length2 = target.getTimeSlotLength();
		LocalTime end2 = start2.plusHours((int) (length2/1)).plusMinutes((long) ((length2%1)* 60));
		return timeConflict(start2, start1, end1) ||
				timeConflict(end2, start1, end1) ||
				timeConflict(start1, start2, end2) ||
				timeConflict(end1, start2, end2) ||
				(start1.equals(start2) && end1.equals(end2));
	}

	/***
	 * localTime1∈(localTime2, localTime3)
	 * @param localTime1
	 * @param localTime2
	 * @param localTime3
	 * @return
	 */
	public static Boolean timeConflict(LocalTime localTime1, LocalTime localTime2, LocalTime localTime3){
		// 1>2 && 1<3
		return localTime1.isAfter(localTime2) && localTime1.isBefore(localTime3);
	}

	public static void main(String[] args) throws UnknownHostException {
		SysOrderPlanDTO sysOrderPlanDTO = new SysOrderPlanDTO();
		RulesMonthlyVo rulesMonthlyVo = new RulesMonthlyVo();
		rulesMonthlyVo.setStart(LocalDate.of(2020, 02,03));
		rulesMonthlyVo.setEnd(LocalDate.of(2021, 02,03));
		sysOrderPlanDTO.setRulesMonthlyVo(rulesMonthlyVo);
		Object ba = OptionalBean.ofNullable(sysOrderPlanDTO)
				.getBean(SysOrderPlanDTO::getRulesMonthlyVo)
				.getBean(RulesMonthlyVo::getStart).get();
		Object ba2 = OptionalBean.ofNullable(sysOrderPlanDTO)
				.getBean(SysOrderPlanDTO::getRulesMonthlyVo)
				.getBean(RulesMonthlyVo::getEnd).get();
		System.out.println("ssdaw");

		PasswordEncoder ENCODER = new BCryptPasswordEncoder();
		System.out.println(ENCODER.encode("Qawe1"));
		System.out.println(ENCODER.encode("Qawe1"));
		System.out.println(ENCODER.encode("Qawe1"));
		System.out.println(ENCODER.encode("Qawe1"));
		System.out.println(ENCODER.encode("Qawe1"));

		Integer a = 127;
		Integer b = 127;
		Integer c = 128;
		Integer d = 128;
		System.out.println(a == b);  //true
		System.out.println(c == d);	 //false

		Integer aa = -128;
		Integer bb = -128;
		Integer cc = -129;
		Integer dd = -129;
		System.out.println(aa == bb);  //true
		System.out.println(cc == dd);  //false

	}
}

