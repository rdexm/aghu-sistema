package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCustoCcts;
import br.gov.mec.aghu.model.AnuGrupoQuadroDieta;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoPesos;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProducaoObjetoCusto;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoPesoClienteVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoProducaoExamesVO;

public class SigObjetoCustoVersoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigObjetoCustoVersoes> {

	private static final long serialVersionUID = -202329073538252593L;

	public List<SigObjetoCustoVersoes> pesquisarObjetoCustoVersoesPrincipal(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigCentroProducao sigCentroProducao, FccCentroCustos fccCentroCustos, DominioSituacaoVersoesCustos situacao, String nome,
			DominioTipoObjetoCusto tipoObjetoCusto, Boolean possuiComposicao) {

		DetachedCriteria criteria = this.criarCriteria(sigCentroProducao, fccCentroCustos, situacao, nome, tipoObjetoCusto, possuiComposicao);
		
		criteria.addOrder(Order.asc("oc." + SigObjetoCustos.Fields.NOME.toString()));
		criteria.addOrder(Order.desc("ocv." + SigObjetoCustoVersoes.Fields.DATA_INICIO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarCount(SigCentroProducao sigCentroProducao, FccCentroCustos fccCentroCustos, DominioSituacaoVersoesCustos situacao, String nome,
			DominioTipoObjetoCusto tipoObjetoCusto, Boolean possuiComposicao) {
		return this.executeCriteriaCountDistinct(this.criarCriteria(sigCentroProducao, fccCentroCustos, situacao, nome, tipoObjetoCusto, possuiComposicao),"ocv." + SigObjetoCustoVersoes.Fields.SEQ.toString(),true);
	}

	private DetachedCriteria criarCriteria(SigCentroProducao sigCentroProducao, FccCentroCustos fccCentroCustos, DominioSituacaoVersoesCustos situacao,
			String nome, DominioTipoObjetoCusto tipoObjetoCusto, Boolean possuiComposicao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "oc", JoinType.INNER_JOIN);
		
		if(sigCentroProducao != null || fccCentroCustos != null){
			
			DetachedCriteria criteriaExistis = DetachedCriteria.forClass(SigObjetoCustoCcts.class, "occc");
			criteriaExistis.createCriteria("occc." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), "cct", JoinType.LEFT_OUTER_JOIN);
			criteriaExistis.createAlias("cct." + FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), "cto", JoinType.LEFT_OUTER_JOIN);
			
			criteriaExistis.setProjection(Projections.property("occc." + SigObjetoCustoCcts.Fields.SEQ.toString()));
			criteriaExistis.add(Restrictions.eqProperty("occc." + SigObjetoCustoCcts.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString(), "ocv." + SigObjetoCustoVersoes.Fields.SEQ.toString()));
			
			if (sigCentroProducao != null) {
				criteriaExistis.add(Restrictions.eq("cct." + FccCentroCustos.Fields.CENTRO_PRODUCAO.toString(), sigCentroProducao));
			}

			if (fccCentroCustos != null) {
				criteriaExistis.add(Restrictions.eq("occc." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), fccCentroCustos));
			}
			
			criteria.add(Subqueries.exists(criteriaExistis));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), situacao));
		}

		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike("oc." + SigObjetoCustos.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}

		if (tipoObjetoCusto != null) {
			criteria.add(Restrictions.eq("oc." + SigObjetoCustos.Fields.IND_TIPO.toString(), tipoObjetoCusto));
		}

		if (possuiComposicao != null && possuiComposicao) { 
			criteria.add(Restrictions.isNotEmpty(SigObjetoCustoVersoes.Fields.COMPOSICOES.toString()));
		} else if (possuiComposicao != null && !possuiComposicao) {
			criteria.add(Restrictions.isEmpty(SigObjetoCustoVersoes.Fields.COMPOSICOES.toString()));
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria;
	}	
	
	public List<SigObjetoCustoVersoes> buscaObjetoCustoPrincipalAtivoPeloCentroCusto(FccCentroCustos fccCentroCustos, Object paramPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");

		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.INNER_JOIN);

		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.DIRECIONADOR_RATEIO.toString(), "odr", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("odr." + SigObjetoCustoDirRateios.Fields.DIRECIONADORES.toString(), "dirOdr", JoinType.LEFT_OUTER_JOIN);

		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.CLIENTES.toString(), "occ", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("occ." + SigObjetoCustoClientes.Fields.DIRECIONADORES.toString(), "dirOcc", JoinType.LEFT_OUTER_JOIN);

		//centro de custo
		if (fccCentroCustos != null && fccCentroCustos.getCodigo() != null) {
			criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "oct", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("oct." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), fccCentroCustos));
			criteria.add(Restrictions.eq("oct." + SigObjetoCustoCcts.Fields.IND_TIPO.toString(), DominioTipoObjetoCustoCcts.P));
		}

		//Objeto de custo ativo
		criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.A));

		//direcionador de rateio ativo, rateio para clientes e produção manual
		criteria.add(Restrictions.eq("odr." + SigObjetoCustoDirRateios.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("dirOdr." + SigDirecionadores.Fields.INDICADOR_TIPO.toString(), DominioTipoDirecionadorCustos.RT));
		criteria.add(Restrictions.eq("dirOdr." + SigDirecionadores.Fields.INDICADOR_TIPO_CALCULO_OBJETO.toString(), DominioTipoCalculoObjeto.PM));

		//clientes ativos com direcionadores rateio para clientes e produção manual
		criteria.add(Restrictions.eq("occ." + SigObjetoCustoClientes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("dirOcc." + SigDirecionadores.Fields.INDICADOR_TIPO.toString(), DominioTipoDirecionadorCustos.RT));
		criteria.add(Restrictions.eq("dirOcc." + SigDirecionadores.Fields.INDICADOR_TIPO_CALCULO_OBJETO.toString(), DominioTipoCalculoObjeto.PM));

		String nome = paramPesquisa.toString();
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike("obj." + SigObjetoCustos.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc("obj." + SigObjetoCustos.Fields.NOME.toString()));
		
		//#28305 - Clausula de distinct, pois retorna varias vezes o mesmo objeto de custo no suggestionbox
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return executeCriteria(criteria);

	}

	/**
	 * Pesquisa lista de versões de objeto de custo através do objeto custo.
	 * 
	 * @param SigObjetoCustos
	 */
	public List<SigObjetoCustoVersoes> pesquisarObjetoCustoVersoes(SigObjetoCustos objetoCusto, DominioSituacaoVersoesCustos situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class);

		if (objetoCusto != null) {
			criteria.add(Restrictions.eq(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), objetoCusto));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), situacao));
		}

		return executeCriteria(criteria);
	}

	public List<SigObjetoCustoVersoes> pesquisarObjetoCustoIsProdutoServico(FccCentroCustos fccCentroCustos, Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "oc", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "occc", JoinType.INNER_JOIN);
		criteria.createCriteria("occc." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), "cc", JoinType.INNER_JOIN);

		criteria.add(Restrictions.or(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.A),
				Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.P)));

		criteria.add(Restrictions.eq("occc." + SigObjetoCustoCcts.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		if (fccCentroCustos != null && fccCentroCustos.getCodigo() != null) {
			criteria.add(Restrictions.or(Restrictions.eq("occc." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), fccCentroCustos),
					Restrictions.eq("oc." + SigObjetoCustos.Fields.IND_COMPARTILHA.toString(), true)));
		}

		String strPesquisa = (String) param;
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq("oc." + SigObjetoCustos.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike("oc." + SigObjetoCustos.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc("oc." + SigObjetoCustos.Fields.NOME.toString()));

		return executeCriteria(criteria);
	}
	
	
	/**
	 * Busca os objetos de custo relacionados à produção por PHI do mês
	 * 
	 * @author rogeriovieira
	 * @param processamentoCusto processamento custo
	 * @return  lista com os registros retornados do banco
	 */
	public List<ObjetoCustoProducaoExamesVO> buscaObjetosCustoProducaoPHI(SigProcessamentoCusto processamentoCusto) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		
		//Projeção para incluir as propriedades no group by
		criteria.setProjection(Projections
			.projectionList()
			.add(Projections.groupProperty("ocv." + SigObjetoCustoVersoes.Fields.SEQ.toString()), ObjetoCustoProducaoExamesVO.Fields.SEQ.toString())
			.add(Projections.groupProperty("dhp." + SigDetalheProducao.Fields.CENTRO_CUSTO.toString()), ObjetoCustoProducaoExamesVO.Fields.CENTRO_CUSTO.toString())
			.add(Projections.groupProperty("dhp." + SigDetalheProducao.Fields.PAGADOR.toString()), ObjetoCustoProducaoExamesVO.Fields.PAGADOR.toString())
			.add(Projections.sum("dhp." + SigDetalheProducao.Fields.QTDE.toString()), ObjetoCustoProducaoExamesVO.Fields.QTDE.toString()));
		
		criteria.add(Restrictions.eq(SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.A));

		//Relacionamento com a tabela  agh.sig_objeto_custo_phis
		DetachedCriteria criteriaObjetoCustoPhi = criteria.createCriteria(SigObjetoCustoVersoes.Fields.PHI.toString(), "phc");
		criteriaObjetoCustoPhi.add(Restrictions.eq(SigObjetoCustoPhis.Fields.SITUACAO.toString(), DominioSituacao.A));

		//Relacionamente com a tabela FAT_PROCED_HOSP_INTERNOS para chegar na detalhe produção
		DetachedCriteria criteriaFatPhi = criteriaObjetoCustoPhi.createCriteria(SigObjetoCustoPhis.Fields.FAT_PHI.toString(), "fatProd");

		//Relacionamento com a tabela agh.sig_detalhe_producoes
		DetachedCriteria criteriaDetProd = criteriaFatPhi.createCriteria(FatProcedHospInternos.Fields.DETALHE_PRODUCAO.toString(), "dhp");
		criteriaDetProd.add(Restrictions.eq(SigDetalheProducao.Fields.GRUPO.toString(), DominioGrupoDetalheProducao.PHI));
		criteriaDetProd.add(Restrictions.eq(SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));

		DetachedCriteria criteriaExistis = DetachedCriteria.forClass(SigObjetoCustoCcts.class, "cct");
		criteriaExistis.setProjection(Projections.property("cct." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString()));
		criteriaExistis.add(Restrictions.eqProperty("cct." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(),
				"dhp." + SigDetalheProducao.Fields.CENTRO_CUSTO.toString()));
		criteriaDetProd.add(Subqueries.exists(criteriaExistis));
		
		//Comparação que corresponde a clausula: to_date(<data atual>, 'dd/MM/YYYY') between ocv.data_inicio and coalesce(ocv.data_fim,sysdate)
		Date dtAtual = DateUtil.truncaData(new Date());
		criteria.add(Restrictions.le(SigObjetoCustoVersoes.Fields.DATA_INICIO.toString(), dtAtual));
		criteria.add(Restrictions.or(Restrictions.isNull(SigObjetoCustoVersoes.Fields.DATA_FIM.toString()),
				Restrictions.ge(SigObjetoCustoVersoes.Fields.DATA_FIM.toString(), dtAtual)));


		//Ordenação pelos campos principais
		criteria.addOrder(Order.asc("ocv." + SigObjetoCustoVersoes.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc("dhp." + SigDetalheProducao.Fields.CENTRO_CUSTO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(ObjetoCustoProducaoExamesVO.class));

		return executeCriteria(criteria);

	}
	
	/**
	 * Busca os objetos de custo relacionados à produção por paciente do mês
	 * 
	 * @author rogeriovieira
	 * @param processamentoCusto  processamento mensal ligados ao detalhe de produção
	 * @return lista com vo que corresponde ao retorno do banco
	 */
	public List<ObjetoCustoProducaoExamesVO> buscaObjetosCustoProducaoPaciente(SigProcessamentoCusto processamentoCusto) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigDetalheProducao.class, "dhp");		
		
		//Relacionamento entre as tabelas
		criteria.createCriteria("dhp." + SigDetalheProducao.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.A));
		criteria.add(Restrictions.eq("dhp." + SigDetalheProducao.Fields.GRUPO.toString(), DominioGrupoDetalheProducao.PAC));
		
		//Projeção para incluir as propriedades no group by
		criteria.setProjection(Projections
			.projectionList()
			.add(Projections.property("ocv." + SigObjetoCustoVersoes.Fields.SEQ.toString()), ObjetoCustoProducaoExamesVO.Fields.SEQ.toString())
			.add(Projections.property("dhp." + SigDetalheProducao.Fields.CENTRO_CUSTO.toString()),ObjetoCustoProducaoExamesVO.Fields.CENTRO_CUSTO.toString())
			.add(Projections.property("dhp." + SigDetalheProducao.Fields.PAGADOR.toString()),ObjetoCustoProducaoExamesVO.Fields.PAGADOR.toString())
			.add(Projections.property("dhp." + SigDetalheProducao.Fields.QTDE.toString()), ObjetoCustoProducaoExamesVO.Fields.QTDE.toString())
			.add(Projections.property("dhp." + SigDetalheProducao.Fields.SEQ.toString()), ObjetoCustoProducaoExamesVO.Fields.DHP_SEQ.toString())
		);
		
		//Corresponde a restrição to_date(<data atual>, 'dd/MM/YYYY') between ocv.data_inicio and coalesce(ocv.data_fim,<data atual>)
		Date dataAtual = new Date();
		criteria.add(Restrictions.le("ocv." + SigObjetoCustoVersoes.Fields.DATA_INICIO.toString(), dataAtual));
		criteria.add(Restrictions.or(Restrictions.ge("ocv." + SigObjetoCustoVersoes.Fields.DATA_FIM.toString(), dataAtual),
				Restrictions.isNull("ocv." + SigObjetoCustoVersoes.Fields.DATA_FIM.toString())));
		
		criteria.add(Restrictions.eq("dhp." + SigDetalheProducao.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));
				
		criteria.addOrder(Order.asc("ocv." + SigObjetoCustoVersoes.Fields.SEQ));
		criteria.addOrder(Order.asc("dhp." + SigDetalheProducao.Fields.SEQ));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ObjetoCustoProducaoExamesVO.class));
		
		return executeCriteria(criteria);
	}

	public List<SigObjetoCustoVersoes> pesquisarObjetoCustoVersoesAssistencial(FccCentroCustos fccCentroCustos, Integer seqSigObjetoCustoVersao, Object param) {
		DetachedCriteria criarCriteria = this.criarCriteria(null, fccCentroCustos, null, (String) param, null, null);

		criarCriteria.add(Restrictions.ne("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.I));
		criarCriteria.add(Restrictions.eq("occc." + SigObjetoCustoCcts.Fields.IND_TIPO.toString(), DominioTipoObjetoCustoCcts.P));
		criarCriteria.add(Restrictions.ne("ocv." + SigObjetoCustoVersoes.Fields.SEQ.toString(), seqSigObjetoCustoVersao));
		criarCriteria.add(Restrictions.eq("oc." + SigObjetoCustos.Fields.IND_TIPO.toString(), DominioTipoObjetoCusto.AS));

		return executeCriteria(criarCriteria);
	}

	/**
	 * Consula todos os insumos associados em atividades de objetos de custos com produção no mês de competência do processamento, 
	 * que não tinham quantidade específica ou que tinham quantidade específica e sobrou valores para debitar (disponibilidade).
	 *  
	 * @author rmalvezzi
	 * @param seqProcessamento			Seq do processamento atual.
	 * @return							Retorna todos os insumos ordenados por centro de custo e código do material (insumo).
	 */
	public ScrollableResults buscarInsumosAlocadosAtividadePesoPorRateio(Integer seqProcessamento) {
		StringBuilder sql = new StringBuilder(600);

		sql.append("SELECT tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( " AS tvd_seq, ");
		sql.append("ais." ).append( SigAtividadeInsumos.Fields.SEQ.toString() ).append( " AS ave_seq, ");
		sql.append("cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( " AS cmt_seq, ");
		
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " AS cct_codigo, ");
		sql.append("ais." ).append( SigAtividadeInsumos.Fields.MATERIAL_CODIGO.toString() ).append( " AS mat_codigo, ");
		sql.append("cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( " AS dir_seq_atividade, ");
		sql.append(" COALESCE(ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append( ",1) * SUM (COALESCE(dhp." ).append( SigDetalheProducao.Fields.QTDE.toString()).append( ",1)) AS peso_oc ");

		sql.append("FROM " ).append( SigProcessamentoCusto.class.getSimpleName() ).append( " pmu ");

		sql.append("INNER JOIN pmu.").append(SigProcessamentoCusto.Fields.LISTA_CALCULO_OBJETO_CUSTO ).append( " cbj "); 
		sql.append(" LEFT JOIN cbj." ).append( SigCalculoObjetoCusto.Fields.LISTA_PRODUCAO_OBJETO_CUSTO.toString() ).append( " pjc ");
		sql.append(" LEFT JOIN pjc." ).append( SigProducaoObjetoCusto.Fields.DETALHE_PRODUCAO.toString() ).append( " dhp ");		
		
		sql.append("INNER JOIN cbj.").append(SigCalculoObjetoCusto.Fields.LISTA_CALCULO_COMPONENTES_CBJSEQ ).append( " cmt ");
		
		sql.append("INNER JOIN cbj.").append(SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO ).append( " ocv ");

		sql.append("INNER JOIN ocv.").append(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO ).append( " obj ");
		
		sql.append("INNER JOIN cmt.").append(SigCalculoComponente.Fields.OBJETO_CUSTO_COMPOSICAO ).append( " cbt ");
		
		sql.append("INNER JOIN cbt.").append(SigObjetoCustoComposicoes.Fields.ATIVIDADE).append(" tvd ");
		
		sql.append("INNER JOIN tvd.").append(SigAtividades.Fields.LISTA_ATIVIDADE_INSUMO ).append(" ais ");
		
		sql.append("LEFT JOIN obj.").append(SigObjetoCustos.Fields.OBJETO_CUSTO_PESO).append(" ope ");
		
		sql.append("WHERE ");
		sql.append("pmu." ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :seqProcessamento ");
		sql.append("AND (dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " in ( :grupos ) ");
		sql.append("  OR dhp." ).append( SigDetalheProducao.Fields.GRUPO.toString() ).append( " IS NULL) ");
		sql.append("AND ais." ).append( SigAtividadeInsumos.Fields.QTD_EUSO.toString() ).append( " IS NULL ");
		sql.append("AND ais." ).append( SigAtividadeInsumos.Fields.VIDA_UTIL_QTDE.toString() ).append( " IS NULL ");
		sql.append("AND ais." ).append( SigAtividadeInsumos.Fields.VIDA_UTIL_TEMPO.toString() ).append( " IS NULL ");
		
		sql.append("GROUP BY ");
		sql.append("tvd." ).append( SigAtividades.Fields.SEQ.toString() ).append( ", ");
		sql.append("ais." ).append( SigAtividadeInsumos.Fields.SEQ.toString() ).append( ", ");
		sql.append("cmt." ).append( SigCalculoComponente.Fields.SEQ.toString() ).append( ", ");
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ");
		sql.append("ais." ).append( SigAtividadeInsumos.Fields.MATERIAL_CODIGO.toString() ).append( ", ");
		sql.append("cmt." ).append( SigCalculoComponente.Fields.DIRECIONADOR.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString() ).append( ", ");
		sql.append("ope." ).append( SigObjetoCustoPesos.Fields.VALOR.toString() ).append(' ');
		sql.append("ORDER BY ");
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( ", ");
		sql.append("ais." ).append( SigAtividadeInsumos.Fields.MATERIAL_CODIGO.toString());

		Query query = this.createHibernateQuery(sql.toString());
		query.setInteger("seqProcessamento", seqProcessamento);
		query.setParameterList("grupos", new DominioGrupoDetalheProducao[]{ DominioGrupoDetalheProducao.PHI, DominioGrupoDetalheProducao.PAC});
		return query.setFetchSize(ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE).scroll(ScrollMode.FORWARD_ONLY);
	}


	public Integer getNroDaVersaoDoNovoObjetoDeCusto(SigObjetoCustoVersoes objetoCustoVersao) {

		List<SigObjetoCustoVersoes> list = this.listarVersoesObjetoCusto(objetoCustoVersao.getSigObjetoCustos());
		Integer retorno = 0;
		if (list != null && list.size() > 0) {
			for (SigObjetoCustoVersoes sigObjetoCustoVersoes : list) {
				if (retorno < sigObjetoCustoVersoes.getNroVersao()) {
					retorno = sigObjetoCustoVersoes.getNroVersao();
				}
			}
		}
		return ++retorno;
	}

	public List<SigObjetoCustoVersoes> listarVersoesObjetoCusto(SigObjetoCustos objetoCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class);
		criteria.add(Restrictions.eq(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), objetoCusto));
		return this.executeCriteria(criteria);
	}

	public List<SigObjetoCustoVersoes> buscarVersoesObjetoCustoDataInicioConflito(SigObjetoCustoVersoes objetoCustoVersao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class);
		//todas as versões do objeto de custo
		criteria.add(Restrictions.eq(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), objetoCustoVersao.getSigObjetoCustos()));
		//que não seja a versão atividade
		criteria.add(Restrictions.ne(SigObjetoCustoVersoes.Fields.SEQ.toString(), objetoCustoVersao.getSeq()));
		//em que a dataInicio da versão atividade seja maior que a do banco
		criteria.add(Restrictions.le(SigObjetoCustoVersoes.Fields.DATA_INICIO.toString(), objetoCustoVersao.getDataInicio()));
		//e a data de fim do banco seja nula, ou maior que a data inicio da ativada 
		criteria.add(Restrictions.or(Restrictions.ge(SigObjetoCustoVersoes.Fields.DATA_FIM.toString(), objetoCustoVersao.getDataInicio()),
				Restrictions.isNull(SigObjetoCustoVersoes.Fields.DATA_FIM.toString())));
		return this.executeCriteria(criteria);
	}

	public List<ObjetoCustoPesoClienteVO> pesquisarObjetoCustoPesoCliente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FccCentroCustos centroCusto, SigDirecionadores direcionador, String nome, DominioSituacaoVersoesCustos situacao) {
		DetachedCriteria criteria = criarCriteriaPesquisarObjetoCustoPesoCliente(centroCusto, direcionador, nome, situacao);
		criteria.addOrder(Order.desc("obj." + SigObjetoCustos.Fields.NOME.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarObjetoCustoPesoClienteCount(FccCentroCustos centroCusto, SigDirecionadores direcionador, String nome,
			DominioSituacaoVersoesCustos situacao) {
		return (long) this.executeCriteria(criarCriteriaPesquisarObjetoCustoPesoCliente(centroCusto, direcionador, nome, situacao)).size();
	}

	private DetachedCriteria criarCriteriaPesquisarObjetoCustoPesoCliente(FccCentroCustos centroCusto, SigDirecionadores direcionador, String nome, DominioSituacaoVersoesCustos situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.INNER_JOIN);
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.CLIENTES.toString(), "occ", JoinType.INNER_JOIN);
		criteria.createAlias("occ." + SigObjetoCustoClientes.Fields.DIRECIONADORES, "dir", JoinType.INNER_JOIN);

		//filtros tela
		//centro de custo
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "oct", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("oct." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), centroCusto));

		//direcionador
		if (direcionador != null) {
			criteria.add(Restrictions.eq("occ." + SigObjetoCustoClientes.Fields.DIRECIONADORES.toString(), direcionador));
		}

		//nome
		if (nome != null && StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike("obj." + SigObjetoCustos.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}

		//situacao
		if (situacao != null) {
			criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), situacao));
		}
		else{
			criteria.add(Restrictions.in("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO, new Object[] { DominioSituacaoVersoesCustos.A, DominioSituacaoVersoesCustos.E, DominioSituacaoVersoesCustos.P }));
		}
		criteria.add(Restrictions.eq("obj." + SigObjetoCustos.Fields.IND_TIPO, DominioTipoObjetoCusto.AP));
		criteria.add(Restrictions.eq("occ." + SigObjetoCustoClientes.Fields.SITUACAO, DominioSituacao.A));

		criteria.setProjection(Projections
			.projectionList()
			.add(Projections.groupProperty("obj." + SigObjetoCustos.Fields.NOME.toString()), ObjetoCustoPesoClienteVO.Fields.NOME.toString())
			.add(Projections.groupProperty("ocv." + SigObjetoCustoVersoes.Fields.SEQ.toString()),ObjetoCustoPesoClienteVO.Fields.SEQ_OBJETO_CUSTO_VERSAO.toString())
			.add(Projections.groupProperty("ocv." + SigObjetoCustoVersoes.Fields.NRO_VERSAO.toString()),ObjetoCustoPesoClienteVO.Fields.NRO_VERSAO.toString())
			.add(Projections.groupProperty("ocv." + SigObjetoCustoVersoes.Fields.DATA_INICIO.toString()),ObjetoCustoPesoClienteVO.Fields.DATA_INICIO.toString())
			.add(Projections.groupProperty("ocv." + SigObjetoCustoVersoes.Fields.DATA_FIM.toString()), ObjetoCustoPesoClienteVO.Fields.DATA_FIM.toString())
			.add(Projections.groupProperty("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString()), ObjetoCustoPesoClienteVO.Fields.SITUACAO.toString())
			.add(Projections.groupProperty("occ." + SigObjetoCustoClientes.Fields.DIRECIONADORES), ObjetoCustoPesoClienteVO.Fields.DIRECIONADOR.toString())
		);
		criteria.setResultTransformer(Transformers.aliasToBean(ObjetoCustoPesoClienteVO.class));

		return criteria;
	}

	public List<SigObjetoCustoVersoes> buscarObjetoCustoVersoesRelatorio(FccCentroCustos filtoCentroCusto, SigObjetoCustos filtroObjetoCusto,
			DominioSituacaoVersoesCustos filtroSituacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "oc", JoinType.INNER_JOIN);

		if (filtoCentroCusto != null) {
			criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "occc", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq("occc." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), filtoCentroCusto));
		} else {
			criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "occc", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.isNull("occc." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString()));
		}

		if (filtroObjetoCusto != null) {
			criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), filtroObjetoCusto));
		}

		if (filtroSituacao != null) {
			criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), filtroSituacao));
		}

		criteria.addOrder(Order.desc("oc." + SigObjetoCustos.Fields.IND_TIPO.toString()));
		criteria.addOrder(Order.asc("oc." + SigObjetoCustos.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("ocv." + SigObjetoCustoVersoes.Fields.NRO_VERSAO.toString()));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * Busca um objeto de custo ativo atraves do Fat procedimento (PHI).
	 * 
	 * @param procedimento		O procedimento em questão.
	 * @return 					{@link SigObjetoCustoVersoes} oc vinculado ao procedimento.
	 * @author jgugel
	 */
	public List<SigObjetoCustoVersoes> buscarListaObjetoCustoAtivoPeloPHI(FatProcedHospInternos procedimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.PHI.toString(), "oph", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("oph." + SigObjetoCustoPhis.Fields.FAT_PHI.toString(), procedimento));
		criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.A));
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Busca um objeto de custo atraves do Fat procedimento (PHI).
	 * 
	 * @param procedimento		O procedimento em questão.
	 * @return 					{@link SigObjetoCustoVersoes} oc vinculado ao procedimento.
	 * @author jgugel
	 */
	public List<SigObjetoCustoVersoes> buscarListaObjetoCustoPeloPHI(FatProcedHospInternos procedimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.PHI.toString(), "oph", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.INNER_JOIN);
		criteria.add(Restrictions.ne("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.I));
		criteria.add(Restrictions.eq("oph." + SigObjetoCustoPhis.Fields.FAT_PHI.toString(), procedimento));
		return this.executeCriteria(criteria);
	}

	public List<SigObjetoCustoVersoes> buscarListaObjetoCustoAtivoPeloPHIeCC(FatProcedHospInternos procedimento, FccCentroCustos centroCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.PHI.toString(), "oph", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "cct", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("cct." + SigObjetoCustoCcts.Fields.IND_SITUACAO, DominioSituacao.A));
		criteria.add(Restrictions.eq("cct." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO, centroCusto));
		criteria.add(Restrictions.ne("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.I));
		criteria.add(Restrictions.eq("oph." + SigObjetoCustoPhis.Fields.FAT_PHI.toString(), procedimento));
		return this.executeCriteria(criteria);
	}

	public List<SigObjetoCustoVersoes> buscarListaObjetoCustoPeloPHIeCC(FatProcedHospInternos procedimento, FccCentroCustos centroCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.PHI.toString(), "oph", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "cct", JoinType.LEFT_OUTER_JOIN);		
		criteria.add(Restrictions.or(
				Restrictions.and(Restrictions.eq("cct." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO, centroCusto),
						Restrictions.eq("cct." + SigObjetoCustoCcts.Fields.IND_SITUACAO, DominioSituacao.A)),
				Restrictions.isNull("cct." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO)));
		criteria.add(Restrictions.ne("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.I));
		criteria.add(Restrictions.eq("oph." + SigObjetoCustoPhis.Fields.FAT_PHI.toString(), procedimento));
		return this.executeCriteria(criteria);
	}
	
	/**
	* Busca um objeto de custo ativo atraves do codigo da refeição;.
	* 
	* @param codigoRefeicao Código refeição
	* @return versão do objeto de custo
	*/
	public SigObjetoCustoVersoes buscarObjetoCustoVersaoPelaRefeicao(Short codigoRefeicao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.PHI.toString(), "oph", JoinType.INNER_JOIN);
		criteria.createCriteria("oph." + SigObjetoCustoPhis.Fields.FAT_PHI.toString(), "phi", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.A));
		criteria.add(Restrictions.eq("phi." + FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("phi." + FatProcedHospInternos.Fields.GRUPO_QUADRO_DIETA.toString() + '.' + AnuGrupoQuadroDieta.Fields.SEQ, codigoRefeicao));
		return (SigObjetoCustoVersoes) this.executeCriteriaUniqueResult(criteria);
	}

	public SigObjetoCustoVersoes buscarObjetoCustoVersoesAtualizado(SigObjetoCustoVersoes sigObjCustoVersoes) {
		this.entityManagerClear();
		return obterPorChavePrimaria(sigObjCustoVersoes.getSeq(), true,
				SigObjetoCustoVersoes.Fields.CLIENTES,
				SigObjetoCustoVersoes.Fields.CLIENTES_CENTRO_CUSTO,
				SigObjetoCustoVersoes.Fields.CLIENTES_DIRECIONADORES);
	}
	
	public SigObjetoCustoVersoes obterSigObjetoCustoVersaoAtivaPorParametro(BigDecimal valorNumericoParametro){
		if(valorNumericoParametro != null){
			DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class);
			criteria.add(Restrictions.eq(SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_SEQ.toString(), valorNumericoParametro.intValue()));
			criteria.add(Restrictions.eq(SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			return (SigObjetoCustoVersoes) this.executeCriteriaUniqueResult(criteria);
		}
		return null;
	}	
	
	public List<SigObjetoCustoVersoes> buscarObjetoCustoVersoesCentroCusto(Integer seqCct, String paramPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoVersoes.class, "ocv");
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj");
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS.toString(), "oct");
		
		criteria.createAlias("oct." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), "cct");
		
		if(seqCct != null) {
			criteria.add(Restrictions.eq("cct." + FccCentroCustos.Fields.CODIGO.toString(), seqCct));
		}
		
		criteria.add(Restrictions.eq("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacaoVersoesCustos.A));
		
		if(CoreUtil.isNumeroInteger(paramPesquisa)) {
			criteria.add(Restrictions.eq("obj." + SigObjetoCustos.Fields.SEQ.toString(), Integer.parseInt(paramPesquisa)));
		} else 
			if(paramPesquisa != null && StringUtils.isNotBlank(paramPesquisa)) {
				criteria.add(Restrictions.ilike("obj." + SigObjetoCustos.Fields.NOME.toString(), paramPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc("obj." + SigObjetoCustos.Fields.NOME.toString()));
		
		return this.executeCriteria(criteria);
	}
}
