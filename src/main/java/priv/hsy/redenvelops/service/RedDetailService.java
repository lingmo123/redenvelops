package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.netty.handler.codec.string.StringDecoder;
import priv.hsy.redenvelops.entity.RedDetail;
import priv.hsy.redenvelops.entity.Result;

import java.math.BigInteger;

public interface RedDetailService {
    /**
     * 查询数据库中此id是否抢过红包
     *
     * @param wrapper 条件语句
     * @return 查询结果
     */
    RedDetail selectOne(Wrapper<RedDetail> wrapper);
    /**
     * 更新抢红包明细
     *
     * @param uid   抢红包用户id
     * @param rid   红包id
     * @param money 所抢到的金额
     * @return
     */
    String insert(BigInteger uid, BigInteger rid, double money);
    /**
     * 在数据库中查找指定红包ID的明细
     *
     * @param rid 红包id
     * @return
     */
    Result<Object> selectDetails(BigInteger rid);
    /**
     * 查询指定用户抢红包明细
     *
     * @param uid
     * @return
     */
    Result<Object> userRedDetails(BigInteger uid);


}
