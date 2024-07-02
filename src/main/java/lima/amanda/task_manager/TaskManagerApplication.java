package lima.amanda.task_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagerApplication {

	public static void main(String[] args) {
		System.out.println("teste ok!");
		SpringApplication.run(TaskManagerApplication.class, args);
	}

}
