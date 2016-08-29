package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;



@Entity
@Table(name = "RAP_PERMISSAO_CONSELHO_PROFISSIONAL", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = {
		"CPR_CODIGO", "PERMISSAO"}))
public class RapPermissaoConselhoProfissional extends BaseEntitySeq<Integer> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1851974206363025257L;

	private Integer seq;
	
	private RapConselhosProfissionais conselhoProfissional;
	
	private String permissao;
	
	
	public RapPermissaoConselhoProfissional(){
		
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 5)
	public Integer getSeq() {
		return seq;
	}


	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CPR_CODIGO", nullable = false)
	public RapConselhosProfissionais getConselhoProfissional() {
		return conselhoProfissional;
	}


	public void setConselhoProfissional(
			RapConselhosProfissionais conselhoProfissional) {
		this.conselhoProfissional = conselhoProfissional;
	}

	@Column(name = "PERMISSAO", nullable = false, length = 30)
	@Length(max = 30)
	public String getPermissao() {
		return permissao;
	}


	public void setPermissao(String permissao) {
		this.permissao = permissao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		RapPermissaoConselhoProfissional other = (RapPermissaoConselhoProfissional) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	public enum Fields {
		SEQ("seq"), CONSELHO_PROFISSIONAL("conselhoProfissional"), PERMISSAO("permissao");

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
