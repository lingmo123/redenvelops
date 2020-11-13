package priv.hsy.redenvelops.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import priv.hsy.redenvelops.entity.User;


@Mapper
public interface UserMapper extends BaseMapper<User> {
}
