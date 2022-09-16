package es.akka.backenddevtestakka.dto;

import lombok.Data;

@Data
public class ProductDetailDto {

    private String id;

    private String name;

    private String price;

    private boolean availability;

}
