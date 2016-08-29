package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.vo.PlanejamentoCompraVO;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.suprimentos.vo.ScoPlanejamentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PlanejamentoScON extends BaseBusiness {

	@EJB
	private SolicitacaoCompraRN solicitacaoCompraRN;
	
	private static final Log LOG = LogFactory.getLog(PlanejamentoScON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5922834917785524834L;

	public enum PlanejamentoScONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_PPS_LICITACAO_NAO_ENCONTRADO, MENSAGEM_DATA_ANTERIOR_INFORMADA, 
		MENSAGEM_QTD_REFORCO_ZERADA, MENSAGEM_SALDO_AF_MENOR_QUE_REFORCO, MENSAGEM_SALDO_PROG_MENOR_QUE_REFORCO,
		MENSAGEM_REFORCO_MULTIPLO_FATOR_CONVERSAO, MENSAGEM_APROVADA_MULTIPLO_FATOR_CONVERSAO;
	}
	
	
	/**
	 * Atualiza a SC com os dados editados na grade da tela do planejamento, atualizando os dados da AF e
	 * gerando parcelas de entrega quando necessario
	 * @param listaAlteracoes
	 * @param nroLibRefs
	 * @param nroLibAss
	 * @throws BaseException
	 */
	public void atualizarPlanejamentoSco(List<ScoPlanejamentoVO> listaAlteracoes, 
			List<Integer> nroLibRefs, List<Integer> nroLibAss) throws BaseException {
		
		for (ScoPlanejamentoVO item : listaAlteracoes) {
			
			ScoSolicitacaoDeCompra solicitacao = getScoSolicitacoesDeComprasDAO()
					.obterPorChavePrimaria(item.getNumeroSolicitacaoCompra());
			
			ScoSolicitacaoDeCompra solicitacaoOriginal = getScoSolicitacoesDeComprasDAO()
					.obterOriginal(item.getNumeroSolicitacaoCompra());
			
			ScoItemAutorizacaoForn itemAf = getAutFornecimentoFacade().obterItemAfPorSolicitacaoCompra(solicitacao.getNumero(), false, false);
			
			this.validarDatasAnalise(item, solicitacaoOriginal);
			//#42058 - Valida se houve alteração para os campos de Qtd Reforço, Qtd Aprovada, Lib. Ref. e Lib. Ass.
			if (solicitacao.getQtdeReforco() != solicitacaoOriginal
					.getQtdeReforco()
					|| solicitacao.getQtdeAprovada() != solicitacaoOriginal
							.getQtdeAprovada()
					|| nroLibRefs.indexOf(solicitacao.getNumero()) >= 0
					|| nroLibAss.indexOf(solicitacao.getNumero()) >= 0) {

				this.validarQtdeReforco(item, solicitacaoOriginal, itemAf,
						nroLibRefs);
				this.validarQtdeAprovada(item, solicitacaoOriginal, itemAf);

			}
		
			solicitacao.setDtAnalise(item.getDataAnaliseCompra());
			solicitacao.setQtdeReforco(item.getQtdeReforco());
			solicitacao.setQtdeAprovada(item.getQtdeAprovada());
			
			this.liberarReforco(solicitacao, item, nroLibRefs, nroLibAss, itemAf);
			
			if (nroLibAss.indexOf(solicitacao.getNumero()) >= 0) {
				limparReforcoSolicitacao(solicitacao);		
				assinarDemaisParcelas(solicitacao, itemAf);
			}
			
			getSolicitacaoCompraRN().atualizarSolicitacaoCompra(solicitacao, solicitacaoOriginal);
			
		}
	}

	/**
	 * Se o checkbox para liberacao de reforco tiver marcado, faz as validacoes e processos necessarios para 
	 * do reforco digitado gerar uma parcela de entrega do item da AF
	 * @param solicitacao
	 * @param item
	 * @param nroLibRefs
	 * @param nroLibAss
	 * @return Boolean
	 * @throws BaseException
	 */
	private Boolean liberarReforco(ScoSolicitacaoDeCompra solicitacao, ScoPlanejamentoVO item,
			List<Integer> nroLibRefs, List<Integer> nroLibAss, ScoItemAutorizacaoForn itemAf) throws BaseException {
		
		Boolean ret = Boolean.FALSE;
		if (nroLibRefs.indexOf(solicitacao.getNumero()) >= 0) {
			this.validarReforcoPreenchido(item);
			
			this.validarSaldoItemAf(solicitacao, item.getQtdeReforco(), itemAf);
			
			this.gerarParcelasEntrega(solicitacao, nroLibAss, itemAf);
			
			ret = Boolean.TRUE;
		}
		
		return ret;
	}
	
	private void validarReforcoPreenchido(ScoPlanejamentoVO item) throws BaseException {
		if (item.getQtdeReforco() == null || item.getQtdeReforco().equals(Long.valueOf(0))) {
			throw new ApplicationBusinessException(PlanejamentoScONExceptionCode.MENSAGEM_QTD_REFORCO_ZERADA, item.getNumeroSolicitacaoCompra());
		}
	}
	
	private void assinarDemaisParcelas(ScoSolicitacaoDeCompra solicitacao, ScoItemAutorizacaoForn itemAf) throws BaseException {
		if (itemAf != null) {
			List<ScoProgEntregaItemAutorizacaoFornecimento> listaProgEntrega = this.getAutFornecimentoFacade().pesquisarProgEntregaItemAfPlanejamento(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero());
			
			if (listaProgEntrega != null) {
				for (ScoProgEntregaItemAutorizacaoFornecimento progEntrega : listaProgEntrega) {
					this.assinarParcela(progEntrega, true);
					this.getAutFornecimentoFacade().persistirProgEntregaItemAf(progEntrega);
				}
			}
		}
	}
	
	/**
	 * Gera a parcela de entrega para o reforco
	 * @param solicitacao
	 * @param nroLibAss
	 * @throws BaseException
	 */
	private void gerarParcelasEntrega(ScoSolicitacaoDeCompra solicitacao, List<Integer> nroLibAss, ScoItemAutorizacaoForn itemAf) throws BaseException {		
		if (itemAf != null) {
			Integer numParcela = this.getAutFornecimentoFacade().obterMaxNumeroParcela(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero());
			if (numParcela == null) {
				numParcela = 1;
			} else {
				numParcela++;
			}
			Integer seqParcela = this.getAutFornecimentoFacade().obterMaxNumeroSeqParcelaItemAf(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero(), numParcela);			
			if (seqParcela == null) {
				seqParcela = 1;
			} else {
				seqParcela++;
			}
			
			/*
			 * Calcula a data de entrega se existir prazo de entrega na proposta pega de la senao
			 * do parametro desconsiderando fim de semana e feriados
			 */
			
			Date dataEntregaCalculada = null;
			if (itemAf.getAutorizacoesForn() != null && 
				itemAf.getAutorizacoesForn().getPropostaFornecedor() != null &&
				itemAf.getAutorizacoesForn().getPropostaFornecedor().getPrazoEntrega() != null){
				Short prazoEntrega = itemAf.getAutorizacoesForn().getPropostaFornecedor().getPrazoEntrega();
				dataEntregaCalculada =  DateUtil.adicionaDias(new Date(), prazoEntrega.intValue());
				
			}
			else {
				AghParametros parametroPrazoEntrega = getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_PRAZO_ENTREGA);
				Short prazoEntrega = parametroPrazoEntrega.getVlrNumerico().shortValue(); 
							
				dataEntregaCalculada = calcDtRefer(new Date(), prazoEntrega.intValue());
			}
			
			ScoProgEntregaItemAutorizacaoFornecimentoId id = new ScoProgEntregaItemAutorizacaoFornecimentoId();
			id.setIafAfnNumero(itemAf.getId().getAfnNumero());
			id.setIafNumero(itemAf.getId().getNumero());
			id.setParcela(numParcela);
			id.setSeq(seqParcela);

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			ScoProgEntregaItemAutorizacaoFornecimento progEntrega = null;
			
			progEntrega = new ScoProgEntregaItemAutorizacaoFornecimento();
			progEntrega.setId(id);
			progEntrega.setDtGeracao(new Date());
			progEntrega.setDtPrevEntrega(dataEntregaCalculada);
			progEntrega.setDtEntrega(null);
			progEntrega.setQtde(solicitacao.getQtdeReforco().intValue());
			progEntrega.setQtdeEntregue(0);
			progEntrega.setRapServidor(servidorLogado);
			progEntrega.setRapServidorAlteracao(null);
			progEntrega.setIndAssinatura(Boolean.FALSE);
			progEntrega.setIndEmpenhada(DominioAfEmpenhada.N);
			progEntrega.setIndEnvioFornecedor(Boolean.FALSE);
			progEntrega.setIndRecalculoAutomatico(Boolean.FALSE);
			progEntrega.setIndRecalculoManual(Boolean.FALSE);
			progEntrega.setIndImpressa(Boolean.FALSE);
			progEntrega.setDtAtualizacao(null);
			progEntrega.setDtAssinatura(null);
			progEntrega.setRapServidorAssinatura(null);
			progEntrega.setDtAlteracao(null);
			progEntrega.setIndCancelada(Boolean.FALSE);
			progEntrega.setDtCancelamento(null);
			progEntrega.setRapServidorCancelamento(null);
			progEntrega.setScoJustificativa(null);
			progEntrega.setDtNecessidadeHcpa(null);
			progEntrega.setIndEfetivada(Boolean.FALSE);
			progEntrega.setIndEntregaImediata(Boolean.FALSE);
			progEntrega.setObservacao(null);
			progEntrega.setQtdeEntregueProv(0);
			progEntrega.setValorEfetivado(0.00);
			progEntrega.setQtdeEntregueAMais(0);
			progEntrega.setIndTramiteInterno(Boolean.FALSE);
			progEntrega.setScoAfPedido(null);
			progEntrega.setIndConversaoUnidade(false);
			progEntrega.setIndPublicado(false);
			progEntrega.setValorTotal(this.getAutFornecimentoFacade().obterValorTotalProgEntregaItemAf(id.getIafAfnNumero(), id.getIafNumero(), solicitacao.getQtdeReforco().intValue(), 0, progEntrega.getValorEfetivado()));
			
			if (nroLibAss.indexOf(solicitacao.getNumero()) >= 0) {
				this.assinarParcela(progEntrega, true);				
			} else {
				this.assinarParcela(progEntrega, false);
			}
			
            ScoSolicitacaoProgramacaoEntrega scoSolicitacaoProgramacaoEntrega = new ScoSolicitacaoProgramacaoEntrega();			
			scoSolicitacaoProgramacaoEntrega.setSolicitacaoCompra(solicitacao);
			scoSolicitacaoProgramacaoEntrega.setQtde(solicitacao.getQtdeReforco().intValue());
			scoSolicitacaoProgramacaoEntrega.setIndPrioridade(Short.valueOf("1"));
			scoSolicitacaoProgramacaoEntrega.setProgEntregaItemAf(progEntrega);
			
			this.getAutFornecimentoFacade().persistirProgEntregaItemAf(progEntrega);
			
			this.getAutFornecimentoFacade().persistir(scoSolicitacaoProgramacaoEntrega);
		}
	}
	
	private void assinarParcela(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAf, Boolean assinar) {
		if (assinar) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			progEntregaItemAf.setRapServidorLibPlanej(servidorLogado);
			progEntregaItemAf.setIndPlanejamento(Boolean.TRUE);
			progEntregaItemAf.setDtLibPlanejamento(new Date());
		} else {
			progEntregaItemAf.setRapServidorLibPlanej(null);
			progEntregaItemAf.setIndPlanejamento(Boolean.FALSE);
			progEntregaItemAf.setDtLibPlanejamento(null);
		}
	}
	
	/**
	 * Valida se o item da AF possui saldo suficiente para o reforco digitado
	 * @param solicitacao
	 * @param qtdReforco
	 * @throws ApplicationBusinessException
	 */
	private void validarSaldoItemAf(ScoSolicitacaoDeCompra solicitacao, Long qtdReforco, ScoItemAutorizacaoForn itemAf) throws ApplicationBusinessException {
		if (itemAf != null) {
			Integer qtdSaldoAf = (Integer)CoreUtil.nvl(itemAf.getQtdeSolicitada(),0) - (Integer)CoreUtil.nvl(itemAf.getQtdeRecebida(), 0);
			
			if (qtdSaldoAf < qtdReforco.intValue()) {
				throw new ApplicationBusinessException(PlanejamentoScONExceptionCode.MENSAGEM_SALDO_AF_MENOR_QUE_REFORCO, qtdReforco, solicitacao.getNumero(), qtdSaldoAf);
			}
		}
	}
	
	/**
	 * Após gerar a parcela de reforco zera a qtd de reforco da SC e encaminha para o ponto de parada da licitacao
	 * @param listaSolicitacoes
	 * @throws BaseException
	 */
	private void limparReforcoSolicitacao(ScoSolicitacaoDeCompra solicitacao) throws BaseException {
		
		// obtem o ponto de parada da licitacao
		ScoPontoParadaSolicitacao ppsLicitacao = this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.LI);
		
		if (ppsLicitacao == null) {
			throw new ApplicationBusinessException(
					PlanejamentoScONExceptionCode.MENSAGEM_PARAMETRO_PPS_LICITACAO_NAO_ENCONTRADO);
		}						
		
		// encaminha a solicitação de compras para o ponto de parada da licitação
		solicitacao.setPontoParada(solicitacao.getPontoParadaProxima());
		solicitacao.setPontoParadaProxima(ppsLicitacao);
		solicitacao.setQtdeReforco(Long.valueOf("0"));
		solicitacao.setDtAnalise(null);		
	}

	/**
	 * Valida se as datas de analise digitadas estao no futuro
	 * @param item
	 * @param solComprasOriginal
	 * @throws ApplicationBusinessException
	 */
	private void validarDatasAnalise(ScoPlanejamentoVO item, ScoSolicitacaoDeCompra solComprasOriginal) throws ApplicationBusinessException {
		if (item.getDataAnaliseCompra() != null) {
			if (solComprasOriginal != null && solComprasOriginal.getDtAnalise() == null) {
				if (obterDataTruncada(item.getDataAnaliseCompra()).before(obterDataTruncada(new Date()))) {
					throw new ApplicationBusinessException(
							PlanejamentoScONExceptionCode.MENSAGEM_DATA_ANTERIOR_INFORMADA);
				}
			}
		}
	}
	
	/**
	 * Valida quantidade de reforco com qtde de saldo de parcelas
	 * @param item
	 * @param solComprasOriginal
	 * @throws ApplicationBusinessException
	 */
	private void validarQtdeReforco(ScoPlanejamentoVO item, ScoSolicitacaoDeCompra solicitacao, ScoItemAutorizacaoForn itemAf, List<Integer> nroLibRefs) throws ApplicationBusinessException {				
		if (item.getQtdeReforco() != null && nroLibRefs.indexOf(solicitacao.getNumero()) >= 0) {
			PlanejamentoCompraVO planejamentoComprasVO = new PlanejamentoCompraVO();
			
			planejamentoComprasVO.setSlcNumero(solicitacao.getNumero());
			planejamentoComprasVO.setItemAf(itemAf);
			
			List<PlanejamentoCompraVO> listaPlanejamentoCompraVO = new ArrayList<PlanejamentoCompraVO>();
			listaPlanejamentoCompraVO.add(planejamentoComprasVO);
			
			Long qtdeReforco = item.getQtdeReforco();
			Integer qtdeSaldo = obterQtdSaldoParcelas(solicitacao, listaPlanejamentoCompraVO);
			
			if (qtdeSaldo < qtdeReforco.intValue()) {
				throw new ApplicationBusinessException(PlanejamentoScONExceptionCode.MENSAGEM_SALDO_PROG_MENOR_QUE_REFORCO, qtdeReforco, solicitacao.getNumero(), qtdeSaldo);
			}
			
			if (itemAf != null) {
				this.validarQtdeReforcoMultiplaFatorConversaoFornecedor(solicitacao, item.getQtdeReforco().intValue(), itemAf.getFatorConversaoForn());
			}
		}
	}
	
	private void validarQtdeAprovada(ScoPlanejamentoVO item, ScoSolicitacaoDeCompra solicitacao, ScoItemAutorizacaoForn itemAf) throws ApplicationBusinessException {				
		if (itemAf != null) {
			this.validarQtdeAprovadaMultiplaFatorConversaoFornecedor(solicitacao, item.getQtdeAprovada().intValue(), itemAf.getFatorConversaoForn());
		}
	}

	private Date obterDataTruncada(Date data) {
		Date dataTruncada = null;
		
		if (data != null) {
			dataTruncada = DateUtils.truncate(data, Calendar.DATE);
		}
		
		return dataTruncada;
	}

	/**
	 * Calcula o saldo das parcelas baseado numa SC
	 * @param scoItem
	 * @param listaControle
	 * @return Integer
	 */
	public Integer obterQtdSaldoParcelas(ScoSolicitacaoDeCompra scoItem, List<PlanejamentoCompraVO> listaControle) {
		Integer index = this.obterIndiceListaControle(scoItem, listaControle);
		Integer result = 0;
		
		if (index >= 0 && listaControle.get(index).getItemAf() != null) {
			List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas = getComprasFacade().
					pesquisaProgEntregaItemAf(listaControle.get(index).getItemAf().getId().getAfnNumero(), 
							Integer.valueOf(listaControle.get(index).getItemAf().getId().getNumero()), false, false);
			
			if (listaParcelas != null) {
				Integer qtdeProgramada = getAutFornecimentoFacade().obterQtdeProgramadaProgEntregaItemAf(listaParcelas);		
				Integer qtdAf = listaControle.get(index).getItemAf().getQtdeSolicitada();
				result = (Integer)CoreUtil.nvl(qtdAf, 0)  - (Integer)CoreUtil.nvl(qtdeProgramada, 0);
			}
		
		}
		return result; 
	}
	
	/**
	 * Baseado numa SC cria um PlanejamentoCompraVO
	 * @param scoItem
	 * @return PlanejamentoCompraVO
	 */
	public PlanejamentoCompraVO preencherControleVO(ScoSolicitacaoDeCompra scoItem) {
		PlanejamentoCompraVO obj = new PlanejamentoCompraVO();
		
		obj.setSlcNumero(scoItem.getNumero());
		obj.setItemAf(getAutFornecimentoFacade().obterItemAfPorSolicitacaoCompra(scoItem.getNumero(), false, false));
		obj.setMostrarLinkParcelas(false);
		obj.setProtegerQtdeAprovRef(false);
		obj.setMostraLinkLibAss(true);
		obj.setMostrarLinkLibRef(true);
		
		// #30326
		if (getEstoqueFacade().verificarExisteReforcoSolicitacaoCompras(obj.getSlcNumero())) {
			if (this.getAutFornecimentoFacade().verificarProgEntregaItemAfPlanejamento(obj.getItemAf().getId().getAfnNumero(), obj.getItemAf().getId().getNumero())) {
				obj.setProtegerQtdeAprovRef(true);
				obj.setMostrarLinkLibRef(false);				
			}
		} 
		
		if (obj.getItemAf() != null) {
			obj.setMostrarLinkParcelas(true);
			
			if (obj.getItemAf().getIndContrato() != null && obj.getItemAf().getIndContrato() &&  
					(obj.getItemAf().getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.PA)  ||
							obj.getItemAf().getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.AE))) {
				obj.setMostrarLinkAf(Boolean.TRUE);
			} else {
				obj.setMostrarLinkAf(Boolean.FALSE);
			}
		} else {
			obj.setMostraLinkLibAss(false);
			obj.setMostrarLinkLibRef(false);
		}
		
		return obj;
	}
	
	/**
	 * Obtem a posicao do item da SC na lista de ScoPlanejamentoVO
	 * @param item
	 * @param listAlteracoes
	 * @return Integer
	 */
	public Integer obterIndiceLista(ScoSolicitacaoDeCompra item, List<ScoPlanejamentoVO> listAlteracoes) {
		ScoPlanejamentoVO planVO = montarItemObjetoVO(item);
		return listAlteracoes.indexOf(planVO);
	}

	/**
	 * Obtem a posicao do item da SC na lista de PlanejamentoCompraVO
	 * @param item
	 * @param listaControle
	 * @return Integer
	 */
	public Integer obterIndiceListaControle(ScoSolicitacaoDeCompra item, List<PlanejamentoCompraVO> listaControle) {
		PlanejamentoCompraVO  controlVO = preencherControleVO(item);
		return listaControle.indexOf(controlVO);
	}

	/**
	 * Verifica se determinado link deve ser mostrado ou nao na tela
	 * @param listaControle
	 * @param item
	 * @param verificarParcela
	 * @param verificarLibRef
	 * @param verificarAf
	 * @param verificarLibAss
	 * @param protegerQtde
	 * @return Boolean
	 */
	public Boolean verificarHabilitacaoCamposAf(List<PlanejamentoCompraVO> listaControle, ScoSolicitacaoDeCompra item, 
			Boolean verificarParcela, Boolean verificarLibRef, Boolean verificarAf, Boolean verificarLibAss, Boolean protegerQtde) {
		Boolean ret = Boolean.FALSE;
		if (listaControle != null){
			Integer index = this.obterIndiceListaControle(item, listaControle);
			if (index >= 0) {
				if (listaControle.get(index) != null){
					if (listaControle.get(index).getSlcNumero().equals(item.getNumero())) {
						if (verificarParcela && listaControle.get(index).getMostrarLinkParcelas()) {
							ret = Boolean.TRUE;
							
						}
						if (verificarLibRef && listaControle.get(index).getMostrarLinkLibRef()) {
							ret = Boolean.TRUE;
						}
						
						if (listaControle.get(index).getMostrarLinkAf() != null){
							if (verificarAf && listaControle.get(index).getMostrarLinkAf()) {
								ret = Boolean.TRUE;
							}
						}
						if (verificarLibAss && listaControle.get(index).getMostraLinkLibAss()) {
							ret = Boolean.TRUE;
						}
						if (protegerQtde && listaControle.get(index).getProtegerQtdeAprovRef()) {
							ret = Boolean.TRUE;
						}
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * Baseado numa SC monta um ScoPlanejamentoVO
	 * @param item
	 * @return ScoPlanejamentoVO
	 */
	public ScoPlanejamentoVO montarItemObjetoVO(ScoSolicitacaoDeCompra item) {
		return new ScoPlanejamentoVO(item.getNumero(), item.getDtAnalise(),
				item.getQtdeAprovada(), item.getQtdeReforco(), Boolean.FALSE);
	}

	/**
	 * Obtem a descricao do ponto de parada
	 * @param item
	 * @param proximo
	 * @return String
	 * @throws ApplicationBusinessException
	 */
	public String obterDescricaoPontoParada(ScoSolicitacaoDeCompra item, Boolean proximo)  throws ApplicationBusinessException {
		String descricaoPontoParada = "";
		if (proximo) {
			
			if (item.getPontoParadaProxima() == null) {
				return "";
			}
			
			item.setPontoParadaProxima(scoPontoParadaSolicitacaoDAO.obterPorChavePrimaria(item.getPontoParadaProxima().getCodigo()));
			
			descricaoPontoParada = item.getPontoParadaProxima().getDescricao();
			
			if (this.getComprasCadastrosBasicosFacade()
					.verificarPontoParadaComprador(
							item.getPontoParadaProxima())) {
				if (item.getServidorCompra() != null && item.getServidorCompra().getPessoaFisica() != null){
					
					item.setServidorCompra(registroColaboradorFacade.obterRapServidor(item.getServidorCompra().getId()));
					item.getServidorCompra().setPessoaFisica(registroColaboradorFacade.obterPessoaFisica(item.getServidorCompra().getPessoaFisica().getCodigo()));
										
					StringBuilder descricaoVendedor = new StringBuilder(
							descricaoPontoParada).append(" - ").append(
							item.getServidorCompra().getPessoaFisica()
									.getNome());
					descricaoPontoParada = descricaoVendedor.toString();
				}
			}
			
		} else {
			
			if (item.getPontoParada() == null) {
				return "";
			}
			
			
			item.setPontoParada(scoPontoParadaSolicitacaoDAO.obterPorChavePrimaria(item.getPontoParada().getCodigo()));
			
			descricaoPontoParada = item.getPontoParada().getDescricao();
			
			if (this.getComprasCadastrosBasicosFacade()
					.verificarPontoParadaComprador(item.getPontoParada())) {
				
				if (item.getServidorCompra() != null && item.getServidorCompra().getPessoaFisica() != null){
					item.setServidorCompra(registroColaboradorFacade.obterRapServidor(item.getServidorCompra().getId()));
					item.getServidorCompra().setPessoaFisica(registroColaboradorFacade.obterPessoaFisica(item.getServidorCompra().getPessoaFisica().getCodigo()));
					
					StringBuilder descricaoVendedor = new StringBuilder(
							descricaoPontoParada).append(" - ").append(
							item.getServidorCompra().getPessoaFisica()
									.getNome());
					descricaoPontoParada = descricaoVendedor.toString();
				}
			}
		}

		return descricaoPontoParada;
	}
	
	private Date calcDtRefer(Date dataSolic, Integer nroDiasRef){
		
		Date dataCalculada = dataSolic;
		Date dtReferencia = DateUtil.adicionaDias(dataSolic, nroDiasRef);
		Integer qntFinalSemana = 0;
		Integer qntFeriado = 0;
		Integer qntAdic = 0;
		Integer qntAcum = 0;
		Boolean controle = true;
		while(controle){
			while(dataCalculada.compareTo(dtReferencia) <= 0){
				if(DateUtil.isFinalSemana(dataCalculada)){
					qntFinalSemana++;
				}
				dataCalculada = DateUtil.adicionaDias(dataCalculada, 1);
			}
			
			dataCalculada = dataSolic;		
			qntFeriado = getAghuFacade().obterQtdFeriadosEntreDatasSemFindeSemTurno(dataSolic,dtReferencia).intValue();		
			qntAdic = qntFinalSemana + qntFeriado;
			qntFinalSemana = 0;
			qntAdic = qntAdic - qntAcum;
			if(qntAdic == 0){
				return dtReferencia;
			}else{
				qntAcum = qntAcum + qntAdic;
				dtReferencia = DateUtil.adicionaDias(dtReferencia, 1);
			}
		}
		
		return null;
	}

	private void validarQtdeReforcoMultiplaFatorConversaoFornecedor(ScoSolicitacaoDeCompra solicitacaoCompra, 
			Integer qtdReforco, Integer fatorConversaoForn) throws ApplicationBusinessException {
		if (qtdReforco % (Integer)CoreUtil.nvl(fatorConversaoForn,1) != 0) {
			throw new ApplicationBusinessException(PlanejamentoScONExceptionCode.MENSAGEM_REFORCO_MULTIPLO_FATOR_CONVERSAO, qtdReforco, solicitacaoCompra.getNumero(), fatorConversaoForn);			
		}
	}
	
	private void validarQtdeAprovadaMultiplaFatorConversaoFornecedor(ScoSolicitacaoDeCompra solicitacaoCompra, 
			Integer qtdAprovada, Integer fatorConversaoForn) throws ApplicationBusinessException {		
		if (qtdAprovada % (Integer)CoreUtil.nvl(fatorConversaoForn,1) != 0 &&
			qtdAprovada != solicitacaoCompra.getQtdeAprovada().intValue()) {
			throw new ApplicationBusinessException(PlanejamentoScONExceptionCode.MENSAGEM_APROVADA_MULTIPLO_FATOR_CONVERSAO, qtdAprovada, solicitacaoCompra.getNumero(), fatorConversaoForn);			
		}
	}
	
	private SolicitacaoCompraRN getSolicitacaoCompraRN() {
		return solicitacaoCompraRN;
	}
	
	private ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}

	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}
	
	private ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return scoPontoParadaSolicitacaoDAO;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
	protected IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IAghuFacade getAghuFacade()  {
		return aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}