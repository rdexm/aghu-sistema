package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamTrgGravidade;

public class TrgGravidadeFluxogramaVO implements Serializable {
	private static final long serialVersionUID = -353616862683270950L;

	private MamTrgGravidade trgGravidade;
	private MamFluxograma fluxograma;

	public MamTrgGravidade getTrgGravidade() {
		return trgGravidade;
	}

	public void setTrgGravidade(MamTrgGravidade trgGravidade) {
		this.trgGravidade = trgGravidade;
	}

	public MamFluxograma getFluxograma() {
		return fluxograma;
	}

	public void setFluxograma(MamFluxograma fluxograma) {
		this.fluxograma = fluxograma;
	}
}
