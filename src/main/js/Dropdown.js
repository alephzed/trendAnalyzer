import React, {Component} from 'react';
// import ReactDOM from "react-dom";
// import { BrowserRouter, Route, withRouter } from "react-router-dom";

class DropDown extends Component {
    // onSelect = e => {
    //     console.log(e.target.value);
    // }

    onChange(e) {
        console.log(e);
        this.props.history.push(`/${e.target.value}`);
    }
    constructor(props) {
        super(props);
        this.onChange = this.onChange.bind(this);
        console.log(this.props)
    }
    render() {
        return (
            <select onChange={this.onChange}>
                <option value="stuff">Nasdaq</option>
                <option value="second-route">S&P 500</option>
            </select>
        );
    }
}

export default DropDown;
