package com.cynricshu.thirdparty.timenlp.converter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lunar
 *
 * @Author Xia Shuai()
 * @Create 2019/9/4 6:36 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lunar {
    private int lunarYear;
    private int lunarMonth;
    private int lunarDay;
    private boolean leap;
}
