package br.gov.mec.aghu.util;

import java.util.Calendar;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioDiaSemanaColetaExames;
import br.gov.mec.aghu.dominio.DominioMes;

public class AghuEnumUtils {
	
	/**
	 * Método que retorna o dia da semana com tres digitos por extenso.
	 * 
	 * @param data
	 * @return DominioDiaSemanaColetaExames
	 */
	public static DominioDiaSemanaColetaExames retornaDiaSemanaColetaExames(
			Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);

		DominioDiaSemanaColetaExames diaSemana;
		Integer diaSemanaInt = calendar.get(Calendar.DAY_OF_WEEK);

		switch (diaSemanaInt) {
		case 1:
			diaSemana = DominioDiaSemanaColetaExames.DOM;
			break;
		case 2:
			diaSemana = DominioDiaSemanaColetaExames.SEG;
			break;
		case 3:
			diaSemana = DominioDiaSemanaColetaExames.TER;
			break;
		case 4:
			diaSemana = DominioDiaSemanaColetaExames.QUA;
			break;
		case 5:
			diaSemana = DominioDiaSemanaColetaExames.QUI;
			break;
		case 6:
			diaSemana = DominioDiaSemanaColetaExames.SEX;
			break;
		case 7:
			diaSemana = DominioDiaSemanaColetaExames.SAB;
			break;
		default:
			diaSemana = null;
			break;
		}
		return diaSemana;
	}
	
	//**************
		/**
		 * Método que retorna o mês de uma data
		 * 
		 * @param data
		 * @return
		 */
		public static DominioMes retornaMes(Date data) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(data);

			DominioMes mes;
			Integer mesInt = calendar.get(Calendar.MONTH);

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
		
		/**
		 * Método que retorna o dia da semana com tres digitos por extenso.
		 * 
		 * @param data
		 * @return DominioDiaSemana
		 */
		public static DominioDiaSemana retornaDiaSemanaAghu(Date data) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(data);

			DominioDiaSemana diaSemana;
			Integer diaSemanaInt = calendar.get(Calendar.DAY_OF_WEEK);

			switch (diaSemanaInt) {
			case 1:
				diaSemana = DominioDiaSemana.DOM;
				break;
			case 2:
				diaSemana = DominioDiaSemana.SEG;
				break;
			case 3:
				diaSemana = DominioDiaSemana.TER;
				break;
			case 4:
				diaSemana = DominioDiaSemana.QUA;
				break;
			case 5:
				diaSemana = DominioDiaSemana.QUI;
				break;
			case 6:
				diaSemana = DominioDiaSemana.SEX;
				break;
			case 7:
				diaSemana = DominioDiaSemana.SAB;
				break;
			default:
				diaSemana = null;
				break;
			}
			return diaSemana;
		}

}
