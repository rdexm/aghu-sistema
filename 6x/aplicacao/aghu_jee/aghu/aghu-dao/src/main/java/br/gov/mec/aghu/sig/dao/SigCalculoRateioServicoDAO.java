package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCalculoRateioServico;
import br.gov.mec.aghu.model.SigProcessamentoCusto;


public class SigCalculoRateioServicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoRateioServico>{
	
	private static final long serialVersionUID = 6519597331348657217L;

	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(100);
		
		sql.append(" DELETE ").append(SigCalculoRateioServico.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigCalculoRateioServico.Fields.CALCULO_OBJETO_CUSTO.toString()).append('.').append(SigCalculoObjetoCusto.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT c.").append(SigCalculoObjetoCusto.Fields.SEQ.toString());
			sql.append(" FROM ").append( SigCalculoObjetoCusto.class.getSimpleName()).append(" c ");
			sql.append(" WHERE c.").append(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		//		createQuery("DELETE SigCalculoRateioServico crp where crp.sigCalculoObjetoCustos.seq IN " +
		//			"(SELECT C.seq FROM SigCalculoObjetoCusto c WHERE c.sigProcessamentoCustos.seq = :pSeq ) ");
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
	
	public boolean verificarContratoContabilizado(ScoContrato contrato){
		boolean contabilizado = false;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoRateioServico.class);
		criteria.createAlias(SigCalculoRateioServico.Fields.AF_CONTRATO.toString(), "afContrato");
		criteria.add(Restrictions.eq("afContrato." + ScoAfContrato.Fields.CONTRATO.toString(), contrato));

		contabilizado = executeCriteriaExists(criteria);
				
		return contabilizado;
	}
	
}
