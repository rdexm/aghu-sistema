package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndicadorOrigemTipoEstadoPacientes implements Dominio {

	E("E"), // Emergência
	C("C"), // CTI
	T("T");// Todos

	private String valor;

	private DominioIndicadorOrigemTipoEstadoPacientes(String v) {
		this.valor = v;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E:
			return "Emergência";
		case C:
			return "CTI";
		case T:
			return "Todos";
		}
		return "";
	}

}
