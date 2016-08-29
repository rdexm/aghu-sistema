package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.TipoMovimentoVO;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class SceTipoMovimentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceTipoMovimento> {

	private static final long serialVersionUID = 301523295793800211L;

	public SceTipoMovimento obterSceTipoMovimentosAtivoPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return (SceTipoMovimento) executeCriteriaUniqueResult(criteria);
	}

	public SceTipoMovimento obterSceTipoMovimentosAtivoPorSceTipoMovimentos(SceTipoMovimento sceTipoMovimentos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.SEQ.toString(), sceTipoMovimentos.getId().getSeq()));
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.COMPLEMENTO.toString(), sceTipoMovimentos.getId().getComplemento()));
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return (SceTipoMovimento) executeCriteriaUniqueResult(criteria);
	}

	public SceTipoMovimento obterSceTipoMovimentosSeqComplemento(SceTipoMovimento sceTipoMovimentos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.SEQ.toString(), sceTipoMovimentos.getId().getSeq()));
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.COMPLEMENTO.toString(), sceTipoMovimentos.getId().getComplemento()));
		return (SceTipoMovimento) executeCriteriaUniqueResult(criteria);
	}

	public SceTipoMovimento obterSceTipoMovimentosSeqComplemento(Short seq, Byte complemento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.COMPLEMENTO.toString(), complemento));
		return (SceTipoMovimento) executeCriteriaUniqueResult(criteria);
	}

	public List<SceTipoMovimento> obterTipoMovimentoPorSeqDescricao(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);
		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq(SceTipoMovimento.Fields.SEQ.toString(), Short.parseShort(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(SceTipoMovimento.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa),
						MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public List<SceTipoMovimento> obterTipoMovimentoPorSeqDescricaoAjustes(String param, List<Short> paramsValores) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);
		String strPesquisa = param;

		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq(SceTipoMovimento.Fields.SEQ.toString(), Short.parseShort(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(SceTipoMovimento.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa),
						MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.in(SceTipoMovimento.Fields.SEQ.toString(), paramsValores));

		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public List<SceTipoMovimento> obterTipoMovimentoPorSiglaDescricao(Object param) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);

		final String srtPesquisa = (String) param;

		if (CoreUtil.isNumeroInteger(param)) {

			criteria.add(Restrictions.eq(SceTipoMovimento.Fields.SEQ.toString(), Short.valueOf(srtPesquisa)));

		} else if (StringUtils.isNotEmpty(srtPesquisa)) {

			Criterion lhs = Restrictions.ilike(SceTipoMovimento.Fields.SIGLA.toString(), srtPesquisa, MatchMode.EXACT);
			Criterion rhs = Restrictions.ilike(SceTipoMovimento.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(lhs, rhs));

		}

		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(SceTipoMovimento.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);

	}
	
	/**
	 * #34348 List Box
	 * Método utilizado para obter Tipos de Movimentos 
	 * utilizados no List Box.
	 */
	public List<TipoMovimentoVO> obterTipoMovimentoListBox() {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);
		
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_ESL.toString(), Boolean.TRUE));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(SceTipoMovimento.Fields.SEQ.toString()), TipoMovimentoVO.Fields.SEQ.toString())
				.add(Projections.property(SceTipoMovimento.Fields.SIGLA.toString()), TipoMovimentoVO.Fields.SIGLA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(TipoMovimentoVO.class));

		return executeCriteria(criteria);
	}
	
	public List<SceTipoMovimento> obterTipoMovimentoPorSigla(Object param) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);

		final String srtPesquisa = (String) param;

		criteria.add(Restrictions.ilike(SceTipoMovimento.Fields.SIGLA.toString(), srtPesquisa, MatchMode.EXACT));

		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(SceTipoMovimento.Fields.DESCRICAO.toString()));

		List<SceTipoMovimento> tipoMovimentos = executeCriteria(criteria);

		if (tipoMovimentos != null && !tipoMovimentos.isEmpty()) {

			return tipoMovimentos;

		}

		return obterTipoMovimentoPorSiglaDescricao(param);
	}

	public List<SceTipoMovimento> obterSiglaTipoMovimento(Object param) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);

		final String strPesquisa = (String) param;

		if (strPesquisa != null && !strPesquisa.equals("")) {
			Criterion eqSigla = Restrictions.like(SceTipoMovimento.Fields.SIGLA.toString(), strPesquisa.toUpperCase(), MatchMode.ANYWHERE);
			Criterion eqDescricao = Restrictions.like(SceTipoMovimento.Fields.DESCRICAO.toString(), strPesquisa.toUpperCase(), MatchMode.ANYWHERE);

			criteria.add(Restrictions.or(eqSigla, eqDescricao));
		}

		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		//trecho alterado pela estória #11033
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_ESL.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_OPERACAO_BASICA.toString(), DominioIndOperacaoBasica.CR));
		//

		criteria.addOrder(Order.asc(SceTipoMovimento.Fields.SIGLA.toString()));

		return executeCriteria(criteria);

	}

	public List<SceTipoMovimento> obterListaTipoMovimentosAtivos(Object param) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);
		String strPesquisa = (String) param;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq(SceTipoMovimento.Fields.SEQ.toString(), Short.parseShort(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(SceTipoMovimento.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa),
						MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_EXIGE_MOTIVO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(SceTipoMovimento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	public Long obterListaTipoMovimentosAtivosCount(Object param) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(SceTipoMovimento.class);
		String strPesquisa = (String) param;
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq(SceTipoMovimento.Fields.SEQ.toString(), Short.parseShort(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(SceTipoMovimento.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa),
						MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_EXIGE_MOTIVO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(SceTipoMovimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}

}
