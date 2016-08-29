package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExigenciaExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class AelExigenciaExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExigenciaExame> {
	
	private static final long serialVersionUID = -8053596950702754270L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelExigenciaExame.class);
    }
	
	private DetachedCriteria obterCriteriaPesquisa(AelUnfExecutaExames unfExecutaExames, AghUnidadesFuncionais unidadeFuncional, DominioSituacao situacao) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(AelExigenciaExame.Fields.UNF_EXECUTA_EXAMES.toString(), "UEX");
		criteria.createAlias("UEX."+AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA");
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "EXA");
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "EMA_MA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExigenciaExame.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		
		if(situacao != null) {
			criteria.add(Restrictions.eq(AelExigenciaExame.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(unfExecutaExames != null) {
			criteria.add(Restrictions.eq(AelExigenciaExame.Fields.UNF_EXECUTA_EXAMES.toString(), unfExecutaExames));
		}
		
		if(unidadeFuncional != null) {
			criteria.add(Restrictions.eq(AelExigenciaExame.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));
		}
		return criteria;
    }
	
	public List<AelExigenciaExame> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			AelUnfExecutaExames unfExecutaExames, AghUnidadesFuncionais unidadeFuncional, DominioSituacao situacao) {
		DetachedCriteria criteria = obterCriteriaPesquisa(unfExecutaExames, unidadeFuncional, situacao);


		criteria.createAlias("UEX."+AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL.toString(), "UEX_UNF");
		criteria.addOrder(Order.asc("EXA."+AelExames.Fields.DESCRICAO_USUAL.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarCount(AelUnfExecutaExames unfExecutaExames, AghUnidadesFuncionais unidadeFuncional, DominioSituacao situacao) {
		DetachedCriteria criteria = obterCriteriaPesquisa(unfExecutaExames, unidadeFuncional, situacao);
		return executeCriteriaCount(criteria);
	}
	
	public List<AelExigenciaExame> obterPorUnfExecutaExames(AelUnfExecutaExames unfExecutaExames) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelExigenciaExame.Fields.UNF_EXECUTA_EXAMES.toString(), unfExecutaExames));
		return executeCriteria(criteria);
	}
	
	public List<AelExigenciaExame> obterAtivasPorUnfExecutaExamesUnidadeFuncional(AelUnfExecutaExames unfExecutaExames, AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelExigenciaExame.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AelExigenciaExame.Fields.UNF_EXECUTA_EXAMES.toString(), unfExecutaExames));
		criteria.add(Restrictions.eq(AelExigenciaExame.Fields.UNIDADE_FUNCIONAL.toString(), unidadeFuncional));
		return executeCriteria(criteria);
	}
	
}
