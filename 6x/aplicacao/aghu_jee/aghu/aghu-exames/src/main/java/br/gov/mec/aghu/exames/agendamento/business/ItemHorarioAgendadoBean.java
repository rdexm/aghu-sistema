package br.gov.mec.aghu.exames.agendamento.business;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class ItemHorarioAgendadoBean extends BaseBusiness implements ItemHorarioAgendadoLocal {


@EJB
private ItemHorarioAgendadoON itemHorarioAgendadoON;

	private static final Log LOG = LogFactory.getLog(ItemHorarioAgendadoBean.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;

	public enum ItemHorarioAgendadoBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_PERSISTIR_ITEM_HORARIO;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6826005070131099824L;
	@Resource
	protected SessionContext ctx;
	@Override
	public void gravarHorario(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO,VAelHrGradeDispVO vAelHrGradeDispVO, String nomeMicrocomputador, List<AgendamentoExameVO> examesAgendamentoSelecao) throws BaseException {
		try {
			getItemHorarioAgendadoON().gravarHorario(listaItemHorarioAgendadoVO, vAelHrGradeDispVO, nomeMicrocomputador);
			
			getItemHorarioAgendadoDAO().flush();
		} catch (BaseException e) {
			logError(e.getMessage(), e);
//			examesAgendamentoSelecao = getItemHorarioAgendadoON().reatacharListaExamesAgendamentoSelecao(examesAgendamentoSelecao);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(ItemHorarioAgendadoBeanExceptionCode.ERRO_AO_TENTAR_PERSISTIR_ITEM_HORARIO);
		}		
	}
	
	@Override
	public  List<AgendamentoExameVO> reatacharListaExamesAgendamentoSelecao(List<AgendamentoExameVO> examesAgendamentoSelecao) {
		return getItemHorarioAgendadoON().reatacharListaExamesAgendamentoSelecao(examesAgendamentoSelecao);
	}
	
	private AelItemHorarioAgendadoDAO getItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
		
	}
	private ItemHorarioAgendadoON getItemHorarioAgendadoON() {
		return itemHorarioAgendadoON;
	}
	
	

	
}
