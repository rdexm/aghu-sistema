package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoScJnDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.dao.ScoSsJnDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacaoId;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pela geradao de solicitações, itens do PAC e complementos
 * de AF, a partir de uma entrega programada.
 * 
 * @author mlcruz
 */
@Stateless
public class ScoSolicitacaoProgramacaoEntregaGeracaoON extends BaseBusiness {


@EJB
private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;

	private static final Log LOG = LogFactory.getLog(ScoSolicitacaoProgramacaoEntregaGeracaoON.class);
	private static final long serialVersionUID = 3614229510514398190L;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	
	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@Inject
	private ScoSsJnDAO scoSsJnDAO;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@Inject
	private ScoScJnDAO scoScJnDAO;
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	@EJB
	private AutFornecimentoRN autFornecimentoRN;
	
	@EJB
	private ScoAutorizacaoFornJnRN scoAutorizacaoFornJnRN;

	@EJB
	private ScoAutorizacaoFornRN scoAutorizacaoFornRN;
	
	@EJB
	private ScoItemAutorizacaoFornJnRN scoItemAutorizacaoFornJnRN;
	
	@EJB
	private ScoProgEntregaItemAutorizacaoFornecimentoON scoProgEntregaItemAutorizacaoFornecimentoON;
	
	@EJB
	private ScoSolicitacaoProgramacaoEntregaON scoSolicitacaoProgramacaoEntregaON;
	
	
	private enum ExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_PONTO_PARADA_NAO_ENCONTRADO, MENSAGEM_COND_PGTO_NAO_ENCONTRADA;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	/**
	 * Gera SC.
	 * 
	 * @param itemAf Item de AF.
	 * @param parcela Parcela do item da AF.
	 * @return 
	 * @throws BaseException 
	 */
	public void gerarSc(ScoItemAutorizacaoForn itemAf,
			ParcelaItemAutFornecimentoVO parcela) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		
		ScoSolicitacaoDeCompra scOrigem = itemAf.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra();
		ScoSolicitacaoDeCompra novaSc = new ScoSolicitacaoDeCompra();
		Date now = new Date();
		
		// Ponto de parada do tipo CP..
		ScoPontoParadaSolicitacao ptParada = getComprasCadastrosBasicosFacade()
				.obterPontoParadaPorTipo(DominioTipoPontoParada.CP);

		// ..deve estar ativo.
		if (ptParada != null && DominioSituacao.A.equals(ptParada.getSituacao())) {
			novaSc.setPontoParada(ptParada);
		} else {
			ExceptionCode.MENSAGEM_PONTO_PARADA_NAO_ENCONTRADO
					.throwException(DominioTipoPontoParada.CP);
		}
		
		// Ponto de parada do tipo LI..
		ScoPontoParadaSolicitacao ptParadaProx = getComprasCadastrosBasicosFacade()
				.obterPontoParadaPorTipo(DominioTipoPontoParada.LI);

		// ..deve estar ativo.
		if (ptParadaProx != null && DominioSituacao.A.equals(ptParadaProx.getSituacao())) {
			novaSc.setPontoParadaProxima(ptParadaProx);
		} else {
			ExceptionCode.MENSAGEM_PONTO_PARADA_NAO_ENCONTRADO
					.throwException(DominioTipoPontoParada.LI);
		}
		
		novaSc.setServidor(servidorLogado);
		novaSc.setMaterial(scOrigem.getMaterial());
		novaSc.setCentroCusto(servidorLogado.getCentroCustoLotacao());
		novaSc.setCentroCustoAplicada(parcela.getCentroCusto());
		novaSc.setServidorAutorizacao(getChefeCompras());
		novaSc.setDtSolicitacao(now);
		novaSc.setDtDigitacao(now);
		novaSc.setQtdeAprovada(parcela.getQtdeDetalhada().longValue());
		novaSc.setQtdeSolicitada(parcela.getQtdeDetalhada().longValue());
		novaSc.setExclusao(false);
		novaSc.setUrgente(scOrigem.getUrgente());
		novaSc.setDevolucao(scOrigem.getDevolucao());
		novaSc.setOrcamentoPrevio(scOrigem.getOrcamentoPrevio());
		novaSc.setModalidadeLicitacao(scOrigem.getModalidadeLicitacao());
		novaSc.setServidorCompra(scOrigem.getServidorCompra());
		novaSc.setNaturezaDespesa(scOrigem.getNaturezaDespesa());
		novaSc.setVerbaGestao(parcela.getVerbaGestao());
		novaSc.setCentroCustoAutzTecnica(scOrigem.getCentroCustoAutzTecnica());
		novaSc.setDiasDuracao(scOrigem.getDiasDuracao());
		novaSc.setQtdeReforco((long) 0);
		novaSc.setDescTecnica(scOrigem.getDescTecnica());
		novaSc.setDescTecnicaCont(scOrigem.getDescTecnicaCont());
		novaSc.setAplicacao(scOrigem.getAplicacao());
		novaSc.setJustificativaUso(scOrigem.getJustificativaUso());
		novaSc.setDtAutorizacao(scOrigem.getDtAutorizacao());
		novaSc.setDtDescTecnica(scOrigem.getDtDescTecnica());
		novaSc.setValorUnitPrevisto(scOrigem.getValorUnitPrevisto());
		novaSc.setMotivoUrgencia(scOrigem.getMotivoUrgencia());
		novaSc.setJustificativaDevolucao(scOrigem.getJustificativaDevolucao());
		novaSc.setNroInvestimento(scOrigem.getNroInvestimento());
		novaSc.setDescricao(scOrigem.getDescricao());
		novaSc.setGeracaoAutomatica(true);
		novaSc.setQtdeEntregue((long) 0);
		novaSc.setEfetivada(false);
		novaSc.setFundoFixo(scOrigem.getFundoFixo());
		novaSc.setUnidadeMedida(scOrigem.getUnidadeMedida());
		novaSc.setRecebimento(false);
		novaSc.setServidorOrcamento(scOrigem.getServidorOrcamento());
		novaSc.setNroProjeto(scOrigem.getNroProjeto());
		novaSc.setAlmoxarifado(scOrigem.getAlmoxarifado());
		novaSc.setItpPdmNumero(scOrigem.getItpPdmNumero());
		novaSc.setItpNumero(scOrigem.getItpNumero());
		novaSc.setPaciente(scOrigem.getPaciente());
		novaSc.setMatExclusivo(scOrigem.getMatExclusivo());
		novaSc.setJustificativaExclusividade(scOrigem.getJustificativaExclusividade());
		novaSc.setOrdemRecebimento(scOrigem.getOrdemRecebimento());
		novaSc.setEntregaUnica(scOrigem.getEntregaUnica());
		novaSc.setPrioridade(scOrigem.getPrioridade());
		novaSc.setMotivoPrioridade(scOrigem.getMotivoPrioridade());
		novaSc.setDtMaxAtendimento(scOrigem.getDtMaxAtendimento());	
		getSolicitacaoComprasFacade().persistirSolicitacaoDeCompra(novaSc, null);
		logInfo(String.format("SC %d gerada.", novaSc.getNumero()));
		parcela.setSolicitacaoCompra(novaSc);
	}

	/**
	 * Gera SS.
	 * 
	 * @param itemAf Item de AF.
	 * @param parcela Parcela do item da AF.
	 * @throws BaseException 
	 */
	public void gerarSs(ScoItemAutorizacaoForn itemAf,
			ParcelaItemAutFornecimentoVO parcela) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		
		ScoSolicitacaoServico ssOrigem = itemAf.getScoFaseSolicitacao().get(0).getSolicitacaoServico();
		ScoSolicitacaoServico novaSs = new ScoSolicitacaoServico();
		Date now = new Date();
		
		// Ponto de parada do tipo CP..
		ScoPontoParadaSolicitacao ptParada = getComprasCadastrosBasicosFacade()
				.obterPontoParadaPorTipo(DominioTipoPontoParada.CP);

		// ..deve estar ativo.
		if (ptParada != null && DominioSituacao.A.equals(ptParada.getSituacao())) {
			novaSs.setPontoParada(ptParada);
		} else {
			ExceptionCode.MENSAGEM_PONTO_PARADA_NAO_ENCONTRADO
					.throwException(DominioTipoPontoParada.CP);
		}
		
		// Ponto de parada do tipo LI..
		ScoPontoParadaSolicitacao ptParadaLocAtual = getComprasCadastrosBasicosFacade()
				.obterPontoParadaPorTipo(DominioTipoPontoParada.LI);

		// ..deve estar ativo.
		if (ptParadaLocAtual != null && DominioSituacao.A.equals(ptParadaLocAtual.getSituacao())) {
			novaSs.setPontoParadaLocAtual(ptParadaLocAtual);
		} else {
			ExceptionCode.MENSAGEM_PONTO_PARADA_NAO_ENCONTRADO
					.throwException(DominioTipoPontoParada.LI);
		}
		
		novaSs.setServidor(servidorLogado);
		novaSs.setServico(ssOrigem.getServico());
		novaSs.setCentroCusto(servidorLogado.getCentroCustoLotacao());
		novaSs.setCentroCustoAplicada(parcela.getCentroCusto());
		novaSs.setServidorAutorizador(getChefeCompras());
		novaSs.setDtSolicitacao(now);
		novaSs.setDtDigitacao(now);
		novaSs.setQtdeSolicitada(parcela.getQtdeDetalhada());
		novaSs.setIndUrgente(ssOrigem.getIndUrgente());
		novaSs.setIndDevolucao(ssOrigem.getIndDevolucao());
		novaSs.setOrcamentoPrevio(ssOrigem.getOrcamentoPrevio());
		novaSs.setModalidadeLicitacao(ssOrigem.getModalidadeLicitacao());
		novaSs.setServidorComprador(ssOrigem.getServidorComprador());
		novaSs.setNaturezaDespesa(ssOrigem.getNaturezaDespesa());
		novaSs.setVerbaGestao(parcela.getVerbaGestao());
		novaSs.setCctCodigoAutzTecnica(ssOrigem.getCctCodigoAutzTecnica());
		novaSs.setAplicacao(ssOrigem.getAplicacao());
		novaSs.setJustificativaUso(ssOrigem.getJustificativaUso());
		novaSs.setDtAutorizacao(now);
		novaSs.setValorUnitPrevisto(new BigDecimal(parcela.getValorDetalhado()));
		novaSs.setDtAnalise(ssOrigem.getDtAnalise());
		novaSs.setDtEncerramento(ssOrigem.getDtEncerramento());
		novaSs.setMotivoUrgencia(ssOrigem.getMotivoUrgencia());
		novaSs.setJustificativaDevolucao(ssOrigem.getJustificativaDevolucao());
		novaSs.setNroInvestimento(ssOrigem.getNroInvestimento());
		novaSs.setDescricao(ssOrigem.getDescricao());
		novaSs.setIndEfetivada(false);
		novaSs.setIndExclusao(false);
		novaSs.setIndPrioridade(ssOrigem.getIndPrioridade());
		novaSs.setMotivoPrioridade(ssOrigem.getMotivoPrioridade());
		novaSs.setIndExclusivo(ssOrigem.getIndExclusivo());
		novaSs.setJustificativaExclusividade(ssOrigem.getJustificativaExclusividade());
		novaSs.setDtMaxAtendimento(ssOrigem.getDtMaxAtendimento());
		getSolicitacaoServicoFacade().persistirSolicitacaoDeServico(novaSs, null);
		logInfo(String.format("SS %d gerada.", novaSs.getNumero()));
		parcela.setSolicitacaoServico(novaSs);
	}

	/**
	 * Altera centro de custo ou verba de gestão de uma solicitação com base na
	 * solicitação do item de uma AF.
	 * 
	 * @param tipoSolicitacao Tipo de solicitação.
	 * @param itemAf Item de uma AF.
	 * @param parcela VO contendo a solicitação a ser alterada.
	 * @throws BaseException
	 */
	public void alterarSolicitacao(DominioTipoSolicitacao tipoSolicitacao,
			ScoItemAutorizacaoForn itemAf,
			ParcelaItemAutFornecimentoVO parcela)
			throws BaseException {
		switch (tipoSolicitacao) {
		case SC:
			ScoSolicitacaoDeCompra scAlterada = parcela.getSolicitacaoCompra();
			ScoSolicitacaoDeCompra scClone = getSolicitacaoComprasFacade().clonarSolicitacaoDeCompra(scAlterada);
			scAlterada.setCentroCustoAplicada(parcela.getCentroCusto());
			scAlterada.setVerbaGestao(parcela.getVerbaGestao());
			getSolicitacaoComprasFacade().persistirSolicitacaoDeCompra(scAlterada, scClone);
			break;
		case SS:
			ScoSolicitacaoServico ssAlterada = parcela.getSolicitacaoServico();
			ScoSolicitacaoServico ssClone = getSolicitacaoServicoFacade().clonarSolicitacaoServico(ssAlterada);
			ssAlterada.setCentroCustoAplicada(parcela.getCentroCusto());
			ssAlterada.setVerbaGestao(parcela.getVerbaGestao());
			getSolicitacaoServicoFacade().persistirSolicitacaoDeServico(ssAlterada, ssClone);
			break;
			
		default: throw new IllegalArgumentException(tipoSolicitacao.toString());
		}
	}

	/**
	 * Gera uma nova solicitação, um andamento para a solicitação, um novo item
	 * do PAC, uma nova proposta para o item do pac, um novo complemento para a
	 * autorização de fornecimento, um novo item de autorização de fornecimento,
	 * uma nova parcela de liberação e atualiza o saldo do item original da AF.
	 * 
	 * @param tipoSolicitacao Tipo de solicitação.
	 * @param itemAf Item de AF.
	 * @param parcela Parcela do item da AF.
	 * @param prevEntrega 
	 * @param valorParcela 
	 * @param qtdeParcelaAf 
	 * @param planejada 
	 * @param id 
	 * @throws BaseException 
	 */
	public ScoAutorizacaoForn gerarTudo(DominioTipoSolicitacao tipoSolicitacao,
			ScoItemAutorizacaoForn itemAf,
			ParcelaItemAutFornecimentoVO parcela,
			Date prevEntrega, Double valorParcela, Integer qtdeParcelaAf,
			DominioSimNao planejada)
			throws BaseException {
		switch (tipoSolicitacao) {
		case SC:
			gerarSc(itemAf, parcela);
			break;
			
		case SS:
			gerarSs(itemAf, parcela);
			break;
			
		default: throw new IllegalArgumentException(tipoSolicitacao.toString());
		}

		return gerarItemPacAf(tipoSolicitacao, itemAf, parcela,
				prevEntrega, valorParcela, qtdeParcelaAf, planejada);
	}

	/**
	 * Gera um novo item do PAC, uma nova proposta, um novo complemento para a
	 * autorização de Fornecimento e uma nova parcela de liberação, será
	 * atualizado o saldo do item original da AF.
	 * 
	 * @param tipoSolicitacao
	 * @param itemAf
	 * @param parcela
	 * @param previsaoEntrega 
	 * @param valorParcelaAf 
	 * @param qtdParcelaAf 
	 * @param indPlanejada 
	 * @param id 
	 * @throws ApplicationBusinessException
	 */
	public ScoAutorizacaoForn gerarItemPacAf(DominioTipoSolicitacao tipoSolicitacao,
			ScoItemAutorizacaoForn itemAf,
			ParcelaItemAutFornecimentoVO parcela,
			Date previsaoEntrega, Double valorParcelaAf, Integer qtdParcelaAf,
			DominioSimNao indPlanejada)
			throws BaseException {
		// Item do PAC
		ScoItemLicitacao itemPacOrigem = itemAf.getItemPropostaFornecedor().getItemLicitacao();
		Integer pacId = itemPacOrigem.getId().getLctNumero();
		Short proxItem = getScoItemLicitacaoDAO().proximosItensLicitacao(pacId).shortValue();
		ScoItemLicitacaoId novoItemPacId = new ScoItemLicitacaoId(pacId, proxItem);
		ScoItemLicitacao novoItemPac = new ScoItemLicitacao();
		novoItemPac.setLicitacao(itemPacOrigem.getLicitacao());
		novoItemPac.setId(novoItemPacId);
		novoItemPac.setClassifItem(proxItem);
		novoItemPac.setValorUnitario(itemPacOrigem.getValorUnitario());
		novoItemPac.setExclusao(false);
		novoItemPac.setPropostaEscolhida(itemPacOrigem.getPropostaEscolhida());
		novoItemPac.setEmAf(true);
		novoItemPac.setValorOriginalItem(itemPacOrigem.getValorOriginalItem());
		novoItemPac.setJulgParcial(itemPacOrigem.getJulgParcial());
		novoItemPac.setDtJulgParcial(itemPacOrigem.getDtJulgParcial());
		novoItemPac.setServidorJulgParcial(itemPacOrigem.getServidorJulgParcial());	
		getPacFacade().inserir(novoItemPac);
		
		logInfo(String.format("Item do PAC [lctNumero = %d, numero = %d] gerado.", 
				novoItemPac.getId().getLctNumero(), novoItemPac.getId().getNumero()));
		
		Object solicitacao;
		
		switch (tipoSolicitacao) {
		case SC:
			solicitacao = parcela.getSolicitacaoCompra();
			break;
			
		case SS:
			solicitacao = parcela.getSolicitacaoServico();
			break;
			
		default: throw new IllegalArgumentException(tipoSolicitacao.toString());
		}
		
		//  Fase de Solicitação do Item do PAC
		ScoFaseSolicitacao faseNovoItemPac = getFaseSolicitacao(tipoSolicitacao, solicitacao);		
		faseNovoItemPac.setItemLicitacao(novoItemPac);		
		getComprasFacade().inserirScoFaseSolicitacao(faseNovoItemPac);
		
		logInfo(String.format("Fase de solicitação do item do PAC %d gerada.",
				faseNovoItemPac.getNumero()));
		
		// Condicoes de Pagamento
		for (ScoCondicaoPgtoLicitacao condPgtoOrigem : itemPacOrigem.getCondicoesPagamento()) {
			ScoCondicaoPgtoLicitacao novaCondPgto = new ScoCondicaoPgtoLicitacao();
			novaCondPgto.setNumero(condPgtoOrigem.getNumero());
			novaCondPgto.setItemLicitacao(novoItemPac);
			novaCondPgto.setFormaPagamento(condPgtoOrigem.getFormaPagamento());
			novaCondPgto.setPercAcrescimo(condPgtoOrigem.getPercAcrescimo());
			novaCondPgto.setPercDesconto(condPgtoOrigem.getPercDesconto());
			getPacFacade().persistir(novaCondPgto);
			
			logInfo(String.format("Condição de pagamento %d do item do PAC gerada.", 
					novaCondPgto.getSeq()));
			
			// Parcelas
			for (ScoParcelasPagamento parcOrigem : condPgtoOrigem.getParcelas()) {
				ScoParcelasPagamento novaParcela = new ScoParcelasPagamento();
				novaParcela.setCondicaoPgtoLicitacao(novaCondPgto);
				novaParcela.setParcela(parcOrigem.getParcela());
				novaParcela.setPrazo(parcOrigem.getPrazo());
				novaParcela.setPercPagamento(parcOrigem.getPercPagamento());
				novaParcela.setValorPagamento(parcOrigem.getValorPagamento());
				getComprasFacade().persistirParcelaPagamento(novaParcela);
				
				logInfo(String.format("Parcela de condição de pagamento " +
						"do item do PAC %d gerada.", novaParcela.getSeq()));
			}
		}
		
		// Item de Proposta de Fornecedor
		ScoItemPropostaFornecedor novoItemPropForn = gerarItemPropostaFornecedor(
				itemAf.getItemPropostaFornecedor(), novoItemPac, parcela);

		// Complemento de AF
		ScoAutorizacaoForn novaAf = gerarComplementoAf(parcela,
				itemAf.getAutorizacoesForn(), tipoSolicitacao,
				solicitacao, novoItemPropForn, pacId);
		
		// Item de AF
		ScoItemAutorizacaoForn novoItemAf = new ScoItemAutorizacaoForn();
		novoItemAf.setAutorizacoesForn(novaAf);
		ScoItemAutorizacaoFornId novoItemAfId = new ScoItemAutorizacaoFornId();
		novoItemAfId.setAfnNumero(novaAf.getNumero());
		novoItemAfId.setNumero(1);
		novoItemAf.setId(novoItemAfId);
		novoItemAf.setItemPropostaFornecedor(novoItemPropForn);
		novoItemAf.setUnidadeMedida(itemAf.getUnidadeMedida());
		
		switch (tipoSolicitacao) {
		case SC:
			ScoSolicitacaoDeCompra sc = (ScoSolicitacaoDeCompra) solicitacao;
			novoItemAf.setQtdeSolicitada(sc.getQtdeSolicitada().intValue());
			novoItemAf.setValorUnitario(itemAf.getValorUnitario());
			break;
			
		case SS:
			// ScoSolicitacaoServico ss = (ScoSolicitacaoServico) solicitacao;
			novoItemAf.setQtdeSolicitada(1); // #32041 ss.getQtdeSolicitada().intValue());
			novoItemAf.setValorUnitario((novoItemPropForn.getValorUnitario() == null ? 0.0 : novoItemPropForn.getValorUnitario().doubleValue())
					* (itemPacOrigem.getFrequenciaEntrega() == null ? 0 : itemPacOrigem.getFrequenciaEntrega()));// #32041 ss.getValorUnitPrevisto().doubleValue());
			valorParcelaAf = novoItemAf.getValorUnitario();
			break;
			
		default: throw new IllegalArgumentException(tipoSolicitacao.toString());
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		novoItemAf.setFatorConversao(itemAf.getFatorConversao());
		novoItemAf.setServidor(servidorLogado);
		novoItemAf.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.AE);
		novoItemAf.setPercIpi(itemAf.getPercIpi());
		novoItemAf.setPercAcrescimoItem(itemAf.getPercAcrescimoItem());
		novoItemAf.setPercDesconto(itemAf.getPercDesconto());
		novoItemAf.setPercAcrescimo(itemAf.getPercAcrescimo());
		novoItemAf.setPercDescontoItem(itemAf.getPercDescontoItem());
		novoItemAf.setIndExclusao(false);
		novoItemAf.setSequenciaAlteracao(0);
		novoItemAf.setMarcaComercial(itemAf.getMarcaComercial());
		novoItemAf.setNomeComercial(itemAf.getNomeComercial());
		novoItemAf.setIndEstorno(false);
		novoItemAf.setPercVarPreco(itemAf.getPercVarPreco());
		novoItemAf.setIndRecebimento(itemAf.getIndRecebimento());
		novoItemAf.setValorEfetivado(0.0);
		novoItemAf.setIndContrato(itemAf.getIndContrato());
		novoItemAf.setIndConsignado(itemAf.getIndConsignado());
		novoItemAf.setIndProgrEntgAuto(itemAf.getIndProgrEntgAuto());
		novoItemAf.setIndAnaliseProgrPlanej(itemAf.getIndAnaliseProgrPlanej());
		novoItemAf.setIndProgrEntgBloq(itemAf.getIndProgrEntgBloq());
		novoItemAf.setUmdCodigoForn(itemAf.getUmdCodigoForn());
		novoItemAf.setFatorConversaoForn(itemAf.getFatorConversaoForn());
		novoItemAf.setIndPreferencialCum(itemAf.getIndPreferencialCum());
		novoItemAf.setModeloComercial(itemAf.getModeloComercial());
		getScoItemAutorizacaoFornRN().inserirItemAutorizacaoFornecimento(novoItemAf);
		
		qtdParcelaAf = novoItemAf.getQtdeSolicitada();
		
		logInfo(String.format("Item de AF [afnNumero = %d, numero = %d] gerado.", 
				novoItemAf.getId().getAfnNumero(), novoItemAf.getId().getNumero()));
		
		// Journal de Item de AF
		ScoItemAutorizacaoFornJn novaIafJn = new ScoItemAutorizacaoFornJn();
		novaIafJn.setJnUser(servidorLogado.getUsuario());
		novaIafJn.setJnOperation(DominioOperacoesJournal.INS.toString());
		novaIafJn.setScoItemAutorizacaoForn(novoItemAf);
		novaIafJn.setNumero(novoItemAf.getId().getNumero());
		novaIafJn.setItemPropostaFornecedor(novoItemAf.getItemPropostaFornecedor());
		novaIafJn.setIndEstorno(novoItemAf.getIndEstorno());
		novaIafJn.setUnidadeMedida(novoItemAf.getUnidadeMedida());
		novaIafJn.setQtdeSolicitada(novoItemAf.getQtdeSolicitada());
		novaIafJn.setFatorConversao(novoItemAf.getFatorConversao());
		novaIafJn.setValorUnitario(novoItemAf.getValorUnitario());
		novaIafJn.setSerMatricula(novoItemAf.getServidor().getId().getMatricula());
		novaIafJn.setSerVinCodigo(novoItemAf.getServidor().getId().getVinCodigo().intValue());
		
		if (novoItemAf.getServidorEstorno() != null){
			novaIafJn.setSerMatriculaEstorno(novoItemAf.getServidorEstorno().getId().getMatricula());
			novaIafJn.setSerVinCodigoEstorno(novoItemAf.getServidorEstorno().getId().getVinCodigo().intValue());
			novaIafJn.setDtEstorno(novoItemAf.getDtEstorno());
		}
		
		novaIafJn.setIndSituacao(novoItemAf.getIndSituacao());
		novaIafJn.setPercIpi(novoItemAf.getPercIpi());
		novaIafJn.setPercAcrescimoItem(novoItemAf.getPercAcrescimoItem());
		novaIafJn.setPercDesconto(novoItemAf.getPercDesconto());
		novaIafJn.setPercAcrescimo(novoItemAf.getPercAcrescimo());
		novaIafJn.setPercDescontoItem(novoItemAf.getPercDescontoItem());
		novaIafJn.setDtExclusao(novoItemAf.getDtExclusao());
		novaIafJn.setIndExclusao(novoItemAf.getIndExclusao());
		novaIafJn.setSequenciaAlteracao(novoItemAf.getSequenciaAlteracao());
		novaIafJn.setMarcaComercial(novoItemAf.getMarcaComercial());
		novaIafJn.setNomeComercial(novoItemAf.getNomeComercial());
		novaIafJn.setPercVarPreco(novoItemAf.getPercVarPreco().doubleValue());
		novaIafJn.setQtdeRecebida(novoItemAf.getQtdeRecebida());
		novaIafJn.setIndRecebimento(novoItemAf.getIndRecebimento());
		novaIafJn.setValorEfetivado(novoItemAf.getValorEfetivado());
		novaIafJn.setJnDateTime(new Date());
		novaIafJn.setAfnNumero(novaAf.getNumero());
		
		getScoItemAutorizacaoFornJnRN().inserirItemAutorizacaoFornecimentoJn(novaIafJn);		
		logInfo(String.format("Journal do item de AF %d gerada.", novaIafJn.getSeqJn()));
		
		// Fase Solicitação do Item de AF
		ScoFaseSolicitacao faseNovoItemAf = getFaseSolicitacao(tipoSolicitacao, solicitacao);		
		faseNovoItemAf.setItemAutorizacaoForn(novoItemAf);
		
		getComprasFacade().inserirScoFaseSolicitacao(faseNovoItemAf);
		logInfo(String.format("Fase de solicitação do item de AF %d gerada.", faseNovoItemAf.getNumero()));
		
		// Parcela
		ScoProgEntregaItemAutorizacaoFornecimentoId progEntregaId = new ScoProgEntregaItemAutorizacaoFornecimentoId();
		progEntregaId.setIafAfnNumero(novoItemAfId.getAfnNumero());
		progEntregaId.setIafNumero(novoItemAfId.getNumero());
		
		Integer numeroParcela = getScoProgEntregaItemAutorizacaoFornecimentoON()
				.obterMaxNumeroParcela(novoItemAfId.getAfnNumero(),
						novoItemAfId.getNumero()) + 1;
		
		progEntregaId.setParcela(numeroParcela);
		progEntregaId.setSeq(1);
		
		ScoProgEntregaItemAutorizacaoFornecimento progEntrega = getScoProgEntregaItemAutorizacaoFornecimentoON()
				.atualizarProgEntregaItemAutorizacaoFornecimento(previsaoEntrega, valorParcelaAf, qtdParcelaAf, 
						indPlanejada, progEntregaId, tipoSolicitacao, true);
		
		getScoSolicitacaoProgramacaoEntregaON()
				.gerarSolicitacaoProgramacaoEntrega(parcela, progEntrega,
						novoItemAf, itemAf, tipoSolicitacao);
		
		return novaAf;
	}
	
	/**
	 * Gera item de proposta de fornecedor.
	 * 
	 * @param itemAf Item de AF
	 * @param novoItemPac Item do PAC
	 * @param parcela Parcela
	 */
	private ScoItemPropostaFornecedor gerarItemPropostaFornecedor(ScoItemPropostaFornecedor itemPropFornOrigem,
			ScoItemLicitacao novoItemPac, ParcelaItemAutFornecimentoVO parcela) throws BaseException {
		ScoItemPropostaFornecedor novoItemPropForn = new ScoItemPropostaFornecedor();
		novoItemPropForn.setPropostaFornecedor(itemPropFornOrigem.getPropostaFornecedor());
		Integer pfrnLctNumero = itemPropFornOrigem.getId().getPfrLctNumero();
		Integer pfrnNumero = itemPropFornOrigem.getId().getPfrFrnNumero();
		
		Short numero = getScoItemPropostaFornecedorDAO()
				.obterMaxNumeroItemPropostaFornecedor(pfrnNumero, pfrnLctNumero, true);

		novoItemPropForn.setId(new ScoItemPropostaFornecedorId(pfrnLctNumero,
				pfrnNumero, numero));
		
		novoItemPropForn.setItemLicitacao(novoItemPac);
		novoItemPropForn.setUnidadeMedida(itemPropFornOrigem.getUnidadeMedida());
		novoItemPropForn.setMoeda(itemPropFornOrigem.getMoeda());
		novoItemPropForn.setQuantidade(parcela.getQtdeDetalhada().longValue());
		novoItemPropForn.setIndEscolhido(itemPropFornOrigem.getIndEscolhido());
		novoItemPropForn.setIndComDesconto(itemPropFornOrigem.getIndComDesconto());
		novoItemPropForn.setIndNacional(itemPropFornOrigem.getIndNacional());
		novoItemPropForn.setIndDesclassificado(itemPropFornOrigem.getIndDesclassificado());
		novoItemPropForn.setFatorConversao(itemPropFornOrigem.getFatorConversao());
		novoItemPropForn.setMarcaComercial(itemPropFornOrigem.getMarcaComercial());
		novoItemPropForn.setNomeComercial(itemPropFornOrigem.getNomeComercial());
		novoItemPropForn.setCriterioEscolhaProposta(itemPropFornOrigem.getCriterioEscolhaProposta());
		novoItemPropForn.setPercAcrescimo(itemPropFornOrigem.getPercAcrescimo());
		novoItemPropForn.setPercIpi(itemPropFornOrigem.getPercIpi());
		novoItemPropForn.setPercDesconto(itemPropFornOrigem.getPercDesconto());
		novoItemPropForn.setValorUnitario(itemPropFornOrigem.getValorUnitario());
		novoItemPropForn.setObservacao(itemPropFornOrigem.getObservacao());
		novoItemPropForn.setMotDesclassif(itemPropFornOrigem.getMotDesclassif());
		novoItemPropForn.setIndExclusao(false);
		novoItemPropForn.setIndAnalisadoPt(itemPropFornOrigem.getIndAnalisadoPt());
		novoItemPropForn.setIndAutorizUsr(itemPropFornOrigem.getIndAutorizUsr());
		novoItemPropForn.setJustifAutorizUsr(itemPropFornOrigem.getJustifAutorizUsr());
		novoItemPropForn.setApresentacao(itemPropFornOrigem.getApresentacao());
		novoItemPropForn.setNroOrcamento(itemPropFornOrigem.getNroOrcamento());
		novoItemPropForn.setDtEscolha(itemPropFornOrigem.getDtEscolha());
		novoItemPropForn.setModeloComercial(itemPropFornOrigem.getModeloComercial());
		novoItemPropForn.setDtEntregaAmostra(itemPropFornOrigem.getDtEntregaAmostra());
		getPacFacade().inserirItemPropostaFornecedor(novoItemPropForn);
		
		logInfo(String.format("Item de proposta de fornecedor " +
				"[pfrLctNumero = %d, pfrFrnNumero = %d, numero = %d] gerado.", 
				novoItemPropForn.getId().getPfrLctNumero(), 
				novoItemPropForn.getId().getPfrFrnNumero(), 
				novoItemPropForn.getId().getNumero()));
		
		// Condição de Pagamento
		boolean condPropFound = false;
		ScoCondicaoPagamentoPropos condPgtoPropOrigem = itemPropFornOrigem.getCondicaoPagamentoPropos();
		
		// Se a condição for do item, então gera uma nova condição.
		if (condPgtoPropOrigem != null) {
			condPropFound = true;
			ScoCondicaoPagamentoPropos novaCondPgtoProp = new ScoCondicaoPagamentoPropos();
			novaCondPgtoProp.setFormaPagamento(condPgtoPropOrigem.getFormaPagamento());
			novaCondPgtoProp.setIndCondEscolhida(condPgtoPropOrigem.getIndCondEscolhida());
			novaCondPgtoProp.setItemPropostaFornecedor(novoItemPropForn);
			novaCondPgtoProp.setNumero(condPgtoPropOrigem.getNumero());
			novaCondPgtoProp.setPercAcrescimo(condPgtoPropOrigem.getPercAcrescimo());
			novaCondPgtoProp.setPercDesconto(condPgtoPropOrigem.getPercDesconto());
			
			logInfo(String.format("Condição de pagamento de proposta de fornecedor " +
					"%d gerada", novaCondPgtoProp.getNumero()));
			
			List<ScoParcelasPagamento> parcelas = new ArrayList<ScoParcelasPagamento>();
			
			for (ScoParcelasPagamento parcelaOrigem : condPgtoPropOrigem.getParcelas()) {
				ScoParcelasPagamento novaParcela = new ScoParcelasPagamento();
				novaParcela.setCondicaoPagamentoPropos(novaCondPgtoProp);
				novaParcela.setParcela(parcelaOrigem.getParcela());
				novaParcela.setPercPagamento(parcelaOrigem.getPercPagamento());
				novaParcela.setPrazo(parcelaOrigem.getPrazo());
				novaParcela.setUltimaParcela(parcelaOrigem.getUltimaParcela());
				novaParcela.setValorPagamento(parcelaOrigem.getValorPagamento());
				parcelas.add(novaParcela);
				
				logInfo(String.format("Parcela de condição de pagamento de " +
						"proposta de fornecedor %d gerada.", novaParcela.getSeq()));
			}

			getComprasFacade().inserir(novaCondPgtoProp, parcelas);
			novoItemPropForn.setCondicaoPagamentoPropos(novaCondPgtoProp);
		// Se a condição for da proposta e escolhida, então apenas referencia.
		} else {
			for (ScoCondicaoPagamentoPropos condProp : itemPropFornOrigem.getPropostaFornecedor().getCondicoesPagamento()) {
				// A proposta deve ter uma condição de pagamento escolhida.
				if (Boolean.TRUE.equals(condProp.getIndCondEscolhida())) {
					novoItemPropForn.setCondicaoPagamentoPropos(condProp);
					condPropFound = true;
					break;
				}
			}
		}	
		
		if (!condPropFound) {
			ExceptionCode.MENSAGEM_COND_PGTO_NAO_ENCONTRADA.throwException(
					itemPropFornOrigem.getId().getNumero(), 
					itemPropFornOrigem.getId().getPfrFrnNumero(), 
					itemPropFornOrigem.getId().getPfrLctNumero());
		} else {
			getPacFacade().inserirItemPropostaFornecedor(novoItemPropForn);
			
			logInfo(String.format("Condição de pagamento de proposta de fornecedor " +
					"%d associada ao item [pfrLctNumero = %d, pfrFrnNumero = %d, numero = %d].",
					novoItemPropForn.getCondicaoPagamentoPropos().getNumero(), 
					novoItemPropForn.getId().getPfrLctNumero(), 
					novoItemPropForn.getId().getPfrFrnNumero(), 
					novoItemPropForn.getId().getNumero()));
		}
		
		return novoItemPropForn;
	}
	
	private ScoAutorizacaoForn gerarComplementoAf(ParcelaItemAutFornecimentoVO parcela, ScoAutorizacaoForn afOrigem,
			DominioTipoSolicitacao tipoSolicitacao, Object solicitacao,
			ScoItemPropostaFornecedor novoItemPropForn, Integer pacId) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Complemento AF
		ScoAutorizacaoForn novaAf = new ScoAutorizacaoForn();
		novaAf.setServidor(servidorLogado);
		
		switch (tipoSolicitacao) {
		case SC:
			ScoSolicitacaoDeCompra sc = (ScoSolicitacaoDeCompra) solicitacao;
			novaAf.setVerbaGestao(sc.getVerbaGestao());
			break;
			
		case SS:
			ScoSolicitacaoServico ss = (ScoSolicitacaoServico) solicitacao;
			novaAf.setVerbaGestao(ss.getVerbaGestao());
			break;
			
		default: throw new IllegalArgumentException(tipoSolicitacao.toString());
		}
		
		novaAf.setMoeda(afOrigem.getMoeda());
		novaAf.setNaturezaDespesa(afOrigem.getNaturezaDespesa());
		novaAf.setPropostaFornecedor(afOrigem.getPropostaFornecedor());
		novaAf.setCondicaoPagamentoPropos(novoItemPropForn.getCondicaoPagamentoPropos());
		novaAf.setDtGeracao(new Date());
		Short max = getScoAutorizacaoFornDAO().obterMaxNroComplemento(pacId);
		
		if (max != null) {
			max ++;
		} else {
			max = 1;
		}
		
		novaAf.setNroComplemento(max);
		novaAf.setSituacao(DominioSituacaoAutorizacaoFornecimento.AE);
		novaAf.setGeracao(afOrigem.getGeracao());
		novaAf.setMotivoAlteracaoAf(afOrigem.getMotivoAlteracaoAf());
		novaAf.setServidorAutorizado(getChefeCompras());
		novaAf.setDtPrevEntrega(afOrigem.getDtPrevEntrega());
		novaAf.setSequenciaAlteracao((short) 0);
		novaAf.setValorFrete(afOrigem.getValorFrete());
		novaAf.setObservacao(afOrigem.getObservacao());
		novaAf.setNroContrato(afOrigem.getNroContrato());
		novaAf.setExclusao(false);
		novaAf.setServidorAssinaCoord(afOrigem.getServidorAssinaCoord());
		novaAf.setAprovada(DominioAprovadaAutorizacaoForn.N);
		novaAf.setModalidadeEmpenho(afOrigem.getModalidadeEmpenho());
		novaAf.setImprRefContrato(afOrigem.getImprRefContrato());
		novaAf.setDtAssinaturaChefia(afOrigem.getDtAssinaturaChefia());
		novaAf.setDtAssinaturaCoord(afOrigem.getDtAssinaturaCoord());
		novaAf.setDtVenctoContrato(afOrigem.getDtVenctoContrato());
		novaAf.setServidorGestor(afOrigem.getServidorGestor());
		novaAf.setEntregaProgramada(afOrigem.getEntregaProgramada());
		getScoAutorizacaoFornRN().inserir(novaAf);
		logInfo(String.format("Complemento de AF %d gerado.", novaAf.getNumero()));
		
		//TODO migração arquitetura EVENTS
//		getEvents().raiseEvent("br.gov.mec.aghu.compras.complementoAfGerado", parcela, novaAf);
		
		// Journal do Complemento da AF
		ScoAutorizacaoFornJn afJn = new ScoAutorizacaoFornJn();
		afJn.setJnUser(servidorLogado.getUsuario());
		afJn.setJnOperation(DominioOperacoesJournal.INS.toString());
		afJn.setNumero(novaAf.getNumero());
		afJn.setServidor(novaAf.getServidor());
		afJn.setServidorControlado(novaAf.getServidorControlado());
		afJn.setVerbaGestao(novaAf.getVerbaGestao());
		afJn.setNaturezaDespesa(novaAf.getNaturezaDespesa());
		afJn.setPropostaFornecedor(novaAf.getPropostaFornecedor());
		afJn.setCondicaoPagamentoPropos(novaAf.getCondicaoPagamentoPropos());
		afJn.setServidorExcluido(novaAf.getServidorExcluido());
		afJn.setDtGeracao(novaAf.getDtGeracao());
		afJn.setSituacao(afOrigem.getSituacao());
		afJn.setJnDateTime(new Date());
		afJn.setMotivoAlteracaoAf(novaAf.getMotivoAlteracaoAf());
		afJn.setServidorAutorizado(novaAf.getServidorAutorizado());
		afJn.setDtEstorno(novaAf.getDtEstorno());
		afJn.setDtAlteracao(novaAf.getDtAlteracao());
		afJn.setDtPrevEntrega(novaAf.getDtPrevEntrega());
		afJn.setDtExclusao(novaAf.getDtExclusao());
		afJn.setSequenciaAlteracao(novaAf.getSequenciaAlteracao());
		afJn.setValorFrete(novaAf.getValorFrete());
		afJn.setObservacao(novaAf.getObservacao());
		afJn.setNroContrato(novaAf.getNroContrato());
		afJn.setIndExclusao(novaAf.getExclusao());
		afJn.setNroEmpenho(novaAf.getNroEmpenho());
		afJn.setServidorAssinaCoord(novaAf.getServidorAssinaCoord());
		afJn.setIndAprovada(novaAf.getAprovada());
		afJn.setModalidadeEmpenho(novaAf.getModalidadeEmpenho());
		afJn.setNroComplemento(max);
		afJn.setDtAssinaturaChefia(novaAf.getDtAssinaturaChefia());
		afJn.setDtAssinaturaCoord(novaAf.getDtAssinaturaCoord());
		afJn.setDtVenctoContrato(novaAf.getDtVenctoContrato());		
		afJn.setServidorGestor(novaAf.getServidorGestor());
		getScoAutorizacaoFornJnRN().inserirAutorizacaoFornecimentoJn(afJn);
		logInfo(String.format("Journal de complemento de AF %d gerada.", afJn.getSeq()));
		
		return novaAf;
	}
	
	/**
	 * Obtem nova fase de solicitação.
	 * 
	 * @param tipoSolicitacao Tipo de Solicitação
	 * @param solicitacao Solicitação (SC/SS)
	 * @return Nova Fase
	 */
	private ScoFaseSolicitacao getFaseSolicitacao(DominioTipoSolicitacao tipoSolicitacao, Object solicitacao) {
		ScoFaseSolicitacao faseSolicitacao = new ScoFaseSolicitacao();
		
		switch (tipoSolicitacao) {
		case SC:
			faseSolicitacao.setTipo(DominioTipoFaseSolicitacao.C);
			faseSolicitacao.setSolicitacaoDeCompra((ScoSolicitacaoDeCompra) solicitacao);
			break;
			
		case SS:
			faseSolicitacao.setTipo(DominioTipoFaseSolicitacao.S);
			faseSolicitacao.setSolicitacaoServico((ScoSolicitacaoServico) solicitacao);
			break;
			
		default: throw new IllegalArgumentException(tipoSolicitacao.toString());
		}
		
		faseSolicitacao.setExclusao(false);
		faseSolicitacao.setGeracaoAutomatica(true);
		return faseSolicitacao;
	}
	
	/**
	 * Obtem usuário chefe de compras parametrizado.
	 * 
	 * @return Usuário
	 * @throws ApplicationBusinessException 
	 */
	private RapServidores getChefeCompras() throws ApplicationBusinessException {
		Integer matricula = getParametroFacade()
				.obterAghParametro(AghuParametrosEnum.P_MATR_CHEFE_CPRAS)
				.getVlrNumerico().intValue();
		
		Short vinCodigo = getParametroFacade()
				.obterAghParametro(AghuParametrosEnum.P_VIN_CHEFE_CPRAS)
				.getVlrNumerico().shortValue();
		
		RapServidoresId chefeComprasId = new RapServidoresId(matricula, vinCodigo);
		RapServidores chefeCompras = getRegistroColaboradorFacade().obterServidor(chefeComprasId);
		return chefeCompras;
	}
	
	// Dependências
	
	protected ScoItemAutorizacaoFornRN getScoItemAutorizacaoFornRN() {
		return scoItemAutorizacaoFornRN;
	}
	
	protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}
	
	protected ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
		return scoSolicitacaoServicoDAO;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	protected ScoScJnDAO getScoScJnDAO() {
		return scoScJnDAO;
	}
	
	protected ScoSsJnDAO getScoSsJnDAO() {
		return scoSsJnDAO;
	}
	
	protected IPacFacade getPacFacade() {
		return pacFacade;
	}
	
	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}
	
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return solicitacaoComprasFacade;
	}
	
	protected ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return solicitacaoServicoFacade;
	}
	
	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}
	
	protected AutFornecimentoRN getAutFornecimentoRN() {
		return autFornecimentoRN;
	}
	
	protected ScoAutorizacaoFornJnRN getScoAutorizacaoFornJnRN() {
		return scoAutorizacaoFornJnRN;
	}
	
	protected ScoAutorizacaoFornRN getScoAutorizacaoFornRN() {
		return scoAutorizacaoFornRN;
	}
	
	protected ScoItemAutorizacaoFornJnRN getScoItemAutorizacaoFornJnRN() {
		return scoItemAutorizacaoFornJnRN;
	}
	
	protected ScoProgEntregaItemAutorizacaoFornecimentoON getScoProgEntregaItemAutorizacaoFornecimentoON() {
		return scoProgEntregaItemAutorizacaoFornecimentoON;
	}
	
	protected ScoSolicitacaoProgramacaoEntregaON getScoSolicitacaoProgramacaoEntregaON() {
		return scoSolicitacaoProgramacaoEntregaON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}