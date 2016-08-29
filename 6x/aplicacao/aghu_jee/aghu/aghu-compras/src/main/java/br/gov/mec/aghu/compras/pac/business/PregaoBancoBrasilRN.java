package br.gov.mec.aghu.compras.pac.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoContatoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoCriterioEscolhaPropostaDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFormaPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoMarcaComercialDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoUnidadeMedidaDAO;
import br.gov.mec.aghu.compras.pac.vo.EmpresasHomologadasLoteLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.FasesLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.ItemLicitacaoCabecalhoVO;
import br.gov.mec.aghu.compras.pac.vo.PropostaItemLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.PropostaLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.RegistroLancesLoteDisputaPregaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoImpressaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoFornecedor;
import br.gov.mec.aghu.estoque.dao.FcpMoedaDAO;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoContatoFornecedorId;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PregaoBancoBrasilRN extends BaseBusiness {
	
	private static final long serialVersionUID = 1995578206686798448L;
	
	private static final Log LOG = LogFactory.getLog(PregaoBancoBrasilRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;
	
	@Inject
	private ScoContatoFornecedorDAO scoContatoFornecedorDAO;
	
	@Inject
	private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;
	
	@Inject
	private ScoFormaPagamentoDAO scoFormaPagamentoDAO;
	
	@Inject
	private ScoCondicaoPagamentoProposDAO scoCondicaoPagamentoProposDAO;
		
	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	@Inject
	private ScoUnidadeMedidaDAO scoUnidadeMedidaDAO;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaForncedorDAO;
	
	@Inject
	private ScoCriterioEscolhaPropostaDAO scoCriterioEscolhaPropostaDAO;
	
	@Inject
	private FcpMoedaDAO fcpMoedaDAO;
	
	@Inject
	private ScoMarcaComercialDAO scoMarcaComercialDAO;
	
	@Inject
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private IParametroFacade parametroFacade;
	
	@EJB
	private ScoItemPropostaFornecedorRN scoItemPropostaFornecedorRN;
	
	@EJB
	private ScoLicitacaoImpressaoRN scoLicitacaoImpressaoRN;
	
	@EJB 
	private ScoItemLicitacaoRN scoItemLicitacaoRN;
	
	enum PregaoBancoBrasilRNExceptionCode implements BusinessExceptionCode {
		LICITACAO_NAO_HOMOLOGADA, FORNECEDOR_NAO_ENCONTRADO, SELECIONE_UM_PAC, SEM_ARQUIVO_VINCULADO;
	}
	
	/**
	 * @ORABD AOP715_GERA_PROPOSTA
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void gerarPropostaPregaoBB(Integer numPac, String nomeArquivoProcessado) throws ApplicationBusinessException{
		List<ScoLicitacaoImpressaoVO> lista = scoLicitacaoImpressaoRN.lerArquivoProcessadoLicitacaoHomologada(numPac, nomeArquivoProcessado);
		for(ScoLicitacaoImpressaoVO registroVO : lista){
			ScoLicitacao licitacao = scoLicitacaoDAO.obterPorChavePrimaria(Integer.parseInt(registroVO.getProcesso()));
			//tipo 02
			if(verificarHomologada(registroVO.getListaFasesLicitacao())){
				//tipo 07
				for(PropostaLicitacaoVO fornecedorLicitacaoVO : registroVO.getListaPropostaLicitacao()){
					ScoFornecedor fornecedorExistente  = null;
					fornecedorExistente = scoFornecedorDAO.obterFornecedorPorCNPJ(Long.parseLong(fornecedorLicitacaoVO.getCnpjTipo7()));
					if (fornecedorExistente == null){
						ScoFornecedor fornecedorNovo = criarFornecedor(fornecedorLicitacaoVO);
						scoFornecedorDAO.persistir(fornecedorNovo);
						
						ScoContatoFornecedor contatoFornecedor = criarContatoFornecedor(fornecedorLicitacaoVO, fornecedorNovo);
						scoContatoFornecedorDAO.persistir(contatoFornecedor);
					}
				}
				processaTipo12(registroVO, licitacao);
				processaTipo03(registroVO);
				processaTipo09(registroVO);
				atualizarEscolhidos(registroVO);
				
			} else {
				throw new ApplicationBusinessException(PregaoBancoBrasilRNExceptionCode.LICITACAO_NAO_HOMOLOGADA
						, registroVO.getProcesso(), registroVO.getFaseAtualLicitacao());
			}
		}
	}


	private void processaTipo12(ScoLicitacaoImpressaoVO registroVO,	ScoLicitacao licitacao) throws ApplicationBusinessException {
		String loteAtual = StringUtils.EMPTY;
		List<ScoItemLicitacao> itensLicitacao = null;
		//tipo 12
		for (RegistroLancesLoteDisputaPregaoVO lancesVO: registroVO.getListaRegistroLancesLoteDisputaPregao()){
			ScoFornecedor fornecedorExistente  = null;
			fornecedorExistente = scoFornecedorDAO.obterFornecedorPorCNPJ(Long.parseLong(lancesVO.getCnpjTipo12()));
			if (fornecedorExistente != null){
				ScoPropostaFornecedorId idProposta = new ScoPropostaFornecedorId(Integer.parseInt(registroVO.getProcesso()), fornecedorExistente.getNumero());
				ScoPropostaFornecedor propostaFornecedor = null; 
				propostaFornecedor = scoPropostaFornecedorDAO.obterPorChavePrimaria(idProposta);
				if (propostaFornecedor == null){
					//Inclui proposta
					propostaFornecedor = criarPropostaFornecedor(licitacao, fornecedorExistente, idProposta);
					scoPropostaFornecedorDAO.persistir(propostaFornecedor);
					ScoCondicaoPagamentoPropos condicaoPagamento = criarCondicaoPagamento(propostaFornecedor);
					scoCondicaoPagamentoProposDAO.persistir(condicaoPagamento);
					
					//Inclui prazo de pagamento
					ScoParcelasPagamento parcelasPagamento = criarParcelasPagamento(condicaoPagamento);
					scoParcelasPagamentoDAO.persistir(parcelasPagamento);
				}
				if (!loteAtual.equals(lancesVO.getLoteTipo12())){
					loteAtual = lancesVO.getLoteTipo12();
					itensLicitacao = scoItemLicitacaoDAO.obterItensPorLote(Integer.parseInt(registroVO.getProcesso()), Short.parseShort(loteAtual));
				}
				
				for (ScoItemLicitacao itemAtual : itensLicitacao) {
					
					ScoFaseSolicitacao faseSolicitacao = null;
					faseSolicitacao = scoFaseSolicitacaoDAO.pesquisarFaseESolicitacaoPorItemLicitacao(
							Integer.parseInt(registroVO.getProcesso()), 
							itemAtual.getId().getNumero());
					
					BigDecimal valorUnitario = BigDecimal.valueOf(Double.valueOf(lancesVO.getValor().replace(".","").replace(",", ".")));
					
					ScoUnidadeMedida unidadeMedida = scoUnidadeMedidaDAO.obterPorChavePrimaria("UN");
					Integer quantidade = null;
					if(DominioTipoFaseSolicitacao.C == faseSolicitacao.getTipo()){
						quantidade = faseSolicitacao.getSolicitacaoDeCompra().getQtdeAprovada().intValue();
						unidadeMedida = faseSolicitacao.getSolicitacaoDeCompra().getUnidadeMedida();
					} else {
						quantidade = faseSolicitacao.getSolicitacaoServico().getQtdeSolicitada();
					}
					if (itensLicitacao.size() == 1) {
						valorUnitario = valorUnitario.divide(BigDecimal.valueOf(quantidade), 4, BigDecimal.ROUND_DOWN);
					}
					ScoItemPropostaFornecedor itemProposta = scoItemPropostaForncedorDAO.obterItemPorLicitacaoFornecedorNumeroItem(
							fornecedorExistente.getNumero(), 
							Integer.parseInt(registroVO.getProcesso()), 
							itemAtual.getId().getNumero());                                                                                                                                                                                                             
					if (itemProposta == null){
						itemProposta = criarItemPropostaFornecedor(registroVO, fornecedorExistente, null, itemAtual, quantidade,
								unidadeMedida, "PREGÃO ELETRÔNICO", valorUnitario, false);
						scoItemPropostaFornecedorRN.inserirItemPropostaFornecedor(itemProposta);
						
					} else if (itemProposta.getValorUnitario().compareTo(valorUnitario) > 0) {
							itemProposta.setValorUnitario(valorUnitario);
							scoItemPropostaFornecedorRN.atualizarItemPropostaFornecedor(itemProposta);
					}
				
				}
				
			} else {
				throw new ApplicationBusinessException(PregaoBancoBrasilRNExceptionCode.FORNECEDOR_NAO_ENCONTRADO
						, lancesVO.getCnpjTipo12());
			}
		}
	}


	private void atualizarEscolhidos(ScoLicitacaoImpressaoVO registroVO) throws ApplicationBusinessException {
		String loteAtual = StringUtils.EMPTY;
		List<ScoItemLicitacao> itensLicitacao = null;
		Short codigo = null;
		try {
			codigo = parametroFacade.buscarValorShort(AghuParametrosEnum.P_AGHU_PG_COD_CRITERIO_ESCOLHA_MP);
		} catch (ApplicationBusinessException e) {
			// valor defaut segundo documento da estória #5482 é 1, caso não seja encontrado
			if (e.getCode().equals(AghParemetrosONExceptionCode.PARAMETRO_INVALIDO)) {
				codigo = Short.valueOf("1");
			}
		}
		ScoCriterioEscolhaProposta criterioEscolhaProposta =  scoCriterioEscolhaPropostaDAO.obterOriginal(codigo);	
			
		for (EmpresasHomologadasLoteLicitacaoVO registroTipo03 :  registroVO.getListaEmpresasHomologadasLoteLicitacao()) {
			BigDecimal valorLote = BigDecimal.valueOf(Double.valueOf(registroTipo03.getValorLote().replace(".","").replace(",", ".")));
			if (!loteAtual.equals(registroTipo03.getLoteTipo3()) &
					valorLote.compareTo(BigDecimal.ZERO) == 1){
				ScoFornecedor fornecedor = scoFornecedorDAO.obterFornecedorPorCNPJ(Long.parseLong(registroTipo03.getCnpjTipo3()));
				
				loteAtual = registroTipo03.getLoteTipo3();
				itensLicitacao = scoItemLicitacaoDAO.obterItensPorLote(Integer.parseInt(registroVO.getProcesso()), Short.parseShort(loteAtual));
				for (ScoItemLicitacao scoItemLicitacao : itensLicitacao) {
					ScoItemPropostaFornecedor itemProposta = scoItemPropostaForncedorDAO.obterItemPorLicitacaoFornecedorNumeroItem(
							fornecedor.getNumero(), 
							Integer.parseInt(registroVO.getProcesso()), 
							scoItemLicitacao.getId().getNumero());
					itemProposta.setIndEscolhido(true);
					itemProposta.setCriterioEscolhaProposta(criterioEscolhaProposta);
					itemProposta.setCondicaoPagamentoPropos(scoCondicaoPagamentoProposDAO.obterCondicaoPagamentoPorPropostaFornecedor(
							fornecedor.getNumero(), Integer.parseInt(registroVO.getProcesso())));
					scoItemLicitacao.setPropostaEscolhida(true);
					scoItemLicitacao.setSituacaoJulgamento(DominioSituacaoJulgamento.JU);
					
					scoItemPropostaFornecedorRN.atualizarItemPropostaFornecedor(itemProposta);
					scoItemLicitacaoRN.atualizarItemLicitacao(scoItemLicitacao);
										
				}
			}
		}
	}

	private void processaTipo03(ScoLicitacaoImpressaoVO registroVO) throws ApplicationBusinessException {
		String loteAtual = StringUtils.EMPTY;
		List<ScoItemLicitacao> itensLicitacao = null;
		for (EmpresasHomologadasLoteLicitacaoVO registroTipo03 :  registroVO.getListaEmpresasHomologadasLoteLicitacao()) {
			ScoFornecedor fornecedor = scoFornecedorDAO.obterFornecedorPorCNPJ(Long.parseLong(registroTipo03.getCnpjTipo3()));
			BigDecimal valorLote = BigDecimal.valueOf(Double.valueOf(registroTipo03.getValorLote().replace(".","").replace(",", ".")));
			if (!loteAtual.equals(registroTipo03.getLoteTipo3()) &&
				valorLote.compareTo(BigDecimal.ZERO) == 1){
				loteAtual = registroTipo03.getLoteTipo3();
				itensLicitacao = scoItemLicitacaoDAO.obterItensPorLote(Integer.parseInt(registroVO.getProcesso()), Short.parseShort(loteAtual));
				ScoCondicaoPagamentoPropos condicaoPagamento = null;
				//BigDecimal valorLote = BigDecimal.valueOf(Double.valueOf(registroTipo03.getValorLote().replace(".","").replace(",", ".")));
				for (ScoItemLicitacao scoItemLicitacao : itensLicitacao) {
					ScoUnidadeMedida unidadeMedida = scoUnidadeMedidaDAO.obterPorChavePrimaria("UN");
					Integer quantidade = null;
					ScoFaseSolicitacao faseSolicitacao = null;
					faseSolicitacao = scoFaseSolicitacaoDAO.pesquisarFaseESolicitacaoPorItemLicitacao(
							Integer.parseInt(registroVO.getProcesso()), 
							scoItemLicitacao.getId().getNumero());
					if(faseSolicitacao.getTipo() == DominioTipoFaseSolicitacao.C){
						quantidade = faseSolicitacao.getSolicitacaoDeCompra().getQtdeAprovada().intValue();
						unidadeMedida = faseSolicitacao.getSolicitacaoDeCompra().getUnidadeMedida();
					} else {
						quantidade = faseSolicitacao.getSolicitacaoServico().getQtdeSolicitada();
					}
					String observacao = StringUtils.EMPTY;
					BigDecimal valorUnitario = null;
					if(itensLicitacao.size() == 1){
						valorUnitario = valorLote.divide(BigDecimal.valueOf(quantidade), 4, BigDecimal.ROUND_DOWN);
						observacao = "Valor Unitário = Valor Lote.";
					} else {
						valorUnitario = valorLote.divide(BigDecimal.valueOf(quantidade), 4, BigDecimal.ROUND_DOWN);
						valorUnitario = valorUnitario.divide(BigDecimal.valueOf(itensLicitacao.size()), 4, BigDecimal.ROUND_DOWN);
						observacao = "Obs.Vlr Unit não confiável.Orig:Vlr Lote Nº'"+Integer.valueOf(loteAtual)+"("+valorLote.setScale(0, RoundingMode.DOWN).longValueExact()+")rateado";
					}
					
					ScoItemPropostaFornecedor itemProposta = scoItemPropostaForncedorDAO.obterItemPorLicitacaoFornecedorNumeroItem(
							fornecedor.getNumero(), 
							Integer.parseInt(registroVO.getProcesso()), 
							scoItemLicitacao.getId().getNumero());
					if (itemProposta == null){
						valorUnitario = valorLote.divide(BigDecimal.valueOf(quantidade), 4, BigDecimal.ROUND_DOWN);
						condicaoPagamento = scoCondicaoPagamentoProposDAO.obterCondicaoPagamentoPorPropostaFornecedor(
								fornecedor.getNumero(), Integer.parseInt(registroVO.getProcesso()));
						itemProposta = criarItemPropostaFornecedor(registroVO, fornecedor, condicaoPagamento, scoItemLicitacao, quantidade,
								unidadeMedida, observacao, valorUnitario, false);
						scoItemPropostaFornecedorRN.inserirItemPropostaFornecedor(itemProposta);
						
					} else {
							itemProposta.setObservacao(observacao);
							itemProposta.setCondicaoPagamentoPropos(condicaoPagamento);
							scoItemPropostaFornecedorRN.atualizarItemPropostaFornecedor(itemProposta);
					}
				}
			}
		}
	}
	
	private void processaTipo09(ScoLicitacaoImpressaoVO registroVO)
			throws ApplicationBusinessException {
		String loteAtual = StringUtils.EMPTY;
		List<ScoItemLicitacao> itensLicitacao = null;
		for (ItemLicitacaoCabecalhoVO cabecalhoTipo05 : registroVO.getListaItensLicitacaoCabecalho()) {
			for (PropostaItemLicitacaoVO registroTipo09 : cabecalhoTipo05.getListaTipo09()) {
				if (!loteAtual.equals(cabecalhoTipo05.getLoteTipo5())) {
					loteAtual = cabecalhoTipo05.getLoteTipo5();
					itensLicitacao = scoItemLicitacaoDAO.obterItensPorLote(Integer.parseInt(registroVO.getProcesso()), Short.parseShort(loteAtual));
				}
				ScoItemLicitacao scoItemLicitacao = itensLicitacao.get(Integer.parseInt(registroTipo09.getItemTipo9()) - 1);
				ScoFornecedor fornecedor = scoFornecedorDAO.obterFornecedorPorCNPJ(Long.parseLong(registroTipo09.getCnpjTipo9()));
				
				ScoItemPropostaFornecedor itemProposta = scoItemPropostaForncedorDAO.obterItemPorLicitacaoFornecedorNumeroItem(
								fornecedor.getNumero(),
								Integer.parseInt(registroVO.getProcesso()),
								scoItemLicitacao.getId().getNumero());
				if (itemProposta != null) {
					itemProposta.setValorUnitario(BigDecimal.valueOf(Double.valueOf(registroTipo09.getValorUnitario().replace(".", "").replace(",", "."))).stripTrailingZeros());
					itemProposta.setObservacao(null);
					scoItemPropostaFornecedorRN.atualizarItemPropostaFornecedor(itemProposta);
				}
			}
		}
	}

	private ScoItemPropostaFornecedor criarItemPropostaFornecedor(
			ScoLicitacaoImpressaoVO registroVO,
			ScoFornecedor fornecedorExistente,
			ScoCondicaoPagamentoPropos condicaoPagamento,
			ScoItemLicitacao itemAtual, Integer quantidade,
			ScoUnidadeMedida unidadeMedida, String observacao,
			BigDecimal valorUnitario, Boolean indEscolhido) {
		ScoItemPropostaFornecedor itemProposta;
		itemProposta = new ScoItemPropostaFornecedor(new ScoItemPropostaFornecedorId(Integer.parseInt(registroVO.getProcesso()) 
				, fornecedorExistente.getNumero()
				, scoItemPropostaForncedorDAO.obterMaxNumeroItemPropostaFornecedor(fornecedorExistente.getNumero(), Integer.parseInt(registroVO.getProcesso()), true)));
		itemProposta.setItemLicitacao(itemAtual);
		itemProposta.setUnidadeMedida(unidadeMedida);
		itemProposta.setMoeda(fcpMoedaDAO.obterPorChavePrimaria(Short.parseShort("1")));
		itemProposta.setQuantidade(quantidade.longValue());
		itemProposta.setIndEscolhido(Boolean.FALSE);
		itemProposta.setIndComDesconto(Boolean.FALSE);
		itemProposta.setIndNacional(Boolean.TRUE);
		itemProposta.setIndDesclassificado(Boolean.FALSE);
		itemProposta.setFatorConversao(1);
		itemProposta.setMarcaComercial(scoMarcaComercialDAO.obterPorChavePrimaria(9999));
		itemProposta.setValorUnitario(valorUnitario);
		itemProposta.setObservacao(observacao);
		itemProposta.setIndExclusao(Boolean.FALSE);
		itemProposta.setIndAnalisadoPt(Boolean.FALSE);
		itemProposta.setIndEscolhido(indEscolhido);
		itemProposta.setCondicaoPagamentoPropos(condicaoPagamento);
		itemProposta.setIndAutorizUsr(Boolean.FALSE);
		return itemProposta;
	}

	private ScoParcelasPagamento criarParcelasPagamento(
			ScoCondicaoPagamentoPropos condicaoPagamento) throws ApplicationBusinessException {
		ScoParcelasPagamento parcelasPagamento = new ScoParcelasPagamento();
		parcelasPagamento.setCondicaoPagamentoPropos(condicaoPagamento);
		parcelasPagamento.setParcela(Short.parseShort("1"));
		parcelasPagamento.setPrazo(parametroFacade.buscarValorShort(AghuParametrosEnum.P_FPG_PRAZO_PREGAO_BB));
		parcelasPagamento.setPercPagamento(new BigDecimal(100));
		return parcelasPagamento;
	}

	private ScoCondicaoPagamentoPropos criarCondicaoPagamento(
			ScoPropostaFornecedor propostaFornecedor) throws ApplicationBusinessException {
		ScoCondicaoPagamentoPropos condicaoPagamento;
		condicaoPagamento = new ScoCondicaoPagamentoPropos();
		condicaoPagamento.setFormaPagamento(scoFormaPagamentoDAO.obterPorChavePrimaria(
				parametroFacade.buscarValorShort(AghuParametrosEnum.P_FPG_PREGAO_BB)));
		condicaoPagamento.setIndCondEscolhida(Boolean.FALSE);
		condicaoPagamento.setPropostaFornecedor(propostaFornecedor);
		return condicaoPagamento;
	}

	private ScoPropostaFornecedor criarPropostaFornecedor(
			ScoLicitacao licitacao, ScoFornecedor fornecedorExistente,
			ScoPropostaFornecedorId idProposta) {
		ScoPropostaFornecedor propostaFornecedor;
		propostaFornecedor = new ScoPropostaFornecedor(idProposta);
		propostaFornecedor.setDtDigitacao(Calendar.getInstance().getTime());
		propostaFornecedor.setDtApresentacao(Calendar.getInstance().getTime());
		propostaFornecedor.setPrazoEntrega(Short.parseShort("10"));
		propostaFornecedor.setIndExclusao(Boolean.FALSE);
		propostaFornecedor.setLicitacao(licitacao);
		propostaFornecedor.setServidor(servidorLogadoFacade.obterServidorLogado());
		propostaFornecedor.setFornecedor(fornecedorExistente);
		return propostaFornecedor;
	}

	private ScoContatoFornecedor criarContatoFornecedor(
			PropostaLicitacaoVO fornecedorLicitacaoVO,
			ScoFornecedor fornecedorNovo) {
		ScoContatoFornecedor contatoFornecedor = new ScoContatoFornecedor();
		contatoFornecedor.setId(new ScoContatoFornecedorId(fornecedorNovo.getNumero(), Short.parseShort("1")));
		if(fornecedorLicitacaoVO.getContato() != null && !fornecedorLicitacaoVO.getContato().trim().isEmpty()){
			contatoFornecedor.setNome(StringUtils.abbreviate(fornecedorLicitacaoVO.getContato().trim(), 15)); 	
		} else {
			contatoFornecedor.setNome("Contato"); 	
		}
		contatoFornecedor.setFuncao("VENDAS");
		contatoFornecedor.setDdd(Short.parseShort(fornecedorLicitacaoVO.getDddContato()));
		contatoFornecedor.setFone(Long.parseLong(fornecedorLicitacaoVO.getNumeroTelefoneContato()));
		contatoFornecedor.setEMail(fornecedorLicitacaoVO.getEmail());
		contatoFornecedor.setIndRecEmailEdital(Boolean.FALSE);
		return contatoFornecedor;
	}

	private ScoFornecedor criarFornecedor(
			PropostaLicitacaoVO fornecedorLicitacaoVO) {
		ScoFornecedor fornecedorNovo = new ScoFornecedor();
		fornecedorNovo.setTipoFornecedor(DominioTipoFornecedor.FNA);
		fornecedorNovo.setRazaoSocial(fornecedorLicitacaoVO.getNomeTipo7());
		fornecedorNovo.setNomeFantasia(fornecedorLicitacaoVO.getNomeTipo7());
		fornecedorNovo.setDtCadastramento(Calendar.getInstance().getTime());
		fornecedorNovo.setSituacao(DominioSituacao.A);
		fornecedorNovo.setBairro(fornecedorLicitacaoVO.getBairroTipo7());
		fornecedorNovo.setCgc(Long.parseLong(fornecedorLicitacaoVO.getCnpjTipo7()));
		fornecedorNovo.setServidor(servidorLogadoFacade.obterServidorLogado());
		return fornecedorNovo;
	}
	
	private Boolean verificarHomologada(List<FasesLicitacaoVO> fasesLicitacao){
		for (FasesLicitacaoVO fase: fasesLicitacao){
			if (fase.getEstadoLicitacao().contains("HOMOLOGADA")){
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	public void validarSelecaoPregaoEletronico(List<ScoLicitacaoVO> licitacoesSelecionadas) throws ApplicationBusinessException{
		//verifica se foi selecionado mais de uma licitacao ou nenhuma na tela
		if (licitacoesSelecionadas == null || licitacoesSelecionadas.isEmpty() || licitacoesSelecionadas.size() != 1) {
			throw new ApplicationBusinessException(PregaoBancoBrasilRNExceptionCode.SELECIONE_UM_PAC);
		}
		//verifica se a coluna Nome Arq. Retorno está vazia
		if (licitacoesSelecionadas.get(0).getNomeArqRetorno() == null) {
			throw new ApplicationBusinessException(PregaoBancoBrasilRNExceptionCode.SEM_ARQUIVO_VINCULADO);
		}
	}
}