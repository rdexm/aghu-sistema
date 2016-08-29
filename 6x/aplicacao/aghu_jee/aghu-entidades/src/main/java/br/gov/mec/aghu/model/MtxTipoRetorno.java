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
import org.hibernate.validator.constraints.Length;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mtxTreSq1", sequenceName="AGH.MTX_TRE_SQ1", allocationSize = 1)
@Table(name = "MTX_TIPO_RETORNO", schema = "AGH")
public class MtxTipoRetorno extends BaseEntitySeq<Integer> {
	
	private static final long serialVersionUID = -1983280417265871203L;
	
	private Integer seq;
	private String descricao;
	private DominioTipoRetorno indTipo;
	private DominioSituacao indSituacao;
	private RapServidores servidor;
	private Date criadoEm;
	private Integer version;
	
	
	public MtxTipoRetorno() {
		
	}
	
	public MtxTipoRetorno(Integer seq) {
		this.seq = seq;
	}

	public MtxTipoRetorno(Integer seq, String descricao, DominioTipoRetorno indTipo, DominioSituacao indSituacao) {
		this.seq = seq;
		this.descricao = descricao;
		this.indTipo = indTipo;
		this.indSituacao = indSituacao;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxTreSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 200, nullable = false)
	@Length(min = 1, max = 200)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_TIPO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoRetorno getIndTipo() {
		return indTipo;
	}

	public void setIndTipo(DominioTipoRetorno indTipo) {
		this.indTipo = indTipo;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable=false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable=false) })
	@ManyToOne(fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
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
		IND_TIPO("indTipo"),
		IND_SITUACAO("indSituacao"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm");

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
