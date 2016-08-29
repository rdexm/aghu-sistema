package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeCentroCustos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoAssociadoAtividadeVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoAtividadeVO;

public class SigObjetoCustoComposicoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigObjetoCustoComposicoes> {

	private static final long serialVersionUID = -250749936583456226L;

	public List<SigObjetoCustoComposicoes> buscarPorAtividade(SigAtividades atividade) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoComposicoes.class, "objCC");

		criteria.add(Restrictions.eq("objCC." + SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString(), atividade));

		return this.executeCriteria(criteria);
	}

	public List<SigObjetoCustoComposicoes> pesquisarComposicoesPorObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoComposicoes.class);
		criteria.createAlias(SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES_COMPOE.toString(), "OCVC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SigObjetoCustoComposicoes.Fields.DIRECIONADORES.toString(), "DIR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString(), "ATIV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATIV."+SigAtividades.Fields.ATIVIDADE_CENTROS_CUSTOS.toString(), "ATIV_CC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATIV_CC."+SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString(), "CC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES.toString(), seqObjetoCustoVersao));
		return executeCriteria(criteria);
	}

	public List<SigObjetoCustoComposicoes> pesquisarComposicoesPorObjetoCustoVersaoAtivo(Integer seqObjetoCustoVersao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoComposicoes.class);
		criteria.add(Restrictions.eq(SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES.toString(), seqObjetoCustoVersao));
		criteria.add(Restrictions.eq(SigObjetoCustoComposicoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public List<SigObjetoCustoComposicoes> pesquisarObjetoCustoComposicao(SigObjetoCustoVersoes objetoCustoVersao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoComposicoes.class);

		criteria.add(Restrictions.eq(SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES.toString(), objetoCustoVersao.getSeq()));

		return this.executeCriteria(criteria);
	}
	
	public List<SigObjetoCustoComposicoes> pesquisarAssociacaoVersoesObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoComposicoes.class);

		criteria.add(Restrictions.eq(SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES_COMPOE_SEQ.toString(), objetoCustoVersao.getSeq()));

		return this.executeCriteria(criteria);
	}

	/**
	 * Lista as composicoes de um objeto de custo versão
	 * @param objetoCustoVersao - OC 
	 * @return List<{@link SigObjetoCustoComposicoes}> -  lista de composicoes
	 * @author jgugel 
	 */
	public List<SigObjetoCustoComposicoes> pesquisarObjetoCustoComposicaoAtivo(SigObjetoCustoVersoes objetoCustoVersao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoComposicoes.class);

		criteria.add(Restrictions.eq(SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES_OBJ.toString(), objetoCustoVersao));

		criteria.add(Restrictions.eq(SigObjetoCustoComposicoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return this.executeCriteria(criteria);
	}
		
	public List<SigObjetoCustoComposicoes> pesquisarObjetoCustoComposicaoAtivoObjetoCustoVersaoAtivo(SigAtividades atividade) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoComposicoes.class);

		criteria.add(Restrictions.eq(SigObjetoCustoComposicoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.createCriteria(SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES_OBJ.toString(), "ocv", JoinType.INNER_JOIN);		
		criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));		
		criteria.add(Restrictions.eq(SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString(), atividade));

		return this.executeCriteria(criteria);
	}

	public List<ObjetoCustoAssociadoAtividadeVO> pesquisarObjetosCustoAssociadosAtividades(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, SigAtividades atividade) {
		DetachedCriteria criteria = this.criarPesquisaObjetosCustoAssociadosAtividadesCriteria(atividade, true);
		criteria.setResultTransformer(Transformers.aliasToBean(ObjetoCustoAssociadoAtividadeVO.class));
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, true);
	}
	
	public Integer pesquisarObjetosCustoAssociadosAtividadesCount(SigAtividades atividade){
		List<Object> lista = this.executeCriteria(this.criarPesquisaObjetosCustoAssociadosAtividadesCriteria(atividade, false));
		return lista.size();
	}

	private DetachedCriteria criarPesquisaObjetosCustoAssociadosAtividadesCriteria(SigAtividades atividade, boolean utilizarOrder) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoComposicoes.class, "occ");
		criteria.createCriteria("occ."+SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES_OBJ.toString(), "ocv", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv."+SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "oc", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv."+SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "occc", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("occc."+SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), "cc", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("oc."+SigObjetoCustos.Fields.NOME.toString())), SigObjetoCustos.Fields.NOME.toString())
				.add(Projections.property("ocv."+SigObjetoCustoVersoes.Fields.NRO_VERSAO.toString()), SigObjetoCustoVersoes.Fields.NRO_VERSAO.toString())
				.add(Projections.property("ocv."+SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString()), SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString())
				.add(Projections.property("cc."+FccCentroCustos.Fields.CODIGO.toString()), "cctCodigo" )
				.add(Projections.property("cc."+FccCentroCustos.Fields.DESCRICAO.toString()), "cctDescricao" )
		);

		criteria.add(Restrictions.eq("occ."+SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString(), atividade));
		
		if(utilizarOrder){
			criteria.addOrder(Order.asc("oc."+SigObjetoCustos.Fields.NOME.toString()));
			criteria.addOrder(Order.asc("ocv."+SigObjetoCustoVersoes.Fields.NRO_VERSAO.toString()));
		}
		
		return criteria;
	}
	
	/**
	* Busca o objeto de cuisto da nutricao parenteral pelo codigo do procedimento
	* 
	* @param codigoProcedimento
	* @return ObjetoCustoAtividadeVO  VO com a seq do objeto de custo versão e atividade
	* @author rhrosa
	*/
	public ObjetoCustoAtividadeVO buscarObjetoDeCustoNutricaoParenteral(short codigoProcedimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ophi." + SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString()), ObjetoCustoAtividadeVO.Fields.OCV_SEQ.toString())
				.add(Projections.property("atv." + SigAtividades.Fields.SEQ.toString()), ObjetoCustoAtividadeVO.Fields.TVD_SEQ.toString())
		);
		criteria.createAlias("ocv."+ SigObjetoCustoVersoes.Fields.PHI, "ophi", JoinType.INNER_JOIN);
		criteria.createAlias("ophi."+SigObjetoCustoPhis.Fields.FAT_PHI, "phi", JoinType.INNER_JOIN);
		criteria.createAlias("ocv."+SigObjetoCustoVersoes.Fields.COMPOSICOES, "occ",  JoinType.INNER_JOIN);
		criteria.createAlias("occ."+SigObjetoCustoComposicoes.Fields.ATIVIDADE, "atv", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("ophi."+ SigObjetoCustoPhis.Fields.SITUACAO, DominioSituacao.A));
		criteria.add(Restrictions.eq("phi."+ FatProcedHospInternosPai.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ, codigoProcedimento));
		criteria.setResultTransformer(Transformers.aliasToBean(ObjetoCustoAtividadeVO.class));
		return (ObjetoCustoAtividadeVO)executeCriteriaUniqueResult(criteria);
	}
	
	public List<SigObjetoCustoComposicoes> obterPorParametros(Integer objSeq, Integer tvdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoComposicoes.class, "OCC");
		criteria.createAlias(SigObjetoCustoComposicoes.Fields.ATIVIDADE.toString(), "ATV");
		criteria.createAlias(SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES_OBJ.toString(), "OCV");
		criteria.add(Restrictions.eq("OCC."+SigObjetoCustoComposicoes.Fields.ATIVIDADE_SEQ.toString(), tvdSeq));
		criteria.add(Restrictions.eq("OCC."+SigObjetoCustoComposicoes.Fields.OBJETO_CUSTO_VERSOES.toString(), objSeq));
		
		return this.executeCriteria(criteria);
	}

}
