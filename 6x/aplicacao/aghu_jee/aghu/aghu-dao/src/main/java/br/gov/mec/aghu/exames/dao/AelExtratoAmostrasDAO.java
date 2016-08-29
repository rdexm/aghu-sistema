package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelExtratoAmostras;



public class AelExtratoAmostrasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExtratoAmostras> {
	
	
	private static final long serialVersionUID = -8226388258425658323L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoAmostras.class);
		return criteria;
    }

	public List<AelExtratoAmostras> buscarAelExtratosAmostrasPorAmostra(
			AelAmostras aelAmostras) {
		
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SOE_SEQ.toString(), aelAmostras.getId().getSoeSeq()));
		dc.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SEQP.toString(), aelAmostras.getId().getSeqp()));

		return executeCriteria(dc);
	}
	
	public List<AelExtratoAmostras> buscarAelExtratosAmostrasPorAmostra(Integer amoSoeSeq, Short amoSeqp) {
		
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
		dc.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SEQP.toString(), amoSeqp));
		
		dc.addOrder(Order.asc(AelExtratoAmostras.Fields.AMO_SEQP.toString()));
		
		return executeCriteria(dc);
	}
	
	
	
	/**
	 * Pesquisa extrado de amostras através do id e com situação diferente da informada
	 * @param aelAmostras
	 * @param situacaoDiferente
	 * @return
	 */
	public List<AelExtratoAmostras> buscarExtratosAmostrasPorSituacaoDiferenteInformada(Integer soeSeq, Short seqp, DominioSituacaoAmostra situacaoDiferente) {
		
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SOE_SEQ.toString(), soeSeq));
		dc.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SEQP.toString(), seqp));
		dc.add(Restrictions.ne(AelExtratoAmostras.Fields.SITUACAO.toString(), situacaoDiferente));
		dc.addOrder(Order.desc(AelExtratoAmostras.Fields.SEQP.toString()));
		
		return executeCriteria(dc);
	}
	
	/**
	 * Pesquisa extrado de amostras através do id e com situação diferente da informada
	 * @param aelAmostras
	 * @param situacaoDiferente
	 * @return
	 */
	public List<AelExtratoAmostras> buscarExtratosAmostrasPorSituacaoDiferenteInformada(AelAmostras aelAmostras, DominioSituacaoAmostra situacaoDiferente) {
		
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SOE_SEQ.toString(), aelAmostras.getId().getSoeSeq()));
		dc.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SEQP.toString(), aelAmostras.getId().getSeqp()));
     	dc.add(Restrictions.ne(AelExtratoAmostras.Fields.SITUACAO.toString(), situacaoDiferente));
		dc.addOrder(Order.desc(AelExtratoAmostras.Fields.SEQP.toString()));
		
		return executeCriteria(dc);
	}
	
	public AelExtratoAmostras pesquisarExtratoAmostraAnterior(Integer amoSoeSeq, Short amoSeqp) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoAmostras.class, "exm1");

        criteria.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SOE_SEQ.toString(), amoSoeSeq));
        criteria.add(Restrictions.eq(AelExtratoAmostras.Fields.AMO_SEQP.toString(), amoSeqp));

        DetachedCriteria subCriteria1 = DetachedCriteria.forClass(AelExtratoAmostras.class, "exm2");
        subCriteria1.setProjection(Projections.projectionList().add(Projections.max("exm2."+AelExtratoAmostras.Fields.SEQP.toString())));
        subCriteria1.add(Restrictions.eqProperty("exm2."+AelExtratoAmostras.Fields.AMO_SOE_SEQ.toString(), "exm1."+AelExtratoAmostras.Fields.AMO_SOE_SEQ.toString()));
        subCriteria1.add(Restrictions.eqProperty("exm2."+AelExtratoAmostras.Fields.AMO_SEQP.toString(), "exm1."+AelExtratoAmostras.Fields.AMO_SEQP.toString()));

        DetachedCriteria subCriteria2 = DetachedCriteria.forClass(AelExtratoAmostras.class, "exm3");
        subCriteria2.setProjection(Projections.projectionList().add(Projections.max("exm3."+AelExtratoAmostras.Fields.SEQP.toString())));
        subCriteria2.add(Restrictions.eqProperty("exm3."+AelExtratoAmostras.Fields.AMO_SOE_SEQ.toString(), "exm2."+AelExtratoAmostras.Fields.AMO_SOE_SEQ.toString()));
        subCriteria2.add(Restrictions.eqProperty("exm3."+AelExtratoAmostras.Fields.AMO_SEQP.toString(), "exm2."+AelExtratoAmostras.Fields.AMO_SEQP.toString()));

        subCriteria1.add(Subqueries.propertyLt("exm2."+AelExtratoAmostras.Fields.SEQP.toString(), subCriteria2));
        
        criteria.add(Subqueries.propertyEq("exm1."+AelExtratoAmostras.Fields.SEQP.toString(), subCriteria1));

        return (AelExtratoAmostras) executeCriteriaUniqueResult(criteria);

  }


	
}
