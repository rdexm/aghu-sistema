package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcMaterialImpNotaSalaUnDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.MbcMaterialImpNotaSalaUn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de BANCO para MBC_MATERIAL_IMP_NOTA_SALA_UNS
 * 
 * @author aghu
 * 
 */
@Stateless
public class MbcMaterialImpNotaSalaUnRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcMaterialImpNotaSalaUnRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMaterialImpNotaSalaUnDAO mbcMaterialImpNotaSalaUnDAO;


	@EJB
	private IEstoqueFacade iEstoqueFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4389949879578238587L;

	public enum MbcMaterialImpNotaSalaUnRNExceptionCode implements BusinessExceptionCode {
		MBC_00255, MBC_00541, MBC_00542;
	}

	/*
	 * Métodos para PERSISTIR
	 */

	/**
	 * Persistir MbcMaterialImpNotaSalaUn
	 * 
	 * @param materialImpNotaSalaUn
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void persistirMbcMaterialImpNotaSalaUn(MbcMaterialImpNotaSalaUn materialImpNotaSalaUn) throws BaseException {
		if (materialImpNotaSalaUn.getSeq() == null) { // Inserir
			this.inserirMbcMaterialImpNotaSalaUn(materialImpNotaSalaUn);
		} else { // Atualizar
			this.atualizarMbcMaterialImpNotaSalaUn(materialImpNotaSalaUn);
		}
	}

	/*
	 * Métodos INSERIR
	 */

	/**
	 * ORADB MBCT_MNS_BRI (INSERT)
	 * 
	 * @param materialImpNotaSalaUn
	 * @param servidorLogado
	 * @throws BaseException
	 */
	protected void preInserirMbcMaterialImpNotaSalaUn(MbcMaterialImpNotaSalaUn materialImpNotaSalaUn) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		materialImpNotaSalaUn.setServidor(servidorLogado); // RN1
		materialImpNotaSalaUn.setCriadoEm(new Date()); // RN1
		this.verificarMaterialInativo(materialImpNotaSalaUn.getMaterial()); // RN2
		this.verificarUnidadeMedidaMaterial(materialImpNotaSalaUn.getMaterial(), materialImpNotaSalaUn.getUnidadeMedida()); // RN3
	}

	/**
	 * Inserir MbcMaterialImpNotaSalaUn
	 * 
	 * @param materialImpNotaSalaUn
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserirMbcMaterialImpNotaSalaUn(MbcMaterialImpNotaSalaUn materialImpNotaSalaUn) throws BaseException {
		this.preInserirMbcMaterialImpNotaSalaUn(materialImpNotaSalaUn);
		this.getMbcMaterialImpNotaSalaUnDAO().persistir(materialImpNotaSalaUn);
		this.getMbcMaterialImpNotaSalaUnDAO().flush();
	}

	/*
	 * Métodos ATUALIZAR
	 */

	/**
	 * ORADB TRIGGER MBCT_MNS_BRU (UPDATE)
	 * 
	 * @param materialImpNotaSalaUn
	 * @param servidorLogado
	 * @throws BaseException
	 */
	protected void preAtualizarMbcMaterialImpNotaSalaUn(MbcMaterialImpNotaSalaUn materialImpNotaSalaUn) throws BaseException {
		this.verificarMaterialInativo(materialImpNotaSalaUn.getMaterial()); // RN1
		this.verificarUnidadeMedidaMaterial(materialImpNotaSalaUn.getMaterial(), materialImpNotaSalaUn.getUnidadeMedida()); // RN2
	}

	/**
	 * Atualizar MbcMaterialImpNotaSalaUn
	 * 
	 * @param materialImpNotaSalaUn
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void atualizarMbcMaterialImpNotaSalaUn(MbcMaterialImpNotaSalaUn materialImpNotaSalaUn) throws BaseException {
		this.preAtualizarMbcMaterialImpNotaSalaUn(materialImpNotaSalaUn);
		this.getMbcMaterialImpNotaSalaUnDAO().atualizar(materialImpNotaSalaUn);
		this.getMbcMaterialImpNotaSalaUnDAO().flush();
	}
	
	
	/*
	 * Métodos REMOVER
	 */
	
	

	/**
	 * Remover MbcMaterialImpNotaSalaUn
	 * 
	 * @param materialImpNotaSalaUn
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void removerMbcMaterialImpNotaSalaUn(MbcMaterialImpNotaSalaUn materialImpNotaSalaUn) throws BaseException {
		
		MbcMaterialImpNotaSalaUn material = this.getMbcMaterialImpNotaSalaUnDAO().obterPorChavePrimaria(materialImpNotaSalaUn.getSeq());
		
		if (material != null) {
			this.getMbcMaterialImpNotaSalaUnDAO().remover(material);
			this.getMbcMaterialImpNotaSalaUnDAO().flush();
		}
	}

	/*
	 * Procedures
	 */

	/**
	 * ORADB PROCEDURE MBCK_MNS_RN.RN_MNSP_VER_MATERIAL Verifica se o material está ativo
	 * 
	 * @param material
	 * @throws BaseException
	 */
	protected void verificarMaterialInativo(ScoMaterial material) throws BaseException {
		// Verifica se o material selecionado está ATIVO
		if (material != null && !DominioSituacao.A.equals(material.getIndSituacao())) {
			throw new ApplicationBusinessException(MbcMaterialImpNotaSalaUnRNExceptionCode.MBC_00255);
		}
	}

	/**
	 * ORADB PROCEDURE MBCK_MNS_RN.RN_MNSP_VER_UNID_MAT Verifica se o material está ativo
	 * 
	 * @param material
	 * @param unidadeMedida
	 * @throws BaseException
	 */
	protected void verificarUnidadeMedidaMaterial(ScoMaterial material, ScoUnidadeMedida unidadeMedida) throws BaseException {

		// É OBRIGATÓRIO informar a unidade de medida do material para impressão
		if (material != null && unidadeMedida == null) {
			throw new ApplicationBusinessException(MbcMaterialImpNotaSalaUnRNExceptionCode.MBC_00541);
		}

		// Verifica se a unidade de medida do material é DIFERENTE da unidade de medida para impressão
		if ((material != null && !material.getUnidadeMedida().equals(unidadeMedida))) {
			// Verifica a ocorrência de registros em SCE_CONVERSAO_UNIDADE_CONSUMOS
			SceConversaoUnidadeConsumos conversaoUnidadeConsumos = getEstoqueFacade().obterConversaoUnidadePorMaterialUnidadeMedida(material, unidadeMedida);
			if (conversaoUnidadeConsumos == null) {
				throw new ApplicationBusinessException(MbcMaterialImpNotaSalaUnRNExceptionCode.MBC_00542);
			}
		}

	}
	
	/**
	 * Verifica a ocorrência de registros em SCE_CONVERSAO_UNIDADE_CONSUMOS
	 * @param material
	 * @return
	 */
	protected SceConversaoUnidadeConsumos obterConversaoUnidadePorMaterial(ScoMaterial material){
		return getEstoqueFacade().obterConversaoUnidadePorMaterial(material);
	}


	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected MbcMaterialImpNotaSalaUnDAO getMbcMaterialImpNotaSalaUnDAO() {
		return mbcMaterialImpNotaSalaUnDAO;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.iEstoqueFacade;
	}

}
