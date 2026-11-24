package com.example.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.stock.item.Item;
import com.example.stock.item.ItemRepository;
import com.example.stock.movement.StockMovementRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
class StockMovementApiTest {

    @Autowired MockMvc mvc;
    @Autowired ItemRepository items;
    @Autowired StockMovementRepository movements;

    @BeforeEach
    void setUp() {
        movements.deleteAll();
        items.deleteAll();
        items.save(new Item("T-001", "テスト部品", "個", new BigDecimal("10"), new BigDecimal("5"), null));
    }

    private String body(String code, String type, String qty) {
        return """
                {"itemCode":"%s","type":"%s","quantity":%s,"reason":"テスト","userName":"tester"}
                """.formatted(code, type, qty);
    }

    private BigDecimal stockOf(String code) {
        return items.findById(code).orElseThrow().getQuantity();
    }

    @Test
    @DisplayName("入庫：在庫数が増え、区分 E の履歴が残る（旧 unitEntradaMat.BtnGravarClick）")
    void receiveIncreasesStock() throws Exception {
        mvc.perform(post("/api/movements").contentType(APPLICATION_JSON).content(body("T-001", "IN", "2.5")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stockAfter").value(12.5));

        assertThat(stockOf("T-001")).isEqualByComparingTo("12.5");
        assertThat(movements.findAll()).singleElement()
                .satisfies(m -> assertThat(m.getType().legacyCode()).isEqualTo("E"));
    }

    @Test
    @DisplayName("出庫：在庫数が減り、区分 S の履歴が残る（旧 unitSaidaMat.BtnGravarClick）")
    void issueDecreasesStock() throws Exception {
        mvc.perform(post("/api/movements").contentType(APPLICATION_JSON).content(body("T-001", "OUT", "10")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stockAfter").value(0));

        assertThat(stockOf("T-001")).isEqualByComparingTo("0");
        assertThat(movements.findAll()).singleElement()
                .satisfies(m -> assertThat(m.getType().legacyCode()).isEqualTo("S"));
    }

    @Test
    @DisplayName("在庫を超える出庫は 422 で拒否し、在庫も履歴も変えない（旧 dbQuantExit のチェック）")
    void issueBeyondStockIsRejected() throws Exception {
        mvc.perform(post("/api/movements").contentType(APPLICATION_JSON).content(body("T-001", "OUT", "10.001")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.available").value(10));

        assertThat(stockOf("T-001")).isEqualByComparingTo("10");
        assertThat(movements.count()).isZero();
    }

    @Test
    @DisplayName("未登録の品目コードは 404（旧 dbCodMercExit「CÓDIGO não Cadastrado」）")
    void unknownItemIsRejected() throws Exception {
        mvc.perform(post("/api/movements").contentType(APPLICATION_JSON).content(body("NOPE", "IN", "1")))
                .andExpect(status().isNotFound());
        assertThat(movements.count()).isZero();
    }

    @Test
    @DisplayName("数量 0 以下・品目コード未入力は 400")
    void invalidRequestIsRejected() throws Exception {
        mvc.perform(post("/api/movements").contentType(APPLICATION_JSON).content(body("T-001", "IN", "0")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/movements").contentType(APPLICATION_JSON).content(body("", "IN", "1")))
                .andExpect(status().isBadRequest());
        assertThat(stockOf("T-001")).isEqualByComparingTo("10");
    }

    @Test
    @DisplayName("在庫一覧で最低在庫を下回る品目にフラグが立つ")
    void listFlagsItemsBelowMinimum() throws Exception {
        items.save(new Item("T-002", "不足部品", "個", new BigDecimal("1"), new BigDecimal("3"), null));
        mvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("T-001"))
                .andExpect(jsonPath("$[0].belowMinimum").value(false))
                .andExpect(jsonPath("$[1].belowMinimum").value(true));
    }
}
