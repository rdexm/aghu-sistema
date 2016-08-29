package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @modulo estoque
 *
 */

public class SceAlmoxarifadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceAlmoxarifado> {

	private static final long serialVersionUID = 7963765208239476434L;
	
	public SceAlmoxarifado obterAlmoxarifadoPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifado.class);
		criteria.createAlias(SceAlmoxarifado.Fields.CCT_CODIGO.toString(), "CCT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceAlmoxarifado.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(SceAlmoxarifado.Fields.SEQ.toString(), seq));
		return (SceAlmoxarifado) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<SceAlmoxarifado> obterAlmoxarifadoPorSeqDescricao(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifado.class);
		
		criteria.createAlias(SceAlmoxarifado.Fields.CCT_CODIGO.toString(), "centroCusto", JoinType.LEFT_OUTER_JOIN);
		
		String strPesquisa = param;

		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq(SceAlmoxarifado.Fields.SEQ.toString(), Short.parseShort(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(SceAlmoxarifado.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa), MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.eq(SceAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		//criteria.addOrder(Order.asc(SceAlmoxarifado.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(SceAlmoxarifado.Fields.SEQ.toString()));
		
		return this.executeCriteria(criteria);
	}
	
	public SceAlmoxarifado obterSceAlmoxarifadosAtivoPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceAlmoxarifado.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(SceAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return (SceAlmoxarifado) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtém almoxarifados ativos por código ou descrição
	 * @param param Código ou descrição
	 * @return Lista de almoxarifados
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorCodigoDescricao(Object param) {
		return this.executeCriteria(montarCriteriaAlmoxarifadosPorCodigoDescricao(param, true, SceAlmoxarifado.Fields.SEQ.toString()));
	}

	/**
	 * Obtém almoxarifados ativos ou não por código ou descrição
	 * @param param Código ou descrição
	 * @return Lista de almoxarifados
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosPorCodigoDescricao(Object param) {
		return this.executeCriteria(montarCriteriaAlmoxarifadosPorCodigoDescricao(param, false, SceAlmoxarifado.Fields.SEQ.toString()));
	}
	
	/**
	 * Verifica a existencia de um almoxarifado central que tenha uma seq diferente da informada no parametro.
	 * @param seq
	 * @return
	 */
	public Boolean existeAlmoxarifadoCentralDiferenteSeq(Short seq) {
		DetachedCriteria dc = DetachedCriteria.forClass(SceAlmoxarifado.class);
		if (seq != null) {
			dc.add(Restrictions.ne(SceAlmoxarifado.Fields.SEQ.toString(), seq));
		}
		dc.add(Restrictions.eq(SceAlmoxarifado.Fields.IND_CENTRAL.toString(), Boolean.TRUE));
		return this.executeCriteriaCount(dc) > 0;
	}
	

	/**
	 * Monta a critéria para vusca de almoxarifados pro código ou descrição;
	 * @param parametro
	 * @param apenasAtivos
	 * @return DetachedCriteria
	 */
	private DetachedCriteria montarCriteriaAlmoxarifadosPorCodigoDescricao(
			Object parametro, boolean apenasAtivos, String odernacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				SceAlmoxarifado.class, "almoxarifado");
		String strPesquisa = "";
		if (parametro != null) {
			strPesquisa = (String) parametro;
		}
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				criteria.add(Restrictions.eq("almoxarifado."
						+ SceAlmoxarifado.Fields.SEQ.toString(),
						Short.valueOf(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("almoxarifado."
						+ SceAlmoxarifado.Fields.DESCRICAO.toString(),
						strPesquisa, MatchMode.ANYWHERE));
			}
		}
		if(apenasAtivos)
		{
			criteria.add(Restrictions.eq("almoxarifado."
					+ SceAlmoxarifado.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
		}

		criteria.addOrder(Order.asc("almoxarifado."
					+ odernacao));
		
		return criteria;
	}
	
	public Boolean existeAlmoxarifadoCadastrado(Short seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifado.class);
		
		if (seq != null) {
			criteria.add(Restrictions.eq(SceAlmoxarifado.Fields.SEQ.toString(), seq));
		}
		
		return this.executeCriteriaCount(criteria) > 0;
	}
	
	/**
	 * Pesquisa os almoxarifados ativos, por código e descrição, ordenados pela descrição
	 * 
	 * @param parametro
	 * @return
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorCodigoDescricaoOrdenadosPelaDescricao(Object param) {
		return this.executeCriteria(montarCriteriaAlmoxarifadosPorCodigoDescricao(param, true, SceAlmoxarifado.Fields.SEQ.toString()));
	}
	
	/**
	 * 
	 * @param seq
	 * @return
	 */
	public SceAlmoxarifado obterAlmoxarifadoAtivoPorId(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifado.class);
		criteria.add(Restrictions.eq(SceAlmoxarifado.Fields.SEQ.toString(),seq));
		criteria.add(Restrictions.eq(SceAlmoxarifado.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		return (SceAlmoxarifado) this.executeCriteriaUniqueResult(criteria);
	}


	/**
	 * 
	 * @param parametro
	 * @param codigoCentroCustoAtuacao
	 * @param indCentral
	 * @param codigosAlmoxarifados
	 * @return
	 */	
	public List<SceAlmoxarifado> pesquisarAlmoxarifadoPorCentroCustoDisponivelUsuarioCodigoDescricao(String parametro, List<Integer> listaCodigoCentroCusto, Boolean indCentral, List<Short> codigosAlmoxarifados) {
		DetachedCriteria dc = DetachedCriteria.forClass(SceAlmoxarifado.class, "ALM");	

		dc.createAlias("ALM.".concat(SceAlmoxarifado.Fields.CCT_CODIGO.toString()), "FFC");
		
		Criterion restricao2 = Restrictions.eq("ALM.".concat(SceAlmoxarifado.Fields.IND_SITUACAO.toString()), DominioSituacao.A);
		Criterion restricao3 = null;
		Criterion restricao4 = null;
		Criterion restricao5 = null;
		if (listaCodigoCentroCusto != null && listaCodigoCentroCusto.size() > 0) {
			restricao5 = Restrictions.in("FFC.".concat(FccCentroCustos.Fields.CODIGO.toString()), listaCodigoCentroCusto);
			restricao4 = Restrictions.and(restricao5, restricao2);
		}
		Criterion restricao6 = Restrictions.eq("ALM.".concat(SceAlmoxarifado.Fields.IND_CENTRAL.toString()), Boolean.TRUE);
		Criterion restricao7 = null;
		
		String strPesquisa = "";
		if (parametro != null) {
			strPesquisa = parametro;
		}
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				dc.add(Restrictions.eq("ALM.".concat(SceAlmoxarifado.Fields.SEQ.toString()), Short.valueOf(strPesquisa)));
			} else {
				dc.add(Restrictions.ilike("ALM.".concat(SceAlmoxarifado.Fields.DESCRICAO.toString()),	strPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		// incluir almoxarifado central
		if(restricao4 != null){
			if(indCentral) {
				restricao7 = Restrictions.or(restricao4, restricao6);	
			} else {
				restricao7 = restricao4;
			}
			
			// almoxarifados nao distribuidores
			if(codigosAlmoxarifados != null && codigosAlmoxarifados.size() > 0) {
				restricao3 = Restrictions.and(Restrictions.not(Restrictions.in("ALM.".concat(SceAlmoxarifado.Fields.SEQ.toString()), codigosAlmoxarifados)), restricao2);
				dc.add(Restrictions.or(restricao7, restricao3));
			} else {
				dc.add(restricao7);
			}
		}
		
		dc.addOrder(Order.asc("ALM."  + SceAlmoxarifado.Fields.SEQ.toString()));
		
		return executeCriteria(dc, 0, 100, null, false);
	}
		
	public List<SceAlmoxarifado> listarAlmoxarifados(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Short codigo,
			String descricao, Integer codigoCentroCustos,
			Integer diasEstoqueMinimo, Boolean central,
			Boolean calculaMediaPonderada, Boolean bloqueiaEntTransf,
			DominioSituacao situacao) {
		
		DetachedCriteria dc = montarCriteriaListarAlmoxarifados(codigo,
				descricao, codigoCentroCustos, diasEstoqueMinimo, central,
				calculaMediaPonderada, bloqueiaEntTransf, situacao);
		
		
		

		dc.addOrder(Order.asc(SceAlmoxarifado.Fields.SEQ.toString()));

		return executeCriteria(dc, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarAlmoxarifadosCount(Short codigo, String descricao,
			Integer codigoCentroCustos, Integer diasEstoqueMinimo,
			Boolean central, Boolean calculaMediaPonderada,
			Boolean bloqueiaEntTransf, DominioSituacao situacao) {
			
		DetachedCriteria dc = montarCriteriaListarAlmoxarifados(codigo,
				descricao, codigoCentroCustos, diasEstoqueMinimo, central,
				calculaMediaPonderada, bloqueiaEntTransf, situacao);

		return executeCriteriaCount(dc);
	}
	
	private DetachedCriteria montarCriteriaListarAlmoxarifados(Short codigo,
			String descricao, Integer codigoCentroCustos,
			Integer diasEstoqueMinimo, Boolean central,
			Boolean calculaMediaPonderada, Boolean bloqueiaEntTransf,
			DominioSituacao situacao) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz());
		
		dc.createAlias(SceAlmoxarifado.Fields.CCT_CODIGO.toString(), "cc", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias(SceAlmoxarifado.Fields.SERVIDOR.toString(), "ser", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("ser.".concat(RapServidores.Fields.PESSOA_FISICA.toString()), "pes", JoinType.LEFT_OUTER_JOIN);
		
		if (codigo != null) {
			dc.add(Restrictions.idEq(codigo));	
		}
		if (StringUtils.isNotBlank(descricao)) {
			dc.add(Restrictions.ilike(SceAlmoxarifado.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));			
		}
		if (codigoCentroCustos != null) {
			
			dc.add(Restrictions.eq("cc.".concat(FccCentroCustos.Fields.CODIGO.toString()), codigoCentroCustos));			
		}
		if (diasEstoqueMinimo != null) {
			dc.add(Restrictions.eq(SceAlmoxarifado.Fields.DIAS_ESTOQUE_MINIMO.toString(), diasEstoqueMinimo));	
		}
		if (central != null) {
			dc.add(Restrictions.eq(SceAlmoxarifado.Fields.IND_CENTRAL.toString(), central));	
		}
		if (calculaMediaPonderada != null) {
			dc.add(Restrictions.eq(SceAlmoxarifado.Fields.IND_CALCULA_MEDIA_PONDERADA.toString(), calculaMediaPonderada));	
		}
		if (bloqueiaEntTransf != null) {
			dc.add(Restrictions.eq(SceAlmoxarifado.Fields.IND_BLOQ_ENTR_TRANSF.toString(), bloqueiaEntTransf));	
		}
		if (situacao != null) {
			dc.add(Restrictions.eq(SceAlmoxarifado.Fields.IND_SITUACAO.toString(), situacao));	
		}
		
		return dc;
	}
	
	public Boolean isAlmoxarifadoPossuiItemTrBloqueado(Short seqAlmoxarifado) {
		DetachedCriteria dc = DetachedCriteria.forClass(SceTransferencia.class, "trf");
		dc.createAlias("trf.".concat(SceTransferencia.Fields.ITEM_TRANSFERENCIA.toString()), "itr", Criteria.INNER_JOIN);
		dc.createAlias("trf.".concat(SceTransferencia.Fields.SCE_ALMOX_RECEB.toString()), "almrec", Criteria.INNER_JOIN);
		dc.add(Restrictions.eq("almrec.".concat(SceAlmoxarifado.Fields.SEQ.toString()), seqAlmoxarifado));
		dc.add(Restrictions.eq("trf.".concat(SceTransferencia.Fields.EFETIVADA.toString()), Boolean.TRUE));
		dc.add(Restrictions.eq("itr.".concat(SceItemTransferencia.Fields.MAT_BLOQUEADO.toString()), Boolean.TRUE));
		return executeCriteriaCount(dc) > 0;
	}
	
	
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorSeqOuDescricao(Object param) {
		return this.executeCriteria(montarCriteriaAlmoxarifadosPorCodigoDescricao(param, true, SceAlmoxarifado.Fields.SEQ.toString()));
	}
	
	
	/**
	 * Retorna uma lista de almoxarifados<br>
	 * conforme pesquisa realizada na suggestion
	 * 
	 * @param param
	 * @return
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadoPorSeqDescricao(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifado.class);
		
		DetachedCriteria subQuerie = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class);
		subQuerie.add(Restrictions.eq(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()));
		subQuerie.setProjection(pList);
		criteria.add(Subqueries.exists(subQuerie));
		
		if (StringUtils.isNotBlank(param)) {
			if (CoreUtil.isNumeroShort(param)) {
				criteria.add(Restrictions.eq(SceAlmoxarifado.Fields.SEQ.toString(), Short.parseShort(param)));
			} else {
				criteria.add(Restrictions.ilike(SceAlmoxarifado.Fields.DESCRICAO.toString(), StringUtils.trim(param), MatchMode.ANYWHERE));
			}
		}

		criteria.add(Restrictions.eq(SceAlmoxarifado.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return this.executeCriteria(criteria);
	}
	
}
