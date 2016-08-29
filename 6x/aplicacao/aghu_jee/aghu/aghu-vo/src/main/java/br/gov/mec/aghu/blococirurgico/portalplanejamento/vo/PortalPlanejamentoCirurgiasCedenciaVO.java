package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.RapServidores;

public class PortalPlanejamentoCirurgiasCedenciaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3554477335236957655L;
	
	private Short sala;
	private String turno; 
	private Date data;
	private RapServidores equipeOriginal;
	private RapServidores equipeRecebe;
	private MbcProfAtuaUnidCirgs equipe;
	
	public Short getSala() {
		return sala;
	}
	
	public void setSala(Short sala) {
		this.sala = sala;
	}
	
	public String getTurno() {
		return turno;
	}
	
	public void setTurno(String turno) {
		this.turno = turno;
	}
	
	public Date getData() {
		return data;
	}
	
	public void setData(Date data) {
		this.data = data;
	}
	
	public RapServidores getEquipeOriginal() {
		return equipeOriginal;
	}

	public void setEquipeOriginal(RapServidores equipeOriginal) {
		this.equipeOriginal = equipeOriginal;
	}

	public RapServidores getEquipeRecebe() {
		return equipeRecebe;
	}

	public void setEquipeRecebe(RapServidores equipeRecebe) {
		this.equipeRecebe = equipeRecebe;
	}

	public MbcProfAtuaUnidCirgs getEquipe() {
		return equipe;
	}

	public void setEquipe(MbcProfAtuaUnidCirgs equipe) {
		this.equipe = equipe;
	}



	public enum Fields {
		SALA("sala"),
		TURNO("turno"),
		DATA("data"),
		EQUIPE_ORIGINAL("equipeOriginal"),
		EQUIPE_RECEBE("equipeRecebe"),
		EQUIPE("equipe"); 

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
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getData());
		umHashCodeBuilder.append(this.getEquipeOriginal());
		umHashCodeBuilder.append(this.getEquipeRecebe());
		umHashCodeBuilder.append(this.getSala());
		umHashCodeBuilder.append(this.getTurno());
		return umHashCodeBuilder.toHashCode();

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PortalPlanejamentoCirurgiasCedenciaVO)) {
			return false;
		}
		PortalPlanejamentoCirurgiasCedenciaVO other = (PortalPlanejamentoCirurgiasCedenciaVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getData(), other.getData());
		umEqualsBuilder.append(this.getEquipeOriginal(), other.getEquipeOriginal());
		umEqualsBuilder.append(this.getEquipeRecebe(), other.getEquipeRecebe());
		umEqualsBuilder.append(this.getSala(), other.getSala());
		umEqualsBuilder.append(this.getTurno(), other.getTurno());
		return umEqualsBuilder.isEquals();
	}

}
