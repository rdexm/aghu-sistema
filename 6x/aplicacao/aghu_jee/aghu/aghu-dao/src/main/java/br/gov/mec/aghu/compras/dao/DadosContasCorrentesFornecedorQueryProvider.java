package br.gov.mec.aghu.compras.dao;

import java.util.Properties;

import br.gov.mec.aghu.compras.vo.CadastroContasCorrentesFornecedorVO;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class DadosContasCorrentesFornecedorQueryProvider {
	
	public String getQueryForList(CadastroContasCorrentesFornecedorVO filtro){
		StringBuilder query = new StringBuilder(400);
		query.append("SELECT CNF.FRN_NUMERO AS fornecedorNumero, FRN.CGC AS fornecedorCGC, FRN.CPF AS fornecedorCPF, ");
		query.append("FRN.RAZAO_SOCIAL AS fornecedorRazaoSocial, CNF.AGB_BCO_CODIGO AS bancoCodigo, BCO.NOME AS bancoNome, ");
		query.append("CNF.AGB_CODIGO AS agenciaCodigo, AGB.DESCRICAO AS agenciaDescricao, CNF.CONTA_CORRENTE AS contaCorrente, ");
		query.append("CNF.IND_PREFERENCIAL AS indPreferencial ");
		query.append(getQueryBody(filtro, false));
		return query.toString();
	}
	
	public String getQueryForCount(CadastroContasCorrentesFornecedorVO filtro) {
		StringBuilder query = new StringBuilder(30);
		query.append("SELECT COUNT(*) as count ");
		query.append(getQueryBody(filtro, true));
		return query.toString();
	}
	
	private String getQueryBody(CadastroContasCorrentesFornecedorVO filtro, boolean isCount){
		StringBuilder body = new StringBuilder(450);
		body.append("FROM AGH.SCO_CONTA_CORRENTE_FORNECEDOR CNF ");
		body.append("INNER JOIN AGH.SCO_FORNECEDORES FRN ON  CNF.FRN_NUMERO = FRN.NUMERO ");
		body.append("INNER JOIN AGH.FCP_AGENCIA_BANCOS AGB ON CNF.AGB_BCO_CODIGO = AGB.BCO_CODIGO  AND CNF.AGB_CODIGO = AGB.CODIGO ");
		body.append("INNER JOIN AGH.FCP_BANCOS BCO ON AGB.BCO_CODIGO = BCO.CODIGO ");
		body.append("WHERE  CNF.FRN_NUMERO = "+filtro.getFornecedor().getNumero()+" ");
		if(filtro.getAgenciaBanco() != null){
			body.append("AND CNF.AGB_BCO_CODIGO = "+filtro.getAgenciaBanco().getFcpBanco().getCodigo()+" ");
			body.append("AND CNF.AGB_CODIGO = "+filtro.getAgenciaBanco().getId().getCodigo()+" ");
		}
		if(filtro.getNumeroConta() != null){
			body.append("AND CNF.CONTA_CORRENTE = '"+filtro.getNumeroConta()+"' ");
		}
		if(!isCount){
			body.append("ORDER BY CNF.FRN_NUMERO, CNF.AGB_BCO_CODIGO ");
		}
		return body.toString();
	}
	
	public Properties getPropertieForDomain(){
		Properties domainPropertie = new Properties();
		domainPropertie.put("enumClass", "br.gov.mec.aghu.dominio.DominioSimNao");
		domainPropertie.put("type", "12");
		return domainPropertie;
	}

	

}
