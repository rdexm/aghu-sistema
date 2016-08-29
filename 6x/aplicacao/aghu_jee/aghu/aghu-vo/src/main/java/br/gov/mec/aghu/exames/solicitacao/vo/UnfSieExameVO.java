package br.gov.mec.aghu.exames.solicitacao.vo;

import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;

/**
 * VO que armazena 2 objetos:<br><p>
 * AelUnfExecutaExames<br>
 * AelSinonimoExame<br>
 * @author ndeitch
 */
public class UnfSieExameVO {

	public UnfSieExameVO(AelUnfExecutaExames exame) {
		this.aelUnfExecutaExames = exame;
	}
	
	public UnfSieExameVO(AelUnfExecutaExames exame, AelSinonimoExame sinonimo){
		this.aelUnfExecutaExames = exame;
		this.aelSinonimoExame = sinonimo;
	}
	
	private AelUnfExecutaExames aelUnfExecutaExames;
	private AelSinonimoExame aelSinonimoExame;
	
	public AelUnfExecutaExames getAelUnfExecutaExames() {
		return aelUnfExecutaExames;
	}
	public void setAelUnfExecutaExames(AelUnfExecutaExames aelUnfExecutaExames) {
		this.aelUnfExecutaExames = aelUnfExecutaExames;
	}
	public AelSinonimoExame getAelSinonimoExame() {
		return aelSinonimoExame;
	}
	public void setAelSinonimoExame(AelSinonimoExame aelSinonimoExame) {
		this.aelSinonimoExame = aelSinonimoExame;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((aelSinonimoExame == null) ? 0 : aelSinonimoExame.hashCode());
		result = prime
				* result
				+ ((aelUnfExecutaExames == null) ? 0 : aelUnfExecutaExames
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		UnfSieExameVO other = (UnfSieExameVO) obj;
		if (aelSinonimoExame == null) {
			if (other.aelSinonimoExame != null){
				return false;
			}
		} else if (!aelSinonimoExame.equals(other.aelSinonimoExame)){
			return false;
		}
		if (aelUnfExecutaExames == null) {
			if (other.aelUnfExecutaExames != null){
				return false;
			}
		} else if (!aelUnfExecutaExames.equals(other.aelUnfExecutaExames)){
			return false;
		}
		return true;
	} 
	
}
