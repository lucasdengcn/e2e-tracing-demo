
import { Context } from "@opentelemetry/api";
import { ReadableSpan, Span, SpanProcessor } from "@opentelemetry/sdk-trace-web";
import { AttributeNames } from "./AttributeNames";

export class ModuleIdSpanProcessor implements SpanProcessor {
    private readonly _name;

    constructor(name: string) {
        this._name = name;
    }

    forceFlush(): Promise<void> {
        return Promise.resolve();
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    onStart(span: Span, parentContext: Context): void {
        span.setAttribute(AttributeNames.MODULE_ID, this._name);
    }

    // eslint-disable-next-line @typescript-eslint/no-unused-vars, @typescript-eslint/no-empty-function
    onEnd(span: ReadableSpan): void {}

    shutdown(): Promise<void> {
        return Promise.resolve();
    }
}