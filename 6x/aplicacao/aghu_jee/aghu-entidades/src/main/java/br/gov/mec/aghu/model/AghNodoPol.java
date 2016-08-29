package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * AghNodoPol criado por tfelini
 */
@Entity
@Table(name = "AGH_NODOS_POL", schema = "AGH")
public class AghNodoPol extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -6191395618118206283L;

	private Integer seq;
	private String nome;
	private String descricao;
	private DominioSituacao status;
	private Date alteradoEm;
	private String alteradoPor;
	private Integer ordem;
	private String icone;

	public AghNodoPol() {
	}

	@Id
	@Column(name = "SEQ",  nullable = false, precision = 6, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NOME", unique = true, nullable = false, length =30)
	@Length(max = 30)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "ALTERADO_POR", length = 30)
	@Length(max = 30)
	public String getAlteradoPor() {
		return this.alteradoPor;
	}

	public void setAlteradoPor(String umNomeUsuario) {
		this.alteradoPor = (umNomeUsuario != null && umNomeUsuario.length()>30) ? umNomeUsuario.substring(0, 30) : umNomeUsuario;
	}

	@Column(name = "DESCRICAO", length = 80, nullable = false)
	@Length(max = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "STATUS", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getStatus() {
		return status;
	}

	public void setStatus(DominioSituacao status) {
		this.status = status;
	}
	
	
	@Column(name = "ORDEM", precision = 6, scale = 0, nullable = false)
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@Column(name = "ICONE", length = 2000)
	@Length(max = 2000)
	public String getIcone() {
		return icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}


	public enum Fields {
		NOME("nome"), DESCRICAO("descricao"), ORDEM("ordem");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof AghNodoPol)) {
			return false;
		}
		AghNodoPol other = (AghNodoPol) obj;
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
