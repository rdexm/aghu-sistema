package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.view.VMamReceitas;


public class VMamReceitasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMamReceitas> {

	private static final long serialVersionUID = 345471680543302753L;
	
	public List<VMamReceitas> obterListaVMamReceitas(Integer pacCodigo, Integer conNumero, Integer atdSeq, Date dtCriacao, DominioTipoReceituario dominioTipoReceituario){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VMamReceitas.class, "VRE");
	
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(VMamReceitas.Fields.IND_INTERNO.toString()).as(VMamReceitas.Fields.IND_INTERNO.toString()))
				.add(Projections.property(VMamReceitas.Fields.DESCRICAO.toString()).as(VMamReceitas.Fields.DESCRICAO.toString()))
				.add(Projections.property(VMamReceitas.Fields.QUANTIDADE.toString()).as(VMamReceitas.Fields.QUANTIDADE.toString()))
				.add(Projections.property(VMamReceitas.Fields.FORMA_USO.toString()).as(VMamReceitas.Fields.FORMA_USO.toString()))
				.add(Projections.property(VMamReceitas.Fields.IND_USO_CONTINUO.toString()).as(VMamReceitas.Fields.IND_USO_CONTINUO.toString()))
				.add(Projections.property(VMamReceitas.Fields.TIPO.toString()).as(VMamReceitas.Fields.TIPO.toString()))
				);

		criteria.setResultTransformer(Transformers.aliasToBean(VMamReceitas.class));
		
		criteria.add(Restrictions.eq(VMamReceitas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(VMamReceitas.Fields.TIPO.toString(), dominioTipoReceituario));
		
		if(conNumero != null){
			criteria.add(Restrictions.eq(VMamReceitas.Fields.CON_NUMERO.toString(), conNumero));
		}
		
		criteria.addOrder(Order.asc(VMamReceitas.Fields.TIPO.toString()));
		criteria.addOrder(Order.asc(VMamReceitas.Fields.IND_USO_CONTINUO.toString()));
		criteria.addOrder(Order.asc(VMamReceitas.Fields.IND_INTERNO.toString()));
		criteria.addOrder(Order.asc(VMamReceitas.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}
}