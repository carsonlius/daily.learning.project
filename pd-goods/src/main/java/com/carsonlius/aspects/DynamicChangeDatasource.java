package com.carsonlius.aspects;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.carsonlius.config.PinDaConfig;
import com.carsonlius.dto.BaseDto;
import com.carsonlius.dto.DataSourceDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.UUID;


/**
 * @Author carsonlius
 * @Date 2022/3/6 19:01
 * @Version 1.0
 */
@Component
@Aspect
public class DynamicChangeDatasource {
    private Logger logger = LoggerFactory.getLogger(DynamicChangeDatasource.class);

    @Autowired
    private PinDaConfig pinDaConfig;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DefaultDataSourceCreator dataSourceCreator;

    @Pointcut("execution(* com.carsonlius.controller.*.*(..))")
    public void changeDatasource() {
    }

    @Around("changeDatasource()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object result = null;
        String poolName = "";
        try {

            //
            logger.info("开始加载数据库");
            poolName = setDataSource(joinPoint);
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            logger.info("执行切换数据源之后");
            removeDatasource(poolName);
            //Do Something useful, If you have
        }
        return result;
    }

    /**
     * 删除久数据源
     */
    private void removeDatasource(String poolName) {
        if (StringUtils.isEmpty(poolName)) {
            logger.error("没有加载到数据库:" + poolName);
            return;
        }

        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(poolName);
    }

    /**
     * 切换数据源头
     */
    private String setDataSource(ProceedingJoinPoint joinPoint) {

        BaseDto baseDto = null;
        for (Object arg : joinPoint.getArgs()) {
            System.out.println("arg" + arg);
            if (arg instanceof BaseDto) {
                baseDto = (BaseDto) arg;
                break;
            }
        }

        // todo 缺少必须参数
//        if (baseDto == null || StringUtils.isEmpty(baseDto.getMerchantId())) {
//
//            throw new BizException(ExceptionCode.ILLEGALA_ARGUMENT_EX.getCode(), ExceptionCode.ILLEGALA_ARGUMENT_EX.getMsg());
//        }
        String merchantId = "goods";

        if (baseDto != null) {
            merchantId = baseDto.getMerchantId();
        }


        // todo 这里要从数据库获取商家数据库配置 (测试这里可以写死)
        DataSourceDTO dto = new DataSourceDTO();
        dto.setPassword(pinDaConfig.getPassword());
        dto.setUsername(pinDaConfig.getUsername());
        String poolName = wrapperPoolName(merchantId);
        dto.setPoolName(poolName);

        String url = "";
        String ip = pinDaConfig.getIp();
        String port = pinDaConfig.getPort();
        if ("goods".equals(merchantId)) {

            url = "jdbc:mysql://" + ip + ":" + port + "/pd_goods?serverTimezone=CTT&characterEncoding=utf8&useUnicode=true&useSSL=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true";
        } else {
            url = "jdbc:mysql://" + ip + ":" + port + "/pd_auth?serverTimezone=CTT&characterEncoding=utf8&useUnicode=true&useSSL=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true";
        }

        dto.setUrl(url);

        // 注入数据源
        add(dto);

        //  手动切换数据源
        DynamicDataSourceContextHolder.push(poolName);

        return poolName;
    }

    /**
     * 保证PoolName
     *
     * @param merchantId
     * @return
     */
    private String wrapperPoolName(String merchantId) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return merchantId.concat("_").concat(uuid);
    }

    /**
     * 添加数据源
     * 通用数据源会根据maven中配置的连接池根据顺序依次选择 默认的顺序为druid>hikaricp>beecp>dbcp>spring basic
     */
    public void add(DataSourceDTO dto) {
        // 添加数据源
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtils.copyProperties(dto, dataSourceProperty);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(dto.getPoolName(), dataSource);
    }
}
