package br.gov.mec.aghu.compras.dao;

import br.gov.mec.aghu.model.FcpRetencaoAliquotaJn;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FcpTributoJnDAO extends BaseDao<FcpRetencaoAliquotaJn> {

	private static final long serialVersionUID = 1814232431655024276L;

	@Override
	public void persistir(FcpRetencaoAliquotaJn fcpRetencaoAliquotaJn) {
		if (fcpRetencaoAliquotaJn != null && fcpRetencaoAliquotaJn.getSeqJn() == null) {
			super.persistir(fcpRetencaoAliquotaJn);
		}
	}

}
