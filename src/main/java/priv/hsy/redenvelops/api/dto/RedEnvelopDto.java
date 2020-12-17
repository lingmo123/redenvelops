package priv.hsy.redenvelops.api.dto;

import java.io.Serializable;
import java.math.BigInteger;

import lombok.Data;

import javax.validation.constraints.*;


@Data
public class RedEnvelopDto implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotNull(message = "不能为空")
    @DecimalMin(value = "0.01",message = "红包金额必须大于或等于0.01")
    @DecimalMax(value = "10000" ,message = "红包金额必须小于或等于10000")
    private String totalMoney;

    @NotNull(message = "不能为空")
    @Min(value = 1,message = "红包个数最少为1")
    @Max(value = 1000,message = "红包个数最多为1000")
    private Integer count;

    @NotNull(message = "不能为空")
    private BigInteger sendId;

}
