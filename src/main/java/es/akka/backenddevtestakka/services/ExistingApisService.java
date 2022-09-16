package es.akka.backenddevtestakka.services;

import es.akka.backenddevtestakka.dto.ProductDetailDto;
import es.akka.backenddevtestakka.exceptions.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Service
public class ExistingApisService {
    @Value("${existingApis.url}")
    private String existingApisUrl;

    @Value("${existingApis.paths.get-product-similarids}")
    private String pathSimilarIds;

    @Value("${existingApis.paths.get-product-productId}")
    private String pathProductIds;

    WebClient webClient = WebClient.create(existingApisUrl);

    private ProductDetailDto getProductById(String id) throws ProductNotFoundException{
        String endpoint = pathProductIds.replace("{productId}", id);

        Mono<ProductDetailDto> response = webClient.get()
                .uri(endpoint)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> clientResponse.bodyToMono(ProductDetailDto.class).flatMap(error -> Mono.error(new ProductNotFoundException(id))))
                .bodyToMono(ProductDetailDto.class);

        return response.block();
    }

    public List<ProductDetailDto> getSimilarProducts(String id) throws ProductNotFoundException{
        String endpoint = pathSimilarIds.replace("{productId}", id);

        Mono<List<String>> response = webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});

        List<String> ids = response.block();
        List<ProductDetailDto> similarProducts = new ArrayList<>();
        if (ids != null) {
            for(String productId: ids){
                similarProducts.add(getProductById(productId));
            }
        }
        return similarProducts;
    }
}
