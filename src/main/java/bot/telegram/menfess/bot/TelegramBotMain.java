package bot.telegram.menfess.bot;

import bot.telegram.menfess.UserState;

import bot.telegram.menfess.entity.User;
import bot.telegram.menfess.service.UserService;
import bot.telegram.menfess.utils.UserWordUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;



@Slf4j
public class TelegramBotMain extends TelegramLongPollingBot {

    final private UserService userService;

    private static final String GREEN_BOX = "\uD83D\uDFE9";
    private static final String YELLOW_BOX = "\uD83D\uDFE8";
    private static final String RED_BOX = "\uD83D\uDFE5";

    private final Map<Long, UserState> userStateMap;
    private final Map<Long, String> userAnswerMap;

    @Value("${bot.username}")
    private final String botUsername;

    public TelegramBotMain(String botToken, String botUsername, UserService userService) {
        super(botToken);
        this.botUsername = botUsername;
        this.userService = userService;
        this.userStateMap = new HashMap<>();
        this.userAnswerMap = new HashMap<>();

    }

    private void startGame(Long chatId) {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/bot/telegram/menfess/utils/wordlist.csv"))) {
            String[] words = br.lines().toArray(String[]::new);
            int randomIndex = (int) (Math.random() * words.length);
            String answer = words[randomIndex].toUpperCase();
            log.info(answer);
            userAnswerMap.put(chatId, answer);
            userStateMap.put(chatId, UserState.PLAYING);
            if (userService.findById(chatId) == null) {
                User user = new User();
                user.setId(chatId);
                user.setPoints(0L);
                userService.addUser(user);
            }
            message("Tebak huruf dari kata:\n" + GREEN_BOX + GREEN_BOX + GREEN_BOX + GREEN_BOX + GREEN_BOX + "\nKata terdiri dari 5 huruf yang terdaftar di KBBI.", chatId);
        } catch (Exception e) {
            log.error("Error reading word list: {}", e.getMessage());
            message("Terjadi kesalahan saat memulai permainan.", chatId);
            log.warn(e.getMessage());
        }
    }

    private void giveUp(Long chatId) {
        Long points = getPoints(chatId);
        if (getPoints(chatId) == null) {
            points = 0L;
        }

        String answer = userAnswerMap.get(chatId);
        message("Kata yang benar adalah: " + answer + "\nPoint kamu adalah " + points, chatId);
        userStateMap.put(chatId, UserState.STARTED);
        userAnswerMap.remove(chatId);
    }

    private void handleGuess(Long chatId, String guess) {
        String answer = userAnswerMap.get(chatId);
        char[] resultChars = new UserWordUtils().userWordListRequest(answer, guess);

        StringBuilder result = new StringBuilder();
        for (char c : resultChars) {
            switch (c) {
                case 'Y' -> result.append(GREEN_BOX);
                case 'B' -> result.append(YELLOW_BOX);
                case 'X' -> result.append(RED_BOX);
            }
        }

        if (result.toString().contains(YELLOW_BOX) || result.toString().contains(RED_BOX)) {
            message("Tebakanmu: " + result, chatId);
        } else {
            setPoints(chatId, 10L);
            var points = getPoints(chatId);
            message("Selamat, kamu sudah menebak dengan benar!\nPoint Kamu adalah " + points , chatId);
            userStateMap.put(chatId, UserState.STARTED);
            userAnswerMap.remove(chatId);
        }
    }

    private void message(String text, Long chatId) {
        try {
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Gagal mengirim pesan: {}", e.getMessage());
        }
    }

    private Long getPoints(Long usersId) {
        return userService.findUserPoints(usersId);
    }
    private void setPoints(Long usersId, Long points) {
        userService.updatePoints(usersId, points);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText().trim();

        if (inputText.equalsIgnoreCase("/start")) {
            startGame(chatId);
            return;
        }

        if (userStateMap.get(chatId) != UserState.PLAYING) {
            message("Ketik /start untuk memulai permainan.", chatId);
            return;
        }

        if (inputText.equalsIgnoreCase("/nyerah")) {
            giveUp(chatId);
            return;
        }

        if (inputText.length() != 5) {
            message("Tebakanmu harus terdiri dari 5 huruf!", chatId);
            return;
        }

        handleGuess(chatId, inputText.toUpperCase());
    }


    @Override
    public void clearWebhook() {

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }



}
