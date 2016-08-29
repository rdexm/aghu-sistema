package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class DiagnosticosPrePosOperatoriosVO {
	private Integer prontuario;
	private Integer pacCodigo;
	private Integer numeroCirurgia;
	private Date mes;
	private String nomeEspecialidade;
	private Short seqp;
	private Integer matricula;
	private Short vinCodigo;
	private String strProntuario;
	private String strDataMesAno;
	private String strEquipe;
	private String strDiagnostico;
	private String strPre;
	private String strPos;

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getNumeroCirurgia() {
		return numeroCirurgia;
	}

	public void setNumeroCirurgia(Integer numeroCirurgia) {
		this.numeroCirurgia = numeroCirurgia;
	}

	public Date getMes() {
		return mes;
	}

	public void setMes(Date mes) {
		this.mes = mes;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getStrProntuario() {
		return strProntuario;
	}

	public void setStrProntuario(String strProntuario) {
		this.strProntuario = strProntuario;
	}

	public String getStrDataMesAno() {
		return strDataMesAno;
	}

	public void setStrDataMesAno(String strDataMesAno) {
		this.strDataMesAno = strDataMesAno;
	}

	public String getStrEquipe() {
		return strEquipe;
	}

	public void setStrEquipe(String strEquipe) {
		this.strEquipe = strEquipe;
	}

	public String getStrDiagnostico() {
		return strDiagnostico;
	}

	public void setStrDiagnostico(String strDiagnostico) {
		this.strDiagnostico = strDiagnostico;
	}

	public void setStrPre(String strPre) {
		this.strPre = strPre;
	}

	public String getStrPre() {
		return strPre;
	}

	public void setStrPos(String strPos) {
		this.strPos = strPos;
	}

	public String getStrPos() {
		return strPos;
	}

	public enum Fields {
		PRONTUARIO("prontuario"),
		PAC_CODIGO("pacCodigo"),
		NRO_CIRURGIA("numeroCirurgia"),
		MES("mes"),
		ESPECIALIDADE("nomeEspecialidade"),
		DCG_SEQP("seqp"),
		SER_MATRICULA_PROF("matricula"),
		SER_VIN_CODIGO_PROF("vinCodigo"),
		STR_PRONTUARIO("strProntuario"),
		STR_DATA_MES_ANO("strDataMesAno"),
		STR_EQUIPE("strEquipe"),
		STR_DIAGNOSTICO("strDiagnostico"),
		STR_PRE("strPre"),
		STR_POS("strPos");
	
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashCode = new HashCodeBuilder();
		hashCode.append(strProntuario)
		.append(pacCodigo)
		.append(numeroCirurgia)
		.append(strDataMesAno)
		.append(strDiagnostico)
		.append(strPre)
		.append(strPos)
		.append(strEquipe)
		.append(nomeEspecialidade);

		return hashCode.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DiagnosticosPrePosOperatoriosVO other = (DiagnosticosPrePosOperatoriosVO) obj;

		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(getProntuario(), other.getProntuario())
				.append(getPacCodigo(), other.getPacCodigo())
				.append(getNumeroCirurgia(), other.getNumeroCirurgia())
				.append(getStrDataMesAno(), other.getStrDataMesAno())
				.append(getStrDiagnostico(), other.getStrDiagnostico())
				.append(getStrPre(), other.getStrPre())
				.append(getStrPos(), other.getStrPos())
				.append(getStrEquipe(), other.getStrEquipe())
				.append(getNomeEspecialidade(), other.getNomeEspecialidade());

		return equalsBuilder.isEquals();
	}


}
