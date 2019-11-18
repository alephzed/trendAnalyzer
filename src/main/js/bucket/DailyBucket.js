import React, {Component} from "react";
var convertTrendType = require('../util.js').convertTrendType;

class DailyBucket extends Component {
    render() {
        return (
            <tr>
                <td>Daily</td>
                <td>{convertTrendType(this.props.dailyBucket.dailyBucketId.trendType)}</td>
                <td>{this.props.dailyBucket.dailyBucketId.daysInTrendCount}</td>
                <td>{this.props.dailyBucket.cumulativePercentage}</td>
                <td>{this.props.dailyBucket.lastStoredQuoteClose}</td>
            </tr>
        )
    }
}

export default DailyBucket;