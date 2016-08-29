package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.vo.PriorizaEntregaVO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO;
import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO.TipoProgramacaoEntrega;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @author amenegotto
 *
 */
@Stateless
public class ScoSolicitacaoProgramacaoEntregaON extends BaseBusiness {

	@EJB
	private ScoAutorizacaoFornRN scoAutorizacaoFornRN;
	@EJB
	private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;
	@EJB
	private ScoSolicitacaoProgramacaoEntregaRN scoSolicitacaoProgramacaoEntregaRN;
	@EJB
	private ScoSolicitacaoProgramacaoEntregaGeracaoON scoSolicitacaoProgramacaoEntregaGeracaoON;

	private static final Log LOG = LogFactory.getLog(ScoSolicitacaoProgramacaoEntregaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

	@Inject
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	private static final long serialVersionUID = 7276860322721573921L;

	public enum ScoSolicitacaoProgramacaoEntregaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_QTDE_DISTRIBUIDA_MAIOR_RECEBIDA, MENSAGEM_VALOR_DISTRIBUIDO_MAIOR_RECEBIDO, MENSAGEM_QTDE_OBRIGATORIO, MENSAGEM_VALOR_OBRIGATORIO;
	}

	//	/**
	//	 * Persiste lista de solicitação de programação de entrega.
	//	 * 
	//	 * @param listaPea Lista a ser persistida.
	//	 * @param tipoSolicitacao Solicitação de compra ou serviço.
	//	 * @param progEntrega Programação de entrega (entidade pai).
	//	 * @param prevEntrega Data de previsão de entrega.
	//	 * @param valorParcela Valor da parcela.
	//	 * @param qtdeParcelaAf Quantidade de parcelas da AF.
	//	 * @param planejada Planejada ou não. 
	//	 * @param id ID da programação de entrega.
	//	 * @throws BaseException
	//	 */
		public void persistirListaSolicitacaoProgramacao(
				List<ParcelaItemAutFornecimentoVO> listaPea,
				DominioTipoSolicitacao tipoSolicitacao,
				ScoProgEntregaItemAutorizacaoFornecimento progEntrega, Date prevEntrega,
				Double valorParcela, Integer qtdeParcelaAf,
				DominioSimNao planejada,
				ScoProgEntregaItemAutorizacaoFornecimentoId id)
				throws BaseException {
			if (listaPea.isEmpty()) {
				ParcelaItemAutFornecimentoVO parcela = new ParcelaItemAutFornecimentoVO();
				parcela.setSeqDetalhe(null);
				parcela.setIafAfnNumero(progEntrega.getId().getIafAfnNumero());
				parcela.setIafNumero(Integer.valueOf(progEntrega.getId().getIafNumero()));
				parcela.setIndPrioridade((short)1);
				parcela.setSeq(Integer.valueOf(progEntrega.getId().getSeq()));
				parcela.setParcela(Integer.valueOf(progEntrega.getId().getParcela()));					
				if (tipoSolicitacao == DominioTipoSolicitacao.SC) {
					parcela.setQtdeDetalhada(progEntrega.getQtde());
					parcela.setQtdeEntregue(progEntrega.getQtdeEntregue());
				} else {
					parcela.setValorDetalhado(progEntrega.getValorTotal());
					parcela.setValorEfetivado(progEntrega.getValorEfetivado());
				}
				this.montarSolicitacaoProgEntrega(parcela, tipoSolicitacao,
						progEntrega, prevEntrega, valorParcela,
						qtdeParcelaAf, planejada, id);
			} else {			
				for (ParcelaItemAutFornecimentoVO parcela : listaPea) {
					this.montarSolicitacaoProgEntrega(parcela, tipoSolicitacao,
							progEntrega, prevEntrega, valorParcela,
							qtdeParcelaAf, planejada, id);
				}
			}
		}

	public ScoAutorizacaoForn montarSolicitacaoProgEntrega(ParcelaItemAutFornecimentoVO parcela, DominioTipoSolicitacao tipoSolicitacao,
			ScoProgEntregaItemAutorizacaoFornecimento progEntrega, Date prevEntrega, Double valorParcela, Integer qtdeParcelaAf, DominioSimNao planejada,
			ScoProgEntregaItemAutorizacaoFornecimentoId id) throws BaseException {
		ScoItemAutorizacaoForn itemAf = this.getScoItemAutorizacaoFornDAO().obterItemAutorizacaoFornPorId(id.getIafAfnNumero(), id.getIafNumero());

		ScoAutorizacaoForn novaAf = null;
		// Altera solicitação ou gera solicitação, item do PAC e/ou compl. AF.
		if (parcela.getTipoProgramacaoEntrega() != null) {
			switch (parcela.getTipoProgramacaoEntrega()) {
			case ALTERAR_SOLICITACAO:
				getScoSolicitacaoProgramacaoEntregaGeracaoON().alterarSolicitacao(tipoSolicitacao, itemAf, parcela);

				gerarSolicitacaoProgramacaoEntrega(parcela, progEntrega, itemAf, tipoSolicitacao);
				break;

			case GERAR_SOLICITACAO:
				switch (tipoSolicitacao) {
				case SC:
					getScoSolicitacaoProgramacaoEntregaGeracaoON().gerarSc(itemAf, parcela);
					break;
				case SS:
					getScoSolicitacaoProgramacaoEntregaGeracaoON().gerarSs(itemAf, parcela);
					break;

				default:
					throw new IllegalArgumentException(tipoSolicitacao.toString());
				}

				gerarSolicitacaoProgramacaoEntrega(parcela, progEntrega, itemAf, tipoSolicitacao);
				break;

			case GERAR_TUDO:
				novaAf = getScoSolicitacaoProgramacaoEntregaGeracaoON().gerarTudo(tipoSolicitacao, itemAf, parcela, prevEntrega, valorParcela, qtdeParcelaAf,
						planejada);
				break;

			case GERAR_ITEM_PAC_AF:
				novaAf = getScoSolicitacaoProgramacaoEntregaGeracaoON().gerarItemPacAf(tipoSolicitacao, itemAf, parcela, prevEntrega, valorParcela,
						qtdeParcelaAf, planejada);
				break;
			}
		} else {
			gerarSolicitacaoProgramacaoEntrega(parcela, progEntrega, itemAf, tipoSolicitacao);
		}

		// Evita gerar novos registros com base no que já foi gerado.
		parcela.setTipoProgramacaoEntrega(null);

		return novaAf;
	}

	/**
	 * Gera parcela para item de AF original.
	 * 
	 * @param parcela Parcela
	 * @param progEntrega Parcela
	 * @param itemAf Item de AF
	 * @param tipoSolicitacao Tipo de Solicitação
	 * @throws BaseException 
	 */
	public void gerarSolicitacaoProgramacaoEntrega(ParcelaItemAutFornecimentoVO parcela, ScoProgEntregaItemAutorizacaoFornecimento progEntrega,
			ScoItemAutorizacaoForn itemAf, DominioTipoSolicitacao tipoSolicitacao) throws BaseException {
		gerarSolicitacaoProgramacaoEntrega(parcela, progEntrega, itemAf, itemAf, tipoSolicitacao);
	}

	/**
	 * Gera parcela para item de AF gerado.
	 * 
	 * @param parcela Parcela
	 * @param progEntrega Parcela
	 * @param itemAf Item de AF
	 * @param tipoSolicitacao Tipo de Solicitação
	 * @throws BaseException 
	 */
	public void gerarSolicitacaoProgramacaoEntrega(ParcelaItemAutFornecimentoVO parcela, ScoProgEntregaItemAutorizacaoFornecimento progEntrega,
			ScoItemAutorizacaoForn itemAf, ScoItemAutorizacaoForn itemAfOrigem, DominioTipoSolicitacao tipoSolicitacao) throws BaseException {
		ScoSolicitacaoProgramacaoEntrega solic = null;

		if (parcela.getSeqDetalhe() == null) {
			solic = new ScoSolicitacaoProgramacaoEntrega();
		} else {
			solic = this.getScoSolicitacaoProgramacaoEntregaDAO().obterPorChavePrimaria(parcela.getSeqDetalhe());
		}

		solic.setIndPrioridade((short) parcela.getIndPrioridade());
		solic.setProgEntregaItemAf(progEntrega);
		solic.setScoItensAutorizacaoForn(itemAf);
		TipoProgramacaoEntrega tipoProgEntrega = parcela.getTipoProgramacaoEntrega();
		TipoProgramacaoEntrega gerarTudo = ParcelaItemAutFornecimentoVO.TipoProgramacaoEntrega.GERAR_TUDO;
		TipoProgramacaoEntrega gerarItemPacAf = ParcelaItemAutFornecimentoVO.TipoProgramacaoEntrega.GERAR_ITEM_PAC_AF;

		// Se gerar item de AF..
		if (gerarTudo.equals(tipoProgEntrega) || gerarItemPacAf.equals(tipoProgEntrega)) {
			// É atualizado a quantidade / valor (abatido) do item da autorização de
			// fornecimento original.
			abater(tipoSolicitacao, parcela, solic, itemAfOrigem);

			// Vincula item de AF de origem para somar saldo do item de AF
			// gerado quando este for excluído.
			solic.setItemAfOrigem(itemAfOrigem);

			// Seta para 1 a prioridade, pois so vai existir uma SC
			solic.setIndPrioridade((short) 1);
		}

		if (tipoSolicitacao == DominioTipoSolicitacao.SC) {
			if (parcela.getSolicitacaoCompra() != null) {
				solic.setSolicitacaoCompra(parcela.getSolicitacaoCompra());
			} else {
				solic.setSolicitacaoCompra(itemAf.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra());
			}
			solic.setQtde(parcela.getQtdeDetalhada());
			solic.setQtdeEntregue(parcela.getQtdeEntregue());
		} else {
			if (parcela.getSolicitacaoServico() != null) {
				solic.setSolicitacaoServico(parcela.getSolicitacaoServico());
			} else {
				solic.setSolicitacaoServico(itemAf.getScoFaseSolicitacao().get(0).getSolicitacaoServico());
			}
			solic.setValor(parcela.getValorDetalhado());
			solic.setValorEfetivado(parcela.getValorEfetivado());
		}

		getScoSolicitacaoProgramacaoEntregaRN().persistir(solic);

		// Seta ID da programação de entraga no VO para evitar nova inclusão.
		parcela.setSeqDetalhe(solic.getSeq());
	}

	/**
	 * Abate quantidade/valor do item de AF.
	 * 
	 * @param tipoSolicitacao Tipo de Solicitação
	 * @param parcela Parcela
	 * @param solic Programação de Entrega
	 * @throws BaseException 
	 */
	private void abater(DominioTipoSolicitacao tipoSolicitacao, ParcelaItemAutFornecimentoVO parcela, ScoSolicitacaoProgramacaoEntrega solic,
			ScoItemAutorizacaoForn itemAf) throws BaseException {
		switch (tipoSolicitacao) {
		case SC:
			int qtde = parcela.getQtdeDetalhada();

			if (solic.getQtde() != null) {
				qtde -= solic.getQtde();
			}

			qtde *= -1;
			itemAf.setQtdeSolicitada(itemAf.getQtdeSolicitada() + qtde);
			break;

		case SS:
			double valor = parcela.getValorDetalhado();

			if (solic.getValor() != null) {
				valor -= solic.getValor();
			}

			valor *= -1;
			itemAf.setValorUnitario(itemAf.getValorUnitario() + valor);
			break;

		default:
			throw new IllegalArgumentException(tipoSolicitacao.toString());
		}

		getScoItemAutorizacaoFornRN().atualizarItemAutorizacaoFornecimento(itemAf);
	}

	/**
	 * Exclui solicitação de programação de entrega e seus itens a partir do ID.
	 * 
	 * @param idParcela ID
	 * @throws BaseException 
	 */
	public void excluirListaSolicitacaoProgramacao(ScoProgEntregaItemAutorizacaoFornecimentoId idParcela) throws BaseException {
		List<ScoSolicitacaoProgramacaoEntrega> listaProgEntrega = this.getScoSolicitacaoProgramacaoEntregaDAO()
				.pesquisarSolicitacaoProgEntregaPorItemProgEntrega(idParcela.getIafAfnNumero(), Integer.valueOf(idParcela.getIafNumero()),
						Integer.valueOf(idParcela.getSeq()), Integer.valueOf(idParcela.getParcela()));

		for (ScoSolicitacaoProgramacaoEntrega parcela : listaProgEntrega) {
			ScoSolicitacaoProgramacaoEntrega solic = this.getScoSolicitacaoProgramacaoEntregaDAO().obterPorChavePrimaria(parcela.getSeq());
			excluirSolicitacaoProgramacao(solic);
		}
	}

	/**
	 * Exclui um ou mais itens de solicitação de programação de entrega.
	 * 
	 * @param listaPeaExclusao Itens a serem excluídos.
	 * @throws BaseException 
	 */
	public void excluirListaSolicitacaoProgramacao(List<ParcelaItemAutFornecimentoVO> listaPeaExclusao) throws BaseException {
		for (ParcelaItemAutFornecimentoVO parcela : listaPeaExclusao) {
			ScoSolicitacaoProgramacaoEntrega solic = this.getScoSolicitacaoProgramacaoEntregaDAO().obterPorChavePrimaria(parcela.getSeqDetalhe());

			excluirSolicitacaoProgramacao(solic);
		}
	}

	/**
	 * Exclui detalhe de solicitação de programação de entrega.
	 * 
	 * Se a mesma tiver gerado um item de AF, o vinculo deverá ser apagado, a AF
	 * com seu respectivo complemento e seu item, devem ficar com a situação de
	 * excluídos e o saldo desta parcela, somado ao do item da AF original.
	 * @throws BaseException
	 */
	private void excluirSolicitacaoProgramacao(ScoSolicitacaoProgramacaoEntrega solic) throws BaseException {
		ScoItemAutorizacaoForn itemAfOrigem = solic.getItemAfOrigem();

		if (itemAfOrigem != null) {
			ScoItemAutorizacaoForn itemAf = solic.getScoItensAutorizacaoForn();
			itemAf.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.EX);

			getScoItemAutorizacaoFornRN().atualizarItemAutorizacaoFornecimento(itemAf);
			ScoAutorizacaoForn af = itemAf.getAutorizacoesForn();
			af.setSituacao(DominioSituacaoAutorizacaoFornecimento.EX);
			getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(af);

			if (solic.getSolicitacaoCompra() != null) {
				itemAfOrigem.setQtdeRecebida(itemAfOrigem.getQtdeRecebida() + solic.getQtde());
			} else {
				itemAfOrigem.setValorEfetivado(itemAfOrigem.getValorEfetivado() + solic.getValor());
			}

			getScoItemAutorizacaoFornRN().atualizarItemAutorizacaoFornecimento(itemAfOrigem);
		}

		getScoSolicitacaoProgramacaoEntregaRN().remover(solic);
	}

	/**
	 * Pesquisa solicitacoes associadas a programacao (oes) de entregas para um item de AF, levando
	 * em consideracao se ja existe recebimento 
	 * @param peaIafAfnNumero
	 * @param peaIafNumero
	 * @param peaSeq
	 * @param peaParcela
	 * @param nrpSeq
	 * @param nroItem
	 * @param qtdLimite
	 * @param valorLimite
	 * @param tipoSolicitacao
	 * @return List
	 */
	public List<PriorizaEntregaVO> pesquisarSolicitacaoProgEntregaItemAf(Integer peaIafAfnNumero, Short peaIafNumero, Integer peaSeq, Integer peaParcela,
			Integer nrpSeq, Integer nroItem, Integer qtdLimite, Double valorLimite, DominioTipoSolicitacao tipoSolicitacao) {

		List<PriorizaEntregaVO> listaResultado = new ArrayList<PriorizaEntregaVO>();

		if (nrpSeq != null && nroItem != null) {
			listaResultado = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisarSolicitacaoProgEntregaComRecebimentoProvisorio(nrpSeq, nroItem);
		} else if (peaIafAfnNumero != null && peaIafNumero != null && peaSeq != null && peaParcela != null) {
			listaResultado = this.pesquisarListaSolicitacoesProgEntrega(peaIafAfnNumero, peaIafNumero, peaSeq, peaParcela, qtdLimite, valorLimite,
					tipoSolicitacao);
		}

		return listaResultado;
	}

	/**
	 * Retorna uma lista das solicitacoes associadas a determinada programacao de entrega de um item de AF quando nao existe recebimento previo.
	 * Neste caso faz o rateio da quantidade recebidas entre as solicitacoes. 
	 * @param peaIafAfnNumero
	 * @param peaIafNumero
	 * @param peaSeq
	 * @param peaParcela
	 * @param qtdLimite
	 * @param valorLimite
	 * @param tipoSolicitacao
	 * @return List
	 */
	public List<PriorizaEntregaVO> pesquisarListaSolicitacoesProgEntrega(Integer peaIafAfnNumero, Short peaIafNumero, Integer peaSeq, Integer peaParcela,
			Integer qtdLimite, Double valorLimite, DominioTipoSolicitacao tipoSolicitacao) {

		// lista de parcelas ja processadas, para nao trazer novamente
		List<Integer> listaParcelasProcessadas = new ArrayList<Integer>();
		listaParcelasProcessadas.add(peaParcela);

		// primeiro pega a parcela que foi selecionada na tela e coloca na lista resultado
		List<PriorizaEntregaVO> listaResultado = this.pesquisarListaSolicitacoesProgEntregaSelecionada(peaIafAfnNumero, peaIafNumero, peaSeq, peaParcela);

		if (tipoSolicitacao.equals(DominioTipoSolicitacao.SC)) {
			// apos pegar as solicitacoes da parcela selecionada, distribui as quantidades conforme o saldo em cada solicitacao.			
			Integer qtdExcedente = this.distribuirQtdeLimite(qtdLimite, listaResultado, peaParcela);

			while (qtdExcedente > 0) {
				// 	se sobrar algo, pega o número da próxima parcela que ele deve calcular 
				ScoSolicitacaoProgramacaoEntrega proximaProgramacao = this.getScoSolicitacaoProgramacaoEntregaDAO()
						.obterProximaSolicitacaoProgEntregaPorItemProgEntrega(peaIafAfnNumero, peaIafNumero.intValue(), peaSeq, listaParcelasProcessadas);

				// se nao existir proximaProgramacao cai fora
				if (proximaProgramacao == null) {
					qtdExcedente = 0;
				} else {
					// adiciona na lista de resultado as solicitacoes da proxima parcela
					listaResultado.addAll(this.pesquisarListaSolicitacoesProgEntregaSelecionada(proximaProgramacao.getProgEntregaItemAf().getId()
							.getIafAfnNumero(), proximaProgramacao.getProgEntregaItemAf().getId().getIafNumero().shortValue(), proximaProgramacao
							.getProgEntregaItemAf().getId().getSeq(), proximaProgramacao.getProgEntregaItemAf().getId().getParcela()));

					// distribui para a proxima parcela, até que nao exista mais saldo
					qtdExcedente = this.distribuirQtdeLimite(qtdExcedente, listaResultado, proximaProgramacao.getProgEntregaItemAf().getId().getParcela());

					// vou gravar o numero da parcela para nao processar novamente
					listaParcelasProcessadas.add(proximaProgramacao.getProgEntregaItemAf().getId().getParcela());
				}
			}
		} else {
			// apos pegar as solicitacoes da parcela selecionada, distribui as quantidades conforme o saldo em cada solicitacao.			
			Double valorExcedente = this.distribuirValorLimite(valorLimite, listaResultado, peaParcela);

			while (valorExcedente > 0.00) {
				// 	se sobrar algo, pega o número da próxima parcela que ele deve calcular 
				ScoSolicitacaoProgramacaoEntrega proximaProgramacao = this.getScoSolicitacaoProgramacaoEntregaDAO()
						.obterProximaSolicitacaoProgEntregaPorItemProgEntrega(peaIafAfnNumero, peaIafNumero.intValue(), peaSeq, listaParcelasProcessadas);

				// se nao existir proximaProgramacao cai fora
				if (proximaProgramacao == null) {
					valorExcedente = 0.00;
				} else {
					// adiciona na lista de resultado as solicitacoes da proxima parcela
					listaResultado.addAll(this.pesquisarListaSolicitacoesProgEntregaSelecionada(proximaProgramacao.getProgEntregaItemAf().getId()
							.getIafAfnNumero(), proximaProgramacao.getProgEntregaItemAf().getId().getIafNumero().shortValue(), proximaProgramacao
							.getProgEntregaItemAf().getId().getSeq(), proximaProgramacao.getProgEntregaItemAf().getId().getParcela()));

					// distribui para a proxima parcela, até que nao exista mais saldo
					valorExcedente = this.distribuirValorLimite(valorExcedente, listaResultado, proximaProgramacao.getProgEntregaItemAf().getId().getParcela());

					// vou gravar o numero da parcela para nao processar novamente
					listaParcelasProcessadas.add(proximaProgramacao.getProgEntregaItemAf().getId().getParcela());
				}
			}
		}

		return listaResultado;
	}

	/**
	 * Retorna uma lista das solicitacoes associadas a programacao de entrega de um item da AF selecionados na tela para posterior processamento
	 * @param peaIafAfnNumero
	 * @param peaIafNumero
	 * @param peaSeq
	 * @param peaParcela
	 * @return List
	 */
	private List<PriorizaEntregaVO> pesquisarListaSolicitacoesProgEntregaSelecionada(Integer peaIafAfnNumero, Short peaIafNumero, Integer peaSeq,
			Integer peaParcela) {

		List<PriorizaEntregaVO> listaResultado = new ArrayList<PriorizaEntregaVO>();
		List<ScoSolicitacaoProgramacaoEntrega> listaSpe = this.getScoSolicitacaoProgramacaoEntregaDAO().pesquisarSolicitacaoProgEntregaPorItemProgEntrega(
				peaIafAfnNumero, Integer.valueOf(peaIafNumero), peaSeq, peaParcela);

		if (listaSpe != null) {
			for (ScoSolicitacaoProgramacaoEntrega item : listaSpe) {
				Integer saldoSc = ((Integer) CoreUtil.nvl(item.getQtde(), 0)) - ((Integer) CoreUtil.nvl(item.getQtdeEntregue(), 0));
				Double saldoSs = ((Double) CoreUtil.nvl(item.getValor(), 0.00)) - ((Double) CoreUtil.nvl(item.getValorEfetivado(), 0.00));

				if (saldoSc > 0 || saldoSs > 0.00) {
					PriorizaEntregaVO objAdd = this.obterPriorizaEntregaVO(peaIafAfnNumero, peaIafNumero, peaSeq, peaParcela, null, null);

					if (item.getProgEntregaItemAf().getScoAfPedido() != null) {
						objAdd.setNumeroAfp(item.getProgEntregaItemAf().getScoAfPedido().getId().getNumero());
					}
					objAdd.setPrevisaoEntrega(item.getProgEntregaItemAf().getDtPrevEntrega());
					objAdd.setPrioridade(item.getIndPrioridade());
					objAdd.setSeqItemRecbXProgrEntrega(null);
					objAdd.setSeqSolicitacaoProgramacaoEntrega(item.getSeq());

					if (item.getSolicitacaoCompra() != null) {
						objAdd.setSolicitacaoCompra(item.getSolicitacaoCompra());
						objAdd.setSaldoSolicitacaoCompra(saldoSc);
						objAdd.setQtdeRecebidaSolicitacaoCompra(0);
					} else {
						objAdd.setSolicitacaoCompra(null);
						objAdd.setSaldoSolicitacaoCompra(null);
						objAdd.setQtdeRecebidaSolicitacaoCompra(null);
					}

					if (item.getSolicitacaoServico() != null) {
						objAdd.setSolicitacaoServico(item.getSolicitacaoServico());
						objAdd.setSaldoSolicitacaoServico(new BigDecimal(saldoSs.toString()));
						objAdd.setValorRecebidoSolicitacaoServico(BigDecimal.ZERO);
					} else {
						objAdd.setSolicitacaoServico(null);
						objAdd.setSaldoSolicitacaoServico(null);
						objAdd.setValorRecebidoSolicitacaoServico(null);
					}

					listaResultado.add(objAdd);
				}

			}
		}

		return listaResultado;
	}

	/**
	 * Instancia um PriorizaEntregaVO baseado nos valores passados por parametro
	 * @param peaIafAfnNumero
	 * @param peaIafNumero
	 * @param peaSeq
	 * @param peaParcela
	 * @param nrpSeq
	 * @param nroItem
	 * @return PriorizaEntregaVO
	 */
	private PriorizaEntregaVO obterPriorizaEntregaVO(Integer peaIafAfnNumero, Short peaIafNumero, Integer peaSeq, Integer peaParcela, Integer nrpSeq,
			Integer nroItem) {
		PriorizaEntregaVO objAdd = new PriorizaEntregaVO();

		objAdd.setRowId(RandomUtils.nextInt(0, 10000));
		objAdd.setNumeroAf(peaIafAfnNumero);
		objAdd.setNumeroItemAf(peaIafNumero);
		objAdd.setSeqProgEntrega(peaSeq);
		objAdd.setParcelaProgEntrega(peaParcela);
		objAdd.setSeqRecebimento(nrpSeq);
		objAdd.setItemRecebimento(nroItem);
		objAdd.setNumeroParcela(peaParcela);

		return objAdd;
	}

	/**
	 * Faz o rateio da quantidade digitada na tela do recebimento entre as solicitacoes,
	 * conforme o saldo existente em cada solicitacao. Retorna o excedente que poderá ser
	 * jogado na próxima parcela.
	 * @param qtdeLimite
	 * @param listaPriorizacao
	 * @return Integer
	 */
	private Integer distribuirQtdeLimite(Integer qtdeLimite, List<PriorizaEntregaVO> listaPriorizacao, Integer numeroParcela) {
		Integer saldoAEntregar = Integer.valueOf(qtdeLimite);

		for (PriorizaEntregaVO item : listaPriorizacao) {

			// se for a parcela que estamos trabalhando...
			if (item.getNumeroParcela().equals(numeroParcela)) {
				if (saldoAEntregar == 0) {
					item.setQtdeRecebidaSolicitacaoCompra(saldoAEntregar);
				} else {
					// Pega o que ja foi previamente recebido
					Integer previamenteRecebido = 0;
					if (item.getQtdeRecebidaSolicitacaoCompra() != null) {
						previamenteRecebido = item.getQtdeRecebidaSolicitacaoCompra();
					}

					// verifica se possui saldo no limite para entregar o saldo do item
					Integer saldoLimite = saldoAEntregar - item.getSaldoSolicitacaoCompra();

					// se tiver
					if (saldoLimite > 0) {
						// entrega todo saldo do item
						item.setQtdeRecebidaSolicitacaoCompra(previamenteRecebido + item.getSaldoSolicitacaoCompra());
						saldoAEntregar = saldoAEntregar - item.getSaldoSolicitacaoCompra();
					} else {
						// entrega somente até ter saldo. Tem que ser + pois - com - dá +
						Integer restante = item.getSaldoSolicitacaoCompra() + saldoLimite;
						item.setQtdeRecebidaSolicitacaoCompra(previamenteRecebido + restante);
						saldoAEntregar = saldoAEntregar - restante;
					}
				}
			}
		}

		return saldoAEntregar;
	}

	/**
	 * Faz o rateio do valor digitado na tela do recebimento entre as solicitacoes,
	 * conforme o saldo existente em cada solicitacao. Retorna o excedente que poderá ser
	 * jogado na próxima parcela.
	 * @param valorLimite
	 * @param listaPriorizacao
	 * @return Double
	 */
	private Double distribuirValorLimite(Double valorLimite, List<PriorizaEntregaVO> listaPriorizacao, Integer numeroParcela) {
		BigDecimal saldoAEntregar = BigDecimal.valueOf(valorLimite);

		for (PriorizaEntregaVO item : listaPriorizacao) {

			// se for a parcela que estamos trabalhando...
			if (item.getNumeroParcela().equals(numeroParcela)) {
				if (saldoAEntregar.compareTo(BigDecimal.ZERO) == 0) {
					item.setValorRecebidoSolicitacaoServico(BigDecimal.ZERO);
				} else {

					// Pega o que ja foi previamente recebido
					BigDecimal previamenteRecebido = BigDecimal.ZERO;
					if (item.getValorRecebidoSolicitacaoServico() != null) {
						previamenteRecebido = item.getValorRecebidoSolicitacaoServico();
					}

					// verifica se possui saldo no limite para entregar o saldo do item
					BigDecimal saldoLimite = saldoAEntregar.subtract(item.getSaldoSolicitacaoServico());

					// se tiver
					if (saldoLimite.compareTo(BigDecimal.ZERO) > 0) {
						// entrega todo saldo do item
						item.setValorRecebidoSolicitacaoServico(previamenteRecebido.add(item.getSaldoSolicitacaoServico()));
						saldoAEntregar = saldoAEntregar.subtract(item.getSaldoSolicitacaoServico());
					} else {
						// entrega somente até ter saldo. Tem que ser + pois - com - dá +
						BigDecimal restante = item.getSaldoSolicitacaoServico().add(saldoLimite);
						item.setValorRecebidoSolicitacaoServico(previamenteRecebido.add(restante));
						saldoAEntregar = saldoAEntregar.subtract(restante);
					}
				}
			}
		}

		return saldoAEntregar.doubleValue();
	}

	/**
	 * Verifica se a quantidade distribuida entre as SCs da lista eh maior que a qtde recebida informada
	 * @param qtdeLimite
	 * @param listaPriorizacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarQtdeDistribuidaMaiorRecebida(Integer qtdeLimite, List<PriorizaEntregaVO> listaPriorizacao) throws ApplicationBusinessException {
		Integer somaSolicitacaoes = 0;

		for (PriorizaEntregaVO item : listaPriorizacao) {
			somaSolicitacaoes = somaSolicitacaoes + item.getQtdeRecebidaSolicitacaoCompra();
		}

		if (!somaSolicitacaoes.equals(qtdeLimite)) {
			throw new ApplicationBusinessException(ScoSolicitacaoProgramacaoEntregaONExceptionCode.MENSAGEM_QTDE_DISTRIBUIDA_MAIOR_RECEBIDA, somaSolicitacaoes,
					qtdeLimite);
		}
	}

	/**
	 * Verifica se o valor distribuido entre as SSs da lista eh maior que o valor recebido informada
	 * @param valorLimite
	 * @param listaPriorizacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarValorDistribuidoMaiorRecebido(Double valorLimite, List<PriorizaEntregaVO> listaPriorizacao) throws ApplicationBusinessException {
		Double somaSolicitacaoes = 0.00;

		for (PriorizaEntregaVO item : listaPriorizacao) {
			somaSolicitacaoes = somaSolicitacaoes + item.getValorRecebidoSolicitacaoServico().doubleValue();
		}

		if (!somaSolicitacaoes.equals(valorLimite)) {
			throw new ApplicationBusinessException(ScoSolicitacaoProgramacaoEntregaONExceptionCode.MENSAGEM_VALOR_DISTRIBUIDO_MAIOR_RECEBIDO,
					somaSolicitacaoes, valorLimite);
		}
	}

	private void verificarCamposObrigatorios(List<PriorizaEntregaVO> listaPriorizacao, DominioTipoSolicitacao tipoSolicitacao)
			throws ApplicationBusinessException {
		for (PriorizaEntregaVO item : listaPriorizacao) {
			if (tipoSolicitacao.equals(DominioTipoSolicitacao.SC) && item.getQtdeRecebidaSolicitacaoCompra() == null) {
				throw new ApplicationBusinessException(ScoSolicitacaoProgramacaoEntregaONExceptionCode.MENSAGEM_QTDE_OBRIGATORIO);
			}
			if (tipoSolicitacao.equals(DominioTipoSolicitacao.SS) && item.getValorRecebidoSolicitacaoServico() == null) {
				throw new ApplicationBusinessException(ScoSolicitacaoProgramacaoEntregaONExceptionCode.MENSAGEM_VALOR_OBRIGATORIO);
			}
		}
	}

	/**
	 * Realiza a confirmacao ou a gravacao da distribuicao feita nas solicitacoes. O retorno String é utilizado
	 * dentro da controller para saber se volta para a tela de recebimento ou se grava e fica na mesma tela.
	 */
	public List<PriorizaEntregaVO> gravarPriorizacaoEntrega(Boolean processoRecebimento, DominioTipoSolicitacao tipoSolicitacao,
			List<PriorizaEntregaVO> listaPriorizacao, Integer qtdeLimite, Double valorLimite) throws ApplicationBusinessException {

		this.verificarCamposObrigatorios(listaPriorizacao, tipoSolicitacao);

		if (tipoSolicitacao.equals(DominioTipoSolicitacao.SC)) {
			this.verificarQtdeDistribuidaMaiorRecebida(qtdeLimite, listaPriorizacao);
		} else {
			this.verificarValorDistribuidoMaiorRecebido(valorLimite, listaPriorizacao);
		}

		if (processoRecebimento) {

			return listaPriorizacao;
		} else {
			this.getEstoqueFacade().persistirSceItemRecbXProgrEntrega(listaPriorizacao);
		}

		return null;
	}

	// Dependências

	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	protected ScoItemAutorizacaoFornRN getScoItemAutorizacaoFornRN() {
		return scoItemAutorizacaoFornRN;
	}

	protected ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO() {
		return scoSolicitacaoProgramacaoEntregaDAO;
	}

	protected ScoSolicitacaoProgramacaoEntregaRN getScoSolicitacaoProgramacaoEntregaRN() {
		return scoSolicitacaoProgramacaoEntregaRN;
	}

	protected ScoAutorizacaoFornRN getScoAutorizacaoFornRN() {
		return scoAutorizacaoFornRN;
	}

	protected ScoSolicitacaoProgramacaoEntregaGeracaoON getScoSolicitacaoProgramacaoEntregaGeracaoON() {
		return scoSolicitacaoProgramacaoEntregaGeracaoON;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

}
