package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


@SuppressWarnings({"PMD.CyclomaticComplexity"})
public enum DominioDiasMes implements Dominio {

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
	ULTIMO_DIA(31);
	
	private int value;
	
	private DominioDiasMes(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}
	
	public String getDescricaoNumerica() {

		switch (this) {
		case UM:
			return "1";
		case DOIS:
			return "2";
		case TRES:
			return "3";
		case QUATRO:
			return "4";
		case CINCO:
			return "5";
		case SEIS:
			return "6";
		case SETE:
			return "7";
		case OITO:
			return "8";
		case NOVE:
			return "9";
		case DEZ:
			return "10";
		case ONZE:
			return "11";
		case DOZE:
			return "12";
		case TREZE:
			return "13";
		case QUATORZE:
			return "14";
		case QUINZE:
			return "15";
		case DEZESSEIS:
			return "16";
		case DEZESSETE:
			return "17";
		case DEZOITO:
			return "18";
		case DEZENOVE:
			return "19";
		case VINTE:
			return "20";
		case VINTE_E_UM:
			return "21";
		case VINTE_E_DOIS:
			return "22";
		case VINTE_E_TRES:
			return "23";
		case VINTE_E_QUATRO:
			return "24";
		case VINTE_E_CINCO:
			return "25";
		case VINTE_E_SEIS:
			return "26";
		case VINTE_E_SETE:
			return "27";
		case VINTE_E_OITO:
			return "28";
		case VINTE_E_NOVE:
			return "29";
		case TRINTA:
			return "30";
		case ULTIMO_DIA:
			return "Último dia do mês";
		default:
			return "";
		}
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
		case ULTIMO_DIA:
			return "Último dia do mês";
		default:
			return "";
		}
	}
}
