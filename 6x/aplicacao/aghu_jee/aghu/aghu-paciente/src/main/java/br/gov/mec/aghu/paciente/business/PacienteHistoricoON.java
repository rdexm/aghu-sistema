package br.gov.mec.aghu.paciente.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.paciente.dao.AipPacientesHistDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de Pacientes no Histórico.
 */
@Stateless
public class PacienteHistoricoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PacienteHistoricoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipPacientesHistDAO aipPacientesHistDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5801428281664201821L;

	private enum PacienteHistoricoONExceptionCode implements BusinessExceptionCode {
		NOME_PACIENTE_OBRIGATORIO_PESQUISA_PACIENTE_HISTORICO;
	}

	public List<AipPacientesHist> pesquisaPacientesHistorico(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, String nome) throws ApplicationBusinessException {
		if (StringUtils.isBlank(nome)) {
			throw new ApplicationBusinessException(PacienteHistoricoONExceptionCode.NOME_PACIENTE_OBRIGATORIO_PESQUISA_PACIENTE_HISTORICO);
		}

		return this.getAipPacientesHistDAO().pesquisaPacientesHistorico(firstResult, maxResults, orderProperty, asc, nome);
	}

	public Long pesquisaPacientesHistoricoCount(String nome) throws ApplicationBusinessException {
		if (StringUtils.isBlank(nome)) {
			throw new ApplicationBusinessException(PacienteHistoricoONExceptionCode.NOME_PACIENTE_OBRIGATORIO_PESQUISA_PACIENTE_HISTORICO);
		}

		return this.getAipPacientesHistDAO().pesquisaPacientesHistoricoCount(nome);
	}

	protected AipPacientesHistDAO getAipPacientesHistDAO() {
		return aipPacientesHistDAO;
	}
	
}
