package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaEstadoPaciente;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEstadoPacienteDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterAltaEstadoPacienteRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaEstadoPacienteRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaEstadoPacienteDAO mpmAltaEstadoPacienteDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2195745023194046868L;

	public enum ManterAltaEstadoPacienteRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_ALTA_ESTADO_PACIENTE, ERRO_ATUALIZAR_ALTA_ESTADO_PACIENTE, ERRO_REMOVER_ALTA_ESTADO_PACIENTE;

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
	 * Insere objeto MpmAltaEstadoPaciente.
	 * 
	 * @param {MpmAltaEstadoPaciente} altaEstadoPaciente
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaEstadoPaciente(MpmAltaEstadoPaciente altaEstadoPaciente) throws ApplicationBusinessException {
		if (altaEstadoPaciente.getAltaSumario() == null) {
			throw new IllegalArgumentException("MpmAltaEstadoPaciente nao esta associado corretamente a MpmAltaSumario.");
		}

		try {

			this.preInserirAltaEstadoPaciente(altaEstadoPaciente);
			this.getAltaEstadoPacienteDAO().persistir(altaEstadoPaciente);
			this.getAltaEstadoPacienteDAO().flush();

		} catch (Exception e) {

			ManterAltaEstadoPacienteRNExceptionCode.ERRO_INSERIR_ALTA_ESTADO_PACIENTE
					.throwException(e);

		}

	}
	
	/**
	 * Atualiza um objeto <code>MpmAltaEstadoPaciente</code>
	 * 
	 * @param altaEstadoPaciente
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAltaEstadoPaciente(MpmAltaEstadoPaciente altaEstadoPaciente) throws ApplicationBusinessException {
		if (altaEstadoPaciente.getAltaSumario() == null) {
			throw new IllegalArgumentException("MpmAltaEstadoPaciente nao esta associado corretamente a MpmAltaSumario.");
		}

		try {
			this.preAtualizarAltaEstadoPaciente(altaEstadoPaciente);
			this.getAltaEstadoPacienteDAO().atualizar(altaEstadoPaciente);
			this.getAltaEstadoPacienteDAO().flush();
		} catch (Exception e) {

			ManterAltaEstadoPacienteRNExceptionCode.ERRO_ATUALIZAR_ALTA_ESTADO_PACIENTE
					.throwException(e);

		}
	}
	
	/**
	 * Atualiza um objeto <code>MpmAltaEstadoPaciente</code>
	 * 
	 * @param altaEstadoPaciente
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaEstadoPaciente(MpmAltaEstadoPaciente altaEstadoPaciente) throws ApplicationBusinessException {
		
		try {
			
			this.preRemoverAltaEstadoPaciente(altaEstadoPaciente);
			this.getAltaEstadoPacienteDAO().remover(altaEstadoPaciente);
			this.getAltaEstadoPacienteDAO().flush();
			
		} catch (Exception e) {
			logError(e.getMessage());
			ManterAltaEstadoPacienteRNExceptionCode.ERRO_REMOVER_ALTA_ESTADO_PACIENTE
					.throwException(e);

		}
	}

	/**
	 * ORADB Trigger MPMT_AEP_BRI
	 * 
	 * @param {MpmAltaEstadoPaciente} altaEstadoPaciente
	 * @throws ApplicationBusinessException
	 */
	protected void preInserirAltaEstadoPaciente(MpmAltaEstadoPaciente altaEstadoPaciente) throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaEstadoPaciente.getAltaSumario());

	}
	
	/**
	 * ORADB Trigger MPMT_AEP_BRU
	 * 
	 * @param {MpmAltaEstadoPaciente} altaEstadoPaciente
	 * @throws ApplicationBusinessException
	 */
	protected void preAtualizarAltaEstadoPaciente(MpmAltaEstadoPaciente altaEstadoPaciente) throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaEstadoPaciente.getAltaSumario());
		
//		Integer newVersion = (altaEstadoPaciente.getVersion() == null) ? Integer.valueOf(0) : altaEstadoPaciente.getVersion();
//		newVersion = newVersion + 1;
//		altaEstadoPaciente.setVersion(newVersion);

	}
	
	/**
	 * ORADB Trigger MPMT_AEP_BRU
	 * 
	 * @param {MpmAltaEstadoPaciente} altaEstadoPaciente
	 * @throws ApplicationBusinessException
	 */
	protected void preRemoverAltaEstadoPaciente(MpmAltaEstadoPaciente altaEstadoPaciente) throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaEstadoPaciente.getAltaSumario());

	}
	
	/**
	 * Método que verifica a validação
	 * do estado clínico do paciente. Deve
	 * pelo menos ter um registro associado 
	 * ao sumário do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 */
	public Boolean validarAltaEstadoPaciente(MpmAltaSumarioId altaSumarioId) {
		List<Long> result = this.getAltaEstadoPacienteDAO().listAltaEstadoPaciente(altaSumarioId);
		
		Long rowCount = 0L;
        if (!result.isEmpty()) {
            rowCount = (Long) result.get(0);
        }
        
		return rowCount > 0;
	}
	

	private ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaEstadoPacienteDAO getAltaEstadoPacienteDAO() {
		return mpmAltaEstadoPacienteDAO;
	}

}
