package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VMamMedicamentos;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class VMamMedicamentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMamMedicamentos>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2323733684932588819L;
	/**
	 * #41635 Consulta sugestionbox SB1 tela de receitas 
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public List<VMamMedicamentos> obterMedicamentosReceitaPorDescricaoOuCodigo(String descricaoCodigo){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(VMamMedicamentos.class);
		Integer codigo = null;
		if(CoreUtil.isNumeroInteger(descricaoCodigo)){
			codigo = Integer.parseInt(descricaoCodigo);
		}
		criteria.add(Restrictions.or(
				Restrictions.eq(VMamMedicamentos.Fields.MAT_CODIGO.toString(),codigo),
				Restrictions.ilike(VMamMedicamentos.Fields.DESCRICAO.toString(),StringUtils.lowerCase(descricaoCodigo),MatchMode.ANYWHERE)));
		criteria.addOrder(Order.asc(VMamMedicamentos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long obterMedicamentosReceitaPorDescricaoOuCodigoCount(String descricaoCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMamMedicamentos.class);
		Integer codigo = null;
		if (CoreUtil.isNumeroInteger(descricaoCodigo)) {
			codigo = Integer.parseInt(descricaoCodigo);
		}
		criteria.add(Restrictions.or(
				Restrictions.eq(VMamMedicamentos.Fields.MAT_CODIGO.toString(),codigo),
				Restrictions.ilike(VMamMedicamentos.Fields.DESCRICAO.toString(),StringUtils.lowerCase(descricaoCodigo),MatchMode.ANYWHERE)));
		return this.executeCriteriaCount(criteria);
	}
	
	public List<VMamMedicamentos> obterMedicamentosReceitaPorDescricaoExata(String descricao){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(VMamMedicamentos.class);
		criteria.add(Restrictions.ilike(VMamMedicamentos.Fields.DESCRICAO.toString(),StringUtils.lowerCase(descricao),MatchMode.EXACT));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
}
