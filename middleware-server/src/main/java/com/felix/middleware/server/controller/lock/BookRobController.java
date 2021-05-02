package com.felix.middleware.server.controller.lock;

import com.felix.middleware.api.enums.StatusCode;
import com.felix.middleware.api.response.BaseResponse;
import com.felix.middleware.server.dto.BookRobDto;
import com.felix.middleware.server.service.IBookRobService;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 书籍抢购controller
 * @author: Felix
 * @date: 2021/5/2 17:34
 */
@RestController
public class BookRobController {

    private static final Logger log = LoggerFactory.getLogger(BookRobController.class);

    private static final String PREFIX = "book/rob";

    @Autowired
    private IBookRobService bookRobService;

    /**
     * 抢购书籍
     * @param dto
     * @return
     */
    @RequestMapping(value = PREFIX + "/request", method = RequestMethod.GET)
    public BaseResponse robBook(BookRobDto dto) {
        if (Strings.isNullOrEmpty(dto.getBookNo()) || dto.getUserId() == null || dto.getUserId() <= 0) {
            return new BaseResponse(StatusCode.INVALID_PARAMS);
        }

        BaseResponse response = new BaseResponse(StatusCode.SUCCESS);
        try {
            //不加锁的情况
            //bookRobService.robWithNoLock(dto);

            //加Zookeeper分布式锁
            bookRobService.robWithZkLock(dto);
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.FAIL.getCode(), e.getMessage());
        }

        return response;
    }
}
