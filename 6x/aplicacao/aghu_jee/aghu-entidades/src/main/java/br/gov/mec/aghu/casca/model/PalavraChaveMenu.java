package br.gov.mec.aghu.casca.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "CSC_PALAVRAS_CHAVE_MENU", schema = "CASCA")
@SequenceGenerator(name="cscPcmSeq", sequenceName="AGH.CASCA_DSH_SEQ", allocationSize = 1)
public class PalavraChaveMenu extends BaseEntityId<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1408174649522056620L;
	
	private Integer id;
	private Menu menu;
	private String palavra;
	private Boolean ativo;

	@Id
	@Column(name = "ID", nullable = false, precision = 8, scale = 0)
	@GeneratedValue(strategy=GenerationType.AUTO, generator="cscPcmSeq")
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_MENU")
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	@Column(name = "PALAVRA", nullable = false)
	public String getPalavra() {
		return palavra;
	}

	public void setPalavra(String palavra) {
		this.palavra = palavra;
	}

	@Column(name = "ATIVO", nullable = false, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
	
	public enum Fields {
		ID("id"), USUARIO("usuario"), MENU("menu"), ATIVO("ativo"), PALAVRA("palavra");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
