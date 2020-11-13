package priv.hsy.redenvelops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class RedInfo {
    @TableId(type = IdType.AUTO)
    private Integer rid;
    private Double totalmoney;
    private Integer count;
    private Boolean status;
    private Integer sendid;
}
