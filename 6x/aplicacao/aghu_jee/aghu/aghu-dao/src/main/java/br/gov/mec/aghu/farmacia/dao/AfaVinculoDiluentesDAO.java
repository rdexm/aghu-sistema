package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaVinculoDiluentes;

public class AfaVinculoDiluentesDAO extends BaseDao<AfaVinculoDiluentes>{
	
	private static final long serialVersionUID = -7753173294407610937L;

	public Long pesquisarDiluentesCount(AfaMedicamento medicamento) {
		DetachedCriteria criteria = recuperarDiluentesPorCodigoMedicamento(medicamento);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<AfaVinculoDiluentes> recuperarListaPaginadaDiluentes(Integer firstResult,Integer maxResult, String orderProperty, boolean asc, AfaMedicamento medicamento) {
		DetachedCriteria criteria = recuperarDiluentesPorCodigoMedicamento(medicamento);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public DetachedCriteria recuperarDiluentesPorCodigoMedicamento(AfaMedicamento medicamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaVinculoDiluentes.class);
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.MEDICAMENTO.toString(), medicamento));
		
		return criteria;
	}

	public Long verificaUsualPrescricao(AfaVinculoDiluentes diluente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaVinculoDiluentes.class);
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.MEDICAMENTO.toString(), diluente.getMedicamento()));
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.IND_USUAL_PRESCRICAO.toString(), true));
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteriaCount(criteria);
	}
	
	public AfaVinculoDiluentes buscarUsualPrescricaoPorMedicamento(Integer codigoMedicamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaVinculoDiluentes.class);
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.SEQ_MEDICAMENTO.toString(), codigoMedicamento));
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.IND_USUAL_PRESCRICAO.toString(), true));
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return (AfaVinculoDiluentes) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AfaVinculoDiluentes> recuperaListaVinculoDiluente(String descricaoMedicamento, AfaMedicamento medicamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaVinculoDiluentes.class);
		
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.MEDICAMENTO.toString(), medicamento));
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if (StringUtils.isNotBlank(descricaoMedicamento)) {
			criteria.createAlias(AfaVinculoDiluentes.Fields.DILUENTE.toString(), "DILU");
			criteria.add(Restrictions.ilike("DILU" + "." + AfaMedicamento.Fields.DESCRICAO.toString(), descricaoMedicamento, MatchMode.ANYWHERE));
		}
	
		return executeCriteria(criteria);
	}
	
	public Long recuperarVinculoDiluente(AfaMedicamento medicamento, AfaMedicamento diluente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaVinculoDiluentes.class);
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.MEDICAMENTO.toString(), medicamento));
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.DILUENTE.toString(), diluente));
		
		return this.executeCriteriaCount(criteria);
	}
	
	public Boolean verificarVinculoDiluenteExistente(AfaMedicamento medicamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaVinculoDiluentes.class);
		criteria.add(Restrictions.eq(AfaVinculoDiluentes.Fields.DILUENTE.toString(), medicamento));
		
		return executeCriteriaExists(criteria);
	}
	
	
}



