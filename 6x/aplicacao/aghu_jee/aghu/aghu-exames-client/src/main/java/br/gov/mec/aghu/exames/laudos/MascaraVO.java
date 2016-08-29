package br.gov.mec.aghu.exames.laudos;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Representa cada campo da mascara de exames.
 * 
 * @author cvagheti
 * 
 */
public class MascaraVO implements Serializable, Comparable<MascaraVO> {

	private static final long serialVersionUID = -6147852297868903456L;

	/**
	 * Valor para calculo de quebra de linha. <br />
	 * Significa que se a diferença entre as posicaLinhaImpressao for maior que
	 * a constante, deve ser quebrada a linha.
	 */
	public static final int ENTRE_LINHAS = 10;

	private IdVO id = new IdVO();

	private boolean italico;
	private boolean negrito;
	private boolean sublinhado;
	private String fonte;
	private Integer tamanhoFonte;
	private Integer quantidadeCaracteres;
	private Character alinhamento;
	private Short posicaoColunaImpressao;
	private Integer posicaoLinhaImpressao;
	private boolean riscado;
	private String cor;
	private Short larguraObjetoVisual;
	private Short alturaObjetoVisual;
	private String valor;

	private boolean quebraLinha;
	private Integer linha;
	private boolean exibeTela;
	private boolean exibeRelatorio;

	public MascaraVO() {

	}

	public MascaraVO(MascaraVO.IdVO id) {
		this.id = id;
	}

	public IdVO getId() {
		return id;
	}

	public void setId(IdVO id) {
		this.id = id;
	}

	public boolean isItalico() {
		return italico;
	}

	public void setItalico(boolean italico) {
		this.italico = italico;
	}

	public boolean isNegrito() {
		return negrito;
	}

	public void setNegrito(boolean negrito) {
		this.negrito = negrito;
	}

	public boolean isSublinhado() {
		return sublinhado;
	}

	public void setSublinhado(boolean sublinhado) {
		this.sublinhado = sublinhado;
	}

	public String getFonte() {
		return fonte;
	}

	public void setFonte(String fonte) {
		this.fonte = fonte;
	}

	public Integer getTamanhoFonte() {
		return tamanhoFonte;
	}

	public void setTamanhoFonte(Integer tamanhoFonte) {
		this.tamanhoFonte = tamanhoFonte;
	}

	public Integer getQuantidadeCaracteres() {
		return quantidadeCaracteres;
	}

	public void setQuantidadeCaracteres(Integer quantidadeCaracteres) {
		this.quantidadeCaracteres = quantidadeCaracteres;
	}

	public Character getAlinhamento() {
		return alinhamento;
	}

	public void setAlinhamento(Character alinhamento) {
		this.alinhamento = alinhamento;
	}

	public Short getPosicaoColunaImpressao() {
		return posicaoColunaImpressao;
	}

	public void setPosicaoColunaImpressao(Short posicaoColunaImpressao) {
		this.posicaoColunaImpressao = posicaoColunaImpressao;
	}

	public Integer getPosicaoLinhaImpressao() {
		return posicaoLinhaImpressao;
	}

	public void setPosicaoLinhaImpressao(Integer posicaoLinhaImpressao) {
		this.posicaoLinhaImpressao = posicaoLinhaImpressao;
	}

	public boolean isRiscado() {
		return riscado;
	}

	public void setRiscado(boolean riscado) {
		this.riscado = riscado;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public Short getLarguraObjetoVisual() {
		return larguraObjetoVisual;
	}

	public void setLarguraObjetoVisual(Short larguraObjetoVisual) {
		this.larguraObjetoVisual = larguraObjetoVisual;
	}

	public Short getAlturaObjetoVisual() {
		return alturaObjetoVisual;
	}

	public void setAlturaObjetoVisual(Short alturaObjetoVisual) {
		this.alturaObjetoVisual = alturaObjetoVisual;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * Indica que antes deste elemento existe uma quebra de linha.
	 * 
	 * @return
	 */
	public boolean isQuebraLinha() {
		return quebraLinha;
	}

	public void setQuebraLinha(boolean quebraLinha) {
		this.quebraLinha = quebraLinha;
	}

	public Integer getLinha() {
		return linha;
	}

	public void setLinha(Integer linha) {
		this.linha = linha;
	}

	public boolean isExibeTela() {
		return exibeTela;
	}

	public void setExibeTela(boolean exibeTela) {
		this.exibeTela = exibeTela;
	}

	public boolean isExibeRelatorio() {
		return exibeRelatorio;
	}

	public void setExibeRelatorio(boolean exibeRelatorio) {
		this.exibeRelatorio = exibeRelatorio;
	}

	@Override
	public int compareTo(MascaraVO other) {
		if (this.getLinha() == null || other.getLinha() == null) {
			return 0;
		}
		if (this.getLinha().equals(other.getLinha())) {
			return this.getPosicaoColunaImpressao().compareTo(
					other.getPosicaoColunaImpressao());
		}

		return this.getLinha().compareTo(other.getLinha());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	/**
	 * @author cvagheti
	 *
	 */
	public static class IdVO implements Serializable {
		private static final long serialVersionUID = 2606586236955236609L;
		public String velEmaExaSigla;
		public Integer velEmaManSeq;
		public Integer velSeqp;
		public Integer calSeq;
		public Integer seqp;

		public IdVO() {
		}

		public IdVO(String velEmaExaSigla, Integer velEmaManSeq,
				Integer velSeqp, Integer calSeq, Integer seqp) {
			this.velEmaExaSigla = velEmaExaSigla;
			this.velEmaManSeq = velEmaManSeq;
			this.velSeqp = velSeqp;
			this.calSeq = calSeq;
			this.seqp = seqp;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("velEmaExaSigla", this.velEmaExaSigla)
					.append("velEmaManSeq", this.velEmaManSeq)
					.append("velSeqp", this.velSeqp)
					.append("calSeq", this.calSeq).append("seqp", this.seqp)
					.toString();
		}
	}

}
