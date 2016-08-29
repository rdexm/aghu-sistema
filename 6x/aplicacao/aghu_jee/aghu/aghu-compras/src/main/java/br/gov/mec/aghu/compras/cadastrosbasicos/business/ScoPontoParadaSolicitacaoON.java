package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoPontoParadaSolicitacaoON extends BaseBusiness {


@EJB
private ScoPontoParadaSolicitacaoRN scoPontoParadaSolicitacaoRN;

	private static final Log LOG = LogFactory.getLog(ScoPontoParadaSolicitacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = -5627260756542865169L;

	public enum ScoPontoParadaSolicitacaoONExceptionCode implements
			BusinessExceptionCode {MENSAGEM_PARAM_OBRIG; }
	
	/**
	 * Método de pesquisa para a página de lista. Pesquisa por código/descrição/situação
	 * @param codigo, descricao, situacao, firstResult, maxResult, orderProperty, asc
	 * @author dilceia.alves
	 * @since 26/10/2012
	 */
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {

		return this.getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaSolicitacao(
				firstResult, maxResult, orderProperty, asc, scoPontoParadaSolicitacao);
	}
	
	/**
	 * Método count de pesquisa para a página de lista. Pesquisa por código/descrição/situação
	 * @param codigo, descricao, situacao
	 * @author dilceia.alves
	 * @since 26/10/2012
	 */
	public Long pesquisarPontoParadaSolicitacaoCount(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) {

		return this.getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaSolicitacaoCount(
				scoPontoParadaSolicitacao);
	}
	
	/**
	 * Obtém o ponto de parada pela chave primária (código).
	 * @param codigo
	 * @author dilceia.alves
	 * @since 29/10/2012
	 */
	public ScoPontoParadaSolicitacao obterPontoParadaSolicitacao(Short codigo){
		return getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(codigo);
	}
	
	/**
	 * Insere ponto de parada 
	 * @param ScoPontoParadaSolicitacao
	 * @author dilceia.alves
	 * @since 29/10/2012
	 * @throws ApplicationBusinessException 
	 */
	public void inserirPontoParadaSolicitacao(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao)
	    throws ApplicationBusinessException {

		if (scoPontoParadaSolicitacao == null) {
			throw new ApplicationBusinessException(ScoPontoParadaSolicitacaoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoPontoParadaSolicitacaoRN().persistir(scoPontoParadaSolicitacao);
	}
	
	/**
	 * Altera ponto de parada
	 * @param ScoPontoParadaSolicitacao
	 * @author dilceia.alves
	 * @since 29/10/2012
	 * @throws ApplicationBusinessException 
	 */
	public void alterarPontoParadaSolicitacao(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, ScoPontoParadaSolicitacao scoPontoParadaSolAnterior) throws ApplicationBusinessException {

		if (scoPontoParadaSolicitacao == null) {
			throw new ApplicationBusinessException(ScoPontoParadaSolicitacaoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		this.getScoPontoParadaSolicitacaoRN().atualizar(scoPontoParadaSolicitacao, scoPontoParadaSolAnterior);
	}
	
	/**
	 * Exclui ponto de parada pelo código
	 * @param ScoPontoParadaSolicitacao
	 * @author dilceia.alves
	 * @since 29/10/2012
	 * @throws ApplicationBusinessException 
	 */
	public void excluirPontoParadaSolicitacao(final Short scoPontoParadaSolicitacao)
			throws ApplicationBusinessException {
		
		this.getScoPontoParadaSolicitacaoRN().remover(scoPontoParadaSolicitacao);
	}
	
	/**
	 * Verifica se o ponto de parada é de comprador
	 * @param pontoDeParada
	 * @return Boolean
	 * @throws ApplicationBusinessException 
	 */
	public Boolean verificarPontoParadaComprador(ScoPontoParadaSolicitacao pontoParada) throws ApplicationBusinessException {		
		return (pontoParada != null) ? pontoParada.getExigeResponsavel(): false;
				
	}
		
	public ScoPontoParadaSolicitacao obterPontoParadaChefia() throws ApplicationBusinessException {
		return this.obterPontoParadaPorTipo(DominioTipoPontoParada.CH);
	}
		
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPermitidosPorCodigoOuDescricao (
			String filtro, Boolean fromLiberacao) throws ApplicationBusinessException {
		RapServidores servidor = getServidorLogadoFacade().obterServidorLogado();
		return this.getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaSolicitacaoPermitidosPorCodigoOuDescricao (filtro, servidor, 
				this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PPS_FILTRO_LIBERACAO).getVlrTexto(), fromLiberacao);
	}
	
	public ScoPontoParadaSolicitacao obterPontoParadaPorTipo(DominioTipoPontoParada tipoPontoParada) {
		return this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(tipoPontoParada);
	}
	
	/**
	 * Envia a Solicitação de Compras para Proximo Ponto Parada.
	 * 
	 * Caso o parametro vRapPessoaServidor seja diferente de null, será direcionado para ORADB - SCOP_ENVIA_SC_COMPRADOR.sql
	 * 
	 * Caso o parametro vRapPessoaServidor seja null, será direcionado para ORADB - SCOP_ENVIA_SC.sql
	 * 
	 * @param solicitacaoDeCompra
	 * @param pontoParadaSelecionado
	 * @throws ApplicationBusinessException 
	 */
	public void enviarSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoPontoParadaSolicitacao pontoParadaDestino)
					throws ApplicationBusinessException {
		getScoPontoParadaSolicitacaoRN().enviarSolicitacaoCompra(solicitacaoDeCompra, pontoParadaDestino);
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO(){
		return scoPontoParadaSolicitacaoDAO;
	}
	
	protected ScoPontoParadaSolicitacaoRN getScoPontoParadaSolicitacaoRN(){
		return scoPontoParadaSolicitacaoRN;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}