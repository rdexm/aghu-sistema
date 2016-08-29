package br.gov.mec.aghu.estoque.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.estoque.dao.SceCfopDAO;
import br.gov.mec.aghu.model.SceCfop;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class SceCfopRN extends BaseBusiness {

	/** Identificador único */
	private static final long serialVersionUID = -4733685123431026037L;

	@Inject
	private SceCfopDAO cfopDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return null;
	}

	private enum SceCfopExceptionCode implements BusinessExceptionCode {
		ESTE_CFOP_JA_EXISTE;
	}

	/**
	 * Método de pesquisa de {@link SceCfop}.
	 *
	 * @param firstResult
	 *            Indicação do primeiro registro.
	 * @param maxResult
	 *            Indicação do último registro.
	 * @param orderProperty
	 * @param asc
	 *            Indica ordenação: ascendente(true) ou descendente(false).
	 * @param cfop
	 *            Objeto da classe.
	 * @return {@link List} populada com os objetos.
	 * @throws BaseException
	 */
	public List<SceCfop> pesquisarListaCFOP(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, SceCfop cfop)
			throws BaseException {
		return this.getCfopDAO().pesquisarSceCfopList(cfop, firstResult,
				maxResult, orderProperty, asc);
	}

	/**
	 * Obtém o paginador da lista de {@link SceCfop}.
	 * 
	 * @param cfop
	 *            Objeto da classe.
	 * @return {@link Long} que representa a paginação da lista do método
	 *         pesquisar.
	 * @throws BaseException
	 */
	public Long pesquisarListaCFOPCount(SceCfop cfop) throws BaseException {
		return this.getCfopDAO().pesquisarSceCfopCount(cfop);
	}

	/**
	 * Método que busca por uma {@link SceCfop} baseado no código.
	 * 
	 * @param codigoCfop
	 *            Código da {@link SceCfop}.
	 * @return {@link SceCfop} especificada.
	 * @throws BaseException
	 */
	public SceCfop pesquisarCFOP(Short codigoCfop) throws BaseException {
		return this.getCfopDAO().pesquisarCodigo(codigoCfop);
	}

	/**
	 * Método que insere um novo {@link SceCfop}.
	 * 
	 * @param cfop
	 *            {@link SceCfop} a ser inserido.
	 * @throws ApplicationBusinessException
	 */
	public void inserirCFOP(SceCfop cfop) throws ApplicationBusinessException {

		SceCfop cfopVerificar = this.getCfopDAO().pesquisarCodigo(
				cfop.getCodigo());

		if (cfopVerificar != null) {
			throw new ApplicationBusinessException(
					SceCfopExceptionCode.ESTE_CFOP_JA_EXISTE, Severity.ERROR,
					cfopVerificar.getCodigo());
		} else {
			this.getCfopDAO().incluir(cfop);
		}
	}

	/**
	 * Método de atualização do {@link SceCfop}.
	 * 
	 * @param cfop
	 *            Objeto da classe.
	 */
	public void atualizarSCFOP(SceCfop cfop) throws ApplicationBusinessException {
		
		this.getCfopDAO().alterar(cfop);
		this.getCfopDAO().flush();
	}

	/**
	 * Método de exclusão do {@link SceCfop}.
	 * 
	 * @param cfop
	 *            Objeto da classe
	 * @throws BaseException
	 */
	public void excluirCFOP(SceCfop cfop) throws BaseException {
		this.getCfopDAO().excluir(cfop.getCodigo());
	}

	
	public SceCfopDAO getCfopDAO() {
		return cfopDAO;
	}
}
