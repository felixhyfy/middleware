package com.felix.middleware.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @description: 二倍均值法设置发红包金额
 * @author: Felix
 * @date: 2021/4/27 21:45
 */
public class RedPacketUtil {

    /**
     * 发红包算法，金额参数以分为单位
     *
     * @param totalAmount
     * @param totalPeopleNum
     * @return
     */
    public static List<Integer> divideRedPackage(Integer totalAmount, Integer totalPeopleNum) {
        List<Integer> amountList = new ArrayList<>();

        if (totalAmount > 0 && totalPeopleNum > 0) {
            Integer restAmount = totalAmount;
            Integer restPeopleNum = totalPeopleNum;

            Random random = new Random();
            for (int i = 0; i < totalPeopleNum - 1; i++) {
                //随机范围：[1, 剩余人均金额的两倍)，左闭右开

                int amount = random.nextInt(restAmount / restPeopleNum * 2 - 1) + 1;
                restAmount -= amount;
                restPeopleNum--;
                amountList.add(amount);
            }
            //最后一个红包金额
            amountList.add(restAmount);
        }

        return amountList;
    }
}
