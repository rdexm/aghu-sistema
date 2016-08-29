package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sce_producao_internas database table.
 * 
 */
@Entity
@SequenceGenerator(name="scePriSq1", sequenceName="AGH.SCE_PRI_SQ1", allocationSize = 1)
@Table(name="SCE_PRODUCAO_INTERNAS")
public class SceProducaoInterna extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 381301377989935904L;
	private Integer seq;
	//private ScePacoteMateriais pacoteMaterial;
	private Integer pmtCctCodigo;
	private Integer pmtCctCodigoRefere;
	private Integer pmtNumero;
	private Integer almSeq;
	private Timestamp dtEfetivacao;
	private Timestamp dtEstorno;
	private Timestamp dtGeracao;
	private String indEstorno;
	private String indProducaoInterna;
	private String indSituacao;
	private Integer serMatricula;
	private Integer serMatriculaEfetivado;
	private Integer serMatriculaEstornado;
	private Integer serVinCodigo;
	private Integer serVinCodigoEfetivado;
	private Integer serVinCodigoEstornado;
	private Integer tmvComplemento;
	private Integer tmvSeq;
	private Integer version;

    public SceProducaoInterna() {
    }

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scePriSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 7, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name="ALM_SEQ")
	public Integer getAlmSeq() {
		return this.almSeq;
	}

	public void setAlmSeq(Integer almSeq) {
		this.almSeq = almSeq;
	}

	@Column(name="DT_EFETIVACAO")
	public Timestamp getDtEfetivacao() {
		return this.dtEfetivacao;
	}

	public void setDtEfetivacao(Timestamp dtEfetivacao) {
		this.dtEfetivacao = dtEfetivacao;
	}

	@Column(name="DT_ESTORNO")
	public Timestamp getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Timestamp dtEstorno) {
		this.dtEstorno = dtEstorno;
	}

	@Column(name="DT_GERACAO")
	public Timestamp getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Timestamp dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	@Column(name="IND_ESTORNO")
	public String getIndEstorno() {
		return this.indEstorno;
	}

	public void setIndEstorno(String indEstorno) {
		this.indEstorno = indEstorno;
	}

	@Column(name="IND_PRODUCAO_INTERNA")
	public String getIndProducaoInterna() {
		return this.indProducaoInterna;
	}

	public void setIndProducaoInterna(String indProducaoInterna) {
		this.indProducaoInterna = indProducaoInterna;
	}

	@Column(name="IND_SITUACAO")
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

/*	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({
			@JoinColumn(name = "PMT_CCT_CODIGO_REFERE", referencedColumnName = "CCT_CODIGO_REFERE"),
			@JoinColumn(name = "PMT_CCT_CODIGO", referencedColumnName = "CCT_CODIGO"), 
			@JoinColumn(name = "PMT_NUMERO", referencedColumnName = "NUMERO") })
	public ScePacoteMateriais getPacoteMaterial() {
		return pacoteMaterial;
	}

	public void setPacoteMaterial(ScePacoteMateriais pacoteMaterial) {
		this.pacoteMaterial = pacoteMaterial;
	}*/

	@Column(name="SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name="SER_MATRICULA_EFETIVADO")
	public Integer getSerMatriculaEfetivado() {
		return this.serMatriculaEfetivado;
	}

	public void setSerMatriculaEfetivado(Integer serMatriculaEfetivado) {
		this.serMatriculaEfetivado = serMatriculaEfetivado;
	}

	@Column(name="SER_MATRICULA_ESTORNADO")
	public Integer getSerMatriculaEstornado() {
		return this.serMatriculaEstornado;
	}

	public void setSerMatriculaEstornado(Integer serMatriculaEstornado) {
		this.serMatriculaEstornado = serMatriculaEstornado;
	}

	@Column(name="SER_VIN_CODIGO")
	public Integer getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name="SER_VIN_CODIGO_EFETIVADO")
	public Integer getSerVinCodigoEfetivado() {
		return this.serVinCodigoEfetivado;
	}

	public void setSerVinCodigoEfetivado(Integer serVinCodigoEfetivado) {
		this.serVinCodigoEfetivado = serVinCodigoEfetivado;
	}

	@Column(name="SER_VIN_CODIGO_ESTORNADO")
	public Integer getSerVinCodigoEstornado() {
		return this.serVinCodigoEstornado;
	}

	public void setSerVinCodigoEstornado(Integer serVinCodigoEstornado) {
		this.serVinCodigoEstornado = serVinCodigoEstornado;
	}

	@Column(name="TMV_COMPLEMENTO")
	public Integer getTmvComplemento() {
		return this.tmvComplemento;
	}

	public void setTmvComplemento(Integer tmvComplemento) {
		this.tmvComplemento = tmvComplemento;
	}

	@Column(name="TMV_SEQ")
	public Integer getTmvSeq() {
		return this.tmvSeq;
	}

	public void setTmvSeq(Integer tmvSeq) {
		this.tmvSeq = tmvSeq;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name="PMT_CCT_CODIGO")
	public Integer getPmtCctCodigo() {
		return pmtCctCodigo;
	}

	public void setPmtCctCodigo(Integer pmtCctCodigo) {
		this.pmtCctCodigo = pmtCctCodigo;
	}

	@Column(name="PMT_CCT_CODIGO_REFERE")
	public Integer getPmtCctCodigoRefere() {
		return pmtCctCodigoRefere;
	}

	public void setPmtCctCodigoRefere(Integer pmtCctCodigoRefere) {
		this.pmtCctCodigoRefere = pmtCctCodigoRefere;
	}

	@Column(name="PMT_NUMERO")
	public Integer getPmtNumero() {
		return pmtNumero;
	}

	public void setPmtNumero(Integer pmtNumero) {
		this.pmtNumero = pmtNumero;
	}

	/**
	 * 
	 * @author aghu
	 *
	 */
	public enum Fields {
		SEQ("seq"), 
		CENTRO_CUSTO_PROPRIETARIO("pmtCctCodigoRefere"),
		CENTRO_CUSTO_APLICACAO("pmtCctCodigo"),
		NUMERO_PACOTE_MATERIAL("pmtNumero");
		//PACOTE_MATERIAL("pacoteMaterial");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof SceProducaoInterna)) {
			return false;
		}
		SceProducaoInterna other = (SceProducaoInterna) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}