package com.zjp.search.query;

import com.zjp.search.enums.BoolQueryType;
import com.zjp.search.enums.QueryWay;

/**
 * ━━━━━━oooo━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃stay hungry stay foolish
 * 　　　　┃　　　┃Code is far away from bug with the animal protecting
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━萌萌哒━━━━━━
 * Module Desc:com.zjp.search.query
 * User: zjprevenge
 * Date: 2016/9/8
 * Time: 9:45
 */

public abstract class AbsQueryItem implements IQueryItem {

    protected BoolQueryType queryType;

    protected QueryWay queryWay;

    protected String fieldName;

    protected String fieldValue;

    public AbsQueryItem() {
    }

    /**
     * 默认提供 must term 查询
     *
     * @param fieldName
     * @param fieldValue
     */
    public AbsQueryItem(String fieldName, String fieldValue) {
        this(BoolQueryType.MUST, QueryWay.TERM, fieldName, fieldValue);
    }

    /**
     * 构造器
     *
     * @param queryType  查询类型
     * @param queryWay   查询方式
     * @param fieldName  字段名称
     * @param fieldValue 查询字段值
     */
    public AbsQueryItem(BoolQueryType queryType,
                        QueryWay queryWay,
                        String fieldName,
                        String fieldValue) {
        this.queryType = queryType;
        this.queryWay = queryWay;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public BoolQueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(BoolQueryType queryType) {
        this.queryType = queryType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public QueryWay getQueryWay() {
        return queryWay;
    }

    public void setQueryWay(QueryWay queryWay) {
        this.queryWay = queryWay;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * 获取查询的bool类型
     *
     * @return
     */
    public BoolQueryType boolQueryType() {
        return getQueryType();
    }

}
