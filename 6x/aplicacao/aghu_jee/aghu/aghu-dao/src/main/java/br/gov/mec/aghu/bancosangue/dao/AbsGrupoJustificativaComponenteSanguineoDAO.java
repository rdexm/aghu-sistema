package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AbsGrupoJustificativaComponenteSanguineoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsGrupoJustificativaComponenteSanguineo> {

	

	private static final long serialVersionUID = -1036380189519358195L;


	/**
	 * Lista os grupos de justificativas referentes ao componente ou procedimento selecionado. Caso os 2 parâmetros sejam passados nulo, retorna nulo. 
	 * Caso os 2 sejam informados, pesquisa somente pelo procedHemoterapicoCodigo. Pois teoricamente, somente um pode ser informado.  
	 * @param procedHemoterapicoCodigo
	 * @param componenteSanguineoCodigo
	 * @return lista de AbsGrupoJustificativaComponenteSanguineo
	 */
	@SuppressWarnings("unchecked")
	public List<AbsGrupoJustificativaComponenteSanguineo> listarGruposJustifcativasDoComponenteOuProcedimento(String procedHemoterapicoCodigo, String componenteSanguineoCodigo){
		List<AbsGrupoJustificativaComponenteSanguineo> lista = null;
		boolean executarCrit = false;
		Query query;
		StringBuilder hql = new StringBuilder(200).append(" select distinct ");
		hql.append(" GJCS ");	
		hql.append(" from  ");
		hql.append( AbsGrupoJustificativaComponenteSanguineo.class.getSimpleName() ).append( " GJCS ");
		
		hql.append(" join GJCS.");
		hql.append( AbsGrupoJustificativaComponenteSanguineo.Fields.JUSTIFICATIVAS_COMPONENTE_SANGUINEO.toString() ).append( " JCS ");
		
		if(StringUtils.isNotBlank(procedHemoterapicoCodigo)){
			hql.append( " left join JCS.");
			hql.append( AbsJustificativaComponenteSanguineo.Fields.PROCEDIMENTO_HEMOTERAPICO.toString() ).append( " PHE ");
			hql.append(" where ");
			hql.append(" lower(PHE.").append( AbsProcedHemoterapico.Fields.CODIGO.toString()).append( ") like lower(:procedHemoterapicoCodigo) " );
			
			executarCrit = true;
			
		}else if(StringUtils.isNotBlank(componenteSanguineoCodigo)){
			hql.append( " left join JCS.");
			hql.append( AbsJustificativaComponenteSanguineo.Fields.COMPONENTE_SANGUINEO.toString() ).append( " CSA ");
			hql.append(" where ");
			hql.append(" lower(CSA.").append( AbsComponenteSanguineo.Fields.CODIGO.toString()).append( ") like lower(:componenteSanguineoCodigo) " );

			executarCrit = true;
		}
		
		hql.append(" AND JCS.");
		hql.append( AbsJustificativaComponenteSanguineo.Fields.SITUACAO.toString() ).append( " = :situacao ");
		
		query = createHibernateQuery(hql.toString());
		
		if(StringUtils.isNotBlank(procedHemoterapicoCodigo)){
			query.setParameter("procedHemoterapicoCodigo", procedHemoterapicoCodigo);
		} else if(StringUtils.isNotBlank(componenteSanguineoCodigo)){
			query.setParameter("componenteSanguineoCodigo", componenteSanguineoCodigo);
		}
		query.setParameter("situacao", DominioSituacao.A);
		
		if(executarCrit){//Se os dois argumentos forem passados nulos, não executa a consulta.
			lista = query.list();
		}
		return lista;
	}

	public String buscarDescricaoJustificativaSolicitacao(
			String procedHemoterapicoCodigo, String componenteSanguineoCodigo) {
		String descricao = null;
		if (StringUtils.isNotBlank(procedHemoterapicoCodigo)) {
			AbsProcedHemoterapico procedimentoHemoterapico = buscaProcedimentoHemoterapicoPorId(procedHemoterapicoCodigo);
			if (procedimentoHemoterapico != null) {
				descricao = procedimentoHemoterapico.getDescricao();
			}
		} else if (StringUtils.isNotBlank(componenteSanguineoCodigo)) {
			AbsComponenteSanguineo componenteSanguineo = buscaComponenteSanguineoPorId(componenteSanguineoCodigo);
			if (componenteSanguineo != null) {
				descricao = componenteSanguineo.getDescricao();
			}
		}

		return descricao;
	}

	private AbsProcedHemoterapico buscaProcedimentoHemoterapicoPorId(
			String procedHemoterapicoCodigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsProcedHemoterapico.class);

		if (StringUtils.isNotBlank(procedHemoterapicoCodigo)) {
			criteria.add(Restrictions.ilike(AbsProcedHemoterapico.Fields.CODIGO
					.toString(), procedHemoterapicoCodigo, MatchMode.EXACT));
		}

		return (AbsProcedHemoterapico) executeCriteriaUniqueResult(criteria);
	}

	private AbsComponenteSanguineo buscaComponenteSanguineoPorId(
			String componenteSanguineoCodigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsComponenteSanguineo.class);

		if (StringUtils.isNotBlank(componenteSanguineoCodigo)) {
			criteria.add(Restrictions.ilike(
					AbsComponenteSanguineo.Fields.CODIGO.toString(),
					componenteSanguineoCodigo, MatchMode.EXACT));
		}

		return (AbsComponenteSanguineo) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AbsGrupoJustificativaComponenteSanguineo> pesquisarGrupoJustificativaComponenteSanquineo(
			int firstResult, int maxResults, String orderProperty, Boolean asc,
			Short seq, String descricao, DominioSituacao situacao, String titulo) {
		
		DetachedCriteria dc = criarCriteriaPesquisarGrupoJustificativaComponenteSanquineo(seq, descricao, situacao, titulo);
		
		dc.addOrder(Order.asc(AbsGrupoJustificativaComponenteSanguineo.Fields.SEQ.toString()));
		dc.addOrder(Order.asc(AbsGrupoJustificativaComponenteSanguineo.Fields.DESCRICAO.toString()));
		dc.addOrder(Order.asc(AbsGrupoJustificativaComponenteSanguineo.Fields.TITULO.toString()));
		
		return executeCriteria(dc, firstResult, maxResults, orderProperty, asc);
	}

	
	private DetachedCriteria criarCriteriaPesquisarGrupoJustificativaComponenteSanquineo(Short seq, String descricao, DominioSituacao situacao, String titulo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "GJC");
		
		if (seq != null) {
			dc.add(Restrictions.eq("GJC.".concat(AbsGrupoJustificativaComponenteSanguineo.Fields.SEQ.toString()), seq));
		}
		
		if (descricao != null && !descricao.isEmpty()) {
			dc.add(Restrictions.ilike("GJC.".concat(AbsGrupoJustificativaComponenteSanguineo.Fields.DESCRICAO.toString()), descricao, MatchMode.ANYWHERE));
		}
		
		if (situacao != null) {
			dc.add(Restrictions.eq("GJC.".concat(AbsGrupoJustificativaComponenteSanguineo.Fields.SITUACAO.toString()), situacao));
		}
		
		if (titulo != null && !titulo.isEmpty()) {
			dc.add(Restrictions.ilike("GJC.".concat(AbsGrupoJustificativaComponenteSanguineo.Fields.TITULO.toString()), titulo, MatchMode.ANYWHERE));
		}
		
		return dc;
	}

	public Long pesquisarGrupoJustificativaComponenteSanquineoCount(Short seq, String descricao, DominioSituacao situacao, String titulo) {
		DetachedCriteria dc = criarCriteriaPesquisarGrupoJustificativaComponenteSanquineo(seq, descricao, situacao, titulo);
		
		return executeCriteriaCount(dc);
	}
	
	
	/**
	 * Consulta utilizada na suggestion box
	 * 
	 * @param param
	 * @return
	 */
	private DetachedCriteria montarCriteriaSuggestionBox(String param) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsGrupoJustificativaComponenteSanguineo.class);
		
		
		if (CoreUtil.isNumeroInteger(param)) {
			criteria.add(Restrictions.eq(AbsGrupoJustificativaComponenteSanguineo.Fields.SEQ.toString(), Short.valueOf(param)));
		} else {
			if (StringUtils.isNotBlank(param)) {
				criteria.add(Restrictions.ilike(AbsGrupoJustificativaComponenteSanguineo.Fields.TITULO.toString(), param, MatchMode.ANYWHERE));
			}
			
		}
		// ativo
		criteria.add(Restrictions.eq(AbsGrupoJustificativaComponenteSanguineo.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AbsGrupoJustificativaComponenteSanguineo.Fields.TITULO.toString()));		
		return criteria;
	}
	

	/**
	 * Consulta utilizada na suggestion box
	 * @param param
	 * @return
	 */
	public List<AbsGrupoJustificativaComponenteSanguineo> pesquisarGrupoJustificativaComponenteSanguineo(String param) {
		return executeCriteria(montarCriteriaSuggestionBox(param));
	}	
	
	public AbsGrupoJustificativaComponenteSanguineo obterGrupoJustificativaPorId(Short seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsGrupoJustificativaComponenteSanguineo.class, "grp");
		criteria.createAlias("grp." + AbsGrupoJustificativaComponenteSanguineo.Fields.SERVIDOR.toString(), "ser", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ser."+ RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("grp." + AbsGrupoJustificativaComponenteSanguineo.Fields.SERVIDOR_ALTERADO.toString(), "alt", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("alt." + RapServidores.Fields.PESSOA_FISICA.toString(), "pesAlt", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("grp." + AbsGrupoJustificativaComponenteSanguineo.Fields.SEQ.toString(), seq));

		return (AbsGrupoJustificativaComponenteSanguineo) executeCriteriaUniqueResult(criteria);

	

	}


}
