package br.gov.mec.aghu.prescricaomedica.dao;



import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import br.gov.mec.aghu.model.MpmTextoPadraoParecer;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MpmTextoPadraoParecerDAO extends BaseDao<MpmTextoPadraoParecer> {

	
	private static final long serialVersionUID = -4807955042371540318L;

	public List<MpmTextoPadraoParecer> obterMpmTextoPadraoParecer(String sigla, String descricao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTextoPadraoParecer.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MpmTextoPadraoParecer.Fields.SIGLA.toString()).as(MpmTextoPadraoParecer.Fields.SIGLA.toString()))
				.add(Projections.property(MpmTextoPadraoParecer.Fields.DESCRICAO.toString()).as(MpmTextoPadraoParecer.Fields.DESCRICAO.toString())));
		if((sigla != null && !sigla.trim().isEmpty()) && (descricao != null)){
			criteria.add(Restrictions.or(Restrictions.ilike(MpmTextoPadraoParecer.Fields.DESCRICAO.toString(),this.replaceCaracterEspecial(descricao.trim()), MatchMode.ANYWHERE),
					Restrictions.eq(MpmTextoPadraoParecer.Fields.SIGLA.toString(), this.replaceCaracterSigla(sigla.trim()))));
		}else if(sigla != null && !sigla.trim().isEmpty()){
			criteria.add(Restrictions.eq(MpmTextoPadraoParecer.Fields.SIGLA.toString(),this.replaceCaracterSigla(sigla.trim())));
		}else if(descricao != null){
			criteria.add(Restrictions.ilike(MpmTextoPadraoParecer.Fields.DESCRICAO.toString(), this.replaceCaracterEspecial(descricao.trim()), MatchMode.ANYWHERE));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(MpmTextoPadraoParecer.class));
		
		return executeCriteria(criteria);
	}
	
	
	public Object obterSiglaMpmTextoPadraoParecer(String sigla){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTextoPadraoParecer.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MpmTextoPadraoParecer.Fields.SIGLA.toString()).as(MpmTextoPadraoParecer.Fields.SIGLA.toString()))
				.add(Projections.property(MpmTextoPadraoParecer.Fields.DESCRICAO.toString()).as(MpmTextoPadraoParecer.Fields.DESCRICAO.toString())));
		if(sigla != null && !sigla.trim().isEmpty()){
			criteria.add(Restrictions.eq(MpmTextoPadraoParecer.Fields.SIGLA.toString(),sigla));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(MpmTextoPadraoParecer.class));
		
		return executeCriteriaUniqueResult(criteria);	
	}
	
	private String replaceCaracterEspecial(String descricao) {
		if(descricao != null){
	       return descricao.replace("_", "\\_").replace("~", "\\~").replace("+","\\+").replace("´", "\\´").replace("`", "\\`").replace("^","\\^").replace("-", "\\-");
		}
		return descricao;
	}
	
	private String replaceCaracterSigla(String sigla){
		if(sigla != null){
			return sigla.replace("-", "\\-").replace(".", "\\.");
		}
		return sigla;
	}
		
}
