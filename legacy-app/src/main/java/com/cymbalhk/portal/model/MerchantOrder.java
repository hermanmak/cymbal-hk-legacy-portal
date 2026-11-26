package com.cymbalhk.portal.model;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "merchant_orders")
public class MerchantOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_ref", nullable = false, unique = true)
    private String orderRef;

    @Column(name = "merchant_id", nullable = false)
    private Integer merchantId;

    @Column(name = "amount_hkd", nullable = false)
    private BigDecimal amountHkd;

    @Column(name = "fee_hkd", nullable = false)
    private BigDecimal feeHkd;

    @Column(name = "net_payout_hkd", nullable = false)
    private BigDecimal netPayoutHkd;

    @Column(nullable = false)
    private String status; // PENDING | APPROVED | SETTLED | CANCELLED

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    // Transient display field populated by join queries
    private transient String merchantName;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getOrderRef() { return orderRef; }
    public void setOrderRef(String orderRef) { this.orderRef = orderRef; }

    public Integer getMerchantId() { return merchantId; }
    public void setMerchantId(Integer merchantId) { this.merchantId = merchantId; }

    public BigDecimal getAmountHkd() { return amountHkd; }
    public void setAmountHkd(BigDecimal amountHkd) { this.amountHkd = amountHkd; }

    public BigDecimal getFeeHkd() { return feeHkd; }
    public void setFeeHkd(BigDecimal feeHkd) { this.feeHkd = feeHkd; }

    public BigDecimal getNetPayoutHkd() { return netPayoutHkd; }
    public void setNetPayoutHkd(BigDecimal netPayoutHkd) { this.netPayoutHkd = netPayoutHkd; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
}
