package br.gov.mec.aghu.compras.contaspagar.dao;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.contaspagar.vo.FiltroConsultaTitulosVO;


public class PesquisaTitulosQueryBuilder {
	
	private Boolean isOracle;
	private Boolean isCount;
	private FiltroConsultaTitulosVO filtros;
	private String order;
	private Boolean asc;
	
	private String obterSqlProjection(){
		
		StringBuilder builder  = new StringBuilder(2925);
		
		builder.append("SELECT NRS.SEQ as nrSeq,"
				+ " NRS.DT_GERACAO as nrDtGeracao,"
				+ " FRF.CODIGO as fonteCodigo,"
				+ " FRF.DESCRICAO as fonteDescricao,");
		
		if (isOracle) {
			builder.append(" (SELECT liq.VINCULACAO_PAGTO FROM SCE_LIQUIDACOES_SIAFI liq WHERE liq.nrs_seq = NRS.SEQ AND rownum = 1) AS liqVincPag, ");
		} else {
			builder.append(" (SELECT liq.VINCULACAO_PAGTO FROM agh.SCE_LIQUIDACOES_SIAFI liq WHERE liq.nrs_seq = NRS.SEQ FETCH FIRST 1 ROW ONLY) AS liqVincPag, ");
		}
		
		builder.append( " TTL.SEQ as tituloSeq,"
				+ " DFE.NUMERO as docNumero,"
				+ " DFE.SERIE as docSerie,"
				+ " VBG.SEQ as vbGestaoSeq,"
				+ " VBG.DESCRICAO as vbGestaoDesc,"
				+ " TTL.DT_VENCIMENTO as tituloDtVenc,");
		
		//fbraganca: ajuste em consulta pois 'FETCH FIRST 1 ROW ONLY' não roda em base Oracle
		if(isOracle) {
			builder.append(" (select liq.nro_empenho from SCE_LIQUIDACOES_SIAFI liq where liq.nrs_seq = NRS.SEQ and rownum = 1) as liqSiafiNrEmpenho,");
		} else {
			builder.append(" (SELECT liq.nro_empenho FROM agh.sce_liquidacoes_siafi liq WHERE liq.nrs_seq = nrs.seq FETCH FIRST 1 ROW ONLY) AS liqSiafiNrEmpenho,");
		}

		builder.append( " TTL.DT_PROG_PAG as titDtProgPag,"
				+ " TTL.IND_SITUACAO as tituloIndSituacao,"
				+ " frn.numero as fornNumero,"
				+ " frn.cgc as fornCgc, "
				+ " frn.cpf as fornCpf,"
				+ " frn.razao_social as fornRazaoSocial,"
				+ " frn.dt_validade_fgts as fornDtValidadeFgts,"
				+ " frn.dt_validade_inss as fornDtValidadeInss,"
				+ " frn.dt_validade_recfed as fornDtValidadeRecFed,"
				+ " '('||frn.ddd||')' || frn.fone as fornTelefone,"
				+ " AFN.NTD_GND_CODIGO as GndCodigo,"
				+ " AFN.NTD_CODIGO as natCodigo,"
				+ " NTD.DESCRICAO as natDescricao,"
				+ " AFN.PFR_LCT_NUMERO as propFornLctNumero,"
				+ " AFN.NRO_COMPLEMENTO as numeroComplemento,"

				+ " (select case inr.mat_codigo when NULL then 'Serviço' else 'Material' end"
				+ " from agh.sce_item_nrs inr");
		
		if(isOracle) {
			builder.append(" where inr.nrs_seq = NRS.SEQ and rownum = 1) as tipoMaterial, ");
		} else {
			builder.append(" where inr.nrs_seq = NRS.SEQ FETCH FIRST 1 ROW ONLY) as tipoMaterial, ");
		}

		builder.append( " PGT.NRO_DOCUMENTO as numeroDocumento,"
				+ " TDP.DESCRICAO as tipoDocPagtoDesc,"
				+ " PGT.ind_estorno as pagIndEstorno,"
				+ " PGT.DT_PAGAMENTO as dtPagamento,"
				+ " PGT.observacao as pagObservacao, "

				+ " (select sum(valor)"
				+ " from agh.FCP_RETENCAO_ALIQUOTAS fra, agh.fcp_valor_tributos vtr"
				+ " where vtr.inr_nrs_seq = TTL.NRS_SEQ and vtr.ttl_seq = ttl.seq and fra.fri_codigo  = vtr.fri_codigo"
				+ " and fra.imposto     = vtr.far_imposto and fra.numero = vtr.far_numero and fra.imposto not in ('MULTA','DESCONTO','DEVOLUÇÃO')) as valorTributos, "

				+ " TTL.VALOR as tituloValor,"

				+ " (select sum(valor)"
				+ " from agh.FCP_RETENCAO_ALIQUOTAS fra, agh.fcp_valor_tributos vtr"
				+ " where vtr.inr_nrs_seq = TTL.NRS_SEQ and vtr.ttl_seq = ttl.seq "
				+ " and fra.fri_codigo  = vtr.fri_codigo and fra.imposto = vtr.far_imposto"
				+ " and fra.numero = vtr.far_numero and fra.imposto in ('DESCONTO','DEVOLUÇÃO'))"
				+ " as valorDesconto, "

				+ " (select sum(valor)"
				+ " from agh.FCP_RETENCAO_ALIQUOTAS fra, agh.fcp_valor_tributos vtr "
				+ " where vtr.inr_nrs_seq = TTL.NRS_SEQ and vtr.ttl_seq = ttl.seq and fra.fri_codigo  = vtr.fri_codigo"
				+ " and fra.imposto = vtr.far_imposto and fra.numero = vtr.far_numero and fra.imposto in ('MULTA')) as valorMulta,"

				+ " PGT.VLR_DESCONTO as pgtvalorDesconto, "
				+ " PGT.VLR_ACRESCIMO as valorAcrescimo, "
				+ " CTAFRN.agb_bco_codigo as agbBcoCodigo,"
				+ " CTAFRN.nome as nomeBanco, "
				+ " CTAFRN.agb_codigo as agbCodigo, "
				+ " CTAFRN.conta_corrente as contaCorrenteFornec, "
				+ " FPG.DESCRICAO as pagamentoDescricao, "

				+ " coalesce("
				+ "(select distinct 'Sim'"
				+ " from agh.fcp_valor_tributos vtr"
				+ " where vtr.inr_nrs_seq = NRS.SEQ and far_imposto = 'INSS'),'Não') as temINSS, "

				+ " coalesce((select distinct 'Sim' "
				+ "from agh.fcp_valor_tributos vtr"
				+ " where vtr.inr_nrs_seq = NRS.SEQ and far_imposto = 'MULTA'),'Não') as temMulta ");

		return builder.toString();
	}
	
	private String obterSqlProjectionCount(){
		return " SELECT COUNT(*) ";
	}
	
	private String obterSqlFrom(){
		StringBuilder builder = new StringBuilder(1099);
		
		builder.append( " FROM agh.FCP_TITULOS TTL"
				+ " inner join agh.SCE_NOTA_RECEBIMENTOS NRS ON NRS.SEQ = TTL.NRS_SEQ"
				+ " inner join agh.SCE_DOCUMENTO_FISCAL_ENTRADAS DFE on DFE.SEQ = NRS.DFE_SEQ"
				+ " inner join agh.SCO_AUTORIZACOES_FORN AFN on AFN.NUMERO = NRS.AFN_NUMERO"
				+ " inner join agh.SCO_FORNECEDORES FRN on FRN.NUMERO = DFE.FRN_NUMERO"
				+ " left join agh.FSO_FONTES_RECURSO_FINANC FRF on FRF.CODIGO = NRS.FRF_CODIGO"
				+ " left join agh.FCP_PAGAMENTOS PGT on PGT.TTL_SEQ = TTL.SEQ"
				+ " left join agh.FCP_TIPO_DOC_PAGAMENTOS TDP on TDP.SEQ = PGT.TDP_SEQ"
				+ " left join agh.SCO_CONDICOES_PAGAMENTO_PROPOS CDP on CDP.NUMERO = AFN.CDP_NUMERO"
				+ " left join agh.SCO_FORMA_PAGAMENTOS FPG on FPG.CODIGO = CDP.FPG_CODIGO"
				+ " left join agh.FSO_VERBAS_GESTAO VBG on VBG.SEQ = AFN.VBG_SEQ"
				+ " left join agh.FSO_NATUREZAS_DESPESA NTD on NTD.CODIGO = AFN.NTD_CODIGO and NTD.GND_CODIGO = AFN.NTD_GND_CODIGO"
				+ " left join "
				+ "(select CTA.FRN_NUMERO, cta.agb_bco_codigo, bco.nome, cta.agb_codigo, cta.conta_corrente"
				+ " from agh.SCO_CONTA_CORRENTE_FORNECEDOR CTA, agh.FCP_BANCOS BCO"
				+ " where CTA.IND_PREFERENCIAL = 'S' and BCO.CODIGO = CTA.AGB_BCO_CODIGO) CTAFRN on CTAFRN.FRN_NUMERO = FRN.NUMERO");
		
		return builder.toString();
	}
	
	private String obterSqlWhere(){
		StringBuilder sqlWhereBuilder = new StringBuilder(151);
		sqlWhereBuilder.append(" where exists (select 1 from agh.SCE_LIQUIDACOES_SIAFI LQD where LQD.IND_ENVIADO = 'S' and LQD.nrs_seq = NRS.SEQ) ");
		
		// 01 String situacaoTitulo
		setFiltroSituacaoTitulo(sqlWhereBuilder);

		// 02 DominioSimNao estornado
		setFiltroEstornado(sqlWhereBuilder);

		// 03 DominioSimNao programado
		setFiltroProgramado(sqlWhereBuilder);

		// 04 Integer titulo
		setFiltroTitulo(sqlWhereBuilder);

		// 05 Long notaRecebimento
		setFiltroNotaRecebimento(sqlWhereBuilder);

		// 06 Date dataInicial
		setFiltroDataInicial(sqlWhereBuilder);

		// 07 Date dataFinal
		setFiltroDataFinal(sqlWhereBuilder);

		// 08 Long notaFiscal
		setFiltroNotaFiscal(sqlWhereBuilder);

		// 09 Date dataGeracaoNR
		setFiltroDataGeracaoNR(sqlWhereBuilder);

		// 10 ScoFornecedor scoFornecedor
		setFiltroFornecedor(sqlWhereBuilder);

		setFiltronaturezaDespesa(sqlWhereBuilder);

		// 13 FsoVerbaGestao fsoVerbaGestao
		setFiltroVerbaGestao(sqlWhereBuilder);

		// 14 Long numeroAF
		setFiltroNumeroAF(sqlWhereBuilder);

		// 15 Long complementoAF
		setFiltroComplementoAF(sqlWhereBuilder);

		// 16 DominioTipoPagamento tipoPagamento
		setFiltroTipoPagamento(sqlWhereBuilder);

		// 17 Long numeroDocumento
		setFiltroNumDoc(sqlWhereBuilder);

		// 18 FcpTipoDocPagamento fcpTipoDocPagamento
		setFiltroTipoDocPagamento(sqlWhereBuilder);

		// 19 DominioSimNao inss
		setFiltroINSS(sqlWhereBuilder);
		
		// 20 Date dataInicial pag
		setFiltroDataInicialPag(sqlWhereBuilder);
		
		// 21 Date dataFinalPag
		setFiltroDataFinalPag(sqlWhereBuilder);
		
		// 22 DominioSimNao pgtoEstornado
		setFiltroPgtoEstornado(sqlWhereBuilder);
		
		if(!isCount) {
			if(!StringUtils.isBlank(order)) {
				order = StringUtils.replace(order, "___", ".");
				sqlWhereBuilder.append(" ORDER BY ");
				sqlWhereBuilder.append(order);
				sqlWhereBuilder.append((Boolean.TRUE.equals(asc) ? " ASC " : " DESC"));
			}
			else {
				sqlWhereBuilder.append(" order by TTL.DT_VENCIMENTO, TTL.SEQ ");
			}
		}
		
		return sqlWhereBuilder.toString();
	}

	public void setFiltroINSS(StringBuilder sqlWhereBuilder) {
		if (filtros.getInss() != null) {
			sqlWhereBuilder.append(" and coalesce((select distinct 'Sim' from agh.fcp_valor_tributos "
					+ " vtr where vtr.inr_nrs_seq = NRS.SEQ and far_imposto = 'INSS'),'Não') = :inss ");
		}
	}

	public void setFiltroTipoDocPagamento(StringBuilder sqlWhereBuilder) {
		if (filtros.getFcpTipoDocPagamento() != null) {
			sqlWhereBuilder.append(" and PGT.TDP_SEQ = :fcpTipoDocPagamento ");
		}
	}

	public void setFiltroNumDoc(StringBuilder sqlWhereBuilder) {
		if (filtros.getNumeroDocumento() != null && filtros.getNumeroDocumento() > 0) {
			sqlWhereBuilder.append(" and PGT.NRO_DOCUMENTO = :numeroDocumento ");
		}
	}

	public void setFiltroTipoPagamento(StringBuilder sqlWhereBuilder) {
		if (filtros.getTipoPagamento() != null) {
			sqlWhereBuilder.append(" and (select case inr.mat_codigo when NULL then 'Serviço' else 'Material' end ")
					.append(" from agh.sce_item_nrs inr where inr.nrs_seq = NRS.SEQ ");
			if(isOracle) {
				sqlWhereBuilder.append(" and rownum = 1) = :tipoPagamento ");
			} else {
				sqlWhereBuilder.append(" FETCH FIRST 1 ROW ONLY) = :tipoPagamento ");
			}
		}
	}

	public void setFiltroComplementoAF(StringBuilder sqlWhereBuilder) {
		if (filtros.getComplementoAF() != null) {
			sqlWhereBuilder.append(" and AFN.NRO_COMPLEMENTO = :complementoAF  ");
		}
	}

	public void setFiltroNumeroAF(StringBuilder sqlWhereBuilder) {
		if (filtros.getNumeroAF() != null) {
			sqlWhereBuilder.append(" and AFN.PFR_LCT_NUMERO = :numeroAF ");
		}
	}

	public void setFiltroVerbaGestao(StringBuilder sqlWhereBuilder) {
		if (filtros.getFsoVerbaGestao() != null) {
			sqlWhereBuilder.append(" and VBG.SEQ = :fsoVerbaGestao ");
		}
	}

	public void setFiltronaturezaDespesa(StringBuilder sqlWhereBuilder) {
		if (filtros.getFsoNaturezaDespesa() != null) {
			sqlWhereBuilder.append(" and AFN.NTD_GND_CODIGO = :fsoGrupoNaturezaDespesa  ");
			sqlWhereBuilder.append(" and AFN.NTD_CODIGO = :fsoNaturezaDespesa ");
		}
	}

	public void setFiltroFornecedor(StringBuilder sqlWhereBuilder) {
		if (filtros.getScoFornecedor() != null) {
			sqlWhereBuilder.append(" and FRN.NUMERO = :scoFornecedor ");
		}
	}

	public void setFiltroDataGeracaoNR(StringBuilder sqlWhereBuilder) {
		if (filtros.getDataGeracaoNR() != null) {
			if(isOracle) {
				sqlWhereBuilder.append(" and trunc(NRS.DT_GERACAO) = :dataGeracaoNR ");
				
			} else {
				sqlWhereBuilder.append(" and date(NRS.DT_GERACAO) = :dataGeracaoNR ");
			}
		}
	}

	public void setFiltroNotaFiscal(StringBuilder sqlWhereBuilder) {
		if (filtros.getNotaFiscal() != null) {
			sqlWhereBuilder.append(" and DFE.NUMERO = :notaFiscal ");
		}
	}

	public void setFiltroDataFinal(StringBuilder sqlWhereBuilder) {
		if (filtros.getDataFinal() != null) {
			sqlWhereBuilder.append(" and TTL.DT_VENCIMENTO <= :dataFinal ");
		}
	}

	public void setFiltroDataInicial(StringBuilder sqlWhereBuilder) {
		if (filtros.getDataInicial() != null) {
			sqlWhereBuilder.append(" and TTL.DT_VENCIMENTO >= :dataInicial ");
		}
	}

	public void setFiltroNotaRecebimento(StringBuilder sqlWhereBuilder) {
		if (filtros.getNotaRecebimento() != null && filtros.getNotaRecebimento() > 0) {
			sqlWhereBuilder.append("and NRS.SEQ = :notaRecebimento ");
		}
	}

	public void setFiltroTitulo(StringBuilder sqlWhereBuilder) {
		if (filtros.getTitulo() != null && filtros.getTitulo() > 0) {
			sqlWhereBuilder.append(" and TTL.SEQ = :titulo ");
		}
	}

	public void setFiltroProgramado(StringBuilder sqlWhereBuilder) {
		if (filtros.getProgramado() != null) {
			if (filtros.getProgramado().isSim()) {
				sqlWhereBuilder.append(" and TTL.DT_PROG_PAG is not null ");
			} else {
				sqlWhereBuilder.append(" and TTL.DT_PROG_PAG is null ");
			}
		}
	}

	public void setFiltroEstornado(StringBuilder sqlWhereBuilder) {
		if (filtros.getEstornado() != null && filtros.getEstornado().isSim()) {
			sqlWhereBuilder.append(" and (TTL.ind_estorno = :estornado) ");
		} else if (filtros.getEstornado() != null) {
			sqlWhereBuilder.append(" and (TTL.ind_estorno = :estornado or TTL.ind_estorno is null) ");
		}
	}

	public void setFiltroSituacaoTitulo(StringBuilder sqlWhereBuilder) {
		if (filtros.getSituacaoTitulo() != null) {
			sqlWhereBuilder.append(" and TTL.IND_SITUACAO = :situacaoTitulo ");
		}
	}
	
	public void setFiltroDataInicialPag(StringBuilder sqlWhereBuilder) {
		if (filtros.getDataInicialPag() != null) {
			if(isOracle) {
			   sqlWhereBuilder.append(" and trunc(PGT.DT_GERACAO) >= :dataInicialPag ");
			} else {
			   sqlWhereBuilder.append(" and date(PGT.DT_GERACAO) >= :dataInicialPag ");
			}
		}
	}
	
	public void setFiltroDataFinalPag(StringBuilder sqlWhereBuilder) {
		if (filtros.getDataFinalPag() != null) {
			if(isOracle) {
			   sqlWhereBuilder.append(" and trunc(PGT.DT_GERACAO) <= :dataFinalPag ");
			} else {
			   sqlWhereBuilder.append(" and date(PGT.DT_GERACAO) <= :dataFinalPag ");
			}
		}
	}
	
	public void setFiltroPgtoEstornado(StringBuilder sqlWhereBuilder) {
		if (filtros.getPgtoEstornado() != null && filtros.getPgtoEstornado().isSim()) {
			sqlWhereBuilder.append(" and (PGT.ind_estorno = :pgtoEstornado) ");
		} else if (filtros.getPgtoEstornado() != null) {
			sqlWhereBuilder.append(" and (PGT.ind_estorno = :pgtoEstornado or PGT.ind_estorno is null) ");
		}
	}
	
	public void querySetParameter(Query query) {
		
		// 1 String situacaoTitulo
		querySetParameterSituacaoTitulo(query);

		// 2 DominioSimNao estornado
		querySetParameterEstornado(query);

		// 3 DominioSimNao programado não tem parâmetro a ser settado.

		// 4 Long titulo
		querySetParameterTitulo(query);

		// 05 Long notaRecebimento
		querySetParameterNotaRecebimento(query);

		// 06 Date dataInicial
		querySetParameterDataInicial(query);

		// 07 Date dataFinal
		querySetParameterDataFim(query);

		// 08 Long notaFiscal
		querySetParameterNotaFiscal(query);

		// 09 Date dataGeracaoNR
		querySetParameterDataGeracaoNR(query);

		// 10 ScoFornecedor scoFornecedor
		querySetParameterFornecedor(query);

		querySetParameterNaturezaDespesa(query);

		// 13 FsoVerbaGestao fsoVerbaGestao
		querySetParameterVerbaGestacao(query);

		// 14 Long numeroAF
		querySetParameterNumeroAF(query);

		// 15 Long complementoAF
		querySetParameterComplementoAF(query);

		// 16 DominioTipoPagamento tipoPagamento
		querySetParameterTipoPagamento(query);

		// 17 Long numeroDocumento
		querySetParameterNumDoc(query);

		// 18 FcpTipoDocPagamento fcpTipoDocPagamento
		querySetParameterTipoDocPagamento(query);

		// 19 DominioSimNao inss
		querySetParameterINSS(query);
		
		// 20 Date dataInicial Pag
		querySetParameterDataInicialPag(query);

		// 21 Date dataFinal Pag
		querySetParameterDataFimPag(query);
		
		// 22 DominioSimNao pgtoEstornado
		querySetParameterPgtoEstornado(query);
	}

	public void querySetParameterINSS(Query query) {
		if (filtros.getInss() != null) {
			query.setParameter("inss", filtros.getInss().getDescricao());
		}
	}

	public void querySetParameterTipoDocPagamento(Query query) {
		if (filtros.getFcpTipoDocPagamento() != null) {
			query.setParameter("fcpTipoDocPagamento", filtros.getFcpTipoDocPagamento().getSeq());
		}
	}

	public void querySetParameterNumDoc(Query query) {
		if (filtros.getNumeroDocumento() != null && filtros.getNumeroDocumento() > 0) {
			query.setParameter("numeroDocumento", filtros.getNumeroDocumento());
		}
	}

	public void querySetParameterTipoPagamento(Query query) {
		if (filtros.getTipoPagamento() != null) {
			query.setParameter("tipoPagamento", filtros.getTipoPagamento().getDescricao());
		}
	}

	public void querySetParameterComplementoAF(Query query) {
		if (filtros.getComplementoAF() != null) {
			query.setParameter("complementoAF", filtros.getComplementoAF());
		}
	}

	public void querySetParameterNumeroAF(Query query) {
		if (filtros.getNumeroAF() != null) {
			query.setParameter("numeroAF", filtros.getNumeroAF());
		}
	}

	public void querySetParameterVerbaGestacao(Query query) {
		if (filtros.getFsoVerbaGestao() != null) {
			query.setParameter("fsoVerbaGestao", filtros.getFsoVerbaGestao().getSeq());
		}
	}

	public void querySetParameterNaturezaDespesa(Query query) {
		if (filtros.getFsoNaturezaDespesa() != null) {
			query.setParameter("fsoGrupoNaturezaDespesa", filtros.getFsoNaturezaDespesa().getGrupoNaturezaDespesa().getCodigo());
			query.setParameter("fsoNaturezaDespesa", filtros.getFsoNaturezaDespesa().getId().getCodigo());
		}
	}

	public void querySetParameterFornecedor(Query query) {
		if (filtros.getScoFornecedor() != null) {
			query.setParameter("scoFornecedor", filtros.getScoFornecedor().getNumero());
		}
	}

	public void querySetParameterDataGeracaoNR(Query query) {
		if (filtros.getDataGeracaoNR() != null) {
			query.setParameter("dataGeracaoNR", filtros.getDataGeracaoNR());
		}
	}

	public void querySetParameterNotaFiscal(Query query) {
		if (filtros.getNotaFiscal() != null) {
			query.setParameter("notaFiscal", filtros.getNotaFiscal());
		}
	}

	public void querySetParameterDataFim(Query query) {
		if (filtros.getDataFinal() != null) {
			query.setParameter("dataFinal", filtros.getDataFinal());
		}
	}

	public void querySetParameterDataInicial(Query query) {
		if (filtros.getDataInicial() != null) {
			query.setParameter("dataInicial", filtros.getDataInicial());
		}
	}

	public void querySetParameterNotaRecebimento(Query query) {
		if (filtros.getNotaRecebimento() != null && filtros.getNotaRecebimento() > 0) {
			query.setParameter("notaRecebimento", filtros.getNotaRecebimento());
		}
	}

	public void querySetParameterTitulo(Query query) {
		if (filtros.getTitulo() != null && filtros.getTitulo() > 0) {
			query.setParameter("titulo", filtros.getTitulo());
		}
	}

	public void querySetParameterEstornado(Query query) {
		if (filtros.getEstornado() != null) {
			query.setParameter("estornado", filtros.getEstornado().toString());
		}
	}

	public void querySetParameterSituacaoTitulo(Query query) {
		if (filtros.getSituacaoTitulo() != null) {
			query.setParameter("situacaoTitulo", filtros.getSituacaoTitulo().toString());
		}
	}
	
	

	public void querySetParameterDataInicialPag(Query query) {
		if (filtros.getDataInicialPag() != null) {
			query.setParameter("dataInicialPag", filtros.getDataInicialPag());
		}
	}
	
	public void querySetParameterDataFimPag(Query query) {
		if (filtros.getDataFinalPag() != null) {
			query.setParameter("dataFinalPag", filtros.getDataFinalPag());
		}
	}
	
	public void querySetParameterPgtoEstornado(Query query) {
		if (filtros.getPgtoEstornado() != null) {
			query.setParameter("pgtoEstornado", filtros.getPgtoEstornado().toString());
		}
	}
	
	private String obterSql() {
		StringBuilder sql =  new StringBuilder(2000);
		
		if (isCount) {
			sql.append(obterSqlProjectionCount());
		} else {
			sql.append(obterSqlProjection());
		}
		
		sql.append(obterSqlFrom())
		   .append(obterSqlWhere());
		
		return sql.toString();	
	}
	
	public PesquisaTitulosQueryBuilder(FiltroConsultaTitulosVO filtros, Boolean isOracle, Boolean isCount, String orderBy, Boolean asc){
		this.filtros = filtros;
		this.isOracle = isOracle;
		this.isCount = isCount;
		this.order = orderBy;
		this.asc = asc;
	}

	public String builder() {
		return obterSql();	
	}
}
