package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o nome do mês
 * 
 * @author dansantos
 * 
 */
public enum DominioMes implements Dominio {
	
	JANEIRO(0),
	FEVEREIRO(1),
	MARCO(2),
	ABRIL(3),
	MAIO(4),
	JUNHO(5),
	JULHO(6),
	AGOSTO(7), 
	SETEMBRO(8),
	OUTUBRO(9),
	NOVEMBRO(10),
	DEZEMBRO(11);
	
	private int value;
	
	private DominioMes(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case JANEIRO:
			return "JANEIRO";
		case FEVEREIRO:
			return "FEVEREIRO";
		case MARCO:
			return "MARÇO";
		case ABRIL:
			return "ABRIL";
		case MAIO:
			return "MAIO";
		case JUNHO:
			return "JUNHO";
		case JULHO:
			return "JULHO";
		case AGOSTO:
			return "AGOSTO";
		case SETEMBRO:
			return "SETEMBRO";
		case OUTUBRO:
			return "OUTUBRO";
		case NOVEMBRO:
			return "NOVEMBRO";
		case DEZEMBRO:
			return "DEZEMBRO";
		default:
			return "";
		}
	}
	
	public String getDescricaoAbreviacao() {
		switch (this) {
		case JANEIRO:
			return "JAN";
		case FEVEREIRO:
			return "FEV";
		case MARCO:
			return "MAR";
		case ABRIL:
			return "ABR";
		case MAIO:
			return "MAI";
		case JUNHO:
			return "JUN";
		case JULHO:
			return "JUL";
		case AGOSTO:
			return "AGO";
		case SETEMBRO:
			return "SET";
		case OUTUBRO:
			return "OUT";
		case NOVEMBRO:
			return "NOV";
		case DEZEMBRO:
			return "DEZ";
		default:
			return "";
		}
	}
	
	public static DominioMes obterDominioMes(Integer mesInt){
		DominioMes mes;
		switch (mesInt) {
		case 0:
			mes = DominioMes.JANEIRO;
			break;
		case 1:
			mes = DominioMes.FEVEREIRO;
			break;
		case 2:
			mes = DominioMes.MARCO;
			break;
		case 3:
			mes = DominioMes.ABRIL;
			break;
		case 4:
			mes = DominioMes.MAIO;
			break;
		case 5:
			mes = DominioMes.JUNHO;
			break;
		case 6:
			mes = DominioMes.JULHO;
			break;
		case 7:
			mes = DominioMes.AGOSTO;
			break;
		case 8:
			mes = DominioMes.SETEMBRO;
			break;
		case 9:
			mes = DominioMes.OUTUBRO;
			break;
		case 10:
			mes = DominioMes.NOVEMBRO;
			break;
		case 11:
			mes = DominioMes.DEZEMBRO;
			break;
		default:
			mes = null;
			break;
		}
		return mes;
	}

}
