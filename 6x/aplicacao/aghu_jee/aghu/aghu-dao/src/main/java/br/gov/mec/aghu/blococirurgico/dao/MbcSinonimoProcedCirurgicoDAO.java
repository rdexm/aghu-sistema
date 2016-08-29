package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcSinonimoProcCirg;

public class MbcSinonimoProcedCirurgicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcSinonimoProcCirg> {

	private static final long serialVersionUID = 4099431122315152457L;

	public List<MbcSinonimoProcCirg> buscaSinonimosPeloSeqProcedimento(Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSinonimoProcCirg.class);
		criteria.add(Restrictions.eq(MbcSinonimoProcCirg.Fields.PCI_SEQ.toString(), pciSeq));
		
		criteria.addOrder(Order.asc(MbcSinonimoProcCirg.Fields.DESCRICAO.toString()));
		
		return super.executeCriteria(criteria);
	}


	public Short buscaMenorSeqpSinonimosPeloSeqProcedimento(Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSinonimoProcCirg.class);
		criteria.add(Restrictions.eq(MbcSinonimoProcCirg.Fields.PCI_SEQ.toString(), pciSeq));
		
		criteria.setProjection(Projections.min(MbcSinonimoProcCirg.Fields.SEQP.toString()));
		
		return (Short) super.executeCriteriaUniqueResult(criteria);
	}

	public Long quantidadeSinonimosPelaDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSinonimoProcCirg.class);
		criteria.add(Restrictions.eq(MbcSinonimoProcCirg.Fields.DESCRICAO.toString(), descricao));
		
		return executeCriteriaCount(criteria);
	}

	public Short buscaProximoSeqpSinonimosPeloSeqProcedimento(Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSinonimoProcCirg.class);
		criteria.add(Restrictions.eq(MbcSinonimoProcCirg.Fields.PCI_SEQ.toString(), pciSeq));
		
		criteria.setProjection(Projections.max(MbcSinonimoProcCirg.Fields.SEQP.toString()));
		
		Short seqp = (Short) super.executeCriteriaUniqueResult(criteria); 
		
		if(seqp == null) {
			seqp = 0;
		}
		else {
			seqp ++;
		}
		
		return seqp;
	}
	
	public List<MbcSinonimoProcCirg> pesquisarSinonimosPorPciSeq(Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcSinonimoProcCirg.class);
		criteria.add(Restrictions.eq(MbcSinonimoProcCirg.Fields.PCI_SEQ.toString(), pciSeq));
		return executeCriteria(criteria); 
	}

}
