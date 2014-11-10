$(window).load(function(){

  //callback to open each tab after the PerspectivesLoadedEvent gets fired
  var callback = function(args) {
    setTimeout(function(){
      startCnsPoll();
    }, 3000);
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
    new CnsPoll().start("/pentaho/plugin/cns/api/queue/subscribe","/pentaho/plugin/cns/api/queue/update");
  };
});
