package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Classe de Domínio para auxiliar no controle do envio de 
 * xml na integração com SIASG/SICON.
 * @author agerling
 *
 */
public enum DominioStatusEnvio implements Dominio {
	
	/**
	 * Envio realizado com sucesso
	 */
	SUCESSO,

	/**
	 * Envio realizado com pendências
	 */
	PENDENTE,
	
	/**
	 * Erro no envio
	 */
	ERRO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		
			case SUCESSO:
				return "Envio realizado com sucesso";
			case PENDENTE:
				return "Envio realizado com pendências !!!";
			case ERRO:
				return "Erro no envio !!!";
			default:
				return "";
		}
	}
}
