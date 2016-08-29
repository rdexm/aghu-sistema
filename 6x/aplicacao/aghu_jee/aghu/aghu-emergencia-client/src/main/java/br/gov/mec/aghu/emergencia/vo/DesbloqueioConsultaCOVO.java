package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

/**
 * Retorno de regras relativas a desbloqueio de consulta no CO.
 * 
 * @author luismoura
 * 
 */
public class DesbloqueioConsultaCOVO implements Serializable {
	private static final long serialVersionUID = 6029610387328930343L;

	private boolean notasAdicionais;
	private boolean habilitaDesbloqueio;
	private boolean habilitaBloqueio;
	private boolean habilitaExclusao;

	private boolean habilitaFinalizarConsulta;
	private boolean habilitaInternar;

	private boolean permiteAlterarAbaGestAtual;
	private boolean permiteAlterarAbaConsCO;

	public boolean isNotasAdicionais() {
		return notasAdicionais;
	}

	public void setNotasAdicionais(boolean notasAdicionais) {
		this.notasAdicionais = notasAdicionais;
	}

	public boolean isHabilitaDesbloqueio() {
		return habilitaDesbloqueio;
	}

	public void setHabilitaDesbloqueio(boolean habilitaDesbloqueio) {
		this.habilitaDesbloqueio = habilitaDesbloqueio;
	}

	public boolean isHabilitaExclusao() {
		return habilitaExclusao;
	}

	public void setHabilitaExclusao(boolean habilitaExclusao) {
		this.habilitaExclusao = habilitaExclusao;
	}

	public boolean isHabilitaBloqueio() {
		return habilitaBloqueio;
	}

	public void setHabilitaBloqueio(boolean habilitaBloqueio) {
		this.habilitaBloqueio = habilitaBloqueio;
	}

	public boolean isHabilitaFinalizarConsulta() {
		return habilitaFinalizarConsulta;
	}

	public void setHabilitaFinalizarConsulta(boolean habilitaFinalizarConsulta) {
		this.habilitaFinalizarConsulta = habilitaFinalizarConsulta;
	}

	public boolean isHabilitaInternar() {
		return habilitaInternar;
	}

	public void setHabilitaInternar(boolean habilitaInternar) {
		this.habilitaInternar = habilitaInternar;
	}

	public boolean isPermiteAlterarAbaGestAtual() {
		return permiteAlterarAbaGestAtual;
	}

	public void setPermiteAlterarAbaGestAtual(boolean permiteAlterarAbaGestAtual) {
		this.permiteAlterarAbaGestAtual = permiteAlterarAbaGestAtual;
	}

	public boolean isPermiteAlterarAbaConsCO() {
		return permiteAlterarAbaConsCO;
	}

	public void setPermiteAlterarAbaConsCO(boolean permiteAlterarAbaConsCO) {
		this.permiteAlterarAbaConsCO = permiteAlterarAbaConsCO;
	}
}
