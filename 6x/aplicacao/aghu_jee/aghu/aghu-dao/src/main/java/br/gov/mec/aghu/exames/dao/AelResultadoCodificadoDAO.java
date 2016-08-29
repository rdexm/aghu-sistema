package br.gov.mec.aghu.exames.dao;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.vo.ResultadoCodificadoExameVO;
import br.gov.mec.aghu.exames.vo.ResultadosCodificadosVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificadoId;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.MciMicroorgPatologiaExame;
import br.gov.mec.aghu.model.MciMicroorganismoPatologia;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelResultadoCodificadoDAO extends	br.gov.mec.aghu.core.persistence.dao.BaseDao<AelResultadoCodificado> {

	private static final long serialVersionUID = 2422711005722261708L;

	public List<AelResultadoCodificado> buscarAelResultadoCodificadoPorCampoLaudo(final String filtro, final Integer calSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class);

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelCampoLaudo.class);
		subCriteria.add(Restrictions.eq(AelCampoLaudo.Fields.SEQ.toString(), calSeq));
		subCriteria.setProjection(Projections.property(AelCampoLaudo.Fields.GTC_SEQ.toString()));

		criteria.add(Subqueries.propertyEq(AelResultadoCodificado.Fields.GTC_SEQ.toString(), subCriteria));
		criteria.add(Restrictions.ilike(AelResultadoCodificado.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria);
	}

	// #37923 -busca SB
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificadosPorDescricao(String descricao, Integer pAghuCcihRegistroBacteria) {
		final DetachedCriteria criteria = criarCriteriaResultadoCodificadoExamePorDescricao(descricao, pAghuCcihRegistroBacteria);
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long buscarResultadosCodificadosPorDescricaoCount(String descricao, Integer pAghuCcihRegistroBacteria) {
		final DetachedCriteria criteria = criarCriteriaResultadoCodificadoExamePorDescricao(descricao, pAghuCcihRegistroBacteria);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria criarCriteriaResultadoCodificadoExamePorDescricao(String descricao , Integer pAghuCcihRegistroBacteria) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class, "RCD");
		criteria.createAlias("RCD." + AelResultadoCodificado.Fields.GRUPO_RESULTADO_CODIFICADO , "GTC");

		criteria.add(Restrictions.eq("RCD." + AelResultadoCodificado.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("RCD." + AelResultadoCodificado.Fields.BACTERIA_VIRUS_FUNGO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("GTC." + AelGrupoResultadoCodificado.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		//P_AGHU_CCIH_ Registro_Bacteria
		criteria.add(Restrictions.eq("RCD." + AelResultadoCodificado.Fields.GTC_SEQ.toString(), pAghuCcihRegistroBacteria));

		if(StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike("RCD." + AelResultadoCodificado.Fields.DESCRICAO.toString(), descricao, MatchMode.EXACT));
		}

		ProjectionList projection =	Projections.projectionList()
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.SEQ.toString()), "rcdSeq")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.DESCRICAO.toString()), "rcdDescricao")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.SITUACAO.toString()), "rcdSituacao")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.BACTERIA_VIRUS_FUNGO.toString()), "rcdVirusFungo")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.POSITIVO_CCI.toString()), "rcdPositivoCci")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.SEQ.toString()), "gtcSeq")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.DESCRICAO.toString()), "gtcDescricao")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.SITUACAO.toString()), "gtcSituacao");
		
		
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(ResultadoCodificadoExameVO.class));
		return criteria;
	}

	public Long buscarResultadosCodificadosBacteriaMultirCount(String param, Integer pAghuCcihRegistroBacteria) {
		final DetachedCriteria criteria = criarCriteriaResultadoCodificadoExameBacteriaMultir(param, pAghuCcihRegistroBacteria);
		return executeCriteriaCount(criteria);
	}
	
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificadosBacteriaMultir(String param, Integer pAghuCcihRegistroBacteria) {
		final DetachedCriteria criteria = criarCriteriaResultadoCodificadoExameBacteriaMultir(param, pAghuCcihRegistroBacteria);
		criteria.addOrder(Order.asc(AelResultadoCodificado.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}

	private DetachedCriteria criarCriteriaResultadoCodificadoExameBacteriaMultir(String param, Integer pAghuCcihRegistroBacteria) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class, "RCD");
		criteria.createAlias("RCD." + AelResultadoCodificado.Fields.GRUPO_RESULTADO_CODIFICADO , "GTC");

		criteria.add(Restrictions.eq("RCD." + AelResultadoCodificado.Fields.SITUACAO.toString(), DominioSituacao.A));
		//criteria.add(Restrictions.eq("RCD." + AelResultadoCodificado.Fields.BACTERIA_VIRUS_FUNGO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("GTC." + AelGrupoResultadoCodificado.Fields.SITUACAO.toString(), DominioSituacao.A));

		if(StringUtils.isNotBlank(param) && CoreUtil.isNumeroInteger(param)) {
			Integer seq = Integer.valueOf(param);
			Criterion c1 =  Restrictions.eq("RCD." + AelResultadoCodificado.Fields.SEQ.toString(), seq);
			Criterion c2 =  Restrictions.eq("GTC." + AelGrupoResultadoCodificado.Fields.SEQ.toString(), seq);
			criteria.add(Restrictions.or(c1, c2));
		} else  if(StringUtils.isNotBlank(param)) {
			Criterion c1 =  Restrictions.ilike("RCD." + AelResultadoCodificado.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE);
			Criterion c2 =  Restrictions.ilike("GTC." + AelGrupoResultadoCodificado.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(c1, c2));
		}
		
		//P_AGHU_CCIH_ Registro_Bacteria
		criteria.add(Restrictions.eq("RCD." + AelResultadoCodificado.Fields.GTC_SEQ.toString(), pAghuCcihRegistroBacteria));

		ProjectionList projection =	Projections.projectionList()
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.SEQ.toString()), "rcdSeq")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.DESCRICAO.toString()), "rcdDescricao")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.SITUACAO.toString()), "rcdSituacao")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.BACTERIA_VIRUS_FUNGO.toString()), "rcdVirusFungo")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.POSITIVO_CCI.toString()), "rcdPositivoCci")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.SEQ.toString()), "gtcSeq")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.DESCRICAO.toString()), "gtcDescricao")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.SITUACAO.toString()), "gtcSituacao");
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(ResultadoCodificadoExameVO.class));
		return criteria;
	}	

	// #1326 e #36555 -busca SB
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificados(String param) {
		final DetachedCriteria criteria = criarCriteriaResultadoCodificadoExame(param);
		return executeCriteria(criteria, 0, 100, null, false);
	}

	private DetachedCriteria criarCriteriaResultadoCodificadoExame(String param) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class, "RCD");
		criteria.createAlias("RCD." + AelResultadoCodificado.Fields.GRUPO_RESULTADO_CODIFICADO , "GTC");

		criteria.add(Restrictions.eq("RCD." + AelResultadoCodificado.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("RCD." + AelResultadoCodificado.Fields.BACTERIA_VIRUS_FUNGO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("GTC." + AelGrupoResultadoCodificado.Fields.SITUACAO.toString(), DominioSituacao.A));

		if(StringUtils.isNotBlank(param) && CoreUtil.isNumeroInteger(param)) {
			Integer seq = Integer.getInteger(param);
			Criterion c1 =  Restrictions.eq("RCD." + AelResultadoCodificado.Fields.SEQ.toString(), seq);
			Criterion c2 =  Restrictions.eq("GTC." + AelGrupoResultadoCodificado.Fields.SEQ.toString(), seq);
			criteria.add(Restrictions.or(c1, c2));
		} else  if(StringUtils.isNotBlank(param)) {
			Criterion c1 =  Restrictions.ilike("RCD." + AelResultadoCodificado.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE);
			Criterion c2 =  Restrictions.ilike("GTC." + AelGrupoResultadoCodificado.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(c1, c2));
		}

		ProjectionList projection =	Projections.projectionList()
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.SEQ.toString()), "rcdSeq")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.DESCRICAO.toString()), "rcdDescricao")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.SITUACAO.toString()), "rcdSituacao")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.BACTERIA_VIRUS_FUNGO.toString()), "rcdVirusFungo")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.POSITIVO_CCI.toString()), "rcdPositivoCci")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.SEQ.toString()), "gtcSeq")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.DESCRICAO.toString()), "gtcDescricao")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.SITUACAO.toString()), "gtcSituacao");
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(ResultadoCodificadoExameVO.class));
		return criteria;
	}

	// #1326 c2
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificadosPorMicroorgPatologia(MciMicroorganismoPatologia patologia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MciMicroorganismoPatologia.class, "MPT");
		criteria.createAlias("MPT." + MciMicroorganismoPatologia.Fields.MCI_MICROORG_PATOLOGIA_EXAMEES.toString(), "MPE");
		criteria.createAlias("MPE." + MciMicroorgPatologiaExame.Fields.AEL_RESULTADO_CODIFICADO.toString(), "RCD");
		criteria.createAlias("RCD." + AelResultadoCodificado.Fields.GRUPO_RESULTADO_CODIFICADO.toString() , "GTC");

		criteria.add(Restrictions.eq("MPT." + MciMicroorganismoPatologia.Fields.ID_PAI_SEQ.toString(), patologia.getId().getPaiSeq()));
		criteria.add(Restrictions.eq("MPT." + MciMicroorganismoPatologia.Fields.ID_SEQP.toString(), patologia.getId().getSeqp()));

		ProjectionList projection =	Projections.projectionList()
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.SEQ.toString()), "rcdSeq")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.DESCRICAO.toString()), "rcdDescricao")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.SITUACAO.toString()), "rcdSituacao")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.BACTERIA_VIRUS_FUNGO.toString()), "rcdVirusFungo")
				.add(Projections.property("RCD."+ AelResultadoCodificado.Fields.POSITIVO_CCI.toString()), "rcdPositivoCci")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.SEQ.toString()), "gtcSeq")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.DESCRICAO.toString()), "gtcDescricao")
				.add(Projections.property("GTC."+ AelGrupoResultadoCodificado.Fields.SITUACAO.toString()), "gtcSituacao")
				.add(Projections.property("MPE."+ MciMicroorgPatologiaExame.Fields.IND_SITUACAO.toString()), "situacaoExame")
				.add(Projections.property("MPT."+ MciMicroorganismoPatologia.Fields.ID_PAI_SEQ.toString()), "seqPai")
				.add(Projections.property("MPT."+ MciMicroorganismoPatologia.Fields.ID_SEQP.toString()), "seqp")
				;

		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(ResultadoCodificadoExameVO.class));
		return executeCriteria(criteria);
	}

	/**
	 * Retorna uma lista de resultado<br>
	 * codificado pelo parametro de pesquisa<br>
	 * da suggestion.
	 * 
	 * @param param
	 * @param calSeq
	 * @return
	 */
	public List<AelResultadoCodificado> listarResultadoCodificado(String param, Integer calSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class);

		if(CoreUtil.isNumeroInteger(param)) {
			criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.GTC_SEQ.toString(),
					Integer.parseInt(param)));
		} else if(StringUtils.isNotBlank(param)) {
			criteria.add(Restrictions.ilike(AelResultadoCodificado.Fields.DESCRICAO.toString(),
					param, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.in(AelResultadoCodificado.Fields.GTC_SEQ.toString(),
				this.obterSubQueryListarResultadoCodificao(calSeq)));

		criteria.addOrder(Order.asc(AelResultadoCodificado.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Consulta C30 do Web Service #39353
	 * 
	 * @param gtcSeq
	 * @param seqp
	 * @return
	 */
	public String obterDescricao(final Integer gtcSeq, final Integer seqp) {
		AelResultadoCodificado result = super.obterPorChavePrimaria(new AelResultadoCodificadoId(gtcSeq, seqp));
		if (result != null) {
			return result.getDescricao();
		}
		return null;
	}

	/**
	 * Obtém o seqp máximo
	 * 
	 * @param exaSigla
	 * @param manSeq
	 * @return
	 */
	public Integer obterMaxSeqp(final Integer codigoGrupo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class);

		criteria.setProjection(Projections.max(AelVersaoLaudo.Fields.SEQP.toString()));

		criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.GTC_SEQ.toString(), codigoGrupo));

		return (Integer) executeCriteriaUniqueResult(criteria);

	}




	public boolean pesquisaDescricaoExistenteResultadoCodificadoPorGrupo(AelGrupoResultadoCodificado grupoResult, String descricaoResulCod) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class);
		criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.DESCRICAO.toString(), descricaoResulCod.trim()));
		criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.GTC_SEQ.toString(), grupoResult.getSeq()));

		return (executeCriteriaCount(criteria)>0);
	}


	public List<AelResultadoCodificado> pesquisaResultadosCodificadosPorParametros(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ResultadosCodificadosVO filtroResultado) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class, "rec");

		if(filtroResultado.getSeq() != null){
			criteria.add(Restrictions.eq("rec." + AelResultadoCodificado.Fields.SEQ.toString(), filtroResultado.getSeq()));
		}

		if(!StringUtils.isBlank(filtroResultado.getDescricao())){
			criteria.add(Restrictions.ilike(AelResultadoCodificado.Fields.DESCRICAO.toString(), filtroResultado.getDescricao(), MatchMode.ANYWHERE));
		}

		if(filtroResultado.getPositivoCci() != null){
			criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.POSITIVO_CCI.toString(), filtroResultado.getPositivoCci()));
		}

		if(filtroResultado.getBacteriaVirusFungo() != null){
			criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.BACTERIA_VIRUS_FUNGO.toString(), filtroResultado.getBacteriaVirusFungo()));
		}

		if(filtroResultado.getSituacao() != null){
			criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.SITUACAO.toString(), filtroResultado.getSituacao()));
		}

		if(filtroResultado.getGrupoResultadoCodificado() != null){
			criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.GRUPO_RESULTADO_CODIFICADO.toString(), filtroResultado.getGrupoResultadoCodificado()));
		}

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisaResultadosCodificadosPorParametrosCount(ResultadosCodificadosVO filtroResultado) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class, "rec");

		if(filtroResultado.getSeq() != null){
			criteria.add(Restrictions.eq("rec." + AelResultadoCodificado.Fields.SEQ.toString(), filtroResultado.getSeq()));
		}

		if(!StringUtils.isBlank(filtroResultado.getDescricao())){
			criteria.add(Restrictions.ilike(AelResultadoCodificado.Fields.DESCRICAO.toString(), filtroResultado.getDescricao(), MatchMode.ANYWHERE));
		}

		if(filtroResultado.getPositivoCci() != null){
			criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.POSITIVO_CCI.toString(), filtroResultado.getPositivoCci()));
		}

		if(filtroResultado.getBacteriaVirusFungo() != null){
			criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.BACTERIA_VIRUS_FUNGO.toString(), filtroResultado.getBacteriaVirusFungo()));
		}

		if(filtroResultado.getSituacao() != null){
			criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.SITUACAO.toString(), filtroResultado.getSituacao()));
		}

		if(filtroResultado.getGrupoResultadoCodificado() != null){
			criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.GRUPO_RESULTADO_CODIFICADO.toString(), filtroResultado.getGrupoResultadoCodificado()));
		}

		return executeCriteriaCount(criteria);
	}

	public List<AelResultadoCodificado> pesquisarResulCodificadosPorGtcSeqDescricao(String descricao, Integer gtcSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class);
		criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.GTC_SEQ.toString(), gtcSeq));
		if(descricao != null){
			criteria.add(Restrictions.ilike(AelResultadoCodificado.Fields.DESCRICAO.toString(),	descricao.trim()));
		} else{
			criteria.add(Restrictions.isNull(AelResultadoCodificado.Fields.DESCRICAO.toString()));
		}

		return executeCriteria(criteria);
	}


	/**
	 * Pesquisa Resultados Codificados por gtcSeq e seqp
	 * @param gtcSeq
	 * @param seqp
	 * @return
	 */
	public List<AelResultadoCodificado> pesquisarResultadoCodificadoPorGtcSeqSeqp(Integer gtcSeq, Integer seqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCodificado.class);

		criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.GTC_SEQ.toString(), gtcSeq));
		criteria.add(Restrictions.eq(AelResultadoCodificado.Fields.SEQ.toString(), seqp));

		return executeCriteria(criteria);
	}

	public List<AelResultadoCodificado> pesquisarResultadosCodificadosPorCampoLaudo(AelCampoLaudo campoLaudo) {

		Query consulta = this
				.createQuery(
						"from AelResultadoCodificado rc where :campoLaudo member of rc."
								+ AelResultadoCodificado.Fields.GRUPO_RESULTADO_CODIFICADO
								.toString()
								+ "."
								+ AelGrupoResultadoCodificado.Fields.CAMPOS_LAUDO
								.toString()
								+ " and rc."
								+ AelResultadoCodificado.Fields.SITUACAO
								.toString() + " = 'A' order by descricao ");


		consulta.setParameter("campoLaudo", campoLaudo);

		return consulta.getResultList();
	}




	/**
	 * Subquery chamada pela<br>
	 * consulta listarResultadoCodificado.
	 * @param seq
	 * @return
	 */
	private List<Integer> obterSubQueryListarResultadoCodificao(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoResultadoCodificado.class);

		criteria.createAlias(AelGrupoResultadoCodificado.Fields.CAMPOS_LAUDO.toString(), "CAL");

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property(AelGrupoResultadoCodificado.Fields.SEQ.toString()));
		criteria.setProjection(projection);

		criteria.add(Restrictions.eq("CAL." + AelCampoLaudo.Fields.SEQ.toString(), seq));

		List<Integer> resultado = executeCriteria(criteria);

		if(resultado.isEmpty()) {
			resultado = new LinkedList<Integer>();
			resultado.add(Integer.valueOf(0));
		}

		return resultado;
	}


	@Override
	protected void obterValorSequencialId(AelResultadoCodificado elemento) {
		if(elemento == null || elemento.getGrupoResulCodificado() == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório não informado!");
		}

		AelResultadoCodificadoId id = new AelResultadoCodificadoId();
		id.setGtcSeq(elemento.getGrupoResulCodificado().getSeq());

		Integer seqp = this.obterMaxSeqp(elemento.getGrupoResulCodificado().getSeq());
		if(seqp == null){
			seqp=0;
		}

		id.setSeqp(++seqp); // Incrementa seqp

		// Seta id no novo elemento
		elemento.setId(id);
	}



	public Long buscarResultadosCodificadosCount(String param) {
		final DetachedCriteria criteria = criarCriteriaResultadoCodificadoExame(param);
		return executeCriteriaCount(criteria);
	}

}
