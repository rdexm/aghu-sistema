package br.gov.mec.aghu.controlepaciente.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EcpLimiteItemControle;
/**
 * 
 * @modulo controlepaciente.cadastrosbasicos
 *
 */
public class EcpLimiteItemControleDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EcpLimiteItemControle> {

	private static final long serialVersionUID = 5519389159566228740L;

	public Long pesquisarLimitesItemControleCount(EcpItemControle itemControle) {
		DetachedCriteria criteria = montaCriteriaPesquisarLimitesItemControle(itemControle);
		return this.executeCriteriaCount(criteria);
	}

	public List<EcpLimiteItemControle> pesquisarLimitesItemControle(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, EcpItemControle itemControle) {
		
		List<EcpLimiteItemControle> resultPagina = new ArrayList<EcpLimiteItemControle>();
		List<EcpLimiteItemControle> resultAll = new ArrayList<EcpLimiteItemControle>();
		List<EcpLimiteItemControle> resultD;
		List<EcpLimiteItemControle> resultM;
		List<EcpLimiteItemControle> resultA;
		
		DetachedCriteria criteriaDia = montaCriteriaPesquisarLimitesItemControleDia(itemControle, orderProperty);
		DetachedCriteria criteriaMes = montaCriteriaPesquisarLimitesItemControleMes(itemControle, orderProperty);
		DetachedCriteria criteriaAno = montaCriteriaPesquisarLimitesItemControleAno(itemControle, orderProperty);
		
		resultD = this.executeCriteria(criteriaDia);
		resultM = this.executeCriteria(criteriaMes);
		resultA = this.executeCriteria(criteriaAno);
		
		if(!resultD.isEmpty()){
			resultAll.addAll(resultD);
		}
		
		if(!resultM.isEmpty()){
			resultAll.addAll(resultM);
		}
		
		if(!resultA.isEmpty()){
			resultAll.addAll(resultA);
		}
		
		if(!resultAll.isEmpty() && (resultAll.size() > firstResult)){
			
			int limite = maxResult + firstResult;
			
			for (int i = firstResult; (i < limite && i < resultAll.size()); i++) {
				resultPagina.add(resultAll.get(i));
			}
		}
		
		return resultPagina;
	}

	private DetachedCriteria montaCriteriaPesquisarLimitesItemControle(EcpItemControle itemControle) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpLimiteItemControle.class);
		
		if(itemControle != null && itemControle.getSeq() != null){
			criteria.add(Restrictions.eq(EcpLimiteItemControle.Fields.ITEM_CONTROLE.toString(), itemControle));
		}
				
		return criteria;
	}
	
	private DetachedCriteria montaCriteriaPesquisarLimitesItemControleDia(EcpItemControle itemControle, String orderProperty) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpLimiteItemControle.class);
		
		if(itemControle != null && itemControle.getSeq() != null){
			criteria.add(Restrictions.eq(EcpLimiteItemControle.Fields.ITEM_CONTROLE.toString(), itemControle));
		}
		
		criteria.add(Restrictions.eq(EcpLimiteItemControle.Fields.MEDIDA_IDADE.toString(), DominioUnidadeMedidaIdade.D));
		criteria.addOrder(Order.asc(orderProperty));
				
		return criteria;
	}
	
	private DetachedCriteria montaCriteriaPesquisarLimitesItemControleMes(EcpItemControle itemControle, String orderProperty) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpLimiteItemControle.class);
		
		if(itemControle != null && itemControle.getSeq() != null){
			criteria.add(Restrictions.eq(EcpLimiteItemControle.Fields.ITEM_CONTROLE.toString(), itemControle));
		}
		
		criteria.add(Restrictions.eq(EcpLimiteItemControle.Fields.MEDIDA_IDADE.toString(), DominioUnidadeMedidaIdade.M));
		criteria.addOrder(Order.asc(orderProperty));
				
		return criteria;
	}
	
	private DetachedCriteria montaCriteriaPesquisarLimitesItemControleAno(EcpItemControle itemControle, String orderProperty) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpLimiteItemControle.class);
		
		if(itemControle != null && itemControle.getSeq() != null){
			criteria.add(Restrictions.eq(EcpLimiteItemControle.Fields.ITEM_CONTROLE.toString(), itemControle));
		}
		
		criteria.add(Restrictions.eq(EcpLimiteItemControle.Fields.MEDIDA_IDADE.toString(), DominioUnidadeMedidaIdade.A));
		criteria.addOrder(Order.asc(orderProperty));
				
		return criteria;
	}
	
	/**
	 * Busca limite de Erro e Normalidade para um item por medidaIdade e Idade
	 * Se tiver mais de um retorna o primeiro em ordem Idade_Minima
	 * @param itemControle
	 * @param medidaIdade
	 * @param idade
	 * @return
	 */
	public EcpLimiteItemControle buscaLimiteItemControle(
			EcpItemControle itemControle,DominioUnidadeMedidaIdade medidaIdade, Integer idade ) {

		EcpLimiteItemControle limiteItemControle = null;
		Byte  idadeByte  = idade.byteValue();
        Short idadeShort = idade.shortValue();
        
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpLimiteItemControle.class);
		criteria.add(Restrictions.eq(
				EcpLimiteItemControle.Fields.ITEM_CONTROLE.toString(), itemControle));
		criteria.add(Restrictions.eq(
				EcpLimiteItemControle.Fields.MEDIDA_IDADE.toString(), medidaIdade));
		criteria.add(Restrictions.le(
				EcpLimiteItemControle.Fields.IDADE_MINIMA.toString(), idadeByte  ));
		criteria.add(Restrictions.ge(
				EcpLimiteItemControle.Fields.IDADE_MAXIMA.toString(), idadeShort ));
		
		criteria.addOrder(Order.asc(EcpLimiteItemControle.Fields.IDADE_MINIMA.toString()));	
		
		List<EcpLimiteItemControle> lstLimite = executeCriteria(criteria);

		if(lstLimite != null && lstLimite.size() > 0){
			limiteItemControle = lstLimite.get(0); 
		}
		return limiteItemControle;
	}
	
	public List<EcpLimiteItemControle> pesquisarLimitesItemControle(EcpItemControle itemControle) {
		DetachedCriteria criteria = montaCriteriaPesquisarLimitesItemControle(itemControle);
		return this.executeCriteria(criteria);
	}
	
	public Long obterNumeroRegistrosItemPorControle(EcpItemControle itemControle) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpLimiteItemControle.class);
		criteria.add(Restrictions.eq(
				EcpLimiteItemControle.Fields.ITEM_CONTROLE.toString()+"."+EcpItemControle.Fields.SEQ.toString(),
				itemControle.getSeq()));
		return executeCriteriaCount(criteria);
	}
}
