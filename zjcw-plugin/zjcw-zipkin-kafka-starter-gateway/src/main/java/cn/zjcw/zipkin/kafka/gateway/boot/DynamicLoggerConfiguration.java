package cn.zjcw.zipkin.kafka.gateway.boot;


import cn.zjcw.zipkin.kafka.core.config.ZipkinKafkaPropertySourcesConstants;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Set;


@Configuration
@ConditionalOnProperty({
        ZipkinKafkaPropertySourcesConstants.ZIPKIN_KAFKA_LOGGER_INFO_ENABLED})
@EnableApolloConfig
public class DynamicLoggerConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(DynamicLoggerConfiguration.class);

  private static final String LOGGER_TAG = "logging.level.";

  @Autowired
  private LoggingSystem loggingSystem;

  @ApolloConfig
  private Config config;

  @ApolloConfigChangeListener
  private void onChange(ConfigChangeEvent changeEvent) {
    refreshLoggingLevels();
  }

  @PostConstruct
  private void refreshLoggingLevels() {
    Set<String> keyNames = config.getPropertyNames();
    for (String key : keyNames) {
      if (containsIgnoreCase(key, LOGGER_TAG)) {
        String strLevel = config.getProperty(key, "info");
        LogLevel level = LogLevel.valueOf(strLevel.toUpperCase());
        loggingSystem.setLogLevel(key.replace(LOGGER_TAG, ""), level);
        logger.debug("{}:{}", key, strLevel);
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
