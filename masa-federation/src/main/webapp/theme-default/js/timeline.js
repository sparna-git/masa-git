
var timelinePlugin = function(yasr) {
  var plugin = {};
  //var options = $.extend(true, {}, timelinePlugin.defaults);
  var cm = null;
  var draw = function() {
     console.log('run draw');
     var container = $("<div id='timeResult' style='margin-top:1em;'></div>");
     container.empty().appendTo(yasr.resultsContainer);
     var element = document.getElementById('timeResult');
     var itemsList=[];
  

    var bindings = yasr.results.getBindings();
    if (!bindings || bindings.length === 0) {
      return [];
    }
    
     for (var i = 0; i < bindings.length; i++) {
      
          var binding = bindings[i];
          var label=null;
          var stringFirstDate=null;
          var stringSecondDate=null;
          var countTypeDate=0;
          var start=null;
          var end=null;
          var uri=null;
          for (var bindingVar in binding) {
                  
                //check if value is a xsd date
                if(binding[bindingVar].datatype=='http://www.w3.org/2001/XMLSchema#date'){
                
                     countTypeDate=countTypeDate+1;
                     if(countTypeDate==1){
                      stringFirstDate=binding[bindingVar].value.substring(0,10);
                      start=new Date(stringFirstDate);
                    }else{
                      stringSecondDate=binding[bindingVar].value.substring(0,10);
                      end=new Date(stringSecondDate);
                    }    
                   
              }else if (binding[bindingVar].type=='literal') {
                  label=binding[bindingVar].value;
              }else if(binding[bindingVar].type=='uri') {
                uri=binding[bindingVar].value;
              }
                    
          }

          //check if dates is not null

          if(stringFirstDate!=null && stringSecondDate!=null){
                if(start.getTime()<end.getTime()){
                  itemsList.push({
                    id: i+1, content: '<a href="'+uri+'" target="_blank" style="text-decoration:none; cursor:pointer;" title="'+uri+'">'+label+'</a>', 
                    start: stringFirstDate, 
                    end: stringSecondDate, 
                    title:stringFirstDate+' / '+stringSecondDate
                  });
                }else{
                  itemsList.push({
                    id: i+1, 
                    content: '<a href="'+uri+'" style="text-decoration:none; cursor:pointer;" target="_blank" title="'+uri+'">'+label+'</a>', 
                    start: stringSecondDate, 
                    end: stringFirstDate, 
                    title:stringSecondDate+' / '+stringFirstDate});
                }
          }

          //check if one of dates is null

          if(stringFirstDate!=null && stringSecondDate==null){  
            itemsList.push({
              id: i+1, 
              content: '<a href="'+uri+'" style="text-decoration:none; cursor:pointer;" target="_blank" title="'+uri+'">'+label+'</a>', 
              start: stringFirstDate,
              title:stringFirstDate
            });
          }
      
    }

    var items = new vis.DataSet(itemsList);
    var options = {limitSize : false, showTooltips: true};

    // Create a Timeline
    var timeline = new vis.Timeline(element, items, options);

  };

  var canHandleResults = function() {

   return getDateVariables().length > 0 && getDateVariables().length <= 2;
  };


   var valueIsXsdDate = function(val) {
    if(val=='http://www.w3.org/2001/XMLSchema#date'){
      return true;
    }else{
      return false;
    }
  };


  var getDateVariables = function() {
    if (!yasr.results) return [];
    var bindings = yasr.results.getBindings();
    if (!bindings || bindings.length === 0) {
      return [];
    }
    var dateVars = [];
    var checkedVars = [];
    for (var i = 0; i < bindings.length; i++) {
      var binding = bindings[i];
      for (var bindingVar in binding) {
        if (checkedVars.indexOf(bindingVar) === -1 && binding[bindingVar].value) {
          checkedVars.push(bindingVar);
          if (valueIsXsdDate(binding[bindingVar].datatype)) dateVars.push(bindingVar);
        }
      }
      if (checkedVars.length === yasr.results.getVariables().length) {
        //checked all vars. can break now
        break;
      }
    }
    return dateVars;
  };

  
  return {
    draw: draw,
    name: "Timeline",
    canHandleResults: canHandleResults,
    getPriority: 1
  };
};

