package de.bashburg.springbook.orderservice.order.web

import de.bashburg.springbook.orderservice.config.SecurityConfig
import de.bashburg.springbook.orderservice.order.domain.Order
import de.bashburg.springbook.orderservice.order.domain.OrderService
import de.bashburg.springbook.orderservice.order.domain.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import kotlin.test.Test

@WebFluxTest(OrderController::class)
@Import(SecurityConfig::class)
class OrderControllerTestWebFluxTests {

    @Autowired
    lateinit var webClient: WebTestClient

    @MockBean
    lateinit var reactiveJwtDecoder: ReactiveJwtDecoder

    @MockBean
    lateinit var orderService: OrderService

    @Test
    fun `when book is not available then reject order`() {
        val orderRequest = OrderRequest("1234567890", 3)
        val expectedOrder = OrderService.buildRejectedOrder(orderRequest.isbn, orderRequest.quantity)

        given(orderService.submitOrder(orderRequest.isbn, orderRequest.quantity)).willReturn(Mono.just(expectedOrder))

        webClient
            .mutateWith(
                mockJwt().authorities(SimpleGrantedAuthority("ROLE_customer"))
            )
            .post()
            .uri("/orders")
            .bodyValue(orderRequest)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(Order::class.java)
            .value {
                assertThat(it).isNotNull
                assertThat(it.status).isEqualTo(OrderStatus.REJECTED)
            }
    }
}