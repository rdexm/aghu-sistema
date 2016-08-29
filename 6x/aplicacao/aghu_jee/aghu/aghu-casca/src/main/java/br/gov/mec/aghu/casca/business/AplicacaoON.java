package br.gov.mec.aghu.casca.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.casca.dao.AplicacaoDAO;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AplicacaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AplicacaoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AplicacaoDAO aplicacaoDAO;

	private static final long serialVersionUID = 2657551899265385485L;

	public enum AplicacaoONExceptionCode implements BusinessExceptionCode {
		CASCA_MENSAGEM_APLICACAO_NAO_INFORMADA, CASCA_MENSAGEM_APLICACAO_JA_CADASTRADA, 
		CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO, CASCA_VIOLACAO_FK_APLICACAO, 
		CASCA_MENSAGEM_APLICACAO_SEM_CONTEXTO, CASCA_MENSAGEM_APLICACAO_SEM_SERVIDOR, 
		CASCA_MENSAGEM_APLICACAO_SEM_NOME; 
	}

	/**
	 * 
	 * @param nome
	 * @return
	 */
	public List<Aplicacao> pesquisarAplicacaoPorNome(Object nome) {
		return aplicacaoDAO.pesquisarAplicacaoPorNome(nome);
	}

	/**
	 * 
	 * @param aplicacao
	 * @throws ApplicationBusinessException
	 */
	public void salvar(Aplicacao aplicacao) throws ApplicationBusinessException {

		if (aplicacao == null) {
			throw new ApplicationBusinessException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_NAO_INFORMADA);
		}

		validarAplicacao(aplicacao);

		if (aplicacao.getId() == null) {
			List<Aplicacao> lista = null;
			if (aplicacao.getExterno()) {
				lista = aplicacaoDAO.pesquisarAplicacoes(
						aplicacao.getServidor(), aplicacao.getPorta(),
						aplicacao.getContexto(), null, null);
			} else {
				// aplicacoes internas nao tem servidor, porta ou contexto
				lista = aplicacaoDAO.pesquisarAplicacoes(null, null, null,
						aplicacao.getNome(), aplicacao.getExterno());
			}

			if (!lista.isEmpty()) {
				throw new ApplicationBusinessException(
						AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_JA_CADASTRADA);
			}
			aplicacaoDAO.persistir(aplicacao);
			aplicacaoDAO.flush();
		} else {
			aplicacaoDAO.atualizar(aplicacao);
			aplicacaoDAO.flush();
		}
	}

	private void validarAplicacao(Aplicacao aplicacao)
			throws ApplicationBusinessException {
		if (StringUtils.isEmpty(aplicacao.getNome())) {
			throw new ApplicationBusinessException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_SEM_NOME);
		}
		
		if (!aplicacao.getExterno()) {
			return;
		}

		if (StringUtils.isEmpty(aplicacao.getContexto())) {
			throw new ApplicationBusinessException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_SEM_CONTEXTO);
		}

		if (StringUtils.isEmpty(aplicacao.getServidor())) {
			throw new ApplicationBusinessException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_APLICACAO_SEM_SERVIDOR);
		}

	}

	/**
	 * 
	 * @param idAplicacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Aplicacao obterAplicacao(Integer idAplicacao) {
		if (idAplicacao == null) {
			throw new IllegalArgumentException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO.toString());
		}

		return aplicacaoDAO.obterPorChavePrimaria(idAplicacao);
	}
	
	/**
	 * 
	 * @param idAplicacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Aplicacao obterAplicacaoParaExclusao(Integer idAplicacao) throws ApplicationBusinessException {
		if (idAplicacao == null) {
			throw new ApplicationBusinessException(
					AplicacaoONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		return aplicacaoDAO.obterPorChavePrimaria(idAplicacao);
	}


	
	public Aplicacao obterAplicacaoPorContexto(final String nomeContexto, String servidor) {
		return aplicacaoDAO.obterAplicacaoPorContexto(nomeContexto, servidor);
	}	

	/**
	 * 
	 * @param idAplicacao
	 * @throws ApplicationBusinessException
	 */

	public void excluirAplicacao(Integer idAplicacao)
			throws ApplicationBusinessException {

		Aplicacao aplicacao = obterAplicacaoParaExclusao(idAplicacao);
		try {
			aplicacaoDAO.remover(aplicacao);
			aplicacaoDAO.flush();
		} catch (PersistenceException ce) {
			logError("Exceção capturada: ", ce);
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new ApplicationBusinessException(
						AplicacaoONExceptionCode.CASCA_VIOLACAO_FK_APLICACAO,
						cve.getConstraintName());
			}
		}
	}
}