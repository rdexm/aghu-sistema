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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;


/**
 * The persistent class for the abs_proced_hemoterapicos_jn database table.
 * 
 */
@Entity
@Table(name="ABS_PROCED_HEMOTERAPICOS_JN", schema = "AGH")
@SequenceGenerator(name="absPhejJnSeq", sequenceName="AGH.ABS_PHE_jn_seq", allocationSize = 1)
@Immutable
public class AbsProcedHemoterapicosJn extends BaseJournal implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 42648480667292293L;
	private String codigo;
	private Date criadoEm;
	private String descricao;
	private Boolean indAmostra;
	private Boolean indJustificativa;
	private DominioSituacao indSituacao;
	private String informacoes;
	private RapServidores rapServidores;

    public AbsProcedHemoterapicosJn() {
    }

    @Id
    @Column(name = "SEQ_JN", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "absPhejJnSeq")
    @Override
    public Integer getSeqJn() {
        return super.getSeqJn();
    }
    
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	@Column(name="IND_AMOSTRA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAmostra() {
		return this.indAmostra;
	}

	public void setIndAmostra(Boolean indAmostra) {
		this.indAmostra = indAmostra;
	}


	@Column(name = "IND_JUSTIFICATIVA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndJustificativa() {
		return this.indJustificativa;
	}

	public void setIndJustificativa(Boolean indJustificativa) {
		this.indJustificativa = indJustificativa;
	}


	@Column(name="IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Lob
	@Type(type="text")
	@Column(name = "INFORMACOES")
	public String getInformacoes() {
		return this.informacoes;
	}

	public void setInformacoes(String informacoes) {
		this.informacoes = informacoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = true),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = true) })
	public RapServidores getRapServidores() {		
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public enum Fields {
		CODIGO("codigo"), 
		CRIADO_EM("criadoEm"),
		DESCRICAO("descricao"), 
		IND_AMOSTRA("indAmostra"), 
		IND_JUSTIFICATIVA("indJustificativa"), 
		IND_SITUACAO("indSituacao"), 
		INFORMACOES("informacoes"), 
		SERVIDOR("rapServidores");
		
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