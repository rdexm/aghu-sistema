package br.gov.mec.aghu.core.utils;

import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe com a responsabilidade de manipulacao de Strings.<br>
 * 
 * @author rcorvalao
 *
 */
public class StringUtil {
	
	public static final String NOVA_LINHA_HTML = "</br>";

	/**
	 * Concatena os parametros da seguinte forma:<br>
	 * string = <b>strObrigatoria</b>;
	 * if ( <b>strOpcional</b> isNotBlank ) string = string + <b>separador</b> + <b>strOpcional</b>;<br>
	 * 
	 * strObrigatoria, obrigatorio.
	 * separador, se nao for informado serah utilizado "; ".
	 * 
	 * @param strObrigatoria
	 * @param strOpcional
	 * @param separador
	 * @return String nao nula e nao vazia.
	 */
	public static String concatenar(final String strObrigatoria, final String strOpcional, final String separador) {
		if ( StringUtils.isBlank(strObrigatoria) ) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		String pipeline;
		if ( StringUtils.isBlank(separador) ) {
			pipeline = "; ";
		} else {
			pipeline = separador;
		}

		//String str = StringUtils.join(objs);

		StringBuffer descricao = new StringBuffer(strObrigatoria);
		if ( StringUtils.isNotBlank(strOpcional) ) {
			descricao.append(pipeline).append(strOpcional);
		}

		return descricao.toString();
	}

	/**
	 * Verifica se existe alguma diferenca nas String informadas.<br>
	 * String vazia eh considera como null.<br>
	 * 
	 * @param string1
	 * @param string2
	 * @return <code>true</code> se existir alguma diferenca nas strins.
	 */
	public static Boolean modificado(String string1, String string2) {
		if (StringUtils.isBlank(string1)) {
			string1 = null;
		}
		if (StringUtils.isBlank(string2)) {
			string2 = null;
		}

		if (string1 == null && string2 != null) {
			return Boolean.TRUE;
		}
		if (string1 != null && string2 == null) {
			return Boolean.TRUE;
		}
		if (string1 != null && string2 != null) {
			return !string1.equalsIgnoreCase(string2);
		}

		return Boolean.FALSE;
	}

	/**
	 * Testa se é diferente de nulo e retira os espaços do início e do fim de uma string.
	 * Evita replicação de if's no código para invocar o método trim da classe String
	 * que deve ser feito para atender ao checklist.
	 * @param texto
	 * @return
	 */
	public static String trim(final String texto) {
		String textTrim = texto;
		if (StringUtils.isNotBlank(textTrim)) {
			textTrim = textTrim.trim();
		}
		return textTrim;
	}

	/**
	 * Normaliza textos. Remove acentuados.
	 * @param texto
	 * @return
	 */
	public static String normaliza(String texto){
		texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
		texto = texto.replaceAll("[^\\p{ASCII}]", "");
		return texto;
	}

	/** Remove caracteres diferentes das letras do alfabeto e os acentos
	 * 
	 * @param texto
	 * @return
	 */
	public static String removeCaracteresDiferentesAlfabetoEacentos(String texto) {
		texto = normaliza(texto);
		final String alfabeto = "abcdefghijklmnopqrstuvxywz";
		StringBuffer novoTexto = new StringBuffer();
		for (int x = 0; x < texto.length(); x++) {
			if (texto.substring(x, x + 1).equals(" ") || alfabeto.contains(texto.substring(x, x + 1).toLowerCase())) {
				novoTexto.append(texto.substring(x, x + 1));
			}
			if (texto.substring(x, x + 1).equals("/") && (x + 1 < texto.length()) && !texto.substring(x + 1, x + 2).equals(" ")) {
				novoTexto.append(' ');
			}
		}

		return novoTexto.toString();
	}

	/** Remove espaços do final do texto
	 * 
	 * @param texto
	 * @return
	 */
	public static String rightTrim(final String texto) {
		return texto.replaceAll("\\s+$", "");
	}

	/** Remove espaços do início do texto
	 * 
	 * @param texto
	 * @return
	 */
	public static String leftTrim(final String texto) {
		return texto.replaceAll("^\\s+", "");
	}
	
	/**
	 * Adiciona a String passada zeros a esquerda.
	 * Se o param for 123, e qtdeCasas for 7, 
	 * então será adicionado 4 zeros na frente de 123
	 * o retono será 0000123
	 * @param num
	 * @param qtdeDigitos
	 * @return
	 */
	public static String adicionaZerosAEsquerda(String num, Integer qtdeCasas){
		num = num==null ? "0" : num;
		qtdeCasas = qtdeCasas==null ? 0 : qtdeCasas;
		StringBuffer buf = new StringBuffer();
		for(int i =0;i<qtdeCasas;i++){
			buf.append('0');
		}
		String retorno = buf.toString() + num;
		return retorno.substring(retorno.length()-qtdeCasas);
	}
	
	/**
	 * Adiciona a Number passada zeros a esquerda.
	 * Se o param for 123, e qtdeCasas for 7, 
	 * então será adicionado 4 zeros na frente de 123
	 * o retono será 0000123
	 * @param num
	 * @param qtdeCasas
	 * @return
	 */
	public static String adicionaZerosAEsquerda(Number num, Integer qtdeCasas){
		num = num==null ? 0 : num;
		String numero = num.toString();
		return adicionaZerosAEsquerda(numero, qtdeCasas);
	}
	
	/**
	 * Método para truncar String
	 * 
	 * @param texto
	 * @param reticencias
	 * @param size
	 * @return {@link String}
	 */
	public static String trunc(String texto, Boolean reticencias, Long size) {
		if(texto == null || texto.isEmpty()){
			return "";
		}
		
		if(size != null && texto.length() > size) {
			if(reticencias != null && reticencias){
				return texto.substring(0,size.intValue()-2) + "...";
			}else{
				return texto.substring(0,size.intValue());
			}
		} else {
			return texto;
		}
	}
	
	public static String subtituiAcentos(String acentuada) {  
		CharSequence cs = new StringBuilder(acentuada);  
		return Normalizer.normalize(cs, Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");  
	}
	
	public static String formataNumeroAp(Object valor){
		final StringBuilder sb = new StringBuilder(valor.toString());
		
		while (sb.length() < 8)
		{
			// Adiciona 0 a esquerda ate atingir o numero de caracteres correto
			sb.insert(0, '0');
		}
		
		//Adiciona a máscara
		sb.insert(6, '/');
		return sb.toString();
		
	}


}
