function isNumeric(input) {
	return (input - 0) == input && input.length > 0;
}

String.prototype.format = function () 
{
  var args = arguments;
  return this.replace(/\{\{|\}\}|\{(\d+)\}/g, function (m, n) {
    if (m == "{{") { return "{"; }
    if (m == "}}") { return "}"; }
    return args[n];
  });
};

Date.prototype.format = function(format)
{
  var o = {
    "M+" : this.getMonth()+1, 						//month
    "d+" : this.getDate(),    						//day
    "h+" : this.getHours(),   						//hour
    "m+" : this.getMinutes(), 						//minute
    "s+" : this.getSeconds(), 						//second
    "q+" : Math.floor((this.getMonth()+3)/3),  		//quarter
    "S" : this.getMilliseconds() 					//millisecond
  }

  if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
    (this.getFullYear()+"").substr(4 - RegExp.$1.length));
  for(var k in o)if(new RegExp("("+ k +")").test(format))
    format = format.replace(RegExp.$1,
      RegExp.$1.length==1 ? o[k] :
        ("00"+ o[k]).substr((""+ o[k]).length));
  return format;
}

function dateFromISOString(str) 
{
	var t = str.split(/[- :]/);
	return new Date(t[0], t[1]-1, t[2], t[3], t[4], t[5]);
}

function convertUTCDateToLocalDate(date) {
	var newDate = new Date(date.getTime());
	
    var offset = date.getTimezoneOffset() / 60;
    var hours = date.getHours();
   
    newDate.setHours(hours - offset);
    
    return newDate;   
}
