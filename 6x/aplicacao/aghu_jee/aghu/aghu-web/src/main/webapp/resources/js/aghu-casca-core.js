/**
 *  aghu-form.js -> Funções de customização e layout dos forms do AGHU
 *  Autor: Cristiano Quadros  
 */

var session = {
	ativaOnUnload : true,	
		
	closeSessionOnUnload : function(){
		if (session.ativaOnUnload){
			logoutOnUnload();
		}	
	},
	
	desativaSessionOnUnload : function(){
		session.ativaOnUnload=false;
	}			
}


var menu = {
	start : function(){
		var menuLink = jQuery("a#menu");
		menuLink.css("cursor", "pointer");
		menuLink.click(function (e) {
			menu.open(menuLink);
			e.stopPropagation();
		});	
		
		jQuery("body").click(function () {
			if (menuLink.hasClass('menu-opened')){
				menu.close(menuLink);			}
		});
		
	},
	
	open : function(menuLink){
		
		if (menuLink.hasClass('menu-opened')){
			menu.close(menuLink);
		}else{
			menuLink.addClass('menu-opened');
			amazonmenu.open('cascamenuprincipal');
		}
		return false;
	},
	
	close : function(menuLink){
		if (menuLink.length){
			menuLink.removeClass('menu-opened');
			amazonmenu.close('cascamenuprincipal');
		}	
	},
	
	frameClose : function(){
		menu.close(jQuery("a#menu.menu-opened"));
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
	
	
}


var dash = {
		init : function() {
			jQuery('div.casca-dash-icone').each(function() {
				var panel = jQuery(this);
				var graph = panel.find('img:first');
				var click = graph.attr('onclick');
				if (click) {
					panel.attr('ondblclick', click);
					graph.removeAttr('onclick');
				}
			});
			
			var close = jQuery('div.casca-fechar-card');
			close.on('mousedown', function(evt) {
				jQuery(close.on('mouseup mousemove', function handler(evt) {
					var click = close.attr('onmouseup');
					if (evt.type === 'mouseup') { // click
						pagina.closeFavorito(evt.target.id);
					} 
					jQuery(close.off('mouseup mousemove', handler));
				}));
			});	

			var dashBoard = jQuery('div.casca-dash-icone');
			dashBoard.on('mousedown', function(evt) {
				jQuery(dashBoard.on('mouseup mousemove', function handler(evt) {
					if (evt.type === 'mouseup') { // click
						var panel = jQuery(this);
						var isBtnFechar = evt.target.className
								.indexOf('casca-fechar-card') > -1;
						var click = panel.attr('ondblclick');
						if (click && !isBtnFechar) {
							panel.dblclick();
						}
					}
					jQuery(dashBoard.off('mouseup mousemove', handler));
				}));
			});
		}
}


var fav = {
	enabledStar : function (menu){
		var $menu = jQuery(menu);
		var click = $menu.attr('onclick'); 
		click = click.replace('adicionar', 'remover').replace('enabled', 'disabled');
		$menu.attr('onclick', click);
		$menu.attr('class', 'star-on');
	},
	
	disabledStar : function (menu){
		var $menu = jQuery(menu);
		var click = $menu.attr('onclick'); 
		click = click.replace('remover', 'adicionar').replace('disabled', 'enabled');
		$menu.attr('onclick', click);
		$menu.attr('class', 'star-off');
	}	
}

var timer = {
	startCounterSession : function(timer, restart){
		var calcDate = Date.now() + (timer*1000);
		var objDate = new Date(calcDate);
		var divSession = jQuery('div#tempoSessao');
		if (restart){
			divSession.countdown(objDate);
		}else{
			divSession.countdown(objDate)
			   .on('update.countdown', updateSession)
			   .on('finish.countdown', finishedSession);
		}	
	}
}

var pol = {
	openExpanded : false,
	load : function(openwest, select){
		pol.changeClikFromLinktoSpan();
		if (openwest){
			PF('layoutPOLWG').open('west');
		}
		if (select){
			pol.selectFirstNodePOL();
		}else{
			jQuery('span.node-link-pol-selected').removeClass('node-link-pol-selected');
		}			
	},	
		
	validaNodoPOL : function(url, maximize, layout){
		if (url.length==0){
			return false;
		}

		if (maximize){
			layout.open('west');
		}
		return true;
	},
	
	selectNodePOL : function(nodelink){
		jQuery('span.node-link-pol-selected').removeClass('node-link-pol-selected');
		jQuery(nodelink).closest('span.ui-treenode-content').addClass('node-link-pol-selected');
	},

	selectFirstNodePOL : function(){
		jQuery('span.node-link-pol-selected').removeClass('node-link-pol-selected');
		if(pol.openExpanded) {
			jQuery('a.node-link-pol-first').closest('span.ui-treenode-content[aria-expanded=true]').addClass('node-link-pol-selected');	
		} else {
			jQuery('a.node-link-pol-first').closest('span.ui-treenode-content').first().addClass('node-link-pol-selected');
		}
	},

	changeClikFromLinktoSpan : function(){
		jQuery('a.node-link-pol').each(function(){
			var $item = jQuery(this);
			var click = $item.attr('onclick');
			if (!click || click.length==0){
				return false;
			}else{
				$item.removeAttr('onclick');
				var span = $item.parents('span:first').get(0);
				span.setAttribute('onclick', click);
				span.setAttribute('style', 'width:90%');
			}		
		});
	}	
}

function hideModalPOL(){
	PF('layoutPOLWG').close('west');
}

function openModalPOL(){
	PF('layoutPOLWG').open('west');
}


function fixDialogMaximize(dialog){
	if(undefined == dialog.doToggleMaximize) {
		dialog.doToggleMaximize = dialog.toggleMaximize;
		dialog.toggleMaximize = function() {
	        this.doToggleMaximize();
	        var marginsDiff = this.content.outerHeight() - this.content.height();
	        var newHeight = this.jq.innerHeight() - this.titlebar.outerHeight() - marginsDiff;
	        this.content.height(newHeight);
	    };
	}
}	


function updateSession(event){
	var $this = jQuery(this).html(event.strftime('Tempo de Sessão: '
		+ '<span>%H</span>:'
		+ '<span>%M</span>:'
		+ '<span>%S</span>'));		
}


function finishedSession(event){
	timeoutSession();
}

function abrirNovaJanela(url) {
	window.open(url);
}