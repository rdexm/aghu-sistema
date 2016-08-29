package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoMovimentoConta implements Dominio {
	/**
	 * Saldo inicial da conta em valores próprios, por exemplo, consumo de insumos do fornecedor HCPA
	 */
	SIP,
	/**
	 * Saldo inicial da conta em valores de terceiros, por exemplo, consumo de insumos de outros fornecedores
	 */
	SIT,
	/**
	 * Saldo de vida útil da competência
	 */
	SVU,
	/**
	 * Valor alocado em atividades (observando as parametrizações)
	 */
	VAA,
	/**
	 * Valor alocado em atividades por meio de rateio
	 */
	VAR,
	/**
	 * Valor de rateio geral
	 */
	VRG;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case SIP:
			return "Saldo inicial da conta em valores próprios, por exemplo, consumo de insumos do fornecedor HCPA";
		case SIT:
			return "Saldo inicial da conta em valores de terceiros, por exemplo, consumo de insumos de outros fornecedores";
		case SVU:
			return "Saldo de vida útil da competência";
		case VAA:
			return "Valor alocado em atividades (observando as parametrizações)";
		case VAR:
			return "Valor alocado em atividades por meio de rateio";
		case VRG:
			return "Valor de rateio geral";
		default:
			return "";
		}
	}

}
