package com.service.extension.telemetry;

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

import java.util.logging.Logger;

public class OTELSpanProcessor implements SpanProcessor {

    private static final Logger log = Logger.getLogger(OTELConfigurationProvider.class.getName());

    public OTELSpanProcessor() {
    }

    @Override
    public void onStart(Context context, ReadWriteSpan span) {
        TraceState traceState = span.getParentSpanContext().getTraceState();
        // log.info("start: " + traceState);
        if (null != traceState) {
            span.setAttribute("app.businessUnit", traceState.get("bu"));
            span.setAttribute("app.session.userId", traceState.get("uid"));
        }
    }

    @Override
    public boolean isStartRequired() {
        return true;
    }

    @Override
    public void onEnd(ReadableSpan readableSpan) {
    }

    @Override
    public boolean isEndRequired() {
        return false;
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode forceFlush() {
        return CompletableResultCode.ofSuccess();
    }

}
