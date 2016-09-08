package com.zjp.search.service;

import com.alibaba.fastjson.JSON;
import com.zjp.search.annotation.Doc;
import com.zjp.search.bean.DocSource;
import com.zjp.search.bean.IMappingType;
import com.zjp.search.request.IndexRequest;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
 * Time: 18:10
 */
@Service
public class IndexService {

    private static final Logger log = LoggerFactory.getLogger(IndexService.class);

    @Resource
    private TransportClient client;

    @Resource
    private MappingService mappingService;

    /**
     * 创建索引
     *
     * @param request
     */
    public void createIndex(IndexRequest request) throws Exception {
        Class<? extends IMappingType> dataType = request.getDataType();
        List<DocSource> sources = request.getSources();
        Doc doc = dataType.getAnnotation(Doc.class);
        String index = StringUtils.isNoneBlank(doc.index()) ? doc.index() : dataType.getSimpleName();
        String type = StringUtils.isNoneBlank(doc.type()) ? doc.type() : dataType.getSimpleName();

        //索引存在，删除索引
        if (isIndexExist(index)) {
            deleteIndex(index);
        }
        //文档数据不为空
        if (sources != null && !sources.isEmpty()) {
            //创建索引
            client.admin().indices().prepareCreate(index).execute().actionGet();
            //设置mapping
            if (mappingService.isTypeExists(index, type)) {
                mappingService.createIndexEmpty(dataType);
            }
            //批量创建索引数据
            BulkRequestBuilder bulk = client.prepareBulk();
            for (DocSource source : sources) {
                org.elasticsearch.action.index.IndexRequest indexRequest = client.prepareIndex(index, type, source.getId())
                        .setSource(JSON.toJSONString(source.getDocument())).request();
                bulk.add(indexRequest);
            }
            BulkResponse responses = bulk.execute().actionGet();
            if (responses.hasFailures()) {
                log.warn("bulk create index error...");
            }
        }
    }

    /**
     * 删除索引
     *
     * @param index 索引
     * @return
     */
    public boolean deleteIndex(String index) {
        return client.admin()
                .indices()
                .prepareDelete(index)
                .execute()
                .actionGet()
                .isAcknowledged();
    }

    /**
     * 验证索引是否存在
     *
     * @param index 索引
     * @return
     */
    public boolean isIndexExist(String index) {
        return client.admin()
                .indices()
                .exists(new IndicesExistsRequest(index))
                .actionGet()
                .isExists();
    }

    /**
     * 添加文档
     *
     * @param request
     */
    public void addDoc(IndexRequest request) {
        Class<? extends IMappingType> dataType = request.getDataType();
        List<DocSource> sources = request.getSources();
        Doc doc = dataType.getAnnotation(Doc.class);
        String index = StringUtils.isNoneBlank(doc.index()) ? doc.index() : dataType.getSimpleName();
        String type = StringUtils.isNoneBlank(doc.type()) ? doc.type() : dataType.getSimpleName();
        BulkRequestBuilder bulk = client.prepareBulk();
        for (DocSource source : sources) {
            bulk.add(client.prepareIndex(index, type, source.getId())
                    .setSource(JSON.toJSONString(source)));
        }
        bulk.execute().actionGet();
    }

    /**
     * 更新文档
     *
     * @param request
     */
    public void updateDoc(IndexRequest request) {
        Class<? extends IMappingType> dataType = request.getDataType();
        List<DocSource> sources = request.getSources();
        Doc doc = dataType.getAnnotation(Doc.class);
        String index = StringUtils.isNoneBlank(doc.index()) ? doc.index() : dataType.getSimpleName();
        String type = StringUtils.isNoneBlank(doc.type()) ? doc.type() : dataType.getSimpleName();
        BulkRequestBuilder bulk = client.prepareBulk();
        for (DocSource source : sources) {
            bulk.add(client.prepareUpdate(index, type, source.getId())
                    .setDoc(JSON.toJSONString(source)));
        }
        bulk.execute().actionGet();
    }

    /**
     * 删除单个文档
     *
     * @param index    索引
     * @param typeName 类型
     * @param docId    文档id
     */
    public void deleteDoc(String index, String typeName, String docId) {
        client.prepareDelete(index, typeName, docId)
                .execute()
                .actionGet();
    }

    /**
     * 批量删除文档
     *
     * @param index    索引
     * @param typeName 类型
     * @param docIds   文档id集合
     */
    public void batchDeleteDoc(String index, String typeName, List<String> docIds) {
        BulkRequestBuilder bulk = client.prepareBulk();
        for (String docId : docIds) {
            bulk.add(client.prepareDelete(index, typeName, docId));
        }
        bulk.execute().actionGet();
    }
}
