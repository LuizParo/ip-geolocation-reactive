package me.github.lparo.geolocation.controller.exception;

import lombok.extern.slf4j.Slf4j;
import me.github.lparo.geolocation.controller.dto.ExceptionBody;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RequestPredicates.all;

@Slf4j
@Component
public class RouteExceptionHandler extends AbstractErrorWebExceptionHandler {

    public RouteExceptionHandler(ErrorAttributes errorAttributes,
                                 ApplicationContext applicationContext,
                                 ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new ResourceProperties(), applicationContext);

        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                all(),
                this::renderErrorResponse
        );
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        final Map<String, Object> errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.EXCEPTION
        ));
        log.info("error attributes: {}", errorAttributes);

        final String exception = (String) errorAttributes.get("exception");
        final String message = (String) errorAttributes.get("message");

        final ExceptionBody exceptionBody = ExceptionBody.from(exception, message);

        return ServerResponse.status(exceptionBody.getStatus())
                .contentType(APPLICATION_JSON)
                .body(fromValue(exceptionBody));
    }
}
