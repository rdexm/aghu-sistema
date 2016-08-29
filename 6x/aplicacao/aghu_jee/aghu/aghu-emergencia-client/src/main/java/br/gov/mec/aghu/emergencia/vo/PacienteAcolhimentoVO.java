package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Date;

public class PacienteAcolhimentoVO implements Serializable {
	private static final long serialVersionUID = -4101227518458831214L;

	private Long trgSeq;
	private Integer codigo;
	private Integer prontuario;
	private String nome;
	private Integer idade;
	private String queixaPrincipal;
	private Date dtHrSituacao;
	private Short ordem;
	private String codCor;
	private String descricaoGravidade;
	private Boolean indGermesMulti;
	private Boolean indTransferido;
	private String pacProntuarioFormatado;
	
	
	public enum Fields {

		DT_HR_SITUACAO("dtHrSituacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	public Long getTrgSeq() {
		return trgSeq;
	}
	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getIdade() {
		return idade;
	}
	public void setIdade(Integer idade) {
		this.idade = idade;
	}
	public String getQueixaPrincipal() {
		return queixaPrincipal;
	}
	public void setQueixaPrincipal(String queixaPrincipal) {
		this.queixaPrincipal = queixaPrincipal;
	}
	public Date getDtHrSituacao() {
		return dtHrSituacao;
	}
	public void setDtHrSituacao(Date dtHrSituacao) {
		this.dtHrSituacao = dtHrSituacao;
	}
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public String getCodCor() {
		return codCor;
	}
	public void setCodCor(String codCor) {
		this.codCor = codCor;
	}
	public String getDescricaoGravidade() {
		return descricaoGravidade;
	}
	public void setDescricaoGravidade(String descricaoGravidade) {
		this.descricaoGravidade = descricaoGravidade;
	}
	public Boolean getIndGermesMulti() {
		return indGermesMulti;
	}
	public void setIndGermesMulti(Boolean indGermesMulti) {
		this.indGermesMulti = indGermesMulti;
	}
	public Boolean getIndTransferido() {
		return indTransferido;
	}
	public void setIndTransferido(Boolean indTransferido) {
		this.indTransferido = indTransferido;
	}
	public String getPacProntuarioFormatado() {
		return pacProntuarioFormatado;
	}
	public void setPacProntuarioFormatado(String pacProntuarioFormatado) {
		this.pacProntuarioFormatado = pacProntuarioFormatado;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((trgSeq == null) ? 0 : trgSeq.hashCode());
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
		if (!(obj instanceof PacienteAcolhimentoVO)) {
			return false;
		}
		PacienteAcolhimentoVO other = (PacienteAcolhimentoVO) obj;
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		if (trgSeq == null) {
			if (other.trgSeq != null) {
				return false;
			}
		} else if (!trgSeq.equals(other.trgSeq)) {
			return false;
		}
		return true;
	}
	
	
}
