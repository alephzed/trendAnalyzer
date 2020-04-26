import React, {Component} from 'react';
import {
    Route,
    Switch,
    NavLink,
    HashRouter,
    withRouter
} from "react-router-dom";
import AllTrends from "./AllTrends";
import Admin from "./admin";
import DropDown from "./Dropdown";

const Menu = withRouter(DropDown);

 class Main extends Component {


     constructor(props){
         super();
         //following is not required if u are using => functions in ES6.
         // this.onData1Changed = this.onData1Changed.bind(this);
     }

     render() {
         return (
             <HashRouter>
                 <div>
                     <h1>Quote Trend Application</h1>
                     <Menu/>
                     <ul className="header">
                         <li><NavLink replace exact to="/chart/GSPC">AllTrends S & P</NavLink></li>
                         <li><NavLink replace exact to="/chart/IXIC">AllTrends Nasdaq</NavLink></li>
                         <li><NavLink replace exact to="/stuff">Admin</NavLink></li>
                     </ul>
                     <div className="content">
                         <Switch>
                            <Route exact path="/chart/:symbol" component={AllTrends}/>
                             <Route path="/stuff" component={Admin}/>
                         </Switch>
                     </div>
                 </div>
             </HashRouter>
         );
     }
 }

 export default Main;
