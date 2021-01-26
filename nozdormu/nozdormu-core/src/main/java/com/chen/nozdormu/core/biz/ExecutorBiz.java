package com.chen.nozdormu.core.biz;

import com.chen.nozdormu.core.biz.model.IdleBeatParam;
import com.chen.nozdormu.core.biz.model.KillParam;
import com.chen.nozdormu.core.biz.model.LogParam;
import com.chen.nozdormu.core.biz.model.LogResult;
import com.chen.nozdormu.core.biz.model.ReturnT;
import com.chen.nozdormu.core.biz.model.TriggerParam;

/**
 * Created by xuxueli on 17/3/1.
 */
public interface ExecutorBiz {

    /**
     * beat
     */
    ReturnT<String> beat();

    /**
     * idle beat
     */
    ReturnT<String> idleBeat(IdleBeatParam idleBeatParam);

    /**
     * run
     */
    ReturnT<String> run(TriggerParam triggerParam);

    /**
     * kill
     */
    ReturnT<String> kill(KillParam killParam);
}
