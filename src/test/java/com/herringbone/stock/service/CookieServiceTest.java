package com.herringbone.stock.service;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CookieServiceTest {

    @Test
    void getCachedCookieCrumb() {
        String crazyString = "foo\\u002Fbar";
        System.out.println(crazyString);
        String replacedString = StringUtils.replace(crazyString, "\\u002F", "/");
        System.out.println(replacedString);
    }

    @Test
    void getCookieCrumb() {
    }
}
