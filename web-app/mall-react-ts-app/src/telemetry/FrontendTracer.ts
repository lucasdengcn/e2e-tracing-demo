import { SpanStatusCode} from "@opentelemetry/api";
import { CompositePropagator, W3CTraceContextPropagator } from '@opentelemetry/core';
import { registerInstrumentations } from '@opentelemetry/instrumentation';
import { getWebAutoInstrumentations } from '@opentelemetry/auto-instrumentations-web';
//import { ConsoleSpanExporter, SimpleSpanProcessor, TracerConfig, WebTracerProvider } from '@opentelemetry/sdk-trace-web';
import {
  BatchSpanProcessor,
  TracerConfig,
  WebTracerProvider,
  TraceIdRatioBasedSampler,
  Span,
} from '@opentelemetry/sdk-trace-web';

import { ZoneContextManager } from '@opentelemetry/context-zone';
import { SemanticResourceAttributes } from '@opentelemetry/semantic-conventions';
import { Resource, browserDetector } from '@opentelemetry/resources';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-proto';
import { detectResourcesSync } from '@opentelemetry/resources/build/src/detect-resources';
import { SessionIdSpanProcessor } from './SessionIdSpanProcessor';
import { ModuleIdSpanProcessor } from './ModuleIdSpanProcessor';

export interface FrontendTracerConfig {
    collectorEndpoint: string;
    appVersion: string;
    appEnv: string;
    appName: string;
    appModule: string;
    samplePercentage: number;
    apiEndpoint: string;
    apiRegex?: string
}

const FrontendTracer = async (config: FrontendTracerConfig) => {

    const collectorOptions = {
        url: `${config.collectorEndpoint}/v1/traces`, // OTLE collector backend
        headers: {
          Authorization: 'Bearer access-token'
        },
        concurrencyLimit: 1
      };
      
      let resource = new Resource({
        [SemanticResourceAttributes.SERVICE_VERSION]: config.appVersion,
        [SemanticResourceAttributes.SERVICE_NAMESPACE]: config.appEnv,
        [SemanticResourceAttributes.SERVICE_NAME]: config.appName,
      });
      const detectedResources = detectResourcesSync({ detectors: [browserDetector] });
      resource = resource.merge(detectedResources);
      //
      const providerConfig: TracerConfig = {
        resource: resource,
        sampler: new TraceIdRatioBasedSampler(config.samplePercentage),
      };
      //
      const provider = new WebTracerProvider(providerConfig);
      
      // we will use ConsoleSpanExporter to check the generated spans in dev console
      // provider.addSpanProcessor(new SimpleSpanProcessor(new ConsoleSpanExporter()));
      
      provider.addSpanProcessor(new ModuleIdSpanProcessor(config.appModule));
      provider.addSpanProcessor(new SessionIdSpanProcessor());

      provider.addSpanProcessor(
        new BatchSpanProcessor(new OTLPTraceExporter(collectorOptions)),
      );
      
      provider.register({
        contextManager: new ZoneContextManager(),
        propagator: new CompositePropagator({
            propagators: [new W3CTraceContextPropagator()],
          }),
      });
      
      /**
       * https://www.honeycomb.io/blog/observable-frontends-opentelemetry-browser
       * https://github.com/open-telemetry/opentelemetry-js-contrib/issues/548
       */
      registerInstrumentations({
        tracerProvider: provider,
        instrumentations: [
          // getWebAutoInstrumentations initializes all the package.
          // it's possible to configure each instrumentation if needed.
          getWebAutoInstrumentations({
            '@opentelemetry/instrumentation-fetch': {
              propagateTraceHeaderCorsUrls: [new RegExp(config.apiEndpoint)],
              clearTimingResources: true,
              enabled: true,
              applyCustomAttributesOnSpan: (
                span,
                request,
                result
              ) => {
                span.setAttribute('app.synthetic_request', 'false');
                const attributes = (span as any).attributes;
                if (attributes.component === "fetch") {
                  span.updateName(
                    `${attributes["http.method"]} ${attributes["http.url"]}`
                  );
                }
                if (result.status && result.status > 299) {
                  span.setStatus({ code: SpanStatusCode.ERROR });
                }
              },	
            },
            '@opentelemetry/instrumentation-document-load': {
              enabled: true,
              applyCustomAttributesOnSpan: {
                documentLoad: (span) => {
                },
                documentFetch: (span) => {
                },
                resourceFetch: (span, resource) => {
                }
              }
            },
            '@opentelemetry/instrumentation-user-interaction': {
              eventNames: ['click'],
              enabled: true,
              shouldPreventSpanCreation: (eventType, element, span) => {
                var traceable = element.getAttribute('traceable');
                // console.log(element);
                if(!traceable || traceable == 'false'){
                  // will not record this span
                  console.log('OTEL will not record this span, it is not traceable: ', element);
                  return true;
                }
                span.updateName(`click ${element.id}`);
                span.setAttribute('target.id', element.id);
                span.setAttribute('target.title', element.title || element.innerText);
                // will record this span
                console.log('OTEL will record this span: ', element);
                return false;
              }	
            },
            '@opentelemetry/instrumentation-xml-http-request': {
              enabled: true,
              propagateTraceHeaderCorsUrls: [new RegExp(config.apiEndpoint)],
            },
          }),
        ],
      });
      

};

export default FrontendTracer;