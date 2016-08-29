package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.RecebimentosProvisoriosVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.dao.SceDocumentoFiscalEntradaDAO;
import br.gov.mec.aghu.estoque.dao.SceEntrSaidSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemEntrSaidSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecbXProgrEntregaDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.vo.ConfirmacaoRecebimentoFiltroVO;
import br.gov.mec.aghu.estoque.vo.ItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.estoque.vo.ValidaConfirmacaoRecebimentoVO;
import br.gov.mec.aghu.estoque.vo.ValidaUnidadeMaterialVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceItemRecbXProgrEntrega;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceItemRecebProvisorioId;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConfirmacaoRecebimentoON extends BaseBusiness {

	@EJB
	private SceItemRecbXProgrEntregaRN sceItemRecbXProgrEntregaRN;

	@EJB
	private SceItemRecebProvisorioRN sceItemRecebProvisorioRN;

	@EJB
	private SceItemNotaRecebimentoRN sceItemNotaRecebimentoRN;

	@EJB
	private SceNotaRecebimentoProvisorioRN sceNotaRecebimentoProvisorioRN;

	@EJB
	private SceMovimentoMaterialRN sceMovimentoMaterialRN;

	@EJB
	private SceNotaRecebimentoRN sceNotaRecebimentoRN;

	@Inject
	private SceEntrSaidSemLicitacaoDAO sceEntrSaidSemLicitacaoDAO;

	private static final Log LOG = LogFactory.getLog(ConfirmacaoRecebimentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@Inject
	private SceNotaRecebimentoDAO sceNotaRecebimentoDAO;

	@Inject
	private SceNotaRecebProvisorioDAO sceNotaRecebProvisorioDAO;

	@Inject
	private SceDocumentoFiscalEntradaDAO sceDocumentoFiscalEntradaDAO;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	@Inject
	private SceItemRecbXProgrEntregaDAO sceItemRecbXProgrEntregaDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;

	@Inject
	private SceItemEntrSaidSemLicitacaoDAO sceItemEntrSaidSemLicitacaoDAO;

	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	public enum ConfirmacaoRecebimentoONExceptionCode implements BusinessExceptionCode {
		ERRO_CONFIRMACAO_RECEBIMENTO_INFORMAR_NOTA_FISCAL, ERRO_CONFIRMACAO_RECEBIMENTO_NR_GERADA, ERRO_CONFIRMACAO_RECEBIMENTO_UNIDADE_MEDIDA, ERRO_VALOR_NOTA_FISCAL, ERRO_NAO_EXISTE_VALOR_ASSINADO, ERRO_VALOR_FORA_PERCENTUAL_VARIACAO, ERRO_VALOR_FORA_PERCENTUAL_VARIACAO_2, ERRO_CLONE_NOTA_RECEBIMENTO, ERRO_CLONE_NOTA_RECEBIMENTO_PROVISORIO, ERRO_CONFIRMACAO_RECEBIMENTO_SEM_PEDIDO_ESL, ERRO_SALDO_INSUFICIENTE_AF
	}

	private static final long serialVersionUID = -4100462446116708948L;
	private static final Integer CONSTANTE_FATOR_CONVERSAO = 1;

	/**
	 * SCEF_CONFIRM_RECEB - GERA_NR
	 * 
	 * @param notaRecebimentoProvisorio
	 * @throws BaseException
	 */
	public ValidaConfirmacaoRecebimentoVO confirmarRecebimento(SceNotaRecebProvisorio notaRecebimentoProvisorio,
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal, String nomeMicrocomputador, Boolean validaRegras, boolean flush)
			throws BaseException {
		return confirmarRecebimento(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal, nomeMicrocomputador, validaRegras, flush,
				false);

	}

	/**
	 * SCEF_CONFIRM_RECEB - GERA_NR
	 * 
	 * @param notaRecebimentoProvisorio
	 * @throws BaseException
	 */
	public ValidaConfirmacaoRecebimentoVO confirmarRecebimento(SceNotaRecebProvisorio notaRecebimentoProvisorio,
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal, String nomeMicrocomputador, Boolean validaRegras, boolean flush,
			boolean validarEsl) throws BaseException {

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		List<ItemRecebimentoProvisorioVO> listaItens = this.pesquisarItensNotaRecebimentoProvisorio(notaRecebimentoProvisorio.getSeq(),
				Boolean.FALSE);
		ValidaConfirmacaoRecebimentoVO retorno = null;

		if (notaRecebimentoProvisorio.getScoAfPedido() != null) {
			retorno = validarRegras(validarEsl, validaRegras, notaRecebimentoProvisorioOriginal, servidorLogado, listaItens);
			if (retorno != null) {
				return retorno;
			}

			SceNotaRecebimento notaRecebimento = new SceNotaRecebimento();
			notaRecebimento.setDocumentoFiscalEntrada(this.getSceDocumentoFiscalEntradaDAO().obterPorChavePrimaria(
					notaRecebimentoProvisorio.getDocumentoFiscalEntrada().getSeq()));
			notaRecebimento.setAutorizacaoFornecimento(this.getComprasFacade().obterScoAutorizacaoFornPorChavePrimaria(
					notaRecebimentoProvisorio.getScoAfPedido().getScoAutorizacaoForn().getNumero()));
			notaRecebimento.setDtGeracao(new Date());
			notaRecebimento.setServidorGeracao(servidorLogado);
			notaRecebimento.setEstorno(Boolean.FALSE);
			notaRecebimento.setIndGerado(Boolean.FALSE);
			notaRecebimento.setDebitoNotaRecebimento(Boolean.FALSE);
			notaRecebimento.setIndTributacao(Boolean.FALSE);
			notaRecebimento.setIndTribLiberada(Boolean.FALSE);
			notaRecebimento.setNotaRecebProvisorio(notaRecebimentoProvisorio);
			notaRecebimento = this.getSceNotaRecebimentoRN().inserir(notaRecebimento, flush);
			notaRecebimentoProvisorio.setNotaRecebimento(notaRecebimento);

			for (ItemRecebimentoProvisorioVO item : listaItens) {
				SceItemNotaRecebimento itemNotaRecebimento = new SceItemNotaRecebimento();
				ScoItemAutorizacaoForn itemAutorizacaoForn = this.getComprasFacade().obterItemAutorizacaoFornPorId(item.getAfnNumero(),
						item.getNumero());
				ScoMaterial material = null;
				if (item.getCodigoMaterial() != null) {
					material = this.getComprasFacade().obterMaterialPorId(item.getCodigoMaterial());
				}
				ScoServico servico = null;
				if (item.getCodigoServico() != null) {
					servico = this.getComprasFacade().obterServicoPorId(item.getCodigoServico());
				}
				itemNotaRecebimento.setNotaRecebimento(notaRecebimento);
				itemNotaRecebimento.setItemAutorizacaoForn(itemAutorizacaoForn);
				itemNotaRecebimento.setQuantidade(item.getQuantidade());
				if (item.getValor() != null) {
					DecimalFormat df = new DecimalFormat("#.00");
					String valor = df.format(item.getValor());
					valor = valor.replace(",", ".");
					itemNotaRecebimento.setValor(Double.valueOf(valor));
				}

				if (material != null) {
					itemNotaRecebimento.setMaterial(material);
					if (material.getUnidadeMedida() != null) {
						itemNotaRecebimento.setUnidadeMedida(this.getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(
								material.getUnidadeMedida().getCodigo()));
					}
				} else {
					itemNotaRecebimento.setUnidadeMedida(null);
				}

				itemNotaRecebimento.setServico(servico);
				itemNotaRecebimento.setIndDebitoNrIaf(Boolean.FALSE);
				itemNotaRecebimento.setIndUsoMaterial(Boolean.FALSE);
				itemNotaRecebimento.setIndTributacao(Boolean.FALSE);

				// try {
				this.getSceItemNotaRecebimentoRN().inserir(itemNotaRecebimento, nomeMicrocomputador, flush);
				// } catch (Exception e) {
				// throw new
				// AGHUNegocioExceptionSemRollback(ConfirmacaoRecebimentoONExceptionCode.ERRO_INSERCAO_ITEM_NOTA_RECEBIMENTO);
				// }

			}

			SceNotaRecebimento notaRecebimentoOriginal;

			try {
				notaRecebimentoOriginal = (SceNotaRecebimento) BeanUtils.cloneBean(notaRecebimento);
			} catch (Exception e) {
				throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_CLONE_NOTA_RECEBIMENTO);
			}

			notaRecebimento.setIndGerado(Boolean.TRUE);

			try {
				this.getSceNotaRecebimentoRN().atualizar(notaRecebimento, notaRecebimentoOriginal, nomeMicrocomputador, false);
				notaRecebimentoProvisorio.setIndConfirmado(Boolean.TRUE);
				this.getSceNotaRecebimentoProvisorioRN().atualizar(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal);
			} catch (ApplicationBusinessException e) {
				throw new ApplicationBusinessException(e.getCode(), e);
			}

		}

		if (notaRecebimentoProvisorio.getEslSeq() != null) {
			confirmarRecebimentoEsl(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal, servidorLogado, nomeMicrocomputador);

		} else {
			throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_CONFIRMACAO_RECEBIMENTO_SEM_PEDIDO_ESL);
		}
		return null;
	}

	/**
	 * Valida regras
	 * 
	 * @param notaRecebimentoProvisorio
	 * @param servidorLogado
	 * @param listaItens
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private ValidaConfirmacaoRecebimentoVO validarRegras(boolean validarEsl, boolean validarRegra,
			SceNotaRecebProvisorio notaRecebimentoProvisorio, RapServidores servidorLogado, List<ItemRecebimentoProvisorioVO> listaItens)
			throws ApplicationBusinessException {

		ValidaConfirmacaoRecebimentoVO validaConfirmacaoRecebimentoVO = null;
		if (validarEsl
				&& this.getSceEntrSaidSemLicitacaoDAO().existeEslPendenteParaAF(
						notaRecebimentoProvisorio.getScoAfPedido().getId().getAfnNumero())) {
			validaConfirmacaoRecebimentoVO = new ValidaConfirmacaoRecebimentoVO();
			String mensagemModal = this.getResourceBundleValue("MSG_EMPRESTIMO_PENDENTE");
			validaConfirmacaoRecebimentoVO.setMensagemModal(mensagemModal);
			validaConfirmacaoRecebimentoVO.setValidarEsl(true);
			return validaConfirmacaoRecebimentoVO;
		}

		if (validarRegra) {
			if (notaRecebimentoProvisorio.getDocumentoFiscalEntrada() == null) {
				throw new ApplicationBusinessException(
						ConfirmacaoRecebimentoONExceptionCode.ERRO_CONFIRMACAO_RECEBIMENTO_INFORMAR_NOTA_FISCAL);
			}

			this.verificarGeracaoNotaRecebimento(notaRecebimentoProvisorio);
			final ItemRecebimentoProvisorioVO itemRecebimentoProvisorioVO1 = listaItens.get(0);
			if (itemRecebimentoProvisorioVO1.getCodigoMaterial() != null) {
				this.verificarUnidadeMaterial(notaRecebimentoProvisorio);
			}
			this.verificarNotaFiscal(notaRecebimentoProvisorio);
			// TODO verificar empenho. ver #25016
			this.verificarSaldo(notaRecebimentoProvisorio);
			validaConfirmacaoRecebimentoVO = this.verificarSituacaoItens(listaItens, true);

			if (validaConfirmacaoRecebimentoVO != null) {
				validaConfirmacaoRecebimentoVO.setValidarEsl(false);
				return validaConfirmacaoRecebimentoVO;
			}
			return validaConfirmacaoRecebimentoVO;
		}
		return validaConfirmacaoRecebimentoVO;
	}

	public ValidaConfirmacaoRecebimentoVO verificarSituacaoItens(List<ItemRecebimentoProvisorioVO> listaItens, Boolean isConfirmacao)
			throws ApplicationBusinessException {
		ValidaConfirmacaoRecebimentoVO validaConfirmacaoRecebimentoVO = new ValidaConfirmacaoRecebimentoVO();
		Boolean situacaoA = Boolean.FALSE;
		// Boolean situacaoB = Boolean.FALSE;
		List<ItemRecebimentoProvisorioVO> listaItensComPendencia = new ArrayList<ItemRecebimentoProvisorioVO>();

		for (ItemRecebimentoProvisorioVO item : listaItens) {
			if (item.getAfnNumero() == null || item.getNumero() == null) {
				continue;
			}

			ScoItemAutorizacaoForn itemAutorizacaoForn = this.getComprasFacade().obterItemAutorizacaoFornPorId(item.getAfnNumero(),
					item.getNumero());
			if (item.getCodigoMaterial() != null) { // Material
				validaConfirmacaoRecebimentoVO = this.verificarSituacaoItem(itemAutorizacaoForn, item, isConfirmacao);
				if (validaConfirmacaoRecebimentoVO.getValorForaPercentualVariacao() != null
						&& validaConfirmacaoRecebimentoVO.getValorForaPercentualVariacao()
						|| validaConfirmacaoRecebimentoVO.isValorForaPercentualVariacaoItens()) {
					return validaConfirmacaoRecebimentoVO;
				}
			}

			if (isConfirmacao) {
				if (item.getCodigoMaterial() == null) { // Serviço
					if ((item.getValorEfetivado() == null ? 0.0 : item.getValorEfetivado())
							+ (item.getValor() == null ? 0.0 : item.getValor()) > (item.getValorItem() == null ? 0.0 : item.getValorItem())) {
						throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_SALDO_INSUFICIENTE_AF);
					}

				} else { // Material
					Integer qtdSaldoAf = this.calcularQtdSaldoAf(itemAutorizacaoForn);
					item.setQtdSaldoAf(qtdSaldoAf);
					if (qtdSaldoAf < (item.getQuantidade() / itemAutorizacaoForn.getFatorConversao())) {
						situacaoA = Boolean.TRUE;
						item.setExisteSaldo(false);
						listaItensComPendencia.add(item);
					}
				}

				/*
				 * 
				 * retirado conforme Defeito em Qualidade #30133 Double
				 * valorUnitarioMaximo = null; Double valorUnitarioMinimo =
				 * null; if (itemAutorizacaoForn.getPercVarPreco() != null) {
				 * valorUnitarioMaximo = itemAutorizacaoForn.getValorUnitario()
				 * + ((itemAutorizacaoForn.getValorUnitario() *
				 * itemAutorizacaoForn.getPercVarPreco() / 100));
				 * valorUnitarioMinimo = itemAutorizacaoForn.getValorUnitario()
				 * - ((itemAutorizacaoForn.getValorUnitario() *
				 * itemAutorizacaoForn.getPercVarPreco() / 100)); } else {
				 * valorUnitarioMaximo = itemAutorizacaoForn.getValorUnitario();
				 * valorUnitarioMinimo = itemAutorizacaoForn.getValorUnitario();
				 * } if (item.getValor() / (item.getQuantidade() /
				 * itemAutorizacaoForn.getFatorConversao()) >
				 * valorUnitarioMaximo) { item.setValorItem(item.getValor() /
				 * (item.getQuantidade() /
				 * itemAutorizacaoForn.getFatorConversao()));
				 * item.setValorConfere(false); situacaoB = Boolean.TRUE;
				 * 
				 * listaItensComPendencia.add(item); } if (item.getValor() /
				 * (item.getQuantidade() /
				 * itemAutorizacaoForn.getFatorConversao()) <
				 * valorUnitarioMinimo) { item.setValorItem(item.getValor() /
				 * (item.getQuantidade() /
				 * itemAutorizacaoForn.getFatorConversao()));
				 * item.setValorConfere(false); situacaoB = Boolean.TRUE;
				 * listaItensComPendencia.add(item); }
				 */

			}

		}

		if (situacaoA /* || situacaoB */) {
			validaConfirmacaoRecebimentoVO.setListaItensComPendencia(listaItensComPendencia);
			return validaConfirmacaoRecebimentoVO;
		} else {
			return null;
		}
	}

	private Integer calcularQtdSaldoAf(final ScoItemAutorizacaoForn itemAutorizacaoForn) {
		if (itemAutorizacaoForn.getQtdeRecebida() != null) {
			return itemAutorizacaoForn.getQtdeSolicitada() - itemAutorizacaoForn.getQtdeRecebida();
		} else {
			return itemAutorizacaoForn.getQtdeSolicitada();
		}
	}

	public ValidaConfirmacaoRecebimentoVO gravarItensRecebimento(List<ItemRecebimentoProvisorioVO> listaItens, Boolean valida)
			throws ApplicationBusinessException {
		if (valida) {
			ValidaConfirmacaoRecebimentoVO validaConfirmacaoRecebimentoVO = this.verificarSituacaoItens(listaItens, false);
			if (validaConfirmacaoRecebimentoVO != null) {
				return validaConfirmacaoRecebimentoVO;
			}
		}

		for (ItemRecebimentoProvisorioVO item : listaItens) {
			// SceItemRecebProvisorio itemRecebProvisorio = new
			// SceItemRecebProvisorio();
			SceItemRecebProvisorioId id = new SceItemRecebProvisorioId();
			id.setNroItem(item.getNroItem());
			id.setNrpSeq(item.getNrpSeq());
			SceItemRecebProvisorio itemRecebProvisorio = this.getSceItemRecebProvisorioDAO().obterPorChavePrimaria(id);
			itemRecebProvisorio.setValor(item.getValor());
			this.getSceItemRecebProvisorioRN().atualizar(itemRecebProvisorio);
		}
		return null;
	}

	/**
	 * 
	 * @param notaRecebimentoProvisorio
	 * @throws BaseException
	 * @throws MECBaseException
	 */

	public void estornarRecebimento(SceNotaRecebProvisorio notaRecebimentoProvisorio,
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal) throws BaseException {
		if (notaRecebimentoProvisorio.getScoAfPedido() != null) {
			notaRecebimentoProvisorio.setIndEstorno(Boolean.TRUE);
			notaRecebimentoProvisorio.setDtEstorno(new Date());
			this.getSceNotaRecebimentoProvisorioRN().atualizar(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal);
			atualizarParcelasEstorno(notaRecebimentoProvisorio);
		}

		if (notaRecebimentoProvisorio.getEslSeq() != null) {
			try {
				notaRecebimentoProvisorio.setIndEstorno(Boolean.TRUE);
				notaRecebimentoProvisorio.setDtEstorno(new Date());
				this.getSceNotaRecebimentoProvisorioRN().atualizar(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal);
			} catch (ApplicationBusinessException e) {
				throw new ApplicationBusinessException(e);
			}

			SceEntrSaidSemLicitacao sceEsl = this.getSceEntrSaidSemLicitacaoDAO().obterPorChavePrimaria(
					notaRecebimentoProvisorio.getEslSeq());
			if (sceEsl != null) {
				sceEsl.setDtEstorno(new Date());
				sceEsl.setIndEstorno(Boolean.TRUE);
				sceEsl.setServidorEstornado(this.getServidorLogadoFacade().obterServidorLogado());
				this.getSceEntrSaidSemLicitacaoDAO().atualizar(sceEsl);
			}
		}
	}

	public void atualizarParcelasEstorno(SceNotaRecebProvisorio notaRecebimentoProvisorio) throws BaseException {
		List<ItemRecebimentoProvisorioVO> lista = this.pesquisarItensNotaRecebimentoProvisorio(notaRecebimentoProvisorio.getSeq(),
				Boolean.FALSE);

		for (ItemRecebimentoProvisorioVO item : lista) {
			SceItemRecebProvisorioId id = new SceItemRecebProvisorioId();
			id.setNroItem(item.getNroItem());
			id.setNrpSeq(item.getNrpSeq());
			List<SceItemRecbXProgrEntrega> listaItems = this.getSceItemRecbXProgrEntregaDAO()
					.pesquisarItemRecbXProgrEntregaPorItemRecebimentoProvisorio(id);

			for (SceItemRecbXProgrEntrega itemRecbXProgrEntrega : listaItems) {
				itemRecbXProgrEntrega.setIndEstornado(true);
				this.getSceItemRecbXProgrEntregaRN().atualizar(itemRecbXProgrEntrega);
				ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutorizacaoFornecimento = itemRecbXProgrEntrega
						.getScoProgEntregaItemAutorizacaoFornecimento();
				if (progEntregaItemAutorizacaoFornecimento.getValorEfetivado() != null && itemRecbXProgrEntrega.getValorEfetivado() != null) {
					progEntregaItemAutorizacaoFornecimento.setValorEfetivado(progEntregaItemAutorizacaoFornecimento.getValorEfetivado()
							- itemRecbXProgrEntrega.getValorEfetivado());
				}

				if (item.getCodigoMaterial() != null && progEntregaItemAutorizacaoFornecimento.getQtdeEntregue() != null
						&& itemRecbXProgrEntrega.getQtdeEntregue() != null) {
					progEntregaItemAutorizacaoFornecimento.setQtdeEntregue(progEntregaItemAutorizacaoFornecimento.getQtdeEntregue()
							- itemRecbXProgrEntrega.getQtdeEntregue().intValue());
				}

				if (progEntregaItemAutorizacaoFornecimento.getValorEfetivado() == null
						|| progEntregaItemAutorizacaoFornecimento.getValorEfetivado().equals(0)
						|| progEntregaItemAutorizacaoFornecimento.getQtdeEntregue() == null
						|| progEntregaItemAutorizacaoFornecimento.getQtdeEntregue().equals(0)) {
					progEntregaItemAutorizacaoFornecimento.setDtEntrega(null);
				} else {
					Date dtEntrega = this.getSceItemRecebProvisorioDAO().obterMaiorDataEstornoRecebimento(
							notaRecebimentoProvisorio.getDtGeracao(), progEntregaItemAutorizacaoFornecimento.getId());
					progEntregaItemAutorizacaoFornecimento.setDtEntrega(dtEntrega);
				}

				this.getAutFornecimentoFacade().persistirProgEntregaItemAf(progEntregaItemAutorizacaoFornecimento);
				ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega = itemRecbXProgrEntrega
						.getScoSolicitacaoProgramacaoEntrega();
				if (solicitacaoProgramacaoEntrega != null) {
					solicitacaoProgramacaoEntrega.setValorEfetivado(solicitacaoProgramacaoEntrega.getValorEfetivado()
							- itemRecbXProgrEntrega.getValorEfetivado());
					if (item.getCodigoMaterial() != null) {
						solicitacaoProgramacaoEntrega
								.setQtdeEntregue(solicitacaoProgramacaoEntrega.getQtdeEntregue()
										- (itemRecbXProgrEntrega.getQtdeEntregue() == null ? 0 : itemRecbXProgrEntrega.getQtdeEntregue()
												.intValue()));
					}
					this.getAutFornecimentoFacade().persistir(solicitacaoProgramacaoEntrega);
				}
			}
		}
	}

	public List<ItemRecebimentoProvisorioVO> pesquisarItensProvisoriosAlterados(List<ItemRecebimentoProvisorioVO> lista) {
		List<ItemRecebimentoProvisorioVO> listaRetorno = new ArrayList<ItemRecebimentoProvisorioVO>();
		for (ItemRecebimentoProvisorioVO item : lista) {
			Double valorAtual = item.getValor();
			SceItemRecebProvisorioId id = new SceItemRecebProvisorioId();
			id.setNroItem(item.getNroItem());
			id.setNrpSeq(item.getNrpSeq());
			SceItemRecebProvisorio itemRecebProvisorio = this.getSceItemRecebProvisorioDAO().obterOriginal(id);
			if (!valorAtual.equals(itemRecebProvisorio.getValor())) {
				listaRetorno.add(item);
			}
		}
		return listaRetorno;
	}

	public void verificarGeracaoNotaRecebimento(SceNotaRecebProvisorio notaRecebimentoProvisorio) throws ApplicationBusinessException {
		final SceNotaRecebimento nota = this.getSceNotaRecebimentoDAO().obterNotaRecebimentoPorNotaRecebimentoProvisorio(
				notaRecebimentoProvisorio.getSeq());
		if (nota != null) {
			throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_CONFIRMACAO_RECEBIMENTO_NR_GERADA,
					nota.getSeq());
		}
	}

	public void verificarNotaFiscal(SceNotaRecebProvisorio notaRecebimentoProvisorio) throws ApplicationBusinessException {
		Double somatorioValorItem = this.getSceItemRecebProvisorioDAO().obterValorTotalItemNotaFiscal(
				notaRecebimentoProvisorio.getDocumentoFiscalEntrada().getSeq()); // consulta13;
		Double valorTotalNf = notaRecebimentoProvisorio.getDocumentoFiscalEntrada().getValorTotalNf();
		if (somatorioValorItem != null && valorTotalNf != null && somatorioValorItem > valorTotalNf) {
			throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_VALOR_NOTA_FISCAL, somatorioValorItem,
					valorTotalNf);
		}
	}

	public void verificarSaldo(SceNotaRecebProvisorio notaRecebimentoProvisorio) throws ApplicationBusinessException {
		Long valorAssinadoCount = this.getComprasFacade().verificarValorAssinadoPorAf(
				notaRecebimentoProvisorio.getScoAfPedido().getScoAutorizacaoForn().getNumero());
		if (valorAssinadoCount == 0) {
			throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_NAO_EXISTE_VALOR_ASSINADO);
		}
	}

	public ValidaConfirmacaoRecebimentoVO verificarSituacaoItem(ScoItemAutorizacaoForn item,
			ItemRecebimentoProvisorioVO itemRecebimentoProvisorioVO, Boolean isConfirmacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		ValidaConfirmacaoRecebimentoVO validaConfirmacaoRecebimentoVO = new ValidaConfirmacaoRecebimentoVO();
		Double valorUnitarioAf = item.getValorUnitario();
		Float perVarPrecoAf = item.getPercVarPreco();
		Double valorUnitarioMinimo = valorUnitarioAf - ((perVarPrecoAf / 100) * valorUnitarioAf);
		Double valorUnitarioMaximo = valorUnitarioAf + ((perVarPrecoAf / 100) * valorUnitarioAf);
		Double valorUnitarioItem = itemRecebimentoProvisorioVO.getValor() / itemRecebimentoProvisorioVO.getQuantidade();

		if (valorUnitarioItem < valorUnitarioMinimo || valorUnitarioItem > valorUnitarioMaximo) {
			if (isConfirmacao && !this.cascaFacade.usuarioTemPermissao(servidorLogado.getUsuario(), "excederPerVariaValor")) {

				// alterado seguindo as definições do Defeito em Qualidade
				// #30133
				// throw new
				// AGHUNegocioExceptionSemRollback(ConfirmacaoRecebimentoONExceptionCode.ERRO_VALOR_FORA_PERCENTUAL_VARIACAO,valorUnitarioItem,perVarPrecoAf,valorUnitarioAf);

				throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_VALOR_FORA_PERCENTUAL_VARIACAO,
						perVarPrecoAf);

				// b. Se ocorrer a situação acima para algum dos itens
				// verificados e se o usuário logado no sistema NÃO POSSUIR a
				// permissão excederPerVariaValor, emitir a mensagem: M5 e
				// interromper a operação.

			} else if (isConfirmacao) {
				validaConfirmacaoRecebimentoVO.setValorForaPercentualVariacao(true);

				// alterado seguindo as definições do Defeito em Qualidade
				// #30133
				// mensagemModal =
				// Interpolator.instance().interpolate(mensagemModal,
				// valorUnitarioItem,perVarPrecoAf,valorUnitarioAf);

				String mensagemModal = this.getResourceBundleValue("ERRO_VALOR_FORA_PERCENTUAL_VARIACAO_CONFIRMACAO");
				mensagemModal = java.text.MessageFormat.format(mensagemModal, perVarPrecoAf);

				validaConfirmacaoRecebimentoVO.setMensagemModal(mensagemModal);
				return validaConfirmacaoRecebimentoVO;

			}

			if (!isConfirmacao && !this.cascaFacade.usuarioTemPermissao(servidorLogado.getUsuario(), "excederPerVariaValor")) {
				throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_VALOR_FORA_PERCENTUAL_VARIACAO_2,
						perVarPrecoAf);
			} else if (!isConfirmacao) {
				validaConfirmacaoRecebimentoVO.setValorForaPercentualVariacaoItens(true);
				String mensagemModal = this.getResourceBundleValue("ERRO_VALOR_FORA_PERCENTUAL_VARIACAO_GRAVAR_ITENS");
				mensagemModal = java.text.MessageFormat.format(mensagemModal, perVarPrecoAf);
				validaConfirmacaoRecebimentoVO.setMensagemModal(mensagemModal);
				return validaConfirmacaoRecebimentoVO;
			}
		}
		return validaConfirmacaoRecebimentoVO;
	}

	private void verificarUnidadeMaterial(SceNotaRecebProvisorio notaRecebimentoProvisorio) throws ApplicationBusinessException {

		Integer parametroFatorConversao = CONSTANTE_FATOR_CONVERSAO;
		final AghParametros fornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		Integer parametroFornecedorPadrao = fornecedor.getVlrNumerico().intValue();
		String msg = "";
		String msg2 = "";

		List<ValidaUnidadeMaterialVO> listaItens = this.getSceItemRecebProvisorioDAO().pesquisarUnidadeMaterialAfEstoque(
				notaRecebimentoProvisorio, parametroFatorConversao, parametroFornecedorPadrao);

		if (listaItens != null && listaItens.size() > 0) {
			for (ValidaUnidadeMaterialVO validaUnidadeMaterialVO : listaItens) {
				msg = msg.concat(" " + validaUnidadeMaterialVO.getItlNumero().toString() + ", ");
				msg2 = msg2.concat(" " + validaUnidadeMaterialVO.getMatCodigo().toString() + ", ");
			}

			msg = msg.concat(this.getResourceBundleValue("ERRO_CONFIRMACAO_RECEBIMENTO_UNIDADE_MEDIDA1"));
			msg2 = msg2.concat(this.getResourceBundleValue("ERRO_CONFIRMACAO_RECEBIMENTO_UNIDADE_MEDIDA2"));
			msg = msg.concat(msg2);
			throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_CONFIRMACAO_RECEBIMENTO_UNIDADE_MEDIDA, msg);
		}
	}

	public List<SceNotaRecebProvisorio> pesquisarNotasRecebimentoProvisorioConfirmacao(final ConfirmacaoRecebimentoFiltroVO filtroVO,
			final Boolean indConfirmado, final Boolean indEstorno) {
		final List<SceNotaRecebProvisorio> retorno = this.getSceNotaRecebProvisorioDAO().pesquisarNotasRecebimentoProvisorioConfirmacao(
				filtroVO, indConfirmado, indEstorno);

		for (SceNotaRecebProvisorio sceNotaRecebProvisorio : retorno) {
			sceNotaRecebProvisorio.setValorNotaFiscal(this.getSceItemRecebProvisorioDAO().obterValorTotalNotaFiscal(
					sceNotaRecebProvisorio.getSeq()));
		}
		return retorno;
	}

	/**
	 * Método que verifica se o tempo de reposição de uma material em um
	 * almoxarifado está dentro do prazo de tolerância para um determinado
	 * fornecedor.
	 * 
	 * #25016 - Confirmação de recebimento de Materiais e Serviços
	 * 
	 * @ORADB SCEF_CONFIRM_RECEB
	 * 
	 * @param seqAlm
	 * @param codigoMaterial
	 * @param fonecedor
	 * @param tolerancia
	 * @return
	 */
	public Boolean verificarTempoReposicao(final Short seqAlm, final Integer codigoMaterial, final Integer fonecedor,
			final Integer tolerancia) {
		// Busca um item de SCE_ESTQ_ALMOXS. Como a PK é um seq que não
		// representa uma ligação única entre um Material, almoxarifado e
		// fornecedor, buscamos uma lista que DEVE retornar 1 objeto, apenas. No
		// banco oracle, atualmente não tem nenhum caso q retorne uma lista, mas
		// teoricamente pode. Então será buscada uma lista e pego o primeiro
		// registro.
		final SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = getSceEstoqueAlmoxarifadoDAO()
				.pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoMaterialFornecedor(seqAlm, codigoMaterial, fonecedor);
		if (sceEstoqueAlmoxarifado == null) {
			return Boolean.FALSE;
		} else {
			if (sceEstoqueAlmoxarifado.getQtdePontoPedido() != null && sceEstoqueAlmoxarifado.getTempoReposicao() != null
					&& sceEstoqueAlmoxarifado.getTempoReposicao() > 0) {
				return (sceEstoqueAlmoxarifado.getQtdePontoPedido() / sceEstoqueAlmoxarifado.getTempoReposicao()) >= tolerancia;
			} else {
				return Boolean.FALSE;
			}
		}
	}

	/**
	 * Verifica se um recebimento provisorio eh referente a servico ou material
	 * 
	 * @param notaRecebProv
	 * @return Boolean
	 */
	public Boolean verificarRecebimentoServico(SceNotaRecebProvisorio notaRecebProv) {
		DominioTipoFaseSolicitacao tipoFase = this.getSceNotaRecebProvisorioDAO().obterTipoNotaRecebimento(notaRecebProv.getSeq());
		if (tipoFase == null) {
			tipoFase = DominioTipoFaseSolicitacao.C;
		}
		return tipoFase.equals(DominioTipoFaseSolicitacao.S);
	}

	public List<ItemRecebimentoProvisorioVO> pesquisarItensNotaRecebimentoProvisorio(final Integer nrpSeq, final Boolean indExclusao)
			throws ApplicationBusinessException {

		final List<ItemRecebimentoProvisorioVO> retorno = this.getSceItemRecebProvisorioDAO().pesquisarItensNotaRecebimentoProvisorio(
				nrpSeq, indExclusao);

		if (retorno != null && !retorno.isEmpty()) {
			final AghParametros toloranciaEstoque = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_DURACAO_ESTOQUE);
			final AghParametros fornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			for (final ItemRecebimentoProvisorioVO itemRecebimentoProvisorioVO : retorno) {
				if (itemRecebimentoProvisorioVO.getCodigoMaterial() != null) {
					itemRecebimentoProvisorioVO.setAtraso(this.verificarTempoReposicao(itemRecebimentoProvisorioVO.getSeqAlm(),
							itemRecebimentoProvisorioVO.getCodigoMaterial(), fornecedor.getVlrNumerico().intValue(), toloranciaEstoque
									.getVlrNumerico().intValue()));
				} else {
					itemRecebimentoProvisorioVO.setAtraso(false);
				}
				itemRecebimentoProvisorioVO.setExisteSpe(this.getSceItemRecbXProgrEntregaDAO().verificarLigacaoSolicitacaoParcelaEntrega(
						itemRecebimentoProvisorioVO.getNrpSeq(), itemRecebimentoProvisorioVO.getNroItem()));
			}
		}
		return retorno;
	}

	public void calcularVariacaoItemRecebimentoProvisorio(ItemRecebimentoProvisorioVO item) {
		SceItemRecebProvisorioId id = new SceItemRecebProvisorioId();
		id.setNroItem(item.getNroItem());
		id.setNrpSeq(item.getNrpSeq());
		SceItemRecebProvisorio itemRecebProvisorio = this.getSceItemRecebProvisorioDAO().obterPorChavePrimaria(id);
		Double diferenca = itemRecebProvisorio.getValor() - item.getValor();
		Float variacao = (diferenca.floatValue() / itemRecebProvisorio.getValor().floatValue()) * 100;
		item.setVariacaoAtual(Math.abs(variacao));
	}

	public SceNotaRecebProvisorio clonarNotaRecebimentoProvisorio(SceNotaRecebProvisorio notaRecebimentoProvisorio)
			throws ApplicationBusinessException {
		try {
			return (SceNotaRecebProvisorio) BeanUtils.cloneBean(notaRecebimentoProvisorio);
		} catch (Exception e) {
			throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_CLONE_NOTA_RECEBIMENTO_PROVISORIO);
		}
	}

	public void recarregaItensAlterados(List<ItemRecebimentoProvisorioVO> listaItensAlterados) {
		for (final ItemRecebimentoProvisorioVO itemRecebimentoProvisorioVO : listaItensAlterados) {
			SceItemRecebProvisorioId id = new SceItemRecebProvisorioId();
			id.setNroItem(itemRecebimentoProvisorioVO.getNroItem());
			id.setNrpSeq(itemRecebimentoProvisorioVO.getNrpSeq());
			final SceItemRecebProvisorio itemRecebProvisorio = this.getSceItemRecebProvisorioDAO().obterPorChavePrimaria(id);
			this.getSceItemRecebProvisorioDAO().refresh(itemRecebProvisorio);
		}
	}

	/**
	 * Lista recebimentos provisorios e informa sigla e valor total NFe
	 * 
	 * @param filtroVO
	 * @param indConfirmado
	 * @param indEstorno
	 * @return
	 */
	public List<RecebimentosProvisoriosVO> listarRecebimentosProvisorios(final ConfirmacaoRecebimentoFiltroVO filtroVO,
			final Boolean indConfirmado, final Boolean indEstorno) {
		final List<RecebimentosProvisoriosVO> retorno = this.getSceNotaRecebProvisorioDAO().pesquisarRecProvisorio(filtroVO, indConfirmado,
				indEstorno);
		for (RecebimentosProvisoriosVO rendimentosProvisorios : retorno) {
			rendimentosProvisorios.setValorTotalNfe(this.getSceItemRecebProvisorioDAO().obterValorTotalNotaFiscal(
					rendimentosProvisorios.getSeq()));
			rendimentosProvisorios.setSigla(this.getSceEntrSaidSemLicitacaoDAO().pesquisarDescricaoTipoMovimento(
					rendimentosProvisorios.getEslSeq()));
		}
		return retorno;
	}

	/**
	 * # 25016 - RN 14: confirma recebimento para o fluxo de esl
	 * 
	 * @param notaRecebimentoProvisorio
	 * @param notaRecebimentoProvisorioOriginal
	 * @param servidorLogado
	 * @param nomeMicrocomputador
	 * @throws BaseException
	 */

	private void confirmarRecebimentoEsl(SceNotaRecebProvisorio notaRecebimentoProvisorio,
			SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal, RapServidores servidorLogado, String nomeMicrocomputador)
			throws BaseException {

		if (notaRecebimentoProvisorio.getDocumentoFiscalEntrada() == null) {
			throw new ApplicationBusinessException(ConfirmacaoRecebimentoONExceptionCode.ERRO_CONFIRMACAO_RECEBIMENTO_INFORMAR_NOTA_FISCAL);
		} else {
			verificarNotaFiscal(notaRecebimentoProvisorio);

			List<SceItemEntrSaidSemLicitacao> listaItemEntrSaidSemLicitacoes = this.getSceItemEntrSaidSemLicitacaoDAO()
					.listaItemEntrSaidSemLicitacao(notaRecebimentoProvisorio.getSeq());

			final AghParametros fornecedorParametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			ScoFornecedor fornecedor = null;

			if (fornecedorParametro != null) {

				fornecedor = this.getComprasFacade().obterFornecedorPorChavePrimaria(fornecedorParametro.getVlrNumerico().intValue());

			}

			for (SceItemEntrSaidSemLicitacao sceItemEntrSaidSemLicitacao : listaItemEntrSaidSemLicitacoes) {
				try {
					this.getSceMovimentoMaterialRN().atualizarMovimentoMaterial(sceItemEntrSaidSemLicitacao.getAlmoxarifados(),
							sceItemEntrSaidSemLicitacao.getScoMaterial(), sceItemEntrSaidSemLicitacao.getScoUnidadeMedida(),
							sceItemEntrSaidSemLicitacao.getQuantidade(), null, Boolean.FALSE,
							sceItemEntrSaidSemLicitacao.getSceEntrSaidSemLicitacao().getSceTipoMovimento(), null,
							sceItemEntrSaidSemLicitacao.getSceEntrSaidSemLicitacao().getSeq(), null,
							montaHistorico(sceItemEntrSaidSemLicitacao), fornecedor, null, null, null,
							BigDecimal.valueOf(sceItemEntrSaidSemLicitacao.getValor()), null, nomeMicrocomputador, true);
				} catch (ApplicationBusinessException e) {
					LOG.error(e.getMessage());
				}

			}

			this.getSceNotaRecebimentoProvisorioRN().atualizar(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal);
			SceEntrSaidSemLicitacao sceEsl = this.getSceEntrSaidSemLicitacaoDAO().obterPorChavePrimaria(
					notaRecebimentoProvisorio.getEslSeq());
			if (sceEsl != null) {
				sceEsl.setDtEfetivacao(new Date());
				sceEsl.setIndEfetivado("S");
				sceEsl.setServidorEfetivado(servidorLogado);
				this.getSceEntrSaidSemLicitacaoDAO().atualizar(sceEsl);
			}

			try {
				notaRecebimentoProvisorio.setIndConfirmado(Boolean.TRUE);
				this.getSceNotaRecebimentoProvisorioRN().atualizar(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal);
			} catch (ApplicationBusinessException e) {
				throw new ApplicationBusinessException(e.getCode(), e);
			}
		}
	}

	/**
	 * 
	 * @param sceItemEntrSaidSemLicitacao
	 * @return
	 */
	private String getDescricaoMarcaComercial(SceItemEntrSaidSemLicitacao sceItemEntrSaidSemLicitacao) {
		if (sceItemEntrSaidSemLicitacao.getScoMarcaComercial() != null) {
			if (sceItemEntrSaidSemLicitacao.getScoMarcaComercial().getDescricao() != null) {
				return sceItemEntrSaidSemLicitacao.getScoMarcaComercial().getDescricao();
			} else if (sceItemEntrSaidSemLicitacao.getScoNomeComercial() != null) {
				return sceItemEntrSaidSemLicitacao.getScoMarcaComercial().getCodigo() + ", "
						+ sceItemEntrSaidSemLicitacao.getScoNomeComercial().getNome();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param sceItemEntrSaidSemLicitacao
	 * @return
	 * @throws MECBaseException
	 */
	public String montaHistorico(SceItemEntrSaidSemLicitacao sceItemEntrSaidSemLicitacao) throws ApplicationBusinessException {
		StringBuilder historico = new StringBuilder(70);
		String documentoComercial = sceItemEntrSaidSemLicitacao.getSceEntrSaidSemLicitacao().getSceDocumentoFiscalEntrada() != null ? sceItemEntrSaidSemLicitacao
				.getSceEntrSaidSemLicitacao().getSceDocumentoFiscalEntrada().getSeq().toString()
				: "";
		historico.append("DOCUMENTO FISCAL DE ENTRADA: ").append(documentoComercial);
		/* Concatena dados do fornecedor. */
		String fornecedor = sceItemEntrSaidSemLicitacao.getSceEntrSaidSemLicitacao().getScoFornecedor() != null ? sceItemEntrSaidSemLicitacao
				.getSceEntrSaidSemLicitacao().getScoFornecedor().getNomeFantasia()
				: "";
		historico.append(" - Fornecedor: ").append(fornecedor)
		.append(" - Marca Comercial: ").append(getDescricaoMarcaComercial(sceItemEntrSaidSemLicitacao));
		return historico.toString();
	}

	protected SceNotaRecebimentoDAO getSceNotaRecebimentoDAO() {
		return sceNotaRecebimentoDAO;
	}

	protected SceItemRecebProvisorioDAO getSceItemRecebProvisorioDAO() {
		return sceItemRecebProvisorioDAO;
	}

	protected SceNotaRecebProvisorioDAO getSceNotaRecebProvisorioDAO() {
		return sceNotaRecebProvisorioDAO;
	}

	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}

	protected SceItemRecbXProgrEntregaDAO getSceItemRecbXProgrEntregaDAO() {
		return sceItemRecbXProgrEntregaDAO;
	}

	protected SceDocumentoFiscalEntradaDAO getSceDocumentoFiscalEntradaDAO() {
		return sceDocumentoFiscalEntradaDAO;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected SceNotaRecebimentoRN getSceNotaRecebimentoRN() {
		return sceNotaRecebimentoRN;
	}

	protected SceNotaRecebimentoProvisorioRN getSceNotaRecebimentoProvisorioRN() {
		return sceNotaRecebimentoProvisorioRN;
	}

	protected SceItemNotaRecebimentoRN getSceItemNotaRecebimentoRN() {
		return sceItemNotaRecebimentoRN;
	}

	protected SceItemRecebProvisorioRN getSceItemRecebProvisorioRN() {
		return sceItemRecebProvisorioRN;
	}

	protected SceItemRecbXProgrEntregaRN getSceItemRecbXProgrEntregaRN() {
		return sceItemRecbXProgrEntregaRN;
	}

	protected SceMovimentoMaterialRN getSceMovimentoMaterialRN() {
		return sceMovimentoMaterialRN;
	}

	protected SceEntrSaidSemLicitacaoDAO getSceEntrSaidSemLicitacaoDAO() {
		return sceEntrSaidSemLicitacaoDAO;
	}

	protected SceItemEntrSaidSemLicitacaoDAO getSceItemEntrSaidSemLicitacaoDAO() {
		return sceItemEntrSaidSemLicitacaoDAO;
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}
