package br.gov.mec.aghu.exames.business;

import java.io.Serializable;


public class DominioVariaveisEmitirRelatorioExamesPaciente implements Serializable{

    
    /**
     * 
     */
    private static final long serialVersionUID = -2351515271215372437L;

    public static final String V_UFE_UMA_MAN_SEQ_ANT = "vUfeUmaManSeqAnt";

    public static final String V_UFE_UNF_SEQ_ANT = "vUfeUnfSeqAnt";

    public static final String V_DTHR_EVENTO_LIB_ANT = "vDthrEventoLibAnt";

    public static final String V_PERTENCE_SUMARIO_ANT = "vPertenceSumarioAnt";

    public static final String V_RECEM_NASCIDO_ANT = "vRecemNascidoAnt";

    public static final String V_PRONTUARIO_ANT = "vProntuarioAnt";

    public static final String V_ORDEM_AGRUP = "vOrdemAgrup";

    public static final String V_EST_EXAME_MASC = "vEstExameMasc";

    public static final String V_ISE_SEQ_DEPT = "vIseSeqDept";

    public static final String V_SOE_SEQP_DEPT_ANT = "vSoeSeqpDeptAnt";

    public static final String V_UNF_SEQ_CONTROLE = "vUnfSeqControle";

    public static final String V_DTHR_FIM_EXAMES_TAB = "vDthrFimExamesTab";

    public static final String V_IND_IMPRIME = "vIndImprime";

    public static final String V_ORDEM_EXAMES_TAB = "vOrdemExamesTab";

    public static final String V_DTHR_EVENTO_AREA_EXEC = "vDthrEventoAreaExec";

    public static final String V_DESCRICAO_EXAMES_TAB = "vDescricaoExamesTab";

    public static final String V_RCD_SEQ = "vRcdSeq";

    public static final String V_RCD_GTC_SEQ = "vRcdGtcSeq";

    public static final String V_CAL_NOME_SUMARIO = "vCalNomeSumario";

    public static final String V_CAL_NOME = "vCalNome";

    public static final String V_VALOR = "vValor";

    public static final String V_CAC_SEQ = "vCacSeq";

    public static final String V_CAL_SEQ = "vCalSeq";

    public static final String V_DTHR_FIM_EXAMES_MASC = "vDthrFimExamesMasc";

    public static final String V_DESCRICAO_EXAMES_MASC = "vDescricaoExamesMasc";

    public static final String LISTA_AEL_POL_SUM_MASC_CAMPO_EDIT = "listaAelPolSumMascCampoEdit";

    public static final String LISTA_AEL_POL_SUM_LEGENDA = "listaAelPolSumLegenda";

    public static final String LISTA_AEL_POL_SUM_OBSERVACAO = "listaAelPolSumObservacao";

    public static final String V_COD_OBSERVACAO = "vCodObservacao";

    public static final String V_RESULTADO_AUX = "vResultadoAux";

    public static final String V_POSICAO_LINHA_IMP = "vPosicaoLinhaImp";

    public static final String V_NRO_LINHA = "vNroLinha";

    public static final String V_NRO_CAMPO = "vNroCampo";

    public static final String V_RESULTADO_COM_MASCARA = "vResultadoComMascara";

    public static final String V_VERSAO_SEQP = "vVersaoSeqp";

    public static final String V_VERSAO_MAN_SEQ = "vVersaoManSeq";

    public static final String V_VERSAO_EXA_SIGLA = "vVersaoExaSigla";

    public static final String V_SOE_SEQ_DEPT_ANT = "vSoeSeqDeptAnt";

    public static final String V_SOE_SEQ_ANT = "vSoeSeqAnt";

    public static final String V_EST_CAMPO_EDIT = "vEstCampoEdit";

    public static final String V_DESCRICAO_PAI = "vDescricaoPai";

    public static final String V_SEQ_DEPT_ANT = "vSeqDeptAnt";

    public static final String V_EST_ITENS_ATU = "vEstItensAtu";

    public static final String V_EST_EXAME_TAB = "vEstExameTab";

    public static final String V_LTO_LTO_ID = "vLtoLtoId";

    public static final String V_EST_LEGENDA = "vEstLegenda";

    public static final String V_EST_LV_NRO_TAB_POR_PRNTEGENDA = "vEstLvNroTabPorPrntegenda";

    public static final String V_RESULTADO_TAB = "vResultadoTab";

    public static final String V_NRO_TAB_POR_PRNTV_LEGENDA = "vNroTabPorPrntvLegenda";

    public static final String V_LEGENDA = "vLegenda";

    public static final String V_OBSERVACAO = "vObservacao";

    public static final String V_DESCRICAO_NOTAS_ADICIONAIS = "vDescricaoNotasAdicionais";

    public static final String V_DESCRICAO_RESULTADO = "vDescricaoResultado";

    public static final String V_DESCRICAO_CODIFICADO = "vDescricaoCodificado";

    public static final String V_DESCRICAO_CARACTERISTICA = "vDescricaoCaracteristica";

    public static final String LISTA_AEL_POL_SUM_EXAME_TAB = "listaAelPolSumExameTab";

    public static final String LISTA_AEL_POL_SUM_EXAME_MASC = "listaAelPolSumExameMasc";

    public static final String LISTA_AEL_POL_SUM_MASC_LINHA = "listaAelPolSumMascLinha";

    public static final String V_UFE_EMA_MAN_SEQ_ANT = "vUfeEmaManSeqAnt";

    public static final String V_PERTENCE_SUMARIO = "vPertenceSumario";

    public static final String V_UFE_UNF_SEQ = "vUfeUnfSeq";

    public static final String V_DTHR_EVENTO_LIB = "vDthrEventoLib";

    public static final String V_RECEM_NASCIDO = "vRecemNascido";

    public static final String V_SOE_SEQP_ANT = "vSoeSeqpAnt";

    public static final String V_SEQP_ANT = "vSeqpAnt";

    public static final String V_EST_MASC_LINHA = "vEstMascLinha";

    public static final String V_ORDEM_REL = "vOrdemRel";

    public static final String V_PRNT_CONTR_FIM = "vPrntContrFim";

    public static final String V_PRNT_CONTROLE = "vPrntControle";

    public static final String V_NRO_MASC_POR_PRNT = "vNroMascPorPrnt";

    public static final String V_NRO_TAB_POR_PRNT = "vNroTabPorPrnt";
    
    public static final String V_REE_VALOR = "vReeValor";
}
