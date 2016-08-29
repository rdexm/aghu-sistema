/**
 * 
 */
package br.gov.mec.aghu.estoque.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.SceCfop;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * @author joao.pan
 *
 */
@Stateless
public class SceCfopON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4428002521955694954L;
	
	@Inject
	private SceCfopRN cfopRN;

	@Override
	@Deprecated
	protected Log getLogger() {
		return null;
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
		return this.getCfopRN().pesquisarListaCFOP(firstResult, maxResult,
				orderProperty, asc, cfop);
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
		return this.getCfopRN().pesquisarListaCFOPCount(cfop);
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
		return this.getCfopRN().pesquisarCFOP(codigoCfop);
	}

	/**
	 * Método que insere um novo {@link SceCfop}.
	 * 
	 * @param cfop
	 *            {@link SceCfop} a ser inserido.
	 * @throws ApplicationBusinessException
	 */
	public void inserirCFOP(SceCfop cfop) throws ApplicationBusinessException {
		this.getCfopRN().inserirCFOP(cfop);
	}

	/**
	 * Método de atualização do {@link SceCfop}.
	 * 
	 * @param cfop
	 *            Objeto da classe.
	 * @throws BaseException
	 */
	public void atualizarSCFOP(SceCfop cfop) throws BaseException {
		this.getCfopRN().atualizarSCFOP(cfop);
	}

	/**
	 * Método de exclusão do {@link SceCfop}.
	 * 
	 * @param cfop
	 *            Objeto da classe
	 * @throws BaseException
	 */
	public void excluirCFOP(SceCfop cfop) throws BaseException {
		this.getCfopRN().excluirCFOP(cfop);
	}

	/**
	 * @return the cfopRN
	 */
	private SceCfopRN getCfopRN() {
		return cfopRN;
	}

}
