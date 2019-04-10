package com.baobeidaodao.springcloud.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author DaoDao
 */
@Slf4j
@RequestMapping(value = "")
@RestController
public class GatewayController {

    @Value("${spring.application.name}")
    private String applicationName;

    @RequestMapping(value = "")
    public String index() {
        log.info(applicationName);
        return applicationName + "哇哇哇哇";
    }

}
