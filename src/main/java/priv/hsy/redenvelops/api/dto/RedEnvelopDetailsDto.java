package priv.hsy.redenvelops.api.dto;

import lombok.Data;
import java.io.Serializable;
/**
*
* @author hsy
* @date 2020/12/16 10:37
*/

@Data
public class RedEnvelopDetailsDto implements Serializable {

    private static final long serialVersionUID=1L;
    private Integer totalRedCount;
    private Integer redNotCount;
    private Integer redSnatchCount;
    private Integer redOverCount;
    private String totalMoney;
    private String snatchMoney;
}
