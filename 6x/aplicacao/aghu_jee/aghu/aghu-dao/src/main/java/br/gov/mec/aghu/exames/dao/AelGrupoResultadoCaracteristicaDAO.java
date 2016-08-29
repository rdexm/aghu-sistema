package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelGrupoResultadoCaracteristicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoResultadoCaracteristica>{
	
	
	 private static final long serialVersionUID = 3581938580900970773L;

	@SuppressWarnings("unchecked")
	 public AelGrupoResultadoCaracteristica obterOriginal(Integer id) {
	
		StringBuilder hql = new StringBuilder(100);
	
		hql.append("select o.").append(AelGrupoResultadoCaracteristica.Fields.SEQ.toString());
		hql.append(", o.").append(AelGrupoResultadoCaracteristica.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelGrupoResultadoCaracteristica.Fields.DESCRICAO.toString());
		hql.append(", o.").append(AelGrupoResultadoCaracteristica.Fields.SITUACAO.toString());
		hql.append(", o.").append(AelGrupoResultadoCaracteristica.Fields.ORDEM_IMPRESSAO.toString());
		hql.append(", o.").append(AelGrupoResultadoCaracteristica.Fields.SERVIDOR.toString());

		
		hql.append(" from ").append(AelGrupoResultadoCaracteristica.class.getSimpleName()).append(" o ");
		
		hql.append(" where o.").append(AelGrupoResultadoCaracteristica.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);
		
		AelGrupoResultadoCaracteristica original = null;
		List<Object[]> camposList = (List<Object[]>) query.getResultList();
		
		if(camposList != null && camposList.size() > 0) {
			
			Object[] campos = camposList.get(0);
			original = new AelGrupoResultadoCaracteristica();
			
			original.setSeq(id);
			original.setCriadoEm((Date) campos[1]);
			original.setDescricao((String) campos[2]);
			original.setSituacao((DominioSituacao) campos[3]);
			original.setOrdemImpressao((Integer) campos[4]);
			original.setServidor((RapServidores) campos[5]);
		}
		
		return original;
	
	 }
	
	
	/**
	 * Pesquisa Grupo Resultado Característica por seq ou descrição
	 * @param objPesquisa
	 * @return
	 */
	public List<AelGrupoResultadoCaracteristica> pesquisarGrupoResultadoCaracteristicaPorSeqDescricao(Object objPesquisa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoResultadoCaracteristica.class);

		if (objPesquisa != null) {
			
			String strPesquisa = (String) objPesquisa;
			
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(AelGrupoResultadoCaracteristica.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(AelGrupoResultadoCaracteristica.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa), MatchMode.ANYWHERE));
			}
			
		}

		criteria.addOrder(Order.asc(AelGrupoResultadoCaracteristica.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria);
	}
	
	
	public AelGrupoResultadoCaracteristica obterGrupoResultadoCaracteristicaPorDescricao(String objPesquisa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoResultadoCaracteristica.class);
		criteria.add(Restrictions.eq(AelGrupoResultadoCaracteristica.Fields.DESCRICAO.toString(), StringUtils.trim(objPesquisa)));

		return (AelGrupoResultadoCaracteristica)this.executeCriteriaUniqueResult(criteria);
	}
	
	
	
	
	public List<AelGrupoResultadoCaracteristica> pesquisarGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = this.obterCriteriaPesquisa(grupoResultadoCaracteristica);
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	
	public Long pesquisarGrupoResultadoCaracteristicaCount(AelGrupoResultadoCaracteristica grupoResultadoCaracteristica) {
		DetachedCriteria criteria = obterCriteriaPesquisa(grupoResultadoCaracteristica);
		
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaPesquisa(AelGrupoResultadoCaracteristica grupoResultadoCaracteristica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoResultadoCaracteristica.class);
		
		if(grupoResultadoCaracteristica.getSeq() != null) {
			criteria.add(Restrictions.eq(AelGrupoResultadoCaracteristica.Fields.SEQ.toString(), 
					grupoResultadoCaracteristica.getSeq()));
		}
		
		if(StringUtils.isNotBlank(grupoResultadoCaracteristica.getDescricao())) {
			criteria.add(Restrictions.ilike(
					AelGrupoResultadoCaracteristica.Fields.DESCRICAO.toString(), 
						grupoResultadoCaracteristica.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if(grupoResultadoCaracteristica.getOrdemImpressao() != null) {
			criteria.add(Restrictions.eq(
					AelGrupoResultadoCaracteristica.Fields.ORDEM_IMPRESSAO.toString(), 
					grupoResultadoCaracteristica.getOrdemImpressao()));
		}
		
		if(grupoResultadoCaracteristica.getSituacao() != null) {
			criteria.add(Restrictions.eq(
					AelGrupoResultadoCaracteristica.Fields.SITUACAO.toString(), 
					grupoResultadoCaracteristica.getSituacao()));
		}
		
		return criteria;
	}
	
}
