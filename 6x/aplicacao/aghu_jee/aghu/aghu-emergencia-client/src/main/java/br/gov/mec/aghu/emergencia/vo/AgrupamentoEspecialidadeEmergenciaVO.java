package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.model.MamEmgAgrupaEsp;

public class AgrupamentoEspecialidadeEmergenciaVO implements Serializable {
	private static final long serialVersionUID = 5272174020680994550L;

	private MamEmgAgrupaEsp agrupamentoEspecialidadeEmergencia;
	private Especialidade especialidade;

	public MamEmgAgrupaEsp getAgrupamentoEspecialidadeEmergencia() {
		return agrupamentoEspecialidadeEmergencia;
	}

	public void setAgrupamentoEspecialidadeEmergencia(MamEmgAgrupaEsp agrupamentoEspecialidadeEmergencia) {
		this.agrupamentoEspecialidadeEmergencia = agrupamentoEspecialidadeEmergencia;
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((especialidade == null) ? 0 : especialidade.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		AgrupamentoEspecialidadeEmergenciaVO other = (AgrupamentoEspecialidadeEmergenciaVO) obj;
		if (especialidade == null) {
			if (other.especialidade != null) {
				return false;
			}
		} else if (!especialidade.equals(other.especialidade)) {
			return false;
		}
		return true;
	}
}
