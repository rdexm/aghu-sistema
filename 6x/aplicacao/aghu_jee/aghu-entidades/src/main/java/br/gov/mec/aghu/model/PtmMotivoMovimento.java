package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "PTM_MOTIVO_MOVIMENTO", schema = "AGH")
@SequenceGenerator(name = "ptmMmSeq", sequenceName = "AGH.PTM_MM_SEQ", allocationSize = 1)

public class PtmMotivoMovimento extends BaseEntitySeq<Integer> implements Serializable{

	private static final long serialVersionUID = -7251082870716314346L;
	
	private Integer seq;
	private String motivoMovimento;
	private List<PtmSituacaoMotivoMovimento> ptmSituacaoMotivoMovimentos;
	private Integer version;
	
	public PtmMotivoMovimento(){
		
	}
	
	public PtmMotivoMovimento(Integer seq, String motivoMovimento){
		this.seq = seq;
		this.motivoMovimento = motivoMovimento;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmMmSeq")
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "MOTIVO_MOVIMENTO", length = 250)
	@Length(max = 250)
	public String getMotivoMovimento() {
		return motivoMovimento;
	}

	public void setMotivoMovimento(String motivoMovimento) {
		this.motivoMovimento = motivoMovimento;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ptmMotivoMovimento")
	public List<PtmSituacaoMotivoMovimento> getPtmSituacaoMotivoMovimentos() {
		return ptmSituacaoMotivoMovimentos;
	}

	public void setPtmSituacaoMotivoMovimentos(List<PtmSituacaoMotivoMovimento> ptmSituacaoMotivoMovimentos) {
		this.ptmSituacaoMotivoMovimentos = ptmSituacaoMotivoMovimentos;
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
		MOTIVO_MOVIMENTO("motivoMovimento"),
		SITUACOES_MOTIVO_MOVIMENTO("ptmSituacaoMotivoMovimentos"),
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
