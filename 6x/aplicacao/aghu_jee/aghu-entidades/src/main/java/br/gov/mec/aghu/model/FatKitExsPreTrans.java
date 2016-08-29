package br.gov.mec.aghu.model;

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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="fatKepSq1", sequenceName="AGH.FAT_KEP_SQ1")
@Table(name = "FAT_KIT_EXS_PRE_TRANS", schema = "AGH")
public class FatKitExsPreTrans extends BaseEntitySeq<Integer> {

	private static final long serialVersionUID = -7767526519147463971L;
	
	private Integer seq;
	private RapServidores servidor;
	private RapServidores servidorAltera;

	private Date criadoEm;
	private Date alteradoEm;
	private FatProcedHospInternos procedimentoHospitalarInterno;
	private FatTipoTratamentos fatTipoTratamentos;
	
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fatKepSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(final Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(final Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	public void setFatTipoTratamentos(FatTipoTratamentos fatTipoTratamentos) {
		this.fatTipoTratamentos = fatTipoTratamentos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPT_SEQ", nullable = false, insertable=false, updatable=false)
	public FatTipoTratamentos getFatTipoTratamentos() {
		return fatTipoTratamentos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(final RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ALTERA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAltera() {
		return servidorAltera;
	}

	public void setServidorAltera(RapServidores servidorAltera) {
		this.servidorAltera = servidorAltera;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(final Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ", nullable = false)
	public FatProcedHospInternos getProcedimentoHospitalarInterno() {
		return procedimentoHospitalarInterno;
	}

	public void setProcedimentoHospitalarInterno(
			final FatProcedHospInternos procedimentoHospitalarInterno) {
		this.procedimentoHospitalarInterno = procedimentoHospitalarInterno;
	}
	
	public enum Fields {
		SEQ("seq"),
		PROCEDIMENTO_HOSPITALAR_INTERNO("procedimentoHospitalarInterno"),
		PHI_SEQ("procedimentoHospitalarInterno.seq"),
		FAT_TIPO_TRATAMENTOS("fatTipoTratamentos"),
		TPT_SEQ("fatTipoTratamentos.seq"),
		SERVIDOR("servidor"),
		SERVIDOR_ALTERA("servidorAltera"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm");
		
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