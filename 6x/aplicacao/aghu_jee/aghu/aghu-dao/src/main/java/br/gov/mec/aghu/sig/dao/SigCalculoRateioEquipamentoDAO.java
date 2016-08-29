package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioEquipamento;
import br.gov.mec.aghu.model.SigProcessamentoCusto;


public class SigCalculoRateioEquipamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoRateioEquipamento>{
	
	private static final long serialVersionUID = -6458850553586539783L;

	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(100);
		
		sql.append(" DELETE ").append(SigCalculoRateioEquipamento.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigCalculoRateioEquipamento.Fields.CALCULO_OBJETO_CUSTO.toString()).append('.').append(SigCalculoObjetoCusto.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT c.").append(SigCalculoObjetoCusto.Fields.SEQ.toString());
			sql.append(" FROM ").append( SigCalculoObjetoCusto.class.getSimpleName()).append(" c ");
			sql.append(" WHERE c.").append(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		//		createQuery("DELETE SigCalculoRateioEquipamento cre where cre.sigCalculoObjetoCustos.seq IN " +
		//			"(SELECT C.seq FROM SigCalculoObjetoCusto c WHERE c.sigProcessamentoCustos.seq = :pSeq ) ");
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
}
