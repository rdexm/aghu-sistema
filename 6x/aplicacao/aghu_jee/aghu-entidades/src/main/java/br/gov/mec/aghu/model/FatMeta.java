package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "FAT_METAS", schema = "AGH")
@SequenceGenerator(name = "fatFcmSq1", sequenceName = "AGH.FAT_FCM_SQ1", allocationSize = 1)
public class FatMeta extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5946219353230685342L;
	private Integer seq;
	private Long quantidade;
	private BigDecimal valor;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer version;
	private RapServidores servidor;
	private RapServidores servidorAltera;
	private FatFormaOrganizacao fatFormasOrganizacao;
	private FatCaractFinanciamento fatCaractFinanciamento;
	private FatItensProcedHospitalar fatItensProcedHospitalar;
	private Date dthrInicioVig;
	private Date dthrFimVig;
	private FatGrupo fatGrupo;
	private FatSubGrupo fatSubGrupo;
	private Boolean indInternacao;
	private Boolean indAmbulatorio;
		
	// getters & setters

	@Id
	@Column(name = "SEQ", length = 8, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fatFcmSq1")
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "QUANTIDADE", length = 10)
	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name = "VALOR", length = 14)
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ALTERADO_EM")
	@Temporal(TemporalType.TIMESTAMP) 
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor(){
		return servidor;
	}
	
	public void setServidor(RapServidores servidor ){
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA_ALTERA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO_ALTERA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAltera(){
		return servidorAltera;
	}
	
	public void setServidorAltera(RapServidores servidorAltera){
		this.servidorAltera = servidorAltera;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "FOG_CODIGO", referencedColumnName = "CODIGO"),
			@JoinColumn(name = "FOG_SGR_GRP_SEQ", referencedColumnName = "SGR_GRP_SEQ"),
			@JoinColumn(name = "FOG_SGR_SUB_GRUPO", referencedColumnName = "SGR_SUB_GRUPO") })
	public FatFormaOrganizacao getFatFormasOrganizacao() {
		return fatFormasOrganizacao;
	}

	public void setFatFormasOrganizacao(FatFormaOrganizacao fatFormasOrganizacao) {
		this.fatFormasOrganizacao = fatFormasOrganizacao;
	}

	@ManyToOne
	@JoinColumn(name = "FCF_SEQ", referencedColumnName = "SEQ")
	public FatCaractFinanciamento getFatCaractFinanciamento() {
		return fatCaractFinanciamento;
	}

	public void setFatCaractFinanciamento(
			FatCaractFinanciamento fatCaractFinanciamento) {
		this.fatCaractFinanciamento = fatCaractFinanciamento;
	}

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "IPH_SEQ", referencedColumnName = "SEQ"),
			@JoinColumn(name = "IPH_PHO_SEQ", referencedColumnName = "PHO_SEQ") })
	public FatItensProcedHospitalar getFatItensProcedHospitalar() {
		return fatItensProcedHospitalar;
	}

	public void setFatItensProcedHospitalar(
			FatItensProcedHospitalar fatItensProcedHospitalar) {
		this.fatItensProcedHospitalar = fatItensProcedHospitalar;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DTHR_INICIO_VIG", nullable = true, length = 7)
	public Date getDthrInicioVig() {
		return dthrInicioVig;
	}

	public void setDthrInicioVig(Date dthrInicioVig) {
		this.dthrInicioVig = dthrInicioVig;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DTHR_FIM_VIG")
	public Date getDthrFimVig() {
		return dthrFimVig;
	}

	public void setDthrFimVig(Date dthrFimVig) {
		this.dthrFimVig = dthrFimVig;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GRUPO", referencedColumnName = "SEQ")
	public FatGrupo getFatGrupo() {
		return fatGrupo;
	}

	public void setFatGrupo(FatGrupo fatGrupo) {
		this.fatGrupo = fatGrupo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
		@JoinColumn(name = "SGR_GRP_SEQ", referencedColumnName = "GRP_SEQ"),
		@JoinColumn(name = "SGR_SUB_GRUPO", referencedColumnName = "SUB_GRUPO") })
	public FatSubGrupo getFatSubGrupo() {
		return fatSubGrupo;
	}

	public void setFatSubGrupo(FatSubGrupo fatSubGrupo) {
		this.fatSubGrupo = fatSubGrupo;
	}

	@Column(name = "IND_INTERNACAO", nullable = true, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInternacao() {
		return indInternacao;
	}

	public void setIndInternacao(Boolean indInternacao) {
		this.indInternacao = indInternacao;
	}

	@Column(name = "IND_AMBULATORIO", nullable = true, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAmbulatorio() {
		return indAmbulatorio;
	}

	public void setIndAmbulatorio(Boolean indAmbulatorio) {
		this.indAmbulatorio = indAmbulatorio;
	}

	public enum Fields {
		SEQ("seq"), 
		QUANTIDADE("quantidade"), 
		VALOR("valor"), 
		CRIADO_EM("criadoEm"), 
		ALTERADO_EM("alteradoEm"), 
		VERSION("version"), 
		SERVIDOR("servidor"),
		SERVIDOR_ALTERA("servidorAltera"),
		FAT_FORMAS_ORGANIZACAO("fatFormasOrganizacao"), 
		FAT_CARACT_FINANCIAMENTO("fatCaractFinanciamento"), 
		FAT_ITENS_PROCED_HOSPITALAR("fatItensProcedHospitalar"),
		DTHR_INICIO_VIG("dthrInicioVig"),
		DTHR_FIM_VIG("dthrFimVig"),
		GRUPO("fatGrupo"),
		SGR_SUB_GRUPO("fatSubGrupo"),
		IND_INTERNACAO("indInternacao"),
		IND_AMBULATORIO("indAmbulatorio")
		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@Override
	public String toString() {
		
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof FatMeta)) {
			return false;
		}
		FatMeta castOther = (FatMeta) other;
		
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
}