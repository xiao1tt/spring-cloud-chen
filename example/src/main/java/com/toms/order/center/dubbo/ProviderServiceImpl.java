package com.toms.order.center.dubbo;

import com.toms.example.api.bean.Content;
import com.toms.example.api.rpc.EchoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenxiaotong
 */
@DubboService
public class ProviderServiceImpl implements EchoService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String echo(Content content) {
        logger.info("dubbo:{}", content.getString());

        return "Discovery " + content.getString();
    }

    @Override
    public String value() {
        return "owner";
    }
}