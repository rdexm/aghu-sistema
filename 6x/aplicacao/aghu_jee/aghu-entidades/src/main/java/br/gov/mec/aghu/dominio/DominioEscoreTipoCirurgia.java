package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreTipoCirurgia implements Dominio {
	
	E0,
	E11,
	E8,
	E6,
	E5
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E0:
			return "Outras razões";
		case E11:
			return "Transplante: Rim, Fígado, Pâncreas, Fígado e Pâncreas, Outro transplante";
		case E8:
			return "Trauma - Outro, isolado: (inclui Tórax, Abdômen, Membro); Politraumatismo";
		case E6:
			return "Cirurgia Cardíada: Revascularização do Miocárdio sem reparo valvular";
		case E5:
			return "Neurocirurgia: Acidente Vascular Encefálico";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E0:
			return "0";
		case E11:
			return "-11";
		case E8:
			return "-8";
		case E6:
			return "-6";
		case E5:
			return "5";
		default:
			return "";
		}
	}
	
	public Short getShortValue() {
		switch (this) {
		case E0:
			return (short)0;
		case E11:
			return (short)-11;
		case E8:
			return (short)-8;
		case E6:
			return (short)-6;
		case E5:
			return (short)5;
		default:
			return null;
		}
	}
	
}
