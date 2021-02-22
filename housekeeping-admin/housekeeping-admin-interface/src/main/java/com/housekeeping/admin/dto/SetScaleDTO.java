package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.CompanyScale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author su
 * @Date 2021/2/22 16:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetScaleDTO {

    private List<Integer> scale; //1 10 30 50    0-1  2-10   11-30  31-50  51-n 長度為4 為5段
    private List<BigDecimal> monthPrice;//       0    100    200     300   500  長度為5 為5段
    private List<BigDecimal> yearPrice;//        0    1000   2000    3000  5000 長度為5 為5段
    private String code;

    public List<CompanyScale> toCompanyScaleList(){
        List<CompanyScale> companyScaleList = new ArrayList<>();
        Integer length = monthPrice.size();
        for (int i = 0; i < length; i++) {
            Integer start = i == 0 ? 0 : scale.get(i - 1)+1;
            Integer end = i == length-1 ? Integer.MAX_VALUE : scale.get(i);
            String scale = start + " " + end;
            BigDecimal monthPrice = this.getMonthPrice().get(i);
            BigDecimal yearPrice = this.getYearPrice().get(i);
            companyScaleList.add(new CompanyScale(i+1, scale, monthPrice, yearPrice, code));
        }
        return companyScaleList;
    }

}
