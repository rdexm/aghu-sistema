package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.SigCalculoIndiretoPessoa;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigProcessamentoCusto;

public class SigCalculoIndiretoPessoaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoIndiretoPessoa>{
	
	private static final long serialVersionUID = -34788956890765690L;
	
	public void removerPorProcessamento(Integer idProcessamentoCusto) {
		StringBuilder sql = new StringBuilder(100);
		sql.append(" DELETE " ).append( SigCalculoIndiretoPessoa.class.getSimpleName().toString() ).append( " cie ");
		sql.append(" WHERE cie." ).append( SigCalculoIndiretoPessoa.Fields.CALCULO_OBJETO_CUSTO.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString());
		sql.append(" IN ( ");
		sql.append(" SELECT c." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString());
		sql.append(" FROM " ).append( SigCalculoObjetoCusto.class.getSimpleName() ).append( " c ");
		sql.append(" WHERE c." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pSeq");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();	
	}
}