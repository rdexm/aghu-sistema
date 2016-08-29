package br.gov.mec.aghu.estoque.dao;

import java.util.Date;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceHistoricoEstoqueAlmoxarifadoMovimento;

public class SceHistoricoEstoqueAlmoxarifadoMovimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceHistoricoEstoqueAlmoxarifadoMovimento>{
	
	private static final long serialVersionUID = 8341531477875834422L;

	private DetachedCriteria obterCriteriaPesquisarHistoricoEstoqueAlmoxarifadoMovimentoPorDataCompetencia(Date dataCompetencia){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoEstoqueAlmoxarifadoMovimento.class);
		criteria.add(Restrictions.eq(SceHistoricoEstoqueAlmoxarifadoMovimento.Fields.ID_HEA_DT_COMPETENCIA.toString(), dataCompetencia));
		return criteria;
	}
	
	/**
	 * Pesquisa SceHistoricoEstoqueAlmoxarifado por data de competência
	 * @param dataCompetencia
	 * @return
	 */
	public Long pesquisarHistoricoEstoqueAlmoxarifadoMovimentoPorDataCompetenciaCount(Date dataCompetencia){
		DetachedCriteria criteria = this.obterCriteriaPesquisarHistoricoEstoqueAlmoxarifadoMovimentoPorDataCompetencia(dataCompetencia);
		criteria.setProjection(Projections.count(SceHistoricoEstoqueAlmoxarifadoMovimento.Fields.ID_HEA_SEQ.toString()));
		return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Persiste Históricos de Estoque Almoxarifado Movimento para Fechamento de Estoque Mensal (etapa 4)
	 * Obs. SQL NATIVO devido aos problemas de performance
	 * @param dataCompetencia
	 * @param servidorLogado
	 * @return quantidade de registros persistidos na operação
	 */
	public Integer persistirHistoricoEstoqueAlmoxarifadoMovimentoFechamentoEstoqueMensal(Date dataCompetencia){
		
		// Verifica o tipo de banco
		@SuppressWarnings("deprecation")
		final boolean isOracle = isOracle();

		// SQL Insert
		StringBuilder sqlInsert = new StringBuilder(150);
		
		sqlInsert.append("INSERT INTO AGH.SCE_HIST_ESTQ_ALMOX_MVTOS(");
		sqlInsert.append("hea_seq,");
			sqlInsert.append("hea_dt_competencia,"); // Parâmetro
		sqlInsert.append("tmv_seq,");
		sqlInsert.append("tmv_complemento,");
		sqlInsert.append("quantidade,");
		sqlInsert.append("Valor)");
		
		// SQL Select
		StringBuilder sqlSelect = new StringBuilder(150);
		
		sqlSelect.append("SELECT EAM.eal_seq,");
			sqlSelect.append(SceHistoricoEstoqueAlmoxarifadoDAO.getSqlDataCompetenciaFechamentoEstoque(isOracle, dataCompetencia) + SceHistoricoEstoqueAlmoxarifadoDAO.VIRGULA); // V_DT_COMPETENCIA
	 	sqlSelect.append("EAM.tmv_seq,");
	 	sqlSelect.append("EAM.tmv_complemento,");
	 	sqlSelect.append("EAM.quantidade,");
	 	sqlSelect.append("EAM.valor");
	 	sqlSelect.append(" FROM AGH.SCE_ESTQ_ALMOX_MVTOS EAM");

	 	// Cria SQL nativa: SQL Insert + SQL Select
	 	SQLQuery query = this.createSQLQuery(sqlInsert.toString() + sqlSelect.toString());
		
		// Executa a inclusão e retorna a quantidade de registros gravados
		Integer retorno = query.executeUpdate();
		this.flush();
		return retorno;
		
	}
	


}
