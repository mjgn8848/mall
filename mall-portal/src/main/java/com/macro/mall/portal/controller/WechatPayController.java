package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.WechatPayParam;
import com.macro.mall.portal.service.WechatPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 微信支付Controller
 */
@Tag(name = "WechatPayController", description = "微信支付接口")
@RestController
@RequestMapping("/wechat/pay")
public class WechatPayController {

    @Autowired
    private WechatPayService wechatPayService;

    @Operation(summary = "发起微信支付")
    @PostMapping("/pay")
    public CommonResult<Object> pay(@RequestBody WechatPayParam param) {
        Object result = wechatPayService.pay(param);
        return CommonResult.success(result);
    }

    @Operation(summary = "微信支付回调")
    @PostMapping("/notify")
    public void notify(@RequestBody String result) {
        wechatPayService.notify(result);
    }
}