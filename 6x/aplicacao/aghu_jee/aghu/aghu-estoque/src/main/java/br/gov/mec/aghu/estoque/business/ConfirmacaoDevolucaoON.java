package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
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
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.dao.SceBoletimOcorrenciasDAO;
import br.gov.mec.aghu.estoque.dao.SceDocumentoFiscalEntradaDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceItemBocDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecbXProgrEntregaDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.vo.PendenciasDevolucaoVO;
import br.gov.mec.aghu.estoque.vo.SceItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceItemBoc;
import br.gov.mec.aghu.model.SceItemDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemDevolucaoFornecedorId;
import br.gov.mec.aghu.model.SceItemRecbXProgrEntrega;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConfirmacaoDevolucaoON extends BaseBusiness {

	@EJB
	private SceDevolucaoFornecedorRN sceDevolucaoFornecedorRN;

	@EJB
	private SceItemRecbXProgrEntregaRN sceItemRecbXProgrEntregaRN;
	
	@EJB
	private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;
	
	@EJB
	private SceItemDevolucaoFornecedorRN sceItemDevolucaoFornecedorRN;
	
	@EJB
	private SceBoletimOcorrenciasRN sceBoletimOcorrenciasRN;
	
	@EJB
	private SceTipoMovimentosRN sceTipoMovimentosRN;
	
	@EJB
	private SceHistoricoProblemaMaterialRN sceHistoricoProblemaMaterialRN;
	
	private static final Log LOG = LogFactory.getLog(ConfirmacaoDevolucaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceNotaRecebimentoDAO sceNotaRecebimentoDAO;
	
	@Inject
	private SceNotaRecebProvisorioDAO sceNotaRecebProvisorioDAO;
	
	@Inject
	private SceDocumentoFiscalEntradaDAO sceDocumentoFiscalEntradaDAO;
	
	@Inject
	private SceBoletimOcorrenciasDAO sceBoletimOcorrenciasDAO;
	
	@Inject
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;

	@Inject
	private SceItemRecbXProgrEntregaDAO sceItemRecbXProgrEntregaDAO;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@Inject
	private SceItemBocDAO sceItemBocDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	private static final long serialVersionUID = 5959174924562412031L;

	public enum ConfirmacaoDevolucaoONONExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG001, MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG002, MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG003,
		MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG004a,MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG004b,MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG005,
		MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG006a,MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG006b, MENSAGEM_FIXA_DFS,
		MENSAGEM_ITEM_AF_NAO_ENCONTRADO, MENSAGEM_QTDE_INSUFICIENTE_ESTORNO;
	}

	/**
	 * Pesquisa Pendencias de Confirmacao de Devolucao ao Fornecedor pelo numero da NR
	 * @param numeroNr
	 * @return List
	 */	
	public List<PendenciasDevolucaoVO> pesquisarPendenciasDevolucao(Integer numeroNr) {
		List<PendenciasDevolucaoVO> listaRetorno = new ArrayList<PendenciasDevolucaoVO>();
		
		List<SceItemBoc> listaItensBoc = this.getSceItemBocDAO().pesquisarPendenciasDevolucao(numeroNr);
		for (SceItemBoc item : listaItensBoc) {
			PendenciasDevolucaoVO pendencia = new PendenciasDevolucaoVO();
			pendencia.setNroItem(item.getId().getNroItem());
			pendencia.setQtdeSaida(item.getQtde());
			pendencia.setValorTotal(item.getValor());
			pendencia.setDescricao(item.getDescricao());
			pendencia.setBocSeq(item.getId().getBocSeq());
			pendencia.setValorUnitario(item.getValor().divide(new BigDecimal(item.getQtde()), 2, RoundingMode.HALF_EVEN));
			
			if (item.getMatCodigo() != null) {
				ScoMaterial material = this.getComprasFacade().obterMaterialPorId(item.getMatCodigo());
				
				if (material != null) {
					pendencia.setMatCodigo(material.getCodigo());
					pendencia.setNomeMaterial(material.getNome());
					pendencia.setUnidadeMaterial(material.getUmdCodigo());
					pendencia.setDescricaoMaterial(material.getDescricao());
				}
				
				Long qtdeEmBo = this.getSceItemBocDAO().contarNrEmBoletimOcorrencia(numeroNr, pendencia.getMatCodigo());
				Long qtdeEntrada = this.getSceNotaRecebimentoDAO().contarQtdeEntradaPorNr(numeroNr, material.getCodigo());
				Long qtdeSaldo = qtdeEntrada - qtdeEmBo;

				pendencia.setQtdEntrada(qtdeEntrada.intValue());
				pendencia.setQtdSaldo(qtdeSaldo.intValue());
			} 	
			
			listaRetorno.add(pendencia);
		}
		
		return listaRetorno;
	}
	
	private SceDevolucaoFornecedor inserirDevolucaoFornecedor(Long numeroDfs, String serieDfs, Date dataDfs, Integer seqDocFiscalEntrada, Integer tmvDocDf, Integer bocSeq) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		String motivo = getResourceBundleValue(ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_FIXA_DFS
						.toString());
		
		SceDevolucaoFornecedor devolucaoFornecedor = new SceDevolucaoFornecedor();
		devolucaoFornecedor.setNroNfDevolucao(numeroDfs.intValue());
		devolucaoFornecedor.setSerieNfDevolucao(serieDfs);
		devolucaoFornecedor.setDtGeracao(new Timestamp((new Date()).getTime()));
		devolucaoFornecedor.setDtEmissaoNfd(new Timestamp(dataDfs.getTime()));
		devolucaoFornecedor.setIndEstorno(Boolean.FALSE);
		devolucaoFornecedor.setDtFechamentoNfd(null);
		devolucaoFornecedor.setDtEstorno(null);
		devolucaoFornecedor.setMotivo(motivo);
		devolucaoFornecedor.setTmvSeq(tmvDocDf);
		devolucaoFornecedor.setServidor(servidorLogado);
		devolucaoFornecedor.setServidorEstornado(null);
		devolucaoFornecedor.setIndGerado(DominioSimNao.N.toString());
		devolucaoFornecedor.setIndTramiteInterno(Boolean.FALSE);
		devolucaoFornecedor.setBocSeq(bocSeq);
		
		SceDocumentoFiscalEntrada dfe = this.getSceDocumentoFiscalEntradaDAO().obterPorChavePrimaria(seqDocFiscalEntrada);
		if (dfe != null) {
			devolucaoFornecedor.setSceDocumentoFiscalEntrada(dfe);
		}
		
		SceTipoMovimento tipoMovimento = this.getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(tmvDocDf.shortValue());
		if (tipoMovimento != null) {
			devolucaoFornecedor.setTmvComplemento(tipoMovimento.getId().getComplemento().intValue());
		}
		
		this.getSceDevolucaoFornecedorRN().inserir(devolucaoFornecedor, true);
		
		return devolucaoFornecedor;
	}
			
	/**
	 * Realiza a confirmacao da devolucao ao fornecedor retornando os itens confirmados com sucesso
	 * @param listaPendencias
	 * @param seqDfe
	 * @param seqNotaRecebimento
	 * @param numeroDfs
	 * @param serieDfs
	 * @param dataDfs
	 * @param nomeMicroComputador
	 * @return List
	 * @throws BaseException
	 */
	public List<String> confirmarDevolucaoFornecedor(List<PendenciasDevolucaoVO> listaPendencias, Integer seqDfe,
			Integer seqNotaRecebimento, Long numeroDfs, String serieDfs, Date dataDfs, String nomeMicroComputador) throws BaseException {
		Set<String> listaRetorno = new HashSet<String>();
		
		if (numeroDfs == null) {
			throw new ApplicationBusinessException(
					ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG001);								
		}
		
		if (serieDfs == null) {
			throw new ApplicationBusinessException(
					ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG002);
		}
		
		if (dataDfs == null) {
			throw new ApplicationBusinessException(
					ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG003);
		}
		
		if (listaPendencias == null || listaPendencias.isEmpty()) {
			throw new ApplicationBusinessException(
					ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG005);
		}
		
		Short almoxCentral = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL).getVlrNumerico().shortValue();
		Integer fornecedorPadrao = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO).getVlrNumerico().intValue();
		Integer tmvDocDf = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TMV_DOC_DF).getVlrNumerico().intValue();
				
		SceDevolucaoFornecedor devolucaoFornecedor = this.inserirDevolucaoFornecedor(
				numeroDfs, serieDfs, dataDfs, seqDfe, tmvDocDf, listaPendencias.get(0).getBocSeq());

		for (PendenciasDevolucaoVO itemPendencia : listaPendencias) {
			ScoMaterial material = this.getComprasFacade().obterScoMaterialPorChavePrimaria(itemPendencia.getMatCodigo());
			
			Short almoxSeq = almoxCentral;
			
			if (this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DF_ORIG_LOC_ESTOQUE).getVlrTexto().equals("S")) {
				almoxSeq = material.getAlmoxarifado().getSeq();
			}
			
			SceEstoqueAlmoxarifado estoqueAlmox = this.getSceEstoqueAlmoxarifadoDAO().buscarSceEstoqueAlmoxarifadoPorAlmoxarifado(almoxSeq, itemPendencia.getMatCodigo(), fornecedorPadrao);
			
			SceItemDevolucaoFornecedorId idItemDfs = new SceItemDevolucaoFornecedorId();
			idItemDfs.setDfsSeq(devolucaoFornecedor.getSeq());
			idItemDfs.setNumero(itemPendencia.getNroItem().intValue());
			SceItemDevolucaoFornecedor itemDfs = new SceItemDevolucaoFornecedor();
			itemDfs.setId(idItemDfs);
			itemDfs.setValor(itemPendencia.getValorTotal().doubleValue());
			itemDfs.setQuantidade(itemPendencia.getQtdeSaida().intValue());
			itemDfs.setUmdCodigo(material.getUmdCodigo());
			itemDfs.setEalSeq(estoqueAlmox.getSeq());
			itemDfs.setSceDevolucaoFornecedor(devolucaoFornecedor);
			
			this.getSceItemDevolucaoFornecedorRN().inserir(itemDfs, material,  
					nomeMicroComputador, tmvDocDf, seqNotaRecebimento);
			
			listaRetorno.add(this.montarMensagemConfirmacaoDevolucao(devolucaoFornecedor.getSeq()));
		}
		
		if (listaPendencias != null && listaPendencias.size() > 0) {
			this.atualizarParcelasEntrega(devolucaoFornecedor, seqNotaRecebimento);
			this.atualizarBoletimOcorrencia(listaPendencias.get(0).getBocSeq());
		}

		return new ArrayList<String>(listaRetorno);
	}
	
	/**
	 * Atualiza a parte das parcelas de entrega
	 * @param itemDf
	 * @param numeroNr
	 * @param servidorLogado
	 * @throws MECBaseException
	 */
	private void atualizarParcelasEntrega(SceDevolucaoFornecedor df, Integer numeroNr) throws BaseException {

		List<SceItemRecebimentoProvisorioVO> listaItemRecebProvisorio = this.getSceItemRecebProvisorioDAO().pesquisarItemRecebProvisorioPorSeqNrp(df.getSeq());

		for (SceItemRecebimentoProvisorioVO itemVO : listaItemRecebProvisorio) {

			Long qtdeSaldoIdf = itemVO.getIdfQuantidade().longValue();

			List<SceItemRecbXProgrEntrega> listaItemRecebXProgEntrega = this.getSceItemRecbXProgrEntregaDAO().
					pesquisarItemRecbXProgrEntregaPorSeqNrpEItemAf(itemVO.getIrpNrpSeq(), itemVO.getIrpNroItem().shortValue(), 
							itemVO.getPeaIafAfnNumero(), itemVO.getPeaIafNumero().shortValue());

			for (SceItemRecbXProgrEntrega itemRecebXProg : listaItemRecebXProgEntrega) {
				Long qtdEstornada = (Long) CoreUtil.nvl(itemRecebXProg.getQtdeEstornada(), Long.valueOf(0));
				Long qtdEntregue = (Long) CoreUtil.nvl(itemRecebXProg.getQtdeEntregue(), Long.valueOf(0));
				Long qtdeSaldoIpp =  qtdEntregue - qtdEstornada;
				Long qtdeEstorno = Long.valueOf(0);

				if (qtdeSaldoIdf > 0) {
					if (qtdeSaldoIdf <= qtdeSaldoIpp) {
						qtdeEstorno = qtdeSaldoIdf;
						qtdeSaldoIdf = Long.valueOf(0);
					}
				} else {
					qtdeEstorno = qtdeSaldoIpp;
					qtdeSaldoIdf = qtdeSaldoIdf - qtdeSaldoIpp;
				}

				if (qtdeEstorno > 0) {
					ScoItemAutorizacaoForn itemAf = this.getComprasFacade().obterItemAutorizacaoFornPorId(itemVO.getPeaIafAfnNumero(), itemVO.getPeaIafNumero());

					if (itemAf == null) {
						throw new BaseException(ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_ITEM_AF_NAO_ENCONTRADO);
					}

					Double vlrEstorno = qtdeEstorno * itemAf.getValorUnitario();
					Integer qtdeEntregue = this.getAutFornecimentoFacade().calcularTotalEntreguePorItemAf(
							itemRecebXProg.getScoProgEntregaItemAutorizacaoFornecimento().getId().getIafAfnNumero(),
							itemRecebXProg.getScoProgEntregaItemAutorizacaoFornecimento().getId().getIafNumero(), 
							itemRecebXProg.getScoProgEntregaItemAutorizacaoFornecimento().getId().getParcela());

					if (qtdeEstorno > qtdeEntregue) {
						throw new BaseException(ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_QTDE_INSUFICIENTE_ESTORNO, 
								itemRecebXProg.getScoProgEntregaItemAutorizacaoFornecimento().getId().getParcela(), qtdeEstorno);
					}

					this.atualizarItemRecebxProgEntrega(itemRecebXProg, qtdEstornada);
					if (itemRecebXProg.getScoProgEntregaItemAutorizacaoFornecimento() != null) {
						this.atualizarProgEntregaItemAf(itemRecebXProg.getScoProgEntregaItemAutorizacaoFornecimento(), qtdeEstorno, vlrEstorno);
					}

					if (itemRecebXProg.getScoSolicitacaoProgramacaoEntrega() != null) {
						this.atualizarSolicProgEntrega(itemRecebXProg.getScoSolicitacaoProgramacaoEntrega().getSeq(), qtdeEstorno);
					}
				}
			}
		}
	}

	private void atualizarProgEntregaItemAf(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAf, Long qtdEstorno, Double vlrEstorno) throws ApplicationBusinessException {
		ScoProgEntregaItemAutorizacaoFornecimento progEntrega = this.getAutFornecimentoFacade().obterProgEntregaPorChavePrimaria(progEntregaItemAf.getId());

		if (progEntrega != null) {
			progEntrega.setQtdeEntregue((Integer)CoreUtil.nvl(progEntrega.getQtdeEntregue(), 0) - qtdEstorno.intValue());
			progEntrega.setValorEfetivado((Double)CoreUtil.nvl(progEntrega.getValorEfetivado(), 0.00) - vlrEstorno);

			this.getAutFornecimentoFacade().persistirProgEntregaItemAf(progEntrega);
		}
	}

	private void atualizarSolicProgEntrega(Long speSeq, Long qtdEstorno) throws BaseException {
		ScoSolicitacaoProgramacaoEntrega solicProgEntrega = this.getAutFornecimentoFacade().obterSolicitacaoProgEntregaPorId(speSeq);

		if (solicProgEntrega != null) {
			solicProgEntrega.setQtdeEntregue((Integer) CoreUtil.nvl(
					solicProgEntrega.getQtdeEntregue(), 0)
					- qtdEstorno.intValue());
			this.getAutFornecimentoFacade().persistir(solicProgEntrega);
		}
	}

	private void atualizarItemRecebxProgEntrega(SceItemRecbXProgrEntrega itemRecebXProg, Long qtdeEstornada) throws BaseException {
		SceItemRecbXProgrEntrega item = this.getSceItemRecbXProgrEntregaDAO().obterPorChavePrimaria(itemRecebXProg.getSeq());

		if (item != null) {
			item.setQtdeEstornada(qtdeEstornada+(Long)CoreUtil.nvl(item.getQtdeEstornada(), Long.valueOf(0)));
			item.setIndEstornado(Boolean.TRUE);

			this.getSceItemRecbXProgrEntregaRN().atualizar(item);
		}
	}

	private void atualizarBoletimOcorrencia(Integer bocSeq) throws ApplicationBusinessException {
		SceBoletimOcorrencias boletim = this.getSceBoletimOcorrenciasDAO().obterPorChavePrimaria(bocSeq);

		if (boletim != null) {
			boletim.setSituacao(DominioBoletimOcorrencias.E);
			this.getSceBoletimOcorrenciasRN().atualizar(boletim);
		}
	}
	
	/**
	 * Realiza o cancelamento da Devolucao ao Fornecedor retornando os itens cancelados com sucesso
	 * @param listaPendencias
	 * @param numeroDfs
	 * @param serieDfs
	 * @param dataDfs
	 * @param nomeMicroComputador
	 * @param seqNrp
	 * @return List
	 * @throws BaseException
	 */
	public List<String> cancelarDevolucaoFornecedor(List<PendenciasDevolucaoVO> listaPendencias, Long numeroDfs, String serieDfs, Date dataDfs,
			String nomeMicroComputador, Integer seqNrp) throws BaseException {
		Set<String> listaRetorno = new HashSet<String>();
		
		Short almoxCentral = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL).getVlrNumerico().shortValue();
		Short almoxSeq = almoxCentral;
		Integer fornecedorPadrao = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO).getVlrNumerico().intValue();

		for (PendenciasDevolucaoVO item : listaPendencias) {
			if (this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DF_ORIG_LOC_ESTOQUE).getVlrTexto().equals("S")) {
				ScoMaterial material = this.getComprasFacade().obterMaterialPorId(item.getMatCodigo());

				if (material != null) {
					almoxSeq = material.getAlmoxarifado().getSeq();
				}
			}

			SceEstoqueAlmoxarifado estoqueAlmox = this.getSceEstoqueAlmoxarifadoDAO().buscarSceEstoqueAlmoxarifadoPorAlmoxarifado(almoxSeq, item.getMatCodigo(), fornecedorPadrao);

			estoqueAlmox.setQtdeBloqueada(estoqueAlmox.getQtdeBloqueada()+item.getQtdeSaida().intValue());
			try {
				this.getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmox, nomeMicroComputador, false);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(e);
			}
			
			SceBoletimOcorrencias boletim = this.getSceBoletimOcorrenciasDAO().obterPorChavePrimaria(item.getBocSeq());
			
			Integer numeroAf = this.getSceNotaRecebProvisorioDAO().obterNumeroAfPorNumeroRecebProvisorio(seqNrp);
			
			if (boletim != null && numeroAf != null) {
				ScoFornecedor fornecedor = this.getComprasFacade().obterFornecedorPorNumeroAf(numeroAf);
				
				if (fornecedor != null) {
					for (SceItemBoc itemBoc : this.getSceItemBocDAO().pesquisarItemBoletimOcorrenciaPorBocSeq(boletim.getSeq())) {
						List<SceHistoricoProblemaMaterial> listaHistoricos = this.getSceHistoricoProblemaMaterialDAO().
								pesquisarHistoricoProblemaMaterialPorFornecedorQtdProblema(fornecedor.getNumero(), estoqueAlmox.getSeq(), 
										itemBoc.getQtde().intValue(), null);
						for (SceHistoricoProblemaMaterial hist : listaHistoricos) {
							hist.setQtdeDesbloqueada(hist.getQtdeProblema());
							try {
								this.getSceHistoricoProblemaMaterialRN().atualizar(hist, false);
							} catch (BaseException e) {
								throw new ApplicationBusinessException(e);
							}
						}	
					}
				}
			}
			
			boletim.setSituacao(DominioBoletimOcorrencias.H);
			
			this.getSceBoletimOcorrenciasRN().atualizar(boletim);
			
			listaRetorno.add(this.montarMensagemCancelamentoDevolucao(boletim.getSeq()));
		}
		
		return new ArrayList<String>(listaRetorno);
	}
	
	private String montarMensagemCancelamentoDevolucao(Integer bocSeq) {
		String prefixo = getResourceBundleValue(ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG004a
						.toString());
		
		String sufixo = getResourceBundleValue(ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG004b
						.toString());
		
		StringBuilder msg = new StringBuilder();
		msg.append(prefixo).append(bocSeq).append(sufixo);
		
		return msg.toString();
	}
	
	private String montarMensagemConfirmacaoDevolucao(Integer dfsSeq) {
		String prefixo = getResourceBundleValue(ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG006a
						.toString());
		
		String sufixo = getResourceBundleValue(ConfirmacaoDevolucaoONONExceptionCode.MENSAGEM_CONFIRMACAO_DEVOLUCAO_MSG006b
						.toString());
		
		StringBuilder msg = new StringBuilder();
		msg.append(prefixo).append(dfsSeq).append(sufixo);
		
		return msg.toString();
	}
	
	/**
	 * Verifica se o material eh classificado como imobilizado
	 * @param matCodigo
	 * @param vlrUnitario
	 * @return Boolean
	 * @throws ApplicationBusinessException 
	 */
	protected Boolean verificarMaterialImobilizado(Integer matCodigo, BigDecimal vlrUnitario) throws ApplicationBusinessException {
		Boolean ret = Boolean.FALSE;
		if (this.getComprasFacade().verificarMaterialImobilizado(matCodigo)) {
			AghParametros paramValorLimitePequenoPorte = this.getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_VLR_LIM_MAT_PEQ_PORTE);
			
			if (vlrUnitario.compareTo(paramValorLimitePequenoPorte.getVlrNumerico().divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_EVEN)) > 0) {
				ret = Boolean.TRUE;
			}
		}
	
		return ret;
	}
	
	private SceItemBocDAO getSceItemBocDAO() {
		return sceItemBocDAO;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	private SceBoletimOcorrenciasDAO getSceBoletimOcorrenciasDAO() {
		return sceBoletimOcorrenciasDAO;
	}
	
	private SceNotaRecebimentoDAO getSceNotaRecebimentoDAO() {
		return sceNotaRecebimentoDAO;
	}
	
	private SceBoletimOcorrenciasRN getSceBoletimOcorrenciasRN() {
		return sceBoletimOcorrenciasRN;
	}
	
	private SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	private SceDocumentoFiscalEntradaDAO getSceDocumentoFiscalEntradaDAO() {
		return sceDocumentoFiscalEntradaDAO;
	}
		
	private SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN() {
		return sceEstoqueAlmoxarifadoRN;
	}
	
	private SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO() {
		return sceHistoricoProblemaMaterialDAO;
	}
	
	private SceHistoricoProblemaMaterialRN getSceHistoricoProblemaMaterialRN() {
		return sceHistoricoProblemaMaterialRN;
	}
	
	private SceDevolucaoFornecedorRN getSceDevolucaoFornecedorRN() {
		return sceDevolucaoFornecedorRN;
	}
	
	private SceTipoMovimentosRN getSceTipoMovimentosRN() {
		return sceTipoMovimentosRN;
	}
	
	private SceItemDevolucaoFornecedorRN getSceItemDevolucaoFornecedorRN() {
		return sceItemDevolucaoFornecedorRN;
	}
	
	private SceNotaRecebProvisorioDAO getSceNotaRecebProvisorioDAO() {
		return sceNotaRecebProvisorioDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public SceItemRecebProvisorioDAO getSceItemRecebProvisorioDAO() {
		return sceItemRecebProvisorioDAO;
	}

	public void setSceItemRecebProvisorioDAO(SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO) {
		this.sceItemRecebProvisorioDAO = sceItemRecebProvisorioDAO;
	}

	public SceItemRecbXProgrEntregaDAO getSceItemRecbXProgrEntregaDAO() {
		return sceItemRecbXProgrEntregaDAO;
	}

	public void setSceItemRecbXProgrEntregaDAO(
			SceItemRecbXProgrEntregaDAO sceItemRecbXProgrEntregaDAO) {
		this.sceItemRecbXProgrEntregaDAO = sceItemRecbXProgrEntregaDAO;
	}

	public IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	public void setAutFornecimentoFacade(
			IAutFornecimentoFacade autFornecimentoFacade) {
		this.autFornecimentoFacade = autFornecimentoFacade;
	}

	public SceItemRecbXProgrEntregaRN getSceItemRecbXProgrEntregaRN() {
		return sceItemRecbXProgrEntregaRN;
	}

	public void setSceItemRecbXProgrEntregaRN(
			SceItemRecbXProgrEntregaRN sceItemRecbXProgrEntregaRN) {
		this.sceItemRecbXProgrEntregaRN = sceItemRecbXProgrEntregaRN;
	}

}
