package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.Object;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterItemModeloBasicoMedicamentoRN extends BaseBusiness {

	@EJB
	private ManterModeloBasicoMedicamentoRN manterModeloBasicoMedicamentoRN;
	
	private static final Log LOG = LogFactory.getLog(ManterItemModeloBasicoMedicamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MpmModeloBasicoMedicamentoDAO mpmModeloBasicoMedicamentoDAO;
	
	@Inject
	private MpmItemModeloBasicoMedicamentoDAO mpmItemModeloBasicoMedicamentoDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4810668075666946646L;

	private static final String evento_insert = "INSERT";
		
	private static final String evento_delete = "DELETE";
	
	public enum ManterItemModeloBasicoMedicamentoRNExceptionCode implements BusinessExceptionCode {

		ERRO_INSERIR_ITEM_MOD_BASICO_MDTOS, ERRO_ATUALIZAR_ITEM_MOD_BASICO_MDTOS, AFA_00232, MPM_00792, RAP_00175, MPM_01226;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwException(Throwable cause, Object... params)
		throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}
	
	/**
	 * ORADB MPMT_IMM_BRI, 
	 * Trigger de insert de MPM_ITEM_MOD_BASICO_MDTOS
	 * @param itemModeloBasicoMedicamento
	 */
	public void inserirItemModeloBasicoMedicamento(MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		try {
			itemModeloBasicoMedicamento.setServidor(servidorLogado);

			this.preInserirItemModeloBasicoMedicamento(itemModeloBasicoMedicamento);
			this.getMpmItemModeloBasicoMedicamentoDAO().persistir(itemModeloBasicoMedicamento);
			this.getMpmItemModeloBasicoMedicamentoDAO().flush();
			this.enforceItemModeloBasicoMedicamento(itemModeloBasicoMedicamento, evento_insert);

		} catch (Exception e) {

			logError(e.getMessage(), e);
			ManterItemModeloBasicoMedicamentoRNExceptionCode.ERRO_INSERIR_ITEM_MOD_BASICO_MDTOS.throwException(e);

		}

	}
	
	/**
	 * ORADB MPMT_IMM_BRU, 
	 * Trigger de update de MPM_ITEM_MOD_BASICO_MDTOS
	 * @param itemModeloBasicoMedicamento
	 */
	public void atualizarItemModeloBasicoMedicamento(MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento) throws ApplicationBusinessException {
		
		try {
			
			this.preAtualizarItemModeloBasicoMedicamento(itemModeloBasicoMedicamento);
			this.getMpmItemModeloBasicoMedicamentoDAO().merge(itemModeloBasicoMedicamento);
			this.getMpmItemModeloBasicoMedicamentoDAO().flush();
			
		} catch (Exception e) {

			logError(e.getMessage(), e);
			ManterItemModeloBasicoMedicamentoRNExceptionCode.ERRO_ATUALIZAR_ITEM_MOD_BASICO_MDTOS.throwException(e);

		}
		
	}
	
	/**
	 * ORADB TRIGGER MPMT_IMM_ASD
	 * @param itemModeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	public void removerItemModeloBasicoMedicamento(MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento) throws ApplicationBusinessException {
		
		itemModeloBasicoMedicamento = getMpmItemModeloBasicoMedicamentoDAO().obterPorChavePrimaria(itemModeloBasicoMedicamento.getId());
		
		this.verificarUltimoItemModeloBasicoMedicamento(itemModeloBasicoMedicamento.getId().getModeloBasicoPrescricaoSeq(), itemModeloBasicoMedicamento.getId().getModeloBasicoMedicamentoSeq());
		this.getMpmItemModeloBasicoMedicamentoDAO().remover(itemModeloBasicoMedicamento);
		this.getMpmItemModeloBasicoMedicamentoDAO().flush();
		this.enforceItemModeloBasicoMedicamento(itemModeloBasicoMedicamento, evento_delete);
		
	}
	
	
	/**
	 * Implementa métodos da package mpmk_imm_rn utilizada em MPM_ITEM_MOD_BASICO_MDTOS
	 * @param itemModeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void preInserirItemModeloBasicoMedicamento(MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento) throws ApplicationBusinessException {
				
		if (itemModeloBasicoMedicamento == null) {
			
			throw new IllegalArgumentException("Parâmetro obrigatório");
		
		}
		
		//this.validarDoseMedicamento(itemModeloBasicoMedicamento.getDose(), itemModeloBasicoMedicamento.getMedicamento().getMatCodigo());
		this.validarExclusividade(itemModeloBasicoMedicamento.getServidor().getId().getMatricula()
				, itemModeloBasicoMedicamento.getServidor().getId().getVinCodigo()
				, itemModeloBasicoMedicamento.getModeloBasicoMedicamento().getId().getModeloBasicoPrescricaoSeq());
		this.validarFormaDosagem(itemModeloBasicoMedicamento.getMedicamento().getMatCodigo()
				, itemModeloBasicoMedicamento.getFormaDosagem().getSeq());
		this.validarObservacao(itemModeloBasicoMedicamento.getMedicamento().getMatCodigo()
				, itemModeloBasicoMedicamento.getObservacao());
		
	}
	
	/**
	 * ORADB TRIGGER MPMT_IMM_BRU
	 * @param itemModeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void preAtualizarItemModeloBasicoMedicamento(MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			ManterItemModeloBasicoMedicamentoRNExceptionCode.RAP_00175.throwException();
		}
		
		MpmItemModeloBasicoMedicamento antigoItemModeloBasicoMedicamento = this.getMpmItemModeloBasicoMedicamentoDAO().obterPorChavePrimaria(itemModeloBasicoMedicamento.getId());
		 
		if (itemModeloBasicoMedicamento != null && antigoItemModeloBasicoMedicamento != null) {
			
		/*	if (CoreUtil.modificados(itemModeloBasicoMedicamento.getDose(), antigoItemModeloBasicoMedicamento.getDose()) || CoreUtil.modificados(itemModeloBasicoMedicamento.getId().getMedicamentoMaterialCodigo(), itemModeloBasicoMedicamento.getId().getMedicamentoMaterialCodigo())) {
				
				this.validarDoseMedicamento(itemModeloBasicoMedicamento.getDose(), itemModeloBasicoMedicamento.getId().getMedicamentoMaterialCodigo());
				
			}*/
			
			if (CoreUtil.modificados(servidorLogado, antigoItemModeloBasicoMedicamento.getServidor())) {
				
				this.validarExclusividade(servidorLogado.getId().getMatricula(), servidorLogado.getId().getVinCodigo(), itemModeloBasicoMedicamento.getId().getModeloBasicoPrescricaoSeq());
				
			}
			
			if (CoreUtil.modificados(itemModeloBasicoMedicamento.getId().getMedicamentoMaterialCodigo(), antigoItemModeloBasicoMedicamento.getId().getMedicamentoMaterialCodigo())) {
				
				this.validarFormaDosagem(itemModeloBasicoMedicamento.getId().getMedicamentoMaterialCodigo(), itemModeloBasicoMedicamento.getFormaDosagem().getSeq());
				
			}
			
			if (CoreUtil.modificados(itemModeloBasicoMedicamento.getObservacao(), antigoItemModeloBasicoMedicamento.getObservacao()) || CoreUtil.modificados(itemModeloBasicoMedicamento.getId().getMedicamentoMaterialCodigo(), antigoItemModeloBasicoMedicamento.getId().getMedicamentoMaterialCodigo())) {
				
				this.validarObservacao(itemModeloBasicoMedicamento.getId().getMedicamentoMaterialCodigo(), itemModeloBasicoMedicamento.getObservacao());
				
			}
			
		}
		
	}
	
	/**
	 * ORADB PROCEDURE MPMP_ENFORCE_IMM_RULES
	 * @param itemModeloBasicoMedicamento
	 * @param evento
	 * @throws ApplicationBusinessException
	 */
	public void enforceItemModeloBasicoMedicamento(MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento, String evento) throws ApplicationBusinessException {
		
		if (evento.equals(evento_insert) || evento.equals(evento_delete)) {
			
			this.verificarSolucao(itemModeloBasicoMedicamento.getId().getModeloBasicoPrescricaoSeq(), itemModeloBasicoMedicamento.getId().getModeloBasicoMedicamentoSeq());
			
		} 
		
	}
	
	/**
	 * ORADB PROCEDURE RN_IMMP_VER_SOLUCAO
	 * @param modeloBasicoPrescricaoSeq
	 * @param modeloBasicoMedicamentoSeq
	 * @throws ApplicationBusinessException
	 */
	public void verificarSolucao(Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoMedicamentoSeq) throws ApplicationBusinessException {
		
		MpmModeloBasicoMedicamento modeloBasicoMedicamento = this.getModeloBasicoMedicamentoDAO().obterModeloBasicoMedicamento(modeloBasicoPrescricaoSeq, modeloBasicoMedicamentoSeq);
		Long qtdItens = this.getMpmItemModeloBasicoMedicamentoDAO().obterQuantidadeItensMedicamento(modeloBasicoPrescricaoSeq, modeloBasicoMedicamentoSeq);
		
		if (modeloBasicoMedicamento != null) {
			
			if (qtdItens != null && qtdItens > 1) {
				
				modeloBasicoMedicamento.setIndSolucao(true);
				
			} else {
				
				modeloBasicoMedicamento.setIndSolucao(false);
				
			}
			
			this.getManterModeloBasicoMedicamentoRN().atualizarModeloBasicoMedicamento(modeloBasicoMedicamento);
			
		}
		
	}
	
	/**
	 * ORADB Procedure mpmk_imm_rn.rn_immp_ver_exclusiv
	 * @param matriculaServidor
	 * @param vinculoServidor
	 * @param mdbSeq
	 * @throws ApplicationBusinessException
	 */
	public void validarExclusividade(Integer matriculaServidor, Short vinculoServidor, Integer mdbSeq) throws ApplicationBusinessException {
				
		this.getManterModeloBasicoMedicamentoRN().verificarExclusividade(mdbSeq, matriculaServidor, vinculoServidor);
		
	}
	
	/**
	 * ORADB mpmk_imm_rn.rn_immp_ver_frm_dos
	 * @param medicamentoMaterialCodigo
	 * @param fdsSeq
	 * @throws ApplicationBusinessException
	 */
	public void validarFormaDosagem(Integer medicamentoMaterialCodigo, Integer fdsSeq) throws ApplicationBusinessException {
		
		AfaFormaDosagem formaDosagens = getFarmaciaFacade().obterAfaFormaDosagem(fdsSeq);
		
		if (formaDosagens != null) {
			
			if (!formaDosagens.getIndSituacao().equals(DominioSituacao.A)) {
				
				throw new ApplicationBusinessException(ManterItemModeloBasicoMedicamentoRNExceptionCode.AFA_00232);
				
			} else if (!formaDosagens.getAfaMedicamentos().getMatCodigo().equals(medicamentoMaterialCodigo)) {
				
				throw new ApplicationBusinessException(ManterItemModeloBasicoMedicamentoRNExceptionCode.MPM_00792);
				
			}
			
		}
		
	}
	
	/**
	 * ORADB PROCEDURE mpmk_imm_rn.rn_immp_ver_obs
	 * @param codigoMedicamento
	 * @param observacao
	 * @throws ApplicationBusinessException
	 */
	public void validarObservacao(Integer codigoMedicamento, String observacao) throws ApplicationBusinessException {
		
		this.getPrescricaoMedicaFacade().verificaExigeObservacao(codigoMedicamento, observacao);
		
	}
	
	/**
	 * ORADB PROCEDURE RN_IMMP_VER_ULT_ITEM
	 * @param modeloBasicoPrescricaoSeq
	 * @param modeloBasicoMedicamentoSeq
	 * @throws ApplicationBusinessException
	 */
	public void verificarUltimoItemModeloBasicoMedicamento(Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoMedicamentoSeq) throws ApplicationBusinessException {
		
		Long qtdItens = this.getMpmItemModeloBasicoMedicamentoDAO().obterQuantidadeItensMedicamento(modeloBasicoPrescricaoSeq, modeloBasicoMedicamentoSeq);
		
		if (qtdItens != null && qtdItens == 1) {
			
			throw new ApplicationBusinessException(ManterItemModeloBasicoMedicamentoRNExceptionCode.MPM_01226);
			
		}
		
	}

	
	protected MpmItemModeloBasicoMedicamentoDAO getMpmItemModeloBasicoMedicamentoDAO() {
		return mpmItemModeloBasicoMedicamentoDAO;
	}
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}
	
	protected ManterModeloBasicoMedicamentoRN getManterModeloBasicoMedicamentoRN() {
		return manterModeloBasicoMedicamentoRN;
	}
	
	protected MpmModeloBasicoMedicamentoDAO getModeloBasicoMedicamentoDAO() {
		return mpmModeloBasicoMedicamentoDAO;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
