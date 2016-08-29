package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FcpBanco;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FcpBancoDAO extends BaseDao<FcpBanco> {

	private static final int MAX_RESULT_SUGGESTION_BOX = 100;
	private static final long serialVersionUID = 7857418208613109173L;

	/**
	 * Restrições da listagem de bancos.
	 * @param fcpBanco
	 * @return
	 */
	private DetachedCriteria createPesquisarCriteria(FcpBanco fcpBanco) {
		// Tabela
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpBanco.class);
		// Código do banco
		Short codigoBanco = fcpBanco.getCodigo();
		// Nome do banco
		String nomeBanco = fcpBanco.getNome();
		// Where - Código de banco
		if(codigoBanco != null) {
			criteria.add(Restrictions.eq(FcpBanco.Fields.CODIGO.toString(), codigoBanco));
		}
		// Where - Nome de banco
		if(nomeBanco != null && !nomeBanco.isEmpty()) {
			criteria.add(Restrictions.ilike(FcpBanco.Fields.NOME.toString(), nomeBanco, MatchMode.ANYWHERE));
		}
		return criteria;
	}
	
	/**
	 * Restrições da listagem de bancos por código ou descrição.
	 * @param fcpBanco
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisarCriteriaCodigoOuDescricao(String parametro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpBanco.class);
		if(CoreUtil.isNumeroShort(parametro)) {
			criteria.add(Restrictions.eq(FcpBanco.Fields.CODIGO.toString(), Short.parseShort(parametro)));
		} else {
			if(StringUtils.isNotBlank(parametro)){
				criteria.add(Restrictions.ilike(FcpBanco.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}
	
	/**
	 * Contador de bancos.
	 * @param fcpBanco
	 * @return
	 */
	public Long pesquisar(FcpBanco fcpBanco) {
		return executeCriteriaCount(createPesquisarCriteria(fcpBanco));
	}
	
	/**
	 * Listagem de bancos.
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param fcpBanco
	 * @return
	 */
	public List<FcpBanco>  pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpBanco fcpBanco) {
		return executeCriteria(createPesquisarCriteria(fcpBanco), firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Listagem de bancos por código ou descrição.
	 * @param fcpBanco
	 * @return
	 */
	public List<FcpBanco> pesquisarCodigoDescricao(String parametro) {
		return executeCriteria(obterCriteriaPesquisarCriteriaCodigoOuDescricao(parametro), 0, MAX_RESULT_SUGGESTION_BOX, FcpBanco.Fields.NOME.toString(), true);
	}
	
	/**
	 * Contador de bancos por código ou descrição.
	 * @param fcpBanco
	 * @return
	 */
	public Long pesquisarCodigoDescricaoCount(String parametro) {
		return executeCriteriaCount(obterCriteriaPesquisarCriteriaCodigoOuDescricao(parametro));
	}
	
	/**
	 * Pesquisar banco por chave primária.
	 * @param fcpBanco
	 * @return
	 */
	public FcpBanco pesquisarCodigo(Short codigoBanco) {
		return obterPorChavePrimaria(codigoBanco);
	}
	
	/**
	 * Remover banco.
	 * @param codigoBanco
	 */
	public void excluir(Short codigoBanco) throws BaseException {
		removerPorId(codigoBanco);
	}
	
	/**
	 * Alterar banco.
	 * @param fcpBanco
	 */
	public void alterar(FcpBanco fcpBanco) {
		atualizar(fcpBanco);
	}
	
	/**
	 * Inserir banco.
	 * @param fcpBanco
	 */
	public void incluir(FcpBanco fcpBanco) {
		persistir(fcpBanco);
	}
	
}