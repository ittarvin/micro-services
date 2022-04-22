package cn.zjcw.gateway.route;


import com.alibaba.fastjson.JSON;
import com.ctc.wstx.util.StringUtil;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
@EnableApolloConfig
@Component
public class DynamicRouteConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(DynamicRouteConfiguration.class);

  private static final String CONFIG_TAG = "cn.zjcw.gateway.dynamic.router";

  @Autowired
  private GatewayService gatewayService;

  @ApolloConfig
  private Config config;

  @ApolloConfigChangeListener
  private void onChange(ConfigChangeEvent changeEvent) {
        refreshRouter();
  }

  @PostConstruct
  private void refreshRouter() {
    Set<String> keyNames = config.getPropertyNames();
    for (String key : keyNames) {

      if (containsIgnoreCase(key, CONFIG_TAG)) {

        String configInfo = config.getProperty(key, null);

        logger.debug("{}:{}", key, configInfo);

        //
        if(!StringUtils.isEmpty(configInfo)){

          List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);

          gatewayService.updateRoutes(definitionList);

        }

      }
    }
  }


  private static boolean containsIgnoreCase(String str, String searchStr) {
    if (str == null || searchStr == null) {
      return false;
    }
    int len = searchStr.length();
    int max = str.length() - len;
    for (int i = 0; i <= max; i++) {
      if (str.regionMatches(true, i, searchStr, 0, len)) {
        return true;
      }
    }
    return false;
  }




}
