package priv.hsy.redenvelops.api.redenvelop;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import priv.hsy.redenvelops.api.dto.*;
import priv.hsy.redenvelops.entity.Result;

/**
*
* @author hsy
* @date 2020/12/10 14:13
*/

public interface RedEnvelopApi {

    @PostMapping(value = "/api/setred")
    public Result<Object> setRed(@RequestBody RedEnvelopDto redEnvelopDto);

}

