package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;

public class VMpmServConselhoGeralVO implements Serializable {

	private static final long serialVersionUID = 8856402904213213010L;

	private Integer matricula;
	private Short vinCodigo;
	private Integer cctCodigo;
	private Integer cctCodigoAtua;
	private Integer ocaCodigo;
	private Short grfCodigo;
	private String nome;
	private String sigla;
	private Short cprCodigo;
	private String nroRegConselho;
	private DominioSituacaoVinculo indSituacao;
	private Date dtInicioVinculo;
	private Date dtFimVinculo;

	public String obterConselhoSigla() {

		StringBuilder retorno = new StringBuilder();

		if (nroRegConselho != null) {
			retorno.append(nroRegConselho);
		}

		retorno.append("          ");

		if (sigla != null) {
			retorno.append(sigla);
		}

		return retorno.toString();
	}

	public enum Fields {

		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		CCT_CODIGO("cctCodigo"), 
		CCT_CODIGO_ATUA("cctCodigoAtua"), 
		OCA_CODIGO("ocaCodigo"), 
		GRF_CODIGO("grfCodigo"), 
		NOME("nome"),
		SIGLA("sigla"),
		CPR_CODIGO("cprCodigo"), 
		NRO_REG_CONSELHO("nroRegConselho"),
		IND_SITUACAO("indSituacao"),
		DT_INICIO_VINCULO("dtInicioVinculo"),
		DT_FIM_VINCULO("dtFimVinculo");

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

		return new HashCodeBuilder()
				.append(matricula)
				.append(vinCodigo)
				.append(cctCodigo)
				.append(cctCodigoAtua)
				.append(ocaCodigo)
				.append(grfCodigo)
				.append(nome)
				.append(sigla)
				.append(cprCodigo)
				.append(nroRegConselho)
				.append(indSituacao)
				.append(dtInicioVinculo)
				.append(dtFimVinculo)
				.toHashCode();
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

		VMpmServConselhoGeralVO other = (VMpmServConselhoGeralVO) obj;

		return new EqualsBuilder()
				.append(matricula, other.matricula)
				.append(vinCodigo, other.vinCodigo)
				.append(cctCodigo, other.cctCodigo)
				.append(cctCodigoAtua, other.cctCodigoAtua)
				.append(ocaCodigo, other.ocaCodigo)
				.append(grfCodigo, other.grfCodigo)
				.append(nome, other.nome)
				.append(sigla, other.sigla)
				.append(cprCodigo, other.cprCodigo)
				.append(nroRegConselho, other.nroRegConselho)
				.append(indSituacao, other.indSituacao)
				.append(dtInicioVinculo, other.dtInicioVinculo)
				.append(dtFimVinculo, other.dtFimVinculo)
				.isEquals();
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

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Integer getCctCodigoAtua() {
		return cctCodigoAtua;
	}

	public void setCctCodigoAtua(Integer cctCodigoAtua) {
		this.cctCodigoAtua = cctCodigoAtua;
	}

	public Integer getOcaCodigo() {
		return ocaCodigo;
	}

	public void setOcaCodigo(Integer ocaCodigo) {
		this.ocaCodigo = ocaCodigo;
	}

	public Short getGrfCodigo() {
		return grfCodigo;
	}

	public void setGrfCodigo(Short grfCodigo) {
		this.grfCodigo = grfCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Short getCprCodigo() {
		return cprCodigo;
	}

	public void setCprCodigo(Short cprCodigo) {
		this.cprCodigo = cprCodigo;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public DominioSituacaoVinculo getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoVinculo indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Date getDtInicioVinculo() {
		return dtInicioVinculo;
	}

	public void setDtInicioVinculo(Date dtInicioVinculo) {
		this.dtInicioVinculo = dtInicioVinculo;
	}

	public Date getDtFimVinculo() {
		return dtFimVinculo;
	}

	public void setDtFimVinculo(Date dtFimVinculo) {
		this.dtFimVinculo = dtFimVinculo;
	}

}
