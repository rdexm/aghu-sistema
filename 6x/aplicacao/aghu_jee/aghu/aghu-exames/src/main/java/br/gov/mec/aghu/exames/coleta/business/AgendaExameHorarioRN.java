package br.gov.mec.aghu.exames.coleta.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AgendaExameHorarioRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AgendaExameHorarioRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject
	private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IColetaExamesFacade coletaExamesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4049281627531218275L;
	
	public enum AgendaExamesHorariosRNExceptionCode implements BusinessExceptionCode {
		AEL_00915, AEL_00871, AEL_00918, AEL_00908, AEL_00910, AEL_00874, AEL_00917, ERRO_FALTA_ITENS_COLETADOS;
	}

	/**
	 * Verifica se os itens que compõem este horário possuem amostras e se há exames agendados no horário.
	 * 
	 * @param itemHorarioAgendado
	 */
	public void verificarAmostrasExamesAgendados(AelItemHorarioAgendado itemHorarioAgendado) throws ApplicationBusinessException {
		if(!getColetaExamesFacade().verificarMaterialAnaliseColetavel(itemHorarioAgendado)) {
			throw new ApplicationBusinessException(
					AgendaExamesHorariosRNExceptionCode.AEL_00915);
		}
		
		if(getAelAmostraItemExamesDAO().listarAmostrasComHorarioAgendado(itemHorarioAgendado.getId().getHedGaeUnfSeq(), 
				itemHorarioAgendado.getId().getHedGaeSeqp(), itemHorarioAgendado.getId().getHedDthrAgenda()).isEmpty()) {
			throw new ApplicationBusinessException(
					AgendaExamesHorariosRNExceptionCode.AEL_00871);
		}
		
	}
	
	private void validarRecebimentoPacientes(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) throws ApplicationBusinessException {
		AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
		id.setHedGaeUnfSeq(hedGaeUnfSeq);
		id.setHedGaeSeqp(hedGaeSeqp);
		id.setHedDthrAgenda(hedDthrAgenda);
		
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		itemHorarioAgendado.setId(id);
		
		if(getColetaExamesFacade().verificarMaterialAnaliseColetavel(itemHorarioAgendado)) {
			throw new ApplicationBusinessException(
					AgendaExamesHorariosRNExceptionCode.AEL_00918);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: RN_ISEP_ATU_AE_ITEM
	 * 
	 * @param hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda
	 */
	public void receberItemSolicitacaoExameAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, DominioSituacaoHorario dominioSituacaoHorario, String nomeMicrocomputador) throws BaseException {
		if(DominioSituacaoHorario.M.equals(dominioSituacaoHorario)) {
			validarRecebimentoPacientes(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
			
			AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
			AelSitItemSolicitacoes vIseSituacaoAreaExecutora = getAelSitItemSolicitacoesDAO().obterPeloId(parametro.getVlrTexto());
			AghParametros agendado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO);
			AelSitItemSolicitacoes situacaoAgendado = getAelSitItemSolicitacoesDAO().obterPeloId(agendado.getVlrTexto());
			
			List<AelItemHorarioAgendado> listItensAgendados = 
				getAelItemHorarioAgendadoDAO().obterExamesAgendados(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, situacaoAgendado);
			
			for(AelItemHorarioAgendado item : listItensAgendados) {
				item.getItemSolicitacaoExame().setSituacaoItemSolicitacao(vIseSituacaoAreaExecutora);
				getSolicitacaoExameFacade().atualizar(item.getItemSolicitacaoExame(), nomeMicrocomputador);
			}
		} else {
			throw new ApplicationBusinessException(
					AgendaExamesHorariosRNExceptionCode.AEL_00908);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: RN_ISEP_ATU_AG_ITEM
	 * 
	 * @param hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, dominioSituacaoHorario
	 */
	public void voltarItemSolicitacaoExameAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, DominioSituacaoHorario dominioSituacaoHorario, String nomeMicrocomputador) throws BaseException {
		if(DominioSituacaoHorario.E.equals(dominioSituacaoHorario)) {
			validarRecebimentoPacientes(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
			
			AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
			AelSitItemSolicitacoes vIseSituacaoAreaExecutora = getAelSitItemSolicitacoesDAO().obterPeloId(parametro.getVlrTexto());
			AghParametros agendado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO);
			AelSitItemSolicitacoes situacaoAgendado = getAelSitItemSolicitacoesDAO().obterPeloId(agendado.getVlrTexto());
			
			List<AelItemHorarioAgendado> listItensAgendados = 
				getAelItemHorarioAgendadoDAO().obterExamesAgendados(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, vIseSituacaoAreaExecutora);
			
			for(AelItemHorarioAgendado item : listItensAgendados) {
				item.getItemSolicitacaoExame().setSituacaoItemSolicitacao(situacaoAgendado);
				getSolicitacaoExameFacade().atualizar(item.getItemSolicitacaoExame(), nomeMicrocomputador);
			}
		} else {
			throw new ApplicationBusinessException(
					AgendaExamesHorariosRNExceptionCode.AEL_00910);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: RN_ISEP_ATU_EST_COL
	 * 
	 * @param hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda
	 */
	public void voltarColeta(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda, DominioSituacaoHorario situacao, String nomeMicrocomputador) throws BaseException {
		validarVoltarColeta(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, situacao);
		
		AghParametros coletado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO);
		AelSitItemSolicitacoes vIseSituacaoColetado = getAelSitItemSolicitacoesDAO().obterPeloId(coletado.getVlrTexto());
		
		List<AelItemHorarioAgendado> listItensAgendados = 
			getAelItemHorarioAgendadoDAO().obterItensHorarioAgendadoComAmostras(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, DominioSituacaoAmostra.C);
		
		if(listItensAgendados != null && !listItensAgendados.isEmpty()) {
			for(AelItemHorarioAgendado item : listItensAgendados) {
				if(vIseSituacaoColetado.equals(item.getItemSolicitacaoExame().getSituacaoItemSolicitacao())) {
					
					AelExtratoItemSolicitacao aelExtratoItemSolicitacao = getAelExtratoItemSolicitacaoDAO()
						.obterPenultimoExtratoPorItemSolicitacao(item.getId().getIseSoeSeq(), item.getId().getIseSeqp());
					
					if(aelExtratoItemSolicitacao != null) {
						if (aelExtratoItemSolicitacao.getAelMotivoCancelaExames() != null) {
							item.getItemSolicitacaoExame().setAelMotivoCancelaExames(aelExtratoItemSolicitacao.getAelMotivoCancelaExames());
						}
						item.getItemSolicitacaoExame().setComplementoMotCanc(aelExtratoItemSolicitacao.getComplementoMotCanc());
						item.getItemSolicitacaoExame().setSituacaoItemSolicitacao(aelExtratoItemSolicitacao.getAelSitItemSolicitacoes());
						getSolicitacaoExameFacade().atualizar(item.getItemSolicitacaoExame(), nomeMicrocomputador);
					}
				}
			}
		} else {
			throw new ApplicationBusinessException(
					AgendaExamesHorariosRNExceptionCode.ERRO_FALTA_ITENS_COLETADOS);
		}
	}
	
	private void validarVoltarColeta(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda,
			DominioSituacaoHorario situacao) throws ApplicationBusinessException {

		if(!DominioSituacaoHorario.E.equals(situacao)) {
			throw new ApplicationBusinessException(
					AgendaExamesHorariosRNExceptionCode.AEL_00874);
		} else {
			AelItemHorarioAgendadoId id = new AelItemHorarioAgendadoId();
			id.setHedGaeUnfSeq(hedGaeUnfSeq);
			id.setHedGaeSeqp(hedGaeSeqp);
			id.setHedDthrAgenda(hedDthrAgenda);
			
			AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
			itemHorarioAgendado.setId(id);
			
			if(!getColetaExamesFacade().verificarMaterialAnaliseColetavel(itemHorarioAgendado)) {
				throw new ApplicationBusinessException(
						AgendaExamesHorariosRNExceptionCode.AEL_00917);
			}
		}
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO(){
		return aelAmostraItemExamesDAO;
	}
	
	protected IColetaExamesFacade getColetaExamesFacade() {
		return this.coletaExamesFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO(){
		return aelExtratoItemSolicitacaoDAO;
	}
	
}
