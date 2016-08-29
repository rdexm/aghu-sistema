		
/** Funcao que barra o enter em um campo do formulario como tab */
function barraEnter(evt){
	
	var k=evt.keyCode||evt.which;
	return k!=13;
}

/**
 * Desabilita o ENTER.
 * 
 * @param e
 * @return
 */
function disableEnterKey(e){
     var key;
     if(window.event)
          key = window.event.keyCode; //IE
     else
          key = e.which; //FF

     if(key == 13)
    	 return false;
     else
    	 return true;
}

function stopRKey(evt) {
	var evt = (evt) ? evt : ((event) ? event : null);
   	var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
   	if ((evt.keyCode == 13 || evt.which == 13) && (node.type=="text" || node.type=="checkbox")) {
   		return false;
   	}
}

function enterOnblur(evt, element){
	var k=evt.keyCode||evt.which;
	
	if (k == 13){ // Tecla 'ENTER'
		jQuery(element).blur();
		return false;
	}
	
	return true;
}

function enterInSelectField(event, enter){
	if (event.keyCode == 13 && enter!='true'){
		jQuery('input.bt_ok:first').click();		
	}
}


function changeEnterToCommandButton(event, buttonId){
	if (event.keyCode == 13){
		jQuery('input#'+buttonId).click();		
	}
}

/**
 * Recebe o evento e o valor do componente e retorna no formato que for configurado exemplo ##/##-####
 * 
 * @param objeto
 * @param teclaPress
 * @param mascara
 * @return
 */
function mascaraCampoDados(objeto, teclaPress, mascara){
	campo = eval (objeto);
	tamanhoMascara = mascara.length;
	tamanhoCampo = campo.value.length ;
	eventoTecla = capturarTeclaEvento(teclaPress);
	teclaComum = isTeclaComum(eventoTecla);
	
	if (isTeclaNumerica(eventoTecla)){
		if (!teclaComum){
			for(var i = tamanhoCampo ; i < tamanhoMascara; i++){
				if (mascara.charAt(i) == '#') 
					return true;
      			else
      				campo.value += mascara.charAt(i);
      		}
		}
		return true;
	}
	
	return false;
}

/**
 * Captura a tecla pressionada.
 * 
 * @param teclaPress
 * @return
 */
function capturarTeclaEvento(teclaPress){
	if (document.all)
		return window.event.keyCode;
	else if(document.layers || navigator.appName == "Netscape")
		return teclaPress.which;
	else
		return teclaPress.keyCode;
}

/**
 * Identifica se é um tecla comum. 
 * 
 * TODO: Melhorar comentário.
 * 
 * @param eventoTecla
 * @return
 */
function isTeclaComum(eventoTecla){
	return  eventoTecla == 0 || eventoTecla == 8 || eventoTecla == 9 || eventoTecla == 16 
			|| eventoTecla == 17 || eventoTecla == 18 || eventoTecla ==  20;
}


/**
 * É tecla numérica?
 * 
 * @param eventoTecla
 * @return
 */
function isTeclaNumerica(eventoTecla){
	switch (eventoTecla) {
		case   0:
		case   8:
		case   9:
		case  16:
		case  17:
		case  18:
		case  20:
		case  48://0
		case  49://1
		case  50://2
		case  51://3
		case  52://4
		case  53://5
		case  54://6
		case  55://7
		case  56://8
		case  57://9
			return true;
	}
	return false;
}

function isTeclaNumericaKeyboard(eventoTecla){
	switch (eventoTecla) {
		case   0:
		case   8:
		case   9:
		case  13: //enter
		case  16:
		case  17:
		case  18:
		case  20:
		case  33: // pgup
		case  34: // pgdown
		case  35: // end
		case  36: // home
		case  37: // left
		case  38: // up
		case  39: // right
		case  40: // down
		case  46: // delete
		case  48://0
		case  49://1
		case  50://2
		case  51://3
		case  52://4
		case  53://5
		case  54://6
		case  55://7
		case  56://8
		case  57://9
		case  96://0
		case  97://1
		case  98://2
		case  99://3
		case  100://4
		case  101://5
		case  102://6
		case  103://7
		case  104://8
		case  105://9
			return true;
	}
	return false;
}

function mascara(o,f){
    v_obj=o
    v_fun=f
    setTimeout("execmascara()",1)
}

function execmascara(){
    v_obj.value=v_fun(v_obj.value)
}

function soNumeros(v){
    return v.replace(/\D/g,"")
}




/**
 * Esta função valida a data no formato: dd/MM/yyyy HH:mm. Adicionando HH:mm igual a 00:00 quando não informado.
 * A mesma pode ser mais robusta de maneira a identificar quando o usuario digitar apenas a HH e etc.
 * 
 * @param obj
 * @return campo data no padrão dd/MM/yyyy HH:mm
 */
function validarCampoDataHora(obj){
	
	campo = eval(obj);
	
	valorCampo = campo.value;
	
	if(valorCampo == "")
		return;
	
	//Ex: 01/07/1
	if(valorCampo.length < 8){
		alert('Formato inválido. DD/MM/AAAA. Ex: 10/09/2012');
		return;
	}
	
	ano = (valorCampo.substring(6,10));
	
	if(ano.length != 4){
		//Atribuindo ano atual.
		data = new Date();
		anoAtual = ""+data.getFullYear();
		campo.value = valorCampo.substring(0,6) + (ano.length==3?anoAtual.substring(0,1):anoAtual.substring(0,2)) + ano;
	}
	
	//Hora
	hora = (valorCampo.substring(8,12));
	
	if(hora.length != 5){
		valorCampo = campo.value;
		switch(valorCampo.length){
		case 10:
			campo.value = campo.value + " 00:00";
			break;
		case 11:
			campo.value = campo.value + "00:00";
			break;
		case 12:
			// Ex: 01/01/2010 8 => 01/01/2010 08:00
			campo.value = campo.value.substring(0,11) + "0" + campo.value.charAt(11) + ":00"; 
			break;
		case 13:
			aux = valorCampo.substring(11,13);
			
			if(aux<23){
				// Ex: 01/01/2010 08 => 01/01/2010 08:00
				campo.value = campo.value.substring(0,13) +":00";
			}else{
				// Ex: 01/01/2010 83 => 01/01/2010 08:30
				campo.value = campo.value.substring(0,11) + "0" + campo.value.charAt(11) + ":" + campo.value.charAt(12) + "0";
			}
			
			break;
		case 14:
			aux = valorCampo.substring(11,13);
			
			if(aux<23){
				// Ex: 01/01/2010 08: => 01/01/2010 08:00
				campo.value = campo.value.substring(0,13) +":00";
			}else{
				// Ex: 01/01/2010 83: => 01/01/2010 08:30
				campo.value = campo.value.substring(0,11) + "0" + campo.value.charAt(11) + ":" + campo.value.charAt(12) + "0";
			}
			
			break;
		case 15:
			aux = valorCampo.substring(11,13);
			if(aux<23){
				// Ex: 01/01/2010 08:5 => 01/01/2010 08:50
				campo.value = campo.value.substring(0,15) +"0";
			}else{
				// Ex: 01/01/2010 83:5 => 01/01/2010 08:35
				campo.value = campo.value.substring(0,11) + "0" + campo.value.charAt(11) + ":" + campo.value.charAt(12) + campo.value.charAt(14);
			}
			break;
		default:
			// Se for 16 estará ok. A máscara garantirá que não terá mais que 16 characteres.
			break;
		}
	}
	
	valorCampo = campo.value;
	
	//Caso dia, mes, hora ou minuto estejam no formato não esperado: substitui por 00 ou 01.
	
	dia = valorCampo.substring(0,2);
	if(dia < 1 || dia > 31){
		alert('Formato inválido. Dia: 1 - 31');
		campo.value = "01" + campo.value.substring(2,16);
	}
	
	mes = valorCampo.substring(3,5);
	if(mes < 1 || mes > 12){
		alert('Formato inválido. Mês: 1 - 12');
		campo.value = campo.value.substring(0,3) + "01" + campo.value.substring(5,16);
	}
		
	hora = valorCampo.substring(11,13);
	if(hora > 23){
		alert('Formato inválido. Hora: 0 - 23');
		campo.value = campo.value.substring(0,11) + "00:" + campo.value.charAt(14) + campo.value.charAt(15);
	}
	
	min = valorCampo.substring(14,16);
	if(min > 59){
		alert('Formato inválido. Minuto: 0 - 59');
		campo.value = campo.value.substring(0,14) + "00";
	}
	
	
}

/**
 * Esta função valida a data no formato: dd/MM/yyyy HH:mm:ss. Adicionando HH:mm:ss igual a 00:00:00 quando não informado.
 * A mesma pode ser mais robusta de maneira a identificar quando o usuario digitar apenas a HH e etc.
 * 
 * @param obj
 * @return campo data no padrão dd/MM/yyyy HH:mm:ss
 */
function validarCampoDataHoraMinSec(obj){
	
	campo = eval(obj);
	
	valorCampo = campo.value;
	
	if(valorCampo == "")
		return;
	
	//Ex: 01/07/1
	if(valorCampo.length < 8){
		alert('Formato inválido. DD/MM/AAAA. Ex: 10/09/2012');
		return;
	}
	
	ano = (valorCampo.substring(6,10));
	
	if(ano.length != 4){
		//Atribuindo ano atual.
		data = new Date();
		anoAtual = ""+data.getFullYear();
		campo.value = valorCampo.substring(0,6) + (ano.length==3?anoAtual.substring(0,1):anoAtual.substring(0,2)) + ano;
	}
	
	//Hora
	hora = (valorCampo.substring(8,12));
	
	if(hora.length != 5){
		valorCampo = campo.value;
		switch(valorCampo.length){
		case 10:
			campo.value = campo.value + " 00:00:00";
			break;
		case 11:
			campo.value = campo.value + "00:00:00";
			break;
		case 12:
			// Ex: 01/01/2010 8 => 01/01/2010 08:00
			campo.value = campo.value.substring(0,11) + "0" + campo.value.charAt(11) + ":00:00"; 
			break;
		case 13:
			aux = valorCampo.substring(11,13);
			
			if(aux<23){
				// Ex: 01/01/2010 08 => 01/01/2010 08:00
				campo.value = campo.value.substring(0,13) +":00:00";
			}else{
				// Ex: 01/01/2010 83 => 01/01/2010 08:30
				campo.value = campo.value.substring(0,11) + "0" + campo.value.charAt(11) + ":" + campo.value.charAt(12) + "0:00";
			}
			
			break;
		case 14:
			aux = valorCampo.substring(11,13);
			
			if(aux<23){
				// Ex: 01/01/2010 08: => 01/01/2010 08:00
				campo.value = campo.value.substring(0,13) +":00:00";
			}else{
				// Ex: 01/01/2010 83: => 01/01/2010 08:30
				campo.value = campo.value.substring(0,11) + "0" + campo.value.charAt(11) + ":" + campo.value.charAt(12) + "0:00";
			}
			
			break;
		case 15:
			aux = valorCampo.substring(11,13);
			if(aux<23){
				// Ex: 01/01/2010 08:5 => 01/01/2010 08:50
				campo.value = campo.value.substring(0,15) +"0:00";
			}else{
				// Ex: 01/01/2010 83:5 => 01/01/2010 08:35
				campo.value = campo.value.substring(0,11) + "0" + campo.value.charAt(11) + ":" + campo.value.charAt(12) + campo.value.charAt(14)+":00";
			}
			break;
		case 16:
			// Ex: 01/01/2010 08:50 => 01/01/2010 08:50:00
			campo.value = campo.value.substring(0,16) +":00";
			
			break;
		case 17:
			// Ex: 01/01/2010 08:50: => 01/01/2010 08:50:00
			campo.value = campo.value.substring(0,17) +"00";
		
			break;
		case 18:
			// Ex: 01/01/2010 08:50:0 => 01/01/2010 08:50:00
			campo.value = campo.value.substring(0,18) +"0";
			
			break;
		default:
			// Se for 16 estará ok. A máscara garantirá que não terá mais que 16 characteres.
			break;
		}
	}
	
	valorCampo = campo.value;
	
	//Caso dia, mes, hora ou minuto estejam no formato não esperado: substitui por 00 ou 01.
	
	dia = valorCampo.substring(0,2);
	if(dia < 1 || dia > 31){
		alert('Formato inválido. Dia: 1 - 31');
		campo.value = "01" + campo.value.substring(2,16);
	}
	
	mes = valorCampo.substring(3,5);
	if(mes < 1 || mes > 12){
		alert('Formato inválido. Mês: 1 - 12');
		campo.value = campo.value.substring(0,3) + "01" + campo.value.substring(5,16);
	}
		
	hora = valorCampo.substring(11,13);
	if(hora > 23){
		alert('Formato inválido. Hora: 0 - 23');
		campo.value = campo.value.substring(0,11) + "00:" + campo.value.charAt(14) + campo.value.charAt(15);
	}
	
	min = valorCampo.substring(14,16);
	if(min > 59){
		alert('Formato inválido. Minuto: 0 - 59');
		campo.value = campo.value.substring(0,14) + "00";
	}
	
	sec = valorCampo.substring(17,19);
	if(sec > 59){
		alert('Formato inválido. Segundo: 0 - 59');
		campo.value = campo.value.substring(0,17) + "00";
	}
}

function formatar(src, mask , e) {
	var tecla = ( window.event ) ? e.keyCode : e.which;/*pega qual tecla foi pressionada*/
	if ( tecla == 8 || tecla == 0 ) return true; /*retorna verdadeiro se foi delete, backspace...*/
	var i = src.value.length;
	var saida = mask.substring(0,1);
	var texto = mask.substring(i);

	if (texto.substring(0,1) != saida){
		src.value += texto.substring(0,1);
	}
}

function impedeCaracteresEspeciais(objeto){
	objeto.value = objeto.value.replace(/[^a-z^A-Z^0-9.\-ÁáÂâÀàÃãÉéÍíÔôÓóÕõÇç\ \\\/]/g,"");
	objeto.value = objeto.value.replace('@',""); 
}

function impedeCaracteresEspeciaisParam(objeto,tipo){
	if(tipo == 'email'){
		objeto.value = objeto.value.replace(/[^a-z^0-9\-_@.\ \\\/]/g,"");
	}
	
	if(tipo == 'web'){
		objeto.value = objeto.value.replace(/[^a-z^0-9\-_.\ \\\/]/g,"");
	}

}

function bloquearImpressao(e,modal){
	var ctrl = e.ctrlKey;
	var tecla = e.keyCode; 
	if (ctrl && tecla==80) {  
		modal.show();
		if(e.cancelable){
			e.preventDefault();
		} 
		e.keyCode=0; 
		e.returnValue=false; 
		return false;
	}
}


/*
 * Função criada para truncar os valores no componente outputText
 * Utilize no campo value e usa escape='false'
 * Autor: Cristiano Quadros
 */
function truncStr(str, cols){
	var newStr = str;
	if (cols==null || cols>0){
		newStr = newStr.substring(0, cols);
	}
	return newStr;
}

var retornoModal = '';

function abrirJanelaModal(urlBase, params) {
	var url = urlBase;
	
	if (params != null) {
		var auxParams = '';
		var parameters = params.parameters;
		if (parameters != null) {
			for (var prefix in parameters) {
				if (auxParams != '') {
					auxParams += '&';
				}
				var aux = prefix + '=' + parameters[ prefix ];
				auxParams += aux;
			}
		}
		url += '?' + auxParams;
	}
	
	window.showModalDialog(url, null, "dialogWidth:1024px;dialogHeight:768px;center:yes");
	
	if (params != null) {
		var closeFunction = params.onClose;
		if (closeFunction != null) {
			closeFunction.call();
		}
	}
}
/*
 * Corrige o bug do js para horário de verão
 * 
 */
function fixDate(calendar){  
	if(calendar && calendar.getSelectedDateString("HH") == '23'){
		var timeDate = calendar.selectedDate.getTime();
		var testData = new Date(timeDate+(60*60*1000));
		testData.setHours(0,0,0,0);
		if (testData.getHours()>0){
			calendar.selectedDate.setTime(timeDate+(60*60*1000));        	
		}
	}    
} 



/*
 *
 * Limpa o campo input que conter apenas zero
 *
 */
function limpaZero(comp) {
    if(comp.value=='0' || comp.value=='00'){
    	comp.value='';
	}
}


function validaDataVazia(input) {
	if (!/\d/.test(input.value)) { // possui um número
		input.value = '';
	}
}
function validaEnterDataVazia(input, event){
	if(event.keyCode == 13){
		validaDataVazia(input);
	}
}

function caretToEndIfNumbers(el, evt) {
	var evt = (evt) ? evt : ((event) ? event : null);
	
	// Apenas numeros
	if ((evt.keyCode >= 48 && evt.keyCode <= 57) 
			|| (evt.keyCode >= 96 && evt.keyCode <= 105)) {
		index = el.value.length;
		
		if (el.createTextRange) { 
			var range = el.createTextRange(); 
			range.move("character", index); 
			range.select(); 
		} else if (el.selectionStart != null) {
			el.focus();
			el.setSelectionRange(index, index); 
		}
	}
}

function caretToEndIfNumbers(input) {
	var evt = (evt) ? evt : ((event) ? event : null);
	
	// Apenas numeros
	if ((evt.keyCode >= 48 && evt.keyCode <= 57) 
			|| (evt.keyCode >= 96 && evt.keyCode <= 105)) {
		index = el.value.length;
		
		if (el.createTextRange) { 
			var range = el.createTextRange(); 
			range.move("character", index); 
			range.select(); 
		} else if (el.selectionStart != null) {
			el.focus();
			el.setSelectionRange(index, index); 
		}
	}
}

//Metodo que pode ser usando em onclick, 
//para selecionar todos selectBooleanCheckbox de uma lista.
function checkAll(inputId, state) {

	var idNew = inputId.substr(0, inputId.lastIndexOf(':'));
	var idtab = idNew;
	var elemetos = document.getElementById(idtab);
	var inputs = elemetos.getElementsByTagName('input');

	for (var i = 0; i <= inputs.length; i++) {
		var input = inputs[i];
		if (input != undefined && !input.getAttribute('disabled')) {
			if (input.getAttribute('type') == 'checkbox' && state) {
					input.checked = true;
			} else {

				input.checked = false;

			}
		}
	}
}