package com.kesei.rag.mocker.support.utils;

import com.kesei.rag.mocker.support.FakerType;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 随机生成数据封装
 *
 * @author viamamo
 */
public class FakerUtils {
    
    private final static Faker ZH_FAKER = new Faker(new Locale("zh-CN"));
    
    private final static Faker EN_FAKER = new Faker();
    
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 获取随机值
     *
     * @param fakerType
     * @return
     */
    public static String getRandomValue(FakerType fakerType) {
        if (fakerType == null) {
            return RandomStringUtils.randomAlphanumeric(2, 6);
        }
        return switch (fakerType) {
            case NAME -> ZH_FAKER.name().name();
            case CITY -> ZH_FAKER.address().city();
            case EMAIL -> EN_FAKER.internet().emailAddress();
            case URL -> EN_FAKER.internet().url();
            case IP -> ZH_FAKER.internet().ipV4Address();
            case INTEGER -> String.valueOf(ZH_FAKER.number().randomNumber());
            case DECIMAL -> String.valueOf(RandomUtils.nextFloat(0, 100000));
            case UNIVERSITY -> ZH_FAKER.university().name();
            case DATE -> EN_FAKER.date()
                    .between(Timestamp.valueOf("2022-01-01 00:00:00"), Timestamp.valueOf("2023-01-01 00:00:00"))
                    .toLocalDateTime().format(DATE_TIME_FORMATTER);
            case TIMESTAMP -> String.valueOf(EN_FAKER.date()
                    .between(Timestamp.valueOf("2022-01-01 00:00:00"), Timestamp.valueOf("2023-01-01 00:00:00"))
                    .getTime());
            case PHONE -> ZH_FAKER.phoneNumber().cellPhone();
            default -> RandomStringUtils.randomAlphanumeric(2, 6);
        };
    }
    
    public static void main(String[] args) {
        getRandomValue(null);
    }
}
