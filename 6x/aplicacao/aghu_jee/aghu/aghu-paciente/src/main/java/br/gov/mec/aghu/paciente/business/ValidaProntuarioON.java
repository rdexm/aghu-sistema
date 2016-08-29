package br.gov.mec.aghu.paciente.business;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockOptions;

import br.gov.mec.aghu.model.AipCodeControls;
import br.gov.mec.aghu.paciente.business.validacaoprontuario.InterfaceValidaProntuario;
import br.gov.mec.aghu.paciente.business.validacaoprontuario.ValidaProntuarioException;
import br.gov.mec.aghu.paciente.business.validacaoprontuario.ValidaProntuarioFactory;
import br.gov.mec.aghu.paciente.dao.AipCodeControlsDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ValidaProntuarioON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ValidaProntuarioON.class);

	private static final long serialVersionUID = -4451361320125008617L;

	private static final String SEQUENCE_PRONTUARIO_REAL = "AIP_PAC_SQ2";

	private static final String SEQUENCE_PRONTUARIO_VIRTUAL = "AIP_PAC_SQ3";

	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private AipCodeControlsDAO aipCodeControlsDAO;
	
	public void validaProntuario() {
		LOG.info("Rotina ValidaProntuarioScheduler.validaProntuario iniciada em: " + Calendar.getInstance().getTime());
		try {
			InterfaceValidaProntuario validadorProntuario = new ValidaProntuarioFactory().getValidaProntuario(true);

			processaValidacaoProntuario(SEQUENCE_PRONTUARIO_REAL, validadorProntuario);
			processaValidacaoProntuario(SEQUENCE_PRONTUARIO_VIRTUAL, validadorProntuario);

			LOG.info("Rotina ValidaProntuarioScheduler.validaProntuario finalizada em: "
					+ Calendar.getInstance().getTime());

		} catch (ValidaProntuarioException e) {
			LOG.error("Erro ao obter o objeto InterfaceValidaProntuario através da classe ValidaProntuarioFactory",	e);
		}
	}

	
	private void processaValidacaoProntuario(String sequence,
			InterfaceValidaProntuario validadorProntuario) {

		AipCodeControls aipCodeControlsComLock = aipCodeControlsDAO
				.obterCodeControl(sequence, LockOptions.UPGRADE);
		Integer prontuarioBase = aipCodeControlsComLock.getNextValue();

		if (prontuarioBase == null) {
			LOG.info("Erro ao ler o prontuário do banco de dados, utilizando a sequence " + sequence);
			return;
		}

		Integer prontuarioComModulo = Integer.valueOf(prontuarioBase.toString()
				+ validadorProntuario.executaModulo(prontuarioBase));

		LOG.debug("Leu a sequence " + sequence + ", valor: " + prontuarioBase
				+ "(prontuário com módulo calculado: " + prontuarioComModulo
				+ ")");

		Boolean existePacienteComProntuario = aipPacientesDAO
				.existePacientePorProntuario(prontuarioComModulo);

		if (existePacienteComProntuario) {
			do {
				LOG.warn("Já existe um paciente com o prontuário "
						+ prontuarioComModulo + ". A sequence " + sequence
						+ " é inválida.");

				prontuarioBase++;

				prontuarioComModulo = Integer.valueOf(prontuarioBase.toString()
						+ validadorProntuario.executaModulo(prontuarioBase));

				LOG.debug("Leu a sequence " + sequence + ", valor: "
						+ prontuarioBase + "(prontuário com módulo calculado: "
						+ prontuarioComModulo + ")");

				existePacienteComProntuario = aipPacientesDAO
						.existePacientePorProntuario(prontuarioComModulo);
			} while (existePacienteComProntuario);

			LOG.debug("Atualizando a sequence " + sequence
					+ " para ter o valor " + prontuarioBase);

			aipCodeControlsComLock.setNextValue(prontuarioBase);
			aipCodeControlsDAO.atualizar(aipCodeControlsComLock);
			aipCodeControlsDAO.flush();
		} else {
			LOG.debug("A sequence " + sequence + " está com o valor correto.");
		}
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}
