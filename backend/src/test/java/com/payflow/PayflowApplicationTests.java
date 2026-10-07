package com.payflow;

import com.payflow.config.JwtService;
import com.payflow.dto.CheckoutDtos;
import com.payflow.entity.Product;
import com.payflow.entity.User;
import com.payflow.repository.InventoryReservationRepository;
import com.payflow.repository.OrderRepository;
import com.payflow.repository.ProductRepository;
import com.payflow.repository.UserRepository;
import com.payflow.service.CheckoutService;
import com.payflow.service.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:payflow;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "app.jwt.secret=test-secret-for-payflow-at-least-32-bytes"
})
@AutoConfigureMockMvc
class PayflowApplicationTests {
  @Autowired MockMvc mvc;
  @Autowired CheckoutService checkoutService;
  @Autowired JwtService jwtService;
  @Autowired UserRepository users;
  @Autowired ProductRepository products;
  @Autowired OrderRepository orders;
  @Autowired InventoryReservationRepository reservations;
  @MockitoBean StripeService stripe;

  private Product product;
  private User user;

  @BeforeEach
  void setUp() {
    reservations.deleteAll();
    orders.deleteAll();
    products.deleteAll();
    users.deleteAll();
    user = new User();
    user.setEmail("shopper@example.com");
    user.setPasswordHash("test-hash");
    user = users.save(user);
    product = new Product();
    product.setName("Test product");
    product.setPriceCents(1500);
    product.setStock(5);
    product = products.save(product);
  }

  @Test
  void discoversControllersRepositoriesAndSecurity() throws Exception {
    mvc.perform(get("/api/products")).andExpect(status().isOk());
    mvc.perform(post("/api/checkout/session")
        .contentType("application/json").content("{\"items\":[]}"))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void allowsFrontendPreflight() throws Exception {
    mvc.perform(options("/api/checkout/session")
        .header("Origin", "http://localhost:5173")
        .header("Access-Control-Request-Method", "POST")
        .header("Access-Control-Request-Headers", "authorization,content-type"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
  }

  @Test
  void rejectsInvalidNestedCartItems() throws Exception {
    String token = jwtService.issueToken(user.getEmail());
    for (String item : List.of("{\"productId\":1,\"quantity\":0}",
        "{\"productId\":1,\"quantity\":-2}", "{\"quantity\":1}", "null")) {
      mvc.perform(post("/api/checkout/session")
          .header("Authorization", "Bearer " + token)
          .contentType("application/json").content("{\"items\":[" + item + "]}"))
          .andExpect(status().isBadRequest());
    }
    assertThat(reservations.count()).isZero();
  }

  @Test
  void rejectsOverflowingLineAndCartTotalsWithoutReservingInventory() {
    product.setPriceCents(Integer.MAX_VALUE);
    products.save(product);
    var requests = List.of(
        new CheckoutDtos.CheckoutRequest(List.of(new CheckoutDtos.CartItem(product.getId(), 2))),
        new CheckoutDtos.CheckoutRequest(List.of(
            new CheckoutDtos.CartItem(product.getId(), 1),
            new CheckoutDtos.CartItem(product.getId(), 1)))
    );
    for (var request : requests) {
      assertThatThrownBy(() -> checkoutService.createCheckout(user.getEmail(), request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Order total exceeds the supported amount");
      assertThat(products.findById(product.getId()).orElseThrow().getStock()).isEqualTo(5);
      assertThat(reservations.count()).isZero();
      assertThat(orders.count()).isZero();
    }
  }

  @Test
  void rollsBackInventoryAndOrderWhenStripeThrowsCheckedException() throws Exception {
    when(stripe.createCheckoutSession(anyString(), anyList()))
        .thenThrow(new Exception("Payment provider unavailable"));
    var request = new CheckoutDtos.CheckoutRequest(
        List.of(new CheckoutDtos.CartItem(product.getId(), 2)));

    assertThatThrownBy(() -> checkoutService.createCheckout(user.getEmail(), request))
        .hasMessage("Payment provider unavailable");

    assertThat(products.findById(product.getId()).orElseThrow().getStock()).isEqualTo(5);
    assertThat(reservations.count()).isZero();
    assertThat(orders.count()).isZero();
  }
}
