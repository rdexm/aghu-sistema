package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.centrocusto.vo.CentroCustosVO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornecedorPedidoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoJustificativaDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.vo.FiltroProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.QuantidadeProgramadaRecebidaItemVO;
import br.gov.mec.aghu.dominio.DominioTipoSolitacaoAF;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.EstoqueMaterialVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoJustificativa;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ProgramacaoGeralEntregaAFRN extends BaseBusiness {

	private static final String CHECKED = "checked";
	private static final String _HIFEN_ = " - ";
	private static final Log LOG = LogFactory.getLog(ProgramacaoGeralEntregaAFRN.class);
	private static final long serialVersionUID = -7824872305093127024L;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@Inject
	private ScoJustificativaDAO scoJustificativaDAO;

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

	@Inject
	private ScoAutorizacaoFornecedorPedidoDAO scoAutorizacaoFornecedorPedidoDAO;

	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;

	private enum ProgramacaoGeralEntregaAFRNExceptionCode implements BusinessExceptionCode {
		INFORMAR_FILTROS_CPGAF;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public Long countItensProgGeralEntregaAF(FiltroProgrGeralEntregaAFVO filtro) throws ApplicationBusinessException {
		//validarCamposObrigatorios(filtro);
		Long count= 0L;
//		List<ProgrGeralEntregaAFVO> resultadoCompra = null;
		AghParametros fornecedorPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		filtro.setNumeroFornecedorPadrao(fornecedorPadrao.getVlrNumerico().intValue());

		if(DominioTipoSolitacaoAF.M.equals(filtro.getTipoItem())) {
			count =  getScoProgEntregaItemAutorizacaoFornecimentoDAO().listarParcelasAFCompraCount(filtro);
		} else if(DominioTipoSolitacaoAF.S.equals(filtro.getTipoItem())) {
			count =  getScoProgEntregaItemAutorizacaoFornecimentoDAO().listarParcelasAFServicoCount(filtro);
		} else {
			Long count2 = 0L;
			count =  getScoProgEntregaItemAutorizacaoFornecimentoDAO().listarParcelasAFCompraCount(filtro);
			count2 = getScoProgEntregaItemAutorizacaoFornecimentoDAO().listarParcelasAFServicoCount(filtro);
			count = count + count2; 
			
		}
		return count;
	}
	
	public List<ProgrGeralEntregaAFVO> pesquisarItensProgGeralEntregaAF(FiltroProgrGeralEntregaAFVO filtro, Integer firstResult, Integer maxResult) throws ApplicationBusinessException {
		//validarCamposObrigatorios(filtro);
		List<ProgrGeralEntregaAFVO> resultadoCompra = null;
		AghParametros fornecedorPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		filtro.setNumeroFornecedorPadrao(fornecedorPadrao.getVlrNumerico().intValue());

		if (DominioTipoSolitacaoAF.M.equals(filtro.getTipoItem())) {
			resultadoCompra = getScoProgEntregaItemAutorizacaoFornecimentoDAO().listarParcelasAFCompra(filtro,firstResult,maxResult);
		} else if (DominioTipoSolitacaoAF.S.equals(filtro.getTipoItem())) {
			resultadoCompra = getScoProgEntregaItemAutorizacaoFornecimentoDAO().listarParcelasAFServico(filtro,firstResult,maxResult);
		} else {
			resultadoCompra = getScoProgEntregaItemAutorizacaoFornecimentoDAO().listarParcelasAFCompra(filtro,firstResult,maxResult);
			List<ProgrGeralEntregaAFVO> resultadoServico = getScoProgEntregaItemAutorizacaoFornecimentoDAO().listarParcelasAFServico(filtro,firstResult,maxResult);
			// une as listas somente se as duas não forem vazias
			if ((resultadoCompra != null && !resultadoCompra.isEmpty()) && (resultadoServico != null && !resultadoServico.isEmpty())) {
				resultadoCompra.addAll(resultadoServico);
			} else if ((resultadoCompra == null || resultadoCompra.isEmpty()) && (resultadoServico != null && !resultadoServico.isEmpty())) {
				// senão usa a que não é vazia
				resultadoCompra = resultadoServico;
			}
		}

		for (int i = 0; i < resultadoCompra.size(); i++) {
			ProgrGeralEntregaAFVO item = resultadoCompra.get(i);
			item.setSeq(i);
			setDadosEstoque(item, filtro);
			setInfoChecked(item);
			calcularQtdRecebimento(item);
		}
		return resultadoCompra;
	}

	private void setInfoChecked(ProgrGeralEntregaAFVO item) {
		if (item.getIndEmp() == null) {
			item.setEmp(_HIFEN_);
		} else {
			item.setEmpChecked(CHECKED);
		}
		if (BooleanUtils.isFalse(item.getIndCanc())) {
			item.setCanc(_HIFEN_);
		} else {
			item.setCancChecked(CHECKED);
		}
		if (BooleanUtils.isFalse(item.getIndAss())) {
			item.setAss(_HIFEN_);
		} else {
			item.setAssChecked(CHECKED);
		}
		if (BooleanUtils.isFalse(item.getIndEntregaImediata())) {
			item.setEntImed(_HIFEN_);
		} else {
			item.setEntChecked(CHECKED);
		}
		if (BooleanUtils.isFalse(item.getIndEnvioForn())) {
			item.setEnvForn(_HIFEN_);
		} else {
			item.setEnvFornChecked(CHECKED);
		}
		if (BooleanUtils.isFalse(item.getIndPlan())) {
			item.setPlan(_HIFEN_);
		} else {
			item.setPlanChecked(CHECKED);
		}
		if (BooleanUtils.isFalse(item.getIndRecalculoAutomatico())) {
			item.setRecAut(_HIFEN_);
		} else {
			item.setRecAutCkeched(CHECKED);
		}
		if (BooleanUtils.isFalse(item.getIndRecalculoManual())) {
			item.setRecManual(_HIFEN_);
		} else {
			item.setRecManualChecked(CHECKED);
		}
		if (BooleanUtils.isFalse(item.getIndTramiteInterno())) {
			item.setTramInt(_HIFEN_);
		} else {
			item.setTramIntChecked(CHECKED);
		}
	}

	// Pra cada item aplica rn abaixo, após da C3
	// espacoDisponivel := Sce_Estq_Almoxs.qtde_estq_max - (sce_estq_almoxs.qtde_disponivel + sce_estq_almoxs.qtde_bloqueada)
	private void setDadosEstoque(ProgrGeralEntregaAFVO item, FiltroProgrGeralEntregaAFVO filtro) {
		EstoqueMaterialVO estoqueMaterialVO = getEstoqueFacade().obterEstoqueMaterial(item.getMatCodigo(), filtro.getDataCompetencia(),
				filtro.getNumeroFornecedorPadrao());
		if (estoqueMaterialVO != null) {
			Integer estoqueDisponivel = calcularEspacoDisponivel(estoqueMaterialVO);
			item.setEspacoDisponivel(estoqueDisponivel);
			item.setDominioABC(estoqueMaterialVO.getClassificacaoAbc());
			item.setDescAlm(estoqueMaterialVO.getAlmoxarifadoDescricao());
			item.setAlm(estoqueMaterialVO.getAlmoxarifadoSeq());
			item.setSubClassificacaoAbc(estoqueMaterialVO.getSubClassificacaoAbc());
		}
	}

	private Integer calcularEspacoDisponivel(EstoqueMaterialVO estoqueMaterial) {
		if (estoqueMaterial.getQuantidadeMaxima() == null && estoqueMaterial.getQuantidadeDisponivel() == null
				&& estoqueMaterial.getQuantidadeBloqueada() == null) {
			return 0;
		}
		return estoqueMaterial.getQuantidadeMaxima()
				- (estoqueMaterial.getQuantidadeDisponivel() + estoqueMaterial.getQuantidadeBloqueada());
	}

	public void pesquisarInfoComplementares(ProgrGeralEntregaAFVO item) {
		//C4
		Date data = getScoAutorizacaoFornecedorPedidoDAO().buscarDataMaisAntigaEnvioFornecedor(item);
		item.setEnvioAFP(data);
		// C5
		ScoItemAutorizacaoForn dadosC5 = getScoItemAutorizacaoFornDAO().obterItemAutorizacaoFornPorId(item.getNroAF(), item.getItemAF());
		if (dadosC5 != null) {
			item.setQtdRecAF(dadosC5.getQtdeRecebida());
			item.setQtdItemAF(dadosC5.getQtdeSolicitada());
		}
		// C6
		QuantidadeProgramadaRecebidaItemVO dadosC6 = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.buscarQuantidadeProgramadaRecebidaItem(item.getNroAF(), item.getItemAF());
		if (dadosC6 != null) {
			item.setQtdTotalProg(dadosC6.getQuantidadeTotal());
			item.setTotalEntregue(dadosC6.getQuantidadeEntregueTotal());
		}
		// C7
		ScoProgEntregaItemAutorizacaoFornecimento dadosC7 = getScoProgEntregaItemAutorizacaoFornecimentoDAO()
				.obterProgramacaoEntregaItemAFPorItemAfNumeroParcela(item.getIafAfnNumero(), item.getIafNumero(), item.getParcela());
		if (dadosC7 != null) {
			item.setValorEfetivado(dadosC7.getValorEfetivado());
			item.setAssinatura(dadosC7.getDtAssinatura());
			item.setQtdEntParc(dadosC7.getQtdeEntregue());

		}
		// C8
		CentroCustosVO dadosC8 = null;
		//indica que o item não é de compra, pois a unidade de medida material é "buscada" da base somente para busca
		//de compra
		if (StringUtils.isBlank(item.getUmdMat())) {
			dadosC8 = getCentroCustoFacade().obterCentroCustoParaSolicitacaoCompraOuServico(item.getNroSol(), false);
			item.setSolicitaoDesc("Solicitação de Serviço");
			if (dadosC8 != null) {
				if (dadosC8.getFcc1() != null) {
					item.setDescCCSol(dadosC8.getFcc1().getDescricao());
				}
				if (dadosC8.getFcc2() != null) {
					item.setDescCCApp(dadosC8.getFcc2().getDescricao());
				}
			}
		} else {
			dadosC8 = getCentroCustoFacade().obterCentroCustoParaSolicitacaoCompraOuServico(item.getNroSol(), true);
			item.setSolicitaoDesc("Solicitação de Compra");
			if (dadosC8 != null) {
				if (dadosC8.getFcc1() != null) {
					item.setDescCCSol(dadosC8.getFcc1().getDescricao());
				}
				if (dadosC8.getFcc2() != null) {
					item.setDescCCApp(dadosC8.getFcc2().getDescricao());
				}
			}
		}

		ScoJustificativa dadosC9 = getScoJustificativaDAO().obterJustificativaEmpenhoPorParcela(item.getIafAfnNumero(),
				item.getIafNumero(), item.getParcela(), item.getProgEntregaItemAFSeq());
		if (dadosC9 != null) {
			item.setJustEmpenho(dadosC9.getDescricao());
		}
	}

	//RN2
	public void validarCamposObrigatorios(FiltroProgrGeralEntregaAFVO filtro) throws ApplicationBusinessException {
		if (filtro == null
				|| (filtro.getNroAF() == null && filtro.getCp() == null && filtro.getCentroCustoSol() == null
						&& filtro.getCentroCustoApp() == null && filtro.getFornecedor() == null && filtro.getModalidade() == null
						&& filtro.getModalidadeEmpenho() == null && filtro.getServico() == null && filtro.getMaterial() == null
						&& filtro.getGrupoMaterial() == null && filtro.getGrupoServico() == null && filtro.getNroSolicitacao() == null)) {
			throw new ApplicationBusinessException(ProgramacaoGeralEntregaAFRNExceptionCode.INFORMAR_FILTROS_CPGAF);
		}
	}

	private void calcularQtdRecebimento(ProgrGeralEntregaAFVO vo) {
		Double qtdReceber = null;
		if (vo.getQtdParcela() != null && vo.getFatorConv() != null) {
			qtdReceber = (double) (vo.getQtdParcela() * vo.getFatorConv());
		} else if (vo.getQtdParcela() != null && (vo.getFatorConv() == null || vo.getFatorConv() == 0)) {
			vo.setFatorConv(1);
			qtdReceber = (double) (vo.getQtdParcela() * vo.getFatorConv());
		}
		vo.setQtdReceber(qtdReceber);
	}

	
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return this.scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	private  ScoAutorizacaoFornecedorPedidoDAO getScoAutorizacaoFornecedorPedidoDAO(){
		return this.scoAutorizacaoFornecedorPedidoDAO;
		}

	private ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return this.scoItemAutorizacaoFornDAO;
	}

	private ScoJustificativaDAO getScoJustificativaDAO() {
		return this.scoJustificativaDAO;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	public IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

	public ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

}
