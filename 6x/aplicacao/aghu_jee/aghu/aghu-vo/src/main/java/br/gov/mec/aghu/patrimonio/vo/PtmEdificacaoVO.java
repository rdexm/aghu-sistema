package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PtmEdificacaoVO implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4566984265117606049L;
	
	private Integer seq;
	private String nome;
	private String descricao;
	private Long numeroBem;
	private String numBemConcatenadoNome;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getNumeroBem() {
		return numeroBem;
	}

	public void setNumeroBem(Long numeroBem) {
		this.numeroBem = numeroBem;
	}

	public String getNumBemConcatenadoNome() {
		numBemConcatenadoNome = numeroBem.toString() + " - " + nome;
		return numBemConcatenadoNome;
	}

	public void setNumBemConcatenadoNome(String numBemConcatenadoNome) {
		this.numBemConcatenadoNome = numBemConcatenadoNome;
	}

	public enum Fields {

		SEQ("seq"),
		NOME("nome"),
		DESCRICAO("descricao"),
		NUMERO_BEM("numeroBem");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	

	@Override
	public int hashCode() {

		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append(seq);
		
		return builder.build();
	}

	@Override
	public boolean equals(Object obj) {

		EqualsBuilder builder = new EqualsBuilder();

		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		PtmEdificacaoVO other = (PtmEdificacaoVO) obj;

		builder.append(seq, other.getSeq());

		return builder.build();
	}

}
