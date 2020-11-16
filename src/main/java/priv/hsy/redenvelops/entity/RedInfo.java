package priv.hsy.redenvelops.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class RedInfo {
    @TableId(type = IdType.AUTO)
    private Integer rid;
    private Double totalmoney;
    private Integer count;
    private Boolean status;
    private Integer sendid;
    private Timestamp creattime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getCreattime() {
        return creattime;
    }
}
