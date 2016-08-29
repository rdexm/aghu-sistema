package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.internacao.dao.AinTiposCaraterInternacaoDAO;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de Tipos de Caráter de Internação.
 */
@Stateless
public class TiposCaraterInternacaoCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TiposCaraterInternacaoCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinTiposCaraterInternacaoDAO ainTiposCaraterInternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7829033494386105815L;



	private enum TiposCaraterInternacaoCRUDExceptionCode implements
			BusinessExceptionCode {
		ERRO_PERSISTIR_TIPOSCARATERINTERNACAO, ERRO_REMOVER_TIPOSCARATERINTERNACAO, DESCRICAO_TIPOSCARATERINTERNACAO_OBRIGATORIO, DESCRICAO_TIPOSCARATERINTERNACAO_JA_EXISTENTE, CODIGOSUS_TIPOSCARATERINTERNACAO_OBRIGATORIO, ERRO_EXCUSAO_FK_CIDS, ERRO_EXCUSAO_FK_INTERNACAO;
	}

	
	/**
	 * Método responsável pela persistência de um tipo de caráter de internação.
	 * 
	 * @param tipo
	 *            de caráter de internação
	 * @throws ApplicationBusinessException
	 */
	public void persistirTiposCaraterInternacao(
			AinTiposCaraterInternacao tiposCaraterInternacao)
			throws ApplicationBusinessException {
		if (tiposCaraterInternacao.getCodigo() == null) {
			// inclusão
			this.incluirTiposCaraterInternacao(tiposCaraterInternacao);
		} else {
			// edição
			this.atualizarTiposCaraterInternacao(tiposCaraterInternacao);
		}

	}

	/**
	 * Método responsável por incluir um novo tipo de caráter de internação.
	 * 
	 * @param tipo
	 *            de caráter de internação
	 * @throws ApplicationBusinessException
	 */
	//
	private void incluirTiposCaraterInternacao(
			AinTiposCaraterInternacao tiposCaraterInternacao)
			throws ApplicationBusinessException {
		this.validarDadosTiposCaraterInternacao(tiposCaraterInternacao);

		try {
			AinTiposCaraterInternacaoDAO ainTiposCaraterInternacaoDAO = this.getAinTiposCaraterInternacaoDAO();
			ainTiposCaraterInternacaoDAO.persistir(tiposCaraterInternacao);
			ainTiposCaraterInternacaoDAO.flush();
		} catch (Exception e) {
			logError("Erro ao incluir o tipo de caráter de internação.", e);
			throw new ApplicationBusinessException(
					TiposCaraterInternacaoCRUDExceptionCode.ERRO_PERSISTIR_TIPOSCARATERINTERNACAO);
		}
	}

	/**
	 * Método responsável pela atualização de um tipo de caráter de internação.
	 * 
	 * @param tipo
	 *            de caráter de internação
	 * @throws ApplicationBusinessException
	 */
	//
	private void atualizarTiposCaraterInternacao(
			AinTiposCaraterInternacao tiposCaraterInternacao)
			throws ApplicationBusinessException {
		this.validarDadosTiposCaraterInternacao(tiposCaraterInternacao);

		try {
			AinTiposCaraterInternacaoDAO ainTiposCaraterInternacaoDAO = this.getAinTiposCaraterInternacaoDAO();
			ainTiposCaraterInternacaoDAO.atualizar(tiposCaraterInternacao);
			ainTiposCaraterInternacaoDAO.flush();
		} catch (Exception e) {
			logError("Erro ao atualizar o tipo de caráter de internação.", e);
			throw new ApplicationBusinessException(
					TiposCaraterInternacaoCRUDExceptionCode.ERRO_PERSISTIR_TIPOSCARATERINTERNACAO);
		}
	}

	/**
	 * Método responsável pelas validações dos dados de um tipo de caráter de
	 * internação. Método utilizado para inclusão e atualização de um tipo de
	 * caráter de internação.
	 * 
	 * @param observação
	 *            de alta do paciente
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosTiposCaraterInternacao(
			AinTiposCaraterInternacao tiposCaraterInternacao)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(tiposCaraterInternacao.getDescricao())) {
			throw new ApplicationBusinessException(
					TiposCaraterInternacaoCRUDExceptionCode.DESCRICAO_TIPOSCARATERINTERNACAO_OBRIGATORIO);
		}

		if (tiposCaraterInternacao.getCodSus() == null) {
			throw new ApplicationBusinessException(
					TiposCaraterInternacaoCRUDExceptionCode.CODIGOSUS_TIPOSCARATERINTERNACAO_OBRIGATORIO);
		}
	}

	
	/**
	 * Apaga um tipo de caráter de internação do banco de dados.
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public void removerTiposCaraterInternacao(
			Integer codigo)
			throws ApplicationBusinessException {
		try {
			AinTiposCaraterInternacao tiposCaraterInternacao = this.getAinTiposCaraterInternacaoDAO().obterTiposCaraterInternacao(codigo);
			ainTiposCaraterInternacaoDAO.remover(tiposCaraterInternacao);
			ainTiposCaraterInternacaoDAO.flush();
		} catch (Exception e) {
			logError("Erro ao remover tipo de caráter de internação.", e);
			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause()
							.getClass())) {
				if (StringUtils.containsIgnoreCase(
						((ConstraintViolationException) e.getCause())
								.getConstraintName(), "AGH_CCI_TCI_FK1")) {
					throw new ApplicationBusinessException(
							TiposCaraterInternacaoCRUDExceptionCode.ERRO_EXCUSAO_FK_CIDS);
				} else if (StringUtils.containsIgnoreCase(
						((ConstraintViolationException) e.getCause())
								.getConstraintName(), "AIN_INT_TCI_FK1")) {
					throw new ApplicationBusinessException(
							TiposCaraterInternacaoCRUDExceptionCode.ERRO_EXCUSAO_FK_INTERNACAO);
				}
			}
			throw new ApplicationBusinessException(
					TiposCaraterInternacaoCRUDExceptionCode.ERRO_REMOVER_TIPOSCARATERINTERNACAO);
		}
	}
	
	protected AinTiposCaraterInternacaoDAO getAinTiposCaraterInternacaoDAO() {
		return ainTiposCaraterInternacaoDAO;
	}
	
}
