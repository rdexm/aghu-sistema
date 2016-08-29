package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;

/**
 * 
 * Migração da package genérica de ambulatórios triagem
 * 
 *  ORADB: MAMK_TRG_GENERICA
 * 
 * @author dansantos
 */
@Stateless
public class AmbulatorioTriagemRN extends BaseBusiness {

@Inject
private AipPacientesDAO aipPacientesDAO;

@EJB
private ICadastroPacienteFacade cadastroPacienteFacade;

@EJB
private IPacienteFacade pacienteFacade;


private static final Log LOG = LogFactory.getLog(AmbulatorioTriagemRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	/**
	 * 
	 */
	private static final long serialVersionUID = 222384176547255030L;

	public enum AmbulatorioTriagemRNRNExceptionCode implements BusinessExceptionCode {

        APPLICATION_ERROR_20000_ERRO; 
	}

	
	/**
	 * #42360
	 * @throws ApplicationBusinessException 
	 * @ORADB: MAMC_INS_PRNT_VIRTUA
	 */
	public Integer inserirPontuarioVirtual(Integer pacCodigo, String nomeMicrocomputador) throws ApplicationBusinessException{
	
		Integer prontuarioVirtual = null;

		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo);
		LOG.info("<<< NOME DO PACIENTE AMBULATORIO EMERGENCIA" + paciente.getNome() + " >>>");

		if (paciente.getProntuario() != null) {
			LOG.info("<<< PRONTUARIO DO PACIENTE AMBULATORIO EMERGENCIA" + paciente.getProntuario()+ " >>>");
			return paciente.getProntuario();

		} else {

			LOG.info("<<< PRONTUARIO DO PACIENTE NULO, GERANDO PRONTUARIO VIRTUAL AMBULATORIO EMERGENCIA >>>");
			LOG.info("<<< OBTENDO PRONTUARIO VIRTUAL AMBULATORIO >>>");
			
			prontuarioVirtual = this.cadastroPacienteFacade.gerarNumeroProntuarioVirtualPacienteEmergencia(paciente, nomeMicrocomputador);

			LOG.info("<<<< PRONTUARIO DO PACIENTE AMBULATORIO EMERGENCIA VIRTUAL OBTIDO " + prontuarioVirtual + " >>>>");
			
			LOG.info("<< ATUALIZADO O PROTUARIO DO PACIENTE AMBULATORIO EMERGENCIA " + prontuarioVirtual + " >>>>");

			if(prontuarioVirtual == null){
				throw new ApplicationBusinessException(AmbulatorioTriagemRNRNExceptionCode.APPLICATION_ERROR_20000_ERRO);
			}

			return prontuarioVirtual;
		}

	}

	public AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}

	public void setAipPacientesDAO(AipPacientesDAO aipPacientesDAO) {
		this.aipPacientesDAO = aipPacientesDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}
	
}