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
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;
import br.gov.mec.aghu.dominio.DominioSituacao;


@Entity
@SequenceGenerator(name = "aghSamisJnSq", sequenceName = "AGH.AGH_SAMIS_JN_SQ1")
@Table(name = "AGH_SAMIS_JN", schema = "AGH")
public class AghSamisJn extends BaseEntityCodigo<Short> implements java.io.Serializable {

	private static final long serialVersionUID = 4343268590269924702L;


	private Short codigoJn;
	private String servidorJn;
	private Date dataAlteracaoExclusao;
	private String operacaoJn;
	private Short codigo;
	private String descricao;
	private DominioSituacao indAtivo;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer version;
	private Date criadoEm;

	public AghSamisJn() {
	}

	public AghSamisJn(Short codigoJn,String servidorJn, Date dataAlteracaoExclusao, String operacaoJn, Short codigo, String descricao, DominioSituacao indAtivo, Integer serMatricula, Short serVinCodigo) {
		this.codigoJn = codigoJn;
		this.servidorJn = servidorJn;
		this.dataAlteracaoExclusao = dataAlteracaoExclusao;
		this.operacaoJn = operacaoJn;
		this.codigo = codigo;
		this.descricao = descricao;
		this.indAtivo = indAtivo;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}
	
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghSamisJnSq")
	@Id
	@Column(name = "SEQ_JN", nullable = false, precision = 4, scale = 0)
	public Short getCodigoJn() {
		return this.codigoJn;
	}
	
	public void setCodigoJn(Short codigoJn) {
		this.codigoJn = codigoJn;
	}
	
	@Column(name = "JN_USER", nullable = false, length = 30)
	@NotNull
	@Length(max = 30)
	public String getServidorJn() {
		return servidorJn;
	}

	public void setServidorJn(String servidorJn) {
		this.servidorJn = servidorJn;
	}
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JN_DATE_TIME", nullable = false, length = 7)
	public Date getDataAlteracaoExclusao() {
		return dataAlteracaoExclusao;
	}

	public void setDataAlteracaoExclusao(Date dataAlteracaoExclusao) {
		this.dataAlteracaoExclusao = dataAlteracaoExclusao;
	}
	
	
	@Column(name = "JN_OPERATION", nullable = false, length = 3)
	@NotNull
	@Length(max = 3)
	public String getOperacaoJn() {
		return operacaoJn;
	}

	public void setOperacaoJn(String operacaoJn) {
		this.operacaoJn = operacaoJn;
	}

	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@NotNull
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(DominioSituacao indAtivo) {
		this.indAtivo = indAtivo;
	}
	

	/**
	 * Campo sintético criado para mapear diretamente este dominio booleano em
	 * um componente selectOneCheckBox
	 * 
	 */
	@Transient
	public boolean isAtivo() {
		if (getIndAtivo() != null) {
			return getIndAtivo() == DominioSituacao.A;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setAtivo(boolean valor) {
		setIndAtivo(DominioSituacao.getInstance(valor));
	}

	
	@Column(name = "SER_MATRICULA", precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Transient
	public String getDescricaoAtivo() {
		return isAtivo() ? "Sim" : "Não";
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		CODIGO("codigo"), DESCRICAO("descricao"), IND_ATIVO("indAtivo"), CRIADO_EM(
				"criadoEm");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getCodigo() == null) ? 0 : getCodigo().hashCode());
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
		if (!(obj instanceof AghSamis)) {
			return false;
		}
		AghSamis other = (AghSamis) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}

}