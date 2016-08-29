package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MptDiaTipoSessao;

public class MptDiaTipoSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptDiaTipoSessao> {

	private static final long serialVersionUID = -7571768985024057005L;

	public MptDiaTipoSessao obterDiaTipoSessaoPorDiaTpsSeq(Byte dia, Short tpsSeq) {
		DetachedCriteria criteria = obterCriteriaPorDiaTpsSeq(dia, tpsSeq);
		
		return (MptDiaTipoSessao) executeCriteriaUniqueResult(criteria);
	}
	
	public boolean verificarExistenciaDiaTipoSessaoPorDiaTpsSeq(Byte dia, Short tpsSeq) {
		DetachedCriteria criteria = obterCriteriaPorDiaTpsSeq(dia, tpsSeq);
		
		return executeCriteriaExists(criteria);
	}

	private DetachedCriteria obterCriteriaPorDiaTpsSeq(Byte dia, Short tpsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptDiaTipoSessao.class);
		criteria.add(Restrictions.eq(MptDiaTipoSessao.Fields.DIA.toString(), dia));
		criteria.add(Restrictions.eq(MptDiaTipoSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		return criteria;
	}
	
	public List<MptDiaTipoSessao> obterDiasPorTipoSessao(Short tpsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptDiaTipoSessao.class);
		criteria.add(Restrictions.eq(MptDiaTipoSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		
		return executeCriteria(criteria);
	}
}