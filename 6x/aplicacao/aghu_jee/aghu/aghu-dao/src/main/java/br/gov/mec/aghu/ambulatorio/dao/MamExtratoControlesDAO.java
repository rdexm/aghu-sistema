package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamExtratoControles;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;

public class MamExtratoControlesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamExtratoControles> {

	
	private static final long serialVersionUID = 5621100995885711010L;

	public Short nextSeqp(MamControles controle){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoControles.class);
		criteria.setProjection(Projections.max(MamExtratoControles.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(MamExtratoControles.Fields.CTL_SEQ.toString(), controle.getSeq()));
		Short value = (Short) executeCriteriaUniqueResult(criteria);
		if (value==null) {
			value=Short.valueOf("0");
		}
		return ++value;
	}

	public Short buscaProximoSeqp(Long ctlSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoControles.class);
		
		criteria.setProjection(Projections.max(MamExtratoControles.Fields.SEQP.toString()));

		criteria.add(Restrictions.eq(MamExtratoControles.Fields.CTL_SEQ.toString(), ctlSeq));

		Short result = (Short) executeCriteriaUniqueResult(criteria);
		if (result == null) {
			result = (short) 0;
		}
		result++;

		return result;
	}


	public List<MamExtratoControles> listarExtratosControlesPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoControles.class);

		criteria.createAlias(MamExtratoControles.Fields.CONTROLE.toString(), "controle");

		criteria.createAlias("controle." + MamControles.Fields.CONSULTA.toString(), "consulta");

		criteria.add(Restrictions.eq("consulta." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));

		return executeCriteria(criteria);
	}
	
	public List<MamExtratoControles> pesquisarExtratoControlePorNumeroControleESituacaoNaoCancelado(Long ctlSeq, Short seqCancelado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoControles.class);
		criteria.add(Restrictions.eq(MamExtratoControles.Fields.CTL_SEQ.toString(), ctlSeq));
		criteria.add(Restrictions.ne(MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString()+"."+MamSituacaoAtendimentos.Fields.SEQ.toString(), seqCancelado));
		criteria.addOrder(Order.desc(MamExtratoControles.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarExtratoControlePorNumeroConSultaESituacaoConcluidoCount(Integer conNumero, Short seqConcluido) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoControles.class);
		criteria.createAlias(MamExtratoControles.Fields.CONTROLE.toString(), MamExtratoControles.Fields.CONTROLE.toString());
		criteria.add(Restrictions.eq(MamExtratoControles.Fields.CONTROLE.toString()+"."+MamControles.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq(MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString()+"."+MamSituacaoAtendimentos.Fields.SEQ.toString(), seqConcluido));
		return executeCriteriaCount(criteria);
	}
	
}