package au.com.dealsdirect.data.network.model.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * dp Created by Admin on 11/9/16.
 */
public class GetOrdersResponse {

    @SerializedName("next_date")
    @Expose
    private String nextDate;

    @SerializedName("orders")
    @Expose
    private List<Order> orders;

    public String getNextDate() {
        return nextDate;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public static class Order {
        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("number")
        @Expose
        private Integer number;

        @SerializedName("invoices")
        @Expose
        private List<Invoice> invoices;

        @SerializedName("shipments")
        @Expose
        private List<Shipment> shipments;

        @SerializedName("payment")
        @Expose
        private Payment payment;

        public String getId() {
            return id;
        }

        public Integer getNumber() {
            return number;
        }

        public List<Invoice> getInvoices() {
            return invoices;
        }

        public List<Shipment> getShipments() {
            return shipments;
        }

        public Payment getPayment() {
            return payment;
        }

        public static class Invoice {
            @SerializedName("id")
            @Expose
            private String id;

            @SerializedName("number")
            @Expose
            private Integer number;

            @SerializedName("status")
            @Expose
            private String status;

            @SerializedName("ordered")
            @Expose
            private List<Product> ordered;

            @SerializedName("cancelled")
            @Expose
            private List<Product> cancelled;

            @SerializedName("returned")
            @Expose
            private List<Product> returned;

            @SerializedName("delivery")
            @Expose
            private Delivery delivery;

            @SerializedName("payment")
            @Expose
            private Payment payment;

            @SerializedName("actions")
            @Expose
            private List<String> actions;

            public String getId() {
                return id;
            }

            public Integer getNumber() {
                return number;
            }

            public String getStatus() {
                return status;
            }

            public List<Product> getOrdered() {
                return ordered;
            }

            public List<Product> getCancelled() {
                return cancelled;
            }

            public List<Product> getReturned() {
                return returned;
            }

            public Delivery getDelivery() {
                return delivery;
            }

            public Payment getPayment() {
                return payment;
            }

            public List<String> getActions() {
                return actions;
            }

            public static class Product {
                @SerializedName("order_item_id")
                @Expose
                private String orderItemId;

                @SerializedName("return_id")
                @Expose
                private String returnId;

                @SerializedName("cancelled_quantity")
                @Expose
                private Integer cancelledQuantity;

                @SerializedName("refund_total")
                @Expose
                private Float refundTotal;

                @SerializedName("price")
                @Expose
                private Float price;

                @SerializedName("price_total")
                @Expose
                private Float priceTotal;

                @SerializedName("quantity")
                @Expose
                private Integer quantity;

                @SerializedName("type")
                @Expose
                private String type;

                @SerializedName("id")
                @Expose
                private String id;

                @SerializedName("name")
                @Expose
                private String name;

                @SerializedName("brand")
                @Expose
                private String brand;

                @SerializedName("brand_link")
                @Expose
                private String brandLink;

                @SerializedName("product_link")
                @Expose
                private String productLink;

                @SerializedName("image_url")
                @Expose
                private String imageUrl;

//                @SerializedName("customization")
//                @Expose
//                private ??? customization;

                @SerializedName("size")
                @Expose
                private String size;

                @SerializedName("actions")
                @Expose
                private List<String> actions;

                public Float getPrice() {
                    return price;
                }

                public Float getPriceTotal() {
                    return priceTotal;
                }

                public String getType() {
                    return type;
                }

                public String getId() {
                    return id;
                }

                public String getName() {
                    return name;
                }

                public String getBrand() {
                    return brand;
                }

                public String getBrandLink() {
                    return brandLink;
                }

                public String getProductLink() {
                    return productLink;
                }

                public String getImageUrl() {
                    return imageUrl;
                }

                public String getSize() {
                    return size;
                }

                public List<String> getActions() {
                    return actions;
                }

                public Integer getQuantity() {
                    return quantity;
                }

                public String getOrderItemId() {
                    return orderItemId;
                }

                public String getReturnId() {
                    return returnId;
                }

                public Integer getCancelledQuantity() {
                    return cancelledQuantity;
                }

                public Float getRefundTotal() {
                    return refundTotal;
                }
            }

            public static class Delivery {
                @SerializedName("contact")
                @Expose
                private String contact;

                @SerializedName("address")
                @Expose
                private String address;

                @SerializedName("from")
                @Expose
                private String from;

                @SerializedName("to")
                @Expose
                private String to;

                @SerializedName("estimate")
                @Expose
                private String estimate;

                @SerializedName("tracking_url")
                @Expose
                private String trackingUrl;

                @SerializedName("price")
                @Expose
                private String price;

                @SerializedName("steps")
                @Expose
                private List<Step> steps;

                public String getContact() {
                    return contact;
                }

                public String getAddress() {
                    return address;
                }

                public String getFrom() {
                    return from;
                }

                public String getTo() {
                    return to;
                }

                public void setTo(String to) {
                    this.to = to;
                }

                public String getEstimate() {
                    return estimate;
                }

                public String getTrackingUrl() {
                    return trackingUrl;
                }

                public String getPrice() {
                    return price;
                }

                public List<Step> getSteps() {
                    return steps;
                }

                public static class Step {
                    @SerializedName("title")
                    @Expose
                    private String title;

                    @SerializedName("text")
                    @Expose
                    private String text;

                    @SerializedName("date")
                    @Expose
                    private String date;

                    @SerializedName("date_type")
                    @Expose
                    private String dateType;

                    @SerializedName("status")
                    @Expose
                    private String status;

                    @SerializedName("type")
                    @Expose
                    private String type;

                    @SerializedName("action")
                    @Expose
                    private String action;

                    @SerializedName("icon_url")
                    @Expose
                    private String iconUrl;

                    @SerializedName("progress")
                    @Expose
                    private Float progress;

                    public String getTitle() {
                        return title;
                    }

                    public String getText() {
                        return text;
                    }

                    public String getDate() {
                        return date;
                    }

                    public String getDateType() {
                        return dateType;
                    }

                    public String getStatus() {
                        return status;
                    }

                    public void setStatus(String status) {
                        this.status = status;
                    }

                    public String getType() {
                        return type;
                    }

                    public String getAction() {
                        return action;
                    }

                    public String getIconUrl() {
                        return iconUrl;
                    }

                    public void setIconUrl(String iconUrl) {
                        this.iconUrl = iconUrl;
                    }

                    public float getProgress() {
                        return progress == null ? 0 : progress;
                    }
                }
            }
        }

        public static class Shipment {
            @SerializedName("location_filter")
            @Expose
            private String locationFilter;

            @SerializedName("delivery_price")
            @Expose
            private Float deliveryPrice;

            @SerializedName("invoiceNumbers")
            @Expose
            private List<Integer> invoiceNumbers;

            public String getLocationFilter() {
                return locationFilter;
            }

            public Float getDeliveryPrice() {
                return deliveryPrice;
            }

            public List<Integer> getInvoiceNumbers() {
                return invoiceNumbers;
            }
        }

        public static class Payment {
            @SerializedName("payment_amount")
            @Expose
            private Float paymentAmount;

            @SerializedName("delivery_amount")
            @Expose
            private Float deliveryAmount;

            @SerializedName("discount_amount")
            @Expose
            private Float discountAmount;

            @SerializedName("items_amount")
            @Expose
            private Float itemsAmount;

            @SerializedName("items_count")
            @Expose
            private Integer itemsCount;

            @SerializedName("refund_amount")
            @Expose
            private Float refundAmount;

            @SerializedName("refund_items_count")
            @Expose
            private Float refundItemsCount;

            @SerializedName("total_amount")
            @Expose
            private Float totalAmount;

            public Float getPaymentAmount() {
                return paymentAmount;
            }

            public Float getDeliveryAmount() {
                return deliveryAmount;
            }

            public Float getDiscountAmount() {
                return discountAmount;
            }

            public Float getItemsAmount() {
                return itemsAmount;
            }

            public Integer getItemsCount() {
                return itemsCount;
            }

            public Float getRefundAmount() {
                return refundAmount;
            }

            public Float getRefundItemsCount() {
                return refundItemsCount;
            }

            public Float getTotalAmount() {
                return totalAmount;
            }
        }
    }
}
