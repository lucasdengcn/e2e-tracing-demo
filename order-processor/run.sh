mvn clean

export JAVA_TOOL_OPTIONS="-javaagent:../opentelemetry-javaagent.jar -Dotel.javaagent.extensions=../otel-extension-0.0.1-SNAPSHOT.jar"
export OTEL_SERVICE_NAME="order-processor2"

export OTEL_TRACES_EXPORTER=otlp
export OTEL_METRICS_EXPORTER=otlp
export OTEL_LOGS_EXPORTER=otlp
#
export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:14318

dapr run --app-id order-processor2 --app-port 9101 --dapr-http-port 3611 --dapr-grpc-port 60011 --log-level warn mvn spring-boot:run
