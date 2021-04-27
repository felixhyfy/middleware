package com.felix.middleware.server.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @description:
 * @author: Felix
 * @date: 2021/4/27 17:07
 */
@Data
@ToString
public class PhoneUser implements Serializable {

    private String phone;
    private Double fare;

    public PhoneUser() {
    }

    public PhoneUser(String phone, Double fare) {
        this.phone = phone;
        this.fare = fare;
    }

    //手机号相同，代表充值记录重复(只适用于特殊的排名需要)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneUser phoneUser = (PhoneUser) o;

        return phone != null ? phone.equals(phoneUser.phone) : phoneUser.phone == null;
    }

    @Override
    public int hashCode() {
        return phone != null ? phone.hashCode() : 0;
    }
}
