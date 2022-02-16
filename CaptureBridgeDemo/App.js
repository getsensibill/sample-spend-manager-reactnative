/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { Component } from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  NativeEventEmitter,
  NativeModules,
  DeviceEventEmitter,
  Button,
  Image,
  TouchableHighlight,
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';


export default class App extends Component {

  constructor () {
    super();

    this.state = {
      base64Icon : ''
    }

    let captureEventEmitter = new NativeEventEmitter(NativeModules.SensibillCaptureBridgeEventEmitter)

    captureEventEmitter.addListener('sendImage', (data) => {
      console.log(data);
    	this.setImage('data:image/png;base64,' + data);
    })
  }

  render() {
    return (
      <>
        <StatusBar barStyle="dark-content" />
        <SafeAreaView>
          <ScrollView
            contentInsetAdjustmentBehavior="automatic"
            style={styles.scrollView}>
            {global.HermesInternal == null ? null : (
              <View style={styles.engine}>
                <Text style={styles.footer}>Engine: Hermes</Text>
              </View>
            )}
            <View style={styles.body}>

              <View style={styles.sectionContainer}>
                <Text style={styles.sectionTitle}>Sensibill Capture</Text>
                <Text style={styles.versionText}>2022.0.0</Text>
              </View>
              <Separator/>
              <View style={styles.sectionContainer}>
              <Button
                title="Launch Capture"
                onPress={ () => {
                  console.log('Launching Capture');
                  SensibillCaptureBridge.launchCapture();
                  }
                }
              />
              </View>

              <View style={styles.imageContainer}>
              <Image style={{width: 320, height: 350, resizeMode: "contain", borderWidth: 1, borderColor: 'red'}}  source={{uri: this.state.base64Icon }}/>

              </View>
            </View>
          </ScrollView>
        </SafeAreaView>
      </>
    );
  }

  setImage(img) {
    this.setState({base64Icon : img})
  }
};

const { SensibillCaptureBridge } = NativeModules;

const Separator = () => (
  <View style={styles.separator} />
);

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  imageContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
    justifyContent: 'center',
    alignItems:'center',
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
    textAlign: 'center'
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
  separator: {
    marginVertical: 8,
    borderBottomColor: '#737373',
    borderBottomWidth: StyleSheet.hairlineWidth,
  },
  versionText: {
    fontSize: 12,
    textAlign: 'center',
  }
});
