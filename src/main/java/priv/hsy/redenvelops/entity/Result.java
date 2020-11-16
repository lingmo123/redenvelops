package priv.hsy.redenvelops.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import priv.hsy.redenvelops.utils.JsonUtil;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    @ApiModelProperty(name = "结果编码", required = true)
    private int code;
    @ApiModelProperty(name = "结果消息", required = true)
    private String message;
    @ApiModelProperty(name = "结果明细数据")
    private T data;

    @Override
    public String toString() {
        return JsonUtil.getJsonStringOrNull(this);
    }

}
