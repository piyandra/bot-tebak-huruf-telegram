package bot.telegram.wordle.service;

import bot.telegram.wordle.entity.User;
import bot.telegram.wordle.repository.UserRespository;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRespository userRespository;

    public UserService(UserRespository userRespository) {
        this.userRespository = userRespository;
    }

    public User findById(Long id) {
        return userRespository.findById(id).orElse(null);
    }
    public void addUser(User user) {
        userRespository.save(user);
    }
    public Long findUserPoints(Long userId) {
        return userRespository.findById(userId).map(User::getPoints).orElse(0L);
    }
    public void updatePoints(Long id, Long points) {
        userRespository.findById(id).map(user -> {
            user.setPoints(points + user.getPoints());
            return userRespository.save(user);
        });
    }
}
