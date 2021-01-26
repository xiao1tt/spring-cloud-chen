package com.toms.order.center.dubbo;

import com.toms.example.api.bean.Content;
import com.toms.example.api.rpc.EchoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @author chenxiaotong
 */
@Service
public class ConsumeService {

    @DubboReference
    private EchoService echoService;

    public String echo(String string) {
        Content content = new Content();
        content.setString(string);
        return echoService.echo(content);
    }
}
