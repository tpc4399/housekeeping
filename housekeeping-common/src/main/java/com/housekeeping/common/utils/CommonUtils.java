package com.housekeeping.common.utils;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.SysOrderPlanDTO;
import com.housekeeping.admin.vo.RecommendedEmployeesVo;
import com.housekeeping.admin.vo.RulesMonthlyVo;
import com.housekeeping.common.entity.PeriodOfTime;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

	/***
	 * 时间段包含a包含b
	 * a start1================end1
	 * b   start2===========end2
	 * 包含的条件  start1<=start2 && end2<=end1
	 * 	也就是  !start1>start2 && !end2>end1
	 * @param a
	 * @param b
	 * @return
	 */
	public static Boolean periodOfTimeAContainsPeriodOfTimeB(PeriodOfTime a, PeriodOfTime b){
		LocalTime start1 = a.getTimeSlotStart();
		Float length1 = a.getTimeSlotLength();
		LocalTime end1 = start1.plusHours((int) (length1/1)).plusMinutes((long) ((length1%1)* 60));

		LocalTime start2 = b.getTimeSlotStart();
		Float length2 = b.getTimeSlotLength();
		LocalTime end2 = start2.plusHours((int) (length2/1)).plusMinutes((long) ((length2%1)* 60));

		if (!start1.isAfter(start2) && !end2.isAfter(end1)){
			return true;
		}else{
			return false;
		}
	}

	/***
	 * 得到相交的时间段
	 * @param a
	 * @param b
	 * @return
	 */
	public static PeriodOfTime getIntersectionTimeSlot(PeriodOfTime a, PeriodOfTime b){
		LocalTime start1 = a.getTimeSlotStart();
		Float length1 = a.getTimeSlotLength();
		LocalTime end1 = start1.plusHours((int) (length1/1)).plusMinutes((long) ((length1%1)* 60));

		LocalTime start2 = b.getTimeSlotStart();
		Float length2 = b.getTimeSlotLength();
		LocalTime end2 = start2.plusHours((int) (length2/1)).plusMinutes((long) ((length2%1)* 60));

		LocalTime start;
		LocalTime end;
		start = start1.isBefore(start2) ? start2 : start1;
		end = end1.isBefore(end2) ? end1 : end2;

		int second = end.toSecondOfDay() - start.toSecondOfDay();

		return new PeriodOfTime(start, (float)second/3600);
	}

	/***
	 *
	 * @param latitude1 坐標點1 纬度
	 * @param longitude1 坐標點1 经度
	 * @param latitude2 坐標點2 纬度
	 * @param longitude2 坐標點2 经度
	 * @return
	 */
	public static String getInstanceByPoint(String latitude1,
											String longitude1,
											String latitude2,
											String longitude2){
		GlobalCoordinates source = new GlobalCoordinates(Double.parseDouble(latitude1), Double.parseDouble(longitude1));
		GlobalCoordinates target = new GlobalCoordinates(Double.parseDouble(latitude2), Double.parseDouble(longitude2));

		Double meter = getDistanceMeter(source, target, Ellipsoid.Sphere);

		return meter.toString();
	}
	public static double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid){

		//创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
		GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);

		return geoCurve.getEllipsoidalDistance();
	}


	//Object转Map
	public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Class<?> clazz = obj.getClass();
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			String fieldName = field.getName();
			Object value = field.get(obj);
			if (value == null) {
				value = "";
			}
			map.put(fieldName, value);
		}
		return map;
	}

	//Map转Object
	public static Object mapToObject(Map<Object, Object> map, Class<?> beanClass) throws Exception {
		if (map == null)
			return null;
		Object obj = beanClass.newInstance();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			int mod = field.getModifiers();
			if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
				continue;
			}
			field.setAccessible(true);
			if (map.containsKey(field.getName())) {
				Object value = null;
				if (field.getType().equals(LocalDate.class)){
					String str = (String) map.get(field.getName());
					LocalDate localDate = str.length() == 0 ? null : LocalDate.of(Integer.valueOf(str.substring(0, 4)), Integer.valueOf(str.substring(5, 7)), Integer.valueOf(str.substring(8, 10)));
					value = localDate;
				}else if (field.getType().equals(LocalTime.class)){
					String str = (String) map.get(field.getName());
					LocalTime localTime = str.length() == 0 ? null : LocalTime.of(Integer.valueOf(str.substring(0, 2)), Integer.valueOf(str.substring(3, 5)), Integer.valueOf(str.substring(6, 8)));
					value = localTime;
				}else if (field.getType().equals(LocalDateTime.class)){
					String str = (String) map.get(field.getName());
					LocalDateTime localDateTime = str.length() == 0 ? null : LocalDateTime.of(
							Integer.valueOf(str.substring(0, 4)),
							Integer.valueOf(str.substring(5, 7)),
							Integer.valueOf(str.substring(8, 10)),
							Integer.valueOf(str.substring(11, 13)),
							Integer.valueOf(str.substring(14, 16)),
							Integer.valueOf(str.substring(17, 19))
					);
					value = localDateTime;
				}else if (field.getType().equals(Float.class)){
					value = new Float((Double) map.get(field.getName()));
				}else {
					value = map.get(field.getName());
				}
				field.set(obj, value);
			}
		}
		return obj;
	}

	//list转string 空格隔开
	public static String listToString(List<Object> list){
		StringBuilder sb = new StringBuilder();
		list.forEach(x->{
			sb.append(x.toString());
			sb.append(" ");
		});
		return new String(sb).trim();
	}

	public static String arrToString(Object[] arr){
		StringBuilder sb = new StringBuilder();
		for (Object o : arr) {
			sb.append(o.toString());
			sb.append(" ");
		}
		return new String(sb).trim();
	}

	/* 工作内容string 切割成List */
	public static List<Integer> stringToList(String jobId){
		List<Integer> list = new ArrayList<>();
		String[] strings = jobId.split(" ");
		for (int i = 0; i < strings.length; i++) {
			list.add(Integer.valueOf(strings[i]));
		}
		return list;
	}

	public static void main(String[] args) throws UnknownHostException {

		long section0 = System.currentTimeMillis();
		//默认升序 desc降序
		SortListUtil<RecommendedEmployeesVo> sortList = new SortListUtil<RecommendedEmployeesVo>();
		List<RecommendedEmployeesVo> reList = new ArrayList<>();
		for (int i = 0; i < 1000000; i++) {
			RecommendedEmployeesVo re = new RecommendedEmployeesVo(i, new Random().nextInt(10000), new BigDecimal(new Random().nextInt(10000)), new Random().nextFloat());
			reList.add(re);
		}
		long section1 = System.currentTimeMillis();
		Collections.shuffle(reList);
		long section2 = System.currentTimeMillis();
		System.out.println("==========原来的顺序==========");
//		for (Iterator<RecommendedEmployeesVo> iterator = reList.iterator(); iterator.hasNext(); ) {
//			RecommendedEmployeesVo re = iterator.next();
//			System.out.println(re);
//		}
		long section3 = System.currentTimeMillis();
		System.out.println("======按照instance排序=======");
		sortList.Sort(reList, "getInstance", null);
//		for (Iterator<RecommendedEmployeesVo> iterator = reList.iterator(); iterator.hasNext(); ) {
//			RecommendedEmployeesVo re = iterator.next();
//			System.out.println(re);
//		}
		long section4 = System.currentTimeMillis();

		System.out.println("========按照price排序========");
		sortList.Sort(reList, "getPrice", null);
//		for (Iterator<RecommendedEmployeesVo> iterator = reList.iterator(); iterator.hasNext(); ) {
//			RecommendedEmployeesVo re = iterator.next();
//			System.out.println(re);
//		}
		long section5 = System.currentTimeMillis();

		System.out.println("========按照score排序========");
		sortList.Sort(reList, "getScore", null);
//		for (Iterator<RecommendedEmployeesVo> iterator = reList.iterator(); iterator.hasNext(); ) {
//			RecommendedEmployeesVo re = iterator.next();
//			System.out.println(re);
//		}
		long section6 = System.currentTimeMillis();

		System.out.println("总时间："+(section6 - section0)+"ms");
		System.out.println("生成数据的时间："+(section1 - section0)+"ms");
		System.out.println("打乱数据的时间："+(section2 - section1)+"ms");
		System.out.println("输出数据的时间："+(section3 - section2)+"ms");
		System.out.println("按照instance排序+输出数据的时间："+(section4 - section3)+"ms");
		System.out.println("按照price排序+输出数据的时间："+(section5 - section4)+"ms");
		System.out.println("按照score排序+输出数据的时间："+(section6 - section5)+"ms");



	}

}

