package es.akka.backenddevtestakka.services;

import es.akka.backenddevtestakka.dto.ProductDetailDto;
import es.akka.backenddevtestakka.exceptions.ProductInternalServerException;
import es.akka.backenddevtestakka.exceptions.ProductNotFoundException;
import es.akka.backenddevtestakka.exceptions.ProductTimeOutException;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


@Service
@Log
public class ExistingApisService {
    @Value("${existingApis.url}")
    private String existingApisUrl;

    @Value("${existingApis.paths.get-product-similarids}")
    private String pathSimilarIds;

    @Value("${existingApis.paths.get-product-productId}")
    private String pathProductIds;

    WebClient webClient = WebClient.create();

    private ProductDetailDto getProductById(String id) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(existingApisUrl+pathProductIds)
                        .build(id)
                )
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(error -> Mono.error(new ProductNotFoundException(id))))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(error -> Mono.error(new ProductInternalServerException())))
                .bodyToMono(ProductDetailDto.class)
                .timeout(Duration.ofSeconds(15))
                .doOnError(TimeoutException.class, e -> {
                    throw new ProductTimeOutException();
                })
                .block();
    }

    private List<String> getSimilarProductsIdList(String id) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(existingApisUrl+pathSimilarIds)
                        .build(id)
                )
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(error -> Mono.error(new ProductNotFoundException(id))))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> clientResponse.bodyToMono(String.class).flatMap(error -> Mono.error(new ProductInternalServerException())))
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .timeout(Duration.ofSeconds(15))
                .doOnError(TimeoutException.class, e -> {
                    throw new ProductTimeOutException();
                })
                .block();
    }

    public List<ProductDetailDto> getSimilarProducts(String id) {

        List<String> ids = getSimilarProductsIdList(id);
        List<ProductDetailDto> similarProducts = new ArrayList<>();

        if (ids != null) {
            for (String productId : ids) {
                similarProducts.add(getProductById(productId));
            }
        }

        return similarProducts;
    }
}
