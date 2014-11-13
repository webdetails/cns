var allow = true;
var startUrl;
var pollUrl;

function CnsPoll() {

  this.start = function start(start, poll) {

    startUrl = start;
    pollUrl = poll;
    
    if (request) {
      request.abort(); // abort any pending request
    }

    // fire off the request to MatchUpdateController
    var request = $.ajax({
      url : startUrl,
      type : "get",
    });

    // This is jQuery 1.8+
    // callback handler that will be called on success
    request.done(function(reply) {

      setInterval(function() {
        if (allow === true) {
          allow = false;
          getUpdate();
        }
      }, 5000); // 5 seconds
    });
    
    // callback handler that will be called on failure
    request.fail(function(jqXHR, textStatus, errorThrown) {
      // log the error to the console
      console.log("[Polling] at start, the following error occured: " + textStatus, errorThrown);
    });
    
    
  };
  
  function getUpdate() {
                                  
    if (request) {
      request.abort();  // abort any pending request
    }
    
    // fire off the request to MatchUpdateController
    var request = $.ajax({
      url : pollUrl,
      type : "get",
    });

    // This is jQuery 1.8+
    // callback handler that will be called on success
    request.done(function( notification ) {
      console.log("[Polling] Received a notification: " + notification );

      if( !$.notify ){
          console.log("[Polling] $.notify undefined");
          require(['/pentaho/api/repos/cns/static/custom/js/notify/notify-modified.js']);
          return;
      }

      if( notification && notification.message ) {

        notification.notificationType = notification.notificationType || "default";

        if( !isStylingSet( notification.notificationType ) ){ 
          initStyling( notification.notificationType ); 
        }
        
        var msg = notification.message;
        if( 'mail' == notification.notificationType ){
          msg = 'From ' + notification.recipient + ': ' + notification.message;
        }

        $.notify( msg , { 
                              style: notification.notificationType , 
                              autoHide: true, 
                              autoHideDelay: 8000, 
                              showAnimation: 'slideDown',
                              showDuration: 500,
                              hideAnimation: 'slideUp',
                              hideDuration: 500
                            });
      }
    });
    
    function isStylingSet( styling ){
      return $.notify.getStyle( styling ) ? true : false;
    }

    // TODO put styles in a css file, and only call the classes here

    function initStyling( styling ){
      if( 'default' == styling ){
        $.notify.addStyle('default', {
            html: "<div><img style='vertical-align:middle; padding-right: 10px;' src='/pentaho/api/repos/cns/static/custom/img/pentaho24x24.png'</img><span data-notify-text/></div>",
            classes: {
            base: {
              "display": "inline-block",
              "font-size": "14px",
              "margin": "3px",
              "white-space": "nowrap",
              "background-color": "white",
              "padding": "10px",
              "color": "#1973BC",
              "border-radius": "5px"
            }
          }
        });
      } else if( 'twitter' == styling ) {
        $.notify.addStyle('twitter', {
            html: "<div><img style='vertical-align:middle; padding-right: 10px;' src='/pentaho/api/repos/cns/static/custom/img/twitter24x24.png'</img><span data-notify-text/></div>",
            classes: {
            base: {
              "display": "inline-block",
              "font-size": "14px",
              "margin": "3px",
              "white-space": "nowrap",
              "background-color": "#E5F2F7",
              "padding": "10px",
              "color": "black",
              "border-radius": "5px"
            }
          }
        });
      } else if( 'pdi' == styling ) {
        $.notify.addStyle('pdi', {
            html: "<div><img style='vertical-align:middle; padding-right: 10px;' src='/pentaho/api/repos/cns/static/custom/img/pdi24x24.png'</img><span data-notify-text/></div>",
            classes: {
            base: {
              "display": "inline-block",
              "font-size": "14px",
              "margin": "3px",
              "white-space": "nowrap",
              "background-color": "white",
              "padding": "10px",
              "color": "black",
              "border-radius": "5px"
            }
          }
        });
      } else if( 'mail' == styling ) {
        $.notify.addStyle('mail', {
            html: "<div><img style='vertical-align:middle; padding-right: 10px;' src='/pentaho/api/repos/cns/static/custom/img/mail24x24.png'</img><span data-notify-text/></div>",
            classes: {
            base: {
              "display": "inline-block",
              "font-size": "14px",
              "margin": "3px",
              "white-space": "nowrap",
              "background-color": "white",
              "padding": "10px",
              "color": "black",
              "border-radius": "5px"
            }
          }
        });
      }
    };

    // callback handler that will be called on failure
    request.fail(function(jqXHR, textStatus, errorThrown) {
      // log the error to the console
      console.log("[Polling] The following error occured: " + textStatus, errorThrown);
    });

    // callback handler that will be called regardless if the request failed or succeeded
    request.always(function() {
      allow = true;
    });
  };  
};
