package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.ConsultaRecebimentoMaterialServicoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PriorizaEntregaVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RecebimentoMaterialServicoVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornecedorPedidoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoRecebParcelasAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRecbXProgrEntrega;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceItemRecebProvisorioId;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedidoId;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;

@Stateless
public class RecebeMaterialServicoON  extends BaseBusiness {


@EJB
private AutFornecimentoON autFornecimentoON;

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;
	
	@Inject
	private ScoAutorizacaoFornecedorPedidoDAO scoAutorizacaoFornecedorPedidoDAO;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	@Inject 
	private ScoRecebParcelasAutorizacaoFornDAO scoRecebParcelasAutorizacaoFornDAO;
	
	//@EJB
	//private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;
	
	//@EJB
	//private ScoProgEntregaItemAutorizacaoFornecimentoRN scoProgEntregaItemAutorizacaoFornecimentoRN;
	
	private static final Log LOG = LogFactory.getLog(RecebeMaterialServicoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private EmailUtil emailUtil;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private RecebeMaterialServicoValorExcedidoON recebeMaterialServicoValorExcedidoON;

	/**
	 * 
	 */
	private static final long serialVersionUID = 44626191057942476L;
	
	public enum RecebimentoItemAFONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_OBRIGATORIO_INFORMAR_NOTA_FISCAL_ENTRADA,
		MENSAGEM_NAO_PERMITIDO_ENTRADA_NOTA_FISCAL,
		MENSAGEM_AFE_PEDIDO_NAO_ENCONTRADA,
		MENSAGEM_VALOR_PERCENTUAL_EXCEDIDO,
		MENSAGEM_CRITICA_SOMA_RECEBIMENTOS,
		MENSAGEM_CADASTRAR_MATRICULA_VINCULO_CHEFE_ALMOX,
		MENSAGEM_MUDANCA_VALOR_UNITARIO_ITEM_AF,
		MENSAGEM_CRITICA_FORNECEDOR_DIF_NF,
		MENSAGEM_CRITICA_NF_SEM_VALOR;
	}
		 
	public SceNotaRecebProvisorio receberParcelaItensAF(
			List<RecebimentoMaterialServicoVO> listaItemAF,
			SceDocumentoFiscalEntrada documentoFiscalEntrada,
			RapServidores servidorLogado, ScoFornecedor fornecedor)
			throws BaseException, ItemRecebimentoValorExcedido {
		return receberParcelaItensAF(listaItemAF, documentoFiscalEntrada,
				servidorLogado, fornecedor, false);
	}
	 
	/**
	* Registra a entrega dos itens de parcelas de recebimento gerando e retornando uma nota de recebimento provisório. 
	* Insere nas tabelas SceNotaRecebProvisorio, SceItemRecebProvisorio, SceItemRecbXProgrEntrega 
	* Atualiza as tabelas ScoSolicitacaoProgramacaoEntrega, ScoProgEntregaItemAutorizacaoFornecimento e ScoItemAutorizacaoForn
	* 
	* @param listaItemAF
	* @param documentoFiscalEntrada
	* @param servidorLogado
	* @param force 
	* @return SceNotaRecebProvisorio
	* @throws MECBaseException
	* @throws ItemRecebimentoValorExcedido Itens de recebimento com valores excedidos se force = false. 
	*/		
	public SceNotaRecebProvisorio receberParcelaItensAF(
			List<RecebimentoMaterialServicoVO> listaItemAF,
			SceDocumentoFiscalEntrada documentoFiscalEntrada,
			RapServidores servidorLogado, ScoFornecedor fornecedor,
			boolean force) throws BaseException, ItemRecebimentoValorExcedido {
		verificarMudancaValorUnitarioItemAf(listaItemAF);
		validarObrigatoriedadeNotaFiscalEntrada(documentoFiscalEntrada);
		criticarValorNotaFiscal(listaItemAF, documentoFiscalEntrada, fornecedor);
		validarCadastroParametrosSistema();
		SceNotaRecebProvisorio notaRecebimentoProvisorio = null;
		Integer nroItem = 1;
		ItemRecebimentoValorExcedido status = new ItemRecebimentoValorExcedido();
	
		for (RecebimentoMaterialServicoVO itemAF : listaItemAF) {
			if (getRecebeMaterialServicoValorExcedidoON().possuiEntrega(itemAF)) {
				try {
					getRecebeMaterialServicoValorExcedidoON()
						.validarValorUnitarioExcedentePermitido(itemAF, servidorLogado, force);
				} catch (ItemRecebimentoValorExcedido e) {
					status.add(e);
				}
			}
		}
	
		status.throwIfExcedeu();
	
		for (RecebimentoMaterialServicoVO itemAF : listaItemAF) {
			if (getRecebeMaterialServicoValorExcedidoON().possuiEntrega(itemAF)) {				
				ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAF = obterProgEntregaItemAF(
						itemAF.getNumeroAf(), itemAF.getNumeroItemAf(),
						itemAF.getSeqParcela(), itemAF.getParcela());
	
				if (notaRecebimentoProvisorio == null) {
					notaRecebimentoProvisorio = atualizarNotaRecebProvisorio(
							progEntregaItemAF, documentoFiscalEntrada, servidorLogado);
				}
	
				SceItemRecebProvisorio itemRecebProvisorio = gerarItensNotaRecebProvisorio(
						progEntregaItemAF, notaRecebimentoProvisorio, itemAF, nroItem);
	
				nroItem = itemRecebProvisorio.getId().getNroItem() + 1;
	
				atualizarSolicProgrEntrega(progEntregaItemAF, itemAF,
						itemRecebProvisorio, servidorLogado);
	
				atualizarRecebimentoScoItemAutorizacaoForn(progEntregaItemAF, servidorLogado);
			}
		}
		
		return notaRecebimentoProvisorio;
	}
	
	private void verificarMudancaValorUnitarioItemAf(List<RecebimentoMaterialServicoVO> listaItemAf) throws ApplicationBusinessException {
		if (listaItemAf != null && !listaItemAf.isEmpty()) {
			if (listaItemAf.get(0).getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C)) {
				ScoItemAutorizacaoFornId chaveItemAF = new ScoItemAutorizacaoFornId();
				chaveItemAF.setAfnNumero(listaItemAf.get(0).getNumeroAf());
				chaveItemAF.setNumero(listaItemAf.get(0).getNumeroItemAf());
				ScoItemAutorizacaoForn itemAutorizacaoForn = getScoItemAutorizacaoFornDAO().obterPorChavePrimaria(chaveItemAF);
				this.getScoItemAutorizacaoFornDAO().refresh(itemAutorizacaoForn);
				
				BigDecimal valorUnitarioBanco = new BigDecimal(itemAutorizacaoForn.getValorUnitario()).setScale(4, RoundingMode.HALF_UP);
				
				if (valorUnitarioBanco.compareTo(listaItemAf.get(0).getValorUnitario().setScale(4, RoundingMode.HALF_UP)) != 0) {
					throw new ApplicationBusinessException(
							RecebimentoItemAFONExceptionCode.MENSAGEM_MUDANCA_VALOR_UNITARIO_ITEM_AF);
				}
			}
		}
	}
	
	private SceNotaRecebProvisorio atualizarNotaRecebProvisorio(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAF, SceDocumentoFiscalEntrada documentoFiscalEntrada,  RapServidores servidorLogado) throws BaseException {
		SceNotaRecebProvisorio notaRecebProvisorio = new SceNotaRecebProvisorio();
		
		if (progEntregaItemAF.getScoAfPedido() == null){
			throw new ApplicationBusinessException(
					RecebimentoItemAFONExceptionCode.MENSAGEM_AFE_PEDIDO_NAO_ENCONTRADA);							
		}
		
		ScoAutorizacaoFornecedorPedido autorizacaoFornecedorPedido = getScoAutorizacaoFornecedorPedidoDAO()
				.obterPorChavePrimaria(new ScoAutorizacaoFornecedorPedidoId(progEntregaItemAF.getScoAfPedido().getId().getAfnNumero(),
								                                            progEntregaItemAF.getScoAfPedido().getId().getNumero()));
		
		notaRecebProvisorio.setScoAfPedido(autorizacaoFornecedorPedido);
		notaRecebProvisorio.setDocumentoFiscalEntrada(documentoFiscalEntrada);
		notaRecebProvisorio.setDtGeracao(new Date());
		notaRecebProvisorio.setServidor(servidorLogado);
		notaRecebProvisorio.setIndConfirmado(Boolean.FALSE);
		notaRecebProvisorio.setIndEstorno(Boolean.FALSE);
		notaRecebProvisorio.setSeq(getEstoqueFacade().obterProximoxSeqSceNotaRecebProvisorio());
		notaRecebProvisorio.setVersion(0);
		
		this.getEstoqueFacade().atualizarNotaRecProvisorio(notaRecebProvisorio);
		
		return notaRecebProvisorio;
	}
	
	private SceItemRecebProvisorio gerarItensNotaRecebProvisorio( ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAF,
			                      SceNotaRecebProvisorio notaRecebProvisorio, RecebimentoMaterialServicoVO itemAF, Integer nroItem){
		SceItemRecebProvisorio itemRecebProvisorio = null;

		Integer quantidadeRecebida = 0;
		Double valorTotal = Double.valueOf(0);
		Double valorRecebido = Double.valueOf(0);
		
		// Gera um registro de recebimento provisório para cada parcela do item
		/*if ((itemAF.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C) && itemAF.getQtdEntregue() > itemAF.getSaldoQtd())
				|| (itemAF.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.S) && itemAF.getValorEntregue().compareTo(itemAF.getValorSaldo()) > 0)){
			// Caso o item esteja recebendo uma quantidade/valor maior que o seu saldo, então o excedente esta sendo
			// distribuído entre outras parcelas. Estas parcelas são encontradas em uma lista de priorização.
			Integer numParcela = null;
			
			if (itemAF.getListaPriorizacao().get(0) != null){
				numParcela = itemAF.getListaPriorizacao().get(0).getNumeroParcela();
			}
			
			for (PriorizaEntregaVO priorizaEntrega : itemAF.getListaPriorizacao()){
				if (numParcela.equals(priorizaEntrega.getNumeroParcela())){
					if (itemAF.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C)){
						quantidadeRecebida = quantidadeRecebida + (priorizaEntrega.getQtdeRecebidaSolicitacaoCompra() * itemAF.getFatorConversao());
						valorTotal = valorTotal + (itemAF.getValorUnitario().multiply(new BigDecimal(priorizaEntrega.getQtdeRecebidaSolicitacaoCompra())).doubleValue());
					} else {
						valorRecebido = valorRecebido + priorizaEntrega.getValorRecebidoSolicitacaoServico().doubleValue();
					}					
				} else {
					// Se a quantidade/valor recebida é menor que o saldo da parcela, então a parcela vinculada ao item de recebimento provisório é do próprio item
					itemRecebProvisorio = atualizarItemNotaRecebProvisorio(progEntregaItemAF, notaRecebProvisorio, itemAF.getTipoSolicitacao(), nroItem, quantidadeRecebida, valorTotal, valorRecebido);

					nroItem = nroItem + 1;					
					numParcela = priorizaEntrega.getNumeroParcela();
					if (itemAF.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C)){
						quantidadeRecebida = priorizaEntrega.getQtdeRecebidaSolicitacaoCompra() * itemAF.getFatorConversao();
						valorTotal = itemAF.getValorUnitario().multiply(new BigDecimal(priorizaEntrega.getQtdeRecebidaSolicitacaoCompra())).doubleValue();
					} else {
						valorRecebido = priorizaEntrega.getValorRecebidoSolicitacaoServico().doubleValue();
					}					
				}
			}			

			itemRecebProvisorio = atualizarItemNotaRecebProvisorio(progEntregaItemAF, notaRecebProvisorio, itemAF.getTipoSolicitacao(), nroItem, quantidadeRecebida, valorTotal, valorRecebido);

		}  else {*/
			if (itemAF.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C)){
				quantidadeRecebida = itemAF.getQtdEntregue() * itemAF.getFatorConversao();
				valorTotal = itemAF.getValorTotal().doubleValue();
				
			} else {
				quantidadeRecebida = 1;
				valorRecebido = itemAF.getValorEntregue().doubleValue();
			}

			itemRecebProvisorio = atualizarItemNotaRecebProvisorio(progEntregaItemAF, notaRecebProvisorio, itemAF.getTipoSolicitacao(), nroItem, quantidadeRecebida, valorTotal, valorRecebido);
		//}
		
		return itemRecebProvisorio;
	}
	
	private SceItemRecebProvisorio atualizarItemNotaRecebProvisorio( ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAF,
													SceNotaRecebProvisorio notaRecebProvisorio, DominioTipoFaseSolicitacao tipoSolicitacao,
													Integer nroItem, Integer quantidadeRecebida, Double valorTotal, Double valorRecebido) {
		SceItemRecebProvisorio itemRecebProvisorio = new SceItemRecebProvisorio();
		
		SceItemRecebProvisorioId idItemRecebProvisorio = new SceItemRecebProvisorioId();
		idItemRecebProvisorio.setNrpSeq(notaRecebProvisorio.getSeq());
		idItemRecebProvisorio.setNroItem(nroItem);		
		itemRecebProvisorio.setId(idItemRecebProvisorio);
		
		itemRecebProvisorio.setNotaRecebimentoProvisorio(notaRecebProvisorio);
		itemRecebProvisorio.setProgEntregaItemAf(progEntregaItemAF);
		
		if (tipoSolicitacao.equals(DominioTipoFaseSolicitacao.C)){
			itemRecebProvisorio.setQuantidade(quantidadeRecebida);
			itemRecebProvisorio.setValor(valorTotal);
		} else {
			itemRecebProvisorio.setQuantidade(1);
			itemRecebProvisorio.setValor(valorRecebido);
		}
						
		itemRecebProvisorio.setVersion(0);
		
		this.getEstoqueFacade().atualizarItemRecebProvisorio(itemRecebProvisorio);

		// Opção pelo flush devido à necessidade de setar o SceItemRecebProvisorio em SceItemRecbXProgrEntrega
		flush();

		return itemRecebProvisorio;
	}		
	
	// Atualização do valor/quantidade recebida na tabela ScoProgEntregaItemAutorizacaoFornecimento
	private void atualizarProgEntregaItemAutorizacaoFornecimento(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAF, DominioTipoFaseSolicitacao tipoSolicitacao, Integer quantidadeRecebida, Double valorRecebido, RapServidores usuarioLogado) throws ApplicationBusinessException {
		progEntregaItemAF.setDtEntrega(new Date());
		
		if (tipoSolicitacao.equals(DominioTipoFaseSolicitacao.C)){
			Integer qtdEntregue = progEntregaItemAF.getQtdeEntregue();
			if (qtdEntregue == null){
				qtdEntregue = 0;
			}
			progEntregaItemAF.setQtdeEntregue(qtdEntregue + quantidadeRecebida);
		}

		Double valorEfetivado = progEntregaItemAF.getValorEfetivado();
		if (valorEfetivado == null){
			valorEfetivado = Double.valueOf(0);
		}
		valorEfetivado = valorEfetivado + valorRecebido; 
		progEntregaItemAF.setValorEfetivado(valorEfetivado);
		
		//getScoProgEntregaItemAutorizacaoFornecimentoRN().persistir(progEntregaItemAF, usuarioLogado);
		this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().atualizar(progEntregaItemAF);
	}
	
	
	// Cada item na tabela ScoProgEntregaItemAutorizacaoFornecimento possui uma lista de ScoSolicitacaoProgramacaoEntrega relacionadas às solicitações de compra/serviço
	// deste item. Para cada recebimento deste item, uma lista de priorização armazena os valores que serão atualizados nas programações de entrega do item.
	// Também é gerado um novo registro na tabela SceItemRecbXProgrEntrega para cada programação de entrega atendida.
	private void atualizarSolicProgrEntrega(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAF, RecebimentoMaterialServicoVO itemAF, SceItemRecebProvisorio itemRecebProvisorio, RapServidores usuarioLogado) throws ApplicationBusinessException {
		for (PriorizaEntregaVO priorizaEntregaVO : itemAF.getListaPriorizacao()){
			ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega = getScoSolicitacaoProgramacaoEntregaDAO().obterPorChavePrimaria(priorizaEntregaVO.getSeqSolicitacaoProgramacaoEntrega());
			
			if (solicitacaoProgramacaoEntrega.getSolicitacaoCompra() != null && priorizaEntregaVO.getQtdeRecebidaSolicitacaoCompra() != null && priorizaEntregaVO.getQtdeRecebidaSolicitacaoCompra() > 0){

				// Cálculo do valor unitário com base no valor informado dividido pela quantidade entregue
				BigDecimal valorItem = itemAF.getValorTotal().divide(new BigDecimal(itemAF.getQtdEntregue()), 4, RoundingMode.HALF_UP);
				// Valor efetivado calculado com base na quantidade entregue na parcela
				valorItem= valorItem.multiply(new BigDecimal(priorizaEntregaVO.getQtdeRecebidaSolicitacaoCompra()));
				
				Integer quantidadeAtualizada = solicitacaoProgramacaoEntrega.getQtdeEntregue();
				
				if (quantidadeAtualizada == null){
					quantidadeAtualizada = priorizaEntregaVO.getQtdeRecebidaSolicitacaoCompra();
				} else {
					quantidadeAtualizada = quantidadeAtualizada + priorizaEntregaVO.getQtdeRecebidaSolicitacaoCompra();
				}
				
				solicitacaoProgramacaoEntrega.setQtdeEntregue(quantidadeAtualizada);
				solicitacaoProgramacaoEntrega.setValorEfetivado(valorItem.doubleValue());
				getScoSolicitacaoProgramacaoEntregaDAO().atualizar(solicitacaoProgramacaoEntrega);

				ScoProgEntregaItemAutorizacaoFornecimento pea = obterProgEntregaItemAF(priorizaEntregaVO.getNumeroAf(), priorizaEntregaVO.getNumeroItemAf().intValue(), priorizaEntregaVO.getSeqProgEntrega(), priorizaEntregaVO.getParcelaProgEntrega());
				atualizarProgEntregaItemAutorizacaoFornecimento(pea, DominioTipoFaseSolicitacao.C, priorizaEntregaVO.getQtdeRecebidaSolicitacaoCompra(), valorItem.doubleValue(), usuarioLogado);
				
				atualizarItemRecebXProgrEntrega(itemRecebProvisorio, pea, solicitacaoProgramacaoEntrega, priorizaEntregaVO.getQtdeRecebidaSolicitacaoCompra().longValue(), valorItem.doubleValue());
			} else {
				if (solicitacaoProgramacaoEntrega.getSolicitacaoServico() != null && priorizaEntregaVO.getValorRecebidoSolicitacaoServico() != null && priorizaEntregaVO.getValorRecebidoSolicitacaoServico().compareTo(BigDecimal.ZERO) > 0){
					Double valorAtualizado = solicitacaoProgramacaoEntrega.getValorEfetivado(); 
					
					if (valorAtualizado == null){
						valorAtualizado = priorizaEntregaVO.getValorRecebidoSolicitacaoServico().doubleValue();
					} else {
						valorAtualizado = valorAtualizado + priorizaEntregaVO.getValorRecebidoSolicitacaoServico().doubleValue();
					}
					
					solicitacaoProgramacaoEntrega.setValorEfetivado(valorAtualizado);
					getScoSolicitacaoProgramacaoEntregaDAO().atualizar(solicitacaoProgramacaoEntrega);
					
					ScoProgEntregaItemAutorizacaoFornecimento pea = obterProgEntregaItemAF(priorizaEntregaVO.getNumeroAf(), priorizaEntregaVO.getNumeroItemAf().intValue(), priorizaEntregaVO.getSeqProgEntrega(), priorizaEntregaVO.getParcelaProgEntrega());
					atualizarProgEntregaItemAutorizacaoFornecimento(pea, DominioTipoFaseSolicitacao.S, Integer.valueOf(0), priorizaEntregaVO.getValorRecebidoSolicitacaoServico().doubleValue(), usuarioLogado);

					if (priorizaEntregaVO.getValorRecebidoSolicitacaoServico() != null && priorizaEntregaVO.getValorRecebidoSolicitacaoServico().compareTo(BigDecimal.ZERO) > 0){
						atualizarItemRecebXProgrEntrega(itemRecebProvisorio, pea, solicitacaoProgramacaoEntrega, Long.valueOf(0), priorizaEntregaVO.getValorRecebidoSolicitacaoServico().doubleValue());
					}
				}
			}
		}
	}
	
	private void atualizarItemRecebXProgrEntrega(SceItemRecebProvisorio itemRecebProvisorio, ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAF, ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega, Long quantidadeRecebida,  Double valorRecebido){		
		SceItemRecbXProgrEntrega itemRecbXProgrEntrega = new SceItemRecbXProgrEntrega();
		
		itemRecbXProgrEntrega.setSceItemRecebProvisorio(itemRecebProvisorio);
		itemRecbXProgrEntrega.setScoProgEntregaItemAutorizacaoFornecimento(progEntregaItemAF);
		itemRecbXProgrEntrega.setScoSolicitacaoProgramacaoEntrega(solicitacaoProgramacaoEntrega);
		
		itemRecbXProgrEntrega.setQtdeEntregue(quantidadeRecebida);			
		itemRecbXProgrEntrega.setValorEfetivado(valorRecebido);
		
		itemRecbXProgrEntrega.setIndEntregaImediata(DominioSimNao.N.toString());
		itemRecbXProgrEntrega.setIndTramiteInterno(DominioSimNao.N.toString());
		itemRecbXProgrEntrega.setIndEstornado(false);
		
		this.getEstoqueFacade().persistirSceItemRecbXProgrEntrega(itemRecbXProgrEntrega);
		
		// Opção pelo flush devido à SceItemRecbXProgrEntregaRN calcular o seq por getSceItemRecbXProgrEntregaDAO().obterMaxItemRecbXProgrEntrega();
		flush();
	}
	
	private void atualizarRecebimentoScoItemAutorizacaoForn(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAF, RapServidores servidorLogado) throws ApplicationBusinessException {
		ScoItemAutorizacaoForn itemAutorizacaoForn = progEntregaItemAF.getScoItensAutorizacaoForn();
		itemAutorizacaoForn.setIndRecebimento(Boolean.TRUE);
		
	//	getScoItemAutorizacaoFornRN().atualizarItemAutorizacaoFornecimento(itemAutorizacaoForn, servidorLogado);
		getScoItemAutorizacaoFornDAO().atualizar(itemAutorizacaoForn);
	}
	
	public boolean verificarConfirmacaoImediataRecebimento() throws ApplicationBusinessException{
		boolean recebimentoImediato = false;
		
		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONF_RECEB_IMEDIATA);
		if (parametro != null){				
			String parametroRecebImediata = parametro.getVlrTexto();
			if (parametroRecebImediata.equalsIgnoreCase("S")){
				recebimentoImediato =  true;
		    }
		}	
		
		return recebimentoImediato;
	}
	
	public void enviarEmailSolicitanteCompras(List<RecebimentoMaterialServicoVO> listaItemAF) throws ApplicationBusinessException{		
		for (RecebimentoMaterialServicoVO itemAF : listaItemAF){
			if (itemAF.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C) && itemAF.getQtdEntregue() != null && itemAF.getQtdEntregue() > 0){
				for (PriorizaEntregaVO priorizaEntregaVO : itemAF.getListaPriorizacao()){
					ScoSolicitacaoProgramacaoEntrega solicitacaoProgramacaoEntrega = getScoSolicitacaoProgramacaoEntregaDAO().obterPorChavePrimaria(priorizaEntregaVO.getSeqSolicitacaoProgramacaoEntrega());
					ScoSolicitacaoDeCompra solicitacaoCompra = solicitacaoProgramacaoEntrega.getSolicitacaoCompra();
					if (solicitacaoCompra.getMaterial().getEstocavel().equals(Boolean.FALSE)){
			
						String nomeMaterial = solicitacaoCompra.getMaterial().getNome();
						Integer codMaterial = solicitacaoCompra.getMaterial().getCodigo();
						Integer numeroSC = solicitacaoCompra.getNumero();
						Date dtAutorizacao = solicitacaoCompra.getDtAutorizacao();
						SimpleDateFormat dataAutorizacao = new SimpleDateFormat("dd/MM/yyyy"); 			
						String emailDestinatario = solicitacaoCompra.getServidor().getEmail();
						String emailRemetente = null;
						String nomeRemetente = null;			
						
						// Matrícula/Vinculo do chefe do almoxarifado estão cadastrados nos parâmetros do sistema
						// Com eles busca-se nome e email do remetente da mensagem
						AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATRICULA_CHEFE_ALMOX);
						if (parametro != null){
							BigDecimal matriculaChefeAlmox = parametro.getVlrNumerico(); 
							
							parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VINCULO_CHEFE_ALMOX);
							if (parametro!= null){
								BigDecimal vinculoChefeAlmox = parametro.getVlrNumerico();
														
								RapServidoresId idServidor = new RapServidoresId();							
								idServidor.setMatricula(matriculaChefeAlmox.intValue());
								idServidor.setVinCodigo(vinculoChefeAlmox.shortValue());
							
								RapServidores chefeAlmox = getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(idServidor);  
								
								if (chefeAlmox != null){
									emailRemetente = chefeAlmox.getEmail();
									nomeRemetente = chefeAlmox.getPessoaFisica().getNome();
								}
							}
						}
						
						if (emailDestinatario != null && !emailDestinatario.isEmpty()){
							StringBuilder sbMensagem = new StringBuilder();
							String assunto = getResourceBundleValue("ASSUNTO_EMAIL_SOLICITANTE_COMPRA");
							String conteudo = getResourceBundleValue("CONTEUDO_EMAIL_SOLICITANTE_COMPRA");
							String conteudoEmail = MessageFormat.format(conteudo, nomeMaterial, codMaterial.toString(), numeroSC.toString(), dataAutorizacao.format(dtAutorizacao), nomeRemetente);
							sbMensagem.append(conteudoEmail);
							
							getEmailUtil().enviaEmail(emailRemetente, emailDestinatario, null, assunto, sbMensagem.toString());
						}
					}
				}
			}	
		}
	}
	
	
	private void validarObrigatoriedadeNotaFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws ApplicationBusinessException{
		AghParametros parametro;
		
		if (documentoFiscalEntrada != null && !permiteNotaFiscalEntrada()){
			throw new ApplicationBusinessException(
					RecebimentoItemAFONExceptionCode.MENSAGEM_NAO_PERMITIDO_ENTRADA_NOTA_FISCAL);				
		}

		if (documentoFiscalEntrada == null){
			boolean exigeNotaFiscal = false;
			
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONF_RECEB_IMEDIATA);			
			if (parametro != null){				
				String parametroRecebImediata = parametro.getVlrTexto();
				if (parametroRecebImediata.equalsIgnoreCase("S")){
					exigeNotaFiscal =  true;
			    }
			}	
			
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EXIGE_NF_NO_RECEBIMENTO);			
			if (parametro != null){				
				String exigeNFRecibimento = parametro.getVlrTexto();				
				if (exigeNFRecibimento.equalsIgnoreCase("S")){
					exigeNotaFiscal =  true;		
				}
			}
			
			if (exigeNotaFiscal){
				throw new ApplicationBusinessException(
						RecebimentoItemAFONExceptionCode.MENSAGEM_OBRIGATORIO_INFORMAR_NOTA_FISCAL_ENTRADA);				
			}
		}			
	}
	
 	public boolean permiteNotaFiscalEntrada() throws ApplicationBusinessException{
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ENTRADA_NF_MAT_SERV);
		
		if (parametro != null){
			String entradaNFMaterialServico = parametro.getVlrTexto();	
			if (entradaNFMaterialServico.equalsIgnoreCase("C")){
				return false;
			}
		} 

		return true;
 	}
 	
 	// Quando um recebimento é vinculado a uma nota fiscal e o parâmetro P_CRITICA_VLR_NF possui os valores R ou NR
 	// é verificado se o valor que esta sendo recebido somado a possíveis valores já vinculado a nota ultrapassa o valor total desta.
 	private void criticarValorNotaFiscal(List<RecebimentoMaterialServicoVO> listaItemAF, SceDocumentoFiscalEntrada documentoFiscalEntrada, ScoFornecedor fornecedor) throws ApplicationBusinessException{
 		if (documentoFiscalEntrada != null){
 			
 			if (documentoFiscalEntrada.getValorTotalNf() == null || documentoFiscalEntrada.getValorTotalNf() == 0){
 				throw new ApplicationBusinessException(RecebimentoItemAFONExceptionCode.MENSAGEM_CRITICA_NF_SEM_VALOR);	
 			}

 			AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CRITICA_VLR_NF);		
 			
			if (parametro != null){				
				String parametroCriticaValoNotaFiscal = parametro.getVlrTexto();
				if (parametroCriticaValoNotaFiscal.equalsIgnoreCase("R") || parametroCriticaValoNotaFiscal.equalsIgnoreCase("NF")){

					Double valorJaRecebido = getEstoqueFacade().obterValorTotalItemNotaFiscal(documentoFiscalEntrada.getSeq());					
					if (valorJaRecebido == null){
						valorJaRecebido = Double.valueOf(0);				 											
					}
					
					BigDecimal valorEntrega = BigDecimal.ZERO;
					for (RecebimentoMaterialServicoVO itemAF : listaItemAF){
						if (itemAF.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C) && itemAF.getQtdEntregue() != null && itemAF.getQtdEntregue() > 0){
							valorEntrega = valorEntrega.add(itemAF.getValorTotal());
						}
						
						if ((itemAF.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.S) && itemAF.getValorEntregue() != null && itemAF.getValorEntregue().compareTo(BigDecimal.ZERO) > 0)){
							valorEntrega = valorEntrega.add(itemAF.getValorEntregue());
						}
					}	

					Double valorTotal = valorJaRecebido + valorEntrega.doubleValue();
					if (valorTotal > documentoFiscalEntrada.getValorTotalNf()){
						Double diferenca = valorTotal - documentoFiscalEntrada.getValorTotalNf();
	 					throw new ApplicationBusinessException(
	 							RecebimentoItemAFONExceptionCode.MENSAGEM_CRITICA_SOMA_RECEBIMENTOS, valorTotal, diferenca, documentoFiscalEntrada.getValorTotalNf());				 											
					}
			    }				
			}
			
            

            // Verificação da relação entre o fornecedor da AF e o emissor da nota fiscal
            // Somente pode ser aceito o recebimento com fornedor da AF diferente do emissor da nota fiscal se estes possuirem a mesma raiz de CNPJ
            String cnpjRaizFornecedor, cnpjRaizNF;           
            cnpjRaizFornecedor = fornecedor.getCnpjCpf().toString().substring(0, 7); 			                     
            cnpjRaizNF = documentoFiscalEntrada.getFornecedor().getCnpjCpf().toString().substring(0, 7);

            if (!documentoFiscalEntrada.getFornecedor().equals(fornecedor) && !cnpjRaizFornecedor.equals(cnpjRaizNF)){
                    throw new ApplicationBusinessException(RecebimentoItemAFONExceptionCode.MENSAGEM_CRITICA_FORNECEDOR_DIF_NF);

            }                        
			
 			
 		} 		
 	}
 	
	// Verificaçao do cadastro de matrícula/vinculo do Chefe do Almoxarifado
    private void validarCadastroParametrosSistema() throws ApplicationBusinessException{
    	AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATRICULA_CHEFE_ALMOX);
    	if (parametro == null || parametro.getVlrNumerico() == null){
			throw new ApplicationBusinessException(
					RecebimentoItemAFONExceptionCode.MENSAGEM_CADASTRAR_MATRICULA_VINCULO_CHEFE_ALMOX);				
    	}
    	parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VINCULO_CHEFE_ALMOX);    
    	if (parametro == null || parametro.getVlrNumerico() == null){
			throw new ApplicationBusinessException(
					RecebimentoItemAFONExceptionCode.MENSAGEM_CADASTRAR_MATRICULA_VINCULO_CHEFE_ALMOX);				
    	}
    }
 	
	
	/**
	 * Lista as parcelas da AF que ainda tenham saldo a receber
	 * Estória #25015
	 * @param numeroAF
	 * @param complementoAf
	 * @param tipoSolicitacao (Material/Serviço)
	 * @return List<RecebimentoMaterialServicoVO>
	 */	 	
	public List<RecebimentoMaterialServicoVO> pesquisarProgEntregaItensComSaldoPositivo(Integer numeroAF, Short complementoAf, DominioTipoFaseSolicitacao tipoSolicitacao){
		
		ScoAutorizacaoForn af = getScoAutorizacaoFornDAO().buscarAutFornPorNumPac(numeroAF, complementoAf);
		Integer iafAfnNumero = af.getNumero();
		
		List<ConsultaRecebimentoMaterialServicoVO> listaConsultaRecimentoMaterialServico;
		List<RecebimentoMaterialServicoVO> listaRecebimentoMaterialServicoVO = new ArrayList<RecebimentoMaterialServicoVO>();
		
		listaConsultaRecimentoMaterialServico = getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisarProgEntregaItensComSaldoPositivo(iafAfnNumero, tipoSolicitacao);
				
		for (ConsultaRecebimentoMaterialServicoVO consultaRecimentoMaterialServico : listaConsultaRecimentoMaterialServico){
			ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAF = obterProgEntregaItemAF(consultaRecimentoMaterialServico.getItemAFNNumero(), consultaRecimentoMaterialServico.getItemNumero(), consultaRecimentoMaterialServico.getSeqParcela(), consultaRecimentoMaterialServico.getParcela());
			ScoItemAutorizacaoFornId chaveItemAF = new ScoItemAutorizacaoFornId();
			chaveItemAF.setAfnNumero(consultaRecimentoMaterialServico.getItemAFNNumero());
			chaveItemAF.setNumero(consultaRecimentoMaterialServico.getItemNumero());
			ScoItemAutorizacaoForn itemAutorizacaoForn = getScoItemAutorizacaoFornDAO().obterPorChavePrimaria(chaveItemAF);
			this.getScoItemAutorizacaoFornDAO().refresh(itemAutorizacaoForn);
			RecebimentoMaterialServicoVO recebimentoMaterialServico = new RecebimentoMaterialServicoVO();
			
			recebimentoMaterialServico.setTipoSolicitacao(tipoSolicitacao);
			
			recebimentoMaterialServico.setItlNumero(consultaRecimentoMaterialServico.getItlNumero());
			recebimentoMaterialServico.setParcela(consultaRecimentoMaterialServico.getParcela());
			recebimentoMaterialServico.setAfp(consultaRecimentoMaterialServico.getAfp());
			recebimentoMaterialServico.setDtPrevEntrega(consultaRecimentoMaterialServico.getDtPrevEntrega());
			if (itemAutorizacaoForn.getUnidadeMedida() != null){
				recebimentoMaterialServico.setUnidade(itemAutorizacaoForn.getUnidadeMedida().getCodigo());
			}
			
			recebimentoMaterialServico.setNumeroAf(chaveItemAF.getAfnNumero());
			recebimentoMaterialServico.setNumeroItemAf(chaveItemAF.getNumero());
			recebimentoMaterialServico.setSeqParcela(consultaRecimentoMaterialServico.getSeqParcela());
			
			if (consultaRecimentoMaterialServico.getQtdeEntregue() == null){
				recebimentoMaterialServico.setSaldoQtd(consultaRecimentoMaterialServico.getQtde());
			} else {
				recebimentoMaterialServico.setSaldoQtd(consultaRecimentoMaterialServico.getQtde() - consultaRecimentoMaterialServico.getQtdeEntregue());
			}
			
			if (consultaRecimentoMaterialServico.getValorEfetivado() == null){
				recebimentoMaterialServico.setValorSaldo(new BigDecimal(consultaRecimentoMaterialServico.getValorTotal()));
			} else {
				recebimentoMaterialServico.setValorSaldo(new BigDecimal(consultaRecimentoMaterialServico.getValorTotal()).subtract(new BigDecimal(consultaRecimentoMaterialServico.getValorEfetivado())));
			}

			ScoSolicitacaoDeCompra solicitacaoCompra = itemAutorizacaoForn.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra();
			if (solicitacaoCompra != null){
				recebimentoMaterialServico.setCodigoMaterialServico(solicitacaoCompra.getMaterial().getCodigo());
				recebimentoMaterialServico.setNomeMaterialServico(solicitacaoCompra.getMaterial().getNome());
				recebimentoMaterialServico.setDescricaoMaterialServico(solicitacaoCompra.getMaterial().getDescricao());				
				recebimentoMaterialServico.setValorUnitario(new BigDecimal(itemAutorizacaoForn.getValorUnitario()));
				recebimentoMaterialServico.setQtdSaldoAssinado(getScoProgEntregaItemAutorizacaoFornecimentoDAO().calculaQtdSaldoAssinado(iafAfnNumero, consultaRecimentoMaterialServico.getItemNumero()));
				
				AghParametros fornecedor;

				try {
					fornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
					SceEstoqueAlmoxarifado localEstoque = getEstoqueFacade().pesquisarEstoqueAlmoxarifadoPorAlmoxarifadoMaterialFornecedor(solicitacaoCompra.getMaterial().getAlmoxarifado().getSeq(), solicitacaoCompra.getMaterial().getCodigo(), fornecedor.getVlrNumerico().intValue());
					if (localEstoque != null && localEstoque.getEndereco() != null){
						recebimentoMaterialServico.setLocalEstoque(solicitacaoCompra.getMaterial().getAlmoxarifado().getSeq().toString() + "(" + localEstoque.getEndereco() + ")");
					}
				} catch (ApplicationBusinessException e) {
					recebimentoMaterialServico.setLocalEstoque(null);
				}
			} else {
				ScoSolicitacaoServico solicitacaoServico = itemAutorizacaoForn.getScoFaseSolicitacao().get(0).getSolicitacaoServico();
				if (solicitacaoServico != null){
					recebimentoMaterialServico.setCodigoMaterialServico(solicitacaoServico.getServico().getCodigo());
					recebimentoMaterialServico.setNomeMaterialServico(solicitacaoServico.getServico().getNome());
					recebimentoMaterialServico.setDescricaoMaterialServico(solicitacaoServico.getServico().getDescricao());	
					recebimentoMaterialServico.setValorUnitario(new BigDecimal(consultaRecimentoMaterialServico.getValorTotal()));	
					recebimentoMaterialServico.setValorSaldoAssinado(getScoProgEntregaItemAutorizacaoFornecimentoDAO().calculaValorSaldoAssinado(iafAfnNumero, consultaRecimentoMaterialServico.getItemNumero()));
					
				}
			}
			
			recebimentoMaterialServico.setDescricaoSolicitacao(obterDescricaoSolicitacao(itemAutorizacaoForn));
			if (itemAutorizacaoForn.getMarcaComercial() != null && itemAutorizacaoForn.getMarcaComercial().getDescricao() != null){
				recebimentoMaterialServico.setMarcaComercial(itemAutorizacaoForn.getMarcaComercial().getDescricao());
			}			
			recebimentoMaterialServico.setFatorConversao(itemAutorizacaoForn.getFatorConversao());
			recebimentoMaterialServico.setIndConsignado(itemAutorizacaoForn.getIndConsignado());
			recebimentoMaterialServico.setUnidadeMedida(getUnidadeMedia(itemAutorizacaoForn));
			
			if (progEntregaItemAF.getSolicitacoesProgEntrega() == null || (progEntregaItemAF.getSolicitacoesProgEntrega() != null && progEntregaItemAF.getSolicitacoesProgEntrega().size() == 1)){
				recebimentoMaterialServico.setSomenteUmaSolicitacaoParaParcela(true);
			} else {
				recebimentoMaterialServico.setSomenteUmaSolicitacaoParaParcela(false);
			}
			
			recebimentoMaterialServico.setDescricaoQuantidadeItemAF(getAutFornecimentoON().obterDescricaoQtdItemAF(itemAutorizacaoForn, recebimentoMaterialServico.getSaldoQtd()));
			
			recebimentoMaterialServico.setPermiteRecebimento(true);
			
			listaRecebimentoMaterialServicoVO.add(recebimentoMaterialServico);
		}
		
		return listaRecebimentoMaterialServicoVO;		
	}

	/**
	 * Busca lista de números de Autorização de Fornecimento com saldo na programação de entrega, filtrando por numero da AF, Complemento e Fornecedor
	 * 
	 * @param numero da AF
	 * @param numero de complemento de AF
	 * @param fornecedor
	 * @return Retorna a lista de Autorização de Fornecimento
	 **/		
	public List<ScoAutorizacaoForn> pesquisarAFNumComplementoFornecedor(Object numeroAf, Short numComplementoAf, Integer numFornecedor, ScoMaterial material, ScoServico servico){
		Integer numAf = null;
		
		String filterStr = (String) numeroAf;
		if (CoreUtil.isNumeroInteger(filterStr)){
			numAf = Integer.valueOf(filterStr);
		}
		
    	List<ScoAutorizacaoForn> listaAF = getScoRecebParcelasAutorizacaoFornDAO().pesquisarAFNumComplementoFornecedor(numAf, numComplementoAf, numFornecedor, material, servico);
    	List<ScoAutorizacaoForn> listaAFSemDuplicados = new ArrayList<ScoAutorizacaoForn>();

    	boolean existe;
    	for (ScoAutorizacaoForn afResult : listaAF){
    		existe = false;
    		for (ScoAutorizacaoForn afReturn : listaAFSemDuplicados){
    			if (afReturn.getPropostaFornecedor().getId().getLctNumero().equals(afResult.getPropostaFornecedor().getId().getLctNumero())){
    				existe = true;
    			}
    		}

    		if (!existe){
    			getScoRecebParcelasAutorizacaoFornDAO().initialize(afResult);
    			getScoRecebParcelasAutorizacaoFornDAO().initialize(afResult.getPropostaFornecedor());
    			if (afResult.getPropostaFornecedor() != null) {
    				getScoRecebParcelasAutorizacaoFornDAO().initialize(afResult.getPropostaFornecedor().getLicitacao());
    			}
    			listaAFSemDuplicados.add(afResult);
    		}
    	}

    	return listaAFSemDuplicados;    	
	}
		
	/**
	 * Busca lista de complementos de AF que tenham saldo na programação de entrega, filtrando por numero da AF, Complemento e Fornecedor
	 * 
	 * @param numero da AF
	 * @param numero de complemento de AF
	 * @param fornecedor
	 * @return Retorna a lista de complmento de AF
	 **/		
	public List<ScoAutorizacaoForn> pesquisarComplementoNumAFNumComplementoFornecedor(Integer numeroAf, Object numComplementoAf, Integer numFornecedor, ScoMaterial material, ScoServico servico){
		List<ScoAutorizacaoForn> listaNroComplementoAF = null;
		Short nroComplementoAf = null;
		
		String filterStr = (String) numComplementoAf;
		if (CoreUtil.isNumeroShort(filterStr)){
			nroComplementoAf = Short.valueOf(filterStr);
		}
		
		listaNroComplementoAF = getScoRecebParcelasAutorizacaoFornDAO().pesquisarComplementoNumAFNumComplementoFornecedor(numeroAf, nroComplementoAf, numFornecedor, material, servico);
		
    	List<ScoAutorizacaoForn> listaAFSemDuplicados = new ArrayList<ScoAutorizacaoForn>();
    	boolean existe;
    	for (ScoAutorizacaoForn afResult : listaNroComplementoAF){
    		existe = false;
    		for (ScoAutorizacaoForn afReturn : listaAFSemDuplicados){
    			if (afReturn.getNroComplemento().equals(afResult.getNroComplemento())){
    				existe = true;
    			}
    		}
    		
    		if (!existe){
    			listaAFSemDuplicados.add(afResult);
    		}
    	}
		
    	return listaAFSemDuplicados;
	}
	
	/**
	 * Busca lista de fornecedores cujas AF tenham saldo na programação de entrega, filtrando por numero da AF, Complemento e Fornecedor
	 * 
	 * @param numero da AF
	 * @param numero de complemento de AF
	 * @param fornecedor
	 * @return Retorna a lista de fornecedores
	 **/		
	public List<ScoFornecedor> pesquisarFornecedorNumAfNumComplementoFornecedor(Integer numeroAf, Short numComplementoAf, Object fornFilter, ScoMaterial material, ScoServico servico, SceDocumentoFiscalEntrada documentoFiscalEntrada){	
		
		List<ScoFornecedor> listaFornecedor = getScoRecebParcelasAutorizacaoFornDAO().pesquisarFornecedorNumAfNumComplementoFornecedor(numeroAf, numComplementoAf, fornFilter, material, servico);
		Set<ScoFornecedor> listaFornecedorSemDuplicados = new HashSet<ScoFornecedor>();
		String cnpjRaizDfe = "";

		if (documentoFiscalEntrada != null) {
			if (documentoFiscalEntrada.getFornecedor().getCnpjCpf().toString().length() >= 8) {
				cnpjRaizDfe = documentoFiscalEntrada.getFornecedor().getCnpjCpf().toString().substring(0, 8);	
			} else {
				cnpjRaizDfe = documentoFiscalEntrada.getFornecedor().getCnpjCpf().toString();
			}
		}
		
    	for (ScoFornecedor fornResult : listaFornecedor){
    		
    		if (documentoFiscalEntrada != null) {
	    		String cnpjRaizQuery = "";
	    		
				if (fornResult.getCnpjCpf().toString().length() >= 8) {
					cnpjRaizQuery = fornResult.getCnpjCpf().toString().substring(0, 8);	
				} else {
					cnpjRaizQuery = fornResult.getCnpjCpf().toString();
				}
				
				if (!cnpjRaizQuery.equals(cnpjRaizDfe)) {
					continue;
				}				
    		}    		
    		listaFornecedorSemDuplicados.add(fornResult);
    	}
		
    	return new ArrayList<ScoFornecedor>(listaFornecedorSemDuplicados);		
	}
	
	/**
	 * Buscar lista de materiais presentes em parcelas de AF que ainda tenham saldo.
	 * Filtrando pelo número da af, e/ou número do complemento, e/ou fornecedor da AF, e/ou código do material ou sua descrição 
	 * 
	 * @param numeroAf
	 * @param numComplementoAf
	 * @param numFornecedor
	 * @param param
	 * @return
	 */
	public List<ScoMaterial> pesquisarMaterialaReceber(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, String param){

		List<ScoMaterial> listaMaterial = getScoRecebParcelasAutorizacaoFornDAO().pesquisarMaterialaReceber(numeroAf, numComplementoAf, numFornecedor, param);
		
		List<ScoMaterial> listaMaterialSemDuplicado = new ArrayList<ScoMaterial>();
		
		boolean existe;
		for (ScoMaterial material : listaMaterial){
			existe = false;
			
			for (ScoMaterial materialTemp : listaMaterialSemDuplicado){
				 if (materialTemp.getCodigo().equals(material.getCodigo())){
					 existe = true;
				 }
			}
			
			if (!existe){
				listaMaterialSemDuplicado.add(material);
			}
		}
		
		return listaMaterialSemDuplicado;		
	}
	
	/**
	 * Buscar lista de servicos presentes em parcelas de AF que ainda tenham saldo.
	 * Filtrando pelo número da af, e/ou número do complemento, e/ou fornecedor da AF, e/ou código do servico ou sua descrição 
	 * 
	 * @param numeroAf
	 * @param numComplementoAf
	 * @param numFornecedor
	 * @param param
	 * @return
	 */
	public List<ScoServico> pesquisarServicoaReceber(Integer numeroAf, Short numComplementoAf, Integer numFornecedor, Object param){
		
		List<ScoServico> listaServico = getScoRecebParcelasAutorizacaoFornDAO().pesquisarServicoaReceber(numeroAf, numComplementoAf, numFornecedor, param);
		
		List<ScoServico> listaServicoSemDuplicado = new ArrayList<ScoServico>();
		
		boolean existe;
		for (ScoServico servico : listaServico){
			existe = false;
			
			for (ScoServico servicoTemp : listaServicoSemDuplicado){
				 if (servicoTemp.getCodigo().equals(servico.getCodigo())){
					 existe = true;
				 }
			}
			
			if (!existe){
				listaServicoSemDuplicado.add(servico);
			}
		}
		
		return listaServicoSemDuplicado;
	}
	
		
	private ScoProgEntregaItemAutorizacaoFornecimento obterProgEntregaItemAF(Integer numeroAF, Integer numeroItemAF, Integer seqParcela, Integer parcela){
		ScoProgEntregaItemAutorizacaoFornecimentoId idProgEntregaItemAF = new ScoProgEntregaItemAutorizacaoFornecimentoId();
		idProgEntregaItemAF.setIafAfnNumero(numeroAF);
		idProgEntregaItemAF.setIafNumero(numeroItemAF);
		idProgEntregaItemAF.setSeq(seqParcela);
		idProgEntregaItemAF.setParcela(parcela);
		return getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterPorChavePrimaria(idProgEntregaItemAF);		
	}
	
	private String obterDescricaoSolicitacao(ScoItemAutorizacaoForn itemAutorizacaoForn) {
		String descricao = null;

		if (itemAutorizacaoForn.getScoFaseSolicitacao() != null
				&& itemAutorizacaoForn.getScoFaseSolicitacao().get(0) != null
				&& itemAutorizacaoForn.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra() != null) {
			descricao = itemAutorizacaoForn.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra().getDescricao();
		}

		return descricao;
	}
	
	private String getUnidadeMedia(ScoItemAutorizacaoForn itemAutorizacaoForn) {
		String descricaoUnidadeMedida = null;
		if (itemAutorizacaoForn.getScoFaseSolicitacao() != null
				&& itemAutorizacaoForn.getScoFaseSolicitacao().get(0) != null
				&& itemAutorizacaoForn.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra() != null
				&& itemAutorizacaoForn.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra().getMaterial() != null
				&& itemAutorizacaoForn.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra().getMaterial().getUmdCodigo() != null) {
			descricaoUnidadeMedida = itemAutorizacaoForn.getScoFaseSolicitacao().get(0).getSolicitacaoDeCompra().getMaterial().getUmdCodigo();
		}

		return descricaoUnidadeMedida;
	}
	
	private ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO(){
		return scoAutorizacaoFornDAO;
	}
	
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO(){
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}
	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO(){
		return scoItemAutorizacaoFornDAO;
	}
	
	private ScoAutorizacaoFornecedorPedidoDAO getScoAutorizacaoFornecedorPedidoDAO(){
		return scoAutorizacaoFornecedorPedidoDAO;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	private ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO(){
		return scoSolicitacaoProgramacaoEntregaDAO;
	}
	
	private ScoRecebParcelasAutorizacaoFornDAO getScoRecebParcelasAutorizacaoFornDAO(){
		return scoRecebParcelasAutorizacaoFornDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}
	
	protected EmailUtil getEmailUtil() {
		return (EmailUtil) emailUtil;
	}
	
	private IRegistroColaboradorFacade getRegistroColaboradorFacade(){
		return registroColaboradorFacade;
	}
	
    protected AutFornecimentoON getAutFornecimentoON(){
    	return autFornecimentoON;    
    }

    protected RecebeMaterialServicoValorExcedidoON getRecebeMaterialServicoValorExcedidoON() {
		return recebeMaterialServicoValorExcedidoON;
	}      
    
}
