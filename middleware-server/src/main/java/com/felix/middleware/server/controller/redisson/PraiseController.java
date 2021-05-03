package com.felix.middleware.server.controller.redisson;

import com.felix.middleware.api.enums.StatusCode;
import com.felix.middleware.api.response.BaseResponse;
import com.felix.middleware.server.dto.PraiseDto;
import com.felix.middleware.server.service.IPraiseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 博客点赞业务Controller
 * @author: Felix
 * @date: 2021/5/3 20:30
 */
@RestController
public class PraiseController {

    private static final Logger log = LoggerFactory.getLogger(PraiseController.class);

    private static final String PREFIX = "blog/praise";

    @Autowired
    private IPraiseService praiseService;

    /**
     * 点赞
     * @param dto
     * @param result
     * @return
     */
    @RequestMapping(value = PREFIX + "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse addPraise(@RequestBody @Validated PraiseDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return new BaseResponse(StatusCode.INVALID_PARAMS);
        }

        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        Map<String, Object> resMap = new HashMap<>();
        try {
            //调用点赞博客处理方法-传统处理方式
            //praiseService.addPraise(dto);
            //调用点赞博客处理方法-分布式锁
            praiseService.addPraiseLock(dto);

            //获取博客的总点赞数
            Long total = praiseService.getBlogPraiseTotal(dto.getBlogId());

            resMap.put("praiseTotal", total);
        } catch (Exception e) {
            log.error("点赞博客-发生异常：{}", dto, e.fillInStackTrace());
            response = new BaseResponse(StatusCode.FAIL.getCode(), e.getMessage());
        }

        response.setData(resMap);
        return response;
    }

    /**
     * 取消点赞
     * @param dto
     * @param result
     * @return
     */
    @RequestMapping(value = PREFIX + "/cancel", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse cancelPraise(@RequestBody @Validated PraiseDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return new BaseResponse(StatusCode.INVALID_PARAMS);
        }

        //定义响应结果实例
        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        Map<String, Object> resMap = new HashMap<>();
        try {
            //调用取消点赞博客的处理方法
            praiseService.cancelPraise(dto);

            //获取博客的总点赞数
            Long total = praiseService.getBlogPraiseTotal(dto.getBlogId());
            resMap.put("praiseTotal", total);
        } catch (Exception e) {
            log.error("取消点赞博客-发生异常：{}", dto, e.fillInStackTrace());
            response = new BaseResponse(StatusCode.FAIL.getCode(), e.getMessage());
        }
        response.setData(resMap);
        return response;
    }

}
