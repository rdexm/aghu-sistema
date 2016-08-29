package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoRefCodes;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class ScoRefCodesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoRefCodes> { 

	private static final long serialVersionUID = -5730653457227762851L;

	private DetachedCriteria criarCriteriaScoRefCodesPorTipoOperConversao(final String valor, final String dominio) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoRefCodes.class);

		criteria.add(Restrictions.eq(ScoRefCodes.Fields.RV_DOMAIN.toString(), dominio));

		final Criterion cri1 = Restrictions.and(Restrictions.isNull(ScoRefCodes.Fields.RV_HIGH_VALUE.toString()),
				Restrictions.eq(ScoRefCodes.Fields.RV_LOW_VALUE.toString(), valor));

		final Criterion cri2 = Restrictions.and(Restrictions.ge(ScoRefCodes.Fields.RV_LOW_VALUE.toString(), valor),
				Restrictions.le(ScoRefCodes.Fields.RV_HIGH_VALUE.toString(), valor));

		criteria.add(Restrictions.or(cri1, cri2));
		return criteria;
	}

	public List<ScoRefCodes> buscarScoRefCodesPorTipoOperConversao(final String valor, final String dominio) {
			return executeCriteria(criarCriteriaScoRefCodesPorTipoOperConversao(valor,dominio));
	}
	
	public Long validarScoRefCodesPorTipoOperConversao(final String valor, final String dominio) {
		return executeCriteriaCount(criarCriteriaScoRefCodesPorTipoOperConversao(valor,dominio));
	}
	
	private DetachedCriteria criarCriteriaScoRefCodesPorSituacao(String rvLowValue, String rvMeaning, List<String> rvLowValues) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoRefCodes.class);

		if(rvLowValue != null && !rvLowValue.isEmpty()){
			criteria.add(Restrictions.eq(ScoRefCodes.Fields.RV_LOW_VALUE.toString(), rvLowValue.toUpperCase()));
		}
		if(rvMeaning != null && !rvMeaning.isEmpty()){
			criteria.add(Restrictions.ilike(ScoRefCodes.Fields.RV_MEANING.toString(), rvMeaning, MatchMode.ANYWHERE));
		}
		if(rvLowValues != null && rvLowValues.size() > 0){
			criteria.add(Restrictions.in(ScoRefCodes.Fields.RV_LOW_VALUE.toString(), rvLowValues));
		}

		return criteria;
	}
	
	public List<ScoRefCodes> buscarScoRefCodesPorSituacao(String rvLowValue, String rvMeaning, List<String> rvLowValues) {
		DetachedCriteria criteria = criarCriteriaScoRefCodesPorSituacao(rvLowValue,rvMeaning, rvLowValues);
		criteria.addOrder(Property.forName(ScoRefCodes.Fields.RV_LOW_VALUE.toString()).asc());
		return executeCriteria(criteria);
	}
	
	public List<ScoRefCodes> buscarScoRefCodesPorDominio(String rvDomain) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoRefCodes.class);

		if(rvDomain != null && !rvDomain.isEmpty()){
			criteria.add(Restrictions.eq(ScoRefCodes.Fields.RV_DOMAIN.toString(), rvDomain));
		}
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Busca utilizando os campos que estiverem preenchidos
	 * @param refCode
	 * @param valorExato - True indica que o operador nos campos sera =, False indica que o like será utilizado
	 * @return
	 * @author bruno.mourao
	 * @since 28/03/2012
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public List<ScoRefCodes> buscarScoRefCodesPorRefCodes(ScoRefCodes refCode, Boolean valorExato){
		
		List<ScoRefCodes> result = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoRefCodes.class,"SRC");
		
		if(refCode != null){
			result = new ArrayList<ScoRefCodes>();
			if(refCode.getSeq() != null){
				ScoRefCodes codes = this.obterPorChavePrimaria(refCode.getSeq());
				result.add(codes);
			}
			else{
				if(refCode.getRvDomain() != null){
					if(valorExato){
						criteria.add(Restrictions.eq("SRC." + ScoRefCodes.Fields.RV_DOMAIN.toString(), refCode.getRvDomain()));
					}
					else{
						criteria.add(Restrictions.like("SRC." + ScoRefCodes.Fields.RV_DOMAIN.toString(), refCode.getRvDomain()));
					}
				}
				
				if(refCode.getRvAbbreviation() != null){
					if(valorExato){
						criteria.add(Restrictions.eq("SRC." + ScoRefCodes.Fields.RV_ABBREVIATION.toString(), refCode.getRvAbbreviation()));
					}
					else{
						criteria.add(Restrictions.like("SRC." + ScoRefCodes.Fields.RV_ABBREVIATION.toString(), refCode.getRvAbbreviation()));
					}
				}
				
				if(refCode.getRvMeaning() != null){
					if(valorExato){
						criteria.add(Restrictions.eq("SRC." + ScoRefCodes.Fields.RV_MEANING.toString(), refCode.getRvMeaning()));
					}
					else{
						criteria.add(Restrictions.like("SRC." + ScoRefCodes.Fields.RV_MEANING.toString(), refCode.getRvMeaning()));
					}
				}
				
				if(refCode.getRvHighValue() != null){
					if(valorExato){
						criteria.add(Restrictions.eq("SRC." + ScoRefCodes.Fields.RV_HIGH_VALUE.toString(), refCode.getRvHighValue()));
					}
					else{
						criteria.add(Restrictions.like("SRC." + ScoRefCodes.Fields.RV_HIGH_VALUE.toString(), refCode.getRvHighValue()));
					}
				}
				
				if(refCode.getRvLowValue() != null){
					if(valorExato){
						criteria.add(Restrictions.eq("SRC." + ScoRefCodes.Fields.RV_LOW_VALUE.toString(), refCode.getRvLowValue()));
					}
					else{
						criteria.add(Restrictions.like("SRC." + ScoRefCodes.Fields.RV_LOW_VALUE.toString(), refCode.getRvLowValue()));
					}
				}
				
				if(refCode.getRvType() != null){
					if(valorExato){
						criteria.add(Restrictions.eq("SRC." + ScoRefCodes.Fields.RV_TYPE.toString(), refCode.getRvType()));
					}
					else{
						criteria.add(Restrictions.like("SRC." + ScoRefCodes.Fields.RV_TYPE.toString(), refCode.getRvType()));
					}
				}
				result = this.executeCriteria(criteria);
			}
		}
		
		return result;
	}
	
	/**
	 * C2 - 27073
	 */
	public List<ScoRefCodes> obterClassificacaoEconomica(String rvDomain, String lowValue){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoRefCodes.class);
		criteria.add(Restrictions.eq(ScoRefCodes.Fields.RV_DOMAIN.toString(), rvDomain));
		criteria.add(Restrictions.eq(ScoRefCodes.Fields.RV_LOW_VALUE.toString(), lowValue));
		return executeCriteria(criteria);
	}
}
