package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.vo.ProcessoGeracaoAutomaticaVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoLogGeracaoScMatEstocavel;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoLogGeracaoScMatEstocavelDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoLogGeracaoScMatEstocavel> {

	private static final long serialVersionUID = -6089877012117954721L;

	
	public Integer obterMaxProcesso() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLogGeracaoScMatEstocavel.class);
		criteria.setProjection(Projections.max(ScoLogGeracaoScMatEstocavel.Fields.SEQ_PROCESSO.toString()));  
		
		Integer max = (Integer) executeCriteriaUniqueResult(criteria);
		if (max == null) {
			max = Integer.valueOf((int) 0);
		}
				
		return ++max;
		
	}

	public List<ScoLogGeracaoScMatEstocavel> pesquisarRegistrosLogPorNumDias(Integer numDias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLogGeracaoScMatEstocavel.class, "LOG");
		
		criteria.add(Restrictions.
				sqlRestriction("{alias}."+ScoLogGeracaoScMatEstocavel.Fields.DT_GERACAO.name()+" < (current_date - "+numDias+")" ));
		
		return executeCriteria(criteria);
	}
	
	
	public Date obterDataUltimaExecucao() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLogGeracaoScMatEstocavel.class);
		criteria.setProjection(Projections.max(ScoLogGeracaoScMatEstocavel.Fields.DT_GERACAO.toString()));  
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	/**

	 * Pesquisa um processo de geracao automatica de material estocavel pela seq do processo ou pela data de geracao 

	 * @param param

	 * @return List

	 */

	public List<ProcessoGeracaoAutomaticaVO> pesquisarProcessoGeracaoCodigoData(Object param) {
		String strPesquisa = (String) param;
		DetachedCriteria criteria = getProcessoGeracaoCriteria();
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq("LOG."+ScoLogGeracaoScMatEstocavel.Fields.SEQ_PROCESSO.toString(), Integer.valueOf(strPesquisa)));
		} else {
			criteria.add(Restrictions.sqlRestriction("TO_CHAR("+"{alias}."+ScoLogGeracaoScMatEstocavel.Fields.DT_GERACAO.name()+", 'DD/MM/YYYY') LIKE '"+strPesquisa+"%'"));						
		}

		return executeCriteria(criteria, 0, 50, null, false);

	}

	private DetachedCriteria obterCriteriaLogProcessoGeracao(ProcessoGeracaoAutomaticaVO processo, ScoMaterial material, DominioSimNao indContrato) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLogGeracaoScMatEstocavel.class, "LOG");
		
		criteria.createAlias("LOG." + ScoLogGeracaoScMatEstocavel.Fields.MATERIAL.toString(), "MATERIAL", JoinType.INNER_JOIN);
		criteria.createAlias("LOG." + ScoLogGeracaoScMatEstocavel.Fields.ALMOXARIFADO.toString(), "ALMOXARIFADO", JoinType.INNER_JOIN);
		criteria.createAlias("LOG." + ScoLogGeracaoScMatEstocavel.Fields.SOLICITACAO_COMPRA.toString(), "SOLICITACAO_COMPRA", JoinType.LEFT_OUTER_JOIN);		
		
		if (processo != null) {
			criteria.add(Restrictions.eq("LOG."+ScoLogGeracaoScMatEstocavel.Fields.SEQ_PROCESSO.toString(), processo.getSeqProcesso()));
		}

		if (material != null) {
			criteria.add(Restrictions.eq("MATERIAL."+ScoMaterial.Fields.CODIGO.toString(), material.getCodigo()));
		}

		if (indContrato != null) {
			if (indContrato.isSim()) {
				criteria.add( 
						Restrictions.isNotNull("SOLICITACAO_COMPRA."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
			} else {
				criteria.add(
						Restrictions.isNull("SOLICITACAO_COMPRA."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
			}
		}

		return criteria;
	}

	/**
	 * Pesquisa um processo de geracao SC de material estocavel por processo, material e se esta em contrato
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param processo
	 * @param material
	 * @param indContrato
	 * @return List
	 */
	public List<ScoLogGeracaoScMatEstocavel> pesquisarLogGeracaoScMaterialEstocavel(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, ProcessoGeracaoAutomaticaVO processo, ScoMaterial material, DominioSimNao indContrato) {
		DetachedCriteria criteria = this.obterCriteriaLogProcessoGeracao(
				processo, material, indContrato);

		if (orderProperty == null) {
			criteria.addOrder(Order
					.desc(ScoLogGeracaoScMatEstocavel.Fields.QTDE_A_COMPRAR
							.toString()));
		}

		List<ScoLogGeracaoScMatEstocavel> logGeracaoSC = this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
		
		return logGeracaoSC;
	}

	/**
	 * Faz o count dos processos de geracao SC de material estocavel por processo, material e se esta em contrato
	 * @param processo
	 * @param material
	 * @param indContrato
	 * @return Integer
	 */
	public Long contarLogGeracaoScMaterialEstocavel (ProcessoGeracaoAutomaticaVO processo, ScoMaterial material, DominioSimNao indContrato) {
		Long countLogGeracaoSC = executeCriteriaCount(this.obterCriteriaLogProcessoGeracao(processo, material, indContrato));
		return countLogGeracaoSC;
	}

	/** Obtem último processo de geração. */
	public ProcessoGeracaoAutomaticaVO obterUltimoProcessoGeracao() {
		DetachedCriteria criteria = getProcessoGeracaoCriteria();
//		criteria.getExecutableCriteria(getSession()).setMaxResults(1);
		List<ProcessoGeracaoAutomaticaVO> lista = this.executeCriteria(criteria);
		if(lista!= null && !lista.isEmpty()){
			return (ProcessoGeracaoAutomaticaVO)lista.get(0);
		}
		else{
			return null;
		}
	}
	
	private DetachedCriteria getProcessoGeracaoCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				ScoLogGeracaoScMatEstocavel.class, "LOG");

		ProjectionList p = Projections.projectionList();
		criteria.setProjection(p);

		p.add(Projections.property("LOG."
				+ ScoLogGeracaoScMatEstocavel.Fields.SEQ_PROCESSO.toString()),
				ProcessoGeracaoAutomaticaVO.Fields.SEQ_PROCESSO.toString());

		p.add(Projections.sqlProjection("TO_CHAR(MIN({alias}."
				+ ScoLogGeracaoScMatEstocavel.Fields.DT_GERACAO.name()
				+ "), 'DD/MM/YYYY HH24:MI:SS') as DTGERACAOFORMATADA",
				new String[] { ProcessoGeracaoAutomaticaVO.Fields.DT_GERACAO_FORMATADA
						.toString() }, new Type[] { StringType.INSTANCE }));

		p.add(Projections.sqlProjection("MIN({alias}."
				+ ScoLogGeracaoScMatEstocavel.Fields.DT_GERACAO.name()
				+ ") as DTGERACAO",
				new String[] { ProcessoGeracaoAutomaticaVO.Fields.DT_GERACAO
						.toString() }, new Type[] { DateType.INSTANCE }));

		p.add(Projections.groupProperty("LOG."
				+ ScoLogGeracaoScMatEstocavel.Fields.SEQ_PROCESSO.toString()));
		
		
		criteria.setResultTransformer(Transformers
				.aliasToBean(ProcessoGeracaoAutomaticaVO.class));
		
		// se ordenar diretamente pelo 2 quando vira o mes a ordem se perde, pois a ordenacao é string
		criteria.addOrder(PesquisaProcessoGeracaoOrder.sqlFormula("3 desc"));
		criteria.addOrder(PesquisaProcessoGeracaoOrder.sqlFormula("1 desc"));
		return criteria;
	}
	
	private static final class PesquisaProcessoGeracaoOrder extends Order {
		private static final long serialVersionUID = -7635714369338427056L;
		private String sqlFormula;

		protected PesquisaProcessoGeracaoOrder(String propertyName,
				boolean ascending) {
			super(propertyName, ascending);
			sqlFormula = propertyName;
		}

		@Override
		public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
				throws HibernateException {
			return sqlFormula;
		}

		static Order sqlFormula(String sqlFormula) {
			return new PesquisaProcessoGeracaoOrder(sqlFormula, true);
		}
	}
}