package br.gov.mec.aghu.model.temp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "AIP_FONEMAS", schema = "AGH")
public class AipFonemasRotinaFonema implements java.io.Serializable {

	private static final long serialVersionUID = 8739706678776654366L;
	
	private String fonema;
	private Long contador;
	private Long contadorMae;

	public AipFonemasRotinaFonema() {
	}

	public AipFonemasRotinaFonema(String fonema, Long contador) {
		this.fonema = fonema;
		this.contador = contador;
	}

	public AipFonemasRotinaFonema(String fonema, Long contador, Long contadorMae) {
		this.fonema = fonema;
		this.contador = contador;
		this.contadorMae = contadorMae;
	}

	@Id
	@Column(name = "FONEMA", nullable = false, length = 6)
	@Length(max = 6)
	public String getFonema() {
		return this.fonema;
	}

	public void setFonema(String fonema) {
		this.fonema = fonema;
	}

	@Column(name = "CONTADOR", nullable = false, precision = 11, scale = 0)
	public Long getContador() {
		if (this.contador == null){
			return Long.valueOf(0);
		}
		return this.contador;
	}

	public void setContador(Long contador) {
		this.contador = contador;
	}

	@Column(name = "CONTADOR_MAE", precision = 11, scale = 0)
	public Long getContadorMae() {
		if (this.contadorMae == null){
			return Long.valueOf(0);
		}
		return this.contadorMae;
	}

	public void setContadorMae(Long contadorMae) {
		this.contadorMae = contadorMae;
	}

	public enum Fields {
		FONEMA("fonema");

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
		result = prime * result + ((fonema == null) ? 0 : fonema.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		AipFonemasRotinaFonema other = (AipFonemasRotinaFonema) obj;
		if (fonema == null) {
			if (other.fonema != null) {
				return false;
			}
		} else if (!fonema.equals(other.fonema)) {
			return false;
		}
		return true;
	}
	
}
