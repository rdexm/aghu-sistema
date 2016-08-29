package br.gov.mec.aghu.compras.dao;

import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ConsultarAndamentoProcessoCompraQueryProvider {

    private ConsultarAndamentoProcessoCompraVO filtro;

    public ConsultarAndamentoProcessoCompraQueryProvider() {

    }

    public ConsultarAndamentoProcessoCompraQueryProvider(ConsultarAndamentoProcessoCompraVO filtro) {
	this.setFiltro(filtro);
    }

    public String buildQuery() {
	return getProjections().append(getQuery(false)).toString();
    }

    private StringBuffer getProjections() {
	StringBuffer sql = new StringBuffer(5000);
	sql.append("SELECT ")
	.append("PAC.SEQ AS pacSeq, ")
	.append("PAC.LCT_NUMERO AS numeroPac, ")
	.append("PAC.DT_ENTRADA AS dtEntrada, ")
	.append("PAC.DT_SAIDA AS dtSaida, ")
	.append("PAC.LCP_CODIGO AS lcpCodigo ")
	.append("FROM AGH.SCO_ANDAMENTO_PROCESSO_COMPRAS PAC ")
	.append("WHERE PAC.AFN_NUMERO IS NULL ");
	return sql;
    }

    public String buildQueryCount() {
	return getProjectionsCount().append(getQuery(true)).toString();
    }

    private StringBuffer getProjectionsCount() {
	StringBuffer sql = new StringBuffer(100);
	sql.append("SELECT COUNT(*) ")
	.append("FROM AGH.SCO_ANDAMENTO_PROCESSO_COMPRAS PAC ")
	.append("WHERE PAC.AFN_NUMERO IS NULL ");
	return sql;
    }

    private StringBuffer getQuery(boolean isCount) {
	StringBuffer sql = new StringBuffer(200);
	sql.append(getNumeroPacRestriction()).append(getPendenteRestriction()).append(getModalidadeLicitacaoRestriction())
		.append(getTipoRestriction()).append(getLocalRestriction()).append(getObjetoRestriction())
		.append(getFornecedorRestriction()).append(getNumeroAFRestriction()).append(getScRestriction()).append(getSsRestriction())
		.append(getGrupoMaterialRestriction()).append(getMaterialRestriction()).append(getServicoRestriction())
		.append(getMarcaRestriction()).append(getCentroCustoSolicitanteRestriction()).append(getCentroCustoAplicacaoRestriction())
		.append(getPacAFRestriction()).append(getPacIncompletoRestriction()).append(getPacAFPendenteRestriction())
		.append(getPacEncerradoRestriction()).append(getPacInvestimentoRestriction()).append(getDataGeracaoInicialRestriction())
		.append(getDataGeracaoFinalRestriction()).append(getDataVencimentoInicialRestriction())
		.append(getDataVencimentoFinalRestriction()).append(getGestorRestriction());
	if (!isCount) {
	    sql.append("ORDER BY PAC.SEQ DESC ");
	}
	return sql;
    }

    public String buildQueryNumeroAFsPorPac(Integer numeroPac) {
	return getQueryAFsPorPacCount(numeroPac).toString();
    }

    private StringBuffer getQueryAFsPorPacCount(Integer numeroPac) {
	StringBuffer sql = new StringBuffer(100);
	sql.append("SELECT COUNT(*) ")
	.append("FROM AGH.SCO_AUTORIZACOES_FORN AFN ")
	.append("WHERE PFR_LCT_NUMERO = ").append( numeroPac ).append(' ')
	.append(getPacEmAfGeneralRestriction())
	.append(getPacEmAfMarcaRestriction());
	return sql;
    }

    public String buildQueryObterResponsavelPeloProcesso(Integer pacSeq) {
		StringBuffer sql = new StringBuffer(400);
		sql.append("SELECT PES.NOME AS nome ")
	    .append("FROM AGH.SCO_ANDAMENTO_PROCESSO_COMPRAS PAC, ")
	    .append("AGH.RAP_PESSOAS_FISICAS PES, ")
	    .append("AGH.RAP_VINCULOS VIN, ")
	    .append("AGH.RAP_SERVIDORES SER ")
	    .append("WHERE VIN.CODIGO = SER.VIN_CODIGO ")
	    .append("AND SER.PES_CODIGO = PES.CODIGO ")
	    .append("AND PAC.SER_VIN_CODIGO = SER.VIN_CODIGO ")
	    .append("AND PAC.SER_MATRICULA = SER.MATRICULA ")
	    .append("AND PAC.SEQ = "+pacSeq+" ");
		return sql.toString();
	}

    public String buildQueryObterDadosGestor(Integer numeroPac) {
		StringBuffer sql = new StringBuffer(500);
		sql.append("SELECT PES.NOME AS nomeGestor, ")
	    .append("LCT.SER_MATRICULA_GESTOR AS matriculaGestor, ")
	    .append("LCT.MLC_CODIGO AS mlcCodigo ")
	    .append("FROM AGH.SCO_LICITACOES LCT, ")
	    .append("AGH.RAP_PESSOAS_FISICAS PES, ")
	    .append("AGH.RAP_VINCULOS VIN, ")
	    .append("AGH.RAP_SERVIDORES SER ")
	    .append("WHERE VIN.CODIGO = SER.VIN_CODIGO ")
	    .append("AND  SER.PES_CODIGO = PES.CODIGO ")
	    .append("AND LCT.NUMERO = "+numeroPac+" ")
	    .append("AND SER.MATRICULA  = COALESCE(LCT.SER_MATRICULA_GESTOR, LCT.SER_MATRICULA_DIGITADA) ")
	    .append("AND SER.VIN_CODIGO = COALESCE(LCT.SER_VIN_CODIGO_GESTOR, LCT.SER_VIN_CODIGO_DIGITADA) ");
		return sql.toString();
	}

    public String buildQueryDadosPrimeiraAF(Integer numeroPac) {
	StringBuffer sql = new StringBuffer(500);
	sql.append("SELECT AFN.PFR_LCT_NUMERO AS af, ")
	.append("AFN.NRO_COMPLEMENTO AS cp, ")
	.append("AFN.DT_VENCTO_CONTRATO AS vencimentoContrato, ")
	.append("AFN.IND_SITUACAO AS situacao, ")
	.append("AFN.DT_GERACAO AS dataGeracao ")
	.append("FROM AGH.SCO_AUTORIZACOES_FORN AFN ")
	.append("WHERE NUMERO = (SELECT MIN(AFN.NUMERO) ")
	.append("FROM AGH.SCO_AUTORIZACOES_FORN AFN ")
	.append("WHERE PFR_LCT_NUMERO = "+numeroPac+" ")
	.append(getPacEmAfGeneralRestriction())
	.append(getPacEmAfMarcaRestriction())
	.append(") ");
	if(filtro.getFornecedor() != null){
		sql.append("AND PFR_FRN_NUMERO = "+filtro.getFornecedor().getNumero()+" ");
	}
	sql.append("AND ((IND_SITUACAO IN ('AE','PA') AND COALESCE("+getFiltroPacAfCoalesce(filtro.getPacAF())+" ,'S') = 'S') ")
	.append("OR COALESCE("+getFiltroPacAfCoalesce(filtro.getPacAF())+",'S') = 'N') ")
	.append(getPacEmAfMarcaRestriction());
	return sql.toString();
    }

    private String getFiltroPacAfCoalesce(DominioSimNao pacAF) {
	if (pacAF == null) {
	    return "NULL";
	} else {
	    return "'" + pacAF + "'";
	}
    }

    private StringBuffer getPacEmAfMarcaRestriction() {
	StringBuffer sql = new StringBuffer(150);
	sql.append("AND NUMERO IN ").append("(SELECT AFN_NUMERO FROM AGH.SCO_ITENS_AUTORIZACAO_FORN IAF WHERE AFN_NUMERO = AFN.NUMERO ");
	if (filtro.getMarcaComercial() != null) {
	    sql.append("AND MCM_CODIGO = " + filtro.getMarcaComercial().getCodigo() + " )");
	} else {
	    sql.append(") ");
	}
	return sql;
    }

    private StringBuffer getPacEmAfGeneralRestriction() {
	StringBuffer sql = new StringBuffer(800);
	if (filtro.getFornecedor() != null) {
	    sql.append("AND PFR_FRN_NUMERO = " + filtro.getFornecedor().getNumero() + " ");
	}
	sql.append("AND ((IND_SITUACAO IN ('AE','PA') AND COALESCE(" + getFiltroPacAfCoalesce(filtro.getPacAF()) + ",'S') = 'S') ").append(
		"OR COALESCE(" + getFiltroPacAfCoalesce(filtro.getPacAF()) + ",'S') = 'N') ")

	.append("AND (NUMERO IN ")
	.append("(SELECT AFN_NUMERO FROM AGH.SCO_SOLICITACOES_DE_COMPRAS SLC, ")
	.append("AGH.SCO_FASES_SOLICITACOES FSC, AGH.SCO_ITENS_AUTORIZACAO_FORN IAF ")
	.append("WHERE IAF.AFN_NUMERO = AFN.NUMERO ")
	.append("AND FSC.IAF_AFN_NUMERO = IAF.AFN_NUMERO ")
	.append("AND FSC.IAF_NUMERO     = IAF.NUMERO ")
	.append("AND SLC.NUMERO         = FSC.SLC_NUMERO ");
	if (filtro.getMaterial() != null) {
	    sql.append("AND SLC.MAT_CODIGO     = " + filtro.getMaterial().getCodigo() + " ) ");
	} else {
	    sql.append(") ");
	}
	sql.append("OR NUMERO IN ")
	.append("(SELECT AFN_NUMERO FROM AGH.SCO_SOLICITACOES_SERVICO SLS, ")
	.append("AGH.SCO_FASES_SOLICITACOES FSC, AGH.SCO_ITENS_AUTORIZACAO_FORN IAF ")
	.append("WHERE IAF.AFN_NUMERO = AFN.NUMERO ")
	.append("AND FSC.IAF_AFN_NUMERO = IAF.AFN_NUMERO ")
	.append("AND FSC.IAF_NUMERO     = IAF.NUMERO ")
	.append("AND SLS.NUMERO         = FSC.SLS_NUMERO ");
	if (filtro.getServico() != null) {
	    sql.append("AND SLS.SRV_CODIGO     = " + filtro.getServico().getCodigo() + " ) ");
	} else {
	    sql.append(") ");
	}
	sql.append(") ");
	return sql;
    }

    private String getGestorRestriction() {
	if (filtro.getGestor() != null && !filtro.getGestor().isEmpty()) {
	    return "AND (PAC.LCT_NUMERO IN " + "(SELECT NUMERO " + "FROM AGH.V_RAP_PESSOA_SERVIDOR VPS, " + "AGH.SCO_LICITACOES LCT " 
		    + "WHERE LCT.NUMERO = PAC.LCT_NUMERO " + "AND VPS.SER_MATRICULA = LCT.SER_MATRICULA_GESTOR "
		    + "AND VPS.NOME LIKE '%' || '"
		    + filtro.getGestor()
		    + "' || '%') "
		    + "OR "
		    + "PAC.LCT_NUMERO IN "
		    + "(SELECT NUMERO "
		    + "FROM AGH.V_RAP_PESSOA_SERVIDOR VPS, "
		    + "AGH.SCO_LICITACOES LCT "
		    + "WHERE LCT.NUMERO = PAC.LCT_NUMERO "
		    + "AND LCT.MLC_CODIGO NOT IN ('TP','CC','CV') "
		    + "AND LCT.SER_MATRICULA_GESTOR IS NULL "
		    + "AND VPS.SER_MATRICULA = LCT.SER_MATRICULA_DIGITADA "
		    + "AND VPS.NOME LIKE '%' || '"
		    + filtro.getGestor()
		    + "' || '%') "
		    + "OR "
		    + "PAC.LCT_NUMERO IN "
		    + "(SELECT NUMERO "
		    + "FROM AGH.SCO_LICITACOES LCT "
		    + "WHERE LCT.NUMERO = PAC.LCT_NUMERO "
		    + "AND MLC_CODIGO IN ('TP','CC','CV') "
		    + "AND SER_MATRICULA_GESTOR IS NULL "
		    + "AND '" + filtro.getNomeComissaoParam().getVlrTexto() + "' LIKE '%' || '" + filtro.getGestor() + "' || '%')) ";
	} else {
	    return "";
	}
    }

    private String getDataVencimentoFinalRestriction() {
	if (filtro.getDataVencimentoFinal() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT AFN.PFR_LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN AFN "
		    + "WHERE AFN.DT_VENCTO_CONTRATO <= TO_DATE('"
		    + DateUtil.obterDataFormatada(filtro.getDataVencimentoFinal(), "dd/MM/yyyy") + "', 'dd/MM/yyyy')) ";
	} else {
	    return "";
	}
    }

    private String getDataVencimentoInicialRestriction() {
	if (filtro.getDataVencimentoInicial() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT AFN.PFR_LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN AFN "
		    + "WHERE AFN.DT_VENCTO_CONTRATO >= TO_DATE('"
		    + DateUtil.obterDataFormatada(filtro.getDataVencimentoInicial(), "dd/MM/yyyy") + "', 'dd/MM/yyyy')) ";
	} else {
	    return "";
	}
    }

    private String getDataGeracaoFinalRestriction() {
	if (filtro.getDataGeracaoFinal() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT NUMERO " + "FROM AGH.SCO_LICITACOES LIC " + "WHERE NUMERO = PAC.LCT_NUMERO "
		    + "AND LIC.DT_DIGITACAO <= TO_DATE('" + DateUtil.obterDataFormatada(filtro.getDataGeracaoFinal(), "dd/MM/yyyy")
		    + "', 'dd/MM/yyyy')) ";
	} else {
	    return "";
	}
    }

    private String getDataGeracaoInicialRestriction() {
	if (filtro.getDataGeracaoInicial() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT NUMERO " + "FROM AGH.SCO_LICITACOES LIC " + "WHERE NUMERO = PAC.LCT_NUMERO "
		    + "AND LIC.DT_DIGITACAO >= TO_DATE('" + DateUtil.obterDataFormatada(filtro.getDataGeracaoInicial(), "dd/MM/yyyy")
		    + "', 'dd/MM/yyyy') )";
	} else {
	    return "";
	}
    }

    private String getPacInvestimentoRestriction() {
	if (filtro.getPacInvestimento() == DominioSimNao.S) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT PFR_LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN "
		    + "WHERE PFR_LCT_NUMERO = PAC.LCT_NUMERO " + "AND NTD_GND_CODIGO IN (" + filtro.getCodigosNaturezaParam().getVlrTexto()
		    + ")) ";
	} else {
	    return "";
	}
    }

    private String getPacEncerradoRestriction() {
	if (filtro.getPacEncerrado() == DominioSimNao.S) {
	    return "AND ((PAC.LCT_NUMERO IN " + "(SELECT PFR_LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN "
		    + "WHERE PFR_LCT_NUMERO = PAC.LCT_NUMERO) " + "AND PAC.LCT_NUMERO NOT IN " + "(SELECT PAC.LCT_NUMERO "
		    + "FROM AGH.SCO_AUTORIZACOES_FORN " + "WHERE PFR_LCT_NUMERO = PAC.LCT_NUMERO " + "AND IND_SITUACAO IN ('AE','PA'))) "
		    + "OR " + "(PAC.LCT_NUMERO NOT IN " + "(SELECT PFR_LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN "
		    + "WHERE PFR_LCT_NUMERO = PAC.LCT_NUMERO) " + "AND PAC.LCT_NUMERO IN " + "(SELECT NUMERO " + "FROM AGH.SCO_LICITACOES "
		    + "WHERE NUMERO = PAC.LCT_NUMERO " + "AND IND_EXCLUSAO = 'S'))) ";
	} else {
	    return "";
	}
    }

    private String getPacAFPendenteRestriction() {
	if (filtro.getPacAFPendente() == DominioSimNao.S) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT PAC.LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN "
		    + "WHERE PFR_LCT_NUMERO = PAC.LCT_NUMERO " + "AND IND_SITUACAO IN ('AE','PA')) ";
	} else {
	    return "";
	}
    }

    private String getPacIncompletoRestriction() {
	if (filtro.getPacIncompleto() == DominioSimNao.S) {
	    return "AND PAC.LCT_NUMERO IN ( " + "SELECT ITL.LCT_NUMERO " + "FROM AGH.SCO_ITENS_LICITACOES ITL "
		    + "WHERE ITL.LCT_NUMERO = PAC.LCT_NUMERO " + "AND ITL.IND_EXCLUSAO = 'N' " + "AND NOT EXISTS ( " + "SELECT 1 "
		    + "FROM AGH.SCO_ITENS_AUTORIZACAO_FORN IAF, " + "AGH.SCO_ITEM_PROPOSTAS_FORNECEDOR IPF "
		    + "WHERE IPF.PFR_LCT_NUMERO = ITL.LCT_NUMERO " + "AND IPF.ITL_NUMERO = ITL.NUMERO "
		    + "AND IAF.IPF_PFR_LCT_NUMERO = IPF.PFR_LCT_NUMERO " + "AND IAF.IPF_NUMERO = IPF.NUMERO)) ";
	} else {
	    return "";
	}
    }

    private String getPacAFRestriction() {
	if (filtro.getPacAF() == DominioSimNao.S) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT PFR_LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN AFN "
		    + "WHERE PFR_LCT_NUMERO = PAC.LCT_NUMERO) ";
	} else {
	    return "";
	}
    }

    private String getCentroCustoAplicacaoRestriction() {
	if (filtro.getCentroCustoAplicacao() != null) {
	    return "AND (PAC.LCT_NUMERO IN " + "(SELECT ITL_LCT_NUMERO " + "FROM AGH.SCO_FASES_SOLICITACOES FSC, "
		    + "AGH.SCO_SOLICITACOES_DE_COMPRAS SLC " + "WHERE SLC.CCT_CODIGO_E_APLICADA = "
		    + filtro.getCentroCustoAplicacao().getCodigo() + " " + "AND FSC.SLC_NUMERO = SLC.NUMERO) " + "OR "
		    + "PAC.LCT_NUMERO IN " + "(SELECT ITL_LCT_NUMERO " + "FROM AGH.SCO_FASES_SOLICITACOES FSC, "
		    + "AGH.SCO_SOLICITACOES_SERVICO SLS " + "WHERE SLS.CCT_CODIGO_APLICADA = "
		    + filtro.getCentroCustoAplicacao().getCodigo() + " " + "AND FSC.SLS_NUMERO = SLS.NUMERO)) ";
	} else {
	    return "";
	}
    }

    private String getCentroCustoSolicitanteRestriction() {
	if (filtro.getCentroCustoSolicitante() != null) {
	    return "AND (PAC.LCT_NUMERO IN " + "(SELECT ITL_LCT_NUMERO " + "FROM AGH.SCO_FASES_SOLICITACOES FSC, "
		    + "AGH.SCO_SOLICITACOES_DE_COMPRAS SLC " + "WHERE SLC.CCT_CODIGO = " + filtro.getCentroCustoSolicitante().getCodigo()
		    + " " + "AND FSC.SLC_NUMERO = SLC.NUMERO) " + "OR " + "PAC.LCT_NUMERO IN " + "(SELECT ITL_LCT_NUMERO "
		    + "FROM AGH.SCO_FASES_SOLICITACOES FSC, " + "AGH.SCO_SOLICITACOES_SERVICO SLS " + "WHERE SLS.CCT_CODIGO = "
		    + filtro.getCentroCustoSolicitante().getCodigo() + " " + "AND FSC.SLS_NUMERO = SLS.NUMERO)) ";
	} else {
	    return "";
	}
    }

    private String getMarcaRestriction() {
	if (filtro.getMarcaComercial() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT AFN.PFR_LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN AFN, "
		    + "AGH.SCO_ITENS_AUTORIZACAO_FORN IAF " + "WHERE IAF.MCM_CODIGO = " + filtro.getMarcaComercial().getCodigo() + " "
		    + "AND AFN.NUMERO = IAF.AFN_NUMERO) ";
	} else {
	    return "";
	}
    }

    private String getServicoRestriction() {
	if (filtro.getServico() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT ITL_LCT_NUMERO " + "FROM AGH.SCO_FASES_SOLICITACOES FSC, "
		    + "AGH.SCO_SOLICITACOES_SERVICO SLS " + "WHERE SLS.SRV_CODIGO = " + filtro.getServico().getCodigo() + " "
		    + "AND FSC.SLS_NUMERO = SLS.NUMERO) ";
	} else {
	    return "";
	}
    }

    private String getMaterialRestriction() {
	if (filtro.getMaterial() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT ITL_LCT_NUMERO " + "FROM AGH.SCO_FASES_SOLICITACOES FSC, "
		    + "AGH.SCO_SOLICITACOES_DE_COMPRAS SLC " + "WHERE SLC.MAT_CODIGO = " + filtro.getMaterial().getCodigo() + " "
		    + "AND FSC.SLC_NUMERO = SLC.NUMERO) ";
	} else {
	    return "";
	}
    }

    private String getGrupoMaterialRestriction() {
	if (filtro.getGrupoMaterial() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT ITL_LCT_NUMERO " + "FROM AGH.SCO_FASES_SOLICITACOES FSC, "
		    + "AGH.SCO_SOLICITACOES_DE_COMPRAS SLC, " + "AGH.SCO_MATERIAIS MAT " + "WHERE MAT.GMT_CODIGO = "
		    + filtro.getGrupoMaterial().getCodigo() + " " + "AND SLC.MAT_CODIGO = MAT.CODIGO "
		    + "AND FSC.SLC_NUMERO = SLC.NUMERO) ";
	} else {
	    return "";
	}
    }

    private String getSsRestriction() {
	if (filtro.getSs() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT ITL_LCT_NUMERO " + "FROM AGH.SCO_FASES_SOLICITACOES FSC "
		    + "WHERE FSC.SLS_NUMERO = " + filtro.getSs() + ") ";
	} else {
	    return "";
	}
    }

    private String getScRestriction() {
	if (filtro.getSc() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT ITL_LCT_NUMERO " + "FROM AGH.SCO_FASES_SOLICITACOES FSC "
		    + "WHERE FSC.SLC_NUMERO = " + filtro.getSc() + ") ";
	} else {
	    return "";
	}
    }

    private String getNumeroAFRestriction() {
	StringBuffer retorno = new StringBuffer(200);
	if (filtro.getNumeroAF() != null) {
	    retorno.append("AND PAC.LCT_NUMERO IN " + "(SELECT PFR_LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN AFN "
		    + "WHERE AFN.PFR_LCT_NUMERO = " + filtro.getNumeroAF().toString() + " ");

	    retorno.append(getComplementoRestriction());
	}
	return retorno.toString();
    }

    private String getComplementoRestriction() {
	if (filtro.getComplemento() != null) {
	    return "AND AFN.NRO_COMPLEMENTO = " + filtro.getComplemento() + ") ";
	} else {
	    return ") ";
	}
    }

    private String getFornecedorRestriction() {
	if (filtro.getFornecedor() != null) {
	    return "AND PAC.LCT_NUMERO IN  " + "(SELECT PFR_LCT_NUMERO FROM AGH.SCO_AUTORIZACOES_FORN AFN WHERE AFN.PFR_FRN_NUMERO = "
		    + filtro.getFornecedor().getNumero() + ") ";
	} else {
	    return "";
	}
    }

    private String getObjetoRestriction() {
	if (filtro.getObjeto() != null && !filtro.getObjeto().isEmpty()) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT NUMERO " + "FROM AGH.SCO_LICITACOES LIC " + "WHERE NUMERO = PAC.LCT_NUMERO "
		    + "AND LIC.DESCRICAO LIKE '%' || '" + filtro.getObjeto() + "' || '%') ";
	} else {
	    return "";
	}
    }

    private String getLocalRestriction() {
	if (filtro.getLocal() != null) {
	    return "AND PAC.LCP_CODIGO = '" + filtro.getLocal().getCodigo() + "' ";
	} else {
	    return "";
	}
    }

    private String getTipoRestriction() {
	if (filtro.getTipo() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT NUMERO " + "FROM AGH.SCO_LICITACOES LIC " + "WHERE NUMERO = PAC.LCT_NUMERO "
		    + "AND LIC.TIPO = '" + filtro.getTipo() + "') ";
	} else {
	    return "";
	}
    }

    private String getModalidadeLicitacaoRestriction() {
	if (filtro.getModalidadeLicitacao() != null) {
	    return "AND PAC.LCT_NUMERO IN " + "(SELECT NUMERO " + "FROM AGH.SCO_LICITACOES LIC " + "WHERE NUMERO = PAC.LCT_NUMERO "
		    + "AND LIC.MLC_CODIGO = '" + filtro.getModalidadeLicitacao().getCodigo() + "' ) ";
	} else {
	    return "";
	}
    }

    private String getPendenteRestriction() {
	if (filtro.getPendente() == true) {
	    return "AND  DT_SAIDA IS NULL " + "AND  PAC.LCT_NUMERO IN " + "(SELECT NUMERO " + "FROM AGH.SCO_LICITACOES "
		    + "WHERE NUMERO = PAC.LCT_NUMERO " + "AND IND_EXCLUSAO  = 'N') " + "AND (PAC.LCT_NUMERO NOT IN "
		    + "(SELECT PFR_LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN " + "WHERE PFR_LCT_NUMERO = PAC.LCT_NUMERO) " + "OR "
		    + "(PAC.LCT_NUMERO IN " + "(SELECT PFR_LCT_NUMERO " + "FROM AGH.SCO_AUTORIZACOES_FORN "
		    + "WHERE PFR_LCT_NUMERO = PAC.LCT_NUMERO " + "AND IND_SITUACAO  IN ('AE','PA')))) ";
	} else {
	    return "";
	}
    }

    private String getNumeroPacRestriction() {
	if (filtro.getNumeroPac() != null) {
	    return "AND PAC.LCT_NUMERO = " + filtro.getNumeroPac() + " ";
	} else {
	    return "";
	}
    }

    public void setFiltro(ConsultarAndamentoProcessoCompraVO filtro) {
	this.filtro = filtro;
    }

    public ConsultarAndamentoProcessoCompraVO getFiltro() {
	return filtro;
    }

}
