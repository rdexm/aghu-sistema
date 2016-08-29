package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

public class ComplementoCidVO extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3510889928130342435L;

	private Integer seq;
	private String codigo;
	private String descricao;
	private String descricaoEditada;
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricaoEditada() {
		return descricaoEditada;
	}

	public void setDescricaoEditada(String descricaoEditada) {
		this.descricaoEditada = descricaoEditada;
	}

	public String getSeqCodigo() {
		return StringUtils.leftPad(seq.toString(), 5) + " - " + codigo;
	}

	public String getCodigoDescricao() {
		return "(" + codigo +") " + (StringUtils.isEmpty(descricaoEditada) ? descricao : descricaoEditada);
	}


	public enum Fields {

		CODIGO("codigo"),
		SEQ("seq"),
		DESCRICAO("descricao"),
		DESCRICAO_EDITADA("descricaoEditada");

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
