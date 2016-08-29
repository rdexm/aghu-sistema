package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "LWS_COM_ANTIBIOGRAMA", schema = "AGH")
public class LwsComAntibiograma extends BaseEntityId<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6793571366880272319L;
	private Long id;
	private LwsComResultado lwsComResultado;
	private String codGermeLis;
	private String codDrogaLis;
	private String infoOrganismo;
	private Double mic;
	private String ris;
	private Integer dia;

	public LwsComAntibiograma() {
	}

	public LwsComAntibiograma(Long id, LwsComResultado lwsComResultado) {
		this.id = id;
		this.lwsComResultado = lwsComResultado;
	}

	public LwsComAntibiograma(Long id, LwsComResultado lwsComResultado, String codGermeLis, String codDrogaLis,
			String infoOrganismo, Double mic, String ris, Integer dia) {
		this.id = id;
		this.lwsComResultado = lwsComResultado;
		this.codGermeLis = codGermeLis;
		this.codDrogaLis = codDrogaLis;
		this.infoOrganismo = infoOrganismo;
		this.mic = mic;
		this.ris = ris;
		this.dia = dia;
	}

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_RESULTADO", nullable = false)
	public LwsComResultado getLwsComResultado() {
		return this.lwsComResultado;
	}

	public void setLwsComResultado(LwsComResultado lwsComResultado) {
		this.lwsComResultado = lwsComResultado;
	}

	@Column(name = "COD_GERME_LIS", length = 20)
	@Length(max = 20)
	public String getCodGermeLis() {
		return this.codGermeLis;
	}

	public void setCodGermeLis(String codGermeLis) {
		this.codGermeLis = codGermeLis;
	}

	@Column(name = "COD_DROGA_LIS", length = 20)
	@Length(max = 20)
	public String getCodDrogaLis() {
		return this.codDrogaLis;
	}

	public void setCodDrogaLis(String codDrogaLis) {
		this.codDrogaLis = codDrogaLis;
	}

	@Column(name = "INFO_ORGANISMO", length = 300)
	@Length(max = 300)
	public String getInfoOrganismo() {
		return this.infoOrganismo;
	}

	public void setInfoOrganismo(String infoOrganismo) {
		this.infoOrganismo = infoOrganismo;
	}

	@Column(name = "MIC", precision = 17, scale = 17)
	public Double getMic() {
		return this.mic;
	}

	public void setMic(Double mic) {
		this.mic = mic;
	}

	@Column(name = "RIS", length = 1)
	@Length(max = 1)
	public String getRis() {
		return this.ris;
	}

	public void setRis(String ris) {
		this.ris = ris;
	}

	@Column(name = "DIA")
	public Integer getDia() {
		return this.dia;
	}

	public void setDia(Integer dia) {
		this.dia = dia;
	}

	public enum Fields {

		ID("id"),
		LWS_COM_RESULTADOS("lwsComResultado"),
		COD_GERME_LIS("codGermeLis"),
		COD_DROGA_LIS("codDrogaLis"),
		INFO_ORGANISMO("infoOrganismo"),
		MIC("mic"),
		RIS("ris"),
		DIA("dia");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
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
		if (!(obj instanceof LwsComAntibiograma)) {
			return false;
		}
		LwsComAntibiograma other = (LwsComAntibiograma) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
