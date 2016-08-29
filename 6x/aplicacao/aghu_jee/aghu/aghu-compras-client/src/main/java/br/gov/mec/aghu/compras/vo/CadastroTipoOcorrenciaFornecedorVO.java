package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class CadastroTipoOcorrenciaFornecedorVO implements Serializable {

	private static final long serialVersionUID = 1094758748075863219L;
	
	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;
	
	public Short getCodigo() {
		return codigo;
	}
	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

}
