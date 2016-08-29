package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghWFTemplateExecutor;

public class AghWFTemplateExecutorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghWFTemplateExecutor> {
	
	private static final long serialVersionUID = -4179667239631119553L;
	

	
	public  DetachedCriteria obterCriteriaBasica() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFTemplateExecutor.class);		
		return criteria;
	}
	
	
//	SQL para modelo de executores de cada etapa:
//		Parâmetros: agh_wf_template_etapas.SEQ , agh_wf_template_etapas.WTF_SEQ
//
//		SELECT AGH_WF_TEMPLATE_EXECUTORES.SER_MATRICULA, 
//		       AGH_WF_TEMPLATE_EXECUTORES.SER_VIN_CODIGO, 
//		       IND_RECEBE_NOTIF 
//		FROM   AGH_WF_TEMPLATE_EXECUTORES 
//		WHERE  AGH_WF_TEMPLATE_EXECUTORES.WTE_SEQ = 1 -- AGH_WF_TEMPLATE_ETAPAS.SEQ
//		AND 	 AGH_WF_TEMPLATE_EXECUTORES.WTF_SEQ = 1 -- AGH_WF_TEMPLATE_ETAPAS.WTF_SEQ
	// #37052 C2
	public List<AghWFTemplateExecutor> obterTemplateExecutoresPorTemplateEtapa(Integer seqTemplateEtapa, Integer seqTemplateFluxo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghWFTemplateExecutor.class);
		criteria.add(Restrictions.eq(AghWFTemplateExecutor.Fields.WTE_SEQ.toString(), seqTemplateEtapa));
		criteria.add(Restrictions.eq(AghWFTemplateExecutor.Fields.WTF_SEQ.toString(), seqTemplateFluxo));			
		
		return executeCriteria(criteria);
	}
}
