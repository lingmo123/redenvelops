package priv.hsy.redenvelops.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.User;
import priv.hsy.redenvelops.mapper.UserMapper;
import priv.hsy.redenvelops.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public List<User> selectAll() {
        return this.baseMapper.selectList(null);
    }

    @Override
    public User selectById(Integer id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public boolean updateById(User user) {
        this.baseMapper.updateById(user);
        return true;
    }

    @Override
    public int delete(Wrapper<User> wrapper) {
        this.baseMapper.delete(wrapper);
        return 0;
    }

    /**
     * 用户抢到红包更新账户余额
     *
     * @param id
     * @param money
     * @return
     */
    @Override
    public String updateUserifo(int uid, double money) {
        User user = selectById(uid);
        double totalmoney = user.getMoney();
        totalmoney += money;
        user.setMoney(totalmoney);
        updateById(user);
        return "更新用户余额成功！";
    }
}
