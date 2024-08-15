package de.bashburg.springbook.orderservice.order.domain

import de.bashburg.springbook.orderservice.TestcontainersConfiguration
import de.bashburg.springbook.orderservice.config.DataConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.test.StepVerifier
import kotlin.test.Test

@Testcontainers
@DataR2dbcTest
@Import(DataConfig::class)
class OrderRepositoryR2dbcTests {

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Test
    fun `create rejected order`() {
        val rejectedOrder = OrderService.buildRejectedOrder("12347890", 3)

        StepVerifier
            .create(orderRepository.save(rejectedOrder))
            .expectNextMatches { it.status == OrderStatus.REJECTED }
            .verifyComplete()
    }

    companion object {
        @Container
        @ServiceConnection
        val postgresContainer = TestcontainersConfiguration.getPostgresContainer()
    }
}