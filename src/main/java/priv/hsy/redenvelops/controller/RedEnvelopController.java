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
     * 分页显示所有的红包详情
     *
     * @param pageBean 实体类
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/getpageallred")
    @ApiOperation(value = "分页查询所有的红包详情")
    public Result<Object> getPageAllRed(@RequestBody RedPageBean pageBean) {

        return redEnvelopService.selectPage(
                pageBean.getCurrentPage(), pageBean.getPageSize(), null);
    }

    /**
     * 分页查询所有正在抢和抢完的红包信息
     *
     * @param pageBean 实体类
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/getpageredenvelop")
    @ApiOperation(value = "分页查询所有正在抢和抢完的红包信息")
    public Result<Object> getPageRedInfo(@RequestBody RedPageBean pageBean) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).or().eq("status", 2);
        return redEnvelopService.selectPage(
                pageBean.getCurrentPage(), pageBean.getPageSize(), wrapper);
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
        //红包金额下限
        if (Double.parseDouble(redEnvelop.getTotalMoney()) < MIN) {
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
            //红包数量下限
        } else if (redEnvelop.getCount() < 1) {
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
        }  else {//发红包用户余额更新
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
        //红包金额下限
        if (Double.parseDouble(envelop.getTotalMoney()) < MIN) {
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
            //红包数量下限
        } else if (envelop.getCount() < 1) {
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
            //用户余额是否不足
        } else {
            return redEnvelopService.updateRed(envelop);
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
        return redEnvelopService.sendRed(rid);
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
        return redisService.redGet(rid, uid);
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
            return redDetailService.selectDetails(rid);
    }

    /**
     * 分页显示所有未发送的红包详情
     *
     * @param pageBean 实体类
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/queryrid")
    @ApiOperation(value = "根据红包id查询")
    public Result<Object> queryId(@RequestBody RedPageBean pageBean) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper.eq("rid", pageBean.getRid());
        return redEnvelopService.selectPage(
                pageBean.getCurrentPage(), pageBean.getPageSize(), wrapper);
    }


}

