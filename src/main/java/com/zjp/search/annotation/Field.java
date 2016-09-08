package com.zjp.search.annotation;

import com.zjp.search.enums.Analyzer;
import com.zjp.search.enums.FieldType;
import com.zjp.search.enums.Index;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
 * Module Desc:com.zjp.search.annotation
 * User: zjprevenge
 * Date: 2016/9/7
 * Time: 17:03
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Field {

    /**
     * 字段名称
     *
     * @return
     */
    String name() default "";

    /**
     * 字段类型
     *
     * @return
     */
    FieldType type() default FieldType.STRING;

    /**
     * 分词器，默认不分词
     *
     * @return
     */
    Analyzer analyzer() default Analyzer.not_analyzed;

    /**
     * 是否存储，默认no or yes
     *
     * @return
     */
    String store() default "no";

    /**
     * 是否对字段进行分析
     * 默认 not_analyzed，analyzed
     */
    Index index() default Index.not_analyzed;
}
