package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.MbcCompSangProcCirg;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;

public class MbcCompSangProcCirgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcCompSangProcCirg> {

	private static final long serialVersionUID = 4097431122321518867L;

	public List<MbcCompSangProcCirg> buscarAsscComponenteSangPeloSeqProcedimento(Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCompSangProcCirg.class);
		criteria.createAlias(MbcCompSangProcCirg.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "PCI");
		criteria.createAlias(MbcCompSangProcCirg.Fields.ABS_COMPONENTE_SANGUINEO.toString(), "CSG");
		criteria.createAlias(MbcCompSangProcCirg.Fields.AGH_ESPECIALIDADES.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("PCI."+MbcProcedimentoCirurgicos.Fields.SEQ.toString(), pciSeq));
		criteria.addOrder(Order.asc("CSG."+AbsComponenteSanguineo.Fields.DESCRICAO.toString()));
		
		return super.executeCriteria(criteria);
	}
	
	
	public MbcCompSangProcCirg buscarComponenteSanguineoEEspecialidadePorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCompSangProcCirg.class);
		criteria.createAlias(MbcCompSangProcCirg.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "PCI");
		criteria.createAlias(MbcCompSangProcCirg.Fields.ABS_COMPONENTE_SANGUINEO.toString(), "CSG");
		criteria.createAlias(MbcCompSangProcCirg.Fields.AGH_ESPECIALIDADES.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MbcCompSangProcCirg.Fields.SEQ.toString(), seq));
		criteria.addOrder(Order.asc("CSG."+AbsComponenteSanguineo.Fields.DESCRICAO.toString()));
		return  (MbcCompSangProcCirg) executeCriteriaUniqueResult(criteria);
	}

	public Long obterCountCompSangPorPciSeqECodigoCompSang(Integer pciSeq, String codigoCompSang) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCompSangProcCirg.class);
		
		criteria.createAlias(MbcCompSangProcCirg.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "PCI");
		criteria.createAlias(MbcCompSangProcCirg.Fields.ABS_COMPONENTE_SANGUINEO.toString(), "CSG");
		
		criteria.add(Restrictions.eq("PCI." + MbcProcedimentoCirurgicos.Fields.SEQ.toString(), pciSeq));
		criteria.add(Restrictions.eq("CSG." + AbsComponenteSanguineo.Fields.CODIGO.toString(), codigoCompSang));
		
		criteria.setProjection(Projections.property(MbcCompSangProcCirg.Fields.SEQ.toString()));
		
		return executeCriteriaCount(criteria);
	}

	public List<MbcCompSangProcCirg> buscarMbcCompSangProcCirg(final Integer pciSeq, final Short espSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcCompSangProcCirg.class);

		criteria.add(Restrictions.eq(MbcCompSangProcCirg.Fields.PCI_SEQ.toString(), pciSeq));
		criteria.add(Restrictions.or(Restrictions.eq(MbcCompSangProcCirg.Fields.ESP_SEQ.toString(), espSeq),
				Restrictions.isNull(MbcCompSangProcCirg.Fields.ESP_SEQ.toString())));

		criteria.addOrder(Order.asc(MbcCompSangProcCirg.Fields.ESP_SEQ.toString()));
		return super.executeCriteria(criteria);
	}

	public Boolean verificarCirurgiaPossuiComponenteSanguineo(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcCompSangProcCirg.class, "MCSPC");
		DetachedCriteria criteriaProcEsp = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "MPEPC");
		
		criteriaProcEsp.add(Restrictions.eq("MPEPC." + MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		criteriaProcEsp.setProjection(Projections.property("MPEPC." + MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.toString()));
		
		criteria.add(Subqueries.propertyIn("MCSPC." + MbcCompSangProcCirg.Fields.PCI_SEQ.toString(), criteriaProcEsp));		
		return executeCriteriaCount(criteria) > 0;
	}
}
