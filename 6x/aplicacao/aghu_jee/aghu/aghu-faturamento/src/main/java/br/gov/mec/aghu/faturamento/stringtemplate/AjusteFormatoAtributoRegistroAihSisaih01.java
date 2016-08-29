package br.gov.mec.aghu.faturamento.stringtemplate;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.AjusteFormatoAtributoRegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihAcidenteTrabalho;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihComum;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihDadosOpm;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihDocumentoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihEnderecoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihEspecificacao;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihIdentificacaoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihInternacao;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihParto;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihProcedimentosSecundariosEspeciais;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihRegistroCivil;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihUtiNeonatal;

public class AjusteFormatoAtributoRegistroAihSisaih01
		implements
			AjusteFormatoAtributoRegistroAih {

	private static final String MASCARA_3N = "#3N";
	private static final String MASCARA_20D = "#20d";
	private static final String MASCARA_20S_ = "#20S";
	private static final String MASCARA_8S = "#8s";
	private static final String MASCARA_20S = "#20s";
	private static final String MASCARA_32D = "#32d";
	private static final String MASCARA_11D = "#11d";
	private static final String MASCARA_4N = "#4N";
	private static final String MASCARA_4D = "#4d";
	private static final String MASCARA_6S = "#6s";
	private static final String MASCARA_14D = "#14d";
	private static final String MASCARA_8T = "#8t";
	private static final String MASCARA_10D = "#10d";
	private static final String MASCARA_1D = "#1d";
	private static final String MASCARA_15D = "#15d";
	private static final String MASCARA_4S = "#4s";
	private static final String MASCARA_70S = "#70s";
	private static final String MASCARA_2D = "#2d";
	private static final String MASCARA_13D = "#13d";
	private static final String MASCARA_6D = "#6d";
	private static final String MASCARA_7D = "#7d";
	private static final String MASCARA_10N = "#10N";
	private static final String MASCARA_6T = "#6t";
	private static final String MASCARA_8D = "#8d";
	private static final String MASCARA_3D = "#3d";
	private final static AjusteFormatoAtributoRegistroAihSisaih01 instance = new AjusteFormatoAtributoRegistroAihSisaih01();

	protected static void ajustarFormato(final RegistroAihComum reg) {

		reg.setFormat(RegistroAihComum.NomeAtributoTemplate.NU_LOTE, MASCARA_8D);
		reg.setFormat(RegistroAihComum.NomeAtributoTemplate.QT_LOTE, MASCARA_3D);
		reg.setFormat(RegistroAihComum.NomeAtributoTemplate.APRES_LOTE, MASCARA_6T);
		reg.setFormat(RegistroAihComum.NomeAtributoTemplate.SEQ_LOTE, MASCARA_3D);
		reg.setFormat(RegistroAihComum.NomeAtributoTemplate.ORG_EMIS_AIH, MASCARA_10N);
		reg.setFormat(RegistroAihComum.NomeAtributoTemplate.CNES_HOSP, MASCARA_7D);
		reg.setFormat(RegistroAihComum.NomeAtributoTemplate.MUN_HOSP, MASCARA_6D);
		reg.setFormat(RegistroAihComum.NomeAtributoTemplate.NU_AIH, MASCARA_13D);
		reg.setFormat(RegistroAihComum.NomeAtributoTemplate.IDENT_AIH, MASCARA_2D);
		reg.setFormat(RegistroAihComum.NomeAtributoTemplate.ESPEC_AIH, MASCARA_2D);
	}

	protected static void ajustarFormato(final RegistroAihEspecificacao reg) {

		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.MOD_INTERN, MASCARA_2D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.SEQ_AIH5, MASCARA_3D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.AIH_PROX, MASCARA_13D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.AIH_ANT, MASCARA_13D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DT_EMISSAO, MASCARA_8T);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DT_INTERN, MASCARA_8T);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DT_SAIDA, MASCARA_8T);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.PROC_SOLICITADO, MASCARA_10D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.ST_MUDAPROC, MASCARA_1D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.PROC_REALIZADO, MASCARA_10D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.CAR_INTERN, MASCARA_2D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.MOT_SAIDA, MASCARA_2D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.IDENT_MED_SOL, MASCARA_1D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DOC_MED_SOL, MASCARA_15D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.IDENT_MED_RESP, MASCARA_1D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DOC_MED_RESP, MASCARA_15D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.IDENT_DIRCLINICO, MASCARA_1D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DOC_DIRCLINICO, MASCARA_15D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.IDENT_AUTORIZ, MASCARA_1D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DOC_AUTORIZ, MASCARA_15D);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DIAG_PRIN, MASCARA_4S);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DIAG_SEC, MASCARA_4S);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DIAG_COMPL, MASCARA_4S);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.DIAG_OBITO, MASCARA_4S);
		reg.setFormat(RegistroAihEspecificacao.NomeAtributoTemplate.COD_SOL_LIB, MASCARA_3N);
	}

	protected static void ajustarFormato(final RegistroAihIdentificacaoPaciente reg) {

		reg.setFormat(RegistroAihIdentificacaoPaciente.NomeAtributoTemplate.NM_PACIENTE, MASCARA_70S);
		reg.setFormat(RegistroAihIdentificacaoPaciente.NomeAtributoTemplate.DT_NASC_PAC, MASCARA_8T);
		reg.setFormat(RegistroAihIdentificacaoPaciente.NomeAtributoTemplate.SEXO_PAC, "#1s");
		reg.setFormat(RegistroAihIdentificacaoPaciente.NomeAtributoTemplate.RACA_COR, "#2N");
		reg.setFormat(RegistroAihIdentificacaoPaciente.NomeAtributoTemplate.NM_MAE_PAC, MASCARA_70S);
		reg.setFormat(RegistroAihIdentificacaoPaciente.NomeAtributoTemplate.NM_RESP_PAC, MASCARA_70S);
		reg.setFormat(RegistroAihIdentificacaoPaciente.NomeAtributoTemplate.TP_DOC_PAC, MASCARA_1D);
		reg.setFormat(RegistroAihIdentificacaoPaciente.NomeAtributoTemplate.ETNIA_INDIGENA, MASCARA_4D);
		reg.setFormat(RegistroAihIdentificacaoPaciente.NomeAtributoTemplate.NU_CNS, MASCARA_15D);
		reg.setFormat(RegistroAihIdentificacaoPaciente.NomeAtributoTemplate.NAC_PAC, MASCARA_3D);
	}

	protected static void ajustarFormato(final RegistroAihEnderecoPaciente reg) {

		reg.setFormat(RegistroAihEnderecoPaciente.NomeAtributoTemplate.TP_LOGRADOURO, MASCARA_3D);
		reg.setFormat(RegistroAihEnderecoPaciente.NomeAtributoTemplate.LOGR_PAC, "#50s");
		reg.setFormat(RegistroAihEnderecoPaciente.NomeAtributoTemplate.NU_END_PAC, MASCARA_7D);
		reg.setFormat(RegistroAihEnderecoPaciente.NomeAtributoTemplate.COMPL_END_PAC, "#15s");
		reg.setFormat(RegistroAihEnderecoPaciente.NomeAtributoTemplate.BAIRRO_PAC, "#30s");
		reg.setFormat(RegistroAihEnderecoPaciente.NomeAtributoTemplate.COD_MUN_END_PAC, MASCARA_6D);
		reg.setFormat(RegistroAihEnderecoPaciente.NomeAtributoTemplate.UF_PAC, "#2s");
		reg.setFormat(RegistroAihEnderecoPaciente.NomeAtributoTemplate.CEP_PAC, MASCARA_8D);
	}

	protected static void ajustarFormato(final RegistroAihInternacao reg) {

		reg.setFormat(RegistroAihInternacao.NomeAtributoTemplate.NU_PRONTUARIO, MASCARA_15D);
		reg.setFormat(RegistroAihInternacao.NomeAtributoTemplate.NU_ENFERMARIA, MASCARA_4N);
		reg.setFormat(RegistroAihInternacao.NomeAtributoTemplate.NU_LEITO, MASCARA_4N);
	}

	protected static void ajustarFormato(final RegistroAihProcedimentosSecundariosEspeciais reg) {

		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.IN_PROF, MASCARA_1D);
		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.IDENT_PROF, MASCARA_15D);
		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.CBO_PROF, MASCARA_6D);
		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.IN_EQUIPE, MASCARA_1D);
		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.IN_SERVICO, MASCARA_1D);
		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.IDENT_SERVICO, MASCARA_14D);
		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.IN_EXECUTOR, MASCARA_1D);
		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.IDENT_EXECUTOR, MASCARA_15D);
		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.COD_PROCED, MASCARA_10D);
		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.QTD_PROCED, MASCARA_3D);
		reg.setFormat(RegistroAihProcedimentosSecundariosEspeciais.NomeAtributoTemplate.CMPT, MASCARA_6S);
	}

	protected static void ajustarFormato(final RegistroAihUtiNeonatal reg) {

		reg.setFormat(RegistroAihUtiNeonatal.NomeAtributoTemplate.SAIDA_UTINEO, MASCARA_1D);
		reg.setFormat(RegistroAihUtiNeonatal.NomeAtributoTemplate.PESO_UTINEO, MASCARA_4D);
		reg.setFormat(RegistroAihUtiNeonatal.NomeAtributoTemplate.MESGEST_UTINEO, MASCARA_1D);
	}

	protected static void ajustarFormato(final RegistroAihAcidenteTrabalho reg) {

		reg.setFormat(RegistroAihAcidenteTrabalho.NomeAtributoTemplate.CNPJ_EMPREG, MASCARA_14D);
		reg.setFormat(RegistroAihAcidenteTrabalho.NomeAtributoTemplate.CBOR, MASCARA_6D);
		reg.setFormat(RegistroAihAcidenteTrabalho.NomeAtributoTemplate.CNAER, MASCARA_3D);
		reg.setFormat(RegistroAihAcidenteTrabalho.NomeAtributoTemplate.TP_VINCPREV, MASCARA_1D);
	}

	protected static void ajustarFormato(final RegistroAihParto reg) {

		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.QT_VIVOS, MASCARA_1D);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.QT_MORTOS, MASCARA_1D);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.QT_ALTA, MASCARA_1D);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.QT_TRANSF, MASCARA_1D);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.QT_OBITO, MASCARA_1D);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.QT_FILHOS, MASCARA_2D);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.GRAU_INSTRU, MASCARA_1D);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.CID_INDICACAO, MASCARA_4N);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.TP_CONTRACEP1, MASCARA_2D);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.TP_CONTRACEP2, MASCARA_2D);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.ST_GESTRISCO, MASCARA_1D);
		reg.setFormat(RegistroAihParto.NomeAtributoTemplate.NU_PRENATAL, MASCARA_11D);
	}

	protected static void ajustarFormato(final RegistroAihDocumentoPaciente reg) {

		reg.setFormat(RegistroAihDocumentoPaciente.NomeAtributoTemplate.NU_DOC_PAC, MASCARA_32D);
	}

	protected static void ajustarFormato(final RegistroAihRegistroCivil reg) {

		reg.setFormat(RegistroAihRegistroCivil.NomeAtributoTemplate.NUMERO_DN, MASCARA_11D);
		reg.setFormat(RegistroAihRegistroCivil.NomeAtributoTemplate.NOME_RN, MASCARA_70S);
		reg.setFormat(RegistroAihRegistroCivil.NomeAtributoTemplate.RS_CART, MASCARA_20S);
		reg.setFormat(RegistroAihRegistroCivil.NomeAtributoTemplate.LIVRO_RN, MASCARA_8S);
		reg.setFormat(RegistroAihRegistroCivil.NomeAtributoTemplate.FOLHA_RN, MASCARA_4D);
		reg.setFormat(RegistroAihRegistroCivil.NomeAtributoTemplate.TERMO_RN, MASCARA_8D);
		reg.setFormat(RegistroAihRegistroCivil.NomeAtributoTemplate.DT_EMIS_RN, MASCARA_8T);
		reg.setFormat(RegistroAihRegistroCivil.NomeAtributoTemplate.LINHA, MASCARA_3D);
		reg.setFormat(RegistroAihRegistroCivil.NomeAtributoTemplate.MATRICULA, MASCARA_32D);
	}

	protected static void ajustarFormato(final RegistroAihDadosOpm reg) {

		reg.setFormat(RegistroAihDadosOpm.NomeAtributoTemplate.COD_OPM, MASCARA_10D);
		reg.setFormat(RegistroAihDadosOpm.NomeAtributoTemplate.LINHA, MASCARA_3D);
		reg.setFormat(RegistroAihDadosOpm.NomeAtributoTemplate.REG_ANVISA, MASCARA_20S_);
		reg.setFormat(RegistroAihDadosOpm.NomeAtributoTemplate.SERIE, MASCARA_20S_);
		reg.setFormat(RegistroAihDadosOpm.NomeAtributoTemplate.LOTE, MASCARA_20S_);
		reg.setFormat(RegistroAihDadosOpm.NomeAtributoTemplate.NOTA_FISCAL, MASCARA_20D);
		reg.setFormat(RegistroAihDadosOpm.NomeAtributoTemplate.CNPJ_FORN, MASCARA_14D);
		reg.setFormat(RegistroAihDadosOpm.NomeAtributoTemplate.CNPJ_FABRIC, MASCARA_14D);
	}

	private AjusteFormatoAtributoRegistroAihSisaih01() {

		super();
	}

	@Override
	public void ajustarFormatoAtributos(final RegistroAih registro) {

		if (registro instanceof RegistroAihComum) {
			ajustarFormato((RegistroAihComum) registro);
		} else if (registro instanceof RegistroAihEspecificacao) {
			ajustarFormato((RegistroAihEspecificacao) registro);
		} else if (registro instanceof RegistroAihIdentificacaoPaciente) {
			ajustarFormato((RegistroAihIdentificacaoPaciente) registro);
		} else if (registro instanceof RegistroAihEnderecoPaciente) {
			ajustarFormato((RegistroAihEnderecoPaciente) registro);
		} else if (registro instanceof RegistroAihInternacao) {
			ajustarFormato((RegistroAihInternacao) registro);
		} else if (registro instanceof RegistroAihProcedimentosSecundariosEspeciais) {
			ajustarFormato((RegistroAihProcedimentosSecundariosEspeciais) registro);
		} else if (registro instanceof RegistroAihUtiNeonatal) {
			ajustarFormato((RegistroAihUtiNeonatal) registro);
		} else if (registro instanceof RegistroAihAcidenteTrabalho) {
			ajustarFormato((RegistroAihAcidenteTrabalho) registro);
		} else if (registro instanceof RegistroAihParto) {
			ajustarFormato((RegistroAihParto) registro);
		} else if (registro instanceof RegistroAihDocumentoPaciente) {
			ajustarFormato((RegistroAihDocumentoPaciente) registro);
		} else if (registro instanceof RegistroAihRegistroCivil) {
			ajustarFormato((RegistroAihRegistroCivil) registro);
		} else if (registro instanceof RegistroAihDadosOpm) {
			ajustarFormato((RegistroAihDadosOpm) registro);
		} else {
			throw new IllegalArgumentException("Registro do tipo: " + registro.getClass().getSimpleName() + " nao suportado");
		}
	}

	public static AjusteFormatoAtributoRegistroAih getInstance() {

		return instance;
	}
}
