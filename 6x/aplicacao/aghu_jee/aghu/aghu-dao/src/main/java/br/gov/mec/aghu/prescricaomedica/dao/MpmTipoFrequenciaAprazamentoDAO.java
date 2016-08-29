package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MpmTipoFrequenciaAprazamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTipoFrequenciaAprazamento> {

	private static final long serialVersionUID = 6414617983030613062L;

	public List<MpmTipoFrequenciaAprazamento> obterListaTipoFrequenciaAprazamento(String strPesquisa) {
		DetachedCriteria criteria = this.montaFiltroPesquisaTipoFrequenciaAprazamento(strPesquisa);
		criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.SINTAXE.toString()));
		criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()));
		return  executeCriteria(criteria);
	}

	private DetachedCriteria montaFiltroPesquisaTipoFrequenciaAprazamento(String strPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class);
		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString(), strPesquisa, MatchMode.EXACT));
			criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			List<MpmTipoFrequenciaAprazamento> lista = executeCriteria(criteria);
			if (lista != null && !lista.isEmpty()) {
				return criteria;
			} else {
				criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class);
				criteria.add(Restrictions.ilike(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
				criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
				criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.SINTAXE.toString()));
				criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()));
				return criteria;
			} 
		}	
		else {
			criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			return criteria;
		}
	}

	public Long obterListaTipoFrequenciaAprazamentoCount(String strPesquisa) {
		DetachedCriteria criteria = this.montaFiltroPesquisaTipoFrequenciaAprazamento(strPesquisa);
		return  executeCriteriaCount(criteria);
	}
	
	public List<MpmTipoFrequenciaAprazamento> obterListaTipoFrequenciaAprazamentoDigitaFrequencia(Boolean listarApenasAprazamentoSemFrequencia, Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class);
		criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (StringUtils.isNotEmpty((String) parametro)) {
			criteria.add(Restrictions.ilike(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString(), parametro.toString(), MatchMode.ANYWHERE));
		}				
		// Se deve listar apenas os aprazamentos em que não é necessário informar a frequencia
		if (listarApenasAprazamentoSemFrequencia) {
			criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString(), false));
		}
		criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.SINTAXE.toString()));
		criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}
	
	public Long obterListaTipoFrequenciaAprazamentoDigitaFrequenciaCount(Boolean listarApenasAprazamentoSemFrequencia, Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class);
		criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (StringUtils.isNotEmpty((String) parametro)) {
			criteria.add(Restrictions.ilike(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString(), parametro.toString(), MatchMode.ANYWHERE));
		}				
		// Se deve listar apenas os aprazamentos em que não é necessário informar a frequencia
		if (listarApenasAprazamentoSemFrequencia) {
			criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString(), false));
		}
		return executeCriteriaCount(criteria);
	}
	
	public List<MpmTipoFrequenciaAprazamento> obterListaTipoFrequenciaAprazamentoHemoterapico(String strPesquisa) {
		if (StringUtils.isNotBlank(strPesquisa)) {
			DetachedCriteria criteria = DetachedCriteria
					.forClass(MpmTipoFrequenciaAprazamento.class);

			criteria.add(Restrictions.ilike(
					MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString(),
					strPesquisa, MatchMode.EXACT));
			
			criteria.add(Restrictions
					.eq(MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO
							.toString(), DominioSituacao.A));
			
			criteria.add(Restrictions.eq(
					MpmTipoFrequenciaAprazamento.Fields.IND_USO_HEMOTERAPIA
							.toString(), Boolean.TRUE));
			
			List<MpmTipoFrequenciaAprazamento> lista = executeCriteria(criteria);

			if (lista != null && !lista.isEmpty()) {
				return lista;
			} else {
				criteria = DetachedCriteria
						.forClass(MpmTipoFrequenciaAprazamento.class);

				criteria.add(Restrictions.ilike(
						MpmTipoFrequenciaAprazamento.Fields.DESCRICAO
								.toString(), strPesquisa, MatchMode.ANYWHERE));

				criteria.add(Restrictions.eq(
						MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO
								.toString(), DominioSituacao.A));

				criteria.add(Restrictions.eq(
						MpmTipoFrequenciaAprazamento.Fields.IND_USO_HEMOTERAPIA
								.toString(), Boolean.TRUE));
				
				return executeCriteria(criteria);
			}
		} else {
			DetachedCriteria criteria = DetachedCriteria
					.forClass(MpmTipoFrequenciaAprazamento.class);
			
			criteria.add(Restrictions
					.eq(MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO
							.toString(), DominioSituacao.A));

			criteria.add(Restrictions.eq(
					MpmTipoFrequenciaAprazamento.Fields.IND_USO_HEMOTERAPIA
							.toString(), Boolean.TRUE));
			
			return executeCriteria(criteria);
		}
		
	}


	/**
	 * Busca tipo frequencia aprazamento.
	 * 
	 * @param {Integer} seq
	 * @return {MpmModeloBasicoPrescricao}
	 */
	public MpmTipoFrequenciaAprazamento obterTipoFrequenciaAprazamentoPeloId(
			Short seq){

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class, "TFA");
		criteria.createAlias(MpmTipoFrequenciaAprazamento.Fields.APRAZAMENTO_FREQUENCIAS.toString(), "MAF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MpmTipoFrequenciaAprazamento.Fields.APRAZAMENTO_FREQUENCIAS_RAP_SERVIDOR.toString(), "MAF_SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MpmTipoFrequenciaAprazamento.Fields.APRAZAMENTO_FREQUENCIAS_RAP_SERVIDOR_PESSOA_FISICA.toString(), "MAF_SER_PF", JoinType.LEFT_OUTER_JOIN);		
		criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.SEQ.toString(), seq));
		
		return (MpmTipoFrequenciaAprazamento) executeCriteriaUniqueResult(criteria);

	}
	
	
	public int getNumeroAleatorio()
	{
	    return 4; //escolhido aleatoriamente.
	              //garantia de ser aleatório.
	}
	
	
	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria montaFiltroPesquisa(MpmTipoFrequenciaAprazamento entity){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class);
		
		if (entity==null){
			return null;
		}
		if (entity.getSeq()!=null){
			criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.SEQ.toString(), entity.getSeq()));
		}	
		if (StringUtils.isNotBlank(entity.getSigla())) {
			criteria.add(Restrictions.like(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString(), entity.getSigla(),MatchMode.ANYWHERE).ignoreCase());
		}
		if (StringUtils.isNotBlank(entity.getDescricao())) {
			criteria.add(Restrictions.like(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString(), entity.getDescricao(),MatchMode.ANYWHERE).ignoreCase());
		}
		if (StringUtils.isNotBlank(entity.getSintaxe())) {
			criteria.add(Restrictions.like(MpmTipoFrequenciaAprazamento.Fields.SINTAXE.toString(), entity.getSintaxe(),MatchMode.ANYWHERE).ignoreCase());
		}
		if (entity.getIndFormaAprazamento()!=null) {
			criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_FORMA_APRAZAMENTO.toString(), entity.getIndFormaAprazamento()));
		}
		if (entity.getIndDigitaFrequencia()!=null) {
			criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString(), entity.getIndDigitaFrequencia()));
		}
		if (entity.getIndUsoQuimioterapia()!=null) {
			criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_USO_QUIMIOTERAPIA.toString(), entity.getIndUsoQuimioterapia()));
		}
		if (entity.getIndUsoHemoterapia()!=null) {
			criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_USO_HEMOTERAPIA.toString(), entity.getIndUsoHemoterapia()));
		}		
		if (entity.getIndSituacao()!=null) {
			criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO.toString(), entity.getIndSituacao()));
		}
	
		return criteria;
	}
	
	
	public List<MpmTipoFrequenciaAprazamento> listarAprazamentosFrequenciaPaginator(MpmTipoFrequenciaAprazamento entity, int firstResult, 
			int maxResults, String orderProperty, boolean asc){
		return executeCriteria(montaFiltroPesquisa(entity));
	}
	
	public Long countAprazamentosFrequenciaPaginator(MpmTipoFrequenciaAprazamento entity){
		DetachedCriteria criteria = montaFiltroPesquisa(entity);
		
		return executeCriteriaCount(criteria);
	}
	
	public boolean siglaJaExiste(MpmTipoFrequenciaAprazamento entity){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class);
		if (entity==null){
			return false;
		}
		if (entity.getSeq()!=null){
			criteria.add(Restrictions.ne(MpmTipoFrequenciaAprazamento.Fields.SEQ.toString(), entity.getSeq()));
		}	
		criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString(), entity.getSigla()).ignoreCase());
		
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * Pesquisa tipos de aprazamento ativos e de frequência por código ou descrição
	 * @param objPesquisa Códuigo/ Descrição
	 * @return Lista de tipos de aprazamento
	 */
	public List<MpmTipoFrequenciaAprazamento> pesquisarTipoAprazamentoAtivoFrequenciaPorCodigoDescricao(
			Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				MpmTipoFrequenciaAprazamento.class, "TFA");

		
		ProjectionList p = Projections.projectionList();

		p.add(Projections.property("TFA."+MpmTipoFrequenciaAprazamento.Fields.SEQ
				.toString()), MpmTipoFrequenciaAprazamento.Fields.SEQ
				.toString());
		
		p.add(Projections.property("TFA."+MpmTipoFrequenciaAprazamento.Fields.SIGLA
				.toString()), MpmTipoFrequenciaAprazamento.Fields.SIGLA
				.toString());
		
		p.add(Projections.property("TFA."+MpmTipoFrequenciaAprazamento.Fields.SINTAXE
				.toString()), MpmTipoFrequenciaAprazamento.Fields.SINTAXE
				.toString());
		
		p.add(Projections.property("TFA."+MpmTipoFrequenciaAprazamento.Fields.DESCRICAO
				.toString()), MpmTipoFrequenciaAprazamento.Fields.DESCRICAO
				.toString());
		
		p.add(Projections.property("TFA."+MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO
				.toString()), MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO
				.toString());
		
		p.add(Projections.property("TFA."+MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA
				.toString()), MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA
				.toString());
		
		criteria.setProjection(p);
		
		
		String strPesquisa = "";
		if (objPesquisa != null) {
			strPesquisa = (String) objPesquisa;
		}
		if (StringUtils.isNotBlank(strPesquisa)) {			
			if (CoreUtil.isNumeroShort(objPesquisa)) {
				criteria.add(Restrictions.eq("TFA."+MpmTipoFrequenciaAprazamento.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.or(Restrictions.ilike("TFA."+MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString(),
													strPesquisa, MatchMode.EXACT), 
											 Restrictions.ilike("TFA."+MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString(),
													strPesquisa, MatchMode.ANYWHERE)));
			}
		}
		
		criteria.add(Restrictions.eq("TFA."+MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("TFA."+MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString(), Boolean.TRUE));
		criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString()));
		criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers
				.aliasToBean(MpmTipoFrequenciaAprazamento.class));

		return this.executeCriteria(criteria);
	}

	public List<MpmTipoFrequenciaAprazamento> listarAprazamentos(String strPesquisa) {		 
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class, "TFA");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString()));
		p.add(Projections.property(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()));
		p.add(Projections.property(MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString()));
		
		if (StringUtils.isNotBlank(strPesquisa)) {			
			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq("TFA."+MpmTipoFrequenciaAprazamento.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.or(Restrictions.ilike("TFA."+MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString(), strPesquisa, MatchMode.EXACT), 
											 Restrictions.ilike("TFA."+MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE)));
			}
		}
		criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Number listarAprazamentosCount(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class, "TFA");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString()));
		p.add(Projections.property(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()));
		p.add(Projections.property(MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString()));
		
		if (StringUtils.isNotBlank(strPesquisa)) {			
			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq("TFA."+MpmTipoFrequenciaAprazamento.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.or(Restrictions.ilike("TFA."+MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString(), strPesquisa, MatchMode.EXACT), 
											 Restrictions.ilike("TFA."+MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE)));
			}
		}
		return executeCriteriaCount(criteria);
	}	

	/**
	 * @author marcelo.deus
	 * #44281 - C4
	 */
	public List<MpmTipoFrequenciaAprazamento> listarSuggestionTipoFrequenciaAprazamento(String param){
	
		DetachedCriteria criteria = montarCriteriaTipoFrequenciaAprazamento(param);
		criteria.addOrder(Order.desc(MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString()));
		criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString()));
		
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long listarSuggestionTipoFrequenciaAprazamentoCount(String param){
		
		DetachedCriteria criteria = montarCriteriaTipoFrequenciaAprazamento(param);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaTipoFrequenciaAprazamento(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class);
		String srtPesquisa = (String) param;
		
		criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(srtPesquisa != null){
			criteria.add(Restrictions.or(Restrictions.ilike(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString(), srtPesquisa, MatchMode.ANYWHERE), 
						Restrictions.ilike(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE)));
		}
		return criteria;
	}
	
	private DetachedCriteria pesquisarFrequenciaAprazamento(String objPesquisa){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoFrequenciaAprazamento.class);
				
		if (objPesquisa != null) {
			criteria.add(Restrictions.or(Restrictions.ilike(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString(), objPesquisa, MatchMode.ANYWHERE), 
					Restrictions.ilike(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString(), objPesquisa, MatchMode.ANYWHERE)));		
		}
	
		criteria.add(Restrictions.eq(MpmTipoFrequenciaAprazamento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
						
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property(MpmTipoFrequenciaAprazamento.Fields.SEQ.toString()).as(MpmTipoFrequenciaAprazamento.Fields.SEQ.toString()))
				.add(Projections.property(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString()).as(MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString()))
				.add(Projections.property(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()).as(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()))
				.add(Projections.property(MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString()).as(MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString()));

		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(MpmTipoFrequenciaAprazamento.class));
		
		return criteria;
	}
	
	public List<MpmTipoFrequenciaAprazamento> pesquisaFrequenciaAprazamento(String objPesquisa){
		DetachedCriteria criteria = pesquisarFrequenciaAprazamento(objPesquisa);
		criteria.addOrder(Order.asc(MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisaFrequenciaAprazamentoCount(String objPesquisa){
		DetachedCriteria criteria = pesquisarFrequenciaAprazamento(objPesquisa);
		return executeCriteriaCount(criteria);
	}
	
}