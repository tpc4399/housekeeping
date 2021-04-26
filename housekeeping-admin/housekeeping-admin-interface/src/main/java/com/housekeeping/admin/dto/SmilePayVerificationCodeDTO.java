package com.housekeeping.admin.dto;

/**
 * @Author su
 * @Date 2021/4/26 17:38
 */
public class SmilePayVerificationCodeDTO {

    private String a; /* A =	商家驗證參數 (共四碼,不足四碼前面補零)
                                目前的商家驗證參數：1974
                                例如商家驗證參數為1234 則 A = 1234 */
    private String b; /* B =	收款金額 (取八碼,不足八碼前面補零)
                                例如金額為 532 元 則 B = 00000532 */
    private String c; /* C =	Smseid參數 (回送  Roturl 時 的 Smseid 參數的後四碼，如不為數字則以 9 替代 )
                                例如Smseid為 12_24_123  ，後四碼 為 "_123"  則 C = 9123 */

}
