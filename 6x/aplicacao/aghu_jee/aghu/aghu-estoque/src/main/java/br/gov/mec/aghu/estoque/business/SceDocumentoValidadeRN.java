package br.gov.mec.aghu.estoque.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceDocumentoValidadeDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoValidade;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceTipoMovimentoId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SceDocumentoValidadeRN extends BaseBusiness{

	@EJB
	private ControlarValidadeMaterialRN controlarValidadeMaterialRN;
	
	@EJB
	private SceTipoMovimentosRN sceTipoMovimentosRN;
	
	private static final Log LOG = LogFactory.getLog(SceDocumentoValidadeRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceDocumentoValidadeDAO sceDocumentoValidadeDAO;
	
	@Inject
	private SceTipoMovimentosDAO sceTipoMovimentosDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6553806962444469696L;
	
	public enum SceDocumentoValidadeRNExceptionCode implements BusinessExceptionCode {
		SCE_00369,SCE_00876;
	}
	
	/*
	 * Métodos para Inserir SceDocumentoValidade
	 */

	
	/**
	 * ORADB TRIGGER SCET_DCV_BRI (INSERT)
	 * @param documentoValidade
	 * @param isInserirPorDocumentoValidade variável de package SCEK_VAL_ATUALIZACAO.V_INCL_VAL_POR_DOC_VAL
	 * @throws BaseException
	 */
	private void preInserir(SceDocumentoValidade documentoValidade, boolean isInserirPorDocumentoValidade) throws BaseException{
		
		this.validarTipoMovimentoAtivo(documentoValidade); // RN1
		this.atualizarServidorLogadoDataGeracao(documentoValidade); // RN2
		this.validarQuantidade(documentoValidade); // RN3
		
		// Verifica se a variável (package SCEK_VAL_ATUALIZACAO.V_INCL_VAL_POR_DOC_VAL) é VERDADEIRA
		if(isInserirPorDocumentoValidade){
			this.atualizarValidade(documentoValidade); // RN4	
		}
		
	}
	
	
	/**
	 * Inserir SceDocumentoValidade
	 * @param documentoValidade
	 * @throws BaseException
	 */
	public void inserir(SceDocumentoValidade documentoValidade) throws BaseException{
		this.inserir(documentoValidade, false); // A variável de package SCEK_VAL_ATUALIZACAO.V_INCL_VAL_POR_DOC_VAL é FALSA
	} 
	
	/**
	 * Inserir SceDocumentoValidade
	 * @param documentoValidade
	 * @param isInserirPorDocumentoValidade variável de package SCEK_VAL_ATUALIZACAO.V_INCL_VAL_POR_DOC_VAL
	 * @throws BaseException
	 */
	public void inserir(SceDocumentoValidade documentoValidade, boolean isInserirPorDocumentoValidade) throws BaseException{
		this.preInserir(documentoValidade, isInserirPorDocumentoValidade);
		this.getSceDocumentoValidadeDAO().persistir(documentoValidade);
	}
	
	/*
	 * RNs Inserir
	 */
	
	/**
	 * ORADB PROCEDURE SCEK_DCV_RN.RN_DCVP_VER_TMV_ATIV
	 * Verifica se o tipo de movimento está ativo
	 * @param documentoValidade
	 */
	public void validarTipoMovimentoAtivo(SceDocumentoValidade documentoValidade) throws ApplicationBusinessException{
		
		SceTipoMovimentoId id = new SceTipoMovimentoId();
		id.setComplemento(documentoValidade.getId().getTmvComplemento().byteValue());
		id.setSeq(documentoValidade.getId().getTmvSeq().shortValue());
		SceTipoMovimento tipoMovimento = this.getSceTipoMovimentosDAO().obterPorChavePrimaria(id);
		
		this.getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(tipoMovimento);
	}
	
	/**
	 * ORADB PROCEDURE SCEK_DCV_RN.RN_DCVP_ATU_GERACAO
	 * Atualiza servidor com o usuário logado e data de geração com a data atual
	 * @param material
	 * @throws ApplicationBusinessException  
	 */
	public void atualizarServidorLogadoDataGeracao(SceDocumentoValidade documentoValidade) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		documentoValidade.setServidor(servidorLogado);
		documentoValidade.setDtGeracao(new Date());
		
	}
	
	/**
	 * ORADB RN_DCVP_VER_QTDE SCEK_DCV_RN.RN_DCVP_VER_QTDE
	 * Valida a quantidade do documento
	 * @param documentoValidade
	 */
	public void validarQuantidade(SceDocumentoValidade documentoValidade) throws ApplicationBusinessException{
		
		final Integer quantidade = documentoValidade.getQuantidade();
		
		// Se a quantidade for menor ou igual a zero
		if(documentoValidade == null || quantidade <= 0){
			
			// Obtém o código do estoque almoxarifado do documento de validade
			final Integer ealSeq = documentoValidade.getValidade().getId().getEalSeq();
			
			// Pesquisa estoque almoxarifado por código e que contenha material
			List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxariado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxariafoComMaterialPorSeq(ealSeq);
			
			if(listaEstoqueAlmoxariado != null && !listaEstoqueAlmoxariado.isEmpty()){
				throw new ApplicationBusinessException(SceDocumentoValidadeRNExceptionCode.SCE_00876, listaEstoqueAlmoxariado.get(0).getMaterial().getCodigo());
			} else{
				throw new ApplicationBusinessException(SceDocumentoValidadeRNExceptionCode.SCE_00369);
			}
			
		}
		
		
	}

	/**
	 * ORADB PROCEDURE SCEK_DCV_RN.RN_DCVP_ATU_VAL
	 * @param documentoValidade
	 * @throws BaseException 
	 */
	public void atualizarValidade(SceDocumentoValidade documentoValidade) throws BaseException{

		final Date data = documentoValidade.getValidade().getId().getData();
		final SceEstoqueAlmoxarifado ealOrigem = documentoValidade.getValidade().getEstoqueAlmoxarifado();
		final SceTipoMovimento tipoMovimento = documentoValidade.getTipoMovimento(); 
		final Integer nroDocumento = documentoValidade.getId().getNroDocumento(); 
		final Integer quantidade= documentoValidade.getQuantidade(); 
		
		this.getControlarValidadeMaterialRN().atualizarQuantidadesValidade(data, ealOrigem, tipoMovimento, nroDocumento, quantidade);
	}
	
	/**
	 * Getters para RNs e DAOs
	 */

	protected SceDocumentoValidadeDAO getSceDocumentoValidadeDAO(){
		return sceDocumentoValidadeDAO;
	}
	
	protected SceTipoMovimentosRN getSceTipoMovimentosRN(){
		return sceTipoMovimentosRN;
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected ControlarValidadeMaterialRN getControlarValidadeMaterialRN(){
		return controlarValidadeMaterialRN;
	}
	
	protected SceTipoMovimentosDAO getSceTipoMovimentosDAO(){
		return sceTipoMovimentosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
