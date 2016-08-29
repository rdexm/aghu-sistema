package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.faturamento.vo.VFatProfRespDcsVO;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @modulo registrocolaborador.cadastrosbasicos
 *
 */
public class RapTipoQualificacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapTipoQualificacao> {
	
	private static final long serialVersionUID = 8277377105096489326L;
	private static final Short NIVEL_CURSO_GRADUACAO_ID = 3;
	
	protected RapTipoQualificacaoDAO() {
	}
	
	
	public RapTipoQualificacao obterQualificacaoEConselhoPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(RapTipoQualificacao.class);
		criteria.createAlias(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CP", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(RapTipoQualificacao.Fields.CODIGO.toString(), codigo));
		return (RapTipoQualificacao) executeCriteriaUniqueResult(criteria);
	}
	
	public RapTipoQualificacao graduacaoPorDescricaoSituacaoTipoCount(String descricao,
			DominioSituacao situacao, DominioTipoQualificacao tipo,
			RapConselhosProfissionais conselho) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapTipoQualificacao.class);

		RapTipoQualificacao example = new RapTipoQualificacao();

		example.setIndSituacao(situacao);
		example.setDescricao(descricao);
		example.setTipoQualificacao(tipo);
		example.setConselhoProfissional(conselho);

		criteria.add(Example.create(example).enableLike(MatchMode.EXACT)
				.ignoreCase());

		return (RapTipoQualificacao) executeCriteriaUniqueResult(criteria);
	}

	public List<RapTipoQualificacao> pesquisarGraduacao(Integer codigo,
			String descricao, DominioSituacao situacao,
			DominioTipoQualificacao tipo, RapConselhosProfissionais conselho,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		DetachedCriteria criteriaFiltro = montarConsulta(codigo, descricao, situacao, tipo, conselho);
		criteriaFiltro.createAlias(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CP", JoinType.LEFT_OUTER_JOIN);
		
		criteriaFiltro.addOrder(Order.asc(RapTipoQualificacao.Fields.DESCRICAO
				.toString()));		
		
		criteriaFiltro.addOrder(Order.asc(RapTipoQualificacao.Fields.CODIGO
				.toString()));		
		
		return executeCriteria(criteriaFiltro, firstResult, maxResult,
				orderProperty, asc);
	}
	
	private DetachedCriteria montarConsulta(Integer codigo, String descricao,
			DominioSituacao situacao, DominioTipoQualificacao tipo,
			RapConselhosProfissionais conselho) {

		DetachedCriteria criteriaMontagem = DetachedCriteria
				.forClass(RapTipoQualificacao.class);

		RapTipoQualificacao example = new RapTipoQualificacao();
		
		example.setDescricao(StringUtils.trimToNull(descricao));				
		example.setIndSituacao(situacao);		
		example.setTipoQualificacao(tipo);

		criteriaMontagem.add(Example.create(example).enableLike(
				MatchMode.ANYWHERE).ignoreCase());
		
		criteriaMontagem.add(Restrictions.or(Restrictions.eq(
				RapTipoQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				DominioTipoQualificacao.CCC), Restrictions.eq(
				RapTipoQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				DominioTipoQualificacao.CSC)));

		criteriaMontagem.add(Restrictions.or(Restrictions.eq(
				RapTipoQualificacao.Fields.CCC_NIVEL_CURSO.toString(),
				NIVEL_CURSO_GRADUACAO_ID), Restrictions.eq(
				RapTipoQualificacao.Fields.CSC_NIVEL_CURSO.toString(),
				NIVEL_CURSO_GRADUACAO_ID)));		

		if (codigo != null) {
			criteriaMontagem.add(Restrictions.eq(
					RapTipoQualificacao.Fields.CODIGO.toString(), codigo));
		}

		if (conselho != null) {
			criteriaMontagem.add(Restrictions.eq(
					RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), conselho));
		}		
		
		return criteriaMontagem;
	}
	
	public Long pesquisarGraduacaoCount(Integer codigo, String descricao,
			DominioSituacao situacao, DominioTipoQualificacao tipo,
			RapConselhosProfissionais conselho) {

		DetachedCriteria criteria = montarConsulta(codigo, descricao, situacao,
				tipo, conselho);

		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna os conselhos encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	public List<RapConselhosProfissionais> pesquisarConselhosPorDescricao(String valor) {
		DetachedCriteria _criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class);

		
		Criterion sigla = Restrictions.ilike(RapConselhosProfissionais.Fields.SIGLA.toString(), valor, MatchMode.ANYWHERE);
		Criterion nome = Restrictions.ilike(RapConselhosProfissionais.Fields.NOME.toString(), valor, MatchMode.ANYWHERE);
		
		LogicalExpression le = Restrictions.or(sigla, nome);
		
		_criteria.add (le);
		
		List<RapConselhosProfissionais> list = executeCriteria(_criteria);

		return list;
	}
	
	/**
	 * Retorna os conselhos ativos encontrados com a string fornecida no atributo
	 * descrição.
	 * 
	 * @param valor
	 * @return
	 */
	public List<RapConselhosProfissionais> pesquisarConselhosAtivosPorDescricao(String valor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class);

		criteria = this.obterCriterioPesquisarConselhosAtivosPorDescricao(criteria, valor);
		
		criteria.addOrder(Order.asc(RapConselhosProfissionais.Fields.NOME.toString()));
		
		List<RapConselhosProfissionais> list = executeCriteria(criteria);

		return list;
	}
	
	
	public Long pesquisarConselhosAtivosPorDescricaoCount(String valor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapConselhosProfissionais.class);
		criteria = this.obterCriterioPesquisarConselhosAtivosPorDescricao(criteria, valor);
		
		return this.executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria obterCriterioPesquisarConselhosAtivosPorDescricao(DetachedCriteria criteria, String valor) {
		Criterion sigla = Restrictions.ilike(RapConselhosProfissionais.Fields.SIGLA.toString(), valor, MatchMode.ANYWHERE);
		Criterion nome = Restrictions.ilike(RapConselhosProfissionais.Fields.NOME.toString(), valor, MatchMode.ANYWHERE);
		LogicalExpression le = Restrictions.or(sigla, nome);
		criteria.add (le);
		criteria.add(Restrictions.eq(RapConselhosProfissionais.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}
	
	
	/**
	 * Retorna os cursos de graduação.
	 * 
	 * @param curso
	 * @return retorna apenas os 100 primeiros
	 */
	public List<RapTipoQualificacao> pesquisarCursoGraduacao(Object curso) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapTipoQualificacao.class);

		criteria.add(Restrictions.eq(RapTipoQualificacao.Fields.SITUACAO
				.toString(), DominioSituacao.A));

		String stParametro = (String) curso;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(stParametro)) {
			codigo = Integer.valueOf(stParametro);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(
					RapTipoQualificacao.Fields.CODIGO.toString(), codigo));
		} else {
			criteria.add(Restrictions.ilike(
					RapTipoQualificacao.Fields.DESCRICAO.toString(),
					stParametro, MatchMode.ANYWHERE));

		}

		// tipo in CCC,CSC
		DominioTipoQualificacao[] tipos = { DominioTipoQualificacao.CCC,
				DominioTipoQualificacao.CSC };
		criteria.add(Restrictions.in(
				RapTipoQualificacao.Fields.TIPO_QUALIFICACAO.toString(), tipos));

		// cccNivelCurso ou cscNivelCurso == 3
		Disjunction orNivelCurso = Restrictions.disjunction();
		orNivelCurso.add(Restrictions.eq(
				RapTipoQualificacao.Fields.CCC_NIVEL_CURSO.toString(),
				(short) 3));
		orNivelCurso.add(Restrictions.eq(
				RapTipoQualificacao.Fields.CSC_NIVEL_CURSO.toString()
						.toString(), (short) 3));
		criteria.add(orNivelCurso);

		// order by
		criteria.addOrder(Order.asc(RapTipoQualificacao.Fields.DESCRICAO
				.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}	
	
	/**
	 * Pesquisa de Profissionais
	 * 
	 * @param sigla
	 * @param filtros - dados a serem pesquisados (QUANDO LOAD, devera vir no formato [codigoVinculo,matricula]
	 * @param isLoad  - informa se é uma pesquisa de load de tela, neste caso retornara apenas um valor
	 * 
	 * ORADB: AGH.V_FAT_PROF_RESP_DCS
	 * @return
	 */
	public List<VFatProfRespDcsVO> pesquisarVFatProfRespDcsVO(final String []sigla, final Object filtros, final boolean isLoad){
		final List<VFatProfRespDcsVO> result = executeCriteria( getCriteriaVFatProfRespDcsVO(sigla, filtros, true, isLoad) );
		
		return result;
	}
	
	public Long pesquisarVFatProfRespDcsVOCount(final String []sigla, Object filtros){
		return executeCriteriaCount(getCriteriaVFatProfRespDcsVO(sigla, filtros, true, false));
	}

	private DetachedCriteria getCriteriaVFatProfRespDcsVO(final String[] sigla, final Object filtros, final boolean isCount, final boolean isLoad) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoasFisicas.class, "pes");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("pes."+RapPessoasFisicas.Fields.NOME.toString()), "nome")
				.add(Projections.property("ser."+RapServidores.Fields.CODIGO_VINCULO.toString()), "serVinCodigo")
				.add(Projections.property("ser."+RapServidores.Fields.MATRICULA.toString()), "serMatricula") );
		
		criteria.createAlias("pes."+RapPessoasFisicas.Fields.MATRICULAS.toString(), "ser")
				.createAlias("pes."+RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "qlf")
				.createAlias("qlf."+RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "tql")		 
				.createAlias("tql."+RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "cpr");

		criteria.add(Restrictions.or(
										Restrictions.isNull("ser."+RapServidores.Fields.DATA_FIM_VINCULO.toString()),
										Restrictions.gt("ser."+RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date())
									) 
					);
		
		criteria.add(Restrictions.isNotNull("qlf."+RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		criteria.add(Restrictions.in("cpr."+RapConselhosProfissionais.Fields.SIGLA.toString(), sigla));
		
		if(isLoad){
			final Object []vals = (Object[]) filtros;
			
			if(vals.length != 2){
				throw new IllegalArgumentException("Parâmetros incorretos, informe: [codigoVinculo,matricula]");
			}
			
			criteria.add(Restrictions.eq("ser."+RapServidores.Fields.CODIGO_VINCULO.toString(), Short.valueOf(vals[0].toString())));
			criteria.add(Restrictions.eq("ser."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(vals[1].toString())));
			
		} else if(filtros != null && !filtros.toString().trim().isEmpty()){
			if(CoreUtil.isNumeroLong(filtros)){
				if(CoreUtil.isNumeroShort(filtros)){
					criteria.add( 
							Restrictions.or(
									Restrictions.eq("ser."+RapServidores.Fields.CODIGO_VINCULO.toString(), Short.valueOf(filtros.toString())),
									Restrictions.eq("ser."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(filtros.toString()))
							)
					);
					
				} else {
					criteria.add(Restrictions.eq("ser."+RapServidores.Fields.MATRICULA.toString(), Integer.valueOf(filtros.toString())));
				}
				
			} else {
				criteria.add(Restrictions.ilike("pes."+RapPessoasFisicas.Fields.NOME.toString(), filtros.toString(), MatchMode.ANYWHERE));
			}
		}
		
		
		if(!isCount){
			criteria.addOrder(Order.asc("pes."+RapPessoasFisicas.Fields.NOME.toString()));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(VFatProfRespDcsVO.class));
		
		return criteria;
	}
}