package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoEscalaLeitoRecemNascido;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class McoEscalaLeitoRecemNascidoDAO extends BaseDao<McoEscalaLeitoRecemNascido>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9057194759257747912L;
	
	public List<McoEscalaLeitoRecemNascido> pesquisarMcoEscalaLeitoRecemNascido(String leito, Short vinCodigo, Integer matricula) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoEscalaLeitoRecemNascido.class);
		if(StringUtils.isNotBlank(leito)) {
			criteria.add(Restrictions.ilike(McoEscalaLeitoRecemNascido.Fields.LEITO.toString(), leito, MatchMode.ANYWHERE));
		}
		if(vinCodigo != null) {
			criteria.add(Restrictions.eq(McoEscalaLeitoRecemNascido.Fields.SERVIDOR_VIN_CODIGO_RESPONSAVEL.toString(), vinCodigo));
		}
		if(matricula != null) {
			criteria.add(Restrictions.eq(McoEscalaLeitoRecemNascido.Fields.SERVIDOR_MATRICULA_RESPONSAVEL.toString(), matricula));
		}
		criteria.addOrder(Order.asc(McoEscalaLeitoRecemNascido.Fields.LEITO.toString()));
		return executeCriteria(criteria);
	}
}
