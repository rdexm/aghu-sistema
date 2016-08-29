package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultaProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroEspecialidadeDisponivelVO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class AmbulatorioConsultaON extends BaseBusiness {


	@EJB
	private ProcedimentoConsultaRN procedimentoConsultaRN;
	
	@EJB
	private AmbulatorioConsultaRN ambulatorioConsultaRN;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private static final Log LOG = LogFactory.getLog(AmbulatorioConsultaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AacConsultaProcedHospitalarDAO aacConsultaProcedHospitalarDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3276478549450000908L;
	
	/**
	 * Insere procedimentos, caso atributo faturaSus seja true no retorno da consulta.
	 * Quando faturaSus == false, atualiza no faturamento os procedimentos para a situacao Cancelado. 
	 * 
	 * @param consulta
	 */
	public void processarConsultaProcedimentoPorRetorno(Integer consultaNumero, String nomeMicrocomputador, final Date dataFimVinculoServidor, AacRetornos retorno,
			Boolean aack_prh_rn_v_apac_diaria, 
			Boolean aack_aaa_rn_v_protese_auditiva, 
			Boolean fatk_cap_rn_v_cap_encerramento) throws BaseException {
		
		AacConsultas consulta = ambulatorioFacade.obterConsulta(consultaNumero);
		
		if (retorno != null){
			consulta.setRetorno(retorno);
		}
		
		List<AacConsultaProcedHospitalar> listaConsProcedHosp = 
				getAacConsultaProcedHospitalarDAO().buscarConsultaProcedHospPorNumeroConsulta(consulta.getNumero());
		if (consulta.getRetorno() != null && consulta.getRetorno().getFaturaSus() && listaConsProcedHosp.isEmpty()) {
			ambulatorioConsultaRN.atualizarConsultaProcedimento(consulta, consulta.getSituacaoConsulta(), nomeMicrocomputador, dataFimVinculoServidor, aack_prh_rn_v_apac_diaria, aack_aaa_rn_v_protese_auditiva, fatk_cap_rn_v_cap_encerramento);
			ambulatorioConsultaRN.atualizarConsultaProcGrade(consulta, nomeMicrocomputador, dataFimVinculoServidor, aack_prh_rn_v_apac_diaria, 	aack_aaa_rn_v_protese_auditiva,	fatk_cap_rn_v_cap_encerramento);
		}
		else if (!listaConsProcedHosp.isEmpty()) {
			for (AacConsultaProcedHospitalar consultaProcedHospitalar : listaConsProcedHosp) {
				getProcedimentoConsultaRN().executarEnforceConsultaProcedimentoHospitalar(
						consultaProcedHospitalar, null, DominioOperacaoBanco.DEL, nomeMicrocomputador, dataFimVinculoServidor);
			}
		}
		
	}
	
	public void validarCamposPreenchidos(FiltroEspecialidadeDisponivelVO filtro) throws ApplicationBusinessException{
		if(filtro.getDtFinal().before(filtro.getDtInicial())){
			throw new ApplicationBusinessException("MENSAGEM_DATA_FINAL_MAIOR_DATA_INICIAL_VALIDADE_VENCIA", Severity.ERROR);
		}
		if(!verificarCamposPreenchidos(filtro)){
			throw new ApplicationBusinessException("8665_CAMPOS_OBRIGATORIOS", Severity.ERROR);
		}
	}
	
	private boolean verificarCamposPreenchidos(FiltroEspecialidadeDisponivelVO filtro) throws ApplicationBusinessException{
		if((filtro.getEspecialidade() != null) || (filtro.getAutorizacao() != null) 
				|| (filtro.getCondicaoAtendimento() != null) || (filtro.getPagador() != null )
				|| (filtro.getSituacao()!= null)){
			return true;
		}
		
		return false;
	}	
	protected AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
		return ambulatorioConsultaRN;
	}
	
	protected AacConsultaProcedHospitalarDAO getAacConsultaProcedHospitalarDAO() {
		return aacConsultaProcedHospitalarDAO;
	}
	
	protected ProcedimentoConsultaRN getProcedimentoConsultaRN() {
		return procedimentoConsultaRN;
	}

}
