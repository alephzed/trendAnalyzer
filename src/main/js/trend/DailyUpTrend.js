import React, {Component} from "react";
import {getTbody} from "./trendProcessor";
var trendConsts = require('../model/TrendConstants').trendConsts;

function trendLogic(lastQuote, rangeBottom, rangeTop) {
    return ((lastQuote <= rangeBottom) && (lastQuote > rangeTop));
}

function alertCondition(currentLevel, rangeLevel, rangePercentile) {
    return (currentLevel == null || currentLevel < rangeLevel) && rangePercentile > 40;
}

class DailyUpTrend extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        if (this.props.dailyUp) {
            const _ranges = this.props.dailyUp.ranges;
            var _lastquote = this.props.quote.last;
            return (
                <table>
                    <caption>Daily Uptrend</caption>
                    <tbody>
                    <tr>
                        <th>Range Bottom</th>
                        <th>Range Top</th>
                        <th>Percent</th>
                    </tr>
                    {getTbody(_ranges, _lastquote, trendConsts.Daily.uptrend, trendLogic, alertCondition)}
                    </tbody>
                </table>
            )
        } else {
            return (
                <div>Not initialized</div>
            )
        }
    }
}

export default DailyUpTrend;
