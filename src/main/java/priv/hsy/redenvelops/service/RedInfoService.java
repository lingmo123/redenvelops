package priv.hsy.redenvelops.service;

import priv.hsy.redenvelops.entity.RedInfoPageBean;
import priv.hsy.redenvelops.entity.RedInfo;
import priv.hsy.redenvelops.entity.Result;

import java.util.List;

public interface RedInfoService {

    RedInfo selectById(int rid);

    List<RedInfo> select();

    int insert(RedInfo redInfo);

    boolean update(RedInfo redInfo,int rid, int count, double totalMoney);

    Result<Object> setRed(RedInfo redInfo);

    RedInfoPageBean selectPage(int currentPage, int pageSize);
}
