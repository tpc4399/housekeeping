package com.housekeeping.common.utils;

/**
 * @Author su
 * @create 2020/10/29 16:48
 */
public interface CommonConstants {
	/***
	 * redis的key前綴
	 */
	String LOGIN_KEY_BY_PHONE = "HK_LOGIN_KEY_BY_PHONE";
	/***
	 * redis的key前綴
	 */
	String FORGET_KEY_BY_PHONE = "HK_FORGET_KEY_BY_PHONE";
	/***
	 * redis的key前綴
	 */
	String REGISTER_KEY_BY_PHONE = "HK_REGISTER_KEY_BY_PHONE";
	/***
	 * redis驗證碼緩存有效時間，分鐘
	 */
	Integer VALID_TIME_MINUTES = 3;
	/**
	 * 正式環境的存儲公司logoUrl的絕對路徑
	 */
	String HK_COMPANY_LOGO_ABSTRACT_PATH_PREFIX_PROV = "HKFile/CompanyLogoImg/userId=";
	/**
	 * 正式環境的存儲公司组图片Url的絕對路徑
	 */
	String HK_GROUP_URL_ABSTRACT_PATH_PREFIX_PROV = "HKFile/GroupLogoImg/userId=";
	/**
	 * 正式環境的存儲公司五张图片的絕對路徑
	 */
	String HK_COMPANY_IMG_ABSTRACT_PATH_PREFIX_PROV = "HKFile/CompanyFiveImg/userId=";
	/**
	 * 正式環境的存儲员工头像的絕對路徑
	 */
	String HK_EMPLOYEES_HEAD_ABSTRACT_PATH_PREFIX_PROV = "HKFile/EmployeesHead/userId=";
	/**
	 * 正式環境的存儲经理头像的絕對路徑
	 */
	String HK_MANAGER_HEAD_ABSTRACT_PATH_PREFIX_PROV = "HKFile/ManagerHead/userId=";
	/**
	 * 正式環境的存儲客户头像的絕對路徑
	 */
	String HK_CUSTOMER_HEAD_ABSTRACT_PATH_PREFIX_PROV = "HKFile/CustomerHead/userId=";
	/**
	 * 正式環境的订单评价照片的絕對路徑
	 */
	String HK_ORDER_EVALUATION_IMAGE_ABSTRACT_PATH_PREFIX_PROV = "HKFile/OrderEvaluationImage/orderId=";

	/**
	 * 神秘代码的零件
	 */
	String[] emn = new String[]{
			"0","1","2","3","4","5","6","7","8","9",
			"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
			"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"
	};
	/**
	 * 员工的神秘代码前缀
	 */
	String LOGIN_EMPLOYEES_PREFIX = "LOGIN_EMPLOYEES_";
	/**
	 * 经理的神秘代码前缀
	 */
	String LOGIN_MANAGER_PREFIX = "LOGIN_MANAGER_";

	/**
	 * 管理员请求标志
	 */
	String REQUEST_ORIGIN_ADMIN = "REQUEST_ORIGIN_ADMIN";

	/**
	 * 管理员请求标志
	 */
	String REQUEST_ORIGIN_COMPANY = "REQUEST_ORIGIN_COMPANY";

	/**
	 * 经理请求标志
	 */
	String REQUEST_ORIGIN_MANAGER = "REQUEST_ORIGIN_MANAGER";

	/**
	 * 员工请求标志
	 */
	String REQUEST_ORIGIN_EMPLOYEES = "REQUEST_ORIGIN_EMPLOYEES";

	/**
	 * 客户请求标志
	 */
	String REQUEST_ORIGIN_CUSTOMER = "REQUEST_ORIGIN_CUSTOMER";

	/**
	 * 绑定邮箱的验证代码前缀
	 */
	String BINDING_EMAIL_PREFIX = "BINDING_EMAIL_";

	/**
	 * 删除
	 */
	String STATUS_DEL = "1";

	Integer EMPTY_FILE = 1005;
	/**
	 * 正常
	 */
	String STATUS_NORMAL = "0";

	/**
	 * 锁定
	 */
	String STATUS_LOCK = "9";

	/**
	 * 菜单
	 */
	String MENU = "0";

	/**
	 * 编码
	 */
	String UTF8 = "UTF-8";
	/**
	 * 编码
	 */
	Integer HAVE_USERS = 1004;

	/**
	 * JSON 资源
	 */
	String CONTENT_TYPE = "application/json; charset=utf-8";

	/**
	 * 前端工程名
	 */
	String FRONT_END_PROJECT = "pig-ui";

	/**
	 * 后端工程名
	 */
	String BACK_END_PROJECT = "pig";
	/**
	 * 后端工程名
	 */
	String BASE_SERVICE_ROLEID = "1000";

	/**
	 * 成功标记
	 */
	Integer SUCCESS = 0;
	/**
	 * 失败标记
	 */
	Integer FAIL = 1;
	/**
	 * 失败标记
	 */
	Integer WRONG_DEPT = 1001;


	/**
	 * 用戶名重複，請重新輸入用戶名
	 */
	Integer REPEAT_USERNAME = 1002;

	Integer REPEAT_SC_NAME = 1003;
	Integer WRONG_PASSWORD = 1006;
	Integer NOT_FOUND_USER = 1016;
	Integer REPEAT_MAIL = 1017;
	Integer OVER_NUM_SCOPE_DETAIL = 1020;
	Integer LACK_LONG_GOAL = 2001;
	Integer DEAD_ID = 999;
	/**
	 * 验证码前缀
	 */
	String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY_";
	/**
	 * 默认学校普通Admin角色
	 */
	Integer DEFAULT_SCHOOL_ROLE_ID= 10;
	/**
	 * 默认供应商普通Admin角色
	 */
	Integer DEFAULT_VENDER_ROLE_ID= 11;

	Integer SCHOOL_PARENT_ID=12;
	Integer VENDOR_PARENT_ID=15;
}
