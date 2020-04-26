
CREATE TABLE dailyquote (
    id bigint NOT NULL auto_increment primary key,
    date TIMESTAMP NOT NULL,
    open double precision NOT NULL,
    high double precision NOT NULL,
    low double precision NOT NULL,
    close double precision NOT NULL,
    volume numeric NOT NULL,
    adjclose double precision NOT NULL,
    daytype bigint,
    trendtype bigint,
    prevday numeric,
    nextday numeric,
    timeentered date,
    logchange double precision,
    volatility double precision,
    version numeric,
    nextday_id numeric,
    prevday_id numeric,
    spike double precision,
    tickerid bigint
);

CREATE TABLE monthlyquote (
    id bigint NOT NULL auto_increment primary key,
    date TIMESTAMP NOT NULL,
    open double precision,
    high double precision,
    low double precision,
    close double precision,
    volume numeric,
    adjclose double precision,
    monthtype bigint,
    trendtype bigint,
    prevmonthid numeric,
    nextmonthid numeric,
    monthdaystartid numeric,
    monthdayendid numeric,
    timeentered date,
    logchange double precision,
    volatility double precision,
    spike double precision,
    tickerid bigint
);

CREATE TABLE monthlytrend (
    id bigint NOT NULL auto_increment primary key,
    trendtype bigint,
    trendstartid numeric,
    trendendid numeric,
    previoustrendid numeric,
    nexttrendid numeric,
    monthsintrendcount bigint,
    trendpointchange double precision,
    trendpercentagechange double precision,
    trendvolume numeric,
    tickerid bigint
);

CREATE TABLE dailytrend (
    id bigint NOT NULL auto_increment primary key,
    trendtype bigint NOT NULL,
    trendstartid numeric,
    trendendid numeric,
    previoustrendid numeric,
    nexttrendid numeric,
    daysintrendcount bigint,
    trendpointchange double precision,
    trendpercentagechange double precision,
    version numeric,
    tickerid bigint
);


CREATE TABLE weeklyquote (
    id bigint NOT NULL auto_increment primary key,
    date TIMESTAMP NOT NULL,
    open double precision NOT NULL,
    high double precision NOT NULL,
    low double precision NOT NULL,
    close double precision NOT NULL,
    volume numeric NOT NULL,
    adjclose double precision NOT NULL,
    weektype bigint,
    trendtype bigint,
    prevweekid numeric,
    nextweekid numeric,
    weekdaystartid numeric,
    weekdayendid numeric,
    timeentered date,
    logchange double precision,
    volatility double precision,
    spike double precision,
    tickerid bigint
);

CREATE TABLE weeklytrend (
    id bigint NOT NULL auto_increment primary key,
    trendtype bigint,
    trendstartid numeric,
    trendendid numeric,
    previoustrendid numeric,
    nexttrendid numeric,
    weeksintrendcount bigint,
    trendpointchange double precision,
    trendpercentagechange double precision,
    trendvolume numeric,
    daysintrendcount bigint,
    tickerid bigint
);

CREATE TABLE ticker (
    id bigint NOT NULL,
    symbol text NOT NULL,
    exchange text NOT NULL,
    lastupdate date NOT NULL,
    version numeric,
    cleartype bigint,
    trendcount bigint,
    clearlevel double precision,
    volume bigint,
    bought bigint,
    buyflag boolean,
    aroonup bigint,
    aroondown bigint,
    last25dayhigh double precision,
    last25daylow double precision,
    aroonupprevious bigint,
    aroondownprevious bigint,
    dayssincearoonhigh bigint,
    dayssincearoonlow bigint,
    alias text,
    startdate TIMESTAMP
);

CREATE TABLE trendtype (
    id bigint NOT NULL,
    description text,
    trendvalue bigint NOT NULL
);

CREATE TABLE yahooquoteoptions (
    tag text,
    description text,
    field text,
    version numeric,
    id numeric NOT NULL
);

CREATE OR REPLACE VIEW DailyBuckets AS
 SELECT t1.total,
    t1.trendtype,
    t1.frequency,
    t1.percentage,
    t1.daysintrendcount,
    sum(t2.percentage) AS cumulativepercentage,
    sum(t2.frequency) AS cumulativefrequency,
    t1.tickerid
   FROM ( SELECT t.total,
            t.trendtype,
            s.daysintrendcount,
            s.frequency,
            s.frequency::double precision / t.total::double precision * 100::double precision AS percentage,
            s.tickerid
           FROM ( SELECT sum(xx.frequency) AS total,
                    xx.trendtype,
                    xx.tickerid
                   FROM ( SELECT dailytrend.trendtype,
                            dailytrend.daysintrendcount,
                            count(*) AS frequency,
                            dailytrend.tickerid
                           FROM dailytrend
                          GROUP BY dailytrend.tickerid, dailytrend.trendtype, dailytrend.daysintrendcount
                          ORDER BY dailytrend.tickerid, dailytrend.trendtype, dailytrend.daysintrendcount) xx
                  GROUP BY xx.tickerid, xx.trendtype) t
             JOIN ( SELECT dailytrend.trendtype,
                    dailytrend.daysintrendcount,
                    count(*) AS frequency,
                    dailytrend.tickerid
                   FROM dailytrend
                  GROUP BY dailytrend.tickerid, dailytrend.trendtype, dailytrend.daysintrendcount
                  ORDER BY dailytrend.tickerid, dailytrend.trendtype, dailytrend.daysintrendcount) s ON s.trendtype = t.trendtype) t1
     JOIN ( SELECT t.total,
            t.trendtype,
            s.daysintrendcount,
            s.frequency,
            s.frequency::double precision / t.total::double precision * 100::double precision AS percentage,
            s.tickerid
           FROM ( SELECT sum(yy.frequency) AS total,
                    yy.trendtype,
                    yy.tickerid
                   FROM ( SELECT dailytrend.trendtype,
                            dailytrend.daysintrendcount,
                            count(*) AS frequency,
                            dailytrend.tickerid
                           FROM dailytrend
                          GROUP BY dailytrend.tickerid, dailytrend.trendtype, dailytrend.daysintrendcount
                          ORDER BY dailytrend.tickerid, dailytrend.trendtype, dailytrend.daysintrendcount) yy
                  GROUP BY yy.tickerid, yy.trendtype) t
             JOIN ( SELECT dailytrend.trendtype,
                    dailytrend.daysintrendcount,
                    count(*) AS frequency,
                    dailytrend.tickerid
                   FROM dailytrend
                  GROUP BY dailytrend.tickerid, dailytrend.trendtype, dailytrend.daysintrendcount
                  ORDER BY dailytrend.tickerid, dailytrend.trendtype, dailytrend.daysintrendcount) s ON s.trendtype = t.trendtype) t2 ON t1.daysintrendcount >= t2.daysintrendcount AND t1.trendtype = t2.trendtype
  GROUP BY t1.tickerid, t1.total, t1.trendtype, t1.frequency, t1.percentage, t1.daysintrendcount
  ORDER BY t1.tickerid, t1.trendtype, t1.daysintrendcount;

CREATE OR REPLACE VIEW WeeklyBuckets AS
SELECT t1.total,
       t1.trendtype,
       t1.frequency,
       t1.percentage,
       t1.weeksintrendcount,
       sum(t2.percentage) AS cumulativepercentage,
       sum(t2.frequency) AS cumulativefrequency,
       t1.tickerid
FROM ( SELECT t.total,
              t.trendtype,
              s.weeksintrendcount,
              s.frequency,
              s.frequency::double precision / t.total::double precision * 100::double precision AS percentage,
              s.tickerid
       FROM ( SELECT sum(xx.frequency) AS total,
                     xx.trendtype,
                     xx.tickerid
              FROM ( SELECT weeklytrend.trendtype,
                            weeklytrend.weeksintrendcount,
                            count(*) AS frequency,
                            weeklytrend.tickerid
                     FROM weeklytrend
                     GROUP BY weeklytrend.tickerid, weeklytrend.trendtype, weeklytrend.weeksintrendcount
                     ORDER BY weeklytrend.tickerid, weeklytrend.trendtype, weeklytrend.weeksintrendcount) xx
              GROUP BY xx.tickerid, xx.trendtype) t
                JOIN ( SELECT weeklytrend.trendtype,
                              weeklytrend.weeksintrendcount,
                              count(*) AS frequency,
                              weeklytrend.tickerid
                       FROM weeklytrend
                       GROUP BY weeklytrend.tickerid, weeklytrend.trendtype, weeklytrend.weeksintrendcount
                       ORDER BY weeklytrend.tickerid, weeklytrend.trendtype, weeklytrend.weeksintrendcount) s ON s.trendtype = t.trendtype and s.tickerid = t.tickerid) t1
         JOIN ( SELECT t.total,
                       t.trendtype,
                       s.weeksintrendcount,
                       s.frequency,
                       s.frequency::double precision / t.total::double precision * 100::double precision AS percentage,
                       s.tickerid
                FROM ( SELECT sum(yy.frequency) AS total,
                              yy.trendtype,
                              yy.tickerid
                       FROM ( SELECT weeklytrend.trendtype,
                                     weeklytrend.weeksintrendcount,
                                     count(*) AS frequency,
                                     weeklytrend.tickerid
                              FROM weeklytrend
                              GROUP BY weeklytrend.tickerid, weeklytrend.trendtype, weeklytrend.weeksintrendcount
                              ORDER BY weeklytrend.tickerid, weeklytrend.trendtype, weeklytrend.weeksintrendcount) yy
                       GROUP BY yy.tickerid, yy.trendtype) t
                         JOIN ( SELECT weeklytrend.trendtype,
                                       weeklytrend.weeksintrendcount,
                                       count(*) AS frequency,
                                       weeklytrend.tickerid
                                FROM weeklytrend
                                GROUP BY weeklytrend.tickerid, weeklytrend.trendtype, weeklytrend.weeksintrendcount
                                ORDER BY weeklytrend.tickerid, weeklytrend.trendtype, weeklytrend.weeksintrendcount) s ON s.trendtype = t.trendtype and s.tickerid = t.tickerid) t2 ON t1.weeksintrendcount >= t2.weeksintrendcount AND t1.trendtype = t2.trendtype and t1.tickerid = t2.tickerid
GROUP BY t1.tickerid, t1.total, t1.trendtype, t1.frequency, t1.percentage, t1.weeksintrendcount
ORDER BY t1.tickerid, t1.trendtype, t1.weeksintrendcount;


CREATE OR REPLACE VIEW MonthlyBuckets AS
SELECT t1.total,
    t1.trendtype,
    t1.frequency,
    t1.percentage,
    t1.monthsintrendcount,
    sum(t2.percentage) AS cumulativepercentage,
    sum(t2.frequency) AS cumulativefrequency,
    t1.tickerid
   FROM ( SELECT t.total,
            t.trendtype,
            s.monthsintrendcount,
            s.frequency,
            s.frequency::double precision / t.total::double precision * 100::double precision AS percentage,
            s.tickerid
           FROM ( SELECT sum(xx.frequency) AS total,
                    xx.trendtype,
                    xx.tickerid
                   FROM ( SELECT monthlytrend.trendtype,
                            monthlytrend.monthsintrendcount,
                            count(*) AS frequency,
                            monthlytrend.tickerid
                           FROM monthlytrend
                          GROUP BY monthlytrend.tickerid, monthlytrend.trendtype, monthlytrend.monthsintrendcount
                          ORDER BY monthlytrend.tickerid, monthlytrend.trendtype, monthlytrend.monthsintrendcount) xx
                  GROUP BY xx.tickerid, xx.trendtype) t
             JOIN ( SELECT monthlytrend.trendtype,
                    monthlytrend.monthsintrendcount,
                    count(*) AS frequency,
                    monthlytrend.tickerid
                   FROM monthlytrend
                  GROUP BY monthlytrend.tickerid, monthlytrend.trendtype, monthlytrend.monthsintrendcount
                  ORDER BY monthlytrend.tickerid, monthlytrend.trendtype, monthlytrend.monthsintrendcount) s ON s.trendtype = t.trendtype and s.tickerid = t.tickerid) t1
     JOIN ( SELECT t.total,
            t.trendtype,
            s.monthsintrendcount,
            s.frequency,
            s.frequency::double precision / t.total::double precision * 100::double precision AS percentage,
            s.tickerid
           FROM ( SELECT sum(yy.frequency) AS total,
                    yy.trendtype,
                    yy.tickerid
                   FROM ( SELECT monthlytrend.trendtype,
                            monthlytrend.monthsintrendcount,
                            count(*) AS frequency,
                            monthlytrend.tickerid
                           FROM monthlytrend
                          GROUP BY monthlytrend.tickerid, monthlytrend.trendtype, monthlytrend.monthsintrendcount
                          ORDER BY monthlytrend.tickerid, monthlytrend.trendtype, monthlytrend.monthsintrendcount) yy
                  GROUP BY yy.tickerid, yy.trendtype) t
             JOIN ( SELECT monthlytrend.trendtype,
                    monthlytrend.monthsintrendcount,
                    count(*) AS frequency,
                    monthlytrend.tickerid
                   FROM monthlytrend
                  GROUP BY monthlytrend.tickerid, monthlytrend.trendtype, monthlytrend.monthsintrendcount
                  ORDER BY monthlytrend.tickerid, monthlytrend.trendtype, monthlytrend.monthsintrendcount) s ON s.trendtype = t.trendtype and s.tickerid = t.tickerid) t2 ON t1.monthsintrendcount >= t2.monthsintrendcount AND t1.trendtype = t2.trendtype and t1.tickerid = t2.tickerid
  GROUP BY t1.tickerid, t1.total, t1.trendtype, t1.frequency, t1.percentage, t1.monthsintrendcount
  ORDER BY t1.tickerid, t1.trendtype, t1.monthsintrendcount;

