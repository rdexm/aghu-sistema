package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatAgrupItemConta;

public class FatAgrupItemContaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatAgrupItemConta> {

	private static final long serialVersionUID = -1642290362119563841L;

	public List<FatAgrupItemConta> pesquisarPorCodigoContaHospitalar(
			Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatAgrupItemConta.class);
		criteria.add(Restrictions.eq(FatAgrupItemConta.Fields.CTH_SEQ.toString(),
				cthSeq));
		return this
				.executeCriteria(criteria);
	}
	
	/**
	 * Remove os FatAgrupItemConta relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCodigoContaHospitalar(Integer cthSeq){
		javax.persistence.Query query = createQuery("delete " + FatAgrupItemConta.class.getName() + 
																	   " where " + FatAgrupItemConta.Fields.CTH_SEQ.toString() + " = :cthSeq ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}

	public List<FatAgrupItemConta> buscaItensContaHospitalarOrdemValorDescDthrRealizadoAsc(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatAgrupItemConta.class);

		criteria.add(Restrictions.eq(FatAgrupItemConta.Fields.CTH_SEQ.toString(), cthSeq));
		
		criteria.addOrder(Order.desc(FatAgrupItemConta.Fields.VALOR.toString()));
		criteria.addOrder(Order.asc(FatAgrupItemConta.Fields.DTHR_REALIZADO.toString()));

		return this.executeCriteria(criteria);
	}

}
