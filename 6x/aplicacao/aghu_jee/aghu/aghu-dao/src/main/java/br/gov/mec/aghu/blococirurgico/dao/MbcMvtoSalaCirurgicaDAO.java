package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MbcMvtoSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;

public class MbcMvtoSalaCirurgicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMvtoSalaCirurgica> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7411903571077431460L;

	public MbcMvtoSalaCirurgica obterUltimoMovimentoPorId(MbcSalaCirurgicaId idSalaCirurgica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMvtoSalaCirurgica.class);
		criteria.add(Restrictions.eq(MbcMvtoSalaCirurgica.Fields.UNF_SEQ.toString(), idSalaCirurgica.getUnfSeq()));
		criteria.add(Restrictions.eq(MbcMvtoSalaCirurgica.Fields.SEQP.toString(), idSalaCirurgica.getSeqp()));
		criteria.add(Restrictions.isNull(MbcMvtoSalaCirurgica.Fields.DT_FIM_MVTO.toString()));
		
		List<MbcMvtoSalaCirurgica>  listMbcMvtoSalaCirurgica =  executeCriteria(criteria,0,1,null,false);
		return listMbcMvtoSalaCirurgica != null ? listMbcMvtoSalaCirurgica.get(0) : null;
	}
	
	
	public List<MbcMvtoSalaCirurgica> obterHistoricoSalaCirurgica(Short seqp, Short unfSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMvtoSalaCirurgica.class);
        criteria.createAlias(MbcMvtoSalaCirurgica.Fields.MBC_TIPO_SALA.toString(), "TIPO_SALA", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MbcMvtoSalaCirurgica.Fields.SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(MbcMvtoSalaCirurgica.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.addOrder(Order.desc(MbcMvtoSalaCirurgica.Fields.DT_INICIO_MVTO.toString()));
		criteria.addOrder(Order.desc(MbcMvtoSalaCirurgica.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}
	
	
}
