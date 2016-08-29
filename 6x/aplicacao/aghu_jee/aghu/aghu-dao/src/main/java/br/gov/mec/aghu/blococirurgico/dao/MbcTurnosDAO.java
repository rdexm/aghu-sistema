package br.gov.mec.aghu.blococirurgico.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcTurnos;

public class MbcTurnosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcTurnos> {

	private static final long serialVersionUID = 6798003091525348958L;

	public List<MbcTurnos> pesquisarTiposTurnoPorFiltro(MbcTurnos turnoFiltro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTurnos.class);
		
		if(turnoFiltro.getDescricao() != null && !turnoFiltro.getDescricao().equals("")){
			criteria.add(Restrictions.ilike(MbcTurnos.Fields.DESCRICAO.toString(), turnoFiltro.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if(turnoFiltro.getOrdem() != null && !turnoFiltro.getOrdem().equals("")){
			criteria.add(Restrictions.eq(MbcTurnos.Fields.ORDEM.toString(), turnoFiltro.getOrdem()));
		}
		if(turnoFiltro.getTurno() != null && !turnoFiltro.getTurno().equals("")){
			criteria.add(Restrictions.eq(MbcTurnos.Fields.TURNO.toString(), turnoFiltro.getTurno()));
		}
		if(turnoFiltro.getSituacao() != null){
			criteria.add(Restrictions.eq(MbcTurnos.Fields.SITUACAO.toString(), turnoFiltro.getSituacao()));
		}
		criteria.addOrder(Order.asc(MbcTurnos.Fields.ORDEM.toString()));
		return executeCriteria(criteria);
	}

	public List<MbcTurnos> buscarPorDescricao(final Object objPesquisa) {
		final DetachedCriteria criteria = criarCriteriaBuscarPorDescricao(objPesquisa);
		criteria.addOrder(Order.asc(MbcTurnos.Fields.ORDEM.toString()));
		return executeCriteria(criteria);
	}

	public Long buscarPorDescricaoCount(final Object objPesquisa) {
		return executeCriteriaCount(criarCriteriaBuscarPorDescricao(objPesquisa));
	}

	private DetachedCriteria criarCriteriaBuscarPorDescricao(final Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcTurnos.class);
		criteria.add(Restrictions.ilike(MbcTurnos.Fields.DESCRICAO.toString(), objPesquisa == null ? "" : objPesquisa.toString(), MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq(MbcTurnos.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	public List<MbcTurnos> pesquisarTurnos(String objPesquisa) {
		DetachedCriteria criteria = criarCriteriapesquisarTurnos(objPesquisa);
		criteria.addOrder(Order.asc(MbcTurnos.Fields.ORDEM.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria criarCriteriapesquisarTurnos(String objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTurnos.class);
		String descricao = StringUtils.trimToNull(objPesquisa);		
		if (StringUtils.isNotBlank(descricao)) {			
			criteria.add(Restrictions.ilike(MbcTurnos.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}	
		criteria.add(Restrictions.eq(MbcTurnos.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	public Boolean verificarExistenciaTurnoAtivoComMesmaDescricao(String descricao, String id) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcTurnos.class);
		criteria.add(Restrictions.eq(MbcTurnos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.like(MbcTurnos.Fields.DESCRICAO.toString(), descricao));
		criteria.add(Restrictions.ne(MbcTurnos.Fields.TURNO.toString(), id));
		List<MbcTurnos> result = executeCriteria(criteria); 
		return result.size() > 0 ? true : false;
	}

	public Long pesquisarTurnosCount(String objPesquisa) {
		return executeCriteriaCount(criarCriteriapesquisarTurnos(objPesquisa));
	}

	public List<MbcTurnos> pesquisarTurnosPorUnidade(String param, Short unfSeq) { 
		List<MbcTurnos> retorno = new ArrayList<MbcTurnos>();
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTurnos.class);
		criteria.createAlias(MbcTurnos.Fields.HORARIO_TURNO_CIRG.toString(), "HTC");
		
		criteria.add(Restrictions.eq("HTC."+MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), unfSeq));
		if (param != null) {
			criteria.add(Restrictions.eq("HTC."+MbcHorarioTurnoCirg.Fields.TURNO_ID.toString(), param.toUpperCase()));
		}
		retorno =  executeCriteria(criteria);
		
		if (retorno == null || retorno.isEmpty()) {
			criteria = DetachedCriteria.forClass(MbcTurnos.class);	
			criteria.createAlias(MbcTurnos.Fields.HORARIO_TURNO_CIRG.toString(), "HTC");
			
			criteria.add(Restrictions.eq("HTC."+MbcHorarioTurnoCirg.Fields.UNF_SEQ.toString(), unfSeq));
			if (param != null) {
				criteria.add(Restrictions.ilike(MbcTurnos.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE));
			}
			retorno =  executeCriteria(criteria);
		}
		return retorno;
	}
	
	public Long pesquisarTurnosPorUnidadeCount(String param, Short unfSeq) {
		List<MbcTurnos> retorno = pesquisarTurnosPorUnidade(param, unfSeq);
		Long count = Long.valueOf(0);
		if (retorno != null) {
			count = Long.valueOf(retorno.size());
		}
		return count;		
	}	
	
}
