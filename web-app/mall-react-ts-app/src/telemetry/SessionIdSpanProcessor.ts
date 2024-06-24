
import { Context } from "@opentelemetry/api";
import { ReadableSpan, Span, SpanProcessor } from "@opentelemetry/sdk-trace-web";
import { AttributeNames } from "./AttributeNames";
import { TraceState } from "@opentelemetry/core";

export class SessionIdSpanProcessor implements SpanProcessor {
    forceFlush(): Promise<void> {
        return Promise.resolve();
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    onStart(span: Span, parentContext: Context): void {
        span.spanContext().traceState = new TraceState('country=HK,bu=PLUK,fflag=flag01,uid=fuser-001');
        span.setAttribute(AttributeNames.SESSION_USER_ID, 'fuser-001');
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars, @typescript-eslint/no-empty-function
    onEnd(span: ReadableSpan): void {}

    shutdown(): Promise<void> {
        return Promise.resolve();
    }
}