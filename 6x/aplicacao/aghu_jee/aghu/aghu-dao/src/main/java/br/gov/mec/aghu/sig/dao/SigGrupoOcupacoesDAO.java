package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigGrupoOcupacaoCargos;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.sig.custos.vo.EquipeCirurgiaVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class SigGrupoOcupacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigGrupoOcupacoes> {

	private static final long serialVersionUID = -5787875215555023132L;

	public List<SigGrupoOcupacoes> pesquisarGrupoOcupacao(Object paramPesquisa, FccCentroCustos centroCustoAtividade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigGrupoOcupacoes.class);

		String srtPesquisa = (String) paramPesquisa;

		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(paramPesquisa)) {
				criteria.add(Restrictions.eq(SigGrupoOcupacoes.Fields.SEQ.toString(), Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(SigGrupoOcupacoes.Fields.DESCRICAO.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(SigGrupoOcupacoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.or(Restrictions.isNull(SigGrupoOcupacoes.Fields.CENTRO_CUSTO.toString()),
				Restrictions.eq(SigGrupoOcupacoes.Fields.CENTRO_CUSTO.toString(), centroCustoAtividade)));

		criteria.addOrder(Order.asc(SigGrupoOcupacoes.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}

	public List<SigGrupoOcupacoes> pesquisarGrupoOcupacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String descricao,
			DominioSituacao situacao, FccCentroCustos centroCusto) {

		DetachedCriteria criteria = this.criarCriteriaPesquisaGrupoOcupacao(descricao, situacao, centroCusto, true);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, true);
	}

	public Long pesquisarGrupoOcupacaoCount(String descricao, DominioSituacao situacao, FccCentroCustos centroCusto) {

		DetachedCriteria criteria = this.criarCriteriaPesquisaGrupoOcupacao(descricao, situacao, centroCusto, false);
		return this.executeCriteriaCount(criteria);
	}

	public List<SigGrupoOcupacoes> obterGruposPelaDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigGrupoOcupacoes.class);
		criteria.add(Restrictions.ilike(SigGrupoOcupacoes.Fields.DESCRICAO.toString(), descricao, MatchMode.EXACT));
		return this.executeCriteria(criteria);
	}

	protected DetachedCriteria criarCriteriaPesquisaGrupoOcupacao(String descricao, DominioSituacao situacao, FccCentroCustos centroCusto, boolean utilizarOrder) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigGrupoOcupacoes.class);
		
		criteria.setFetchMode(SigGrupoOcupacoes.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(SigGrupoOcupacoes.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(SigGrupoOcupacoes.Fields.IND_SITUACAO.toString(), situacao));
		}

		if (centroCusto != null) {
			criteria.add(Restrictions.eq(SigGrupoOcupacoes.Fields.CENTRO_CUSTO.toString(), centroCusto));
		}

		if (utilizarOrder) {
			criteria.addOrder(Order.asc(SigGrupoOcupacoes.Fields.DESCRICAO.toString()));
		}

		return criteria;
	}

	/**
	 * Efetua a busca das equipes cirurgicas que efetuaram uma determinada cirurgia
	 * @param crgSeq - Seq da cirurgia
	 * @return List<{@link EquipeCirurgiaVO}> 
	 * @author jgugel
	 */
	@SuppressWarnings("unchecked")
	public List<EquipeCirurgiaVO> buscaEquipeCirurgia(Integer crgSeq) {
		StringBuilder hql = new StringBuilder(600);

		hql.append("SELECT ");
		hql.append("goc." ).append( SigGrupoOcupacoes.Fields.SEQ ).append( ", ");
		hql.append("ser." ).append( RapServidores.Fields.CODIGO_CARGO ).append( ", ");
		hql.append("ser." ).append( RapServidores.Fields.CODIGO_OCUPACAO ).append( ", ");
		hql.append("COALESCE(ser." ).append( RapServidores.Fields.CENTRO_CUSTO_ATUACAO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ser."
				).append( RapServidores.Fields.CENTRO_CUSTO_LOTACAO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ") AS cct_codigo, ");
		hql.append("CASE ");
		hql.append("   WHEN pcg." ).append( MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF ).append( " IN ('ANP', 'ANC', 'ANR') THEN 'A' ");
		hql.append("   WHEN pcg." ).append( MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF ).append( " IN ('MPF', 'MCO', 'MAX', 'INS') THEN 'M' ");
		hql.append("   ELSE 'O' ");
		hql.append("END as tipo, ");
		hql.append("COUNT(*) ");
		hql.append("FROM ");

		hql.append(MbcProfCirurgias.class.getSimpleName() ).append( " pcg, ");
		hql.append(RapServidores.class.getSimpleName() ).append( " ser  ");
		hql.append(" LEFT OUTER JOIN  ");
		hql.append("ser." ).append( RapServidores.Fields.OCUPACAO_CARGO.toString() ).append( " roc ");
		hql.append(" LEFT OUTER JOIN ");
		hql.append("roc." ).append( RapOcupacaoCargo.Fields.SIG_OCUPACAO_CARGO.toString() ).append( " gca ");
		hql.append(" LEFT OUTER JOIN ");
		hql.append("gca." ).append( SigGrupoOcupacaoCargos.Fields.SIG_GRUPO_OCUPACOES ).append( " goc ");

		hql.append("where  ");
		hql.append("pcg." ).append( MbcProfCirurgias.Fields.CIRURGIA ).append( '.' ).append( MbcCirurgias.Fields.SEQ ).append( " = :seqCirurgia ");
		hql.append("and pcg." ).append( MbcProfCirurgias.Fields.SERVIDOR_PUC ).append( '.' ).append( RapServidores.Fields.VIN_CODIGO ).append( " = ser." ).append( RapServidores.Fields.VIN_CODIGO
				).append( ' ');
		hql.append("and pcg." ).append( MbcProfCirurgias.Fields.SERVIDOR_PUC ).append( '.' ).append( RapServidores.Fields.MATRICULA ).append( " = ser." ).append( RapServidores.Fields.MATRICULA ).append( ' ');
		hql.append("and goc." ).append( SigGrupoOcupacoes.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( " is null ");

		hql.append("group by goc." ).append( SigGrupoOcupacoes.Fields.SEQ ).append( ", ");
		hql.append("ser." ).append( RapServidores.Fields.CODIGO_CARGO ).append( ", ");
		hql.append("ser." ).append( RapServidores.Fields.CODIGO_OCUPACAO ).append( ", ");
		hql.append("COALESCE(ser." ).append( RapServidores.Fields.CENTRO_CUSTO_ATUACAO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ser."
				).append( RapServidores.Fields.CENTRO_CUSTO_LOTACAO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( "), ");
		hql.append("CASE ");
		hql.append("   WHEN pcg." ).append( MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF ).append( " IN ('ANP', 'ANC', 'ANR') THEN 'A' ");
		hql.append("   WHEN pcg." ).append( MbcProfCirurgias.Fields.PUC_IND_FUNCAO_PROF ).append( " IN ('MPF', 'MCO', 'MAX', 'INS') THEN 'M' ");
		hql.append("   ELSE 'O' ");
		hql.append("END ");
		hql.append("order by 5 ");

		org.hibernate.Query query = this.createHibernateQuery(hql.toString());
		query.setParameter("seqCirurgia", crgSeq);

		query.setResultTransformer((ResultTransformer) new EquipeCirurgiaVO());
		return (List<EquipeCirurgiaVO>) query.list();
	}
}
