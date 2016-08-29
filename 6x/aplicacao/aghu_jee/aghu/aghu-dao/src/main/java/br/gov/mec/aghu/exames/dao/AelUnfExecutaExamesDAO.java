package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoPopVersao;
import br.gov.mec.aghu.dominio.DominioTipoPesquisaExame;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameSuggestionVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelLoteExame;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpaPopAgendas;
import br.gov.mec.aghu.model.MpaPopExame;
import br.gov.mec.aghu.model.MpaPopVersoes;
import br.gov.mec.aghu.model.MpaPops;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapConselhosProfissionaisDAO;


public class AelUnfExecutaExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelUnfExecutaExames> {

	private static final long serialVersionUID = 3525830601873852994L;

	@Inject
	RapConselhosProfissionaisDAO rapConselhosProfissionaisDAO;
	
	private DetachedCriteria obterCriteria() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelUnfExecutaExames.class);
		return criteria;
    }
	

	public List<AelUnfExecutaExames> buscaListaAelUnfExecutaExames(final String sigla, final Integer manSeq, final Short unfSeq, final Boolean ativo) {
		final DetachedCriteria dc = obterCriteria();

		dc.createAlias(AelUnfExecutaExames.Fields.UNF_SEQ_COMPARECE.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias(AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNC", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias(AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias(AelUnfExecutaExames.Fields.MATERIAL_ANALISE.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("EMA.".concat(AelExamesMaterialAnalise.Fields.EXAME.toString()), "EXA", JoinType.LEFT_OUTER_JOIN);

		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), sigla));
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), manSeq));
		if(!ativo){
			dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.SITUACAO.toString(), DominioSituacao.A));
			dc.add(Restrictions.eq("EMA."+AelExamesMaterialAnalise.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			dc.add(Restrictions.eq("EXA."+AelExames.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			dc.add(Restrictions.eq("MAT."+AelMateriaisAnalises.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		}
		if (unfSeq != null) {
			dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		}
		return executeCriteria(dc);
	}

	public List<AelUnfExecutaExames> buscaListaAelUnfExecutaExamesAtivas(
			final String sigla, final Integer manSeq) {
		final DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), sigla));
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), manSeq));
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(dc);
	}

	
	public List<AelUnfExecutaExames> pesquisarAelUnfExecutaExamesExecutaPlantaoPorEmaExaSiglaEmaManSeq(
			final String emaExaSigla, final Integer emaManSeq) {
		final DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.IND_EXECUTA_EM_PLANTAO.toString(), Boolean.TRUE));
		return executeCriteria(dc);
	}


	public AelUnfExecutaExames buscaAelUnfExecutaExames(final String emaExaSigla,
			final Integer emaManSeq, final Short unfSeq) {
		
		final DetachedCriteria dc = obterDetachedCriteriabuscaAelUnfExecutaExames(emaExaSigla, emaManSeq, unfSeq);
		
		dc.createAlias(AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("EMA.".concat(AelExamesMaterialAnalise.Fields.EXAME.toString()), "EXA", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("EMA.".concat(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()), "EMA_MAT", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias(AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		
		return (AelUnfExecutaExames)executeCriteriaUniqueResult(dc);
	}
	
	private 		DetachedCriteria obterDetachedCriteriabuscaAelUnfExecutaExames (final String emaExaSigla,
			final Integer emaManSeq, final Short unfSeq){
		final DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		return criteria;
	}
	
	/**
	 * Busca o objeto AelUnfExecutaExames sem estourar o erro, caso não retorne um UniqueResult
	 * @param emaExaSigla Sigla do exame
	 * @param emaManSeq Seq do material de aálise
	 * @param unfSeq Seq da unidade funcional
	 * @return AelUnfExecutaExames
	 */
	public AelUnfExecutaExames obterAelUnfExecutaExames(final String emaExaSigla, final Integer emaManSeq, final Short unfSeq) {
		final DetachedCriteria dc = obterCriteria();
		dc.createAlias(AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL.toString(), "uf");
		dc.createAlias(AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", JoinType.INNER_JOIN);
		dc.createAlias(AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString(), "UNF", JoinType.INNER_JOIN);
		dc.createAlias(AelUnfExecutaExames.Fields.MATERIAL_ANALISE.toString(), "MAN", JoinType.INNER_JOIN);
		dc.createAlias("EMA." + AelExamesMaterialAnalise.Fields.EXAME.toString(), "EXA", JoinType.INNER_JOIN);
		dc.createAlias("EXA." + AelExames.Fields.SINONIMO_EXAME.toString(), "SIE", JoinType.LEFT_OUTER_JOIN);
		
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		
		return (AelUnfExecutaExames)executeCriteriaUniqueResult(dc);		
		
//		final Long count = executeCriteriaCount(dc);
//		
//		if (count.intValue() > 0) {
//			return buscaAelUnfExecutaExames(emaExaSigla, emaManSeq, unfSeq);
//		} else {
//			return null;
//		}
		
	}
	
	/**
	 * Obtém o contador da Busca o objeto AelUnfExecutaExames sem estourar o erro, caso não retorne um UniqueResult
	 * @param emaExaSigla Sigla do exame
	 * @param emaManSeq Seq do material de aálise
	 * @param unfSeq Seq da unidade funcional
	 * @return Integer
	 */
	public Long obterAelUnfExecutaExamesCount(final String emaExaSigla, final Integer emaManSeq, final Short unfSeq) {
		
		final DetachedCriteria dc = obterCriteria();
		
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		dc.add(Restrictions.eq(AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		    
		return executeCriteriaCount(dc);
		
	}
	
	public AelUnfExecutaExames buscaAelUnfExecutaExames(final AelExames exame, final AelMateriaisAnalises materialAnalise, final AghUnidadesFuncionais unidadeFuncional) {
		if (exame == null || exame.getSigla() == null
				|| materialAnalise == null || materialAnalise.getSeq() == null
				|| unidadeFuncional == null || unidadeFuncional.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		return this.buscaAelUnfExecutaExames(exame.getSigla(), materialAnalise.getSeq(), unidadeFuncional.getSeq());
	}
	
	public boolean existeUnfExecutaExames(final AelUnfExecutaExames unfExecutaRemover, final Class<?> class1, final Enum<?> sigla, final Enum<?> manSeq, final Enum<?> unfSeq) {

		if (unfExecutaRemover == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		final DetachedCriteria criteria = DetachedCriteria.forClass(class1);
		criteria.add(Restrictions.eq(sigla.toString(),unfExecutaRemover.getId().getEmaExaSigla()));
		criteria.add(Restrictions.eq(manSeq.toString(),unfExecutaRemover.getId().getEmaManSeq()));
		criteria.add(Restrictions.eq(unfSeq.toString(),unfExecutaRemover.getId().getUnfSeq().getSeq()));
		
		return (executeCriteriaCount(criteria) > 0);
	}	
	

	public Long countUnfExecutaExameAtivaMaterialAnaliseColetavel(final String emaExaSigla, final Integer emaManSeq) {
		final DetachedCriteria criteria = obterCriteria();
		final String aliasMaterial = "MA";
		criteria.createAlias(AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(),AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString());
		criteria.createAlias(AelUnfExecutaExames.Fields.MATERIAL_ANALISE.toString(),aliasMaterial);

		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasMaterial+"."+AelMateriaisAnalises.Fields.IND_COLETAVEL, Boolean.TRUE));
		
		return executeCriteriaCount(criteria);
	}

	public AelUnfExecutaExames obterDataReativacaoUnfExecutaExameAtiva(final String emaExaSigla, final Integer emaManSeq, final Short unfSeq) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(),AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString());
		criteria.createAlias(AelUnfExecutaExames.Fields.MATERIAL_ANALISE.toString(),AelUnfExecutaExames.Fields.MATERIAL_ANALISE.toString());
		criteria.createAlias(AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString(),AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString());

		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.IND_DESATIVA_TEMP.toString(), true));
		
		return (AelUnfExecutaExames) executeCriteriaUniqueResult(criteria);
	}
	
	public List<ExameSuggestionVO> pesquisaUnidadeExecutaSinonimoExame(String nomeExame) {
		return pesquisaUnidadeExecutaSinonimoExame(nomeExame, null, false, null, null);
	}
	
	/**
	 * Pesquisa por AelUnfExecutaExames que tenha AelSinonimoExame.nome igual ao parametro nomeExame.<br>
	 * Atraves da associacao obrigatorio entre as entidades:<br>
	 * <p>
	 * AelExamesMaterialAnalise -> AelMateriaisAnalise
	 *                          -> AelExames -> AelSinonimoExame
	 *                          -> AelUnfExecutaExames -> AghUnidadesFuncionais
	 * </p>
	 * 
	 * @param nomeExame
	 * @return Lista de array de duas posicoes de Object. Posicoes: { AelUnfExecutaExames, AelSinonimoExame }.
	 */
	public List<Object[]> pesquisaUnidadeExecutaSinonimoExameAntigo(String nomeExame,Short seqUnidade, Boolean isSus, RapServidoresId idServidor, Integer seqAtendimento) {

		final Query query = this.createQuery(pesquisaUnidadeExecutaSinonimoExameHQL(nomeExame, false,seqUnidade,isSus, idServidor, seqAtendimento));
		query.setParameter("indSituacaoAtiva", DominioSituacao.A);
		query.setParameter("indSolSistemaNAO", Boolean.FALSE);
		query.setParameter("indDependenteEmaNAO", Boolean.FALSE);
		if (StringUtils.isNotBlank(nomeExame)) {
            query.setParameter("sigla", nomeExame.toUpperCase());
            query.setParameter("sinonimoNome", "%" + nomeExame + "%");
			query.setParameter("sinonimoNomeUpper", "%" + nomeExame.toUpperCase() + "%");
			query.setParameter("sinonimoNomeUpperSemAcento", "%" + StringUtil.removeCaracteresDiferentesAlfabetoEacentos(nomeExame.toUpperCase()) + "%");
		}			
		if (seqUnidade != null && isSus){
			query.setParameter("uniFuncional", seqUnidade);
		}
		if (idServidor != null && idServidor.getMatricula() != null && idServidor.getVinCodigo() != null && seqAtendimento != null){
			query.setParameter("seqAtendimento", seqAtendimento);
			query.setParameter("serMatricula", idServidor.getMatricula());
			query.setParameter("serVinculo", idServidor.getVinCodigo());	
		}
		
		query.setMaxResults(100);
		return  query.getResultList();
	}
	/**
	 * Pesquisa por AelUnfExecutaExames que tenha AelSinonimoExame.nome igual ao parametro nomeExame.<br>
	 * Atraves da associacao obrigatorio entre as entidades:<br>
	 * <p>
	 * AelExamesMaterialAnalise -> AelMateriaisAnalise
	 *                          -> AelExames -> AelSinonimoExame
	 *                          -> AelUnfExecutaExames -> AghUnidadesFuncionais
	 * </p>aelExamesMaterialAnalise
	 * 
	 * @param nomeExame
	 * @return Lista de array de duas posicoes de Object. Posicoes: { AelUnfExecutaExames, AelSinonimoExame }.
	 */
	public List<ExameSuggestionVO> pesquisaUnidadeExecutaSinonimoExame(String nomeExame,Short seqUnidade, Boolean isSus, RapServidoresId idServidor, Integer seqAtendimento) {

        	DetachedCriteria criteria = DetachedCriteria.forClass(AelUnfExecutaExames.class, "UFE");
        	criteria.createAlias(AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", JoinType.INNER_JOIN);
        	criteria.createAlias(AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString(), "UNF", JoinType.INNER_JOIN);
        	criteria.createAlias(AelUnfExecutaExames.Fields.MATERIAL_ANALISE.toString(), "MAN", JoinType.INNER_JOIN);
        	criteria.createAlias("EMA." + AelExamesMaterialAnalise.Fields.EXAME.toString(), "EXA", JoinType.INNER_JOIN);
        	criteria.createAlias("EXA." + AelExames.Fields.SINONIMO_EXAME.toString(), "SIE", JoinType.INNER_JOIN);
        
        	criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
        	criteria.add(Restrictions.or(Restrictions.isNull("EMA." + AelExamesMaterialAnalise.Fields.IND_SOL_SISTEMA.toString()),
        		Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.IND_SOL_SISTEMA.toString(), Boolean.FALSE)));
        	criteria.add(Restrictions.eq("EMA." + AelExamesMaterialAnalise.Fields.IND_DEPENDENTE.toString(), Boolean.FALSE));
        	criteria.add(Restrictions.eq("EXA." + AelExames.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
        	criteria.add(Restrictions.eq("UFE." + AelUnfExecutaExames.Fields.SITUACAO.toString(), DominioSituacao.A));
        	criteria.add(Restrictions.eq("SIE." + AelSinonimoExame.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
        	criteria.add(Restrictions.eq("MAN." + AelMateriaisAnalises.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
        	criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SITUACAO.toString(), DominioSituacao.A));
        
        	if (seqUnidade != null && isSus) {
        	    criteria.createAlias(AelUnfExecutaExames.Fields.PERMISSAOUNIDSOLICS.toString(), "UPS", JoinType.INNER_JOIN);
        	    criteria.add(Restrictions.eq("UPS." + AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString(), seqUnidade));
        	}
        
        	if (StringUtils.isNotBlank(nomeExame)) {
        	    criteria.add(Restrictions.or(Restrictions.like("EMA." + AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(),
        		    nomeExame.toUpperCase()),
        	    Restrictions.or(Restrictions.ilike("SIE."+AelSinonimoExame.Fields.NOME.toString(), nomeExame, MatchMode.ANYWHERE)
            		,Restrictions.ilike("MAN."+AelMateriaisAnalises.Fields.DESCRICAO.toString(), nomeExame, MatchMode.ANYWHERE)
            		,Restrictions.ilike("UNF."+AghUnidadesFuncionais.Fields.DESCRICAO.toString(), nomeExame, MatchMode.ANYWHERE)
            		),
//        	    Restrictions.or(Restrictions.ilike("SIE."+AelSinonimoExame.Fields.NOME.toString(), nomeExame.toUpperCase())
//            		,Restrictions.ilike("MAN."+AelMateriaisAnalises.Fields.DESCRICAO.toString(), nomeExame.toUpperCase())
//            		,Restrictions.ilike("UNF."+AghUnidadesFuncionais.Fields.DESCRICAO.toString(), nomeExame.toUpperCase())
//            		),
        	    Restrictions.or(Restrictions.ilike("SIE."+AelSinonimoExame.Fields.NOME.toString(), StringUtil.removeCaracteresDiferentesAlfabetoEacentos(nomeExame.toUpperCase()), MatchMode.ANYWHERE)
            		,Restrictions.ilike("MAN."+AelMateriaisAnalises.Fields.DESCRICAO.toString(), StringUtil.removeCaracteresDiferentesAlfabetoEacentos(nomeExame.toUpperCase()), MatchMode.ANYWHERE)
            		,Restrictions.ilike("UNF."+AghUnidadesFuncionais.Fields.DESCRICAO.toString(), StringUtil.removeCaracteresDiferentesAlfabetoEacentos(nomeExame.toUpperCase()), MatchMode.ANYWHERE)
            		)));
        	}
        	
        
        	filtroExameEspecialidadeAtendimento(criteria, idServidor, seqAtendimento);
        
        	criteria.addOrder(Order.asc("SIE." + AelSinonimoExame.Fields.NOME.toString()));
        	criteria.addOrder(Order.asc("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()));
        	criteria.addOrder(Order.asc("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()));
        	
        	criteria.setProjection(Projections.projectionList()
        		.add(Projections.property("EMA." + AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString()), "exameSigla")
        		.add(Projections.property("SIE."+AelSinonimoExame.Fields.NOME.toString()), "sinonimoExameNome")
        		.add(Projections.property("EXA."+AelExames.Fields.DESCRICAO_USUAL.toString()), "exaDescricao")
        		.add(Projections.property("MAN."+AelMateriaisAnalises.Fields.DESCRICAO.toString()), "manDescricao")
        		.add(Projections.property("MAN."+AelMateriaisAnalises.Fields.SEQ.toString()), "manSeq")
        		.add(Projections.property(AelUnfExecutaExames.Fields.UNF_SEQ.toString()), "ufeUnfSeq")
        		.add(Projections.property("UNF."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()), "ufeUnfDescricao")
        		);

        	criteria.setResultTransformer(Transformers.aliasToBean(ExameSuggestionVO.class));
        	
        	List<ExameSuggestionVO> result = executeCriteria(criteria, 0, 100, null, false);
		return result;
	}
	
	public Boolean buscaAelUnfExecutaExamesComPermissaoSus (Short seqUnidade){
	    DetachedCriteria criteria = DetachedCriteria.forClass(AelUnfExecutaExames.class, "UFE");
	    criteria.createAlias(AelUnfExecutaExames.Fields.PERMISSAOUNIDSOLICS.toString(), "UPS", JoinType.INNER_JOIN);
	    criteria.add(Restrictions.eq("UPS." + AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString(), seqUnidade));
	    return executeCriteriaExists(criteria);
	}
	
	public Long pesquisaExamesSuggestionCount(String exame, List<String> siglasFiltro, DominioTipoPesquisaExame tipoPesquisa){
	    SQLQuery query = obterQuerySqlSuggestion(exame, siglasFiltro, true, tipoPesquisa);
	    query.addScalar("count", LongType.INSTANCE);
	    return (Long) query.uniqueResult();
	}
	
	public List<ExameSuggestionVO> pesquisaExamesSuggestion (String exame, List<String> siglasFiltro, boolean buscaCompleta, DominioTipoPesquisaExame tipoPesquisa) {
	    SQLQuery query = obterQuerySqlSuggestion(exame, siglasFiltro,false, tipoPesquisa);
	     
		query.addScalar("exameSigla", StringType.INSTANCE);
		query.addScalar("sinonimoExameNome", StringType.INSTANCE);
		query.addScalar("exaDescricao", StringType.INSTANCE);
		query.addScalar("manDescricao", StringType.INSTANCE);
		query.addScalar("manSeq", IntegerType.INSTANCE);
		query.addScalar("ufeUnfSeq", ShortType.INSTANCE);
		query.addScalar("ufeUnfDescricao", StringType.INSTANCE);
		query.addScalar("sinonimos", StringType.INSTANCE);
		query.addScalar("sinonimosComAcento", StringType.INSTANCE);

		if(!buscaCompleta){
			query.setMaxResults(100);
		}
		query.setResultTransformer(Transformers.aliasToBean(ExameSuggestionVO.class));
		List<ExameSuggestionVO> result = query.list();
	    return result;
	}


	private SQLQuery obterQuerySqlSuggestion(String exame, List<String> siglasFiltro, boolean count, DominioTipoPesquisaExame tipoPesquisa) {
	    StringBuffer sql = new StringBuffer(5000);
	    sql.append(" SELECT EMA.EXA_SIGLA AS exameSigla, ")
            .append("   EMA.MAN_SEQ AS manSeq,                            ")
            .append("   UFE.UNF_SEQ AS ufeUnfSeq,                         ")
            .append("   EXA.DESCRICAO       AS exaDescricao,              ")
            .append("   EXA.DESCRICAO_USUAL AS sinonimoExameNome,         ")
            .append("   MAN.DESCRICAO       AS manDescricao,              ")
            .append("   UNF.DESCRICAO       AS ufeUnfDescricao,           ")
            
            .append("   upper(EMA.EXA_SIGLA                               ")
            .append("   || ' '                                            ")
            .append("   || EXA.DESCRICAO                                  ")
            .append("   || ' @@@ '                                        ")
            .append("   || listagg(sie.nome, ' *** ') within GROUP (      ")
            .append(" ORDER BY sie.nome ASC))                             ")
            .append(" AS sinonimosComAcento,                              ")

            .append("   TRANSLATE(upper(EMA.EXA_SIGLA                     ")
            .append("   || ' '                                            ")
            .append("   || EXA.DESCRICAO                                  ")
            .append("   || ' @@@ '                                        ")
            .append("   || listagg(sie.nome, ' *** ') within GROUP (      ")
            .append(" ORDER BY sie.nome ASC)),                            ")
            .append(" 'ãõáéíóúçäëïöüâêîôûàèìòùçÃÕÁÉÍÓÚÇÄËÏÖÜÂÊÎÔÛÀÈÌÒÙÇ', ")
            .append(" 'aoaeioucaeiouaeiouaeioucAOAEIOUCAEIOUAEIOUAEIOUC') ")
            .append(" AS sinonimos                                        ")
            .append(" FROM agh_unidades_funcionais unf,                   ")
            .append("   ael_materiais_analises man,                       ")
            .append("   ael_exames exa,                                   ")
            .append("   ael_sinonimos_exames sie,                         ")
            .append("   ael_exames_material_analise ema,                  ")
            .append("   ael_unf_executa_exames ufe                        ")
            .append(" WHERE ufe.ind_situacao    = 'A'                     ")
            .append(" AND ema.exa_sigla         = ufe.ema_exa_sigla       ")
            .append(" AND ema.man_seq           = ufe.ema_man_seq         ")
            .append(" AND ema.ind_situacao      = 'A'                     ")
            .append(" AND sie.exa_sigla         = ema.exa_sigla           ")
            .append(" AND sie.ind_situacao      = 'A'                     ")
            .append(" AND exa.sigla             = ema.exa_sigla           ")
            .append(" AND exa.ind_situacao      = 'A'                     ")
            .append(" AND man.seq               = ema.man_seq             ")
            .append(" AND man.ind_situacao      = 'A'                     ")
            .append(" AND unf.seq               = ufe.unf_seq             ")
            .append(" AND unf.ind_sit_unid_func = 'A'                     ")
            .append(" AND (ema.IND_SOL_SISTEMA IS NULL                    ")
            .append(" OR ema.IND_SOL_SISTEMA    = 'N')                    ")
            .append(" AND ema.IND_DEPENDENTE    ='N'                      ");
            if (siglasFiltro != null && !siglasFiltro.isEmpty()){
            	sql.append(" and ema.exa_sigla in (:siglasFiltro)             ");
            }
            sql.append(" GROUP BY EMA.EXA_SIGLA ,                         ")
            .append("   EMA.MAN_SEQ ,                                     ")
            .append("   UFE.UNF_SEQ ,                                     ")
            .append("   EXA.DESCRICAO_USUAL,                              ")
            .append("   EXA.DESCRICAO,                                    ")
            .append("   MAN.DESCRICAO ,                                   ")
            .append("   UNF.DESCRICAO                                     ")
            .append(" ORDER BY EXA.DESCRICAO_USUAL ASC,                   ")
            .append("   MAN.DESCRICAO ASC,                                ")
            .append("   unf.descricao ASC                                 ");
	     
	    if (StringUtils.isNotBlank(exame)){
		StringBuffer sqlExames = new StringBuffer(5000);
		if (count)  {
		    sqlExames.append(" Select count(*) as count from (");
		} else {
		    sqlExames.append(" Select * from (");
		}
		sqlExames.append(sql)
		.append(" ) where ")
		.append(" sinonimos like :exameUpperCase ") 
		.append("  or sinonimos like :exameSemAcento ");
		sql = sqlExames;
            } else {
        	    if (count){
        		StringBuffer sqlExames = new StringBuffer(5000);
        		sqlExames.append(" Select count(*) as count from (")
        		.append(sql)
        		.append(" )  ");
        		sql = sqlExames;
        	    }
            }
	    SQLQuery query = createSQLQuery(sql.toString());
	    if (StringUtils.isNotBlank(exame)){
	    	if (DominioTipoPesquisaExame.INICIO.equals(tipoPesquisa)) {
				query.setString("exameUpperCase", exame.toUpperCase() + "%");
				query.setString("exameSemAcento", removeCaracteresDiferentesAlfabetoEacentos(exame.toUpperCase()) + "%");
	    	}
	    	else {
				query.setString("exameUpperCase", "%" + exame.toUpperCase() + "%");
				query.setString("exameSemAcento", "%" + removeCaracteresDiferentesAlfabetoEacentos(exame.toUpperCase()) + "%");
	    	}
	    }
	     if (siglasFiltro != null && !siglasFiltro.isEmpty()){
	    	 query.setParameterList("siglasFiltro", siglasFiltro);
	     }
	    return query;
	}
	
	private String removeCaracteresDiferentesAlfabetoEacentos(String texto) {
		texto = StringUtil.normaliza(texto);
		final String alfabeto = "abcdefghijklmnopqrstuvxywz0123456789";
		StringBuffer novoTexto = new StringBuffer();
		for (int x = 0; x < texto.length(); x++) {
			if (texto.substring(x, x + 1).equals(" ") || alfabeto.contains(texto.substring(x, x + 1).toLowerCase())) {
				novoTexto.append(texto.substring(x, x + 1));
			}
			if (texto.substring(x, x + 1).equals("/") && (x + 1 < texto.length()) && !texto.substring(x + 1, x + 2).equals(" ")) {
				novoTexto.append(' ');
			}
		}

		return novoTexto.toString();
	}
	
	
	public void filtroExameEspecialidadeAtendimento(DetachedCriteria criteria, RapServidoresId idServidor, Integer seqAtendimento){
		if (idServidor != null && idServidor.getMatricula() != null && idServidor.getVinCodigo() != null && seqAtendimento != null){
		    final DetachedCriteria subCriteria = DetachedCriteria.forClass(AghEspecialidades.class, "ESP");
		    subCriteria.createAlias(AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "PRE", JoinType.INNER_JOIN);
		    
		    subCriteria.createAlias(AghEspecialidades.Fields.MPA_POP_AGENDAS.toString(), "POA", JoinType.INNER_JOIN);
		  
		    subCriteria.createAlias("POA."+MpaPopAgendas.Fields.MPA_POP_VERSOES.toString(), "POV", JoinType.INNER_JOIN);
		    
		    subCriteria.createAlias("POV."+MpaPopVersoes.Fields.MPA_POP.toString(), "POP", JoinType.INNER_JOIN);
		    
		    subCriteria.createAlias("POV."+MpaPopVersoes.Fields.MPA_POP_EXAME.toString(), "POE", JoinType.INNER_JOIN);
		    
		    subCriteria.createAlias(AghEspecialidades.Fields.ATENDIMENTOS.toString(), "ATD", JoinType.INNER_JOIN);
		    
		    subCriteria.setProjection(Projections.distinct(Projections.property("POE."+MpaPopExame.Fields.EXAME_SIGLA.toString())));

		    subCriteria.add(Restrictions.eq("POV."+MpaPopVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoPopVersao.L));
		    
		    subCriteria.add( Restrictions.or(
			    Restrictions.and(
			    Restrictions.eq("ATD."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.A),
			    Restrictions.eq("POP."+MpaPops.Fields.TIPO.toString(), DominioSituacao.A),
			    Restrictions.eq("PRE."+AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), DominioSimNao.S)
		    ),
		    	    Restrictions.and(
			    Restrictions.eq("ATD."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I),
			    Restrictions.eq("POP."+MpaPops.Fields.TIPO.toString(), DominioSituacao.I),
			    Restrictions.eq("PRE."+AghProfEspecialidades.Fields.IND_ATUA_INTERNACAO.toString(), DominioSimNao.S)
		    )));
		    subCriteria.add( Restrictions.eq("ATD."+ AghAtendimentos.Fields.SEQ.toString(), seqAtendimento));
		    subCriteria.add( Restrictions.eq("PRE."+ AghProfEspecialidades.Fields.SER_MATRICULA.toString(), idServidor.getMatricula()));
		    subCriteria.add( Restrictions.eq("PRE."+ AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), idServidor.getVinCodigo()));
		  criteria.add(Subqueries.propertyIn("EMA."+AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), subCriteria));
		}

	}
	
	public Long pesquisaUnidadeExecutaSinonimoExameCountAntigo(String nomeExame,Short seqUnidade, Boolean isSus, RapServidoresId idServidor, Integer seqAtendimento) {

		final Query query = this.createQuery(pesquisaUnidadeExecutaSinonimoExameHQL(nomeExame, true,seqUnidade, isSus, idServidor, seqAtendimento));
		query.setParameter("indSituacaoAtiva", DominioSituacao.A);
		query.setParameter("indSolSistemaNAO", Boolean.FALSE);
		query.setParameter("indDependenteEmaNAO", Boolean.FALSE);
		if (StringUtils.isNotBlank(nomeExame)) {
            query.setParameter("sigla", nomeExame.toUpperCase());
			query.setParameter("sinonimoNome", "%" + nomeExame + "%");
			query.setParameter("sinonimoNomeUpper", "%" + nomeExame.toUpperCase() + "%");
			query.setParameter("sinonimoNomeUpperSemAcento", "%" + StringUtil.removeCaracteresDiferentesAlfabetoEacentos(nomeExame.toUpperCase()) + "%");
		}			
		if (seqUnidade != null && isSus){
			query.setParameter("uniFuncional", seqUnidade);
		}
		if (idServidor != null && idServidor.getMatricula() != null && idServidor.getVinCodigo() != null && seqAtendimento != null){
			query.setParameter("seqAtendimento", seqAtendimento);
			query.setParameter("serMatricula", idServidor.getMatricula());
			query.setParameter("serVinculo", idServidor.getVinCodigo());	
		}
		return (Long) query.getSingleResult();
	}
	
	
	
	private String pesquisaUnidadeExecutaSinonimoExameHQL(String nomeExame, boolean count, Short seqUnidade, Boolean isSus, RapServidoresId idServidor, Integer seqAtendimento){
	
		/* Sql projetado para o Desenvolvimento da estoria.
		 * Caso seja informado Número do Atendimento e ID do Rapservidor serão listados apenas exames
		 * do protocolo de enfermagem disponíveis para a especialidade deste atendimento.
		select ema.exa_sigla ufe_ema_exa_sigla
		, ema.man_seq ufe_ema_man_seq
		, man.descricao dsp_descricao_material
		, sie.nome dsp_descricao_usual_exame
		, unf.descricao dsp_descricao_unidade
		, man.descricao || ' (' || unf.descricao || ')' dsp_material_unidade
		, ufe.unf_seq ufe_unf_seq
		from agh.ael_exames_material_analise ema 
		   inner join agh.ael_exames exa on exa.sigla = ema.exa_sigla
		      inner join agh.ael_sinonimos_exames sie on sie.exa_sigla = exa.sigla
		   inner join agh.ael_materiais_analises man on man.seq = ema.man_seq
		   inner join agh.ael_unf_executa_exames ufe on ufe.ema_exa_sigla = ema.exa_sigla and ufe.ema_man_seq = ema.man_seq
		      inner join agh.agh_unidades_funcionais unf on unf.seq = ufe.unf_seq
		where ema.ind_situacao = 'A'
		and (ema.ind_sol_sistema is null or ema.ind_sol_sistema = 'N')
		and ema.ind_dependente = 'N'
		and exa.ind_situacao = 'A'
		and ufe.ind_situacao = 'A'
		and sie.ind_situacao = 'A'
		and man.ind_situacao = 'A'
		and unf.ind_sit_unid_func = 'A'  */		

		nomeExame = StringUtils.trimToEmpty(nomeExame);
		
		final StringBuilder hql = new StringBuilder(500);
		
		if (count){
			hql.append("select count(*)");
		}else{
			hql.append("select ufe, sie");
		}		
		hql.append(" from ").append(AelExamesMaterialAnalise.class.getSimpleName()).append(" ema ");
		hql.append(" inner join ema.").append(AelExamesMaterialAnalise.Fields.EXAME.toString()).append(" exa ");
		hql.append(" inner join exa.").append(AelExames.Fields.SINONIMO_EXAME.toString()).append(" sie ");
		hql.append(" inner join ema.").append(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()).append(" man ");
		hql.append(" inner join ema.").append(AelExamesMaterialAnalise.Fields.UNF_EXECUTA_EXAME.toString()).append(" ufe ");
		hql.append(" inner join ufe.").append(AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString()).append(" unf ");
		
		// #31396 - Verifica permissões caso exista uma Unidade Funcional selecionada e convenio for igual a SUS
		if (seqUnidade != null && isSus){
			hql.append(" inner join ufe.").append(AelUnfExecutaExames.Fields.PERMISSAOUNIDSOLICS.toString()).append(" unfPermiss ");
		}
		
		hql.append(" where ema.").append(AelExamesMaterialAnalise.Fields.IND_SITUACAO.toString()).append(" =  :indSituacaoAtiva");
		hql.append(" and ( ema.").append(AelExamesMaterialAnalise.Fields.IND_SOL_SISTEMA.toString()).append(" is null ");
		hql.append(" or ema.").append(AelExamesMaterialAnalise.Fields.IND_SOL_SISTEMA.toString()).append(" = :indSolSistemaNAO )");
		hql.append(" and ema.").append(AelExamesMaterialAnalise.Fields.IND_DEPENDENTE.toString()).append(" =  :indDependenteEmaNAO ");
		hql.append(" and exa.").append(AelExames.Fields.IND_SITUACAO.toString()).append(" =  :indSituacaoAtiva ");
		hql.append(" and ufe.").append(AelUnfExecutaExames.Fields.SITUACAO.toString()).append(" =  :indSituacaoAtiva ");
		hql.append(" and sie.").append(AelSinonimoExame.Fields.IND_SITUACAO.toString()).append(" =  :indSituacaoAtiva ");
		hql.append(" and man.").append(AelMateriaisAnalises.Fields.IND_SITUACAO.toString()).append(" =  :indSituacaoAtiva ");
		hql.append(" and unf.").append(AghUnidadesFuncionais.Fields.SITUACAO.toString()).append(" =  :indSituacaoAtiva ");
		
		// #31396 - Verifica permissões caso exista uma Unidade Funcional selecionada e convenio for igual a SUS
		if (seqUnidade != null && isSus){
			hql.append(" and unfPermiss.").append(AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString()).append(" = :uniFuncional ");
		}
				
		if (StringUtils.isNotBlank(nomeExame)) {
			hql.append(" and (( ema.").append(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString()).append(" like upper(:sigla)) or (");
            hql.append(getClausulaDescricao()).append(" like :sinonimoNome ");
			hql.append(" or ").append(getClausulaDescricao()).append(" like :sinonimoNomeUpper  ");
			hql.append(" or ").append(getClausulaDescricao()).append(" like :sinonimoNomeUpperSemAcento )) ");
		}
		
		filtroExameEspecialidadeAtendimento(hql, idServidor, seqAtendimento);
		
		if (!count){
			hql.append( "order by ").
				append("sie.").append(AelSinonimoExame.Fields.NOME.toString()).
					append(", man.").append(AelMateriaisAnalises.Fields.DESCRICAO.toString()).					
					append(", unf.").append(AghUnidadesFuncionais.Fields.DESCRICAO.toString()).append(' ');
		}		
		return hql.toString();
		
	}
	
	// Caso o usuário possua um perfil com a permissão 'listarExamesProtocEnfermagem' só devem serem 
	//  listado exames relacionados ao protocolo de enfermagem
//	in (select distinct poe.exa_sigla
//			from agh_especialidades esp,
//			agh_prof_especialidades pre,
//			mpa_pop_agendas poa,
//			MPA_POPS pop,
//			mpa_pop_versoes pov,
//			MPA_POP_EXAMES poe,
//			agh_atendimentos atd
//			where esp.seq = pre.esp_seq
//			and esp.seq = poa.esp_seq
//			and poa.pov_pop_seq = pop.seq
//			and pop.seq = pov.pop_seq
//			and pov.pop_seq = poe.pov_pop_seq
//			and pov.versao = poe.pov_versao
//			and pov.ind_situacao = 'L'
//			and atd.esp_seq=poa.esp_seq
//			and atd.seq=13618754
//			and pre.SER_MATRICULA=31234
//			and pre.SER_VIN_CODIGO=1
//			and ( (atd.origem = 'A' and pop.tipo = 'A' and pre.ind_atua_ambt = 'S') or
//			(atd.origem = 'I' and pop.tipo = 'I' and pre.ind_atua_internacao = 'S')))   	
	
	public void filtroExameEspecialidadeAtendimento(StringBuilder hql, RapServidoresId idServidor, Integer seqAtendimento){
		if (idServidor != null && idServidor.getMatricula() != null && idServidor.getVinCodigo() != null && seqAtendimento != null){
			hql.append(" and ema.").append(AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString()).append(" in (select distinct ");
			hql.append(" poe.").append(MpaPopExame.Fields.EXAME_SIGLA.toString());
			hql.append(" from ").append(AghEspecialidades.class.getSimpleName()).append(" esp, ");
			hql.append(AghProfEspecialidades.class.getSimpleName()).append(" pre, ");
			hql.append(MpaPopAgendas.class.getSimpleName()).append(" poa, ");
			hql.append(MpaPops.class.getSimpleName()).append(" pop, ");
			hql.append(MpaPopVersoes.class.getSimpleName()).append(" pov, ");
			hql.append(MpaPopExame.class.getSimpleName()).append(" poe, ");
			hql.append(AghAtendimentos.class.getSimpleName()).append(" atd ");
			hql.append(" where esp.").append(AghEspecialidades.Fields.SEQ.toString());
			hql.append(" = pre.").append(AghProfEspecialidades.Fields.ESP_SEQ.toString());
			
			hql.append(" and esp.").append(AghEspecialidades.Fields.SEQ.toString());
			hql.append(" = poa.").append(MpaPopAgendas.Fields.ESP_SEQ.toString());

			hql.append(" and poa.").append(MpaPopAgendas.Fields.POV_POP_SEQ.toString());
			hql.append(" = pop.").append(MpaPops.Fields.SEQ.toString());

			hql.append(" and pop.").append(MpaPops.Fields.SEQ.toString());
			hql.append(" = pov.").append(MpaPopVersoes.Fields.POP_SEQ.toString());

			hql.append(" and pov.").append(MpaPopVersoes.Fields.POP_SEQ.toString());
			hql.append(" = poe.").append(MpaPopExame.Fields.POV_POP_SEQ.toString());

			hql.append(" and pov.").append(MpaPopVersoes.Fields.VERSAO.toString());
			hql.append(" = poe.").append(MpaPopExame.Fields.POV_VERSAO.toString());

			hql.append(" and pov.").append(MpaPopVersoes.Fields.IND_SITUACAO.toString());
			hql.append(" = '").append(DominioSituacaoPopVersao.L.toString()).append("' ");

			hql.append(" and atd.").append(AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString());
			hql.append(" = poa.").append(MpaPopAgendas.Fields.ESP_SEQ.toString());

			hql.append(" and ( (atd.").append(AghAtendimentos.Fields.ORIGEM.toString()).append(" = '").append(DominioOrigemAtendimento.A.toString()).append("' ");
			hql.append(" and pop.").append(MpaPops.Fields.TIPO.toString()).append(" = '").append(DominioSituacao.A.toString()).append("' ");
			hql.append(" and pre.").append(AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString()).append(" = '").append(DominioSimNao.S.toString()).append("') ");
			
			hql.append(" or (atd.").append(AghAtendimentos.Fields.ORIGEM.toString()).append(" = '").append(DominioOrigemAtendimento.I.toString()).append("' ");
			hql.append(" and pop.").append(MpaPops.Fields.TIPO.toString()).append(" = '").append(DominioSituacao.I.toString()).append("' ");
			hql.append(" and pre.").append(AghProfEspecialidades.Fields.IND_ATUA_INTERNACAO.toString()).append(" = '").append(DominioSimNao.S.toString()).append("') )");
			
			hql.append(" and atd.").append(AghAtendimentos.Fields.SEQ.toString()).append(" =  :seqAtendimento ");		
			hql.append(" and pre.").append(AghProfEspecialidades.Fields.SER_MATRICULA.toString()).append(" =  :serMatricula ");
			hql.append(" and pre.").append(AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString()).append(" =  :serVinculo ");
			hql.append(" )");
		}

	}
	
	/**
	 * Pesquisa por AelUnfExecutaExames que pertença ao lote informado por parâmetro
	 * .<br>
	 * Atraves da associacao obrigatorio entre as entidades:<br>
	 * <p>
	 * AelExamesMaterialAnalise -> AelMateriaisAnalise
	 *                          -> AelExames -> AelSinonimoExame
	 *                          -> AelUnfExecutaExames -> AghUnidadesFuncionais
	 * </p>
	 * @param siglasFiltro 
	 * 
	 * @param nomeExame
	 * @return Lista de array de duas posicoes de Object. Posicoes: { AelUnfExecutaExames, AelSinonimoExame }.
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisaUnidadeExecutaSinonimoExameLote(final Short leuSeq, List<String> siglasFiltro, Short seqUnidade, Boolean isSus, Integer seqAtendimento) {
		/* Sql projetado para o Desenvolvimento da estoria.
		 * Caso seja informado Número do Atendimento e ID do Rapservidor serão listados apenas exames
		 * do protocolo de enfermagem disponíveis para a especialidade deste atendimento.
			SELECT sie.nome      dsp_descricao_usual_exame,
		       man.descricao dsp_descricao_material,
		       unf.descricao dsp_descricao_unidade,
		       Lower(man.descricao) dsp_material_unidade,
		       ema.exa_sigla ufe_ema_exa_sigla,
		       ema.man_seq   ufe_ema_man_seq,
		       ufe.unf_seq   ufe_unf_seq
		FROM   agh.ael_lote_exames loe
			 INNER JOIN agh.ael_exames_material_analise ema 
		         ON ema.exa_sigla = loe.ema_exa_sigla 
		            AND ema.man_seq = loe.ema_man_seq 
		       INNER JOIN agh.ael_exames exa 
		         ON exa.sigla = ema.exa_sigla 
		       INNER JOIN agh.ael_sinonimos_exames sie
		         ON sie.exa_sigla = exa.sigla
		       INNER JOIN agh.ael_materiais_analises man 
		         ON man.seq = ema.man_seq 
		       INNER JOIN agh.ael_unf_executa_exames ufe
			 ON ema.exa_sigla = ufe.ema_exa_sigla
				AND ema.man_seq = ufe.ema_man_seq
		       INNER JOIN agh.agh_unidades_funcionais unf
			 ON unf.seq = ufe.unf_seq
		WHERE  ufe.ind_situacao = 'A'
		       AND ema.ind_situacao = 'A'
		       AND sie.ind_situacao = 'A'
		       AND exa.ind_situacao = 'A'
		       AND man.ind_situacao = 'A'
		       AND unf.ind_sit_unid_func = 'A'
		       AND ( ema.ind_sol_sistema is NULL
		              OR ema.ind_sol_sistema = 'N' )
		       AND ema.ind_dependente = 'N'
			and loe.leu_seq = 11
		 */		
		final StringBuilder hql = new StringBuilder(550);
		
		hql.append("select ufe, sie");
		
		hql.append(" from ").append(AelLoteExame.class.getSimpleName()).append(" leu ");
		hql.append(" inner join leu.").append(AelLoteExame.Fields.EMA.toString()).append(" ema ");
		hql.append(" inner join ema.").append(AelExamesMaterialAnalise.Fields.EXAME.toString()).append(" exa ");
		
		hql.append(" inner join exa.").append(AelExames.Fields.SINONIMO_EXAME.toString()).append(" sie ");
		hql.append(" inner join ema.").append(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()).append(" man ");
				
		hql.append(" inner join ema.").append(AelExamesMaterialAnalise.Fields.UNF_EXECUTA_EXAME.toString()).append(" ufe ");
		hql.append(" inner join ufe.").append(AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString()).append(" unf ");
		
		// #31396 - Verifica permissões caso exista uma Unidade Funcional selecionada e convenio for igual a SUS
		if (seqUnidade != null && isSus){
			hql.append(" inner join ufe.").append(AelUnfExecutaExames.Fields.PERMISSAOUNIDSOLICS.toString()).append(" unfPermiss ");
		}
		
		hql.append(" where ema.").append(AelExamesMaterialAnalise.Fields.IND_SITUACAO.toString()).append(" =  :indSituacaoAtiva");
		hql.append(" and ( ema.").append(AelExamesMaterialAnalise.Fields.IND_SOL_SISTEMA.toString()).append(" is null ");
		hql.append(" or ema.").append(AelExamesMaterialAnalise.Fields.IND_SOL_SISTEMA.toString()).append(" = :indSolSistemaNAO )");
		hql.append(" and ema.").append(AelExamesMaterialAnalise.Fields.IND_DEPENDENTE.toString()).append(" =  :indDependenteEmaNAO ");
		hql.append(" and exa.").append(AelExames.Fields.IND_SITUACAO.toString()).append(" =  :indSituacaoAtiva ");
		hql.append(" and ufe.").append(AelUnfExecutaExames.Fields.SITUACAO.toString()).append(" =  :indSituacaoAtiva ");

		hql.append(" and sie.").append(AelSinonimoExame.Fields.IND_SITUACAO.toString()).append(" =  :indSituacaoAtiva ");
		hql.append(" and man.").append(AelMateriaisAnalises.Fields.IND_SITUACAO.toString()).append(" =  :indSituacaoAtiva ");
		
		hql.append(" and unf.").append(AghUnidadesFuncionais.Fields.SITUACAO.toString()).append(" =  :indSituacaoAtiva ");
		hql.append(" and leu.").append(AelLoteExame.Fields.LEUSEQ.toString()).append(" =  :leuSeq ");
		
		// #31396 - Verifica permissões caso exista uma Unidade Funcional selecionada e convenio for igual a SUS
		if (seqUnidade != null && isSus){
			hql.append(" and unfPermiss.").append(AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString()).append(" = :uniFuncional ");
		}
		if (siglasFiltro != null && !siglasFiltro.isEmpty()){
			hql.append(" and exa.sigla in ( :siglasFiltro ) ");
		}
		
		hql.append( "order by ");
		hql.append("sie.").append(AelSinonimoExame.Fields.NOME.toString());
		hql.append(", man.").append(AelMateriaisAnalises.Fields.DESCRICAO.toString()).append(", ");
		hql.append(" unf.").append(AghUnidadesFuncionais.Fields.DESCRICAO.toString()).append(' ');
		
		final Query query = this.createQuery(hql.toString());
		query.setParameter("indSituacaoAtiva", DominioSituacao.A);
		query.setParameter("indSolSistemaNAO", Boolean.FALSE);
		query.setParameter("indDependenteEmaNAO", Boolean.FALSE);
		query.setParameter("leuSeq", leuSeq);
		if (seqUnidade != null && isSus){
			query.setParameter("uniFuncional", seqUnidade);
		}
		
		if (seqUnidade != null && isSus){
			query.setParameter("uniFuncional", seqUnidade);
		}
		if (siglasFiltro != null && !siglasFiltro.isEmpty()){
			query.setParameter("siglasFiltro", siglasFiltro);
		}
		
		return (List<Object[]>) query.getResultList();
	}
	
	private String getClausulaDescricao() {
		final StringBuilder builder = new StringBuilder();
		final String separador = " || ' - ' || ";
		
		builder.append(" sie.").append(AelSinonimoExame.Fields.NOME.toString()).append(separador).
				append("man.").append(AelMateriaisAnalises.Fields.DESCRICAO.toString()).append(separador).
				append("unf.").append(AghUnidadesFuncionais.Fields.DESCRICAO.toString()).append(' ');
		
		return builder.toString();
	}

	/**
	 * Busca o tempo médio de ocupação da sala.
	 * 
	 * @param emaExaSigla Sigla do exame
	 * @param emaManSeq Seq do material de aálise
	 * @param unfSeq Seq da unidade funcional
	 * @return Date
	 */
	public Date obterTempoMedioOcupacaoSala(String emaExaSigla, Integer emaManSeq, Short unfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelUnfExecutaExames.class);
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		
		criteria.setProjection(Projections.max(AelUnfExecutaExames.Fields.TEMPO_MEDIO_OCUPACAO_SALA.toString()));
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}


	public List<String> buscaSiglasFiltroProtocEnfAgenda(Integer seqAtendimento, RapServidoresId idServidor) {
		
		List<RapConselhosProfissionais> listaConselhoProf = this.listarConselhoProfissionalComNroRegConselhoColabAtivo(idServidor.getMatricula(), idServidor.getVinCodigo());
		
		if (listaConselhoProf == null || listaConselhoProf.isEmpty()){
			return null;
		} else {
		    final DetachedCriteria subCriteria = DetachedCriteria.forClass(AghEspecialidades.class, "ESP");
		    subCriteria.createAlias(AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "PRE", JoinType.INNER_JOIN);
		    
		    subCriteria.createAlias(AghEspecialidades.Fields.MPA_POP_AGENDAS.toString(), "POA", JoinType.INNER_JOIN);
		  
		    subCriteria.createAlias("POA."+MpaPopAgendas.Fields.MPA_POP_VERSOES.toString(), "POV", JoinType.INNER_JOIN);
		    
		    subCriteria.createAlias("POV."+MpaPopVersoes.Fields.MPA_POP.toString(), "POP", JoinType.INNER_JOIN);
		    
		    subCriteria.createAlias("POV."+MpaPopVersoes.Fields.MPA_POP_EXAME.toString(), "POE", JoinType.INNER_JOIN);
		    
		    subCriteria.createAlias(AghEspecialidades.Fields.ATENDIMENTOS.toString(), "ATD", JoinType.INNER_JOIN);
		    
		    subCriteria.setProjection(Projections.distinct(Projections.property("POE."+MpaPopExame.Fields.EXAME_SIGLA.toString())));
	
		    subCriteria.add(Restrictions.eq("POV."+MpaPopVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoPopVersao.L));
		    
		    subCriteria.add(Restrictions.and(Restrictions.eq("ATD."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.A),
				     Restrictions.eq("POP."+MpaPops.Fields.TIPO.toString(), DominioSituacao.A),
				     Restrictions.eq("PRE."+AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), Boolean.TRUE)));
			subCriteria.add( Restrictions.eq("ATD."+ AghAtendimentos.Fields.SEQ.toString(), seqAtendimento));
			subCriteria.add( Restrictions.eq("PRE."+ AghProfEspecialidades.Fields.SER_MATRICULA.toString(), idServidor.getMatricula()));
			subCriteria.add( Restrictions.eq("PRE."+ AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), idServidor.getVinCodigo()));
			
			subCriteria.add(Restrictions.in("POP."+ MpaPops.Fields.CONSELHO_PROFISSIONAL.toString(), listaConselhoProf));	    
			
			return executeCriteria(subCriteria);
		}
		
	}
	
	
    public List<String> buscaSiglasFiltroProtocEnf(RapServidoresId idServidor) {
		List<RapConselhosProfissionais> listaConselhoProf = this.listarConselhoProfissionalComNroRegConselhoColabAtivo(idServidor.getMatricula(), idServidor.getVinCodigo());
		
		if (listaConselhoProf == null || listaConselhoProf.isEmpty()){
			return null;
		} else {
	    	List<String> listaSiglas = null;
	    	
	    	final DetachedCriteria subCriteria = DetachedCriteria.forClass(MpaPopExame.class, "POE");
	    	subCriteria.createAlias(MpaPopExame.Fields.POP_VERSAO.toString(), "POV", JoinType.INNER_JOIN);
	    	subCriteria.createAlias("POV."+MpaPopVersoes.Fields.MPA_POP.toString(), "POP", JoinType.INNER_JOIN);
	    	
	    	subCriteria.add( Restrictions.eq("POV."+ MpaPopVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoPopVersao.L));
	    	subCriteria.add( Restrictions.eq("POP."+ MpaPops.Fields.TIPO.toString(), DominioSituacao.I));
	    	
			subCriteria.add(Restrictions.in("POP."+ MpaPops.Fields.CONSELHO_PROFISSIONAL.toString(), listaConselhoProf));	 
			
			subCriteria.setProjection(Projections.distinct(Projections.property("POE."+MpaPopExame.Fields.EXAME_SIGLA.toString())));
			
			listaSiglas = executeCriteria(subCriteria);
			
	    	return listaSiglas;
		}
    }

	
	private List<RapConselhosProfissionais> listarConselhoProfissionalComNroRegConselhoColabAtivo(Integer matricula, Short vinCodigo){
		return rapConselhosProfissionaisDAO.listarConselhoProfissionalComNroRegConselhoColabAtivo(matricula, vinCodigo);
	}
	
}
