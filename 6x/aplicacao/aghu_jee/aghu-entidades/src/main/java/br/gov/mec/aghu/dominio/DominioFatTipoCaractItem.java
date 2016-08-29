package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * <p>
 * Gerado via Bash com a seguinte linha:<br/>
 * <code>
 * cat list.txt | while read STR; do E=${STR//ç/c}; E=${E//ê/e}; E=${E//ã/a}; E=${E//ó/o}; E=${E^^}; E=${E// /_}; E=${E//.}; echo "$E(\"$STR\"),"; done
 * </code>
 * </p>
 * @author gandriotti
 *
 */
public enum DominioFatTipoCaractItem implements Dominio {
	
	PARTO("PARTO"),
	QTD_MAXIMA_COBRAVEL_APAC("Qtd Maxima Cobravel APAC"),
	COBRA_EXCEDENTE_BPA("Cobra Excedente BPA"),
	COBRA_APAC("Cobra APAC"),
	MODO_LANCAMENTO_FAT("Modo Lancamento FAT"),
	PROCEDIMENTO_PRINCIPAL_APAC("Procedimento Principal APAC"),
	BIMESTRAL_A_PARTIR_13_MESES("Bimestral a partir 13 meses"),
	TRANSPLANTE("Transplante"),
	COBRA_BPA("Cobra BPA"),
	COBRA_SISCOLO("Cobra SISCOLO"),
	COBRA_EM_CODIGO_REPRES_QTDE("Cobra em Codigo Repres Qtde"),
	QUANTIDADE_APACS_ANO("Quantidade Apacs Ano"),
	COMPLEXIDADE("Complexidade"),
	NAO_COBRA_SE_HOUVER_TRAT_AMB("Nao cobra se houver trat. amb."),
	HOSPITAL_DIA_CIRURGICO("Hospital dia cirurgico"),
	CODIGO_TIPO_APAC("Codigo Tipo APAC"),
	CID_FIXO_APEX("Cid fixo APEX"),
	QUANTIDADE_PARA_TRATAMENTO("Quantidade para tratamento"),
	DIARIA_UTI("Diaria UTI"),
	ACTP("ACTP"),
	QTD_MAXIMA_ANO_ADULTO("Qtd Maxima Ano Adulto"),
	QTD_MAXIMA_ANO_INFANTIL("Qtd Maxima Ano Infantil"),
	APARELHO_AUDITIVO("Aparelho Auditivo"),
	PROCEDIMENTO_SECUNDARIO_APAC("Procedimento Secundario APAC"),
	REPOSICAO_APARELHO_AUDITIVO("Reposição Aparelho Auditivo"),
	CANCELA_EXAMES_RELACIONADOS("Cancela exames relacionados"),
	QUANTIDADE_MINIMA_GRUPO("Quantidade minima grupo"),
	NRO_MAXIMO_DIAS_NA_CONTA("Nro Maximo Dias na Conta"),
	PROCEDIMENTO_EMERGENCIA("Procedimento Emergencia"),
	EXIGE_METASTASE("Exige metastase"),
	DCIH_MUTIRAO("DCIH Mutirão"),
	CAMPOS_PLANEJADOS("Campos Planejados"),
	FATURA_ESPECIALIDADE_NEFRO("Fatura Especialidade Nefro"),
	COBRA_EM_AIH("Cobra Em AIH"),
	COBRA_BPI("Cobra BPI"),
	CLINICA_ADICIONAL_DO_SSM("Clinica Adicional do SSM"),
	CALCULA_IDADE_BPA("Calcula idade BPA"),
	EXIGE_CNS_PACIENTE("Exige CNS paciente"),
	NAO_CONSISTE_CBO("Nao consiste CBO"),
	QTD_MAXIMA_COBRAVEL_BPI("Qtd Maxima Cobravel BPI"),
	QTD_MAXIMA_COBRAVEL_BPI_MES("Qtd Maxima Cobravel BPI mês"),
	EXIGE_CID("Exige Cid"),
	LANCAR_AMBULATORIO("Lançar ambulatório"),
	POSSUI_INDICADOR_DE_EQUIPE("Possui indicador de equipe"),
	NAO_VALIDA_CBO("Nao Valida CBO"),
	ADMITE_LIBERACAO_DE_QTD("Admite liberação de Qtd"),
	DEVE_SER_AUTORIZADO_PELA_SMS("Deve ser autorizado pela SMS"),
	EXIGE_CNRAC("Exige CNRAC"),
	EXIGE_CERIH_INTERNACAO("Exige CERIH Internação"),
	COBRA_DIARIA_POR_ITEM_LANCADO("Cobra diaria por item lançado"),	
	FATURAMENTO_MULTIPLOS_PROCEDIMENTOS_CIRURGICOS("Faturamento de multiplos procedimentos cirurgicos"),
	GERA_SEQUENCIA_POR_COMPATIBILIDADE_DO_PRINCIPAL("Gera sequência por compatibilidade do principal"),
	NAO_GERA_SLCT_LAUDO_AIH("Não gera sltc de laudo AIH"),
	NAO_SEPARA_COMPETENCIA("Nao separa por competencia");
	
	private final String descricao; 
	
	private DominioFatTipoCaractItem(String descricao) {

		this.descricao = descricao;
	}

	@Override
	public int getCodigo() {

		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		return this.descricao;
	}

}
