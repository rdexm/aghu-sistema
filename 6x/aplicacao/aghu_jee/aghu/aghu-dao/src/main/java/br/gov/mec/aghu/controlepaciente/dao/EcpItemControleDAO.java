package br.gov.mec.aghu.controlepaciente.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoGrupoControle;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.core.commons.CoreUtil;
/**
 * 
 * @modulo controlepaciente.cadastrosbasicos
 *
 */
public class EcpItemControleDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EcpItemControle> {

	private static final long serialVersionUID = 7921826177478119499L;

	public List<EcpItemControle> listarAtivos(EcpGrupoControle grupo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(EcpItemControle.class);
		criteria.createAlias(EcpItemControle.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(EcpItemControle.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (grupo != null) {
			criteria.add(Restrictions.eq(EcpItemControle.Fields.GRUPO.toString(), grupo));
		}

		criteria.addOrder(Order.asc(EcpItemControle.Fields.ORDEM.toString()));

		return this.executeCriteria(criteria);
	}

	public Long pesquisarItensCount(String sigla, String descricao,EcpGrupoControle grupo, DominioSituacao situacao) {
		DetachedCriteria criteria = montaCriteriaPesquisarItens(sigla,descricao, grupo, situacao);
		return this.executeCriteriaCount(criteria);
	}

	public List<EcpItemControle> pesquisarItens(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, String sigla,
			String descricao, EcpGrupoControle grupo, DominioSituacao situacao) {

		DetachedCriteria criteria = montaCriteriaPesquisarItens(sigla,descricao, grupo, situacao);

		criteria.createAlias(EcpItemControle.Fields.GRUPO.toString(), "grupo", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(EcpItemControle.Fields.UNIDADE_MEDIDA_MEDICA.toString(), "umm", JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.asc(EcpItemControle.Fields.GRUPO.toString()+ "." + EcpGrupoControle.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(EcpItemControle.Fields.ORDEM.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria montaCriteriaPesquisarItens(String sigla,String descricao, EcpGrupoControle grupo, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(EcpItemControle.class);

		if (StringUtils.isNotBlank(sigla)) {
			criteria.add(Restrictions.ilike(EcpItemControle.Fields.SIGLA.toString(), sigla,MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(EcpItemControle.Fields.DESCRICAO.toString(), descricao,MatchMode.ANYWHERE));
		}

		if (grupo != null && grupo.getSeq() != null) {
			criteria.add(Restrictions.eq(EcpItemControle.Fields.GRUPO.toString(), grupo));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(EcpItemControle.Fields.SITUACAO.toString(), situacao));
		}

		return criteria;
	}

	/**
	 * Método que retorna os itens de controle cadastrados para o paciente num
	 * determindado período
	 * 
	 * @param paciente
	 * @param dataHoraInicio
	 * @param dataHoraFim
	 * @return
	 */
	public List<EcpItemControle> pesquisarItemControlePorPacientePeriodo(
			AipPacientes paciente, Date dataHoraInicio, Date dataHoraFim, Long trgSeq,DominioTipoGrupoControle... tipoGrupo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpItemControle.class);

		criteria.createAlias(EcpItemControle.Fields.GRUPO.toString(), "GRUPO");
		criteria.createAlias(
				EcpItemControle.Fields.CONTROLE_PACIENTES.toString(),
				"CONTROLE_PACIENTE");
		criteria.createAlias(
				EcpItemControle.Fields.CONTROLE_PACIENTES.toString() + "."
						+ EcpControlePaciente.Fields.HORARIO.toString(),
				"HORARIO");
		
		if (tipoGrupo!=null){
			criteria.add(Restrictions.in(
				"GRUPO."+ EcpGrupoControle.Fields.TIPO.toString(), tipoGrupo));
		}
		
		DetachedCriteria subCriteria = DetachedCriteria
				.forClass(EcpHorarioControle.class);

		subCriteria.setProjection(Projections
				.property(EcpHorarioControle.Fields.SEQ.toString()));

		subCriteria.add(Restrictions.eq(
				EcpHorarioControle.Fields.PACIENTE.toString(), paciente));
		if(dataHoraInicio != null && dataHoraFim != null) {
			subCriteria.add(Restrictions.between(
					EcpHorarioControle.Fields.DATA_HORA.toString(), dataHoraInicio,
					dataHoraFim));
		}
		
		if(trgSeq != null) {
			subCriteria.add(Restrictions.eq(
					EcpHorarioControle.Fields.TRG_SEQ.toString(), trgSeq));
		}
		
		criteria.add(Subqueries.propertyIn("HORARIO."
				+ EcpHorarioControle.Fields.SEQ.toString(), subCriteria));

		criteria.addOrder(Order.asc("GRUPO."
				+ EcpGrupoControle.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(EcpItemControle.Fields.ORDEM.toString()));

		List<EcpItemControle> lista = executeCriteria(criteria);

		// controle para tirar os elementos duplicados
		// não foi possível utilizar o distinct na criteria devido a ordenação
		List<EcpItemControle> listaAux = new ArrayList<EcpItemControle>(lista);
		for (EcpItemControle el : listaAux) {
			if (lista.indexOf(el) != lista.lastIndexOf(el)) {
				lista.remove(el);
			}
		}

		return lista;
	}

	public List<EcpItemControle> pesquisarItensPorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpItemControle.class);
		criteria.add(Restrictions.ilike(EcpItemControle.Fields.DESCRICAO.toString(), descricao));
		return executeCriteria(criteria);
	}

	public List<EcpItemControle> pesquisarItensPorSigla(String sigla) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpItemControle.class);
		criteria.add(Restrictions.ilike(EcpItemControle.Fields.SIGLA.toString(), sigla));
		return executeCriteria(criteria);
	}
	
	/**
	 * Monta consulta que busca os sinais vitais por sigla, descricao, grupo e situacao
	 * 
	 * #35290
	 * 
	 * @param strPesquisa
	 * @param seqGrupo
	 * @return
	 */
	private DetachedCriteria montarPesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(String strPesquisa, Integer seqGrupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpItemControle.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq(EcpItemControle.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.or(Restrictions.ilike(EcpItemControle.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE),
						Restrictions.ilike(EcpItemControle.Fields.SIGLA.toString(), strPesquisa, MatchMode.ANYWHERE)));
			}
		}

		if (seqGrupo != null) {
			criteria.createAlias(EcpItemControle.Fields.GRUPO.toString(), "EcpItemControle");
			criteria.add(Restrictions.eq("EcpItemControle" + "." + EcpGrupoControle.Fields.SEQ.toString(), seqGrupo));
		}

		criteria.add(Restrictions.eq(EcpItemControle.Fields.SITUACAO.toString(), DominioSituacao.A));

		return criteria;
	}

	/**
	 * Busca os sinais vitais por sigla, descricao, grupo e situacao
	 * 
	 * #35290
	 * 
	 * @param strPesquisa
	 * @param seqGrupo
	 * @param maxResults
	 * @return
	 */
	public List<EcpItemControle> pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(String strPesquisa, Integer seqGrupo, Integer maxResults) {
		DetachedCriteria criteria = this.montarPesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(strPesquisa, seqGrupo);
		criteria.addOrder(Order.asc(EcpItemControle.Fields.DESCRICAO.toString()));
		if (maxResults != null) {
			return super.executeCriteria(criteria, 0, maxResults, null, true);
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * Busca número de sinais vitais por sigla, descricao, grupo e situacao
	 * 
	 * #35290
	 * 
	 * @param strPesquisa
	 * @param seqGrupo
	 * @return
	 */
	public Long pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupoCount(String strPesquisa, Integer seqGrupo) {
		DetachedCriteria criteria = this.montarPesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(strPesquisa, seqGrupo);
		return super.executeCriteriaCount(criteria);
	}

	/**
	 * Busca os dados dos respectivos sinais vitais
	 * 
	 * #35315
	 * 
	 * @param listSeqs
	 * @return
	 */
	public List<EcpItemControle> pesquisarSinaisVitaisAtivosPorDescricaoGrupo(List<Short> listSeqs) {
		if (listSeqs == null || listSeqs.isEmpty()) {
			return Collections.emptyList();
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpItemControle.class);
		criteria.add(Restrictions.in(EcpItemControle.Fields.SEQ.toString(), listSeqs));
		criteria.addOrder(Order.asc(EcpItemControle.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria);
	}
	
	public String obterDescricaoItemControle(Short iceSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpItemControle.class);
		criteria.setProjection(Projections.property(EcpItemControle.Fields.DESCRICAO.toString()));
		
		criteria.add(Restrictions.eq(EcpItemControle.Fields.SEQ.toString(), iceSeq));
		criteria.add(Restrictions.eq(EcpItemControle.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return (String) executeCriteriaUniqueResult(criteria);
	}
}
