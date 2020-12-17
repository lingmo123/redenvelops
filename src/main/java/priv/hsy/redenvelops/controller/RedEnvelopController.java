package priv.hsy.redenvelops.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import priv.hsy.redenvelops.api.dto.*;
import priv.hsy.redenvelops.entity.*;
import priv.hsy.redenvelops.service.*;

import java.math.BigInteger;

@Api
@RestController
@Slf4j
public class RedEnvelopController {

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
    @ApiOperation("分页查询所有的红包详情")
    @PostMapping(value = "/api/getpageallred")
    public Result<Object> getPageAllRed(@Validated @RequestBody RedPageDto pageBean) {

        return redEnvelopService.selectPage(
                pageBean.getCurrentPage(), pageBean.getPageSize(), null);
    }

    /**
     * 分页查询所有正在抢和抢完的红包信息
     *
     * @param pageBean 实体类
     * @return 返回状态码和消息提示
     */
    @ApiOperation("分页查询所有正在抢和抢完的红包信息")
    @PostMapping(value = "/api/getpageredenvelop")
    public Result<Object> getPageRedInfo(@RequestBody RedPageDto pageBean) {
        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).or().eq("status", 2);
        return redEnvelopService.selectPage(
                pageBean.getCurrentPage(), pageBean.getPageSize(), wrapper);
    }


    //    @Override
    @ApiOperation("@Override")
    @PostMapping(value = "/api/setred")
    public Result<Object> setRed(@Validated @RequestBody RedEnvelopDto redEnvelopDto) {
            RedEnvelop redEnvelop = new RedEnvelop();
            redEnvelop.setTotalMoney(redEnvelopDto.getTotalMoney());
            redEnvelop.setCount(redEnvelopDto.getCount());
            redEnvelop.setSendId(redEnvelopDto.getSendId());
            return redEnvelopService.setRed(redEnvelop);

    }


    /**
     * 更新已设置的红包
     *
     * @return 返回状态码和消息提示
     * @data RedEnvelopDto 实体类
     */
    @ApiOperation("更新已设置的红包")
    @PostMapping(value = "/api/updatered")
    public Result<Object> updateRed(@Validated @RequestBody RedEnvelopDto redEnvelopDto) {
            RedEnvelop redEnvelop = new RedEnvelop();
            redEnvelop.setTotalMoney(redEnvelopDto.getTotalMoney());
            redEnvelop.setCount(redEnvelopDto.getCount());
            redEnvelop.setSendId(redEnvelopDto.getSendId());
            return redEnvelopService.updateRed(redEnvelop);

    }

    /**
     * 发送红包接口
     *
     * @param rid 红包id
     * @return 返回状态码和消息提示
     */
    @ApiOperation("发送红包")
    @PostMapping(value = "/api/sendred")
    public Result<Object> sendRed(@ApiParam(value = "红包id") @RequestParam(value = "rid", required = true) BigInteger rid) {
        return redEnvelopService.sendRed(rid);
    }

    /**
     * 抢红包接口
     *
     * @return 返回状态码和消息提示
     */
    @ApiOperation("抢红包")
    @PostMapping(value = "/api/getred")
    public Result<Object> getRed(@RequestBody RedUserDto redUserDto) {
        return redisService.redGet(redUserDto.getRid(), redUserDto.getUid());
    }

    /**
     * 无id抢红包
     *
     * @param rid 红包id
     * @return 返回状态码和消息提示
     */
    @ApiOperation("无id抢红包")
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
    @ApiOperation("获得红包明细")
    @PostMapping(value = "/api/getreddetails")
    public Result<Object> getRedDetails(@ApiParam(value = "红包id") @RequestParam("rid") BigInteger rid) {
        return redDetailService.selectDetails(rid);
    }

    /**
     * 根据红包id查询红包
     *
     * @param redPageDto 实体类
     * @return 返回状态码和消息提示
     */
    @ApiOperation("根据红包id查询红包")
    @PostMapping(value = "/api/queryrid")
    public Result<Object> queryId(@RequestBody RedPageDto redPageDto) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper.eq("rid", redPageDto.getRid());
        return redEnvelopService.selectPage(
                redPageDto.getCurrentPage(), redPageDto.getPageSize(), wrapper);
    }

    /**
     * 用户查看自己抢到的红包详情
     *
     * @param uid
     * @return
     */
    @ApiOperation("用户查看自己抢到的红包详情")
    @PostMapping(value = "/api/user/reddetails")

    public Result<Object> userRedDetails(@RequestParam("uid") BigInteger uid) {

        return redDetailService.userRedDetails(uid);
    }

    /**
     * 所有的红包详情
     * @return 返回状态码和消息提示
     */
    @GetMapping(value = "/api/allredinfo")
    @ApiOperation(value = "分页查询所有的红包详情")
    public Result<Object> getAllRedInfo() {
        redEnvelopService.test();
        return null;
//        return redEnvelopService.selectAllRed();
    }

}

