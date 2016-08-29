package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTipoEtiqueta implements Dominio {

	// Etiqueta Folha A4 para medicamento
	E,
	// Etiqueta Folha A4 identificação itens do paciente
	I,
	// Etiqueta Folha A4 mista
	M,
	// Etiqueta de Exame Pré-transfusional (PCT)
	T;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E:
			return "Etiqueta Folha A4 para medicamento";
		case I:
			return "Etiqueta Folha A4 identificação itens do paciente";
		case M:
			return "Etiqueta Folha A4 mista";
		case T:
			return "Etiqueta de Exame Pré-transfusional (PCT)";
		default:
			return "";
		}
	}

	public static DominioTipoEtiqueta getInstance(String valor) {
		if ("E".equals(valor)) {
			return DominioTipoEtiqueta.E;
		} else if ("I".equals(valor)) {
			return DominioTipoEtiqueta.I;
		} else if ("M".equals(valor)) {
			return DominioTipoEtiqueta.M;
		} else if ("T".equals(valor)) {
			return DominioTipoEtiqueta.T;
		} else {
			return null;
		}
	}
	
}
