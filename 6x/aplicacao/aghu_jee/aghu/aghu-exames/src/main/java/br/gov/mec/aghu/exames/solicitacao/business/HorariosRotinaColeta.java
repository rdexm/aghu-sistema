package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.exames.solicitacao.vo.DataProgramadaVO;
import br.gov.mec.aghu.model.AelHorarioRotinaColetas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

public abstract class HorariosRotinaColeta extends BaseBusiness {
	public abstract List<DataProgramadaVO> getHorariosRotinaColeta(AghUnidadesFuncionais unidadeSolicitante, AghUnidadesFuncionais unfSeqAvisa, Date dataHoraAtual, Date dataHoraLimite); 

	/**
	 * Itera a lista de horarios encontrados naquela data adicionando em uma lista os horários que estão dentro do range permitido.
	 * @param dataHoraAtual
	 * @param dataHoraLimite
	 * @param horariosRotina
	 * @return List<DataProgramadaVO>
	 */
	public List<DataProgramadaVO> getListaDataProgramada(Date dataHoraAtual, Date dataHoraLimite, List<AelHorarioRotinaColetas> horariosRotina) {
		List<DataProgramadaVO> lista = new ArrayList<DataProgramadaVO>();
		if (horariosRotina != null) {
			//Ordena a lista por horário
			ordenarLista(horariosRotina);
			for (AelHorarioRotinaColetas rotinaColeta : horariosRotina) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataHoraAtual);
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(rotinaColeta.getId().getHorario());
				cal2.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
				cal2.set(Calendar.MONTH, cal.get(Calendar.MONTH));
				cal2.set(Calendar.YEAR, cal.get(Calendar.YEAR));
				if (DateValidator.validaDataMenorIgual(cal2.getTime(), dataHoraLimite)) { //Se a data buscada naquele dia for um horário menos ou igual ao limite
					cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
					cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
					cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
					cal.set(Calendar.MILLISECOND, cal2.get(Calendar.MILLISECOND));
					DataProgramadaVO dataProgramadaVO = new DataProgramadaVO(cal.getTime());
					lista.add(dataProgramadaVO);
				}
			}
		}

		return lista;
	}

	public void ordenarLista(List<AelHorarioRotinaColetas> horariosRotina) {
		Collections.sort(horariosRotina, new Comparator<AelHorarioRotinaColetas>() {
			@Override
			public int compare(AelHorarioRotinaColetas o1,AelHorarioRotinaColetas o2) {
				Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				cal1.setTime(o1.getId().getHorario());
				cal2.setTime(o2.getId().getHorario());

				//Coloca qualquer data para comparar apenas hora, e minuto
				return DateUtil.obterData(2000, 1, 1, cal1.get(Calendar.HOUR_OF_DAY), cal1.get(Calendar.MINUTE)).
				compareTo(DateUtil.obterData(2000, 1, 1, cal2.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.MINUTE)));
			}
		}); 
	}
}
