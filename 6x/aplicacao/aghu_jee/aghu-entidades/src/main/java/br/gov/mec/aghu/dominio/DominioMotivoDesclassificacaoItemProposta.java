package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o motivo pelo qual um item proposta é desclassificado
 * 
 * @author agerling
 * 
 */
public enum DominioMotivoDesclassificacaoItemProposta implements Dominio {
	
	/**
	 * Sem Amostra
	 */
	SA,

	/**
	 * Material em Desacordo com o Licitado
	 */
	MD,
	
	/**
	 * Marca Em Análise
	 */
	EA,
	
	/**
	 * Marca com Parecer Desfavorável
	 */
	PD,
	
	/**
	 * Documentação Incompleta
	 */
	DI,
	
	/**
	 * Sem Documentação
	 */
	SD;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case SA:
			return "Sem Amostra";
		case MD:
			return "Material em Desacordo com o Licitado";
		case EA:
			return "Marca Em Análise";
		case PD:
			return "Marca com Parecer Desfavorável";
		case DI:
			return "Documentação Apresentada Está Incompleta";
		case SD:
			return "Sem Documentação";
		default:
			return "";
		}
	}

}
