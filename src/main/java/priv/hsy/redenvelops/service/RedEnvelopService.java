package priv.hsy.redenvelops.service;

import priv.hsy.redenvelops.entity.*;


public interface RedEnvelopService {
    RedEnvelop selectById(int rid);

    boolean update(Integer rid, Double money);

    String updateEnvelop(RedEnvelop redEnvelop, int count, double totalMoney);

    String sendRed(int rid);

    RedEnvelopPageBean selectPage(int currentPage, int pageSize);

    RedEnvelopPageBean selectPageRedInfo(int currentPage, int pageSize);

    Result<Object> setRed(RedEnvelop redEnvelop);

    String overRed(int rid);
}
