package br.gov.mec.aghu.cups.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.configuracao.dao.ImpServidorCupsDAO;
import br.gov.mec.aghu.model.cups.ImpServidorCups;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe provedora dos metodos de negócio para o cadastro de servidores Cups
 * 
 * @author Heliz
 */
@Stateless
public class ServidorCupsON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ServidorCupsON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ImpServidorCupsDAO impServidorCupsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6737626891294690806L;

	private enum ServidorCupsONExceptionCode implements BusinessExceptionCode {
		PARAMETRO_NAO_INFORMADO, VIOLACAO_FK_SERVIDOR_CUPS, SERVIDOR_CUPS_EXISTENTE, SERVIDOR_CUPS_INEXISTENTE_PARA_ID, SERVIDOR_CUPS_INEXISTENTE, REGISTRO_NULO_EXCLUSAO
	};

	protected ImpServidorCupsDAO getImpServidorCupsDAO() {
		return impServidorCupsDAO;
	}

	/**
	 * @param ipServidor
	 * @param nomeCups
	 * @param descricao
	 * @return
	 */
	public Long pesquisarServidorCupsCount(String ipServidor,
			String nomeCups, String descricao) {

		return getImpServidorCupsDAO().pesquisarServidorCupsCount(ipServidor,
				nomeCups, descricao);
	}

	/**
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param ipServidor
	 * @param nomeCups
	 * @param descricao
	 * @return
	 */
	public List<ImpServidorCups> pesquisarServidorCups(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String ipServidor, String nomeCups, String descricao) {
		return getImpServidorCupsDAO().pesquisarServidorCups(firstResult,
				maxResult, orderProperty, asc, ipServidor, nomeCups, descricao);
	}

	/**
	 * @param idServidorCups
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public ImpServidorCups obterServidorCups(Integer idServidorCups)
			throws ApplicationBusinessException {
		if (idServidorCups == null) {
			throw new ApplicationBusinessException(
					ServidorCupsONExceptionCode.SERVIDOR_CUPS_EXISTENTE);
		}

		ImpServidorCups impServidorCups = getImpServidorCupsDAO()
				.obterPorChavePrimaria(idServidorCups);

		if (impServidorCups == null) {
			throw new ApplicationBusinessException(
					ServidorCupsONExceptionCode.SERVIDOR_CUPS_EXISTENTE);
		}

		return impServidorCups;
	}

	/**
	 * Excluir.
	 * 
	 * @param idServidorCups
	 * @throws ApplicationBusinessException
	 */
	public void excluirServidorCups(Integer idServidorCups)
			throws ApplicationBusinessException {
		
		try {
			ImpServidorCups impServidorCups = obterServidorCups(idServidorCups);
			getImpServidorCupsDAO().remover(impServidorCups);
			getImpServidorCupsDAO().flush();
		} catch (ApplicationBusinessException e){
			LOG.error(e.getMessage(),e);
			throw new ApplicationBusinessException(ServidorCupsONExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}catch (PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new ApplicationBusinessException(
						ServidorCupsONExceptionCode.VIOLACAO_FK_SERVIDOR_CUPS,
						cve.getConstraintName());
			}
		}
	}

	/**
	 * Gravar.
	 * 
	 * @param impServidorCups
	 * @throws ApplicationBusinessException
	 */
	public void gravarServidorCups(ImpServidorCups impServidorCups)
			throws ApplicationBusinessException {

		if (impServidorCups == null) {
			throw new ApplicationBusinessException(
					ServidorCupsONExceptionCode.SERVIDOR_CUPS_EXISTENTE);
		}

		if (impServidorCups.getId() == null) { // Inclusao

			if (getImpServidorCupsDAO().isServidorCupsExistente(null,
					impServidorCups.getIpServidor())) {
				throw new ApplicationBusinessException(
						ServidorCupsONExceptionCode.SERVIDOR_CUPS_EXISTENTE);
			}

			getImpServidorCupsDAO().persistir(impServidorCups);
			getImpServidorCupsDAO().flush();
		} else { // Alteracao

			if (getImpServidorCupsDAO().isServidorCupsExistente(
					impServidorCups.getId(), impServidorCups.getIpServidor())) {
				throw new ApplicationBusinessException(
						ServidorCupsONExceptionCode.SERVIDOR_CUPS_EXISTENTE);
			}

			getImpServidorCupsDAO().merge(impServidorCups);
			getImpServidorCupsDAO().flush();
		}

	}

	/**
	 * Pesquisar servidor por descrição.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<ImpServidorCups> pesquisarServidorCups(Object paramPesquisa) {
		return getImpServidorCupsDAO().pesquisarServidorCups(paramPesquisa);
	}
}
