package br.gov.mec.aghu.compras.contaspagar.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.commons.CoreUtil;

public class FornecedorVO implements Serializable {

	private static final long serialVersionUID = -8562743377716022507L;

	private Integer numero;
	private Long cgc;
	private Long cpf;
	private String razaoSocial;
	
	public String getCpfCnpjRazaoSocialFormatado() {
		if (this.cpf == null) {
			if (this.cgc == null) {
				return this.razaoSocial;
			}
			return CoreUtil.formatarCNPJ(this.cgc).concat(" - ").concat(this.razaoSocial);
		}
		return CoreUtil.formataCPF(this.cpf).concat(" - ").concat(this.razaoSocial);
	}
	
	public String getCpfCnpjFormatado() {
		if (this.cpf == null) {
			if (this.cgc == null) {
				return StringUtils.EMPTY;
			}
			return CoreUtil.formatarCNPJ(this.cgc);
		}
		return CoreUtil.formataCPF(this.cpf);
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Long getCgc() {
		return cgc;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	
	public enum Fields {
		NUMERO("numero"),
		CGC("cgc"), 
		CPF("cpf"), 
		RAZAO_SOCIAL("razaoSocial");
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
}
