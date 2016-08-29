package br.gov.mec.aghu.compras.pac.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPgtoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerAvaliacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerOcorrenciaDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoUnidadeMedidaDAO;
import br.gov.mec.aghu.compras.vo.PropFornecAvalParecerVO;
import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FcpMoeda;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.suprimentos.vo.ScoFaseSolicitacaoVO;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPropostaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class ScoItemPropostaFornecedorON extends BaseBusiness {


	@EJB
	private DadosItemLicitacaoON dadosItemLicitacaoON;
		
	@EJB
	private ScoItemPropostaFornecedorRN scoItemPropostaFornecedorRN;
	
	private static final Log LOG = LogFactory.getLog(ScoItemPropostaFornecedorON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private ScoCondicaoPagamentoProposDAO scoCondicaoPagamentoProposDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ScoUnidadeMedidaDAO scoUnidadeMedidaDAO;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	
	@Inject
	private ScoCondicaoPgtoLicitacaoDAO scoCondicaoPgtoLicitacaoDAO;
	
	@Inject
	private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;
	
	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private ScoParecerAvaliacaoDAO scoParecerAvaliacaoDAO;
	
	@Inject
	private ScoParecerMaterialDAO scoParecerMaterialDAO;
	
	@Inject
	private ScoParecerOcorrenciaDAO scoParecerOcorrenciaDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	private static final long serialVersionUID = -893064524258218268L;

	public enum ScoItemPropostaFornecedorONExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_ITEMPROPOSTA_MSG001_INCLUSAO,MENSAGEM_ITEMPROPOSTA_MSG001_ALTERACAO,
		MENSAGEM_ITEMPROPOSTA_MSG011, MENSAGEM_JULGAMENTO_MSG008, MENSAGEM_ITEMPROPOSTA_MSG007a,
		MENSAGEM_JULGAMENTO_MSG006, MENSAGEM_ITEMPROPOSTA_MSG015, MENSAGEM_ITEMPROPOSTA_MSG016,
		MENSAGEM_ITEMPROPOSTA_MSG017;
	}
	
	/**
	 * Insere, altera ou exclui uma lista de itens de propostas conforme VO preenchido
	 * @param listaItensPropostas
	 * @param listaItensPropostasExclusao
	 * @param propostaEmEdicao
	 * @throws ApplicationBusinessException
	 */
	public void gravarItemProposta(List<ScoItemPropostaVO> listaItensPropostas, 
			List<ScoItemPropostaVO> listaItensPropostasExclusao, Boolean propostaEmEdicao) throws ApplicationBusinessException {
		// RN08
		if (listaItensPropostas == null || listaItensPropostas.size() == 0) {
			if (propostaEmEdicao == Boolean.FALSE) {
				throw new ApplicationBusinessException(
						ScoItemPropostaFornecedorONExceptionCode.MENSAGEM_ITEMPROPOSTA_MSG001_INCLUSAO);								
			} else {
				throw new ApplicationBusinessException(
						ScoItemPropostaFornecedorONExceptionCode.MENSAGEM_ITEMPROPOSTA_MSG001_ALTERACAO);
			}
		}
		
		if (listaItensPropostasExclusao != null && listaItensPropostasExclusao.size() > 0) {
			this.excluirItemProposta(listaItensPropostasExclusao);		
		}
		
		this.atualizarItemProposta(listaItensPropostas, propostaEmEdicao);
	}
	
	/**
	 * Retorna o próximo número de um item de proposta de fornecedor considerando o que está no banco 
	 * e o que está na tela e ainda não foi persistido
	 * @param listaItensPropostas
	 * @param listaItensPropostasExclusao
	 * @param numeroPac
	 * @param numeroFornecedor
	 * @return Short
	 */
	public Short obterProximoNumeroItemPropostaFornecedor(List<ScoItemPropostaVO> listaItensPropostas, 
			List<ScoItemPropostaVO> listaItensPropostasExclusao, Integer numeroPac, Integer numeroFornecedor) { 
		Short ret = 0;
		for (ScoItemPropostaVO item : listaItensPropostas) {
			if (item.getNumeroItemProposta() > ret) {
				ret = item.getNumeroItemProposta();
			}
		}
		
		if (listaItensPropostasExclusao != null) {
			for (ScoItemPropostaVO item : listaItensPropostasExclusao) {
				if (item.getNumeroItemProposta() > ret) {
					ret = item.getNumeroItemProposta();
				}
			}
		}
		
		Short maxBanco = getScoItemPropostaFornecedorDAO().obterMaxNumeroItemPropostaFornecedor(numeroFornecedor, numeroPac, false);		
		if (maxBanco > ret) {
			ret = maxBanco;
		}
		
		return ++ret;	
	}
		
	/**
	 * Retorna se a unidade de medida utilizada na solicitação de compras base com a unidade de medida proposta
	 * pelo fornecedor na proposta conforme o fator de conversão informado
	 * @param faseSolicitacao
	 * @param fatorConversao
	 * @param embalagem
	 * @return Boolean
	 */
	public Boolean validarFatorConversao(ScoFaseSolicitacaoVO faseSolicitacao, Integer fatorConversao, ScoUnidadeMedida embalagem) {
		Boolean ret = Boolean.FALSE;
		ScoSolicitacaoDeCompra sc = null;
		if (faseSolicitacao.getSolicitacaoCompra() != null){
		    sc = this.comprasFacade.obterScoSolicitacaoDeCompraPorChavePrimaria(faseSolicitacao.getSolicitacaoCompra().getNumero());
		    if (sc != null && sc.getUnidadeMedida() != null){
		    	sc.setUnidadeMedida(this.comprasFacade.obterUnidadeMedidaPorId(sc.getUnidadeMedida().getCodigo()));
		    }
		}
		
		if (sc != null &&
				sc.getUnidadeMedida() != null && embalagem != null) {
			if (!sc.getUnidadeMedida().equals(embalagem) &&	fatorConversao <= 1) {
				ret = Boolean.TRUE;
			}
		}
				
		return ret;		
	}
	
	/**
	 * Valida se o fornecedor já fez alguma proposta para o mesmo item de licitação com a mesma marca comercial
	 * @param listaItensPropostas
	 * @param faseSolicitacao
	 * @param marcaComercial
	 * @return Boolean
	 * @throws ApplicationBusinessException 
	 */
	public void validarInsercaoItemPropostaDuplicado(List<ScoItemPropostaVO> listaItensPropostas, ScoFaseSolicitacaoVO faseSolicitacao, 
			ScoMarcaComercial marcaComercial, ScoFornecedor scoFornecedor) throws ApplicationBusinessException {
		
		
		AghParametros parametroPropostaMesmoItemFornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PROPOSTA_MESMO_ITEM_FORNECEDOR);
		
		for (ScoItemPropostaVO item : listaItensPropostas) {
			
			if (DominioSimNao.N.toString().equals(parametroPropostaMesmoItemFornecedor.getVlrTexto())){				
				if (item.getNumeroItemPac().equals(faseSolicitacao.getItemLicitacao().getId().getNumero()) &&
						item.getFornecedorProposta().equals(scoFornecedor)) {					
					throw new ApplicationBusinessException(
							ScoItemPropostaFornecedorONExceptionCode.MENSAGEM_ITEMPROPOSTA_MSG016);
				}
			}
			else {
				if (item.getNumeroItemPac().equals(faseSolicitacao.getItemLicitacao().getId().getNumero()) &&
						item.getMarcaComercial().equals(marcaComercial)) {
					throw new ApplicationBusinessException(
							ScoItemPropostaFornecedorONExceptionCode.MENSAGEM_ITEMPROPOSTA_MSG015);
				}
			}
		}
	}
	
	/**
	 * Verifica se a quantidade informada no item de proposta do fornecedor é iguao a da da solicitação, basedado no fator de conversão
	 * informado na tela 
	 * @param qtdItemProposta
	 * @param fatorConversao
	 * @param qtdItemSolicitacao
	 * @return Boolean
	 */
	public Boolean validarQuantidadePropostaDiferenteSolicitacao(Long qtdItemProposta, Integer fatorConversao, Integer qtdItemSolicitacao) {
		return (qtdItemProposta * fatorConversao) != qtdItemSolicitacao;	
	}
	
	/**
	 * Valida se o fornecedor já fez alguma proposta para o mesmo item de licitação com a mesma marca comercial
	 * @param listaItensPropostas
	 * @param faseSolicitacao
	 * @param marcaComercial
	 * @return Boolean
	 * @throws ApplicationBusinessException 
	 */
	public void validarInsercaoItemPropostaValorUnitario(BigDecimal valorUnitarioItemProposta) throws ApplicationBusinessException {
		
		if (!(valorUnitarioItemProposta.compareTo(BigDecimal.ZERO) == 1)){
			throw new ApplicationBusinessException(
					ScoItemPropostaFornecedorONExceptionCode.MENSAGEM_ITEMPROPOSTA_MSG017);
		}
	}
	
	/**
	 * Exclui uma lista de itens de propostas
	 * @param listaItensPropostasExclusao
	 * @throws ApplicationBusinessException 
	 */
	private void excluirItemProposta(List<ScoItemPropostaVO> listaItensPropostasExclusao) throws ApplicationBusinessException {
		for (ScoItemPropostaVO itemVO : listaItensPropostasExclusao) {
			if (itemVO.getTipoOperacao() != DominioOperacaoBanco.INS) {
				ScoItemPropostaFornecedorId idItem = new ScoItemPropostaFornecedorId(
						itemVO.getNumeroPac(), itemVO.getFornecedorProposta().getNumero(),itemVO.getNumeroItemProposta());
				ScoItemPropostaFornecedor itemProposta = this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(idItem);
				
				if (itemProposta != null) {
					this.getScoItemPropostaFornecedorRN().removerItemPropostaFornecedor(itemProposta);
				}
			}
		}
	}
	
	/**
	 * Insere ou altera uma lista de itens de propostas
	 * @param listaItensPropostas
	 * @param propostaEmEdicao
	 * @throws ApplicationBusinessException
	 */
	private void atualizarItemProposta(List<ScoItemPropostaVO> listaItensPropostas, Boolean propostaEmEdicao) throws ApplicationBusinessException {
		for (ScoItemPropostaVO itemVO : listaItensPropostas) {
			
			ScoItemPropostaFornecedor itemProposta = null;
			
			if (itemVO.getTipoOperacao() == DominioOperacaoBanco.UPD || itemVO.getTipoOperacao() == null) {			
				ScoItemPropostaFornecedorId idItem = new ScoItemPropostaFornecedorId(
						itemVO.getNumeroPac(), itemVO.getFornecedorProposta().getNumero(),itemVO.getNumeroItemProposta());
				itemProposta = this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(idItem);	
				
				// RN14
				if (itemProposta != null && (itemProposta.getValorUnitario().compareTo(itemVO.getValorUnitarioItemProposta()) != 0) &&
						this.getScoParcelasPagamentoDAO().verificarPropostaPossuiCondicaoPorValor(
								itemProposta.getId().getPfrFrnNumero(), itemProposta.getId().getPfrLctNumero(), itemProposta.getId().getNumero())) {
					throw new ApplicationBusinessException(
							ScoItemPropostaFornecedorONExceptionCode.MENSAGEM_ITEMPROPOSTA_MSG007a);
				}
			} else if (itemVO.getTipoOperacao() == DominioOperacaoBanco.INS) {
				ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().
						obterItemLicitacaoPorNumeroLicitacaoENumeroItem(itemVO.getNumeroPac(), Short.valueOf(itemVO.getNumeroItemPac()));								
				
				ScoPropostaFornecedor propostaFornecedor = this.getScoPropostaFornecedorDAO().
						obterPorChavePrimaria(new ScoPropostaFornecedorId(itemVO.getNumeroPac(), itemVO.getFornecedorProposta().getNumero()));
				
				ScoItemPropostaFornecedorId idItem = new ScoItemPropostaFornecedorId(
						itemVO.getNumeroPac(), itemVO.getFornecedorProposta().getNumero(),itemVO.getNumeroItemProposta());
				
				itemProposta = new ScoItemPropostaFornecedor();
				itemProposta.setId(idItem);
				itemProposta.setIndExclusao(Boolean.FALSE);
				itemProposta.setDtExclusao(null);
				itemProposta.setIndAnalisadoPt(Boolean.FALSE);
				itemProposta.setIndAutorizUsr(Boolean.FALSE);
				itemProposta.setIndEscolhido(Boolean.FALSE);
				itemProposta.setIndComDesconto(Boolean.FALSE);
				itemProposta.setIndDesclassificado(Boolean.FALSE);
				
				if (itemLicitacao != null) {
					itemProposta.setItemLicitacao(itemLicitacao);
				}
				
				if (propostaFornecedor != null) {
					itemProposta.setPropostaFornecedor(propostaFornecedor);
				}
			}
			
			if (itemProposta != null) {
				if (itemVO.getApresentacao() != null) {
					itemProposta.setApresentacao(itemVO.getApresentacao());
				}
				if (itemVO.getCodigoMaterialFornecedor() != null) {
					itemProposta.setCodigoItemFornecedor(itemVO.getCodigoMaterialFornecedor());
				}
				itemProposta.setFatorConversao(itemVO.getFatorConversao());
				itemProposta.setIndNacional(itemVO.getIndNacional());
				itemProposta.setMarcaComercial(itemVO.getMarcaComercial());
				itemProposta.setMoeda(itemVO.getMoedaItemProposta());
				if (itemVO.getModeloComercial() != null) {
					itemProposta.setModeloComercial(itemVO.getModeloComercial());
				}
				if (itemVO.getNumeroOrcamento() != null) {
					itemProposta.setNroOrcamento(itemVO.getNumeroOrcamento());
				}
				if (itemVO.getObservacao() != null) {
					itemProposta.setObservacao(itemVO.getObservacao());
				}
				itemProposta.setQuantidade(itemVO.getQtdItemProposta());
				itemProposta.setUnidadeMedida(itemVO.getUnidadeProposta());
				itemProposta.setValorUnitario(itemVO.getValorUnitarioItemProposta());
	
				if (this.getScoItemLicitacaoDAO().verificarParecerMaterialDesfavoravelPorNumeroEItemLicitacao(itemVO)) {
					itemProposta.setIndDesclassificado(Boolean.TRUE);
					
					try {
						itemProposta.setMotDesclassif(DominioMotivoDesclassificacaoItemProposta.valueOf(obterParametroParecerDesfavoravel()));
					} catch (ApplicationBusinessException e) {
						itemProposta.setMotDesclassif(DominioMotivoDesclassificacaoItemProposta.PD);
					}
				}
				this.persistirItemProposta(itemProposta);
			} else {
				throw new ApplicationBusinessException(
						ScoItemPropostaFornecedorONExceptionCode.MENSAGEM_ITEMPROPOSTA_MSG011);	
			}
		}
	}
	
	private void persistirItemProposta(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		if (!this.getScoItemAutorizacaoFornDAO().verificarItemPropostaFornecedorEmAf(
				itemProposta.getId().getPfrLctNumero(), itemProposta.getId().getNumero(), 
				this.getComprasFacade().obterFornecedorPorNumero(itemProposta.getId().getPfrFrnNumero()))) {
			this.getScoItemPropostaFornecedorRN().atualizarItemPropostaFornecedor(itemProposta);
		}
	}

	/**
	 * Retorna o valor total da proposta do fornecedor
	 * @param propostaFornecedor
	 * @return BigDecimal
	 */
	public BigDecimal obterValorTotalProposta(ScoPropostaFornecedor propostaFornecedor){
		BigDecimal valorTotal = BigDecimal.ZERO; 
		
		List<ScoItemPropostaVO> listaItens = this.pesquisarItemPropostaPorNumeroLicitacao(propostaFornecedor.getId().getLctNumero(), propostaFornecedor.getFornecedor()); 
		
		for (ScoItemPropostaVO item : listaItens) {
			if (item.getQtdItemProposta() != null) {
				valorTotal = valorTotal.add(item.getValorUnitarioItemProposta().multiply(new BigDecimal(item.getQtdItemProposta())));
			}
		}
		
		return valorTotal;
	}

	/**
	 * Retorna o valor total de um determinado item de proposta
	 * @param itemProposta
	 * @return BigDecimal
	 */
	public BigDecimal obterValorTotalItemProposta(ScoItemPropostaFornecedor itemProposta){
		BigDecimal valorTotal = BigDecimal.ZERO; 
		
		if (itemProposta.getQuantidade() != null && itemProposta.getValorUnitario() != null) {
			valorTotal = itemProposta.getValorUnitario().multiply(new BigDecimal(itemProposta.getQuantidade()));
		}
		
		return valorTotal;
	}

	/**
	 * Retorna a moeda padrão que deve ser utilizada no cadastro de propostas de fornecedores, conforme parâmetro do banco P_MOEDA_CORRENTE
	 * @return FcpMoeda
	 * @throws ApplicationBusinessException
	 */
	public FcpMoeda obterMoedaPadraoItemProposta() throws ApplicationBusinessException {
		return this.getEstoqueFacade().obterMoedaPorChavePrimaria(this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_MOEDA_CORRENTE)
				.getVlrNumerico().shortValue());
	}

	/**
	 * Retorna o parâmetro de P_PARECER_DESFAVORAVEL
	 * @return String
	 * @throws ApplicationBusinessException
	 */
	public String obterParametroParecerDesfavoravel() throws ApplicationBusinessException {
		return this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_PARECER_DESFAVORAVEL)
				.getVlrTexto();
	}

	/**
	 * Valida para determinada item de proposta do fornecedor se ele está habilitado a ser julgado sem parecer técnico (IND_AUT_USR)
	 * ou se o hospital não utiliza parecer técnico (P_UTILIZA_PARECER_TECNICO)
	 * @param itemProposta
	 * @throws ApplicationBusinessException
	 */
	public void validarLiberacaoParecerTecnico(ScoItemPropostaFornecedor itemProposta) throws ApplicationBusinessException {
		
		ScoItemLicitacao scoItemLicitacao = itemProposta.getItemLicitacao();
		Boolean isSolicitacaoServico = Boolean.FALSE;
		
		if (scoItemLicitacao != null){
			List<ScoFaseSolicitacao> listaScoFaseSolicitacao = new ArrayList<ScoFaseSolicitacao>(scoItemLicitacao.getFasesSolicitacao());
			
			if (listaScoFaseSolicitacao != null && !listaScoFaseSolicitacao.isEmpty()){
				isSolicitacaoServico = (listaScoFaseSolicitacao.get(0).getSolicitacaoServico() != null);
			}			
		}
		
		if (!isSolicitacaoServico) {
			if (verificarUtilizaParecerTecnico()) {			
				if (!this.obterParecerTecnicoItemProposta(itemProposta).equals(DominioParecer.PF.getDescricao())  
						&& itemProposta.getIndAutorizUsr() == Boolean.FALSE) {
					throw new ApplicationBusinessException(
							ScoItemPropostaFornecedorONExceptionCode.MENSAGEM_JULGAMENTO_MSG008);	
				}
			}
		}
	}
	
	/**
	 * Verifica se a instituicao utiliza parecer tecnico
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarUtilizaParecerTecnico() throws ApplicationBusinessException {
		Boolean ret = Boolean.FALSE;
		
		AghParametros utilizaParecerTecnico = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_UTILIZA_PARECER_TECNICO);
		
		if (utilizaParecerTecnico != null && utilizaParecerTecnico.getVlrTexto().equalsIgnoreCase(DominioSimNao.S.getDescricao().toUpperCase())) {
			ret = Boolean.TRUE;
		}
		
		return ret;
	}
	
	/**
	 * Retorna uma string contendo o parecer técnico de determinado item de proposta, baseado em sua marca e/ou marca/modelo
	 * @param item
	 * @return String
	 */
	public String obterParecerTecnicoItemProposta(ScoItemPropostaFornecedor item) {
		String ret = "";
		ScoMaterial material = this.getDadosItemLicitacaoON().obterMaterialItemLicitacao(item.getItemLicitacao()); 
		if (material != null) {
			if (item.getModeloComercial() != null) {
				ret = this.getScoParecerOcorrenciaDAO().obterParecerOcorrenciaMarcaModeloItemProposta(item, material, false);
			
				if (StringUtils.isBlank(ret)) {
					ret = this.getScoParecerAvaliacaoDAO().obterParecerAvaliacaoMarcaModeloItemProposta(item, material, false);
				}
			} else {
				ret = this.getScoParecerOcorrenciaDAO().obterParecerOcorrenciaMarcaModeloItemProposta(item, material, true);
				
				if (StringUtils.isBlank(ret)) {
					ret = this.getScoParecerAvaliacaoDAO().obterParecerAvaliacaoMarcaModeloItemProposta(item, material, true);
				}
			} 
		} 
		return (StringUtils.isBlank(ret) ? "Sem Parecer" : ret);
	}
	
	/**
	 * Copia já persistindo no banco as condições de pagamento da licitação (PAC) para proposta passada como parâmetro
	 * @param pfrLctNumero
	 * @param pfrFrnNumero
	 * @param numeroItem
	 * @param numeroItemLicitacao
	 * @throws ApplicationBusinessException 
	 */
	public void copiarCondicaoPagamentoLicitacao(Integer pfrLctNumero, Integer pfrFrnNumero, Short numeroItem, Short numeroItemLicitacao) throws ApplicationBusinessException {
		List<ScoCondicaoPgtoLicitacao> listaCondicoesLicitacao = 
				this.getScoCondicaoPgtoLicitacaoDAO().pesquisarCondicaoPagamentoLicitacao(pfrLctNumero, numeroItemLicitacao, true);
		
		if (listaCondicoesLicitacao != null && listaCondicoesLicitacao.size() > 0) {
			for (ScoCondicaoPgtoLicitacao item : listaCondicoesLicitacao) {
				Set<ScoParcelasPagamento> setParcelas = this.getScoParcelasPagamentoDAO().pesquisarParcelasPgtoPac(item.getSeq()); 
				
				ScoCondicaoPagamentoPropos condicao = new ScoCondicaoPagamentoPropos();
				condicao.setFormaPagamento(item.getFormaPagamento());
				condicao.setIndCondEscolhida(Boolean.FALSE);
				condicao.setPercAcrescimo(item.getPercAcrescimo());
				condicao.setPercDesconto(item.getPercDesconto());
				
				if (numeroItem == null) {
					condicao.setPropostaFornecedor(this.getScoPropostaFornecedorDAO().obterPorChavePrimaria(new ScoPropostaFornecedorId(pfrLctNumero, pfrFrnNumero)));
				} else {
					condicao.setItemPropostaFornecedor(this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(new ScoItemPropostaFornecedorId(pfrLctNumero,pfrFrnNumero,numeroItem)));
				}
				
				this.getScoCondicaoPagamentoProposDAO().persistir(condicao);
				
				for (ScoParcelasPagamento parcelaLct : setParcelas) {
					ScoParcelasPagamento parcelaProp = new ScoParcelasPagamento();
					parcelaProp.setCondicaoPagamentoPropos(condicao);
					parcelaProp.setParcela(parcelaLct.getParcela());
					parcelaProp.setPercPagamento(parcelaLct.getPercPagamento());
					parcelaProp.setPrazo(parcelaLct.getPrazo());
					parcelaProp.setValorPagamento(parcelaLct.getValorPagamento());
					this.getComprasFacade().persistirParcelaPagamento(parcelaProp);					
				}
			}
		}
	}
	
	public PropFornecAvalParecerVO obterParecerMaterialSituacaoItemProposta(ScoItemPropostaFornecedor item) {
		
		ScoMaterial material = this.getDadosItemLicitacaoON().obterMaterialItemLicitacao(item.getItemLicitacao());
		PropFornecAvalParecerVO propostaFornecedorAvalicaoParecerVO = new PropFornecAvalParecerVO();
		if (material != null) {
			if (item.getModeloComercial() != null) {
				List<ScoParecerOcorrencia> listaParecerOcorrencia = this.getScoParecerOcorrenciaDAO().pesquisarParecerOcorrenciaItemProposta(item, material, true);
				ScoParecerOcorrencia scoParecerOcorrencia = null;
				
				if (listaParecerOcorrencia != null && !listaParecerOcorrencia.isEmpty()) {
					scoParecerOcorrencia = listaParecerOcorrencia.get(0);
					propostaFornecedorAvalicaoParecerVO.setSituacaoParecerDescricao(scoParecerOcorrencia.getParecerOcorrencia().getDescricao());
					propostaFornecedorAvalicaoParecerVO.setCodigoParecerMaterial(scoParecerOcorrencia.getParecerMaterial().getCodigo());
					
				}
				
				if(scoParecerOcorrencia == null){
					List<ScoParecerAvaliacao> listaParecerAvaliacao = this.getScoParecerAvaliacaoDAO().pesquisarParecerAvaliacaoItemProposta(item, material, true);
					ScoParecerAvaliacao scoParecerAvaliacao =null;	
					
					if (listaParecerAvaliacao != null && !listaParecerAvaliacao.isEmpty()) {
					    scoParecerAvaliacao = listaParecerAvaliacao.get(0);
					    propostaFornecedorAvalicaoParecerVO.setSituacaoParecerDescricao(scoParecerAvaliacao.getParecerGeral().getDescricao());
					    propostaFornecedorAvalicaoParecerVO.setCodigoParecerMaterial(scoParecerAvaliacao.getParecerMaterial().getCodigo());
					}
				}	
				
			} else {
				
				List<ScoParecerOcorrencia> listaParecerOcorrencia = this.getScoParecerOcorrenciaDAO().pesquisarParecerOcorrenciaItemProposta(item, material, false);
				ScoParecerOcorrencia scoParecerOcorrencia= null;
				
				if (listaParecerOcorrencia != null && !listaParecerOcorrencia.isEmpty()) {
					scoParecerOcorrencia = listaParecerOcorrencia.get(0);
					propostaFornecedorAvalicaoParecerVO.setSituacaoParecerDescricao(scoParecerOcorrencia.getParecerOcorrencia().getDescricao());
					propostaFornecedorAvalicaoParecerVO.setCodigoParecerMaterial(scoParecerOcorrencia.getParecerMaterial().getCodigo());
					
				}
				
				if(scoParecerOcorrencia == null){
					List<ScoParecerAvaliacao> listaParecerAvaliacao = this.getScoParecerAvaliacaoDAO().pesquisarParecerAvaliacaoItemProposta(item, material, false);
					ScoParecerAvaliacao scoParecerAvaliacao= null;	
					if (listaParecerAvaliacao != null && !listaParecerAvaliacao.isEmpty()) {
					    scoParecerAvaliacao = listaParecerAvaliacao.get(0);
					    propostaFornecedorAvalicaoParecerVO.setSituacaoParecerDescricao(scoParecerAvaliacao.getParecerGeral().getDescricao());
					    propostaFornecedorAvalicaoParecerVO.setCodigoParecerMaterial(scoParecerAvaliacao.getParecerMaterial().getCodigo());
					}
				}				
			} 
		} 
		
		if (StringUtils.isBlank(propostaFornecedorAvalicaoParecerVO.getSituacaoParecerDescricao())){
			
			ScoParecerMaterial scoParecerMaterialPesquisa = new ScoParecerMaterial();
			scoParecerMaterialPesquisa.setMaterial(material);
			scoParecerMaterialPesquisa.setMarcaComercial(item.getMarcaComercial());
			scoParecerMaterialPesquisa.setScoMarcaModelo(item.getModeloComercial());
			
			ScoParecerMaterial  scoParecerMaterialRetorno = this.getScoParecerMaterialDAO().obterParecerTecnicoDuplicidade(scoParecerMaterialPesquisa);
			
			propostaFornecedorAvalicaoParecerVO.setSituacaoParecerDescricao("Sem Parecer");
			
			if (scoParecerMaterialRetorno != null){
			    propostaFornecedorAvalicaoParecerVO.setCodigoParecerMaterial(scoParecerMaterialRetorno.getCodigo());
			}
			else {
				propostaFornecedorAvalicaoParecerVO.setCodigoParecerMaterial(null);
			}
		}
		
		
		return propostaFornecedorAvalicaoParecerVO;
	}
	
	protected ScoCondicaoPgtoLicitacaoDAO getScoCondicaoPgtoLicitacaoDAO() {
		return scoCondicaoPgtoLicitacaoDAO;
	}
	
	/**
	 * Retorna a unidade de medida "default" das solicitações de serviço
	 * @return ScoUnidadeMedida
	 */
	public ScoUnidadeMedida obterUnidadeMedidaSs() {
		return this.getScoUnidadeMedidaDAO().obterPorChavePrimaria("UN");
	}
	
	public List<ScoItemPropostaVO> pesquisarItemPropostaPorNumeroLicitacao(Integer numeroPac, ScoFornecedor fornecedor) {
		return this.getScoItemPropostaFornecedorDAO().pesquisarItemPropostaPorNumeroLicitacao(numeroPac, fornecedor, false, null);
	}
	
	public List<ScoFaseSolicitacaoVO> pesquisarItemLicitacao(Object param, Integer numeroPac) {	
		return this.getScoFaseSolicitacaoDAO().pesquisarItemLicitacao(param, numeroPac);
	}
	
	public List<ScoFaseSolicitacaoVO> pesquisarItemLicitacao(Object param, Integer numeroPac, List<ScoItemPropostaVO> listaItensPropostas) throws ApplicationBusinessException {	
		List<ScoFaseSolicitacaoVO>  listaScoFaseSolicitacaoVO = this.getScoFaseSolicitacaoDAO().pesquisarItemLicitacao(param, numeroPac);
		
        AghParametros parametroPropostaMesmoItemFornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PROPOSTA_MESMO_ITEM_FORNECEDOR);
				
		if (DominioSimNao.N.toString().equals(parametroPropostaMesmoItemFornecedor.getVlrTexto())){
			Iterator<ScoFaseSolicitacaoVO> iteratorFaseSolicitacaoVO = listaScoFaseSolicitacaoVO.iterator();
			while (iteratorFaseSolicitacaoVO.hasNext()) {
				ScoFaseSolicitacaoVO scoFaseSolicitacaoVO = iteratorFaseSolicitacaoVO.next(); 
			
				for (ScoItemPropostaVO scoItemPropostaVO:listaItensPropostas){
					
					if (scoItemPropostaVO.getNumeroItemPac().equals(scoFaseSolicitacaoVO.getItemLicitacao().getId().getNumero())){
						iteratorFaseSolicitacaoVO.remove();
					}
					
				}
			}
		}
		
		return listaScoFaseSolicitacaoVO;
	}
	
	private ScoUnidadeMedidaDAO getScoUnidadeMedidaDAO() {
		return scoUnidadeMedidaDAO;
	}
	
	private ScoParecerAvaliacaoDAO getScoParecerAvaliacaoDAO() {
		return scoParecerAvaliacaoDAO;
	}
	
	private ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}
	
	private ScoParecerOcorrenciaDAO getScoParecerOcorrenciaDAO() {
		return scoParecerOcorrenciaDAO;
	}
	
	private ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}
	
	private ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	private DadosItemLicitacaoON getDadosItemLicitacaoON() {
		return dadosItemLicitacaoON;
	}
	
	private ScoPropostaFornecedorDAO getScoPropostaFornecedorDAO() {
		return scoPropostaFornecedorDAO;
	}
	
	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	private IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
	private ScoCondicaoPagamentoProposDAO getScoCondicaoPagamentoProposDAO() {
		return scoCondicaoPagamentoProposDAO;
	}

	private ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	private IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

	private IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	private ScoParecerMaterialDAO getScoParecerMaterialDAO() {
		return scoParecerMaterialDAO;
	}
	
	private ScoItemPropostaFornecedorRN getScoItemPropostaFornecedorRN() {
		return scoItemPropostaFornecedorRN;
	}
}