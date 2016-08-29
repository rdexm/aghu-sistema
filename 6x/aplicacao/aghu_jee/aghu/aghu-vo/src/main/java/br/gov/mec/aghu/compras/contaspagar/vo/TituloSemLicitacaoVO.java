package br.gov.mec.aghu.compras.contaspagar.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.model.FcpClassificacaoTitulo;
import br.gov.mec.aghu.model.ScoFornecedor;

/**
 * @author rafael.silvestre
 */
public class TituloSemLicitacaoVO {

	private ScoFornecedor credor;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private Date dataVencimento;
	private Short qtdeParcelas;
	private FcpClassificacaoTitulo classificacao;

	public TituloSemLicitacaoVO() {
	}

	public TituloSemLicitacaoVO(ScoFornecedor credor,
			DominioModalidadeEmpenho modalidadeEmpenho, Date dataVencimento,
			Short qtdeParcelas, FcpClassificacaoTitulo classificacao) {
		this.credor = credor;
		this.modalidadeEmpenho = modalidadeEmpenho;
		this.dataVencimento = dataVencimento;
		this.qtdeParcelas = qtdeParcelas;
		this.classificacao = classificacao;
	}

	public ScoFornecedor getCredor() {
		return credor;
	}

	public void setCredor(ScoFornecedor credor) {
		this.credor = credor;
	}

	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Short getQtdeParcelas() {
		return qtdeParcelas;
	}

	public void setQtdeParcelas(Short qtdeParcelas) {
		this.qtdeParcelas = qtdeParcelas;
	}

	public FcpClassificacaoTitulo getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(FcpClassificacaoTitulo classificacao) {
		this.classificacao = classificacao;
	}
}
