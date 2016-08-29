package br.gov.mec.aghu.farmacia.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AfaTipoVelocAdministracoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaTipoVelocAdministracoes>{

	private static final long serialVersionUID = -2340200919966956179L;

	/*
	 * Retorna lista de tipos de velocidade de administração de medicamentos.
	 * <br>
	 * Execução da criteria retorna lista de {@link AfaTipoVelocAdministracoes}.
	 * 
	 * @return
	 */
	public List<AfaTipoVelocAdministracoes> obterListaTiposVelocidadeAdministracao() {
		List<AfaTipoVelocAdministracoes> list;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoVelocAdministracoes.class);
		criteria.add(Restrictions.eq(AfaTipoVelocAdministracoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AfaTipoVelocAdministracoes.Fields.DESCRICAO.toString()));
		
		list = super.executeCriteria(criteria);
		
		return list;
	}

	public DominioSituacao obtemSituacaoTipoVelocidade(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoVelocAdministracoes.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AfaTipoVelocAdministracoes.Fields.IND_SITUACAO.toString())));
		criteria.add(Restrictions.eq(AfaTipoVelocAdministracoes.Fields.SEQ.toString(), seq));
		
		DominioSituacao situacao = (DominioSituacao)executeCriteriaUniqueResult(criteria);
		
		return situacao;
	}
	
	public Long pesquisaVelocidadesAdministracaoCount(Short filtroSeq,
			String filtroDescricao, BigDecimal filtroFatorConversaoMlH,
			Boolean filtroTipoUsualNpt, Boolean filtroTipoUsualSoroterapia,
			DominioSituacao filtroSituacao) {
		return executeCriteriaCount(createCriteriaPesquisaVelocidadesAdministracao(
				filtroSeq, filtroDescricao, filtroFatorConversaoMlH,
				filtroTipoUsualNpt, filtroTipoUsualSoroterapia, filtroSituacao));
	}

	public List<AfaTipoVelocAdministracoes> pesquisaVelocidadesAdministracao(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short filtroSeq, String filtroDescricao,
			BigDecimal filtroFatorConversaoMlH, Boolean filtroTipoUsualNpt,
			Boolean filtroTipoUsualSoroterapia, DominioSituacao filtroSituacao) {
		DetachedCriteria criteria = createCriteriaPesquisaVelocidadesAdministracao(
				filtroSeq, filtroDescricao, filtroFatorConversaoMlH,
				filtroTipoUsualNpt, filtroTipoUsualSoroterapia, filtroSituacao);
		return executeCriteria(criteria, firstResult, maxResults,
				AfaTipoVelocAdministracoes.Fields.DESCRICAO.toString(), true);
	}

	private DetachedCriteria createCriteriaPesquisaVelocidadesAdministracao(
			Short filtroSeq, String filtroDescricao,
			BigDecimal filtroFatorConversaoMlH, Boolean filtroTipoUsualNpt,
			Boolean filtroTipoUsualSoroterapia, DominioSituacao filtroSituacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaTipoVelocAdministracoes.class);

		if (filtroSeq != null) {
			criteria.add(Restrictions.eq(AfaTipoVelocAdministracoes.Fields.SEQ
					.toString(), filtroSeq));
		}

		if (StringUtils.isNotBlank(filtroDescricao)) {
			criteria.add(Restrictions.ilike(
					AfaTipoVelocAdministracoes.Fields.DESCRICAO.toString(),
					filtroDescricao, MatchMode.ANYWHERE));
		}

		if (filtroFatorConversaoMlH != null) {
			criteria.add(Restrictions.eq(
					AfaTipoVelocAdministracoes.Fields.FATOR_CONVERSAO_MLH
							.toString(), filtroFatorConversaoMlH));
		}

		if (filtroTipoUsualNpt != null) {
			criteria.add(Restrictions.eq(
					AfaTipoVelocAdministracoes.Fields.IND_TIPO_USUAL_NPT
							.toString(), filtroTipoUsualNpt));
		}

		if (filtroTipoUsualSoroterapia != null) {
			criteria
					.add(Restrictions
							.eq(
									AfaTipoVelocAdministracoes.Fields.IND_TIPO_USUAL_SOROTERAPIA
											.toString(),
									filtroTipoUsualSoroterapia));
		}

		if (filtroSituacao != null) {
			criteria.add(Restrictions.eq(
					AfaTipoVelocAdministracoes.Fields.IND_SITUACAO.toString(),
					filtroSituacao));
		}
		
		return criteria;
	}

	public List<AfaTipoVelocAdministracoes> cursorVerificaUsoSoroterapiaNutricaoParental(
			Short seqTipoVelocidadeAdmnistracao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaTipoVelocAdministracoes.class);
		
		if (seqTipoVelocidadeAdmnistracao != null) {
			criteria.add(Restrictions.ne(AfaTipoVelocAdministracoes.Fields.SEQ
					.toString(), seqTipoVelocidadeAdmnistracao));
		}
		
		criteria.add(Restrictions.eq(
				AfaTipoVelocAdministracoes.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		return executeCriteria(criteria);
	}

	/**
	 * C06 para suggestion box SB2 estoria #3538
	 * @return
	 */
	public List<AfaTipoVelocAdministracoes> pesquisaAfaTipoVelocAdministracoesAtivos(Object objeto) {
		DetachedCriteria criteria = criaPesquisaPorFiltros(objeto);			
		return executeCriteria(criteria, 0, 100, "seq", true);
	}
	
	public long pesquisaAfaTipoVelocAdministracoesAtivosCount(Object objeto) {
		DetachedCriteria criteria = criaPesquisaPorFiltros(objeto);
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria criaPesquisaPorFiltros(Object objPesquisa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoVelocAdministracoes.class);
		String strPesquisa = (String) objPesquisa;
		criteria.add(Restrictions.eq(AfaTipoVelocAdministracoes.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		if (CoreUtil.isNumeroShort(strPesquisa)) {
			criteria.add(Restrictions.eq(AfaTipoVelocAdministracoes.Fields.SEQ.toString(),Short.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AfaTipoVelocAdministracoes.Fields.DESCRICAO.toString(),strPesquisa, MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	public List<AfaTipoVelocAdministracoes> pesquisarTipoVelocAdministracoes(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoVelocAdministracoes.class);
			
		return executeCriteria(criteria);
	}

}
