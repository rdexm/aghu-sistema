package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaCirgRealizada;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaCirgRealizadaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ManterAltaCirgRealizadaRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaCirgRealizadaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaCirgRealizadaDAO mpmAltaCirgRealizadaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4641219329232721805L;

	public enum ManterAltaCirgRealizadaRNExceptionCode implements
	BusinessExceptionCode {

		MPM_02872, MPM_02619, MPM_02649, MPM_02650, MPM_02620, MPM_02622, MPM_02624, MPM_02623;

	}

	/**
	 * @ORADB MPMT_ACR_BRI
	 * @param altaCirgRealizada
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaCirgRealizada(MpmAltaCirgRealizada altaCirgRealizada)
	throws ApplicationBusinessException {

		this.preInserirAltaCirgRealizada(altaCirgRealizada);
		this.getMpmAltaCirgRealizadaDAO().persistir(altaCirgRealizada);
		this.getMpmAltaCirgRealizadaDAO().flush();
		
	}

	protected void preInserirAltaCirgRealizada(
			MpmAltaCirgRealizada altaCirgRealizada) throws ApplicationBusinessException{

		// Verifica se ALTA_SUMARIOS está ativo
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaCirgRealizada.getAltaSumario());

		this.verificarDataCirurgia(altaCirgRealizada);

	}

	protected void preAtualizarAltaCirgRealizada(MpmAltaCirgRealizada altaCirgRealizada) throws ApplicationBusinessException{

		MpmAltaCirgRealizadaDAO dao = this.getMpmAltaCirgRealizadaDAO();
		MpmAltaCirgRealizada altaCirgRealizadaVelha = dao.obterAltaCirgRealizadaOriginalDesatachado(altaCirgRealizada);

		//RN2 - Se a nova situação (IND_SITUACÃO) for igual a “A” e a antiga situação for igual a “I”
		this.getAltaSumarioRN().verificarSituacao(altaCirgRealizada.getIndSituacao(), altaCirgRealizadaVelha.getIndSituacao(), ManterAltaCirgRealizadaRNExceptionCode.MPM_02623);

		//RNU7 e RNU8
		this.validarAlteracaoDthrCirurgia(altaCirgRealizada, altaCirgRealizadaVelha);

		this.verificarAlteracoesUpdateAltaCirgRealizada(altaCirgRealizada, altaCirgRealizadaVelha);

		this.verificarIndCarga(altaCirgRealizada, altaCirgRealizadaVelha);

		//  RN1 - Verifica se ALTA_SUMARIOS está ativo
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaCirgRealizada.getAltaSumario());

		this.validarAlteracaoDthrCirurgia(altaCirgRealizada,altaCirgRealizadaVelha);

		this.validarCamposAlterados(altaCirgRealizada, altaCirgRealizadaVelha);

	}
	//DominioSimNao.N.equals(altaCirgRealizadaNova.getIndCarga())

	/**
	 * @ORADB Procedure mpmk_acr_rn.rn_acrp_ver_update
	 * 
	 * Verifica Alteração ind Carga.
	 * 	 
	 * @param {MpmAltaCirgRealizada} altaCirgRealizadaNova, {MpmAltaCirgRealizada} altaCirgRealizadaVelha
	 * @throws ApplicationBusinessException
	 */
	protected void verificarAlteracoesUpdateAltaCirgRealizada(MpmAltaCirgRealizada altaCirgRealizadaNova,MpmAltaCirgRealizada altaCirgRealizadaVelha) throws ApplicationBusinessException{
		if(!altaCirgRealizadaNova.getIndCarga()){

			if(CoreUtil.modificados(altaCirgRealizadaNova.getDescCirurgia(), altaCirgRealizadaVelha.getDescCirurgia()) && 
					!CoreUtil.modificados(altaCirgRealizadaVelha.getProcedimentoCirurgico(), altaCirgRealizadaVelha.getProcedimentoCirurgico())	
			){
				throw new ApplicationBusinessException (
						ManterAltaCirgRealizadaRNExceptionCode.MPM_02619);
			}
			if(CoreUtil.modificados(altaCirgRealizadaNova.getProcedimentoCirurgico(), altaCirgRealizadaVelha.getProcedimentoCirurgico())
					&& !CoreUtil.modificados(altaCirgRealizadaNova.getDescCirurgia(), altaCirgRealizadaVelha.getDescCirurgia())
			){
				throw new ApplicationBusinessException (
						ManterAltaCirgRealizadaRNExceptionCode.MPM_02649);
			}
			if(altaCirgRealizadaNova.getProcedimentoEspCirurgia() != null){
				throw new ApplicationBusinessException (
						ManterAltaCirgRealizadaRNExceptionCode.MPM_02650);
			}
		}
		if(altaCirgRealizadaNova.getIndCarga() && altaCirgRealizadaNova.getProcedimentoEspCirurgia() == null) {
			throw new ApplicationBusinessException (
					ManterAltaCirgRealizadaRNExceptionCode.MPM_02620);
		}
		
	}

	/**
	 * @ORADB Procedure mpmk_acr_rn.rn_acrp_ver_ind_carg
	 * 
	 * Verifica Ind Carga.
	 * 	 
	 * @param {MpmAltaCirgRealizada} altaCirgRealizadaNova, {MpmAltaCirgRealizada} altaCirgRealizadaVelha
	 * @throws ApplicationBusinessException
	 */
	protected void verificarIndCarga(MpmAltaCirgRealizada altaCirgRealizadaNova,MpmAltaCirgRealizada altaCirgRealizadaVelha)throws ApplicationBusinessException{

		if(CoreUtil.modificados(altaCirgRealizadaNova.getIndCarga(), altaCirgRealizadaVelha.getIndCarga())){
			throw new ApplicationBusinessException (
					ManterAltaCirgRealizadaRNExceptionCode.MPM_02622);
		}
	}

	protected void validarAlteracaoDthrCirurgia(MpmAltaCirgRealizada altaCirgRealizadaNova,MpmAltaCirgRealizada altaCirgRealizadaVelha) throws ApplicationBusinessException{

		if(CoreUtil.modificados(altaCirgRealizadaNova.getDthrCirurgia(), altaCirgRealizadaVelha.getDthrCirurgia())){

			this.verificarDataCirurgia(altaCirgRealizadaNova);
		}

	}


	/**
	 * @ORADB Procedure mpmk_acr_rn.rn_acrp_ver_dthr_cir
	 * 
	 * Verifica data de cirurgia.
	 * 	 
	 * @param {MpmAltaCirgRealizada} altaCirgRealizada
	 * @throws ApplicationBusinessException
	 */
	protected void verificarDataCirurgia(MpmAltaCirgRealizada altaCirgRealizada) throws ApplicationBusinessException{

		if (altaCirgRealizada.getAltaSumario() != null) {

			Date dataInicio = this.getAltaSumarioRN().obterDataInternacao2(altaCirgRealizada.getAltaSumario().getId().getApaAtdSeq());

			if (!DateUtil.entreTruncado(altaCirgRealizada.getDthrCirurgia(), dataInicio, new Date())) {

				throw new ApplicationBusinessException(
						ManterAltaCirgRealizadaRNExceptionCode.MPM_02872);

			}

		}

	}

	/**
	 * @ORADB MPMT_ACR_BRU Procedure mpmk_acr_rn.rn_acrp_ver_modific
	 * 
	 * Não  permitir alterar nenhum campo  se o ind_carga for igual a 'S'
	 * 	 
	 * @param {MpmAltaCirgRealizada} altaCirgRealizadaNova, {MpmAltaCirgRealizada} altaCirgRealizadaVelha
	 * @throws ApplicationBusinessException
	 */
	protected void validarCamposAlterados(MpmAltaCirgRealizada altaCirgRealizadaNova,MpmAltaCirgRealizada altaCirgRealizadaVelha)throws ApplicationBusinessException {

		if(altaCirgRealizadaNova.getIndCarga()){

			if( 
					CoreUtil.modificados(altaCirgRealizadaNova.getId(), altaCirgRealizadaVelha.getId()) ||
					CoreUtil.modificados(altaCirgRealizadaNova.getDescCirurgia(), altaCirgRealizadaVelha.getDescCirurgia()) ||
					CoreUtil.modificados(altaCirgRealizadaNova.getDthrCirurgia(), altaCirgRealizadaVelha.getDthrCirurgia()) ||
					CoreUtil.modificados(altaCirgRealizadaNova.getIndCarga(), altaCirgRealizadaVelha.getIndCarga()) ||
					CoreUtil.modificados(altaCirgRealizadaNova.getProcedimentoCirurgico(), altaCirgRealizadaVelha.getProcedimentoCirurgico()) ||
					CoreUtil.modificados(altaCirgRealizadaNova.getProcedimentoEspCirurgia(), altaCirgRealizadaVelha.getProcedimentoEspCirurgia())
			){
				throw new ApplicationBusinessException (
						ManterAltaCirgRealizadaRNExceptionCode.MPM_02624);
			}
		}
	}

	/**
	 * Atualiza objeto MpmAltaCirgRealizada.
	 * 
	 * @param {MpmAltaCirgRealizada} altaCirgRealizada
	 * @throws ApplicationBusinessException
	 */
	public void atualizaraltaCirgRealizada(
			MpmAltaCirgRealizada altaCirgRealizada)
	throws ApplicationBusinessException {
		
		this.preAtualizarAltaCirgRealizada(altaCirgRealizada);
		this.getMpmAltaCirgRealizadaDAO().merge(altaCirgRealizada);
		this.getMpmAltaCirgRealizadaDAO().flush();
		
	}
	
	/**
	 * ORADB MPMT_ACR_BRD
	 * @param altaCirgRealizada
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaCirgRealizada(MpmAltaCirgRealizada altaCirgRealizada) throws ApplicationBusinessException {

		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaCirgRealizada.getAltaSumario());
		this.getMpmAltaCirgRealizadaDAO().remover(altaCirgRealizada);	
		this.getMpmAltaCirgRealizadaDAO().flush();

	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaCirgRealizadaDAO getMpmAltaCirgRealizadaDAO() {
		return mpmAltaCirgRealizadaDAO;
	}
}
