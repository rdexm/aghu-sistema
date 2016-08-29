package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "AIP_ETNIAS", schema = "AGH")
public class AipEtnia extends BaseEntityId<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3382722758239756046L;
	private Integer id;
	private String descricao;
	
	
	@Id
	@Column(name = "ID", updatable = false, nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 100)
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public enum Fields {

		ID("id"),
		DESCRICAO("descricao");

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
	public String toString() {
		return this.descricao;
	}

}
