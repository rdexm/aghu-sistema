package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@SequenceGenerator(name="scoJpSq1", sequenceName="SCO_JP_SQ1", allocationSize = 1)
@Table(name="SCO_JUSTIFICATIVA_PRECO" , schema = "AGH")
public class ScoJustificativaPreco extends BaseEntityCodigo<Short> implements Serializable {
	
	private static final long serialVersionUID = 1237194890613149890L;
	
	private Short codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	private Integer version;
	
	public ScoJustificativaPreco() {
	}
	  
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator = "scoJpSq1")
	@Column(name = "CODIGO", unique = true, nullable = false, precision = 3, scale = 0)
	public Short getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "DESCRICAO", nullable = true, length = 240)
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {

		CODIGO("codigo"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao");

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
		return new HashCodeBuilder().append(getCodigo()).hashCode();		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof ScoJustificativaPreco) ){
			return false;
		}
		ScoJustificativaPreco other = (ScoJustificativaPreco) obj;
		return new EqualsBuilder().append(this.getCodigo(), other.getCodigo()).isEquals();
				
	}
}
