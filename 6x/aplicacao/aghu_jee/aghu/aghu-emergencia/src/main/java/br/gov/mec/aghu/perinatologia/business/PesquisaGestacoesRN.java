package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.paciente.vo.PacienteProntuarioConsulta;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.service.ServiceException;

/**
 * RN da estória #36508 - Pesquisar Gestações
 * 
 * @author luismoura
 * 
 */
@Stateless
public class PesquisaGestacoesRN extends BaseBusiness {
	
	private static final long serialVersionUID = 6743775141259299211L;

	private static final String P_UNIDADE_CO = "P_UNIDADE_CO";
	private static final String P_UNIDADE_EMG_CO = "P_UNIDADE_EMG_CO";

	@Inject
	private IPacienteService pacienteService;

	@Inject
	private IAmbulatorioService ambulatorioService;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Override
	protected Log getLogger() {
		return null;
	}

	private enum PesquisarGestacoesRNExceptionCode implements BusinessExceptionCode {
		ERRO_PRONTUARIO_NUMEROCONSULTA_NAO_INFORMADO, //
		ERRO_PRONTUARIO_E_NUMEROCONSULTA_INFORMADO, //
		ERROR_SEM_COMUNICACAO_OBSTETRICO, //
		ERRO_PACIENTE_SEM_CONSULTA_CO, //
		ERRO_CONSULTA_NAO_CADASTRADA, //
		ERRO_PARAMETRO, //
		ERRO_PRONTUARIO_INVALIDO, //
		;
	}

	/**
	 * RN01 e RN03 de #36508 - Pesquisar Gestações
	 * 
	 * @param nroProntuario
	 * @param nroConsulta
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public PacienteProntuarioConsulta obterDadosGestante(Integer nroProntuario, Integer nroConsulta) throws ApplicationBusinessException {
		PacienteProntuarioConsulta gestante = null;
		try {
			if (nroProntuario != null) {
				gestante = pacienteService.obterDadosGestantePorProntuario(nroProntuario);
				if (gestante == null) {
					throw new ApplicationBusinessException(PesquisarGestacoesRNExceptionCode.ERRO_PRONTUARIO_INVALIDO);
				}
			} else if (nroConsulta != null) {
				gestante = pacienteService.obterDadosGestantePorConsulta(nroConsulta);
				if (gestante == null) {
					throw new ApplicationBusinessException(PesquisarGestacoesRNExceptionCode.ERRO_CONSULTA_NAO_CADASTRADA);
				}
			} else {
				throw new ApplicationBusinessException(PesquisarGestacoesRNExceptionCode.ERRO_PRONTUARIO_NUMEROCONSULTA_NAO_INFORMADO);
			}
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(e.getMessage(), Severity.ERROR);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PesquisarGestacoesRNExceptionCode.ERROR_SEM_COMUNICACAO_OBSTETRICO);
		}

		return gestante;
	}

	/**
	 * RN02 de #36508 - Pesquisar Gestações
	 * 
	 * Buscar a idade do paciente através da Data de Nascimento do Paciente utilizando o campo DT_NASCIMENTO e calcular a idade através do
	 * método
	 * 
	 * @Transient public String getIdadeFormat() do Pojo AipPacientes.java;
	 * 
	 * @param dtNasc
	 * @return
	 */
	public String getIdadeFormatada(Date dtNasc) {
		String tempo = "anos";
		String idadeFormat = null;
		if (dtNasc != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(dtNasc);
			Calendar dataCalendario = new GregorianCalendar();
			dataCalendario.setTime(new Date());

			// Obtém a idade baseado no ano
			Integer idadeNum = dataCalendario.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR);

			if (dataCalendario.get(Calendar.MONTH) < dataNascimento.get(Calendar.MONTH)) {
				idadeNum--;
			} else if (dataCalendario.get(Calendar.MONTH) == dataNascimento.get(Calendar.MONTH)
					&& dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
				idadeNum--;
			}

			if (idadeNum == 1) {
				tempo = "ano";
			}
			if (idadeNum < 1) {
				if (dataCalendario.get(Calendar.MONTH) < dataNascimento.get(Calendar.MONTH)) {
					idadeNum = dataCalendario.get(Calendar.MONTH) + (12 - dataNascimento.get(Calendar.MONTH));
				} else if ((dataCalendario.get(Calendar.YEAR) != dataNascimento.get(Calendar.YEAR))
						&& (dataCalendario.get(Calendar.MONTH) == dataNascimento.get(Calendar.MONTH))
						&& (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH))) {
					idadeNum = dataCalendario.get(Calendar.MONTH) + (11 - dataNascimento.get(Calendar.MONTH));
				} else {
					idadeNum = dataCalendario.get(Calendar.MONTH) - dataNascimento.get(Calendar.MONTH);
					if (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
						idadeNum--;
					}
				}

				tempo = "meses";
				if (idadeNum == 1) {
					tempo = "mês";
				}

				if (idadeNum < 1) {
					if (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
						Integer lastDayMonth = dataNascimento.getActualMaximum(Calendar.DAY_OF_MONTH);
						idadeNum = (lastDayMonth - dataNascimento.get(Calendar.DAY_OF_MONTH)) + dataCalendario.get(Calendar.DAY_OF_MONTH);
					} else {
						idadeNum = dataCalendario.get(Calendar.DAY_OF_MONTH) - dataNascimento.get(Calendar.DAY_OF_MONTH);
					}

					// Soma 1 para dias(de acordo com ORADB AIPC_IDADE_ANO_MES)
					idadeNum++;

					tempo = "dias";
					if (idadeNum == 1) {
						tempo = "dia";
					}
				}
			}
			idadeFormat = idadeNum + " " + tempo;
		}

		return idadeFormat;
	}

	/**
	 * Busca ultima consulta da gestação
	 * 
	 * RN04 de #36508 - Pesquisar Gestações
	 * 
	 * @param gsoSeqp
	 * @param pacCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Integer buscarUltimaConsulta(Short gsoSeqp, Integer pacCodigo) throws ApplicationBusinessException {
		Integer consulta = null;
		try {
			consulta = pacienteService.obterDadosGestantePorGestacaoPaciente(gsoSeqp, pacCodigo);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(e.getMessage(), Severity.ERROR);
		}
		return consulta;
	}

	/**
	 * RN05 de #36508 - Pesquisar Gestações
	 * 
	 * @param pacCodigo
	 * @throws ApplicationBusinessException
	 */
	public Integer buscarUltimaConsultaCO(Integer pacCodigo) throws ApplicationBusinessException {
		Short unidadeCO = this.getParametroShort(P_UNIDADE_CO);
		Short unidadeEmergenciaCO = this.getParametroShort(P_UNIDADE_EMG_CO);

		List<Integer> consultas = null;
		try {
			consultas = ambulatorioService.pesquisarConsultaPorPacienteUnidadeFuncional(pacCodigo, unidadeCO, unidadeEmergenciaCO);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(e.getMessage(), Severity.ERROR);
		}

		if (consultas != null && !consultas.isEmpty()) {
			return consultas.get(0);
		}

		return null;
	}

	/**
	 * Busca o valor numérico do parâmetro "parametro", através de chamada do serviço #34780
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Short getParametroShort(String parametro) throws ApplicationBusinessException {
		Object parametroGrupo = null;
		try {
			parametroGrupo = parametroFacade.obterAghParametroPorNome(parametro, "vlrNumerico");
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PesquisarGestacoesRNExceptionCode.ERRO_PARAMETRO);
		}
		Short retorno = null;
		if (parametroGrupo != null) {
			retorno = ((BigDecimal) parametroGrupo).shortValue();
		} else {
			throw new ApplicationBusinessException(PesquisarGestacoesRNExceptionCode.ERRO_PARAMETRO);
		}
		return retorno;
	}
}
