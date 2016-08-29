package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;

public class RelatorioCirurgiaProcedProfissionalVO {
	
	private Date data;
	private String mesAno;
	private String dataDiaMesAno;
	private Integer serMatriculaProf;
	private Short serVinCodigoProf;
	private Integer prontuario;
	private String nome;
	private Boolean indPrincipal;
	private String indPrincipalDescricao;
	private String procedimento;
	private DominioTipoAtuacao tipoAtuacao;
	private DominioFuncaoProfissional funcaoProfissional;
	private String atuacao;
	private String siglaEsp;

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getMesAno() {
		return mesAno;
	}

	public void setMesAno(String mesAno) {
		this.mesAno = mesAno;
	}

	public String getDataDiaMesAno() {
		return dataDiaMesAno;
	}

	public void setDataDiaMesAno(String dataDiaMesAno) {
		this.dataDiaMesAno = dataDiaMesAno;
	}

	public Integer getSerMatriculaProf() {
		return serMatriculaProf;
	}

	public void setSerMatriculaProf(Integer serMatriculaProf) {
		this.serMatriculaProf = serMatriculaProf;
	}

	public Short getSerVinCodigoProf() {
		return serVinCodigoProf;
	}

	public void setSerVinCodigoProf(Short serVinCodigoProf) {
		this.serVinCodigoProf = serVinCodigoProf;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getIndPrincipal() {
		return indPrincipal;
	}

	public void setIndPrincipal(Boolean indPrincipal) {
		this.indPrincipal = indPrincipal;
	}

	public String getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}

	public String getSiglaEsp() {
		return siglaEsp;
	}

	public void setSiglaEsp(String siglaEsp) {
		this.siglaEsp = siglaEsp;
	}
	
	public String getIndPrincipalDescricao() {
		return indPrincipalDescricao;
	}

	public void setIndPrincipalDescricao(String indPrincipalDescricao) {
		this.indPrincipalDescricao = indPrincipalDescricao;
	}
	
	public DominioFuncaoProfissional getFuncaoProfissional() {
		return funcaoProfissional;
	}

	public void setFuncaoProfissional(DominioFuncaoProfissional funcaoProfissional) {
		this.funcaoProfissional = funcaoProfissional;
	}

	public DominioTipoAtuacao getTipoAtuacao() {
		return tipoAtuacao;
	}

	public void setTipoAtuacao(DominioTipoAtuacao tipoAtuacao) {
		this.tipoAtuacao = tipoAtuacao;
	}

	public String getAtuacao() {
		return atuacao;
	}

	public void setAtuacao(String atuacao) {
		this.atuacao = atuacao;
	}

	@SuppressWarnings("ucd")
	public enum Fields {
		DATA("data"),
		MES_ANO("mesAno"),
		DATA_DIA_MES_ANO("dataDiaMesAno"),
		SER_MATRICULA_PROF("serMatriculaProf"), 
		SER_VIN_CODIGO_PROF("serVinCodigoProf"),		
		PRONTUARIO("prontuario"),
		NOME("nome"),
		IND_PRINCIPAL("indPrincipal"),
		PROCEDIMENTO("procedimento"),
		TIPO_ATUACAO("tipoAtuacao"),
		FUNCAO_PROFISSIONAL("funcaoProfissional"),
		ATUACAO("atuacao"),
		SIGLA_ESP("siglaEsp");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashCode =  new HashCodeBuilder();
		hashCode.append(getData())
			.append(getMesAno())
			.append(getDataDiaMesAno())
			.append(getSerMatriculaProf())
			.append(getSerVinCodigoProf())
			.append(getProntuario())
			.append(getNome())
			.append(getIndPrincipal())
			.append(getIndPrincipalDescricao())
			.append(getProcedimento())
			.append(getAtuacao())
			.append(getSiglaEsp());
		
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
		if (!(obj instanceof RelatorioCirurgiaProcedProfissionalVO)) {
			return false;
		}
		RelatorioCirurgiaProcedProfissionalVO other = (RelatorioCirurgiaProcedProfissionalVO) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(getData(), other.getData())
			.append(getMesAno(), other.getMesAno())
			.append(getDataDiaMesAno(), other.getDataDiaMesAno())
			.append(getSerMatriculaProf(), other.getSerMatriculaProf())
			.append(getSerVinCodigoProf(), other.getSerVinCodigoProf())
			.append(getProntuario(), other.getProntuario())
			.append(getNome(), other.getNome())
			.append(getIndPrincipal(), other.getIndPrincipal())
			.append(getIndPrincipalDescricao(), other.getIndPrincipalDescricao())
			.append(getProcedimento(), other.getProcedimento())
			.append(getAtuacao(), other.getAtuacao())
			.append(getSiglaEsp(), other.getSiglaEsp());
		return equalsBuilder.isEquals();
		
	}

	@Override
	public String toString() {
		
		return "RelatorioCirurgiaProcedProfissionalVO [data=" + data
				+ ", mesAno=" + mesAno + ", dataDiaMesAno=" + dataDiaMesAno
				+ ", serMatriculaProf=" + serMatriculaProf
				+ ", serVinCodigoProf=" + serVinCodigoProf + ", prontuario="
				+ prontuario + ", nome=" + nome + ", indPrincipal="
				+ indPrincipal + ", indPrincipalDescricao="
				+ indPrincipalDescricao + ", procedimento=" + procedimento
				+ ", tipoAtuacao=" + tipoAtuacao + ", funcaoProfissional="
				+ funcaoProfissional + ", atuacao=" + atuacao + ", siglaEsp="
				+ siglaEsp + "]";
	}

}
