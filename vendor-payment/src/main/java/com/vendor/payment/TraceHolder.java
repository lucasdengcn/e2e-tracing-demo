package com.vendor.payment;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

import java.util.UUID;

public class TraceHolder {

    public static String getCurrentTraceId(){
        SpanContext spanContext = Span.current().getSpanContext();
        String traceId = spanContext.getTraceId();
        return traceId;
    }

    /**
     * 00-0c56f92980e58b6aeee785a9d3446630-4c99f8273e007e17-01
     * @return
     */
    public static String getTraceParent(){
        SpanContext spanContext = Span.current().getSpanContext();
        String traceId = spanContext.getTraceId();
        String spanId = spanContext.getSpanId();
        return "00-" + traceId + "-" + spanId + "-01";
    }

    public static String buildTraceParent(String traceId){
        String spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return "00-" + traceId + "-" + spanId + "-00";
    }
}
