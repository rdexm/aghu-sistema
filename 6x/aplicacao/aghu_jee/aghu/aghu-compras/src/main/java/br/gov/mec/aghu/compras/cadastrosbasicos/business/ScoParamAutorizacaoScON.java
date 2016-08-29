package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoParamAutorizacaoScDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParamAutorizacaoSc;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoParamAutorizacaoScON extends BaseBusiness {


@EJB
private ScoParamAutorizacaoScRN scoParamAutorizacaoScRN;

	private static final Log LOG = LogFactory.getLog(ScoParamAutorizacaoScON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private ScoParamAutorizacaoScDAO scoParamAutorizacaoScDAO;

	private static final long serialVersionUID = 9005252892033561011L;

	public enum ScoParamAutorizacaoScONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG, MENSAGEM_RN1_M03_PARAM_AUTORIZACAO_SC, MENSAGEM_RN2_M04_PARAM_AUTORIZACAO_SC, MENSAGEM_RN3_M05_PARAM_AUTORIZACAO_SC, MENSAGEM_RN3_M06_PARAM_AUTORIZACAO_SC;
	}
	
	/**
	 * Método de pesquisa para a página de lista.
	 * @param cCSolicitante, cCAplicacao, solicitante, situacao, firstResult, maxResult, orderProperty, asc
	 * @author dilceia.alves
	 * @since 16/11/2012
	 */
	public List<ScoParamAutorizacaoSc> pesquisarParamAutorizacaoSc(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		return this.getScoParamAutorizacaoScDAO().pesquisarParamAutorizacaoSc(
				firstResult, maxResult, orderProperty, asc, scoParamAutorizacaoSc);
	}
	
	/**
	 * Método count de pesquisa para a página de lista. 
	 * @param cCSolicitante, cCAplicacao, solicitante, situacao
	 * @author dilceia.alves
	 * @since 16/11/2012
	 */
	public Long pesquisarParamAutorizacaoScCount(ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		return this.getScoParamAutorizacaoScDAO().pesquisarParamAutorizacaoScCount(
				scoParamAutorizacaoSc);
	}
	
	/**
	 * Obtém o parâmetro de autorizacao da SC pela chave primária (seq).
	 * @param seq
	 * @author dilceia.alves
	 * @since 16/11/2012
	 */
	public ScoParamAutorizacaoSc obterParamAutorizacaoSc(Integer seq){
		return getScoParamAutorizacaoScDAO().obterParametrosAutorizacaoSC(seq);
	}
	
	public ScoParamAutorizacaoSc obterParametrosAutorizacaoSCPrioridade(FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicacao,
			RapServidores servidor){
		return getScoParamAutorizacaoScDAO().obterParametrosAutorizacaoSCPrioridade(centroCusto, centroCustoAplicacao, servidor);
	}
	
	/**
	 * Insere parâmetro de autorizacao da SC
	 * @param ScoParamAutorizacaoSc
	 * @author dilceia.alves
	 * @since 16/11/2012
	 * @throws ApplicationBusinessException 
	 */
	public void inserirParamAutorizacaoSc(ScoParamAutorizacaoSc scoParamAutorizacaoSc)
	    throws ApplicationBusinessException {

		if (scoParamAutorizacaoSc == null) {
			throw new ApplicationBusinessException(
					ScoParamAutorizacaoScONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		// RN1
		if (scoParamAutorizacaoSc.getPontoParada() == null && scoParamAutorizacaoSc.getPontoParadaProxima() == null
				&& scoParamAutorizacaoSc.getServidorAutoriza() == null && scoParamAutorizacaoSc.getServidorCompra() == null){
			throw new ApplicationBusinessException(
					ScoParamAutorizacaoScONExceptionCode.MENSAGEM_RN1_M03_PARAM_AUTORIZACAO_SC);
		}
		
		// RN2
		if (scoParamAutorizacaoSc.getIndSituacao() == DominioSituacao.A){
			ScoParamAutorizacaoSc paramResult = this.getScoParamAutorizacaoScDAO().pesquisarParamAutorizacao(scoParamAutorizacaoSc);
			
			if (paramResult != null && paramResult.getSeq() != null){
					throw new ApplicationBusinessException(
					ScoParamAutorizacaoScONExceptionCode.MENSAGEM_RN2_M04_PARAM_AUTORIZACAO_SC);
			}		
		}
		
		// RN3
		if (scoParamAutorizacaoSc.getPontoParada() != null && scoParamAutorizacaoSc.getPontoParadaProxima() == null){
			throw new ApplicationBusinessException(
					ScoParamAutorizacaoScONExceptionCode.MENSAGEM_RN3_M05_PARAM_AUTORIZACAO_SC);
		}
		
		if (scoParamAutorizacaoSc.getPontoParadaProxima() != null && scoParamAutorizacaoSc.getPontoParada() == null){
			throw new ApplicationBusinessException(
					ScoParamAutorizacaoScONExceptionCode.MENSAGEM_RN3_M06_PARAM_AUTORIZACAO_SC);
		}

        //Obtém novamente o servidor comprador, devido este ter sido carregado através de uma view (V_RAP_SERVIDORES)				
		RapServidores comprador = null;
		if (scoParamAutorizacaoSc.getServidorCompra() != null) {
			comprador = this.getRegistroColaboradorFacade().obterServidor(scoParamAutorizacaoSc.getServidorCompra());
			scoParamAutorizacaoSc.setServidorCompra(comprador);
		}		
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		scoParamAutorizacaoSc.setServidorCriacao(servidorLogado);
		scoParamAutorizacaoSc.setCriadoEm(new Date());  
		this.getScoParamAutorizacaoScRN().persistir(scoParamAutorizacaoSc);
	}
	
	/**
	 * Altera parâmetro de autorizacao da SC
	 * @param ScoParamAutorizacaoSc
	 * @author dilceia.alves
	 * @since 16/11/2012
	 * @throws ApplicationBusinessException 
	 */
	public void alterarParamAutorizacaoSc(ScoParamAutorizacaoSc scoParamAutorizacaoSc)
		throws ApplicationBusinessException {
	
		if (scoParamAutorizacaoSc == null) {
			throw new ApplicationBusinessException(
					ScoParamAutorizacaoScONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		// RN1
		if (scoParamAutorizacaoSc.getPontoParada() == null && scoParamAutorizacaoSc.getPontoParadaProxima() == null
				&& scoParamAutorizacaoSc.getServidorAutoriza() == null && scoParamAutorizacaoSc.getServidorCompra() == null){
			throw new ApplicationBusinessException(
					ScoParamAutorizacaoScONExceptionCode.MENSAGEM_RN1_M03_PARAM_AUTORIZACAO_SC);
		}
		
		// RN2
		if (scoParamAutorizacaoSc.getIndSituacao() == DominioSituacao.A){
			
			ScoParamAutorizacaoSc paramResult = this.getScoParamAutorizacaoScDAO().pesquisarParamAutorizacao(scoParamAutorizacaoSc);
			
			if (paramResult != null){
				if (!paramResult.getSeq().equals(scoParamAutorizacaoSc.getSeq())){
					throw new ApplicationBusinessException(
					ScoParamAutorizacaoScONExceptionCode.MENSAGEM_RN2_M04_PARAM_AUTORIZACAO_SC);
				}
			}		
		}
		
		// RN3
		if (scoParamAutorizacaoSc.getPontoParada() != null && scoParamAutorizacaoSc.getPontoParadaProxima() == null){
			throw new ApplicationBusinessException(
					ScoParamAutorizacaoScONExceptionCode.MENSAGEM_RN3_M05_PARAM_AUTORIZACAO_SC);
		}
		
		if (scoParamAutorizacaoSc.getPontoParadaProxima() != null && scoParamAutorizacaoSc.getPontoParada() == null){
			throw new ApplicationBusinessException(
					ScoParamAutorizacaoScONExceptionCode.MENSAGEM_RN3_M06_PARAM_AUTORIZACAO_SC);
		}
		
        //Obtém novamente o servidor comprador, devido este ter sido carregado através de uma view (V_RAP_SERVIDORES)				
		RapServidores comprador = null;
		if (scoParamAutorizacaoSc.getServidorCompra() != null) {
			comprador = this.getRegistroColaboradorFacade().obterServidor(scoParamAutorizacaoSc.getServidorCompra());
			scoParamAutorizacaoSc.setServidorCompra(comprador);
		}		
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		scoParamAutorizacaoSc.setServidorAlteracao(servidorLogado);
		this.getScoParamAutorizacaoScRN().atualizar(scoParamAutorizacaoSc);
	}
	
	protected ScoParamAutorizacaoScDAO getScoParamAutorizacaoScDAO(){
		return scoParamAutorizacaoScDAO;
	}
	
	protected ScoParamAutorizacaoScRN getScoParamAutorizacaoScRN(){
		return scoParamAutorizacaoScRN;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}