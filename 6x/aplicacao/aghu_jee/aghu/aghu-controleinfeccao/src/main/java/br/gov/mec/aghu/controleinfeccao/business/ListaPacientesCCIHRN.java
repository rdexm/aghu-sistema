package br.gov.mec.aghu.controleinfeccao.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.ListaPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoInfeccaoTopografiasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoMedidaPreventivasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoProcedimentoRiscosDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.controleinfeccao.vo.FiltroListaPacienteCCIHVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ListaPacientesCCIHRN extends BaseBusiness {

	private static final long serialVersionUID = -5021622563486055443L;

	private static final Log LOG = LogFactory.getLog(ListaPacientesCCIHRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	//@EJB
	//private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	//@EJB
	//private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private MciMvtoMedidaPreventivasDAO mciMvtoMedidaPreventivasDAO;
	
	//@Inject
	//private MciMvtoInfeccaoTopografiaDAO mciMvtoInfeccaoTopografiaDAO;
	
	@Inject 
	private MciMvtoProcedimentoRiscosDAO mciMvtoProcedimentoRiscosDAO;
	
	//@Inject
	//private MciMvtoFatorPredisponentesDAO mciMvtoFatorPredisponentesDAO;
	
	@Inject
	private MciNotificacaoGmrDAO mciNotificacaoGmrDAO;
	
	@Inject
	private MciMvtoInfeccaoTopografiasDAO mciMvtoInfeccaoTopografiasDAO;
	
	
	private enum ListaPacientesCCIHRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PERIODO_ATENIMENTO_LISTA, MENSAGEM_PARAMETRO_P_AGHU_CCIH_PERIODO_ATEND_BUSCA_ATIVA_NAO_CONFIGURADO, MENSAGEM_DEMAIS_CRITERIOS;
	}

	public List<ListaPacientesCCIHVO> pesquisarPacientesCCIH(FiltroListaPacienteCCIHVO filtro, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException {
		
		if (filtro.getDtInicioAtendimento() != null && filtro.getDtFimAtendimento() != null) {
			this.validarPeriodoPesquisaAtendimento(filtro);
		}
		
		List<ListaPacientesCCIHVO> resultado = this.pacienteFacade.pesquisarPacientesCCIH(firstResult, maxResults, orderProperty, asc, filtro);
		
		for (ListaPacientesCCIHVO vo : resultado) {
			final Integer codigoPaciente = vo.getCodigo();
			
			// IND_PACIENTE_INTERNADO (Internado)
			vo.setIndPacienteInternado(this.aghuFacade.existePacienteInternadoListarPacientesCCIH(codigoPaciente));
			
			if(this.mciMvtoMedidaPreventivasDAO.existeNotificacaoNaoConferidaListarPacientesCCIH(codigoPaciente)
					|| this.mciMvtoInfeccaoTopografiasDAO.existeNotificacaoNaoConferidaListarPacientesCCIH(codigoPaciente)
					|| this.mciMvtoProcedimentoRiscosDAO.existeNotificacaoNaoConferidaListarPacientesCCIH(codigoPaciente)){
				// IND_NOTIF_NAO_CONFERIDAS (Com notificações não conferidas)
				vo.setIndNotifNaoConferidas(true);	
			}
			
			// IND_PACIENTE_GMR
			vo.setIndPacienteGmr(this.mciNotificacaoGmrDAO.existeNotificacaoListarPacientesCCIH(codigoPaciente));
		}
//		
//		List<Integer> listaPacientes = new ArrayList<Integer>();
//		
//		if (filtro.getConsulta() != null || filtro.getProntuario() != null) {
//			//Union 1
//			listaPacientes.addAll(aghuFacade.listarPacCodigosPorConsultaOuProntuario(filtro.getConsulta(), filtro.getProntuario()));
//		} else {
//			//Union 2
//			AghParametros paramSitAtd = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_MAM_SAT_PACIENTE_EM_ATENDIMENTO);
//			listaPacientes.addAll(aghuFacade.listarPacientesPorDataHoraMovimento(paramSitAtd.getVlrNumerico().shortValue(), DominioOrigemAtendimento.A, 
//					filtro.getDtInicioAtendimento(), filtro.getDtFimAtendimento()));
//			
//			//Union 3
//			listaPacientes.addAll(blocoCirurgicoFacade.listarPacientesPorCirurgiaEquipeProcedimento(filtro.getDtInicioCriterios(), filtro.getDtFimCriterios(), 
//					filtro.getEquipe().getId().getSerMatricula(), filtro.getEquipe().getId().getSerVinCodigo(), filtro.getUnfCirurgia().getSeq(), filtro.getCodigoProcedimento()));
//			
//			//Union 4
//			listaPacientes.addAll(ambulatorioFacade.listarPacientesPorUnidadeLeitoInternado(filtro.getDtInicioCriterios(), filtro.getDtFimCriterios(), 
//					filtro.getUnfCirurgia().getSeq(), filtro.getLeito().getLeitoID(), filtro.getIndInternado()));
//			
//			if (filtro.getSituacaoNotificacao() != null) {
//				//Union 5
//				if (filtro.getDoencaCondicao()) {
//					listaPacientes.addAll(mciMvtoMedidaPreventivasDAO.listarCodigoPacienteMvtoMedidaPreventiva(filtro.getDtInicioAtendimento(), filtro.getDtFimAtendimento(), filtro.getSituacaoNotificacao(), 
//							filtro.getCodigoDoencaCondicao(), filtro.getConferido()));
//				}
//				
//				//Union 6
//				if (filtro.getTopografiaInfeccao()){
//					listaPacientes.addAll(mciMvtoInfeccaoTopografiaDAO.listarCodigoPacienteMvtoInfeccaoTopografia(filtro.getDtInicioAtendimento(), filtro.getDtFimAtendimento(), 
//							filtro.getSituacaoNotificacao(), filtro.getCodigoTopografia(), filtro.getConferido()));
//				}
//				
//				//Union 7
//				if (filtro.getProcedimentoRisco()) {
//					listaPacientes.addAll(mciMvtoProcedimentoRiscosDAO.listarCodigoPacienteProcedimentoRisco(filtro.getDtInicioAtendimento(), 
//							filtro.getDtFimAtendimento(), filtro.getSituacaoNotificacao(), filtro.getConferido()));
//				}
//				
//				//Union 8
//				if (filtro.getFatorePredisponente()) {
//					listaPacientes.addAll(mciMvtoFatorPredisponentesDAO.listarCodigoPacienteFatorPredisponente(filtro.getDtInicioAtendimento(), filtro.getDtFimAtendimento(), filtro.getSituacaoNotificacao()));
//				}
//				
//				//Union 9
//				if (filtro.getGmr()) {
//					listaPacientes.addAll(mciNotificacaoGmrDAO.listarPacientesNotificacaoGMR(filtro.getDtInicioAtendimento(), filtro.getDtFimAtendimento(), filtro.getSituacaoNotificacao()));
//				}
//			}			
//		}		
		
		return resultado;
	}
	
	public Long pesquisarPacientesCCIHCount(FiltroListaPacienteCCIHVO filtro) throws ApplicationBusinessException {
		if (filtro.getDtInicioAtendimento() != null && filtro.getDtFimAtendimento() != null) {
			this.validarPeriodoPesquisaAtendimento(filtro);
		}
		return this.pacienteFacade.pesquisarPacientesCCIHCount(filtro);
	}

	
	//RN3
	public void validarPeriodoPesquisaAtendimento(FiltroListaPacienteCCIHVO filtro) throws ApplicationBusinessException {
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CCIH_PERIODO_ATEND_BUSCA_ATIVA);
		Float diff = null;
		if(filtro != null && filtro.getDtFimAtendimento() != null && filtro.getDtInicioAtendimento() != null){
			diff = CoreUtil.diferencaEntreDatasEmDias(filtro.getDtFimAtendimento(), filtro.getDtInicioAtendimento());
		}else {
			return;
		}
		if (diff > parametro.getVlrNumerico().floatValue()) {
			throw new ApplicationBusinessException(ListaPacientesCCIHRNExceptionCode.MENSAGEM_PERIODO_ATENIMENTO_LISTA, 
					parametro.getVlrNumerico().floatValue());
		}
	}
	
	public void validarNotificacaoSelecionada(FiltroListaPacienteCCIHVO filtro) throws ApplicationBusinessException {
		if (filtro.getSituacaoNotificacao() != null && (filtro.getDtInicioCriterios() == null|| filtro.getDtFimCriterios() == null)) {
			throw new ApplicationBusinessException(ListaPacientesCCIHRNExceptionCode.MENSAGEM_DEMAIS_CRITERIOS);
		}
	}
}
