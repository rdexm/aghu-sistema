package br.gov.mec.aghu.exames.agendamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioAgendaUnidade;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteAgenda;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorUnidadeVO;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioAgendasPorUnidadeON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioAgendasPorUnidadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelHorarioExameDispDAO aelHorarioExameDispDAO;

@EJB
private IAghuFacade aghuFacade;


	/**
	 * 
	 */
	private static final long serialVersionUID = -6196239225380110259L;
	
	public enum RelatorioAgendasPorUnidadeONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_AGENDA_DATA_FIM_MAIOR, MENSAGEM_INTERVALO_MAIOR_QUE_PERMITIDO;
	}

	/**
	 * Retorna a Agenda de Horários Por Unidade
	 * 
	 * @param unidadeExecutora, dtInicio, dtFim, origem, ordenacao
	 * @return
	 *  
	 */
	public List<RelatorioAgendaPorUnidadeVO> obterAgendasPorUnidade(AghUnidadesFuncionais unidadeExecutora, Date dtInicio,
			Date dtFim, DominioOrigemPacienteAgenda origem, DominioOrdenacaoRelatorioAgendaUnidade ordenacao) throws ApplicationBusinessException {
		
		if(dtFim != null) {
			if(dtInicio.after(dtFim)) {
				throw new ApplicationBusinessException(RelatorioAgendasPorUnidadeONExceptionCode.MENSAGEM_AGENDA_DATA_FIM_MAIOR);
			}
			// Calcula a diferença em dias entre as datas Inicio e Fim
			GregorianCalendar ini = new GregorianCalendar();
			ini.setTime(dtInicio);

	        GregorianCalendar fim = new GregorianCalendar();  
	        fim.setTime(dtFim);

	        long dt1 = ini.getTimeInMillis();  
	        long dt2 = fim.getTimeInMillis();  
	        Integer dias = (int) (((dt2 - dt1) / 86400000)+1); 
	        if(dias>30) {
	        	throw new ApplicationBusinessException(RelatorioAgendasPorUnidadeONExceptionCode.MENSAGEM_INTERVALO_MAIOR_QUE_PERMITIDO);
	        }
		}
		
		List<Short> listUnfSeqHierarquico = this.getAghuFacade().obterUnidadesFuncionaisHierarquicasPorCaract(unidadeExecutora.getSeq());
	
		List<RelatorioAgendaPorUnidadeVO> agendasPorSala = getAelHorarioExameDisDAO().obterAgendaPorUnidade(unidadeExecutora, dtInicio, dtFim, origem, ordenacao, listUnfSeqHierarquico);
		
		return agendasPorSala;
	}	
	
	/**
	 * Retorna lista de SEQ de Solicitações de Exame da Agenda
	 * 
	 * @return
	 */
	public List<Integer> obterSolicitacoesExame(List<RelatorioAgendaPorUnidadeVO> colecao) {
		List<Integer> listaSolicitacoesExame = new ArrayList<Integer>();
		
		for(RelatorioAgendaPorUnidadeVO agenda : colecao) {
			if(agenda.getSolicitacao()!=null) {
				if(!listaSolicitacoesExame.contains(agenda.getSolicitacao())) {
					listaSolicitacoesExame.add(agenda.getSolicitacao());
				}
			}
		}
		return listaSolicitacoesExame;
	}	
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected AelHorarioExameDispDAO getAelHorarioExameDisDAO() {
		return aelHorarioExameDispDAO;
	}

}
