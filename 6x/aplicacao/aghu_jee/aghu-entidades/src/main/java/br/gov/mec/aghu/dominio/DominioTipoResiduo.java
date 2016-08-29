package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



public enum DominioTipoResiduo implements Dominio {
	
	BIO,
	QUI,
	RAD,
	REC,
	ORG,
	PER;

	@Override
	public int getCodigo() {		
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case BIO:
			return "Risco Biológico";
		case QUI:
			return "Risco Químico";
		case RAD:
			return "Radioativo";
		case REC:
			return "Comum Reciclável";
		case ORG:
			return "Comum Orgânico";
		case PER:
			return "Perfurocortantes";
		default:
			return "";
		}

	}

}
