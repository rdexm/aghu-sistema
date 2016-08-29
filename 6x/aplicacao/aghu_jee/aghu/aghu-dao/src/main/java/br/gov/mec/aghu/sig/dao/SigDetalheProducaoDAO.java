package br.gov.mec.aghu.sig.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.DetalheProducaoObjetoCustoVO;
import br.gov.mec.aghu.view.VSigProducaoExames;

public class SigDetalheProducaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigDetalheProducao> {

	private static final long serialVersionUID = 7070964233007405098L;

	public void removerPorProcessamento(Integer idProcessamentoCusto) {

		StringBuilder sql = new StringBuilder(50);

		sql.append(" DELETE " ).append( SigDetalheProducao.class.getSimpleName().toString() ).append( " ca ");
		sql.append(" WHERE ca." ).append( SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pSeq");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();
	}

	/**
	 * Pesquisar detalhe de produção
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto processamento
	 * @param fccCentroCustos centro de custo
	 * @param objCustoVersao versão do objeto de custo
	 * @return detalhes de produção de acordo com os parâmetro informados
	 */
	public List<SigDetalheProducao> pesquisarDetalheProducao(SigProcessamentoCusto sigProcessamentoCusto, FccCentroCustos fccCentroCustos,
			SigObjetoCustoVersoes objCustoVersao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigDetalheProducao.class, "dhp");

		criteria.add(Restrictions.eq("dhp." + SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO.toString(), sigProcessamentoCusto));
		criteria.add(Restrictions.eq("dhp." + SigDetalheProducao.Fields.CENTRO_CUSTO.toString(), fccCentroCustos));
		criteria.add(Restrictions.eq("dhp." + SigDetalheProducao.Fields.GRUPO.toString(), DominioGrupoDetalheProducao.PHI));
		
		criteria.add(Subqueries.propertyIn("dhp." + SigDetalheProducao.Fields.FAT_PHI.toString(), this.subCriteriaPhiAtivoObjetoCustoVersao(objCustoVersao)));

		return executeCriteria(criteria);
	}
	
	private DetachedCriteria subCriteriaPhiAtivoObjetoCustoVersao(SigObjetoCustoVersoes objCustoVersao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoPhis.class, "phi");

		criteria.add(Restrictions.eq("phi." + SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO.toString(), objCustoVersao));
		criteria.add(Restrictions.eq("phi." + SigObjetoCustoPhis.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.setProjection(Projections.projectionList().add(Projections.property("phi." + SigObjetoCustoPhis.Fields.FAT_PHI.toString())));

		return criteria;
	}

	/**
	 * SQL em uma view, criada com base no SQL da versão anterior da especificação.
	 * A origem do exame irá determinar se ele é finalístico, foi realizado para um paciente externo, ou intermediário, 
	 * quando foi realizado para um paciente interno.
	 * 
	 * @author rmalvezzi
	 * @param processamentoCusto		Processamento Atual.
	 * @return							Retorna a produção de exames de cada centro de custo no mês de competência do processamento, 
	 * 									agrupados por PHI e Origem.
	 */	
	public ScrollableResults buscarProducaoExamesPorMesCompetencia(SigProcessamentoCusto processamentoCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VSigProducaoExames.class, "vpe");
		criteria.add(Restrictions.between("vpe." + VSigProducaoExames.Fields.DATA_LIBERADO.toString(), processamentoCusto.getDataInicio(),
				processamentoCusto.getDataFim()));

		ProjectionList projection = Projections.projectionList().add(Projections.groupProperty("vpe." + VSigProducaoExames.Fields.CCT_CODIGO.toString()))
				.add(Projections.groupProperty("vpe." + VSigProducaoExames.Fields.PHI_SEQ.toString()))
				.add(Projections.groupProperty("vpe." + VSigProducaoExames.Fields.ORIGEM.toString()))
				.add(Projections.sum("vpe." + VSigProducaoExames.Fields.NRO_DIAS_PRODUCAO.toString()))
				.add(Projections.sum("vpe." + VSigProducaoExames.Fields.QTDE_EXAMES.toString()));

		criteria.setProjection(projection);

		criteria.addOrder(Order.asc("vpe." + VSigProducaoExames.Fields.CCT_CODIGO.toString()));
		criteria.addOrder(Order.asc("vpe." + VSigProducaoExames.Fields.PHI_SEQ.toString()));
		criteria.addOrder(Order.asc("vpe." + VSigProducaoExames.Fields.ORIGEM.toString()));
		
		return createScrollableResults(criteria, ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE, ScrollMode.FORWARD_ONLY);
	}

	public Long pesquisarProducaoCount(FccCentroCustos centroCusto,	SigProcessamentoCusto competencia, SigObjetoCustos objetoCusto, SigDirecionadores direcionador) {
		DetachedCriteria criteria = this.criarCriteriaPesquisaProducao(centroCusto, competencia, objetoCusto, direcionador, false);
		return Long.valueOf(this.executeCriteria(criteria).size());	
		//Não é possível fazer o count da consulta (nem o count distinct), pois a mesma utiliza projection que acaba sendo substituida no método executeCriteriaCount
		//return this.executeCriteriaCount(criteria);	
	}
	
	
	public List<DetalheProducaoObjetoCustoVO> pesquisarProducao(Integer firstResult, Integer maxResult, String orderProperty, Boolean asc, FccCentroCustos centroCusto,
			SigProcessamentoCusto competencia, SigObjetoCustos objetoCusto, SigDirecionadores direcionador) {
		DetachedCriteria criteria = this.criarCriteriaPesquisaProducao(centroCusto, competencia, objetoCusto, direcionador, true);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, true);
	}

	protected DetachedCriteria criarCriteriaPesquisaProducao(FccCentroCustos centroCusto, SigProcessamentoCusto competencia, SigObjetoCustos objetoCusto,
			SigDirecionadores direcionador, boolean utilizarOrder) {

		//agh.sig_detalhe_producoes dpr,
		DetachedCriteria criteria = DetachedCriteria.forClass(SigDetalheProducao.class, "dpr");

		criteria.setProjection(Projections.distinct( Projections.projectionList()
			//vo.setSeqDetalheProducao(detalheProducao.getSeq());
			.add(Projections.min("dpr." + SigDetalheProducao.Fields.SEQ.toString()), DetalheProducaoObjetoCustoVO.Fields.SEQ_DETALHE_PRODUCAO.toString())
			//vo.setNomeDirecionador(detalheProducao.getSigDirecionadores().getNome());
			.add(Projections.groupProperty("dir." + SigDirecionadores.Fields.NOME.toString()), DetalheProducaoObjetoCustoVO.Fields.NOME_DIRECIONADOR.toString())
			//vo.setNomeObjetoCusto(detalheProducao.getSigObjetoCustoVersoes().getSigObjetoCustos().getNome());
			.add(Projections.groupProperty("obj." + SigObjetoCustos.Fields.NOME.toString()), DetalheProducaoObjetoCustoVO.Fields.NOME_OBJETO_CUSTO.toString())
			//vo.setNumeroVersao(detalheProducao.getSigObjetoCustoVersoes().getNroVersao());
			.add(Projections.groupProperty("ocv." + SigObjetoCustoVersoes.Fields.NRO_VERSAO.toString()), DetalheProducaoObjetoCustoVO.Fields.NUMERO_VERSAO.toString())
			//vo.setCompetencia(detalheProducao.getSigProcessamentoCustos().getCompetenciaMesAno());
			.add(Projections.groupProperty("pmu." + SigProcessamentoCusto.Fields.COMPETENCIA.toString()), DetalheProducaoObjetoCustoVO.Fields.COMPETENCIA.toString())
		));
		//agh.sig_processamento_custos pmu
		criteria.createCriteria("dpr." + SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu", Criteria.INNER_JOIN);

		//agh.sig_direcionadores dir,
		criteria.createCriteria("dpr." + SigDetalheProducao.Fields.DIRECIONADOR.toString(), "dir", Criteria.INNER_JOIN);

		//agh.sig_objeto_custo_versoes ocv,
		criteria.createCriteria("dpr." + SigDetalheProducao.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", Criteria.INNER_JOIN);

		//agh.sig_objeto_custos obj,
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", Criteria.INNER_JOIN);

		//agh.sig_objeto_custo_clientes occ,
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.CLIENTES.toString(), "occ", Criteria.INNER_JOIN);
		
		//agh.sig_objeto_custo_dir_rateios ocd
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.DIRECIONADOR_RATEIO.toString(), "ocd", Criteria.INNER_JOIN);

		//agh.SIG_OBJETO_CUSTO_CCTS cct,
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "cct", Criteria.INNER_JOIN);

		//and ocv.ind_situacao in ('A', 'E', 'P')
		criteria.add(Restrictions.ne("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.I));

		//and obj.ind_tipo = 'AP'
		criteria.add(Restrictions.eq("obj." + SigObjetoCustos.Fields.IND_TIPO.toString(), DominioTipoObjetoCusto.AP));

		//and occ.ind_situacao = 'A'
		criteria.add(Restrictions.eq("occ." + SigObjetoCustoClientes.Fields.SITUACAO.toString(), DominioSituacao.A));

		//and dir.ind_tipo_calculo = 'PM'
		criteria.add(Restrictions.eq("dir." + SigDirecionadores.Fields.INDICADOR_TIPO_CALCULO_OBJETO.toString(), DominioTipoCalculoObjeto.PM));
		
		//and occ.ind_situacao = 'A'
		criteria.add(Restrictions.eq("occ."+SigObjetoCustoClientes.Fields.SITUACAO, DominioSituacao.A));
		
		//and ocd.ind_situacao <> 'I'
		criteria.add(Restrictions.ne("ocd." + SigObjetoCustoDirRateios.Fields.SITUACAO.toString(), DominioSituacao.I));
		
		
		criteria.add(Restrictions.eqProperty("ocd." + SigObjetoCustoDirRateios.Fields.DIRECIONADORES+"."+SigDirecionadores.Fields.SEQ, "dir."+SigDirecionadores.Fields.SEQ));

		//Parâmetros informados na tela
		if (centroCusto != null) {
			//criteria.add(Restrictions.eq("dpr."+SigDetalheProducao.Fields.CENTRO_CUSTO.toString(), centroCusto));
			//criteria.add(Restrictions.eq("occ."+SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString(), centroCusto));
			criteria.add(Restrictions.eq("cct." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), centroCusto));

		}

		if (competencia != null) {
			criteria.add(Restrictions.eq("dpr." + SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO.toString(), competencia));
		}

		if (objetoCusto != null) {
			criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), objetoCusto));
		}

		if (direcionador != null) {
			criteria.add(Restrictions.eq("dpr." + SigDetalheProducao.Fields.DIRECIONADOR.toString(), direcionador));
		}

		if (utilizarOrder) {
			//obj.nome, 
			criteria.addOrder(Order.asc("obj." + SigObjetoCustos.Fields.NOME.toString()));
			//pmu.competencia
			criteria.addOrder(Order.asc("pmu." + SigProcessamentoCusto.Fields.COMPETENCIA.toString()));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(DetalheProducaoObjetoCustoVO.class));

		return criteria;
	}

	public SigDetalheProducao obterPorParametrosCliente(FccCentroCustos centroCusto, SigObjetoCustoVersoes objetoCustoVersoes,
			SigDirecionadores direcionadores, SigProcessamentoCusto competencia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigDetalheProducao.class);
		criteria.setFetchMode(SigDetalheProducao.Fields.DIRECIONADOR.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(SigDetalheProducao.Fields.CENTRO_CUSTO.toString(), centroCusto));
		criteria.add(Restrictions.eq(SigDetalheProducao.Fields.OBJETO_CUSTO_VERSAO.toString(), objetoCustoVersoes));
		criteria.add(Restrictions.eq(SigDetalheProducao.Fields.DIRECIONADOR.toString(), direcionadores));
		criteria.add(Restrictions.eq(SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO.toString(), competencia));

		return (SigDetalheProducao) this.executeCriteriaUniqueResult(criteria);
	}

	
	/**
	 * Retorna todos os detalhes produções vinculados ao objeto de custo de uma competencia o direcionador associado
	 * @param seqVersao 
	 * @param sigDirecionadores
	 * @param competencia 
	 * @return lista de seq detalhe produção com os objetos vinculados.
	 */
	public List<Integer> pesquisarDetalhesProducoesParaExcluir(Integer seqVersao, SigDirecionadores sigDirecionadores, Integer competencia){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigProcessamentoCusto.class, "pmu");
		
		criteria.createCriteria("pmu." + SigProcessamentoCusto.Fields.LISTA_DETALHE_PRODUCAO.toString(), "det", Criteria.INNER_JOIN);

		criteria.createCriteria("det." + SigDetalheProducao.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("pmu.seq", competencia));
		criteria.add(Restrictions.eq("ocv.seq", seqVersao));
		criteria.add(Restrictions.eq("det."+SigDetalheProducao.Fields.DIRECIONADOR.toString(), sigDirecionadores));
		
		criteria.setProjection(Projections.projectionList().add(Projections.property("det.seq")));
		
		return executeCriteria(criteria);
		
	}
	
}