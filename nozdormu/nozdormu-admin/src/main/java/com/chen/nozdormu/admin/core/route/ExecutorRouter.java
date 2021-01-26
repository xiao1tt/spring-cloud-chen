package com.chen.nozdormu.admin.core.route;

import com.chen.nozdormu.core.biz.model.ReturnT;
import com.chen.nozdormu.core.biz.model.TriggerParam;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xuxueli on 17/3/10.
 */
public abstract class ExecutorRouter {

    protected static Logger logger = LoggerFactory.getLogger(ExecutorRouter.class);

    /**
     * route address
     *
     * @return ReturnT.content=address
     */
    public abstract ReturnT<String> route(TriggerParam triggerParam, List<String> addressList);

}
