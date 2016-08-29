package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.MamUnidXMedicacao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamUnidXMedicacaoDAO extends BaseDao<MamUnidXMedicacao> {

	private static final long serialVersionUID = -3700623507010907316L;

	public Boolean existeUnidXMedicacaoPorUnidadeAtendem(MamUnidAtendem mamUnidAtendem)	{
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXMedicacao.class, "MamUnidXMedicacao");
		criteria.add(Restrictions.eq(MamUnidXMedicacao.Fields.MAM_UNID_ATENDEM.toString(), mamUnidAtendem));
		return (this.executeCriteriaCount(criteria) > 0);
	}
	
	public List<Integer> listaSeqUnidXMedicacao(Long trgSeq) {
		
		final String UXM = "UXM.";
		final String UAN = "UAN.";
		final String MDM = "MDM.";
		final String TRG = "TRG.";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXMedicacao.class, "UXM");
		criteria.createAlias(UXM + MamUnidXMedicacao.Fields.MAM_UNID_ATENDEM.toString(), "UAN");
		criteria.createAlias(UXM + MamUnidXMedicacao.Fields.MAM_ITEM_MEDICACAO.toString(), "MDM");
		
		criteria.setProjection(Projections.property(MDM + MamItemMedicacao.Fields.SEQ.toString()));
		
		criteria.add(Restrictions.eq(UXM + MamUnidXMedicacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(UAN + MamUnidAtendem.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MDM + MamItemMedicacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		final DetachedCriteria criteriaExists = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		
		criteriaExists.setProjection(Projections.property(TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		
		criteriaExists.add(Restrictions.eq(TRG + MamTriagens.Fields.SEQ.toString(), trgSeq));
		criteriaExists.add(Restrictions.eqProperty(UAN + MamUnidAtendem.Fields.UNF_SEQ.toString(),
				TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		
		StringBuffer sql = new StringBuffer(150);
		sql.append(" mdm2_.SEQ not in ( "
				+ " select tdc.mdm_seq FROM agh.mam_trg_medicacoes tdc "
				+ " WHERE  tdc.trg_seq = trg_.seq AND "
				+ " tdc.mdm_seq = mdm2_.seq) ");
		
		criteriaExists.add(Restrictions.sqlRestriction(sql.toString()));
		
		criteria.add(Subqueries.exists(criteriaExists));
		
		return executeCriteria(criteria);
	}
	
	public Boolean verificaComplementoMedicacaoObrigatorio(Integer mdmSeq, Long trgSeq) {
		final String UXM = "UXM.";
		final String TRG = "TRG.";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXMedicacao.class, "UXM");
		
		criteria.add(Restrictions.eq(UXM + MamUnidXMedicacao.Fields.MDM_SEQ.toString(), mdmSeq));
		criteria.add(Restrictions.eq(UXM + MamUnidXMedicacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(UXM + MamUnidXMedicacao.Fields.IND_OBRIGATORIO.toString(), Boolean.TRUE));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		subCriteria.setProjection(Projections.property(TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		subCriteria.add(Restrictions.eq(TRG + MamTriagens.Fields.SEQ.toString(), trgSeq));
		
		criteria.add(Subqueries.propertyIn(UXM + MamUnidXMedicacao.Fields.UAN_UNF_SEQ.toString(), subCriteria));
		
		return (this.executeCriteriaCount(criteria) > 0);
	}
	
	public List<Integer> obterListaSeqUnidXMedicacaoObrigatorios(Long trgSeq) {
		final String UXM = "UXM.";
		final String TRG = "TRG.";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXMedicacao.class, "UXM");
		
		criteria.setProjection(Projections.property(UXM + MamUnidXMedicacao.Fields.MDM_SEQ.toString()));
		criteria.add(Restrictions.eq(UXM + MamUnidXMedicacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(UXM + MamUnidXMedicacao.Fields.IND_OBRIGATORIO.toString(), Boolean.TRUE));
		
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(MamTriagens.class, "TRG");
		subCriteria.setProjection(Projections.property(TRG + MamTriagens.Fields.UNF_SEQ.toString()));
		subCriteria.add(Restrictions.eq(TRG + MamTriagens.Fields.SEQ.toString(), trgSeq));
		
		criteria.add(Subqueries.propertyIn(UXM + MamUnidXMedicacao.Fields.UAN_UNF_SEQ.toString(), subCriteria));
		
		return executeCriteria(criteria);
	}
}