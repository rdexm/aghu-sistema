package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote;
import br.gov.mec.aghu.model.AelLoteExameUsual;

public class LoteDefaultVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6386356556261663287L;
	
	private DominioSolicitacaoExameLote tipoLoteDefault;
	private AelLoteExameUsual loteDefault;
	public DominioSolicitacaoExameLote getTipoLoteDefault() {
		return tipoLoteDefault;
	}
	public void setTipoLoteDefault(DominioSolicitacaoExameLote tipoLoteDefault) {
		this.tipoLoteDefault = tipoLoteDefault;
	}
	public AelLoteExameUsual getLoteDefault() {
		return loteDefault;
	}
	public void setLoteDefault(AelLoteExameUsual loteDefault) {
		this.loteDefault = loteDefault;
	}
}
