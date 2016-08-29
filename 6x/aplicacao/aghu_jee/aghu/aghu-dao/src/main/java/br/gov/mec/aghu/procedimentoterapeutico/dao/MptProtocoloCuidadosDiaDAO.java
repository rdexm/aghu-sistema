package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MptProtocoloCuidados;
import br.gov.mec.aghu.model.MptProtocoloCuidadosDia;

public class MptProtocoloCuidadosDiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptProtocoloCuidadosDia> {

	private static final long serialVersionUID = 8013636135587822544L;
	
	private final String ALIAS_PCD_PONTO = "PCD."; 
	private final String ALIAS_PCU_PONTO = "PCU."; 
	
	public MptProtocoloCuidadosDia obterProtocoloCuidadosDiaPorPcuSeqDia(Integer pcuSeq, Short dia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCuidadosDia.class, "PCD");
		
		criteria.createAlias(ALIAS_PCD_PONTO + MptProtocoloCuidadosDia.Fields.PROTOCOLO_CUIDADOS.toString(), "PCU");
		
		criteria.add(Restrictions.eq(ALIAS_PCU_PONTO + MptProtocoloCuidados.Fields.SEQ, pcuSeq));
		criteria.add(Restrictions.eq(ALIAS_PCD_PONTO + MptProtocoloCuidadosDia.Fields.DIA, dia));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_PCD_PONTO + MptProtocoloCuidadosDia.Fields.SEQ.toString()), MptProtocoloCuidadosDia.Fields.SEQ.toString())				
				.add(Projections.property(ALIAS_PCD_PONTO + MptProtocoloCuidadosDia.Fields.MODIFICADO.toString()), MptProtocoloCuidadosDia.Fields.MODIFICADO.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptProtocoloCuidadosDia.class));
		
		return (MptProtocoloCuidadosDia) executeCriteriaUniqueResult(criteria);
		
	}
	
	public MptProtocoloCuidadosDia obterProtocoloCuidadosDiaPorPcuSeqDiaCompleto(Integer pcuSeq, Short dia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCuidadosDia.class, "PCD");
		
		criteria.createAlias(ALIAS_PCD_PONTO + MptProtocoloCuidadosDia.Fields.VERSAO_PROTOCOLO_SESSAO.toString(), "VPS");
		criteria.createAlias(ALIAS_PCD_PONTO + MptProtocoloCuidadosDia.Fields.PROTOCOLO_CUIDADOS.toString(), "PCU");
		
		criteria.add(Restrictions.eq(ALIAS_PCU_PONTO + MptProtocoloCuidados.Fields.SEQ, pcuSeq));
		criteria.add(Restrictions.eq(ALIAS_PCD_PONTO + MptProtocoloCuidadosDia.Fields.DIA, dia));		
		
		return (MptProtocoloCuidadosDia) executeCriteriaUniqueResult(criteria);
		
	}
	
	public List<MptProtocoloCuidadosDia> verificarDiaCuidado(Integer seqEdicao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCuidadosDia.class);
		criteria.add(Restrictions.eq(MptProtocoloCuidadosDia.Fields.PROTOCOLO_CUIDADOS_SEQ.toString(), seqEdicao));
		return executeCriteria(criteria);
	}
	
}
