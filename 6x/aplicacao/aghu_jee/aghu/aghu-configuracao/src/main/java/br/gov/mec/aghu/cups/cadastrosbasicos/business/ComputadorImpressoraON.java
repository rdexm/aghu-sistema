package br.gov.mec.aghu.cups.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.configuracao.dao.ImpClasseImpressaoDAO;
import br.gov.mec.aghu.configuracao.dao.ImpComputadorDAO;
import br.gov.mec.aghu.configuracao.dao.ImpComputadorImpressoraDAO;
import br.gov.mec.aghu.configuracao.dao.ImpImpressoraDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.model.cups.ImpImpressora;

@Stateless
public class ComputadorImpressoraON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ComputadorImpressoraON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ImpImpressoraDAO impImpressoraDAO;

@Inject
private ImpComputadorImpressoraDAO impComputadorImpressoraDAO;

@Inject
private ImpClasseImpressaoDAO impClasseImpressaoDAO;

@Inject
private ImpComputadorDAO impComputadorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2420873633775486818L;

	/**
	 * Enumeracao com os codigos de mensagens de exceções negociais do cadastro
	 * de computador
	 */
	private enum ComputadorImpressoraONExceptionCode implements
			BusinessExceptionCode {
		PARAMETRO_NAO_INFORMADO, VIOLACAO_FK_COMPUTADOR_IMPRESSORA, COMPUTADOR_IMPRESSORA_EXISTENTE, COMPUTADOR_IMPRESSORA_INEXISTENTE, COMPUTADOR_IMPRESSORA_NAO_ALTERADO, COMPUTADOR_IMPRESSORA_CLASSE_IMPRESSAO, COMPUTADOR_IMPRESSORA_TIPO_CUPS, ERRO_REMOVER_COMPUTADOR_IMPRESSORA, REGISTRO_NULO_EXCLUSAO
	};

	protected ImpComputadorImpressoraDAO getImpComputadorImpressoraDAO() {
		return impComputadorImpressoraDAO;
	}

	protected ImpImpressoraDAO getImpImpressoraDAO() {
		return impImpressoraDAO;
	}

	/**
	 * Pesquisar count.
	 * 
	 * @param impComputadorImpressora
	 * @return
	 */
	public Long pesquisarComputadorImpressoraCount(
			ImpComputadorImpressora impComputadorImpressora) {
		return getImpComputadorImpressoraDAO()
				.pesquisarComputadorImpressoraCount(impComputadorImpressora);
	}

	/**
	 * Metodo definido para retornar uma lista de computadores
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param impComputadorImpressora
	 * @return
	 */
	public List<ImpComputadorImpressora> pesquisarComputadorImpressora(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ImpComputadorImpressora impComputadorImpressora) {

		return getImpComputadorImpressoraDAO().pesquisarComputadorImpressora(
				firstResult, maxResult, orderProperty, asc,
				impComputadorImpressora);
	}

	/**
	 * Método definido para obter um computador
	 * 
	 * @param idComputadorImpressora
	 * @return idComputadorImpressora
	 */
	public ImpComputadorImpressora obterComputadorImpressora(
			Integer idComputadorImpressora) throws ApplicationBusinessException {
		if (idComputadorImpressora == null) {
			throw new ApplicationBusinessException(
					ComputadorImpressoraONExceptionCode.PARAMETRO_NAO_INFORMADO);
		}

		ImpComputadorImpressora impComputadorImpressora = getImpComputadorImpressoraDAO()
				.obterPorChavePrimaria(idComputadorImpressora, true,
						ImpComputadorImpressora.Fields.IMP_CLASSE_IMPRESSAO,
						ImpComputadorImpressora.Fields.IMP_COMPUTADOR,
						ImpComputadorImpressora.Fields.IMP_IMPRESSORA);

		if (impComputadorImpressora == null) {
			throw new ApplicationBusinessException(
					ComputadorImpressoraONExceptionCode.COMPUTADOR_IMPRESSORA_INEXISTENTE);

		}

		return impComputadorImpressora;
	}

	public void excluirComputadorImpressora(Integer idComputadorImpressora)
			throws ApplicationBusinessException {

		
		try {
			ImpComputadorImpressora impComputadorImpressora = obterComputadorImpressora(idComputadorImpressora);
			getImpComputadorImpressoraDAO().remover(impComputadorImpressora);
			getImpComputadorImpressoraDAO().flush();
		}  catch (ApplicationBusinessException e){
			LOG.error(e.getMessage(),e);
			throw new ApplicationBusinessException(ComputadorImpressoraONExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}catch (PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new ApplicationBusinessException(
						ComputadorImpressoraONExceptionCode.VIOLACAO_FK_COMPUTADOR_IMPRESSORA,
						cve.getConstraintName());
			} else {
				throw new ApplicationBusinessException(
						ComputadorImpressoraONExceptionCode.ERRO_REMOVER_COMPUTADOR_IMPRESSORA);
			}

		}
	}

	/**
	 * @param impComputadorImpressora
	 * @throws ApplicationBusinessException
	 */
	public void gravarComputadorImpressora(
			ImpComputadorImpressora impComputadorImpressora)
			throws ApplicationBusinessException {

		if (impComputadorImpressora == null) {
			throw new ApplicationBusinessException(
					ComputadorImpressoraONExceptionCode.COMPUTADOR_IMPRESSORA_INEXISTENTE);
		}

		if (impComputadorImpressora.getId() == null) { // Inclusao

			if (getImpComputadorImpressoraDAO()
					.isComputadorImpressoraExistente(null,
							impComputadorImpressora)) {
				throw new ApplicationBusinessException(
						ComputadorImpressoraONExceptionCode.COMPUTADOR_IMPRESSORA_EXISTENTE,
						impComputadorImpressora.getImpClasseImpressao().getClasseImpressao(),
						impComputadorImpressora.getImpComputador()
								.getNomeComputador());
			}

			if (isValidarImpressoraTipoCups(impComputadorImpressora)) {
				// reatacha objeto para persistir
				impComputadorImpressora.setImpClasseImpressao(impClasseImpressaoDAO
						.obterPorChavePrimaria(impComputadorImpressora.getImpClasseImpressao().getId()));
				impComputadorImpressora.setImpComputador(impComputadorDAO
								.obterPorChavePrimaria(impComputadorImpressora.getImpComputador()
										.getSeq()));
				impComputadorImpressora.setImpImpressora(impImpressoraDAO
						.obterPorChavePrimaria(impComputadorImpressora.getImpImpressora().getId()));

				getImpComputadorImpressoraDAO()
						.persistir(impComputadorImpressora);
			}

		} else { // Alteracao

			if (getImpComputadorImpressoraDAO()
					.isComputadorImpressoraExistente(
							impComputadorImpressora.getId(),
							impComputadorImpressora)) {
				throw new ApplicationBusinessException(
						ComputadorImpressoraONExceptionCode.COMPUTADOR_IMPRESSORA_INEXISTENTE);
			}

			if (isValidarImpressoraTipoCups(impComputadorImpressora)) {
				// reatacha objeto para persistir
				impComputadorImpressora.setImpClasseImpressao(impClasseImpressaoDAO
						.obterPorChavePrimaria(impComputadorImpressora.getImpClasseImpressao().getId()));
				impComputadorImpressora.setImpComputador(impComputadorDAO
						.obterPorChavePrimaria(impComputadorImpressora.getImpComputador()
								.getSeq()));
				impComputadorImpressora.setImpImpressora(impImpressoraDAO
						.obterPorChavePrimaria(impComputadorImpressora.getImpImpressora().getId()));
				getImpComputadorImpressoraDAO().merge(impComputadorImpressora);
			}
		}
	}

	/**
	 * Valida impressora CUPS.
	 * 
	 * @param impComputadorImpressora
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private boolean isValidarImpressoraTipoCups(
			ImpComputadorImpressora impComputadorImpressora)
			throws ApplicationBusinessException {

		boolean retorno = false;

		ImpImpressora impressora = getImpImpressoraDAO().obterPorChavePrimaria(
				impComputadorImpressora.getImpImpressora().getId());

		if (impressora.getTipoCups().getCodigo() == impComputadorImpressora
				.getImpClasseImpressao().getTipoCups().getCodigo()) {
			retorno = true;
		} else {
			throw new ApplicationBusinessException(
					ComputadorImpressoraONExceptionCode.COMPUTADOR_IMPRESSORA_TIPO_CUPS);
		}

		return retorno;

	}

}
