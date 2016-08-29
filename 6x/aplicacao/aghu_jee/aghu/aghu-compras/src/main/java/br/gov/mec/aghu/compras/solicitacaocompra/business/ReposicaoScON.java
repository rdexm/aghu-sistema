package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoItemLoteReposicaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLoteReposicaoDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.vo.CriterioReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.FiltroReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.ItemReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.SolReposicaoMaterialVO;
import br.gov.mec.aghu.dominio.DominioBaseReposicao;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioInclusaoLoteReposicao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoLoteReposicao;
import br.gov.mec.aghu.dominio.DominioTipoConsumo;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.EstatisticaEstoqueAlmoxarifadoVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.ScoItemLoteReposicao;
import br.gov.mec.aghu.model.ScoLoteReposicao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ReposicaoScON extends BaseBusiness {

	private static final long serialVersionUID = -5922834917785524834L;
	private static final Log LOG = LogFactory.getLog(ReposicaoScON.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private ScoItemLoteReposicaoDAO scoItemLoteReposicaoDAO;

	@Inject
	private ScoLoteReposicaoDAO scoLoteReposicaoDAO;

	@Inject
	private ScoMaterialDAO scoMaterialDAO;

	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;

	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;

	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

	@EJB
	private GerarSolicitacaoCompraAlmoxarifadoRN gerarSolicitacaoCompraAlmoxarifadoRN;

	@EJB
	private SolicitacaoCompraRN solicitacaoCompraRN;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public enum ReposicaoScONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CAMPOS_GERACAO_NAO_INFORMADOS, MENSAGEM_CONSUMO_NAO_INFORMADO, MENSAGEM_PERIODO_FUTURO, MENSAGEM_PERIODO_MINIMO_UM_DIA, MENSAGEM_PERIODO_NAO_VALIDO, MENSAGEM_PERIODO_PASSADO, MENSAGEM_NOME_LOTE_NAO_INFORMADO, QUANTIDADE_ZERO_INFORMADA, MENSAGEM_EXCLUSAO_LOTE_NAO_PERMITIDA, MENSAGEM_EXCLUSAO_ITEM_LOTE_NAO_PERMITIDA, MENSAGEM_EXCLUSAO_ITEM_LOTE_SEM_SC;
	}

	public ItemReposicaoMaterialVO montarItemReposicaoVO(ScoMaterial material, ScoLoteReposicao loteReposicao, ScoSolicitacaoDeCompra slc,
			CriterioReposicaoMaterialVO criterioReposicao) {

		material = this.getScoMaterialDAO().merge(material); 
		
		AghParametros fornecPadrao = null;
		AghParametros competencia = null;
		AghParametros almoxPadrao = null;
		try {
			fornecPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			competencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
			almoxPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL);
		} catch (ApplicationBusinessException e) {
			LOG.error("Parametros não cadastrados");
		}

		if (criterioReposicao == null) {
			criterioReposicao = this.montarCriterioReposicao(loteReposicao);
		}

		ItemReposicaoMaterialVO item = new ItemReposicaoMaterialVO();
		item.setCodigoGrupoMaterial(material.getGmtCodigo());

		item.setDescricaoMaterial(material.getDescricao());
		item.setMatCodigo(material.getCodigo());
		item.setNomeGrupoMaterial(material.getGrupoMaterial().getDescricao());
		item.setNomeMaterial(material.getNome());

		if (slc != null) {
			item.setQtd(slc.getQtdeAprovada().intValue());
			item.setQtdOriginal(item.getQtd());
			item.setListaScRelacionada(getScoItemLoteReposicaoDAO().pesquisarSolicitacoesEmLoteSemPac(slc));
			item.setSlcGerada(slc.getNumero());
		} else {

			BigDecimal fatorSegurancaCalculado = calcularFatorSeguranca(criterioReposicao.getFatorSeguranca());
			BigDecimal intervaloDias = DateUtil.calcularDiasEntreDatasComPrecisao(criterioReposicao.getDataInicioReposicao(),
					criterioReposicao.getDataFimReposicao());

			if (criterioReposicao.getBaseReposicao().equals(DominioBaseReposicao.HC)) {
				item.setQtd(calcularHistoricoConsumo(item.getMatCodigo(), criterioReposicao, fatorSegurancaCalculado,
						loteReposicao.getCentroCustoAplicacao()).setScale(0, RoundingMode.HALF_UP).intValue());
			} else {
				item.setQtd(calcularConsumoProjetado(item.getMatCodigo(), criterioReposicao, fatorSegurancaCalculado, intervaloDias,
						competencia.getVlrData(), almoxPadrao.getVlrNumerico().shortValue(), fornecPadrao.getVlrNumerico().intValue())
						.setScale(0, RoundingMode.HALF_UP).intValue());
			}

			item.setQtdOriginal(Integer.valueOf(item.getQtd()));
			item.setListaScRelacionada(getScoItemLoteReposicaoDAO().pesquisarSolicitacoesEmLoteSemPac(item.getMatCodigo(),
					loteReposicao.getCentroCustoAplicacao()));

			if (item.getListaScRelacionada() != null && !item.getListaScRelacionada().isEmpty()) {
				item.setSlcGerada(item.getListaScRelacionada().get(0).getSlcNumero());
			}
		}

		SceEstoqueGeral egr = this.getEstoqueFacade().obterSceEstoqueGeralPorMaterialDataCompetencia(material, competencia.getVlrData());
		if (egr != null) {
			item.setCustoMedio(egr.getCustoMedioPonderado());
		}
		SceEstoqueAlmoxarifado eal = this.getEstoqueFacade().obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(
				almoxPadrao.getVlrNumerico().shortValue(), material.getCodigo(), fornecPadrao.getVlrNumerico().intValue());
		if (eal != null) {
			item.setPontoPedido(eal.getQtdePontoPedido());
			item.setTempoReposicao(eal.getTempoReposicao());
		}

		if (material.getIndEstocavel().isSim()) {
			item.setTipoMaterial(DominioTipoMaterial.E.getDescricao());
		} else {
			item.setTipoMaterial(DominioTipoMaterial.D.getDescricao());
		}

		item.setConfirmada(false);
		item.setIndInclusao(DominioInclusaoLoteReposicao.MN);

		return item;
	}

	private void verificarMovimentacaoScLoteReposicao(ScoLoteReposicao loteReposicao, Integer seq) throws ApplicationBusinessException {
		ScoPontoParadaSolicitacao pontoParadaAnterior = getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(
				this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.PPS_GER_AUTO).getVlrNumerico().shortValue());
		ScoPontoParadaSolicitacao pontoParadaAtual = this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(
				DominioTipoPontoParada.PL);

		if (seq == null) {
			if (this.getScoItemLoteReposicaoDAO().verificarMovimentacaoScLoteReposicao(loteReposicao, pontoParadaAtual,
					pontoParadaAnterior, null)) {
				throw new ApplicationBusinessException(ReposicaoScONExceptionCode.MENSAGEM_EXCLUSAO_LOTE_NAO_PERMITIDA,
						loteReposicao.getSeq());
			}
		} else {
			if (this.getScoItemLoteReposicaoDAO().verificarMovimentacaoScLoteReposicao(loteReposicao, pontoParadaAtual,
					pontoParadaAnterior, seq)) {
				ScoItemLoteReposicao ilr = this.getScoItemLoteReposicaoDAO().obterPorChavePrimaria(seq);
				throw new ApplicationBusinessException(ReposicaoScONExceptionCode.MENSAGEM_EXCLUSAO_ITEM_LOTE_NAO_PERMITIDA, ilr
						.getSolicitacao().getNumero(), loteReposicao.getSeq());
			}
		}
	}

	public void excluirItemLote(ScoLoteReposicao loteReposicao, Integer seq) throws BaseException {
		this.verificarMovimentacaoScLoteReposicao(loteReposicao, seq);
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		ScoItemLoteReposicao ilr = this.getScoItemLoteReposicaoDAO().obterPorChavePrimaria(seq);

		if (ilr != null) {
			if (ilr.getSolicitacao() == null) {
				throw new ApplicationBusinessException(ReposicaoScONExceptionCode.MENSAGEM_EXCLUSAO_ITEM_LOTE_SEM_SC);
			}
			ScoLoteReposicao lote = this.getScoLoteReposicaoDAO().obterPorChavePrimaria(loteReposicao.getSeq());

			if (lote != null) {
				String justificativa = this.getResourceBundleValue("MENSAGEM_EXCLUSAO_SC_ITEM_LOTE_REPOSICAO", lote.getSeq().toString());
				lote.setSituacao(DominioSituacaoLoteReposicao.AL);
				lote.setServidorAlteracao(servidorLogado);
				lote.setDtAlteracao(new Date());
				this.getScoLoteReposicaoDAO().persistir(lote);

				List<Integer> listaScs = new ArrayList<Integer>();
				listaScs.add(ilr.getSolicitacao().getNumero());

				this.getSolicitacaoCompraRN().inativarListaSolicitacaoCompras(listaScs, justificativa);

				ilr.setIndSelecionado(false);
				ilr.setSolicitacao(null);
				this.getScoItemLoteReposicaoDAO().persistir(ilr);
			}
		}
	}

	public void excluirLote(ScoLoteReposicao loteReposicao) throws BaseException {
		this.verificarMovimentacaoScLoteReposicao(loteReposicao, null);
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		ScoLoteReposicao lote = this.getScoLoteReposicaoDAO().obterPorChavePrimaria(loteReposicao.getSeq());

		if (lote != null) {
			String justificativa = this.getResourceBundleValue("MENSAGEM_EXCLUSAO_SC_LOTE_REPOSICAO", lote.getSeq().toString());
			lote.setSituacao(DominioSituacaoLoteReposicao.EX);
			lote.setServidorExclusao(servidorLogado);
			lote.setDtExclusao(new Date());
			this.getScoLoteReposicaoDAO().persistir(lote);

			List<Integer> listaScs = new ArrayList<Integer>();
			List<ScoItemLoteReposicao> listaItens = this.getScoItemLoteReposicaoDAO().pesquisarScLoteReposicao(lote);
			if (listaItens != null && !listaItens.isEmpty()) {
				for (ScoItemLoteReposicao item : listaItens) {
					listaScs.add(item.getSolicitacao().getNumero());
				}
			}

			this.getSolicitacaoCompraRN().inativarListaSolicitacaoCompras(listaScs, justificativa);
		}
	}

	public void validarCamposObrigatorios(CriterioReposicaoMaterialVO criterioGeracao, Boolean simular) throws ApplicationBusinessException {
		if (criterioGeracao.getBaseReposicao() == null || criterioGeracao.getDataInicioReposicao() == null
				|| criterioGeracao.getDataFimReposicao() == null || criterioGeracao.getFatorSeguranca() == null) {
			throw new ApplicationBusinessException(ReposicaoScONExceptionCode.MENSAGEM_CAMPOS_GERACAO_NAO_INFORMADOS);
		}

		if (!simular && StringUtils.isEmpty(criterioGeracao.getNomeLote())) {
			throw new ApplicationBusinessException(ReposicaoScONExceptionCode.MENSAGEM_NOME_LOTE_NAO_INFORMADO);
		}

		if (criterioGeracao.getBaseReposicao() != null) {
			if (DateUtil.diffInDays(criterioGeracao.getDataFimReposicao(), criterioGeracao.getDataInicioReposicao()) <= 1L) {
				throw new ApplicationBusinessException(ReposicaoScONExceptionCode.MENSAGEM_PERIODO_MINIMO_UM_DIA);
			}
			if (DateUtil.validaDataMaiorIgual(criterioGeracao.getDataInicioReposicao(), criterioGeracao.getDataFimReposicao())) {
				throw new ApplicationBusinessException(ReposicaoScONExceptionCode.MENSAGEM_PERIODO_NAO_VALIDO);
			}
			if (criterioGeracao.getBaseReposicao().equals(DominioBaseReposicao.CP)) {

				if (criterioGeracao.getTipoConsumo() == null) {
					throw new ApplicationBusinessException(ReposicaoScONExceptionCode.MENSAGEM_CONSUMO_NAO_INFORMADO);
				}
				if (DateUtil.truncaData(criterioGeracao.getDataInicioReposicao()).before(
						DateUtils.addDays(DateUtil.truncaData(new Date()), 1))) {
					throw new ApplicationBusinessException(ReposicaoScONExceptionCode.MENSAGEM_PERIODO_FUTURO);
				}

			} else if (criterioGeracao.getBaseReposicao().equals(DominioBaseReposicao.HC)) {
				if (DateUtil.validaDataMaiorIgual(criterioGeracao.getDataFimReposicao(), new Date())) {
					throw new ApplicationBusinessException(ReposicaoScONExceptionCode.MENSAGEM_PERIODO_PASSADO);
				}
			}
		}
	}

	public void inserirInclusaoManualLoteReposicao(ScoLoteReposicao lote, List<ItemReposicaoMaterialVO> listaInclusaoPontual)
			throws ApplicationBusinessException {

		FccCentroCustos ccSolic = getCentroCustoFacade().obterFccCentroCustos(
				this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.CENTRO_CUSTO_SOLIC).getVlrNumerico().intValue());

		FccCentroCustos ccAplic = getCentroCustoFacade().obterFccCentroCustos(
				this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.CENTRO_CUSTO_APLIC).getVlrNumerico().intValue());

		ScoPontoParadaSolicitacao pontoParadaAnterior = getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(
				this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.PPS_GER_AUTO).getVlrNumerico().shortValue());
		ScoPontoParadaSolicitacao pontoParadaAtual = this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(
				DominioTipoPontoParada.PL);

		AghParametros parametroTipoMovimento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR);

		for (ItemReposicaoMaterialVO item : listaInclusaoPontual) {
			ScoMaterial mat = this.getScoMaterialDAO().obterPorChavePrimaria(item.getMatCodigo());
			ScoItemLoteReposicao it = new ScoItemLoteReposicao();
			it.setLoteReposicao(lote);			
			it.setMaterial(mat);
			it.setGrupoMaterial(mat.getGrupoMaterial());
			it.setTipoMaterial(mat.getEstocavel() ? DominioTipoMaterial.E : DominioTipoMaterial.D);
			it.setCustoMedio((BigDecimal) CoreUtil.nvl(item.getCustoMedio(), BigDecimal.ZERO));
			it.setQtdPontoPedido((Integer) CoreUtil.nvl(item.getPontoPedido(), 0));
			it.setTempoReposicao((Integer) CoreUtil.nvl(item.getTempoReposicao(), 0));
			it.setQtdGerada((Integer) CoreUtil.nvl(item.getQtdOriginal(), 0));
			it.setQtdConfirmada((Integer) CoreUtil.nvl(item.getQtd(), 0));
			it.setIndInclusao(DominioInclusaoLoteReposicao.MN);

			if (item.getQtd() > 0) {
				ScoSolicitacaoDeCompra sc = null;

				if (item.getSlcGerada() == null) {
					sc = this.criarSolicitacao(ccSolic,
							(lote.getCentroCustoAplicacao() != null) ? lote.getCentroCustoAplicacao() : ccAplic, parametroTipoMovimento,
							mat, lote.getDescricao(), pontoParadaAnterior, pontoParadaAtual, item);
				} else {
					sc = this.getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(item.getSlcGerada());
					if (sc != null) {

						if (item.getListaScRelacionada() != null && !item.getListaScRelacionada().isEmpty()) {
							this.transferirScLote(item, sc);
						}

						// insere a SC no lote que está sendo criado
						it.setIndInclusao(DominioInclusaoLoteReposicao.OL);
						it.setSolicitacao(sc);
					}
				}
				it.setSolicitacao(sc);
				it.setIndSelecionado(true);
			} else {
				it.setSolicitacao(null);
				it.setIndSelecionado(true);
			}
			this.getScoItemLoteReposicaoDAO().persistir(it);
		}
	}

	private void transferirScLote(ItemReposicaoMaterialVO item, ScoSolicitacaoDeCompra scRelacionada) {
		// atualiza o status do outro lote para alterado		
		ScoLoteReposicao outroLote = this.getScoLoteReposicaoDAO().obterPorChavePrimaria(item.getListaScRelacionada().get(0).getLtrSeq());
		if (outroLote != null) {
			outroLote.setSituacao(DominioSituacaoLoteReposicao.AL);
			outroLote.setDtAlteracao(new Date());
			outroLote.setServidorAlteracao(this.getServidorLogadoFacade().obterServidorLogado());
			this.getScoLoteReposicaoDAO().persistir(outroLote);
		}

		// remove do outro lote o relacionamento com a SC
		ScoItemLoteReposicao itemLote = this.getScoItemLoteReposicaoDAO().obterPorChavePrimaria(
				item.getListaScRelacionada().get(0).getSeqItem());
		itemLote.setIndSelecionado(false);
		itemLote.setSolicitacao(null);
		this.getScoItemLoteReposicaoDAO().persistir(itemLote);

		// se a SC possui qtd diferente da digitada...
		if (!Objects.equals(item.getListaScRelacionada().get(0).getQtdAprovada(), item.getQtd().longValue())) {
			// atualiza a qtd aprovada da SC
			scRelacionada.setQtdeSolicitada(item.getQtd().longValue());
			scRelacionada.setQtdeAprovada(scRelacionada.getQtdeSolicitada());
			this.getScoSolicitacoesDeComprasDAO().persistir(scRelacionada);
		}
	}

	private ScoSolicitacaoDeCompra criarSolicitacao(FccCentroCustos ccSolic, FccCentroCustos ccAplic, AghParametros parametroTipoMovimento,
			ScoMaterial mat, String nomeLote, ScoPontoParadaSolicitacao pontoParadaAnterior, ScoPontoParadaSolicitacao pontoParadaAtual,
			ItemReposicaoMaterialVO item) throws ApplicationBusinessException {
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		Integer qtdePropLidas = 0;
		Integer qtdeProp = 0;
		Double valorUnitarioAcumulado = 0.0;
		RapServidores servidorAutorizada = ccSolic.getRapServidor();

		Double valorUnitPrevisto = getEstoqueFacade().buscarUltimoCustoEntradaPorMaterialTipoMov(mat,
				parametroTipoMovimento.getVlrNumerico().shortValue());

		List<Double> valoresUnitario = getScoItemPropostaFornecedorDAO().buscarValorUnitarioPropostas2Anos(mat);

		for (Double valorUnitario : valoresUnitario) {
			qtdePropLidas++;

			if (qtdePropLidas < 11) {
				qtdeProp++;
				valorUnitarioAcumulado += valorUnitario;
			}
		}

		if (qtdeProp > 0) {
			valorUnitPrevisto = valorUnitarioAcumulado / qtdeProp;
		} else {
			valorUnitPrevisto = 1.0;
		}

		// Se o CCusto não tiver Usuário responsável, usa usuário
		// autorizante OU conectado.
		if (servidorAutorizada == null) {
			servidorAutorizada = servidorLogado;
		}

		ScoSolicitacaoDeCompra solicitacaoDeCompra = new ScoSolicitacaoDeCompra();
		solicitacaoDeCompra.setPontoParada(pontoParadaAnterior);
		solicitacaoDeCompra.setPontoParadaProxima(pontoParadaAtual);
		solicitacaoDeCompra.setServidor(servidorLogado);
		solicitacaoDeCompra.setMaterial(mat);
		solicitacaoDeCompra.setCentroCusto(ccSolic);
		if (item.getCcAlmoxLocalEstoque() != null) {
			solicitacaoDeCompra.setCentroCustoAplicada(item.getCcAlmoxLocalEstoque());
		} else {
			solicitacaoDeCompra.setCentroCustoAplicada(ccAplic);
		}
		solicitacaoDeCompra.setServidorAutorizacao(servidorAutorizada);
		solicitacaoDeCompra.setDtSolicitacao(new Date());
		solicitacaoDeCompra.setDtDigitacao(new Date());
		solicitacaoDeCompra.setExclusao(false);
		solicitacaoDeCompra.setUrgente(false);
		solicitacaoDeCompra.setDevolucao(false);
		solicitacaoDeCompra.setOrcamentoPrevio(DominioSimNao.N.toString());
		solicitacaoDeCompra.setDiasDuracao(Short.valueOf("999"));
		solicitacaoDeCompra.setQtdeReforco(Long.valueOf(0));
		solicitacaoDeCompra.setDtAutorizacao(new Date());
		solicitacaoDeCompra.setValorUnitPrevisto(new BigDecimal(valorUnitPrevisto));
		solicitacaoDeCompra.setGeracaoAutomatica(true);
		solicitacaoDeCompra.setQtdeEntregue(Long.valueOf(0));
		solicitacaoDeCompra.setEfetivada(false);
		solicitacaoDeCompra.setFundoFixo(false);
		solicitacaoDeCompra.setUnidadeMedida(mat.getUnidadeMedida());
		solicitacaoDeCompra.setRecebimento(false);
		solicitacaoDeCompra.setAlmoxarifado(null);
		solicitacaoDeCompra.setPrioridade(Boolean.FALSE);
		solicitacaoDeCompra.setQtdeSolicitada(item.getQtd().longValue());
		solicitacaoDeCompra.setQtdeAprovada(item.getQtd().longValue());

		getGerarSolicitacaoCompraAlmoxarifadoRN().aplicarRegrasOrcamentarias(solicitacaoDeCompra);

		// aqui não passa pelas RNs pois o método acima acabou de já validar as
		// regras orçamentárias.
		// se passar pela RN, leva cerca de 7s por material, do jeito que está,
		// leva 3.
		this.getScoSolicitacoesDeComprasDAO().persistir(solicitacaoDeCompra);

		this.getSolicitacaoCompraRN().inserirSolicitacaoCompraJournal(solicitacaoDeCompra, DominioOperacoesJournal.UPD);

		return solicitacaoDeCompra;

	}

	public BigDecimal calcularConsumoProjetado(Integer matCodigo, CriterioReposicaoMaterialVO criterioReposicao,
			BigDecimal fatorSegurancaCalculado, BigDecimal intervaloDiasCalculado, Date paramCompetencia, Short almCentral,
			Integer fornPadrao) {

		BigDecimal consumoProjetado = BigDecimal.ZERO;
		Boolean pontoPedidoFixado = false;

		SceEstoqueAlmoxarifado ealm = this.getEstoqueFacade().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoMaterialFornecedor(almCentral,
				matCodigo, fornPadrao);
		if (ealm != null) {
			pontoPedidoFixado = ealm.getIndPontoPedidoCalc();
		}

		if (pontoPedidoFixado) {
			consumoProjetado = intervaloDiasCalculado.multiply(
					new BigDecimal(ealm.getQtdePontoPedido()).divide(new BigDecimal(ealm.getTempoReposicao()), 2, RoundingMode.HALF_UP))
					.multiply(fatorSegurancaCalculado);
		} else {
			EstatisticaEstoqueAlmoxarifadoVO estatVO = new EstatisticaEstoqueAlmoxarifadoVO();

			try {
				this.getEstoqueFacade().popularHistoricoConsumo(estatVO, matCodigo, null, paramCompetencia);
			} catch (ApplicationBusinessException e) {
				LOG.error("Nao foi possivel calcular o historico de consumo: " + e.getMessage());
			}

			if (criterioReposicao.getTipoConsumo().equals(DominioTipoConsumo.M)) {
				this.getEstoqueFacade().popularConsumoMedio(estatVO);

				BigDecimal consumoMedio = null;

				try {
					consumoMedio = new BigDecimal(estatVO.getQtdeConsumoMedio().replace(",", "."));
				} catch (NumberFormatException e) {
					LOG.error("Nao foi possivel calcular o consumo medio (" + estatVO.getQtdeConsumoMedio() + "): " + e.getMessage());
					consumoMedio = BigDecimal.ZERO;
				}

				consumoProjetado = consumoMedio.divide(new BigDecimal(30), 2, RoundingMode.HALF_UP).multiply(intervaloDiasCalculado)
						.multiply(fatorSegurancaCalculado);
			} else {
				this.getEstoqueFacade().popularConsumoPonderado(estatVO);

				BigDecimal consumoPonderado = null;

				try {
					consumoPonderado = new BigDecimal(estatVO.getQtdeConsumoPonderado().replace(",", "."));
				} catch (NumberFormatException e) {
					LOG.error("Nao foi possivel calcular o consumo ponderado (" + estatVO.getQtdeConsumoPonderado() + "): "
							+ e.getMessage());
					consumoPonderado = BigDecimal.ZERO;
				}
				consumoProjetado = consumoPonderado.divide(new BigDecimal(30), 2, RoundingMode.HALF_UP).multiply(intervaloDiasCalculado)
						.multiply(fatorSegurancaCalculado);
			}
		}
		return consumoProjetado;
	}

	private BigDecimal calcularHistoricoConsumo(Integer matCodigo, CriterioReposicaoMaterialVO criterioReposicao,
			BigDecimal fatorSegurancaCalculado, FccCentroCustos ccAplic) {

		BigDecimal consumo = new BigDecimal((Long) CoreUtil.nvl(
				getScoMaterialDAO().obterHistoricoConsumo(criterioReposicao.getDataInicioReposicao(),
						criterioReposicao.getDataFimReposicao(), matCodigo, ccAplic), 0L)
				- (Long) CoreUtil.nvl(
						getScoMaterialDAO().obterHistoricoDevolucao(criterioReposicao.getDataInicioReposicao(),
								criterioReposicao.getDataFimReposicao(), matCodigo, ccAplic), 0L));

		consumo = consumo.multiply(fatorSegurancaCalculado);

		return consumo;
	}

	private BigDecimal calcularFatorSeguranca(BigDecimal fatorSeguranca) {
		return fatorSeguranca.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).add(BigDecimal.ONE);
	}

	public List<ItemReposicaoMaterialVO> pesquisarMaterialReposicao(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FiltroReposicaoMaterialVO filtro, CriterioReposicaoMaterialVO criterioReposicao) {
		List<ItemReposicaoMaterialVO> listaItens = getScoMaterialDAO().pesquisarMaterialReposicao(firstResult, maxResult, orderProperty,
				asc, filtro);
		this.processarListaReposicao(filtro.getSomentePesquisa(), criterioReposicao, listaItens, filtro.getCentroCustoAplicacao(), filtro
				.getAlmoxarifadoPadrao().getVlrNumerico().shortValue(), filtro.getFornecedorPadrao().getVlrNumerico().intValue());
		return listaItens;
	}

	public List<ItemReposicaoMaterialVO> pesquisarMaterialReposicao(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoLoteReposicao lote) {
		List<ItemReposicaoMaterialVO> listaItens = getScoItemLoteReposicaoDAO().pesquisarMaterialReposicao(firstResult, maxResult,
				orderProperty, asc, lote);

		for (ItemReposicaoMaterialVO item : listaItens) {
			item.setListaScRelacionada(getScoItemLoteReposicaoDAO().pesquisarSolicitacoesEmLoteSemPac(item.getMatCodigo(),
					lote.getCentroCustoAplicacao()));
			item.setConfirmada(true);
		}

		return listaItens;
	}

	public List<ItemReposicaoMaterialVO> pesquisarMaterialReposicao(FiltroReposicaoMaterialVO filtro,
			CriterioReposicaoMaterialVO criterioReposicao) {
		List<ItemReposicaoMaterialVO> listaItens = getScoMaterialDAO().pesquisarMaterialReposicao(filtro);
		this.processarListaReposicao(filtro.getSomentePesquisa(), criterioReposicao, listaItens, filtro.getCentroCustoAplicacao(), filtro
				.getAlmoxarifadoPadrao().getVlrNumerico().shortValue(), filtro.getFornecedorPadrao().getVlrNumerico().intValue());
		return listaItens;
	}

	private void processarListaReposicao(Boolean somentePesquisa, CriterioReposicaoMaterialVO criterioReposicao,
			List<ItemReposicaoMaterialVO> listaItens, FccCentroCustos ccAplic, Short almCentral, Integer fornPadrao) {
		if (listaItens == null) {
			listaItens = new ArrayList<ItemReposicaoMaterialVO>();
		}

		BigDecimal fatorSegurancaCalculado = null;
		BigDecimal intervaloDias = null;

		if (somentePesquisa != null && !somentePesquisa) {
			fatorSegurancaCalculado = calcularFatorSeguranca(criterioReposicao.getFatorSeguranca());
			intervaloDias = DateUtil.calcularDiasEntreDatasComPrecisao(criterioReposicao.getDataInicioReposicao(),
					criterioReposicao.getDataFimReposicao());
		}

		AghParametros competencia = null;
		try {
			competencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		} catch (ApplicationBusinessException e) {
			LOG.error("Parametros não cadastrados");
		}

		for (ItemReposicaoMaterialVO item : listaItens) {
			if (somentePesquisa != null && !somentePesquisa) {
				if (criterioReposicao.getBaseReposicao().equals(DominioBaseReposicao.HC)) {
					item.setQtd(calcularHistoricoConsumo(item.getMatCodigo(), criterioReposicao, fatorSegurancaCalculado, ccAplic)
							.setScale(0, RoundingMode.HALF_UP).intValue());
				} else {
					item.setQtd(calcularConsumoProjetado(item.getMatCodigo(), criterioReposicao, fatorSegurancaCalculado, intervaloDias,
							competencia.getVlrData(), almCentral, fornPadrao).setScale(0, RoundingMode.HALF_UP).intValue());
				}
				item.setQtdOriginal(Integer.valueOf(item.getQtd()));
			}

			item.setListaScRelacionada(getScoItemLoteReposicaoDAO().pesquisarSolicitacoesEmLoteSemPac(item.getMatCodigo(), ccAplic));

			if (item.getListaScRelacionada() != null && !item.getListaScRelacionada().isEmpty()) {
				item.setSlcGerada(item.getListaScRelacionada().get(0).getSlcNumero());
			}
		}
	}

	public CriterioReposicaoMaterialVO montarCriterioReposicao(ScoLoteReposicao loteReposicao) {
		CriterioReposicaoMaterialVO criterio = new CriterioReposicaoMaterialVO();
		if (loteReposicao.getBaseReposicao() != null) {
			criterio.setBaseReposicao(loteReposicao.getBaseReposicao());
		}
		if (loteReposicao.getDataInicioReposicao() != null) {
			criterio.setDataInicioReposicao(loteReposicao.getDataInicioReposicao());
		}
		if (loteReposicao.getDataFimReposicao() != null) {
			criterio.setDataFimReposicao(loteReposicao.getDataFimReposicao());
		}
		if (loteReposicao.getFatorSeguranca() != null) {
			criterio.setFatorSeguranca(loteReposicao.getFatorSeguranca());
		}
		if (loteReposicao.getDescricao() != null) {
			criterio.setNomeLote(loteReposicao.getDescricao());
		}
		if (loteReposicao.getTipoConsumo() != null) {
			criterio.setTipoConsumo(loteReposicao.getTipoConsumo());
		}
		return criterio;
	}

	public String getListaScs(ItemReposicaoMaterialVO item) {
		StringBuilder sb = new StringBuilder();

		for (SolReposicaoMaterialVO sc : item.getListaScRelacionada()) {
			String key = getResourceBundleValue("TOOLTIP_LOTE_SC_EM_OUTRO_LOTE");
			String msg = MessageFormat.format(key, sc.getSlcNumero(), sc.getQtdAprovada(), sc.getDescricaoLote());

			sb.append(msg).append("<br />");
		}

		return sb.toString();
	}

	public String getScRelacionada(ItemReposicaoMaterialVO item) {
		String ret = "";
		if (item.getListaScRelacionada() != null && !item.getListaScRelacionada().isEmpty() && item.getListaScRelacionada().size() == 1) {
			ret = item.getListaScRelacionada().get(item.getListaScRelacionada().size() - 1).getSlcNumero().toString();
		} else {
			StringBuilder sb = new StringBuilder();
			ret = sb.append(item.getListaScRelacionada().get(item.getListaScRelacionada().size() - 1).getSlcNumero().toString())
					.append("...").toString();
		}

		return ret;
	}

	public String getSplited(final String descricao, final Integer tam) {
		final String spliter = "<br />";

		if (descricao == null) {
			return null;
		}
		if (tam == null || tam <= 0 || descricao.length() <= tam) {
			return descricao;
		}
		final StringBuffer ret = new StringBuffer(descricao.length());
		int i = 0;
		while ((i + tam) < descricao.length()) {
			final String tmp = descricao.substring(i, (i + tam));
			int fim = tmp.lastIndexOf(' ');
			if (fim <= 0) {
				fim = tam;
			}
			if (i > 0) {
				ret.append(spliter);
			}
			ret.append(tmp.substring(0, fim));
			i += fim;
		}
		ret.append(spliter).append(descricao.substring(i));
		return ret.toString();
	}

	public ScoLoteReposicao criarCabecalhoLoteReposicao(FiltroReposicaoMaterialVO filtro, CriterioReposicaoMaterialVO criterioGeracao) {
		ScoLoteReposicao lote = new ScoLoteReposicao();
		lote.setDescricao(criterioGeracao.getNomeLote());
		lote.setSituacao(DominioSituacaoLoteReposicao.GR);
		lote.setIndContrato(false);
		this.preencherFiltroLote(filtro, lote);
		this.preencherFiltroAnaliseLote(filtro, lote);
		this.preencherFiltroPlanejamentoLote(filtro, lote);
		if (filtro.getIndEmAf() != null) {
			lote.setIndEmAf(filtro.getIndEmAf().isSim());
		}

		if (filtro.getIndEmSc() != null) {
			lote.setIndEmSc(filtro.getIndEmSc().isSim());
		}

		lote.setBaseReposicao(criterioGeracao.getBaseReposicao());
		lote.setDataInicioReposicao(criterioGeracao.getDataInicioReposicao());
		lote.setDataFimReposicao(criterioGeracao.getDataFimReposicao());

		if (criterioGeracao.getTipoConsumo() != null) {
			lote.setTipoConsumo(criterioGeracao.getTipoConsumo());
		}
		lote.setFatorSeguranca(criterioGeracao.getFatorSeguranca());
		lote.setServidorGeracao(this.getServidorLogadoFacade().obterServidorLogado());
		lote.setDtGeracao(new Date());

		this.getScoLoteReposicaoDAO().persistir(lote);

		return lote;
	}

	private void preencherFiltroLote(FiltroReposicaoMaterialVO filtro, ScoLoteReposicao lote) {
		if (filtro.getTipoMaterial() != null) {
			lote.setTipoMaterial(filtro.getTipoMaterial());
		}

		if (filtro.getGrupoMaterial() != null) {
			lote.setGrupoMaterial(filtro.getGrupoMaterial());
		}

		if (filtro.getListaClassAbc() != null && !filtro.getListaClassAbc().isEmpty()) {
			StringBuilder classAbc = new StringBuilder();
			for (DominioClassifABC d : filtro.getListaClassAbc()) {
				classAbc.append(d);
			}
			lote.setClassificacaoAbc(classAbc.toString());
		}

		if (filtro.getIndLicitacao() != null) {
			lote.setIndEmLicitacao(filtro.getIndLicitacao().isSim());
		}

		if (filtro.getDataVigencia() != null) {
			lote.setDtVigenciaContrato(filtro.getDataVigencia());
		}

		if (filtro.getClassificacaoMaterial() != null) {
			lote.setCn5Numero(filtro.getClassificacaoMaterial().getId().getNumero());
		}
	}
	
	private void preencherFiltroPlanejamentoLote(FiltroReposicaoMaterialVO filtro, ScoLoteReposicao lote) {
		if (filtro.getIndProducaoInterna() != null) {
			lote.setIndProducaoInterna(filtro.getIndProducaoInterna().isSim());
		}
		if (filtro.getIndComLicitacao() != null) {
			lote.setIndComLicitacao(filtro.getIndComLicitacao().isSim());
		}
		if (filtro.getIndPontoPedido() != null) {
			lote.setIndPontoPedido(filtro.getIndPontoPedido().isSim());
		}
		if (filtro.getCobertura() != null) {
			lote.setCobertura(filtro.getCobertura());
		}
		if (filtro.getIndAfContrato() != null) {
			lote.setIndAfContrato(filtro.getIndAfContrato().isSim());
		}
		if (filtro.getIndAfVencida() != null) {
			lote.setIndAfVencida(filtro.getIndAfVencida().isSim());
		}
		if (filtro.getIndItemContrato() != null) {
			lote.setIndItemContrato(filtro.getIndItemContrato().isSim());
		}
	}

	private void preencherFiltroAnaliseLote(FiltroReposicaoMaterialVO filtro, ScoLoteReposicao lote) {
		if (filtro.getCentroCustoAplicacao() != null) {
			lote.setCentroCustoAplicacao(filtro.getCentroCustoAplicacao());
		}

		if (filtro.getModalidade() != null) {
			lote.setModalidade(filtro.getModalidade());
		}

		if (filtro.getBase() != null) {
			lote.setBaseAnalise(filtro.getBase());
		}

		if (filtro.getDataInicio() != null) {
			lote.setDtInicioBaseAnalise(filtro.getDataInicio());
		}

		if (filtro.getDataFim() != null) {
			lote.setDtFimBaseAnalise(filtro.getDataFim());
		}

		if (filtro.getFrequencia() != null) {
			lote.setFrequenciaBaseAnalise(filtro.getFrequencia());
		}

		if (filtro.getVlrInicial() != null) {
			lote.setVlrInicioBaseAnalise(filtro.getVlrInicial());
		}

		if (filtro.getVlrFinal() != null) {
			lote.setVlrFinalBaseAnalise(filtro.getVlrFinal());
		}
	}

	public void inserirItemLoteReposicao(ItemReposicaoMaterialVO item, List<Integer> nroDesmarcados,
			List<ItemReposicaoMaterialVO> listaAlteracoes, ScoLoteReposicao lote, String nomeLote, FccCentroCustos ccSolic,
			FccCentroCustos ccAplic, AghParametros parametroTipoMovimento, ScoPontoParadaSolicitacao pontoParadaAnterior,
			ScoPontoParadaSolicitacao pontoParadaAtual) throws ApplicationBusinessException {

		ScoMaterial mat = this.getScoMaterialDAO().obterPorChavePrimaria(item.getMatCodigo());
		ScoItemLoteReposicao it = new ScoItemLoteReposicao();
		it.setLoteReposicao(lote);
		it.setMaterial(mat);
		it.setGrupoMaterial(mat.getGrupoMaterial());
		it.setTipoMaterial(mat.getEstocavel() ? DominioTipoMaterial.E : DominioTipoMaterial.D);
		it.setCustoMedio(item.getCustoMedio());
		it.setQtdPontoPedido(item.getPontoPedido());
		it.setTempoReposicao(item.getTempoReposicao());
		it.setQtdGerada(item.getQtdOriginal());

		DominioInclusaoLoteReposicao indInclusao = item.getIndInclusao();
		Integer index = listaAlteracoes.indexOf(item);
		if (index >= 0) {
			indInclusao = listaAlteracoes.get(index).getIndInclusao();
			item.setQtd(listaAlteracoes.get(index).getQtd());
		}

		if (indInclusao == null) {
			indInclusao = DominioInclusaoLoteReposicao.AT;
		}
		it.setQtdConfirmada(item.getQtd());
		it.setIndInclusao(indInclusao);

		Integer indexDesmarcado = nroDesmarcados.indexOf(item.getMatCodigo());
		if (indexDesmarcado >= 0 || item.getQtd() == 0) {
			it.setIndSelecionado(false);
		} else {
			ScoSolicitacaoDeCompra sc = null;
			if (item.getSlcGerada() != null) {
				// neste caso eu tenho a SC de outro lote
				ScoSolicitacaoDeCompra scRelacionada = this.getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(item.getSlcGerada());

				if (scRelacionada != null) {
					this.transferirScLote(item, scRelacionada);

					// insere a SC no lote que está sendo criado
					it.setIndInclusao(DominioInclusaoLoteReposicao.OL);
					it.setSolicitacao(scRelacionada);
				}
			} else {
				sc = this.criarSolicitacao(ccSolic, (lote.getCentroCustoAplicacao() != null) ? lote.getCentroCustoAplicacao() : ccAplic,
						parametroTipoMovimento, mat, nomeLote, pontoParadaAnterior, pontoParadaAtual, item);
				it.setSolicitacao(sc);
			}

			it.setIndSelecionado(true);
		}

		this.getScoItemLoteReposicaoDAO().persistir(it);
	}

	private ScoMaterialDAO getScoMaterialDAO() {
		return this.scoMaterialDAO;
	}

	private GerarSolicitacaoCompraAlmoxarifadoRN getGerarSolicitacaoCompraAlmoxarifadoRN() {
		return this.gerarSolicitacaoCompraAlmoxarifadoRN;
	}

	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return this.solicitacaoComprasFacade;
	}

	private ScoItemLoteReposicaoDAO getScoItemLoteReposicaoDAO() {
		return this.scoItemLoteReposicaoDAO;
	}

	private ScoLoteReposicaoDAO getScoLoteReposicaoDAO() {
		return this.scoLoteReposicaoDAO;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

	protected SolicitacaoCompraRN getSolicitacaoCompraRN() {
		return this.solicitacaoCompraRN;
	}

	protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return this.scoPontoParadaSolicitacaoDAO;
	}

	protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return this.scoSolicitacoesDeComprasDAO;
	}

	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return this.scoItemPropostaFornecedorDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}