package com.chamber.java.library.model;

import com.facebook.crypto.CryptoConfig;

/**
 * Created by hafiq on 28/01/2018.
 */

public enum ChamberType {

    KEY_128(CryptoConfig.KEY_128),
    KEY_256(CryptoConfig.KEY_256);

    private CryptoConfig type;

    public CryptoConfig getConfig()
    {
        return this.type;
    }

    ChamberType(CryptoConfig x) {
        type = x;
    }
}
