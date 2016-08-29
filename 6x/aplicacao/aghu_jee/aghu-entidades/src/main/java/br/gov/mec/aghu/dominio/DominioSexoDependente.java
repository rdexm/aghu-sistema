package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Este domínio foi criado para ser utilizado apenas em RAP_SERVIDORES, para migrar o comportamento
 * encontrado no AGH.
 * 
 * Segundo a regra original, dependente não pode ter sexo diferente de M ou F, ao contrário de outras
 * partes do sistema, onde um cadastro de pessoa pode utilizar até três tipos diferentes de domínio sexo,
 * contendo "Masculino, Feminino, Ignorado, Qualquer e Ambos".
 * 
 * @author ptneto
 *
 */
public enum DominioSexoDependente implements Dominio {

	/**
	 * Masculino
	 */
	M,
	/**
	 * Feminino
	 */
	F;

	/**
	 * Retorna o código da enumeração solicitada.
	 */
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	/**
	 * Retorna a descrição da enumeração solicitada.
	 */
	@Override
	public String getDescricao () {
		
		switch (this) {
		case M: return "Masculino";
		case F: return "Feminino";
		default: return "";
		}
	}
}
