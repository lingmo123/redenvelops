package priv.hsy.redenvelops.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedPageBean {

    private BigInteger rid;

    private Integer currentPage;

    private Integer pageSize;

}
