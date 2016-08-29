/**
 *  aghu-core.js -> Funções de customização e layout dos forms do AGHU
 *  Autor: Cristiano Quadros  
 */

 
var core = {
	load : function(timer, witherror){
		core.ajaxLoad(timer);
		core.downloadAutomaticoRelatorios(false, !witherror);
		
		jQuery("body").click(function () {
			if (parent.menu){
				parent.menu.frameClose();
			}	
		});
		
	},
	
	ajaxLoad : function(timer){
		if (typeof sg != 'undefined'){
			sg.load();
		}
		jQuery('input.prontuario-input').priceFormat({limit:8,centsLimit: 1});
		core.updateTimer(timer);
	},
	
	updateTimer : function(timer) {
		if (typeof parent.timer!='undefined' && typeof parent.timer.startCounterSession == "function"){
			parent.timer.startCounterSession(timer, true);
		}
	},
	
	executeDialogMsg : function(wg){
		var notexist=wg.content.find('div#messagesInDialog').is(':empty');
		if (notexist){
			wg.hide();
		}else{
			wg.show();
		}
	},
	
	clearDialogMsg : function(wg){
		wg.content.find('div#messagesInDialog').empty();
	},	
	
    downloadAutomaticoRelatorios : function(ajax, ativo) {
    	if ((typeof ativo != 'undefined') && !ativo){
    		return;
    	}
    	var fid="print_download_frame";
    	if(!document.getElementById(fid)){ 
            var iframe = document.createElement("iframe");
            iframe.id=fid;
            iframe.name=fid;
            iframe.style.display = "none";
            document.body.appendChild(iframe);
    	}      	
        var $link;
        if (ajax) {
        	$link = jQuery('a.downloadLinkAjax');
        }else{
        	$link = jQuery('a.downloadLink');
        }
        if ($link.length){
	        setTimeout(
	        	function(){
	        		$link.click();
	        	}
	        , 200);
        }   
    }    
};

function jq(myid) {
	 var selector = selectorColon(myid);
	 var q = jQuery(selector);
	return q;
}


function setFocus(selector){
	setTimeout(function(){jQuery(selector).focus()}, 150);
}

function replaceAll (string, token, newtoken){
	while (string.indexOf(token) != -1) {
		string = string.replace(token, newtoken);
	}
	return string;
}

function selectorColon(selector) {
   return selector.replace(/(!|"|\$|%|\'|\(|\)|\*|\+|\,|\.|\/|\:|\;|\?|@)/g, function($1, $2) {
       return "\\" + $2;
   });
}


function fixedHeader(){
	jQuery('div.ui-datatable-scrollable.aghu-server-datatable').each(function(i, e){
		var $table = jQuery(this);
		var $ths=$table.find('div.ui-datatable-scrollable-body table thead th');
		var $tds=$table.find('div.ui-datatable-scrollable-body table tbody tr.ui-widget-content:first td');
		
		$table.find('div.ui-datatable-scrollable-header-box tr th').each(function(i, e){
			var $th = jQuery(e);
			var $td = jQuery($tds.get(i));
			var $thc = jQuery($ths.get(i));
			if ($thc.hasClass('auto-adjust')){
				var count = $td.children().length;
				var w = (count * 16) + 8;
				if (w<=26){
					w=w+4;
				}
				$thc.css('width', w+'px');
				$th.css('width', w+'px');
				
			}
			$thc.removeClass('auto-adjust');
			$th.removeClass('auto-adjust');
		});		
	});	
}	

function trimDecimalPlace(e){                
	var curVal = e.value;
    var parseVal = '';
    if(curVal.indexOf(".") > curVal.indexOf(",")){
        var tokens = curVal.split(".");
        var decimalVal = parseFloat("0."+tokens[1])+"";                 
        if(decimalVal != "0"){
           parseVal = tokens[0]+"."+decimalVal.substring(2);
        }else{
           parseVal = tokens[0];
        }   
    }else{
        var tokens = curVal.split(",");
        var decimalVal = parseFloat("0."+tokens[1])+"";                 
        if(decimalVal != "0"){
           parseVal = tokens[0]+","+decimalVal.substring(2);
        }else{
           parseVal = tokens[0];
        }
    }
    e.value=parseVal;
}