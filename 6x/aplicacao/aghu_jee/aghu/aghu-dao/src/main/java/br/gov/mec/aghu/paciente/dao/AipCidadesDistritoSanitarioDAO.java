package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipCidadesDistritoSanitario;

public class AipCidadesDistritoSanitarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipCidadesDistritoSanitario>{
	
	private static final long serialVersionUID = -4061811976134643541L;

	public List<AipCidadesDistritoSanitario> obterAipCidadesDistritoSanitarioPorAipDistritoSanitario(Short dstCodigo){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AipCidadesDistritoSanitario.class);
		
		criteria.createAlias(AipCidadesDistritoSanitario.Fields.AIP_CIDADE.toString(), "CID");
		criteria.add(Restrictions.eq(AipCidadesDistritoSanitario.Fields.DST_CODIGO.toString(), dstCodigo));
		criteria.addOrder(Order.asc("CID."+AipCidades.Fields.NOME.toString()));
		
		criteria.setFetchMode("CID."+AipCidades.Fields.UF.toString(), FetchMode.JOIN);
		
		return executeCriteria(criteria);
	}
}
