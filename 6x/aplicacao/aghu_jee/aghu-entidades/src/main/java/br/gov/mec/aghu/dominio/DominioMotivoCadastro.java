package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o motivo do cadastro de um paciente
 * 
 * @author ehgsilva
 * 
 */
public enum DominioMotivoCadastro implements Dominio {
	
	/**
	 * Tratamento Renal Substitutivo
	 */
	TRATAMENTO_RENAL_SUBSTITUTIVO(1),
	
	/**
	 * Recém Nato
	 */
	RECEM_NATO(2),

	/**
	 * Gestante
	 */
	GESTANTE(3),
	
	/**
	 * Hanseníase
	 */
	HANSENIASE(4),

	/**
	 * Programa de Volta para Casa
	 */
	PROGRAMA_VOLTA_PRA_CASA(5),
	
	/**
	 * Estabelecimento Prisional
	 */
	ESTABELECIMENTO_PRISIONAL(6),

	/**
	 * Medicamento Excepcional
	 */
	MEDICAMENTO_EXCEPCIONAL(7),
	
	/**
	 * Radioterapia
	 */
	RADIOTERAPIA(8),
	
	/**
	 * Quimioterapia
	 */
	QUIMIOTERAPIA(9),

	/**
	 * Acompanhamento Pós-transplante
	 */
	ACOMPANHAMENTO_POS_TRANSPLANTE(10),
	
	/**
	 * Contagem de Linfócitos T CD4/CD8
	 */
	CONTAGEM_LINFOCITOS(11),
	
	/**
	 * Quantificação Carga Viral HIV
	 */
	QUANTIFICACAO_CARGA_VIRAL_HIV(12),
	
	/**
	 * demais proc. que exigem autorização prévia
	 */
	DEMAIS_PROC_QUE_EXIGEM_AUTORIZACAO_PREVIA(13),
	
	/**
	 * Cirurgias Eletivas de Transplantes
	 */
	CIRURGIAS_ELETIVAS_DE_TRANSPLANTE(14),
	
	/**
	 * Demais Cirurgias Eletivas - AIH
	 */
	DEMAIS_CIRURGIAS_ELETIVAS_AIH(15),
	
	/**
	 * Tuberculose
	 */
	TUBERCULOSE(16),
	
	/**
	 * Outros
	 */
	OUTROS(99);

	private int value;

	private DominioMotivoCadastro(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case TRATAMENTO_RENAL_SUBSTITUTIVO:
			return "Tratamento Renal Substitutivo";
		case RECEM_NATO:
			return "Recém Nato";
		case GESTANTE:
			return "Gestante";
		case HANSENIASE:
			return "Hanseníase";
		case PROGRAMA_VOLTA_PRA_CASA:
			return "Programa de Volta para Casa";
		case ESTABELECIMENTO_PRISIONAL:
			return "Estabelecimento Prisional";
		case MEDICAMENTO_EXCEPCIONAL:
			return "Medicamento Excepcional";
		case RADIOTERAPIA:
			return "Radioterapia";
		case QUIMIOTERAPIA:
			return "Quimioterapia";
		case ACOMPANHAMENTO_POS_TRANSPLANTE:
			return "Acompanhamento Pós-transplante";
		case CONTAGEM_LINFOCITOS:
			return "Contagem de Linfócitos T CD4/CD8";
		case QUANTIFICACAO_CARGA_VIRAL_HIV:
			return "Quantificação Carga Viral HIV";
		case DEMAIS_PROC_QUE_EXIGEM_AUTORIZACAO_PREVIA:
			return "Demais proc. que exigem autorização prévia";
		case CIRURGIAS_ELETIVAS_DE_TRANSPLANTE:
			return "Cirurgias Eletivas de Transplantes";
		case DEMAIS_CIRURGIAS_ELETIVAS_AIH:
			return "Demais Cirurgias Eletivas - AIH";
		case TUBERCULOSE:
			return "Tuberculose";
		case OUTROS:
			return "Outros";
		default:
			return "";
		}
	}

}
