package com.toms.order.center.monitor;


import com.base.commons.monitor.Monitor;
import com.base.commons.monitor.Monitors;

public class MonitorExample {

    @Monitor("monitor-test")
    public String test() {
        return "haha";
    }

    public String test2(String hosCode) {
        Monitors.counter("monitor-test2").tag("hos", hosCode).register().increment();
        return doTest(hosCode);
    }

    private String doTest(String hosCode) {
        return hosCode;
    }
}
