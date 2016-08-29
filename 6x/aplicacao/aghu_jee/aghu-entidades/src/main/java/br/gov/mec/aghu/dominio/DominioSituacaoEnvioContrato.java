package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de envio do contrato
 * 
 * @author agerling
 * 
 */
public enum DominioSituacaoEnvioContrato implements Dominio {
	
	/**
	 * Aguardando Envio
	 */
	A,

	/**
	 * Enviado
	 */
	E,
	
	/**
	 * Erro no Envio
	 */
	EE,
	/**
	 * Aguardando Reenvio;
	 */
	AR;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Aguardando Envio";
		case E:
			return "Enviado";
		case EE:
			return "Erro no Envio";
		case AR:
			return "Aguardando Reenvio";
		default:
			return "";
		}
	}

}
