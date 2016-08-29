package br.gov.mec.aghu.exames.sismama.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaHistoResDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaHistoResJnDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaHistoRes;
import br.gov.mec.aghu.model.AelSismamaHistoResJn;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responável pelas regras de negócio para 
 * Resultado de Exames Histopatológico - SISMAMA.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class ResultadoExameHistopatologicoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ResultadoExameHistopatologicoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelSismamaHistoResDAO aelSismamaHistoResDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AelSismamaHistoResJnDAO aelSismamaHistoResJnDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	private static final long serialVersionUID = -7504222316248095089L;
	
	public enum ResultadoExameHistopatologicoRNExceptionCode implements BusinessExceptionCode {
		AEL_00353
	}
	
	/**
	 * Insere instância de AelSismamaHistoRes.
	 * 
	 * @throws ApplicationBusinessException  
	 * 
	 */
	public void inserirResultadoExameHistopatologico(AelSismamaHistoRes newSismamaHistoRes) 
			throws ApplicationBusinessException {
		executarAntesInserir(newSismamaHistoRes);
		getAelSismamaHistoResDAO().persistir(newSismamaHistoRes);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_S02_BRI
	 * 
	 * @param newAelSismamaHistoRes
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected void executarAntesInserir(AelSismamaHistoRes newAelSismamaHistoRes)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		newAelSismamaHistoRes.setCriadoEm(new Date());
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(ResultadoExameHistopatologicoRNExceptionCode.AEL_00353);
		}
		
		newAelSismamaHistoRes.setServidor(servidorLogado);
	}
	
	/**
	 * Atualiza instância de AelSismamaHistoRes.
	 * 
	 * @param newSismamaHistoRes
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarResultadoExameHistopatologico(
			AelSismamaHistoRes newSismamaHistoRes) throws ApplicationBusinessException {
		AelSismamaHistoResDAO sismamaHistoResDAO = getAelSismamaHistoResDAO();
		AelSismamaHistoRes oldSismamaHistoRes = sismamaHistoResDAO.obterAelSismamaHistoResPorChavePrimaria(newSismamaHistoRes.getSeq());  //obterOriginal(newSismamaHistoRes);
		sismamaHistoResDAO.merge(newSismamaHistoRes);
		executarAposAtualizar(oldSismamaHistoRes, newSismamaHistoRes);
	}

	/**
	 * Trigger
	 * 
	 * ORADB: AELT_S02_ARU
	 * 
	 * @param oldSismamaHistoRes
	 * @param newSismamaHistoRes
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAposAtualizar(AelSismamaHistoRes oldSismamaHistoRes,
			AelSismamaHistoRes newSismamaHistoRes) throws ApplicationBusinessException {
		if (CoreUtil.modificados(newSismamaHistoRes.getSeq(), oldSismamaHistoRes.getSeq())
				|| CoreUtil.modificados(
						newSismamaHistoRes.getSismamaHistoCad(),
						oldSismamaHistoRes.getSismamaHistoCad())
				|| CoreUtil.modificados(newSismamaHistoRes.getResposta(),
						oldSismamaHistoRes.getResposta())
				|| CoreUtil.modificados(
						newSismamaHistoRes.getItemSolicitacaoExame(),
						oldSismamaHistoRes.getItemSolicitacaoExame())) {

			inserirJournal(oldSismamaHistoRes, DominioOperacoesJournal.UPD);
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_S02_ARD
	 * 
	 * @param oldSismamaHistoRes
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAposRemover(AelSismamaHistoRes oldSismamaHistoRes) throws ApplicationBusinessException {
		inserirJournal(oldSismamaHistoRes, DominioOperacoesJournal.DEL);
	}
	
	private void inserirJournal(AelSismamaHistoRes oldSismamaHistoRes,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelItemSolicitacaoExames itemSolicitacaoExame = oldSismamaHistoRes.getItemSolicitacaoExame();
		AelSismamaHistoResJn sismamaHistoResJn = BaseJournalFactory.getBaseJournal(operacao, AelSismamaHistoResJn.class, servidorLogado.getUsuario());
		sismamaHistoResJn.setSeq(oldSismamaHistoRes.getSeq());
		sismamaHistoResJn.setS01Codigo(oldSismamaHistoRes.getSismamaHistoCad().getCodigo());
		sismamaHistoResJn.setResposta(oldSismamaHistoRes.getResposta());
		sismamaHistoResJn.setIseSoeSeq(itemSolicitacaoExame.getId().getSoeSeq());
		sismamaHistoResJn.setIseSeqp(itemSolicitacaoExame.getId().getSeqp());
		getAelSismamaHistoResJnDAO().persistir(sismamaHistoResJn);
	}

	/**
	 * Verifica se existem exames relacionados a SISMAMA em uma
	 * determinada solicitacao de exame (iseSeqp), retornando
	 * true caso afirmativo e false caso contrário.
	 * 
	 * ORADB: AELC_EXAME_SISMAMA
	 * 
	 * @param numeroAp
	 * @param iseSeqp
	 * @param descricaoSismama
	 * @param lu2Seq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarExameSismama(Long numeroAp, Short iseSeqp, String descricaoSismama, Integer lu2Seq) throws ApplicationBusinessException {
		List<AelItemSolicitacaoExames> listaItemSolicitacaoExame = getAelItemSolicitacaoExameDAO()
				.pesquisarItemSolicitacaoExamePorNumeroApDescricaoEIseSeqp(numeroAp, iseSeqp, descricaoSismama, lu2Seq);
		
		if (listaItemSolicitacaoExame.isEmpty()) {
			return Boolean.FALSE;
		}
		
		AelSolicitacaoExames solicitacaoExame = null;
		solicitacaoExame = listaItemSolicitacaoExame.get(0).getSolicitacaoExame();
		
		if (solicitacaoExame == null) {
			return Boolean.FALSE;
		}
		
		AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
		if (atendimento != null) {
			FatConvenioSaudePlano convenioSaudePlano = atendimento.getConvenioSaudePlano();
			Short codigoConvenioSaude = convenioSaudePlano.getConvenioSaude().getCodigo();
			Long countConvenioSaude = getFaturamentoFacade()
					.obterCountConvenioSaudeAtivoPorPgdSeq(codigoConvenioSaude); 
			if (countConvenioSaude > 0) {
				if (getExamesFacade().obterOrigemIg(solicitacaoExame).equals("AMB")) {
					return Boolean.TRUE;
				}
			} else {
				return Boolean.FALSE;
			}
		}
		
		return Boolean.FALSE;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}	
	
	protected AelSismamaHistoResDAO getAelSismamaHistoResDAO() {
		return aelSismamaHistoResDAO;
	}

	protected AelSismamaHistoResJnDAO getAelSismamaHistoResJnDAO() {
		return aelSismamaHistoResJnDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}		

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
