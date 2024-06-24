mvn clean

export JAVA_TOOL_OPTIONS="-javaagent:../opentelemetry-javaagent.jar -Dotel.javaagent.extensions=../otel-extension-0.0.1-SNAPSHOT.jar"
export OTEL_SERVICE_NAME="checkout"

export OTEL_TRACES_EXPORTER=otlp
export OTEL_METRICS_EXPORTER=otlp
export OTEL_LOGS_EXPORTER=otlp
#
export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:14318

dapr run --app-id checkout --app-port 9102 --dapr-http-port 3511 --dapr-grpc-port 60013 --log-level warn mvn spring-boot:run
