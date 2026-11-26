package com.cymbalhk.portal.web;

import com.cymbalhk.portal.model.Merchant;
import com.cymbalhk.portal.model.MerchantOrder;
import com.cymbalhk.portal.repository.LegacyPortalDao;
import com.cymbalhk.portal.service.OrderSettlementService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Monolithic controller mixing server-rendered Thymeleaf views and JSON API endpoints.
 */
@Controller
public class PortalController {

    @Autowired
    private LegacyPortalDao dao;

    @Autowired
    private OrderSettlementService settlementService;

    @Value("${cymbal.legacy.admin.username}")
    private String adminUsername;

    @Value("${cymbal.legacy.admin.fallback.plaintext}")
    private String adminPassword;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        System.out.println("[LEGACY-AUTH] Login attempt for user=" + username);
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            session.setAttribute("LOGGED_IN_USER", username);
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Invalid operator username or password");
        return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @RequestMapping(value = {"/", "/dashboard"}, method = RequestMethod.GET)
    public String dashboard(@RequestParam(value = "q", required = false) String query,
                            HttpSession session,
                            Model model) {
        model.addAttribute("user", session.getAttribute("LOGGED_IN_USER"));
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("merchants", dao.searchMerchants(query));
        model.addAttribute("orders", dao.findAllOrders());
        return "dashboard";
    }

    @RequestMapping(value = "/orders/new", method = RequestMethod.GET)
    public String newOrderForm(Model model) {
        model.addAttribute("merchants", dao.findAllMerchants());
        return "order-form";
    }

    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public String createOrderFromForm(@RequestParam Integer merchantId,
                                      @RequestParam BigDecimal amountHkd,
                                      HttpSession session) {
        String actor = String.valueOf(session.getAttribute("LOGGED_IN_USER"));
        settlementService.createOrder(merchantId, amountHkd, actor);
        return "redirect:/dashboard";
    }

    @RequestMapping(value = "/orders/{id}/status", method = RequestMethod.POST)
    public String updateStatusFromForm(@PathVariable Integer id,
                                       @RequestParam String targetStatus,
                                       HttpSession session) {
        String actor = String.valueOf(session.getAttribute("LOGGED_IN_USER"));
        settlementService.transitionStatus(id, targetStatus, actor);
        return "redirect:/dashboard";
    }

    // --- Ad-hoc JSON API endpoints ---

    @RequestMapping(value = "/api/health", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> health() {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("status", "UP");
        res.put("app", "cymbal-hk-legacy-portal");
        res.put("java", System.getProperty("java.version"));
        return res;
    }

    @RequestMapping(value = "/api/merchants", method = RequestMethod.GET)
    @ResponseBody
    public List<Merchant> apiMerchants(@RequestParam(value = "q", required = false) String query) {
        return dao.searchMerchants(query);
    }

    @RequestMapping(value = "/api/orders", method = RequestMethod.GET)
    @ResponseBody
    public List<MerchantOrder> apiOrders() {
        return dao.findAllOrders();
    }

    @RequestMapping(value = "/api/orders", method = RequestMethod.POST)
    @ResponseBody
    public MerchantOrder apiCreateOrder(@RequestBody Map<String, Object> body, HttpSession session) {
        Integer merchantId = Integer.valueOf(String.valueOf(body.get("merchantId")));
        BigDecimal amountHkd = new BigDecimal(String.valueOf(body.get("amountHkd")));
        String actor = String.valueOf(session.getAttribute("LOGGED_IN_USER"));
        return settlementService.createOrder(merchantId, amountHkd, actor);
    }

    @RequestMapping(value = "/api/orders/{id}/transition", method = RequestMethod.POST)
    @ResponseBody
    public MerchantOrder apiTransitionOrder(@PathVariable Integer id,
                                            @RequestBody Map<String, String> body,
                                            HttpSession session) {
        String actor = String.valueOf(session.getAttribute("LOGGED_IN_USER"));
        return settlementService.transitionStatus(id, body.get("targetStatus"), actor);
    }
}
