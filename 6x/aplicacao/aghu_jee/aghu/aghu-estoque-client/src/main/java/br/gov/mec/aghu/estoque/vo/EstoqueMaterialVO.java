package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.dominio.DominioClassifABC;

public class EstoqueMaterialVO {
	private String almoxarifadoDescricao;
	private Integer quantidadeMaxima;
	private Integer quantidadeDisponivel;
	private Integer quantidadeBloqueada;
	private Short almoxarifadoSeq;
	private DominioClassifABC classificacaoAbc;
	private DominioClassifABC subClassificacaoAbc;

	public Integer getQuantidadeMaxima() {
		return quantidadeMaxima;
	}

	public void setQuantidadeMaxima(Integer quantidadeMaxima) {
		this.quantidadeMaxima = quantidadeMaxima;
	}

	public Integer getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}

	public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
		this.quantidadeDisponivel = quantidadeDisponivel;
	}

	public Integer getQuantidadeBloqueada() {
		return quantidadeBloqueada;
	}

	public void setQuantidadeBloqueada(Integer quantidadeBloqueada) {
		this.quantidadeBloqueada = quantidadeBloqueada;
	}

	public Short getAlmoxarifadoSeq() {
		return almoxarifadoSeq;
	}

	public void setAlmoxarifadoSeq(Short almoxarifadoSeq) {
		this.almoxarifadoSeq = almoxarifadoSeq;
	}

	public DominioClassifABC getClassificacaoAbc() {
		return classificacaoAbc;
	}

	public void setClassificacaoAbc(DominioClassifABC classificacaoAbc) {
		this.classificacaoAbc = classificacaoAbc;
	}

	public DominioClassifABC getSubClassificacaoAbc() {
		return subClassificacaoAbc;
	}

	public void setSubClassificacaoAbc(DominioClassifABC subClassificacaoAbc) {
		this.subClassificacaoAbc = subClassificacaoAbc;
	}

	public void setAlmoxarifadoDescricao(String almoxarifadoDescricao) {
		this.almoxarifadoDescricao = almoxarifadoDescricao;
	}

	public String getAlmoxarifadoDescricao() {
		return almoxarifadoDescricao;
	}

	public enum Fields {
		QUANTIDADE_MAXIMA("quantidadeMaxima"), QUANTIDADE_DISPONIVEL("quantidadeDisponivel"), QUANTIDADE_BLOQUEADA("quantidadeBloqueada"), ALMOXARIFADO_SEQ(
				"almoxarifadoSeq"), ALMOXARIFADO_DESC("almoxarifadoDescricao"), CLASSIFICACAO_ABC("classificacaoAbc"), SUB_CLASSIFIFCACAO_ABC(
				"subClassificacaoAbc");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
