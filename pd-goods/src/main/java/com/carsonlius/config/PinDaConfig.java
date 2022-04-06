package com.carsonlius.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @version V1.0
 * @author: liusen
 * @date: 2022年04月06日 16时55分
 * @contact
 * @company
 */
@Configuration
@ConfigurationProperties(prefix = "pinda.mysql")
@RefreshScope
@Data
public class PinDaConfig {
    private String ip;
    private String port;
    private String username;
    private String password;
}
