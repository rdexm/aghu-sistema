package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaOrgaoTransplante;

public class MbcFichaOrgaoTransplanteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaOrgaoTransplante> {

	private static final long serialVersionUID = -8836221362466179163L;

	public MbcFichaOrgaoTransplante obterFichasOrgaosTransplantes(
			Long seqMbcFichaAnestia, Short seqMbcOrgaoTransplantado) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaOrgaoTransplante.class);
		criteria.add(Restrictions.eq(MbcFichaOrgaoTransplante.Fields.OTR_SEQ.toString(), seqMbcOrgaoTransplantado));
		criteria.add(Restrictions.eq(MbcFichaOrgaoTransplante.Fields.FIC_SEQ.toString(), seqMbcFichaAnestia));
		
		return (MbcFichaOrgaoTransplante) executeCriteriaUniqueResult(criteria);
	}

	public List<MbcFichaOrgaoTransplante> pesquisarMbcFichaOrgaoTransplanteByFichaAnestesia(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaOrgaoTransplante.class);
		criteria.createAlias(MbcFichaOrgaoTransplante.Fields.ORGAO_TRANSPLATADO.toString(), "otr", Criteria.LEFT_JOIN);
		criteria.createAlias(MbcFichaOrgaoTransplante.Fields.FICHA_ANESTESIA.toString(), "fic");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		return executeCriteria(criteria);
	}


}
