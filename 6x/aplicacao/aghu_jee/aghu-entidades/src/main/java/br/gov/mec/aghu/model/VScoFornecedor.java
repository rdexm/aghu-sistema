package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityNumero;


/**
 * The persistent class for the V_SCO_FORNECEDOR database table.
 * 
 */
@Entity
@Table(name="V_SCO_FORNECEDOR")
@Immutable
public class VScoFornecedor extends BaseEntityNumero<Integer> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -6112739577949956060L;
	
	private Long cgcCpf;
	private Integer numeroFornecedor;
	private String razaoSocial;
	private String tipo;
	
	@Id
	@Column(name = "FRN_NUMERO", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}
	
	@Column(name = "CGC_CPF", unique = true, scale = 0)
	public Long getCgcCpf() {
		return cgcCpf;
	}

	public void setCgcCpf(Long cgcCpf) {
		this.cgcCpf = cgcCpf;
	}
	
	@Column(name = "RAZAO_SOCIAL", nullable = false, length = 60)
	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	@Column(name = "TIPO", nullable = false, length = 3)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public enum Fields {
			
		CGC_CPF("cgcCpf"),
		NRO_FORNECEDOR("numeroFornecedor"),
		RAZAO_SOCIAL("razaoSocial"),		    
		TIPO("tipo");
		    
		    private String fields;

			private Fields(String fields) {
				this.fields = fields;
			}

			@Override
			public String toString() {
				return fields;
			}
	}

    public VScoFornecedor() {
    }	
 
 @Transient public Integer getNumero(){ return this.getNumeroFornecedor();} 
 public void setNumero(Integer numero){ this.setNumeroFornecedor(numero);}
}