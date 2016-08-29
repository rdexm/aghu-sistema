package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentosDia;

public class MptProtocoloMedicamentosDiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptProtocoloMedicamentosDia> {

	private static final long serialVersionUID = -4222119251843255245L;
	
	private final String ALIAS_PMD_PONTO = "PMD.";
	private final String ALIAS_PTM_PONTO = "PTM."; 
	
	public MptProtocoloMedicamentosDia obterProtocoloMedicamentosDiaPorPtmSeqDia(Long ptmSeq, Short dia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloMedicamentosDia.class, "PMD");
		
		criteria.createAlias(ALIAS_PMD_PONTO + MptProtocoloMedicamentosDia.Fields.PROTOCOLO_MEDICAMENTOS.toString(), "PTM");
		
		criteria.add(Restrictions.eq(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.SEQ, ptmSeq));
		criteria.add(Restrictions.eq(ALIAS_PMD_PONTO + MptProtocoloMedicamentosDia.Fields.DIA, dia));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_PMD_PONTO + MptProtocoloMedicamentosDia.Fields.SEQ.toString()), MptProtocoloMedicamentosDia.Fields.SEQ.toString())
				.add(Projections.property(ALIAS_PMD_PONTO + MptProtocoloMedicamentosDia.Fields.IND_USO_DOMICILIAR.toString()), MptProtocoloMedicamentosDia.Fields.IND_USO_DOMICILIAR.toString())
				.add(Projections.property(ALIAS_PMD_PONTO + MptProtocoloMedicamentosDia.Fields.MODIFICADO.toString()), MptProtocoloMedicamentosDia.Fields.MODIFICADO.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptProtocoloMedicamentosDia.class));
		
		return (MptProtocoloMedicamentosDia) executeCriteriaUniqueResult(criteria);
		
	}
	
	public MptProtocoloMedicamentosDia obterProtocoloMedicamentosDiaPorPtmSeqDiaCompleto(Long ptmSeq, Short dia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloMedicamentosDia.class, "PMD");
		
		criteria.createAlias(ALIAS_PMD_PONTO + MptProtocoloMedicamentosDia.Fields.VERSAO_PROTOCOLO_SESSAO.toString(), "VPS");
		criteria.createAlias(ALIAS_PMD_PONTO + MptProtocoloMedicamentosDia.Fields.PROTOCOLO_MEDICAMENTOS.toString(), "PTM");
		
		criteria.add(Restrictions.eq(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.SEQ, ptmSeq));
		criteria.add(Restrictions.eq(ALIAS_PMD_PONTO + MptProtocoloMedicamentosDia.Fields.DIA, dia));
		
		return (MptProtocoloMedicamentosDia) executeCriteriaUniqueResult(criteria);
		
	}
	
	public List<MptProtocoloMedicamentosDia> verificarExisteDiaMarcadoParaProtocolo(Long protocolo){

        DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloMedicamentosDia.class);

        criteria.add(Restrictions.eq(MptProtocoloMedicamentosDia.Fields.PROTOCOLO_MEDICAMENTOS_SEQ.toString(), protocolo));

        return executeCriteria(criteria);
	}
}
