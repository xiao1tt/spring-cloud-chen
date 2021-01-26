package com.base.commons.monitor;

import com.google.common.base.Joiner;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;
import io.micrometer.core.lang.Nullable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author chenxiaotong
 */
public class Monitors {

    private static final Joiner JOINER = Joiner.on("_");
    private static final String PREFIX = "toms";

    private static MeterRegistry REGISTRY;

    private static final AtomicBoolean INIT = new AtomicBoolean(false);

    protected static void init(MeterRegistry registry) {
        if (INIT.compareAndSet(false, true)) {
            Monitors.REGISTRY = registry;
        }
    }

    public static CounterBuilder counter(String name) {
        return CounterBuilder.name(name);
    }

    public static GaugeBuilder gauge(String name, Supplier<Number> f) {
        return GaugeBuilder.name(name, f);
    }

    public static SummaryBuilder summary(String name) {
        return SummaryBuilder.name(name);
    }

    public static TimerBuilder timer(String name) {
        return TimerBuilder.name(name);
    }

    public static TimerSample timerSample(String name) {
        return TimerSample.name(name);
    }

    public static class CounterBuilder {

        private final Counter.Builder builder;

        public CounterBuilder(String name) {
            builder = Counter.builder(name);
        }

        public static CounterBuilder name(String name) {
            return new CounterBuilder(name);
        }

        public CounterBuilder tags(String... tags) {
            builder.tags(tags);
            return this;
        }

        public CounterBuilder tags(Map<String, String> tags) {
            tags.forEach(builder::tag);
            return this;
        }

        public CounterBuilder tag(String key, String value) {
            builder.tag(key, value);
            return this;
        }

        public CounterBuilder description(@Nullable String description) {
            builder.description(description);
            return this;
        }

        public CounterBuilder baseUnit(@Nullable String unit) {
            builder.baseUnit(unit);
            return this;
        }

        public Counter register() {
            return builder.register(REGISTRY);
        }

    }

    public static class GaugeBuilder {

        private final Gauge.Builder builder;

        public GaugeBuilder(String name, Supplier<Number> f) {
            builder = Gauge.builder(name, f);
        }

        public static GaugeBuilder name(String name, Supplier<Number> f) {
            return new GaugeBuilder(name, f);
        }

        public GaugeBuilder tags(String... tags) {
            builder.tags(tags);
            return this;
        }

        public GaugeBuilder tags(Map<String, String> tags) {
            tags.forEach(builder::tag);
            return this;
        }

        public GaugeBuilder tag(String key, String value) {
            builder.tag(key, value);
            return this;
        }

        public GaugeBuilder description(@Nullable String description) {
            builder.description(description);
            return this;
        }

        public GaugeBuilder baseUnit(@Nullable String unit) {
            builder.baseUnit(unit);
            return this;
        }

        public Gauge register() {
            return builder.register(REGISTRY);
        }
    }

    public static class TimerSample {

        private String name;
        private Tags tags = Tags.empty();

        private Timer timer;
        private Sample sample;

        private TimerSample(String name) {
            this.name = name;
        }

        public TimerSample tags(String... tags) {
            return tags(Tags.of(tags));
        }

        public TimerSample tags(Iterable<Tag> tags) {
            this.tags = this.tags.and(tags);
            return this;
        }

        public TimerSample tags(Map<String, String> tags) {
            tags.forEach((k, v) -> this.tags.and(k, v));
            return this;
        }

        public TimerSample tag(String key, String value) {
            this.tags = tags.and(key, value);
            return this;
        }

        public static TimerSample name(String name) {
            return new TimerSample(name);
        }

        public Sample start() {
            this.timer = REGISTRY.timer(name, tags);
            this.sample = Timer.start(REGISTRY);
            return sample;
        }

        public long stop() {
            return sample.stop(timer);
        }
    }

    public static class TimerBuilder {

        private static final String SUFFIX = "time";

        private final Timer.Builder builder;

        public TimerBuilder(String name) {
            builder = Timer.builder(JOINER.join(PREFIX, name, SUFFIX));
        }

        public static TimerBuilder name(String name) {
            return new TimerBuilder(name);
        }

        public TimerBuilder tags(String... tags) {
            builder.tags(tags);
            return this;
        }

        public TimerBuilder tags(Map<String, String> tags) {
            tags.forEach(builder::tag);
            return this;
        }

        public TimerBuilder tag(String key, String value) {
            builder.tag(key, value);
            return this;
        }

        public TimerBuilder description(@Nullable String description) {
            builder.description(description);
            return this;
        }

        public Timer register() {
            return builder.register(REGISTRY);
        }
    }

    public static class SummaryBuilder {

        private final DistributionSummary.Builder builder;

        public SummaryBuilder(String name) {
            builder = DistributionSummary.builder(name);
        }

        public static SummaryBuilder name(String name) {
            return new SummaryBuilder(name);
        }

        public SummaryBuilder tags(String... tags) {
            builder.tags(tags);
            return this;
        }

        public SummaryBuilder tag(String key, String value) {
            builder.tag(key, value);
            return this;
        }

        public SummaryBuilder tags(Map<String, String> tags) {
            tags.forEach(builder::tag);
            return this;
        }

        public SummaryBuilder description(@Nullable String description) {
            builder.description(description);
            return this;
        }

        public SummaryBuilder baseUnit(@Nullable String unit) {
            builder.baseUnit(unit);
            return this;
        }

        public DistributionSummary register() {
            return builder.register(REGISTRY);
        }
    }
}
