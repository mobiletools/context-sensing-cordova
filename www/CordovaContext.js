
var exec = require("cordova/exec");

var CordovaContext = {
    start: function(s, f) {
        return cordova.exec(s, f, "CordovaContext", "start",[]);
    },
    enableSensing: function(contexttype,s, f) {
        if(typeof(contexttype)==="string")
            contexttypes=[contexttype];
        return cordova.exec(s, f, "CordovaContext", "enablesensing",contexttypes);
    },
    disableSensing:function(contexttype,s,f){
        if(typeof(contexttype)==="function"){
            f=s;
            s=contexttypes;
            contexttype=[];
        }
        if(typeof(contexttype)==="string")
            contexttype=[contexttype];
        return cordova.exec(s, f, "CordovaContext", "disablesensing",contexttype);
    },
    stop:function(s,f){
        return cordova.exec(s,f,"CordovaContext","stop",[]);
    },
    getItem:function(contexttype,s,f){
        return cordova.exec(s,f,"CordovaContext","getItem",[contexttype]);
    }
}

module.exports = CordovaContext;