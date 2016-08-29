package br.gov.mec.aghu.controleinfeccao.dao;

import br.gov.mec.aghu.model.MciDuracaoMedidaPreventivaJn;

public class MciDuracaoMedidaPreventivasJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciDuracaoMedidaPreventivaJn> {

	private static final long serialVersionUID = -4314368515782385937L;

	// #36265 I1
	public MciDuracaoMedidaPreventivaJn inserir(MciDuracaoMedidaPreventivaJn entity) {
		 super.persistir(entity);
		 flush();
		 return entity;
	}

}
