package br.gov.mec.aghu.compras.autfornecimento.business;

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
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoMarcaComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoMarcaModeloDAO;
import br.gov.mec.aghu.compras.dao.ScoNomeComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.dao.ScoUnidadeMedidaDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.GeracaoAfVO;
import br.gov.mec.aghu.compras.pac.vo.ItemPropostaAFVO;
import br.gov.mec.aghu.compras.pac.vo.ValidaGeracaoAFVO;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.orcamento.business.IOrcamentoFacade;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * package br.gov.mec.aghu.compras.autfornecimento.business;
 * 
 * 
 * @author danilosantos
 * 
 */
@Stateless
public class AutFornecimentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AutFornecimentoRN.class);
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private ScoNomeComercialDAO scoNomeComercialDAO;
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	@EJB
	private IPacFacade pacFacade;
	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;
	@EJB
	private IComprasFacade comprasFacade;
	@Inject
	private ScoUnidadeMedidaDAO scoUnidadeMedidaDAO;
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	@Inject
	private ScoAutorizacaoFornJnDAO scoAutorizacaoFornJnDAO;
	@Inject
	private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	@EJB
	private ScoProgEntregaItemAutorizacaoFornecimentoRN scoProgEntregaItemAutorizacaoFornecimentoRN;
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	@Inject
	private ScoMarcaComercialDAO scoMarcaComercialDAO;
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;	
	@Inject
	private ScoMarcaModeloDAO scoMarcaModeloDAO;
	@Inject
	private ScoItemAutorizacaoFornJnDAO scoItemAutorizacaoFornJnDAO;
	@EJB
	private IEstoqueFacade estoqueFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IOrcamentoFacade orcamentoFacade;
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	@EJB
	private ScoItemAutorizacaoFornJnRN scoItemAutorizacaoFornJnRN;
	@EJB
	private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;
	@EJB
	private ScoAutorizacaoFornRN scoAutorizacaoFornRN;
	@EJB
	private ScoAutorizacaoFornJnRN scoAutorizacaoFornJnRN;
	@EJB
	private ScoSolicitacaoProgramacaoEntregaRN scoSolicitacaoProgramacaoEntregaRN;	
	/**
	 * 
	 */
	private static final long serialVersionUID = -464829745971165825L;

	public enum AutFornecimentoRNExceptionCode implements BusinessExceptionCode {
		GERAR_AF_PAC_05, GERAR_AF_PAC_07, ERRO_CLONE_FASE_SOLICITACAO, NENHUMA_AF_GERADA, ERRO_INSERCAO_AUT_FORNECIMENTO,
		ERRO_INSERCAO_PROG_ENTREGA_ITEM_AF, ERRO_GERACAO_AF_REGRAS_ORCAMENTARIAS;
	}
	
		/**
	 * SCOC_GERA_AF
	 * 
	 * @param numPac
	 * @throws BaseException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	//RN4 e RN5 foram temporariamente comentadas conforme melhoria https://apus.hcpa.ufrgs.br/issues/27518
	public void gerarAutorizacaoFornecimento(Integer numPac, DominioModalidadeEmpenho modalidadeEmpenhoSelecionada) throws BaseException {

		GeracaoAfVO geracaoAfVO = new GeracaoAfVO();
		ValidaGeracaoAFVO validaGeracaoAFVO = new ValidaGeracaoAFVO();
		// RN4
		// Consulta5
		/*if (this.getScoListasSiafiFonteRecDAO()
				.pesquisarListasSiafiFonteRecAtivos() != null) {
			List<ScoListasSiafiFonteRec> lista = this
					.getScoListasSiafiFonteRecDAO()
					.pesquisarListasSiafiFonteRecAtivos();
			ScoListasSiafiFonteRec listaSiafiFonteRec = lista.get(0);
			geracaoAfVO.setSeqLista(listaSiafiFonteRec.getSeqLista());
		} else {
			throw new ApplicationBusinessException(
					AutFornecimentoRNExceptionCode.GERAR_AF_PAC_03);
		}*/
		// RN5
		// Consulta6
		/*if (this.getFcpCompetenciaSistemaSiafiDAO()
				.pesquisarCompetenciaSistemaSiafiNaoEncerrado() != null) {
			List<FcpCompetenciaSistemaSiafi> lista = this
					.getFcpCompetenciaSistemaSiafiDAO()
					.pesquisarCompetenciaSistemaSiafiNaoEncerrado();
			FcpCompetenciaSistemaSiafi fcpCompetenciaSistemaSiafi = lista
					.get(0);
			geracaoAfVO.setDataCompetencia(fcpCompetenciaSistemaSiafi
					.getDtCompetencia());
		} else {
			throw new ApplicationBusinessException(
					AutFornecimentoRNExceptionCode.GERAR_AF_PAC_04);
		}*/
		// Consulta04
		List<ItemPropostaAFVO> lista = this.scoPropostaFornecedorDAO
				.pesquisarItensComProposta(numPac);
		if (lista != null && lista.size() > 0) {
			geracaoAfVO.setAfnNumero(0);
			
			//Consulta 11 foi movida para o início do processo pois pode ser executada apenas uma vez
			Short nroComplemento = scoAutorizacaoFornDAO.obterMaxNroComplemento(numPac);
			if(nroComplemento == null){
				nroComplemento = 1;
			} else {
				nroComplemento++;
			}
			validaGeracaoAFVO.setNroComplemento(nroComplemento.intValue());
			validaGeracaoAFVO.setQtdAfsGerada(0);
			for (ItemPropostaAFVO item : lista) {
				Boolean calculaQuantidade = this.validarCalculoQuantidade(item);
				Boolean calculaValorUnitario = this
						.validarCalculoValorUnitario(item);
				// RN3
				if (this.verificarValidade(item)) {
					continue;
				}
				if (item.getTipo().equals(DominioTipoFaseSolicitacao.C)
						&& item.getItemPropostaFornecedor() != null
						&& !item.getItemPropostaFornecedor().getValorUnitario()
								.equals(item.getValorUnitarioPrevisto())) {
					// RN6
					List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifado = estoqueFacade.pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoFornecedorMaterial(null, item.getMatCodigo(),Integer.valueOf("1"));
					SceEstoqueAlmoxarifado estoqueAlmoxarifado = listaEstoqueAlmoxarifado
							.get(0);
					ScoUnidadeMedida unidadeMedida = estoqueAlmoxarifado
							.getUnidadeMedida();
					if (unidadeMedida != null) {
						geracaoAfVO.setUmdCodigoEstoque(unidadeMedida
								.getCodigo());
					}
					// RN7
					if (calculaQuantidade) {
						geracaoAfVO.setQuantidade(item
								.getItemPropostaFornecedor().getQuantidade()
								* item.getItemPropostaFornecedor()
										.getFatorConversao());
					}
					if (calculaValorUnitario) {
						geracaoAfVO.setValorUnitario(item
								.getItemPropostaFornecedor().getValorUnitario()
								.doubleValue()
								/ item.getItemPropostaFornecedor()
										.getFatorConversao());
					}

					if (geracaoAfVO.getValorUnitario() != null) {
						DecimalFormat df = new DecimalFormat("#.0000");
						String valor = df.format(geracaoAfVO.getValorUnitario());
						valor = valor.replace(",", ".");
						geracaoAfVO.setValorUnitario(Double.valueOf(valor));
						if (geracaoAfVO.getValorUnitario().equals(
								Double.valueOf("0"))) {
							throw new ApplicationBusinessException(
									AutFornecimentoRNExceptionCode.GERAR_AF_PAC_07, item.getItemPropostaFornecedor().getItemLicitacao().getNumeroItemLicitacao(), 
										item.getItemPropostaFornecedor().getValorUnitario(), item .getItemPropostaFornecedor().getFatorConversao(), 
										geracaoAfVO.getValorUnitario());
						} 
						geracaoAfVO.setUmdCodigo(geracaoAfVO
								.getUmdCodigoEstoque());
						geracaoAfVO.setFatorConversao(1);
					}

				} else {
					geracaoAfVO.setUmdCodigoEstoque(item.getItemPropostaFornecedor().getUnidadeMedida().getCodigo());
					geracaoAfVO.setValorUnitario(item.getItemPropostaFornecedor().getValorUnitario().doubleValue());
					geracaoAfVO.setQuantidade(item.getItemPropostaFornecedor().getQuantidade());
				}
				validaGeracaoAFVO = this.validarGeracaoAf(numPac, item, geracaoAfVO, modalidadeEmpenhoSelecionada, validaGeracaoAFVO);
				// RN8

			}
			if (validaGeracaoAFVO.getQtdAfsGerada() > 0) {
				// RN10
				geracaoAfVO.setLicitacao(this.pacFacade.obterLicitacao(
						numPac));
				ScoLicitacao licitacaoClone = this.pacFacade
						.obterLicitacao(numPac);
				geracaoAfVO.getLicitacao().setSituacao(
						DominioSituacaoLicitacao.AF);
				this.pacFacade.alterarLicitacao(geracaoAfVO.getLicitacao(), licitacaoClone);
			} else {
				throw new ApplicationBusinessException(
						AutFornecimentoRNExceptionCode.NENHUMA_AF_GERADA);
			}
		} else {
			throw new ApplicationBusinessException(
					AutFornecimentoRNExceptionCode.GERAR_AF_PAC_05);
		}
	}

	private Boolean validarCalculoQuantidade(ItemPropostaAFVO item) {
		if (item.getItemPropostaFornecedor().getQuantidade() != null
				&& item.getItemPropostaFornecedor().getFatorConversao() != null) {
			return true;
		} else {
			return false;
		}
	}

	private Boolean validarCalculoValorUnitario(ItemPropostaAFVO item) {
		if (item.getItemPropostaFornecedor().getValorUnitario() != null
				&& item.getItemPropostaFornecedor().getFatorConversao() != null) {
			return true;
		} else {
			return false;
		}
	}

	public ValidaGeracaoAFVO validarGeracaoAf(Integer numPac, ItemPropostaAFVO item,
			GeracaoAfVO geracaoAfVO, DominioModalidadeEmpenho modalidadeEmpenhoSelecionada, ValidaGeracaoAFVO validaGeracaoAFVO)
			throws BaseException {
		Boolean validaGeracaoAf = Boolean.FALSE;
		Integer lctNumero = null;
		Integer frnNumero = null;
		Integer vbgSeq = null;
		Integer ntdGndCodigo = null;
		Byte ntdCodigo = null;
		Integer cdpNumero = null;
		
		if (item.getItemPropostaFornecedor() != null
				&& item.getItemPropostaFornecedor().getId() != null) {
			lctNumero = item.getItemPropostaFornecedor().getId()
					.getPfrLctNumero();
			frnNumero = item.getItemPropostaFornecedor().getId()
					.getPfrFrnNumero();
		}
		if (item.getTipo().equals(DominioTipoFaseSolicitacao.C)) {
			vbgSeq = item.getSlcVbgSeq();
			ntdGndCodigo = item.getSlcNtdGndCodigo();
			ntdCodigo = item.getSlcNtdCodigo().byteValue();
		}
		if (item.getTipo().equals(DominioTipoFaseSolicitacao.S)) {
			vbgSeq = item.getSlsVbgSeq();
			ntdGndCodigo = item.getSlsNtdGndCodigo();
			ntdCodigo = item.getSlsNtdCodigo().byteValue();
		}
		if (item.getItemPropostaFornecedor() != null
				&& item.getItemPropostaFornecedor()
						.getCondicaoPagamentoPropos() != null) {
			cdpNumero = item.getItemPropostaFornecedor()
					.getCondicaoPagamentoPropos().getNumero();
		}
		// consulta7
		List<ScoAutorizacaoForn> listaAuForns = this.scoAutorizacaoFornDAO
				.pesquisarAfPorItemProposta(lctNumero, frnNumero, vbgSeq,
						ntdGndCodigo, ntdCodigo, cdpNumero);
		if (listaAuForns != null && listaAuForns.size() > 0) {
			ScoAutorizacaoForn autorizacaoForn = listaAuForns.get(0);
			
			
			// consulta 8
			List<ScoAutorizacaoFornJn> listaAFJNs =  scoAutorizacaoFornJnDAO.pesquisarScoAutorizacaoFornPorNroAF(autorizacaoForn.getNumero());
			if (listaAFJNs.size()==0) {
				geracaoAfVO.setAfnNumero(autorizacaoForn.getNumero());
				validaGeracaoAFVO.setvIafNumero(this.gerarItemAutorizacaoFornecimento(item,
						geracaoAfVO.getAfnNumero(), geracaoAfVO, validaGeracaoAFVO).getvIafNumero());
				validaGeracaoAFVO.setUltimaAutorizacaoFornecimentoGerada(autorizacaoForn);
			} else {
				ScoAutorizacaoForn autorizacaoFornecimento = this
						.popularAutorizacaoFornecimento(item,
								geracaoAfVO);
				validaGeracaoAf = this.validarGeracaoAf(validaGeracaoAFVO.getUltimaAutorizacaoFornecimentoGerada(), autorizacaoFornecimento);
				if(validaGeracaoAf){
					validaGeracaoAFVO.setUltimaAutorizacaoFornecimentoGerada(this
							.inserirAutorizacaoFornecimento(item,
									geracaoAfVO, modalidadeEmpenhoSelecionada, autorizacaoFornecimento, validaGeracaoAFVO.getNroComplemento().shortValue()));
					validaGeracaoAFVO.setQtdAfsGerada(validaGeracaoAFVO.getQtdAfsGerada()+1);
					validaGeracaoAFVO.setNroComplemento(validaGeracaoAFVO.getNroComplemento().intValue()+1);
					scoAutorizacaoFornJnRN.inserirAutorizacaoFornecimentoJn(validaGeracaoAFVO.getUltimaAutorizacaoFornecimentoGerada(), true);	
				}
				if(validaGeracaoAFVO.getUltimaAutorizacaoFornecimentoGerada()!=null){
					validaGeracaoAFVO.setvIafNumero(this.gerarItemAutorizacaoFornecimento(item,
							validaGeracaoAFVO.getUltimaAutorizacaoFornecimentoGerada().getNumero(),
							geracaoAfVO, validaGeracaoAFVO).getvIafNumero());	
				} else {
					validaGeracaoAFVO.setvIafNumero(this.gerarItemAutorizacaoFornecimento(item,
							null,
							geracaoAfVO, validaGeracaoAFVO).getvIafNumero());
				}
				
			}

		} else {
			ScoAutorizacaoForn autorizacaoFornecimento = this
					.popularAutorizacaoFornecimento(item,
							geracaoAfVO);
			validaGeracaoAf = this.validarGeracaoAf(validaGeracaoAFVO.getUltimaAutorizacaoFornecimentoGerada(), autorizacaoFornecimento);
			if(validaGeracaoAf){
				validaGeracaoAFVO.setUltimaAutorizacaoFornecimentoGerada(this
						.inserirAutorizacaoFornecimento(item,
								geracaoAfVO, modalidadeEmpenhoSelecionada, autorizacaoFornecimento, validaGeracaoAFVO.getNroComplemento().shortValue()));
				
				validaGeracaoAFVO.setQtdAfsGerada(validaGeracaoAFVO.getQtdAfsGerada() + 1);
				validaGeracaoAFVO.setNroComplemento(validaGeracaoAFVO.getNroComplemento()+1);
				scoAutorizacaoFornJnRN.inserirAutorizacaoFornecimentoJn(validaGeracaoAFVO.getUltimaAutorizacaoFornecimentoGerada(), true);	
			}
			if(validaGeracaoAFVO.getUltimaAutorizacaoFornecimentoGerada()!=null){
				validaGeracaoAFVO.setvIafNumero(this.gerarItemAutorizacaoFornecimento(item,
						validaGeracaoAFVO.getUltimaAutorizacaoFornecimentoGerada().getNumero(),
						geracaoAfVO, validaGeracaoAFVO).getvIafNumero());	
			} else{
				validaGeracaoAFVO.setvIafNumero(this.gerarItemAutorizacaoFornecimento(item,
						null, geracaoAfVO, validaGeracaoAFVO).getvIafNumero());
			}
			
			
		}
		return validaGeracaoAFVO;
	}
	
	public Boolean validarGeracaoAf(ScoAutorizacaoForn ultimaAfGerada, ScoAutorizacaoForn autorizacaoFornecimento){
		Boolean retorno = Boolean.TRUE;
		if(ultimaAfGerada!=null){
			if(ultimaAfGerada.getPropostaFornecedor()!=null&&ultimaAfGerada.getPropostaFornecedor().getId()!=null&&autorizacaoFornecimento.getPropostaFornecedor()!=null&&autorizacaoFornecimento.getPropostaFornecedor().getId()!=null){
				if(ultimaAfGerada.getPropostaFornecedor().getId().getFrnNumero().equals(autorizacaoFornecimento.getPropostaFornecedor().getId().getFrnNumero())){
					retorno = Boolean.FALSE;
				} else {
					return Boolean.TRUE;
				}
			}
			if(ultimaAfGerada.getVerbaGestao()!=null&&autorizacaoFornecimento.getVerbaGestao()!=null){
				if(ultimaAfGerada.getVerbaGestao().getSeq().equals(autorizacaoFornecimento.getVerbaGestao().getSeq())){
					retorno = Boolean.FALSE;
				} else {
					return Boolean.TRUE;
				}
			} 
			if(ultimaAfGerada.getNaturezaDespesa()!=null && autorizacaoFornecimento.getNaturezaDespesa()!=null){
				if(ultimaAfGerada.getNaturezaDespesa().getId().getCodigo().equals(autorizacaoFornecimento.getNaturezaDespesa().getId().getCodigo())){
					retorno = Boolean.FALSE;
				} else {
					return Boolean.TRUE;
				}
				if(ultimaAfGerada.getNaturezaDespesa().getId().getGndCodigo().equals(autorizacaoFornecimento.getNaturezaDespesa().getId().getGndCodigo())){
					retorno = Boolean.FALSE;
				} else {
					return Boolean.TRUE;
				}
			} 
			if(ultimaAfGerada.getCondicaoPagamentoPropos()!=null && autorizacaoFornecimento.getCondicaoPagamentoPropos()!=null){
				if(ultimaAfGerada.getCondicaoPagamentoPropos().getNumero().equals(autorizacaoFornecimento.getCondicaoPagamentoPropos().getNumero())){
					retorno = Boolean.FALSE;
				} else {
					return Boolean.TRUE;
				}
			}
		}
		
		return retorno;
	}

	// RN9
	public ValidaGeracaoAFVO gerarItemAutorizacaoFornecimento(ItemPropostaAFVO item,
			Integer numero,
			GeracaoAfVO geracaoAfVO, ValidaGeracaoAFVO validaGeracaoAFVO) throws BaseException {
		Short ipfNumero = null;
		Integer prfLctNumero = null;
		Integer prfFrnNumero = null;

		ipfNumero = item.getItemPropostaFornecedor().getId().getNumero();
		prfLctNumero = item.getItemPropostaFornecedor().getId()
				.getPfrLctNumero();
		prfFrnNumero = item.getItemPropostaFornecedor().getId()
				.getPfrFrnNumero();
		// Consulta09
		ScoItemAutorizacaoForn itemAutorizacaoForn = this
				.scoItemAutorizacaoFornDAO.obterItemAFComFases(numero,
						ipfNumero, prfLctNumero, prfFrnNumero);
		if (itemAutorizacaoForn == null) {
			Integer vIafNumeroAux = this.comprasFacade.obterMaxItemAF(numero);
			if(validaGeracaoAFVO.getUltimoItemAutorizacaoFornecimento()==null){
				if(vIafNumeroAux==null){
					vIafNumeroAux = 0;
				}
				vIafNumeroAux++;
				validaGeracaoAFVO.setvIafNumero(vIafNumeroAux);
			}
			if(validaGeracaoAFVO.getUltimoItemAutorizacaoFornecimento()!=null && !validaGeracaoAFVO.getUltimoItemAutorizacaoFornecimento().getId().getAfnNumero().equals(numero)){
				if (vIafNumeroAux == null) {
					validaGeracaoAFVO.setvIafNumero(1);
				} else {
					vIafNumeroAux++;
					validaGeracaoAFVO.setvIafNumero(vIafNumeroAux);
				}
			} 
			
			geracaoAfVO.setIafNumero(validaGeracaoAFVO.getvIafNumero());
			
			ScoItemAutorizacaoForn itemAutorizacaoFornecimento = this.inserirItemAutorizacaoFornecimento(numero, item,
					geracaoAfVO);
			validaGeracaoAFVO.setUltimoItemAutorizacaoFornecimento(itemAutorizacaoFornecimento);
			ScoAutorizacaoForn autorizacaoForn = this.scoAutorizacaoFornDAO.obterPorChavePrimaria(itemAutorizacaoFornecimento.getId().getAfnNumero());
			itemAutorizacaoFornecimento.setAutorizacoesForn(autorizacaoForn);
			
			this.inserirItemAutorizacaoFornecimentoJn(numero, item,	geracaoAfVO);
			this.inserirScoFasesSolicitacao(itemAutorizacaoFornecimento, item);
			ScoItemLicitacao itemLicitacao = this.comprasFacade.obterItemLicitacaoPorNumeroLicitacaoENumeroItem(
										item.getItemPropostaFornecedor().getId().getPfrLctNumero(), item.getItemPropostaFornecedor().getItemLicitacao().getId().getNumero());
			itemLicitacao.setEmAf(Boolean.TRUE);
			this.pacFacade.atualizarItemLicitacao(itemLicitacao);
			
			this.gerarParcelas(itemLicitacao, itemAutorizacaoFornecimento, validaGeracaoAFVO.getvIafNumero(), item.getSlcNumero(), item.getSlsNumero());
			validaGeracaoAFVO.setvIafNumero(validaGeracaoAFVO.getvIafNumero()+1);
			return validaGeracaoAFVO;
		} else {
			geracaoAfVO.setIafNumero(itemAutorizacaoForn.getId().getNumero());
			if (itemAutorizacaoForn.getIndSituacao().equals(
					DominioSituacaoAutorizacaoFornecedor.EX)
					|| itemAutorizacaoForn.getIndEstorno().equals(Boolean.TRUE)) {
				ScoItemAutorizacaoForn itemAutorizacao = scoItemAutorizacaoFornDAO.obterItemAF(numero,	ipfNumero, prfLctNumero, prfFrnNumero);
				itemAutorizacao.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.AE);
				itemAutorizacao.setIndEstorno(Boolean.FALSE);
				itemAutorizacao.setDtEstorno(null);
				itemAutorizacao.setServidorEstorno(null);
				if (item.getSlsNumero() == null) { // Material
					itemAutorizacao.setQtdeSolicitada(geracaoAfVO.getQuantidade().intValue() * item.getFrequenciaEntrega());
					itemAutorizacao.setValorUnitario(geracaoAfVO.getValorUnitario());
				} else {
					itemAutorizacao.setQtdeSolicitada(1);
					itemAutorizacao.setValorUnitario(geracaoAfVO.getValorUnitario() * item.getFrequenciaEntrega());
				}
				itemAutorizacao.setQtdeRecebida(0);
				//Fator de conversao do item da AF sempre sera 1 na geracao
				itemAutorizacao.setFatorConversao(1);
				ScoUnidadeMedida unidadeMedidaForn = this.scoUnidadeMedidaDAO.obterPorChavePrimaria(item.getItemPropostaFornecedor().getUnidadeMedida().getCodigo());
				itemAutorizacao.setUmdCodigoForn(unidadeMedidaForn);
				itemAutorizacao.setFatorConversaoForn(item
						.getItemPropostaFornecedor().getFatorConversao());

				itemAutorizacao.setValorEfetivado(Double.valueOf("0"));
				this.scoItemAutorizacaoFornRN.atualizarItemAutorizacaoFornecimento(itemAutorizacao);
				ScoItemAutorizacaoFornJn scoItemAutorizacaoFornJn = this.scoItemAutorizacaoFornJnDAO.obterItemAfJnPorSequenciaAlteracao(itemAutorizacao.getId().getAfnNumero(), itemAutorizacao.getId().getNumero(), itemAutorizacao.getSequenciaAlteracao());
				if(scoItemAutorizacaoFornJn != null){
					scoItemAutorizacaoFornJn.setQtdeSolicitada(itemAutorizacao.getQtdeSolicitada());
				   scoItemAutorizacaoFornJnRN.persistirItemAutorizacaoFornecimentoJn(scoItemAutorizacaoFornJn);
				}
				List<ScoFaseSolicitacao> listaFaseSolicitacao = this
						.comprasFacade
						.pesquisarFaseSolicitacaoPorItemAutorizacao(numero,
								geracaoAfVO.getIafNumero());
				for (ScoFaseSolicitacao faseSolicitacao : listaFaseSolicitacao) {
					ScoFaseSolicitacao faseSolicitacaoOld = null;
					try {
						faseSolicitacaoOld = (ScoFaseSolicitacao) BeanUtils
								.cloneBean(faseSolicitacao);
					} catch (Exception e) {
						throw new ApplicationBusinessException(
								AutFornecimentoRNExceptionCode.ERRO_CLONE_FASE_SOLICITACAO);
					}
					faseSolicitacao.setExclusao(Boolean.FALSE);
					faseSolicitacao.setDtExclusao(null);
					this.comprasFacade
							.atualizarScoFaseSolicitacao(faseSolicitacao,
									faseSolicitacaoOld);
				}
			}
			return validaGeracaoAFVO;
		}
	}

	public void gerarParcelas(ScoItemLicitacao itemLicitacao, ScoItemAutorizacaoForn itemAutorizacaoFornecimento, Integer vIafNumero, Integer slcNumero, Integer slsNumero) throws BaseException{
		if(itemLicitacao.getLicitacao()!=null && itemLicitacao.getLicitacao().getModalidadeEmpenho().equals(DominioModalidadeEmpenho.ORDINARIO)){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutorizacaoFornecimento = new ScoProgEntregaItemAutorizacaoFornecimento();
			ScoProgEntregaItemAutorizacaoFornecimentoId id = new ScoProgEntregaItemAutorizacaoFornecimentoId();
			id.setIafAfnNumero(itemAutorizacaoFornecimento.getId().getAfnNumero());
			id.setIafNumero(vIafNumero);
			id.setSeq(Integer.valueOf(1));
			id.setParcela(Integer.valueOf(1));
			progEntregaItemAutorizacaoFornecimento.setId(id);
			progEntregaItemAutorizacaoFornecimento.setDtGeracao(new Date());
			progEntregaItemAutorizacaoFornecimento.setDtPrevEntrega(itemAutorizacaoFornecimento.getAutorizacoesForn().getDtPrevEntrega());
			progEntregaItemAutorizacaoFornecimento.setQtde(itemAutorizacaoFornecimento.getQtdeSolicitada());
			progEntregaItemAutorizacaoFornecimento.setRapServidor(servidorLogado);
			progEntregaItemAutorizacaoFornecimento.setIndPlanejamento(Boolean.FALSE);
			progEntregaItemAutorizacaoFornecimento.setIndAssinatura(Boolean.FALSE);
			progEntregaItemAutorizacaoFornecimento.setIndEmpenhada(DominioAfEmpenhada.N);
			progEntregaItemAutorizacaoFornecimento.setIndEnvioFornecedor(Boolean.FALSE);
			progEntregaItemAutorizacaoFornecimento.setIndRecalculoAutomatico(Boolean.FALSE);
			progEntregaItemAutorizacaoFornecimento.setIndRecalculoManual(Boolean.TRUE);
			Double valorTotal = itemAutorizacaoFornecimento.getQtdeSolicitada() * itemAutorizacaoFornecimento.getValorUnitario();
			progEntregaItemAutorizacaoFornecimento.setValorTotal(valorTotal);
			progEntregaItemAutorizacaoFornecimento.setIndImpressa(Boolean.FALSE);
			progEntregaItemAutorizacaoFornecimento.setIndCancelada(Boolean.FALSE);
			progEntregaItemAutorizacaoFornecimento.setDtNecessidadeHcpa(itemAutorizacaoFornecimento.getAutorizacoesForn().getDtPrevEntrega());
			progEntregaItemAutorizacaoFornecimento.setIndEfetivada(Boolean.FALSE);
			progEntregaItemAutorizacaoFornecimento.setIndEntregaImediata(Boolean.FALSE);
			progEntregaItemAutorizacaoFornecimento.setIndTramiteInterno(Boolean.FALSE);
			scoProgEntregaItemAutorizacaoFornecimentoRN.persistir(progEntregaItemAutorizacaoFornecimento);
			ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega = new ScoSolicitacaoProgramacaoEntrega();
			solicitacaoProgramacaoEntrega.setProgEntregaItemAf(progEntregaItemAutorizacaoFornecimento);
			if (slcNumero != null) {
				ScoSolicitacaoDeCompra solicitacaoCompra = this.scoSolicitacoesDeComprasDAO.obterPorChavePrimaria(slcNumero);
				solicitacaoProgramacaoEntrega.setSolicitacaoCompra(solicitacaoCompra);
			}
			if (slsNumero != null) {
				ScoSolicitacaoServico solicitacaoServico = this.scoSolicitacaoServicoDAO.obterPorChavePrimaria(slsNumero);
				solicitacaoProgramacaoEntrega.setSolicitacaoServico(solicitacaoServico);
			}
			solicitacaoProgramacaoEntrega.setQtde(itemAutorizacaoFornecimento.getQtdeSolicitada());
			solicitacaoProgramacaoEntrega.setValor(itemAutorizacaoFornecimento.getQtdeSolicitada()*itemAutorizacaoFornecimento.getValorUnitario());
			solicitacaoProgramacaoEntrega.setIndPrioridade(Short.valueOf("1"));
			solicitacaoProgramacaoEntrega.setQtdeEntregue(0);
			solicitacaoProgramacaoEntrega.setValorEfetivado(Double.valueOf(0));
			solicitacaoProgramacaoEntrega.setItemAfOrigem(null);
			scoSolicitacaoProgramacaoEntregaRN.persistir(solicitacaoProgramacaoEntrega);
		}
	}
	// RN3
	// VERIFICA_VALIDADE_CAMPOS
	public Boolean verificarValidade(ItemPropostaAFVO item) throws BaseException {
		if (item.getItemPropostaFornecedor().getIndEscolhido() == Boolean.FALSE
				|| item.getItemPropostaFornecedor().getValorUnitario() == null
				|| item.getItemPropostaFornecedor()
						.getCondicaoPagamentoPropos() == null) {
			return true;
		}
		if ((item.getTipo().equals(DominioTipoFaseSolicitacao.C) && item
				.getSlcVbgSeq() == null)
				|| (item.getTipo().equals(DominioTipoFaseSolicitacao.C) && item.getSlcNtdGndCodigo() == null)
				|| (item.getTipo().equals(DominioTipoFaseSolicitacao.C) && item.getSlcIndExclusao().equals(Boolean.TRUE))) {
			return true;
		}
		if ((item.getTipo().equals(DominioTipoFaseSolicitacao.S) && item
				.getSlsVbgSeq() == null)
				|| (item.getTipo().equals(DominioTipoFaseSolicitacao.S) && item.getSlsNtdGndCodigo() == null)
				|| (item.getTipo().equals(DominioTipoFaseSolicitacao.S) && item.getSlsIndExclusao().equals(Boolean.TRUE))) {
			return true;
		}
		// VALIDACAO DE REGRAS ORCAMENTARIAS
		this.validarRegrasOrcamentarias(item);
		
		return false;
	}
	
	private void validarRegrasOrcamentarias(ItemPropostaAFVO item) throws BaseException{
		if (item.getSlsNumero() != null) {
			ScoSolicitacaoServico solicitacaoServico = this
					.scoSolicitacaoServicoDAO.obterPorChavePrimaria(
							item.getSlsNumero());
			solicitacaoServicoFacade.validarRegrasOrcamentarias(solicitacaoServico, solicitacaoServico);
		}
		if (item.getSlcNumero() != null) {
			ScoSolicitacaoDeCompra solicitacaoCompra = this
					.scoSolicitacoesDeComprasDAO.obterPorChavePrimaria(
							item.getSlcNumero());
			try{
				solicitacaoComprasFacade.validarRegrasOrcamentarias(solicitacaoCompra);
			}
			catch(BaseException e){
				String mensagem = e.getMessage();
				throw new ApplicationBusinessException(AutFornecimentoRNExceptionCode.ERRO_GERACAO_AF_REGRAS_ORCAMENTARIAS, mensagem);
			}
		}
	}
	
	

	public ScoAutorizacaoForn inserirAutorizacaoFornecimento(ItemPropostaAFVO item,
			GeracaoAfVO geracaoAfVO, DominioModalidadeEmpenho modalidadeEmpenhoSelecionada, ScoAutorizacaoForn autorizacaoFornecimento, Short nroComplemento) throws BaseException {
		if(modalidadeEmpenhoSelecionada==null){
			autorizacaoFornecimento.setModalidadeEmpenho(DominioModalidadeEmpenho.ORDINARIO);
		}
		autorizacaoFornecimento.setDtGeracao(new Date());
		autorizacaoFornecimento.setNroComplemento(nroComplemento);
		autorizacaoFornecimento
				.setSituacao(DominioSituacaoAutorizacaoFornecimento.AE);
		autorizacaoFornecimento.setGeracao(Boolean.TRUE);

		/*AghParametros parametroMatriculaChefeCompras = this
				.parametroFacade.buscarAghParametro(
						AghuParametrosEnum.P_MATR_CHEFE_CPRAS);
		final Integer matriculaChefeCompras = parametroMatriculaChefeCompras
				.getVlrNumerico().intValue();
		AghParametros parametroVinCodigoChefeCompras = this
				.parametroFacade.buscarAghParametro(
						AghuParametrosEnum.P_VIN_CHEFE_CPRAS);
		final Short vinCodigoChefeCompras = parametroVinCodigoChefeCompras
				.getVlrNumerico().shortValue();
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(matriculaChefeCompras);
		id.setVinCodigo(vinCodigoChefeCompras);
		RapServidores servidorAutorizado = this.getRegistroColaboradorFacade()
				.obterRapServidoresPorChavePrimaria(id);
		*/
		autorizacaoFornecimento.setServidorAutorizado(null);
		Date dataPrevisaoEntrega = new Date();
		if (item.getItemPropostaFornecedor().getPropostaFornecedor() != null) {
			if(item.getItemPropostaFornecedor().getPropostaFornecedor().getPrazoEntrega()!=null){
				dataPrevisaoEntrega = DateUtil.adicionaDias(dataPrevisaoEntrega,
						item.getItemPropostaFornecedor().getPropostaFornecedor().getPrazoEntrega().intValue());
			}
			autorizacaoFornecimento.setValorFrete(item.getItemPropostaFornecedor().getPropostaFornecedor().getValorTotalFrete());
		}
		autorizacaoFornecimento.setDtPrevEntrega(dataPrevisaoEntrega);
		autorizacaoFornecimento.setSequenciaAlteracao(Short.valueOf("0"));
		autorizacaoFornecimento.setExclusao(Boolean.FALSE);

		/*AghParametros parametroMatriculaCoordGrum = this.parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_MATR_COORD_GRUM);
		final Integer matriculaCoordGrum = parametroMatriculaCoordGrum
				.getVlrNumerico().intValue();
		AghParametros parametroVinCodigoCoordGrum = this.parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_VIN_COORD_GRUM);
		final Short vinCodigoCoordGrum = parametroVinCodigoCoordGrum
				.getVlrNumerico().shortValue();

		RapServidoresId servidorId = new RapServidoresId();
		servidorId.setMatricula(matriculaCoordGrum);
		servidorId.setVinCodigo(vinCodigoCoordGrum);
		RapServidores servidorCoord = this.getRegistroColaboradorFacade()
				.obterRapServidoresPorChavePrimaria(servidorId);*/
		autorizacaoFornecimento.setServidorAssinaCoord(null);
		autorizacaoFornecimento.setAprovada(DominioAprovadaAutorizacaoForn.S);
		if (item.getModalidadeEmpenho() != null) {
			autorizacaoFornecimento.setModalidadeEmpenho(item.getModalidadeEmpenho());
		}
		autorizacaoFornecimento.setImprRefContrato(Boolean.FALSE);

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		autorizacaoFornecimento.setServidorGestor(servidorLogado);
		
		autorizacaoFornecimento.setEntregaProgramada(Boolean.FALSE);
		return scoAutorizacaoFornRN.inserir(autorizacaoFornecimento);
	}
	
	public ScoItemAutorizacaoForn inserirItemAutorizacaoFornecimento(
			Integer numero, ItemPropostaAFVO item, GeracaoAfVO geracaoAfVO)
			throws BaseException {
		ScoItemAutorizacaoForn itemAutorizacaoFornecimento = new ScoItemAutorizacaoForn();
		ScoItemAutorizacaoFornId id = new ScoItemAutorizacaoFornId();
		id.setAfnNumero(numero);
		id.setNumero(geracaoAfVO.getIafNumero());
		itemAutorizacaoFornecimento.setId(id);
		final ScoItemPropostaFornecedorId itemPropostaFornecedorId = this.getIPFId(item);
		ScoItemPropostaFornecedor itemPropostaFornecedor = scoItemPropostaFornecedorDAO.obterPorChavePrimaria(itemPropostaFornecedorId);
		itemAutorizacaoFornecimento
				.setItemPropostaFornecedor(itemPropostaFornecedor);
		String umdCodigo = geracaoAfVO.getUmdCodigoEstoque();
		if (umdCodigo != null) {
			ScoUnidadeMedida unidadeMedida = this.scoUnidadeMedidaDAO
					.obterPorChavePrimaria(umdCodigo);
			itemAutorizacaoFornecimento.setUnidadeMedida(unidadeMedida);
		}
		
		//Fator de conversao do item da AF sempre sera 1 na geracao
		itemAutorizacaoFornecimento.setFatorConversao(1);
		// #32041
		this.setValorQtd(item, geracaoAfVO, itemAutorizacaoFornecimento);
		itemAutorizacaoFornecimento
				.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.AE);
		if (item.getItemPropostaFornecedor() != null
				&& item.getItemPropostaFornecedor().getPercIpi() != null) {
			itemAutorizacaoFornecimento.setPercIpi(item
					.getItemPropostaFornecedor().getPercIpi().doubleValue());
		}
		if (item.getItemPropostaFornecedor() != null
				&& item.getItemPropostaFornecedor().getPercAcrescimo() != null) {
			itemAutorizacaoFornecimento.setPercAcrescimo(item
					.getItemPropostaFornecedor().getPercAcrescimo()
					.doubleValue());
		}
		itemAutorizacaoFornecimento.setIndExclusao(Boolean.FALSE);
		itemAutorizacaoFornecimento.setSequenciaAlteracao(0);
		ScoMarcaComercial marcaComercial = null;
		if(item.getItemPropostaFornecedor()
						.getMarcaComercial().getCodigo()!=null){
			marcaComercial = this.scoMarcaComercialDAO.obterPorChavePrimaria(item.getItemPropostaFornecedor().getMarcaComercial().getCodigo());
		}
		itemAutorizacaoFornecimento.setMarcaComercial(marcaComercial);
		ScoNomeComercial nomeComercial = null;
		if(item.getItemPropostaFornecedor().getNomeComercial().getId()!=null){
			nomeComercial = this.scoNomeComercialDAO.obterPorChavePrimaria(item.getItemPropostaFornecedor().getNomeComercial().getId());
		}
		ScoMarcaModelo marcaModelo = null;
		if(item.getItemPropostaFornecedor()
				.getModeloComercial() != null && 
		   item.getItemPropostaFornecedor()
						.getModeloComercial().getId()!=null){
			marcaModelo = this.scoMarcaModeloDAO.obterPorChavePrimaria(item.getItemPropostaFornecedor().getModeloComercial().getId());
		}
		itemAutorizacaoFornecimento.setModeloComercial(marcaModelo);
		itemAutorizacaoFornecimento.setNomeComercial(nomeComercial);
		itemAutorizacaoFornecimento.setIndEstorno(Boolean.FALSE);
		AghParametros parametroPercVarPreco = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PERC_VAR_PRECO);
		final Float percVarPreco = parametroPercVarPreco.getVlrNumerico().floatValue();
		itemAutorizacaoFornecimento.setPercVarPreco(percVarPreco);
		itemAutorizacaoFornecimento.setIndRecebimento(Boolean.FALSE);
		itemAutorizacaoFornecimento.setValorEfetivado(Double.valueOf("0"));
		itemAutorizacaoFornecimento.setIndContrato(Boolean.FALSE);
		itemAutorizacaoFornecimento.setIndConsignado(Boolean.FALSE);
		itemAutorizacaoFornecimento.setIndProgrEntgAuto(Boolean.FALSE);
		itemAutorizacaoFornecimento.setIndAnaliseProgrPlanej(Boolean.TRUE);
		itemAutorizacaoFornecimento.setIndProgrEntgBloq(Boolean.FALSE);
		if (item.getItemPropostaFornecedor().getUnidadeMedida() != null) {			
			ScoUnidadeMedida umdCodigoForn = this.scoUnidadeMedidaDAO.obterPorChavePrimaria(item.getItemPropostaFornecedor().getUnidadeMedida().getCodigo());
			itemAutorizacaoFornecimento.setUmdCodigoForn(umdCodigoForn);
		}
		itemAutorizacaoFornecimento.setFatorConversaoForn(item.getItemPropostaFornecedor().getFatorConversao());
		itemAutorizacaoFornecimento.setIndPreferencialCum(Boolean.FALSE);
		itemAutorizacaoFornecimento.setQtdeRecebida(0);
		return scoItemAutorizacaoFornRN.inserirItemAutorizacaoFornecimento(itemAutorizacaoFornecimento);
	}
	
	private ScoItemPropostaFornecedorId getIPFId(ItemPropostaAFVO item) {
		ScoItemPropostaFornecedorId itemPropostaFornecedorId = new ScoItemPropostaFornecedorId();
		itemPropostaFornecedorId.setNumero(item.getItemPropostaFornecedor().getId().getNumero());
		itemPropostaFornecedorId.setPfrLctNumero(item.getItemPropostaFornecedor().getId().getPfrLctNumero());
		itemPropostaFornecedorId.setPfrFrnNumero(item.getItemPropostaFornecedor().getId().getPfrFrnNumero());
		return itemPropostaFornecedorId;
	}

	private void setValorQtd(ItemPropostaAFVO item, GeracaoAfVO geracaoAfVO, ScoItemAutorizacaoForn itemAutorizacaoFornecimento) {
		if (item.getSlsNumero() == null) { // Material
			if (geracaoAfVO.getQuantidade() != null && item.getFrequenciaEntrega() != null) {
				itemAutorizacaoFornecimento.setQtdeSolicitada(geracaoAfVO.getQuantidade().intValue() * item.getFrequenciaEntrega());
			} else if (geracaoAfVO.getQuantidade() != null && item.getFrequenciaEntrega() == null) {
				itemAutorizacaoFornecimento.setQtdeSolicitada(geracaoAfVO.getQuantidade().intValue());
			} else {
				itemAutorizacaoFornecimento.setQtdeSolicitada(0);
			}
			itemAutorizacaoFornecimento.setValorUnitario(geracaoAfVO.getValorUnitario());
		} else {
			itemAutorizacaoFornecimento.setValorUnitario((geracaoAfVO.getValorUnitario() == null ? 0 : geracaoAfVO.getValorUnitario())
					* (item.getFrequenciaEntrega() == null ? 0 : item.getFrequenciaEntrega()));
			itemAutorizacaoFornecimento.setQtdeSolicitada(1);
		}
	}

	public ScoItemAutorizacaoFornJn inserirItemAutorizacaoFornecimentoJn(
			Integer numero, ItemPropostaAFVO item, GeracaoAfVO geracaoAfVO) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoItemAutorizacaoFornJn itemAutorizacaoFornecimentoJn = new ScoItemAutorizacaoFornJn();
		itemAutorizacaoFornecimentoJn.setJnUser(servidorLogado.getUsuario());
		itemAutorizacaoFornecimentoJn.setJnOperation(DominioOperacoesJournal.INS.toString());
		itemAutorizacaoFornecimentoJn.setJnDateTime(new Date());
		itemAutorizacaoFornecimentoJn.setAfnNumero(numero);
		itemAutorizacaoFornecimentoJn.setNumero(geracaoAfVO.getIafNumero());
		itemAutorizacaoFornecimentoJn.setItemPropostaFornecedor(item
				.getItemPropostaFornecedor());
		itemAutorizacaoFornecimentoJn.setIndEstorno(Boolean.FALSE);
		String umdCodigo = geracaoAfVO.getUmdCodigoEstoque();
		if (umdCodigo != null) {
			ScoUnidadeMedida unidadeMedida = scoUnidadeMedidaDAO.obterPorChavePrimaria(umdCodigo);
			itemAutorizacaoFornecimentoJn.setUnidadeMedida(unidadeMedida);
		}
		itemAutorizacaoFornecimentoJn.setFatorConversao(item.getItemPropostaFornecedor().getFatorConversao());
		this.setValoreQtd(itemAutorizacaoFornecimentoJn, item, geracaoAfVO);
		itemAutorizacaoFornecimentoJn.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.AE);
		if (item.getItemPropostaFornecedor().getPercIpi() != null) {
			itemAutorizacaoFornecimentoJn.setPercIpi(item.getItemPropostaFornecedor().getPercIpi().doubleValue());
		}
		if (item.getItemPropostaFornecedor().getPercDesconto() != null) {
			itemAutorizacaoFornecimentoJn.setPercDesconto(item.getItemPropostaFornecedor().getPercDesconto().doubleValue());
		}
		if (item.getItemPropostaFornecedor().getPercAcrescimo() != null) {
			itemAutorizacaoFornecimentoJn.setPercAcrescimo(item.getItemPropostaFornecedor().getPercAcrescimo().doubleValue());
		}
		itemAutorizacaoFornecimentoJn.setIndExclusao(Boolean.FALSE);
		itemAutorizacaoFornecimentoJn.setSequenciaAlteracao(0);
		ScoMarcaComercial marcaComercial = null;
		if(item.getItemPropostaFornecedor()
						.getMarcaComercial().getCodigo()!=null){
			marcaComercial = scoMarcaComercialDAO.obterPorChavePrimaria(item.getItemPropostaFornecedor().getMarcaComercial().getCodigo());
		}
		itemAutorizacaoFornecimentoJn.setMarcaComercial(marcaComercial);
		ScoNomeComercial nomeComercial = null;
		if(item.getItemPropostaFornecedor().getNomeComercial().getId()!=null && item.getItemPropostaFornecedor()
										.getNomeComercial().getId().getMcmCodigo()!=null && item.getItemPropostaFornecedor()
										.getNomeComercial().getId().getNumero()!=null){
			nomeComercial = scoNomeComercialDAO.obterPorChavePrimaria(item.getItemPropostaFornecedor().getNomeComercial().getId());
		}
		itemAutorizacaoFornecimentoJn.setNomeComercial(nomeComercial);
		itemAutorizacaoFornecimentoJn.setPercVarPreco(Double.valueOf("1"));
		itemAutorizacaoFornecimentoJn.setQtdeRecebida(Integer.valueOf("0"));
		itemAutorizacaoFornecimentoJn.setIndRecebimento(Boolean.FALSE);
		itemAutorizacaoFornecimentoJn.setValorEfetivado(Double.valueOf("0"));
		return scoItemAutorizacaoFornJnRN.inserirItemAutorizacaoFornecimentoJn(itemAutorizacaoFornecimentoJn);
	}

	private void setValoreQtd(ScoItemAutorizacaoFornJn itemAutorizacaoFornecimentoJn, ItemPropostaAFVO item, GeracaoAfVO geracaoAfVO) {
		if (item.getSlsNumero() == null) { // Material
			if (geracaoAfVO.getQuantidade() != null && item.getFrequenciaEntrega() != null) {
				itemAutorizacaoFornecimentoJn.setQtdeSolicitada(geracaoAfVO.getQuantidade().intValue() * item.getFrequenciaEntrega());
			}
			itemAutorizacaoFornecimentoJn.setValorUnitario(geracaoAfVO.getValorUnitario());
		} else {
			itemAutorizacaoFornecimentoJn.setQtdeSolicitada(1);
			itemAutorizacaoFornecimentoJn.setValorUnitario((geracaoAfVO.getValorUnitario() == null ? 0.0 : geracaoAfVO.getValorUnitario())
					* (item.getFrequenciaEntrega() == null ? 0 : item.getFrequenciaEntrega()));
		}
	}
	
	public void inserirScoFasesSolicitacao(ScoItemAutorizacaoForn itemGerado, ItemPropostaAFVO item) throws BaseException {
		ScoFaseSolicitacao faseSolicitacao = new ScoFaseSolicitacao();
		faseSolicitacao.setTipo(item.getTipo());
		if (item.getSlsNumero() != null) {
			ScoSolicitacaoServico solicitacaoServico = scoSolicitacaoServicoDAO.obterPorChavePrimaria(item.getSlsNumero());
			faseSolicitacao.setSolicitacaoServico(solicitacaoServico);
		}
		if (item.getSlcNumero() != null) {
			ScoSolicitacaoDeCompra solicitacaoCompra = scoSolicitacoesDeComprasDAO.obterPorChavePrimaria(item.getSlcNumero());
			faseSolicitacao.setSolicitacaoDeCompra(solicitacaoCompra);
		}
		faseSolicitacao.setItemAutorizacaoForn(itemGerado);
		faseSolicitacao.setExclusao(Boolean.FALSE);
		this.comprasFacade.inserirScoFaseSolicitacao(faseSolicitacao);	
	}

	public ScoAutorizacaoForn popularAutorizacaoFornecimento(ItemPropostaAFVO item,
			GeracaoAfVO geracaoAfVO) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoAutorizacaoForn autorizacaoFornecimento = new ScoAutorizacaoForn();
		autorizacaoFornecimento.setServidor(servidorLogado);
		if (item.getSlcVbgSeq() != null) {
			FsoVerbaGestao verbaGestao = cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(item.getSlcVbgSeq());
			autorizacaoFornecimento.setVerbaGestao(verbaGestao);
		}
		if (item.getSlsVbgSeq() != null) {
			FsoVerbaGestao verbaGestao = cadastrosBasicosOrcamentoFacade.obterVerbaGestaoPorChavePrimaria(item.getSlsVbgSeq());
			autorizacaoFornecimento.setVerbaGestao(verbaGestao);
		}
		autorizacaoFornecimento.setMoeda(item.getItemPropostaFornecedor().getMoeda());
		if (item.getSlcNtdCodigo() != null && item.getSlcNtdGndCodigo() != null) {
			FsoNaturezaDespesaId id = new FsoNaturezaDespesaId();
			id.setCodigo(item.getSlcNtdCodigo().byteValue());
			id.setGndCodigo(item.getSlcNtdGndCodigo());
			FsoNaturezaDespesa naturezaDespesa = this.orcamentoFacade.obterFsoNaturezaDespesaPorChavePrimaria(id);
			autorizacaoFornecimento.setNaturezaDespesa(naturezaDespesa);
		}
		if (item.getSlsNtdCodigo() != null && item.getSlsNtdGndCodigo() != null) {
			FsoNaturezaDespesaId id = new FsoNaturezaDespesaId();
			id.setCodigo(item.getSlsNtdCodigo().byteValue());
			id.setGndCodigo(item.getSlsNtdGndCodigo());
			FsoNaturezaDespesa naturezaDespesa = this.orcamentoFacade.obterFsoNaturezaDespesaPorChavePrimaria(id);
			autorizacaoFornecimento.setNaturezaDespesa(naturezaDespesa);
		}
		if (item.getSlcNtdCodigo() != null && item.getSlcNtdGndCodigo() != null) {
			FsoNaturezaDespesaId id = new FsoNaturezaDespesaId();
			id.setCodigo(item.getSlcNtdCodigo().byteValue());
			id.setGndCodigo(item.getSlcNtdGndCodigo());
			FsoNaturezaDespesa naturezaDespesa = orcamentoFacade.obterFsoNaturezaDespesaPorChavePrimaria(id);
			autorizacaoFornecimento.setNaturezaDespesa(naturezaDespesa);
		}
		if (item.getItemPropostaFornecedor().getId() != null) {
			ScoPropostaFornecedorId id = new ScoPropostaFornecedorId();
			id.setFrnNumero(item.getItemPropostaFornecedor().getId().getPfrFrnNumero());
			id.setLctNumero(item.getItemPropostaFornecedor().getId().getPfrLctNumero());
			ScoPropostaFornecedor propostaFornecedor = pacFacade.obterPropostaFornecedor(id);
			autorizacaoFornecimento.setPropostaFornecedor(propostaFornecedor);
		}

		if (item.getItemPropostaFornecedor().getCondicaoPagamentoPropos() != null) {
			Integer cdpNumero = item.getItemPropostaFornecedor()
					.getCondicaoPagamentoPropos().getNumero();
			ScoCondicaoPagamentoPropos condicaoPagamentoPropos = comprasFacade.obterCondicaoPagamentoPropos(cdpNumero);
			autorizacaoFornecimento.setCondicaoPagamentoPropos(condicaoPagamentoPropos);
		}
		return autorizacaoFornecimento;
	}

	public List<GeracaoAfVO> listarPacsParaAF(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, 
			ScoLicitacao licitacao, ScoGrupoMaterial grupoMaterial, Boolean indProcNaoAptoGer) 
					throws ApplicationBusinessException{
		List<ScoLicitacao> listaLicitacao = scoLicitacaoDAO.listarPacsParaAF(firstResult, maxResults, licitacao, grupoMaterial, indProcNaoAptoGer);
		List<GeracaoAfVO> listaGeracaoAfVOs = new ArrayList<GeracaoAfVO>();
		for(ScoLicitacao licitacaoAux: listaLicitacao){
			GeracaoAfVO geracaoAfVO = new GeracaoAfVO();
			geracaoAfVO.setLicitacao(licitacaoAux);
			Long numero = scoPropostaFornecedorDAO.pesquisarItensParaGeracaoCount(licitacaoAux.getNumero());
			if(numero==0L){
				geracaoAfVO.setGeraAf(false);
			} else {
				geracaoAfVO.setGeraAf(true);
			}
			geracaoAfVO.setMostrarAfGeradas(this.scoAutorizacaoFornDAO.contarAfGerada(licitacaoAux.getNumero()) > 0);
			
			listaGeracaoAfVOs.add(geracaoAfVO);
		}
		return listaGeracaoAfVOs;
	}
	
	public Integer listarPacsParaAFCount(ScoLicitacao licitacao, ScoGrupoMaterial grupoMaterial, Boolean indProcNaoAptoGer) throws ApplicationBusinessException{
			return scoLicitacaoDAO.listarPacsParaAFCount(licitacao, grupoMaterial, indProcNaoAptoGer);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public void atualizarAutorizacaoForn(ScoAutorizacaoForn e, boolean flush){
		scoAutorizacaoFornDAO.merge(e);
		if (flush) {
			scoAutorizacaoFornDAO.flush();
		}	
	}
	
}
