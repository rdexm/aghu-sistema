/**
 *  casca.js -> Funções de suporte a operações do casca
 *  Autor: Cristiano Quadros  
 */


var pac = {
	LEITO:'Leito',
	PRONTUARIO:'Prontuario',
	selected:'',
		 
	init: function (maskId, pront, leito){		
		if (!maskId){
			return true;
		}
		jQuery('input#'+maskId+'_prontuario').val(pront);
		jQuery('input#'+maskId+'_leito').val(leito);

		if (leito!='' && pront==''){
			jQuery('select#' + maskId).val(pac.LEITO);
			selected=pac.LEITO;
		}else{
			jQuery('select#' + maskId).val(pac.PRONTUARIO);
			selected=pac.PRONTUARIO;
		}
	},
	
	clear: function (maskId){		
		if (!maskId){
			return true;
		}
		jQuery('select#' + maskId).children().attr('disabled', false);
		jQuery('input#'+maskId+'_leito').attr('readonly', false).val('');
		jQuery('input#'+maskId+'_prontuario').attr('readonly', false).val('');
		if (pac.selected == pac.LEITO){
			jQuery('input#'+maskId+'_leito').focus();
		}else{	
			jQuery('input#'+maskId+'_prontuario').focus();
		}
	},	
	
	render: function (maskId, focus){		
		if (!maskId){
			return true;
		}
		var leito = jQuery('input#'+maskId+'_leito');
		var pront = jQuery('input#'+maskId+'_prontuario');
		pac.selected = jQuery('select#' + maskId).val();
		if (pac.selected == pac.PRONTUARIO){
			leito.css('display', 'none');			
			pront.css('display', 'inline');
			if (focus){
				pront.val('');
				pront.focus();
			}	
		}else{
			pront.css('display', 'none');
			leito.css('display', 'inline');
			if (focus){
				leito.val('');
				leito.focus();
			}
		}
	},
	
	statusSupport: function(maskId, start) {
		var span = jQuery('span#'+maskId+'_descr'); 
		if (span.text().length>0 && start==false){
			return;
		}	
		jQuery('select#' + maskId).children().attr('disabled', start);
		if (pac.selected == pac.LEITO){
			if (start){
				jQuery('input#'+maskId+'_leito').attr('disabled', start);
			}else{
				jQuery('input#'+maskId+'_leito').attr('disabled', start).val('');
			}	
			
		}else if (pac.selected == pac.PRONTUARIO){
			if (start){
				jQuery('input#'+maskId+'_prontuario').attr('disabled', start);
			}else{
				jQuery('input#'+maskId+'_prontuario').attr('disabled', start).val('');					
			}	
		}		
	}	
	
};