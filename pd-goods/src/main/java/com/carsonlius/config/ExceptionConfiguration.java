package com.carsonlius.config;

import com.carsonlius.handler.DefaultGlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局Exception控制
 * @version V1.0
 * @author: liusen
 * @date: 2022年03月14日 19时16分
 * @contact
 * @company
 */
@Configuration
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class ExceptionConfiguration extends DefaultGlobalExceptionHandler {
}
