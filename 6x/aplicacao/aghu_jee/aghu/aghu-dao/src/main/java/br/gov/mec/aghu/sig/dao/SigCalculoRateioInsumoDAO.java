package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioInsumo;
import br.gov.mec.aghu.model.SigProcessamentoCusto;

public class SigCalculoRateioInsumoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoRateioInsumo> {

	private static final long serialVersionUID = 7920824887851896176L;

	public void removerPorProcessamento(Integer idProcessamento) {

		StringBuilder sql = new StringBuilder(100);

		sql.append(" DELETE " ).append( SigCalculoRateioInsumo.class.getSimpleName().toString() ).append( " ca ");
		sql.append(" WHERE ca." ).append( SigCalculoRateioInsumo.Fields.CALCULO_OBJETO_CUSTO.toString() ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ.toString());
		sql.append(" IN ( ");
		sql.append(" SELECT c." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString());
		sql.append(" FROM " ).append( SigCalculoObjetoCusto.class.getSimpleName() ).append( " c ");
		sql.append(" WHERE c." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pSeq");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
}