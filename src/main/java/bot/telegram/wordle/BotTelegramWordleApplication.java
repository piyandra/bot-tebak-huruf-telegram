package bot.telegram.wordle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BotTelegramWordleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotTelegramWordleApplication.class, args);
	}

}
