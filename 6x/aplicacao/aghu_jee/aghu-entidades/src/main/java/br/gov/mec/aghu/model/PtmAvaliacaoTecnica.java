package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioSituacaoAceiteTecnico;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * @author rafael.nascimento
 */

@Entity
@Table(name="PTM_AVALIACAO_TECNICA ", schema = "AGH")
@SequenceGenerator(name="ptmAvaliacaoTecnicaSeq", sequenceName="AGH.PTM_AVT_SQ1", allocationSize = 1)
public class PtmAvaliacaoTecnica extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5185976337842231691L;
	
	private Integer seq;
	private Short vidaUtil;
	private Date dataInicioGarantia;
	private Short tempoGarantia;
	private String justificativa;
	private String descricaoMaterial;
	private DominioAceiteTecnico indStatus;
	private DominioSituacaoAceiteTecnico indSituacao;
	private Date dataCriacao;
	private Date dataUltimaAlteracao;
	private RapServidores servidor;
	private RapServidores servidorFinalizado;
	private RapServidores servidorCertificado;
	private PtmItemRecebProvisorios itemRecebProvisorio;
	private ScoMarcaComercial marcaComercial;
	private ScoMarcaModelo marcaModelo;
	private Integer momCod;
	private Integer mcmCod;
	
	private Set<PtmDesmembramento> desmembramento;
	private Set<PtmBemPermanentes> bemPermanentes;
	
	private Integer version;

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmAvaliacaoTecnicaSeq")
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "VIDA_UTIL", precision = 3, scale = 0)
	public Short getVidaUtil() {
		return vidaUtil;
	}

	public void setVidaUtil(Short vidaUtil) {
		this.vidaUtil = vidaUtil;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INICIO_GARANTIA")
	public Date getDataInicioGarantia() {
		return dataInicioGarantia;
	}

	public void setDataInicioGarantia(Date dataInicioGarantia) {
		this.dataInicioGarantia = dataInicioGarantia;
	}

	@Column(name = "TEMPO_GARANTIA", precision = 3, scale = 0)
	public Short getTempoGarantia() {
		return tempoGarantia;
	}

	public void setTempoGarantia(Short tempoGarantia) {
		this.tempoGarantia = tempoGarantia;
	}

	@Column(name = "JUSTIFICATIVA", length = 500)
	@Length(max = 500)
	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(name = "DESCRICAO_MATERIAL", length = 500)
	@Length(max = 500)
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}

	@Column(name = "IND_STATUS", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioAceiteTecnico getIndStatus() {
		return indStatus;
	}

	public void setIndStatus(DominioAceiteTecnico indStatus) {
		this.indStatus = indStatus;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoAceiteTecnico getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoAceiteTecnico indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CRIACAO", nullable = false)
	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULTIMA_ALTERACAO")
	public Date getDataUltimaAlteracao() {
		return dataUltimaAlteracao;
	}

	public void setDataUltimaAlteracao(Date dataUltimaAlteracao) {
		this.dataUltimaAlteracao = dataUltimaAlteracao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA_FINALIZADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_FINALIZADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorFinalizado() {
		return servidorFinalizado;
	}

	public void setServidorFinalizado(RapServidores servidorFinalizado) {
		this.servidorFinalizado = servidorFinalizado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA_CERTIFICADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CERTIFICADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorCertificado() {
		return servidorCertificado;
	}

	public void setServidorCertificado(RapServidores servidorCertificado) {
		this.servidorCertificado = servidorCertificado;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IRP_SEQ")
	public PtmItemRecebProvisorios getItemRecebProvisorio() {
		return itemRecebProvisorio;
	}

	public void setItemRecebProvisorio(PtmItemRecebProvisorios itemRecebProvisorio) {
		this.itemRecebProvisorio = itemRecebProvisorio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MCM_COD", insertable = false, updatable = false)
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "MCM_COD", referencedColumnName = "MCM_CODIGO", insertable = false, updatable = false),
			@JoinColumn(name = "MOM_COD", referencedColumnName = "SEQP", insertable = false, updatable = false) })
	public ScoMarcaModelo getMarcaModelo() {
		return marcaModelo;
	}

	public void setMarcaModelo(ScoMarcaModelo marcaModelo) {
		this.marcaModelo = marcaModelo;
	}

	@Column(name = "MCM_COD", precision = 6, scale = 0)
	public Integer getMcmCod() {
		return mcmCod;
	}

	public void setMcmCod(Integer mcmCod) {
		this.mcmCod = mcmCod;
	}
	
	
	@Column(name = "MOM_COD", precision = 3, scale = 0)
	public Integer getMomCod() {
		return momCod;
	}

	public void setMomCod(Integer momCod) {
		this.momCod = momCod;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "avaliacaoTecnica")	
	public Set<PtmDesmembramento> getDesmembramento() {
		return desmembramento;
	}

	public void setDesmembramento(Set<PtmDesmembramento> desmembramento) {
		this.desmembramento = desmembramento;
	}
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "avaliacaoTecnica")
	public Set<PtmBemPermanentes> getBemPermanentes() {
		return bemPermanentes;
	}

	public void setBemPermanentes(Set<PtmBemPermanentes> bemPermanentes) {
		this.bemPermanentes = bemPermanentes;
	}

	@Override
	public int hashCode() {

		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeq());
		umHashCodeBuilder.append(this.getVidaUtil());
		umHashCodeBuilder.append(this.getDataInicioGarantia());
		umHashCodeBuilder.append(this.getTempoGarantia());
		umHashCodeBuilder.append(this.getJustificativa());
		umHashCodeBuilder.append(this.getDescricaoMaterial());
		umHashCodeBuilder.append(this.getIndStatus());
		umHashCodeBuilder.append(this.getIndSituacao());
		umHashCodeBuilder.append(this.getDataCriacao());
		umHashCodeBuilder.append(this.getDataUltimaAlteracao());
		umHashCodeBuilder.append(this.getVersion());
		
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof PtmAvaliacaoTecnica)) {
			return false;
		}

		PtmAvaliacaoTecnica other = (PtmAvaliacaoTecnica) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
		umEqualsBuilder.append(this.getVidaUtil(), other.getVidaUtil());
		umEqualsBuilder.append(this.getDataInicioGarantia(), other.getDataInicioGarantia());
		umEqualsBuilder.append(this.getTempoGarantia(), other.getTempoGarantia());
		umEqualsBuilder.append(this.getJustificativa(), other.getJustificativa());
		umEqualsBuilder.append(this.getDescricaoMaterial(), other.getDescricaoMaterial());
		umEqualsBuilder.append(this.getIndStatus(), other.getIndStatus());
		umEqualsBuilder.append(this.getIndSituacao(), other.getIndSituacao());
		umEqualsBuilder.append(this.getDataCriacao(), other.getDataCriacao());
		umEqualsBuilder.append(this.getDataUltimaAlteracao(), other.getDataUltimaAlteracao());
		umEqualsBuilder.append(this.getVersion(), other.getVersion());

		return umEqualsBuilder.isEquals();
	}

	public enum Fields {
		
		SEQ("seq"),
		IRP_SEQ("itemRecebProvisorio.seq"),
		VID_AUTIL("vidaUtil"),
		DATA_INICIO_GARANTIA("dataInicioGarantia"),
		TEMPO_GARANTIA("tempoGarantia"),
		MCM_COD("marcaComercial.codigo"),
		MOM_COD("marcaModelo.id.seqp"),
		MOM_MCM_COD("marcaModelo.id.mcmCodigo"),
		JUSTIFICATIVA("justificativa"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		IND_STATUS("indStatus"),
		IND_SITUACAO("indSituacao"),
		DATA_CRIACAO("dataCriacao"),
		DATA_ULTIMA_ALTERACAO("dataUltimaAlteracao"),
		SERVIDOR("servidor"),
		MATRICULA_SERVIDOR("servidor.id.matricula"),
		VIN_CODIGO_SERVIDOR("servidor.id.vinCodigo"),
		SERVIDOR_FINALIZADO("servidorFinalizado"),
		MATRICULA_SERVIDOR_FINALIZADO("servidorFinalizado.id.matricula"),
		VIN_CODIGO_SERVIDOR_FINALIZADO("servidorFinalizado.id.vinCodigo"),
		SERVIDOR_CERTIFICADO("servidorCertificado"),
		MATRICULA_SERVIDOR_CERTIFICADO("servidorCertificado.id.matricula"),
		VIN_CODIGO_SERVIDOR_CERTIFICADO("servidorCertificado.id.vinCodigo"),
		MARCA_COMERCIAL("marcaComercial"),
		MARCA_MODELO("marcaModelo"),
		ITEM_RECEB_PROVISORIO("itemRecebProvisorio"),
		DESMEMBRAMENTO("desmembramento"),
		BEM_PERMANENTES("bemPermanentes")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}