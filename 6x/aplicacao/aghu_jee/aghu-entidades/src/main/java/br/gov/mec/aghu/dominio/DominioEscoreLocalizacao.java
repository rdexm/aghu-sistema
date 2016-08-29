package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreLocalizacao implements Dominio {
	
	E0,
	E5,
	E7,
	E8;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E0:
			return "Centro Cirúrgico";
		case E5:
			return "Sala Emergência";
		case E7:
			return "Outra UTI";
		case E8:
			return "Outro";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E0:
			return "0";
		case E5:
			return "5";
		case E7:
			return "7";
		case E8:
			return "8";
		default:
			return "";
		}
	}
	
	public Short getShortValue() {
		switch (this) {
		case E0:
			return (short)0;
		case E5:
			return (short)5;
		case E7:
			return (short)7;
		case E8:
			return (short)8;
		default:
			return null;
		}
	}
	
}
