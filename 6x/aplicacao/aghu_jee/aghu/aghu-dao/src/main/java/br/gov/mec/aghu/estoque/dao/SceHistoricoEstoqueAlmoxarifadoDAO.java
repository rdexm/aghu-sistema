package br.gov.mec.aghu.estoque.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.SQLQuery;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceHistoricoEstoqueAlmoxarifado;

public class SceHistoricoEstoqueAlmoxarifadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceHistoricoEstoqueAlmoxarifado>{

	
	private static final long serialVersionUID = -261617354946905726L;

	/**
	 * Persiste Históricos de Estoque Almoxarifado para Fechamento de Estoque Mensal (etapa 3)
	 * Obs. SQL NATIVO devido aos problemas de performance
	 * @param dataCompetencia
	 * @param servidorLogado
	 * @return quantidade de registros persistidos na operação
	 */
	public Integer persistirHistoricoEstoqueAlmoxarifadoFechamentoEstoqueMensal(Date dataCompetencia, RapServidores servidorLogado){

		// Verifica o tipo de banco
		@SuppressWarnings("deprecation")
		final boolean isOracle = isOracle();

		// SQL Insert
		final StringBuilder sbInsert = new StringBuilder(700);
		
		sbInsert.append("INSERT INTO AGH.SCE_HIST_ESTQ_ALMOXS(");
		sbInsert.append("seq,");
			sbInsert.append("DT_COMPETENCIA,"); // Parâmetro
		sbInsert.append("alm_seq,");
		sbInsert.append("mat_codigo,");
		sbInsert.append("umd_codigo,");
		sbInsert.append("dt_geracao,");
		sbInsert.append("ser_matricula,");
		sbInsert.append("ser_vin_codigo,");
		sbInsert.append("ind_estocavel,");
		sbInsert.append("ind_estq_min_calc,");
		sbInsert.append("ind_ponto_pedido_calc,");
		sbInsert.append("ind_situacao,");
			sbInsert.append("DT_GERACAO_HISTORICO,"); // Parâmetro
			sbInsert.append("SER_MATRICULA_HISTORICO,"); // Parâmetro
			sbInsert.append("SER_VIN_CODIGO_HISTORICO,"); // Parâmetro
		sbInsert.append("qtde_em_uso,");
		sbInsert.append("frn_numero,");
		sbInsert.append("qtde_bloqueada,");
		
		// MELHORIA EM PRODUÇÃO #29582
		sbInsert.append("qtde_bloq_problema,"); 
		
		sbInsert.append("qtde_disponivel,");
		sbInsert.append("qtde_ponto_pedido,");
		sbInsert.append("qtde_estq_min,");
		sbInsert.append("qtde_estq_max,");
		sbInsert.append("tempo_reposicao,");
		sbInsert.append("slc_numero,");
		sbInsert.append("endereco,");
		sbInsert.append("dt_alteracao,");
		sbInsert.append("ser_matricula_alterado,");
		sbInsert.append("ser_vin_codigo_alterado,");
		sbInsert.append("dt_desativacao,");
		sbInsert.append("ser_matricula_desativado,");
		sbInsert.append("ser_vin_codigo_desativado,");
		sbInsert.append("ind_controle_validade,");
		sbInsert.append("dt_ultimo_consumo,");
		sbInsert.append("dt_ultima_compra,");
		sbInsert.append("dt_ultima_compra_ff,");
		sbInsert.append("ind_consignado,");
		
		//Melhoria em Produção #31658
				sbInsert.append("QTDE_BLOQ_CONSUMO,");
				sbInsert.append("QTDE_BLOQ_DISPENSACAO,");
				
		sbInsert.append("qtde_bloq_entr_transf)");

		// SQL Select 
		final StringBuilder sbSelect = new StringBuilder(900);

		sbSelect.append("SELECT EA.seq,");
			sbSelect.append(getSqlDataCompetenciaFechamentoEstoque(isOracle, dataCompetencia) + VIRGULA); // V_DT_COMPETENCIA
	 	sbSelect.append("EA.alm_seq,");
	 	sbSelect.append("EA.mat_codigo,");
	 	sbSelect.append("EA.umd_codigo,");
	 	sbSelect.append("EA.dt_geracao,");
	 	sbSelect.append("EA.ser_matricula,");
	 	sbSelect.append("EA.ser_vin_codigo,");
	 	sbSelect.append("EA.ind_estocavel,");
	 	sbSelect.append("EA.ind_estq_min_calc,");
	 	sbSelect.append("EA.ind_ponto_pedido_calc,");
	 	sbSelect.append("EA.ind_situacao,");
	 		sbSelect.append(getSqlDataAtualFechamentoEstoque(isOracle) + VIRGULA); // DT_GERACAO_HISTORICO, 
		 	sbSelect.append(servidorLogado.getId().getMatricula() + VIRGULA); // SER_MATRICULA_HISTORICO
		 	sbSelect.append(servidorLogado.getId().getVinCodigo() + VIRGULA); // SER_VIN_CODIGO_HISTORICO
	 	sbSelect.append("EA.qtde_em_uso,");
	 	sbSelect.append("EA.frn_numero,");
	 	sbSelect.append("EA.qtde_bloqueada,");
	 	
	 	// MELHORIA EM PRODUÇÃO #29582
	 	if(isOracle){
		 	sbSelect.append("(SELECT nvl(sum((HPM.qtde_problema - (nvl(HPM.qtde_desbloqueada,0) + nvl(HPM.qtde_df,0)))),0) FROM AGH.SCE_HIST_PROBLEMA_MATERIAIS HPM WHERE eal_seq = EA.seq),");
	 	} else{
		 	sbSelect.append("(SELECT coalesce(sum((HPM.qtde_problema - (coalesce(HPM.qtde_desbloqueada,0) + coalesce(HPM.qtde_df,0)))),0) FROM AGH.SCE_HIST_PROBLEMA_MATERIAIS HPM WHERE eal_seq = EA.seq),");
	 	}
	 	
	 	sbSelect.append("EA.qtde_disponivel,");
	 	sbSelect.append("EA.qtde_ponto_pedido,");
	 	sbSelect.append("EA.qtde_estq_min,");
	 	sbSelect.append("EA.qtde_estq_max,");
	 	sbSelect.append("EA.tempo_reposicao,");
	 	sbSelect.append("EA.slc_numero,");
	 	sbSelect.append("EA.endereco,");
	 	sbSelect.append("EA.dt_alteracao,");
	 	sbSelect.append("EA.ser_matricula_alterado,");
	 	sbSelect.append("EA.ser_vin_codigo_alterado,");
	 	sbSelect.append("EA.dt_desativacao,");
	 	sbSelect.append("EA.ser_matricula_desativado,");
	 	sbSelect.append("EA.ser_vin_codigo_desativado,");
	 	sbSelect.append("EA.ind_controle_validade,");
	 	sbSelect.append("EA.dt_ultimo_consumo,");
	 	sbSelect.append("EA.dt_ultima_compra,");
	 	sbSelect.append("EA.dt_ultima_compra_ff,");
	 	sbSelect.append("EA.ind_consignado,");
	 	
	 	//Melhoria em Produção #31658
	 	sbSelect.append("EA.QTDE_BLOQ_CONSUMO,");
	 	sbSelect.append("EA.QTDE_BLOQ_DISPENSACAO,");
		
	 	sbSelect.append("EA.qtde_bloq_entr_transf");
	 	sbSelect.append(" FROM AGH.SCE_ESTQ_ALMOXS EA");
	 	
	 	// Cria SQL nativa: SQL Insert + SQL Select
		SQLQuery query = this.createSQLQuery(sbInsert.toString() + sbSelect.toString());
		
		// Executa a inclusão e retorna a quantidade de registros gravados
		Integer retorno = query.executeUpdate();
		this.flush();
		
		return retorno;

	}
	
	public static final String VIRGULA = ",";
	
	/**
	 * Obtém a data de competência em uma SQL nativa ou de acordo com o banco
	 * @param isOracle
	 * @param dataCompetencia
	 * @return
	 */
	public static String getSqlDataCompetenciaFechamentoEstoque(final boolean isOracle, final Date dataCompetencia){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		final String dataFormatada = dateFormat.format(dataCompetencia);
		final String oracle = "TO_DATE('" + dataFormatada + "', 'dd/mm/yyyy hh24:mi:ss')";
		final String postgre = 	"'" + dataFormatada + "'";
		return isOracle ?  oracle : postgre;
	}
	
	/**
	 * Obtém a função SQL nativa para data atual de acordo com o banco 
	 * @param isOracle
	 * @return
	 */
	public static String getSqlDataAtualFechamentoEstoque(final boolean isOracle){
		return isOracle ? "SYSDATE":"NOW()";
	}
	
}


