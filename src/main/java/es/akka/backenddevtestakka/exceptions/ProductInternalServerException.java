package es.akka.backenddevtestakka.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Product not found")
public class ProductInternalServerException extends RuntimeException{
    public ProductInternalServerException() {
        super();
    }
}
