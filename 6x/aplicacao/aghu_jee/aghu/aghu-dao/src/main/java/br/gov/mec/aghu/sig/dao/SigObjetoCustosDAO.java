package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoPorCentroCustoVO;

public class SigObjetoCustosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigObjetoCustos> {

	private static final long serialVersionUID = 3536942429865067091L;

	public List<SigObjetoCustos> pesquisarObjetoCustoAssociadoCentroCustoOuSemCentroCusto(Object param, FccCentroCustos centroCusto) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustos.class, "obj");
		criteria.createCriteria("obj." + SigObjetoCustos.Fields.VERSOES.toString(), "ocv", JoinType.INNER_JOIN);

		if (centroCusto != null) {
			criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "occc", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq("occc." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), centroCusto));
		} else {
			criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "occc", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.isNull("occc." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString()));
		}

		String nome = param.toString();
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike("obj." + SigObjetoCustos.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		// #28488 - Clausula de distinct, pois retorna varias vezes o mesmo objeto de custo no suggestionbox
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return this.executeCriteria(criteria);
	}

	public List<SigObjetoCustos> pesquisarObjetoCustoAssociadoClientes(Object param, FccCentroCustos centroCusto) {

		//Exibir somente os objetos de custo que possuem clientes associados a direcionadores com tipo de cálculo por produção manual.
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustos.class, "obj");

		criteria.createCriteria("obj." + SigObjetoCustos.Fields.VERSOES.toString(), "ocv", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "obc", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.CLIENTES.toString(), "occ", JoinType.INNER_JOIN);
		criteria.createCriteria("occ." + SigObjetoCustoClientes.Fields.DIRECIONADORES.toString(), "dir", JoinType.INNER_JOIN);

		//Compara o centro de custo
		criteria.add(Restrictions.eq("obc." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), centroCusto));
		criteria.add(Restrictions.eq("dir." + SigDirecionadores.Fields.INDICADOR_TIPO_CALCULO_OBJETO.toString(), DominioTipoCalculoObjeto.PM));

		String nome = param.toString();
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike("obj." + SigObjetoCustos.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		//#28305 - Clausula de distinct, pois retorna varias vezes o mesmo objeto de custo no suggestionbox
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return this.executeCriteria(criteria);
	}

	public SigObjetoCustos obterObjetoCustoByNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustos.class, "obj");
		criteria.add(Restrictions.eq("obj." + SigObjetoCustos.Fields.NOME.toString(), nome));
		return (SigObjetoCustos) this.executeCriteriaUniqueResult(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<ObjetoCustoPorCentroCustoVO> pesquisarObjetoCustoPorCentroCusto(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCusto, Boolean indPossuiObjCusto, Boolean indPossuiComposicao, SigCentroProducao centroProducao, DominioSituacao indSituacao) {

		List<ObjetoCustoPorCentroCustoVO> listResult = new ArrayList<ObjetoCustoPorCentroCustoVO>();

		Query query = this.getQueryPesquisaPaginada(centroCusto, indPossuiObjCusto, indPossuiComposicao, centroProducao, indSituacao);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		List<Object[]> list = query.getResultList();
		for (Object[] object : list) {
			listResult.add(ObjetoCustoPorCentroCustoVO.create(object));
		}

		return listResult;
	}

	private Query getQueryPesquisaPaginada(FccCentroCustos centroCusto, Boolean indPossuiObjCusto, Boolean indPossuiComposicao, SigCentroProducao centroProducao, DominioSituacao indSituacao) {
		StringBuilder hql = new StringBuilder(500);
		hql.append("SELECT ");
		hql.append("cp." ).append( SigCentroProducao.Fields.NOME ).append( ", ");
		hql.append("cc." ).append( FccCentroCustos.Fields.CODIGO ).append( ", ");
		hql.append("cc." ).append( FccCentroCustos.Fields.DESCRICAO ).append( ", ");
		hql.append("cc." ).append( FccCentroCustos.Fields.SITUACAO ).append( ", ");
		hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.E ).append('\''
				).append( " THEN 1 ELSE 0 END) as cont_elaboracao, ");
		hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.P ).append('\''
				).append( " THEN 1 ELSE 0 END) as cont_programado, ");
		hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.A ).append('\''
				).append( " THEN 1 ELSE 0 END) as cont_ativo, ");
		hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.I ).append('\''
				).append( " THEN 1 ELSE 0 END) as cont_inativo ");
		hql.append("FROM ");
		hql.append(SigObjetoCustoCcts.class.getSimpleName() ).append( " oct ");
		hql.append("LEFT JOIN oct." ).append( SigObjetoCustoCcts.Fields.OBJETO_CUSTO_VERSAO ).append( " ocv ");
		hql.append("LEFT JOIN oct." ).append( SigObjetoCustoCcts.Fields.CENTRO_CUSTO ).append( " cc ");
		hql.append("LEFT JOIN cc." ).append( FccCentroCustos.Fields.CENTRO_PRODUCAO ).append( " cp ");

		boolean where = false;
		if (centroProducao != null) {
			hql.append("WHERE ");
			where = true;
			hql.append("cp." ).append( SigCentroProducao.Fields.SEQ ).append( " = " ).append( centroProducao.getSeq() ).append(' ');
		}

		if (indPossuiComposicao != null) {
			if (where) {
				hql.append("AND ");
			} else {
				hql.append("WHERE ");
				where = true;
			}
			if (indPossuiComposicao) {
				hql.append("ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ ).append( " in (SELECT occ." ).append( SigObjetoCustoComposicoes.Fields.SEQ ).append( " FROM "
						).append( SigObjetoCustoComposicoes.class.getSimpleName() ).append( " occ) ");
			} else {
				hql.append("ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ ).append( " not in (SELECT occ." ).append( SigObjetoCustoComposicoes.Fields.SEQ ).append( " FROM "
						).append( SigObjetoCustoComposicoes.class.getSimpleName() ).append( " occ) ");
			}
		}

		if (centroCusto != null && !centroCusto.getDescricao().isEmpty()) {
			if (where) {
				hql.append("AND ");
			} else {
				hql.append("WHERE ");
				where = true;
			}
			hql.append("cc." ).append( FccCentroCustos.Fields.CODIGO ).append( " =  " ).append( centroCusto.getCodigo() ).append(' ');
		}

		if (indSituacao != null) {
			if (where) {
				hql.append("AND ");
			} else {
				hql.append("WHERE ");
			}
			hql.append("cc." ).append( FccCentroCustos.Fields.SITUACAO ).append( " = '" ).append( indSituacao ).append( "' ");
		}

		hql.append("GROUP BY ");
		hql.append("cp." ).append( SigCentroProducao.Fields.NOME ).append( ", ");
		hql.append("cc." ).append( FccCentroCustos.Fields.CODIGO ).append( ", ");
		hql.append("cc." ).append( FccCentroCustos.Fields.DESCRICAO ).append( ", ");
		hql.append("cc." ).append( FccCentroCustos.Fields.SITUACAO ).append(' ');

		if (indPossuiObjCusto) {
			hql.append("HAVING ");
			hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.E ).append('\''
					).append( " THEN 1 ELSE 0 END) > 0 or ");
			hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.P ).append('\''
					).append( " THEN 1 ELSE 0 END) > 0 or ");
			hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.A ).append('\''
					).append( " THEN 1 ELSE 0 END) > 0 or ");
			hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.I ).append('\''
					).append( " THEN 1 ELSE 0 END) > 0 ");
		} else {
			hql.append("HAVING ");
			hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.E ).append('\''
					).append( " THEN 1 ELSE 0 END) = 0 and ");
			hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.P ).append('\''
					).append( " THEN 1 ELSE 0 END) = 0 and ");
			hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.A ).append('\''
					).append( " THEN 1 ELSE 0 END) = 0 and ");
			hql.append("SUM(CASE WHEN ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO ).append( " = '" ).append( DominioSituacaoVersoesCustos.I ).append('\''
					).append( " THEN 1 ELSE 0 END) = 0 ");
		}

		hql.append("ORDER BY cc." ).append( FccCentroCustos.Fields.DESCRICAO ).append(' ');

		Query query = this.createQuery(hql.toString());
		return query;
	}

	@SuppressWarnings("unchecked")
	public Integer pesquisarObjetoCustoPorCentroCustoCount(FccCentroCustos centroCusto, Boolean indPossuiObjCusto, Boolean indPossuiComposicao, SigCentroProducao centroProducao, DominioSituacao indSituacao) {
		Query query = this.getQueryPesquisaPaginada(centroCusto, indPossuiObjCusto, indPossuiComposicao, centroProducao, indSituacao);
		List<Object[]> list = query.getResultList();
		if (list != null && !list.isEmpty()) {
			return list.size();
		} else {
			return Integer.valueOf(0);
		}
	}
}
