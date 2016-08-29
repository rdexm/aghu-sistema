package br.gov.mec.aghu.emergencia.dao;



import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.MamUnidXExame;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamUnidXExameDAO extends BaseDao<MamUnidXExame> {

	private static final long serialVersionUID = 4060270788508740122L;

	public Boolean existeUnidXExamePorUnidadeAtendem(MamUnidAtendem mamUnidAtendem)	{
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXExame.class, "MamUnidXExame");
		
		criteria.add(Restrictions.eq(MamUnidXExame.Fields.MAM_UNID_ATENDEM.toString(), mamUnidAtendem));
		
		return (this.executeCriteriaCount(criteria) > 0);
	}
	
	public List<Integer> listaSeqUnidXExame(Long trgSeq) {
		
		final String TRG = "TRG.";
		final String UXE = "UXE.";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXExame.class, "UXE");
		criteria.createAlias(UXE + MamUnidXExame.Fields.MAM_UNID_ATENDEM.toString(), "UAN");
		criteria.createAlias(UXE + MamUnidXExame.Fields.MAM_ITEM_EXAME.toString(), "EMS");
		
		criteria.setProjection(Projections.property("EMS." + MamItemExame.Fields.SEQ.toString()));
		
		criteria.add(Restrictions.eq(UXE + MamUnidXExame.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("UAN." + MamUnidAtendem.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("EMS." + MamItemExame.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		final DetachedCriteria criteriaExists = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		
		criteriaExists.setProjection(Projections.property(TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		
		criteriaExists.add(Restrictions.eq(TRG + MamTriagens.Fields.SEQ.toString(), trgSeq));
		criteriaExists.add(Restrictions.eqProperty("UAN." + MamUnidAtendem.Fields.UNF_SEQ.toString(),
				TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		
		StringBuffer sql = new StringBuffer(150);
		sql.append(" ems2_.SEQ not in ( "
				+ " select txa.ems_seq FROM agh.mam_trg_exames txa "
				+ " WHERE  txa.trg_seq = trg_.seq AND "
				+ " txa.ems_seq = ems2_.seq) ");
		
		criteriaExists.add(Restrictions.sqlRestriction(sql.toString()));
		
		criteria.add(Subqueries.exists(criteriaExists));
		
		
		return executeCriteria(criteria);
	}
	
	public Boolean verificaComplementoExameObrigatorio(Integer emsSeq, Long trgSeq) {
		final String TRG = "TRG.";
		final String UXE = "UXE.";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXExame.class, "UXE");
		criteria.add(Restrictions.eq(UXE + MamUnidXExame.Fields.EMS_SEQ.toString(), emsSeq));
		criteria.add(Restrictions.eq(UXE + MamUnidXExame.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(UXE + MamUnidXExame.Fields.IND_OBRIGATORIO.toString(), Boolean.TRUE));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		subCriteria.setProjection(Projections.property(TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		subCriteria.add(Restrictions.eq(TRG + MamTriagens.Fields.SEQ.toString(), trgSeq));
		
		criteria.add(Subqueries.propertyIn(UXE + MamUnidXExame.Fields.UAN_UNF_SEQ.toString(), subCriteria));
		
		return (this.executeCriteriaCount(criteria) > 0);
	}
	
	public List<Integer> obterListaSeqUnidXExameObrigatorios(Long trgSeq) {
		final String UXE = "UXE.";
		final String TRG = "TRG.";
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXExame.class, "UXE");
		
		criteria.setProjection(Projections.property(UXE + MamUnidXExame.Fields.EMS_SEQ.toString()));
		criteria.add(Restrictions.eq(UXE + MamUnidXExame.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(UXE + MamUnidXExame.Fields.IND_OBRIGATORIO.toString(), Boolean.TRUE));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		subCriteria.setProjection(Projections.property(TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		subCriteria.add(Restrictions.eq(TRG + MamTriagens.Fields.SEQ.toString(), trgSeq));
		
		criteria.add(Subqueries.propertyIn(UXE + MamUnidXExame.Fields.UAN_UNF_SEQ.toString(), subCriteria));
		
		return executeCriteria(criteria);
	}
}