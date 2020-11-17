package priv.hsy.redenvelops.service;

import priv.hsy.redenvelops.entity.PageBean;
import priv.hsy.redenvelops.entity.RedInfo;
import priv.hsy.redenvelops.entity.Result;

import java.util.List;


public interface RedInfoService {

    List<RedInfo> select();

    PageBean selectPage(int limit);

    int insert(RedInfo redInfo);

    Result<Object> setRed(RedInfo redInfo);


}
