import React from "react";
var convertTrendType = require('../util.js').convertTrendType;

class MonthlyBucket extends React.Component {
    render() {
        return (
            <tr>
                <td>Monthly</td>
                <td>{convertTrendType(this.props.monthlyBucket.monthlyBucketId.trendType)}</td>
                <td>{this.props.monthlyBucket.monthlyBucketId.monthsInTrendCount}</td>
                <td>{this.props.monthlyBucket.cumulativePercentage}</td>
                <td>{this.props.monthlyBucket.lastStoredQuoteClose}</td>
                <td>{this.props.monthlyBucket.lastStoredQuoteDate}</td>
            </tr>
        )
    }
}

export default MonthlyBucket;
