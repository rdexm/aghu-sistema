package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que a Situação de Ordem 
 * 
 * @author ehgsilva
 * 
 */
public enum DominioSituacaoOrdProtocolo implements Dominio {
	
	/**
	 * Gerado pelo Protocolo.
	 */
	GP,

	/**
	 * Ja Gerado pelo protocolo.
	 */
	JG,
	/**
	 * Gerado pelo Solicitante.
	 */
	GS,
	/**
	 * Gerado com Exclusão da prescricao.
	 */
	GE,
	/**
	 * Geração Descartada.
	 */
	GD,
	/**
	 * Ordem a ser Excluída da prescrição.
	 */
	EX;
	
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case GP:
			return "Gerado pelo Protocolo";
		case JG:
			return "Ja Gerado pelo protocolo";
		case GS:
			return "Gerado pelo Solicitante";
		case GE:
			return "Geração Descartada";
		case GD:
			return "Gerado pelo Protocolo";
		case EX:
			return "Ordem a ser Excluída da prescrição";
		default:
			return "";
		}
	}

}
