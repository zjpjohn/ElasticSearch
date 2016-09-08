package com.zjp.search.client;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.InetSocketAddress;

/**
 * ©¥©¥©¥©¥©¥©¥oooo©¥©¥©¥©¥©¥©¥
 * ¡¡¡¡¡¡©³©·¡¡¡¡¡¡©³©·
 * ¡¡¡¡©³©¿©ß©¥©¥©¥©¿©ß©·
 * ¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡©¥¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡©×©¿¡¡©»©×¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡©ß¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©§
 * ¡¡¡¡©»©¥©·¡¡¡¡¡¡©³©¥©¿
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡©§stay hungry stay foolish
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡©§Code is far away from bug with the animal protecting
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡©»©¥©¥©¥©·
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©Ç©·
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©³©¿
 * ¡¡¡¡¡¡¡¡©»©·©·©³©¥©×©·©³©¿
 * ¡¡¡¡¡¡¡¡¡¡©§©Ï©Ï¡¡©§©Ï©Ï
 * ¡¡¡¡¡¡¡¡¡¡©»©ß©¿¡¡©»©ß©¿
 * ©¥©¥©¥©¥©¥©¥ÃÈÃÈßÕ©¥©¥©¥©¥©¥©¥
 * Module Desc:com.zjp.search.client
 * User: zjprevenge
 * Date: 2016/9/7
 * Time: 15:28
 */

public class ElasticClient implements InitializingBean, DisposableBean, FactoryBean<TransportClient> {

    private static final Logger log = LoggerFactory.getLogger(ElasticClient.class);

    private String hosts;

    private TransportClient transportClient;

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public TransportClient getTransportClient() {
        return transportClient;
    }

    public void setTransportClient(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    public void destroy() throws Exception {
        if (transportClient != null) {
            transportClient.close();
        }
    }

    public TransportClient getObject() throws Exception {
        return transportClient;
    }

    public Class<?> getObjectType() {
        return transportClient.getClass();
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        Preconditions.checkArgument(StringUtils.isNotBlank(hosts), "hosts must not be empty...");
        log.info("init transportClient from config:{}", hosts);
        Settings settings = Settings.builder()
                .put("client.transport.ping_timeout", 10)
                .put("client.transport.sniff", "true")
                .put("client.transport.ignore_cluster_name", "true")
                .build();
        transportClient = TransportClient.builder().settings(settings).build();
        String[] split = hosts.split(",");
        for (String ipport : split) {
            String[] sp = ipport.split(":");
            String ip = sp[0];
            Integer port = Integer.valueOf(sp[1]);
            transportClient.addTransportAddress(
                    new InetSocketTransportAddress(
                            new InetSocketAddress(ip, port)));
        }
    }
}
