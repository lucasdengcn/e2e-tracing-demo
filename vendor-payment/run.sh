
#export JAVA_TOOL_OPTIONS="-javaagent:../opentelemetry-javaagent.jar"
#export OTEL_SERVICE_NAME="payment-gateway"

#export OTEL_TRACES_EXPORTER=zipkin
#export OTEL_METRICS_EXPORTER=none
#export OTEL_LOGS_EXPORTER=none
#export OTEL_EXPORTER_ZIPKIN_ENDPOINT=http://localhost:9411

#export OTEL_TRACES_EXPORTER=otlp
#export OTEL_METRICS_EXPORTER=none
#export OTEL_LOGS_EXPORTER=none
#
#export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
#export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:14318

export OTEL_TRACES_EXPORTER=none
export OTEL_METRICS_EXPORTER=none
export OTEL_LOGS_EXPORTER=none

mvn clean
mvn spring-boot:run