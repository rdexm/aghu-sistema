package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigProcessamentoCusto;

public class SigCalculoComponenteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoComponente>{

	private static final long serialVersionUID = 3885592164946056908L;

	public void removerPorProcessamento(Integer idProcessamentoCusto) {
		
		StringBuilder sql = new StringBuilder(31);
		
		sql.append(" DELETE ").append(SigCalculoComponente.class.getSimpleName().toString())
		.append(" ca WHERE ca."+SigCalculoComponente.Fields.PROCESSAMENTO_CUSTO.toString()+"."+SigProcessamentoCusto.Fields.SEQ.toString()+" = :pSeq");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();
	}
	
}