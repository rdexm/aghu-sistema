package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de tipo de unidade funcional.
 */
// 
// 
@Stateless
public class TiposUnidadeFuncionalCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TiposUnidadeFuncionalCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4681321193401510309L;

	private enum TiposUnidadeFuncionalCRUDExceptionCode implements
			BusinessExceptionCode {
		ERRO_PERSISTIR_TIPOSUNIDADEFUNCIONAL
		, ERRO_REMOVER_TIPOSUNIDADEFUNCIONAL
		, ERRO_REMOVER_TIPOSUNIDADEFUNCIONAL_COM_DEPENDENTE
		, DESCRICAO_TIPOSUNIDADEFUNCIONAL_OBRIGATORIO
		, DESCRICAO_TIPOSUNIDADEFUNCIONAL_JA_EXISTENTE;
	}
	
	public Long pesquisaTiposUnidadeFuncionalCount(Integer codigoPesquisaTiposUnidadeFuncional, String descricaoPesquisaTiposUnidadeFuncional) {
		return getAghuFacade().pesquisaTiposUnidadeFuncionalCount(codigoPesquisaTiposUnidadeFuncional, descricaoPesquisaTiposUnidadeFuncional);
	}
	
	public List<AghTiposUnidadeFuncional> pesquisaTiposUnidadeFuncional(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigoPesquisaTiposUnidadeFuncional,
			String descricaoPesquisaTiposUnidadeFuncional) {
		return getAghuFacade().pesquisaTiposUnidadeFuncional(firstResult, maxResults, orderProperty, asc, codigoPesquisaTiposUnidadeFuncional, descricaoPesquisaTiposUnidadeFuncional);
	}
	
	/**
	 * @dbtables AghTiposUnidadeFuncional select
	 * @param paramPesquisa
	 * @return
	 */
	public List<AghTiposUnidadeFuncional> listarPorNomeOuCodigo(String strPesquisa) {
		return getAghuFacade().listarPorNomeOuCodigo(strPesquisa);
	}

	/**
	 * Método responsável pela persistência de um tipo de unidade funcional.
	 * 
	 * @param tipo
	 *            de unidade funcional
	 * @throws ApplicationBusinessException
	 */
	
	public void persistirTiposUnidadeFuncional(
			AghTiposUnidadeFuncional tipoUnidadeFuncional)
			throws ApplicationBusinessException {
		if (tipoUnidadeFuncional.getCodigo() == null) {
			// inclusão
			this.incluirTiposUnidadeFuncional(tipoUnidadeFuncional);
		} else {
			// edição
			this.atualizarTiposUnidadeFuncional(tipoUnidadeFuncional);
		}
	}

	/**
	 * Método responsável por incluir um novo tipo de unidade funcional.
	 * 
	 * @param tipo
	 *            de unidade funcional
	 * @throws ApplicationBusinessException
	 */
	
	private void incluirTiposUnidadeFuncional(AghTiposUnidadeFuncional tipoUnidadeFuncional) throws ApplicationBusinessException {
		this.validarDadosTiposUnidadeFuncional(tipoUnidadeFuncional);
		
		getAghuFacade().inserirAghTiposUnidadeFuncional(tipoUnidadeFuncional, true);
	}

	/**
	 * Método responsável pela atualização de um tipo de unidade funcional.
	 * 
	 * @param tipo
	 *            de unidade funcional
	 * @throws ApplicationBusinessException
	 */
	
	private void atualizarTiposUnidadeFuncional(
			AghTiposUnidadeFuncional tipoUnidadeFuncional)
			throws ApplicationBusinessException {
		this.validarDadosTiposUnidadeFuncional(tipoUnidadeFuncional);

		try {
			getAghuFacade().atualizarAghTiposUnidadeFuncionalDepreciado(tipoUnidadeFuncional);

		} catch (Exception e) {
			logError("Erro ao atualizar o tipo de unidade funcional.",
					e);
			throw new ApplicationBusinessException(
					TiposUnidadeFuncionalCRUDExceptionCode.ERRO_PERSISTIR_TIPOSUNIDADEFUNCIONAL);
		}
	}

	/**
	 * Método responsável pelas validações dos dados de tipo de unidade
	 * funcional. Método utilizado para inclusão e atualização de tipo de
	 * unidade funcional.
	 * 
	 * @param tipo
	 *            de unidade funcional
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosTiposUnidadeFuncional(
			AghTiposUnidadeFuncional tipoUnidadeFuncional)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(tipoUnidadeFuncional.getDescricao())) {
			throw new ApplicationBusinessException(
					TiposUnidadeFuncionalCRUDExceptionCode.DESCRICAO_TIPOSUNIDADEFUNCIONAL_OBRIGATORIO);
		}
	}

	/**
	 * Apaga um tipo de unidade funcional do banco de dados.
	 * 
	 * @param tipo
	 *            de unidade funcional Tipo de unidade funcional a ser removida.
	 * @throws ApplicationBusinessException
	 */
	public void removerTiposUnidadeFuncional(
			Integer codigoTipoUnidFunc)
			throws ApplicationBusinessException {
		try {
			
			AghTiposUnidadeFuncional tipoUnidadeFuncional = this.getAghuFacade().obterTiposUnidadeFuncional(codigoTipoUnidFunc);
			
			if (getAghuFacade()
					.validarTipoUnidadeFuncionalComUnidadeFuncional(
							tipoUnidadeFuncional)) {
				getAghuFacade().removerAghTiposUnidadeFuncional(tipoUnidadeFuncional);

			} else {
				throw new ApplicationBusinessException(
						TiposUnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_TIPOSUNIDADEFUNCIONAL_COM_DEPENDENTE);
			}

		} catch (ApplicationBusinessException ex) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw ex;// Relancando a ApplicationBusinessException, senão o throws
						// presente no else cairía no catch exception, não
						// apresentando a mensagem correta.
		} catch (Exception e) {
			logError("Erro ao remover o tipo de unidade funcional.", e);
			throw new ApplicationBusinessException(
					TiposUnidadeFuncionalCRUDExceptionCode.ERRO_REMOVER_TIPOSUNIDADEFUNCIONAL);
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

}
