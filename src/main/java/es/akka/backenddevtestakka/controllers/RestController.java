package es.akka.backenddevtestakka.controllers;

import es.akka.backenddevtestakka.dto.ProductDetailDto;
import es.akka.backenddevtestakka.exceptions.ProductInternalServerException;
import es.akka.backenddevtestakka.exceptions.ProductNotFoundException;
import es.akka.backenddevtestakka.exceptions.ProductTimeOutException;
import es.akka.backenddevtestakka.services.ExistingApisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final ExistingApisService existingApisService;

    @Autowired
    public RestController(ExistingApisService existingApisService) {
        this.existingApisService = existingApisService;
    }

    @GetMapping("/product/{productId}/similar")
    public List<ProductDetailDto> getSimilarProductsById(@PathVariable String productId) {

        List<ProductDetailDto> similarProducts;

        try {
            similarProducts = existingApisService.getSimilarProducts(productId);
        } catch (ProductNotFoundException ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found", ex);
        } catch (ProductInternalServerException ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error, product not found or bad request", ex);
        } catch (ProductTimeOutException ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "TimeOut, the request is taking too long", ex);
        }
        return similarProducts;

    }
}
