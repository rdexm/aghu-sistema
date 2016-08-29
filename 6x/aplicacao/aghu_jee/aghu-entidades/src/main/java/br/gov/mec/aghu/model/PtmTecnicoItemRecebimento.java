package br.gov.mec.aghu.model;

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
import javax.persistence.Version;

import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioIndResponsavel;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="PTM_TECNICO_ITEM_RECEB", schema = "AGH")
@SequenceGenerator(name="ptmTecnicoItemRecebSeq", sequenceName="AGH.PTM_TEI_SQ1", allocationSize = 1)
public class PtmTecnicoItemRecebimento extends BaseEntitySeq<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5039426617137676773L;
	
	private Long seq;
	private PtmItemRecebProvisorios ptmItemRecebProvisorio;
	private RapServidores servidor;
	private RapServidores servidorTecnico;
	private DominioIndResponsavel indResponsavel;
	private Integer version;
	
	public PtmTecnicoItemRecebimento() {
	}

	public PtmTecnicoItemRecebimento(Long seq,
			PtmItemRecebProvisorios ptmItemRecebProvisorio,
			RapServidores servidor, RapServidores servidorTecnico,
			DominioIndResponsavel indResponsavel, Integer version) {
		this.seq = seq;
		this.ptmItemRecebProvisorio = ptmItemRecebProvisorio;
		this.servidor = servidor;
		this.servidorTecnico = servidorTecnico;
		this.indResponsavel = indResponsavel;
		this.version = version;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmTecnicoItemRecebSeq")
	public Long getSeq() {
		return seq;
	}
	
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IRP_SEQ")
	public PtmItemRecebProvisorios getPtmItemRecebProvisorio() {
		return ptmItemRecebProvisorio;
	}
	
	public void setPtmItemRecebProvisorio(PtmItemRecebProvisorios ptmItemRecebProvisorio) {
		this.ptmItemRecebProvisorio = ptmItemRecebProvisorio;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA_TECNICO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_TECNICO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorTecnico() {
		return servidorTecnico;
	}
	
	public void setServidorTecnico(RapServidores servidorTecnico) {
		this.servidorTecnico = servidorTecnico;
	}
	
	@Column(name = "IND_RESPONSAVEL", length = 1)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioIndResponsavel") }, type = "br.gov.mec.aghu.core.model.jpa.DominioStringUserType")	
	public DominioIndResponsavel getIndResponsavel() {
		return indResponsavel;
	}
	
	public void setIndResponsavel(DominioIndResponsavel indResponsavel) {
		this.indResponsavel = indResponsavel;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {

		SEQ("seq"),
		PTM_ITEM_RECEB_PROVISORIO("ptmItemRecebProvisorio"),
		SERVIDOR("servidor"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		SERVIDOR_TECNICO("servidorTecnico"),
		IND_RESPONSAVEL("indResponsavel"),
		SER_MATRICULA_TEC_PADRAO("servidorTecnico.id.matricula"),
		SER_VIN_CODIGO_TEC_PADRAO("servidorTecnico.id.vinCodigo"),
		IRP_SEQ("ptmItemRecebProvisorio.seq");

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
