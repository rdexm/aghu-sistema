package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.SigDetalheFolhaPessoa;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;


public class SigDetalheFolhaPessoaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigDetalheFolhaPessoa>{
	
	private static final long serialVersionUID = 5855998509422114895L;

	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(100);
		
		sql.append(" DELETE ").append(SigDetalheFolhaPessoa.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigDetalheFolhaPessoa.Fields.SIG_MVTO_CONTA_MENSAL.toString()).append('.').append(SigMvtoContaMensal.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT c.").append(SigMvtoContaMensal.Fields.SEQ.toString());
			sql.append(" FROM ").append( SigMvtoContaMensal.class.getSimpleName()).append(" c ");
			sql.append(" WHERE c.").append(SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
}