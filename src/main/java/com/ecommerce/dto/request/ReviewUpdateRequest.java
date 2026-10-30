package com.ecommerce.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateRequest {


    @Min(value = 1,message = "rating needs to be between 1 and 5")
    @Max(value = 5,message = "rating needs to be between 1 and 5")
    private Integer rating;

    @Size(max = 1000)
    private String comment;
}
