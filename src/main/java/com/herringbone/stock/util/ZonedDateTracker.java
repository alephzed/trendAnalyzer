package com.herringbone.stock.util;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class ZonedDateTracker {

    private static final Set<String> specialClosings = new HashSet<>();

    static {
        specialClosings.add("02-13-1950");
        specialClosings.add("12-24-1956");
        specialClosings.add("12-26-1958");
        specialClosings.add("05-29-1961");
        specialClosings.add("11-25-1963");
        specialClosings.add("12-24-1965");
        specialClosings.add("04-09-1968");
        specialClosings.add("06-12-1968");
        specialClosings.add("06-19-1968");
        specialClosings.add("06-26-1968");
        specialClosings.add("07-05-1968");
        specialClosings.add("07-10-1968");
        specialClosings.add("07-17-1968");
        specialClosings.add("07-24-1968");
        specialClosings.add("07-31-1968");
        specialClosings.add("08-07-1968");
        specialClosings.add("08-14-1968");
        specialClosings.add("08-21-1968");
        specialClosings.add("08-28-1968");
        specialClosings.add("09-11-1968");
        specialClosings.add("09-18-1968");
        specialClosings.add("09-25-1968");
        specialClosings.add("10-02-1968");
        specialClosings.add("10-09-1968");
        specialClosings.add("10-16-1968");
        specialClosings.add("10-23-1968");
        specialClosings.add("10-30-1968");
        specialClosings.add("11-11-1968");
        specialClosings.add("11-20-1968");
        specialClosings.add("12-04-1968");
        specialClosings.add("12-11-1968");
        specialClosings.add("12-18-1968");
        specialClosings.add("02-10-1969");
        specialClosings.add("03-31-1969");
        specialClosings.add("07-21-1969");
        specialClosings.add("12-28-1972");
        specialClosings.add("01-25-1973");
        specialClosings.add("07-14-1977");
        specialClosings.add("09-27-1985");
        specialClosings.add("04-27-1994");
        specialClosings.add("09-11-2001");
        specialClosings.add("09-12-2001");
        specialClosings.add("09-13-2001");
        specialClosings.add("09-14-2001");
        specialClosings.add("06-11-2004");
        specialClosings.add("01-02-2007");
        specialClosings.add("10-29-2012");
        specialClosings.add("10-30-2012");
        specialClosings.add("12-05-2018");
    }

    private final ZoneId zoneId = ZoneId.of("America/New_York");

    public ZonedDateTime getEndOfClosestTradingDayCurrent() {
        ZonedDateTime now = ZonedDateTime.now().
                withZoneSameInstant(ZoneId.of("America/New_York"));
        return getEndOfClosestTradingDay(now);
    }

    public ZonedDateTime getEndOfClosestTradingDay(ZonedDateTime startFromDate) {
        assert (startFromDate.getZone().equals(zoneId));
        ZonedDateTime dtEastOpen =
                ZonedDateTime.of(startFromDate.getYear(), startFromDate.getMonthValue(), startFromDate.getDayOfMonth(),
                        9, 30, 0, 0, zoneId);
        ZonedDateTime dateToEvaluate;
        if (isMarketOpen(startFromDate) || startFromDate.isBefore(dtEastOpen)) {
            dateToEvaluate = startFromDate.minusDays(1);
        } else {
            dateToEvaluate = startFromDate;
        }
        while (!wasMarketOpenThisDay(dateToEvaluate)) {
            dateToEvaluate = dateToEvaluate.minusDays(1);
        }
        return dateToEvaluate;
    }

    public ZonedDateTime getEndOfClosestTradingWeekCurrent() {
        return getEndOfClosestTradingWeek(ZonedDateTime.now().
                withZoneSameInstant(ZoneId.of("America/New_York")));
    }

    public ZonedDateTime getEndOfClosestTradingWeek(ZonedDateTime startFromDate) {
        assert (startFromDate.getZone().equals(zoneId));
        long counter = 0;
        ZonedDateTime dateToEvaluate = null;
        while (counter <= 7) {
            dateToEvaluate = startFromDate.minusDays(counter);
            if (isLastTradingDayOfWeek(dateToEvaluate)
                    && (dateToEvaluate.isBefore(startFromDate)
                    || (!isMarketOpen(dateToEvaluate) && isAfterMarketCloseForTradingDay(dateToEvaluate)))) {
                break;
            }
            counter++;
        }
        return dateToEvaluate;
    }

    public ZonedDateTime getEndOfClosestTradingMonthCurrent() {
        return getEndOfLastTradingMonth(ZonedDateTime.now().
                withZoneSameInstant(ZoneId.of("America/New_York")));
    }

    public ZonedDateTime getEndOfLastTradingMonth(ZonedDateTime startFromDate) {
        assert (startFromDate.getZone().equals(zoneId));
        int counter = 0;
        ZonedDateTime dateToEvaluate = ZonedDateTime.now();
        while (counter <= 31) {
            dateToEvaluate = startFromDate.minusDays(counter);
            if (isLastTradingDayOfMonth(dateToEvaluate)
                    && (dateToEvaluate.isBefore(startFromDate)
                    || (!isMarketOpen(dateToEvaluate) && isAfterMarketCloseForTradingDay(dateToEvaluate)))) {
                break;
            }
            counter++;
        }
        return dateToEvaluate;
    }

    protected Boolean isTradingHoliday(ZonedDateTime currentDate) {
        assert (currentDate.getZone().equals(zoneId));
        DayOfWeek dayInWeek = currentDate.getDayOfWeek();
        if (dayInWeek == DayOfWeek.SATURDAY || dayInWeek == DayOfWeek.SUNDAY) {
            return false;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String theDate = currentDate.format(formatter);
        if (specialClosings.contains(theDate)) {
            return true;
        }
        return checkHolidays(currentDate);
    }

    private Boolean checkHolidays(ZonedDateTime currentDate) {
        boolean isHoliday = false;
        Month currentMonth = currentDate.getMonth();
        int dayOfYear = currentDate.getDayOfYear();
        int dayInMonth = currentDate.getDayOfMonth();
        int currentYear = currentDate.getYear();
        DayOfWeek dayInWeek = currentDate.getDayOfWeek();
        if (checkJanuaryHolidays(currentMonth, dayOfYear, dayInWeek, dayInMonth, currentYear)) {
            return true;
        } else if (currentMonth.equals(Month.FEBRUARY) && checkPresidentHolidays(dayInWeek, dayInMonth, currentYear)) {
            isHoliday = true;
        } else if ((currentMonth.equals(Month.MARCH) || currentMonth.equals(Month.APRIL)) && checkGoodFridayHoliday(currentMonth, dayInMonth, currentYear)) {
            isHoliday = true;
        } else if (currentMonth.equals(Month.MAY) && checkMemorialDayHoliday(dayInWeek, dayInMonth, currentYear)) {
            isHoliday = true;
        } else if (currentMonth.equals(Month.JULY) && checkIndependenceDay(dayInWeek, dayInMonth)) {
            isHoliday = true;
        } else if (currentMonth.equals(Month.SEPTEMBER) && checkLaborDay(dayInWeek, dayInMonth)) {
            isHoliday = true;
        } else if (currentMonth.equals(Month.OCTOBER) && checkColumbusDay(dayInWeek, dayInMonth, currentYear)) {
            isHoliday = true;
        } else if (currentMonth.equals(Month.NOVEMBER) && checkNovemberHolidays(dayInWeek, dayInMonth, currentYear)) {
            isHoliday = true;
        } else if (currentMonth.equals(Month.DECEMBER) && checkChristmasHoliday(dayInWeek, dayInMonth)) {
            isHoliday = true;
        }
        return isHoliday;
    }

    private boolean checkJanuaryHolidays(Month currentMonth, int dayOfYear, DayOfWeek dayInWeek, int dayInMonth, int currentYear) {
        return currentMonth.equals(Month.JANUARY) && checkNewYearsHoliday(dayOfYear, dayInWeek, currentYear) || checkMLKHoliday(dayInWeek, dayInMonth,
                currentYear);
    }

    private boolean checkNovemberHolidays(DayOfWeek dayInWeek, int dayInMonth, int currentYear) {
        return checkElectionDayHoliday(dayInWeek, dayInMonth, currentYear) || checkVeteransDayHoliday(dayInWeek,
                dayInMonth, currentYear) || thanksgiving(dayInWeek, dayInMonth);
    }

    private boolean checkColumbusDay(DayOfWeek dayInWeek, int dayInMonth, int currentYear) {
        return (dayInMonth == 12 || (dayInMonth == 13 && dayInWeek == DayOfWeek.MONDAY)) && currentYear < 1954;
    }

    private boolean thanksgiving(DayOfWeek dayInWeek, int dayInMonth) {
        return dayInWeek == DayOfWeek.THURSDAY && (dayInMonth > 21 && dayInMonth < 29);
    }

    private boolean checkChristmasHoliday(DayOfWeek dayInWeek, int dayInMonth) {
        return dayInMonth == 25 || (dayInWeek == DayOfWeek.FRIDAY && dayInMonth == 24) || (dayInWeek == DayOfWeek.MONDAY && dayInMonth == 26);
    }

    private boolean checkLaborDay(DayOfWeek dayInWeek, int dayInMonth) {
        return dayInWeek == DayOfWeek.MONDAY && dayInMonth < 8;
    }

    private boolean checkIndependenceDay(DayOfWeek dayInWeek, int dayInMonth) {
        return dayInMonth == 4 || ((dayInMonth == 3 && dayInWeek == DayOfWeek.FRIDAY) || (dayInMonth == 5 && dayInWeek == DayOfWeek.MONDAY));
    }

    private boolean checkVeteransDayHoliday(DayOfWeek dayInWeek, int dayInMonth, int currentYear) {
        return (dayInMonth == 11 || (dayInMonth == 12 && dayInWeek == DayOfWeek.MONDAY)) && currentYear < 1954;
    }

    private boolean checkElectionDayHoliday(DayOfWeek dayInWeek, int dayInMonth, int currentYear) {
        boolean isElectionDayHoliday = false;
        if (dayInWeek == DayOfWeek.TUESDAY && (dayInMonth > 1 && dayInMonth < 9) && currentYear < 1969) {
            isElectionDayHoliday = true;
        }
        if (dayInWeek == DayOfWeek.TUESDAY && (dayInMonth > 1 && dayInMonth < 9) && (currentYear == 1972 || currentYear == 1976 || currentYear == 1980)) {
            isElectionDayHoliday = true;
        }
        return isElectionDayHoliday;
    }

    private boolean checkMemorialDayHoliday(DayOfWeek dayInWeek, int dayInMonth, int currentYear) {
        boolean isMemorialDayHoliday = false;
        // Memorial Day on Monday
        if (dayInWeek == DayOfWeek.MONDAY && dayInMonth > 24 && currentYear > 1970) {
            isMemorialDayHoliday = true;
        }
        // pre 1970 rule
        if (((dayInMonth == 30 || dayInMonth == 31 )&& dayInWeek == DayOfWeek.MONDAY)
                || (dayInMonth == 30 && dayInWeek == DayOfWeek.FRIDAY)
                || (dayInMonth == 29 && dayInWeek == DayOfWeek.FRIDAY) && currentYear <= 1970) {
            isMemorialDayHoliday = true;
        }
        return isMemorialDayHoliday;
    }

    private boolean checkGoodFridayHoliday(Month currentMonth, int dayInMonth, int currentYear) {
        ZonedDateTime goodFriday = findGoodFriday(currentYear);
        return dayInMonth == goodFriday.getDayOfMonth() && currentMonth == goodFriday.getMonth();
    }

    private boolean checkPresidentHolidays(DayOfWeek dayInWeek, int dayInMonth, int currentYear) {
        boolean isPresidentialHoliday = false;
        // Washington's Birthday was recognized until 1971
        if ((dayInMonth == 22 || (dayInMonth == 23 && dayInWeek == DayOfWeek.MONDAY) || (dayInMonth == 21 && dayInWeek == DayOfWeek.FRIDAY)) && currentYear < 1971) {
            isPresidentialHoliday = true;
        }

        // Lincoln's Birthday
        if (dayInMonth == 12 && dayInWeek != DayOfWeek.FRIDAY && currentYear < 1969 ) {
            isPresidentialHoliday = true;
        }
        // Presidents day wasn't recognized until 1971
        if (dayInWeek == DayOfWeek.MONDAY && (dayInMonth > 14 && dayInMonth < 22) && currentYear > 1970) {
            isPresidentialHoliday = true;
        }
        return isPresidentialHoliday;
    }

    private boolean checkMLKHoliday(DayOfWeek dayInWeek, int dayInMonth, int currentYear) {
        return dayInWeek == DayOfWeek.MONDAY && (dayInMonth > 14 && dayInMonth < 22) && currentYear > 1985;
    }

    private boolean checkNewYearsHoliday(int dayOfYear, DayOfWeek dayInWeek, int currentYear) {
        return dayOfYear == 1 || (dayOfYear == 2 && dayInWeek == DayOfWeek.MONDAY);
    }

    protected Boolean isAfterMarketCloseForTradingDay(ZonedDateTime currentDateTime) {
        assert (currentDateTime.getZone().equals(zoneId));
        int currentYear = currentDateTime.getYear();
        int currentMonth = currentDateTime.getMonthValue();
        int currentDay = currentDateTime.getDayOfMonth();
        ZonedDateTime dtEastClose = ZonedDateTime.of(currentYear, currentMonth, currentDay, 16, 0, 0, 0, zoneId);
        return !(currentDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || currentDateTime.getDayOfWeek() == DayOfWeek.SUNDAY || isTradingHoliday(currentDateTime)) && currentDateTime.isAfter(dtEastClose);

    }

    public Boolean isLastTradingDayOfWeek(ZonedDateTime currentDate) {
        assert (currentDate.getZone().equals(zoneId));
        DayOfWeek dayInWeek = currentDate.getDayOfWeek();
        if (dayInWeek == DayOfWeek.SATURDAY || dayInWeek == DayOfWeek.SUNDAY) {
            return false;
        } else if (dayInWeek == DayOfWeek.FRIDAY && !isTradingHoliday(currentDate)) {
            return true;
        } else if (dayInWeek == DayOfWeek.THURSDAY) {
            ZonedDateTime tomorrow = currentDate.plusDays(1);
            return isTradingHoliday(tomorrow);
        } else {
            return false;
        }
    }

    private int getLastDayOfMonth(Month month, int year) {
        if (month == Month.APRIL || month == Month.JUNE || month == Month.SEPTEMBER || month == Month.NOVEMBER) {
            return 30;
        } else if (month == Month.FEBRUARY && isLeapYear(year)) {
            return 29;
        } else if (month == Month.FEBRUARY) {
            return 28;
        } else {
            return 31;
        }
    }

    private Boolean isLeapYear(int year) {
        return (year % 4) == 0;
    }

    private Boolean isLastTradingDayOfMonth(ZonedDateTime currentDate) {
        assert (currentDate.getZone().equals(zoneId));
        Month currentMonth = currentDate.getMonth();
        DayOfWeek dayInWeek = currentDate.getDayOfWeek();
        int dayInMonth = currentDate.getDayOfMonth();
        int currentYear = currentDate.getYear();
        int lastDayInMonth = getLastDayOfMonth(currentMonth, currentYear);
        if (dayInWeek == DayOfWeek.SATURDAY || dayInWeek == DayOfWeek.SUNDAY) {
            return false;
        } else if ((dayInMonth == lastDayInMonth) && !isTradingHoliday(currentDate)) {
            return true;
        } else if ((dayInWeek == DayOfWeek.FRIDAY) && !isTradingHoliday(currentDate) && ((dayInMonth + 2) >= lastDayInMonth)) {
            return true;
        } else if (dayInWeek == DayOfWeek.FRIDAY) {
            ZonedDateTime nextMonday = currentDate.plusDays(3);
            return isTradingHoliday(nextMonday) && (nextMonday.getDayOfMonth() == getLastDayOfMonth(currentMonth,
                    currentYear));
        } else if ((dayInWeek == DayOfWeek.THURSDAY)) {
            ZonedDateTime tomorrow = currentDate.plusDays(1);
            return isTradingHoliday(tomorrow) && ((dayInMonth + 3) >= lastDayInMonth);
        } else {
            return false;
        }
    }

    public final ZonedDateTime findGoodFriday(int year) {
        if (year <= 1582) {
            throw new IllegalArgumentException("Algorithm invalid before April 1583");
        }

        int golden = (year % 19) + 1; /* E1: metonic cycle */
        int century = (year / 100) + 1; /* E2: e.g. 1984 was in 20th C */
        int x = (3 * century / 4) - 12; /* E3: leap year correction */
        int z = ((8 * century + 5) / 25) - 5; /* E3: sync with moon's orbit */
        int d = (5 * year / 4) - x - 10;
        int epact = (11 * golden + 20 + z - x) % 30; /* E5: epact */
        if ((epact == 25 && golden > 11) || epact == 24) {
            epact++;
        }
        int n = 44 - epact;
        n += 30 * (n < 21 ? 1 : 0); /* E6: */
        n += 7 - ((d + n) % 7);
        if (n > 33) /* E7: */ {
            return new GregorianCalendar(year, Calendar.APRIL, n - 33).toZonedDateTime().withZoneSameInstant(zoneId); /* April */
        } else if (n > 31) {
            return new GregorianCalendar(year, Calendar.MARCH, n - 2).toZonedDateTime().withZoneSameInstant(zoneId); /*
             * Easter is
             * beginning of
             * April and Good
             * Friday is end of
             * March
             */
        } else {
            return new GregorianCalendar(year, Calendar.MARCH, n - 2).toZonedDateTime().withZoneSameInstant(zoneId);
        }
    }

    /**
     * Evaluate if the market is open based the currentDateTime parameter
     *
     * @param currentDateTime
     * @return
     */
    public boolean isMarketOpen(ZonedDateTime currentDateTime) {
        assert (currentDateTime.getZone().equals(zoneId));
        int currentYear = currentDateTime.getYear();
        int currentMonth = currentDateTime.getMonthValue();
        int currentDay = currentDateTime.getDayOfMonth();
        ZonedDateTime dtEastClose = ZonedDateTime.of(currentYear, currentMonth, currentDay, 16, 0, 0, 0, zoneId);
        ZonedDateTime dtEastOpen = ZonedDateTime.of(currentYear, currentMonth, currentDay, 9, 30, 0, 0, zoneId);
        return !(currentDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || currentDateTime.getDayOfWeek() == DayOfWeek.SUNDAY || isTradingHoliday(currentDateTime)) && currentDateTime.isAfter(dtEastOpen) && currentDateTime.isBefore(dtEastClose);
    }

    public boolean isDateToday(ZonedDateTime dateTime) {
        assert (dateTime.getZone().equals(zoneId));
        ZonedDateTime today = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/New_York"));
        return dateTime.getDayOfMonth() == today.getDayOfMonth() && dateTime.getMonthValue() == today.getMonthValue() && dateTime.getYear() == today.getYear();
    }

    public boolean isMarketOpenNow() {
        return isMarketOpen(ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/New_York")));
    }

    /**
     * Evaluate if the market was ever open on this date
     *
     * @param currentDateTime
     * @return
     */
    public boolean wasMarketOpenThisDay(ZonedDateTime currentDateTime) {
        assert (currentDateTime.getZone().equals(zoneId));
        return !(currentDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || currentDateTime.getDayOfWeek() == DayOfWeek.SUNDAY || isTradingHoliday(currentDateTime));
    }

    public boolean isDifferentDate(@NotNull final ZonedDateTime date1, @NotNull final ZonedDateTime date2) {

        Comparator<ZonedDateTime> byMMDDYYY = Comparator.comparing(ZonedDateTime::getYear)
                .thenComparing(ZonedDateTime::getMonthValue)
                .thenComparing(ZonedDateTime::getDayOfMonth);
        return byMMDDYYY.compare(date1, date2) != 0;
    }

    public boolean isAfter(@NotNull final ZonedDateTime date1, @NotNull final ZonedDateTime date2) {

        Comparator<ZonedDateTime> byMMDDYYY = Comparator.comparing(ZonedDateTime::getYear)
                .thenComparing(ZonedDateTime::getMonthValue)
                .thenComparing(ZonedDateTime::getDayOfMonth);
        return byMMDDYYY.compare(date1, date2) > 0;
    }

    public boolean inCurrentMonth(ZonedDateTime date) {
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/New_York"));
        return now.getMonthValue() == date.getMonthValue();
    }

    public boolean inCurrentWeek(ZonedDateTime date) {
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/New_York"));
        return DAYS.between(date, now) <= 6;
    }

    public boolean isNextTradingDay(ZonedDateTime nextDay, ZonedDateTime previousDay) {
        if (nextDay.isBefore(previousDay)) {
            return false;
        } else {
            boolean foundNextTradingDay = false;
            ZonedDateTime tempDay = previousDay;
            while (!foundNextTradingDay) {
                tempDay = tempDay.plusDays(1);
                foundNextTradingDay = wasMarketOpenThisDay(tempDay);
            }
            return tempDay.getYear() == nextDay.getYear() && tempDay.getMonthValue() == nextDay.getMonthValue() && tempDay.getDayOfMonth() == nextDay.getDayOfMonth();
        }
    }
}
