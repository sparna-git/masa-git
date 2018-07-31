
var timelinePlugin = function(yasr) {
  var plugin = {};
  //var options = $.extend(true, {}, timelinePlugin.defaults);
  var cm = null;
  var draw = function() {
     console.log('run draw');
     var container = $("<div id='timeResult'></div>");
     container.empty().appendTo(yasr.resultsContainer);
     var element = document.getElementById('timeResult');
     var itemsList=[];
  

    var bindings = yasr.results.getBindings();
    if (!bindings || bindings.length === 0) {
      return [];
    }
 var count=0;
     for (var i = 0; i < bindings.length; i++) {
      
          var binding = bindings[i];
          var label=null;
          var stringFirstDate=null;
          var stringSecondDate=null;
          var countTypeDate=0;
          var start=null;
          var end=null;
          for (var bindingVar in binding) {
                  
                  if(bindingVar=='nom'){
                    label=binding[bindingVar].value;
                  }

                if(binding[bindingVar].datatype=='http://www.w3.org/2001/XMLSchema#date'){
                
                      if(bindingVar=='naissance'){
                        stringFirstDate=binding[bindingVar].value.substring(0,10);
                        start=new Date(stringFirstDate);
                      }
                    if(bindingVar=='deces'){
                      stringSecondDate=binding[bindingVar].value.substring(0,10);
                      end=new Date(stringSecondDate);
                    }  
              }
                    
          }

          //check if dates is not null

          if(stringFirstDate!=null && stringSecondDate!=null){
                if(start.getTime()<end.getTime()){
                  itemsList.push({id: i+1, content: label, start: stringFirstDate, end: stringSecondDate});
                }else{
                  itemsList.push({id: i+1, content: label, start: stringSecondDate, end: stringFirstDate});
                }
          }

          //check if one of dates is null

          if(stringFirstDate!=null && stringSecondDate==null){  
            itemsList.push({id: i+1, content: label, start: stringFirstDate});
          }

          if(stringFirstDate==null && stringSecondDate!=null){  
            itemsList.push({id: i+1, content: label, start: stringSecondDate});
          }
      
    }

    console.log(itemsList);
    var items = new vis.DataSet(itemsList);
    var options = {limitSize : false};

    // Create a Timeline
    var timeline = new vis.Timeline(element, items, options);

  };

  var canHandleResults = function() {
    if (!yasr.results) return false;
    if (!yasr.results.getOriginalResponseAsString) return false;
    var response = yasr.results.getOriginalResponseAsString();
    if ((!response || response.length == 0) && yasr.results.getException()) return false; //in this case, show exception instead, as we have nothing to show anyway
    return true;
  };

  var getDownloadInfo = function() {
    if (!yasr.results) return null;
    var contentType = yasr.results.getOriginalContentType();
    var type = yasr.results.getType();
    return {
      getContent: function() {
        return yasr.results.getOriginalResponse();
      },
      filename: "queryResults" + (type ? "." + type : ""),
      contentType: contentType ? contentType : "text/plain",
      buttonTitle: "Download response"
    };
  };

  return {
    draw: draw,
    name: "Timeline",
    canHandleResults: canHandleResults,
    getPriority: 1,
    getDownloadInfo: getDownloadInfo
  };
};

