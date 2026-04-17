package vn.edu.fpt.dto.response.rule;

import java.math.BigDecimal;

public class PriceResponse {

    private BigDecimal price;

    public PriceResponse(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}