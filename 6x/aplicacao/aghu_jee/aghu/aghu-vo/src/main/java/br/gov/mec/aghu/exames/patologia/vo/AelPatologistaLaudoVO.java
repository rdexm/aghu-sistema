package br.gov.mec.aghu.exames.patologia.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelPatologistaAps;

public class AelPatologistaLaudoVO implements Serializable {

	private static final long serialVersionUID = -2992276081298664751L;

	AelPatologistaAps patologistaAp;
	
	AelPatologista patologista;

	public AelPatologistaAps getPatologistaAp() {
		return patologistaAp;
	}

	public void setPatologistaAp(AelPatologistaAps patologistaAp) {
		this.patologistaAp = patologistaAp;
	}

	public AelPatologista getPatologista() {
		return patologista;
	}

	public void setPatologista(AelPatologista patologista) {
		this.patologista = patologista;
	}
	
	
}
