package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProducaoObjetoCusto;

public class SigProducaoObjetoCustoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigProducaoObjetoCusto> {

	private static final long serialVersionUID = 4963644129515648286L;

	public void removerPorProcessamento(Integer idProcessamento) {
		
		Query query = this.createQuery(
				"DELETE "+SigProducaoObjetoCusto.class.getSimpleName()+" c where c."+SigProducaoObjetoCusto.Fields.CALCULO_OBJETO_CUSTOS+"."+SigCalculoObjetoCusto.Fields.SEQ+" IN "
						+ "(SELECT C.seq FROM "+SigCalculoObjetoCusto.class.getSimpleName()+" c where c."+SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS+"."+SigProcessamentoCusto.Fields.SEQ+" = :pSeq)");
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
}