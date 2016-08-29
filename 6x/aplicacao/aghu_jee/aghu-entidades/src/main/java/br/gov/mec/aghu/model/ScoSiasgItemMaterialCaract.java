package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "SCO_SIASG_ITEM_MATERIAL_CARACT", schema = "AGH")
public class ScoSiasgItemMaterialCaract extends BaseEntityId<ScoSiasgItemMaterialCaractId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3792389480857810393L;

	private ScoSiasgItemMaterialCaractId id;
	private String itInStatusRegistro;
	private Long itCoUsuario;
	private Integer itDaTransacao;
	private Integer itHoTransacao;
	private String itCoNetnameTerminal;
	private String itNoIncItemCaract;
	private String itCoValorItem;
	private String itVaLivreItem;
	private String itVaFaixaItem;
	private Short itNuCaracteristica;
	private Short itNuSequencialItem;
	private String itInStatusNaoSisg;
	private String itNoValorCaractItem;
	private String itSgUnidadeMedidaItem;
	private String itInSustentavel;

	public ScoSiasgItemMaterialCaract() {

	}

	public ScoSiasgItemMaterialCaract(ScoSiasgItemMaterialCaractId id, 
									String itInStatusRegistro, Long itCoUsuario, 
									Integer itDaTransacao, Integer itHoTransacao,
									String itCoNetnameTerminal, String itNoIncItemCaract,
									String itCoValorItem, String itVaLivreItem, 
									String itVaFaixaItem, Short itNuCaracteristica, 
									Short itNuSequencialItem, String itInStatusNaoSisg, 
									String itNoValorCaractItem, String itSgUnidadeMedidaItem,
									String itInSustentavel) {

		this.id = id;
		this.itInStatusRegistro = itInStatusRegistro;
		this.itCoUsuario = itCoUsuario;
		this.itDaTransacao = itDaTransacao;
		this.itHoTransacao = itHoTransacao;
		this.itCoNetnameTerminal = itCoNetnameTerminal;
		this.itNoIncItemCaract = itNoIncItemCaract;
		this.itCoValorItem = itCoValorItem;
		this.itVaLivreItem = itVaLivreItem;
		this.itVaFaixaItem = itVaFaixaItem;
		this.itNuCaracteristica = itNuCaracteristica;
		this.itNuSequencialItem = itNuSequencialItem;
		this.itInStatusNaoSisg = itInStatusNaoSisg;
		this.itNoValorCaractItem = itNoValorCaractItem;
		this.itSgUnidadeMedidaItem = itSgUnidadeMedidaItem;
		this.itInSustentavel = itInSustentavel;
	}
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "itNuItemMaterialCaract", column = @Column(name = "IT_NU_ITEM_MATERIAL_CARACT", length = 9, nullable = false)),
			@AttributeOverride(name = "itCoCaractItem", column = @Column(name = "IT_CO_CARACT_ITEM", length = 4, nullable = false)) })
			
	public ScoSiasgItemMaterialCaractId getId() {
		return id;
	}

	public void setId(ScoSiasgItemMaterialCaractId id) {
		this.id = id;
	}

	@Column(name = "IT_IN_STATUS_REGISTRO", length = 1)
	public String getItInStatusRegistro() {
		return itInStatusRegistro;
	}

	public void setItInStatusRegistro(String itInStatusRegistro) {
		this.itInStatusRegistro = itInStatusRegistro;
	}

	@Column(name = "IT_CO_USUARIO")
	public Long getItCoUsuario() {
		return itCoUsuario;
	}

	public void setItCoUsuario(Long itCoUsuario) {
		this.itCoUsuario = itCoUsuario;
	}

	@Column(name = "IT_DA_TRANSACAO")
	public Integer getItDaTransacao() {
		return itDaTransacao;
	}

	public void setItDaTransacao(Integer itDaTransacao) {
		this.itDaTransacao = itDaTransacao;
	}

	@Column(name = "IT_HO_TRANSACAO")
	public Integer getItHoTransacao() {
		return itHoTransacao;
	}

	public void setItHoTransacao(Integer itHoTransacao) {
		this.itHoTransacao = itHoTransacao;
	}

	@Column(name = "IT_CO_NETNAME_TERMINAL", length = 8)
	public String getItCoNetnameTerminal() {
		return itCoNetnameTerminal;
	}

	public void setItCoNetnameTerminal(String itCoNetnameTerminal) {
		this.itCoNetnameTerminal = itCoNetnameTerminal;
	}

	@Column(name = "IT_NO_INC_ITEM_CARACT", length = 5)
	public String getItNoIncItemCaract() {
		return itNoIncItemCaract;
	}

	public void setItNoIncItemCaract(String itNoIncItemCaract) {
		this.itNoIncItemCaract = itNoIncItemCaract;
	}

	@Column(name = "IT_CO_VALOR_ITEM", length = 6)
	public String getItCoValorItem() {
		return itCoValorItem;
	}

	public void setItCoValorItem(String itCoValorItem) {
		this.itCoValorItem = itCoValorItem;
	}

	@Column(name = "IT_VA_LIVRE_ITEM", length = 40)
	public String getItVaLivreItem() {
		return itVaLivreItem;
	}

	public void setItVaLivreItem(String itVaLivreItem) {
		this.itVaLivreItem = itVaLivreItem;
	}

	@Column(name = "IT_VA_FAIXA_ITEM", length = 50)
	public String getItVaFaixaItem() {
		return itVaFaixaItem;
	}

	public void setItVaFaixaItem(String itVaFaixaItem) {
		this.itVaFaixaItem = itVaFaixaItem;
	}

	@Column(name = "IT_NU_CARACTERISTICA")
	public Short getItNuCaracteristica() {
		return itNuCaracteristica;
	}

	public void setItNuCaracteristica(Short itNuCaracteristica) {
		this.itNuCaracteristica = itNuCaracteristica;
	}

	@Column(name = "IT_NU_SEQUENCIAL_ITEM")
	public Short getItNuSequencialItem() {
		return itNuSequencialItem;
	}

	public void setItNuSequencialItem(Short itNuSequencialItem) {
		this.itNuSequencialItem = itNuSequencialItem;
	}

	@Column(name = "IT_IN_STATUS_NAO_SISG", length = 1)
	public String getItInStatusNaoSisg() {
		return itInStatusNaoSisg;
	}

	public void setItInStatusNaoSisg(String itInStatusNaoSisg) {
		this.itInStatusNaoSisg = itInStatusNaoSisg;
	}

	@Column(name = "IT_NO_VALOR_CARACT_ITEM", length = 50)
	public String getItNoValorCaractItem() {
		return itNoValorCaractItem;
	}

	public void setItNoValorCaractItem(String itNoValorCaractItem) {
		this.itNoValorCaractItem = itNoValorCaractItem;
	}

	@Column(name = "IT_SG_UNIDADE_MEDIDA_ITEM", length = 7)
	public String getItSgUnidadeMedidaItem() {
		return itSgUnidadeMedidaItem;
	}

	public void setItSgUnidadeMedidaItem(String itSgUnidadeMedidaItem) {
		this.itSgUnidadeMedidaItem = itSgUnidadeMedidaItem;
	}

	@Column(name = "IT_IN_SUSTENTAVEL", length = 1)
	public String getItInSustentavel() {
		return itInSustentavel;
	}

	public void setItInSustentavel(String itInSustentavel) {
		this.itInSustentavel = itInSustentavel;
	}
	
	public enum Fields {

		ID("id"),
		IT_IN_STATUS_REGISTRO("itInStatusRegistro"),
		IT_CO_USUARIO("itCoUsuario"),
		IT_DA_TRANSACAO("itDaTransacao"),
		IT_HO_TRANSACAO("itHoTransacao"),
		IT_CO_NETNAME_TERMINAL("itCoNetnameTerminal"),
		IT_NO_INC_ITEM_CARACT("itNoIncItemCaract"),
		IT_CO_VALOR_ITEM("itCoValorItem"),
		IT_VA_LIVRE_ITEM("itVaLivreItem"),
		IT_VA_FAIXA_ITEM("itVaFaixaItem"),
		IT_NU_CARACTERISTICA("itNuCaracteristica"),
		IT_NU_SEQUENCIAL_ITEM("itNuSequencialItem"),
		IT_IN_STATUS_NAO_SISG("itInStatusNaoSisg"),
		IT_NO_VALOR_CARACT_ITEM("itNoValorCaractItem"),
		IT_SG_UNIDADE_MEDIDA_ITEM("itSgUnidadeMedidaItem"),
		IT_IN_SUSTENTAVEL("itInSustentavel");

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
		if (!(obj instanceof ScoSiasgItemMaterialCaract)) {
			return false;
		}
		ScoSiasgItemMaterialCaract other = (ScoSiasgItemMaterialCaract) obj;
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
