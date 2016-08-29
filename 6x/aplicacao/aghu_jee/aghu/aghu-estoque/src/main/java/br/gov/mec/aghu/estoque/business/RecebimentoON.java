package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceMotivoProblemaDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.vo.ItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.estoque.vo.RecebimentoFiltroVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceItemBoc;
import br.gov.mec.aghu.model.SceItemBocId;
import br.gov.mec.aghu.model.SceItemDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceItemDevolucaoFornecedorId;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RecebimentoON extends BaseBusiness {


@EJB
private ConfirmacaoDevolucaoON confirmacaoDevolucaoON;

	@EJB
	private SceItemBocRN sceItemBocRN;
	
	@EJB
	private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;
	
	@EJB
	private SceDevolucaoFornecedorRN sceDevolucaoFornecedorRN;
	
	@EJB
	private ConfirmacaoRecebimentoON confirmacaoRecebimentoON;
	
	@EJB
	private SceItemDevolucaoFornecedorRN sceItemDevolucaoFornecedorRN;
	
	@EJB
	private SceItemNotaRecebimentoDevolucaoFornecedorRN sceItemNotaRecebimentoDevolucaoFornecedorRN;
	
	@EJB
	private SceBoletimOcorrenciasRN sceBoletimOcorrenciasRN;
	
	@EJB
	private SceNotaRecebimentoRN sceNotaRecebimentoRN;
	
	@EJB
	private SceHistoricoProblemaMaterialRN sceHistoricoProblemaMaterialRN;
	
	@EJB
	private SceTipoMovimentosRN sceTipoMovimentosRN;
	
	private static final Log LOG = LogFactory.getLog(RecebimentoON.class);
	
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
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceMotivoProblemaDAO sceMotivoProblemaDAO;
	
	@Inject
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	private static final long serialVersionUID = 8919020356375253002L;

	public enum RecebimentoONExceptionCode implements BusinessExceptionCode {
		INCONSISTENCIA_NOTA_RECEBIMENTO_PROVISORIO, ERRO_CLONE_NOTA_RECEBIMENTO, ESTORNO_MATERIAL_SALDO_BLOQUEADO;
	}

	public List<SceNotaRecebProvisorio> pesquisarNotasRecebimentoProvisorio(final RecebimentoFiltroVO filtroVO, final Integer firstResult,
			final Integer maxResult, final String orderProperty, final boolean asc) {
		return this.pesquisarNotasRecebimentoProvisorio(filtroVO, firstResult, maxResult, orderProperty, asc, null);
	}

	public List<SceNotaRecebProvisorio> pesquisarNotasRecebimentoProvisorio(final RecebimentoFiltroVO filtroVO, final Integer firstResult,
			final Integer maxResult, final String orderProperty, final boolean asc, final List<ApplicationBusinessException> errosNotasRecebimentoProvisorio) {
		final List<SceNotaRecebProvisorio> retorno = this.getSceNotaRecebProvisorioDAO().pesquisarNotasRecebimentoProvisorio(filtroVO, firstResult, maxResult,
				orderProperty, asc);
		/* retomado o mapeamento OneToOne de nota de recebimento provisório para nota de recebimento
		if (errosNotasRecebimentoProvisorio != null) {
			// Mapeamento OneToOne não realizado pois é possível ter duas ou
			// mais ocorrências em banco, o que não deveria acontecer
			// negocialmente.
			//
			// Sendo assim, ao buscar no banco, validamos a ON e lançamos
			// mensagem de não conformidade. Ver estória #28709
			for (final SceNotaRecebProvisorio sceNotaRecebProvisorio : retorno) {
				try {
					sceNotaRecebProvisorio.setNotaRecebimento(obterNotaRecebimentoPorNotaRecebimentoProv(sceNotaRecebProvisorio.getSeq()));
				} catch (ApplicationBusinessException e) {
					errosNotasRecebimentoProvisorio.add(e);
				}
			}
		}
		*/
		return retorno;
	}

	/**
	 * Método que realiza a busca de uma Nota de recebimento para um recebimento
	 * provisório.
	 * 
	 * Mapeamento OneToOne não realizado pois é possível ter duas ou mais
	 * ocorrências em banco, o que não deveria acontecer negocialmente.
	 * 
	 * Sendo assim, ao buscar no banco, validamos a ON e lançamos mensagem de
	 * não conformidade. Ver estória #28709
	 * 
	 * @param seqNotaRecebProv
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public SceNotaRecebimento obterNotaRecebimentoPorNotaRecebimentoProv(final Integer seqNotaRecebProv) throws ApplicationBusinessException {
		final List<SceNotaRecebimento> resultado = this.getSceNotaRecebimentoDAO().pesquisarNotaRecebimentoPorNotaRecebimentoProvisorio(seqNotaRecebProv, null);
		if (resultado != null && !resultado.isEmpty()) {
			if (resultado.size() == 1) {
				return resultado.get(0);
			} else {
				final StringBuffer nrs = new StringBuffer();
				for (final SceNotaRecebimento sceNotaRecebimento : resultado) {
					nrs.append(sceNotaRecebimento.getSeq()).append(',');
				}
				throw new ApplicationBusinessException(RecebimentoONExceptionCode.INCONSISTENCIA_NOTA_RECEBIMENTO_PROVISORIO, nrs.deleteCharAt(
						nrs.length() - 1).toString());
			}
		}
		return null;
	}
	
	private boolean isMaterialNaCompetencia(final SceNotaRecebProvisorio notaRecebimentoProvisorio) throws ApplicationBusinessException {
		if (notaRecebimentoProvisorio.getNotaRecebimento() == null || notaRecebimentoProvisorio.getNotaRecebimento().getTipoMovimento() == null) {
			return false;
		}
		final AghParametros competencia = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final String vlrDataAuxStr = sdf.format(competencia.getVlrData());
		final Date date = this.getSceMovimentoMaterialDAO().obterDataCompetencia(
				notaRecebimentoProvisorio.getNotaRecebimento().getTipoMovimento().getId().getSeq(),
				notaRecebimentoProvisorio.getNotaRecebimento().getTipoMovimento().getId().getComplemento().intValue(),
				notaRecebimentoProvisorio.getNotaRecebimento().getSeq());
		if (date == null) {
			return false;
		}
		final String dataRetornoAuxStr = sdf.format(date);
		return vlrDataAuxStr.equalsIgnoreCase(dataRetornoAuxStr);
	}

	/**
	 * @param notaRecebimentoProvisorio (a coluna IND_CONFIRMADO = 'S', indica que NÃO é mais recebimento provisório)
	 * @throws BaseException
	 */
	public void estornarRecebConfirmado(SceNotaRecebProvisorio notaRecebimentoProvisorio, String nomeMicrocomputador) throws BaseException{
		
		//Obtém a nota de recebimento - SCE_NOTA_RECEBIMENTOS
		SceNotaRecebimento notaRecebimento = this.getSceNotaRecebimentoDAO().obterPorChavePrimaria(notaRecebimentoProvisorio.getNotaRecebimento().getSeq());
		
		final List<ItemRecebimentoProvisorioVO> retorno = this.getSceItemRecebProvisorioDAO().pesquisarItensNotaRecebimentoProvisorio(
								notaRecebimentoProvisorio.getSeq(), Boolean.FALSE);

		//Realiza o estorno provisório - SCE_NOTA_RECEB_PROVISORIOS
 		SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal = this.getConfirmacaoRecebimentoON()
				.clonarNotaRecebimentoProvisorio(notaRecebimentoProvisorio);
		this.getConfirmacaoRecebimentoON().estornarRecebimento(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal);
		 
		//Verifica se é serviço ou material
		if (!this.isMaterial(retorno)) {   
			//Serviço - Atualiza SCE_NOTA_RECEBIMENTOS
			this.atualizarNR(notaRecebimento, nomeMicrocomputador);
		} else {
			//Material - Verifica competência
			if (this.isMaterialNaCompetencia(notaRecebimentoProvisorio)) {
 				//Dentro da Competência - Atualiza SCE_NOTA_RECEBIMENTOS
 				this.atualizarNR(notaRecebimento, nomeMicrocomputador);
 			} else {
 				//Fora da Competência - Devolução por Tramite Interno
				this.estornarForaCompetencia(retorno, notaRecebimentoProvisorio, notaRecebimento, nomeMicrocomputador);				
 			}
		}
	}
		 
 	private void atualizarNR(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
 		//Atualiza SCE_NOTA_RECEBIMENTOS
		notaRecebimento.setEstorno(Boolean.TRUE);
		notaRecebimento.setDtEstorno(new Date());
		SceNotaRecebimento notaRecebimentoOriginal;
		try {
			notaRecebimentoOriginal = (SceNotaRecebimento) BeanUtils.cloneBean(notaRecebimento);
		} catch (Exception e) {
			throw new ApplicationBusinessException(RecebimentoONExceptionCode.ERRO_CLONE_NOTA_RECEBIMENTO);
		}
		this.getSceNotaRecebimentoRN().atualizar(notaRecebimento, notaRecebimentoOriginal, nomeMicrocomputador, true);
	}

	private void estornarForaCompetencia(List<ItemRecebimentoProvisorioVO> retorno, SceNotaRecebProvisorio notaRecebimentoProvisorio,
				SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Devolução por Tramite Interno (não gera nota fiscal de saída)
 		String pMotivoDev = this.getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_MOT_DEVOLUCAO_TRAMITE_INTERNO);
 		Integer fornecedorPadrao = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO).getVlrNumerico().intValue();
 		Short pTipoProbMatDevTraInterno = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_PROB_MAT_DEV_TRA_INTERNO).getVlrNumerico().shortValue();
 
 		//Insere Boletim Ocorrência - SCE_BOLETIM_OCORRENCIAS
 		SceBoletimOcorrencias boletimOcorrencia = new SceBoletimOcorrencias();
 		boletimOcorrencia.setDtGeracao(new Date());
		boletimOcorrencia.setServidor(servidorLogado);
 		boletimOcorrencia.setSituacao(DominioBoletimOcorrencias.G);
 		boletimOcorrencia.setDocumentoFiscalEntrada(notaRecebimento.getDocumentoFiscalEntrada());
		boletimOcorrencia.setDfeSeqSaida(null);
		boletimOcorrencia.setNotaRecebimento(notaRecebimento);
    	boletimOcorrencia.setSceEntradaSaidaSemLicitacao(null);
 		boletimOcorrencia.setDescricao(pMotivoDev);
		this.getSceBoletimOcorrenciasRN().inserir(boletimOcorrencia);

		//Insere Item Boletim Ocorrência - SCE_ITEM_BOC
		if (retorno != null && !retorno.isEmpty()) {
 			for (final ItemRecebimentoProvisorioVO itemRecebimentoProvisorioVO : retorno) {
				SceItemBocId idItemBoletim = new SceItemBocId();
				idItemBoletim.setBocSeq(boletimOcorrencia.getSeq());
				idItemBoletim.setNroItem(itemRecebimentoProvisorioVO.getItlNumero());
 				SceItemBoc itemBoletim = new SceItemBoc();
 				itemBoletim.setId(idItemBoletim);
 				itemBoletim.setBoletimOcorrencia(boletimOcorrencia);
 				itemBoletim.setMatCodigo(itemRecebimentoProvisorioVO.getCodigoMaterial());
				if (itemRecebimentoProvisorioVO.getQuantidade() != null) {
					itemBoletim.setQtde(itemRecebimentoProvisorioVO.getQuantidade().longValue());
 				}
				if (itemRecebimentoProvisorioVO.getValor() != null) {
 					BigDecimal valor = new BigDecimal(itemRecebimentoProvisorioVO.getValor());
 					itemBoletim.setValor(valor);
 				}
 				itemBoletim.setDescricao(pMotivoDev);
 				this.getSceitemBocRN().inserir(itemBoletim);
 				
 				// #31569 - Atualização do Estoque
				Boolean matImobilizado = this.getConfirmacaoDevolucaoON().verificarMaterialImobilizado(itemRecebimentoProvisorioVO.getCodigoMaterial(), 
												new BigDecimal(itemRecebimentoProvisorioVO.getValor()/itemRecebimentoProvisorioVO.getQuantidade()));
				
				if (matImobilizado!=null && !matImobilizado) {
					//Obtém o material
					ScoMaterial material = this.getComprasFacade().obterScoMaterialPorChavePrimaria(itemRecebimentoProvisorioVO.getCodigoMaterial());
					//Obtém o estoque almoxarifado
					SceEstoqueAlmoxarifado estoqueAlmoxarifado= this.getSceEstoqueAlmoxarifadoDAO().
    						obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor(material.getAlmoxarifado().getSeq().shortValue(),
							itemRecebimentoProvisorioVO.getCodigoMaterial(), fornecedorPadrao);
					if (estoqueAlmoxarifado != null){
 						if (estoqueAlmoxarifado.getQtdeBloqueada() >= itemRecebimentoProvisorioVO.getQuantidade()){
 							
 							estoqueAlmoxarifado.setQtdeBloqueada(estoqueAlmoxarifado.getQtdeBloqueada() - itemRecebimentoProvisorioVO.getQuantidade());
 							this.getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);

 							//Insere Histórico Problema Material - SCE_HIST_PROBLEMA_MATERIAIS
 							SceHistoricoProblemaMaterial sceHistProbAlmox = new SceHistoricoProblemaMaterial();
 							sceHistProbAlmox.setSceEstqAlmox(estoqueAlmoxarifado);
 							sceHistProbAlmox.setDtGeracao(new Date());
 							sceHistProbAlmox.setMotivoProblema(this.getSceMotivoProblemaDAO().obterPorChavePrimaria(pTipoProbMatDevTraInterno));
 							sceHistProbAlmox.setServidor(servidorLogado);
 							sceHistProbAlmox.setQtdeDesbloqueada(0);
 							sceHistProbAlmox.setQtdeDf(0);
 							sceHistProbAlmox.setQtdeProblema(itemRecebimentoProvisorioVO.getQuantidade());
 							sceHistProbAlmox.setIndEfetivado(false);
 							sceHistProbAlmox.setNrsSeq(notaRecebimento.getSeq());						
 							sceHistProbAlmox.setFornecedor(this.getComprasFacade().obterFornecedorComPropostaPorAF(notaRecebimento.getAutorizacaoFornecimento()));
 							this.getSceHistoricoProblemaMaterialRN().inserir(sceHistProbAlmox, true);
 						}
 						else {
 							throw new ApplicationBusinessException(RecebimentoONExceptionCode.ESTORNO_MATERIAL_SALDO_BLOQUEADO);
 						}
 					}
				}
			}
		}
		
		//Insere Devolução Fornecedor - SCE_DEVOLUCAO_FORNECEDORES
 		SceDevolucaoFornecedor devolucaoFornecedor = new SceDevolucaoFornecedor();
 		devolucaoFornecedor.setNroNfDevolucao(null);
 		devolucaoFornecedor.setSerieNfDevolucao(null);
 		devolucaoFornecedor.setDtGeracao(null);
		devolucaoFornecedor.setDtEmissaoNfd(null);
		devolucaoFornecedor.setIndEstorno(Boolean.FALSE);
		devolucaoFornecedor.setDtFechamentoNfd(null);
		devolucaoFornecedor.setDtEstorno(null);
		devolucaoFornecedor.setMotivo(pMotivoDev);
 		devolucaoFornecedor.setSceDocumentoFiscalEntrada(notaRecebimento.getDocumentoFiscalEntrada());
		final Integer tmvDocDf = this.getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TMV_DOC_DF).getVlrNumerico().intValue();
		devolucaoFornecedor.setTmvSeq(tmvDocDf);
		SceTipoMovimento tipoMovimento = this.getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(tmvDocDf.shortValue());
 		if (tipoMovimento != null) {
 			devolucaoFornecedor.setTmvComplemento(tipoMovimento.getId().getComplemento().intValue());
 		}
 		devolucaoFornecedor.setServidor(servidorLogado);
 		devolucaoFornecedor.setServidorEstornado(null);
 		devolucaoFornecedor.setIndGerado("N");
 		devolucaoFornecedor.setBocSeq(boletimOcorrencia.getSeq());
 		devolucaoFornecedor.setIndTramiteInterno(Boolean.TRUE);
 		this.getSceDevolucaoFornecedorRN().inserir(devolucaoFornecedor, true);  
		 
 		//Percorre a lista de itens de NR Provisório, pois, é a mesma lista utilizada para incluir os itens do boletim de ocorrência
 		if (retorno != null && !retorno.isEmpty()) {
			for (final ItemRecebimentoProvisorioVO itemRecebimentoProvisorioVO : retorno) {
				//Insere Item Devolução Fornecedor - SCE_ITEM_DFS
				SceItemDevolucaoFornecedorId idItemDevolucaoFornecedor = new SceItemDevolucaoFornecedorId();
				idItemDevolucaoFornecedor.setDfsSeq(devolucaoFornecedor.getSeq());
				idItemDevolucaoFornecedor.setNumero(itemRecebimentoProvisorioVO.getItlNumero().intValue());
				SceItemDevolucaoFornecedor itemDevolucaoFornecedor = new SceItemDevolucaoFornecedor();
				itemDevolucaoFornecedor.setId(idItemDevolucaoFornecedor);
				if (itemRecebimentoProvisorioVO.getValor() != null) { itemDevolucaoFornecedor.setValor(itemRecebimentoProvisorioVO.getValor()); }
				if (itemRecebimentoProvisorioVO.getQuantidade() != null) {itemDevolucaoFornecedor.setQuantidade(itemRecebimentoProvisorioVO.getQuantidade()); }
				itemDevolucaoFornecedor.setUmdCodigo(itemRecebimentoProvisorioVO.getCodigoUnidadeMedida());
				itemDevolucaoFornecedor.setSceDevolucaoFornecedor(devolucaoFornecedor);
				//Obtém o material
				ScoMaterial material = this.getComprasFacade().obterScoMaterialPorChavePrimaria(itemRecebimentoProvisorioVO.getCodigoMaterial());
				//Obtém o estoque almoxarifado
				SceEstoqueAlmoxarifado estoqueAlmox = this.getSceEstoqueAlmoxarifadoDAO().buscarSceEstoqueAlmoxarifadoPorAlmoxarifado(material.getAlmoxarifado().getSeq().shortValue(), material.getCodigo(), fornecedorPadrao);
				itemDevolucaoFornecedor.setEalSeq(estoqueAlmox.getSeq());
				this.getSceItemDevolucaoFornecedorRN().inserir(itemDevolucaoFornecedor, material, nomeMicrocomputador, tmvDocDf, notaRecebimento.getSeq());
			}
 		}
	}
	
	private boolean isMaterial(final List<ItemRecebimentoProvisorioVO> itens) {
		return itens != null && !itens.isEmpty() && itens.get(0).getCodigoMaterial() != null;
	}
	
	public Boolean mostrarBotaoRecebimentoAntigo() {
		Boolean ret = Boolean.FALSE;
		
		try {
			AghParametros param = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_RECEB_VERSAO_4);
			if (param.getVlrTexto().equalsIgnoreCase("S")) {
				ret = Boolean.TRUE;
			}
		} catch (ApplicationBusinessException e) {
			LOG.error("Parametro P_RECEB_VERSAO_4 nao cadastrado.");
		}
		
		return ret;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected SceNotaRecebProvisorioDAO getSceNotaRecebProvisorioDAO() {
		return sceNotaRecebProvisorioDAO;
	}
	
	protected SceItemRecebProvisorioDAO getSceItemRecebProvisorioDAO() {
		return sceItemRecebProvisorioDAO;
	}

	protected SceNotaRecebimentoDAO getSceNotaRecebimentoDAO() {
		return sceNotaRecebimentoDAO;
	}

	protected SceMovimentoMaterialDAO getSceMovimentoMaterialDAO() {
		return sceMovimentoMaterialDAO;
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected SceMotivoProblemaDAO getSceMotivoProblemaDAO() {
		return sceMotivoProblemaDAO;
	}

	protected SceNotaRecebimentoRN getSceNotaRecebimentoRN() {
		return sceNotaRecebimentoRN;
	}
	
	protected SceBoletimOcorrenciasRN getSceBoletimOcorrenciasRN() {
		return sceBoletimOcorrenciasRN;
	}
	
	protected SceItemBocRN getSceitemBocRN() {
		return sceItemBocRN;
	}

	protected SceTipoMovimentosRN getSceTipoMovimentosRN() {
		return sceTipoMovimentosRN;
	}
	
	protected SceDevolucaoFornecedorRN getSceDevolucaoFornecedorRN() {
		return sceDevolucaoFornecedorRN;
	}
	
	protected SceItemDevolucaoFornecedorRN getSceItemDevolucaoFornecedorRN() {
		return sceItemDevolucaoFornecedorRN;
	}
	
	protected SceItemNotaRecebimentoDevolucaoFornecedorRN getSceItemNotaRecebimentoDevolucaoFornecedorRN() {
		return sceItemNotaRecebimentoDevolucaoFornecedorRN;
	}
	
	protected SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN() {
		return sceEstoqueAlmoxarifadoRN;
	}

	protected SceHistoricoProblemaMaterialRN getSceHistoricoProblemaMaterialRN() {
		return sceHistoricoProblemaMaterialRN;
	}

	protected ConfirmacaoRecebimentoON getConfirmacaoRecebimentoON() {
		return confirmacaoRecebimentoON;
	}
	
	protected ConfirmacaoDevolucaoON getConfirmacaoDevolucaoON() {
			return confirmacaoDevolucaoON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
