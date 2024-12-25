package online.chanjinyee.technical_assessment;

public class AlienClockController {
    public static int alienYear = 2804;
    public static int alienMonth = 18;
    public static int alienDay = 31;
    public static int alienHour = 2;
    public static int alienMinute = 2;
    public static int alienSecond = 88;

    public static int[] daysInMonth = {
            0, // Month 0 (not used)
            44, // Month 1
            42, // Month 2
            48, // Month 3
            40, // Month 4
            48, // Month 5
            44, // Month 6
            40, // Month 7
            44, // Month 8
            42, // Month 9
            40, // Month 10
            40, // Month 11
            42, // Month 12
            44, // Month 13
            48, // Month 14
            42, // Month 15
            40, // Month 16
            44, // Month 17
            38 // Month 18
    };

    // Set earth time attribute like above
    public static int earthYear = 1970;
    public static int earthMonth = 1;
    public static int earthDay = 1;
    public static int earthHour = 0;
    public static int earthMinute = 0;
    public static int earthSecond = 0;

    public static String getAlienDate() {
        return alienYear + " Y, " + alienMonth + " M, " + alienDay + " D, " + alienHour + " H, " + alienMinute + " M, "
                +
                alienSecond + " S";
    }

    // Increase alien time by 1 second (0.5 seconds in Earth time)
    public static void incrementAlienTime() {
        alienSecond++;

        if (alienSecond >= 90) {
            alienSecond = 0;
            alienMinute++;

            if (alienMinute >= 90) {
                alienMinute = 0;
                alienHour++;

                if (alienHour >= 36) {
                    alienHour = 0;
                    alienDay++;

                    if (alienDay > daysInMonth[alienMonth]) {
                        alienDay = 1;
                        alienMonth++;

                        if (alienMonth > 18) {
                            alienMonth = 1;
                            alienYear++;
                        }
                    }
                }
            }
        }
    }

    public static boolean isValidAlienTime(int year, int month, int day, int hour, int minute, int second) {
        return year >= 2804 && month >= 1 && month <= 18 && day >= 1 && day <= daysInMonth[month] &&
                hour >= 0 && hour < 36 && minute >= 0 && minute < 90 && second >= 0 && second < 90;
    }

    public static boolean isValidEarthTime(int year, int month, int day, int hour, int minute, int second) {
        if (year < 1970 || month < 1 || month > 12 || day < 1 || day > 31 || hour < 0 || hour > 23 ||
                minute < 0 || minute > 59 || second < 0 || second > 59) {
            return false;
        }

        if (month == 2) {
            // Check for February days
            if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
                return day <= 29;
            } else {
                return day <= 28;
            }
        }

        if (month == 4 || month == 6 || month == 9 || month == 11) {
            // Check for 30-day months
            return day <= 30;
        }

        // Check for 31-day months
        return day <= 31;
    }

    public static boolean setEarthDateAndTime(int year, int month, int day, int hour, int minute, int second) {

        if (isValidEarthTime(year, month, day, hour, minute, second)) {
            earthYear = year;
            earthMonth = month;
            earthDay = day;
            earthHour = hour;
            earthMinute = minute;
            earthSecond = second;
            convertEarthTimeToAlienTime(year, month, day, hour, minute, second);
            return true;
        } else {
            return false;
        }
    }

    public static void convertEarthTimeToAlienTime(int year, int month, int day, int hour, int minute, int second) {
        int earthTimeInSeconds = calculateEarthTimeInSeconds(year, month, day, hour, minute, second);
        int alienTimeInSeconds = earthTimeInSeconds / 2;

        // Increment alien time by 1 second until it matches the calculated alien time
        while (alienTimeInSeconds > 0) {
            incrementAlienTime(); // Increment alien time by 1 second
            alienTimeInSeconds--;
        }
    }

    private static int calculateEarthTimeInSeconds(int year, int month, int day, int hour, int minute, int second) {
        int seconds = 0;

        // Calculate years
        for (int y = 1970; y < year; y++) {
            seconds += isLeapYear(y) ? 31622400 : 31536000;
        }

        // Calculate months
        for (int m = 1; m < month; m++) {
            seconds += getDaysInMonth(m, year) * 86400;
        }

        // Calculate days
        seconds += (day - 1) * 86400;

        // Calculate hours, minutes, and seconds
        seconds += hour * 3600 + minute * 60 + second;

        return seconds;
    }

    private static boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    private static int getDaysInMonth(int month, int year) {
        if (month == 2) {
            return isLeapYear(year) ? 29 : 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else {
            return 31;
        }
    }

    public static String getEarthDate() {
        return earthYear + " Y, " + earthMonth + " M, " + earthDay + " D, " + earthHour + " H, " + earthMinute + " M, "
                +
                earthSecond + " S";
    }
}
