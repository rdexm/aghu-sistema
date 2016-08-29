/**
 *  aghu-form.js -> Funções de customização e layout dos forms do AGHU
 *  Autor: Cristiano Quadros, Rafael Corvalao
 */

 
var tab = {
	tabSize:0,
	polTitle:'POL',
	redirectTitles:[],
	polURL:'../paciente/prontuarioonline/arvorePOL.xhtml',
	getTab: function(){
		return jQuery("div#casca_tabs.easyui-tabs");
	},
	start : function(){
		var $tabs = tab.getTab();		
		$tabs.tabs({
  	    	fit:true,
  	    	tabHeight:29,
  	    	onBeforeClose:function(title,index){
            	return tab.beforeRemove(title);
			} 	  	    	
  	    });
	},
	
	openPOL : function(reload){
		var $tabs = tab.getTab();
		if ($tabs.tabs('exists', tab.polTitle)){
			$tabs.tabs('select', tab.polTitle);
			if (reload){
				$tabs.tabs('getTab', tab.polTitle).panel('refresh', tab.polURL);
			}	
		}else{
			$tabs.tabs('add',{  
	            title:tab.polTitle,  
	            closable:true,
	            loadingMessage:'',
				bodyCls:'casca-tabs-content',
				href:tab.polURL,
				cache:false,
	  	    	onBeforeClose:function(title,index){
	            	return tab.beforeRemove(title);
				} 				
	        });			
		}			
	},
	
	addTab : function(id, title, url, iconClass, idx) {
		amazonmenu.close('cascamenuprincipal');
		this.addNewTab(id, title, url, iconClass, idx, false);
	},
	
	addNewTab : function(id, title, url, iconClass, idx, replace) {
		var $tabs = tab.getTab();
		var exist = $tabs.tabs('exists', title);
		var idMenu = null;
		if (exist){
			idMenu = $tabs.tabs('getTab', title).panel('options').idMenu;
		}	
	    if (exist  && !replace){
	        if (idMenu==id){
	        	$tabs.tabs('select', title);	        	
	        	
				var $frame = jQuery('#'+util.createIdFrame(title.toLowerCase()));
				var fd = $frame[0].contentWindow || $frame[0].document; 
				if(typeof fd.tabOnFocusExec === 'function'){
	        		fd.tabOnFocusExec();
	        	}        	
	        }else{
	        	var newIdx=2;
				var newTitle='';
	        	if (idx){
	        		newIdx=parseInt(idx)+1;
	        	}
				if (newIdx > 2) {
					newTitle = title.replace(idx, newIdx);
				}else{
					newTitle=newIdx+' '+title;
				}	
	        	this.addTab(id, newTitle, util.replaceAll(url, ';', '&'), iconClass, newIdx);
	        }
	    }else {
	    	if (!replace){
	    		this.putCacheTab(id, title, util.replaceAll(url, ';', '&'), iconClass);	    		
	    	}else{
	        	if (exist && idMenu==id){
	        		if (tab.searchRedirectTitle(title)) {
	        			this.removeTab(title);
	        			tab.addRedirectTitle(title);
	        		} else {
	        			this.removeTab(title);	
	        		}
	        	}
	    	}	
			
	    	var idFrame = util.createIdFrame(title.toLowerCase());
	    	var iframesrc = util.replaceAll(url, ';', '&'); 
	        var content = '<iframe id="'+idFrame+'" name="'+idFrame+'" scrolling="auto" frameborder="0" border="no" src="'+iframesrc+'" class="casca-frame"></iframe>';
	        var icon = iconClass;
	        if (replace){
	        	icon="silk-transmit-go";
	        }
	        $tabs.tabs('add',{  
	            title:title,  
	            content:content, 
	            iconCls:icon,	            
	            closable:true,
				idMenu:id,
				urlRoot:iframesrc,
				bodyCls:'casca-tabs-content'
	        });
	    }  		
	},
	 
	isEmpty : function() {
		return this.tabSize > 0;
	},
	
	removeTab : function(title) {
		tab.getTab().tabs('close', title);
	},
	
	addRedirectTitle : function(title) {
		var x = false;
		for (var i = 0; i < tab.redirectTitles.length; i++) {
	        if (tab.redirectTitles[i] === title) {
	            x = true;
	        }
	    }
	    
		if (x) {
			x = true;	
		} else {
			tab.redirectTitles.push(title);
		}
	},
	
	removeRedirectTitle : function(title) {
		for (var i = 0; i < tab.redirectTitles.length; i++) {
	        if (tab.redirectTitles[i] === title) {
	        	tab.redirectTitles.splice(i,1);
	        }
	    }
	},
	
	searchRedirectTitle : function(title) {
	    for (var i = 0; i < tab.redirectTitles.length; i++) {
	        if (tab.redirectTitles[i] === title) {
	            return true;
	        }
	    }
	    return false;
	},
	
    reload : function(){
    	var $tabs = tab.getTab();
    	var selTab = $tabs.tabs('getSelected');  
    	$tabs.tabs('update', {tab:selTab, options:{}});
    },
    
	beforeRemove : function (title){
		if (!title){
			return true;
		}
		
		if (tab.searchRedirectTitle(title)) {
			tab.removeRedirectTitle(title);
			return true;
		}
		
		if (title==tab.polTitle){
			closePOL();
			tab.closeAfterRemove(title);
			return false;
		}
		
		var close = true;
		var $frame = jQuery('iframe#' + util.createIdFrame(title.toLowerCase()));
		if ($frame.length>0 && $frame[0].src.indexOf('.xhtml')>-1) {
//				&& (typeof $frame[0].contentWindow.refConversationJS == "function")) {
			close = false;
//			$frame[0].contentWindow.refConversationJS();
			tab.closeAfterRemove(title);
		}
		tab.rmvCacheTab(title);
		
		return close;
	},
	
	closeAfterRemove : function (title){
		setTimeout(function(){
			var $tabs = tab.getTab();
			var opts =$tabs.tabs('options');
			var bc = opts.onBeforeClose;
			opts.onBeforeClose = function(){};  // allowed to close now
			$tabs.tabs('close',title);
			opts.onBeforeClose = bc;  // restore the event function					
		}, 150);				
	},
	
	putCacheTab : function (id, title, url, iconClass) {
		var $inputCache=jQuery("input#cacheTab");
		if ($inputCache){		
			var value=$inputCache.val();
			value=value+id+','+title+','+url+','+iconClass+';';
			$inputCache.val(value);
			this.tabSize++;
		}	
	},
	
	rmvCacheTab : function (title) {
		var $inputCache=jQuery("input#cacheTab");
		if ($inputCache){
			var cache=$inputCache.val();
			cache=cache.substr(0, cache.length-1);
			$inputCache.val('');
			var tabs = cache.split(";");
			for (var i=0;i<tabs.length;i++){
				var attr = tabs[i].split(",");
				if (attr[1]!=title){
					tab.putCacheTab(attr[0], attr[1], attr[2], attr[3]);
				}			
			}
		   this.tabSize--;
		} 
	},
	
	buidCacheTab : function () {		
		var $inputCache=jQuery("input#cacheTab");
		if ($inputCache){
			var cache=$inputCache.val();		
			if (cache.length>1){
				cache=cache.substr(0, cache.length-1);
				$inputCache.val('');
				var tabs = cache.split(";");
				for (var i=0;i<tabs.length;i++){
					var attr = tabs[i].split(",");
					this.addTab(attr[0], attr[1], attr[2], attr[3], '1');				
				}
				jQuery("div#casca_tabs.easyui-tabs").tabs('select', 'Início');
			}
		}	
	},
	
	loadPage : function(frameName, urlPage){
		var $iframe = jQuery('iframe#' + frameName);
		$iframe.attr("src", util.replaceAll(urlPage, ';', '&'));
	}
};

var util = {
	replaceAll : function(string, token, newtoken){
		while (string.indexOf(token) != -1) {
	 		string = string.replace(token, newtoken);
		}
		return string;
	},
	createIdFrame : function(str){
		var newStr = util.replaceAll(str, ' ', '_');
		newStr = util.replaceAll(newStr, '/', '_');
		newStr = util.replaceAll(newStr, '(', '');
		newStr = util.replaceAll(newStr, ')', '');
    	return 'i_frame_' + newStr;
	},
	sleep : function(milliseconds) {
		var start = new Date().getTime();
		while ((new Date().getTime() - start) < milliseconds){
			// Do nothing
		}
	},
	changeEnterToCommandButton : function (event, buttonId){
		if (event.keyCode == 13){
			jQuery('a#'+buttonId).click();
			 event.preventDefault();
		}
	}
};