package br.gov.mec.aghu.paciente.business;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.paciente.business.exception.PacienteExceptionCode;
import br.gov.mec.aghu.paciente.dao.AghSamisMovimentosDAO;
import br.gov.mec.aghu.paciente.dao.AipConveniosSaudePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;

/**
 * Classe responsável por prover os métodos de negócio de pesquisa para a entidade
 * de pacientes.
 */
@Stateless
public class PesquisarPacienteON extends BaseBusiness {

	private static final long serialVersionUID = -630983488276008829L;
	
	private static final Log LOG = LogFactory.getLog(PesquisarPacienteON.class);

	@Inject
	private AipConveniosSaudePacienteDAO aipConveniosSaudePacienteDAO;

	@Inject
	private AipPacientesDAO aipPacientesDAO;

	@EJB
	private FonemasPacienteRN fonemasPacienteRN;

	@Inject
	private AghSamisMovimentosDAO aghSamisMovimentosDAO;

	@EJB
	private PacienteON pacienteON;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	private enum PesquisarPacienteONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_LEITO_INVALIDO;
	}
	
	/**
	 * Retorna a 0 se não encontrou o paciente e 1 se encontrou.
	 * 
	 * @param nroProntuario
	 * @param nroCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarPacienteCount(Integer nroProntuario, Integer nroCodigo , Long cpf, BigInteger nroCartaoSaude)
	throws ApplicationBusinessException {
		if (nroProntuario == null && nroCodigo == null && nroCartaoSaude == null && cpf == null) {
			throw new ApplicationBusinessException(PacienteExceptionCode.AIP_PRONTUARIO_E_CODIGO_NULOS);
		} else {
			return this.getAipPacientesDAO().pesquisarPacienteCount(nroProntuario, nroCodigo, cpf,nroCartaoSaude);
		}
	}
	
	public AipPacientes pesquisarPacientePorProntuario(Integer nroProntuario) {
		return this.getAipPacientesDAO().pesquisarPacientePorProntuario(nroProntuario);
	}
	
	/**
	 * 
	 * @param nroProntuario
	 * @return
	 */
	public AipPacientes pesquisarPacientePorProntuarioSemDigito(Integer nroProntuario) {
		return this.getAipPacientesDAO().pesquisarPacientePorProntuarioSemDigito(nroProntuario);
	}
	
	public List<AipPacientes> pesquisarPacientesPorListaProntuario(
			Collection<Integer> nroProntuarios) throws ApplicationBusinessException {
		List<AipPacientes> pacientes = this.getAipPacientesDAO().pesquisarPacientesPorListaProntuario(nroProntuarios);
		for(AipPacientes pac: pacientes){
			getAipPacientesDAO().refresh(pac);
		}

		return pacientes;
	}
	
	public List<AipPacientes> pesquisarPacientesPorProntuario(String strPesquisa) {
		return this.getAipPacientesDAO().pesquisarPacientesPorProntuario(strPesquisa);
	}
	
	public List<AipPacientes> pesquisarPacientesPorProntuarioOuCodigo(String strPesquisa) {
		return this.getAipPacientesDAO().pesquisarPacientesPorProntuarioOuCodigo(strPesquisa);
	}
	
	public List<AipPacientes> pesquisarPacientesComProntuarioPorProntuarioOuCodigo(
			String strPesquisa) {
		return this.getAipPacientesDAO().pesquisarPacientesComProntuarioPorProntuarioOuCodigo(strPesquisa);
	}
	
	public List<AipPacientes> pesquisaPacientes(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer numeroProntuario, Date periodoAltaInicio, Date periodoAltaFim, Date periodoConsultaInicio,
			Date periodoConsultaFim, Date periodoCirurgiaInicio, Date periodoCirurgiaFim, String nomePaciente,
			AghEquipes equipeMedica, AghEspecialidades especialidade, FccCentroCustos servico, AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito) throws ApplicationBusinessException {
		this.getPacienteON().validaDadosPesquisaPacientes(numeroProntuario, periodoAltaInicio, periodoAltaFim, periodoConsultaInicio,
				periodoConsultaFim, periodoCirurgiaInicio, periodoCirurgiaFim, nomePaciente, equipeMedica, especialidade, servico,
				unidadeFuncional, procedimentoCirurgico, leito);

		AinLeitos ainLeito = null;

		if (StringUtils.isNotBlank(leito)) {
			ainLeito = this.getPacienteON().obterLeito(leito);

			if (ainLeito == null) {
				throw new ApplicationBusinessException(PesquisarPacienteONExceptionCode.MENSAGEM_LEITO_INVALIDO);
			}
		}

		List<String> fonemasPaciente = null;
		if (StringUtils.isNotBlank(nomePaciente)) {
			fonemasPaciente = this.getFonemasPacienteRN().obterFonemasNaOrdem(nomePaciente);
		}
		
		return this.getAipPacientesDAO().pesquisaPacientes(firstResult, maxResults, orderProperty, asc, numeroProntuario,
				periodoAltaInicio, periodoAltaFim, periodoConsultaInicio, periodoConsultaFim, periodoCirurgiaInicio,
				periodoCirurgiaFim, nomePaciente, equipeMedica, especialidade, servico, unidadeFuncional, procedimentoCirurgico,
				leito, ainLeito, fonemasPaciente);
	}
	
	public Long pesquisaPacientesCount(Integer numeroProntuario, Date periodoAltaInicio,
			Date periodoAltaFim, Date periodoConsultaInicio, Date periodoConsultaFim,
			Date periodoCirurgiaInicio, Date periodoCirurgiaFim, String nomePaciente,
			AghEquipes equipeMedica, AghEspecialidades especialidade, FccCentroCustos servico,
			AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito)
			throws ApplicationBusinessException {
		this.getPacienteON().validaDadosPesquisaPacientes(numeroProntuario, periodoAltaInicio, periodoAltaFim,
				periodoConsultaInicio, periodoConsultaFim, periodoCirurgiaInicio,
				periodoCirurgiaFim, nomePaciente, equipeMedica, especialidade, servico,
				unidadeFuncional, procedimentoCirurgico, leito);

		AinLeitos ainLeito = null;

		if (StringUtils.isNotBlank(leito)) {
			ainLeito = this.getPacienteON().obterLeito(leito);

			if (ainLeito == null) {
				throw new ApplicationBusinessException(PesquisarPacienteONExceptionCode.MENSAGEM_LEITO_INVALIDO);
			}
		}
		
		List<String> fonemasPaciente = null;
		if (StringUtils.isNotBlank(nomePaciente)) {
			fonemasPaciente = this.getFonemasPacienteRN().obterFonemasNaOrdem(nomePaciente);
		}
		
		return this.getAipPacientesDAO().pesquisaPacientesCount(numeroProntuario, periodoAltaInicio, periodoAltaFim,
				periodoConsultaInicio, periodoConsultaFim, periodoCirurgiaInicio, periodoCirurgiaFim, nomePaciente, equipeMedica,
				especialidade, servico, unidadeFuncional, procedimentoCirurgico, leito, ainLeito, fonemasPaciente);
	}
	
	public List<AipPacientes> pesquisaPacientesMovimentacaoProntuario(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigoPaciente, Integer prontuario, String nomePesquisaPaciente) {
		return getAghSamisMovimentosDAO().pesquisa(firstResult, maxResults,
				orderProperty, asc, codigoPaciente, prontuario, nomePesquisaPaciente);
	}
	
	public Long pesquisaPacienteCount(Integer codigoPaciente, Integer prontuario, String nomePesquisaPaciente) {
		return this.getAghSamisMovimentosDAO().pesquisaCount(codigoPaciente, prontuario, nomePesquisaPaciente);
	}
	
	/**
	 * Pesquisa o nome do paciente buscando pelo numero do prontuario
	 * 
	 * @param prontuario
	 * @return nomePaciente
	 */
	public String pesquisarNomePaciente(Integer prontuario) {
		return this.getAipPacientesDAO().pesquisarNomePaciente(prontuario);
	}
	
	/**
	 * Método usado para obter todos os convênios (AipConveniosSaudePaciente) de
	 * um paciente.
	 * 
	 * @dbtables AipConveniosSaudePaciente select
	 * 
	 * @param paciente
	 * @return
	 */
	public List<AipConveniosSaudePaciente> pesquisarConveniosPaciente(AipPacientes paciente) {
		return getAipConveniosSaudePacienteDAO().pesquisarConveniosPaciente(paciente);
	}

	private AipConveniosSaudePacienteDAO getAipConveniosSaudePacienteDAO() {
		return aipConveniosSaudePacienteDAO;
	}
	
	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}
	
	protected FonemasPacienteRN getFonemasPacienteRN() {
		return fonemasPacienteRN;
	}
	
	protected AghSamisMovimentosDAO getAghSamisMovimentosDAO(){
		return aghSamisMovimentosDAO;
	}
	
	protected PacienteON getPacienteON() {
		return pacienteON;
	}
	
}
