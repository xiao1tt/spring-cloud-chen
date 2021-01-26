package com.base.commons.lang.pojo;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 如果要序列化该对象, 请务必使用root.lang中的JsonUtil
 *
 * @author heyawei
 */
final public class DateRange implements Iterable<LocalDate>, Serializable, Comparable<DateRange> {

    private LocalDate fromDate;

    private LocalDate toDate;

    private static final Splitter SPLITTER = Splitter.on(",");

    public static DateRange create(int days) {
        return DateRange.create(LocalDate.now(), LocalDate.now().plusDays(days));
    }

    public static DateRange create(String json) {
        Preconditions.checkArgument(StringUtils.isNotBlank(json));
        json = json.replace("[", "");
        json = json.replace("]", "");
        List<String> strings = SPLITTER.splitToList(json);
        Preconditions.checkArgument(CollectionUtils.size(strings) == 2);
        LocalDate from = LocalDate.parse(StringUtils.trimToEmpty(strings.get(0)));
        LocalDate to = LocalDate.parse(StringUtils.trimToEmpty(strings.get(1)));
        return create(from, to);
    }

    public static DateRange create(LocalDate fromDate, LocalDate toDate) {
        return new DateRange(fromDate, toDate);
    }

    public static DateRange create(LocalDate fromDate) {
        return new DateRange(fromDate, fromDate.plusDays(1));
    }

    private DateRange(LocalDate fromDate, LocalDate toDate) {
        Preconditions.checkArgument(fromDate != null && toDate != null
                        && (fromDate.compareTo(toDate) < 0),
                "invalid param [%s-%s]", fromDate, toDate);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public DateRange add(LocalDate param) {
        DateRange localDate = DateRange.create(param);
        return this.add(localDate);
    }

    public DateRange add(DateRange target) {
        Preconditions.checkArgument(this.isConnected(target),
                "this.[%s]和that[%s]根本就连不上", this, target);
        LocalDate from = genMin(target.getFromDate());
        LocalDate to = genMax(target.getToDate());
        return DateRange.create(from, to);
    }


    /**
     * @param start 开始的index, 从头部开始数
     * @param end 结束的index, 从尾部开始数
     * @return DateRange
     */
    public DateRange sub(int start, int end) {
        Preconditions.checkArgument(start >= 0);
        Preconditions.checkArgument(end >= 0);
        LocalDate from = fromDate.plusDays(start);
        LocalDate to = toDate.minusDays(end);
        Preconditions.checkArgument(from.compareTo(to) < 0, "this.[%s],sub序列越界", this);
        return DateRange.create(from, to);
    }

    public DateRange sub(int start) {
        return sub(start, 0);
    }

    /**
     * connect 上不代表有交集 例如 : [2015-01-15 - 2015-01-20 ] [2015-01-20] - [2015-01-25]
     */
    public boolean isConnected(DateRange dates) {
        return !isNotConnected(dates);
    }

    public boolean isConnected(LocalDate localDate) {
        return isConnected(DateRange.create(localDate));
    }

    public boolean isNotConnected(DateRange dates) {
        return this.fromDate.compareTo(dates.toDate) > 0 || this.toDate.compareTo(dates.fromDate) < 0;
    }

    public boolean hasIntersection(DateRange target) {
        if (!isConnected(target)) {
            return false;
        }
        // 排除只是恰好链接上的情况
        if (this.fromDate.equals(target.toDate)
                || this.toDate.equals(target.fromDate)) {
            return false;
        }
        //
        return true;
    }

    /**
     * 取交集
     */
    public DateRange intersection(DateRange target) {
        Preconditions.checkArgument(hasIntersection(target));
        LocalDate from = genMin(target.fromDate);
        LocalDate to = genMax(target.toDate);
        return DateRange.create(from, to);
    }

    /**
     * 是否只有一天
     */
    public boolean isSingleDay() {
        return fromDate.plusDays(1).compareTo(toDate) == 0;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public long length() {
        return DateUtil.between(Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), DateUnit.DAY);
    }

    @Override
    public String toString() {
        return "[" + fromDate + "," + toDate + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
        result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DateRange other = (DateRange) obj;
        if (fromDate == null) {
            if (other.fromDate != null) {
                return false;
            }
        } else if (!fromDate.equals(other.fromDate)) {
            return false;
        }
        if (toDate == null) {
            if (other.toDate != null) {
                return false;
            }
        } else if (!toDate.equals(other.toDate)) {
            return false;
        }
        return true;
    }

    public boolean contains(LocalDate localDate) {
        return localDate.compareTo(fromDate) >= 0 && localDate.compareTo(toDate) < 0; // 不包含最后一天
    }

    public boolean contains(DateRange dateRange) {
        return this.fromDate.compareTo(dateRange.fromDate) <= 0
                && this.toDate.compareTo(dateRange.toDate) >= 0;
    }

    @Override
    public Iterator<LocalDate> iterator() {
        return new Iterator<LocalDate>() {

            private LocalDate current = fromDate;

            @Override
            public boolean hasNext() {
                return current.compareTo(toDate) < 0;
            }

            @Override
            public LocalDate next() {
                Preconditions.checkArgument(hasNext(), "已经没有下一个了");
                LocalDate localDate = current;
                current = current.plusDays(1); //后移一位
                return localDate;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public List<LocalDate> toDates() {
        List<LocalDate> result = Lists.newArrayList();
        for (LocalDate localDate : this) {
            result.add(localDate);
        }
        return result;
    }

    @Override
    public int compareTo(DateRange other) {
        Preconditions.checkNotNull(other);
        int f = this.getFromDate().compareTo(other.getFromDate());
        if (0 == f) {
            return this.getToDate().compareTo(other.getToDate());
        }
        return f;
    }

    private LocalDate genMin(LocalDate targetFromDate) {
        return fromDate.compareTo(targetFromDate) > 0 ? targetFromDate : fromDate;
    }

    private LocalDate genMax(LocalDate targetToDate) {
        return fromDate.compareTo(targetToDate) > 0 ? fromDate : targetToDate;
    }
}
