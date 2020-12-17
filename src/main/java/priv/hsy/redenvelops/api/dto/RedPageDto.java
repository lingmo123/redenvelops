package priv.hsy.redenvelops.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedPageDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigInteger rid;

    @NotNull(message = "不能为空")
    @Min(0)
    private Integer currentPage;

    @NotNull(message = "不能为空")
    private Integer pageSize;

}
