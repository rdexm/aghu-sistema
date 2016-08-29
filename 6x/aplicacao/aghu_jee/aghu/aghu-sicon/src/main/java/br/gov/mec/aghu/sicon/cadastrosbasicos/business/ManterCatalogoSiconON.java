package br.gov.mec.aghu.sicon.cadastrosbasicos.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCatalogoSicon;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.dao.ScoCatalogoSiconDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterCatalogoSiconON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterCatalogoSiconON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private ScoCatalogoSiconDAO scoCatalogoSiconDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3923703792142417970L;

	public enum ManterCatalogoSiconONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG, 
		MENSAGEM_CODIGO_DUPLICADO, 
		MENSAGEM_ERRO_HIBERNATE_VALIDATION, 
		MENSAGEM_ERRO_PERSISTIR_DADOS,

	}

	/**
	 * Insere um novo registro na tabela {@code ScoCatalogoSicon}
	 * 
	 * @param _catalogoSicon
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserir(ScoCatalogoSicon _catalogoSicon)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (_catalogoSicon == null) {
			throw new ApplicationBusinessException(
					ManterCatalogoSiconONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		// RN01 - garante código único para item do catálogo
		if (verificaCodigoUnico(_catalogoSicon) == false) {
			throw new ApplicationBusinessException(
					ManterCatalogoSiconONExceptionCode.MENSAGEM_CODIGO_DUPLICADO);
		}

		_catalogoSicon.setCriadoEm(new Date());
		_catalogoSicon.setServidor(servidorLogado);

		try {
			this.getScoCatalogoSiconDAO().persistir(_catalogoSicon);
			this.getScoCatalogoSiconDAO().flush();

		} catch (ConstraintViolationException ise) {
			String mensagem = " Valor inválido para o campo ";
			throw new ApplicationBusinessException(
					ManterCatalogoSiconONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);

		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ManterCatalogoSiconONExceptionCode.MENSAGEM_ERRO_PERSISTIR_DADOS,
					e.getMessage());
		}

	}

	/**
	 * Atualiza um registro de {@code ScoCatalogoSicon}
	 * 
	 * @param _catalogoSicon
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void alterar(ScoCatalogoSicon _catalogoSicon)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (_catalogoSicon == null) {
			throw new ApplicationBusinessException(
					ManterCatalogoSiconONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		// RN01 - garante código único para item do catálogo
		// if (verificaCodigoUnico(_catalogoSicon) == false) {
		// throw new ApplicationBusinessException(
		// ManterCatalogoSiconONExceptionCode.MENSAGEM_CODIGO_DUPLICADO);
		// }

		_catalogoSicon.setAlteradoEm(new Date());
		_catalogoSicon.setServidor(servidorLogado);
		
		try {
			this.getScoCatalogoSiconDAO().merge(_catalogoSicon);
		} catch (ConstraintViolationException ise) {
			String mensagem = " Valor inválido para o campo ";
			throw new ApplicationBusinessException(
					ManterCatalogoSiconONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);

		}
	}

	/**
	 * Verifica se o código sicon do item de catálogo é um valor único na tabela
	 * {@code ScoCatalogoSicon}. Verificação válida apenas para registros ativos
	 * (situação == ativo).
	 * 
	 * @param _catalogoSicon
	 * @return
	 */
	public boolean verificaCodigoUnico(ScoCatalogoSicon _catalogoSicon) {

		Long listaItemCatalogo = this.getScoCatalogoSiconDAO()
				.pesquisarCatalogoSiconCount(_catalogoSicon.getCodigoSicon(),
						null, null, null);

		if (listaItemCatalogo > 0) {
			return false;
		}

		return true;
	}

	/**
	 * Instancia uma DAO para {@code ScoCatalogoSiconDAO}
	 * 
	 * @return Nova instância de {@code ScoCatalogoSiconDAO}
	 */
	protected ScoCatalogoSiconDAO getScoCatalogoSiconDAO() {
		return scoCatalogoSiconDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
