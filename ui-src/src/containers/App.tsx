import React from 'react';

import { BaseComponent } from '../components/BaseComponent/index'
import { BrowserRouter as Router, Route, Link } from "react-router-dom"

const App: React.FC = () => {
  return (
    <>
      <Router>
        <Route exact path="/" component={() => <BaseComponent name={"Base Component"} />} />
      </Router>
    </>
  );
}

export default App;
