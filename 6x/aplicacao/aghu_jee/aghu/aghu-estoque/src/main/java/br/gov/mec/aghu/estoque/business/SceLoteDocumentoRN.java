package br.gov.mec.aghu.estoque.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDocImpressaoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDocImpressaoDAO.SceLoteDocImpressaoDAOExceptionCode;
import br.gov.mec.aghu.estoque.dao.SceLoteDocumentoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceValidadeDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.model.SceLote;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.model.SceLoteFornecedor;
import br.gov.mec.aghu.model.SceLoteId;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.model.SceValidadeId;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class SceLoteDocumentoRN extends BaseBusiness{

@EJB
private SceValidadesRN sceValidadesRN;
@EJB
private SceLoteRN sceLoteRN;
@EJB
private SceLoteFornecedorRN sceLoteFornecedorRN;
@EJB
private SceItemNotaRecebimentoRN sceItemNotaRecebimentoRN;

private static final Log LOG = LogFactory.getLog(SceLoteDocumentoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceLoteDAO sceLoteDAO;

@Inject
private SceLoteDocumentoDAO sceLoteDocumentoDAO;

@EJB
private IComprasFacade comprasFacade;

@Inject
private SceValidadeDAO sceValidadeDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private SceLoteFornecedorDAO sceLoteFornecedorDAO;

@Inject
private SceLoteDocImpressaoDAO sceLoteDocImpressaoDAO;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7598128574814960951L;

	public enum SceLoteDocumentoRNExceptionCode implements BusinessExceptionCode {
		SCE_00683, SCE_00681, SCE_00898, SCE_00669, SCE_00667, MSG_ESL_NR_OBRIGATORIO, MSG_ESL_OU_NR, SCE_LDC_CK2;
	}
	
	public void inserir(SceLoteDocumento loteDocumento) throws BaseException {
		
		preInsert(loteDocumento);
		getLoteDocumentoDAO().persistir(loteDocumento);
		getLoteDocumentoDAO().flush();
		
	}
	
	public void preInsert(SceLoteDocumento loteDocumento) throws BaseException {
		
		validaQuantidadeItemNotaFiscal(loteDocumento);
		validaESLNotaRecebimento(loteDocumento);
		validaEntradas(loteDocumento);
		validaEntradasSemLicitacao(loteDocumento);
		validaQuantidadesLoteDocumentoComItemNotaFiscal(loteDocumento);
		
	}
	
	public void atualizar(SceLoteDocumento loteDocumento) throws BaseException{
		
		preAtualizar(loteDocumento);
		getLoteDocumentoDAO().merge(loteDocumento);
	}
	
	public void preAtualizar(SceLoteDocumento loteDocumento) throws BaseException{
		
		SceLoteDocumento antigoloteDocumento = (SceLoteDocumento) getLoteDocumentoDAO().obterOriginal(loteDocumento);
		
		validaQuantidadeItemNotaFiscal(antigoloteDocumento);
		atualizaValidades(loteDocumento, antigoloteDocumento);
		atualizaLotes(loteDocumento, antigoloteDocumento);
		atualizaFornecedorValidade(loteDocumento, antigoloteDocumento);
		validaESLNotaRecebimento(loteDocumento);
		
	}
	
	/**
	 * ORADB sce_ldc_ck1
	 * Valida se foi selecionado somente nota de recebimento ou somente entrada_saida_sem_licitacao
	 * @param loteDocumento
	 *  
	 */
	protected void validaESLNotaRecebimento(SceLoteDocumento loteDocumento) throws ApplicationBusinessException{
		
		if(loteDocumento!=null && loteDocumento.getEntradaSaidaSemLicitacao()==null && loteDocumento.getItemNotaRecebimento()==null 
				&& loteDocumento.getIdaDalSeq() == null && loteDocumento.getIrmRmsSeq() == null && loteDocumento.getItrTrfSeq() == null ){
			throw new ApplicationBusinessException(SceLoteDocumentoRNExceptionCode.MSG_ESL_NR_OBRIGATORIO);
		}else if(loteDocumento!=null && loteDocumento.getEntradaSaidaSemLicitacao() != null && loteDocumento.getItemNotaRecebimento() != null 
				&& loteDocumento.getIdaDalSeq() != null && loteDocumento.getIrmRmsSeq() != null && loteDocumento.getItrTrfSeq() != null ){
			throw new ApplicationBusinessException(SceLoteDocumentoRNExceptionCode.MSG_ESL_OU_NR);
		}
		
	}
	
	/**Atualização do SCE_LOTE_X_FORNECEDORES
	 * ORADB scek_ldc_rn.rn_ldcp_atu_geracao
	 * @param loteDocumento
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void validaEntradas(SceLoteDocumento loteDocumento) throws BaseException{
//		ScoUnidadeMedida unidadeMedidaDocumento = null;
//		ScoUnidadeMedida unidadeMedidaEstoque = null;
//		Date dtGeracaoDoc = null;
		ScoMaterial material = null;
		Integer marcaCodigo = null;
		ScoFornecedor fornecedor = null;
		SceFornecedorEventual fornecedorEventual = null;
		Integer novaQuantidadeUmd = null;
		Integer qtdeCalculada = null;
		Integer qtdeCalculadaSaida = null;

		
		
		if(loteDocumento.getEntradaSaidaSemLicitacao()!=null && loteDocumento.getEntradaSaidaSemLicitacao().getSceTipoMovimento()!=null){
			loteDocumento.setTipoMovimento(loteDocumento.getEntradaSaidaSemLicitacao().getSceTipoMovimento());
		}

		if(loteDocumento.getEntradaSaidaSemLicitacao()!=null){
//			unidadeMedidaDocumento = loteDocumento.getEntradaSaidaSemLicitacao().getScoUnidadeMedida();
//			dtGeracaoDoc = loteDocumento.getEntradaSaidaSemLicitacao().getDtGeracao();
			material = loteDocumento.getEntradaSaidaSemLicitacao().getScoMaterial();

			if(material==null){
				throw new ApplicationBusinessException(SceLoteDocumentoRNExceptionCode.SCE_00683);
//			}else{
//				unidadeMedidaEstoque = material.getUnidadeMedida(); 	
			}

			//Marcas
			if(loteDocumento.getEntradaSaidaSemLicitacao().getScoMarcaComercial()!=null){
				marcaCodigo  = loteDocumento.getEntradaSaidaSemLicitacao().getScoMarcaComercial().getCodigo();
			}else{
				marcaCodigo  = loteDocumento.getEntradaSaidaSemLicitacao().getScoNomeComercial().getId().getMcmCodigo();	
			}
			
			if(loteDocumento.getEntradaSaidaSemLicitacao().getScoFornecedor()!=null){
				fornecedor = loteDocumento.getEntradaSaidaSemLicitacao().getScoFornecedor();
				fornecedorEventual = null;
			}else{
				if(loteDocumento.getEntradaSaidaSemLicitacao().getSceFornecedorEventual()!=null){
					fornecedor = null;
					fornecedorEventual = loteDocumento.getEntradaSaidaSemLicitacao().getSceFornecedorEventual();
				}else{
					
					if(loteDocumento.getEntradaSaidaSemLicitacao().getSceDocumentoFiscalEntrada()==null){
						throw new ApplicationBusinessException(SceLoteDocumentoRNExceptionCode.SCE_00681);
					}else{
						if(loteDocumento.getEntradaSaidaSemLicitacao().getSceDocumentoFiscalEntrada().getFornecedor()!=null){
							fornecedor = loteDocumento.getEntradaSaidaSemLicitacao().getSceDocumentoFiscalEntrada().getFornecedor();
							fornecedorEventual = null;

						}else{
							fornecedor = null;
							fornecedorEventual = loteDocumento.getEntradaSaidaSemLicitacao().getSceDocumentoFiscalEntrada().getFornecedorEventual();
						}
					}
				}
			}
		}else if(loteDocumento.getItemNotaRecebimento()!=null){
			material = loteDocumento.getItemNotaRecebimento().getMaterial();
			if(material==null){
				throw new ApplicationBusinessException(SceLoteDocumentoRNExceptionCode.SCE_00683);
			}

//			unidadeMedidaEstoque = material.getUnidadeMedida();
			fornecedor = loteDocumento.getFornecedor();
			fornecedorEventual = null;
//			unidadeMedidaDocumento = loteDocumento.getItemNotaRecebimento().getUnidadeMedida();
//			dtGeracaoDoc = loteDocumento.getItemNotaRecebimento().getNotaRecebimento().getDtGeracao();

			//Marcas
			if (loteDocumento.getItemNotaRecebimento().getItemAutorizacaoForn().getMarcaComercial() != null) {
			
				marcaCodigo  = loteDocumento.getItemNotaRecebimento().getItemAutorizacaoForn().getMarcaComercial().getCodigo();
			
			} else {
		
				marcaCodigo  = loteDocumento.getItemNotaRecebimento().getItemAutorizacaoForn().getNomeComercial().getId().getMcmCodigo();	
			
			}

		} else if (loteDocumento.getIdaDalSeq() != null || loteDocumento.getIrmRmsSeq() != null || loteDocumento.getItrTrfSeq() != null) {
			
			material = getComprasFacade().obterScoMaterialOriginal(loteDocumento.getLotMatCodigo());
			marcaCodigo  = loteDocumento.getLotMcmCodigo();
			fornecedor = loteDocumento.getFornecedor();
			fornecedorEventual = null;

		}

		SceLoteFornecedor lotForn = getSceLoteFornecedorDAO().pesquisarLoteFornecedorPorLoteMarcaMaterialDtValidadeFornecedorEFornEvent(
				loteDocumento.getLotCodigo(), 
				marcaCodigo, 
				material.getCodigo(), 
				loteDocumento.getDtValidade(), ((fornecedor!=null)?fornecedor.getNumero():null), ((fornecedorEventual!=null)?fornecedorEventual.getSeq():null));
		
		if (lotForn == null) {

			/*Insere o lote*/
			
			SceLote novoLote = getSceLoteDAO().obterPorChavePrimaria(new SceLoteId(material.getCodigo(), marcaCodigo, loteDocumento.getLotCodigo()));
		
			if (novoLote == null) {
				
				novoLote = new SceLote(new SceLoteId(material.getCodigo(), marcaCodigo, loteDocumento.getLotCodigo()));
				getSceLoteRN().inserir(novoLote);
			
			}

			novaQuantidadeUmd = loteDocumento.getQuantidade();

			/*Insere lote Fornecedor*/
			lotForn = new SceLoteFornecedor();
			lotForn.setLote(novoLote);
			lotForn.setQuantidade(novaQuantidadeUmd);
			lotForn.setDtValidade(loteDocumento.getDtValidade());
			lotForn.setFornecedor(fornecedor);
			lotForn.setSceFornecedorEventual(fornecedorEventual);
			getSceLoteFornecedorRN().inserir(lotForn);
			
		} else {

			novaQuantidadeUmd = loteDocumento.getQuantidade();
			
			if(loteDocumento.getTipoMovimento().getIndOperacaoBasica().equals(DominioIndOperacaoBasica.CR)){
				if(loteDocumento.getEntradaSaidaSemLicitacao()!=null || loteDocumento.getItemNotaRecebimento()!=null){
						qtdeCalculada = ((lotForn!=null && lotForn.getQuantidade()!=null)?lotForn.getQuantidade():0) + novaQuantidadeUmd;
						qtdeCalculadaSaida = ((lotForn!=null && lotForn.getQuantidadeSaida()!=null)?lotForn.getQuantidadeSaida():0);
				}else{
					if(loteDocumento.getIdaDalSeq()!=null || loteDocumento.getItrTrfSeq()!=null){
	
						if(((lotForn!=null && lotForn.getQuantidadeSaida()!=null)?lotForn.getQuantidadeSaida():0) < novaQuantidadeUmd){
							throw new ApplicationBusinessException(SceLoteDocumentoRNExceptionCode.SCE_00898);
						}else{
							qtdeCalculada = ((lotForn != null && lotForn.getQuantidade() != null)?lotForn.getQuantidade():0);
							qtdeCalculadaSaida = ((lotForn != null && lotForn.getQuantidadeSaida() != null)?lotForn.getQuantidadeSaida():0) - novaQuantidadeUmd;	
						}
					}
				}
			}else{
				if(loteDocumento.getEntradaSaidaSemLicitacao()!=null){
					qtdeCalculada = ((lotForn!=null && lotForn.getQuantidade()!=null)?lotForn.getQuantidade():0) - novaQuantidadeUmd;
					qtdeCalculadaSaida = ((lotForn!=null && lotForn.getQuantidadeSaida()!=null)?lotForn.getQuantidadeSaida():0);
				}else{
					if(loteDocumento.getIrmRmsSeq()!=null || loteDocumento.getItrTrfSeq()!=null){
						qtdeCalculada = ((lotForn!=null && lotForn.getQuantidade()!=null)?lotForn.getQuantidade():0);
						qtdeCalculadaSaida = ((lotForn!=null && lotForn.getQuantidadeSaida()!=null)?lotForn.getQuantidadeSaida():0) + novaQuantidadeUmd;
					}
				}
			}
			/*atualiza o lote fornecedor*/
			lotForn.setQuantidade(qtdeCalculada);
			lotForn.setQuantidadeSaida(qtdeCalculadaSaida);
			getSceLoteFornecedorRN().atualizar(lotForn);
		}
		
		if (loteDocumento.getInrEalSeq() != null) {
			
			atualizaValidades(loteDocumento);
			
		}
		
	}
	
	/** Validade se quantidade do lote x documentos é maior que a da entrada saida sem licitação
	 * ORADB scek_ldc_rn.rn_ldcp_ver_qtde_esl
	 * @param loteDocumento
	 * @throws BaseException
	 */
	protected void validaEntradasSemLicitacao(SceLoteDocumento loteDocumento) throws BaseException{
		if(loteDocumento.getEntradaSaidaSemLicitacao()!=null){
			if(loteDocumento.getQuantidade() > loteDocumento.getEntradaSaidaSemLicitacao().getQuantidade()){
				throw new ApplicationBusinessException(SceLoteDocumentoRNExceptionCode.SCE_00669);
			}
		}
	}
	
	/** Validade se quantidade do lote x documentos é maior que item nota recebimento
	 * ORABD scek_ldc_rn.rn_ldcp_ver_qtde_inr
	 * @param loteDocumento
	 * @throws BaseException
	 */
	protected void validaQuantidadesLoteDocumentoComItemNotaFiscal(SceLoteDocumento loteDocumento) throws BaseException{
		if(loteDocumento.getItemNotaRecebimento()!=null){
			if(loteDocumento.getQuantidade() > loteDocumento.getItemNotaRecebimento().getQuantidade()){
				throw new ApplicationBusinessException(SceLoteDocumentoRNExceptionCode.SCE_00667);
			}
		}
	}
	
	/**
	 * Valida se a quantidade é maior que zero
	 * ORADB constraint  SCE_LDC_CK2 table sce_lote_x_documentos
	 * @param loteDocumento
	 * @throws BaseException
	 */
	protected void validaQuantidadeItemNotaFiscal(SceLoteDocumento loteDocumento) throws BaseException{
		if(loteDocumento.getQuantidade()==null || loteDocumento.getQuantidade().intValue() <= 0){
			throw new ApplicationBusinessException(SceLoteDocumentoRNExceptionCode.SCE_LDC_CK2);
		}		
	}

	private void atualizaValidades(SceLoteDocumento loteDocumento) throws BaseException{
		
		AghParametros paramFrnHcpa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		
		SceEstoqueAlmoxarifado estalm = getSceEstoqueAlmoxarifadoDAO().obterPorChavePrimaria((loteDocumento.getIdaDalSeq()!=null && loteDocumento.getIdaEalSeq() != null)? loteDocumento.getIdaEalSeq() :loteDocumento.getInrEalSeq());
		
		SceValidade validade = getSceValidadeDAO().obterValidadePorMaterialDtValidadeEstqAlmoxFornecedor(estalm.getMaterial(), loteDocumento.getDtValidade(), estalm, paramFrnHcpa.getVlrNumerico().intValue());
		
		if (validade == null) {
		
			//Insere nova validade
			SceValidade novaValidade = new SceValidade();
		
			SceValidadeId id = new SceValidadeId();
			id.setData(loteDocumento.getDtValidade());
			id.setEalSeq(estalm.getSeq());
			novaValidade.setId(id);
			novaValidade.setQtdeEntrada(loteDocumento.getQuantidade());
			novaValidade.setQtdeConsumida(0);
			novaValidade.setQtdeDisponivel(loteDocumento.getQuantidade());
			
			getSceValidadesRN().inserir(novaValidade);
		
		} else {
			
			if (loteDocumento.getTipoMovimento().getIndOperacaoBasica().equals(DominioIndOperacaoBasica.CR)) {
				
				validade.setQtdeDisponivel(validade.getQtdeDisponivel() + loteDocumento.getQuantidade());
				validade.setQtdeEntrada(validade.getQtdeEntrada() + loteDocumento.getQuantidade());

			} else if(loteDocumento.getTipoMovimento().getIndOperacaoBasica().equals(DominioIndOperacaoBasica.DB)) {
				
				validade.setQtdeDisponivel(validade.getQtdeDisponivel() - loteDocumento.getQuantidade());
				validade.setQtdeEntrada(validade.getQtdeEntrada() - loteDocumento.getQuantidade());

			}
			
			getSceValidadesRN().preAtualizar(validade);
			
		}
		
	}
	
	private void atualizaValidades(SceLoteDocumento novoLoteDocumento, SceLoteDocumento antigoLoteDocumento) throws BaseException{
		AghParametros paramFrnHcpa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		
		SceEstoqueAlmoxarifado estalm = getSceEstoqueAlmoxarifadoDAO().obterPorChavePrimaria((novoLoteDocumento.getIdaDalSeq()!=null && novoLoteDocumento.getIdaEalSeq() != null)? novoLoteDocumento.getIdaEalSeq() :novoLoteDocumento.getInrEalSeq());
		
		SceValidade validade = getSceValidadeDAO().obterValidadePorMaterialDtValidadeEstqAlmoxFornecedor(estalm.getMaterial(), novoLoteDocumento.getDtValidade(), estalm, paramFrnHcpa.getVlrNumerico().intValue());
		if(validade==null){
			//Insere nova validade
			SceValidade novaValidade = new SceValidade();
			//chave composta
			SceValidadeId id = new SceValidadeId();
			id.setData(novoLoteDocumento.getDtValidade());
			id.setEalSeq(estalm.getSeq());
			//-*-*-*-*-*-*-*-*-*-*-*-*-*
			novaValidade.setId(id);
			novaValidade.setQtdeEntrada(novoLoteDocumento.getQuantidade());
			novaValidade.setQtdeConsumida(0);
			novaValidade.setQtdeDisponivel(novoLoteDocumento.getQuantidade());
			//persiste
			getSceValidadesRN().inserir(novaValidade);
		}else{
			//Atualiza
			if(!novoLoteDocumento.getQuantidade().equals(antigoLoteDocumento.getQuantidade())){
				Integer quantidadeTemp = 0;
				quantidadeTemp = novoLoteDocumento.getQuantidade() - antigoLoteDocumento.getQuantidade();				
				validade.setQtdeDisponivel(validade.getQtdeDisponivel() + quantidadeTemp);
				validade.setQtdeEntrada(validade.getQtdeEntrada() + quantidadeTemp);

				getSceValidadesRN().atualizar(validade);
			}
		}
	}

	private void atualizaFornecedorValidade(SceLoteDocumento novoLoteDocumento, SceLoteDocumento antigoLoteDocumento) throws BaseException{
		SceLoteFornecedor lotForn =	getSceLoteFornecedorDAO().obterLoteFornecedorPorLotCodigoLotMatCodigoLotDtValidade(
																								antigoLoteDocumento.getLotCodigo(), 
																								novoLoteDocumento.getLotMatCodigo(), 
																								antigoLoteDocumento.getDtValidade());

		/*Exclui o lote fornecedor*/
		//old.lot_codigo, :new.lot_mat_codigo, :old.dt_validade)
		SceLoteFornecedor lotFornExcluir = getSceLoteFornecedorDAO().obterLoteFornecedorPorLotCodigoLotMatCodigoLotMcmCodigoDtValidade(antigoLoteDocumento.getLotCodigo(), novoLoteDocumento.getLotMatCodigo(), antigoLoteDocumento.getDtValidade());
		if(lotFornExcluir!=null){
			getSceLoteFornecedorRN().remover(lotFornExcluir);
		}
		
		if (lotForn != null) {
			if (antigoLoteDocumento != null && novoLoteDocumento != null
				&& !antigoLoteDocumento.getQuantidade().equals(novoLoteDocumento.getQuantidade())){

			Integer quantidadeTemp = 0;
			quantidadeTemp = novoLoteDocumento.getQuantidade() - antigoLoteDocumento.getQuantidade();

			lotForn.setQuantidade(lotForn.getQuantidade() + quantidadeTemp);			
		}
		
			lotForn.setSeq(null);
			getSceLoteFornecedorRN().inserir(lotForn);
		}
	}
	
	private void atualizaLotes(SceLoteDocumento novoLoteDocumento, SceLoteDocumento antigoLoteDocumento) throws BaseException {
	
		if (!novoLoteDocumento.getLotCodigo().equals(antigoLoteDocumento.getLotCodigo())) {
			
			ScoMaterial material = novoLoteDocumento.getEntradaSaidaSemLicitacao().getScoMaterial();
			
			SceLote lote = getSceLoteDAO().obterPorChavePrimaria(new SceLoteId(material.getCodigo(), novoLoteDocumento
							.getLotMcmCodigo(), novoLoteDocumento.getLotCodigo()));
			
			if (lote == null) {
				
				lote = new SceLote();
				SceLoteId id = new SceLoteId();
				id.setCodigo(novoLoteDocumento.getLotCodigo());
				id.setMatCodigo(novoLoteDocumento.getLotMatCodigo());
				id.setMcmCodigo(novoLoteDocumento.getLotMcmCodigo());
				lote.setId(id);
				getSceLoteRN().inserir(lote);
				getLoteDocumentoDAO().flush();
			
			}
		
		}
		
	}

	public void remover(SceLoteDocumento loteDocumento) throws BaseException{
		preRemover(loteDocumento);
		getLoteDocumentoDAO().remover(loteDocumento);
	}
	
	/**
	 * ORADB SCET_LDC_BRD
	 * @param loteDocumento
	 * @throws BaseException 
	 */
	private void preRemover(SceLoteDocumento loteDocumento) throws BaseException {
		
		List<SceLoteFornecedor> loteFornecedor = getSceLoteFornecedorDAO().pesquisarLoteFornecedorPorLotCodigoLotMatCodigoLotMcmCodigo(loteDocumento.getLotCodigo(),loteDocumento.getLotMatCodigo(),loteDocumento.getLotMcmCodigo());
		List<SceLoteDocImpressao> loteImpressao = getSceLoteDocImpressaoDAO().pesquisarLoteDocImpressaoPorLdcSeq(loteDocumento.getSeq());
		
		if(loteFornecedor == null || loteFornecedor.isEmpty()){
			if(loteImpressao == null || loteImpressao.isEmpty()){
				atualizarRemocao(loteDocumento);
			}
		}
		
	}

	/**
	 * ORADB scek_ldc_rn.rn_ldcp_atu_delecao
	 * Executa rotina referente a excluir um lote documento
	 * @param loteDocumento
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void atualizarRemocao(SceLoteDocumento loteDocumento) throws BaseException {
		
		if (loteDocumento.getLotMatCodigo() == null) {
			
			throw new ApplicationBusinessException(SceLoteDocumentoRNExceptionCode.SCE_00683);
		
		}
		
		ScoMaterial material = getComprasFacade().obterScoMaterialOriginal(loteDocumento.getLotMatCodigo());
//		ScoUnidadeMedida unidadeMedidaEstoque = null;
//		ScoUnidadeMedida unidadeMedidaDocumento = null;
//		
//		if (material != null) {
//			
//			unidadeMedidaEstoque = material.getUnidadeMedida();
//			
//		}
		
//		Date dtGeracaoDoc = null;
		SceFornecedorEventual fornecedorEventual = null;
		ScoFornecedor fornecedor = null;
		Integer qtdeNovaUmd = 0;
		
		if (loteDocumento.getEntradaSaidaSemLicitacao() != null) {
			
//			unidadeMedidaDocumento = loteDocumento.getEntradaSaidaSemLicitacao().getScoUnidadeMedida();
//			dtGeracaoDoc = loteDocumento.getEntradaSaidaSemLicitacao().getDtGeracao();
			
			if (loteDocumento.getEntradaSaidaSemLicitacao().getSceFornecedorEventual() != null) {
				
				fornecedorEventual = loteDocumento.getEntradaSaidaSemLicitacao().getSceFornecedorEventual();
			
			} else {
				
				if (loteDocumento.getEntradaSaidaSemLicitacao().getScoFornecedor() != null) {
					
					fornecedor = loteDocumento.getEntradaSaidaSemLicitacao().getScoFornecedor();
			
				} else {
					
					SceDocumentoFiscalEntrada documentoFiscalEntrada = loteDocumento.getEntradaSaidaSemLicitacao().getSceDocumentoFiscalEntrada();
					
					if (documentoFiscalEntrada != null) {
						
						if (documentoFiscalEntrada.getFornecedor() != null) {
						
							fornecedor = documentoFiscalEntrada.getFornecedor();
						
						} else {
							
							fornecedorEventual = documentoFiscalEntrada.getFornecedorEventual();
						
						}
					
					}
				
				}
			
			}
		
		} else {
			
			if (loteDocumento.getItemNotaRecebimento() != null) {
			
				fornecedor = loteDocumento.getItemNotaRecebimento().getNotaRecebimento().getDocumentoFiscalEntrada().getFornecedor();
//				unidadeMedidaDocumento = loteDocumento.getItemNotaRecebimento().getUnidadeMedida();
//				dtGeracaoDoc = loteDocumento.getItemNotaRecebimento().getNotaRecebimento().getDtGeracao();
			
			}
		
		}
		
		SceLoteId loteId = new SceLoteId();
		loteId.setCodigo(loteDocumento.getLotCodigo());
		loteId.setMatCodigo(loteDocumento.getLotMatCodigo());
		loteId.setMcmCodigo(loteDocumento.getLotMcmCodigo());
		
		SceLote lote = getSceLoteDAO().obterPorChavePrimaria(loteId);
		SceLoteFornecedor loteFornecedorDiferenteDoc = getSceLoteFornecedorDAO().obterLoteFornecedorDiferenteDocPorLoteDtValidadeFornFornEventual(lote,loteDocumento.getDtValidade(),fornecedor,fornecedorEventual);
		SceLoteFornecedor loteFornecedorIgualDoc = getSceLoteFornecedorDAO().obterLoteFornecedorIgualDocPorLoteDtValidadeFornFornEventual(lote,loteDocumento.getDtValidade(),fornecedor,fornecedorEventual);
		
		if (loteFornecedorIgualDoc != null && lote != null) {
		
			qtdeNovaUmd = loteDocumento.getQuantidade();
			
			Integer qtdeCalculada = loteFornecedorIgualDoc.getQuantidade() - qtdeNovaUmd;
			
			if (qtdeCalculada > 0) {
				
				loteFornecedorIgualDoc.setQuantidade(qtdeCalculada);
				getSceLoteFornecedorRN().atualizar(loteFornecedorIgualDoc);
			
			} else {
				
				if(loteFornecedorDiferenteDoc != null) {
				
					getSceLoteFornecedorRN().remover(loteFornecedorIgualDoc);
				
				} else {
					
					getSceLoteFornecedorRN().remover(loteFornecedorIgualDoc);
					getSceLoteRN().remover(lote);
				
				}
		
			}
		
		}
		
		SceEstoqueAlmoxarifado estalm = getSceEstoqueAlmoxarifadoDAO().obterPorChavePrimaria(loteDocumento.getInrEalSeq());
		
		if (estalm != null) {

			AghParametros paramFrnHcpa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			SceValidade validade = getSceValidadeDAO().obterValidadePorMaterialDtValidadeEstqAlmoxFornecedor(material, loteDocumento.getDtValidade(),estalm,paramFrnHcpa.getVlrNumerico().intValue());
			
			if (validade != null) {
				
				if (!validade.getQtdeEntrada().equals(loteDocumento.getQuantidade())) {
				
					validade.setQtdeDisponivel(validade.getQtdeDisponivel() - loteDocumento.getQuantidade());
					validade.setQtdeEntrada(validade.getQtdeEntrada() - loteDocumento.getQuantidade());
					getSceValidadesRN().atualizar(validade);
				
				} else {
					
					getSceValidadesRN().remover(validade);
				
				}
			}
			
		}
		
	}

	public SceLoteDocumentoDAO getLoteDocumentoDAO(){
		return sceLoteDocumentoDAO;
	}
	
	protected SceLoteFornecedorDAO getSceLoteFornecedorDAO(){
		return sceLoteFornecedorDAO;
	}
	
	protected SceLoteFornecedorRN getSceLoteFornecedorRN(){
		return sceLoteFornecedorRN;
	}

	protected SceLoteDocImpressaoDAO getSceLoteDocImpressaoDAO(){
		return sceLoteDocImpressaoDAO;
	}

	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected SceLoteRN getSceLoteRN(){
		return sceLoteRN;
	}
	
	protected SceLoteDAO getSceLoteDAO(){
		return sceLoteDAO;
	}
	
	protected SceValidadeDAO getSceValidadeDAO(){
		return sceValidadeDAO;
	}
	
	protected SceValidadesRN getSceValidadesRN(){
		return sceValidadesRN;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	protected SceItemNotaRecebimentoRN getSceItemNotaRecebimentoRN(){
		return sceItemNotaRecebimentoRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	@SuppressWarnings("PMD.MissingBreakInSwitch")
	public SceLoteDocImpressao getLoteDocImpressaoByNroEtiqueta(String etiqueta)
			throws ApplicationBusinessException {
		int subset = 'A';
		int tam;
		try {
			final AghParametros parametros = getParametroFacade()
					.obterAghParametro(
							AghuParametrosEnum.P_TIPO_ETIQUETA_MEDICAMENTO);
			if (StringUtils.isNotEmpty(parametros.getVlrTexto())) {
				subset = parametros.getVlrTexto().charAt(0);
			}
		} catch (final BaseException e) {
			logError(new StringBuffer("Erro ao buscar paramentro ")
					.append(AghuParametrosEnum.P_TIPO_ETIQUETA_MEDICAMENTO.toString())
					.append(": ").append(e.getMessage()), e);
		}
		// seta o tamanho da etiqueta para o tipo
		switch (subset) {
		case 'C':
		case 'c':
			tam = 14;
			break;
		case 'B':
		case 'b':
			tam = 14;
			break;
		case 'A':
		case 'a':
		default:
			tam = 15;
			break;
		}
		String nroEtiqueta;
		String intervaloEtiqueta;
		if (etiqueta != null && etiqueta.length() == tam) {
			nroEtiqueta = etiqueta.substring(0, tam - 5);
			intervaloEtiqueta = etiqueta.substring(tam - 5, tam);
		} else {
			throw new ApplicationBusinessException(
					SceLoteDocImpressaoDAOExceptionCode.FORMATO_ETIQUETA_INVALIDO);
		}
		return getSceLoteDocImpressaoDAO()
				.getLoteDocImpressaoByNroEtiquetaFormatada(nroEtiqueta,
						intervaloEtiqueta);
	}
}