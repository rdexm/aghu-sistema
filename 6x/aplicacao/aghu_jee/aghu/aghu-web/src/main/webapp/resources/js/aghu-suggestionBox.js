/**
 *  suggestionComponent.js -> Funções de suporte e operações da Suggestion Box
 *  Autor: Cristiano Quadros  
 */

sg = {
	load : function(){
		this.customize();
	},	
	
	init : function(selected, sgWG, readonly){
		if (selected && sgWG){
			this.sgSelected(sgWG)
		}else if(!readonly){
			sgWG.enable();
			sgWG.activate();			
		}
	},	
		
	sgTab : function (sgWG, e){
		var keyCode = e.keyCode || e.which;
		if (keyCode === 9 && sgWG.input.val()!='') {
			sgWG.close();
			return true;
		}else{
			if (!sgWG.input.value || 0 === sgWG.input.value.length){
				sgWG.dropdown.hide();				
			}else{
				sgWG.dropdown.show();
			}
		}
		return false;
	},
	
	sgClear : function (sgWG){
		sgWG.enable();
		sgWG.activate();
		setFocus(sgWG.input);
	},	
	
	
	sgBlur : function (sgWG){
		if (sgWG && sgWG.active){
			sgWG.input.val('');
			sgWG.dropdown.show();
		}
	},	
	
		
	sgSelected : function (sgWG){
		if (sgWG){
			sgWG.disable();
			sgWG.deactivate();
			sgWG.dropdown.hide();
			var selector = '#' + sgWG.id.replace('suggestion', 'sgClear');
			jq(selector).focus();
		}
	},
	
	sgValida : function (sgWG){
		if (sgWG && !jQuery(sgWG.input).is(':disabled')){
			sgWG.input.val('');	
		}
	},	
		
	customize : function() {		
		jQuery("input:disabled.ui-autocomplete-input:not('.noAdjust')").each(function(){
			var $item=jQuery(this);
		    if ($item.val().length > 0){
		    	var calc = '<span style="display:none">' + $item.val() + '</span>';
		    	jQuery('body').append(calc);
		    	var width = jQuery('body').find('span:last').width();
		    	jQuery('body').find('span:last').remove();
		    	if ((width+10) < $item.width()){
		    		$item.css('width', (width+13)+'px');
		    	}	
		    }	
		});				
		
		
		jQuery('div.breakTo').each(function(k){
			var $item=jQuery(this);
			var text=$item.text();
			if (text.length > 1 && text.indexOf('<br>')==-1) {
				var styleClass=$item.attr('class');
				var breakTo=styleClass.substring(styleClass.indexOf('breakTo'));
				var allclass = breakTo.split(' ');
				var breakTo = allclass[1];
				if (breakTo < text.length){
					$item.removeClass('aghu-suggestion-text').addClass('aghu-suggestion-text-break');
					var newText = text;
					if (allclass.length > 1 && allclass[2]=='middle'){
						var middle=text.length/2;
						newText = text.substring(0, text.indexOf(' ', middle)) + '<br>' +  text.substring(text.indexOf(' ', middle));
					}else if (text.indexOf(' ', breakTo)!=-1){
						newText = text.substring(0, text.indexOf(' ', breakTo)) + '<br>' +  text.substring(text.indexOf(' ', breakTo));
					}	
					$item.html(newText);
				}
			}
		});    		

	},	
	
};		


//Limitar o tamanho da suggestion quando for o ultimo item da pagina. Issue > #70989
$( document ).ready(function() {
    $('.ui-autocomplete-panel').last().css({bottom: "0", height: "auto" });
});