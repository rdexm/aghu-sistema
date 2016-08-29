package br.gov.mec.aghu.administracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AghNotificacaoDestinos;
import br.gov.mec.aghu.model.AghNotificacoes;
import br.gov.mec.aghu.model.RapServidores;

public class AghNotificacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghNotificacoes> {
	
	private static final long serialVersionUID = 4258544652229072238L;

	public List<AghNotificacoes> pesquisarNotificacoes(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, String descricao, String nomeProcesso, RapServidores servidor, Long celular) {
		DetachedCriteria criteria = montarPesquisaNotificacoes(descricao, nomeProcesso, servidor, celular);
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarNotificacoesCount(String descricao, String nomeProcesso, RapServidores servidor, Long celular) {
		DetachedCriteria criteria = montarPesquisaNotificacoes(descricao, nomeProcesso, servidor, celular);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarPesquisaNotificacoes(String descricao,
			String nomeProcesso, RapServidores servidor, Long celular) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghNotificacoes.class, "NTS");
		criteria.createAlias("NTS."+AghNotificacoes.Fields.DESTINOS.toString(), "NTD", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.groupProperty("NTS." + AghNotificacoes.Fields.SEQ.toString()), AghNotificacoes.Fields.SEQ.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.CRIADO_EM.toString()), AghNotificacoes.Fields.CRIADO_EM.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.ALTERADO_EM.toString()), AghNotificacoes.Fields.ALTERADO_EM.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.DESCRICAO.toString()), AghNotificacoes.Fields.DESCRICAO.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.NOME_PROCESSO.toString()), AghNotificacoes.Fields.NOME_PROCESSO.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.INICIO_EM.toString()), AghNotificacoes.Fields.INICIO_EM.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.HORARIO_AGENDAMENTO.toString()), AghNotificacoes.Fields.HORARIO_AGENDAMENTO.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.IND_TERMINO_NOTIFICACOES.toString()), AghNotificacoes.Fields.IND_TERMINO_NOTIFICACOES.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.TERMINA_EM.toString()), AghNotificacoes.Fields.TERMINA_EM.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.TERMINA_APOS.toString()), AghNotificacoes.Fields.TERMINA_APOS.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.VERSION.toString()), AghNotificacoes.Fields.VERSION.toString());
		projList.add(Projections.property("NTS." + AghNotificacoes.Fields.SERVIDOR.toString()), AghNotificacoes.Fields.SERVIDOR.toString());
		criteria.setProjection(projList);
		
		if (descricao != null && !StringUtils.isEmpty(descricao)) {
			criteria.add(Restrictions.ilike("NTS." + AghNotificacoes.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (nomeProcesso != null && !StringUtils.isEmpty(nomeProcesso)) {
			criteria.add(Restrictions.like("NTS." + AghNotificacoes.Fields.NOME_PROCESSO.toString(), nomeProcesso));
		}
		if (servidor != null) {
			criteria.add(Restrictions.eq("NTD." + AghNotificacaoDestinos.Fields.SERVIDOR_CONTATO.toString(), servidor));
		}
		if (celular != null) {
			criteria.add(Restrictions.eq("NTD." + AghNotificacaoDestinos.Fields.CELULAR.toString(), celular));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(AghNotificacoes.class));
		return criteria;
	}
	
	public Boolean existeNotificacaoComDeterminadaDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghNotificacoes.class, "NTS");
		criteria.add(Restrictions.like("NTS." + AghNotificacoes.Fields.DESCRICAO.toString(), descricao));
		return executeCriteriaExists(criteria);
	}
	
	public AghNotificacoes pesquisarNotificacaoPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghNotificacoes.class, "NTS");
		criteria.add(Restrictions.ilike("NTS." + AghNotificacoes.Fields.DESCRICAO.toString(), descricao));
		return (AghNotificacoes) executeCriteriaUniqueResult(criteria);
	}
}
