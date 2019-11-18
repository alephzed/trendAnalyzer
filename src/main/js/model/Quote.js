import React, {Component} from "react";

class Quote extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        if (this.props.quote) {
            return (
                <table>
                    <caption>Quote</caption>
                    <tbody>
                    <tr>
                        <td>Date</td>
                        <td>{this.props.quote.date}</td>
                    </tr>
                    <tr>
                        <td>Time</td>
                        <td>{this.props.quote.lastTradeTime}</td>
                    </tr>
                    <tr>
                        <td>Last</td>
                        <td>{this.props.quote.last} {this.props.quote.change}</td>
                    </tr>
                    <tr>
                        <td>Open</td>
                        <td>{this.props.quote.Open}</td>
                    </tr>
                    <tr>
                        <td>High</td>
                        <td>{this.props.quote.High}</td>
                    </tr>
                    <tr>
                        <td>Low</td>
                        <td>{this.props.quote.Low}</td>
                    </tr>
                    <tr>
                        <td>Close</td>
                        <td>{this.props.quote.Close}</td>
                    </tr>
                    <tr>
                        <td>Previous Close</td>
                        <td>{this.props.quote.prevClose}</td>
                    </tr>
                    </tbody>
                </table>
            )
        } else {
            return (
                <h2></h2>
            )
        }
    }
}

export default Quote;
