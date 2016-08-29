package br.gov.mec.aghu.prescricaomedica.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.RapServidores;

public class DiagnosticosPacienteVO {
	
	public enum Acao {
		ADICIONAR, EXCLUIR, ALTERAR, RESOLVER, NENHUM
	}
	
	private Acao acao = Acao.NENHUM;
	
	private MamDiagnostico mamDiagnostico;
	
	private boolean salvoNoBanco = false;
	
	private RapServidores servidor;
	
	private boolean editando;
	
	public void setAcao(Acao acao) {
		this.acao = acao;
	}

	public Acao getAcao() {
		return acao;
	}

	public void setMamDiagnostico(MamDiagnostico mamDiagnostico) {
		this.mamDiagnostico = mamDiagnostico;
	}

	public MamDiagnostico getMamDiagnostico() {
		return mamDiagnostico;
	}

	public void setSalvoNoBanco(boolean salvoNoBanco) {
		this.salvoNoBanco = salvoNoBanco;
	}

	public boolean isSalvoNoBanco() {
		return salvoNoBanco;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.mamDiagnostico).hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof DiagnosticosPacienteVO)) {
			return false;
		}
		DiagnosticosPacienteVO castOther = (DiagnosticosPacienteVO) other;
		return new EqualsBuilder().append(this.mamDiagnostico.getSeq(), castOther.mamDiagnostico.getSeq())
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("diagnosticoSeq", this.mamDiagnostico.getSeq())
		 .append("salvoNoBanco", isSalvoNoBanco()).append("acao", this.acao).toString();
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public boolean isEditando() {
		return editando;
	}
	
}
