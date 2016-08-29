package br.gov.mec.aghu.emergencia.vo;

import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.model.MamEmgServEspecialidade;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ServidorEspecialidadeEmergenciaVO implements BaseBean {
	private static final long serialVersionUID = -6441060317613491327L;
	
	private MamEmgServEspecialidade mamEmgServEspecialidade;
	private Especialidade especialidade;

	public MamEmgServEspecialidade getMamEmgServEspecialidade() {
		return mamEmgServEspecialidade;
	}

	public void setMamEmgServEspecialidade(MamEmgServEspecialidade mamEmgServEspecialidade) {
		this.mamEmgServEspecialidade = mamEmgServEspecialidade;
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
		ServidorEspecialidadeEmergenciaVO other = (ServidorEspecialidadeEmergenciaVO) obj;
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
