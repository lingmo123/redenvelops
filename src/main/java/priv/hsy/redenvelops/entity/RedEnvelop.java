package priv.hsy.redenvelops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

/**
 * @author 20023636
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedEnvelop {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer rid;
    private Integer sendId;
    private Double totalMoney;
    private Double restMoney;
    private Integer count;
    private Integer restCount;
    private Boolean status;
}
