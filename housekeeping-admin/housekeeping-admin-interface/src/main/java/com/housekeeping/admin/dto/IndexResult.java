package com.housekeeping.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @Date 2021/3/25 15:59
 */
@Data
@NoArgsConstructor
public class IndexResult {
    private Boolean type;/* ture公司 false保洁员 */
    private IndexQueryResultCompany indexQueryResultCompany;
    private IndexQueryResultEmployees indexQueryResultEmployees;
    public IndexResult(IndexQueryResultCompany indexQueryResultCompany){
        this.indexQueryResultCompany = indexQueryResultCompany;
        this.type = true;
    }
    public IndexResult(IndexQueryResultEmployees indexQueryResultEmployees){
        this.indexQueryResultEmployees = indexQueryResultEmployees;
        this.type = false;
    }
}
