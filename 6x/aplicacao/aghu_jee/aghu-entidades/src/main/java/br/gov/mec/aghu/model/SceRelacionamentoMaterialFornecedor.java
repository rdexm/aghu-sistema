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

import br.gov.mec.aghu.dominio.DominioOrigemMaterialFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * 
 * @author julianosena
 *
 */
@Entity
@Table(name="SCE_REL_MAT_FORN", schema = "AGH")
@SequenceGenerator(name = "sceRmfSql1", sequenceName = "AGH.SCE_RMF_SQ1", allocationSize = 1)
public class SceRelacionamentoMaterialFornecedor extends BaseEntitySeq<Long> {

	private static final long serialVersionUID = 3404683684088931916L;

	private Long seq;
	private ScoFornecedor scoFornecedor;
	private ScoMaterial scoMaterial;
	private String codigoMaterialFornecedor;
	private String descricaoMaterialFornecedor;
	private DominioSituacao situacao;
	private DominioOrigemMaterialFornecedor origem;
	private RapServidores matriculaVinCodigo;
	private Date dataCriacao;
	private RapServidores matriculaVinCodigoAlteracao;
	private Date dataAlteracao;
	private Integer version;

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
		DATA_CRIACAO("dataCriacao"),
		RAP_SERVIDORES_ALTERACAO("matriculaVinCodigoAlteracao"),
		DATA_ALTERACAO("dataAlteracao"),
		VERSION("version"),
		MATERIAL_CODIGO("scoMaterial.codigo"),
		FORNECEDOR_NUMERO("scoFornecedor.numero");
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceRmfSql1")
	@Column(name="SEQ")
	public Long getSeq() {
		return this.seq;
	}

	@Override
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
	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
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
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	@Version
	@Column(name="VERSION")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}