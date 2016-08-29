package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.ScoPontoParadaSolicitacaoON.ScoPontoParadaSolicitacaoONExceptionCode;
import br.gov.mec.aghu.compras.dao.ScoCaminhoSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoServidorDAO;
import br.gov.mec.aghu.compras.dao.ScoPtParadaSolJnDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPtParadaSolJn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ScoPontoParadaSolicitacaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoPontoParadaSolicitacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@Inject
	private ScoPtParadaSolJnDAO scoPtParadaSolJnDAO;
	
	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;
	
	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private ScoCaminhoSolicitacaoDAO scoCaminhoSolicitacaoDAO;
	
	@Inject
	private ScoPontoServidorDAO scoPontoServidorDAO;

	private static final long serialVersionUID = -8880591796929105302L;

	public enum ScoPontoParadaSolicitacaoRNExceptionCode implements BusinessExceptionCode{
		MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_SOLICITACAO_COMPRA,
		MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_PONTO_SERVIDOR,
		MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_SOLICITACAO_SERVICO,
		MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_CAMINHO_SOLICITACAO,
		MENSAGEM_ERRO_PERSISTIR_DADOS, MENSAGEM_DESCRICAO_PONTO_PARADA_SOLIC_DUPLICADO;
	}
	
	public void persistir(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao)
	    throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (scoPontoParadaSolicitacao == null || servidorLogado == null) {
			throw new ApplicationBusinessException(ScoPontoParadaSolicitacaoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.validarPontoParadaDescricao(scoPontoParadaSolicitacao);
		
		scoPontoParadaSolicitacao.setServidor(servidorLogado);
		scoPontoParadaSolicitacao.setCriadoEm(new Date());  
		this.getScoPontoParadaSolicitacaoDAO().persistir(scoPontoParadaSolicitacao);
	}
	
	public void atualizar(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, ScoPontoParadaSolicitacao scoPontoParadaSolAnterior) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (scoPontoParadaSolicitacao == null || scoPontoParadaSolAnterior == null || servidorLogado == null) {
			throw new ApplicationBusinessException(ScoPontoParadaSolicitacaoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		this.validarPontoParadaDescricao(scoPontoParadaSolicitacao);

		scoPontoParadaSolicitacao.setServidor(servidorLogado);
		// palhativo para evitar org.hibernate.PropertyValueException: not-null
		// property references a null enquanto a base de dados não é alterada
		if (scoPontoParadaSolicitacao.getCriadoEm() == null) {
			scoPontoParadaSolicitacao.setCriadoEm(new Date());
		}
		this.getScoPontoParadaSolicitacaoDAO().merge(scoPontoParadaSolicitacao);
		
		// inserir o histórico
		try {
			this.posAlterar(scoPontoParadaSolAnterior);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(ScoPontoParadaSolicitacaoRNExceptionCode.MENSAGEM_ERRO_PERSISTIR_DADOS);
		}
	}
	
	/**
	 * @param servidor 
	 * @ORADB ScoPtParadaSolJn	Operation:UPD
	 * @throws BaseException
	 */
	public void posAlterar(ScoPontoParadaSolicitacao scoPontoParadaSolicitacaoOld) throws BaseException {
		this.inserirJn(scoPontoParadaSolicitacaoOld, DominioOperacoesJournal.UPD);
	}

	private void inserirJn(final ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, final DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final ScoPtParadaSolJn jn = BaseJournalFactory.getBaseJournal(operacao, ScoPtParadaSolJn.class, servidorLogado.getUsuario());

		jn.setCodigo(scoPontoParadaSolicitacao.getCodigo());
		jn.setDescricao(scoPontoParadaSolicitacao.getDescricao());
		if (scoPontoParadaSolicitacao.getCriadoEm() != null) {
			jn.setCriadoEm(scoPontoParadaSolicitacao.getCriadoEm());
		} else {
			jn.setCriadoEm(new Date());
		}
		jn.setSituacao(scoPontoParadaSolicitacao.getSituacao());
		getScoPtParadaSolJnDAO().persistir(jn);
	}
	
	public void remover(final Short codigo) throws ApplicationBusinessException {
		
		ScoPontoParadaSolicitacao scoPontoParadaSolicitacao = scoPontoParadaSolicitacaoDAO.obterPorChavePrimaria(codigo);
		
		if (scoPontoParadaSolicitacao == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
     	//Aplica RN's para exclusão física do ponto de parada
		validarExclusaoPontoParada(scoPontoParadaSolicitacao);
		
		//Efetua a exclusão
		this.getScoPontoParadaSolicitacaoDAO().remover(scoPontoParadaSolicitacao);
		this.inserirJn(scoPontoParadaSolicitacao, DominioOperacoesJournal.DEL);
	}
	
	public void validarPontoParadaDescricao(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) 
			throws ApplicationBusinessException {

		if (!this.getScoPontoParadaSolicitacaoDAO().pesquisarPontoParadaSolicitacaoPorDescricao(scoPontoParadaSolicitacao)) {
			throw new ApplicationBusinessException(
					ScoPontoParadaSolicitacaoRNExceptionCode.MENSAGEM_DESCRICAO_PONTO_PARADA_SOLIC_DUPLICADO);
		}
	}
	
	/**
	 * Verifica se a exclusão deste registro pode ser efetuada. 
	 * @param scoPontoParadaSolicitacao
	 * @author dilceia.alves	
	 * @since 31/10/2012
	 */
	public void validarExclusaoPontoParada(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao) 
			throws ApplicationBusinessException {

		//Tabela Sco_solicitacoes_de_compras - Coluna PPS_CODIGO
		Long qtdeSolicitacaoCompra = this.getScoSolicitacoesDeComprasDAO()
				.pesquisarSolicitacaoDeCompraPorPpsCodigoCount(scoPontoParadaSolicitacao.getCodigo());
		if (qtdeSolicitacaoCompra > 0) {
			throw new ApplicationBusinessException(
					ScoPontoParadaSolicitacaoRNExceptionCode.MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_SOLICITACAO_COMPRA);
		}
		
		//Tabela Sco_solicitacoes_de_compras - Coluna PPS_CODIGO_LOC_PROXIMA
		Long qtdeSolicitacaoCompraII = this.getScoSolicitacoesDeComprasDAO()
				.pesquisarSolicitacaoDeCompraPorPpsCodigoLocProximaCount(scoPontoParadaSolicitacao.getCodigo());
		if (qtdeSolicitacaoCompraII > 0) {
			throw new ApplicationBusinessException(
					ScoPontoParadaSolicitacaoRNExceptionCode.MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_SOLICITACAO_COMPRA);
		}

		//Tabela Sco_pontos_servidores - Coluna PPS_CODIGO
		Long qtdePontoServidor = this.getScoPontoServidorDAO()
				.pesquisarPontoServidorPorPpsCodigoCount(scoPontoParadaSolicitacao.getCodigo());
		if (qtdePontoServidor > 0) {
			throw new ApplicationBusinessException(
					ScoPontoParadaSolicitacaoRNExceptionCode.MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_PONTO_SERVIDOR);
		}
		
		//Tabela Sco_solicitacoes_servico - Coluna PPS_CODIGO
		Long qtdeSolicitacaoServico = this.getScoSolicitacaoServicoDAO()
			    .pesquisarSolicitacaoServicoPorPpsCodigoCount(scoPontoParadaSolicitacao.getCodigo());
		if (qtdeSolicitacaoServico > 0) {
			throw new ApplicationBusinessException(
					ScoPontoParadaSolicitacaoRNExceptionCode.MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_SOLICITACAO_SERVICO);
		}
		
		//Tabela Sco_solicitacoes_servico - Coluna PPS_CODIGO_LOC_ATUAL
		Long qtdeSolicitacaoServicoII = this.getScoSolicitacaoServicoDAO()
				.pesquisarSolicitacaoServicoPorPpsCodigoLocAtualCount(scoPontoParadaSolicitacao.getCodigo());
		if (qtdeSolicitacaoServicoII > 0) {
			throw new ApplicationBusinessException(
					ScoPontoParadaSolicitacaoRNExceptionCode.MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_SOLICITACAO_SERVICO);
		}
	
		//Tabela Sco_caminhos_solicitacoes - Coluna PPS_CODIGO
		Long qtdeCaminhoSolic = this.getScoCaminhoSolicitacaoDAO()
				.pesquisarCaminhoSolicitacaoPorPpsCodigoCount(scoPontoParadaSolicitacao.getCodigo());
		if (qtdeCaminhoSolic > 0) {
			throw new ApplicationBusinessException(
					ScoPontoParadaSolicitacaoRNExceptionCode.MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_CAMINHO_SOLICITACAO);
		}

		//Tabela Sco_caminhos_solicitacoes - Coluna PPS_CODIGO_INICIO
		Long qtdeCaminhoSolicII = this.getScoCaminhoSolicitacaoDAO()
				.pesquisarCaminhoSolicitacaoPorPpsCodigoInicioCount(scoPontoParadaSolicitacao.getCodigo());
		if (qtdeCaminhoSolicII > 0) {
			throw new ApplicationBusinessException(
					ScoPontoParadaSolicitacaoRNExceptionCode.MENSAGEM_EXCLUIR_PONTO_PARADA_SOLIC_CAMINHO_SOLICITACAO);
		}
	}
	
	/**
	 * @ORADB - SCOP_ENVIA_SC_COMPRADOR.sql  
	 * @param solicitacaoDeCompra
	 * @param pontoParadaSelecionado
	 * @param vRapPessoaServidor
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void enviarSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra, 
			ScoPontoParadaSolicitacao pontoParadaDestino)
					throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (solicitacaoDeCompra.getNumero() != null) {
			solicitacaoDeCompra.setPontoParadaProxima(pontoParadaDestino);			
			solicitacaoDeCompra.setServidorCompra(servidorLogado);
		}
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO(){
		return scoPontoParadaSolicitacaoDAO;
	}
	
	protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO(){
		return scoSolicitacoesDeComprasDAO;
	}
	
	protected ScoPontoServidorDAO getScoPontoServidorDAO(){
		return scoPontoServidorDAO;
	}
	
	protected ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO(){
		return scoSolicitacaoServicoDAO;
	}

	protected ScoCaminhoSolicitacaoDAO getScoCaminhoSolicitacaoDAO(){
		return scoCaminhoSolicitacaoDAO;
	}
	
	protected ScoPtParadaSolJnDAO getScoPtParadaSolJnDAO(){
		return scoPtParadaSolJnDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
