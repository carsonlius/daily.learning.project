package com.carsonlius;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @version V1.0
 * @author: liusen
 * @date: 2022年02月25日 11时52分
 * @contact
 * @company
 */
@MapperScan(basePackages = "com.carsonlius.mapper")
@SpringBootApplication
@EnableDiscoveryClient
public class DynamicDatasourceApplication {
    private final static Logger logger = LoggerFactory.getLogger(DynamicDatasourceApplication.class);
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext context = SpringApplication.run(DynamicDatasourceApplication.class, args);
        Environment env = context.getEnvironment();

        logger.info("\n --------------------------------------------------------\n \t " +
                        "应用 '{}' 运行成功! 访问链接:\n\t" +
                        "Swagger文件: \t http://{}:{}/swagger-ui.html\n\t" +
                        "Druid监控 : \t http://{}:{}/druid/login.html\n\t" +
                        "-----------------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port")
        );
        logger.debug("地址" + InetAddress.getLocalHost().getCanonicalHostName());
    }
}
