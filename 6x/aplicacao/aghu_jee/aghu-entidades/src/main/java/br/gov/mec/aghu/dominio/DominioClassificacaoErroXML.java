package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Classe de Domínio para auxiliar na classificação de um erro encontrado no
 * envio de xml na integração com SIASG/SICON.
 * 
 * @author agerling
 * 
 */
public enum DominioClassificacaoErroXML implements Dominio {
	
	/**
	 * Erro de estrutura/formação do XML
	 */
	ESTRUTURA,

	/**
	 * Erro de validação no processo de envio
	 */
	VALIDACAO,
	
	/**
	 * Inserido com pendência, erro na validação de ítem
	 */
	ERRO_ITEM;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		
			case ESTRUTURA:
				return "Erro de estrutura/formação do XML";
			case VALIDACAO:
				return "Erro de validação no processo de envio";
			case ERRO_ITEM:
				return "Inserido com pendência, erro na validação de ítem";
			default:
				return "";
		}
	}
}
