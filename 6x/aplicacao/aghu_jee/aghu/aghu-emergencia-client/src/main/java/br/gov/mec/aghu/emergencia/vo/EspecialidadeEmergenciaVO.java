package br.gov.mec.aghu.emergencia.vo;

import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.core.commons.BaseBean;

public class EspecialidadeEmergenciaVO implements BaseBean {
	private static final long serialVersionUID = -4101227518458831214L;

	private MamEmgEspecialidades especialidadeEmergencia;
	private Especialidade especialidade;

	public MamEmgEspecialidades getEspecialidadeEmergencia() {
		return especialidadeEmergencia;
	}

	public void setEspecialidadeEmergencia(MamEmgEspecialidades especialidadeEmergencia) {
		this.especialidadeEmergencia = especialidadeEmergencia;
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
		EspecialidadeEmergenciaVO other = (EspecialidadeEmergenciaVO) obj;
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
