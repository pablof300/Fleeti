import React from 'react'

import styles from './ReactLogo.module.css'
import logo from '../../assets/logo.svg';

export class ReactLogo extends React.Component {
  render() {
    return (
      <header className={styles.AppHeader}>
        <img src={logo} className={styles.AppLogo} />
        <a
          className={styles.AppLink}
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    )
  }
}
