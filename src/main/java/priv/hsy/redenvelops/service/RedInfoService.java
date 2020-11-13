package priv.hsy.redenvelops.service;

import priv.hsy.redenvelops.entity.RedInfo;

import java.util.List;

public interface RedInfoService {

    List<RedInfo> selectAll();

    int insert(RedInfo redInfo);

    String setred(RedInfo redInfo);
}
