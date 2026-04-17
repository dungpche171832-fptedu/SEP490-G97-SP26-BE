package vn.edu.fpt.dto.response.rule;

import java.math.BigDecimal;

public class PriceResponse {

    private BigDecimal price;
    private int totalSeat;
    private BigDecimal totalPrice;

    public PriceResponse(BigDecimal price, int totalSeat) {
        this.price = price;
        this.totalSeat = totalSeat;
        this.totalPrice = price.multiply(BigDecimal.valueOf(totalSeat)); // Tính giá cuối cùng
    }

    // Getter và Setter
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getTotalSeat() {
        return totalSeat;
    }

    public void setTotalSeat(int totalSeat) {
        this.totalSeat = totalSeat;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}