package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.vo.ItemAutFornecimentoJnVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.MaterialServicoVO;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class PesquisarVersoesItensAutfornecimentoON extends BaseBusiness {

	@EJB
	private MantemItemAutFornecimentoON mantemItemAutFornecimentoON;

	private static final Log LOG = LogFactory.getLog(PesquisarVersoesItensAutfornecimentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@Inject
	private ScoItemAutorizacaoFornJnDAO scoItemAutorizacaoFornJnDAO;
	
	@EJB
	private IComprasFacade comprasFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2468088836939403273L;

	/**
	 * Obtem lista de versões anteriores dos itens de uma versão da AF
	 * 
	 * @param numeroAf
	 * @param sequenciaAlteracao
	 * @return Lista de VO's dos itens
	 */
	public List<ItemAutFornecimentoJnVO> obterListaItemAutFornecimentoJnVO(Integer seqAF, Integer numeroAF, Integer sequenciaAlteracao) {
		List<ScoItemAutorizacaoFornJn> listaItemAutorizacaoFornJn = getScoItemAutorizacaoFornJnDAO().obterScoItemAutorizacaoFornJnPorNumPacSeq(seqAF,
				sequenciaAlteracao);

		List<ItemAutFornecimentoJnVO> listaItemAutFornecimentoJnVO = new ArrayList<ItemAutFornecimentoJnVO>();

		for (ScoItemAutorizacaoFornJn itemAFJN : listaItemAutorizacaoFornJn) {
			ItemAutFornecimentoJnVO itemAutFornecimentoJnVO = new ItemAutFornecimentoJnVO();

			Long quantidadeEfetivada = getEstoqueFacade().quantidadeEfetivadaItensAF(seqAF, itemAFJN.getNumero());
			
			if (quantidadeEfetivada != null){
			    itemAutFornecimentoJnVO.setQtdEf(quantidadeEfetivada.intValue());
			}

			itemAutFornecimentoJnVO.setItemAutorizacaoFornJn(itemAFJN);

			MaterialServicoVO materialServicoVO = getManterItemAutFornecimentoON().obterDadosMaterialServico(
					itemAFJN.getScoItemAutorizacaoForn().getScoFaseSolicitacao());
			itemAutFornecimentoJnVO.setCodMatServ(materialServicoVO.getCodigoMatServ());
			itemAutFornecimentoJnVO.setNomeMatServ(materialServicoVO.getNomeMatServ());
			itemAutFornecimentoJnVO.setDescricaoMatServ(materialServicoVO.getDescricaoMatServ());
			itemAutFornecimentoJnVO.setDescricaoSolicitacao(getManterItemAutFornecimentoON().obterDescricaoSolicitacao(
					itemAFJN.getScoItemAutorizacaoForn().getScoFaseSolicitacao()));
			itemAutFornecimentoJnVO.setNumeroItem(itemAFJN.getItemPropostaFornecedor().getItemLicitacao().getId().getNumero().intValue());

			if (itemAFJN.getUnidadeMedida() != null){
				itemAFJN.setUnidadeMedida(this.comprasFacade.obterUnidadeMedidaPorSeq(itemAFJN.getUnidadeMedida().getCodigo()));				
			}
			
			if (itemAFJN.getMarcaComercial() != null) {
				itemAutFornecimentoJnVO.setMarcaComercial(itemAFJN.getMarcaComercial().getCodigoEDescricao());
			}
			itemAutFornecimentoJnVO.setModelo(formatarModelo(itemAFJN));
			itemAutFornecimentoJnVO.setQtdSaldo(calcularQtdSaldo(itemAFJN));
			itemAutFornecimentoJnVO.setValorUnitario(converterParaBigDecimal(itemAFJN.getValorUnitario()));
			itemAutFornecimentoJnVO.setValorBruto(calcularValorBruto(itemAFJN));
			itemAutFornecimentoJnVO.setValorSaldo(calcularValorSaldo(itemAFJN));
			itemAutFornecimentoJnVO.setDescItem(converterParaBigDecimal(itemAFJN.getPercDescontoItem()));
			itemAutFornecimentoJnVO.setDescCondPag(converterParaBigDecimal(itemAFJN.getPercDesconto()));
			itemAutFornecimentoJnVO.setValorDesconto(calcularValorDesconto(itemAFJN, itemAutFornecimentoJnVO.getValorBruto()));
			itemAutFornecimentoJnVO.setValorEfetivado(converterParaBigDecimal(itemAFJN.getValorEfetivado()));
			itemAutFornecimentoJnVO.setAcrescItem(converterParaBigDecimal(itemAFJN.getPercAcrescimoItem()));
			itemAutFornecimentoJnVO.setAcresCondPag(converterParaBigDecimal(itemAFJN.getPercAcrescimo()));
			itemAutFornecimentoJnVO.setValorAcrescimo(calcularValorAcrescimo(itemAFJN, itemAutFornecimentoJnVO.getValorBruto()));
			itemAutFornecimentoJnVO.setValorTotal(calcularValorTotal(itemAFJN));
			itemAutFornecimentoJnVO.setIpi(converterParaBigDecimal(itemAFJN.getPercIpi()));
			itemAutFornecimentoJnVO.setValorIPI(calcularValorIpi(itemAutFornecimentoJnVO));

			listaItemAutFornecimentoJnVO.add(itemAutFornecimentoJnVO);
		}

		return listaItemAutFornecimentoJnVO;
	}

	private BigDecimal converterParaBigDecimal(Double valorDouble) {
		BigDecimal valorBigDecimal = BigDecimal.ZERO;

		if (valorDouble != null) {
			valorBigDecimal = new BigDecimal(valorDouble.toString());
		}

		return valorBigDecimal;
	}

	private BigDecimal calcularValorIpi(ItemAutFornecimentoJnVO itemAutFornecimentoJnVO) {
		BigDecimal valorIpi = BigDecimal.ZERO;

		valorIpi = valorIpi.add(itemAutFornecimentoJnVO.getValorBruto()).add(itemAutFornecimentoJnVO.getValorDesconto())
				.add(itemAutFornecimentoJnVO.getValorAcrescimo());
		valorIpi = valorIpi.multiply(itemAutFornecimentoJnVO.getIpi().divide(new BigDecimal(100)));

		return valorIpi;
	}

	private BigDecimal calcularValorTotal(ScoItemAutorizacaoFornJn itemAFJN) {
		BigDecimal valorItem = BigDecimal.ZERO;

		DominioTipoSolicitacao tipoSolicitacao = getPacFacade().obterTipoSolicitacao(itemAFJN.getItemPropostaFornecedor().getItemLicitacao());

		if (tipoSolicitacao.equals(DominioTipoSolicitacao.SC)) {
			Integer qtdSolicitada;
			Integer qtdRecebida;

			if (itemAFJN.getQtdeSolicitada() == null) {
				qtdSolicitada = 0;
			} else {
				qtdSolicitada = itemAFJN.getQtdeSolicitada();
			}

			if (itemAFJN.getQtdeRecebida() == null) {
				qtdRecebida = 0;
			} else {
				qtdRecebida = itemAFJN.getQtdeRecebida();
			}

			valorItem = valorItem.add(new BigDecimal(qtdSolicitada));
			valorItem = valorItem.subtract(new BigDecimal(qtdRecebida));
			valorItem = valorItem.multiply(new BigDecimal(itemAFJN.getValorUnitario()));
			valorItem = valorItem.add(new BigDecimal(itemAFJN.getValorEfetivado()));
		} else {
			valorItem = new BigDecimal(itemAFJN.getValorUnitario());
		}

		return valorItem;
	}

	private BigDecimal calcularValorAcrescimo(ScoItemAutorizacaoFornJn itemAFJN, BigDecimal valorBruto) {
		BigDecimal valorAcrescimo;
		BigDecimal valorAcrescimoItem = BigDecimal.ZERO;
		BigDecimal valorAcrescimoCondPgto = BigDecimal.ZERO;

		if (itemAFJN.getPercAcrescimoItem() != null && itemAFJN.getPercAcrescimoItem() > 0) {
			valorAcrescimoItem = valorBruto.multiply(converterParaBigDecimal(itemAFJN.getPercAcrescimoItem()).divide(new BigDecimal(100)));
		}

		if (itemAFJN.getPercAcrescimo() != null && itemAFJN.getPercAcrescimo() > 0) {
			valorAcrescimoCondPgto = valorBruto.multiply(converterParaBigDecimal(itemAFJN.getPercAcrescimo()).divide(new BigDecimal(100)));
		}

		valorAcrescimo = valorAcrescimoItem.add(valorAcrescimoCondPgto);

		return valorAcrescimo;
	}

	private BigDecimal calcularValorDesconto(ScoItemAutorizacaoFornJn itemAFJN, BigDecimal valorBruto) {
		BigDecimal valorDesconto;
		BigDecimal valorDescontoItem = BigDecimal.ZERO;
		BigDecimal valorDescontoCondicaoPgto = BigDecimal.ZERO;

		if (itemAFJN.getPercDescontoItem() != null && itemAFJN.getPercDescontoItem() > 0) {
			valorDescontoItem = valorBruto.multiply(converterParaBigDecimal(itemAFJN.getPercDescontoItem()).divide(new BigDecimal(100)));
		}

		if (itemAFJN.getPercDesconto() != null && itemAFJN.getPercDesconto() > 0) {
			valorDescontoCondicaoPgto = valorBruto.multiply(converterParaBigDecimal(itemAFJN.getPercDesconto()).divide(new BigDecimal(100)));
		}

		valorDesconto = valorDescontoItem.add(valorDescontoCondicaoPgto);

		return valorDesconto;
	}

	private BigDecimal calcularValorSaldo(ScoItemAutorizacaoFornJn itemAFJN) {
		BigDecimal valorSaldo = BigDecimal.ZERO;

		DominioTipoSolicitacao tipoSolicitacao = getPacFacade().obterTipoSolicitacao(itemAFJN.getItemPropostaFornecedor().getItemLicitacao());

		if (tipoSolicitacao.equals(DominioTipoSolicitacao.SC)) {
			Integer qtdSolicitada;
			Integer qtdRecebida;

			if (itemAFJN.getQtdeSolicitada() == null) {
				qtdSolicitada = 0;
			} else {
				qtdSolicitada = itemAFJN.getQtdeSolicitada();
			}

			if (itemAFJN.getQtdeRecebida() == null) {
				qtdRecebida = 0;
			} else {
				qtdRecebida = itemAFJN.getQtdeRecebida();
			}

			valorSaldo = valorSaldo.add(new BigDecimal(qtdSolicitada));
			valorSaldo = valorSaldo.subtract(new BigDecimal(qtdRecebida));
			valorSaldo = valorSaldo.multiply(converterParaBigDecimal(itemAFJN.getValorUnitario()));
		} else {
			valorSaldo = new BigDecimal(itemAFJN.getValorUnitario());
			valorSaldo = valorSaldo.subtract(converterParaBigDecimal(itemAFJN.getValorEfetivado()));
		}

		return valorSaldo;
	}

	private BigDecimal calcularValorBruto(ScoItemAutorizacaoFornJn itemAFJN) {
		BigDecimal valorBruto;
		valorBruto = new BigDecimal(itemAFJN.getValorUnitario());

		DominioTipoSolicitacao tipoSolicitacao = getPacFacade().obterTipoSolicitacao(itemAFJN.getItemPropostaFornecedor().getItemLicitacao());

		if (tipoSolicitacao.equals(DominioTipoSolicitacao.SC)) {
			Integer saldo = calcularQtdSaldo(itemAFJN);
			valorBruto = valorBruto.multiply(new BigDecimal(saldo));
		}

		return valorBruto;
	}

	private String formatarModelo(ScoItemAutorizacaoFornJn itemAFJN) {
		StringBuffer modeloFormatado = new StringBuffer("");

		if (itemAFJN.getMarcaModelo() != null) {
			modeloFormatado.append(itemAFJN.getMarcaModelo().getId().getSeqp().toString());

			if (itemAFJN.getMarcaModelo().getDescricao() != null) {

				modeloFormatado.append('-').append(itemAFJN.getMarcaModelo().getDescricao());
			}
		}

		return modeloFormatado.toString();
	}

	private Integer calcularQtdSaldo(ScoItemAutorizacaoFornJn itemAFJN) {
		Integer qtdSaldo, qtdSolicitada, qtdRecebida;

		if (itemAFJN.getQtdeSolicitada() == null) {
			qtdSolicitada = 0;
		} else {
			qtdSolicitada = itemAFJN.getQtdeSolicitada();
		}

		if (itemAFJN.getQtdeRecebida() == null) {
			qtdRecebida = 0;
		} else {
			qtdRecebida = itemAFJN.getQtdeRecebida();
		}

		qtdSaldo = qtdSolicitada - qtdRecebida;

		return qtdSaldo;
	}

	protected ScoItemAutorizacaoFornJnDAO getScoItemAutorizacaoFornJnDAO() {
		return scoItemAutorizacaoFornJnDAO;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	private IPacFacade getPacFacade() {
		return this.pacFacade;
	}

	private MantemItemAutFornecimentoON getManterItemAutFornecimentoON() {
		return mantemItemAutFornecimentoON;
	}

}
