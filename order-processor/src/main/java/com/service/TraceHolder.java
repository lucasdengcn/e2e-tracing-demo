package com.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;

public class TraceHolder {

    public static String getCurrentTraceId(){
        SpanContext spanContext = Span.current().getSpanContext();
        String traceId = spanContext.getTraceId();
        return traceId;
    }

}
