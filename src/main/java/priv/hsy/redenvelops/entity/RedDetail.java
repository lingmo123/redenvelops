package priv.hsy.redenvelops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedDetail {
    @TableId(type = IdType.AUTO)//主键自增 数据库中需要设置主键自增
    private Integer id;
    private Integer rid;
    private Integer receiveId;
    private Double getMoney;
}
