package com.cymbalhk.portal.service;

import com.cymbalhk.portal.model.Merchant;
import com.cymbalhk.portal.model.MerchantOrder;
import com.cymbalhk.portal.repository.LegacyPortalDao;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core HKD merchant settlement rules and order state machine.
 */
@Service
public class OrderSettlementService {

    private static final BigDecimal VIP_THRESHOLD_HKD = new BigDecimal("50000.00");
    private static final BigDecimal VIP_RATE = new BigDecimal("0.018");
    private static final BigDecimal STANDARD_RATE = new BigDecimal("0.025");
    private static final BigDecimal MIN_FEE_HKD = new BigDecimal("15.00");

    @Autowired
    private LegacyPortalDao dao;

    public BigDecimal calculateFeeHkd(BigDecimal amountHkd, String merchantTier) {
        if (amountHkd == null || amountHkd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than HKD 0.00");
        }
        boolean isVip = "VIP".equalsIgnoreCase(merchantTier)
            || amountHkd.compareTo(VIP_THRESHOLD_HKD) >= 0;
        BigDecimal rate = isVip ? VIP_RATE : STANDARD_RATE;
        BigDecimal rawFee = amountHkd.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return rawFee.compareTo(MIN_FEE_HKD) < 0 ? MIN_FEE_HKD : rawFee;
    }

    @Transactional
    public MerchantOrder createOrder(Integer merchantId, BigDecimal amountHkd, String actor) {
        Merchant merchant = dao.findMerchantById(merchantId);
        if (merchant == null) {
            throw new IllegalArgumentException("Merchant not found: " + merchantId);
        }

        BigDecimal normalizedAmount = amountHkd.setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = calculateFeeHkd(normalizedAmount, merchant.getTier());
        BigDecimal netPayout = normalizedAmount.subtract(fee).setScale(2, RoundingMode.HALF_UP);

        String orderRef = "ORD-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());

        MerchantOrder order = new MerchantOrder();
        order.setOrderRef(orderRef);
        order.setMerchantId(merchantId);
        order.setMerchantName(merchant.getName());
        order.setAmountHkd(normalizedAmount);
        order.setFeeHkd(fee);
        order.setNetPayoutHkd(netPayout);
        order.setStatus("PENDING");
        order.setCreatedBy(actor);

        Integer id = dao.insertOrder(order);
        order.setId(id);

        dao.insertAuditLog(
            id,
            "ORDER_CREATED",
            actor,
            "Created " + orderRef + " for HKD " + normalizedAmount + " (fee HKD " + fee + ")"
        );
        return order;
    }

    @Transactional
    public MerchantOrder transitionStatus(Integer orderId, String targetStatus, String actor) {
        MerchantOrder existing = dao.findOrderById(orderId);
        if (existing == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        String current = existing.getStatus();
        if (!isValidTransition(current, targetStatus)) {
            throw new IllegalStateException(
                "Invalid status transition from " + current + " to " + targetStatus
            );
        }

        dao.updateOrderStatus(orderId, targetStatus);
        dao.insertAuditLog(
            orderId,
            "STATUS_" + targetStatus,
            actor,
            "Transitioned " + existing.getOrderRef() + " from " + current + " to " + targetStatus
        );

        existing.setStatus(targetStatus);
        return existing;
    }

    public boolean isValidTransition(String currentStatus, String targetStatus) {
        if ("PENDING".equals(currentStatus)) {
            return "APPROVED".equals(targetStatus) || "CANCELLED".equals(targetStatus);
        }
        if ("APPROVED".equals(currentStatus)) {
            return "SETTLED".equals(targetStatus);
        }
        return false;
    }
}
