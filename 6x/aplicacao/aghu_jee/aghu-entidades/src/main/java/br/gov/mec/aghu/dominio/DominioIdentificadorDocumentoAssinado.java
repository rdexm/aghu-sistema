package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioIdentificadorDocumentoAssinado implements Dominio {

	/**
	 * Atendimento
	 */
	ATD_SEQ,
	/**
	 * Atendimento Diversos
	 */
	ATV_SEQ,

	/**
	 * Ficha anestesia
	 */
	FIC_SEQ,

	/**
	 * Nota Prontuário On-Line
	 */
	NPO_SEQ,
	
	/**
	 * Cirurgia
	 */
	CRG_SEQ,
	
	/**
	 * Agendamento Cirurgico
	 */
	AGD_SEQ,
	
	/**
	 * Solicitação de Internação
	 */
	AIH_SEQ,

    /**
     * Laudo AIH
     */
    LAI_SEQ,
	/**
	 * Admissão Centro Obstétrico
	 */
	ACO_SEQ,
	/**
	 * Assistência ao Parto
	 */
	AP_SEQ;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
	//	return this.toString();
		String descricao;
		switch(this){
		case ATD_SEQ:
			descricao = "Atendimento";
			break;
        case ATV_SEQ:
            descricao = "Atendimento Diversos";
            break;
		case FIC_SEQ:
			descricao = "Ficha Anestésica";
			break;
		case NPO_SEQ:
			descricao = "Nota Adicional de Evoluções";
			break;
		case CRG_SEQ:
			descricao = "Cirurgia";
			break;
		case AGD_SEQ:
			descricao = "Agenda";
			break;
		case AIH_SEQ:
			descricao = "Solicitação de Internação";
			break;			
		case ACO_SEQ:
			descricao = "Admissão Centro Obstétrico";
			break;
		case AP_SEQ:
			descricao = "Assistência ao Parto";
            break;

        case LAI_SEQ:
            descricao = "Laudo AIH";
			break;
			
		default:
			descricao = "";
			break;
	}
	
	return descricao;
}

}
