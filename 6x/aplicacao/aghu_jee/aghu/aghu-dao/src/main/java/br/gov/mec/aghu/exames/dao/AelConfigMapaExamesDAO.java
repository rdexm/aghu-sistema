package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelConfigMapa;
import br.gov.mec.aghu.model.AelConfigMapaExames;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;

public class AelConfigMapaExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelConfigMapaExames> {

	private static final long serialVersionUID = -2339973217430727467L;

	public List<AelConfigMapaExames> pesquisarAelConfigMapaExamesPorAelConfigMapa(final AelConfigMapa configMapa, final String sigla, final String exame, final String material, final DominioSituacao situacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		final DetachedCriteria criteria = obterCriteria(configMapa, sigla, exame, material, situacao);
		criteria.addOrder(Order.asc(AelConfigMapaExames.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc); 
	}
	
	public List<AelConfigMapaExames> pesquisarAelConfigMapaExamesPorAelConfigMapa(final AelConfigMapa configMapa){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelConfigMapaExames.class,"confMapaExam");
		
		if(configMapa != null){
			criteria.add(Restrictions.eq("confMapaExam."+AelConfigMapaExames.Fields.CGM_SEQ.toString(), configMapa.getSeq()));
		}
		
		return executeCriteria(criteria); 
	}
	
	public Long pesquisarAelConfigMapaExamesPorAelConfigMapaCount(final AelConfigMapa configMapa, final String sigla, final String exame, final String material, final DominioSituacao situacao){
		return executeCriteriaCount(obterCriteria(configMapa, sigla, exame, material, situacao));
	}

	private DetachedCriteria obterCriteria(final AelConfigMapa configMapa, final String sigla, final String exame, final String material, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelConfigMapaExames.class,"confMapaExam");
		
		criteria.createAlias("confMapaExam."+AelConfigMapaExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "unidExec");
		criteria.createAlias("unidExec."+AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString(), "unf");
		
		criteria.createAlias("unidExec."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "exameMat");
		criteria.createAlias("exameMat."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "exame");
		criteria.createAlias("exameMat."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "material");

		if(configMapa != null){
			criteria.add(Restrictions.eq("confMapaExam."+AelConfigMapaExames.Fields.CGM_SEQ.toString(), configMapa.getSeq()));
		}
		
		if(StringUtils.isNotEmpty(sigla)){
			criteria.add(Restrictions.ilike("exame."+AelExames.Fields.SIGLA.toString(), sigla, MatchMode.ANYWHERE));
		}

		if(StringUtils.isNotEmpty(exame)){
			criteria.add(Restrictions.ilike("exame."+AelExames.Fields.DESCRICAO.toString(), exame, MatchMode.ANYWHERE));
		}

		if(StringUtils.isNotEmpty(material)){
			criteria.add(Restrictions.ilike("material."+AelMateriaisAnalises.Fields.DESCRICAO.toString(), material, MatchMode.ANYWHERE));
		}
		
		if(situacao != null){
			criteria.add(Restrictions.eq("confMapaExam."+AelConfigMapaExames.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		return criteria;
	}
}