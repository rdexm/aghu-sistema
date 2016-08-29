package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioTipoRemuneracao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "RAP_SERVIDORES_JN", schema = "AGH")
@SequenceGenerator(name = "rapSerJnSeq", sequenceName = "AGH.RAP_SER_JN_SEQ", allocationSize = 1)

@Immutable
public class RapServidoresJn extends BaseJournal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8870208961497507327L;
	private Integer matricula;
	private Short vinCodigo;
	private Integer cctCodigo;
	private String ocaCarCodigo;
	private Integer ocaCodigo;
	private Integer pesCodigo;
	private Date dtInicioVinculo;
	private Integer htrCodigo;
	private Integer cctCodigoAtua;
	private Date dtFimVinculo;
	private Date alteradoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String usuario;
	private String email;
	private String indSituacao;
	private Short ramNroRamal;
	private Short grfCodigo;
	private DominioTipoRemuneracao tipoRemuneracao;
	private Short cargaHoraria;
	private Integer codStarh;
	private Integer cctCodigoDesempenho;
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rapSerJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "MATRICULA", length = 7, nullable = false)
	public Integer getMatricula() {
		return this.matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	@Column(name = "VIN_CODIGO", length = 3, nullable = false)
	public Short getVinCodigo() {
		return this.vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	@Column(name = "CCT_CODIGO", length = 6)
	public Integer getCctCodigo() {
		return this.cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	@Column(name = "OCA_CAR_CODIGO", length = 10)
	public String getOcaCarCodigo() {
		return this.ocaCarCodigo;
	}

	public void setOcaCarCodigo(String ocaCarCodigo) {
		this.ocaCarCodigo = ocaCarCodigo;
	}

	@Column(name = "OCA_CODIGO", length = 5)
	public Integer getOcaCodigo() {
		return this.ocaCodigo;
	}

	public void setOcaCodigo(Integer ocaCodigo) {
		this.ocaCodigo = ocaCodigo;
	}

	@Column(name = "PES_CODIGO", length = 9)
	public Integer getPesCodigo() {
		return this.pesCodigo;
	}

	public void setPesCodigo(Integer pesCodigo) {
		this.pesCodigo = pesCodigo;
	}

	@Column(name = "DT_INICIO_VINCULO")
	public Date getDtInicioVinculo() {
		return this.dtInicioVinculo;
	}

	public void setDtInicioVinculo(Date dtInicioVinculo) {
		this.dtInicioVinculo = dtInicioVinculo;
	}

	@Column(name = "HTR_CODIGO", length = 5)
	public Integer getHtrCodigo() {
		return this.htrCodigo;
	}

	public void setHtrCodigo(Integer htrCodigo) {
		this.htrCodigo = htrCodigo;
	}

	@Column(name = "CCT_CODIGO_ATUA", length = 6)
	public Integer getCctCodigoAtua() {
		return this.cctCodigoAtua;
	}

	public void setCctCodigoAtua(Integer cctCodigoAtua) {
		this.cctCodigoAtua = cctCodigoAtua;
	}

	@Column(name = "DT_FIM_VINCULO")
	public Date getDtFimVinculo() {
		return this.dtFimVinculo;
	}

	public void setDtFimVinculo(Date dtFimVinculo) {
		this.dtFimVinculo = dtFimVinculo;
	}

	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_MATRICULA", length = 7)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", length = 3)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "USUARIO", length = 20)
	public String getUsuario() {
		return this.usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	@Column(name = "EMAIL", length = 45)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "RAM_NRO_RAMAL", length = 4)
	public Short getRamNroRamal() {
		return this.ramNroRamal;
	}

	public void setRamNroRamal(Short ramNroRamal) {
		this.ramNroRamal = ramNroRamal;
	}

	@Column(name = "GRF_CODIGO", length = 3)
	public Short getGrfCodigo() {
		return this.grfCodigo;
	}

	public void setGrfCodigo(Short grfCodigo) {
		this.grfCodigo = grfCodigo;
	}

	@Column(name = "TIPO_REMUNERACAO", length = 1)
	public DominioTipoRemuneracao getTipoRemuneracao() {
		return this.tipoRemuneracao;
	}

	public void setTipoRemuneracao(DominioTipoRemuneracao tipoRemuneracao) {
		this.tipoRemuneracao = tipoRemuneracao;
	}

	@Column(name = "CARGA_HORARIA", length = 3)
	public Short getCargaHoraria() {
		return this.cargaHoraria;
	}

	public void setCargaHoraria(Short cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	@Column(name = "COD_STARH", length = 8)
	public Integer getCodStarh() {
		return this.codStarh;
	}

	public void setCodStarh(Integer codStarh) {
		this.codStarh = codStarh;
	}

	@Column(name = "CCT_CODIGO_DESEMPENHO", length = 6)
	public Integer getCctCodigoDesempenho() {
		return this.cctCodigoDesempenho;
	}

	public void setCctCodigoDesempenho(Integer cctCodigoDesempenho) {
		this.cctCodigoDesempenho = cctCodigoDesempenho;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RapServidoresJn)) {
			return false;
		}
		return new EqualsBuilder().isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().toHashCode();
	}

	public enum Fields {
		JN_USER("nomeUsuario"), JN_DATE_TIME("dataAlteracao"), JN_OPERATION(
				"operacao"), MATRICULA("matricula"), VIN_CODIGO("vinCodigo"), CCT_CODIGO(
				"cctCodigo"), OCA_CAR_CODIGO("ocaCarCodigo"), OCA_CODIGO(
				"ocaCodigo"), PES_CODIGO("pesCodigo"), DT_INICIO_VINCULO(
				"dtInicioVinculo"), HTR_CODIGO("htrCodigo"), CCT_CODIGO_ATUA(
				"cctCodigoAtua"), DT_FIM_VINCULO("dtFimVinculo"), ALTERADO_EM(
				"alteradoEm"), SER_MATRICULA("serMatricula"), SER_VIN_CODIGO(
				"serVinCodigo"), USUARIO("usuario"), EMAIL("email"), IND_SITUACAO(
				"indSituacao"), RAM_NRO_RAMAL("ramNroRamal"), GRF_CODIGO(
				"grfCodigo"), TIPO_REMUNERACAO("tipoRemuneracao"), CARGA_HORARIA(
				"cargaHoraria"), COD_STARH("codStarh"), CCT_CODIGO_DESEMPENHO(
				"cctCodigoDesempenho");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}