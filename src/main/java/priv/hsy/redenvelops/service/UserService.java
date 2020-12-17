package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import priv.hsy.redenvelops.entity.User;

import java.math.BigInteger;
import java.util.List;

public interface UserService {

    List<User> selectAll();

    User selectById(BigInteger id);

    boolean updateById(User user);

    int delete(Wrapper<User> wrapper);
    /**
     * 用户抢到红包更新账户余额
     *
     * @param uid
     * @param money
     * @return
     */
    String updateUserinfo(BigInteger uid, double money);
}
