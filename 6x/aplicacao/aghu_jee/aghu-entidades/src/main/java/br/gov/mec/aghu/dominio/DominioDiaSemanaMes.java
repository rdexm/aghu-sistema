package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


@SuppressWarnings({"PMD.CyclomaticComplexity"})
public enum DominioDiaSemanaMes implements Dominio {

	UM(1),
	DOIS(2),
	TRES(3),
	QUATRO(4),
	CINCO(5),
	SEIS(6),
	SETE(7),
	OITO(8),
	NOVE(9),
	DEZ(10),
	ONZE(11),
	DOZE(12),
	TREZE(13),
	QUATORZE(14),
	QUINZE(15),
	DEZESSEIS(16),
	DEZESSETE(17),
	DEZOITO(18),
	DEZENOVE(19),
	VINTE(20),
	VINTE_E_UM(21),
	VINTE_E_DOIS(22),
	VINTE_E_TRES(23),
	VINTE_E_QUATRO(24),
	VINTE_E_CINCO(25),
	VINTE_E_SEIS(26),
	VINTE_E_SETE(27),
	VINTE_E_OITO(28),
	VINTE_E_NOVE(29),
	TRINTA(30),
	TRINTA_E_UM(31);
	
	private int value;
	
	private DominioDiaSemanaMes(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {

		switch (this) {
		case UM:
			return "Um";
		case DOIS:
			return "Dois";
		case TRES:
			return "Tres";
		case QUATRO:
			return "Quatro";
		case CINCO:
			return "Cinco";
		case SEIS:
			return "Seis";
		case SETE:
			return "Sete";
		case OITO:
			return "Oito";
		case NOVE:
			return "Nove";
		case DEZ:
			return "Dez";
		case ONZE:
			return "Onze";
		case DOZE:
			return "Doze";
		case TREZE:
			return "Treze";
		case QUATORZE:
			return "Quartoze";
		case QUINZE:
			return "Quinze";
		case DEZESSEIS:
			return "Dezesseis";
		case DEZESSETE:
			return "Dezessete";
		case DEZOITO:
			return "Dezoito";
		case DEZENOVE:
			return "Dezenove";
		case VINTE:
			return "Vinte";
		case VINTE_E_UM:
			return "Vinte e um";
		case VINTE_E_DOIS:
			return "Vinte e dois";
		case VINTE_E_TRES:
			return "Vinte e três";
		case VINTE_E_QUATRO:
			return "Vinte e quatro";
		case VINTE_E_CINCO:
			return "Vinte e cinco";
		case VINTE_E_SEIS:
			return "Vinte e seis";
		case VINTE_E_SETE:
			return "Vinte e sete";
		case VINTE_E_OITO:
			return "Vinte e oito";
		case VINTE_E_NOVE:
			return "Vinte e nove";
		case TRINTA:
			return "Trinta";
		case TRINTA_E_UM:
			return "Trinta e um";
		default:
			return "";
		}
	}
	
	public static DominioDiaSemanaMes getInstance(String valor) {
		if ("UM".equals(valor)) {
			return DominioDiaSemanaMes.UM;
		} else if ("DOIS".equals(valor)) {
			return DominioDiaSemanaMes.DOIS;
		} else if ("TRES".equals(valor)) {
			return DominioDiaSemanaMes.TRES;
		} else if ("QUATRO".equals(valor)) {
			return DominioDiaSemanaMes.QUATRO;
		} else if ("CINCO".equals(valor)) {
			return DominioDiaSemanaMes.CINCO;
		} else if ("SEIS".equals(valor)) {
			return DominioDiaSemanaMes.SEIS;
		} else if ("SETE".equals(valor)) {
			return DominioDiaSemanaMes.SETE;
		} else if ("OITO".equals(valor)) {
			return DominioDiaSemanaMes.OITO;
		} else if ("NOVE".equals(valor)) {
			return DominioDiaSemanaMes.NOVE;
		} else if ("DEZ".equals(valor)) {
			return DominioDiaSemanaMes.DEZ;
		} else if ("ONZE".equals(valor)) {
			return DominioDiaSemanaMes.ONZE;
		} else if ("DOZE".equals(valor)) {
			return DominioDiaSemanaMes.DOZE;
		} else if ("TREZE".equals(valor)) {
			return DominioDiaSemanaMes.TREZE;
		} else if ("QUATORZE".equals(valor)) {
			return DominioDiaSemanaMes.QUATORZE;
		} else if ("QUINZE".equals(valor)) {
			return DominioDiaSemanaMes.QUINZE;
		} else if ("DEZESSEIS".equals(valor)) {
			return DominioDiaSemanaMes.DEZESSEIS;
		} else if ("DEZESSETE".equals(valor)) {
			return DominioDiaSemanaMes.DEZESSETE;
		} else if ("DEZOITO".equals(valor)) {
			return DominioDiaSemanaMes.DEZOITO;
		} else if ("DEZENOVE".equals(valor)) {
			return DominioDiaSemanaMes.DEZENOVE;
		} else if ("VINTE".equals(valor)) {
			return DominioDiaSemanaMes.VINTE;
		} else if ("VINTE_E_UM".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_UM;
		} else if ("VINTE_E_DOIS".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_DOIS;
		} else if ("VINTE_E_TRES".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_TRES;
		} else if ("VINTE_E_DOIS".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_DOIS;
		} else if ("VINTE_E_TRES".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_TRES;
		} else if ("VINTE_E_QUATRO".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_QUATRO;
		} else if ("VINTE_E_CINCO".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_CINCO;
		} else if ("VINTE_E_SEIS".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_SEIS;
		} else if ("VINTE_E_SETE".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_SETE;
		} else if ("VINTE_E_OITO".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_OITO;
		} else if ("VINTE_E_NOVE".equals(valor)) {
			return DominioDiaSemanaMes.VINTE_E_NOVE;
		} else if ("TRINTA".equals(valor)) {
			return DominioDiaSemanaMes.TRINTA;
		} else if ("TRINTA_E_UM".equals(valor)) {
			return DominioDiaSemanaMes.TRINTA_E_UM;
		}else {
			return null;
		}
	}
	
}
