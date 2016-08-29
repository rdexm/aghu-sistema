package br.gov.mec.aghu.indicadores.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

import br.gov.mec.aghu.model.AghDatasIg;

public class AghDatasIgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghDatasIg> {
	
	private static final long serialVersionUID = 1066271858029971198L;

	/**
	 * Método para obter a primeira data final da tabela AghDatasIg.
	 * 
	 * @return
	 */
	public Date obterUltimaDataInicial() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghDatasIg.class);
		
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.max(AghDatasIg.Fields.DT_INICIAL.toString()));
		criteria.setProjection(pList);
		
		return (Date) this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Método para retornar um objeto do tipo AghDatasIg com o registro que
	 * possui a data incial e final (essa tabela deverá ter um único registro).
	 * @return
	 */
	public List<AghDatasIg> obterAghDataIg() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghDatasIg.class);
		
		criteria.addOrder(Order.desc(AghDatasIg.Fields.DT_INICIAL.toString()));
		criteria.addOrder(Order.desc(AghDatasIg.Fields.DT_FINAL.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Método para atualizar a data inicial e data final da tabela AGH_DATAS_IG
	 * com as datas passadas por parâmetro.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 */
	public void atualizarDatasIg(Date dataInicial, Date dataFinal) {
		Query query = createQuery(
				"update " + AghDatasIg.class.getName() + " set "
						+ AghDatasIg.Fields.DT_INICIAL.toString() + " = :dataInicial, "
						+ AghDatasIg.Fields.DT_FINAL.toString() + " = :dataFinal");
		
		query.setParameter("dataInicial", dataInicial);
		query.setParameter("dataFinal", dataFinal);
		
		query.executeUpdate();

		this.flush();
	}
		
}
