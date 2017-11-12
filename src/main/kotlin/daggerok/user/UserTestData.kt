package daggerok.user

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackageClasses = arrayOf(UserRepository::class))
open class UserTestData(val userRepository: UserRepository) {

  @Bean
  open fun runner() = CommandLineRunner {

    val log = LoggerFactory.getLogger("testdata runner")

    listOf("Max", "Fax", "Bax")
        .map { User.of(it) }
        .forEach { userRepository.save(it) }

    userRepository.findAll()
        .forEach { log!!.info("user: {}", it) }
  }
}
