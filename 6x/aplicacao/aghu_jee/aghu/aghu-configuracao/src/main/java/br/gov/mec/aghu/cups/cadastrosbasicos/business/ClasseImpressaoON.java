package br.gov.mec.aghu.cups.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.configuracao.dao.ImpClasseImpressaoDAO;
import br.gov.mec.aghu.dominio.DominioTipoCups;
import br.gov.mec.aghu.dominio.DominioTipoImpressoraCups;
import br.gov.mec.aghu.model.cups.ImpClasseImpressao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @author NEUSA
 * @since 19/10/2010
 * 
 */
@Stateless
public class ClasseImpressaoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ClasseImpressaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ImpClasseImpressaoDAO impClasseImpressaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8749474920183258112L;

	private enum ClasseImpressaoONExceptionCode implements BusinessExceptionCode {
		PARAMETRO_NAO_INFORMADO, VIOLACAO_FK_CLASSE_IMPRESSAO, CLASSE_IMPRESSAO_EXISTENTE, MENSAGEM_CODIGO_CLASSE_IMPRESSAO_INVALIDA_PARA_PESQUISA, CLASSE_IMPRESSAO_INEXISTENTE_PARA_ID, CLASSE_IMPRESSAO_INEXISTENTE, ERRO_REMOVER_CLASSE_IMPRESSAO, REGISTRO_NULO_EXCLUSAO
	};

	protected ImpClasseImpressaoDAO getImpClasseImpressaoDAO() {
		return impClasseImpressaoDAO;
	}

	/**
	 * Pesquisar count.
	 * 
	 * @param classeImpressao
	 * @param tipoImpressora
	 * @param descricao
	 * @param tipoCups
	 * @return
	 */
	public Long pesquisarClasseImpressaoCount(String classeImpressao,
			DominioTipoImpressoraCups tipoImpressora, String descricao,
			DominioTipoCups tipoCups) {
		return getImpClasseImpressaoDAO().pesquisarClasseImpressaoCount(
				classeImpressao, tipoImpressora, descricao, tipoCups);
	}

	/**
	 * Pesquisar.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param classeImpressao
	 * @param tipoImpressora
	 * @param descricao
	 * @param tipoCups
	 * @return
	 */
	public List<ImpClasseImpressao> pesquisarClasseImpressao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String classeImpressao,
			DominioTipoImpressoraCups tipoImpressora, String descricao,
			DominioTipoCups tipoCups) {
		return getImpClasseImpressaoDAO().pesquisarClasseImpressao(firstResult,
				maxResult, orderProperty, asc, classeImpressao, tipoImpressora,
				descricao, tipoCups);
	}

	/**
	 * Obter classe de impressão.
	 * 
	 * @param idClasseImpressao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public ImpClasseImpressao obterClasseImpressao(Integer idClasseImpressao)
			throws ApplicationBusinessException {

		ImpClasseImpressao impClasseImpressao = getImpClasseImpressaoDAO()
				.obterPorChavePrimaria(idClasseImpressao);

		return impClasseImpressao;
	}

	/**
	 * Remover.
	 * 
	 * @param idClasseImpressao
	 * @throws ApplicationBusinessException
	 */
	public void excluirClasseImpressao(Integer idClasseImpressao)
			throws ApplicationBusinessException {

		

		try {
			ImpClasseImpressao impClasseImpressora = obterClasseImpressao(idClasseImpressao);
			getImpClasseImpressaoDAO().remover(impClasseImpressora);
			getImpClasseImpressaoDAO().flush();
		} catch (ApplicationBusinessException e){
			LOG.error(e.getMessage(),e);
			throw new ApplicationBusinessException(ClasseImpressaoONExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}catch (PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new ApplicationBusinessException(
						ClasseImpressaoONExceptionCode.VIOLACAO_FK_CLASSE_IMPRESSAO,
						cve.getConstraintName());
			} else {
				throw new ApplicationBusinessException(
						ClasseImpressaoONExceptionCode.ERRO_REMOVER_CLASSE_IMPRESSAO);
			}
		}
	}

	/**
	 * Gravar registro.
	 * 
	 * @param impClasseImpressao
	 * @throws ApplicationBusinessException
	 */
	public void gravarClasseImpressao(ImpClasseImpressao impClasseImpressao)
			throws ApplicationBusinessException {

		if (impClasseImpressao.getId() == null) { // Inclusao

			if (isClasseImpressaoExistente(null,
					impClasseImpressao.getClasseImpressao())) {
				throw new ApplicationBusinessException(
						ClasseImpressaoONExceptionCode.CLASSE_IMPRESSAO_EXISTENTE);
			}

			getImpClasseImpressaoDAO().persistir(impClasseImpressao);
			getImpClasseImpressaoDAO().flush();

		} else { // Alteracao
			if (isClasseImpressaoExistente(impClasseImpressao.getId(),
					impClasseImpressao.getClasseImpressao())) {
				throw new ApplicationBusinessException(
						ClasseImpressaoONExceptionCode.CLASSE_IMPRESSAO_INEXISTENTE);
			}

			getImpClasseImpressaoDAO().merge(impClasseImpressao);
			getImpClasseImpressaoDAO().flush();
		}
	}

	/**
	 * Método definido para verificar a existência de uma classeImpressão
	 * 
	 * @param id
	 *            , classeImpressao e tipoImpressora
	 * @return boolean
	 */
	private boolean isClasseImpressaoExistente(Integer idClasseImpressao,
			String classeImpressao) {

		return getImpClasseImpressaoDAO().isClasseImpressaoExistente(
				idClasseImpressao, classeImpressao);
	}

	/**
	 * Pesquisar Classe Impressão.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<ImpClasseImpressao> pesquisarClasseImpressao(
			Object paramPesquisa) {
		return getImpClasseImpressaoDAO().pesquisarClasseImpressaoPorDescricao(
				paramPesquisa);
	}
}
