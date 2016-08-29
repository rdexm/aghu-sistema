package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.ScoFornecedor;

public class CadastroContasCorrentesFornecedorVO implements Serializable {

	private static final long serialVersionUID = 1094758748075863219L;
	
	private ScoFornecedor fornecedor;
	private FcpAgenciaBanco agenciaBanco;
	private String numeroConta;
	private boolean preferencial;
	
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public FcpAgenciaBanco getAgenciaBanco() {
		return agenciaBanco;
	}
	public void setAgenciaBanco(FcpAgenciaBanco agenciaBanco) {
		this.agenciaBanco = agenciaBanco;
	}
	public String getNumeroConta() {
		return numeroConta;
	}
	public void setNumeroConta(String numeroConta) {
		this.numeroConta = numeroConta;
	}
	public void setPreferencial(boolean preferencial) {
		this.preferencial = preferencial;
	}
	public boolean isPreferencial() {
		return preferencial;
	}
}
