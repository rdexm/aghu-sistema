package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.InputStream;
import java.io.Serializable;

public class ImagemModalidadeOrientacaoVO implements Serializable {

	private static final long serialVersionUID = -1569979609171638487L;
	
	private Integer seqPrescricaoPaciente;
	private Integer atdSeqPrescricaoPaciente;
	private Integer seqpItemPrcrModalidade;
	private String classificacaoTipoModalidade;
	private String descricaoTipoModalidade;
	private Short numVezesSemana;
	private String orientacoesItemPrcrModalidade;
	private byte[] imagem;
	private String modalidadeFormatada;
	private String orientacaoFormatada;
	private String contador;
	private InputStream imagemInputStream;
	
	public enum Fields {
		SEQ_PRESCRICAO_PACIENTE("seqPrescricaoPaciente"),
		ATD_SEQ_PRESCRICAO_PACIENTE("atdSeqPrescricaoPaciente"),
		SEQP_ITEM_PRCR_MODALIDADE("seqpItemPrcrModalidade"),
		CLASSIFICACAO_TIPO_MODALIDADE("classificacaoTipoModalidade"),
		DESCRICAO_TIPO_MODALIDADE("descricaoTipoModalidade"),
		NUM_VEZES_SEMANA("numVezesSemana"),
		ORIENTACOES_ITEM_PRCR_MODALIDADE("orientacoesItemPrcrModalidade"),
		IMAGEM("imagem");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getSeqPrescricaoPaciente() {
		return seqPrescricaoPaciente;
	}
	public void setSeqPrescricaoPaciente(Integer seqPrescricaoPaciente) {
		this.seqPrescricaoPaciente = seqPrescricaoPaciente;
	}
	public Integer getAtdSeqPrescricaoPaciente() {
		return atdSeqPrescricaoPaciente;
	}
	public void setAtdSeqPrescricaoPaciente(Integer atdSeqPrescricaoPaciente) {
		this.atdSeqPrescricaoPaciente = atdSeqPrescricaoPaciente;
	}
	public Integer getSeqpItemPrcrModalidade() {
		return seqpItemPrcrModalidade;
	}
	public void setSeqpItemPrcrModalidade(Integer seqpItemPrcrModalidade) {
		this.seqpItemPrcrModalidade = seqpItemPrcrModalidade;
	}
	public String getClassificacaoTipoModalidade() {
		return classificacaoTipoModalidade;
	}
	public void setClassificacaoTipoModalidade(String classificacaoTipoModalidade) {
		this.classificacaoTipoModalidade = classificacaoTipoModalidade;
	}
	public String getDescricaoTipoModalidade() {
		return descricaoTipoModalidade;
	}
	public void setDescricaoTipoModalidade(String descricaoTipoModalidade) {
		this.descricaoTipoModalidade = descricaoTipoModalidade;
	}
	public Short getNumVezesSemana() {
		return numVezesSemana;
	}
	public void setNumVezesSemana(Short numVezesSemana) {
		this.numVezesSemana = numVezesSemana;
	}
	public String getOrientacoesItemPrcrModalidade() {
		return orientacoesItemPrcrModalidade;
	}
	public void setOrientacoesItemPrcrModalidade(
			String orientacoesItemPrcrModalidade) {
		this.orientacoesItemPrcrModalidade = orientacoesItemPrcrModalidade;
	}
	public byte[] getImagem() {
		return imagem;
	}
	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}
	public String getModalidadeFormatada() {
		return modalidadeFormatada;
	}
	public void setModalidadeFormatada(String modalidadeFormatada) {
		this.modalidadeFormatada = modalidadeFormatada;
	}
	public String getOrientacaoFormatada() {
		return orientacaoFormatada;
	}
	public void setOrientacaoFormatada(String orientacaoFormatada) {
		this.orientacaoFormatada = orientacaoFormatada;
	}
	public String getContador() {
		return contador;
	}
	public void setContador(String contador) {
		this.contador = contador;
	}
	public InputStream getImagemInputStream() {
		return imagemInputStream;
	}
	public void setImagemInputStream(InputStream imagemInputStream) {
		this.imagemInputStream = imagemInputStream;
	}
	
}
