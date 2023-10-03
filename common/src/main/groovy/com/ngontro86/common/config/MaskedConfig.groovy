package com.ngontro86.common.config

import org.apache.commons.net.util.Base64

class MaskedConfig {

    String hashedValue
    String unmaskedValue

    private MaskedConfig() {}

    static MaskedConfig newInstance() {
        return new MaskedConfig()
    }

    MaskedConfig setHashedValue(String str) {
        this.hashedValue = str
        this
    }

    MaskedConfig setRawValue(String str) {
        this.unmaskedValue = str
        this
    }

    MaskedConfig build() {
        if (unmaskedValue == null && hashedValue == null) {
            throw new IllegalStateException("MaskedConfig is not correctly configured....")
        }
        if (unmaskedValue == null) {
            unmaskedValue = new String(Base64.decodeBase64(hashedValue.bytes))
        }
        if (hashedValue == null) {
            hashedValue = new String(Base64.encodeBase64(unmaskedValue.bytes))
        }
        this
    }

}
