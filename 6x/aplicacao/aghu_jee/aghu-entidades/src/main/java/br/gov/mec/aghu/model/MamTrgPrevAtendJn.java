package br.gov.mec.aghu.model;



import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.model.BaseJournal;


@Entity
@SequenceGenerator(name="aghMamTpvJnSeq", sequenceName="AGH.MAM_TPV_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_TRG_PREV_ATENDS_JN", schema = "AGH")
public class MamTrgPrevAtendJn extends BaseJournal {


	private static final long serialVersionUID = -6525678708421312156L;

	private Integer seq;
	private Date dthrPrevAtend;
	private Boolean indImediato;
	private Date geradoEm;	
	private MamTriagens triagem;
	private Integer serMatricula;
	private Short serVinCodigo;
	

	// ATUALIZADOR JOURNALS - ID
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghMamTpvJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}	
		
	@Column(name = "SEQ", nullable = false, precision = 14, scale = 0)
    public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_PREV_ATEND", nullable = false, length = 29)
	@NotNull
	public Date getDthrPrevAtend() {
		return this.dthrPrevAtend;
	}

	public void setDthrPrevAtend(Date dthrPrevAtend) {
		this.dthrPrevAtend = dthrPrevAtend;
	}
	
	@Column(name = "IND_IMEDIATO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndImediato() {
		return this.indImediato;
	}

	public void setIndImediato(Boolean indImediato) {
		this.indImediato = indImediato;
	}
	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "GERADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getGeradoEm() {
		return this.geradoEm;
	}

	public void setGeradoEm(Date geradoEm) {
		this.geradoEm = geradoEm;
	}
			
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="TRG_SEQ", referencedColumnName="SEQ", updatable = false, insertable = false)	
	public MamTriagens getTriagem() {
		return triagem;
	}

	public void setTriagem(MamTriagens triagem) {
		this.triagem = triagem;
	}	
	
	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}	

	
}
