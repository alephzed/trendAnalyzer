import React, {Component} from "react";
import {getTbody} from "./trendProcessor";
var trendConsts = require('../model/TrendConstants').trendConsts;

function trendLogic(lastQuote, rangeBottom, rangeTop) {
    return ((lastQuote >= rangeBottom) && (lastQuote < rangeTop));
}

function alertCondition(currentLevel, rangeLevel, rangePercentile) {
    return (currentLevel == null || currentLevel > rangeLevel) && rangePercentile > 40;
}

class MonthlyDownTrend extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        if (this.props.monthlyDown) {
            const _ranges = this.props.monthlyDown.ranges;
            var _lastquote = this.props.quote.last;
            return (
                <table>
                    <caption>{trendConsts.Monthly.downtrend.caption}</caption>
                    <tbody>
                    <tr>
                        <th>Range Bottom</th>
                        <th>Range Top</th>
                        <th>Percent</th>
                    </tr>
                    {getTbody(_ranges, _lastquote, trendConsts.Monthly.downtrend, trendLogic, alertCondition)}
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

export default MonthlyDownTrend;