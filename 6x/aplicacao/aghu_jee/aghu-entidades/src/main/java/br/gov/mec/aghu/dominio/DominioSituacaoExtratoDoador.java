package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioSituacaoExtratoDoador implements Dominio {

    /**
     * Doador Enviado Redome
     */
    ENV,
    /**
     * Doador Recebido Laboratório
     */
    LAB,
    /**
     * Exame Solicitado
     */
    SOL,
    /**
     * Exame Liberado
     */
    LIB,
    /**
     * Exame Enviado Redome
     */
    EXE,
    /**
     * Não Tipado
     */
    NTP,
    /**
     * Segunda Extração
     */
    SEG,
    /**
     * Ambigüidade
     */
    AMB,
    /**
     * Nova Coleta
     */
    NOV,
    /**
     * Erro
     */
    ERR;

    private int value;

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ENV:
			return "Doador Enviado Redome";
		case LAB:
			return "Doador Recebido Laboratório";
		case SOL:
			return "Exame Solicitado";
		case LIB:
			return "Exame Liberado";
		case EXE:
			return "Exame Enviado Redome";
		case NTP:
			return "Não Tipado";
        case SEG:
            return "Segunda Extração";
        case AMB:
            return "Ambigüidade";
        case NOV:
            return "Nova Coleta";
        case ERR:
            return "Erro";
		default:
			return "";
		}
	}
	
}
