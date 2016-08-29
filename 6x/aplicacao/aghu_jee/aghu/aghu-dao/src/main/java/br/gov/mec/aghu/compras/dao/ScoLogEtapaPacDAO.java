package br.gov.mec.aghu.compras.dao;

import java.util.List;

import javax.persistence.Query;

import br.gov.mec.aghu.model.ScoLogEtapaPac;

public class ScoLogEtapaPacDAO extends  br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoLogEtapaPac>{

	private static final long serialVersionUID = -8723793976710043939L;

	// C12 - #22068
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarHistoricoEtapa(Integer codigoEtapa){
		
		String scoLogEtapaPac = "SCO_LOG_ETAPA_PAC";
		String scoEtapaPac = "SCO_ETAPA_PAC";
		String scoLocalizacaoProcesso = "SCO_LOCALIZACAO_PROCESSOS";
		String rapServidores = "RAP_SERVIDORES";
		String rapPessoasFisicas = "RAP_PESSOAS_FISICAS";
		
		StringBuilder sql = new StringBuilder(800);
		
		sql.append("SELECT LOC.DESCRICAO");
		sql.append(", ETP.DESCRICAO_ETAPA");
		sql.append(", LEP.SITUACAO");
		sql.append(", LEP.TEMPO_PREVISTO");
		sql.append(", LEP.APONTAMENTO_USUARIO");
		sql.append(", RPF.NOME");
		sql.append(", LEP.DATA_APONTAMENTO");
		sql.append(", LEP.DATA_ALTERACAO");
		
		sql.append(" FROM");
		sql.append(" AGH." + scoLogEtapaPac + " LEP, ");
		sql.append(" AGH." + scoEtapaPac + " ETP, ");
		sql.append(" AGH." + scoLocalizacaoProcesso + " LOC, ");
		sql.append(" AGH." + rapServidores + " RAP, ");
		sql.append(" AGH." + rapPessoasFisicas + " RPF");

		sql.append(" WHERE LEP.CODIGO_ETAPA = ETP.CODIGO");
		sql.append(" AND ETP.LCP_CODIGO = LOC.CODIGO");
		sql.append(" AND LEP.SER_VIN_CODIGO = RAP.VIN_CODIGO");
		sql.append(" AND LEP.SER_MATRICULA = RAP.MATRICULA");
		sql.append(" AND RPF.CODIGO = RAP.PES_CODIGO");
		sql.append(" AND ETP.CODIGO = :codigoEtapa");
		
		sql.append(" UNION");
		
		sql.append(" SELECT LOC.DESCRICAO");
		sql.append(", ETP.DESCRICAO_ETAPA");
		sql.append(", ETP.SITUACAO");
		sql.append(", ETP.TEMPO_PREVISTO");
		sql.append(", ETP.APONTAMENTO_USUARIO");
		sql.append(", RPF.NOME");
		sql.append(", ETP.DATA_APONTAMENTO");
		sql.append(", null");
		
		sql.append(" FROM");
		sql.append(" AGH." + scoEtapaPac + " ETP, ");
		sql.append(" AGH." + scoLocalizacaoProcesso + " LOC, ");
		sql.append(" AGH." + rapServidores + " RAP, ");
		sql.append(" AGH." + rapPessoasFisicas + " RPF");
       
		sql.append(" WHERE ETP.LCP_CODIGO = LOC.CODIGO");
		sql.append(" AND ETP.SER_VIN_CODIGO = RAP.VIN_CODIGO");
		sql.append(" AND ETP.SER_MATRICULA = RAP.MATRICULA");
		sql.append(" AND RPF.CODIGO = RAP.PES_CODIGO");
		sql.append(" AND ETP.CODIGO = :codigoEtapa");
		
		Query query = this.createNativeQuery(sql.toString());
		query.setParameter("codigoEtapa", codigoEtapa);

		return query.getResultList();
	}
}
