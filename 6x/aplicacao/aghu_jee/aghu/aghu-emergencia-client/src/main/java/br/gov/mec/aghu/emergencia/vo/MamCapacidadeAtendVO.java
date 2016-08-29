package br.gov.mec.aghu.emergencia.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class MamCapacidadeAtendVO implements BaseBean {
	
	private static final long serialVersionUID = -4101227518458831214L;

	private Integer seq;
	private String nomeEspecialidade;
	private Short qtdeInicialPac;
	private Short qtdeFinalPac;
	private Short capacidadeAtend;
	private DominioSituacao indSituacao;
	private Short espSeq;
	
	
	public enum Fields {
		SEQ("seq"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		QTDE_INICIAL_PAC("qtdeInicialPac"),
		QTDE_FINAL_PAC("qtdeFinalPac"),
		CAPACIDADE_ATEND("capacidadeAtend"),
		IND_SITUACAO("indSituacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Short getQtdeInicialPac() {
		return qtdeInicialPac;
	}

	public void setQtdeInicialPac(Short qtdeInicialPac) {
		this.qtdeInicialPac = qtdeInicialPac;
	}

	public Short getQtdeFinalPac() {
		return qtdeFinalPac;
	}

	public void setQtdeFinalPac(Short qtdeFinalPac) {
		this.qtdeFinalPac = qtdeFinalPac;
	}

	public Short getCapacidadeAtend() {
		return capacidadeAtend;
	}

	public void setCapacidadeAtend(Short capacidadeAtend) {
		this.capacidadeAtend = capacidadeAtend;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MamCapacidadeAtendVO)) {
			return false;
		}
		MamCapacidadeAtendVO other = (MamCapacidadeAtendVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
}
