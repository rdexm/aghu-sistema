/**
 *  aghu-form.js -> Funções de customização e layout dos forms do AGHU
 *  Autor: Cristiano Quadros  
 */

 
var form = {
		cache:'',
		
		load : function(){
			form.tableAutoAdjust();
			form.fixMaxHeightDataTable();			
		},		
		
		nextFocus : function(fieldStr, next){
			var $field=jQuery(fieldStr);
			if ($field.length){
				var $fields = $field.parents('form:eq(0),body').find('button,input,textarea,select');
				var index = $fields.index($field);
				if (!next){
					next=1;
				}
				if (index > -1 && (index + 1) < $fields.length ) {
					$fields.eq(index + next).focus();
				}
			}	
		},
		
		tableAutoAdjust : function(){
			jQuery('div.ui-datatable').each(function(){
				var $table = jQuery(this);
				var w = 0;
				
				var $item = $table.find('div.ui-datatable-scrollable-body table tbody tr td.auto-adjust').first();
				if ($item.length && !$item.hasClass('ok-adjust')){
					var count = $item.children().length;
					var w = (count * 20) + 4;
					if (w<=26){
						w=w+5;
					}
					$item.css('width', w+'px');
					$item.addClass('ok-adjust')
					
					var $header = $table.find('div.ui-datatable-scrollable-header-box table thead th.auto-adjust').first();
					$header.css('width', w+'px');
					
					var $ths=$table.find('div.ui-datatable-scrollable-body table thead th.auto-adjust').first();
					$ths.css('width', w+'px');
				}	
			});
		},	
		
		lineDivNumber : function(e){
			var height = e.height();
		    var line_height = e.css('line-height');
		    line_height = parseFloat(line_height);
		    return Math.round(height / line_height);
		},
		
		loadCacheInput : function(formId){
			var inputs;
			if (formId && formId.length > 0){
				inputs = jQuery("#"+formId+":input:not([type=submit]):not([type=button]):not([type=reset]):not([name='javax.faces.ViewState']):not([name^='form']):not([name*='_active'])");
			}else{
				inputs = jQuery(":input:not([type=submit]):not([type=button]):not([type=reset]):not([name='javax.faces.ViewState']):not([name^='form']):not([name*='_active'])");
			}	
			var i=0;
			var valor='';
			while(i<inputs.length){
				if (inputs[i].type=='checkbox' || inputs[i].type=='radio'){
					valor=valor+ ";" + String(inputs[i].checked);
				}else{	
					valor=valor+ ";" + inputs[i].value;
				}	
				i++;
			}
			return valor;
		},
		updateCache : function(formId){
			this.cache=this.loadCacheInput(formId);
		},
		addCache : function(value){
			this.cache=this.cache+";";
		},		
		
		confirmCancel : function(formId, dialog){
			var valor=this.loadCacheInput(formId);
			if (this.cache==valor){
				return true;
			}
			dialog.show();
			return false;
		},
		
		prontuarioMask : function(input, event){
			 var key = event.charCode || event.keyCode;
			 // Allow: Ctrl+A
			 // Allow: home, end, left, right			 
			 if (jQuery.inArray(key, [46, 8, 9, 27, 13, 110, 190]) !== -1 || (key == 65 && event.ctrlKey === true) || (key >= 35 && key <= 39)) {
                 return;
		     }
	        // Ensure that it is a number and stop the keypress
	        if ((event.shiftKey || (key < 48 || key > 57)) && (key < 96 || key > 105)) {
	        	var val = input.value.replace('/', '');
	        	if (val.length > 1){
	        		val = val.substring(0, val.length-1) + "/" + val.substring(val.length-1, val.lenght);
	        	}
	        	event.preventDefault();
	        }			
		},
		
		resetDataTable : function(wg){
			wg.paginator.setPage(0);
			wg.clearFilters();
		},
		
		fixBugColumnToggleDataTable : function(divId){
			jQuery('div#'+divId)
			.find('.aghu-server-datatable th:hidden').each(function(){
				var index = jQuery(this).index()+1;
				var td = jQuery('.aghu-server-datatable tr td:nth-child('+index+')');
				td.css('display', 'none');
			});
		},
		
		fixMaxHeightDataTable : function(){
			jQuery('.aghu-server-datatable div.ui-datatable-scrollable-body').each(function(e){
				var $item = jQuery(this);
				if (!$item.hasClass('okmaxheight')){
					$item.addClass('okmaxheight');
					var h = $item.css('height');
					$item.removeAttr('style');
					$item.css('max-height', h);
				}	
			});			
		},
		
		pageConfigIgnoreEnter : function(){
			jQuery(document).keypress(function(event){
				 var elem = event.target.nodeName;
			     if (event.which == '13'){
				     if (event.which == '13'){
					     if (elem != 'TEXTAREA' && elem != 'BUTTON') {
				        	event.preventDefault();
				      	}
				     }   				     
			     }      	
			});			
		}
		
};