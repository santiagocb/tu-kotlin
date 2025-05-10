package bootiful.bootiful

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BootifulApplicationTests(@Autowired val customerRepository: CustomerRepository) {

	@Test
	fun contextLoads() {
		runBlocking {
			customerRepository.save(Customer(null, "Hadi"))
			val customers = customerRepository.findAll()
			println(customers.last())

			Assertions.assertNotNull(customers.last().id)
			Assertions.assertEquals(customers.count(), 7)
		}
	}

}
