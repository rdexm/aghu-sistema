package br.gov.mec.aghu.casca.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.Modulo;


public class ModuloDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<Modulo>{

	private static final long serialVersionUID = -7325233182967256714L;

	public Long listarModulosCount(String strPesquisa) {
		DetachedCriteria criteria = criarListaModulosCriteria(strPesquisa);
		return executeCriteriaCount(criteria);
	}

	public List<Modulo> listarModulos(String strPesquisa) {
		DetachedCriteria criteria = criarListaModulosCriteria(strPesquisa);
		return executeCriteria(criteria,true);
	}

	private DetachedCriteria criarListaModulosCriteria(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Modulo.class);
		if (StringUtils.isNotBlank(strPesquisa)){
			Criterion descricaoCriterion = Restrictions.ilike(Modulo.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE);
			if (StringUtils.isNumeric(strPesquisa)) {
				criteria.add(Restrictions.or(descricaoCriterion,
						Restrictions.eq(Modulo.Fields.ID.toString(), Integer.valueOf(strPesquisa))));
			} else {
				criteria.add(descricaoCriterion);
			}
		}	
		return criteria;
	}

	public List<Modulo> listarModulosAtivos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Modulo.class);
		criteria.add(Restrictions.eq(Modulo.Fields.ATIVO.toString(), Boolean.TRUE));
		return this.executeCriteria(criteria,true);
	}
	
	public boolean verificarSeModuloEstaAtivo(String nomeModulo){
		DetachedCriteria criteria = DetachedCriteria.forClass(Modulo.class);
		criteria.add(Restrictions.eq(Modulo.Fields.ATIVO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(Modulo.Fields.NOME.toString(), nomeModulo));
		Modulo retorno = (Modulo) this.executeCriteriaUniqueResult(criteria,true); 
		if(retorno != null && retorno.getId() != null){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Número de módulos do sistema
	 * 
	 * @return
	 */
	public Long listarModulosCount() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Modulo.class);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Lista todos os módulos do sistema
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<Modulo> listarModulos(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Modulo.class);
		if(orderProperty == null){
			orderProperty = Modulo.Fields.NOME.toString();
			asc = true;
		}
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}
	
	/**
	 * Inverte a situação de um módulo
	 * 
	 * @param idModulo
	 */
	public void alterarSituacao(Integer idModulo, Boolean situacaoModulo){
		Modulo modulo = obterPorChavePrimaria(idModulo);
		modulo.setAtivo(situacaoModulo);		
		this.atualizar(modulo);
	}
	
	/**
	 * @param nomeModulo Nome de um módulo a ser pesquisado
	 * @return Lista de módulos pesquisados por nomeModulo
	 */
	public List<Modulo> listarModulosPorNome(String nomeModulo){
		if (nomeModulo == null) {
			return new ArrayList<Modulo>();
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(Modulo.class);
		criteria.add(Restrictions.eq(Modulo.Fields.NOME.toString(), nomeModulo));
		return executeCriteria(criteria,true);
	}
	
}
