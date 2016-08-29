package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcMatOrteseProtCirg;
import br.gov.mec.aghu.model.ScoMaterial;

public class MbcMatOrteseProtCirgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMatOrteseProtCirg> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6961181920951878526L;
	
	
	/**
	 * Pesquisa material de órtese e prótese da escala cirúrgica através da cirurgia
	 * @param crgSeq
	 * @return
	 */
	public List<MbcMatOrteseProtCirg> pesquisarOrteseProteseEscalaCirurgicaPorCirurgia(Integer crgSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMatOrteseProtCirg.class, "ORT");		
		
		criteria.createAlias("ORT.".concat(MbcMatOrteseProtCirg.Fields.SCO_MATERIAL.toString()), "MAT");
		criteria.createAlias("MAT.".concat(ScoMaterial.Fields.UNIDADE_MEDIDA.toString()), "UN");
		criteria.createAlias("ORT.".concat(MbcMatOrteseProtCirg.Fields.MBC_CIRURGIAS.toString()), "CRG");

		criteria.add(Restrictions.eq("CRG.".concat(MbcCirurgias.Fields.SEQ.toString()), crgSeq));

		return executeCriteria(criteria);
	}
	
	public Long countMatOrteseProtesePorCrgSeq(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMatOrteseProtCirg.class, "ORT");
		criteria.add(Restrictions.eq("ORT." + MbcMatOrteseProtCirg.Fields.MBC_CIRURGIAS_SEQ.toString(), crgSeq));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<String> obterListaOrteseProtesePorUnfSeqCrgSeqUnion2(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMatOrteseProtCirg.class, "ORT");
		criteria.createAlias("ORT.".concat(MbcMatOrteseProtCirg.Fields.SCO_MATERIAL.toString()), "MAT");
		
		criteria.setProjection(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()));
		
		criteria.add(Restrictions.eq("ORT." + MbcMatOrteseProtCirg.Fields.MBC_CIRURGIAS_SEQ.toString(), crgSeq));
		return executeCriteria(criteria);
	}

}
