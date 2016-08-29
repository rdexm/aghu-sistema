package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VAinConvenioPlano;

public class VAinConvenioPlanoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAinConvenioPlano>{
	
	private static final long serialVersionUID = 3614348530885010825L;
	private static final Criterion RESTRICTION_IND_SITUACAO_ATIVO = Restrictions.ilike(VAinConvenioPlano.Fields.IND_SITUACAO.toString(), "A", MatchMode.ANYWHERE);
	
    /**
     * Obtem planos de convênio ativos
     * @param parametro
     * @return
     */
	public List<VAinConvenioPlano> pesquisarConveniosAtivos(Object objPesquisa) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(VAinConvenioPlano.class);
	    String srtPesquisa = (String)objPesquisa;
		if (StringUtils.isNotBlank(srtPesquisa)) {
		    	criteria.add(Restrictions.ilike(VAinConvenioPlano.Fields.CONVENIO_PLANO.toString(), srtPesquisa , MatchMode.ANYWHERE ));
		}
		criteria.add(RESTRICTION_IND_SITUACAO_ATIVO);
		criteria.addOrder(Order.asc(VAinConvenioPlano.Fields.CONVENIO_PLANO.toString()));
		return this.executeCriteria(criteria);
	}
	
	public DetachedCriteria obterCriteriaVAinConvenioPlanoAtivoPeloId(byte plano, short cnvCodigo) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(VAinConvenioPlano.class);
	    criteria.add(Restrictions.eq(VAinConvenioPlano.Fields.PLANO.toString(), plano));
	    criteria.add(Restrictions.eq(VAinConvenioPlano.Fields.CODIGO.toString(), cnvCodigo));
		return criteria;
	}
	
	
    /**
     * Obtem planos de convênio através dos atributos da ID
     * @param parametro
     * @return
     */
	public VAinConvenioPlano obterVAinConvenioPlanoPeloId(byte plano, short cnvCodigo) {
	    DetachedCriteria criteria = this.obterCriteriaVAinConvenioPlanoAtivoPeloId(plano, cnvCodigo);
		return (VAinConvenioPlano)this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
     * Obtem planos de convênio ativos através dos atributos da ID
     * @param parametro
     * @return
     */
	public VAinConvenioPlano obterVAinConvenioPlanoAtivoPeloId(byte plano, short cnvCodigo) {
		DetachedCriteria criteria = this.obterCriteriaVAinConvenioPlanoAtivoPeloId(plano, cnvCodigo);
		criteria.add(RESTRICTION_IND_SITUACAO_ATIVO);
		return (VAinConvenioPlano)this.executeCriteriaUniqueResult(criteria);
	}
	
	public String obterDescricaoConvenioPlano(Byte cspSeq, Short cspCnvCodigo) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(VAinConvenioPlano.class);
	    
		ProjectionList projection = Projections.projectionList()
			.add(Projections.property(VAinConvenioPlano.Fields.CONVENIO_PLANO.toString()));
		criteria.setProjection(projection);	
	
	    criteria.add(Restrictions.eq(VAinConvenioPlano.Fields.PLANO.toString(), cspSeq));
	    criteria.add(Restrictions.eq(VAinConvenioPlano.Fields.CODIGO.toString(), cspCnvCodigo));
	    
		return (String) this.executeCriteriaUniqueResult(criteria);
	}

}
