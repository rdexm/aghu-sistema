package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelExtratoDoador;


public class AelExtratoDoadorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExtratoDoador> {


    private static final long serialVersionUID = -2699655455518484736L;

    private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoDoador.class, "AED");
        criteria.createAlias(AelExtratoDoador.Fields.AEL_DOADOR_REDOMES.toString(), "ADR", JoinType.INNER_JOIN);
		return criteria;
    }

	public List<AelExtratoDoador> buscarAelExtratosDoadorPorSeq(Integer seq) {
		
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelExtratoDoador.Fields.SEQ.toString(), seq));

		return executeCriteria(dc);
	}

	}
	
	
	

