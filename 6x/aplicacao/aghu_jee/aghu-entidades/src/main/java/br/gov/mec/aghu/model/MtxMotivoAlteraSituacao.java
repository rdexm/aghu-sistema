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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoMotivoAlteraSituacoes;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mtxMasSq1", sequenceName="AGH.MTX_MAS_SQ1", allocationSize = 1)
@Table(name = "MTX_MOTIVO_ALTERA_SITUACOES", schema = "AGH")
public class MtxMotivoAlteraSituacao extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4174566485381238958L;
	
	private Integer seq;
	private String descricao;
	private DominioTipoMotivoAlteraSituacoes tipo;
	private DominioSituacao indicadorSituacao;
	private RapServidores servidor;
	private Date criadoEm;
	private Integer version;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxMasSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "DESCRICAO", length = 250, nullable = false)
	@Length(max = 250)
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "TIPO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoMotivoAlteraSituacoes getTipo() {
		return tipo;
	}
	public void setTipo(DominioTipoMotivoAlteraSituacoes tipo) {
		this.tipo = tipo;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndicadorSituacao() {
		return indicadorSituacao;
	}
	public void setIndicadorSituacao(DominioSituacao indicadorSituacao) {
		this.indicadorSituacao = indicadorSituacao;
	}
	
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@ManyToOne(fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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
		DESCRICAO("descricao"),
		TIPO("tipo"),
		INDICADOR_SITUACAO("indicadorSituacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	

}
