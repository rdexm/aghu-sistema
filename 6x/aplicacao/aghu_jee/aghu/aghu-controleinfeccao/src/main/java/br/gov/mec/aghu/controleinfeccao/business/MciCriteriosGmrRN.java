package br.gov.mec.aghu.controleinfeccao.business;	
	
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.controleinfeccao.dao.MciCriterioGmrDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciCriterioGmrJnDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MciCriterioGmr;
import br.gov.mec.aghu.model.MciCriterioGmrId;
import br.gov.mec.aghu.model.MciCriterioGmrJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciCriteriosGmrRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(MciCriteriosGmrRN.class);
	
@Override
@Deprecated
protected Log getLogger() {
	return LOG;
}

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

@EJB
private IParametroFacade parametroFacade;

@Inject 
private MciCriterioGmrDAO mciCriteriosGmrDAO;

@Inject
private MciCriterioGmrJnDAO mciCriterioGmrJnDAO;

//@Inject 
//private MciNotificacaoGmrDAO mciNotificacaoGmrDAO;	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1053255394476553480L;

	private enum ManterCriteriosGMRExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_CRITERIO_JA_EXISTENTE, MENSAGEM_ERRO_PERIODO_EXCLUSAO_CRITERIO, MENSAGEM_CRITERIO_RESTRICAO_EXCLUSAO, MENSAGEM_CRITERIO_RESTRICAO_NOTIFICACOES;
	}
	
	public void persistirMciCriteriosGMR(MciCriterioGmr criterio, Integer ambSeq, Integer bmrSeq) throws ApplicationBusinessException{
		if (criterio.getId() == null) {	
			criterio.setId(new MciCriterioGmrId(bmrSeq, ambSeq));
			inserir(criterio);
		} else {
			atualizar(criterio);
		}
	}
	
	//RN03
	public void inserir(MciCriterioGmr criterio) throws ApplicationBusinessException{
		MciCriterioGmr criterioOriginal = this.mciCriteriosGmrDAO.obterCriterioGmrPeloId(criterio.getId());
		
		if (criterioOriginal != null) {
			throw new ApplicationBusinessException(ManterCriteriosGMRExceptionCode.MENSAGEM_ERRO_CRITERIO_JA_EXISTENTE);
		} else {
			criterio.setCriadoEm(new Date());
			criterio.setServidor(servidorLogadoFacade.obterServidorLogado());
			mciCriteriosGmrDAO.persistir(criterio);
//			persistirMciCriteriosGmrJournal(criterioOriginal, DominioOperacoesJournal.INS);
		}
	}	
	
	public void atualizar(MciCriterioGmr criterio) throws ApplicationBusinessException{
		MciCriterioGmr criterioOriginal = this.mciCriteriosGmrDAO.obterCriterioGmrPeloId(criterio.getId());
		criterio.setAlteradoEm(new Date());
		criterio.setServidorMovimentado(servidorLogadoFacade.obterServidorLogado());
		
		if(CoreUtil.modificados(criterio.getId().getAmbSeq(), criterioOriginal.getId().getAmbSeq()) || 
				CoreUtil.modificados(criterio.getId().getBmrSeq(), criterioOriginal.getId().getBmrSeq()) || 
				CoreUtil.modificados(criterio.getAlteradoEm(), criterioOriginal.getAlteradoEm()) ||
				CoreUtil.modificados(criterio.getCriadoEm(), criterioOriginal.getCriadoEm()) || 
				CoreUtil.modificados(criterio.getServidor(), criterioOriginal.getServidor()) ||
				CoreUtil.modificados(criterio.getServidorMovimentado(), criterioOriginal.getServidorMovimentado()) || 
				CoreUtil.modificados(criterio.getSituacao(), criterioOriginal.getServidorMovimentado()) || 
				CoreUtil.modificados(criterio.getVersion(), criterioOriginal.getVersion())) {
			persistirMciCriteriosGmrJournal(criterioOriginal, DominioOperacoesJournal.UPD);
		}
		mciCriteriosGmrDAO.merge(criterio);
	}
	
	private void persistirMciCriteriosGmrJournal(MciCriterioGmr criterio, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		final MciCriterioGmrJn journal = BaseJournalFactory.getBaseJournal(operacao, MciCriterioGmrJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setAmbSeq(criterio.getId().getAmbSeq());
		journal.setBmrSeq(criterio.getId().getBmrSeq());
		journal.setSituacao(criterio.getSituacao());
		journal.setCriadoEm(criterio.getCriadoEm());
		journal.setAlteradoEm(criterio.getAlteradoEm());
		journal.setSerMatricula(criterio.getServidor().getId().getMatricula());
		journal.setSerVinCodigo(criterio.getServidor().getId().getVinCodigo());
		
		if (criterio.getServidorMovimentado() != null) {
			journal.setSerMatriculaMovimentado(criterio.getServidor().getId().getMatricula());
			journal.setSerVinCodigoMovimentado(criterio.getServidor().getId().getVinCodigo());	
		}
		
		mciCriterioGmrJnDAO.persistir(journal);
	}
	
	//RN01
	public void excluir(Integer ambSeq, Integer bmrSeq) throws ApplicationBusinessException, BaseListException {
		MciCriterioGmrId id = new MciCriterioGmrId(bmrSeq, ambSeq);
		MciCriterioGmr criterio = mciCriteriosGmrDAO.obterCriterioGmrPeloId(id);
		
//		Removida a regra, conforme e-mail enviado por Rejane Audy, no dia 30/01/2015 para Michella Michelli, Jean Lau e Filipe Hoffmeister.
//		verificarRestricaoExclusao(ambSeq);
		
		validarPeriodoExclusao(criterio);
		
		mciCriteriosGmrDAO.remover(criterio);
		persistirMciCriteriosGmrJournal(criterio, DominioOperacoesJournal.DEL);
	}
	
	//RN04
	public void validarPeriodoExclusao(MciCriterioGmr criterio) throws ApplicationBusinessException{
		AghParametros param = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		Float qtDias = CoreUtil.diferencaEntreDatasEmDias(new Date(), criterio.getCriadoEm());
		
		if (qtDias > param.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(ManterCriteriosGMRExceptionCode.MENSAGEM_ERRO_PERIODO_EXCLUSAO_CRITERIO);
		}
	}	
	
//	private void verificarRestricaoExclusao(Integer ambSeq) throws BaseListException {
//		List<MciNotificacaoGmr> notificacoes = this.mciNotificacaoGmrDAO.pesquisarNotificacaoGrmPorAmbSeq(ambSeq);
//		BaseListException listaDeErros = new BaseListException();
//		listaDeErros.add(new ApplicationBusinessException(ManterCriteriosGMRExceptionCode.MENSAGEM_CRITERIO_RESTRICAO_EXCLUSAO));
//			
//		if (notificacoes.size() > 0) {
//			for (MciNotificacaoGmr item : notificacoes) {
//				listaDeErros.add(new ApplicationBusinessException(ManterCriteriosGMRExceptionCode.MENSAGEM_CRITERIO_RESTRICAO_NOTIFICACOES, item.getSeq()));
//			}
//			
//			if (listaDeErros.hasException()) {
//				throw listaDeErros;
//			}
//		}	
//	}
	
	public void validarCriterioDuplicado(MciCriterioGmr criterio) throws ApplicationBusinessException {
		MciCriterioGmr criterioOriginal = this.mciCriteriosGmrDAO.obterCriterioGmrPeloId(criterio.getId());
		
		if(criterioOriginal != null) {
			throw new ApplicationBusinessException(ManterCriteriosGMRExceptionCode.MENSAGEM_ERRO_CRITERIO_JA_EXISTENTE);
		}
	}
	
}
