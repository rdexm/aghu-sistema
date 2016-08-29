package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * @author fpalma
 * 
 */
@Entity
@Table(name = "V_MBC_PROF_SERVIDOR", schema = "AGH")
@Immutable
public class VMbcProfServidor extends BaseEntityId<VMbcProfServidorId> implements java.io.Serializable {

	private static final long serialVersionUID = -2268817363721157254L;

	private VMbcProfServidorId id;

	private String nomePessoa;
	private String nomeUsualPessoa;
	private Integer nroCartProfissional;
	private DominioSituacao situacao;
	private AghUnidadesFuncionais unidadeFuncional;
	private RapServidores servidor;
	private DominioFuncaoProfissional indFuncaoProf;

	public VMbcProfServidor() {
	}

	public VMbcProfServidor(VMbcProfServidorId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "unfSeq", column = @Column(name = "PUC_UNF_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "indFuncaoProf", column = @Column(name = "PUC_IND_FUNCAO_PROF", nullable = false, length = 3)) })
	public VMbcProfServidorId getId() {
		return this.id;
	}
	
	public void setId(VMbcProfServidorId id) {
		this.id = id;
	}
	
	@Column(name = "PES_NOME", nullable = false, length = 50)
	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	@Column(name = "PES_NOME_USUAL", nullable = false, length = 30)
	public String getNomeUsualPessoa() {
		return nomeUsualPessoa;
	}

	public void setNomeUsualPessoa(String nomeUsualPessoa) {
		this.nomeUsualPessoa = nomeUsualPessoa;
	}

	@Column(name = "PES_NRO_CART_PROFISSIONAL", precision = 9, scale = 0)
	public Integer getNroCartProfissional() {
		return nroCartProfissional;
	}

	public void setNroCartProfissional(Integer nroCartProfissional) {
		this.nroCartProfissional = nroCartProfissional;
	}

	@Column(name = "PUC_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", nullable = false, updatable = false, insertable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", nullable = false, updatable = false, insertable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "PUC_UNF_SEQ", referencedColumnName = "SEQ", nullable = false,insertable=false, updatable=false) })
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	
	@Column(name = "PUC_IND_FUNCAO_PROF", nullable = false, insertable = false, updatable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioFuncaoProfissional getIndFuncaoProf() {
		return indFuncaoProf;
	}

	public void setIndFuncaoProf(DominioFuncaoProfissional indFuncaoProf) {
		this.indFuncaoProf = indFuncaoProf;
	}
	
	public enum Fields {
		SER_MATRICULA("id.serMatricula"),
		SER_VIN_CODIGO("id.serVinCodigo"),
		UNF_SEQ("id.unfSeq"),
		IND_FUNCAO_PROF("id.indFuncaoProf"),
		NOME_PESSOA("nomePessoa"),
		NOME_USUAL_PESSOA("nomeUsualPessoa"),
		NRO_CART_PROF("nroCartProfissional"),
		SITUACAO("situacao"),
		UNIDADE_FUNCIONAL("unidadeFuncional"),
		SERVIDOR("servidor"),
		FUNCAO("indFuncaoProf");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof VMbcProfServidor)) {
			return false;
		}
		VMbcProfServidor other = (VMbcProfServidor) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

}
