/*
 * Funções para chamadas do casca para o AGHUWeb
 * 
 * 
 */


function exibeMensagemPendenciaPanel() {
	var pendencia = document.getElementById('i_frame_pendencias_frame_id');
	var context = document.getElementById('contextPath').value;
	
	if (pendencia == null) {
		// Cria uma aba com o id i_frame_pendencias_frame_id
		tab.addTab('pendencias_frame_id', 'Pendências', context+'/pages/casca/pendencias.xhtml', null, '1', false);
	} else {
		pendencia.contentWindow.atualizaPendencias();
	}

	Richfaces.showModalPanel('novaPendenciaModal')
}

// faz uma requisição ajax para buscar um token e no callback abre janela
function addTabToken(menuId, menuNome, url, classeIcone) {
	var context = document.getElementById('contextPath').value;
	var urlToken = context+'/token.xhtml';
	jQuery.get( urlToken, function( data ) {
		url += '&' + data.trim();
		tab.addTab(menuId, menuNome, url, classeIcone, '1', true);
	});
}			

// faz uma requisição ajax para buscar um token e no callback abre janela
function abrirOracleWebFormsModal(urlBase, params) {
	var context = document.getElementById('contextPath').value;
	var url = context+'/token.xhtml';
	jQuery.get( url, function( data ) {
		  if (data.indexOf('aghw_token') == -1){
			// erro na busca do token  
		  	data = '';
		  }	
		  
		  callBackAbrirOracleWebFormsModal(urlBase, params, data);
	});
}

//faz uma requisição ajax para buscar um token e no callback abre janela (tarefa #45444)
function abrirAGHUExternoModal(urlBase, params) {
	var context = document.getElementById('contextPath').value;
	var url = context+'/token.xhtml';
	jQuery.get( url, function( data ) {
		  if (data.indexOf('aghw_token') == -1){
			// erro na busca do token  
		  	data = '';
		  }	
		  
		  callBackAbrirAGHUExternoModal(urlBase, params, data);
	});
}

//Função para abrir URLs do AGHU v5 em janela modal
function callBackAbrirAGHUExternoModal(urlBase, params, token) {
	// é esperada uma URL como por exemplo: 
	//http://aghu-appprod2-ora.hcpa:8080/aghu/controlepaciente/visualizarRegistrosControle.seam
	var url = urlBase;
	
	var end = token.indexOf('</body>');
	var t = token.substring(token.indexOf('aghw_token=')+11,end);

	url += '?tkn=' + t;
	
	window.showModalDialog(url, null, null);
	
	if (params != null) {
		var closeFunction = params.onClose;
		if (closeFunction != null) {
			closeFunction.call();
		}
	}
}

// Função para abrir URLs do Oracle Web Forms em janela modal
function callBackAbrirOracleWebFormsModal(urlBase, params, token) {
	// é esperada uma URL como por exemplo: 
	// https://apacheoracle.hcpa.ufrgs.br/forms/frmservlet?config=aghweb&form=mamf_atu_internacao.fmx
	var url = urlBase;
	
	if (params != null) {
		var auxParams = '';
		var parameters = params.parameters;
		var end = token.indexOf('</body>');
		var t = token.substring(token.indexOf('aghw_token='),end);
		
		// adiciona o token em othersparams
		if (parameters.otherparams.trim() != ''){
			parameters.otherparams += '+';
		}
		
		//parameters.otherparams += token;
		parameters.otherparams += t;

		// adiciona os parametros na url
		if (parameters != null) {
			for (var prefix in parameters) {
				if (auxParams != '') {
					auxParams += '&';
				}
				var aux = prefix + '=' + parameters[ prefix ];
				auxParams += aux;
			}
		}
		
		// Como a URL passada já contém um caracter ? então 
		// concatena com & os demais parãmetros
		url += '&' + auxParams;
	}
	
	window.showModalDialog(url, null, "dialogWidth:1024px;dialogHeight:768px;center:yes");
	
	if (params != null) {
		var closeFunction = params.onClose;
		if (closeFunction != null) {
			closeFunction.call();
		}
	}
}			