package br.gov.mec.aghu.internacao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * O propósito desta classe é prover os métodos de negócio para o cadastro de
 * internações.
 */
@Stateless
public class AltaPacienteObservacaoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AltaPacienteObservacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7236193544813926707L;

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle de internacao.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 * 
	 */
	private enum AltaPacienteObservacaoONExceptionCode implements
			BusinessExceptionCode {
		AIN_00831_PRONTUARIO_NAO_ENCONTRADO, AIN_00182_DATA_ENTRE_ANTERIOR_ATUAL, MAIOR_24_HORAS;
	}

	/**
	 * Obtem o atendimento de urgencia mais recente do paciente solicitado.
	 * 
	 * @param pacCodigo
	 * @return AinAtendimentosUrgencia
	 * @throws ApplicationBusinessException
	 */
	public void validaDataAlta(Date dtAlta, Date dtAtendimento)
			throws ApplicationBusinessException {

		if (dtAlta != null) {
			if (dtAlta.after(new Date())) {
				throw new ApplicationBusinessException(
						AltaPacienteObservacaoONExceptionCode.AIN_00182_DATA_ENTRE_ANTERIOR_ATUAL);
			}

			// Diferença em milisegundos.
			long diff = dtAlta.getTime() - dtAtendimento.getTime();

			// Taxa de conversão.
			int tempoHoras = 1000 * 60 * 60;

			// Diferença em horas
			long horasDiferenca = diff / tempoHoras;

			if (horasDiferenca > 24) {
				// Diferença em minutos
				int tempoMinutos = 1000 * 60;
				long minDiferenca = (diff / tempoMinutos)
						- (horasDiferenca * 60);

				throw new ApplicationBusinessException(
						AltaPacienteObservacaoONExceptionCode.MAIOR_24_HORAS,
						horasDiferenca, minDiferenca);
			}
		}
	}

	/**
	 * Obtem o atendimento de urgencia mais recente do paciente solicitado.
	 * 
	 * @param pacCodigo
	 * @return AinAtendimentosUrgencia
	 * @throws ApplicationBusinessException
	 */
	public AinAtendimentosUrgencia obterAtendUrgencia(AipPacientes paciente)
			throws ApplicationBusinessException {

		AinAtendimentosUrgencia atendimentoUrgencia = getAinAtendimentosUrgenciaDAO().obterUltimoAtendimentoUrgenciaDoPaciente(
				paciente);
		if (atendimentoUrgencia != null) {
			return atendimentoUrgencia;
		}

		throw new ApplicationBusinessException(AltaPacienteObservacaoONExceptionCode.AIN_00831_PRONTUARIO_NAO_ENCONTRADO);

	}

	/**
	 * Método para buscar todos atendimentos de urgencia de um determinado
	 * paciente.
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public List<AinAtendimentosUrgencia> pesquisarAtendimentoUrgencia(Integer codigoPaciente) {
		return getAinAtendimentosUrgenciaDAO().pesquisarAtendimentoUrgencia(codigoPaciente);
	}

	protected AinAtendimentosUrgenciaDAO getAinAtendimentosUrgenciaDAO(){
		return ainAtendimentosUrgenciaDAO;
	}
}
