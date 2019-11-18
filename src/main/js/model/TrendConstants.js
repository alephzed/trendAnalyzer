const Trend = {
    Daily: {
        downtrend: {
            message: 'Daily Downtrend indicates SELL.',
            caption: 'Daily Downtrend',
            storage: 'dailyTop',
            notification: 'Sell Notification'
        },
        uptrend: {
            message: 'Daily Uptrend indicates BUY.',
            caption: 'Daily Uptrend',
            storage: 'dailyBottom',
            notification: 'Buy Notification'
        }
    },
    Weekly: {
        downtrend: {
            message: 'Weekly Downtrend indicates SELL.',
            caption: 'Weekly Downtrend',
            storage: 'weeklyTop',
            notification: 'Sell Notification'
        },
        uptrend: {
            message: 'Weekly Uptrend indicates BUY.',
            caption: 'Weekly Uptrend',
            storage: 'weeklyBottom',
            notification: 'Buy Notification'
        }
    },
    Monthly: {
        downtrend: {
            message: 'Monthly Downtrend indicates SELL.',
            caption: 'Monthly Downtrend',
            storage: 'monthlyTop',
            notification: 'Sell Notification'
        },
        uptrend: {
            message: 'Monthly Uptrend indicates BUY.',
            caption: 'Monthly Uptrend',
            storage: 'monthlyBottom',
            notification: 'Buy Notification'
        }
    }
};

exports.trendConsts = Trend;