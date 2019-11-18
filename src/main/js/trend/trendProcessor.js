import React from "react";

const icon = 'http://cdn.sstatic.net/stackexchange/img/logos/so/so-icon.png';

function getTbody(ranges, _lastquote, trend, trendLogic, alertCondition) {
    return ranges.map((range, index) => {
        const rangeBottom = range.rangeBottom.toFixed(2);
        const rangeTop = range.rangeTop.toFixed(2);
        const rangePercentile = (range.incrementalPercent * 100).toFixed(4);
        let tdClass = "inRange";
        if (rangePercentile < 50) {
            tdClass = "outRange";
        }

        if (trendLogic(_lastquote, rangeBottom, rangeTop)) {
            tdClass = "matchedRange";
            const currentTop = sessionStorage.getItem(trend.storage);
            if (alertCondition(currentTop, rangeTop, rangePercentile)) {
                //Put the new rangeTop in the store
                //Create an alert!
                sessionStorage.setItem(trend.storage, rangeTop);
                if (Notification.permission === "granted") {
                    const message = trend.message + rangePercentile;
                    const notification = new Notification(trend.notification, {
                        icon: icon,
                        body: message
                    });
                    console.log(message);
                    setTimeout(notification.close.bind(notification), 30000);
                    notification.onclick = function () {
                        window.open(url);
                    };
                }
            }
        }
        return (
            <tr key={index + '-' + rangeBottom + '-' + rangeTop} className={tdClass}>
                <td key={index + '-' + rangeBottom}>{rangeBottom}</td>
                <td key={index + '-' + rangeTop}>{rangeTop}</td>
                <td key={index + '-' + rangePercentile}>{rangePercentile}</td>
            </tr>
        )
    })
}

module.exports.getTbody = getTbody;
