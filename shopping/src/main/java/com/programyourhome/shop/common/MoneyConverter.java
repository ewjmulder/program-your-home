package com.programyourhome.shop.common;

import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.javamoney.moneta.Money;

/**
 * Gratefully copied from:
 * https://github.com/JavaMoney/javamoney-midas/blob/master/javaee7/src/main/java/org/javamoney/midas/javaee7/jpa/MoneyConverter.java
 */
@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<MonetaryAmount, String> {

    @Override
    public String convertToDatabaseColumn(final MonetaryAmount attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return Money.from(attribute).toString();
    }

    @Override
    public MonetaryAmount convertToEntityAttribute(final String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        return Money.parse(dbData);
    }

}