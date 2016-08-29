package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.dao.AinObservacoesPacAltaDAO;
import br.gov.mec.aghu.model.AinObservacoesPacAlta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de Observações alta paciente.
 */
@Stateless
public class ObservacoesPacAltaCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ObservacoesPacAltaCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AinObservacoesPacAltaDAO ainObservacoesPacAltaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6134165148637087304L;

	private enum ObservacoesPacAltaCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_OBSERVACOESPACALTA, ERRO_REMOVER_OBSERVACOESPACALTA, DESCRICAO_OBSERVACOESPACALTA_OBRIGATORIO, DESCRICAO_OBSERVACOESPACALTA_JA_EXISTENTE;
	}
	
	/**
	 * Método responsável pela persistência de uma observação de alta do paciente.
	 * 
	 * @param observação de alta do paciente
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void persistirObservacoesPacAlta(AinObservacoesPacAlta observacoesPacAlta)
			throws ApplicationBusinessException {
		if (observacoesPacAlta.getCodigo() == null) {
			// inclusão
			this.incluirObservacoesPacAlta(observacoesPacAlta);
		} else {
			// edição
			this.atualizarObservacoesPacAlta(observacoesPacAlta);
		}

	}

	/**
	 * Método responsável por incluir uma nova observação de alta do paciente.
	 * 
	 * @param observação de alta do paciente
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	//
	private void incluirObservacoesPacAlta(AinObservacoesPacAlta observacoesPacAlta)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.validarDadosObservacoesPacAlta(observacoesPacAlta);

		//Seta valores default
		observacoesPacAlta.setServidorCriacao(servidorLogado);
		observacoesPacAlta.setCriadoEm(new Date());
		
		AinObservacoesPacAltaDAO ainObservacoesPacAltaDAO = this.getAinObservacoesPacAltaDAO();
		ainObservacoesPacAltaDAO.persistir(observacoesPacAlta);
		ainObservacoesPacAltaDAO.flush();
	}

	/**
	 * Método responsável pela atualização de uma observação de alta do paciente.
	 * 
	 * @param observação de alta do paciente
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	//
	private void atualizarObservacoesPacAlta(AinObservacoesPacAlta observacoesPacAlta)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.validarDadosObservacoesPacAlta(observacoesPacAlta);

		//Seta valores default
		observacoesPacAlta.setServidorAlteracao(servidorLogado);
		observacoesPacAlta.setAlteradoEm(new Date());
		
		
		AinObservacoesPacAltaDAO ainObservacoesPacAltaDAO = this.getAinObservacoesPacAltaDAO();
		ainObservacoesPacAltaDAO.atualizar(observacoesPacAlta);
		ainObservacoesPacAltaDAO.flush();
	}

	/**
	 * Método responsável pelas validações dos dados de uma observação de alta do paciente. Método
	 * utilizado para inclusão e atualização de uma observação de alta do paciente.
	 * 
	 * @param observação de alta do paciente
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosObservacoesPacAlta(AinObservacoesPacAlta observacoesPacAlta)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(observacoesPacAlta.getDescricao())) {
			throw new ApplicationBusinessException(
					ObservacoesPacAltaCRUDExceptionCode.DESCRICAO_OBSERVACOESPACALTA_OBRIGATORIO);
		}
	}
	
	/**
	 * Apaga uma observação de alta do paciente do banco de dados.
	 * 
	 * @param observação de alta do paciente
	 *            Observação de alta do paciente a ser removida.
	 * @throws ApplicationBusinessException
	 */
	public void removerObservacoesPacAlta(Integer codigo)
			throws ApplicationBusinessException {
		try {
			AinObservacoesPacAltaDAO ainObservacoesPacAltaDAO = this.getAinObservacoesPacAltaDAO();
			AinObservacoesPacAlta observacoesPacAlta = this.ainObservacoesPacAltaDAO
					.obterObservacoesPacAlta(codigo);
			ainObservacoesPacAltaDAO.remover(observacoesPacAlta);
			ainObservacoesPacAltaDAO.flush();
		} catch (Exception e) {
			this.logError("Erro ao remover a observação de alta do paciente.", e);
			throw new ApplicationBusinessException(
					ObservacoesPacAltaCRUDExceptionCode.ERRO_REMOVER_OBSERVACOESPACALTA);
		}
	}
	
	protected AinObservacoesPacAltaDAO getAinObservacoesPacAltaDAO() {
		return ainObservacoesPacAltaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
