package br.gov.mec.aghu.estoque.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoDocumentoFiscalEntrada;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoFiscalEntrada;
import br.gov.mec.aghu.estoque.dao.SceDevolucaoFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceDocumentoFiscalEntradaDAO;
import br.gov.mec.aghu.estoque.dao.SceEntradaSaidaSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.model.SceItemDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class SceDocumentoFiscalEntradaRN extends BaseBusiness {
	
	@EJB
	private ScoFornecedorEventualRN scoFornecedorEventualRN;
	@EJB
	private ScoFornecedorRN scoFornecedorRN;
	
	private static final Log LOG = LogFactory.getLog(SceDocumentoFiscalEntradaRN.class);
	
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
	private ScoFornecedorDAO scoFornecedorDAO;
	
	@Inject
	private SceEntradaSaidaSemLicitacaoDAO sceEntradaSaidaSemLicitacaoDAO;
	
	@Inject
	private SceDocumentoFiscalEntradaDAO sceDocumentoFiscalEntradaDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private SceDevolucaoFornecedorDAO sceDevolucaoFornecedorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7731139316628299313L;

	public enum SceDocumentoFiscalEntradaRNExceptionCode implements BusinessExceptionCode {
		SCE_00656, SCE_00691,SCE_00689,SCE_BOC_DFE_FK1,
		SCE_DFS_DFE_FK1,SCE_ESL_DFE_FK1,SCE_INF_DFE_FK1,SCE_NRS_DFE_FK1,SCE_DFE_CK4;
	}
	
	/*
	 * Métodos para Inserir SceDocumentoFiscalEntrada
	 */
	
	/**
	 * ORADB TRIGGER SCET_DFE_BRI (INSERT)
	 * @param documentoFiscalEntrada
	 * @throws ApplicationBusinessException
	 */
	private void preInserir(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// RN1
		documentoFiscalEntrada.setDtGeracao(new Date());
		documentoFiscalEntrada.setServidor(servidorLogado);
		documentoFiscalEntrada.setIndSituacao(DominioSituacaoDocumentoFiscalEntrada.G);
		documentoFiscalEntrada.setIndLibSapiens(false);
		
		this.verificarRestricao(documentoFiscalEntrada.getNumero());
		this.verificarDocumentoFiscalEntradaDataEmissao(documentoFiscalEntrada.getDtEmissao()); // RN2
		this.verificarDocumentoFiscalEntradaFornecedorAtivo(documentoFiscalEntrada.getFornecedor()); // RN3
		this.verificarDocumentoFiscalEntradaFornecedorEventualAtivo(documentoFiscalEntrada.getFornecedorEventual()); // RN4
		this.atualizarDocumentoFiscalEntradaSerie(documentoFiscalEntrada); //RN5
		this.verificarDocumentoFiscalEntradaUnic(documentoFiscalEntrada.getNumero(),documentoFiscalEntrada.getSerie(),documentoFiscalEntrada.getFornecedor(),documentoFiscalEntrada.getFornecedorEventual()); //RN6
		
	}
	
	/**
	 * Inserir SceDocumentoFiscalEntrada
	 * @param documentoFiscalEntrada
	 * @throws BaseException
	 */
	public void inserir(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException {
		this.preInserir(documentoFiscalEntrada);
		this.getSceDocumentoFiscalEntradaDAO().persistir(documentoFiscalEntrada);
		this.getSceDocumentoFiscalEntradaDAO().flush();
	}
	
	/*
	 * Métodos para Atualizar SceDocumentoFiscalEntrada
	 */
	
	/**
	 * ORADB TRIGGER SCET_DFE_BRU (UPDATE)
	 * @param material
	 * @throws BaseException
	 */
	private void preAtualizar(SceDocumentoFiscalEntrada documentoFiscalEntrada, SceDocumentoFiscalEntrada old) throws BaseException{

		// RN1: Verifica se a data de entrada foi modificada
		if(CoreUtil.modificados(documentoFiscalEntrada.getDtEntrada(), old.getDtEntrada())){
			this.validarDocumentoFiscalEntradaDataEntradaModificada(documentoFiscalEntrada);
		}
		
		// RN2 e RN3: Verifica se o número, série, fornecedor ou fornecedor eventual foram modificados
		if(CoreUtil.modificados(documentoFiscalEntrada.getNumero(), old.getNumero()) 
				|| CoreUtil.modificados(documentoFiscalEntrada.getSerie(), old.getSerie())
				|| CoreUtil.modificados(documentoFiscalEntrada.getFornecedor(), old.getFornecedor()) 
				|| CoreUtil.modificados(documentoFiscalEntrada.getFornecedorEventual(), old.getFornecedorEventual())){
			this.validarDocumentoFiscalEntradaNumeroSerieFornecedorModificados(documentoFiscalEntrada);
		}
		
	}

	/**
	 * Atualizar SceDocumentoFiscalEntrada
	 * @param documentoFiscalEntrada
	 * @throws BaseException
	 */
	public void atualizar(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException{
		
		// Obtem o registro original
		final SceDocumentoFiscalEntrada old = this.getSceDocumentoFiscalEntradaDAO().obterOriginal(documentoFiscalEntrada);
		
		this.verificarRestricao(documentoFiscalEntrada.getNumero());
		this.preAtualizar(documentoFiscalEntrada, old);
		this.getSceDocumentoFiscalEntradaDAO().atualizar(documentoFiscalEntrada);
		this.getSceDocumentoFiscalEntradaDAO().flush();

	}
	
	/**
	 * Remover SceDocumentoFiscalEntrada
	 * @param documentoFiscalEntrada
	 * @throws BaseException
	 */
	public void remover(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException {
		this.validarDependenciasRemover(documentoFiscalEntrada);
		this.getSceDocumentoFiscalEntradaDAO().remover(documentoFiscalEntrada);
		this.getSceDocumentoFiscalEntradaDAO().flush();
	}
	
	
	/**
	 * Valida dependências antes de remover um documento fiscal de entrada
	 * @param documentoFiscalEntrada
	 * @throws BaseException
	 */
	private void validarDependenciasRemover(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException {
		
		BaseListException erros = new BaseListException();

		erros.add(this.getSceDocumentoFiscalEntradaDAO().obterNegocioExceptionDependencias(documentoFiscalEntrada,SceBoletimOcorrencias.class,SceBoletimOcorrencias.Fields.DOCUMENTO_FISCAL_ENTRADA,SceDocumentoFiscalEntradaRNExceptionCode.SCE_BOC_DFE_FK1));
		erros.add(this.getSceDocumentoFiscalEntradaDAO().obterNegocioExceptionDependencias(documentoFiscalEntrada,SceDevolucaoFornecedor.class,SceDevolucaoFornecedor.Fields.DOCUMENTO_FISCAL_ENTRADA,SceDocumentoFiscalEntradaRNExceptionCode.SCE_DFS_DFE_FK1));
		erros.add(this.getSceDocumentoFiscalEntradaDAO().obterNegocioExceptionDependencias(documentoFiscalEntrada,SceEntradaSaidaSemLicitacao.class,SceEntradaSaidaSemLicitacao.Fields.DOCUMENTO_FISCAL_ENTRADA,SceDocumentoFiscalEntradaRNExceptionCode.SCE_ESL_DFE_FK1));
		erros.add(this.getSceDocumentoFiscalEntradaDAO().obterNegocioExceptionDependencias(documentoFiscalEntrada.getSeq(),SceItemDocumentoFiscalEntrada.class,SceItemDocumentoFiscalEntrada.Fields.DFE_SEQ,SceDocumentoFiscalEntradaRNExceptionCode.SCE_INF_DFE_FK1));		
		erros.add(this.getSceDocumentoFiscalEntradaDAO().obterNegocioExceptionDependencias(documentoFiscalEntrada,SceDocumentoFiscalEntrada.class,SceDocumentoFiscalEntrada.Fields.FORNECEDOR,SceDocumentoFiscalEntradaRNExceptionCode.SCE_ESL_DFE_FK1));
		erros.add(this.getSceDocumentoFiscalEntradaDAO().obterNegocioExceptionDependencias(documentoFiscalEntrada,SceNotaRecebimento.class,SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA,SceDocumentoFiscalEntradaRNExceptionCode.SCE_NRS_DFE_FK1));
		
		if (erros.hasException()) {
			throw erros;
		}
		
	}


	/*
	 * RNs Inserir
	 */
	
	/**
	 * ORADB PROCEDURE SCEK_DFE_RN.RN_DFEP_VER_DT_EMISS
	 * Verifica se a data de emissao é posterior à data atual
	 * @param dtEmissao
	 * @throws ApplicationBusinessException 
	 */
	public void verificarDocumentoFiscalEntradaDataEmissao(Date dtEmissao) throws ApplicationBusinessException {
		if(DateUtil.validaDataMaior(dtEmissao, new Date())){
			throw new ApplicationBusinessException(SceDocumentoFiscalEntradaRNExceptionCode.SCE_00656);
		}		
	}
	
	/**
	 * ORADB PROCEDURE SCEK_DFE_RN.RN_DFEP_VER_FRN_ATIV
	 * Verifica se fornecedor é ativo.
	 * @param fornecedor
	 * @throws ApplicationBusinessException
	 */
	public void verificarDocumentoFiscalEntradaFornecedorAtivo(ScoFornecedor fornecedor) throws BaseException {
		this.getScoFornecedorRN().verificarFornecedorAtivo(fornecedor);
	}
	
	/**
	 * ORADB PROCEDURE SCEK_DFE_RN.RN_DFEP_VER_FEV_ATIV
	 * Verifica se fornecedor eventual está ativo
	 * @param fornecedorEventual
	 * @throws ApplicationBusinessException 
	 */
	public void verificarDocumentoFiscalEntradaFornecedorEventualAtivo(SceFornecedorEventual fornecedorEventual) throws BaseException {
		this.getScoFornecedorEventualRN().verificarFornecedorEventualAtivo(fornecedorEventual);
	}


	/**
	 * ORADB PROCEDURE SCEK_DFE_RN.RN_DFEP_VER_DFE_UNIC
	 * Verifica se há outro documento fiscal com mesmo número e série para este fornecedor ou fornecedor eventual
	 * @param numero
	 * @param serie
	 * @param fornecedor
	 * @param fornecedorEventual
	 * @throws ApplicationBusinessException 
	 */
	private void verificarDocumentoFiscalEntradaUnic(Long numero, String serie,ScoFornecedor fornecedor, SceFornecedorEventual fornecedorEventual) throws BaseException {
		if(numero != null){
			if(verificarDocumentoFiscalEntradaUnico(numero,serie,fornecedor,fornecedorEventual)){
				throw new ApplicationBusinessException(SceDocumentoFiscalEntradaRNExceptionCode.SCE_00691);
			}
		}
		
	}


	/**
	 * ORADB FUNCTION SCEK_DFE_RN.DFEC_VER_DFE_UNICO
	 * Verifica se há outro documento fiscal com mesmo número e série para este fornecedor ou fornecedor eventual
	 * @param numero
	 * @param serie
	 * @param fornecedor
	 * @param fornecedorEventual
	 * @return
	 */
	public boolean verificarDocumentoFiscalEntradaUnico(Long numero, String serie,ScoFornecedor fornecedor, SceFornecedorEventual fornecedorEventual) {
		List<SceDocumentoFiscalEntrada> documentos = getSceDocumentoFiscalEntradaDAO().pesquisarDocumentoFiscaisPorNumeroSerieFornecedorOuFornecedorEventual(numero,serie,fornecedor,fornecedorEventual);
		return documentos != null && !documentos.isEmpty();
	}


	/**
	 * ORADB PROCEDURE SCEK_DFE_RN.RN_DFEP_ATU_SERIE
	 * Atualiza serie do documento fiscal para U caso documento fiscal de entrada não contenha série
	 * @param tipoDocumentoFiscalEntrada
	 * @param serie
	 */
	public void atualizarDocumentoFiscalEntradaSerie(SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		if(DominioTipoDocumentoFiscalEntrada.DFE.equals(documentoFiscalEntrada.getTipoDocumentoFiscalEntrada()) && StringUtils.isEmpty(documentoFiscalEntrada.getSerie())){
			documentoFiscalEntrada.setSerie("U");
		}
	}
	
	/*
	 * RNs Atualizar
	 */
	
	/**
	 * ORADB PROCEDURE SCEK_DFE_RN.RN_DFEP_VER_MODIF_DT
	 * @param documentoFiscalEntrada
	 * @throws ApplicationBusinessException
	 */
	public void validarDocumentoFiscalEntradaDataEntradaModificada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws ApplicationBusinessException {
		final Boolean isExisteDocumentosReferenciados = this.verificarDocumentoFiscalEntradaDataDocumentosReferenciados(documentoFiscalEntrada.getSeq(), documentoFiscalEntrada.getDtEntrada());
		if (isExisteDocumentosReferenciados){
			throw new ApplicationBusinessException(SceDocumentoFiscalEntradaRNExceptionCode.SCE_00689,documentoFiscalEntrada.getSeq());			
		}
	}
	
	/**
	 * ORADB PROCEDURES SCEK_DFE_RN.RN_DFEP_VER_MODIF_SR e SCEK_DFE_RN.RN_DFEP_VER_MODIF_FN
	 * @param documentoFiscalEntrada
	 * @throws ApplicationBusinessException
	 */
	public void validarDocumentoFiscalEntradaNumeroSerieFornecedorModificados(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws ApplicationBusinessException {
		final Boolean isExisteDocumentosReferenciados = this.verificarDocumentoFiscalEntradaDocumentosReferenciados(documentoFiscalEntrada.getSeq());
		if (isExisteDocumentosReferenciados){
			throw new ApplicationBusinessException(SceDocumentoFiscalEntradaRNExceptionCode.SCE_00689, documentoFiscalEntrada.getSeq());					
		}
	}
	
	/*
	 * As funções abaixo são reutilizadas nas PROCEDURES de pré-update
	 */

	/**
	 * ORADB FUNCTION SCEK_DFE_RN.DFEC_VER_DOC_REF
	 * Verifica se há documentos que referenciam o documento fiscal de entrada informado
	 * @param documentoFiscalEntrada
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarDocumentoFiscalEntradaDocumentosReferenciados(Integer seqDocumentoFiscalEntrada) throws ApplicationBusinessException {
		return this.verificarDocumentoFiscalEntradaDataDocumentosReferenciados(seqDocumentoFiscalEntrada, null);
	}

	/**
	 * ORADB FUNCTION SCEK_DFE_RN.DFEC_VER_DT_DOC_REF
	 * Verifica se há documentos que referenciam o documento fiscal de entrada informado,
	 * considerando a data de entrada!
	 * @param documentoFiscalEntrada
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarDocumentoFiscalEntradaDataDocumentosReferenciados(final Integer seqDocumentoFiscalEntrada, final Date dataEntrada) throws ApplicationBusinessException {

		// Realiza a consulta em ambas entidades obtendo todas as datas de geração
		final List<Date> listaDataGeracaoEntradaSaidaSemLicitacao = this.getSceEntradaSaidaSemLicitacaoDAO().pesquisarDataGeracaoEntradaSaidaSemLicitacaoPorDocumentoFiscalEntrada(seqDocumentoFiscalEntrada);
		final List<Date> listaDataGeracaoNotaRecebimento = this.getSceNotaRecebimentoDAO().pesquisarDataGeracaoNotaRecebimentoPorDocumentoFiscalEntrada(seqDocumentoFiscalEntrada);
		final List<Date> listaDataGeracaoDevolucaoFornecedor = this.getSceDevolucaoFornecedorDAO().pesquisarDataGeracaoDevolucaoFornecedorPorDocumentoFiscalEntrada(seqDocumentoFiscalEntrada);
		
		// Realiza a união (UNION) dos resultados
		listaDataGeracaoEntradaSaidaSemLicitacao.addAll(listaDataGeracaoNotaRecebimento);
		listaDataGeracaoEntradaSaidaSemLicitacao.addAll(listaDataGeracaoDevolucaoFornecedor);
		
		if(dataEntrada != null){
			
			// Comportamento da FUNCTION SCEK_DFE_RN.DFEC_VER_DT_DOC_REF
			if(listaDataGeracaoEntradaSaidaSemLicitacao != null && !listaDataGeracaoEntradaSaidaSemLicitacao.isEmpty()){
				
				for (Date dataGeracao : listaDataGeracaoEntradaSaidaSemLicitacao) {
					
					// Verifica a ocorrência da data de geração nos resultados encontrados
					if(dataEntrada.equals(dataGeracao)){
						return true;
					}
					
				}
				return false;
			} else{
				return false;
			}
			
		} else {
			
			// Comportamento da FUNCTION SCEK_DFE_RN.DFEC_VER_DOC_REF
			if(listaDataGeracaoEntradaSaidaSemLicitacao != null && !listaDataGeracaoEntradaSaidaSemLicitacao.isEmpty()){
				return true;
			}
			
		}
		
		return false;

	}
	
	/**
	 * Valida se a data inicial é menor ou igual a data final
	 * @return
	 */
	public boolean dataValida(Date dtEmissaoInicial, Date dtEmissaoFinal){
		if(dtEmissaoInicial != null && dtEmissaoFinal != null){
			if (dtEmissaoInicial.after(dtEmissaoFinal)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Verifica se número do documento fiscal de entrada é válido
	 * @param numero
	 * @throws ApplicationBusinessException
	 */
	private void verificarRestricao(Long numero) throws ApplicationBusinessException {
		if (numero <= 0) {
			throw new ApplicationBusinessException(SceDocumentoFiscalEntradaRNExceptionCode.SCE_DFE_CK4);					
		}
	}
	

	/**
	 * Getters para RNs e DAOs
	 */

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected ScoFornecedorRN getScoFornecedorRN() {
		return scoFornecedorRN;
	}
	
	protected ScoFornecedorEventualRN getScoFornecedorEventualRN() {
		return scoFornecedorEventualRN;
	}
	
	protected SceDocumentoFiscalEntradaDAO getSceDocumentoFiscalEntradaDAO() {
		return sceDocumentoFiscalEntradaDAO;
	}
	
	protected SceEntradaSaidaSemLicitacaoDAO getSceEntradaSaidaSemLicitacaoDAO() {
		return sceEntradaSaidaSemLicitacaoDAO;
	}

	protected SceNotaRecebimentoDAO getSceNotaRecebimentoDAO() {
		return sceNotaRecebimentoDAO;
	}
	
	protected SceDevolucaoFornecedorDAO getSceDevolucaoFornecedorDAO() {
		return sceDevolucaoFornecedorDAO;
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected ScoFornecedorDAO getScoFornecedorDAO() {
		return scoFornecedorDAO;
	}

	public List<SceDocumentoFiscalEntrada> pesquisarNotafiscalEntradaNumeroOuFornecedor(Object param, ScoFornecedor fornecedor) {
		ScoFornecedor fornecedorLoaded = null;
		if (fornecedor != null) {
			fornecedorLoaded = getScoFornecedorDAO().obterPorChavePrimaria(fornecedor.getNumero());
		}
		
		return this.getSceDocumentoFiscalEntradaDAO().pesquisarNotafiscalEntradaNumeroOuFornecedor(param, fornecedorLoaded);
	}
		
	
}
