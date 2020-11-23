package priv.hsy.redenvelops.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import priv.hsy.redenvelops.entity.*;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.service.*;
import priv.hsy.redenvelops.utils.ResultUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Api( tags = "红包管理接口模块")
public class UserController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private RedInfoService redInfoService;
    @Autowired
    private RedEnvelopService redEnvelopService;
    @Autowired
    private RedDetailService redDetailService;
    @Autowired
    private RedisService redisService;

    @GetMapping(value = "/getuser")
    public List<User> getUser() {
        List<User> userList = userService.selectAll();
        log.info("userList = [{}]", userList);
        return userList;
    }

    /**
     * 获取红包详情
     *
     * @return 返回状态码，消息提示以及设置的红包详情
     */
    @GetMapping(value = "/api/getredinfo")
    public Result<Object> getRedInfo() {
        List<RedInfo> redInfoList = redInfoService.select();
        return ResultUtil.result(ResultEnum.SUCCESS, redInfoList);
    }

    /**
     * 分页显示所有设置红包详情
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/api/getpageredinfo")
    @ApiOperation(value = "分页查询所有红包设置红包详情")
    public Result<Object> getPageRedInfo(@ApiParam(value = "当前页") @RequestParam("currentPage") Integer currentPage,
                                         @ApiParam(value = "每页大小") @RequestParam("pageSize") Integer pageSize) {

        RedInfoPageBean redInfoPageBean = redInfoService.selectPage(currentPage, pageSize);
        log.info("list = [{}]", redInfoPageBean.getPageRecode());
        return ResultUtil.result(ResultEnum.SUCCESS, redInfoPageBean);
    }

    @GetMapping(value = "/api/getredenvelop")
    public Result<Object> getRedEnvelop() {
        List<RedEnvelop> redEnvelopList = redEnvelopService.selectAll();
        return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopList);
    }

    @PostMapping(value = "/api/getpageredenvelop")
    @ApiOperation(value = "分页查询所有正在发送的红包详情")
    public Result<Object> getPageRedEnvelop(@ApiParam(value = "当前页") @RequestParam("currentPage") Integer currentPage,
                                            @ApiParam(value = "每页大小") @RequestParam("pageSize") Integer pageSize) {

        RedEnvelopPageBean redEnvelopPageBean = redEnvelopService.selectPage(currentPage, pageSize);
        log.info("list = [{}]", redEnvelopPageBean.getPageRecode());
        return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopPageBean);
    }

    /**
     * 编辑红包接口
     *
     * @param redInfo 获取前端form表信息
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/setred")
    @ApiOperation(value = "编辑红包信息")
    public Result<Object> setRed(@RequestBody RedInfo redInfo) {
        User user = userService.selectById(redInfo.getSendId());
        double money = user.getMoney();
        //红包金额下限
        if (redInfo.getTotalMoney() < 0.01) {
//            return "红包金额最小值为0.01";
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
            //红包数量下限
        } else if (redInfo.getCount() < 1) {
//            return "红包数量最小为1";
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
            //用户余额是否不足
        } else if (money < redInfo.getTotalMoney()) {
//            return "你的余额不足，无法设置红包，请加班挣钱！";
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {//发红包用户余额更新
            double restmoney = money - redInfo.getTotalMoney();
            User user1 = new User();
            user1.setUid(1);
            user1.setMoney(restmoney);
            userService.updateById(user1);
            return redInfoService.setRed(redInfo);
        }
    }

    /**
     * 更新已设置的红包
     *
     * @param rid        红包ID
     * @param count      红包数量
     * @param totalMoney 红包金额
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/updatered")
    @ApiOperation(value = "更新已设置的红包")
    public Result<Object> updateRed(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid,
                                    @ApiParam(value = "红包数量") @RequestParam("count") Integer count,
                                    @ApiParam(value = "红包金额") @RequestParam("totalMoney") Double totalMoney) {
        RedInfo redInfo = redInfoService.selectById(rid);
        User user = userService.selectById(redInfo.getSendId());
        if (totalMoney < 0.01) {
//            return "红包金额最小值为0.01";
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
            //红包数量下限
        } else if (count < 1) {
//            return "红包数量最小为1";
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
            //用户余额是否不足
        } else if (totalMoney > redInfo.getTotalMoney() + user.getMoney()) {
//            return "你的余额不足，无法设置红包！";
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {
            //红包信息更新
            double restmoney = (totalMoney - redInfo.getTotalMoney()) + user.getMoney();
            redInfoService.update(redInfo, rid, count, totalMoney);
            //发红包用户余额更新
            user.setUid(redInfo.getSendId());
            user.setMoney(restmoney);
            userService.updateById(user);
            return ResultUtil.result(ResultEnum.REDSET_SUCCESS);
        }
    }

    /**
     * 发送红包接口
     *
     * @param rid 红包id
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/sendred")
    @ApiOperation(value = "发送红包")
    public Result<Object> sendRed(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid) {
        RedInfo redInfo =  redInfoService.selectById(rid);
        RedEnvelop redEnvelop = new RedEnvelop();
        redEnvelop.setCount(redInfo.getCount());
        //设置剩余红包个数
        redEnvelop.setRestCount(redInfo.getCount());
        redEnvelop.setRid(redInfo.getRid());
        redEnvelop.setTotalMoney(redInfo.getTotalMoney());
        //设置剩余金额
        redEnvelop.setRestMoney(redInfo.getTotalMoney());
        redEnvelop.setSendId(redInfo.getSendId());
        redEnvelop.setStatus(true);

        redEnvelopService.insert(redEnvelop);
        //return "成功发送红包！"
        return ResultUtil.result(ResultEnum.REDSEND_SUCCESS);
    }

    /**
     * 抢红包接口
     *
     * @param rid 获取前端红包id
     * @param uid  获取前端抢红包用户id
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/getred")
    @ApiOperation(value = "抢红包")
    public Result<Object> getRed(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid,
                                 @ApiParam(value = "用户id") @RequestParam("id") Integer uid) {
        String key = rid + "redMoneylist";
        String redInfoCount = rid + "redInfoCount";
        String redInfoMoney = rid + "redInfoMoney";
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "first", 5, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(lock)) {
            return ResultUtil.result(ResultEnum.FAIL, "lock");
        }
        try {
//            QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
//            queryWrapper.and(i -> i
//                    .eq("rid", rid)
//                    .eq("receive_id", uid));
//            if (redDetailService.selectOne(queryWrapper) != null) {
////                return "您已经抢过红包了";
//                return ResultUtil.result(ResultEnum.USERGET_NO);
//            }
            Double restMoney = (Double) redisTemplate.opsForValue().get(redInfoMoney);
            Integer restSize = (Integer) redisTemplate.opsForValue().get(redInfoCount);

            log.info("restMoney = {}", restMoney);
            log.info("restSize = {}", restSize);
            if (restSize > 0) {
                redisTemplate.opsForValue().decrement(redInfoCount);

                Double money = (Double) redisTemplate.boundListOps(key).rightPop();
                String usergetkey = rid + "get" + uid;
                assert money != null;
                Boolean lock1 = redisTemplate.opsForValue().setIfAbsent(usergetkey, money);
                if (Boolean.FALSE.equals(lock1)) {
                    redisTemplate.opsForValue().increment(redInfoCount);
                    redisTemplate.opsForList().rightPush(key, money);
                    return ResultUtil.result(ResultEnum.USERGET_NO);
                }
                //红包明细
                redDetailService.insert(uid, rid, money);
                //更新红包剩余数量
                redEnvelopService.update(rid,money);
                //用户抢到红包更新账户余额
                userService.updateUserifo(uid, money);
                return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
            }
            else{

            }
        } catch (Exception e) {
            return ResultUtil.result(ResultEnum.FAIL);
        } finally {
            redisTemplate.delete("lock");
        }
        return null;
    }

    @PostMapping(value = "/api/getrednouid")
    public Result<Object> test(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid) {
        String key = rid + "redMoneylist";
        String redInfoCount = rid + "redInfoCount";
        String redInfoMoney = rid + "redInfoMoney";
        return redisService.redRedisGetNoUid(key, redInfoCount, redInfoMoney);
    }

    @PostMapping(value = "/test1")
    public Result<Object> test1(@RequestParam("rid") Integer rid, @RequestParam("uid") Integer id) {

        String key = rid + "redMoneylist";
        String redInfoCount = rid + "redInfoCount";
        String redInfoMoney = rid + "redInfoMoney";

        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "first", 2, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(lock)) {
            return ResultUtil.result(ResultEnum.FAIL, "lock");
        }
        try {
            QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(i -> i
                    .eq("rid", rid)
                    .eq("receiveid", id));
            if (redDetailService.selectOne(queryWrapper) != null) {
//                return "您已经抢过红包了";
                return ResultUtil.result(ResultEnum.USERGET_NO);
            } else {
                Double restMoney = (double) redisTemplate.opsForValue().get(redInfoMoney);
                Integer restSize = (int) redisTemplate.opsForValue().get(redInfoCount);

                log.info("restMoney = {}", restMoney);
                log.info("restSize = {}", restSize);
//                AtomicLong atomicLong = new AtomicLong(10L);
                if (restSize != null && restSize > 0) {
                    redisTemplate.opsForValue().decrement(redInfoCount);
                    Double money = (Double) redisTemplate.boundListOps(key).rightPop();
                    if (money == null) {
                        return ResultUtil.result(ResultEnum.FAIL, "monry == null");
                    }
//                    AtomicLong atomicLong = new AtomicLong();
//                    atomicLong.g
                    restMoney -= money;
                    redisTemplate.opsForValue().set(redInfoMoney, restMoney);

                    return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
                } else {
                    return ResultUtil.result(ResultEnum.REDCOUNT_NO);
                }
            }
        } catch (Exception e) {
            return ResultUtil.result(ResultEnum.FAIL, "catch");
        } finally {
            redisTemplate.delete("lock");
        }

//            String key = rid + "redmoneylist";
//            //获得此次红包金额
//            int remainSize = redEnvelopcurrent.getRestCount();
//            Double remainMoney = redEnvelopcurrent.getRestMoney();
//            double money = (double) redisTemplate.boundListOps(key).rightPop();
//            log.info("money = ", money);
//            //用户抢到红包更新账户余额
//            userService.updateUserifo(id, money);
//            //更新红包数据
//            remainSize--;
//            remainMoney -= money;
//            redEnvelopService.updateEnvelop(redEnvelopcurrent, remainMoney, remainSize);
//            //更新红包明细
//            redDetailService.updateRedDetail(redEnvelopcurrent, money, rid, id);
//            //return 成功抢到红包
//            return ResultUtil.result(ResultEnum.REDGET_SUCCESS);

    }

    /**
     * 添加红包明细
     * @param rid 红包id
     * @return
     */
    @PostMapping(value = "/api/getreddetails")
    @ApiOperation(value = "添加红包明细")
    public Result<Object> getRedDetails(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid) {
        List<RedDetail> redDetail = redDetailService.selectDetails(rid);
        return ResultUtil.result(ResultEnum.SUCCESS, redDetail);

    }
}

