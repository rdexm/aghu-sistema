package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "PTM_SITUACAO_MOTIVO_MOVIMENTO", schema = "AGH")
@SequenceGenerator(name = "ptmSmmSeq", sequenceName = "AGH.PTM_SMM_SEQ", allocationSize = 1)

public class PtmSituacaoMotivoMovimento extends BaseEntitySeq<Integer> implements Serializable{

	private static final long serialVersionUID = -5424116818648535824L;

	private Integer seq;
	private PtmMotivoMovimento ptmMotivoMovimento;
	private String situacao;
	private List<PtmDescMotivoMovimentos> ptmDescMotivoMovimentos;
	private Integer version;
	
	public PtmSituacaoMotivoMovimento(){
		
	}
	
	public PtmSituacaoMotivoMovimento(Integer seq, PtmMotivoMovimento ptmMotivoMovimento, String situacao){
		this.seq = seq;
		this.ptmMotivoMovimento = ptmMotivoMovimento;
		this.situacao = situacao;
	}
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmSmmSeq")
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
		
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEQ_MM", referencedColumnName = "SEQ", nullable = false)
	public PtmMotivoMovimento getPtmMotivoMovimento() {
		return ptmMotivoMovimento;
	}

	public void setPtmMotivoMovimento(PtmMotivoMovimento ptmMotivoMovimento) {
		this.ptmMotivoMovimento = ptmMotivoMovimento;
	}

	@Column(name = "SITUACAO", length = 250)
	@Length(max = 250)
	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ptmSituacaoMotivoMovimento")
	public List<PtmDescMotivoMovimentos> getPtmDescMotivoMovimentos() {
		return ptmDescMotivoMovimentos;
	}

	public void setPtmDescMotivoMovimentos(List<PtmDescMotivoMovimentos> ptmDescMotivoMovimentos) {
		this.ptmDescMotivoMovimentos = ptmDescMotivoMovimentos;
	}

	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields{
		SEQ("seq"),
		PTM_MOTIVO_MOVIMENTO("ptmMotivoMovimento"),
		SITUACAO("situacao"),
		DESCS_MOTIVO_MOVIMENTOS("ptmDescMotivoMovimentos"),
		VERSION("version");
		
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
