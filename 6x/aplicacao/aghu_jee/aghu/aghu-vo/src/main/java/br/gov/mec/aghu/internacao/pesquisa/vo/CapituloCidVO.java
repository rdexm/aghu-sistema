package br.gov.mec.aghu.internacao.pesquisa.vo;

import java.util.List;

import br.gov.mec.aghu.model.AghCapitulosCid;

public class CapituloCidVO {

	private AghCapitulosCid capituloCid;
	private List<GrupoCidVO> grupoCidList;

	public AghCapitulosCid getCapituloCid() {
		return capituloCid;
	}

	public void setCapituloCid(AghCapitulosCid capituloCid) {
		this.capituloCid = capituloCid;
	}

	public List<GrupoCidVO> getGrupoCidList() {
		return grupoCidList;
	}

	public void setGrupoCidList(List<GrupoCidVO> grupoCidList) {
		this.grupoCidList = grupoCidList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((capituloCid == null) ? 0 : capituloCid.hashCode());
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
		CapituloCidVO other = (CapituloCidVO) obj;
		if (capituloCid == null) {
			if (other.capituloCid != null) {
				return false;
			}
		} else if (!capituloCid.equals(other.capituloCid)) {
			return false;
		}
		return true;
	}
	
}
