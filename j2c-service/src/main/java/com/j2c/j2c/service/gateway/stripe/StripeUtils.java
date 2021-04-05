package com.j2c.j2c.service.gateway.stripe;

import com.neovisionaries.i18n.CurrencyCode;
import lombok.NonNull;

final class StripeUtils {

    static String toStripeCurrency(@NonNull final CurrencyCode currency) {
        return currency.name().toLowerCase();
    }

    static CurrencyCode toEnum(@NonNull final String code) {
        return CurrencyCode.getByCodeIgnoreCase(code);
    }

}
