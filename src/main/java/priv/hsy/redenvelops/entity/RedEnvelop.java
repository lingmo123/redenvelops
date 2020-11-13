package priv.hsy.redenvelops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author 20023636
 */
@Data
public class RedEnvelop {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer rid;
    private Integer sendid;
    private Double totalmoney;
    private Double restmoney;
    private Integer count;
    private Integer restcount;
    private Boolean status;
}
