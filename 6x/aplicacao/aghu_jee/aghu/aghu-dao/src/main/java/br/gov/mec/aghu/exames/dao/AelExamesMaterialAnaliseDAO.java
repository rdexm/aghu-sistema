package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelExamesMaterialAnaliseDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExamesMaterialAnalise> {

	private static final long serialVersionUID = -744217353637695813L;

	@Override
	protected void obterValorSequencialId(AelExamesMaterialAnalise elemento) {
		
		if (elemento.getAelMateriaisAnalises() == null || elemento.getAelExames() == null) {
			
			throw new IllegalArgumentException("ScoFornecedor nao esta associado corretamente.");
			
		}
		
		AelExamesMaterialAnaliseId id = new AelExamesMaterialAnaliseId();
		id.setExaSigla(elemento.getAelExames().getSigla());
		id.setManSeq(elemento.getAelMateriaisAnalises().getSeq());
		elemento.setId(id);
		
	}	
	
	
	public boolean existeAelExamesMaterialAnalise(String exaSigla, Integer manSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesMaterialAnalise.class);
		criteria.add(Restrictions.eq(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), manSeq));
		return executeCriteriaCount(criteria) != 0;
	}
	
	
	public AelExamesMaterialAnalise buscarAelExamesMaterialAnalisePorId(String exaSigla, Integer manSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesMaterialAnalise.class);
		criteria.createAlias(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "man", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExamesMaterialAnalise.Fields.MODELO_CARTA.toString(), "car", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExamesMaterialAnalise.Fields.EXAME.toString(), "exa", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias(AelExamesMaterialAnalise.Fields.SERVIDOR.toString(), "ser", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), manSeq));
		
		
		return (AelExamesMaterialAnalise)executeCriteriaUniqueResult(criteria);
	}

	public List<AelExamesMaterialAnalise> buscarAelExamesMaterialAnalisePorAelExames(AelExames aelExames) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesMaterialAnalise.class);
		criteria.createAlias(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "man", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), aelExames.getSigla()));
		return executeCriteria(criteria);
	}
	
	public List<AelExamesMaterialAnalise> buscarAelExamesMaterialAnaliseAtivoPorAelExames(AelExames aelExames) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesMaterialAnalise.class);
		criteria.add(Restrictions.eq(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), aelExames.getSigla()));
		criteria.add(Restrictions.eq(AelExamesMaterialAnalise.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public List<AelUnfExecutaExames> buscarAelExamesMaterialAelUnfExecutaExames(AelExamesMaterialAnalise material, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelUnfExecutaExames.class);
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), material.getId().getExaSigla()));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), material.getId().getManSeq()));
		if(situacao != null){
			criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.SITUACAO.toString(), situacao));
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * Verificar a existência de registros em outras entidades
	 * @param object
	 * @param class1
	 * @param field
	 * @return
	 */
	public boolean existeItem(AelExamesMaterialAnalise material, Class class1, Enum field, Enum seq) {
		if (material == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(class1);
		criteria.add(Restrictions.eq(field.toString(),material.getId().getExaSigla()));
		criteria.add(Restrictions.eq(seq.toString(),material.getId().getManSeq()));
		return (executeCriteriaCount(criteria) > 0);
	}
	
	  /**
	 * Metodo que monta uma criteria para pesquisar AelExamesMaterialAnalise filtrando
	 *  pela sigla ou seq do material de analise.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaExameMaterialAnalise(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesMaterialAnalise.class);
		criteria.createCriteria(AelExamesMaterialAnalise.Fields.EXAME.toString(), "exa", JoinType.INNER_JOIN);

		criteria.createAlias(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "man", JoinType.LEFT_OUTER_JOIN);
		
		String strPesquisa = (String) objPesquisa;

		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq(AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), Integer.valueOf(strPesquisa)));
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.or(Restrictions.eq("exa."+AelExames.Fields.SIGLA.toString(), strPesquisa.toUpperCase()), 
					Restrictions.ilike("exa."+AelExames.Fields.DESCRICAO.toString(), strPesquisa.toUpperCase(), MatchMode.ANYWHERE)));
			
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria;
	}
	
	private DetachedCriteria montarCriteriaExameMaterialAnaliseUnfExecExames(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesMaterialAnalise.class);
		criteria.createCriteria(AelExamesMaterialAnalise.Fields.EXAME.toString(), "exa", JoinType.INNER_JOIN);
		criteria.createCriteria(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "man", JoinType.INNER_JOIN);
		criteria.createCriteria(AelExamesMaterialAnalise.Fields.UNF_EXECUTA_EXAME.toString(), "ufe", JoinType.INNER_JOIN);
		criteria.createCriteria("ufe." + AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString(), "unf", JoinType.INNER_JOIN);
		
		String strPesquisa = (String) objPesquisa;

		if(CoreUtil.isNumeroInteger(strPesquisa)){
			criteria.add(Restrictions.eq("man."+AelMateriaisAnalises.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
		}else if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.or(Restrictions.eq("exa."+AelExames.Fields.SIGLA.toString(), strPesquisa.toUpperCase()), 
					Restrictions.ilike("exa."+AelExames.Fields.DESCRICAO.toString(), strPesquisa.toUpperCase(), MatchMode.ANYWHERE)));
			
		}

		return criteria;
	}

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por AelExamesMaterialAnalise,
	 * filtrando pela sigla ou seq do material de analise.
	 * @param objPesquisa
	 * @return List<AelExamesMaterialAnalise>
	 */
	public List<AelExamesMaterialAnalise> listarExamesMaterialAnalise(Object objPesquisa){
		List<AelExamesMaterialAnalise> lista = null;
		DetachedCriteria criteria = montarCriteriaExameMaterialAnalise(objPesquisa);
		
		criteria.addOrder(Order.asc(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString()));
		
		lista = executeCriteria(criteria, 0, 100, null, true);
		
		return lista;
	}


	public List<AelExamesMaterialAnalise> listarExamesMaterialAnaliseUnfExecExames(Object objPesquisa, Short unfSeq){
		List<AelExamesMaterialAnalise> lista = null;
		DetachedCriteria criteria = montarCriteriaExameMaterialAnaliseUnfExecExames(objPesquisa);
		criteria.add(Restrictions.eq("unf."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		criteria.add(Restrictions.eq("ufe."+AelUnfExecutaExames.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString()));
		
		lista = executeCriteria(criteria, 0, 100, null, true);
		
		return lista;
	}

	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por AelExamesMaterialAnalise,
	 * filtrando pela sigla ou seq do material de analise.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarExamesMaterialAnaliseCount(Object objPesquisa){
		DetachedCriteria criteria = montarCriteriaExameMaterialAnalise(objPesquisa);

		return executeCriteriaCount(criteria);
	}
	
	public Long obterExamesMaterialAnaliseModeloCartaCount(Short mrtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesMaterialAnalise.class, "ema");
		criteria.createAlias("ema." + AelExamesMaterialAnalise.Fields.MODELO_CARTA.toString(), "mod");
		criteria.add(Restrictions.eq("mod." + AelModeloCartas.Fields.SEQ.toString(), mrtSeq));		
		return executeCriteriaCount(criteria);
	}

	/**
	 * Criteria para buscar os dados de exames e material de analise ativos
	 * 
	 * Web Service #36154
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria montarCriteriaAtivos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesMaterialAnalise.class, "EMA");
		criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	/**
	 * Criteria para buscar os dados de exames e material de analise ativos por descrição
	 * 
	 * Web Service #36154
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria montarCriteriaAtivosPorDescricao(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaAtivos();

		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.EXAME.toString(), "EXA");
		if (StringUtils.isNotBlank(parametro)) {
			criteria.add(Restrictions.ilike("EXA." + AelExames.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	/**
	 * Criteria para buscar os dados de exames e material de analise ativos por sigla
	 * 
	 * Web Service #36154
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria montarCriteriaAtivosPorSigla(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaAtivos();

		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.EXAME.toString(), "EXA");
		if (StringUtils.isNotBlank(parametro)) {
			criteria.add(Restrictions.eq("EXA." + AelExames.Fields.SIGLA.toString(), parametro.toUpperCase()));
		}

		return criteria;
	}

	/**
	 * Buscar os dados de exames e material de analise ativos por descrição e código
	 * 
	 * Web Service #36154
	 * 
	 * @param parametro
	 * @param maxResults
	 * @return
	 */
	public List<AelExamesMaterialAnalise> pesquisarAtivosPorSiglaOuDescricao(String parametro, Integer maxResults) {
		List<AelExamesMaterialAnalise> result = null;

		DetachedCriteria criteria = this.montarCriteriaAtivosPorSigla(parametro);

		criteria.addOrder(Order.asc("EXA." + AelExames.Fields.DESCRICAO.toString()));
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "MAN", JoinType.LEFT_OUTER_JOIN);
		criteria.addOrder(Order.asc("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()));

		if (maxResults != null) {
			result = super.executeCriteria(criteria, 0, maxResults, null, true);
		}
		result = super.executeCriteria(criteria);

		if (result == null || result.isEmpty()) {
			criteria = this.montarCriteriaAtivosPorDescricao(parametro);

			criteria.addOrder(Order.asc("EXA." + AelExames.Fields.DESCRICAO.toString()));
			criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "MAN", JoinType.LEFT_OUTER_JOIN);
			criteria.addOrder(Order.asc("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()));

			if (maxResults != null) {
				result = super.executeCriteria(criteria, 0, maxResults, null, true);
			}
			result = super.executeCriteria(criteria);
		}

		return result;
	}

	/**
	 * Count dos dados de exames e material de analise ativos por descrição e código
	 * 
	 * Web Service #36154
	 * 
	 * @param parametro
	 * @return
	 */
	public Long pesquisarAtivosPorSiglaOuDescricaoCount(String parametro) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorSigla(parametro);
		Long result = super.executeCriteriaCount(criteria);
		if (result == null || result.longValue() == 0) {
			criteria = this.montarCriteriaAtivosPorDescricao(parametro);
			result = super.executeCriteriaCount(criteria);
		}
		return result;
	}

	/**
	 * Buscar os dados de exames e material de analise por sigla e sequencial de material de análise
	 * 
	 * Web Service #37700
	 * 
	 * @param sigla
	 * @param seqMatAnls
	 * @return
	 */
	public List<AelExamesMaterialAnalise> pesquisarExamesPorSiglaMaterialAnalise(String sigla, Integer seqMatAnls) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesMaterialAnalise.class, "EMA");
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.EXAME.toString(), "EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "MAN", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.ilike("EMA." + AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), sigla, MatchMode.EXACT));
		criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), seqMatAnls));
		return executeCriteria(criteria);
	}	
}
