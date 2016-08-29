package br.gov.mec.aghu.exames.agendamento.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorSalaVO;
import br.gov.mec.aghu.exames.dao.AelGradeAgendaExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioAgendasPorSalaON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioAgendasPorSalaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelGradeAgendaExameDAO aelGradeAgendaExameDAO;

@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;

@EJB
private IAghuFacade aghuFacade;


	private static final long serialVersionUID = 4974851483134768743L;

	public enum RelatorioAgendasPorSalaONExceptionCode implements BusinessExceptionCode {
		AEL_01206
	}
	
	/**
	 * Retorna a Agenda de Horários Por Sala para impressão do
	 * Relatório de Agendas Por Sala
	 * 
	 * @param unidadeExecutora, dtAgenda, sala, impHorariosLivres, impEtiquetas, impTickets
	 * @return
	 */
	public List<RelatorioAgendaPorSalaVO> obterAgendasPorSala(AghUnidadesFuncionais unidadeExecutora, Date dtAgenda,
			AelSalasExecutorasExames sala, Boolean impHorariosLivres, Boolean impEtiquetas, Boolean impTickets) {
		
		List<RelatorioAgendaPorSalaVO> agendasPorSala = getAelGradeAgendaExameDAO().obterAgendas(unidadeExecutora, dtAgenda, sala, impHorariosLivres);
		
		if(agendasPorSala != null) {
			for(RelatorioAgendaPorSalaVO agenda : agendasPorSala) {
				if(agenda.getDataNascimento() != null) {
					Period period = new Period(agenda.getDataNascimento().getTime(), Calendar
							.getInstance().getTimeInMillis(), PeriodType.years());
					agenda.setIdade(period.getYears());
				}else{
					agenda.setIdade(null);
				}
				if(agenda.getAtdSeq() != null) {
					AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(agenda.getAtdSeq()); 
					agenda.setLocalizacao(this.getSolicitacaoExameFacade().recuperarLocalPaciente(atendimento));
				}else {
					agenda.setLocalizacao(null);
				}
				/*if(sala == null) {
					agenda.setNomeSala(null);
					agenda.setSalaNumero(null);
				}*/
			}
		}
		
		return agendasPorSala;
	}	
	
	/**
	 * Retorna lista de SEQ de Solicitações de Exame da Agenda
	 * 
	 * @return
	 */
	public List<Integer> obterSolicitacoesExame(List<RelatorioAgendaPorSalaVO> colecao) {
		List<Integer> listaSolicitacoesExame = new ArrayList<Integer>();
		
		for(RelatorioAgendaPorSalaVO agenda : colecao) {
			if(agenda.getSolicitacao()!=null) {
				if(!listaSolicitacoesExame.contains(agenda.getSolicitacao())) {
					listaSolicitacoesExame.add(agenda.getSolicitacao());
				}
			}
		}
		return listaSolicitacoesExame;
	}	
	
	/**
	 * Retorna lista de Solicitações de Exame da Agenda
	 * 
	 * @return
	 */
	public List<AelSolicitacaoExames> obterSolicitacoesExamePorSeq(List<RelatorioAgendaPorSalaVO> colecao) {
		List<AelSolicitacaoExames> listaSolicitacoesExame = new ArrayList<AelSolicitacaoExames>();
		
		for(RelatorioAgendaPorSalaVO agenda : colecao) {
			if(agenda.getSolicitacao()!=null) {
				AelSolicitacaoExames solicitacao = this.getAelSolicitacaoExameDAO().obterPeloId(agenda.getSolicitacao());
				if(!listaSolicitacoesExame.contains(solicitacao)) {
					listaSolicitacoesExame.add(solicitacao);
				}
			}
		}
		return listaSolicitacoesExame;
	}	
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected AelGradeAgendaExameDAO getAelGradeAgendaExameDAO() {
		return aelGradeAgendaExameDAO;
	}

	/**
	 * @ORADB AELR_AGENDA_SALA function AfterReport (Parte 1) - Merge desta procedure com tarefa #21408 definido por Rosane, Leandro e Filipe
	 * @param unidadeExecutora - Unidade executora da pesquisa da agenda
	 * @param impTickets - Se checkbox imprime tickets foi selecionado
	 * @return
	 */
	public Boolean isImprimeTicketsAgendas(AghUnidadesFuncionais unidadeExecutora, Boolean impTickets) {
		Boolean retorno = Boolean.FALSE;
		Short unfSeq = unidadeExecutora.getSeq();
		if (unidadeEhRadiologiaOuMedNuclear(unfSeq) && impTickets && unidadeEhRadiodiagnosticoOuEcografia(unfSeq)) {
			retorno = Boolean.TRUE;
		}
		return retorno;
	}

	/**
	 * @ORADB AELR_AGENDA_SALA function AfterReport (Parte 2) - Merge desta procedure com tarefa #21408 definido por Rosane, Leandro e Filipe
	 * @param unidadeExecutora - Unidade executora da pesquisa da agenda
	 * @param impEtiquetas - Se checkbox imprime etiquetas foi selecionado
	 * @return
	 * @throws BaseException 
	 */
	public ImpImpressora isImprimeEtiquetasAgendas(AghUnidadesFuncionais unidadeExecutora, Boolean impEtiquetas) throws BaseException {
		ImpImpressora retorno = null;
		Short unfSeq = unidadeExecutora.getSeq();
		if (unidadeEhRadiologiaOuMedNuclear(unfSeq) && impEtiquetas && imprimeEtiquetasCaracter(unfSeq)) {
			AghImpressoraPadraoUnids impressoraPadraoCaracter = getAghuFacade().obterImpressora(unfSeq, TipoDocumentoImpressao.ETIQUETAS_CARACTER);
			if (impressoraPadraoCaracter == null || impressoraPadraoCaracter.getImpImpressora() == null) {
				throw new ApplicationBusinessException(RelatorioAgendasPorSalaONExceptionCode.AEL_01206);
			} else {
				retorno = impressoraPadraoCaracter.getImpImpressora();
			}
		}
		return retorno;
	}
	
	protected boolean imprimeEtiquetasCaracter(Short unfSeq) {
		return getAghuFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.IMPRIME_ETIQUETAS_CARACTER);
	}

	protected Boolean unidadeEhRadiodiagnosticoOuEcografia(Short unfSeq) {
		Boolean ehRadiodiagnostico = getAghuFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.UNID_RADIODIAGNOSTICO);
		Boolean ehEcografia = getAghuFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.UNID_ECOGRAFIA);
		
		return ehRadiodiagnostico || ehEcografia;
	}
	
	protected Boolean unidadeEhRadiologiaOuMedNuclear(Short unfSeq) {
		Boolean ehRadiologia = getAghuFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.UNID_RADIOLOGIA);
		Boolean ehMedNuclear = getAghuFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.UNID_MED_NUCLEAR);
		
		return ehRadiologia || ehMedNuclear;
	}

}
