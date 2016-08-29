package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;

public class MbcAgendaProcedimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAgendaProcedimento> {

	private static final long serialVersionUID = -4759004862799018701L;
	
	public List<MbcAgendaProcedimento> pesquisarAgendaProcedimentoPorId(Integer agdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaProcedimento.class, "agt");
		criteria.createAlias("agt." + MbcAgendaProcedimento.Fields.PROCEDIMENTO_CIRURGICO.toString(), "map", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(MbcAgendaProcedimento.Fields.ID_AGD_SEQ.toString(), agdSeq));
		return executeCriteria(criteria);
	}

	public List<MbcAgendaProcedimento> pesquisarPorAgdSeq(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaProcedimento.class,"agt");
		criteria.createAlias("agt."+MbcAgendaProcedimento.Fields.MBC_ESPECIALIDADE_PROC_CIRGS.toString(), "epr");
		criteria.createAlias("epr."+MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), "pci");
		criteria.createAlias("agt."+MbcAgendaProcedimento.Fields.PROCEDIMENTO_CIRURGICO.toString(), "proc", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("agt."+MbcAgendaProcedimento.Fields.MBC_AGENDAS_ID.toString(), agdSeq));
		return executeCriteria(criteria);
	}
	
	public MbcAgendaProcedimento obterProcedimentoCirurgicoPorAgdSeqEprPciSeqEspSeq(Integer agdSeq, Integer eprPciSeq, Short eprEspSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaProcedimento.class,"agt");
		criteria.createAlias("agt."+MbcAgendaProcedimento.Fields.MBC_ESPECIALIDADE_PROC_CIRGS.toString(), "epr");
		criteria.createAlias("epr."+MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), "pci");
		
		criteria.add(Restrictions.eq("agt."+MbcAgendaProcedimento.Fields.MBC_AGENDAS_ID.toString(), agdSeq));
		criteria.add(Restrictions.eq("agt."+MbcAgendaProcedimento.Fields.EPR_ESP_SEQ.toString(), eprEspSeq));
		criteria.add(Restrictions.eq("agt."+MbcAgendaProcedimento.Fields.EPR_PCI_SEQ.toString(), eprPciSeq));
		
		return (MbcAgendaProcedimento) executeCriteriaUniqueResult(criteria);
	}
}