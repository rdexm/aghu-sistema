package br.gov.mec.aghu.exames.agendamento.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface IAgendamentoFacadeBean extends Serializable {
	
	
	void gravarHorario(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO,VAelHrGradeDispVO vAelHrGradeDispVO, String nomeMicrocomputador, List<AgendamentoExameVO> examesAgendamentoSelecao) throws BaseException;
	
	List<AgendamentoExameVO> reatacharListaExamesAgendamentoSelecao(List<AgendamentoExameVO> examesAgendamentoSelecao);
}