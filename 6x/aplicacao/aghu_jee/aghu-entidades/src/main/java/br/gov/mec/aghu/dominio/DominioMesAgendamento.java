package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o nome do mês em lowercase para Agendamento de Sessão
 * 
 * @author rafael.silvestre
 */
public enum DominioMesAgendamento  implements Dominio {
	
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
	
	private DominioMesAgendamento(int value) {
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
			return "Janeiro";
		case FEVEREIRO:
			return "Fevereiro";
		case MARCO:
			return "Março";
		case ABRIL:
			return "Abril";
		case MAIO:
			return "Maio";
		case JUNHO:
			return "Junho";
		case JULHO:
			return "Julho";
		case AGOSTO:
			return "Agosto";
		case SETEMBRO:
			return "Setembro";
		case OUTUBRO:
			return "Outubro";
		case NOVEMBRO:
			return "Novembro";
		case DEZEMBRO:
			return "Dezembro";
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
	
	public static DominioMesAgendamento obterDominioMesAgendamento(Integer mesInt){
		DominioMesAgendamento mes;
		switch (mesInt) {
		case 0:
			mes = DominioMesAgendamento.JANEIRO;
			break;
		case 1:
			mes = DominioMesAgendamento.FEVEREIRO;
			break;
		case 2:
			mes = DominioMesAgendamento.MARCO;
			break;
		case 3:
			mes = DominioMesAgendamento.ABRIL;
			break;
		case 4:
			mes = DominioMesAgendamento.MAIO;
			break;
		case 5:
			mes = DominioMesAgendamento.JUNHO;
			break;
		case 6:
			mes = DominioMesAgendamento.JULHO;
			break;
		case 7:
			mes = DominioMesAgendamento.AGOSTO;
			break;
		case 8:
			mes = DominioMesAgendamento.SETEMBRO;
			break;
		case 9:
			mes = DominioMesAgendamento.OUTUBRO;
			break;
		case 10:
			mes = DominioMesAgendamento.NOVEMBRO;
			break;
		case 11:
			mes = DominioMesAgendamento.DEZEMBRO;
			break;
		default:
			mes = null;
			break;
		}
		return mes;
	}

}
