package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoAutorizacaoFornON extends BaseBusiness {

	@EJB
	private ScoAutorizacaoFornJnRN scoAutorizacaoFornJnRN;
	
	@EJB
	private ScoAutorizacaoFornRN scoAutorizacaoFornRN;
	
	@EJB
	private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;
	
	@EJB
	private ScoSolicitacaoProgramacaoEntregaRN scoSolicitacaoProgramacaoEntregaRN;
	
	@EJB
	private ScoItemAutorizacaoFornJnRN scoItemAutorizacaoFornJnRN;
	
	private static final Log LOG = LogFactory.getLog(ScoAutorizacaoFornON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ScoItemAutorizacaoFornJnDAO scoItemAutorizacaoFornJnDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoAutorizacaoFornJnDAO scoAutorizacaoFornJnDAO;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -464829745471165825L;

	public enum ScoAutorizacaoFornONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_ALTERACAO_PREVISAO_ENTREGA, MENSAGEM_ERRO_EXCLUSAO_SEQUENCIA_ZERO,MENSAGEM_EXCSEQ_NR, MENSAGEM_ERRO_DADOS_CONVERSAO_AF,
		MENSAGEM_ERRO_LIBERACAO_AF;
	}
	
	/**
	 * Verifica se a data de previsão de entrega foi alterada para ruma data anterior a hoje
	 * @param autorizacaoFornecimento
	 * @param afOriginal
	 * @throws ApplicationBusinessException
	 */
	public void validarDataPrevisaoEntrega(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoForn afOriginal) throws ApplicationBusinessException {
		if (autorizacaoFornecimento.getDtPrevEntrega() != null) {			 
			if (afOriginal != null && !Objects.equals(autorizacaoFornecimento.getDtPrevEntrega(), obterDataTruncada(afOriginal.getDtPrevEntrega()))) {
				if (autorizacaoFornecimento.getDtPrevEntrega().before(new Date())) {
					throw new ApplicationBusinessException(ScoAutorizacaoFornONExceptionCode.MENSAGEM_ERRO_ALTERACAO_PREVISAO_ENTREGA);
				}	
			}
		}
	}
	
	/**
	 * Remove a hora de uma data
	 * @param data
	 * @return Date
	 */
	public Date obterDataTruncada(Date data) {
		Date dataTruncada = null;
		
		if (data != null) {
			dataTruncada = DateUtils.truncate(data, Calendar.DATE);
		}
		
		return dataTruncada;
	}
	
	/**
	 * Converte a unidade de medida da AF para a unidade de medida padrão do hospital
	 * @param autorizacaoFornecimento
	 * @return
	 * @throws BaseException 
	 */
	public Integer converterUnidadeAf(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException {
		Integer qtdConversao = 0;
		ScoFornecedor fornecedorPadrao = this.getComprasFacade().obterFornecedorPorChavePrimaria(this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO)
				.getVlrNumerico().intValue());
		
		if (fornecedorPadrao != null) {
			List<ScoItemAutorizacaoForn> listaItens = this.getScoItemAutorizacaoFornDAO().pesquisarItemAfConversaoUnidade(autorizacaoFornecimento.getNumero(), fornecedorPadrao);
			
			for (ScoItemAutorizacaoForn item : listaItens) {
				ScoUnidadeMedida unidadeMedidaEstoque = item.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra().getMaterial().getEstoquesAlmoxarifado().iterator().next().getUnidadeMedida(); 	
				Integer fatorConversaoAf = Integer.valueOf(item.getFatorConversao());	
				if (item.getUnidadeMedida().equals(unidadeMedidaEstoque) && item.getFatorConversao() > 1  ||
						!item.getUnidadeMedida().equals(unidadeMedidaEstoque) && item.getFatorConversao() == 1) {
					throw new ApplicationBusinessException(ScoAutorizacaoFornONExceptionCode.MENSAGEM_ERRO_DADOS_CONVERSAO_AF, 
							unidadeMedidaEstoque.getCodigo(), item.getUnidadeMedida().getCodigo(), item.getFatorConversao(), item.getId().getNumero());
				}				
				
				if (item.getFatorConversao() > 1) {
					item.setUmdCodigoForn(item.getUnidadeMedida());
					item.setFatorConversaoForn(fatorConversaoAf);
					item.setQtdeSolicitada(item.getQtdeSolicitada()*fatorConversaoAf);
					item.setQtdeRecebida(obterValorDefault(item.getQtdeRecebida()) *fatorConversaoAf);
					item.setValorUnitario(item.getValorUnitario()/fatorConversaoAf);
					item.setUnidadeMedida(unidadeMedidaEstoque);
					item.setFatorConversao(1);
					this.getScoItemAutorizacaoFornRN().atualizarItemAutorizacaoFornecimento(item);
					
					ScoItemAutorizacaoFornJn itemJn = this.getScoItemAutorizacaoFornJnDAO().obterItemAfJnPorSequenciaAlteracao(item.getId().getAfnNumero(), item.getId().getNumero(), null);
					if (itemJn != null) {
						itemJn.setQtdeSolicitada(itemJn.getQtdeSolicitada()*fatorConversaoAf);
						itemJn.setQtdeRecebida(obterValorDefault(itemJn.getQtdeRecebida()) *fatorConversaoAf);
						itemJn.setValorUnitario(itemJn.getValorUnitario() / fatorConversaoAf);
						itemJn.setUnidadeMedida(unidadeMedidaEstoque);
						itemJn.setFatorConversao(1);
						this.getScoItemAutorizacaoFornJnRN().persistirItemAutorizacaoFornecimentoJn(itemJn);
					}
					
					List<ScoProgEntregaItemAutorizacaoFornecimento> listaProgEntregas = this.getComprasFacade().pesquisaProgEntregaItemAf(item.getId().getAfnNumero(), item.getId().getNumero(), false, true);
					
					for (ScoProgEntregaItemAutorizacaoFornecimento itemPea : listaProgEntregas) {

						itemPea.setQtde(obterValorDefault(itemPea.getQtde()) * fatorConversaoAf);
						itemPea.setQtdeEntregue(obterValorDefault(itemPea.getQtdeEntregue()) * fatorConversaoAf);
						itemPea.setIndConversaoUnidade(true);
						
						this.getComprasFacade().persistirProgramacaoEntregaAf(itemPea);
					}
					
					List<ScoSolicitacaoProgramacaoEntrega> listaSolProg = this.getScoSolicitacaoProgramacaoEntregaDAO().pesquisarSolicitacaoProgEntregaPorItemAf(item.getId().getAfnNumero(), item.getId().getNumero());
					for (ScoSolicitacaoProgramacaoEntrega itemSolProg : listaSolProg) {
						itemSolProg.setQtde(obterValorDefault(itemSolProg.getQtde()) * fatorConversaoAf);
						itemSolProg.setQtdeEntregue(obterValorDefault(itemSolProg.getQtdeEntregue()) * fatorConversaoAf);
						
						this.getScoSolicitacaoProgramacaoEntregaRN().persistir(itemSolProg);
					}	
					
					qtdConversao++;
				}	
			}
		}
		
		return qtdConversao;
	}
	
	public Integer obterValorDefault(Integer numero){
		return (numero != null ? numero : 0);
	}
	
	/**
	 * Exclui uma sequência de alteração da AF
	 * @param autorizacaoFornecimento
	 * @param ultimaSequenciaAlteracao
	 * @throws BaseException 
	 */
	public void excluirSequenciaAlteracao(ScoAutorizacaoForn autorizacaoFornecimento, Short ultimaSequenciaAlteracao) throws BaseException {
		if (autorizacaoFornecimento.getSequenciaAlteracao() == 0 ) {
			throw new ApplicationBusinessException(ScoAutorizacaoFornONExceptionCode.MENSAGEM_ERRO_EXCLUSAO_SEQUENCIA_ZERO);	
		}
		if (this.getEstoqueFacade().verificarAutorizacaoFornecimentoSaldoNotaRecebimento(autorizacaoFornecimento.getNumero(), autorizacaoFornecimento.getSequenciaAlteracao())) {
			throw new ApplicationBusinessException(ScoAutorizacaoFornONExceptionCode.MENSAGEM_EXCSEQ_NR);
		}
		this.voltarAfSituacaoAnterior(autorizacaoFornecimento, ultimaSequenciaAlteracao);
	}
	
	private void popularSituacaoAnteriorServidores(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoFornJn autJn) {
		if (autJn.getServidorExcluido() != null) {
			autorizacaoFornecimento.setServidorExcluido(autJn.getServidorExcluido());
		} else {
			autorizacaoFornecimento.setServidorExcluido(null);
		}
		if (autJn.getServidorEstorno() != null) {
			autorizacaoFornecimento.setServidorEstorno(autJn.getServidorEstorno());
		} else {
			autorizacaoFornecimento.setServidorEstorno(null);
		}
		if (autJn.getServidorAutorizado() != null) {
			autorizacaoFornecimento.setServidorAutorizado(autJn.getServidorAutorizado());
		} else {
			autorizacaoFornecimento.setServidorAutorizado(null);
		}
		if (autJn.getServidorAssinaCoord() != null) {
			autorizacaoFornecimento.setServidorAssinaCoord(autJn.getServidorAssinaCoord());
		} else {
			autorizacaoFornecimento.setServidorAssinaCoord(null);
		}
	}
	
	private void popularSituacaoAnteriorDatas(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoFornJn autJn) {
		if (autJn.getDtEstorno() != null) {
			autorizacaoFornecimento.setDtEstorno(autJn.getDtEstorno());
		} else {
			autorizacaoFornecimento.setDtEstorno(null);
		}
		if (autJn.getDtAlteracao() != null) {
			autorizacaoFornecimento.setDtAlteracao(autJn.getDtAlteracao());
		} else {
			autorizacaoFornecimento.setDtAlteracao(null);
		}
		if (autJn.getDtPrevEntrega() != null) {
			if (autJn.getDtPrevEntrega().before(new Date())) {
				autorizacaoFornecimento.setDtPrevEntrega(new Date());
			} else {
				autorizacaoFornecimento.setDtPrevEntrega(autJn.getDtPrevEntrega());
			}
		} else {
			autorizacaoFornecimento.setDtPrevEntrega(null);
		}
		if (autJn.getDtExclusao() != null) {
			autorizacaoFornecimento.setDtExclusao(autJn.getDtExclusao());
		} else {
			autorizacaoFornecimento.setDtExclusao(null);
		}
		if (autJn.getDtAssinaturaChefia() != null) {
			autorizacaoFornecimento.setDtAssinaturaChefia(autJn.getDtAssinaturaChefia());
		} else {
			autorizacaoFornecimento.setDtAssinaturaChefia(null);
		}
		if (autJn.getDtAssinaturaCoord() != null) {
			autorizacaoFornecimento.setDtAssinaturaCoord(autJn.getDtAssinaturaCoord());
		} else {
			autorizacaoFornecimento.setDtAssinaturaCoord(null);
		}

	}

	private void popularSituacaoAnteriorOutros(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoFornJn autJn) {
		if (autJn.getValorFrete() != null) {
			autorizacaoFornecimento.setValorFrete(autJn.getValorFrete());
		} else {
			autorizacaoFornecimento.setValorFrete(null);
		}
		if (autJn.getObservacao() != null) {
			autorizacaoFornecimento.setObservacao(autJn.getObservacao());
		} else {
			autorizacaoFornecimento.setObservacao(null);
		}
		if (autJn.getValorEmpenho() != null) {
			autorizacaoFornecimento.setValorEmpenho(new BigDecimal(autJn.getValorEmpenho()));
		} else {
			autorizacaoFornecimento.setValorEmpenho(null);
		}
		if (autJn.getNroContrato() != null) {
			autorizacaoFornecimento.setNroContrato(autJn.getNroContrato());
		} else {
			autorizacaoFornecimento.setNroContrato(null);
		}
		if (autJn.getNroEmpenho() != null) {
			autorizacaoFornecimento.setNroEmpenho(autJn.getNroEmpenho());
		} else {
			autorizacaoFornecimento.setNroEmpenho(null);
		}
	}

	private void removerJournal(ScoAutorizacaoFornJn autJn) throws ApplicationBusinessException {
		List<ScoItemAutorizacaoFornJn> listaItenJn = this.getScoItemAutorizacaoFornJnDAO().pesquisarItemAutFornJnPorNumAfSeqAlteracao(autJn.getNumero(), autJn.getSequenciaAlteracao()); 				
		
		if (listaItenJn != null) {								
			for (ScoItemAutorizacaoFornJn itemJn : listaItenJn) {
				this.getScoItemAutorizacaoFornJnRN().excluirItemAutorizacaoFornecimentoJn(itemJn);
			}
		}
		
		this.getScoAutorizacaoFornJnRN().excluirAutorizacaoFornecimentoJn(autJn);
	}
	
	private void voltarAfSituacaoAnterior(ScoAutorizacaoForn autorizacaoFornecimento, Short versao) throws BaseException {		
		ScoAutorizacaoFornJn journalAtual = this.getScoAutorizacaoFornJnDAO().buscarUltimaScoAutorizacaoFornJnPorNroAF(autorizacaoFornecimento.getNumero(), null);
		
		ScoAutorizacaoFornJn autJn = this.getScoAutorizacaoFornJnDAO().buscarUltimaScoAutorizacaoFornJnPorNroAF(autorizacaoFornecimento.getNumero(), versao);

		if (autJn != null) {
			if (autJn.getIndAprovada() != null) {
				autorizacaoFornecimento.setAprovada(autJn.getIndAprovada());
			} else {
				autorizacaoFornecimento.setAprovada(DominioAprovadaAutorizacaoForn.N);
			}
			autorizacaoFornecimento.setSituacao(autJn.getSituacao());
			autorizacaoFornecimento.setGeracao(autJn.getIndGeracao());
			autorizacaoFornecimento.setVerbaGestao(autJn.getVerbaGestao());
			autorizacaoFornecimento.setMoeda(autJn.getMdaCodigo());
			autorizacaoFornecimento.setNaturezaDespesa(autJn.getNaturezaDespesa());
			autorizacaoFornecimento.setCondicaoPagamentoPropos(autJn.getCondicaoPagamentoPropos());
			autorizacaoFornecimento.setSequenciaAlteracao(autJn.getSequenciaAlteracao());
			autorizacaoFornecimento.setExclusao(autJn.getIndExclusao());
			autorizacaoFornecimento.setModalidadeEmpenho(autJn.getModalidadeEmpenho());
			autorizacaoFornecimento.setMotivoAlteracaoAf(null);
			autorizacaoFornecimento.setServidorControlado(null);
			
			this.popularSituacaoAnteriorDatas(autorizacaoFornecimento, autJn);
			this.popularSituacaoAnteriorOutros(autorizacaoFornecimento, autJn);
			this.popularSituacaoAnteriorServidores(autorizacaoFornecimento, autJn);
			
			this.getScoItemAutorizacaoFornJnRN().voltarSituacaoAnteriorItemJn(autorizacaoFornecimento, autJn);
									
			this.getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(autorizacaoFornecimento);
			
			this.removerJournal(journalAtual);
		}
		
	}
		
	/**
	 * Libera a assinatura de uma AF
	 * @param autorizacaoFornecimento
	 * @throws BaseException 
	 */
	public void liberarAssinaturaAf(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException {
		AghParametros matriculaChefeCompras = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATR_CHEFE_CPRAS);
		AghParametros vinculoChefeCompras = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VIN_CHEFE_CPRAS);
		
		if (matriculaChefeCompras == null && vinculoChefeCompras == null) {
			throw new ApplicationBusinessException(ScoAutorizacaoFornONExceptionCode.MENSAGEM_ERRO_LIBERACAO_AF);
		}
		
		
		RapServidores servidorChefia = this.getRegistroColaboradorFacade()
				.obterRapServidorPorVinculoMatricula(matriculaChefeCompras.getVlrNumerico().intValue(), 
						vinculoChefeCompras.getVlrNumerico().shortValue());
		
		if (servidorChefia == null) {
			throw new ApplicationBusinessException(ScoAutorizacaoFornONExceptionCode.MENSAGEM_ERRO_LIBERACAO_AF);
		}
		
		this.getScoAutorizacaoFornJnRN().autorizarLiberacaoAutorizacaoFornecimentoJn(
				this.getScoAutorizacaoFornJnDAO().pesquisarAutorizacaoFornecimentoJnPendenteAssinatura(
						autorizacaoFornecimento.getNumero(), false), servidorChefia);
		
		if (this.getScoAutorizacaoFornJnDAO().verificarAutorizacaoFornecimentoJnPendenteAssinatura(autorizacaoFornecimento.getNumero())) {
			this.getScoAutorizacaoFornJnRN().autorizarLiberacaoAutorizacaoFornecimentoJn(
					this.getScoAutorizacaoFornJnDAO().pesquisarAutorizacaoFornecimentoJnPendenteAssinatura(
							autorizacaoFornecimento.getNumero(), true), servidorChefia);
		}
		
		autorizacaoFornecimento.setAprovada(DominioAprovadaAutorizacaoForn.S);
		autorizacaoFornecimento.setServidorAutorizado(servidorChefia);
		autorizacaoFornecimento.setDtAssinaturaChefia(new Date());			
		getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(autorizacaoFornecimento);				
	}	
	
	/**
	 * Dentro do CRUD da AF eh necessario fazer a validacao da RN05 portanto este metodo existe somente para isto.
	 * Nos casos normais chamar direto da RN.
	 * @param autorizacaoFornecimento
	 * @throws BaseException
	 */
	public void gravarAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoFornecimento) throws BaseException {
		ScoAutorizacaoForn afOriginal = this.getScoAutorizacaoFornDAO().obterOriginal(autorizacaoFornecimento);
		
		// RN 05
		this.validarDataPrevisaoEntrega(autorizacaoFornecimento, afOriginal);

		this.getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(autorizacaoFornecimento);
		
		
	}
	
	/***
	 * RN28 - Atualiza Servidor AF
	 * @param scoAutorizacaoForn
	 * @throws BaseException 
	 */
	public void atualizarServidorAF(ScoAutorizacaoForn scoAutorizacaoForn) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		scoAutorizacaoForn.setServidorControlado(servidorLogado);
		scoAutorizacaoForn.setDtAlteracao(new Date());
		this.getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(scoAutorizacaoForn);
	}
	
	/**
	 * Atualiza a situacao da AF
	 * @param scoAutorizacaoForn
	 * @param situacao
	 * @throws BaseException
	 */
	public void atualizarSituacaoAF(ScoAutorizacaoForn scoAutorizacaoForn, DominioSituacaoAutorizacaoFornecimento situacao) throws BaseException{
		scoAutorizacaoForn.setSituacao(situacao);
		this.getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(scoAutorizacaoForn);
	}

	protected ScoAutorizacaoFornRN getScoAutorizacaoFornRN() {
		return scoAutorizacaoFornRN;
	}
	
	protected ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO() {
		return scoSolicitacaoProgramacaoEntregaDAO;
	}
	
	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}

	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	protected ScoItemAutorizacaoFornJnDAO getScoItemAutorizacaoFornJnDAO() {
		return scoItemAutorizacaoFornJnDAO;
	}

	protected ScoAutorizacaoFornJnRN getScoAutorizacaoFornJnRN() {
		return scoAutorizacaoFornJnRN;
	}
	
	protected ScoItemAutorizacaoFornJnRN getScoItemAutorizacaoFornJnRN() {
		return scoItemAutorizacaoFornJnRN;
	}
	
	protected ScoAutorizacaoFornJnDAO getScoAutorizacaoFornJnDAO() {
		return scoAutorizacaoFornJnDAO;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected ScoItemAutorizacaoFornRN getScoItemAutorizacaoFornRN() {
		return scoItemAutorizacaoFornRN;
	}
	
	protected ScoSolicitacaoProgramacaoEntregaRN getScoSolicitacaoProgramacaoEntregaRN() {
		return scoSolicitacaoProgramacaoEntregaRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
