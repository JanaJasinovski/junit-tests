package dao;

import org.mockito.stubbing.Answer1;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpy extends UserDao {
    private final UserDao userDao;
    private Map<Integer, Boolean> answers = new HashMap<>();
    public UserDaoSpy(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean delete(Integer userId) {
        // invocation++;
        return answers.getOrDefault(userId, userDao.delete(userId));
    }
}
