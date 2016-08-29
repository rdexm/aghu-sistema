package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelGrupoExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoExames> {
	
	
	private static final long serialVersionUID = 577442372157152080L;

	public List<AelGrupoExames> pesquisarGrupoExamePorCodigoOuDescricaoEUnidade(String parametro, AghUnidadesFuncionais unidadeExecutora){
		DetachedCriteria criteria = montarCriteriaParaPesquisarPorSequencialOuDescricao(parametro);
		if(unidadeExecutora!=null){
			criteria.createAlias(AelGrupoExames.Fields.UNIDADE_FUNCIONAL.toString(), AelGrupoExames.Fields.UNIDADE_FUNCIONAL.toString());
			criteria.add(Restrictions.eq(AelGrupoExames.Fields.UNIDADE_FUNCIONAL.toString()+"."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeExecutora.getSeq()));
		} else {
			return null;
		}
		return executeCriteria(criteria);
	}
	
	public List<AelGrupoExames> pesquisarGrupoExamePorCodigoOuDescricaoEUnidadeAtivos(String parametro, AghUnidadesFuncionais unidadeExecutora){
		DetachedCriteria criteria = montarCriteriaParaPesquisarPorSequencialOuDescricao(parametro);
		if(unidadeExecutora!=null){
			criteria.createAlias(AelGrupoExames.Fields.UNIDADE_FUNCIONAL.toString(), AelGrupoExames.Fields.UNIDADE_FUNCIONAL.toString());
			criteria.add(Restrictions.eq(AelGrupoExames.Fields.UNIDADE_FUNCIONAL.toString()+"."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeExecutora.getSeq()));
			criteria.add(Restrictions.eq(AelGrupoExames.Fields.SITUACAO.toString(), DominioSituacao.A));
			criteria.addOrder(Order.asc(AelGrupoExames.Fields.SEQ.toString()));
		} else {
			return null;
		}
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarCriteriaParaPesquisarPorSequencialOuDescricao(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoExames.class);
		
		if (StringUtils.isNotBlank(parametro)) {
			Criterion criterionNumeroSeq = null;
			if (CoreUtil.isNumeroInteger(parametro)) {
				Integer seqp = Integer.parseInt(parametro);
				criterionNumeroSeq = Restrictions.eq(AelGrupoExames.Fields.SEQ.toString(), seqp);
			} else {
				criterionNumeroSeq = Restrictions.ilike(AelGrupoExames.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE);
			}		
			criteria.add(criterionNumeroSeq);
		}
		
		return criteria;
	}
	
	private DetachedCriteria montarPesquisaGrupoExame(Integer grupoExameSeq, String grupoExameDescricao, 
			DominioSituacao grupoExameSituacao, Boolean agendaExameMesmoHor, Boolean calculaTempo,
			AghUnidadesFuncionais unidadeExecutora) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoExames.class);
		
		if (grupoExameSeq != null) {
			criteria.add(Restrictions.eq(AelGrupoExames.Fields.SEQ.toString(), grupoExameSeq));
		}
		
		if (!StringUtils.isEmpty(grupoExameDescricao)) {
			criteria.add(Restrictions.ilike(AelGrupoExames.Fields.DESCRICAO.toString(), 
					grupoExameDescricao, MatchMode.ANYWHERE));
		}
		
		if (grupoExameSituacao != null) {
			criteria.add(Restrictions.eq(AelGrupoExames.Fields.SITUACAO.toString(), grupoExameSituacao));
		}
		
		if (agendaExameMesmoHor != null) {
			criteria.add(Restrictions.eq(AelGrupoExames.Fields.IND_AGENDA_EX_MESMO_HOR.toString(), agendaExameMesmoHor));
		}
		
		if (calculaTempo != null) {
			criteria.add(Restrictions.eq(AelGrupoExames.Fields.IND_CALCULA_TEMPO.toString(), calculaTempo));
		}
		
		if (unidadeExecutora != null) {
			criteria.add(Restrictions.eq(AelGrupoExames.Fields.UNIDADE_FUNCIONAL.toString()+"."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeExecutora.getSeq()));
		}
		
		return criteria;
	}
	
	public Long pesquisarGardeExameCount(AelGrupoExames grupoExame){
		
		DetachedCriteria criteria = montarPesquisaGrupoExame(grupoExame.getSeq(), grupoExame.getDescricao(), grupoExame.getSituacao(),
				grupoExame.getAgendaExMesmoHor(), grupoExame.getCalculaTempo(), grupoExame.getUnidadeFuncional());
		
		return executeCriteriaCount(criteria);
	}
	
	public List<AelGrupoExames> pesquisarGrupoExame(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, AelGrupoExames grupoExame) {

		DetachedCriteria criteria = montarPesquisaGrupoExame( grupoExame.getSeq(), grupoExame.getDescricao(), 
															  grupoExame.getSituacao(), grupoExame.getAgendaExMesmoHor(), 
															  grupoExame.getCalculaTempo(),
															  grupoExame.getUnidadeFuncional());
		
		criteria.createAlias(AelGrupoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
}
