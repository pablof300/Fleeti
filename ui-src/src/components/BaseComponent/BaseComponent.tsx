import React from 'react';
import styles from './BaseComponent.module.css'
import { LauraApi } from '../../api/index'

import { ReactLogo } from '../ReactLogo/index'

interface Props {
  name: string
}

interface State {
  count: number
  developerName: string
}

export class BaseComponent extends React.Component<Props, State> {
  private api: LauraApi = new LauraApi()

  constructor(props: Props) {
    super(props)
    this.state = { count: 0, developerName: "" }
    this.getDeveloperName = this.getDeveloperName.bind(this);
    this.getDeveloperName()
  }

  async getDeveloperName() {
    this.api.getDeveloperName().then(devName => {
      this.setState({ developerName: devName })
    })
  }

  render() {
    return (
      <>
        <p className={styles.Text}>
          Hello {this.props.name}! The count is {this.state.count} and the developer name is {this.state.developerName}
        </p>
        <ReactLogo />
      </>
    )
  }
}
