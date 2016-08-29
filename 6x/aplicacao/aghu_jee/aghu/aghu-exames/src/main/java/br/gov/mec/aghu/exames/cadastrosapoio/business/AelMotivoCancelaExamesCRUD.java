package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMotivoCancelaExamesDAO;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AelMotivoCancelaExamesCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelMotivoCancelaExamesCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelMotivoCancelaExamesDAO aelMotivoCancelaExamesDAO;
	
	@EJB
	private IParametroFacade iParametroFacade;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3755107959963024149L;

	public enum AelMotivoCancelaExamesExceptionCode implements BusinessExceptionCode {
		AEL_00344, AEL_00343, MOTIVO_CANCELAMENTO_ALTERAR_CAMPOS_INVALIDOS, MOTIVO_CANCELAMENTO_DEPENDENCIA_ITEM_SOLICITACAO, MOTIVO_CANCELAMENTO_DEPENDENCIA_EXTRATO_ITEM;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}

	}

	/**
	 * ORABD TRIGGER AELT_MOC_BRD
	 */
	public void removerMotivoCancelamento(Short seqAelMotivoCancelaExames) throws ApplicationBusinessException {
		final AelMotivoCancelaExames motivoCancelamento = aelMotivoCancelaExamesDAO.obterPorChavePrimaria(seqAelMotivoCancelaExames);
		
		if (motivoCancelamento == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		List<AelItemSolicitacaoExames> listaItem = getAelItemSolicitacaoExameDAO().buscarItensPorMotivoCancelamento(motivoCancelamento);
		
		if (listaItem != null && !listaItem.isEmpty()) {
			throw new ApplicationBusinessException(AelMotivoCancelaExamesExceptionCode.MOTIVO_CANCELAMENTO_DEPENDENCIA_ITEM_SOLICITACAO);
		}
		
		List<AelExtratoItemSolicitacao> listaExtrato = getAelExtratoItemSolicitacaoDAO().buscarExtratoPorMotivoCancelamento(motivoCancelamento);
		
		if (listaExtrato != null && !listaExtrato.isEmpty()) {
			throw new ApplicationBusinessException(AelMotivoCancelaExamesExceptionCode.MOTIVO_CANCELAMENTO_DEPENDENCIA_EXTRATO_ITEM);
		}
		
		AghParametros parametro = null;
		
		try {
			
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		
		} catch(ApplicationBusinessException e) {
			AelMotivoCancelaExamesExceptionCode.AEL_00344.throwException();
		}

		int limite = parametro.getVlrNumerico().intValue();

		//Verifica se o período para deletar é válido
		int diasDesdeCriacao = DateUtil.diffInDaysInteger(new Date(), motivoCancelamento.getCriadoEm());
		
		if (diasDesdeCriacao > limite) {
		
			AelMotivoCancelaExamesExceptionCode.AEL_00343.throwException();
		
		}
		
		getAelMotivoCancelaExamesDAO().remover(motivoCancelamento);
	}
	
	public void persistMotivoCancelamento(AelMotivoCancelaExames motivoCancelamento) throws ApplicationBusinessException {
		if (motivoCancelamento.getSeq() == null) {
			inserirMotivoCancelamento(motivoCancelamento);
		
		} else {
			atualizarMotivoCancelamento(motivoCancelamento);
		}
	}

	/**
	 * ORADB TRIGGER aelk_ael_rn
	 */
	private void atualizarMotivoCancelamento(AelMotivoCancelaExames motivoCancelamento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelMotivoCancelaExames motivoCancelamentoOriginal = getAelMotivoCancelaExamesDAO().obterPeloId(motivoCancelamento.getSeq());
		
		if (!motivoCancelamento.getCriadoEm().equals(motivoCancelamentoOriginal.getCriadoEm()) || 
				!motivoCancelamento.getDescricao().equals(motivoCancelamentoOriginal.getDescricao())){
			throw new ApplicationBusinessException(AelMotivoCancelaExamesExceptionCode.MOTIVO_CANCELAMENTO_ALTERAR_CAMPOS_INVALIDOS);
		}
		
		motivoCancelamento.setSerMatriculaAlterado(servidorLogado.getId().getMatricula());
		motivoCancelamento.setSerVinCodigoAlterado(servidorLogado.getId().getVinCodigo());
		motivoCancelamento.setAlteradoEm(new Date());
		
		getAelMotivoCancelaExamesDAO().merge(motivoCancelamento);
	}

	/**
	 * ORADB TRIGGER aelk_ael_rn
	 * @param motivoCancelamento
	 * @throws ApplicationBusinessException  
	 */
	private void inserirMotivoCancelamento(AelMotivoCancelaExames motivoCancelamento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		motivoCancelamento.setSerMatricula(servidorLogado.getId().getMatricula());
		motivoCancelamento.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		motivoCancelamento.setSerMatriculaAlterado(servidorLogado.getId().getMatricula());
		motivoCancelamento.setSerVinCodigoAlterado(servidorLogado.getId().getVinCodigo());
		motivoCancelamento.setCriadoEm(new Date());
		motivoCancelamento.setAlteradoEm(new Date());
		
		getAelMotivoCancelaExamesDAO().persistir(motivoCancelamento);
		getAelMotivoCancelaExamesDAO().flush();
		
	}
	
	protected AelMotivoCancelaExamesDAO getAelMotivoCancelaExamesDAO(){
		return aelMotivoCancelaExamesDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO(){
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO(){
		return aelExtratoItemSolicitacaoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
