import React, {Component} from 'react';
import BucketList from "./bucket/BucketList";
import MonthlyDownTrend from "./trend/MonthlyDownTrend";
import MonthlyUpTrend from "./trend/MonthlyUpTrend";
import WeeklyUpTrend from "./trend/WeeklyUpTrend";
import WeeklyDownTrend from "./trend/WeeklyDownTrend";
import DailyDownTrend from "./trend/DailyDownTrend";
import DailyUpTrend from "./trend/DailyUpTrend";
import Quote from "./model/Quote";

const ReactDOM = require('react-dom');
const client = require('./client');
const stompClient = require('./websocket-listener');
const symbol = 'GSPC';

class AllTrends extends Component {
    constructor() {
        super();
        this.state =  {
            quote: null,
            // lastquote: null,
            dailyUp : null,
            weeklyUp : null,
            monthlyUp : null,
            dailyDown : null,
            weeklyDown : null,
            monthlyDown : null,
            buckets : null
        };
        this.refreshQuote = this.refreshQuote.bind(this);
        this.refreshTrends = this.refreshTrends.bind(this);
        this.refreshBuckets = this.refreshBuckets.bind(this);
        this.refreshDailyUp = this.refreshDailyUp.bind(this);
        this.refreshWeeklyUp = this.refreshWeeklyUp.bind(this);
        this.refreshMonthlyUp = this.refreshMonthlyUp.bind(this);
        this.refreshDailyDown = this.refreshDailyDown.bind(this);
        this.refreshWeeklyDown = this.refreshWeeklyDown.bind(this);
        this.refreshMonthlyDown = this.refreshMonthlyDown.bind(this);
    }

    componentDidMount() {
        client({method: 'GET', path: '/quote/index/%5EGSPC'}).done(response => {
            this.setState({quote: response.entity});
        });
        client({method: 'GET', path: '/quote/trend/' + symbol + '/Daily/Up/1'}).done(response => {
            this.setState({dailyUp: response.entity});
        });
        client({method: 'GET', path: '/quote/trend/' + symbol + '/Weekly/Up/1'}).done(response => {
            this.setState({weeklyUp: response.entity});
        });
        client({method: 'GET', path: '/quote/trend/' + symbol + '/Monthly/Up/1'}).done(response => {
            this.setState({monthlyUp: response.entity});
        });
        client({method: 'GET', path: '/quote/trend/' + symbol + '/Daily/Down/1'}).done(response => {
            this.setState({dailyDown: response.entity});
        });
        client({method: 'GET', path: '/quote/trend/' + symbol + '/Weekly/Down/1'}).done(response => {
            this.setState({weeklyDown: response.entity});
        });
        client({method: 'GET', path: '/buckets/' + symbol + ''}).done(response => {
            this.setState({buckets: response.entity});
        });
        client({method: 'GET', path: '/quote/trend/' + symbol + '/Monthly/Down/1'}).done(response => {
            this.setState({monthlyDown: response.entity});
        });
        stompClient.register([
            // {route: '/topic/lastquote', callback: this.refreshCurrentPage},
            {route: '/topic/lastfullquote', callback: this.refreshQuote},
            {route: '/topic/trends', callback: this.refreshTrends}, //turning off until figure out how to use this data - the matched trend buckets as determined by the backend
            {route: '/topic/quotebuckets', callback: this.refreshBuckets},
            {route: '/topic/dailyUp', callback: this.refreshDailyUp},
            {route: '/topic/weeklyUp', callback: this.refreshWeeklyUp},
            {route: '/topic/monthlyUp', callback: this.refreshMonthlyUp},
            {route: '/topic/dailyDown', callback: this.refreshDailyDown},
            {route: '/topic/weeklyDown', callback: this.refreshWeeklyDown},
            {route: '/topic/monthlyDown', callback: this.refreshMonthlyDown}
        ]);
        // if (Notification.permission !== "granted") {
            Notification.requestPermission();
        // }
    }

    // refreshCurrentPage(message) {
    //     var msg = message;
    //     var len = msg.body;
    //     this.setState({
    //         lastquote: len
    //     });
    // }

    refreshQuote(message) {
        var msg = JSON.parse(message.body);
        this.setState({
           quote: msg
        });
    }

    refreshBuckets(message) {
        var msg = JSON.parse(message.body);
        this.setState({
            buckets: msg
        });
    }

    refreshTrends(message) {
        var msg = JSON.parse(message.body);
        //TODO - do something with this response?
        console.log(msg);
    }

    refreshDailyUp(message) {
        var msg = JSON.parse(message.body);
        this.setState({
            dailyUp: msg
        });
    }
    refreshWeeklyUp(message) {
        var msg = JSON.parse(message.body);
        this.setState({
            weeklyUp: msg
        });
    }
    refreshMonthlyUp(message) {
        var msg = JSON.parse(message.body);
        this.setState({
            monthlyUp: msg
        });
    }
    refreshDailyDown(message) {
        var msg = JSON.parse(message.body);
        this.setState({
            dailyDown: msg
        });
    }
    refreshWeeklyDown(message) {
        var msg = JSON.parse(message.body);
        this.setState({
            weeklyDown: msg
        });
    }
    refreshMonthlyDown(message) {
        var msg = JSON.parse(message.body);
        this.setState({
            monthlyDown: msg
        });
    }
    render() {
        return (
            <div>
                <h2>The Current Odds Per Period</h2>
                <div className="container">
                    <div className="quote-fixed"><Buckets buckets={this.state.buckets}/></div>
                    <div className="flex-item"><Quote quote={this.state.quote}/></div>
                </div>
                <div style={{textAlign:"center"}}>Go Long/ Close Shorts</div>
                <div className="container">
                    <div className="flex-item"><DailyUpTrend quote={this.state.quote} dailyUp={this.state.dailyUp}/></div>
                    <div className="flex-item"><WeeklyUpTrend quote={this.state.quote} weeklyUp={this.state.weeklyUp}/></div>
                    <div className="flex-item"><MonthlyUpTrend quote={this.state.quote} monthlyUp={this.state.monthlyUp}/></div>
                </div>
                <br/>
                <div style={{textAlign:"center"}}>Close Longs/ Go Short</div>
                <div className="container">
                    <div className="flex-item"><DailyDownTrend quote={this.state.quote} dailyDown={this.state.dailyDown}/></div>
                    <div className="flex-item"><WeeklyDownTrend quote={this.state.quote} weeklyDown={this.state.weeklyDown}/></div>
                    <div className="flex-item"><MonthlyDownTrend quote={this.state.quote} monthlyDown={this.state.monthlyDown}/></div>
                </div>
            </div>
        )
    }
}

class Buckets extends Component {

    constructor(props) {
        super(props);
        // this.state = {buckets: []};
    }

    render() {
        return (
            <BucketList buckets={this.props.buckets}/>
        )
    }
}
// end::app[]

// tag::bucket-list[]

export default AllTrends;

