package br.gov.mec.aghu.estoque.business;

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
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroRecebeMaterialServicoSemAFVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ItensRecebimentoAdiantamentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ItensRecebimentoVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.dao.SceEntrSaidSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemEntrSaidSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebProvisorioDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemEntrSaidSemLicitacao;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceItemRecebProvisorioId;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

/**
 * ON da estória #28585 - RECEBIMENTO DE MATERIAIS E SERVIÇOS POR ADIANTAMENTO DE AUTORIZAÇÃO DE FORNECIMENTO
 * 
 * @author luismoura
 * 
 */
@Stateless
public class RecebimentoSemAFON extends BaseBusiness {
	private static final long serialVersionUID = 6229985604727735304L;

	private static final String PARAMETRO_R = "R";
	private static final String PARAMETRO_NF = "NF";
	private static final String SIGLA_DOACAO = "DO";
	
	private static final Log LOG = LogFactory.getLog(RecebimentoSemAFON.class);
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ConfirmacaoDevolucaoON confirmacaoDevolucaoON;
	
	@Inject
	private SceEntrSaidSemLicitacaoDAO sceEntrSaidSemLicitacaoDAO;
	
	@Inject
	private SceItemEntrSaidSemLicitacaoDAO sceItemEntrSaidSemLicitacaoDAO;
	
	@Inject
	private SceNotaRecebProvisorioDAO sceNotaRecebProvisorioDAO;
	
	@Inject
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;


	public enum RecebimentoSemAFONExceptionCode implements BusinessExceptionCode {
		SOMA_RECEBIMENTOS_ULTRAPASSA_VALOR_TOTAL_NF, //
		VALOR_DEVE_SER_MAIOR_QUE_ZERO, //
		QUANTIDADE_INFORMADA_SUPERIOR_AO_SALDO_AF, //
		VALOR_UNITARIO_FORA_FAIXA_VARIACAO_PERMITIDA, //
		NOTA_OBRIGATORIA_EFETUAR_RECEBIMENTO, //
		PARA_DOACAO_PATR_GERAR_PROC_COMP_MOD_DOACAO, //
		;
	}
	
	/**
	 * Busca lista de números de Autorização de Fornecimento, filtrando por numero da AF, Complemento e Fornecedor
	 * 
	 * @param numeroAf Nro da AF
	 * @param numComplementoAf Nro complemento de AF
	 * @param numFornecedor Nro Fornecedor
	 * @return Retorna a lista de Autorização de Fornecimento
	 **/		
	public List<ScoAutorizacaoForn> pesquisarAF(Object numeroAf, Short numComplementoAf, Integer numFornecedor) {
		Integer numAf = null;
		
		if (CoreUtil.isNumeroInteger(numeroAf)){
			numAf = Integer.valueOf(numeroAf.toString());
		}
		
    	List<ScoAutorizacaoForn> listaAF = getComprasFacade().pesquisarAFComplementoFornecedor(numAf, numComplementoAf, numFornecedor, "AF");
    			
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
    			listaAFSemDuplicados.add(afResult);
    		}
    	}
		
    	return listaAFSemDuplicados;
	}
		
	/**
	 * Busca lista de complementos de AF, filtrando por numero da AF, Complemento e Fornecedor
	 * 
	 * @param numeroAf Nro da AF
	 * @param numComplementoAf Nro complemento de AF
	 * @param numFornecedor Nro Fornecedor
	 * @return Retorna a lista de Autorização de Fornecimento
	 **/		
	public List<ScoAutorizacaoForn> pesquisarComplemento(Integer numeroAf, Object numComplementoAf, Integer numFornecedor) {
		Short nroComplementoAf = null;
		
		if (CoreUtil.isNumeroShort(numComplementoAf)){
			nroComplementoAf = Short.valueOf(numComplementoAf.toString());
		}
		
		List<ScoAutorizacaoForn> listaAFComplementos = getComprasFacade()
			.pesquisarAFComplementoFornecedor(numeroAf, nroComplementoAf, numFornecedor, "CP");
		
    	List<ScoAutorizacaoForn> listaAFComplementosSemDuplicados = new ArrayList<ScoAutorizacaoForn>();
    	boolean existe;
    	for (ScoAutorizacaoForn afResult : listaAFComplementos){
    		existe = false;
    		for (ScoAutorizacaoForn afReturn : listaAFComplementosSemDuplicados){
    			if (afReturn.getNroComplemento().equals(afResult.getNroComplemento())){
    				existe = true;
    			}
    		}
    		
    		if (!existe){
    			listaAFComplementosSemDuplicados.add(afResult);
    		}
    	}
		
    	return listaAFComplementosSemDuplicados;
	}
	
	/**
	 * Busca lista de fornecedores, filtrando por numero da AF, Complemento e Fornecedor
	 * 
	 * @param numeroAf Nro da AF
	 * @param numComplementoAf Nro complemento de AF
	 * @param numFornecedor Nro Fornecedor
	 * @return Retorna a lista de fornecedores
	 **/		
	public List<ScoFornecedor> pesquisarFornecedor(Integer numeroAf, Short numComplementoAf, Object numFornecedor, Boolean indAdiantamentoAF) {
		List<ScoAutorizacaoForn> listaAFFornecedor = null;
		List<ScoFornecedor> listaFornecedor = null;
		List<ScoFornecedor> listaFornecedorSemDuplicados = new ArrayList<ScoFornecedor>();
		boolean existe;
		
		if (indAdiantamentoAF.equals(Boolean.TRUE)) {
			listaAFFornecedor = getComprasFacade().pesquisarAFComplementoFornecedor(numeroAf, numComplementoAf, numFornecedor, "FORN");
			
			for (ScoAutorizacaoForn fornResult : listaAFFornecedor) {
				existe = false;
				for (ScoFornecedor fornReturn : listaFornecedorSemDuplicados) {
					if (fornReturn.getNumero().equals(fornResult.getPropostaFornecedor().getFornecedor().getNumero())) {
						existe = true;
					}
				}
				
				if (!existe) {
					listaFornecedorSemDuplicados.add(fornResult.getPropostaFornecedor().getFornecedor());
				}
			}
		} else {
			listaFornecedor = getComprasFacade().listarFornecedoresAtivos(numFornecedor, 0, 200, null, false);
			
			for (ScoFornecedor fornResult : listaFornecedor) {
	    		existe = false;
	    		for (ScoFornecedor fornReturn : listaFornecedorSemDuplicados) {
	    			if (fornReturn.getNumero().equals(fornResult.getNumero())) {
	    				existe = true;
	    			}
	    		}
	    		
	    		if (!existe) {
	    			listaFornecedorSemDuplicados.add(fornResult);
	    		}
	    	}
		}
		
		
    	return listaFornecedorSemDuplicados;
	}
	
	public List<ItensRecebimentoAdiantamentoVO> pesquisarItensRecebimentoAdiantamento(Integer numeroAF) {
		
		List<ItensRecebimentoAdiantamentoVO> listVo = getComprasFacade().pesquisarItensRecebimentoAdiantamento(numeroAF);
		
		for (ItensRecebimentoAdiantamentoVO vo : listVo) {
			vo.setValorUnitarioConvertido(vo.getValorUnitario() != null ? new BigDecimal(vo.getValorUnitario()) : BigDecimal.ZERO);
			
			Integer quantidadeEslPendenteAF = getComprasFacade().obterQuantidadeEslPendenteAF(vo.getAfnNumero(), vo.getNumero());
			vo.setSaldoQtd((vo.getSaldoAF() != null ? vo.getSaldoAF() : 0) - (quantidadeEslPendenteAF != null ? quantidadeEslPendenteAF : 0));
			vo.setEslPendenteAF(quantidadeEslPendenteAF != null ? quantidadeEslPendenteAF : 0);
		}
		
		return listVo;
	}
	
	public SceNotaRecebProvisorio preReceberItensAdiantamentoAF(FiltroRecebeMaterialServicoSemAFVO filtro,
				List<ItensRecebimentoAdiantamentoVO> listaItensAdiantamento,final String usuarioLogado,
				RapServidores servidorLogado) throws ApplicationBusinessException {
		// RN01 e RN05
		if(filtro.getDocumentoFiscalEntrada() != null) {
			validarValorComprometidoSuperiorValorTotalNota(filtro.getValorComprometidoNF(), filtro.getValorNota());
		} else {
			// RN06
			validarObrigatoriedadeNFEParaRecebimento();
		}
		for (ItensRecebimentoAdiantamentoVO item : listaItensAdiantamento) {
			// RN03
			validarQuantidadeSuperiorSaldoAF(item.getCodigoMaterial(), item.getQtdEntregue(), item.getSaldoQtd());
			// RN04
			if (possuiValorAdiantamento(item)) {
				validaValorExcedePercentualVariacao(usuarioLogado, item.getItlNumero(), item.getPercVarPreco(), item.getQtdEntregue(),
						item.getValorTotal(), item.getValorUnitarioConvertido());
			}
			// RN08
			validarDoacaoPatrimonioImobilizado(filtro.getTipoMovimento().getSigla(), item.getCodigoMaterial(), item.getValorUnitarioConvertido());
		}
		return receberItensAdiantamentoAF(filtro, listaItensAdiantamento, servidorLogado);
	}
	
	public SceNotaRecebProvisorio preReceberItensAdiantamentoAF(FiltroRecebeMaterialServicoSemAFVO filtro,
			List<ItensRecebimentoAdiantamentoVO> listaItensAdiantamento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// RN01 e RN05
		if(filtro.getDocumentoFiscalEntrada() != null) {
			validarValorComprometidoSuperiorValorTotalNota(filtro.getValorComprometidoNF(), filtro.getValorNota());
		} else {
			// RN06
			validarObrigatoriedadeNFEParaRecebimento();
		}
		for (ItensRecebimentoAdiantamentoVO item : listaItensAdiantamento) {
			// RN03
			validarQuantidadeSuperiorSaldoAF(item.getCodigoMaterial(), item.getQtdEntregue(), item.getSaldoQtd());
			// RN04
			if (possuiValorAdiantamento(item)) {
				validaValorExcedePercentualVariacao(this.obterLoginUsuarioLogado(), item.getItlNumero(), item.getPercVarPreco(), item.getQtdEntregue(),
						item.getValorTotal(), item.getValorUnitarioConvertido());
			}
			// RN08
			validarDoacaoPatrimonioImobilizado(filtro.getTipoMovimento().getSigla(), item.getCodigoMaterial(), item.getValorUnitarioConvertido());
		}
		return receberItensAdiantamentoAF(filtro, listaItensAdiantamento, servidorLogado);
	}
	
	private SceNotaRecebProvisorio receberItensAdiantamentoAF(FiltroRecebeMaterialServicoSemAFVO filtro,
			List<ItensRecebimentoAdiantamentoVO> listaItensAdiantamento, RapServidores servidorLogado) {
		
		boolean deveReceber = false;
		for (ItensRecebimentoAdiantamentoVO item : listaItensAdiantamento) {
			// Se pelo menos 1 item possuir quantidade entregue, realiza o recebimento.
			if (possuiValorAdiantamento(item)) {
				deveReceber = true;
				break;
			}
		}
		
		if (deveReceber) {
			// I1
			SceEntrSaidSemLicitacao entrSaidSemLicitacao = gerarSceEntrSaidSemLicitacao(filtro, servidorLogado);
			// I3
			SceNotaRecebProvisorio notaRecebimentoProvisorio = atualizarNotaRecebProvisorio(filtro, servidorLogado, entrSaidSemLicitacao);
			
			for (ItensRecebimentoAdiantamentoVO item : listaItensAdiantamento) {
				if (possuiValorAdiantamento(item)) {
					// I2
					SceItemEntrSaidSemLicitacao itemEntrSaidSemLicitacao = gerarSceItemEntrSaidSemLicitacaoAdiantamentoAF(item, entrSaidSemLicitacao);
					// I4
					atualizarItemNotaRecebProvisorioAdiantamentoAF(item, filtro, notaRecebimentoProvisorio, entrSaidSemLicitacao, itemEntrSaidSemLicitacao);
				}
			}
			return notaRecebimentoProvisorio;
		}
		return null;
	}
	
	public SceNotaRecebProvisorio preReceberItensMateriais(FiltroRecebeMaterialServicoSemAFVO filtro,
			List<ItensRecebimentoVO> listaItensMateriais,final String usuarioLogado,
			RapServidores servidorLogado) throws ApplicationBusinessException {
		// RN01 e RN05
		if(filtro.getDocumentoFiscalEntrada() != null) {
			validarValorComprometidoSuperiorValorTotalNota(filtro.getValorComprometidoNF(), filtro.getValorNota());
		} else {
			// RN06
			validarObrigatoriedadeNFEParaRecebimento();
		}
		for (ItensRecebimentoVO item : listaItensMateriais) {
			// RN08
			validarDoacaoPatrimonioImobilizado(filtro.getTipoMovimento().getSigla(), item.getCodigoMaterial(),
					item.getValorUnitario() != null ? new BigDecimal(item.getValorUnitario()) : null);
		}
		return receberItensMateriais(filtro, listaItensMateriais, servidorLogado);
	}
	
	public SceNotaRecebProvisorio preReceberItensMateriais(FiltroRecebeMaterialServicoSemAFVO filtro,
			List<ItensRecebimentoVO> listaItensMateriais) throws ApplicationBusinessException {
		// RN01 e RN05
		if(filtro.getDocumentoFiscalEntrada() != null) {
			validarValorComprometidoSuperiorValorTotalNota(filtro.getValorComprometidoNF(), filtro.getValorNota());
		} else {
			// RN06
			validarObrigatoriedadeNFEParaRecebimento();
		}
		for (ItensRecebimentoVO item : listaItensMateriais) {
			// RN08
			validarDoacaoPatrimonioImobilizado(filtro.getTipoMovimento().getSigla(), item.getCodigoMaterial(),
					item.getValorUnitario() != null ? new BigDecimal(item.getValorUnitario()) : null);
		}
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		return receberItensMateriais(filtro, listaItensMateriais, servidorLogado);
	}
	
	private SceNotaRecebProvisorio receberItensMateriais(FiltroRecebeMaterialServicoSemAFVO filtro,
			List<ItensRecebimentoVO> listaItens, RapServidores servidorLogado) {
		
		boolean deveReceber = false;
		for (ItensRecebimentoVO item : listaItens) {
			if (possuiValorMaterial(item)) {
				deveReceber = true;
				break;
			}
		}
		
		if (deveReceber) {
			// I1
			SceEntrSaidSemLicitacao entrSaidSemLicitacao = gerarSceEntrSaidSemLicitacao(filtro, servidorLogado);
			// I3
			SceNotaRecebProvisorio notaRecebimentoProvisorio = atualizarNotaRecebProvisorio(filtro, servidorLogado, entrSaidSemLicitacao);
			
			Integer nroItem = 1;
			for (ItensRecebimentoVO item : listaItens) {
				if (possuiValorMaterial(item)) {
					// I2
					SceItemEntrSaidSemLicitacao itemEntrSaidSemLicitacao = gerarSceItemEntrSaidSemLicitacaoMateriais(item, entrSaidSemLicitacao);
					// I4
					SceItemRecebProvisorio itemRecebProvisorio = atualizarItemNotaRecebProvisorioMateriais(item, filtro,
							notaRecebimentoProvisorio, entrSaidSemLicitacao, itemEntrSaidSemLicitacao, nroItem);
					
					nroItem = itemRecebProvisorio.getId().getNroItem() + 1;
				}
			}
			return notaRecebimentoProvisorio;
		}
		return null;
	}
	
	/**
	 * Indica se quantidade é maior que zero.
	 * 
	 * @param item adiantamento
	 * @return Flag
	 */
	public boolean possuiValorAdiantamento(ItensRecebimentoAdiantamentoVO item) {
		return ((item.getQtdEntregue() != null && item.getQtdEntregue().intValue() > 0)
				&& (item.getValorTotal() != null && item.getValorTotal().intValue() > 0));
	}
	
	/**
	 * Indica se quantidade é maior que zero.
	 * 
	 * @param item adiantamento
	 * @return Flag
	 */
	public boolean possuiValorMaterial(ItensRecebimentoVO item) {
		return ((item.getQtdEntregue() != null && item.getQtdEntregue().intValue() > 0)
				&& (item.getValorUnitario() != null && new BigDecimal(item.getValorUnitario()).compareTo(BigDecimal.ZERO) > 0)
				&& (item.getValorTotal() != null && item.getValorTotal().intValue() > 0));
	}
	
	private SceEntrSaidSemLicitacao gerarSceEntrSaidSemLicitacao(FiltroRecebeMaterialServicoSemAFVO filtro, RapServidores servidorLogado) {
		
		SceEntrSaidSemLicitacao entrSaidSemLicitacao = new SceEntrSaidSemLicitacao();
		entrSaidSemLicitacao.setDtGeracao(new Date());
		entrSaidSemLicitacao.setServidor(servidorLogado);
		entrSaidSemLicitacao.setSceTipoMovimento(filtro.getTipoMovimento());
		entrSaidSemLicitacao.setIndAdiantamentoAf(filtro.getIndAdiantamentoAF() ? "S" : "N");
		entrSaidSemLicitacao.setDtPrevEntrega(null);
		entrSaidSemLicitacao.setSceDocumentoFiscalEntrada(filtro.getDocumentoFiscalEntrada());
		entrSaidSemLicitacao.setScoFornecedor(filtro.getFornecedor());
		entrSaidSemLicitacao.setScoFornecedor2(null);
		entrSaidSemLicitacao.setScoFornecedorTransp(null);
		entrSaidSemLicitacao.setContato(null);
		entrSaidSemLicitacao.setFoneContato(null);
		entrSaidSemLicitacao.setLocalEntrega(null);
		entrSaidSemLicitacao.setIndEncerrado(Boolean.FALSE);
		entrSaidSemLicitacao.setDtEncerramento(null);
		entrSaidSemLicitacao.setServidorEncerrado(null);
		entrSaidSemLicitacao.setIndEfetivado("N");
		entrSaidSemLicitacao.setDtEfetivacao(null);
		entrSaidSemLicitacao.setServidorEfetivado(null);
		entrSaidSemLicitacao.setIndEstorno(Boolean.FALSE);
		entrSaidSemLicitacao.setDtEstorno(null);
		entrSaidSemLicitacao.setServidorEstornado(null);
		entrSaidSemLicitacao.setDtPrevDevolucao(null);
		entrSaidSemLicitacao.setIndFormaDevolucao(null);
		entrSaidSemLicitacao.setEslSeqOrigem(null);
		entrSaidSemLicitacao.setNroProjeto(null);
		entrSaidSemLicitacao.setObservacao(null);
		entrSaidSemLicitacao.setVersion(0);
		
		getSceEntrSaidSemLicitacaoDAO().persistir(entrSaidSemLicitacao);
		
		
		return entrSaidSemLicitacao;
	}
	
	protected SceEntrSaidSemLicitacaoDAO getSceEntrSaidSemLicitacaoDAO() {
		return sceEntrSaidSemLicitacaoDAO;
	}

	private SceItemEntrSaidSemLicitacao gerarSceItemEntrSaidSemLicitacaoAdiantamentoAF(ItensRecebimentoAdiantamentoVO item,
			SceEntrSaidSemLicitacao entrSaidSemLicitacao) {
		
		SceItemEntrSaidSemLicitacao itemEntrSaidSemLicitacao = new SceItemEntrSaidSemLicitacao();
		itemEntrSaidSemLicitacao.setSceEntrSaidSemLicitacao(entrSaidSemLicitacao);
		
		ScoMaterial material = getComprasFacade().obterMaterialPorId(item.getCodigoMaterial());
		itemEntrSaidSemLicitacao.setScoMaterial(material);
		itemEntrSaidSemLicitacao.setScoUnidadeMedida(getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(item.getUnidade()));
		
		ScoItemAutorizacaoForn itemAutorizacaoForn = getComprasFacade().obterItemAutorizacaoFornPorId(item.getAfnNumero(), item.getNumero());
		itemEntrSaidSemLicitacao.setItemAutorizacaoForn(itemAutorizacaoForn);
		itemEntrSaidSemLicitacao.setScoMarcaComercial(itemAutorizacaoForn.getMarcaComercial());
		itemEntrSaidSemLicitacao.setScoNomeComercial(null);
		itemEntrSaidSemLicitacao.setScoSolicitacoesCompras(null);
		itemEntrSaidSemLicitacao.setScoSolicitacoesServico(null);
		itemEntrSaidSemLicitacao.setQuantidade(item.getQtdEntregue());
		itemEntrSaidSemLicitacao.setValor(item.getValorTotal().doubleValue());
		itemEntrSaidSemLicitacao.setQtdeConsumida(0);
		itemEntrSaidSemLicitacao.setQtdeDevolvida(0);
		itemEntrSaidSemLicitacao.setIndEncerrado(Boolean.FALSE);
		itemEntrSaidSemLicitacao.setDtEncerramento(null);
		itemEntrSaidSemLicitacao.setServidorEncerrado(null);
		itemEntrSaidSemLicitacao.setIndEstorno(Boolean.FALSE);
		itemEntrSaidSemLicitacao.setDtEstorno(null);
		itemEntrSaidSemLicitacao.setServidorEstornado(null);
		itemEntrSaidSemLicitacao.setSceItemBoc(null);
		itemEntrSaidSemLicitacao.setSceItemRmps(null);
		itemEntrSaidSemLicitacao.setNroPatrimonio(null);
		itemEntrSaidSemLicitacao.setNroControleFisico(null);
		itemEntrSaidSemLicitacao.setObservacao(null);
		itemEntrSaidSemLicitacao.setDadosComplementares(null);
		itemEntrSaidSemLicitacao.setAlmoxarifados(material.getAlmoxarifado());
		itemEntrSaidSemLicitacao.setIslSeqOrigem(null);
		itemEntrSaidSemLicitacao.setIslEslSeqOrigem(null);
		itemEntrSaidSemLicitacao.setVersion(0);
		
		getSceItemEntrSaidSemLicitacaoDAO().persistir(itemEntrSaidSemLicitacao);
		
		
		return itemEntrSaidSemLicitacao;
	}
	
	private SceItemEntrSaidSemLicitacao gerarSceItemEntrSaidSemLicitacaoMateriais(ItensRecebimentoVO item,
			SceEntrSaidSemLicitacao entrSaidSemLicitacao) {
		
		SceItemEntrSaidSemLicitacao itemEntrSaidSemLicitacao = new SceItemEntrSaidSemLicitacao();
		itemEntrSaidSemLicitacao.setSceEntrSaidSemLicitacao(entrSaidSemLicitacao);
		
		ScoMaterial material = getComprasFacade().obterMaterialPorId(item.getCodigoMaterial());
		itemEntrSaidSemLicitacao.setScoMaterial(material);
		itemEntrSaidSemLicitacao.setScoUnidadeMedida(getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(item.getUnidade()));
		
		itemEntrSaidSemLicitacao.setItemAutorizacaoForn(null);
		if (item.getCodigoMarcaComercial() != null) {
			itemEntrSaidSemLicitacao.setScoMarcaComercial(getComprasFacade().buscaScoMarcaComercialPorId(item.getCodigoMarcaComercial()));
		} else {
			itemEntrSaidSemLicitacao.setScoMarcaComercial(null);
		}
		itemEntrSaidSemLicitacao.setScoNomeComercial(null);
		itemEntrSaidSemLicitacao.setScoSolicitacoesCompras(null);
		itemEntrSaidSemLicitacao.setScoSolicitacoesServico(null);
		itemEntrSaidSemLicitacao.setQuantidade(item.getQtdEntregue());
		itemEntrSaidSemLicitacao.setValor(item.getValorTotal().doubleValue());
		itemEntrSaidSemLicitacao.setQtdeConsumida(0);
		itemEntrSaidSemLicitacao.setQtdeDevolvida(0);
		itemEntrSaidSemLicitacao.setIndEncerrado(Boolean.FALSE);
		itemEntrSaidSemLicitacao.setDtEncerramento(null);
		itemEntrSaidSemLicitacao.setServidorEncerrado(null);
		itemEntrSaidSemLicitacao.setIndEstorno(Boolean.FALSE);
		itemEntrSaidSemLicitacao.setDtEstorno(null);
		itemEntrSaidSemLicitacao.setServidorEstornado(null);
		itemEntrSaidSemLicitacao.setSceItemBoc(null);
		itemEntrSaidSemLicitacao.setSceItemRmps(null);
		itemEntrSaidSemLicitacao.setNroPatrimonio(null);
		itemEntrSaidSemLicitacao.setNroControleFisico(null);
		itemEntrSaidSemLicitacao.setObservacao(null);
		itemEntrSaidSemLicitacao.setDadosComplementares(null);
		itemEntrSaidSemLicitacao.setAlmoxarifados(material.getAlmoxarifado());
		itemEntrSaidSemLicitacao.setIslSeqOrigem(null);
		itemEntrSaidSemLicitacao.setIslEslSeqOrigem(null);
		itemEntrSaidSemLicitacao.setVersion(0);
		
		getSceItemEntrSaidSemLicitacaoDAO().persistir(itemEntrSaidSemLicitacao);
		
		
		return itemEntrSaidSemLicitacao;
	}
	
	private SceNotaRecebProvisorio atualizarNotaRecebProvisorio(FiltroRecebeMaterialServicoSemAFVO filtro,
			RapServidores servidorLogado, SceEntrSaidSemLicitacao entrSaidSemLicitacao) {
		
		SceNotaRecebProvisorio notaRecebProvisorio = new SceNotaRecebProvisorio();
		notaRecebProvisorio.setSeq(getSceNotaRecebProvisorioDAO().obterProximoxSeq());
		notaRecebProvisorio.setScoAfPedido(null);
		notaRecebProvisorio.setDocumentoFiscalEntrada(filtro.getDocumentoFiscalEntrada());
		notaRecebProvisorio.setDtGeracao(new Date());
		notaRecebProvisorio.setServidor(servidorLogado);
		notaRecebProvisorio.setIndConfirmado(Boolean.FALSE);
		notaRecebProvisorio.setIndEstorno(Boolean.FALSE);
		notaRecebProvisorio.setDtEstorno(null);
		notaRecebProvisorio.setServidorEstorno(null);
		notaRecebProvisorio.setCodCfop(null);
		notaRecebProvisorio.setAutorizacaoFornecimento(filtro.getAutorizacaoForn());
		notaRecebProvisorio.setEslSeq(entrSaidSemLicitacao.getSeq());
		notaRecebProvisorio.setFrfCodigo(null);
		notaRecebProvisorio.setNumeroEmpenhoSiafi(null);
		notaRecebProvisorio.setVersion(0);
		
		getSceNotaRecebProvisorioDAO().atualizar(notaRecebProvisorio);
		
		
		return notaRecebProvisorio;
	}
	
	private void atualizarItemNotaRecebProvisorioAdiantamentoAF(ItensRecebimentoAdiantamentoVO item,
			FiltroRecebeMaterialServicoSemAFVO filtro, SceNotaRecebProvisorio notaRecebProvisorio, SceEntrSaidSemLicitacao entrSaidSemLicitacao,
			SceItemEntrSaidSemLicitacao itemEntrSaidSemLicitacao) {
		
		SceItemRecebProvisorio itemRecebProvisorio = new SceItemRecebProvisorio();

		SceItemRecebProvisorioId idItemRecebProvisorio = new SceItemRecebProvisorioId();
		idItemRecebProvisorio.setNrpSeq(notaRecebProvisorio.getSeq());
		idItemRecebProvisorio.setNroItem(item.getItlNumero().intValue());		
		itemRecebProvisorio.setId(idItemRecebProvisorio);
		
		itemRecebProvisorio.setProgEntregaItemAf(null);
		itemRecebProvisorio.setQuantidade(item.getQtdEntregue());
		itemRecebProvisorio.setValor(item.getValorTotal().doubleValue());
		
		ScoItemAutorizacaoForn itemAutorizacaoForn = getComprasFacade().obterItemAutorizacaoFornPorId(item.getAfnNumero(), item.getNumero());
		itemRecebProvisorio.setScoItensAutorizacaoForn(itemAutorizacaoForn);
		itemRecebProvisorio.setEslSeq(entrSaidSemLicitacao.getSeq());
		itemRecebProvisorio.setVersion(0);
		itemRecebProvisorio.setSceItemEntrSaidSemLicitacao(itemEntrSaidSemLicitacao);
		
		getSceItemRecebProvisorioDAO().atualizar(itemRecebProvisorio);
	}
	
	private SceItemRecebProvisorio atualizarItemNotaRecebProvisorioMateriais(ItensRecebimentoVO item,
			FiltroRecebeMaterialServicoSemAFVO filtro, SceNotaRecebProvisorio notaRecebProvisorio, SceEntrSaidSemLicitacao entrSaidSemLicitacao,
			SceItemEntrSaidSemLicitacao itemEntrSaidSemLicitacao, Integer nroItem) {
		
		SceItemRecebProvisorio itemRecebProvisorio = new SceItemRecebProvisorio();

		SceItemRecebProvisorioId idItemRecebProvisorio = new SceItemRecebProvisorioId();
		idItemRecebProvisorio.setNrpSeq(notaRecebProvisorio.getSeq());
		idItemRecebProvisorio.setNroItem(nroItem);		
		itemRecebProvisorio.setId(idItemRecebProvisorio);
		
		itemRecebProvisorio.setProgEntregaItemAf(null);
		itemRecebProvisorio.setQuantidade(item.getQtdEntregue());
		itemRecebProvisorio.setValor(item.getValorTotal().doubleValue());
		
		itemRecebProvisorio.setScoItensAutorizacaoForn(null);
		itemRecebProvisorio.setEslSeq(entrSaidSemLicitacao.getSeq());
		itemRecebProvisorio.setVersion(0);
		itemRecebProvisorio.setSceItemEntrSaidSemLicitacao(itemEntrSaidSemLicitacao);
		
		getSceItemRecebProvisorioDAO().atualizar(itemRecebProvisorio);
		
		return itemRecebProvisorio;
	}

	/**
	 * Caso o parâmetro P_CRITICA_VLR_NF (tabela agh_parametros, campo VLR_TEXTO = R ou NF e tiver sido dada entrada na Nota Fiscal não permitir
	 * efetuar recebimento, com valor superior ao valor total da nota.
	 * 
	 * RN01 e RN05 da #28585
	 * 
	 * @param seqDocFiscal
	 * @throws AGHUNegocioException
	 */
	public void validarValorComprometidoSuperiorValorTotalNota(final BigDecimal valorComprometido, final BigDecimal valorTotalNF)
		throws ApplicationBusinessException {


		String parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CRITICA_VLR_NF).getVlrTexto();

		if (PARAMETRO_R.equals(parametro) || PARAMETRO_NF.equals(parametro)) {

			if (CoreUtil.maior(valorComprometido, valorTotalNF)) {
				String prm0 = AghuNumberFormat.formatarNumeroMoeda(valorComprometido);
				String prm1 = AghuNumberFormat.formatarNumeroMoeda(valorComprometido.subtract(valorTotalNF));
				String prm2 = AghuNumberFormat.formatarNumeroMoeda(valorTotalNF);
				throw new ApplicationBusinessException(RecebimentoSemAFONExceptionCode.SOMA_RECEBIMENTOS_ULTRAPASSA_VALOR_TOTAL_NF, prm0, prm1, prm2);
			}

		}
	}

	/**
	 * Não permitir recebimento superior ao saldo do item da AF.
	 * 
	 * RN03 da #28585
	 * 
	 * @param codMaterial
	 * @param qtdInformada
	 * @param qtdSaldoAF
	 * @throws AGHUNegocioException
	 */
	public void validarQuantidadeSuperiorSaldoAF(final Integer codMaterial, final Integer qtdInformada,
			final Integer qtdSaldoAF) throws ApplicationBusinessException {
		if (CoreUtil.maior(qtdInformada, qtdSaldoAF)) {
			throw new ApplicationBusinessException(RecebimentoSemAFONExceptionCode.QUANTIDADE_INFORMADA_SUPERIOR_AO_SALDO_AF, qtdSaldoAF, codMaterial);
		}
	}

	/**
	 * Se o usuário não possui permissão para exceder o valor de variação, não permite valores fora da faixa de variação
	 * 
	 * RN04 da #28585
	 * 
	 * @param usuarioLogado
	 * @param item
	 * @param valorPercVariacao
	 * @param qtdEntrege
	 * @param valorTotal
	 * @param vlrUnitario
	 * @throws AGHUNegocioException
	 */
	public void validaValorExcedePercentualVariacao(final String usuarioLogado, final Short item, final Float valorPercVariacao,
			final Integer qtdEntrege, final BigDecimal valorTotal, final BigDecimal vlrUnitario) throws ApplicationBusinessException {

		if (!cascaFacade.usuarioTemPermissao(usuarioLogado, "excederPerVariaValor")) {

			BigDecimal percentualVariacao = BigDecimal.valueOf(valorPercVariacao);
			BigDecimal quantidadeEntrege = BigDecimal.valueOf(qtdEntrege);

			BigDecimal informado = valorTotal.divide(quantidadeEntrege);

			BigDecimal faixa = percentualVariacao.divide(BigDecimal.valueOf(100)).multiply(vlrUnitario);
			BigDecimal maximo = vlrUnitario.add(faixa);
			BigDecimal minimo = vlrUnitario.subtract(faixa);

			if (CoreUtil.maior(informado, maximo) || CoreUtil.menor(informado, minimo)) {
				String prm1 = AghuNumberFormat.formatarNumeroMoeda(valorPercVariacao);
				throw new ApplicationBusinessException(RecebimentoSemAFONExceptionCode.VALOR_UNITARIO_FORA_FAIXA_VARIACAO_PERMITIDA, item, prm1);
			}
		}
	}

	/**
	 * Valida a obrigatoriedade da seleção da nota fiscal
	 * 
	 * RN06 da #28585
	 * 
	 * @param notaFiscalSelecionada
	 * @throws AGHUNegocioException
	 */
	public void validarObrigatoriedadeNFEParaRecebimento() throws ApplicationBusinessException {


		boolean exigeNota = false;

		IParametroFacade parametroFacade = this.getParametroFacade();

		String confRecebImediata = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONF_RECEB_IMEDIATA).getVlrTexto();
		exigeNota = (DominioSimNao.S.toString().equals(confRecebImediata));

		if (!exigeNota) {
			String exigeNfNoRecebimento = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXIGE_NF_NO_RECEBIMENTO).getVlrTexto();
			exigeNota = (DominioSimNao.S.toString().equals(exigeNfNoRecebimento));
		}

		if (exigeNota) {
			throw new ApplicationBusinessException(RecebimentoSemAFONExceptionCode.NOTA_OBRIGATORIA_EFETUAR_RECEBIMENTO);
		}
	}

	/**
	 * Valida se foi selecionado doaçao de um patrimônio imobilizado
	 * 
	 * RN08 da #28585
	 * 
	 * @param siglaTipo
	 * @param codMaterial
	 * @param valorUnitario
	 * @throws AGHUNegocioException
	 */
	public void validarDoacaoPatrimonioImobilizado(final String siglaTipo, final Integer codMaterial, final BigDecimal valorUnitario)
			throws ApplicationBusinessException {

		if (SIGLA_DOACAO.equals(siglaTipo)) {
			boolean imobilizado = getConfirmacaoDevolucaoON().verificarMaterialImobilizado(codMaterial, valorUnitario);
			if (imobilizado) {
				throw new ApplicationBusinessException(RecebimentoSemAFONExceptionCode.PARA_DOACAO_PATR_GERAR_PROC_COMP_MOD_DOACAO);
			}
		}
	}


		
	
	
	

	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	protected void setCascaFacade(ICascaFacade cascaFacade) {
		this.cascaFacade = cascaFacade;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected void setSceEntrSaidSemLicitacaoDAO(SceEntrSaidSemLicitacaoDAO sceEntrSaidSemLicitacaoDAO) {
		this.sceEntrSaidSemLicitacaoDAO = sceEntrSaidSemLicitacaoDAO;
	}

	protected ConfirmacaoDevolucaoON getConfirmacaoDevolucaoON() {
		return confirmacaoDevolucaoON;
	}

	protected void setConfirmacaoDevolucaoON(ConfirmacaoDevolucaoON confirmacaoDevolucaoON) {
		this.confirmacaoDevolucaoON = confirmacaoDevolucaoON;
	}

	protected SceItemEntrSaidSemLicitacaoDAO getSceItemEntrSaidSemLicitacaoDAO() {
		return sceItemEntrSaidSemLicitacaoDAO;
	}

	protected void setSceItemEntrSaidSemLicitacaoDAO(SceItemEntrSaidSemLicitacaoDAO sceItemEntrSaidSemLicitacaoDAO) {
		this.sceItemEntrSaidSemLicitacaoDAO = sceItemEntrSaidSemLicitacaoDAO;
	}

	protected SceNotaRecebProvisorioDAO getSceNotaRecebProvisorioDAO() {
		return sceNotaRecebProvisorioDAO;
	}

	protected void setSceNotaRecebProvisorioDAO(SceNotaRecebProvisorioDAO sceNotaRecebProvisorioDAO) {
		this.sceNotaRecebProvisorioDAO = sceNotaRecebProvisorioDAO;
	}

	protected SceItemRecebProvisorioDAO getSceItemRecebProvisorioDAO() {
		return sceItemRecebProvisorioDAO;
	}

	protected void setSceItemRecebProvisorioDAO(SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO) {
		this.sceItemRecebProvisorioDAO = sceItemRecebProvisorioDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}
	
	
	
	
}
