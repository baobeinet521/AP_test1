/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  TouchableOpacity,
  NativeModules,
  TextInput,
  NativeEventEmitter,
  FlatList,
  Dimensions
} from 'react-native';
const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' +
    'Cmd+D or shake for dev menu',
  android: 'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});
const widthA=Dimensions.get('window').width;
const SocketManager =NativeModules.SocketManager;
//通过NativeAppEventEmitter.addListener添加监听的方法官方已不建议使用
const bleManagerEmitter = new NativeEventEmitter(SocketManager);
export default class App extends Component<{}> {
  constructor(){
    super();
    this.state={
      msg:"",
      data:[],
      // ip:"10.201.126.1",
      ip:"192.168.1.104",
      // port:8443
      port:9999
    }
    var _this=this;
    bleManagerEmitter.addListener("tcpData",function(e){
      console.log(e);
      const aa={"time":new Date().toUTCString(),"content":e}
      const bb=_this.state.data;
      bb.push(aa);
      _this.setState({data:bb});
    });
    console.log("====初始化");
  }
  componentWillMount(){
    console.log("====componentWillMount");
  }
  renderIterm=(data)=>{
    return (
      <View style={{borderColor:"#003300",borderWidth:1,borderStyle:"solid",margin:1,flex:1}}>
        <Text style={{height:20,width:300,color:"#ff0000"}}>{data.item.time}</Text>
        <Text style={{width:300,color:"#0000ff"}}>{data.item.content}</Text>
      </View>
    )
  }
  _keyExtractor = (item, index) => index;
  render() {
    return (
      <View style={styles.container}>
        <TextInput style={{width:widthA}} onChangeText={(a)=>{this.setState({ip:a})}} placeholder="ip">{this.state.ip}</TextInput>
        <TextInput style={{width:widthA}} onChangeText={(a)=>{this.setState({port:parseInt(a)?parseInt(a):''})}} placeholder="端口">{this.state.port}</TextInput>
        <TextInput style={{width:widthA}} onChangeText={(a)=>{this.setState({msg:a})}} placeholder="指令">{this.state.msg}</TextInput>
        <View style={{height:100,width:widthA,flexDirection: 'row'}}>
          <TouchableOpacity onPress={()=>{this.onClick()}} style={{flex:1,margin:10}} >
              <Text style={{ height: 60, backgroundColor: '#0f0',textAlign: 'center',textAlignVertical:"center"}}>注册socket</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={()=>{this.onsendTcpMsg()}} style={{flex:1,margin:10}}>
              <Text style={{backgroundColor: '#0f0',textAlign: 'center',height:60,textAlignVertical:"center"}}>发送消息</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={()=>{this.setState({data:[]})}} style={{flex:1,margin:10}}>
              <Text style={{backgroundColor: '#0f0',textAlign: 'center',height:60,textAlignVertical:"center"}}>清空接收数据</Text>
          </TouchableOpacity>
        </View>
        <FlatList
          style={{flex:1}}
          data={this.state.data}
          renderItem={this.renderIterm.bind(this)}
          keyExtractor={this._keyExtractor.bind(this)}/>
      </View>
    );
  }
  onClick(){
    console.log("ip+端口",this.state.ip,this.state.port)
    SocketManager.creatSocketManager(this.state.ip,this.state.port,function(e){
      console.log(e)
    });
  }
  onsendTcpMsg(){
    SocketManager.sendMessage(this.state.msg);
  }

}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});
