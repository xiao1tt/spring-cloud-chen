package com.toms.order.center.web;

import com.base.commons.dynamicvalue.core.NacosFile;
import com.toms.order.center.entity.UnifyCenterAccount;
import com.toms.order.center.service.UnifyCenterAccountService;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenxiaotong
 */
@RestController
public class ExampleController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${example.value}")
    private String owner;

    @NacosFile(file = "test.json", group = "toms-example")
    public void parse(String data) {
        logger.info("data:{}", data);
    }

    @Resource
    private UnifyCenterAccountService unifyCenterAccountService;

    @GetMapping(value = "/all")
    public List<UnifyCenterAccount> all() {
        return unifyCenterAccountService.queryAllByLimit(0, 10);
    }

    @GetMapping("/value")
    public String value() {
        return owner;
    }

    public static void main(String[] args) {
        SimpleTypeConverter converter = new SimpleTypeConverter();
        System.out.println(converter.convertIfNecessary("", List.class));
    }
}