package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.EpeCuidadoUnf;
import br.gov.mec.aghu.model.EpeCuidados;

public class EpeCuidadoUnfDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeCuidadoUnf>  {


	private static final long serialVersionUID = -8793516803765341031L;
	
	public List<EpeCuidadoUnf> pesquisarEpeCuidadoUnf(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidadoUnf.class);
		criteria.add(Restrictions.eq(EpeCuidadoUnf.Fields.CUI_SEQ.toString(), seq));
		
		return executeCriteria(criteria);
	}
	
	public List<EpeCuidadoUnf> obterEpeCuidadoUnfPorEpeCuidadoSeq(Short cuidadoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidadoUnf.class);
		criteria.createAlias(EpeCuidadoUnf.Fields.UNIDADE_FUNCIONAL.toString(), "unidade_funcional", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("unidade_funcional." + AghUnidadesFuncionais.Fields.ALA.toString(), "ala", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(EpeCuidadoUnf.Fields.CUIDADO.toString() + "." +  EpeCuidados.Fields.SEQ.toString(), cuidadoSeq));
		return executeCriteria(criteria);
	}
	
	public List<Short> pesquisarTodos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidadoUnf.class);
		criteria.setProjection(Projections.property(EpeCuidadoUnf.Fields.CUI_SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	public List<Short> pesquisarEpeCuidadoUnfPorUnf(Short unf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCuidadoUnf.class);
		criteria.add(Restrictions.eq(EpeCuidadoUnf.Fields.CUI_UNF.toString(), unf));
		criteria.setProjection(Projections.property(EpeCuidadoUnf.Fields.CUI_SEQ.toString()));
		return executeCriteria(criteria);
	}
	
}