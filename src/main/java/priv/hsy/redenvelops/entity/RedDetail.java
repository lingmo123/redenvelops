package priv.hsy.redenvelops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;

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
    private Timestamp getTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getGetTime() {
        return getTime;
    }
}
