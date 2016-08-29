package br.gov.mec.aghu.model;

import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioSituacaoAceiteTecnico;
import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * @author rafael.nascimento
 */

@Entity
@Immutable
@Table(name="PTM_AVALIACAO_TECNICA_JN", schema="AGH")
@SequenceGenerator(name="ptmAvaliacaoTecnicaJnSeq", sequenceName="AGH.PTM_AVT_JN_SEQ", allocationSize = 1)
public class PtmAvaliacaoTecnicaJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6349198669116279860L;
	
	
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
	private Integer version;
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmAvaliacaoTecnicaJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", nullable = false)
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

	@Version
	@Column(name = "VERSION", length = 9, nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		
		SEQ("seq"),
		IRP_SEQ("itemRecebProvisorio.seq"),
		VID_AUTIL("vidaUtil"),
		DATA_INICIO_GARANTIA("dataInicioGarantia"),
		TEMPO_GARANTIA("tempoGarantia"),
		MCM_COD("marcaComercial.id.mcmCodigo"),
		MOM_COD("marcaComercial.id.seqp"),
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
		JN_DATE_TIME("jnDateTime"),
		JN_OPERACAO("jnOperacao"),
		JN_USUARIO("jnUsuario"),
		JN_SEQ("jnSeq")
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
