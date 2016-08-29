package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFFluxo;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.AghWFTemplateFluxo;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;

public class AghWFTemplateEtapaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghWFTemplateEtapa> {
	
	private static final long serialVersionUID = -4179667239631119553L;
	

	
	public  DetachedCriteria obterCriteriaBasica() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFTemplateEtapa.class);		
		return criteria;
	}
	
	// #37052 C1
	public List<AghWFTemplateEtapa> obterTemplateEtapasPorModulo(String modulo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFTemplateEtapa.class, "WTE");
		criteria.createAlias("WTE."+AghWFTemplateEtapa.Fields.WTF_SEQ.toString(), "WTF");
		
		if(StringUtils.isNotBlank(modulo)) {
			criteria.add(Restrictions.eq("WTF."+AghWFTemplateFluxo.Fields.COD_MODULO.toString(), modulo));			
		}
		criteria.addOrder(Order.asc("WTE."+AghWFTemplateEtapa.Fields.SEQ.toString()));
		return executeCriteria(criteria);		
	}
	
	public AghWFTemplateEtapa consultarEtapaWorkflowRequisicao(MbcRequisicaoOpmes requisicaoOpmes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghWFTemplateEtapa.class, "wte");
		criteria.add(Restrictions.eq("wte." + AghWFTemplateEtapa.Fields.CODIGO.toString(), requisicaoOpmes.getSituacao().getCodigo()));
		return (AghWFTemplateEtapa) executeCriteriaUniqueResult(criteria);
	}
	
	public AghWFTemplateEtapa consultarEtapaWorkflowRequisicao(final Short ropSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		DetachedCriteria subWte2 = obterCriteriaMaximoSeqEtapa();

		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.FLUXO.toString(), "WFL");
		criteria.createAlias("WFL." + AghWFFluxo.Fields.ETAPAS.toString(), "WET");
		criteria.createAlias("WET." + AghWFEtapa.Fields.TEMPLATE_ETAPA.toString (), "WTE");
		criteria.add(Restrictions.eqProperty("WFL." + AghWFFluxo.Fields.SEQ.toString(), "WET." + AghWFEtapa.Fields.SEQ.toString()));
		criteria.add(Subqueries.propertyEq("WET." + AghWFEtapa.Fields.SEQ.toString(), subWte2));
		criteria.add(Restrictions.eqProperty("WTE." + AghWFTemplateEtapa.Fields.CODIGO.toString(), "ROP." + MbcRequisicaoOpmes.Fields.SITUACAO.toString()));
		criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.ID.toString(), ropSeq));
		return (AghWFTemplateEtapa) executeCriteriaUniqueResult(criteria);
	}
	
	private DetachedCriteria obterCriteriaMaximoSeqEtapa() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghWFEtapa.class, "WET2");
		criteria.add(Restrictions.eqProperty("WET2." + AghWFEtapa.Fields.WFL_SEQ_SEQ.toString(), "WET." + AghWFEtapa.Fields.WFL_SEQ_SEQ.toString()));
		ProjectionList pl = Projections.projectionList().add(Projections.max("WET2." + AghWFEtapa.Fields.SEQ.toString()));
		criteria.setProjection(pl);
		
		return criteria;
	}
	
	private DetachedCriteria obterCriteriaMaximoSequencialEtapa() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghWFEtapa.class, "ETAPA");
		criteria.add(Restrictions.eqProperty("ETAPA." + AghWFEtapa.Fields.WFL_SEQ_SEQ.toString(), "WET." + AghWFEtapa.Fields.WFL_SEQ_SEQ.toString()));
		ProjectionList pl = Projections.projectionList().add(Projections.max("ETAPA." + AghWFEtapa.Fields.SEQUENCIA.toString()));
		criteria.setProjection(pl);
		
		return criteria;
	}
	
	
	public AghWFTemplateEtapa consultarDescricaoTemplateEtapa(final Short ropSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRequisicaoOpmes.class, "ROP");
		criteria.createAlias("ROP." + MbcRequisicaoOpmes.Fields.FLUXO.toString(), "WFL");
		criteria.createAlias("WFL." + AghWFFluxo.Fields.ETAPAS.toString(), "WET");
		criteria.createAlias("WET." + AghWFEtapa.Fields.TEMPLATE_ETAPA.toString (), "WTE");
		criteria.createAlias("WTE." + AghWFTemplateEtapa.Fields.WTF_SEQ.toString (), "WTF");
		criteria.add(Restrictions.eqProperty("WFL." + AghWFFluxo.Fields.WTF_SEQ.toString(), "WTF." + AghWFTemplateFluxo.Fields.SEQ.toString()));
		criteria.add(Subqueries.propertyEq("WET." + AghWFEtapa.Fields.SEQUENCIA.toString(), obterCriteriaMaximoSequencialEtapa()));
		criteria.add(Restrictions.eqProperty("WTE." + AghWFTemplateEtapa.Fields.CODIGO.toString(), "ROP." + MbcRequisicaoOpmes.Fields.SITUACAO.toString()));
		criteria.add(Restrictions.eq("ROP." + MbcRequisicaoOpmes.Fields.ID.toString(), ropSeq));
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("WTE." + AghWFTemplateEtapa.Fields.DESCRICAO.toString() ), "descricao");
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(AghWFTemplateEtapa.class));
		return	 (AghWFTemplateEtapa) executeCriteriaUniqueResult(criteria);		
	}
	
}
