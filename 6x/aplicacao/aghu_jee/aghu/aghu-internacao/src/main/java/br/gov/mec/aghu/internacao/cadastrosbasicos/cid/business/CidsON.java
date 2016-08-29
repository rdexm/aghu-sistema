package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity"})
@Stateless
public class CidsON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(CidsON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6233606176577960104L;

	public enum CidONExceptionCode implements BusinessExceptionCode {
		CODIGO_CID_NULO, 
		ERRO_REMOVER_CID, 
		ERRO_REMOVER_CID_CONSTRAINT_MPM_NOTA_OBITOS, 
		ERRO_REMOVER_CID_CONSTRAINT_MPM_MOD_BASIC_CIDS, 
		ERRO_REMOVER_CID_CONSTRAINT_MPM_CUF_CID_FK1, 
		ERRO_REMOVER_CID_CONSTRAINT_MPM_PATOLOGIA_OBITOS, 
		ERRO_REMOVER_CID_CONSTRAINT_MPM_CID_ATENDIMENTOS, 
		ERRO_REMOVER_CID_CONSTRAINT_AGH_CIDS, 
		ERRO_REMOVER_CID_CONSTRAINT_AFA_MDTO_NAO_PERMITIDO_CIDS, 
		ERRO_PERSISTIR_CID, 
		ERRO_ATUALIZAR_CID,
		DESCRICAO_CID_NULA, 
		ERRO_CID_JA_EXISTENTE,
		ERRO_GRUPO_CAMPO_OBRIGATORIO, 
		ERRO_SERVIDOR_CAMPO_OBRIGATORIO,
		SERVIDOR_IS_NOT_NULL,
		GCD_SEQ_IS_NOT_NULL, 
		IND_EXIGE_LOCAL_TUMOR_EM_IS_NOT_NULL,
		RESTRICAO_SEXO_IS_NOT_NULL,
		ESTADIAVEL_IS_NOT_NULL,
		CRIADO_EM_IS_NOT_NULL,
		SITUACAO_EM_IS_NOT_NULL, 
		IND_EXIGE_LAUDO_TRATAMENTO_IS_NOT_NULL, 
		PERMITE_PRESC_QUIMIO_IS_NOT_NULL, 
		CID_IGUAL_CATEGORIA, 
		CID_SECUNDARIOS_INCORRETOS, CODIGO_NAO_PERTENCE_A_CATEGORIA_INFORMADA, 
		CID_SECUNDARIO_FINAL_IGUAL_CID_SECUNDARIO_INICIAL,
		CID_SECUNDARIO_INICIAL_IGUAL_CATEGORIA, 
		CID_SECUNDARIO_FINAL_IGUAL_CATEGORIA, 
		CID_SECUNDARIO_INICIAL_MAIOR_CID_FINAL,
		CID_CODIGO_MAIOR_IGUAL_CID_INICIAL,
		CID_CODIGO_MENOR_IGUAL_CID_FINAL, 
		ERRO_REMOVER_CID_CONSTRAINT_CIDS_ATENDIMENTOS,
		ERRO_REMOVER_CID_FORA_DO_PERIODO, 
		ERRO_REMOVER_CID_CONSTRAINT_PATOLOGIA_OBITOS,
 CID_CODIGO_DUPLICADO,
	}
	
	/**
	 * Método para obter <code>AghCids</code> a partir do codigo
	 * 
	 * @param codigo
	 * @return AghCids
	 */
	public AghCid obterCid(String codigo) {
		return this.getAghuFacade().obterCid(codigo);
	}
	
	/**
	 * Método para obter um objeto AghCids através do seu ID.
	 * 
	 * @param seq
	 * @return
	 */
	public AghCid obterCidporSeq(Integer seq) {
		if (seq == null) {
			return null;
		} else {
			return getAghuFacade().obterAghCidsPorChavePrimaria(seq);
		}
	}

	public Long pesquisaCount(Integer seq, String codigo, String descricao, DominioSituacao situacaoPesquisa) {
		return this.getAghuFacade().pesquisaPorCidCount(seq, codigo, descricao, situacaoPesquisa);
	}

	public List<AghCid> pesquisa(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Integer seq,
			String codigo, String descricao, DominioSituacao situacaoPesquisa) {
		return this.getAghuFacade().pesquisaPorCid(firstResult, maxResults, orderProperty, asc, seq, codigo, descricao,
				situacaoPesquisa);
	}

	public void excluirCid(AghCid cid) throws ApplicationBusinessException {
		try {
			this.validarDataCid(cid);
			this.getAghuFacade().removerAghCid(cid);

		} catch (PersistenceException e) {
			logError("Erro ao remover a cid.", e);

			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause()
							.getClass())) {
				if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "MPM_POB_CID_FK1") 
						) {// possui dependencia em	 MPM_PATOLOGIA_OBITOS
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_MPM_PATOLOGIA_OBITOS);
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "MPM_NOB_CID_FK1")) {// possui dependencia  em MPM_NOTA_OBITOS
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_MPM_NOTA_OBITOS);
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "MPM_MBC_CID_FK1")) {// possui dependencia em MPM_MOD_BASIC_CIDS
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_MPM_MOD_BASIC_CIDS);
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "MPM_CUF_CID_FK1")) {// possui dependencia em MPM_CID_UNID_FUNCIONAIS
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_MPM_CUF_CID_FK1);
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "MPM_CIA_CID_FK1")) {// possui dependencia em MPM_CID_ATENDIMENTOS
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_MPM_CID_ATENDIMENTOS);
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "AGH_CID_CID_FK4")) {// possui dependencia em AGH_CIDS
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_AGH_CIDS);
				}// AGH_CID_CID_FK3
				else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "AGH_CID_CID_FK3")) {// possui dependencia em AGH_CIDS
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_AGH_CIDS);
				}// AGH_CID_CID_FK2
				else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "AGH_CID_CID_FK2")) {// possui dependencia  em  AGH_CIDS
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_AGH_CIDS);
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "AFA_MPC_CID_FK1")) {// possui dependencia em AFA_MDTO_NAO_PERMITIDO_CIDS
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_AFA_MDTO_NAO_PERMITIDO_CIDS);
				}
				else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "FAT_TPC_CID_FK1")) {// possui dependencia em Cids Atendimentos
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_CIDS_ATENDIMENTOS);
				}
				else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "AIN_CDI_CID_FK1")) {// possui dependencia em Cids Atendimentos
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_CIDS_ATENDIMENTOS);
				}
				else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "AAC_CAT_CID_FK1")) {// possui dependencia em Cids Atendimentos																				
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_CIDS_ATENDIMENTOS);
				}
				else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "AIN_ATU_CID_FK1")) {
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_FORA_DO_PERIODO);
				} 
				else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "AGH_CCI_CID_FK1")) {
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_FORA_DO_PERIODO);
				} 
				else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "MCI_CIF_CID_FK1")) {
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_FORA_DO_PERIODO);
				} 				
				else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(), "AAC_CCO_CID_FK1")) {// possui
					// dependencia em AFA_MDTO_NAO_PERMITIDO_CIDS
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_REMOVER_CID_CONSTRAINT_PATOLOGIA_OBITOS);
				}
				else{					
					throw new ApplicationBusinessException(CidONExceptionCode.ERRO_REMOVER_CID);
				}
			}
		}
	}
	
	public void incluirCid(AghCid cid) throws ApplicationBusinessException {

		try {
			this.transformarTextosCaixaAlta(cid);
			this.validarCid(cid);
			this.getAghuFacade().inserirAghCid(cid);		 		
		}catch (PersistenceException e) {
			logError("Exceção capturada: ", e);
			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause()
							.getClass())) {
				if (((ConstraintViolationException) e.getCause()) //verifica se é Unique
						.getSQLException().getNextException().getLocalizedMessage().contains("agh_cid_uk1")) {
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_CID_JA_EXISTENTE);
				}
				if (((ConstraintViolationException) e.getCause()) //verifica a constraint de Grupo
						.getSQLException().getNextException().getLocalizedMessage().contains("agh_cid_gcd_fk1")) {
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_GRUPO_CAMPO_OBRIGATORIO);
				}
				if (((ConstraintViolationException) e.getCause()) //verifica a constraint de Grupo
						.getSQLException().getNextException().getLocalizedMessage().contains("agh_cid_ser_fk1")) {
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_SERVIDOR_CAMPO_OBRIGATORIO);
				}
			}
			logError("Erro ao incluir CID.", e);			
			throw new ApplicationBusinessException(
					CidONExceptionCode.ERRO_PERSISTIR_CID);
		}

	}

	public void atualizarCid(AghCid cid) throws ApplicationBusinessException {
		try {
			this.validarCid(cid);
			this.transformarTextosCaixaAlta(cid);
			this.getAghuFacade().atualizarAghCid(cid);
		} catch (PersistenceException e) {
			logError("Exceção capturada: ", e);
			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause()
							.getClass())) {
				if (((ConstraintViolationException) e.getCause()) //verifica se é Unique
						.getSQLException().getNextException().getLocalizedMessage().contains("agh_cid_uk1")) {
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_CID_JA_EXISTENTE);
				}
				if (((ConstraintViolationException) e.getCause()) //verifica a constraint de Grupo
						.getSQLException().getNextException().getLocalizedMessage().contains("agh_cid_gcd_fk1")) {
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_GRUPO_CAMPO_OBRIGATORIO);
				}
				if (((ConstraintViolationException) e.getCause()) //verifica a constraint de Grupo
						.getSQLException().getNextException().getLocalizedMessage().contains("agh_cid_ser_fk1")) {
					throw new ApplicationBusinessException(
							CidONExceptionCode.ERRO_SERVIDOR_CAMPO_OBRIGATORIO);
				}
			}
			logError("Erro ao atualizar", e);
			throw new ApplicationBusinessException(
					CidONExceptionCode.ERRO_ATUALIZAR_CID);
		}

	}

	public List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuId(String descricao, Integer limiteRegistros) {
		return this.getAghuFacade().pesquisarCidsSemSubCategoriaPorCodigoDescricaoOuId(descricao, limiteRegistros);
	}

	public List<AghCid> pesquisarCidsComSubCategoriaPorDescricaoOuId(
			String descricao, Integer limiteRegistros) {
		return this.getAghuFacade().pesquisarCidsComSubCategoriaCodigoPorDescricaoOuId(descricao, limiteRegistros);
	}

	public AghCid nova() throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghCid cids = new AghCid();
		//Setando Valores iniciais
		cids.setSituacao(DominioSituacao.A);
		cids.setIndExigeLocalTumor(false);
		cids.setPermitePrescQuimio(false);
		cids.setEstadiavel(false);
		cids.setRestricaoSexo(DominioSexoDeterminante.Q);
		cids.setExigeLaudoTratamento(false);
		cids.setCriadoEm(new Date());
		cids.setServidor(servidorLogado);
		
		return cids;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void validarCid(AghCid cid) throws ApplicationBusinessException {
		
		if (!StringUtils.isNotBlank(cid.getCodigo())) {
			throw new ApplicationBusinessException(CidONExceptionCode.CODIGO_CID_NULO);
		}		
		if (!StringUtils.isNotBlank(cid.getDescricao())) {
			throw new ApplicationBusinessException(CidONExceptionCode.DESCRICAO_CID_NULA);
		}
		if(cid.getServidor() == null) {
			throw new ApplicationBusinessException(CidONExceptionCode.SERVIDOR_IS_NOT_NULL);
		}		
		if(cid.getGrupoCids() == null) {
			throw new ApplicationBusinessException(CidONExceptionCode.GCD_SEQ_IS_NOT_NULL);
		}		
		if(cid.getCriadoEm() == null) {
			throw new ApplicationBusinessException(CidONExceptionCode.CRIADO_EM_IS_NOT_NULL);
		}
		if(cid.getSituacao() == null) {
			throw new ApplicationBusinessException(CidONExceptionCode.SITUACAO_EM_IS_NOT_NULL);
		}
		if(cid.getIndExigeLocalTumor() == null) {
			throw new ApplicationBusinessException(CidONExceptionCode.IND_EXIGE_LOCAL_TUMOR_EM_IS_NOT_NULL);
		}
		if(cid.getExigeLaudoTratamento() == null) {
			throw new ApplicationBusinessException(CidONExceptionCode.IND_EXIGE_LAUDO_TRATAMENTO_IS_NOT_NULL);
		}
		if(cid.getPermitePrescQuimio() == null) {
			throw new ApplicationBusinessException(CidONExceptionCode.PERMITE_PRESC_QUIMIO_IS_NOT_NULL);
		}
		if(cid.getRestricaoSexo() == null) {
			throw new ApplicationBusinessException(CidONExceptionCode.RESTRICAO_SEXO_IS_NOT_NULL);
		}
		if(cid.getEstadiavel() == null) {
			throw new ApplicationBusinessException(CidONExceptionCode.ESTADIAVEL_IS_NOT_NULL);
		}
		
		
		if(cid.getCid() != null){			
				if(StringUtils.isNotBlank(cid.getCodigo()) 
					&&  StringUtils.isNotBlank(cid.getCid().getCodigo()) 
					&& cid.getCodigo().equalsIgnoreCase(cid.getCid().getCodigo())){
				throw new ApplicationBusinessException(CidONExceptionCode.CID_IGUAL_CATEGORIA);
			}
		}
		
		if(StringUtils.isNotBlank(cid.getCidInicialSecundario()) && StringUtils.isNotBlank(cid.getCidInicialSecundario()) && cid.getCidInicialSecundario().equalsIgnoreCase(cid.getCidFinalSecundario()) ){						
			throw new ApplicationBusinessException(CidONExceptionCode.CID_SECUNDARIO_FINAL_IGUAL_CID_SECUNDARIO_INICIAL);
		}
		if(cid.getCidInicialSecundario()!= null && cid.getCidFinalSecundario() == null ||
				cid.getCidFinalSecundario()!= null && cid.getCidInicialSecundario() == null){
			throw new ApplicationBusinessException(CidONExceptionCode.CID_SECUNDARIOS_INCORRETOS);
		}

		if (cid.getSeq() == null) {
			// Verificando Código do CID duplicado
			final AghCid cidCodigo = this.obterCid(cid.getCodigo());
			if (cidCodigo != null) {
				throw new ApplicationBusinessException(CidONExceptionCode.CID_CODIGO_DUPLICADO, cid.getCodigo());
			}
		}

		if(cid.getCid()!= null && cid.getCodigo() != null){
			if (cid.getCodigo().length() >= 3){
				if(!( cid.getCodigo().substring(1, 3).equalsIgnoreCase(cid.getCid().getCodigo().substring(1, 3)))){
					throw new ApplicationBusinessException(CidONExceptionCode.CODIGO_NAO_PERTENCE_A_CATEGORIA_INFORMADA);
				}				
			}
		}
		
		if(cid.getCodigo() != null && cid.getCidInicialSecundario() != null){
			int maxCodigo = cid.getCodigo().length() < 3 ? cid.getCodigo().length() : 3;
			int maxSecundarioCodigo = cid.getCidInicialSecundario().length() < 3 ? cid.getCidInicialSecundario().length() : 3;
			if(( cid.getCodigo().substring(1, maxCodigo).equalsIgnoreCase(cid.getCidInicialSecundario().substring(1, maxSecundarioCodigo)))){
				throw new ApplicationBusinessException(CidONExceptionCode.CID_SECUNDARIO_INICIAL_IGUAL_CATEGORIA);
			}
		}
		
		if(cid.getCodigo() != null && cid.getCidFinalSecundario() != null){
			int maxCodigo = cid.getCodigo().length() < 3 ? cid.getCodigo().length() : 3;
			int maxSecundarioCodigo = cid.getCidFinalSecundario().length() < 3 ? cid.getCidFinalSecundario().length() : 3;
			if(( cid.getCodigo().substring(1, maxCodigo).equalsIgnoreCase(cid.getCidFinalSecundario().substring(1, maxSecundarioCodigo)))){
				throw new ApplicationBusinessException(CidONExceptionCode.CID_SECUNDARIO_FINAL_IGUAL_CATEGORIA);
			}
		}
		
		if(cid.getCidInicialSecundario() != null && cid.getCidFinalSecundario()!= null){			
			compararTamanhoCids(cid.getCidInicialSecundario(),cid.getCidFinalSecundario());
		}
		
		if(cid.getCodigo() != null && cid.getCidInicialSecundario() != null && cid.getCidFinalSecundario()!= null){
			compararTamanhoCidSecundariosECodigo(cid.getCodigo(),cid.getCidInicialSecundario(),cid.getCidFinalSecundario());			
		}
		
		
	}
	
	public void validarDataCid(AghCid cids) throws ApplicationBusinessException{
	
		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AGH);
		
		Date hoje = new Date();				  
		float qtdDias = CoreUtil.diferencaEntreDatasEmDias(hoje, cids.getCriadoEm());
			
			if(qtdDias > parametro.getVlrNumerico().floatValue()){
				throw new ApplicationBusinessException(
						CidONExceptionCode.ERRO_REMOVER_CID_FORA_DO_PERIODO);
			}
		
				
	}
	
	public void compararTamanhoCids(String cidInicial, String cidFinal)throws ApplicationBusinessException {
		//Comparar letra por letra entre 2 String
		int compare = cidInicial.compareToIgnoreCase(cidFinal);
		if (compare > 0) {
			throw new ApplicationBusinessException(CidONExceptionCode.CID_SECUNDARIO_INICIAL_MAIOR_CID_FINAL);
		}
		if (compare == 0) {
			throw new ApplicationBusinessException(CidONExceptionCode.CID_SECUNDARIO_INICIAL_IGUAL_CATEGORIA);
		}
	}
	//Se código CID é maior ou igual ao código do CID INICIAL SEGUNDÁRIO e menor ou igual ao  CID FINAL SEGUNDÁRIO
	public void compararTamanhoCidSecundariosECodigo(String codigo, String cidInicial, String cidFinal)throws ApplicationBusinessException {
		//Comparar letra por letra entre 2 String
		int compare = codigo.compareToIgnoreCase(cidInicial);
		if(compare > 0){
			throw new ApplicationBusinessException(CidONExceptionCode.CID_CODIGO_MAIOR_IGUAL_CID_INICIAL);
		}
		if (compare == 0) {
			throw new ApplicationBusinessException(CidONExceptionCode.CID_SECUNDARIO_INICIAL_IGUAL_CATEGORIA);
		}
		
		int compare2 = codigo.compareTo(cidFinal);
		if(compare2 < 0){
			// se for menor lança exceção
			throw new ApplicationBusinessException(CidONExceptionCode.CID_CODIGO_MENOR_IGUAL_CID_FINAL);
		}
		if (compare2 == 0) {
			// se for igual  lança exceção
			throw new ApplicationBusinessException(CidONExceptionCode.CID_SECUNDARIO_FINAL_IGUAL_CATEGORIA);
		}
	}
	
	private void transformarTextosCaixaAlta(AghCid cid) {
		cid.setDescricao(cid.getDescricao() == null  ? null : cid.getDescricao().toUpperCase() );
		cid.setDescricaoEditada(cid.getDescricaoEditada() == null ? null : cid.getDescricaoEditada().toUpperCase());
		cid.setCodigo(cid.getCodigo() == null ? null : cid.getCodigo().toUpperCase());
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
