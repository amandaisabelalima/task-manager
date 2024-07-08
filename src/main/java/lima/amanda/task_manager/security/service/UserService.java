//package lima.amanda.task_manager.security.service;
//
//import lima.amanda.task_manager.security.entity.UserEntity;
//import lima.amanda.task_manager.security.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//@Service
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public Mono<UserEntity> findByUsername(String username) {
//        return userRepository.findByUsername(username);
//    }
//    public Mono<UserEntity> save(UserEntity user) {
//        return userRepository.save(user);
//    }
//}
