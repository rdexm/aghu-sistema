package br.gov.mec.aghu.cups.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.configuracao.dao.ImpImpressoraDAO;
import br.gov.mec.aghu.dominio.DominioTipoImpressoraCups;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe provedora dos metodos de negócio para o cadastro de impressoras.
 * 
 * @author Heliz
 */
@Stateless
public class ImpressoraON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ImpressoraON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ImpImpressoraDAO impImpressoraDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5148365676779012984L;

	private enum ImpressoraONExceptionCode implements BusinessExceptionCode {
		PARAMETRO_NAO_INFORMADO, REGISTRO_NULO_EXCLUSAO, VIOLACAO_FK_IMPRESSORA, IMPRESSORA_NAO_ENCONTRADA, IMPRESSORA_NAO_INFORMADA, IMPRESSORA_EXISTENTE, IMPRESSORA_INEXISTENTE, IMPRESSORA_INCOMPATIVEL, IMPRESSORA_UTILIZADA
	};

	protected ImpImpressoraDAO getImpImpressoraDAO() {
		return impImpressoraDAO;
	}

	/**
	 * Pesquisar Impressora.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filaImpressora
	 * @param tipoImpressora
	 * @param descricao
	 * @return
	 */
	public List<ImpImpressora> pesquisarImpressora(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String filaImpressora, DominioTipoImpressoraCups tipoImpressora,
			String descricao) {

		return getImpImpressoraDAO().pesquisarImpressora(firstResult,
				maxResult, orderProperty, asc, filaImpressora, tipoImpressora,
				descricao);
	}

	/**
	 * Pesquisar Impressora count.
	 * 
	 * 
	 * @param filaImpressora
	 * @param tipoImpressora
	 * @param descricao
	 * @return
	 */
	public Long pesquisarImpressoraCount(String filaImpressora,
			DominioTipoImpressoraCups tipoImpressora, String descricao) {

		return getImpImpressoraDAO().pesquisarImpressoraCount(filaImpressora,
				tipoImpressora, descricao);
	}

	/**
	 * Gravar Impressora.
	 * 
	 * @param impImpressora
	 * @param tipoImpressoraAnt
	 * @throws ApplicationBusinessException
	 */
	public void gravarImpressora(ImpImpressora impImpressora,
			DominioTipoImpressoraCups tipoImpressoraAnt)
			throws ApplicationBusinessException {

		if (impImpressora == null) {
			throw new ApplicationBusinessException(
					ImpressoraONExceptionCode.IMPRESSORA_NAO_INFORMADA);
		}

		if (impImpressora.getId() != null) {

			// Compara o valor antigo com o valor digitado
			if (tipoImpressoraAnt != impImpressora.getTipoImpressora()) {
				List<ImpImpressora> listaRedirecionamento = getImpImpressoraDAO()
						.verificaRedirecionamento(impImpressora.getId());
				// Se a impressora for utilizada como redirecionamento, não pode
				// alterar o tipo
				if (listaRedirecionamento.isEmpty() == false) {
					throw new ApplicationBusinessException(
							ImpressoraONExceptionCode.IMPRESSORA_UTILIZADA);
				}

			}

		}

		// Verifica se o redirecionamento é compatível com o tipo de impressora
		// selecionado
		if (impImpressora.getImpRedireciona() != null) {
			List<ImpImpressora> listaImpressora = getImpImpressoraDAO()
					.obterImpressoraRedirecionamento(
							impImpressora,
							impImpressora.getImpRedireciona()
									.getFilaImpressora());
			Boolean encontrou = false;
			for (ImpImpressora imp : listaImpressora) {
				if (imp.getId().equals(
						impImpressora.getImpRedireciona().getId())) {
					encontrou = true;
				}
			}
			if (!encontrou) {
				throw new ApplicationBusinessException(
						ImpressoraONExceptionCode.IMPRESSORA_INCOMPATIVEL);
			}
		}

		if (impImpressora.getId() == null) { // Inclusao

			if (getImpImpressoraDAO().isImpressoraExistente(null,
					impImpressora.getFilaImpressora(),
					impImpressora.getTipoImpressora())) {
				throw new ApplicationBusinessException(
						ImpressoraONExceptionCode.IMPRESSORA_EXISTENTE);
			}
			getImpImpressoraDAO().persistir(impImpressora);
			getImpImpressoraDAO().flush();

		} else { // Alteracao

			if (getImpImpressoraDAO().isImpressoraExistente(
					impImpressora.getId(), impImpressora.getFilaImpressora(),
					impImpressora.getTipoImpressora())) {
				throw new ApplicationBusinessException(
						ImpressoraONExceptionCode.IMPRESSORA_INEXISTENTE);
			}
			impImpressora = getImpImpressoraDAO().merge(impImpressora);
			getImpImpressoraDAO().flush();
		}

		impImpressora = null;
	}

	/**
	 * 
	 * @return
	 */
	public void excluirImpressora(int idImpressora) throws ApplicationBusinessException {		
		try {
			
			ImpImpressora impImpressora = obterImpressora(idImpressora);
			
			getImpImpressoraDAO().remover(impImpressora);
			getImpImpressoraDAO().flush();
		} catch (ApplicationBusinessException e){
			LOG.error(e.getMessage(),e);
			throw new ApplicationBusinessException(ImpressoraONExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}catch (PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new ApplicationBusinessException(
						ImpressoraONExceptionCode.VIOLACAO_FK_IMPRESSORA,
						cve.getConstraintName());
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public ImpImpressora obterImpressora(Integer idImpressora)
			throws ApplicationBusinessException {
		if (idImpressora == null) {
			throw new ApplicationBusinessException(
					ImpressoraONExceptionCode.PARAMETRO_NAO_INFORMADO);
		}

		ImpImpressora impImpressora = getImpImpressoraDAO()
				.obterPorChavePrimaria(idImpressora, true, ImpImpressora.Fields.IMPRESSORA_REDIRECIONAMENTO, ImpImpressora.Fields.SERVIDOR_CUPS);

		if (impImpressora == null) {
			throw new ApplicationBusinessException(
					ImpressoraONExceptionCode.IMPRESSORA_NAO_ENCONTRADA);
		}

		return impImpressora;
	}

	/**
	 * Pesquisar impressora por fila.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<ImpImpressora> pesquisarImpressoraPorFila(Object paramPesquisa) {
		return getImpImpressoraDAO().pesquisarImpressoraPorFila(paramPesquisa);
	}

	/**
	 * Pesquisar impressora.
	 * 
	 * @param paramPesquisa
	 * @param soCodBarras - informa se deve pesquisar somente impressoras de código de barras
	 * @return
	 */
	public List<ImpImpressora> pesquisarImpressora(Object paramPesquisa, boolean soCodBarras) {
		return getImpImpressoraDAO().pesquisarImpressora(paramPesquisa, soCodBarras);
	}
	
	/**
	 * Pesquisar impressora.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<ImpImpressora> pesquisarImpressora(Object paramPesquisa) {
		return getImpImpressoraDAO().pesquisarImpressora(paramPesquisa);
	}

	/**
	 * Impressora Redirecionamento.
	 * 
	 * @param impImpressora
	 * @param strPesquisa
	 * @return
	 */
	public List<ImpImpressora> obterImpressoraRedirecionamento(
			ImpImpressora impImpressora, String strPesquisa) {
		return getImpImpressoraDAO().obterImpressoraRedirecionamento(
				impImpressora, strPesquisa);
	}
}
