package br.gov.mec.aghu.compras.contaspagar.dao;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.utils.DateUtil;


public class PagamentoProgramadoQueryBuilder {
	
	private Boolean isCount;
	private String dataPagamento;
	private String order;
	private Boolean asc;

	private String obterSqlProjection(){
		
		StringBuilder sqlProjectionBuilder = new StringBuilder(484);

		sqlProjectionBuilder
			.append("SELECT frn.razao_social, frn.numero, NRS.SEQ nrsSeq, FRF.CODIGO, FRF.DESCRICAO, LQS.VINCULACAO_PAGTO, TTL.SEQ ttlSeq, TTL.DT_VENCIMENTO, TTL.IND_SITUACAO, TTL.VALOR - coalesce((select sum(valor)  from AGH.FCP_RETENCAO_ALIQUOTAS fra, AGH.fcp_valor_tributos vtr WHERE vtr.inr_nrs_seq = TTL.NRS_SEQ and vtr.ttl_seq = ttl.seq and fra.fri_codigo = vtr.fri_codigo and fra.imposto = vtr.far_imposto and fra.numero = vtr.far_numero and fra.imposto not in ('MULTA','DESCONTO','DEVOLUÇÃO')),0) ");
		
		return sqlProjectionBuilder.toString();
	}
	
	private String obterSqlProjectionCount(){
		return " SELECT COUNT(*) ";
	}
	
	private String obterSqlFrom(){
		StringBuilder sqlFromBuilder = new StringBuilder(372);
		
		sqlFromBuilder
			.append("FROM AGH.FCP_TITULOS TTL inner join AGH.SCE_NOTA_RECEBIMENTOS NRS ON NRS.SEQ = TTL.NRS_SEQ inner join AGH.SCE_DOCUMENTO_FISCAL_ENTRADAS DFE on DFE.SEQ = NRS.DFE_SEQ inner join AGH.SCO_FORNECEDORES FRN on FRN.NUMERO = DFE.FRN_NUMERO left join AGH.FSO_FONTES_RECURSO_FINANC FRF on FRF.CODIGO = NRS.FRF_CODIGO left join AGH.SCE_LIQUIDACOES_SIAFI LQS on LQS.NRS_SEQ = NRS.SEQ ");

		return sqlFromBuilder.toString();
	}
	
	private String obterSqlWhere(){
		StringBuilder sqlWhereBuilder = new StringBuilder(62);
		
		sqlWhereBuilder
			.append(" where TTL.DT_PROG_PAG = '" + dataPagamento + "'" );
			
		if(!isCount){
			if(!StringUtils.isBlank(order)) {
				order = StringUtils.replace(order, "___", ".");
				sqlWhereBuilder.append(" ORDER BY ");
				sqlWhereBuilder.append(order);
				sqlWhereBuilder.append((Boolean.TRUE.equals(asc) ? " ASC " : " DESC"));
			}
			else {
				sqlWhereBuilder.append("order by TTL.DT_VENCIMENTO, TTL.SEQ");
			}
		}
			
		
		return sqlWhereBuilder.toString();
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
	
	public PagamentoProgramadoQueryBuilder(Date dataPagamento, Boolean isCount, String orderBy, Boolean asc){
		
		String dataFormatada = null;
		
		if(dataPagamento == null){
			DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy");
		} else {
			dataFormatada = DateUtil.obterDataFormatada(dataPagamento, "dd/MM/yyyy");
		}
		
		this.dataPagamento = dataFormatada;
		this.isCount = isCount;
		this.order = orderBy;
		this.asc = asc;
	}

	public String builder() {
		return obterSql();	
	}
}
