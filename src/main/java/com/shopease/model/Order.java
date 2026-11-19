package com.shopease.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.shopease.util.Money;

public class Order extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private int userId;
    private String customerName = "";
    private BigDecimal total = BigDecimal.ZERO;
    private OrderStatus status = OrderStatus.PLACED;
    private String paymentMethod = "";
    private String paymentLabel = "";
    private String shipName = "";
    private String shipPhone = "";
    private String shipAddress = "";
    private Date createdAt = new Date();
    private List<OrderItem> items = new ArrayList<>();
    private int itemCountFromDb;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName == null ? "" : customerName;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total == null ? BigDecimal.ZERO : total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod == null ? "" : paymentMethod;
    }

    public String getPaymentLabel() {
        return paymentLabel.isEmpty() ? paymentMethod : paymentLabel;
    }

    public void setPaymentLabel(String paymentLabel) {
        this.paymentLabel = paymentLabel == null ? "" : paymentLabel;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName == null ? "" : shipName;
    }

    public String getShipPhone() {
        return shipPhone;
    }

    public void setShipPhone(String shipPhone) {
        this.shipPhone = shipPhone == null ? "" : shipPhone;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress == null ? "" : shipAddress;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setItemCountFromDb(int itemCountFromDb) {
        this.itemCountFromDb = itemCountFromDb;
    }

    /** Total units in the order (uses the loaded lines, or the count from the admin list query). */
    public int getItemCount() {
        if (items.isEmpty()) {
            return itemCountFromDb;
        }
        int count = 0;
        for (OrderItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public BigDecimal getItemsSubtotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (OrderItem item : items) {
            sum = sum.add(item.getSubtotal());
        }
        return sum;
    }

    public BigDecimal getShippingCharge() {
        BigDecimal charge = total.subtract(getItemsSubtotal());
        return charge.signum() < 0 ? BigDecimal.ZERO : charge;
    }

    public String getItemsSubtotalDisplay() {
        return Money.format(getItemsSubtotal());
    }

    public String getShippingDisplay() {
        return getShippingCharge().signum() == 0 ? "Free" : Money.format(getShippingCharge());
    }

    public String getTotalDisplay() {
        return Money.format(total);
    }

    public String getCreatedDisplay() {
        return new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(createdAt);
    }

    @Override
    public String describe() {
        return "Order #" + getId() + " - " + getTotalDisplay() + " (" + status.getLabel() + ")";
    }
}
