package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMicro;


public class AelGrpTxtPadraoMicroDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrpTxtPadraoMicro>  {
	
	private static final long serialVersionUID = 825606538135189649L;

	public List<AelGrpTxtPadraoMicro> pesquisarGrupoTextoPadraoMicro(
			Short codigo, String descricao, DominioSituacao situacao) {
		
		DetachedCriteria criteria =  DetachedCriteria.forClass(AelGrpTxtPadraoMicro.class);
		
		if (codigo != null) {
			criteria.add(Restrictions.eq(AelGrpTxtPadraoMicro.Fields.SEQ.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AelGrpTxtPadraoMicro.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		
		if (situacao != null) {
			criteria.add(Restrictions.eq(AelGrpTxtPadraoMicro.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		criteria.addOrder(Order.asc(AelGrpTxtPadraoMicro.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
		
	}	
	
}
