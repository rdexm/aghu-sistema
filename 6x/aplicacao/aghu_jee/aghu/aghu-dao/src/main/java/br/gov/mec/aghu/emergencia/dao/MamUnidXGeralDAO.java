package br.gov.mec.aghu.emergencia.dao;



import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamItemGeral;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.MamUnidXGeral;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamUnidXGeralDAO extends BaseDao<MamUnidXGeral> {

	private static final long serialVersionUID = 4060270788508740122L;

	public List<Integer> listaSeqUnidXGeral(Long trgSeq)	{
		final String TRG = "TRG.";
		final String UXG = "UXG.";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXGeral.class, "UXG");
		criteria.createAlias(UXG + MamUnidXGeral.Fields.MAM_UNID_ATENDEM.toString(), "UAN");
		criteria.createAlias(UXG + MamUnidXGeral.Fields.MAM_ITEM_GERAL.toString(), "ITG");
		
		criteria.setProjection(Projections.property("ITG." + MamItemGeral.Fields.SEQ.toString()));
		
		criteria.add(Restrictions.eq(UXG + MamUnidXGeral.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("UAN." + MamUnidAtendem.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("ITG." + MamItemGeral.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		final DetachedCriteria criteriaExists = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		
		criteriaExists.setProjection(Projections.property(TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		
		criteriaExists.add(Restrictions.eq(TRG + MamTriagens.Fields.SEQ.toString(), trgSeq));
		criteriaExists.add(Restrictions.eqProperty("UAN." + MamUnidAtendem.Fields.UNF_SEQ.toString(),
				TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		
		StringBuffer sql = new StringBuffer(150);
		sql.append(" itg2_.SEQ not in ( "
				+ " select tsg.itg_seq FROM agh.mam_trg_gerais tsg "
				+ " WHERE  tsg.trg_seq = trg_.seq AND "
				+ " tsg.itg_seq = itg2_.seq) ");
		
		criteriaExists.add(Restrictions.sqlRestriction(sql.toString()));
		
		criteria.add(Subqueries.exists(criteriaExists));
		
		
		return executeCriteria(criteria);
	}
	
	public Boolean verificaComplementoGeralObrigatorio(Integer itgSeq, Long trgSeq) {
		final String UXG = "UXG.";
		final String TRG = "TRG.";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXGeral.class, "UXG");
		
		criteria.add(Restrictions.eq(UXG + MamUnidXGeral.Fields.ITG_SEQ.toString(), itgSeq));
		criteria.add(Restrictions.eq(UXG + MamUnidXGeral.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(UXG + MamUnidXGeral.Fields.IND_OBRIGATORIO.toString(), Boolean.TRUE));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		subCriteria.setProjection(Projections.property(TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		subCriteria.add(Restrictions.eq(TRG + MamTriagens.Fields.SEQ.toString(), trgSeq));
		
		criteria.add(Subqueries.propertyIn(UXG + MamUnidXGeral.Fields.UAN_UNF_SEQ.toString(), subCriteria));
		
		return (this.executeCriteriaCount(criteria) > 0);
	}
	
	public List<Integer> obterListaSeqUnidXGeralObrigatorios(Long trgSeq) {
		final String UXG = "UXG.";
		final String TRG = "TRG.";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXGeral.class, "UXG");
		
		criteria.setProjection(Projections.property(UXG + MamUnidXGeral.Fields.ITG_SEQ.toString()));
		criteria.add(Restrictions.eq(UXG + MamUnidXGeral.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(UXG + MamUnidXGeral.Fields.IND_OBRIGATORIO.toString(), Boolean.TRUE));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		subCriteria.setProjection(Projections.property(TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		subCriteria.add(Restrictions.eq(TRG + MamTriagens.Fields.SEQ.toString(), trgSeq));
		
		criteria.add(Subqueries.propertyIn(UXG + MamUnidXGeral.Fields.UAN_UNF_SEQ.toString(), subCriteria));
		
		return executeCriteria(criteria);
	}
}