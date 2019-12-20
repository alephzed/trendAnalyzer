import React, {Component} from "react";
var convertTrendType = require('../util.js').convertTrendType;

class WeeklyBucket extends Component {
    render() {
        return (
            <tr>
                <td>Weekly</td>
                <td>{convertTrendType(this.props.weeklyBucket.weeklyBucketId.trendType)}</td>
                <td>{this.props.weeklyBucket.weeklyBucketId.weeksInTrendCount}</td>
                <td>{this.props.weeklyBucket.cumulativePercentage}</td>
                <td>{this.props.weeklyBucket.lastStoredQuoteClose}</td>
                <td>{this.props.weeklyBucket.lastStoredQuoteDate}</td>
            </tr>
        )
    }
}

export default WeeklyBucket;
