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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioOrigemMaterialFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * 
 * @author julianosena
 *
 */
@Entity
@Immutable
@Table(name="SCE_REL_MAT_FORN_JN", schema = "AGH")
@SequenceGenerator(name = "sceRmfJnSql1", sequenceName = "AGH.SCE_RMF_JN_SEQ", allocationSize = 1)
public class SceRelacionamentoMaterialFornecedorJn extends BaseJournal {

	private static final long serialVersionUID = 3404683684088931916L;

	private Long seq;
	private ScoFornecedor scoFornecedor;
	private ScoMaterial scoMaterial;
	private String codigoMaterialFornecedor;
	private String descricaoMaterialFornecedor;
	private DominioSituacao situacao;
	private DominioOrigemMaterialFornecedor origem;
	private RapServidores matriculaVinCodigo;
	private Date dtCriacao;
	private RapServidores matriculaVinCodigoAlteracao;
	private Date dtAlteracao;
	private Integer version;
	private String usuarioAlteracao;
	private Integer serMatriculaAlteracao;
	private Short serVinCodigoAlteracao;

	public enum Fields {
		SEQ("seq"),
		FORNECEDOR("scoFornecedor"),
		MATERIAL("scoMaterial"),
		CODIGO_MATERIAL_FORNECEDOR("codigoMaterialFornecedor"),
		DESCRICAO_MATERIAL_FORNECEDOR("descricaoMaterialFornecedor"),
		SITUACAO("situacao"),
		ORIGEM("origem"),
		RAP_SERVIDORES("matriculaVinCodigo"),
		VINCODIGO("matriculaVinCodigo.vinCodigo"),
		DATA_CRIACAO("dtCriacao"),
		RAP_SERVIDORES_ALTERACAO("matriculaVinCodigoAlteracao"),
		SER_MATRICULA_ALTERACAO("serMatriculaAlteracao"),
		SER_VINCODIGO_ALTERACAO("serVinCodigoAlteracao"),
		DATA_ALTERACAO("dtAlteracao"),
		VERSION("version");

		private String value;

		Fields(String value){
			this.value = value;
		}

		/**
		 * Retorna o valor do enum para selects
		 * 
		 * @return
		 */
		public String getValue(){
			return this.value;
		}

		@Override
		public String toString(){
			return this.getValue();
		}
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceRmfJnSql1")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name="SEQ")
	@NotNull
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long sequence) {
		this.seq = sequence;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FRN_NUMERO", referencedColumnName="NUMERO")
	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="MAT_CODIGO", referencedColumnName="CODIGO")
	public ScoMaterial getScoMaterial() {
		return scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	@Column(name="COD_MAT_FORN")
	public String getCodigoMaterialFornecedor() {
		return codigoMaterialFornecedor;
	}

	public void setCodigoMaterialFornecedor(String codigoMaterialFornecedor) {
		this.codigoMaterialFornecedor = codigoMaterialFornecedor;
	}

	@Column(name="DESC_MAT_FORN")
	public String getDescricaoMaterialFornecedor() {
		return descricaoMaterialFornecedor;
	}

	public void setDescricaoMaterialFornecedor(String descricaoMaterialFornecedor) {
		this.descricaoMaterialFornecedor = descricaoMaterialFornecedor;
	}

	@Column(name="IND_SITUACAO")
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name="IND_ORIGEM")
	@Enumerated(EnumType.STRING)
	public DominioOrigemMaterialFornecedor getOrigem() {
		return origem;
	}

	public void setOrigem(DominioOrigemMaterialFornecedor origem) {
		this.origem = origem;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({  
        @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),  
        @JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")
    })
	public RapServidores getMatriculaVinCodigo() {
		return matriculaVinCodigo;
	}

	public void setMatriculaVinCodigo(RapServidores matriculaVinCodigo) {
		this.matriculaVinCodigo = matriculaVinCodigo;
	}

	@Column(name="DT_CRIACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtCriacao() {
		return dtCriacao;
	}

	public void setDtCriacao(Date dtCriacao) {
		this.dtCriacao = dtCriacao;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({  
        @JoinColumn(name = "SER_MATRICULA_ALTERACAO", referencedColumnName = "MATRICULA"),  
        @JoinColumn(name = "SER_VIN_CODIGO_ALTERACAO", referencedColumnName = "VIN_CODIGO")
    })
	public RapServidores getMatriculaVinCodigoAlteracao() {
		return matriculaVinCodigoAlteracao;
	}

	public void setMatriculaVinCodigoAlteracao(
			RapServidores matriculaVinCodigoAlteracao) {
		this.matriculaVinCodigoAlteracao = matriculaVinCodigoAlteracao;
	}

	@Column(name="DT_ALTERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAlteracao() {
		return dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}

	@Transient
	public String getUsuarioAlteracao() {
		return usuarioAlteracao;
	}

	public void setUsuarioAlteracao(String usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	@Transient
	public Integer getSerMatriculaAlteracao() {
		return serMatriculaAlteracao;
	}

	public void setSerMatriculaAlteracao(Integer serMatriculaAlteracao) {
		this.serMatriculaAlteracao = serMatriculaAlteracao;
	}

	@Transient
	public Short getSerVinCodigoAlteracao() {
		return serVinCodigoAlteracao;
	}

	public void setSerVinCodigoAlteracao(Short serVinCodigoAlteracao) {
		this.serVinCodigoAlteracao = serVinCodigoAlteracao;
	}

	@Column(name="VERSION")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}