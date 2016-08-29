package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.paciente.dao.AipBairrosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe de negócio do módulo de pacientes responsável pelos métodos negociais
 * relativos ao cadastro de Bairros
 */
@Stateless
public class BairroCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(BairroCRUD.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AipBairrosDAO aipBairrosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8680299675048463933L;

	public enum BairroExceptionCode implements BusinessExceptionCode {
		ERRO_REMOVER_BAIRRO_BAIRRO_CEP_LOGRADOURO_DEPENDENTE, ERRO_PERSISTIR_BAIRRO_JA_EXISTENTE;
	}

	/**
	 * Método responsável pela persistência e atualização de um bairro.
	 * 
	 * @param aipBairro
	 * @throws ApplicationBusinessException
	 */
	public void persistirBairro(AipBairros aipBairro)
			throws ApplicationBusinessException {
		final String descricao = aipBairro.getDescricao();
		aipBairro.setDescricao(descricao.trim().toUpperCase());

		AipBairros bairro = getAipBairrosDAO().obterBairroPelaDescricaoExata(
				aipBairro.getDescricao());
		if (bairro != null && !bairro.getCodigo().equals(aipBairro.getCodigo())) {
			throw new ApplicationBusinessException(
					BairroExceptionCode.ERRO_PERSISTIR_BAIRRO_JA_EXISTENTE);
		}

		if (aipBairro.getCodigo() == null) {
			// inclusão
			this.incluirBairro(aipBairro);
		} else {
			// edição
			this.atualizarBairro(aipBairro);
		}
	}

	/**
	 * Método responsável por incluir um bairro novo.
	 * 
	 * @param bairro
	 * @throws ApplicationBusinessException
	 */
	private void incluirBairro(AipBairros aipBairro) {
		getAipBairrosDAO().persistir(aipBairro);
	}

	/**
	 * Método responsável por atualizar um bairro existente.
	 * 
	 * @param bairro
	 * @throws ApplicationBusinessException
	 */
	private void atualizarBairro(AipBairros aipBairro)
			throws ApplicationBusinessException {
		getAipBairrosDAO().atualizar(aipBairro);
		getAipBairrosDAO().flush();
	}

	/**
	 * Exclui um objeto aipBairro
	 * 
	 * @param aipBairro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void excluirBairro(Integer aipBairroCodigo)
			throws ApplicationBusinessException {
		AipBairrosDAO aipBairrosDAO = getAipBairrosDAO();
		AipBairros aipBairro = aipBairrosDAO
				.obterPorChavePrimaria(aipBairroCodigo);
		if (!aipBairro.getAipBairrosCepLogradouros().isEmpty()) {
			throw new ApplicationBusinessException(
					BairroExceptionCode.ERRO_REMOVER_BAIRRO_BAIRRO_CEP_LOGRADOURO_DEPENDENTE);
		}
		aipBairrosDAO.remover(aipBairro);
	}

	protected AipBairrosDAO getAipBairrosDAO() {
		return aipBairrosDAO;
	}
}