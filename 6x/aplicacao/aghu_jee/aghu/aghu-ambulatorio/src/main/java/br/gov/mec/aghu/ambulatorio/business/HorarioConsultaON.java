package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacSituacaoConsultasDAO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author diego.pacheco
 * 
 */
@Stateless
public class HorarioConsultaON extends BaseBusiness {

	@EJB
	private AmbulatorioConsultaRN ambulatorioConsultaRN;
	
	private static final Log LOG = LogFactory.getLog(HorarioConsultaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AacSituacaoConsultasDAO aacSituacaoConsultasDAO;

	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ConsultasON consultasON;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7428615118776464104L;

	public enum HorarioConsultaONExceptionCode implements BusinessExceptionCode {
		AAC_00732
	}

	public void atualizarDisponibilidadeConsultas(List<Integer> consultas, AacSituacaoConsultas novaSituacao, String nomeMicrocomputador) throws NumberFormatException, BaseException {
		
		boolean verificouQualificacao = false;
		
		final Date dtFimVinculoServ = new Date();
		for (Integer numero : consultas) {
			AacConsultas consulta = aacConsultasDAO.obterPorChavePrimaria(numero);
			
			if (!verificouQualificacao) {
				verificaUsuarioQualificado(consulta);
				verificouQualificacao = true;
			}
			
			AacConsultas consultaOld = consultasON.clonarConsulta(consulta);
			consulta.setSituacaoConsulta(novaSituacao);
			
			ambulatorioConsultaRN.atualizarConsulta(consultaOld, consulta, false, nomeMicrocomputador, dtFimVinculoServ, false, false);
		}
		this.getAacConsultasDAO().flush();
	}

	/**
	 * Método chamado ao alterar disponibilidade de horarios de consulta
	 * 
	 * @param entity
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void verificaUsuarioQualificado(AacConsultas consulta)
			throws ApplicationBusinessException {

		AacSituacaoConsultas situacao = (AacSituacaoConsultas) consulta
				.getSituacaoConsulta();

		Boolean usuarioQualificadoReserva = getPacienteFacade()
				.verificarAcaoQualificacaoMatricula("RESERVAR CONSULTA");

		if (situacao != null && !situacao.getSituacao().equals("R")
				&& usuarioQualificadoReserva) {
			throw new ApplicationBusinessException(
					HorarioConsultaONExceptionCode.AAC_00732);
		} else {
			return;		
		}
	}
	
	/**
	 * Retorna lista de situações de consulta ativas conforme
	 * o critério de pesquisa informado.
	 * 
	 * @param parametro
	 * @return List de AacSituacaoConsultas
	 */
	public List<AacSituacaoConsultas> pesquisarSituacoesConsultaAtivas(String parametro) {
		AacSituacaoConsultasDAO dao = getAacSituacaoConsultasDAO();
		List<AacSituacaoConsultas> result = dao
				.pesquisarSituacaoPorSiglaAtiva(parametro);
		if (result == null || result.isEmpty()) {
			result = dao.pesquisarSituacaoPorDescricaoAtiva(parametro);
		}
		return result;
	}
	
	/**
	 * Retorna lista de situações de consulta ativas conforme o critério de
	 * pesquisa informado.
	 * 
	 * @param parametro
	 * @return List de AacSituacaoConsultas
	 */
	public List<AacSituacaoConsultas> pesquisarSituacoesConsultasSemMarcadasAtivas(
			String parametro) {
		AacSituacaoConsultasDAO dao = getAacSituacaoConsultasDAO();
		List<AacSituacaoConsultas> result = dao.pesquisarSituacaoSemMarcadaPorSiglaAtiva(parametro);
		if (result == null || result.isEmpty()) {
			result = dao.pesquisarSituacaoSemMarcadaPorDescricaoAtiva(parametro);
		}
		return result;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
		return ambulatorioConsultaRN;
	}
	
	public AacSituacaoConsultasDAO getAacSituacaoConsultasDAO() {
		return aacSituacaoConsultasDAO;
	}

	public AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	public ConsultasON getConsultasON() {
		return consultasON;
	}
	
}
