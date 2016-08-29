package br.gov.mec.aghu.compras.contaspagar.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.commons.CoreUtil;

public class DatasVencimentosFornecedorVO implements Serializable {

	/**
	 * @author Rafael Cancian
	 */
	private static final long serialVersionUID = -6637712377716022507L;

	private Integer numero;
	private Long cgc;
	private Long cpf;
	private String razaoSocial;
	private Date dataValidadeFgts;
	private Date dataValidadeInss;
	private Date dataValidadeRecFed;
	private Long titulosPagar;
	// Campos populados após a consulta principal
	private String comunicados;
	private String hintComunicados;
	private Boolean selecionado;
	
	public String getFornecedorFormatado() {
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

	public Date getDataValidadeFgts() {
		return dataValidadeFgts;
	}

	public void setDataValidadeFgts(Date dataValidadeFgts) {
		this.dataValidadeFgts = dataValidadeFgts;
	}

	public Date getDataValidadeInss() {
		return dataValidadeInss;
	}

	public void setDataValidadeInss(Date dataValidadeInss) {
		this.dataValidadeInss = dataValidadeInss;
	}

	public Date getDataValidadeRecFed() {
		return dataValidadeRecFed;
	}

	public void setDataValidadeRecFed(Date dataValidadeRecFed) {
		this.dataValidadeRecFed = dataValidadeRecFed;
	}
	
	public Long getTitulosPagar() {
		return titulosPagar;
	}

	public void setTitulosPagar(Long titulosPagar) {
		this.titulosPagar = titulosPagar;
	}

	public String getComunicados() {
		return comunicados;
	}

	public void setComunicados(String comunicados) {
		this.comunicados = comunicados;
	}

	public String getHintComunicados() {
		return hintComunicados;
	}

	public void setHintComunicados(String hintComunicados) {
		this.hintComunicados = hintComunicados;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public enum Fields {
		NUMERO("numero"),
		CGC("cgc"), 
		CPF("cpf"), 
		RAZAO_SOCIAL("razaoSocial"), 
		DATA_VALIDADE_FGTS("dataValidadeFgts"), 
		DATA_VALIDADE_INSS("dataValidadeInss"),
		DATA_VALIDADE_RECEITA_FEDERAL("dataValidadeRecFed"),
		TITULOS_PAGAR("titulosPagar");
		  
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
