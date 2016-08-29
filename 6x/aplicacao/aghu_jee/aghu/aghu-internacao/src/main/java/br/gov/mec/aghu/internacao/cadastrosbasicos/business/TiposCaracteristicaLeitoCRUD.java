package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.internacao.dao.AinTipoCaracteristicaLeitoDAO;
import br.gov.mec.aghu.model.AinTipoCaracteristicaLeito;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de tipo de característica de Leito.
 */
@Stateless
public class TiposCaracteristicaLeitoCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TiposCaracteristicaLeitoCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinTipoCaracteristicaLeitoDAO ainTipoCaracteristicaLeitoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9122378437605943680L;



	private enum TiposCaracteristicaLeitoCRUDExceptionCode implements
			BusinessExceptionCode {
		ERRO_PERSISTIR_TIPO_CARACTERISTICA_LEITO, ERRO_REMOVER_TIPO_CARACTERISTICA_LEITO, DESCRICAO_TIPO_CARACTERISTICA_LEITO_OBRIGATORIO, DESCRICAO_TIPO_CARACTERISTICA_LEITO_JA_EXISTENTE, ERRO_REMOVER_TIPO_CARACTERISTICA_LEITO_COM_DEPENDENTES;
	}

	/**
	 * Método responsável pela persistência de um tipo de característica de
	 * leito.
	 * 
	 * @dbtables AinTipoCaracteristicaLeito select,insert,update
	 * 
	 * @param tipo
	 *            de característica de leito.
	 * @throws ApplicationBusinessException
	 */
	public void persistirTiposCaracteristicaLeito(
			AinTipoCaracteristicaLeito tipoCaracteristicaLeito)
			throws ApplicationBusinessException {
		if (tipoCaracteristicaLeito.getCodigo() == null) {
			// inclusão
			this.incluirTiposCaracteristicaLeito(tipoCaracteristicaLeito);
		} else {
			// edição
			this.atualizarTiposCaracteristicaLeito(tipoCaracteristicaLeito);
		}

	}

	/**
	 * Método responsável por incluir um novo tipo de característica de leito.
	 * 
	 * @param tipo
	 *            de característica de leito
	 * @throws ApplicationBusinessException
	 */
	//
	private void incluirTiposCaracteristicaLeito(AinTipoCaracteristicaLeito tipoCaracteristicaLeito) throws ApplicationBusinessException {
		this.validarDadosTiposCaracteristicaLeito(tipoCaracteristicaLeito);

		AinTipoCaracteristicaLeitoDAO ainTipoCaracteristicaLeitoDAO = this.getAinTipoCaracteristicaLeitoDAO();
		ainTipoCaracteristicaLeitoDAO.persistir(tipoCaracteristicaLeito);
		ainTipoCaracteristicaLeitoDAO.flush();
	}

	/**
	 * Método responsável pela atualização de um tipo de característica de
	 * leito.
	 * 
	 * @param tipo
	 *            de característica de leito
	 * @throws ApplicationBusinessException
	 */
	//
	private void atualizarTiposCaracteristicaLeito(
			AinTipoCaracteristicaLeito tipoCaracteristicaLeito)
			throws ApplicationBusinessException {
		this.validarDadosTiposCaracteristicaLeito(tipoCaracteristicaLeito);

		AinTipoCaracteristicaLeitoDAO ainTipoCaracteristicaLeitoDAO = this.getAinTipoCaracteristicaLeitoDAO();
		ainTipoCaracteristicaLeitoDAO.atualizar(tipoCaracteristicaLeito);
		ainTipoCaracteristicaLeitoDAO.flush();
	}

	/**
	 * Método responsável pelas validações dos dados de tipo de característica
	 * de leito. Método utilizado para inclusão e atualização de tipo de
	 * característica de leito.
	 * 
	 * @param tipo
	 *            de característica de leito
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosTiposCaracteristicaLeito(
			AinTipoCaracteristicaLeito tipoCaracteristicaLeito)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(tipoCaracteristicaLeito.getDescricao())) {
			throw new ApplicationBusinessException(
					TiposCaracteristicaLeitoCRUDExceptionCode.DESCRICAO_TIPO_CARACTERISTICA_LEITO_OBRIGATORIO);
		}

		List<AinTipoCaracteristicaLeito> ainTipoCaracteristicaLeito = null;

		// validar descrição duplicada
		ainTipoCaracteristicaLeito = getAinTipoCaracteristicaLeitoDAO().getTiposCaracteristicaLeitoComMesmaDescricao(tipoCaracteristicaLeito);
		if (ainTipoCaracteristicaLeito != null
				&& !ainTipoCaracteristicaLeito.isEmpty()) {
			throw new ApplicationBusinessException(
					TiposCaracteristicaLeitoCRUDExceptionCode.DESCRICAO_TIPO_CARACTERISTICA_LEITO_JA_EXISTENTE);
		}

	}

	/**
	 * Apaga um tipo de característica de leito do banco de dados.
	 * 
	 * @dbtables AinTipoCaracteristicaLeito delete
	 * 
	 * @param tipo
	 *            de característica de leito. Tipo de característica de leito a
	 *            ser removida.
	 * @throws ApplicationBusinessException
	 */
	public void removerTiposCaracteristicaLeito(
			AinTipoCaracteristicaLeito tipoCaracteristicaLeito)
			throws ApplicationBusinessException {
		try {
			AinTipoCaracteristicaLeitoDAO ainTipoCaracteristicaLeitoDAO = this.getAinTipoCaracteristicaLeitoDAO();
			ainTipoCaracteristicaLeitoDAO.remover(tipoCaracteristicaLeito);
			ainTipoCaracteristicaLeitoDAO.flush();
		} catch (Exception e) {
			logError("Erro ao remover o tipo de característica de leito.", e);
			if (e.getCause() != null
					&& e.getCause().getClass()
							.equals(ConstraintViolationException.class)) {
				throw new ApplicationBusinessException(
						TiposCaracteristicaLeitoCRUDExceptionCode.ERRO_REMOVER_TIPO_CARACTERISTICA_LEITO_COM_DEPENDENTES);
			}
			throw new ApplicationBusinessException(
					TiposCaracteristicaLeitoCRUDExceptionCode.ERRO_REMOVER_TIPO_CARACTERISTICA_LEITO);
		}
	}
	
	protected AinTipoCaracteristicaLeitoDAO getAinTipoCaracteristicaLeitoDAO() {
		return ainTipoCaracteristicaLeitoDAO;
	}

}
