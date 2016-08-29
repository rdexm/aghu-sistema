package br.gov.mec.aghu.exames.agendamento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;


@Stateless
public class AgendamentoFacadeBean extends BaseFacade implements IAgendamentoFacadeBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3172145782201302011L;

	@EJB
	private ItemHorarioAgendadoLocal itemHorarioAgendadoLocal;
	
	@Override
	public void gravarHorario(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO,
			VAelHrGradeDispVO vAelHrGradeDispVO, String nomeMicrocomputador, List<AgendamentoExameVO> examesAgendamentoSelecao) throws BaseException {
		itemHorarioAgendadoLocal.gravarHorario(listaItemHorarioAgendadoVO, vAelHrGradeDispVO, nomeMicrocomputador, examesAgendamentoSelecao);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	public  List<AgendamentoExameVO> reatacharListaExamesAgendamentoSelecao(List<AgendamentoExameVO> examesAgendamentoSelecao) {
		return itemHorarioAgendadoLocal.reatacharListaExamesAgendamentoSelecao(examesAgendamentoSelecao);
	}


}