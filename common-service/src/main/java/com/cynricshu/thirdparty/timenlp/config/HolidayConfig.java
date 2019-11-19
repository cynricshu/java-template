package com.cynricshu.thirdparty.timenlp.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * HolidayConfig
 *
 * @Author Xia Shuai()
 * @Create 2019/9/4 7:25 PM
 */
@Data
public class HolidayConfig {
    private Map<String, String> lunar;
    private Map<String, String> solar;

    public HolidayConfig() {
        lunar = new HashMap<>();
        solar = new HashMap<>();

        setLunar();
        setSolar();
    }

    private void setLunar() {
        lunar.put("中和节", "02-02");
        lunar.put("中秋节", "08-15");
        lunar.put("中元节", "07-15");
        lunar.put("端午节", "05-05");
        lunar.put("春节", "01-01");
        lunar.put("元宵节", "01-15");
        lunar.put("重阳节", "09-09");
        lunar.put("7夕节", "07-07");
        lunar.put("初1节", "01-01");
        lunar.put("初2节", "01-02");
        lunar.put("初3节", "01-03");
        lunar.put("初4节", "01-04");
        lunar.put("初5节", "01-05");
        lunar.put("初6节", "01-06");
        lunar.put("初7节", "01-07");
        lunar.put("初8节", "01-08");
        lunar.put("初9节", "01-09");
        lunar.put("初10节", "01-10");
        lunar.put("初11节", "01-11");
        lunar.put("初12节", "01-12");
        lunar.put("初13节", "01-13");
        lunar.put("初14节", "01-14");
        lunar.put("初15节", "01-15");
    }

    private void setSolar() {
        solar.put("植树节", "03-12");
        solar.put("圣诞节", "12-25");
        solar.put("青年节", "05-04");
        solar.put("教师节", "09-10");
        solar.put("儿童节", "06-01");
        solar.put("元旦节", "01-01");
        solar.put("国庆节", "10-01");
        solar.put("劳动节", "05-01");
        solar.put("妇女节", "03-08");
        solar.put("建军节", "08-01");
        solar.put("航海日节", "07-11");
        solar.put("建党节", "07-01");
        solar.put("记者节", "11-08");
        solar.put("情人节", "02-14");
        solar.put("母亲节", "05-11");
    }
}
