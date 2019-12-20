import React, {Component} from "react";
import DailyBucket from "./DailyBucket";
import WeeklyBucket from "./WeeklyBucket";
import MonthlyBucket from "./MonthlyBucket";

class BucketList extends Component {
    constructor(props) {
        super(props);
        // this.state = {buckets: []};
    }

    render() {
        // var dailyBucket = this.props.buckets.Daily;
        if (this.props.buckets ) {
            var dailyBucket = <DailyBucket dailyBucket={this.props.buckets.Daily}/>
            var weeklyBucket = <WeeklyBucket weeklyBucket={this.props.buckets.Weekly}/>
            var monthlyBucket = <MonthlyBucket monthlyBucket={this.props.buckets.Monthly}/>
            return (
                <table>
                    <caption>Current Buckets</caption>
                    <tbody>
                    <tr>
                        <th>Trend Period</th>
                        <th>Trend Type</th>
                        <th>Periods in Trend Count</th>
                        <th>Cumulative Percentage</th>
                        <th>Last Stored Quote</th>
                        <th>Last Stored Quote Date</th>
                    </tr>
                    {dailyBucket}
                    {weeklyBucket}
                    {monthlyBucket}
                    </tbody>
                </table>
            )
        } else {
            return (
                <table>
                    <tbody>
                    <tr>
                        <th>Trend Period</th>
                        <th>Trend Type</th>
                        <th>Periods in Trend Count</th>
                        <th>Cumulative Percentage</th>
                        <th>Last Stored Quote</th>
                    </tr>
                    </tbody>
                </table>)
        }
    }
}

export default BucketList;
