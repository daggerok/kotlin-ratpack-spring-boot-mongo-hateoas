package daggerok.user

import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter
import java.io.Serializable

@Document
data class User(@Id var id: String? = null,
                var username: String? = null) : Serializable {

  companion object {
    fun of(name: String) = User(username = name)
  }
}

@RepositoryRestResource
interface UserRepository : MongoRepository<User, String> {

  fun findFirstByUsername(username: String): User?

  fun findFirstByUsernameContainingIgnoreCase(name: String): User?

  fun findAllByUsernameContainingIgnoreCase(name: String): List<User>

  fun findAllByUsernameContainingIgnoreCaseOrUsernameContainingIgnoreCase(that: String, tose: String): List<User>
}

@Configuration
open class UserRepositoryConfig : RepositoryRestConfigurerAdapter() {

  override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
    config.exposeIdsFor(User::class.java)
  }
}
