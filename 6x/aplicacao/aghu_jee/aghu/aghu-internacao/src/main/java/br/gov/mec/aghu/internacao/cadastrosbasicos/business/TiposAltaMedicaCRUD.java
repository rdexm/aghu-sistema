package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.internacao.dao.AinTiposAltaMedicaDAO;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * Tipos de Alta Médica.
 */
@Stateless
public class TiposAltaMedicaCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TiposAltaMedicaCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinTiposAltaMedicaDAO ainTiposAltaMedicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2793844788268261609L;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private enum TiposAltaMedicaCRUDExceptionCode implements
			BusinessExceptionCode {
		ERRO_REMOCAO_TIPOALTAMEDICA, DESCRICAO_TIPOALTAMEDICA_JA_EXISTENTE, CODIGO_TIPOALTAMEDICA_JA_EXISTENTE, ERRO_PERSISTIR_TIPOALTAMEDICA, ERRO_ATUALIZAR_TIPOALTAMEDICA, ERRO_EXCUSAO_FK_ATD_URGENCIA;

	}

	/**
	 * Método responsável por remover um registro do tipo TipoAltaMedica.
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public void removerTipoAltaMedica(String codigo) throws ApplicationBusinessException {
		try {
			AinTiposAltaMedica tipoAltaMedica = this.cadastrosBasicosInternacaoFacade.obterTipoAltaMedica(codigo);
			AinTiposAltaMedicaDAO ainTiposAltaMedicaDAO = this.getAinTiposAltaMedicaDAO();
			ainTiposAltaMedicaDAO.remover(tipoAltaMedica);
			ainTiposAltaMedicaDAO.flush();
		} catch (Exception e) {
			logError("Erro ao remover tipoAltaMedica", e);

			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause().getClass())
					&& StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),
							"AGH.AIN_ATU_TAM_FK1")) {
				throw new ApplicationBusinessException(TiposAltaMedicaCRUDExceptionCode.ERRO_EXCUSAO_FK_ATD_URGENCIA);
			}

			throw new ApplicationBusinessException(
					TiposAltaMedicaCRUDExceptionCode.ERRO_REMOCAO_TIPOALTAMEDICA);
		}
	}

	/**
	 * Método responsável pela persistência.
	 * 
	 * @param tipoAltaMedica
	 * @throws ApplicationBusinessException
	 */
	public void persistirTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica, boolean edicao) throws ApplicationBusinessException {
		if (edicao) {
			// edição
			this.atualizarTipoAltaMedica(tipoAltaMedica);
		} else {
			// inclusão
			this.incluirTipoAltaMedica(tipoAltaMedica);
		}
	}

	/**
	 * Método responsável por incluir um novo Tipo de Alta Médica.
	 * 
	 * @param tipoAltaMedica
	 * @throws ApplicationBusinessException
	 */
	//(TransactionPropagationType.SUPPORTS)
	private void incluirTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica) throws ApplicationBusinessException {

		tipoAltaMedica.setCodigo(tipoAltaMedica.getCodigo().toUpperCase());
		tipoAltaMedica.setDescricao(tipoAltaMedica.getDescricao().toUpperCase());

		this.validarDadosTipoAltaMedica(tipoAltaMedica);

		AinTiposAltaMedicaDAO ainTiposAltaMedicaDAO = this.getAinTiposAltaMedicaDAO();
		ainTiposAltaMedicaDAO.persistir(tipoAltaMedica);
		ainTiposAltaMedicaDAO.flush();
	}

	/**
	 * Método responsável pela atualização de um Tipo de Alta Médica.
	 * 
	 * @param tipoAltaMedica
	 * @throws ApplicationBusinessException
	 */
	//(TransactionPropagationType.SUPPORTS)
	private void atualizarTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica) throws ApplicationBusinessException {
		AinTiposAltaMedicaDAO ainTiposAltaMedicaDAO = this.getAinTiposAltaMedicaDAO();
		ainTiposAltaMedicaDAO.atualizar(tipoAltaMedica);
		ainTiposAltaMedicaDAO.flush();
	}

	/**
	 * Método responsável pelas validações dos dados. Método utilizado para
	 * inclusão de tipoAltaMedica.
	 * 
	 * @param tipoAltaMedica
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica)
			throws ApplicationBusinessException {
		// validar codigo duplicado
		AinTiposAltaMedica tipo =  getAinTiposAltaMedicaDAO().obterTipoAltaMedica(tipoAltaMedica.getCodigo());
		if (tipo != null) {
			throw new ApplicationBusinessException(
					TiposAltaMedicaCRUDExceptionCode.CODIGO_TIPOALTAMEDICA_JA_EXISTENTE);
		}
	}

	/* ## Métodos usados na paginação da lista ## */

	
	
	
	
	
	
	protected AinTiposAltaMedicaDAO getAinTiposAltaMedicaDAO() {
		return ainTiposAltaMedicaDAO;
	}
	
	
	
}
