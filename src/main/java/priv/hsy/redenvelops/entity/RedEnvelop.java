package priv.hsy.redenvelops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;

/**
 * @author 20023636
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedEnvelop {
    @TableId(type = IdType.AUTO)
    private Integer rid;
    private Integer sendId;
    private Double totalMoney;
    private Double restMoney;
    private Integer count;
    private Integer restCount;
    private Integer status;
    private Timestamp createTime;
    private Timestamp sendTime;
    private Timestamp updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getCreateTime() {
        return createTime;
    }
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getSendTime() {
        return sendTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getUpdateTime() {
        return updateTime;
    }


}
