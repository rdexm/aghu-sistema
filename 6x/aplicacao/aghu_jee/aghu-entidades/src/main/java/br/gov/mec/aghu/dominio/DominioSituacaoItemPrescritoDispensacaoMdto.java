package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioSituacaoItemPrescritoDispensacaoMdto implements Dominio {
	/**
	 * Gerado pela prescrição
	 */
	GP,
	/**
	 * Alterado de (pós inclusão)
	 */
	DI,
	/**
	 * Alterado para (pós geração)
	 */
	PG,
	/**
	 * Alterado para (pós inclusão)
	 */
	PI,
	/**
	 * Excluído pós geração
	 */
	EG,
	/**
	 * Excluído pós inclusão
	 */
	EI,
	/**
	 * Incluido pelo solicitante
	 */
	IS,
	/**
	 * Incluido pela farmácia
	 */
	IF,
	/**
	 * Alterado de (pós geração)
	 */
	DG,
	/**
	 * Digitado #33115 
	 */
	DT,
	/**
	 * Resultado de Desdobramento
	 */
	RD;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case GP:
			return "Gerado pela prescrição";
		case DI:
			return "Alterado de (pós inclusão)";
		case 	PG:
			return "Alterado para (pós geração)";
		case 	PI:
			return "Alterado para (pós inclusão)";
		case 	EG:
			return "Excluído pós geração";
		case 	EI:
			return "Excluído pós inclusão";
		case 	IS:
			return "Incluido pelo solicitante";
		case 	IF:
			return "Incluido pela farmácia";
		case 	DG:
			return "Alterado de (pós geração)";
		case 	DT:
			return "Digitado";
		case 	RD:
			return "Resultado de desdobramento";
		default:
			return "";
		}
	}
	
	public void setDescricao(String descricao) {
		descricao = descricao;
	}
	
	@Override
	public String toString() {
		return getDescricao();
	}
}
