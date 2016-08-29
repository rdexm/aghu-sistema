package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;

/**
 * <p>
 * Estrutuar como especificado em <a href="http://redmine.mec.gov.br/projects/aghu/repository/changes/documentos/Analise/Sprint36/LaranjaMecanica/2179/2179-GerarArquivoContasPeriodo.doc">http://redmine.mec.gov.br/projects/aghu/repository/changes/documentos/Analise/Sprint36/LaranjaMecanica/2179/2179-GerarArquivoContasPeriodo.doc</a><br/>
 * Acessado em: <code>06.06.2011</code>
 * </p>
 * @author gandriotti
 *
 */

public class RegistroCsvContasPeriodo
		extends
			AbstractRegistroCsv {

	protected static final String NOME_TEMPLATE = "csv_linha_ponto_virgula";
	private static final String MAGIC_STRING_NULL_VALUES_EQ_EMPTY = "";
	public static final String MAGIC_STRING_BOOLEAN_FALSE_EQ_N = "N";
	public static final String MAGIC_STRING_BOOLEAN_TRUE_EQ_S = "S";
	public static final String STRING_FORMAT_DD_MM_YYYY = "%1$td/%1$tm/%1$tY";
	public static final String STRING_FORMAT_DD_MM_YYYY_HH_MM = "%1$td/%1$tm/%1$tY %1$tH:%1$tM";

	protected enum TitulosRegistrosEnum implements
			CamposEnum {

		CONTA("Conta"),
		PRONTUARIO("Prontuario"),
		NOME("Nome"),
		SITUACAO("Situacao"),
		UNF("Unidade Funcional"),
		LEITO("Leito"),
		AIH("AIH"),
		CMCE("CMCE"),
		DESDOBRAMENTO("Desd"),
		DATA_INT("Data Internação"),
		DATA_ALTA("Data Alta"),
		DATA_SMS("Data Envio SMS"),
		CARACTER_INTERNACAO("Carater Internação"),
		FORA_EST("Fora do Estado"),
		ESP_CIRRUR("Esp Cirurgica"),
		COBRA_AIH("Cobra AIH"),
		COMPLEX("Complexidade"),
		SIT_SMS("Situaçao SMS"),
		EXCLUSAO_CRIT("Exclusão de Critica"),
		SOLIC("Solicitado"),
		REALIZADO("Realizado"),
		MSG_ERRO("Msg Erro"),
		TOTAL("Valor Total da Conta"), ;

		private final String desc;

		private TitulosRegistrosEnum(final String desc) {

			this.desc = desc;
		}

		@Override
		public int getIndice() {

			return this.ordinal();
		}

		@Override
		public String getDescricao() {

			return this.desc;
		}
	}

	private final Integer cthSeq;
	private final Integer pacProntuario;
	private final String pacNome;
	private final DominioSituacaoConta cthIndSit;
	private final String unfDesc;
	private final String intLeito;
	private final Long cthNroAih;
	private final Integer cmce;
	private final Boolean isDesdobramento;
	private final Date cthDataInt;
	private final Date cthDataAlta;
	private final Date cthDataSms;
	private final String caracterInternacao;
	private final Boolean isForaEstado;
	private final Boolean isEspCir;
	private final Boolean isCobraAih;
	private final String fcfDesc;
	private final String cthSmsStatus;
	private final String cthCodExcCrit;
	private final String iphCodDescSol;
	private final String iphCodDescReal;
	private final String msgErro;
	private final BigDecimal valorTotal;

	public RegistroCsvContasPeriodo(
			final Integer cthSeq,
			final Integer pacProntuario,
			final String pacNome,
			final DominioSituacaoConta cthIndSit,
			final String unfDesc,
			final String intLeito,
			final Long cthNroAih,
			final Integer cmce,
			final Boolean isDesdobramento,
			final Date cthDataInt,
			final Date cthDataAlta,
			final Date cthDataSms,
			final String caracterInternacao,
			final Boolean isForaEstado,
			final Boolean isEspCir,
			final Boolean isCobraAih,
			final String fcfDesc,
			final String cthSmsStatus,
			final String cthCodExcCrit,
			final String iphCodDescSol,
			final String iphCodDescReal,
			final String msgErro,
			final BigDecimal valorTotal) {

		super(NOME_TEMPLATE);

		this.cthSeq = cthSeq;
		this.pacProntuario = pacProntuario;
		this.pacNome = pacNome;
		this.cthIndSit = cthIndSit;
		this.unfDesc = unfDesc;
		this.intLeito = intLeito;
		this.cthNroAih = cthNroAih;
		this.cmce = cmce;
		this.isDesdobramento = isDesdobramento;
		this.cthDataInt = cthDataInt;
		this.cthDataAlta = cthDataAlta;
		this.cthDataSms = cthDataSms;
		this.caracterInternacao = caracterInternacao;
		this.isForaEstado = isForaEstado;
		this.isEspCir = isEspCir;
		this.isCobraAih = isCobraAih;
		this.fcfDesc = fcfDesc;
		this.cthSmsStatus = cthSmsStatus;
		this.cthCodExcCrit = cthCodExcCrit;
		this.iphCodDescSol = iphCodDescSol;
		this.iphCodDescReal = iphCodDescReal;
		this.msgErro = msgErro;
		this.valorTotal = valorTotal;
	}

	@Override
	protected CamposEnum[] obterCampos() {

		return TitulosRegistrosEnum.values();
	}

	protected Object internObterRegistro(final CamposEnum campo) {

		Object result = null;
		TitulosRegistrosEnum casted = null;

		casted = (TitulosRegistrosEnum) campo;
		switch (casted) {
		case CONTA:
			result = this.cthSeq;
			break;
		case PRONTUARIO:
			result = this.pacProntuario;
			break;
		case NOME:
			result = this.pacNome;
			break;
		case SITUACAO:
			result = this.cthIndSit;
			break;
		case UNF:
			result = this.unfDesc;
			break;
		case LEITO:
			result = this.intLeito;
			break;
		case AIH:
			result = this.cthNroAih;
			break;
		case CMCE:
			result = this.cmce;
			break;
		case DESDOBRAMENTO:
			result = this.isDesdobramento;
			break;
		case DATA_INT:
			result = this.cthDataInt;
			break;
		case DATA_ALTA:
			result = this.cthDataAlta;
			break;
		case DATA_SMS:
			result = this.cthDataSms;
			break;
		case CARACTER_INTERNACAO:
			result = this.caracterInternacao;
			break;
		case FORA_EST:
			result = this.isForaEstado;
			break;
		case ESP_CIRRUR:
			result = this.isEspCir;
			break;
		case COBRA_AIH:
			result = this.isCobraAih;
			break;
		case COMPLEX:
			result = this.fcfDesc;
			break;
		case SIT_SMS:
			result = this.cthSmsStatus;
			break;
		case EXCLUSAO_CRIT:
			result = this.cthCodExcCrit;
			break;
		case SOLIC:
			result = this.iphCodDescSol;
			break;
		case REALIZADO:
			result = this.iphCodDescReal;
			break;
		case MSG_ERRO:
			result = this.msgErro;
			break;
		case TOTAL:
			result = this.valorTotal;
			break;
		default:
			throw new IllegalArgumentException("Campo invalido, descricao " + campo.getDescricao());
		}

		return result;
	}

	@Override
	protected Object obterRegistro(final CamposEnum campo) {

		Object result = null;
		Object obj = null;

		obj = this.internObterRegistro(campo);
		result = obj;
		if (obj == null) {
			result = MAGIC_STRING_NULL_VALUES_EQ_EMPTY;
		} else if (obj instanceof Boolean) {
			result = Boolean.TRUE.equals(obj)
					? MAGIC_STRING_BOOLEAN_TRUE_EQ_S
					: MAGIC_STRING_BOOLEAN_FALSE_EQ_N;
		} else if (obj instanceof Date) {
			if (TitulosRegistrosEnum.DATA_INT.equals(campo) || TitulosRegistrosEnum.DATA_ALTA.equals(campo)) {
				result = String.format(STRING_FORMAT_DD_MM_YYYY, (Date) obj);
			} else {
				result = String.format(STRING_FORMAT_DD_MM_YYYY_HH_MM, (Date) obj);
			}
		} else if (obj instanceof BigDecimal) {
			result = ((BigDecimal) obj).toString().replace(".", ",");
		}

		return result;
	}
	
}
