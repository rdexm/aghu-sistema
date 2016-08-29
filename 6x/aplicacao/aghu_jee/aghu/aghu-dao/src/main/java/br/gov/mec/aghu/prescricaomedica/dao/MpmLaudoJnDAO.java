package br.gov.mec.aghu.prescricaomedica.dao;

import br.gov.mec.aghu.model.MpmLaudoJn;

public class MpmLaudoJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmLaudoJn> {

	
	private static final long serialVersionUID = -7260511887160584846L;

	public void persistirLaudoJn(MpmLaudoJn laudoJn) {

		if (laudoJn != null) {
			this.persistir(laudoJn);
			this.flush();
		} else {
			this.flush();
		}
	}

}
