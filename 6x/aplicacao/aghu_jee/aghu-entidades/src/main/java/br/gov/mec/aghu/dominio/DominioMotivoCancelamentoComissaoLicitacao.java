package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o motivo pelo qual a comissão de licitação cancela um item
 * 
 * @author agerling
 * 
 */
public enum DominioMotivoCancelamentoComissaoLicitacao implements Dominio {
	
	/**
	 * Produto Cotado em Desacordo
	 */
	CD,

	/**
	 * Descrição Incorreta ou Incompleta
	 */
	DI,
	
	/**
	 * Insuficiência de Licitantes
	 */
	IL,
	
	/**
	 * Interesse Administrativo
	 */
	IA,
	
	/**
	 * Pareceres Técnicos Desfavoráveis
	 */
	PD,
	
	/**
	 * Pelo Usuário
	 */
	PU,
	
	/**
	 * Preço Excessivo
	 */
	PE,
	
	/**
	 * Preço em Desacordo com o Mercado
	 */
	PM,
	
	/**
	 * Sem Comparativo de Mercado
	 */
	CM,
	
	/**
	 * Sem Parecer Técnico
	 */
	PT,
	
	/**
	 * Não Houve Propostas para o Item
	 */
	SP,
	
	/**
	 * Vencimento das Propostas
	 */
	VP,
	
	/**
	 * Distribuidor Não Credenciado
	 */
	NC,
	
	/**
	 * Credenciamento Vencido
	 */
	CV,
	
	/**
	 * Parecer Desfavorável e/ou Sem Parecer Técnico
	 */
	DS,
	
	/**
	 * Inabilitado
	 */
	IH,
	
	/**
	 * Em Análise
	 */
	EA,
	
	/**
	 * Em Habitação
	 */
	EH,
	
	/**
	 * Em Negociação
	 */
	EN,
	
	/**
	 * Em Teste
	 */
	ET;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CD:
			return "Produto Cotado em Desacordo";
		case DI:
			return "Descrição Incorreta ou Incompleta";
		case IL:
			return "Insuficiência de Licitantes";
		case IA:
			return "Interesse Administrativo";
		case PD:
			return "Pareceres Técnicos Desfavoráveis";
		case PU:
			return "Pelo Usuário";
		case IH:
			return "Inabilitado";
		case PE:
			return "Preço Excessivo";
		case PM:
			return "Preço em Desacordo com o Mercado";
		case CM:
			return "Sem Comparativo de Mercado";
		case PT:
			return "Sem Parecer Técnico";
		case SP:
			return "Não Houve Propostas para o Item";
		case VP:
			return "Vencimento das Propostas";
		case NC:
			return "Distribuidor Não Credenciado";
		case CV:
			return "Credenciamento Vencido";
		case DS:
			return "Parecer Desfavorável e/ou Sem Parecer Técnico";
		case EA:
			return "Em Análise";
		case EH:
			return "Em Habitação";
		case EN:
			return "Em Negociação";
		case ET:
			return "Em Teste";
		default:
			return "";
		}
	}

}
