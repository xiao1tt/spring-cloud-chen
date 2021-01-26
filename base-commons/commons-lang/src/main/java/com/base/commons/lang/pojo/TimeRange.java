package com.base.commons.lang.pojo;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * @author heyawei
 */
public class TimeRange implements Serializable {

    private LocalTime from;

    private LocalTime to;

    private TimeRange(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }

    public static TimeRange create(LocalTime from, LocalTime to) {
        return new TimeRange(from, to);
    }

    public LocalTime getFrom() {
        return from;
    }

    public LocalTime getTo() {
        return to;
    }

    public boolean contains(LocalTime shortTime) {
        return shortTime.compareTo(getFrom()) >= 0 && shortTime.compareTo(getTo()) <= 0;
    }

    @Override
    public String toString() {
        return "[" + from + "~" + to + "]";
    }

}
