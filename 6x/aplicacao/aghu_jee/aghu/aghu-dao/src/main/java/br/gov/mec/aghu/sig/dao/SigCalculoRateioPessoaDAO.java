package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioPessoa;
import br.gov.mec.aghu.model.SigProcessamentoCusto;


public class SigCalculoRateioPessoaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoRateioPessoa>{
	
	private static final long serialVersionUID = -5283754828482664284L;

	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(100);
		
		sql.append(" DELETE ").append(SigCalculoRateioPessoa.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigCalculoRateioPessoa.Fields.CALCULO_OBJETO_CUSTO.toString()).append('.').append(SigCalculoObjetoCusto.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT c.").append(SigCalculoObjetoCusto.Fields.SEQ.toString());
			sql.append(" FROM ").append( SigCalculoObjetoCusto.class.getSimpleName()).append(" c ");
			sql.append(" WHERE c.").append(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
}
