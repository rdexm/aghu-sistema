package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica implements Dominio {
	/*
	 * Nome
	 */
	NOME,
	/*
	 * Função
	 */
	FUNCAO,
	/*
	 * Código
	 */
	CODIGO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NOME:
			return "Nome";
		case FUNCAO:
			return "Função";
		case CODIGO:
			return "Código";
		default:
			return "";
		}	
	}
}
