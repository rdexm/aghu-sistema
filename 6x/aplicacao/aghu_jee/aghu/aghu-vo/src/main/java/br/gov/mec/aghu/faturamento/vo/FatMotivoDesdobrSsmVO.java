package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.FatMotivoDesdobrSsm;

public class FatMotivoDesdobrSsmVO implements Serializable {
	
	private static final long serialVersionUID = 1028241809589975911L;

	private FatMotivoDesdobrSsm fatMotivoDesdobrSsm;
	
	private boolean editando;

	public FatMotivoDesdobrSsm getFatMotivoDesdobrSsm() {
		return fatMotivoDesdobrSsm;
	}

	public void setFatMotivoDesdobrSsm(FatMotivoDesdobrSsm fatMotivoDesdobrSsm) {
		this.fatMotivoDesdobrSsm = fatMotivoDesdobrSsm;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}
	
}
