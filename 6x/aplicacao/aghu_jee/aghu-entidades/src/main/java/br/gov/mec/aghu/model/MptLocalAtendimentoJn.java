package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mptLoaJnSq1", sequenceName="AGH.MPT_LOA_JN_SEQ", allocationSize = 1)
@Table(name = "MPT_LOCAL_ATENDIMENTO_JN", schema = "AGH")

public class MptLocalAtendimentoJn extends BaseJournal implements java.io.Serializable {
	
	private static final long serialVersionUID = -6812441901110101241L;

	private Short seq;
	private Short seqSal;
	private String descricao;
	private Boolean indReserva;
	private DominioTipoAcomodacao tipoLocal;
	private DominioSituacao indSituacao;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date criadoEm;
	
	public MptLocalAtendimentoJn() {
		
	}
	
	@Id
	@Column(name = "SEQ_JN", nullable = false, precision = 4, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptLoaJnSq1")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "SEQ_SAL", nullable = false)
	public Short getSeqSal() {
		return seqSal;
	}

	public void setSeqSal(Short seqSal) {
		this.seqSal = seqSal;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_RESERVA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndReserva() {
		return indReserva;
	}

	public void setIndReserva(Boolean indReserva) {
		this.indReserva = indReserva;
	}

	@Column(name = "TIPO_LOCAL", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoAcomodacao getTipoLocal() {
		return tipoLocal;
	}

	public void setTipoLocal(DominioTipoAcomodacao tipoLocal) {
		this.tipoLocal = tipoLocal;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	
	@Column(name = "SER_VIN_CODIGO", precision = 4, scale = 0)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	public enum Fields {

		SEQ_JN("seqJn"),
		JN_USER("nomeUsuario"),
		JN_DATE_TIME("dataAlteracao"),
		JN_OPERATION("operacao"),
		SEQ("seq"),
		SEQ_SAL("seqSal"),
		DESCRICAO("descricao"),
		IND_RESERVA("indReserva"),
		TIPO_LOCAL("tipoLocal"),
		IND_SITUACAO("indSituacao"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		CRIADO_EM("criadoEm");

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
