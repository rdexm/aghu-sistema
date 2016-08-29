var mex = {
	counter : 0,
	objectSelectId:'',
	oldObjectSelectId:'',
	positionX:0,
	positionY:0,
	contextMenu:null,
	propertiesVisible:false,
	init : function(conterInit){
		this.hideProperties();
		
		mex.counter=conterInit;
		
		jQuery("#wrapper").bind("contextmenu", function(event) {
			if(jQuery('.ui-multidraggable').length > 1) {
				mex.objectSelectId = '';
				jQuery('.ui-multidraggable').each(function(){
					jQuery(this).removeClass('ui-multidraggable');
					jQuery(this).removeClass('draggedSelect');
				});
				return false;
			}									
			if (!comp.is(".draggedSelect")){
				mex.selectObject(jQuery(this), false);
			}
			mex.contextMenu.show(event, {});
			return false;
		})
		
		jQuery(".dragMenu").draggable({
			helper: 'clone',
		    revert: 'invalid',
			containment: 'frame',
			stack: '.dragged',
			cursor: 'move',
			
			//Draggable

			stop: function (ev, ui) {
				var objName = "#obj" + mex.counter;
				var comp=jQuery(objName);
				if (comp){
					comp.click(function(e) {
						mex.selectObject(jQuery(this), false);
					});
					comp.dblclick(function(e) {
						if(jQuery('.ui-multidraggable').length > 1) {			
							return false;
						}									
						mex.selectObject(jQuery(this), true);
					});	
					comp.bind("contextmenu", function(event) {
						if(jQuery('.ui-multidraggable').length > 1) {
							return false;
						}									
						if (!comp.is(".draggedSelect")){
							mex.selectObject(jQuery(this), false);
						}
						mex.contextMenu.show(event, {});
						return false;
					});	
						
					jQuery(objName).multidraggable({
						containment: 'frame',
						revert: 'invalid',
						stack: '.dragged', 
						cursor: 'move',
						drag:function(ev, ui){
							mex.selectObject(jQuery(this), false);
						},
						stop:function(ev, ui) {
							mex.selectObject(jQuery(this), false);
						}	
					});
					mex.counter++;
					mex.selectObject(comp, true);
				}	
			}			
		});
		
		//Droppable
		jQuery("#frame").droppable({
			drop: function (ev, ui) {			
				var dragElement = jQuery(ui.draggable);
				if (!dragElement.hasClass('dragged')){
					var element = dragElement.clone();
					element.addClass("tempclass").removeClass("dragAux");
					
					if(dragElement.attr("type")!= null && dragElement.attr("type")=='TEXTO_LONGO'){
						element.removeClass("drag4").addClass("drag41");
					}
					
					jQuery(this).append(element);
					jQuery(".tempclass").attr("id", "obj" + mex.counter);

					var comp=jQuery("#obj" + mex.counter);
					comp.removeClass("tempclass").addClass("dragged");

					if(dragElement.attr("type")!= null && dragElement.attr("type") != 'TEXTO_FIXO'){
						element.html('');
					}
					
					var x = Math.floor(ev.pageX - jQuery(this).offset().left);
					var y = Math.floor(ev.pageY - jQuery(this).offset().top); 
					comp.css({"left":x-25,"top":y-12});	
				}else{
					updateController(mex.objectSelectId, jQuery('#'+mex.objectSelectId).attr('type'), mex.positionX, mex.positionY, null);
				}
			}
		});
		mex.desenhaComp();
		mex.prepareLoadComp();
	},

	desenhaComp : function(){
		
		
		jQuery("div.load.drag1").each(function (i) {
			jQuery(this).attr('type', 'TEXTO_FIXO').attr('group', 'LABEL').attr('title','Texto Fixo').html(i.outerHTML);
		});
		jQuery("div.drag.dragAux.drag1").attr('type', 'TEXTO_FIXO').attr('group', 'LABEL').attr('title','Texto Fixo').html("Texto Fixo");

		jQuery("div.drag2").attr('type', 'TEXTO_ALFANUMERICO').attr('group', 'INPUT').html("<div title='Texto Alfanumérico'>Texto Alfa</div>");
		jQuery("div.drag3").attr('type', 'TEXTO_NUMERICO_EXPRESSAO').attr('group', 'INPUT').html("<div title='Texto Numérico/Expressão'>Numérico/Expressão</div>");		
		jQuery("div.drag4").attr('type', 'TEXTO_LONGO').attr('group', 'INPUT').html("<div title='Texto Longo'>Texto Longo</div>");
		jQuery("div.drag41").attr('type', 'TEXTO_LONGO').attr('group', 'INPUT').html("<div title='Texto Longo'>Texto Longo</div>");
		jQuery("div.drag5").attr('type', 'TEXTO_CODIFICADO').attr('group', 'INPUT').attr('title','Texto Codificado').html("Codificado");
		jQuery("div.drag6").attr('type', 'EQUIPAMENTO').attr('group', 'CONTROL').attr('title','Equipamento').html("Equipamento");
		jQuery("div.drag7").attr('type', 'METODO').attr('group', 'CONTROL').attr('title','Método').html("Método");
		jQuery("div.drag8").attr('type', 'RECEBIMENTO').attr('group', 'CONTROL').attr('title','Recebimento').html("Recebimento");		
		jQuery("div.drag9").attr('type', 'HISTORICO').attr('group', 'CONTROL').attr('title','Histórico').html("Histórico");
		jQuery("div.drag10").attr('type', 'VALORES_REFERENCIA').attr('group', 'CONTROL').attr('title','Valores de Referência').html("Vl.Referência");

		jQuery("div.dragMenu.dragAux.drag1.dragMenu").attr('type', 'TEXTO_FIXO').attr('group', 'LABEL').attr('title','Texto Fixo').html("Texto Fixo");
		jQuery("div.drag2.dragMenu").attr('type', 'TEXTO_ALFANUMERICO').attr('group', 'INPUT').html("<div title='Texto Alfanumérico'>Texto Alfa</div>");
		jQuery("div.drag3.dragMenu").attr('type', 'TEXTO_NUMERICO_EXPRESSAO').attr('group', 'INPUT').html("<div title='Texto Numérico/Expressão'>Numérico/Expressão</div>");		
		jQuery("div.drag4.dragMenu").attr('type', 'TEXTO_LONGO').attr('group', 'INPUT').html("<div title='Texto Longo'>Texto Longo</div>");
		jQuery("div.drag41.dragMenu").attr('type', 'TEXTO_LONGO').attr('group', 'INPUT').html("<div title='Texto Longo'>Texto Longo</div>");
		jQuery("div.drag5.dragMenu").attr('type', 'TEXTO_CODIFICADO').attr('group', 'INPUT').attr('title','Texto Codificado').html("Codificado");
		jQuery("div.drag6.dragMenu").attr('type', 'EQUIPAMENTO').attr('group', 'CONTROL').attr('title','Equipamento').html("Equipamento");
		jQuery("div.drag7.dragMenu").attr('type', 'METODO').attr('group', 'CONTROL').attr('title','Método').html("Método");
		jQuery("div.drag8.dragMenu").attr('type', 'RECEBIMENTO').attr('group', 'CONTROL').attr('title','Recebimento').html("Recebimento");		
		jQuery("div.drag9.dragMenu").attr('type', 'HISTORICO').attr('group', 'CONTROL').attr('title','Histórico').html("Histórico");
		jQuery("div.drag10.dragMenu").attr('type', 'VALORES_REFERENCIA').attr('group', 'CONTROL').attr('title','Valores de Referência').html("Vl.Referência");
	},
	
	prepareLoadComp : function(){
		var loadComps = jQuery("div.load");		
		loadComps.click(function(e) {
			mex.selectObject(jQuery(this), false);
		});
		loadComps.dblclick(function(e) {
			if(jQuery('.ui-multidraggable').length > 1) {			
				return false;
			}									
			mex.selectObject(jQuery(this), true);
		});	
		loadComps.bind("contextmenu", function(event) {
			if(jQuery('.ui-multidraggable').length > 1) {			
				return false;
			}									
			if (jQuery(this).is(".draggedSelect")){
				mex.contextMenu.show(event, {});
			}else{
				mex.selectObject(jQuery(this), false);
				mex.contextMenu.show(event, {});
			}
			return false;
		});	

		loadComps.addClass("drag").removeClass("load");
		mex.reduzDivs();

		jQuery(".drag").multidraggable({
			containment: 'frame',
			revert: 'invalid',
			stack: '.dragged', 
			cursor: 'move',
			drag:function(ev, ui){
				mex.selectObject(jQuery(this), false);
			},
			stop:function(ev, ui) {
				mex.selectObject(jQuery(this), false);
			}
		});		
		
	},
	
	selectObject : function(object, render){
		mex.unselectAllObject(true);					
		object.addClass("draggedSelect");		
		mex.objectSelectId=object.attr("id");
		mex.positionX=object.css("left").replace('px', '');
		mex.positionY=object.css("top").replace('px', '');
		jQuery("input[name*='positionX']").val(mex.positionX);
		jQuery("input[name*='positionY']").val(mex.positionY);

		if(object.attr("type")!= null && object.attr("type")=='TEXTO_LONGO'){
			object.removeClass("drag4").addClass("drag41");
		}

		if(jQuery('.ui-multidraggable').length <= 0) {
			if(mex.oldObjectSelectId != mex.objectSelectId) {
				atualizarElementoSelecionadoController(mex.objectSelectId, jQuery('#'+mex.objectSelectId).attr('type'), mex.positionX, mex.positionY, null);
			}
			jQuery('#posicaoXY').text('x: ' + mex.positionX + ', y: '+ mex.positionY);
			mex.oldObjectSelectId = object.attr("id");
		} else {
			jQuery('#posicaoXY').text('');	
		}
		
		if (render){
			jQuery("input#frameCache").val(jQuery("div#frame").html());
			selectController(mex.objectSelectId, jQuery('#'+mex.objectSelectId).attr('type'), mex.positionX, mex.positionY, null);
		}else{
			this.hideProperties();
		}
	},
	
	selectFormulas : function(object){
		mex.unselectAllObject(true);
		object.addClass("draggedSelect");
		
		mex.objectSelectId=object.attr("id");
		mex.positionX=object.css("left").replace('px', '');
		mex.positionY=object.css("top").replace('px', '');
		jQuery("input[name*='positionX']").val(mex.positionX);
		jQuery("input[name*='positionY']").val(mex.positionY);		
		
		jQuery("input#frameCache").val(jQuery("div#frame").html());
		selectFormulasController(mex.objectSelectId, jQuery('#'+mex.objectSelectId).attr('type'), mex.positionX, mex.positionY, null);
		
	},
	
	selectVincular : function(object){
		mex.unselectAllObject(true);
		object.addClass("draggedSelect");
		
		mex.objectSelectId=object.attr("id");
		mex.positionX=object.css("left").replace('px', '');
		mex.positionY=object.css("top").replace('px', '');
		jQuery("input[name*='positionX']").val(mex.positionX);
		jQuery("input[name*='positionY']").val(mex.positionY);		
		
		jQuery("input#frameCache").val(jQuery("div#frame").html());
		selectVicularController(mex.objectSelectId, jQuery('#'+mex.objectSelectId).attr('type'), mex.positionX, mex.positionY, null);
		
	},
	
	selectRelacionar : function(object){
		mex.unselectAllObject(true);
		object.addClass("draggedSelect");
		
		mex.objectSelectId=object.attr("id");
		mex.positionX=object.css("left").replace('px', '');
		mex.positionY=object.css("top").replace('px', '');
		jQuery("input[name*='positionX']").val(mex.positionX);
		jQuery("input[name*='positionY']").val(mex.positionY);		
		
		jQuery("input#frameCache").val(jQuery("div#frame").html());
		selectRelacionarController(mex.objectSelectId, jQuery('#'+mex.objectSelectId).attr('type'), mex.positionX, mex.positionY, 'N');
		
	},
	
	selectRelacionarCodificado : function(object){
		mex.unselectAllObject(true);
		object.addClass("draggedSelect");
		
		mex.objectSelectId=object.attr("id");
		mex.positionX=object.css("left").replace('px', '');
		mex.positionY=object.css("top").replace('px', '');
		jQuery("input[name*='positionX']").val(mex.positionX);
		jQuery("input[name*='positionY']").val(mex.positionY);		
		
		jQuery("input#frameCache").val(jQuery("div#frame").html());
		selectRelacionarCodificadoController(mex.objectSelectId, jQuery('#'+mex.objectSelectId).attr('type'), mex.positionX, mex.positionY, 'C');
		
	},
	
	contextEvent : function(eventStr){
		var comp = jQuery('div#' + this.objectSelectId);
		if (eventStr=='select'){
			this.selectObject(comp, true);
		}else if(eventStr=='formulas'){
			this.selectFormulas(comp);
		}else if(eventStr=='vincular'){
			this.selectVincular(comp);
		}else if(eventStr=='relacionar'){
			this.selectRelacionar(comp);
		}else if(eventStr=='relacionarCodificado'){
			this.selectRelacionarCodificado(comp);
		}else if (eventStr=='unselect'){
			this.unselectAllObject(false);
		}else if (eventStr=='delete'){
			this.hideProperties();
			this.excluir();
			PF('modalConfirmacaoExclusaoWG').hide();			
		}
	},
	
	unselectAllObject : function(isAfterSelect){
		jQuery("div#frame").children().removeClass("draggedSelect");
		jQuery('#frameParamSelect').text('');
		if (!isAfterSelect){
			desSelectController(null);
		}	
	},

	atualiza : function(){	
		var comp = jQuery("#" + mex.objectSelectId);
		var group=comp.attr('group');		
		if (group=='LABEL'){
			this.refreshLabelGroup(comp);
		}else if (group=='INPUT'){
			this.refreshInputGroup(comp);
		}else if (group=='CONTROL'){
			this.refreshControlGroup(comp);
		}
		this.refreshAllGroup(comp);	
	},
	
	excluir : function(){	
		jQuery("#" + mex.objectSelectId).remove();
		excluiController(mex.objectSelectId, null);
	},
	
	limpar : function(){	
		jQuery("div.dragged").remove();
	},		
	
	refreshAllGroup : function(comp){	
		var x = jQuery("input[name*='positionX']").val();
		var y = jQuery("input[name*='positionY']").val();
		comp.css('left', x+'px');				
		comp.css('top', y+'px');		
	},
	
	refreshLabelGroup : function(comp){
		
		var texto = jQuery("#textoLivre").val();
		if (texto && texto.length == 0){
			return;
		}
		
		comp.html(texto);
		
		var style = jQuery("input[name*='textoStyle']").val();
		
		comp.attr('style',style);
		
		var exibicao = jQuery("select[name*='modoExibicao']").val();		
		if (exibicao != 'T' && exibicao != 'A'){
			comp.css('opacity', '0.3');
		}else{
			comp.css('opacity', '1');
		}
	},

	refreshInputGroup : function(comp){
		var altura = jQuery("input[name*='altura']").val();
		if (altura != null && altura.length > 0){
			comp.css('height', altura + 'px');
		}
		var largura = jQuery("input[name*='largura']").val();		
		if (largura != null && largura.length > 0){
			comp.css('width', largura + 'px');
		}
		var exibicao = jQuery("select[name*='modoExibicao']").val();		
		if (exibicao != null && exibicao != 'T' && exibicao != 'A'){
			comp.css('opacity', '0.3');
		}else{
			comp.css('opacity', '1');
		}

		if(comp.attr("type") != null && comp.attr("type")=='TEXTO_LONGO'){
			comp.css('height', '100px');
			comp.css('width', '450px');
			comp.removeClass("drag4").addClass("drag41");
		}
	},
	
	refreshControlGroup : function(comp){
		var texto = jQuery("input[name*='textoLivre']").val();
		if (texto.length == 0){
			return;
		}		
		comp.html(texto);
		var exibicao = jQuery("select[name*='modoExibicao']").val();		
		if (exibicao != 'T' && exibicao != 'A'){
			comp.css('opacity', '0.3');
		}else{
			comp.css('opacity', '1');
		}
		this.ajustaDiv(comp, jQuery("input[name*='textoLivre']").val());		
	},	

	ajustaDiv : function(div, text){
	  var html = jQuery('<span style="position:absolute;width:auto;left:-9999px">' + (text || div.html()) + '</span>');
	  if (div) {
		html.css("font-family", div.css("font-family"));
		html.css("font-size", div.css("font-size"));
		html.css("font-weight", div.css("font-weight"));
	  }
	  jQuery('body').append(html);
	  var width = html.width();
	  var height = html.height();
	  html.remove();
	  
	  div.css('width', (width+2) + 'px');
	  div.css('height', height + 'px');
	  div.css('line-height', height + 'px');
		
	  return width;
	},
	
	reduzDivs : function(){
		jQuery("[type='TEXTO_FIXO']").each(function (i) {
			var id = jQuery(this).attr('id');
			if (id.indexOf('comp') < 0) {
				var h = jQuery(this).css('height');
				var h2 = parseFloat(h);			
				jQuery(this).css('height', (h2-1) + 'px');
			}
		});
		jQuery("[type='TEXTO_NUMERO_EXPRESSAO']").each(function (i) {
			var id = jQuery(this).attr('id');
			if (id.indexOf('comp') < 0) {
				var h = jQuery(this).css('height');
				var h2 = parseFloat(h);			
				jQuery(this).css('height', (h2-0) + 'px');	
			}
		});
		jQuery("[type='TEXTO_ALFANUMERICO']").each(function (i) {
			var id = jQuery(this).attr('id');
			if (id.indexOf('comp') < 0) {
				var h = jQuery(this).css('height');
				var h2 = parseFloat(h);			
				jQuery(this).css('height', (h2-1) + 'px');	
			}
		});
		jQuery("[type='TEXTO_CODIFICADO']").each(function (i) {
			var id = jQuery(this).attr('id');
			if (id.indexOf('comp') < 0) {
				var h = jQuery(this).css('height');
				var h2 = parseFloat(h);
				jQuery(this).css('height', (h2-1) + 'px');
			}
		});
	},
		
	controlDecoration : function(comp, field) {
		if (jQuery(comp).is(':checked')){
			if (field=='S'){
				jQuery("input[name*='riscado']").attr('checked', false);
			}else{
				jQuery("input[name*='sublinhado']").attr('checked', false);				
			}
		}	
	},
	
	hideProperties : function(){
		jQuery('div#propsScroll').css("display","none");
		this.propertiesVisible=false;
	},
	
	showProperties : function(){
		jQuery('div#propsScroll').css("display","show");
		PF("modalPropriedadesWG").show();
		this.propertiesVisible=true;
	},
	
	setTextComponente : function(){
		var txt = jQuery("#textoLivre").val();
		txt=txt.replace(/(<p[^>]+?>|<p>|<\/p>)/img, "");
		txt=txt.replace(/(<root[^>]+?>|<root>|<\/root>)/img, "");
		txt=txt.replace(/(<br[^>]+?>|<br>|<\/br>)/img, "");
		mex.atualiza();
		var comp = jQuery("div#" + mex.objectSelectId);
		comp.html(txt);
		PF('modalPropriedadesWG').show();
	}	
};