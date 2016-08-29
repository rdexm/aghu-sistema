package br.gov.mec.aghu.model;

import java.io.Serializable;
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

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity 
@Table(name = "MTX_EXAME_ULT_RESULTS", schema = "AGH")
@SequenceGenerator(name = "mtxEurSeq", sequenceName = "AGH.MTX_EUR_SQ1", allocationSize = 1)
public class MtxExameUltResults extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 1720123237247427845L;
	
	private Integer seq;
	private AelExames aelExames;
	private AelCampoLaudo campoLaudo;
	private DominioSituacao situacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxEurSeq")
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SIGLA", referencedColumnName = "SIGLA", nullable = false)
	public AelExames getAelExames() {
		return aelExames;
	}

	public void setAelExames(AelExames aelExames) {
		this.aelExames = aelExames;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAL_SEQ", referencedColumnName = "SEQ", nullable = false)
	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}
	
	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}
	
	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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
		AEL_EXAMES("aelExames"),
		AEL_EXAMES_SIGLA("aelExames.sigla"),
		CAMPO_LAUDO("campoLaudo"),
		CAMPO_LAUDO_SEQ("campoLaudo.seq"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		VERSION("version"),
		SIGLA("aelExames.sigla");
		
		private String fields;
		
		private Fields(String fields){
			this.fields = fields;
		}
		
		@Override
		public String toString(){
			return this.fields;
		}
	}
	
}
