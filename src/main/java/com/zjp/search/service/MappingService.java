package com.zjp.search.service;

import com.zjp.search.annotation.Doc;
import com.zjp.search.annotation.Suggest;
import com.zjp.search.bean.IMappingType;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

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
 * Module Desc:com.zjp.search.service
 * User: zjprevenge
 * Date: 2016/9/7
 * Time: 18:17
 */

@Service
public class MappingService {

    private static final Logger log = LoggerFactory.getLogger(MappingService.class);

    @Resource
    private TransportClient client;


    /**
     * 验证type是否存在
     *
     * @param index 索引名称
     * @param type  索引类型
     * @return
     */
    public boolean isTypeExists(String index, String type) {
        return client.admin().indices()
                .typesExists(new TypesExistsRequest(new String[]{index}, type))
                .actionGet()
                .isExists();
    }

    /**
     * 判断是否是简单类型
     *
     * @param clazz
     * @return
     */
    private boolean isPrimitiveType(Class clazz) {
        if (clazz == String.class
                || clazz == Integer.class
                || clazz == BigDecimal.class
                || clazz == Date.class
                || clazz == Long.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == int.class
                || clazz == long.class
                || clazz == double.class
                || clazz == float.class) {
            return true;
        }
        return false;
    }

    /**
     * 创建空的索引
     *
     * @param dataType
     * @return
     */
    public boolean createIndexEmpty(Class<? extends IMappingType> dataType) throws Exception {
        Doc docAnnotation = dataType.getAnnotation(Doc.class);
        String index = StringUtils.isNotBlank(docAnnotation.index()) ? docAnnotation.index() : dataType.getSimpleName();
        String type = StringUtils.isNotBlank(docAnnotation.type()) ? docAnnotation.type() : dataType.getSimpleName();
        CreateIndexRequestBuilder prepareCreate = client.admin().indices().prepareCreate(index);
        prepareCreate.addMapping(type, buildXContentBuilder(type, dataType));
        return prepareCreate.execute().actionGet().isAcknowledged();
    }

    /**
     * 构建 XContentBuilder
     *
     * @param dataType
     * @return
     */
    public XContentBuilder buildXContentBuilder(String type, Class<? extends IMappingType> dataType) throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
        builder.startObject(type)
                .startObject("_timestamp")
                .field("enable", "true")
                .field("store", "no")
                .field("index", "not_analyzer")
                .endObject();
        builder.startObject("properties");
        Field[] fields = dataType.getDeclaredFields();
        //添加字段
        dealMapping(fields, builder);
        //添加建议词
        Suggest suggest = dataType.getAnnotation(Suggest.class);
        if (suggest != null) {
            String analyzer = suggest.analyzer().name();
            String name = suggest.name();
            builder.startObject(name)
                    .field("type", "completion")
                    .field("index_analyzer", analyzer)
                    .field("search_analyzer", analyzer)
                    .field("payloads", "true")
                    .field("preserve_position_increments", false)
                    .field("preserve_separators", false)
                    .endObject();
        }
        return builder.endObject().endObject().endObject();
    }

    /**
     * 添加field mapping
     *
     * @param fields
     * @param builder
     */
    public void dealMapping(Field[] fields, XContentBuilder builder) throws Exception {

        for (Field field : fields) {
            com.zjp.search.annotation.Field fieldAnnotation = field.getAnnotation(com.zjp.search.annotation.Field.class);
            String name = fieldAnnotation.name();
            name = StringUtils.isNotBlank(name) ? name : field.getName();
            builder.startObject(name)
                    .field("type", fieldAnnotation.type().getTypeValue())
                    .field("store", fieldAnnotation.store())
                    .field("analyzer", fieldAnnotation.analyzer().name())
                    .field("index", fieldAnnotation.index().name())
                    .endObject();
        }
    }
}
