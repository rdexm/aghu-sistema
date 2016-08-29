package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrigemSolicitacao implements Dominio {
	
	PRIMEIRO_EXAME(1),
	COMPARATIVO(2);
	
	private int value;
	
	private DominioOrigemSolicitacao(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PRIMEIRO_EXAME:
			return "1° Exame";
		case COMPARATIVO:
			return "Comparativo";
		default:
			return "";
		}
	}

}