package br.gov.mec.aghu.sig.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.ColetaViaSistemaVO;

public class SigDirecionadoresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigDirecionadores> {

	private static final long serialVersionUID = -588900484046980887L;

	public List<SigDirecionadores> pesquisarDirecionadores(DominioTipoDirecionadorCustos tipoDirecionador, DominioTipoCalculoObjeto tipoCalculo, Boolean coletaSistema) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigDirecionadores.class);
		criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_TIPO_CALCULO_OBJETO.toString(), tipoCalculo));
		criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_TIPO.toString(), tipoDirecionador));
		if(coletaSistema != null){
			criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_COLETA_SISTEMA.toString(), coletaSistema)); 
		}
		criteria.addOrder(Order.asc(SigDirecionadores.Fields.NOME.toString()));
		return this.executeCriteria(criteria);
	}

	public List<SigDirecionadores> pesquisarDirecionadores(DominioSituacao situacao, DominioTipoDirecionadorCustos tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigDirecionadores.class);

		criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_SITUACAO.toString(), situacao));
		criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_TIPO.toString(), tipo));

		criteria.addOrder(Order.asc(SigDirecionadores.Fields.NOME.toString()));
		return this.executeCriteria(criteria);
	}

	public List<SigDirecionadores> pesquisarDirecionadoresTempoMaiorMes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigDirecionadores.class, "direcionador");

		criteria.add(Restrictions.eq("direcionador." + SigDirecionadores.Fields.INDICADOR_TIPO.toString(), DominioTipoDirecionadorCustos.RC));

		criteria.add(Restrictions.isNotNull("direcionador." + SigDirecionadores.Fields.INDICADOR_TEMPO.toString()));

		criteria.add(Restrictions.isNull("direcionador." + SigDirecionadores.Fields.FAT_CONV_HORAS.toString()));

		criteria.addOrder(Order.asc(SigDirecionadores.Fields.NOME.toString()));
		return this.executeCriteria(criteria);

	}

	public List<SigDirecionadores> pesquisarDirecionadores(Boolean indTempoIsNull, Boolean filtrarFatConvHoraIsNotNull) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigDirecionadores.class, "direcionador");

		criteria.add(Restrictions.eq("direcionador." + SigDirecionadores.Fields.INDICADOR_TIPO.toString(), DominioTipoDirecionadorCustos.RC));

		if (indTempoIsNull.equals(Boolean.FALSE)) {
			criteria.add(Restrictions.isNotNull("direcionador." + SigDirecionadores.Fields.INDICADOR_TEMPO.toString()));
		} else {
			criteria.add(Restrictions.isNull("direcionador." + SigDirecionadores.Fields.INDICADOR_TEMPO.toString()));
		}

		if (filtrarFatConvHoraIsNotNull) {
			criteria.add(Restrictions.isNotNull("direcionador." + SigDirecionadores.Fields.FAT_CONV_HORAS.toString()));
		}

		criteria.addOrder(Order.asc(SigDirecionadores.Fields.NOME.toString()));
		return this.executeCriteria(criteria);
	}

	public List<SigDirecionadores> pesquisarDirecionadoresAtivosInativo(Boolean indTempoIsNull, Boolean ativo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigDirecionadores.class);
		criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_TIPO.toString(), DominioTipoDirecionadorCustos.RC));
		if (ativo) {
			criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_SITUACAO.toString(), DominioSituacao.A));
		} else {
			criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_SITUACAO.toString(), DominioSituacao.I));
		}

		if (indTempoIsNull.equals(Boolean.FALSE)) {
			criteria.add(Restrictions.isNotNull(SigDirecionadores.Fields.INDICADOR_TEMPO.toString()));
		} else {
			criteria.add(Restrictions.isNull(SigDirecionadores.Fields.INDICADOR_TEMPO.toString()));
		}
		criteria.addOrder(Order.asc(SigDirecionadores.Fields.NOME.toString()));
		return this.executeCriteria(criteria);

	}

	public List<SigDirecionadores> pesquisarDirecionadoresTipoATAB(Boolean ativo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigDirecionadores.class);

		criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_TIPO.toString(), DominioTipoDirecionadorCustos.AT));

		if (ativo) {
			criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_SITUACAO.toString(), DominioSituacao.A));
		} else {
			criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_SITUACAO.toString(), DominioSituacao.I));
		}

		criteria.addOrder(Order.asc(SigDirecionadores.Fields.NOME.toString()));
		return this.executeCriteria(criteria);

	}

	public Long pesquisarDirecionadorCount(SigDirecionadores direcionador) {
		return this.executeCriteriaCount(this.createPesquisaDirecionadorAtividadesCriteria(direcionador, false));
	}

	private DetachedCriteria createPesquisaDirecionadorAtividadesCriteria(SigDirecionadores direcionador, boolean isPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigDirecionadores.class);

		if (StringUtils.isNotBlank(direcionador.getNome())) {
			criteria.add(Restrictions.ilike(SigDirecionadores.Fields.NOME.toString(), direcionador.getNome(), MatchMode.ANYWHERE));
		}
		if (direcionador.getIndTipo() != null) {
			criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_TIPO.toString(), direcionador.getIndTipo()));

		}
		if (direcionador.getIndTipoCalculo() != null) {
			criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_TIPO_CALCULO_OBJETO.toString(), direcionador.getIndTipoCalculo()));
		}
		if (direcionador.getIndColetaSistema() != null) {
			criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_COLETA_SISTEMA.toString(), direcionador.getIndColetaSistema()));
		}
		if (direcionador.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_SITUACAO.toString(), direcionador.getIndSituacao()));
		}
		if (isPesquisa) {
			criteria.addOrder(Order.asc(SigDirecionadores.Fields.NOME.toString()));
		}
		return criteria;

	}

	public List<SigDirecionadores> pesquisaDirecionadoresDoObjetoCusto(SigObjetoCustoVersoes versao, DominioSituacao direcionadorRateioSituacao,
			DominioTipoDirecionadorCustos indTipo, DominioTipoCalculoObjeto indTipoCalculo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoDirRateios.class, "odr");

		criteria.createAlias("odr." + SigObjetoCustoDirRateios.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.INNER_JOIN);
		criteria.createAlias("odr." + SigObjetoCustoDirRateios.Fields.DIRECIONADORES.toString(), "dir", JoinType.INNER_JOIN);

		if (versao != null) {
			criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.SEQ.toString(), versao.getSeq()));
		}

		criteria.add(Restrictions.eq("odr." + SigObjetoCustoDirRateios.Fields.SITUACAO.toString(), direcionadorRateioSituacao));
		criteria.add(Restrictions.eq("dir." + SigDirecionadores.Fields.INDICADOR_TIPO.toString(), indTipo));
		criteria.add(Restrictions.eq("dir." + SigDirecionadores.Fields.INDICADOR_TIPO_CALCULO_OBJETO.toString(), indTipoCalculo));

		criteria.addOrder(Order.asc("dir." + SigDirecionadores.Fields.NOME.toString()));

		criteria.setProjection(Projections.projectionList().add(Projections.property("odr." + SigObjetoCustoDirRateios.Fields.DIRECIONADORES.toString())));

		return this.executeCriteria(criteria);

	}

	public List<SigAtividades> pesquisarDirecionadorAtividade(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigDirecionadores direcionador) {
		return this.executeCriteria(this.createPesquisaDirecionadorAtividadesCriteria(direcionador, true), firstResult, maxResult, orderProperty, true);
	}
	
	public SigDirecionadores obterSigDirecionadorByNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigDirecionadores.class);
		criteria.add(Restrictions.eq(SigDirecionadores.Fields.NOME.toString(), nome));		
		return (SigDirecionadores) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<SigDirecionadores> buscarDirecionadoresColetaViaSistema() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigDirecionadores.class);
		
		criteria.add(Restrictions.eq(SigDirecionadores.Fields.INDICADOR_COLETA_SISTEMA.toString(), true)); 
		criteria.add(Restrictions.isNotNull(SigDirecionadores.Fields.NOME_VIEW.toString()));
		criteria.add(Restrictions.isNotNull(SigDirecionadores.Fields.FILTRO.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	
	public List<ColetaViaSistemaVO> buscarValoresViewDirecionador(SigDirecionadores direcionador, SigProcessamentoCusto processamento ){
		
		StringBuilder sql = new StringBuilder(350);
		sql.append(" SELECT ")
			.append(" pmu_seq, ")
			.append(" filtro, ")
			.append(" cct_codigo, ")
			.append(" qtde ")
		.append(" FROM ")
			.append( direcionador.getNomeView() )
		.append(" WHERE ")
			.append(" filtro = :filtro ")
			.append(" AND  ( pmu_seq = :pmuSeq or pmu_seq is null) ");
		
		Query query = this.createNativeQuery(sql.toString());
		
		query.setParameter("filtro", direcionador.getFiltro());
		query.setParameter("pmuSeq", processamento.getSeq());
		
		return ColetaViaSistemaVO.transformar(query.getResultList());		
	}
}
