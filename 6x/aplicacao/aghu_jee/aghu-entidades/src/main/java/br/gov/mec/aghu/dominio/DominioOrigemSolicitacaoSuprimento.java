package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para a origem de solicitação de suprimentos
 * 
 * @author mpessoa
 * 
 */
public enum DominioOrigemSolicitacaoSuprimento implements Dominio{
	
	/**
	 * Solicitacao de Compra de Material
	 */
	SC,
	/**
	 * Solicitacao de Serviço
	 */
	SS,
	/**
	 * Cadastro de Materiais
	 */
	CM,
	/**
	 * Cadastro de Serviço
	 */
	CS,
	/**
	 * Processo Administrativo de Compras (PAC)
	 */
	PC,
	/**
	 * Parecer Material (Parecer Técnico)
	 */
	PMT,
	/**
	 * Avaliação Técnica (Parecer Técnico)
	 */
	PAT,
	/**
	 *   Avaliação Consultoria(Parecer Técnico)
	 */
	PAC,
	/**
	 *   Avaliação Desempenho(Parecer Técnico)
	 */
	PAD,
	/**
	 *   Ocorrência(Parecer Técnico)
	 */ 
	POC;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case SC:
			return "SOLICITAÇÃO DE COMPRA";
		case SS:
			return "SOLICITAÇÃO DE SERVIÇO";
		case CM:
			return "CADASTRO DE MATERIAIS";
		case CS:
			return "CADASTRO DE SERVIÇO";
		case PC:
			return "PROCESSO ADMINISTRATIVO DE COMPRAS";
		case PMT:
			return "PARECER MATERIAL";	
		case PAT:
			return "AVALIAÇÃO TÉCNICA";		
		case PAC:
			return "AVALIAÇÃO CONSULTORIA";		
		case PAD:
			return "AVALIAÇÃO DESEMPENHO";	
		case POC:
			return "OCORRÊNCIA";		
		default:
			return "";
		}
	}

}
