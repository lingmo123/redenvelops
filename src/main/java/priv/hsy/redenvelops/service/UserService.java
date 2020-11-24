package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import priv.hsy.redenvelops.entity.User;

import java.util.List;

public interface UserService {

    List<User> selectAll();

    User selectById(Integer id);

    boolean updateById(User user);

    int delete(Wrapper<User> wrapper);

    String updateUserinfo(int uid, double money);
}
