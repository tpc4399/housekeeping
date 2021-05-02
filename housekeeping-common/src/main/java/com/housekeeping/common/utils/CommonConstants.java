package com.housekeeping.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.housekeeping.common.annotation.RolesEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author su
 * @create 2020/10/29 16:48
 */
public interface CommonConstants {
	/** redis的key前綴 */
	String NEWPHONE_KEY_BY_PHONE = "HK_NEWPHONE_KEY_BY_PHONE";
	/** redis的key前綴 */
	String CHANGE_KEY_BY_PHONE = "HK_CHANGE_KEY_BY_PHONE";
	/** redis的key前綴 */
	String LOGIN_KEY_BY_PHONE = "HK_LOGIN_KEY_BY_PHONE";
	/** redis的key前綴 */
	String FORGET_KEY_BY_PHONE = "HK_FORGET_KEY_BY_PHONE";
	/** redis的key前綴 */
	String REGISTER_KEY_BY_PHONE = "HK_REGISTER_KEY_BY_PHONE";
	/** redis驗證碼緩存有效時間，分鐘 */
	Integer VALID_TIME_MINUTES = 3;
	/** 正式環境的存儲公司logoUrl的絕對路徑 */
	String HK_COMPANY_LOGO_ABSTRACT_PATH_PREFIX_PROV = "HKFile/CompanyLogoImg/userId=";
	/** 正式環境的存儲公司组图片Url的絕對路徑 */
	String HK_GROUP_URL_ABSTRACT_PATH_PREFIX_PROV = "HKFile/GroupLogoImg";
	/** 正式環境的存儲聊天图片的絕對路徑 */
	String HK_IM_PHOTO_ABSTRACT_PATH_PREFIX_PROV = "HKFile/ImPhoto/userId=";
	/** 正式環境的存儲公司五张图片的絕對路徑 */
	String HK_COMPANY_IMG_ABSTRACT_PATH_PREFIX_PROV = "HKFile/CompanyFiveImg/userId=";
	/** 正式環境的存儲员工头像的絕對路徑 */
	String HK_EMPLOYEES_HEAD_ABSTRACT_PATH_PREFIX_PROV = "HKFile/EmployeesHead/userId=";
	/** 正式環境的存儲经理头像的絕對路徑 */
	String HK_MANAGER_HEAD_ABSTRACT_PATH_PREFIX_PROV = "HKFile/ManagerHead/userId=";
	/** 正式環境的存儲客户头像的絕對路徑 */
	String HK_CUSTOMER_HEAD_ABSTRACT_PATH_PREFIX_PROV = "HKFile/CustomerHead/userId=";
	/** 正式環境的订单评价照片的絕對路徑 */
	String HK_ORDER_EVALUATION_IMAGE_ABSTRACT_PATH_PREFIX_PROV = "HKFile/OrderEvaluationImage/orderId=";
	/** 正式環境的经理员工登入二维码存放地 */
	String HK_LOGIN_IN_QR_ABSTRACT_PATH_PREFIX_PROV = "HKFile/QrCode";
	/** 正式環境的存儲包工五张照片的絕對路徑 */
	String HK_CONTRACT_PHOTOS_ABSTRACT_PATH_PREFIX_PROV = "HKFile/ContractPhotos/";
	/** 正式環境的存儲订单照片的多张图片路径 */
	String HK_ORDER_PHOTOS_ABSTRACT_PATH_PREFIX_PROV = "HKFile/Order/number=";

	/** 神秘代码的零件 */
	String[] emn = new String[]{
			"0","1","2","3","4","5","6","7","8","9",
			"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
			"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"
	};
	/** 员工的神秘代码前缀 */
	String LOGIN_EMPLOYEES_PREFIX = "LOGIN_EMPLOYEES_";
	/** 经理的神秘代码前缀 */
	String LOGIN_MANAGER_PREFIX = "LOGIN_MANAGER_";
	/** 管理员请求标志 */
	String REQUEST_ORIGIN_ADMIN = "REQUEST_ORIGIN_ADMIN";
	/** 管理员请求标志 */
	String REQUEST_ORIGIN_COMPANY = "REQUEST_ORIGIN_COMPANY";
	/** 经理请求标志 */
	String REQUEST_ORIGIN_MANAGER = "REQUEST_ORIGIN_MANAGER";
	/** 员工请求标志 */
	String REQUEST_ORIGIN_EMPLOYEES = "REQUEST_ORIGIN_EMPLOYEES";
	/** 客户请求标志 */
	String REQUEST_ORIGIN_CUSTOMER = "REQUEST_ORIGIN_CUSTOMER";
	/** 绑定邮箱的验证代码前缀 */
	String BINDING_EMAIL_PREFIX = "BINDING_EMAIL_";
	/** 包工的匹配度 */
	Float CONTRACT_COMPATIBILITY = 0.8f;
	/** token失效標誌 */
	String TOKEN_INVALID = "TOKEN_INVALID";
	/** token無法識別 */
	String TOKEN_UNRECOGNIZED = "TOKEN_UNRECOGNIZED";
	/** 鉴权放行标志 */
	String AUTHENTICATION_SUCCESSFUL = "AUTHENTICATION_SUCCESSFUL";
	/** 鉴权失敗标志 */
	String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
	/** 角色對應Map */
	Map<String, RolesEnum> ROLES_ENUM_MAP = new HashMap<String, RolesEnum>(){
		{
			put(REQUEST_ORIGIN_ADMIN, RolesEnum.SYSTEM_ADMIN);
			put(REQUEST_ORIGIN_COMPANY, RolesEnum.USER_COMPANY);
			put(REQUEST_ORIGIN_CUSTOMER, RolesEnum.USER_CUSTOMER);
			put(REQUEST_ORIGIN_MANAGER, RolesEnum.USER_MANAGER);
			put(REQUEST_ORIGIN_EMPLOYEES, RolesEnum.USER_EMPLOYEES);
		}
	};
	/** R的成功标记 */
	Integer SUCCESS = 0;
	/** R的失败标记 */
	Integer FAIL = 1;
	/** 全局配置Redis序列化LocalDateTime */
	ObjectMapper JacksonMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule());
	/** redis存储待使用订单编号集合的key */
	String ORDER_ID_SET = "orderIdSet";

	/** 订单作废中 */
	Integer ORDER_VOID = new Integer(0);
	/** 订单状态变量 待付款状态 */
	Integer ORDER_STATE_TO_BE_PAID = new Integer(2);
	/** 订单处理中 */
	Integer ORDER_PAYMENT_PROCESSING = new Integer(3);
	/** 订单状态变量 进行状态 */
	Integer ORDER_STATE_HAVE_IN_HAND = new Integer(5);
	/** 订单状态变量 待确认状态 */
	Integer ORDER_STATE_TO_BE_CONFIRMED = new Integer(8);
	/** 订单状态变量 待评价状态 */
	Integer ORDER_STATE_TO_BE_EVALUATED = new Integer(15);
	/** 订单状态变量 已完成状态 */
	Integer ORDER_STATE_COMPLETED = new Integer(20);
	/** 订单来源——钟点工 */
	Integer ORDER_ORIGIN_CALENDAR = new Integer(0);
	/** 订单来源——包工 */
	Integer ORDER_ORIGIN_CONTRACT = new Integer(1);
	/** 订单来源——需求单 */
	Integer ORDER_ORIGIN_DEMAND = new Integer(2);
	/** 三方支付：商家验证参数 */
	String PAY_RVG2C = "1974";

}
