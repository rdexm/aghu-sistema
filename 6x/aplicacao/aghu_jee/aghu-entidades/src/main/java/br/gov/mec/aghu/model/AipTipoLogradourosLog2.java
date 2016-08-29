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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * @author fbraganca
 */
@Entity
@Table(name = "AIP_TIPO_LOGRADOUROS_LOG2", schema = "AGH")
@SequenceGenerator(name="aipTipoLogradourosLog2Sq1", sequenceName="AGH.AIP_TLG_LG2_SQ1")
public class AipTipoLogradourosLog2 extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2981186959082780666L;
	private Integer seq;
	private Integer codigo;
	private String abreviatura;
	private String descricao;
	private Integer codNovo;
	private Integer version;
	
	public AipTipoLogradourosLog2() {
		
	}
	
	public AipTipoLogradourosLog2(Integer seq, Integer codigo, String abreviatura,
			String descricao, Integer codNovo, Integer version) {
		this.seq = seq;
		this.codigo = codigo;
		this.abreviatura = abreviatura;
		this.descricao = descricao;
		this.codNovo = codNovo;
		this.version = version;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipTipoLogradourosLog2Sq1")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "CODIGO", nullable = false)
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "ABREVIATURA", nullable = false, length = 12)
	@Length(max = 12)
	public String getAbreviatura() {
		return abreviatura;
	}
	public void setAbreviatura(String abreviatura) {
		this.abreviatura = abreviatura;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "NOVO_CODIGO")
	public Integer getCodNovo() {
		return codNovo;
	}
	public void setCodNovo(Integer codNovo) {
		this.codNovo = codNovo;
	}
	
	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		SEQ("seq"),
		CODIGO("codigo"), 
		ABREVIATURA("abreviatura"), 
		DESCRICAO("descricao"),
		NOVO_CODIGO("codNovo");
		
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		AipTipoLogradourosLog2 other = (AipTipoLogradourosLog2) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}
			
}
