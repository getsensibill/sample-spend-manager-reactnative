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
  TextInput,
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
    this.state = { }
  }

  render() {
    var brandJSON = {
      "colors": {
        "primary": "#658C64",
        "primaryVariant": "#2F4D2F",
        "onPrimary": "#fece9e",
        "secondary": "#7A516B",
        "onSecondary": "#e6c34a",
        "background": "#E6F0FD",
        "onBackground": "#154360",
        "surface": "#E1D8ED",
        "onSurface": "#544939",
        "surfaceVariant": "#CDBEE1",
        "onSurfaceFocus": "#6737A6",
        "error": "#B20000",
        "onError": "#A4E2C9"
      },
      "fonts": {
        "regular": {
          "family": "Lobster, Times, serif",
          "url": "https://fonts.googleapis.com/css2?family=Lobster&display=swap"
        }
      }
    };

    return (
      <>
        <StatusBar barStyle="dark-content" />
        <SafeAreaView>
          <ScrollView contentInsetAdjustmentBehavior="automatic" style={styles.scrollView}>
            {global.HermesInternal == null ? null : (
              <View style={styles.engine}>
                <Text style={styles.footer}>Engine: Hermes</Text>
              </View>
            )}
            <View style={styles.body}>
              <View style={styles.sectionContainer}>
                <Text style={styles.sectionTitle}>Sensibill SDK</Text>
                <Text style={styles.versionText}>2022.0.0</Text>
              </View>
              <Separator />
              <View style={styles.sectionContainer}>
              <Text style={styles.title}>
                1. Provide access token in code to communicate with sensibill API.
              </Text>
              <Text style={styles.title}>
                On iOS `SensibillSDKBridge.m`, go under `provideTokenReplacement...` method.
              </Text>
              <Text style={styles.title}>
                On Android `SensibillSDKBridge.kt`, update the properties at the top of the class.
              </Text>
              <Text style={styles.title}>
                2. Tap Start SDK so that SDK can establish connection with sensibill API.
              </Text>
                <Button
                  title="Start SDK"
                  onPress={ () => {
                      SensibillSDKBridge.startSDK();
                    }
                  }
                />
                <Text style={styles.title}>
                  3. Choose any of the Entry Point.
                </Text>
                <Button
                  title="Launch Spend Manager"
                  onPress={ () => {
                      console.log('Launching Spend Manager')
                      SensibillSDKBridge.launchSpendManager(JSON.stringify(brandJSON));
                    }
                  }
                />
                <Text/>
                <Button
                  title="Launch Receipt Capture"
                  onPress={ () => {
                      console.log('Launching Receipt Capture Flow.')
                      SensibillSDKBridge.launchReceiptCapture();
                    }
                  }
                />
              <Text style={styles.title}>
                4. Stop SDK. Any attempt made to launch spend manager and receipt capture after stopping SDK would result into error.
              </Text>
                <Button
                  title="Stop SDK"
                  onPress={ () => {
                      console.log('Stop SDK');
                      SensibillSDKBridge.stopSDK();
                    }
                  }
                />
              </View>
              <Separator />
            </View>
          </ScrollView>
        </SafeAreaView>
      </>
    );
  }
};

const { SensibillSDKBridge } = NativeModules;

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
    marginTop: 20,
    paddingHorizontal: 20,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
    textAlign: 'center',
  },
  sectionDescription: {
    marginTop: 0,
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
  input: {
    height: 40,
    margin: 10,
    padding: 12,
    borderWidth: 1,
  },
  title: {
    textAlign: 'left',
    marginVertical: 8,
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
