$(window).load(function(){

  //callback to open each tab after the PerspectivesLoadedEvent gets fired
  var callback = function(args) {
    setTimeout(function(){
      startCnsPoll();
    }, 3000);
  };

  var getPUCMethod = function( methodName, frame ) {
    if ( frame === undefined ) {
      return undefined; 
    }

    // if this frame has the method then assume this is the PUC frame
    if ( frame[methodName] !== undefined ) {
      return frame[methodName];
    }

    // if reached topmost frame and did not find method
    if ( frame.parent === frame ) {
      return undefined;
    }
    // else search ancestors
    else {
      return getPUCMethod( methodName, frame.parent );
    }
  };

  //add the handler as soon as it becomes available
  var functionTimed = function(){
    if(typeof mantle_openTab != "undefined"){
      window.clearInterval(timeoutMangeOpenTab);
      callback();
    } else {
      if(typeof mantle_addHandler != "undefined"){ 
        window.top.mantle_addHandler("PerspectivesLoadedEvent", callback);
        window.clearInterval(timeoutMangeOpenTab);
      } else {
        //set runs interval again
      }
    }
  };

  var timeoutMangeOpenTab = setInterval(functionTimed, 100);

  var startCnsPoll = function(){
    new CnsPoll().start("/pentaho/plugin/cns/notificationApi/queue/subscribe","/pentaho/plugin/cns/notificationApi/queue/update");
  };
});
