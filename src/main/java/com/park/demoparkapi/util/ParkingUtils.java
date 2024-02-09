package com.park.demoparkapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingUtils {

    private static final double FIRST_15_MINUTES = 5.00;
    private static final double FIRST_60_MINUTES = 9.25;
    private static final double ADD_15_MINUTES = 1.75;
    private static final double DISCOUNT = 0.30;

    public static BigDecimal calculateCost(LocalDateTime entry, LocalDateTime end) {
        long minutes = entry.until(end, ChronoUnit.MINUTES);
        double total = 0.0;

        if (minutes <= 15) {
            total = FIRST_15_MINUTES;
        } else if (minutes <= 60) {
            total = FIRST_60_MINUTES;
        } else {
            long addMinutes = minutes - 60;
            double totalParts = ((double) addMinutes / 15);
            if (totalParts > (int) totalParts) {
                total += FIRST_60_MINUTES + (ADD_15_MINUTES * ((int) totalParts + 1));
            } else { // 4.0
                total += FIRST_60_MINUTES + (ADD_15_MINUTES * (int) totalParts);
            }
        }

        return new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calculateDiscount(BigDecimal cost, long numOfTimes) {
        BigDecimal discount = ((numOfTimes > 0) && (numOfTimes % 10 == 0))
                ? cost.multiply(new BigDecimal(DISCOUNT))
                : new BigDecimal(0);
        return discount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static String generateReceipt() {
        LocalDateTime date = LocalDateTime.now();
        String receipt = date.toString().substring(0, 19);

        return receipt.replace("-", "")
                .replace(":", "")
                .replace("T", "-");
    }
}
