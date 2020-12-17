package priv.hsy.redenvelops.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

@Data
public class RedUserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "不能为空")
    private BigInteger rid;
    @NotNull(message = "不能为空")
    private BigInteger uid;
}
