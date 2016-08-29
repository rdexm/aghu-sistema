package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.casca.model.Perfil;

public class CsePerfisVO {

	private Perfil perfil;
	private boolean marcado;
	
	public CsePerfisVO() {
		
	}

	public CsePerfisVO(Perfil perfil, boolean marcado) {
		this.perfil = perfil;
		this.marcado = marcado;
	}
	
	public String getNome() {
		return perfil.getNome();
	}
	
	public String getDescricao() {
		return perfil.getDescricao();
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public boolean isMarcado() {
		return marcado;
	}

	public void setMarcado(boolean marcado) {
		this.marcado = marcado;
	}
	
}
