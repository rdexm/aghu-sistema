package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MbcAnestesiaCirurgiasId;
import br.gov.mec.aghu.model.MbcTipoAnestesias;

/**
 * VO da listagem de anestesias na estória #22460 – Agendar procedimentos eletivo, urgência ou emergência
 * 
 * @author aghu
 * 
 */
public class CirurgiaTelaAnestesiaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7655737196767778747L;
	private MbcAnestesiaCirurgiasId id;
	private MbcTipoAnestesias mbcTipoAnestesias;

	public MbcAnestesiaCirurgiasId getId() {
		return id;
	}

	public void setId(MbcAnestesiaCirurgiasId id) {
		this.id = id;
	}

	public MbcTipoAnestesias getMbcTipoAnestesias() {
		return mbcTipoAnestesias;
	}

	public void setMbcTipoAnestesias(MbcTipoAnestesias mbcTipoAnestesias) {
		this.mbcTipoAnestesias = mbcTipoAnestesias;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		CirurgiaTelaAnestesiaVO other = (CirurgiaTelaAnestesiaVO) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
