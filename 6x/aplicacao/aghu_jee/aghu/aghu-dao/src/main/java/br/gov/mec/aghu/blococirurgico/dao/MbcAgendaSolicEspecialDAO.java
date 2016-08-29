package br.gov.mec.aghu.blococirurgico.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.AgendaCirurgiaSolicitacaoEspecialVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;

public class MbcAgendaSolicEspecialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAgendaSolicEspecial> {

	private static final long serialVersionUID = -506628165399209099L;

	public Boolean verificarExisteSolicEspApCongelacao(Integer agdSeq,
			BigDecimal paramApCongelacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaSolicEspecial.class);
		criteria.add(Restrictions.eq(MbcAgendaSolicEspecial.Fields.MBC_AGENDAS_ID.toString(), agdSeq));
		criteria.add(Restrictions.eq(MbcAgendaSolicEspecial.Fields.MBC_NECESSIDADE_CIRURGICAS_ID.toString(), paramApCongelacao.shortValue()));	
		return executeCriteriaCount(criteria) > 0;
	}
	
	public List<MbcAgendaSolicEspecial> buscarMbcAgendaSolicEspecialPorAgdSeq(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaSolicEspecial.class, "ase");
		criteria.createAlias("ase." + MbcAgendaSolicEspecial.Fields.MBC_NECESSIDADE_CIRURGICAS.toString(), "mnc", Criteria.INNER_JOIN);
		criteria.createAlias("mnc." + MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "unf", Criteria.LEFT_JOIN);
		criteria.createAlias("ase." + MbcAgendaSolicEspecial.Fields.MBC_AGENDAS.toString(), "agd", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("ase."+MbcAgendaSolicEspecial.Fields.ID_AGD.toString(), agdSeq));
		return executeCriteria(criteria);
	}
	
	public List<AgendaCirurgiaSolicitacaoEspecialVO> buscarMbcAgendaCirurgiaSolicEspecialPorAgdSeq(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaSolicEspecial.class, "ase");
		criteria.createAlias("ase." + MbcAgendaSolicEspecial.Fields.MBC_NECESSIDADE_CIRURGICAS.toString(), "mnc", Criteria.INNER_JOIN);
		criteria.createAlias("mnc." + MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "unf", Criteria.LEFT_JOIN);
		criteria.createAlias("ase." + MbcAgendaSolicEspecial.Fields.MBC_AGENDAS.toString(), "agd", Criteria.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property("mnc." + MbcNecessidadeCirurgica.Fields.SEQ.toString()), AgendaCirurgiaSolicitacaoEspecialVO.Fields.NCI_SEQ.toString())
			.add(Projections.property("mnc." + MbcNecessidadeCirurgica.Fields.DESCRICAO.toString()), AgendaCirurgiaSolicitacaoEspecialVO.Fields.NCI_DESCRICAO.toString())
			.add(Projections.property("ase." + MbcAgendaSolicEspecial.Fields.ID_SEQP.toString()), AgendaCirurgiaSolicitacaoEspecialVO.Fields.SEQP.toString())
			.add(Projections.property("ase." + MbcAgendaSolicEspecial.Fields.DESCRICAO.toString()), AgendaCirurgiaSolicitacaoEspecialVO.Fields.MSE_DESCRICAO.toString())
			.add(Projections.property("ase." + MbcAgendaSolicEspecial.Fields.ID_AGD.toString()), AgendaCirurgiaSolicitacaoEspecialVO.Fields.AGD_SEQ.toString())
			.add(Projections.property("unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), AgendaCirurgiaSolicitacaoEspecialVO.Fields.UNF_SEQ.toString())
			.add(Projections.property("unf." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), AgendaCirurgiaSolicitacaoEspecialVO.Fields.UNF_DESCRICAO.toString())
		);
		
		criteria.add(Restrictions.eq("ase." + MbcAgendaSolicEspecial.Fields.ID_AGD.toString(), agdSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(AgendaCirurgiaSolicitacaoEspecialVO.class));
		return executeCriteria(criteria);
	}
	
	public Long buscarMbcAgendaSolicEspecialPorNciSeqCount(Short nciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaSolicEspecial.class);
		criteria.add(Restrictions.eq(MbcAgendaSolicEspecial.Fields.MBC_NECESSIDADE_CIRURGICAS_ID.toString(), nciSeq));
		return executeCriteriaCount(criteria);
	}
	
	public Short buscarProximoSeqp(Integer agdSeq, Short nciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaSolicEspecial.class, "ase");
		
		criteria.setProjection(Projections.max(MbcAgendaSolicEspecial.Fields.ID_SEQP.toString()));
		
		criteria.add(Restrictions.eq("ase.".concat(MbcAgendaSolicEspecial.Fields.ID_AGD.toString()), agdSeq));
		criteria.add(Restrictions.eq("ase.".concat(MbcAgendaSolicEspecial.Fields.ID_NCI.toString()), nciSeq));
		
		Short seqp = (Short) executeCriteriaUniqueResult(criteria);
		if(seqp == null) {
			seqp = Short.valueOf("0");
		}
		return ++seqp; 
	}
	
	public MbcAgendaSolicEspecial obterMbcAgendaSolicEspecialPorNciSeqUnfseq(Integer agdSeq, Short nciSeq, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaSolicEspecial.class, "ase");
		criteria.createAlias("ase." + MbcAgendaSolicEspecial.Fields.MBC_NECESSIDADE_CIRURGICAS, "mnc", Criteria.INNER_JOIN);
		criteria.createAlias("mnc." + MbcNecessidadeCirurgica.Fields.AGH_UNIDADES_FUNCIONAIS, "unf", Criteria.LEFT_JOIN);
		criteria.createAlias("ase." + MbcAgendaSolicEspecial.Fields.MBC_AGENDAS, "agd", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("ase." + MbcAgendaSolicEspecial.Fields.ID_AGD.toString(), agdSeq));
		criteria.add(Restrictions.eq("ase." + MbcAgendaSolicEspecial.Fields.ID_NCI.toString(), nciSeq));
		criteria.add(Restrictions.eq("ase." + MbcAgendaSolicEspecial.Fields.ID_SEQP.toString(), seqp));
		
		return (MbcAgendaSolicEspecial) executeCriteriaUniqueResult(criteria);
	}

}
