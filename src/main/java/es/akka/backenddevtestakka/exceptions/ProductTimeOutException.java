package es.akka.backenddevtestakka.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT, reason = "Product not found")
public class ProductTimeOutException extends RuntimeException {
    public ProductTimeOutException() {
        super();
    }
}
