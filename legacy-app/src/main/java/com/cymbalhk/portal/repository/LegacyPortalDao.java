package com.cymbalhk.portal.repository;

import com.cymbalhk.portal.model.Merchant;
import com.cymbalhk.portal.model.MerchantOrder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Hand-written 2016 DAO using raw JdbcTemplate SQL.
 */
@Repository
public class LegacyPortalDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Merchant> merchantRowMapper = new RowMapper<Merchant>() {
        @Override
        public Merchant mapRow(ResultSet rs, int rowNum) throws SQLException {
            Merchant m = new Merchant();
            m.setId(rs.getInt("id"));
            m.setMerchantCode(rs.getString("merchant_code"));
            m.setName(rs.getString("name"));
            m.setDistrict(rs.getString("district"));
            m.setTier(rs.getString("tier"));
            m.setContactEmail(rs.getString("contact_email"));
            m.setCreatedAt(rs.getTimestamp("created_at"));
            return m;
        }
    };

    private final RowMapper<MerchantOrder> orderRowMapper = new RowMapper<MerchantOrder>() {
        @Override
        public MerchantOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
            MerchantOrder o = new MerchantOrder();
            o.setId(rs.getInt("id"));
            o.setOrderRef(rs.getString("order_ref"));
            o.setMerchantId(rs.getInt("merchant_id"));
            o.setMerchantName(rs.getString("merchant_name"));
            o.setAmountHkd(rs.getBigDecimal("amount_hkd"));
            o.setFeeHkd(rs.getBigDecimal("fee_hkd"));
            o.setNetPayoutHkd(rs.getBigDecimal("net_payout_hkd"));
            o.setStatus(rs.getString("status"));
            o.setCreatedBy(rs.getString("created_by"));
            o.setCreatedAt(rs.getTimestamp("created_at"));
            o.setUpdatedAt(rs.getTimestamp("updated_at"));
            return o;
        }
    };

    public List<Merchant> findAllMerchants() {
        return jdbcTemplate.query(
            "SELECT * FROM merchants ORDER BY id ASC",
            merchantRowMapper
        );
    }

    /**
     * LEGACY NOTE: Uses raw string concatenation (known SQL injection risk).
     */
    public List<Merchant> searchMerchants(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllMerchants();
        }
        String sql = "SELECT * FROM merchants WHERE LOWER(name) LIKE LOWER('%" + keyword + "%') "
                   + "OR LOWER(district) LIKE LOWER('%" + keyword + "%') ORDER BY id ASC";
        return jdbcTemplate.query(sql, merchantRowMapper);
    }

    public Merchant findMerchantById(Integer id) {
        List<Merchant> rows = jdbcTemplate.query(
            "SELECT * FROM merchants WHERE id = ?",
            new Object[]{id},
            merchantRowMapper
        );
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<MerchantOrder> findAllOrders() {
        String sql = "SELECT o.*, m.name AS merchant_name "
                   + "FROM merchant_orders o "
                   + "JOIN merchants m ON m.id = o.merchant_id "
                   + "ORDER BY o.created_at DESC, o.id DESC";
        return jdbcTemplate.query(sql, orderRowMapper);
    }

    public MerchantOrder findOrderById(Integer id) {
        String sql = "SELECT o.*, m.name AS merchant_name "
                   + "FROM merchant_orders o "
                   + "JOIN merchants m ON m.id = o.merchant_id "
                   + "WHERE o.id = ?";
        List<MerchantOrder> rows = jdbcTemplate.query(sql, new Object[]{id}, orderRowMapper);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public Integer insertOrder(final MerchantOrder order) {
        final String sql = "INSERT INTO merchant_orders "
                   + "(order_ref, merchant_id, amount_hkd, fee_hkd, net_payout_hkd, status, created_by) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, order.getOrderRef());
                ps.setInt(2, order.getMerchantId());
                ps.setBigDecimal(3, order.getAmountHkd());
                ps.setBigDecimal(4, order.getFeeHkd());
                ps.setBigDecimal(5, order.getNetPayoutHkd());
                ps.setString(6, order.getStatus());
                ps.setString(7, order.getCreatedBy());
                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void updateOrderStatus(Integer orderId, String newStatus) {
        jdbcTemplate.update(
            "UPDATE merchant_orders SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
            newStatus,
            orderId
        );
    }

    public void insertAuditLog(Integer orderId, String action, String actor, String details) {
        jdbcTemplate.update(
            "INSERT INTO audit_logs (order_id, action, actor, details) VALUES (?, ?, ?, ?)",
            orderId,
            action,
            actor,
            details
        );
    }
}
