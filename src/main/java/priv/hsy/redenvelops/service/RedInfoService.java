package priv.hsy.redenvelops.service;

import priv.hsy.redenvelops.entity.RedInfo;
import priv.hsy.redenvelops.entity.Result;

import java.util.List;

public interface RedInfoService {

    List<RedInfo> selectAll();

    Result<Object> select();

    int insert(RedInfo redInfo);

    Result<Object> setred(RedInfo redInfo);


}
