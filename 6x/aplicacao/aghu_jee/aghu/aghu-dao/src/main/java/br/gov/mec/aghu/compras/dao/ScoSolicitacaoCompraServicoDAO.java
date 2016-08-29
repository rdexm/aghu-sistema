package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.compras.pac.vo.PreItemPacVO;
import br.gov.mec.aghu.compras.vo.ParcelaAfPendenteEntregaVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoCompraServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoSolicitacaoCompraServicoDAO
		extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSolicitacaoCompraServico> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7113473938830775325L;

	public Long pesquisarSolicitacaoCompraServicoCount(
			ScoSolicitacaoDeCompra numeroSC, ScoSolicitacaoServico numeroSS) {

		final DetachedCriteria criteria = this.obterCriteriaBasica(numeroSC,
				numeroSS);

		return this.executeCriteriaCount(criteria);
	}

	public List<ScoSolicitacaoCompraServico> listarSolicitacaoCompraServico(
			ScoSolicitacaoDeCompra numeroSC, ScoSolicitacaoServico numeroSS) {

		DetachedCriteria criteria = this
				.obterCriteriaBasica(numeroSC, numeroSS);
		criteria.addOrder(Order
				.asc(ScoSolicitacaoCompraServico.Fields.SLS_NUMERO.toString()));

		return executeCriteria(criteria);
	}

	public List<ScoSolicitacaoCompraServico> pesquisarSolicitacaoDeCompraPorServico(
			ScoSolicitacaoServico solicitacaoServico) {
		DetachedCriteria criteria = this.obterCriteriaBasica(null,
				solicitacaoServico);
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaBasica(
			ScoSolicitacaoDeCompra numeroSC, ScoSolicitacaoServico numeroSS) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoSolicitacaoCompraServico.class);

		criteria.createAlias(
				ScoSolicitacaoCompraServico.Fields.SCO_SOLICITACAO_DE_COMPRA
						.toString(), "sol_compra", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("sol_compra."
				+ ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(),
				"material", JoinType.LEFT_OUTER_JOIN);

		if (numeroSC != null) {
			if (numeroSC.getNumero() != null) {
				criteria.add(Restrictions.eq(
						ScoSolicitacaoCompraServico.Fields.SLC_NUMERO
								.toString(), numeroSC.getNumero()));
			}
		}

		if (numeroSS != null) {
			if (numeroSS.getNumero() != null) {
				criteria.add(Restrictions.eq(
						ScoSolicitacaoCompraServico.Fields.SLS_NUMERO
								.toString(), numeroSS.getNumero()));
			}
		}

		return criteria;
	}

	public List<PreItemPacVO> listaAssociadasSCItensPac(Integer numeroSCIni,
			Integer numeroSCFim) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoSolicitacaoCompraServico.class);

		criteria.createAlias(
				ScoSolicitacaoCompraServico.Fields.SCO_SOLICITACAO_SERVICO
						.toString(), "solicitacaoServico");
		criteria.createAlias(
				ScoSolicitacaoCompraServico.Fields.SCO_SOLICITACAO_DE_COMPRA
						.toString(), "solicitacaoCompra");
		criteria.createAlias("solicitacaoServico."
				+ ScoSolicitacaoServico.Fields.SERVICO.toString(), "SERV");
		criteria.createAlias("solicitacaoServico."
				+ ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), "PP");
		criteria.createAlias("solicitacaoServico."
				+ ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), "CC_AP");
		criteria.createAlias("solicitacaoServico."
				+ ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(),
				"CC_SOL");

		ProjectionList projection = Projections
				.projectionList()
				.add(Projections.property("solicitacaoServico."
						+ ScoSolicitacaoServico.Fields.NUMERO.toString()),
						PreItemPacVO.Fields.NUMERO.toString())
				.add(Projections.property("SERV."
						+ ScoServico.Fields.CODIGO.toString()),
						PreItemPacVO.Fields.COD_MAT_SERV.toString())
				.add(Projections.property("SERV."
						+ ScoServico.Fields.NOME.toString()),
						PreItemPacVO.Fields.NOME_MAT_SERV.toString())
				.add(Projections.property("solicitacaoServico."
						+ ScoSolicitacaoServico.Fields.IND_URGENTE.toString()),
						PreItemPacVO.Fields.IND_URGENTE.toString())
				.add(Projections.property("solicitacaoServico."
						+ ScoSolicitacaoServico.Fields.QTDE_SOLICITADA
								.toString()),
						PreItemPacVO.Fields.QTD_SS.toString())
				.add(Projections.property("solicitacaoServico."
						+ ScoSolicitacaoServico.Fields.VALOR_UNIT_PREVISTO
								.toString()),
						PreItemPacVO.Fields.VALOR_UNIT_PREVISTO.toString())
				.add(Projections.property("PP."
						+ ScoPontoParadaSolicitacao.Fields.CODIGO.toString()),
						PreItemPacVO.Fields.CAIXA_COD.toString())
				.add(Projections
						.property("PP."
								+ ScoPontoParadaSolicitacao.Fields.DESCRICAO
										.toString()),
						PreItemPacVO.Fields.CAIXA_DESC.toString())
				.add(Projections.property("CC_AP."
						+ FccCentroCustos.Fields.CODIGO.toString()),
						PreItemPacVO.Fields.CC_APLICACAO_COD.toString())
				.add(Projections.property("CC_AP."
						+ FccCentroCustos.Fields.DESCRICAO.toString()),
						PreItemPacVO.Fields.CC_APLICACAO_DESC.toString())
				.add(Projections.property("CC_SOL."
						+ FccCentroCustos.Fields.CODIGO.toString()),
						PreItemPacVO.Fields.CC_SOLICITANTE_COD.toString())
				.add(Projections.property("CC_SOL."
						+ FccCentroCustos.Fields.DESCRICAO.toString()),
						PreItemPacVO.Fields.CC_SOLICITANTE_DESC.toString())
				.add(Projections.property("solicitacaoCompra."
						+ ScoSolicitacaoDeCompra.Fields.NUMERO.toString()),
						PreItemPacVO.Fields.ASSOCIADA.toString());

		criteria.setProjection(projection);

		if (numeroSCIni != null) {
			if (numeroSCFim != null) {
				criteria.add(Restrictions.between(
						ScoSolicitacaoCompraServico.Fields.SLC_NUMERO
								.toString(), numeroSCIni, numeroSCFim));
			} else {
				criteria.add(Restrictions.eq(
						ScoSolicitacaoCompraServico.Fields.SLC_NUMERO
								.toString(), numeroSCIni));
			}
		}

	//	criteria.addOrder(Order.desc("solicitacaoServico."
	//			+ ScoSolicitacaoServico.Fields.IND_URGENTE.toString()));
		criteria.addOrder(Order.asc("solicitacaoServico."
				+ ScoSolicitacaoServico.Fields.NUMERO.toString()));

		criteria.setResultTransformer(Transformers
				.aliasToBean(PreItemPacVO.class));
		return executeCriteria(criteria);
	}

	public List<PreItemPacVO> listaAssociadasSSItensPac(Integer numeroSSIni,
			Integer numeroSSFim) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoSolicitacaoCompraServico.class);

		criteria.createAlias(
				ScoSolicitacaoCompraServico.Fields.SCO_SOLICITACAO_DE_COMPRA
						.toString(), "solicitacaoCompra");
		criteria.createAlias(
				ScoSolicitacaoCompraServico.Fields.SCO_SOLICITACAO_SERVICO
						.toString(), "solicitacaoServico");
		criteria.createAlias("solicitacaoCompra."
				+ ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");

		criteria.createAlias(
				"solicitacaoCompra."
						+ ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL
								.toString(), "PP");
		criteria.createAlias(
				"solicitacaoCompra."
						+ ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA
								.toString(), "CC_AP");
		criteria.createAlias("solicitacaoCompra."
				+ ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(),
				"CC_SOL");

		ProjectionList projection = Projections
				.projectionList()
				.add(Projections.property("solicitacaoCompra."
						+ ScoSolicitacaoDeCompra.Fields.NUMERO.toString()),
						PreItemPacVO.Fields.NUMERO.toString())
				.add(Projections.property("MAT."
						+ ScoMaterial.Fields.CODIGO.toString()),
						PreItemPacVO.Fields.COD_MAT_SERV.toString())
				.add(Projections.property("MAT."
						+ ScoMaterial.Fields.NOME.toString()),
						PreItemPacVO.Fields.NOME_MAT_SERV.toString())
				.add(Projections.property("solicitacaoCompra."
						+ ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString()),
						PreItemPacVO.Fields.IND_URGENTE.toString())
				.add(Projections.property("MAT."
						+ ScoMaterial.Fields.IND_ESTOCAVEL.toString()),
						PreItemPacVO.Fields.IND_ESTOCAVEL.toString())
				.add(Projections.property("solicitacaoCompra."
						+ ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA
								.toString()),
						PreItemPacVO.Fields.QTD_SC.toString())
				.add(Projections.property("solicitacaoCompra."
						+ ScoSolicitacaoDeCompra.Fields.VALOR_UNIT_PREVISTO
								.toString()),
						PreItemPacVO.Fields.VALOR_UNIT_PREVISTO.toString())
				.add(Projections.property("PP."
						+ ScoPontoParadaSolicitacao.Fields.CODIGO.toString()),
						PreItemPacVO.Fields.CAIXA_COD.toString())
				.add(Projections
						.property("PP."
								+ ScoPontoParadaSolicitacao.Fields.DESCRICAO
										.toString()),
						PreItemPacVO.Fields.CAIXA_DESC.toString())
				.add(Projections.property("CC_AP."
						+ FccCentroCustos.Fields.CODIGO.toString()),
						PreItemPacVO.Fields.CC_APLICACAO_COD.toString())
				.add(Projections.property("CC_AP."
						+ FccCentroCustos.Fields.DESCRICAO.toString()),
						PreItemPacVO.Fields.CC_APLICACAO_DESC.toString())
				.add(Projections.property("CC_SOL."
						+ FccCentroCustos.Fields.CODIGO.toString()),
						PreItemPacVO.Fields.CC_SOLICITANTE_COD.toString())
				.add(Projections.property("CC_SOL."
						+ FccCentroCustos.Fields.DESCRICAO.toString()),
						PreItemPacVO.Fields.CC_SOLICITANTE_DESC.toString())
				.add(Projections.property("solicitacaoServico."
						+ ScoSolicitacaoServico.Fields.NUMERO.toString()),
						PreItemPacVO.Fields.ASSOCIADA.toString());

		criteria.setProjection(projection);

		if (numeroSSIni != null) {
			if (numeroSSFim != null) {
				criteria.add(Restrictions.between(
						ScoSolicitacaoCompraServico.Fields.SLS_NUMERO
								.toString(), numeroSSIni, numeroSSFim));
			} else {
				criteria.add(Restrictions.eq(
						ScoSolicitacaoCompraServico.Fields.SLS_NUMERO
								.toString(), numeroSSIni));
			}
		}

		// criteria.addOrder(Order.desc("solicitacaoCompra."+ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString()));
		criteria.addOrder(Order.asc("solicitacaoCompra."
				+ ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));

		criteria.setResultTransformer(Transformers
				.aliasToBean(PreItemPacVO.class));
		return executeCriteria(criteria);
	}

	
	public List<ParcelaAfPendenteEntregaVO> consultarParcelasAfPendentesEntrega(
			final Integer numeroAf, final Short complemento,
			final Integer numeroAfp, final Integer grupoMaterial,
			final Integer codigoMaterial, final Integer numeroFornecedor,
			final Date dataPrevisaoEntregaInicial,
			final Date dataPrevisaoEntregaFinal, final DominioSimNao empenhada,
			final DominioSimNao atrasada, final DominioSimNao recebida) {
		StringBuilder sql = new StringBuilder(2000);

		sql.append("select afn.pfr_lct_numero as numeroAf, ");
		sql.append("       afn.nro_complemento as numeroCp, ");
		sql.append("	   pea.seq as seq, ");
		sql.append("       pea.afe_numero as numeroAfp, ");
		sql.append("       pea.afe_afn_numero as numeroAfeAfn, "); // adicionado
		sql.append("       pea.iaf_afn_numero as numeroIafAfnNumero, "); // adicionado
		sql.append("       pea.iaf_numero as numeroIaf, "); // adicionado
		sql.append("       fsc2.itl_numero as numeroItem,  ");
		sql.append("       pea.parcela as parcela, ");
		sql.append("       pea.dt_prev_entrega as previsaoEntrega, ");
		sql.append("       coalesce(pea.qtde,0) as quantidade, ");
		sql.append("       coalesce(pea.qtde_entregue,0) as quantidadeEntregue, ");
		sql.append("       pea.qtde - coalesce(pea.qtde_entregue, 0) as saldo, ");
		sql.append("       iaf.umd_codigo as codigoUnidade, ");
		sql.append("       unmd.descricao as descricaoCodigoUnidade, ");
		sql.append("       iaf.fator_conversao as fatorConversao, ");
		sql.append("       mat.codigo as codigoMaterial, ");
		sql.append("       mat.nome as nomeMaterial, ");
		sql.append("       gmt.codigo as codigoGrupoMaterial, ");
		sql.append("       gmt.descricao as descricaoGrupoMaterial, ");
		sql.append("       mat.codigo || ' - ' || substr(mat.nome,1,20) as material, ");
		sql.append("       pea.dt_prev_entrega_apos_atraso as dataPrevEntregaAposAtraso, ");
		sql.append("       pea.ind_cancelada as indCancelada, ");
		sql.append("       pea.ind_planejamento as indPlanejamento, ");
		sql.append("       pea.ind_assinatura as indAssinatura, ");
		sql.append("       pea.ind_entrega_imediata as indEntregaImediata, ");
		sql.append("       pea.ind_envio_fornecedor as indEnvioFornecedor, ");
		sql.append("       pea.ind_tramite_interno as indTramiteInterno, ");
		sql.append("       pea.ind_empenhada as indEmpenhada, ");
		sql.append("       pea.jst_codigo as jstCodigo, ");
		sql.append("       jstf.descricao as jstDescricao, ");
		sql.append("       pea.observacao as observacao, ");
		sql.append("       frn.numero as numeroFrn, ");
		this.subSelectRecebimento(sql);
		sql.append("  from agh.sco_progr_entrega_itens_af pea, agh.sco_itens_autorizacao_forn iaf, agh.sco_autorizacoes_forn afn,  ");
		sql.append("       agh.sco_fases_solicitacoes fsc1, agh.sco_fases_solicitacoes fsc2, agh.sco_solicitacoes_de_compras slc, ");
		sql.append("       agh.sco_materiais mat, agh.sco_fornecedores frn, agh.sco_grupos_materiais gmt, agh.sco_justificativas jstf, agh.sco_unidades_medida unmd ");
		condicoes(sql);

		inserirFiltrosConsultarParcAfPendEntr(numeroAf, complemento, numeroAfp,
				grupoMaterial, codigoMaterial, numeroFornecedor, sql);

		inserirDatasConsParcAfPendEntr(dataPrevisaoEntregaInicial,
				dataPrevisaoEntregaFinal, sql);

		if (empenhada != null && empenhada.equals(DominioSimNao.S)) {
			sql.append(" and (pea.ind_empenhada = 'S' or pea.ind_empenhada = 'P' or pea.jst_codigo is not null)");
		} else if (empenhada != null && empenhada.equals(DominioSimNao.N)) {
			sql.append(" and (pea.ind_empenhada <> 'S' and pea.ind_empenhada <> 'P' and pea.jst_codigo is null)");
		}

		if (atrasada != null && atrasada.equals(DominioSimNao.S)) {
			sql.append(" and pea.dt_prev_entrega < '"
					+ DateUtil.dataToString(new Date(), "dd/MM/yyyy") + "'");
			sql.append(" and coalesce(pea.qtde,0) > (coalesce(pea.qtde_entregue,0) + (select count(1) ");
			sql.append(" from agh.sce_nota_receb_provisorios NRP, ");
			sql.append(" agh.sce_item_receb_provisorios IRP ");
			sql.append(" where IND_CONFIRMADO = 'N' ");
			sql.append(" and IND_ESTORNO = 'N' ");
			sql.append(" and nrp.seq = irp.nrp_seq ");
			sql.append(" AND IRP.PEA_IAF_NUMERO = pea.iaf_numero ");
			sql.append(" AND IRP.PEA_IAF_AFN_NUMERO = pea.iaf_afn_numero ");
			sql.append(" AND IRP.PEA_PARCELA = pea.parcela ");
			sql.append(" AND IRP.PEA_SEQ = pea.seq))");
		} else if (atrasada != null && atrasada.equals(DominioSimNao.N)) {
			sql.append(" and pea.dt_prev_entrega  >  '"
					+ DateUtil.dataToString(new Date(), "dd/MM/yyyy") + "'");
		}

		if (DominioSimNao.S.equals(recebida)) {
			sql.append(" and iaf.ind_recebimento = 'S'");
		} else if (DominioSimNao.N.equals(recebida)) {
			sql.append(" and iaf.ind_recebimento = 'N'");
		}

		final SQLQuery query = createSQLQuery(sql.toString());

		query.setResultTransformer(Transformers
				.aliasToBean(ParcelaAfPendenteEntregaVO.class));

		Properties paramDominioSimNao = new Properties();
		paramDominioSimNao.put("enumClass",
				"br.gov.mec.aghu.dominio.DominioSimNao");
		paramDominioSimNao.put("type", "12");

		query.addScalar("seq", IntegerType.INSTANCE);
		query.addScalar("numeroAf", IntegerType.INSTANCE);
		query.addScalar("numeroCp", IntegerType.INSTANCE);
		query.addScalar("numeroAfp", IntegerType.INSTANCE);
		query.addScalar("numeroAfeAfn", IntegerType.INSTANCE);
		query.addScalar("numeroIafAfnNumero", IntegerType.INSTANCE);
		query.addScalar("numeroIaf", IntegerType.INSTANCE);
		query.addScalar("numeroItem", IntegerType.INSTANCE);
		query.addScalar("previsaoEntrega", DateType.INSTANCE);
		query.addScalar("quantidade", IntegerType.INSTANCE);
		query.addScalar("quantidadeEntregue", IntegerType.INSTANCE);
		query.addScalar("saldo", IntegerType.INSTANCE);
		query.addScalar("codigoUnidade", StringType.INSTANCE);
		query.addScalar("fatorConversao", IntegerType.INSTANCE);
		query.addScalar("material", StringType.INSTANCE);
		query.addScalar("indCancelada", StringType.INSTANCE);
		query.addScalar("indPlanejamento", StringType.INSTANCE);
		query.addScalar("indEntregaImediata", StringType.INSTANCE);
		query.addScalar("indEnvioFornecedor", StringType.INSTANCE);
		query.addScalar("indTramiteInterno", StringType.INSTANCE);
		query.addScalar("indAssinatura", StringType.INSTANCE);
		query.addScalar("indEmpenhada", StringType.INSTANCE);
		query.addScalar("jstCodigo", IntegerType.INSTANCE);
		query.addScalar("jstDescricao", StringType.INSTANCE);
		query.addScalar("contagemTemRecebimento", IntegerType.INSTANCE);
		query.addScalar("parcela", IntegerType.INSTANCE);
		query.addScalar("codigoMaterial", IntegerType.INSTANCE);
		query.addScalar("nomeMaterial", StringType.INSTANCE);
		query.addScalar("codigoGrupoMaterial", IntegerType.INSTANCE);
		query.addScalar("descricaoGrupoMaterial", StringType.INSTANCE);
		query.addScalar("observacao", StringType.INSTANCE);
		query.addScalar("dataPrevEntregaAposAtraso", DateType.INSTANCE);
		query.addScalar("numeroFrn", IntegerType.INSTANCE);
		query.addScalar("descricaoCodigoUnidade", StringType.INSTANCE);
		
		return query.list();
	}
	
	private void condicoes(StringBuilder sql) {
		sql.append(" where pea.iaf_afn_numero = iaf.afn_numero ");
		sql.append("   and pea.iaf_numero = iaf.numero ");
		sql.append("   and iaf.afn_numero = afn.numero ");
		sql.append("   and iaf.afn_numero = fsc1.iaf_afn_numero ");
		sql.append("   and iaf.numero = fsc1.iaf_numero ");
		sql.append("   and slc.numero = fsc1.slc_numero ");
		sql.append("   and fsc2.slc_numero = fsc1.slc_numero ");
		sql.append("   and fsc2.itl_lct_numero is not null ");
		sql.append("   and mat.codigo = slc.mat_codigo ");
		sql.append("   and frn.numero = afn.pfr_frn_numero ");
		sql.append("   and mat.gmt_codigo = gmt.codigo ");
		sql.append("   and pea.ind_planejamento = 'S' ");
		sql.append("   and pea.ind_assinatura = 'S' ");
		sql.append("   and pea.ind_cancelada = 'N' ");
		sql.append("   and coalesce(pea.qtde_entregue,0) < pea.qtde ");
		sql.append("   and pea.jst_codigo = jstf.codigo ");
		sql.append("   and iaf.umd_codigo = unmd.codigo ");
	}
	
	private void inserirDatasConsParcAfPendEntr(
			final Date dataPrevisaoEntregaInicial,
			final Date dataPrevisaoEntregaFinal, StringBuilder sql) {
		if (dataPrevisaoEntregaInicial != null
				&& dataPrevisaoEntregaFinal != null) {
			sql.append(" and pea.dt_prev_entrega between '"
					+ DateUtil.dataToString(dataPrevisaoEntregaInicial,
							"dd/MM/yyyy")
					+ "' and '"
					+ DateUtil.dataToString(
							DateUtil.adicionaDias(dataPrevisaoEntregaFinal, 1),
							"dd/MM/yyyy") + "'");
		} else if (dataPrevisaoEntregaInicial != null) {
			sql.append(" and pea.dt_prev_entrega = '"
					+ DateUtil.dataToString(dataPrevisaoEntregaInicial,
							"dd/MM/yyyy") + "'");
		} else if (dataPrevisaoEntregaFinal != null) {
			sql.append(" and pea.dt_prev_entrega = '"
					+ DateUtil.dataToString(dataPrevisaoEntregaFinal,
							"dd/MM/yyyy") + "'");
		}
	}

	private void inserirFiltrosConsultarParcAfPendEntr(final Integer numeroAf,
			final Short complemento, final Integer numeroAfp,
			final Integer grupoMaterial, final Integer codigoMaterial,
			final Integer numeroFornecedor, StringBuilder sql) {
		if (numeroAf != null) {
			sql.append(" and afn.pfr_lct_numero = " ).append( numeroAf);
		}

		if (complemento != null) {
			sql.append(" and afn.nro_complemento = " ).append( complemento);
		}

		if (numeroAfp != null) {
			sql.append(" and pea.afe_numero = " ).append( numeroAfp);
		}

		if (grupoMaterial != null) {
			sql.append(" and mat.gmt_codigo = " ).append( grupoMaterial);
		}

		if (codigoMaterial != null) {
			sql.append(" and slc.mat_codigo = " ).append( codigoMaterial);
		}

		if (numeroFornecedor != null) {
			sql.append(" and afn.pfr_frn_numero = " ).append( numeroFornecedor);
		}
	}
	
	
	private void subSelectRecebimento(StringBuilder sql) {
		sql.append(" (select count(*) ");
		sql.append("   from agh.sco_itens_autorizacao_forn iaf, ");
		sql.append("        agh.sco_progr_entrega_itens_af pea2 ");
		sql.append(" where afe_afn_numero  = pea.afe_afn_numero ");
		sql.append("   and afe_numero      = pea.afe_numero ");
		sql.append("   and iaf_numero     = pea.iaf_numero ");
		sql.append("   and (ind_empenhada = 'S' or jst_codigo is not null)  ");
		sql.append("   and ind_cancelada  = 'N' ");
		sql.append("   and dt_entrega  is null ");
		sql.append("   and iaf.afn_numero = pea2.iaf_afn_numero ");
		sql.append("   and iaf.numero     = pea2.iaf_numero ");
		sql.append("   and iaf.ind_recebimento = 'S') as contagemTemRecebimento ");
	}
	
	
}