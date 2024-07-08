//package lima.amanda.task_manager.security.controller;
//
//import lima.amanda.task_manager.security.config.JWTUtil;
//import lima.amanda.task_manager.security.domain.AuthRequest;
//import lima.amanda.task_manager.security.domain.AuthResponse;
//import lima.amanda.task_manager.security.entity.UserEntity;
//import lima.amanda.task_manager.security.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    private final JWTUtil jwtUtil;
//
//    private final UserService userService;
//
//    public AuthController(JWTUtil jwtUtil, UserService userService) {
//        this.jwtUtil = jwtUtil;
//        this.userService = userService;
//    }
//
//    @PostMapping("/login")
//    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
//        return userService.findByUsername(authRequest.getUsername())
//                .map(userDetails -> {
//                    if (userDetails.getPassword().equals(authRequest.getPassword())) {
//                        return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(authRequest.getUsername())));
//                    } else {
//                        throw new BadCredentialsException("Invalid username or password");
//                    }
//                }).switchIfEmpty(Mono.error(new BadCredentialsException("Username not found")));
//    }
//
//    @PostMapping("/signup")
//    public Mono<ResponseEntity<String>> signup(@RequestBody UserEntity user) {
//        return userService.save(user)
//                .map(savedUser -> ResponseEntity.ok("User signup successfully"));
//    }
//
//}
