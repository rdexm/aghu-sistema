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
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name="PTM_EDIFICACOES_JN", schema = "AGH")
@SequenceGenerator(name="ptmEdificacaoJNSeq", sequenceName="AGH.PTM_EDI_JN_SEQ", allocationSize = 1)
public class PtmEdificacaoJN extends BaseJournal{

	private static final long serialVersionUID = 908536674968649919L;
	private Integer seqJN;
	private Integer seq;
	private DominioSituacao situacao;
	private String nome;
	private String descricao;
	private Date dtCriacao;
	private Date dtAlteradoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaAlterado;
	private Short serVinCodigoAlterado;
	private Integer lgrCodigo;
	private Long bpeSeq;
	private Double longitude;
	private Double latitude;
	private Integer numero;
	private String complemento;
	private Integer version;

	@Id
	@Column(name = "SEQ_JN", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmEdificacaoJNSeq")
	public Integer getSeqJN() {
		return seqJN;
	}

	public void setSeqJN(Integer seqJN) {
		this.seqJN = seqJN;
	}
	
	@Column(name = "SEQ", unique = true)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	
	@Column(name="NOME", length= 20)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "DESCRICAO", length = 50)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "DT_ALTERADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAlteradoEm() {
		return dtAlteradoEm;
	}
	
	public void setDtAlteradoEm(Date dtAlteradoEm) {
		this.dtAlteradoEm = dtAlteradoEm;
	}

	@Column(name = "DT_CRIACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtCriacao() {
		return dtCriacao;
	}

	public void setDtCriacao(Date dtCriacao) {
		this.dtCriacao = dtCriacao;
	}
	
	@Column(name = "NUMERO")
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column(name = "COMPLEMENTO")
	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name="LONGITUDE")
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name="LATITUDE")
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	@Column(name = "VERSION", length=9)
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}

//	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeqJN() == null) ? 0 : getSeqJN().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PtmEdificacaoJN)) {
			return false;
		}
		PtmEdificacaoJN other = (PtmEdificacaoJN) obj;
		if (getSeqJN() == null) {
			if (other.getSeqJN() != null) {
				return false;
			}
		} else if (!getSeqJN().equals(other.getSeqJN())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	
	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "SER_MATRICULA_ALTERADO_POR")
	public Integer getSerMatriculaAlterado() {
		return serMatriculaAlterado;
	}

	public void setSerMatriculaAlterado(Integer serMatriculaAlterado) {
		this.serMatriculaAlterado = serMatriculaAlterado;
	}

	@Column(name = "SER_VIN_CODIGO_ALTERADO_POR")
	public Short getSerVinCodigoAlterado() {
		return serVinCodigoAlterado;
	}

	public void setSerVinCodigoAlterado(Short serVinCodigoAlterado) {
		this.serVinCodigoAlterado = serVinCodigoAlterado;
	}

	@Column(name="LGR_SEQ")
	public Integer getLgrCodigo() {
		return lgrCodigo;
	}

	public void setLgrCodigo(Integer lgrCodigo) {
		this.lgrCodigo = lgrCodigo;
	}

	@Column(name="BPE_SEQ")
	public Long getBpeSeq() {
		return bpeSeq;
	}

	public void setBpeSeq(Long bpeSeq) {
		this.bpeSeq = bpeSeq;
	}
}
