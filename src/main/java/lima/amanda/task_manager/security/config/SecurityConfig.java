//package lima.amanda.task_manager.security.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//    private final JWTAuthenticationManager authenticationManager;
//
//    public SecurityConfig(JWTAuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
////        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
////        authenticationWebFilter.setServerAuthenticationConverter(authenticationManager.authenticationConverter());
//
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers("/api/auth/login","/api/auth/signup").permitAll()
//                        .anyExchange().authenticated()
//                )
//                .authenticationManager(authenticationManager)
////                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                .build();
//    }
//}
