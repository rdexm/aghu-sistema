package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MtxTipoRetorno;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;

public class MtxTipoRetornoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxTipoRetorno> {

	
	private static final long serialVersionUID = -1903280417765871209L;

	public List<MtxTipoRetorno> pesquisarTipoRetorno(MtxTipoRetorno mtxTipoRetorno){
		return pesquisarTipoRetorno(mtxTipoRetorno, false);
	}
	
	public List<MtxTipoRetorno> pesquisarTipoRetorno(MtxTipoRetorno mtxTipoRetorno, boolean checarRegistroIgual){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTipoRetorno.class);
		AddCriteria.EQ.filtro(criteria, MtxTipoRetorno.Fields.IND_SITUACAO.toString(), mtxTipoRetorno.getIndSituacao());
		AddCriteria.EQ.filtro(criteria, MtxTipoRetorno.Fields.IND_TIPO.toString(), mtxTipoRetorno.getIndTipo());
		if(checarRegistroIgual){
			AddCriteria.EQ_I.filtro(criteria, MtxTipoRetorno.Fields.DESCRICAO.toString(), mtxTipoRetorno.getDescricao());
			AddCriteria.NE.filtro(criteria, MtxTipoRetorno.Fields.SEQ.toString(), mtxTipoRetorno.getSeq());
		}else{
			AddCriteria.ILIKE.filtro(criteria, MtxTipoRetorno.Fields.DESCRICAO.toString(), mtxTipoRetorno.getDescricao());
		}
		criteria.addOrder(Order.asc(MtxTipoRetorno.Fields.IND_TIPO.toString()));
		criteria.addOrder(Order.asc(MtxTipoRetorno.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public void gravarAtualizarTipoRetorno(MtxTipoRetorno mtxTipoRetorno) throws ApplicationBusinessException, BaseListException{
		if(mtxTipoRetorno.getSeq() == null){
			persistir(mtxTipoRetorno);
		}else{
			atualizar(mtxTipoRetorno);
		}
		flush();
	}
	
	public enum AddCriteria{
		EQ{
			public void filtro(DetachedCriteria criteria, String propertyName, Object value) {
				if(value != null){
					criteria.add(Restrictions.eq(propertyName, value));
				}
			}
		},
		EQ_I{
			public void filtro(DetachedCriteria criteria, String propertyName, Object value) {
				if(value != null){
					criteria.add(Restrictions.eq(propertyName, value).ignoreCase());
				}
			}
		},
		NE{
			public void filtro(DetachedCriteria criteria, String propertyName, Object value) {
				if(value != null){
					criteria.add(Restrictions.ne(propertyName, value));
				}
			}
		},
		LE{
			public void filtro(DetachedCriteria criteria, String propertyName, Object value) {
				if(value != null){
					criteria.add(Restrictions.le(propertyName, value));
				}
			}
		},
		GE{
			public void filtro(DetachedCriteria criteria, String propertyName, Object value) {
				if(value != null){
					criteria.add(Restrictions.ge(propertyName, value));
				}
			}
		},
		ILIKE{
			public void filtro(DetachedCriteria criteria, String propertyName, Object value) {
				if(value != null){
					criteria.add(Restrictions.ilike(propertyName, (String) value, MatchMode.ANYWHERE));
				}
			}
		};
		public abstract void filtro(DetachedCriteria criteria, String propertyName, Object value);
	}
	
	
}