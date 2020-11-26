package priv.hsy.redenvelops.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import priv.hsy.redenvelops.entity.*;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.service.*;
import priv.hsy.redenvelops.utils.ArithmeticUtils;
import priv.hsy.redenvelops.utils.ResultUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Api(tags = "红包管理接口模块")
public class RedEnvelopController {

    /**
     * 红包最小值
     */
    private static final Double MIN = 0.01;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private RedEnvelopService redEnvelopService;
    @Autowired
    private RedDetailService redDetailService;
    @Autowired
    private RedisService redisService;

    /**
     * 分页显示所有未发送的红包详情
     *
     * @param pageBean 实体类
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/getpageredinfo")
    @ApiOperation(value = "分页查询所有未发送的红包详情")
    public Result<Object> getPageRedInfo(@RequestBody RedPageBean pageBean) {
        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper.eq("STATUS", 0);
        RedEnvelopPageBean redEnvelopPageBean = redEnvelopService.selectPage(
                pageBean.getCurrentPage(), pageBean.getPageSize(), wrapper);
        return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopPageBean);
    }

    /**
     * 分页显示所有正在抢的红包详情
     *
     * @param pageBean 实体类
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/getpageredenvelop")
    @ApiOperation(value = "分页查询所有正在抢的红包详情")
    public Result<Object> getPageRedEnvelop(@RequestBody RedPageBean pageBean) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper.eq("STATUS", 1);
        RedEnvelopPageBean redEnvelopPageBean = redEnvelopService.selectPage(
                pageBean.getCurrentPage(), pageBean.getPageSize(), wrapper);
        return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopPageBean);
    }

    /**
     * 分页显示所有已抢完的红包详情
     *
     * @param pageBean 实体类
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/getpageoverred")
    @ApiOperation(value = "分页查询所有已抢完的红包详情")
    public Result<Object> getPageOverRed(@RequestBody RedPageBean pageBean) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper.eq("STATUS", 2);
        RedEnvelopPageBean redEnvelopPageBean = redEnvelopService.selectPage(
                pageBean.getCurrentPage(), pageBean.getPageSize(), wrapper);
        return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopPageBean);
    }

    /**
     * 创建红包接口
     *
     * @param redEnvelop 获取前端form表信息
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/setred")
    @ApiOperation(value = "创建红包")
    public Result<Object> setRed(@RequestBody RedEnvelop redEnvelop) {
        BigInteger uid = redEnvelop.getSendId();
        User user = userService.selectById(uid);
        String userMoney = user.getMoney();
        String totalMoney = redEnvelop.getTotalMoney();
        if (Double.parseDouble(totalMoney )< MIN) {//红包金额下限
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
        } else if (redEnvelop.getCount() < 1) {//红包数量下限
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
        } else if (userMoney.compareTo(totalMoney)<0) {//用户余额是否不足
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {//发红包用户余额更新
            BigDecimal restmoney =ArithmeticUtils.sub(userMoney,totalMoney);
            user.setMoney(restmoney.toString());
            userService.updateById(user);
            return redEnvelopService.setRed(redEnvelop);
        }
    }

    /**
     * 更新已设置的红包
     *
     * @param envelop 实体类
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/updatered")
    @ApiOperation(value = "更新已设置的红包")
    public Result<Object> updateRed(@RequestBody RedEnvelop envelop) {

        BigInteger rid = envelop.getRid();
        RedEnvelop redEnvelop = redEnvelopService.selectById(rid);
        User user = userService.selectById(redEnvelop.getSendId());
        String userMoney = user.getMoney();
        String totalMoney = redEnvelop.getTotalMoney();
        String totalMoney1= envelop.getTotalMoney();
        if (Double.parseDouble(totalMoney1 )< MIN) {//红包金额下限
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
        } else if (envelop.getCount() < 1) { //红包数量下限
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
        } else if (Double.parseDouble(totalMoney1) > Double.parseDouble(totalMoney) + Double.parseDouble(userMoney)) { //用户余额是否不足
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {
            //红包信息更新
            BigDecimal restmoney =ArithmeticUtils.add(ArithmeticUtils.sub(totalMoney1,totalMoney).toString(),userMoney);
            redEnvelopService.updateEnvelop(redEnvelop, envelop.getCount(), totalMoney1);
            //发红包用户余额更新
            user.setUid(redEnvelop.getSendId());
            user.setMoney(restmoney.toString());
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
    public Result<Object> sendRed(@ApiParam(value = "红包id") @RequestParam("rid") BigInteger rid) {
        String result = redEnvelopService.sendRed(rid);
        log.info("发送红包结果 = " + result);
        return ResultUtil.result(ResultEnum.REDSEND_SUCCESS);
    }

    /**
     * 抢红包接口
     *
     * @param rid 获取前端红包id
     * @param uid 获取前端抢红包用户id
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/getred")
    @ApiOperation(value = "抢红包")
    public Result<Object> getRed(@ApiParam(value = "红包id") @RequestParam("rid") BigInteger rid,
                                 @ApiParam(value = "用户id") @RequestParam("uid") BigInteger uid) {
        return redisService.redGet(rid , uid);
    }

    /**
     * 无id抢红包
     *
     * @param rid 红包id
     * @return 返回状态码和消息提示
     */
    @ApiOperation(value = "无id抢红包")
    @PostMapping(value = "/api/getrednouid")
    public Result<Object> test(@ApiParam(value = "红包id") @RequestParam("rid") BigInteger rid) {
        String key = rid + "redMoneyList";
        String redInfoCount = rid + "redInfoCount:";
        String redInfoMoney = rid + "redInfoMoney";
        return redisService.redRedisGetNoUid(key, redInfoCount, redInfoMoney);
    }

    /**
     * 获得红包明细
     *
     * @param rid 红包id
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/getreddetails")
    @ApiOperation(value = "添加红包明细")
    public Result<Object> getRedDetails(@ApiParam(value = "红包id") @RequestParam("rid") BigInteger rid) {
        List<RedDetail> redDetail = redDetailService.selectDetails(rid);
        return ResultUtil.result(ResultEnum.SUCCESS, redDetail);

    }

    @PostMapping(value = "/api/test")
    public Result<Object> test(@RequestParam("rid") BigInteger rid,
                               @RequestParam("uid") BigInteger uid) {

        String key = rid + "RedMoneyList";
        String redInfoCount = rid + "redInfoCount";
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "first", 10, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(lock)) {
            return ResultUtil.fail("lock");
        }
        try {
            Integer restSize = (Integer) redisTemplate.opsForValue().get(redInfoCount);
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
                redEnvelopService.update(rid, money);
                //用户抢到红包更新账户余额
                userService.updateUserinfo(uid, money);

                if (restSize == 1) {
                    redEnvelopService.overRed(rid);
                }
                return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
            }

        } catch (Exception e) {
            return ResultUtil.result(ResultEnum.FAIL);
        } finally {
            redisTemplate.delete("lock");
        }
        return null;
    }

    /**
     * 分页显示所有未发送的红包详情
     *
     * @param pageBean 实体类
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/queryrid")
    @ApiOperation(value = "根据红包id查询")
    @ApiResponse(code = 200, message = "查询成功")

    public Result<Object> queryId(@RequestBody RedPageBean pageBean) {
        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper.eq("rid", pageBean.getRid());
        return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopService.selectPage(
                pageBean.getCurrentPage(), pageBean.getPageSize(), wrapper));

    }

}

