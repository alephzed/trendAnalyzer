package com.herringbone.stock.util;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CookieCrumb implements Serializable {
    String cookie;
    String crumb;

    public boolean initialized() {
        return cookie != null && crumb != null;
    }
}
