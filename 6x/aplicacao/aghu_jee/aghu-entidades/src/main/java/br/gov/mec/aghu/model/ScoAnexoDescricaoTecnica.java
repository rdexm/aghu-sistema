package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@Entity
@Table(name = "SCO_ANEXOS_DESC_TECNICAS", schema = "AGH")
@SequenceGenerator(name = "scoAdtSq1", sequenceName = "AGH.SCO_ADT_SQ1", allocationSize = 1)
public class ScoAnexoDescricaoTecnica extends BaseEntityCodigo<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 140181127471237525L;
	/**
	 * 
	 */
	
	private Integer codigo;
	private ScoDescricaoTecnicaPadrao descricaoTecnica;
	private String arquivo;
	private byte[] anexo;
	private Integer versao;
	private String descricao;
	
	public ScoAnexoDescricaoTecnica() {}
	
	@Id
	@Column(name = "CODIGO", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoAdtSq1")
	public Integer getCodigo() {
		return codigo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CODIGO_DESCRICAO", referencedColumnName="CODIGO", nullable = false)
	public ScoDescricaoTecnicaPadrao getDescricaoTecnica() {
		return descricaoTecnica;
	}
	
	@Column(name = "ARQUIVO", nullable = false)
	public String getArquivo() {
		return arquivo;
	}
	
	@Lob
	@Column(name = "ANEXO", nullable = false)
	@Type(type="org.hibernate.type.BinaryType")
	public byte[] getAnexo() {
		return anexo;
	}
	
	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersao() {
		return versao;
	}
	
	@Column(name = "DESCRICAO", length = 50, nullable = false)
	public String getDescricao() {
		return descricao;
	}
	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public void setDescricaoTecnica(ScoDescricaoTecnicaPadrao descricao) {
		this.descricaoTecnica = descricao;
	}
	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}
	public void setAnexo(byte[] anexo) {
		this.anexo = anexo;
	}
	public void setVersao(Integer versao) {
		this.versao = versao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		if (!(obj instanceof ScoAnexoDescricaoTecnica)) {
			return false;
		}
		ScoAnexoDescricaoTecnica other = (ScoAnexoDescricaoTecnica) obj;
		if (codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!codigo.equals(other.codigo)) {
			return false;
		}
		return true;
	}
	
	
	
}