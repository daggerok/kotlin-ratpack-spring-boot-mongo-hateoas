package daggerok.ratpack;

import daggerok.RatpackSpringBootApplication;
import daggerok.message.MessageService;
import daggerok.user.User;
import daggerok.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ratpack.exec.Blocking;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;
import ratpack.jackson.JsonRender;
import ratpack.spring.config.EnableRatpack;

import java.util.List;

import static java.util.Collections.singletonMap;

@EnableRatpack
@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = {
    RatpackSpringBootApplication.class,
})
public class RatpackRouterConfig {

  final UserRepository userRepository;
  final MessageService messageService;

  @Bean
  Handler one() {
    return ctx -> Blocking
        .get(() -> singletonMap("first by username",
                                userRepository.findFirstByUsernameContainingIgnoreCase(
                                    ctx.getAllPathTokens().get("one"))))
        .then(map -> ctx.render(Jackson.json(map)));
  }

  @Bean
  Handler two() {
    return ctx ->
        Blocking.get(() -> {

          final String name = ctx.getAllPathTokens().getOrDefault("two", "hz...");
          final List<User> user = userRepository.findAllByUsernameContainingIgnoreCase(name);

          return singletonMap("find by username like", user);

        }).then(map -> ctx.render(Jackson.json(map)));
  }

  @Bean
  Handler deeper() {
    return ctx -> Blocking.get(() -> {

      final String that = ctx.getAllPathTokens().get("two");
      final String those = ctx.getAllPathTokens().get("three");
      final List<User> users = userRepository.findAllByUsernameContainingIgnoreCaseOrUsernameContainingIgnoreCase(that, those);

      return singletonMap("all by username like", users);

    }).then(map -> {

      final JsonRender result = Jackson.json(map);
      ctx.render(result);

    });
  }

  @Bean
  Action<Chain> routes() {
    return chain -> chain.get(":one", one())
                         .get(":one/:two", two())
                         .get(":one/:two/:three", deeper())
                         .get(ctx -> ctx.render("hey! " + messageService.makesMeHappy()));
  }
}
