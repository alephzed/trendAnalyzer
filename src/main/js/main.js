import React, {Component} from 'react';
import {
    Route,
    NavLink,
    HashRouter
} from "react-router-dom";
import AllTrends from "./AllTrends";
import Admin from "./admin";

 class Main extends Component {
     render() {
         return (
             <HashRouter>
                 <div>
                     <h1>Quote Trend Application</h1>
                     <ul className="header">
                         <li><NavLink exact to="/">AllTrends</NavLink></li>
                         <li><NavLink to="/stuff">Admin</NavLink></li>
                     </ul>
                     <div className="content">
                         <Route exact path="/" component={AllTrends}/>
                         <Route path="/stuff" component={Admin}/>
                     </div>
                 </div>
             </HashRouter>
         );
     }
 }

 export default Main;