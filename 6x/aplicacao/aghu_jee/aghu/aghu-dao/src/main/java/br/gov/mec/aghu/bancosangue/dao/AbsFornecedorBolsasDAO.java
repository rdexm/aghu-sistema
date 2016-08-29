package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsFornecedorBolsas;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AbsFornecedorBolsasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsFornecedorBolsas>{


	
	private static final long serialVersionUID = 985348659247893679L;

	public List<AbsFornecedorBolsas> pesquisarFornecedor(Object obj) {
		DetachedCriteria criteria = montaCriteriaPequisaFornecedor(obj); 
		return this.executeCriteria(criteria);
	}

	public Long pesquisarFornecedorCount(Object obj) {
		DetachedCriteria criteria = montaCriteriaPequisaFornecedor(obj); 
		return this.executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria montaCriteriaPequisaFornecedor(Object obj) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsFornecedorBolsas.class);
		String strPesquisa = (String) obj;
		if(StringUtils.isNotBlank(strPesquisa)){
			if(CoreUtil.isNumeroInteger(strPesquisa)){
				criteria.add(Restrictions.eq(AbsFornecedorBolsas.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(AbsFornecedorBolsas.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(AbsFornecedorBolsas.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria; 
	}
	
	/**
	 * Obtem os Fornecedor BOlsa pelo codigo informado
	 * 
	 * @return AbsFornecedorBolsas
	 */
	public AbsFornecedorBolsas obterComponeteFornecedorBolsasPorCodigo(Integer codigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AbsFornecedorBolsas.class);
		criteria.add(Restrictions.eq(AbsFornecedorBolsas.Fields.SEQ.toString(), codigo));
		return (AbsFornecedorBolsas) executeCriteriaUniqueResult(criteria);
	}

}
