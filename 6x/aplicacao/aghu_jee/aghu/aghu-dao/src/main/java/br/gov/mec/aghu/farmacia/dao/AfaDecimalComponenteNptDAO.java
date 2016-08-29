package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaDecimalComponenteNpt;




public class AfaDecimalComponenteNptDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaDecimalComponenteNpt> {

	private static final long serialVersionUID = 4070084250474804889L;

	
	// #3504 C6
	public List<AfaDecimalComponenteNpt> listarPorMatCodigo(Integer cod) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDecimalComponenteNpt.class);
	
		criteria.add(Restrictions.eq(AfaDecimalComponenteNpt.Fields.CNP_MED_MAT_CODIGO.toString(), cod));
		
		criteria.addOrder(Order.asc(AfaDecimalComponenteNpt.Fields.SEQP.toString()));
		//criteria.setResultTransformer(Transformers.aliasToBean(ComponenteNPTVO.class));
		return this.executeCriteria(criteria);
	}
	
	public Short obterCasaComponenteCount(Integer componenteSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDecimalComponenteNpt.class);
		criteria.add(Restrictions.eq(AfaDecimalComponenteNpt.Fields.CNP_MED_MAT_CODIGO.toString(), componenteSeq));
		criteria.addOrder(Order.desc(AfaDecimalComponenteNpt.Fields.SEQP.toString()));
		List<AfaDecimalComponenteNpt> lista = this.executeCriteria(criteria);
		if(lista != null){
			if(lista.size() > 0){
				return lista.get(0).getId().getSeqp();
			}
		}
		return (short)0;
	}
	
	public List<AfaDecimalComponenteNpt> obterAfaDecimalComponenteNptPorCodMatPeso(Integer medMatCodigo, Double peso) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaDecimalComponenteNpt.class);
		criteria.add(Restrictions.eq(AfaDecimalComponenteNpt.Fields.CNP_MED_MAT_CODIGO.toString(), medMatCodigo));
		criteria.add(Restrictions.le(AfaDecimalComponenteNpt.Fields.PESO_INICIAL.toString(), peso));
		criteria.add(Restrictions.ge(AfaDecimalComponenteNpt.Fields.PESO_FINAL.toString(), peso));
		return this.executeCriteria(criteria);
	}
}
