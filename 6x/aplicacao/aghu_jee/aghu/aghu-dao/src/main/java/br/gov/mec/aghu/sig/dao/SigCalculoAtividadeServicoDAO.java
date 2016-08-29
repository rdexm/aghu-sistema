package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.SigCalculoAtividadeServico;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigProcessamentoCusto;

public class SigCalculoAtividadeServicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtividadeServico> {

	private static final long serialVersionUID = 5846511115428030059L;

	public void removerPorProcessamento(Integer idProcessamento) {

		StringBuilder sql = new StringBuilder(68);

		sql.append(" DELETE ").append(SigCalculoAtividadeServico.class.getSimpleName().toString())
		.append(" ca WHERE ca.").append(SigCalculoAtividadeServico.Fields.CALCULO_COMPONENTE.toString()).append('.').append(SigCalculoComponente.Fields.SEQ.toString())
		.append(" IN ( SELECT c.").append(SigCalculoComponente.Fields.SEQ.toString())
		.append(" FROM ").append(SigCalculoComponente.class.getSimpleName())
		.append(" c WHERE c.").append(SigCalculoComponente.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString())
		.append(" = :pSeq ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}

	public SigCalculoAtividadeServico buscaPorParametros(Integer integer, Integer integer2, Integer pmuSeq, Integer cmtSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtividadeServico.class, "calcAtvSe");
		criteria.createCriteria("calcAtvSe." + SigCalculoAtividadeServico.Fields.CALCULO_COMPONENTE.toString(), "calcComp", JoinType.INNER_JOIN);
		criteria.createCriteria("calcComp." + SigCalculoComponente.Fields.PROCESSAMENTO_CUSTO.toString(), "procCusto", JoinType.INNER_JOIN);
		criteria.createCriteria("calcAtvSe." + SigCalculoAtividadeServico.Fields.AF_CONTRATO.toString(), " afContrato", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("calcAtvSe." + SigCalculoAtividadeServico.Fields.AUTORIZACAO_FORNECEDOR.toString(), " auFor", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("calcComp." + SigCalculoComponente.Fields.SEQ.toString(), cmtSeq));
		
		if(integer != null){
			criteria.add(Restrictions.eq("afContrato." + ScoAfContrato.Fields.SEQ.toString(), integer));
		}else{
			criteria.add(Restrictions.eq("auFor." + ScoAutorizacaoForn.Fields.NUMERO.toString(), integer2));
		}
		
		criteria.add(Restrictions.eq("procCusto." + SigProcessamentoCusto.Fields.SEQ.toString(), pmuSeq));

		return (SigCalculoAtividadeServico) executeCriteriaUniqueResult(criteria);
	}
}
