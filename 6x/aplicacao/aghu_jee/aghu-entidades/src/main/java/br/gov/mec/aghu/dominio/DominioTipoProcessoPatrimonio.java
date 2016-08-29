package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoProcessoPatrimonio implements Dominio{

	/**
	 * 	Recebimento
	 */
	RECEBIMENTO(1),
	/**
	 * 	Aceite Técnico
	 */
	ACEITE_TECNICO(2),
	/**
	 * 	Notificação Técnica
	 */
	NOTIFICACAO_TECNICA(3),
	/**
	 * 	Manutenção de Bem Permanente
	 */
	MANUTENCAO_BEM_PERMANENTE(4),
	/**
	 * 	Boletim de Ocorrência
	 */
	BOLETIM_OCORRENCIA(5),
	/**
	 * 	Processo Administrativo de Penalidade
	 */
	PROCESSO_ADMINISTRATIVO_PENALIDADE(6),
	/**
	 * 	Regularização de Bens de Terceiros
	 */
	REGULARIZACAO_BENS_TERCEIROS(7),
	/**
	 * 	Baixa
	 */
	BAIXA(8),
	/**
	 * 	Cadastro de Beneficiário
	 */
	CADASTRO_DE_BENEFICIARIO(9),
	/**
	 * 	Doação
	 */
	DOACAO(10),
	/**
	 * Sindicância
	 */
	SINDICANCIA(11);
	
	
	private Integer valor;
	
	private DominioTipoProcessoPatrimonio(Integer valor) {
		this.valor = valor;
	}	
	
	
	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case RECEBIMENTO:
			return "Recebimento";
		case ACEITE_TECNICO:
			return "Aceite Técnico";
		case NOTIFICACAO_TECNICA:
			return "Notificação Técnica";
		case MANUTENCAO_BEM_PERMANENTE:
			return "Manutenção de Bem Permanente";
		case BOLETIM_OCORRENCIA:
			return "Boletim de Ocorrência";
		case PROCESSO_ADMINISTRATIVO_PENALIDADE:
			return "Processo Administrativo de Penalidade";
		case REGULARIZACAO_BENS_TERCEIROS:
			return "Regularização de Bens de Terceiros";
		case BAIXA:
			return "Baixa";
		case CADASTRO_DE_BENEFICIARIO:
			return "Cadastro de Beneficiário";
		case DOACAO:
			return "Doação";
		case SINDICANCIA:
			return "Sindicância";
		default:
			return "";
		}
	}	
	
	public static String getDescricao(Integer i) {
		switch (i) {
		case 1:
			return "Recebimento";
		case 2:
			return "Aceite Técnico";
		case 3:
			return "Notificação Técnica";
		case 4:
			return "Manutenção de Bem Permanente";
		case 5:
			return "Boletim de Ocorrência";
		case 6:
			return "Processo Administrativo de Penalidade";
		case 7:
			return "Regularização de Bens de Terceiros";
		case 8:
			return "Baixa";
		case 9:
			return "Cadastro de Beneficiário";
		case 10:
			return "Doação";
		case 11:
			return "Sindicância";
		default:
			return "";
		}
	}	

}
