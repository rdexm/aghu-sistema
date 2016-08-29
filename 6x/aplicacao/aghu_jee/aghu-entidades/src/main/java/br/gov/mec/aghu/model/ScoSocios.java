package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "SCO_SOCIOS", schema = "AGH")
@SequenceGenerator(name="scoSocSq1", sequenceName="AGH.SCO_SOC_SQ1" , allocationSize = 1)
public class ScoSocios  extends BaseEntitySeq<Integer>  implements Serializable{
	private static final long serialVersionUID = 18843859345783L;
	
	private Integer seq;
	private String nome;
	private String rg;
	private Long cpf;
	private Integer version;
	
	public ScoSocios(){
	}
	
	@Id
	@Column(name = "SEQ", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoSocSq1")
	public Integer getSeq() {
		return this.seq;
	}
	
	public void setSeq(Integer seq){
		this.seq = seq;
	}

	@Column(name = "NOME", length = 50)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Column(name = "cpf")
	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}
	
	@Column(name = "NRO_IDENTIDADE", length = 14)
	public String getRg() {
		return rg;
	}
	
	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		SEQ("seq"),
	    NOME("nome"),
	    RG("rg"),
	    CPF("cpf");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ScoSocios){
			ScoSocios castOther = (ScoSocios) other;
			return new EqualsBuilder()
				.append(this.seq, castOther.seq)
			.isEquals();
		}
		else {
			return false;
		}	
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}
	
}
