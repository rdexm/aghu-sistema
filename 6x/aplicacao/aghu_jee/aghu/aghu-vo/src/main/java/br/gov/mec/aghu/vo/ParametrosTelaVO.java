package br.gov.mec.aghu.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe análoga aos parâmetros de conversação
 * 
 * @author aghu
 * 
 */
public class ParametrosTelaVO implements Serializable {

	private static final long serialVersionUID = 8674857446574329214L;

	private Map<String, Object> mapaParametros = new HashMap<String, Object>();

	private static final String MENSAGEM_PARAMETRO_OBRIGATORIO = "Parâmetro de tela obrigatório não informado.";

	/**
	 * Limpa todos parâmetros
	 */
	public void limparParametros() {
		this.mapaParametros.clear();
	}

	/**
	 * Atribui nome e valor do parâmetro
	 * 
	 * @param nome
	 *            Nome identificador do parâmetro
	 * @param valor
	 *            Valor do parâmetro
	 */
	public void setParametro(String nome, Object valor) {
		if (StringUtils.isEmpty(nome)) {
			throw new IllegalArgumentException();
		}
		this.mapaParametros.put(nome, valor);
	}

	/**
	 * Resgata um parâmetro com seu tipo correspondente
	 * 
	 * @param Nome
	 *            Nome identificador do parâmetro
	 * @param tipoRetorno
	 *            Classe (CAST) do tipo de retorno
	 * @return Valor (convertido) do parâmetro
	 */
	public <T extends Object> T getParametro(String nome, Class<T> tipoRetorno) {
		return this.getParametro(nome, tipoRetorno, false);
	}
	
	/**
	 * Resgata um parâmetro OBRIGATÓRIO com seu tipo correspondente
	 * 
	 * @param Nome
	 *            Nome identificador do parâmetro
	 * @param tipoRetorno
	 *            Classe (CAST) do tipo de retorno
	 * @return Valor (convertido) do parâmetro
	 */
	public <T extends Object> T getParametroObrigatorio(String nome, Class<T> tipoRetorno) {
		return this.getParametro(nome, tipoRetorno, true);
	}

	/**
	 * Resgata o parâmetro com seu tipo correspondente e obrigatoriedade
	 * 
	 * @param Nome
	 *            Nome identificador do parâmetro
	 * @param Classe
	 *            (CAST) do tipo de retorno
	 * @param obrigatorio
	 *            Determina a obrigatoriedade na passagem do parâmetro
	 * @return Valor (convertido) do parâmetro
	 */
	private <T extends Object> T getParametro(String nome, Class<T> tipoRetorno, boolean obrigatorio) {
		if (StringUtils.isEmpty(nome) || tipoRetorno == null) {
			throw new IllegalArgumentException();
		}
		Object valor = this.mapaParametros.get(nome);
		if (obrigatorio && valor == null) {
			throw new IllegalArgumentException(MENSAGEM_PARAMETRO_OBRIGATORIO);
		}
		return tipoRetorno.cast(valor);
	}
	
	public int getTamanho() {
		return this.mapaParametros.size();
	}
	
	public boolean isVazio() {
		return this.mapaParametros.isEmpty();
	}

}
