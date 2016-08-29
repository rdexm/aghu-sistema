package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


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
@Table(name="SCE_CONTROLE_SOLIC_MATERIAIS")
public class SceControleSolicMaterial extends BaseEntitySeq<Integer> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5307086107148360097L;
	
	private Integer seq;
	private ScoClassifMatNiv5 scoClassifMatNiv5;
	private Integer version;
	private String csePerfil;
	
	public enum Fields {
		SEQ("seq"),
		PERFIL("csePerfil"),
		CLASSIF_MAT_NIV5("scoClassifMatNiv5");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

    public SceControleSolicMaterial() {
    }


	@Id
	@SequenceGenerator(name="SCE_CONTROLE_SOLIC_MATERIAIS_SEQ_GENERATOR", sequenceName="AGH.SCE_CSM_SQ1", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCE_CONTROLE_SOLIC_MATERIAIS_SEQ_GENERATOR")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne
	@JoinColumn(name = "CN5_NUMERO", nullable = false)
	public ScoClassifMatNiv5 getScoClassifMatNiv5() {
		return this.scoClassifMatNiv5;
	}

	public void setScoClassifMatNiv5(ScoClassifMatNiv5 cn5Numero) {
		this.scoClassifMatNiv5 = cn5Numero;
	}


	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name="PER_NOME")
	public String getCsePerfil() {
		return this.csePerfil;
	}

	public void setCsePerfil(String csePerfil) {
		this.csePerfil = csePerfil;
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
		if (!(obj instanceof SceControleSolicMaterial)) {
			return false;
		}
		SceControleSolicMaterial other = (SceControleSolicMaterial) obj;
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