package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelProjetoIntercProc;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;

public class AelProjetoIntercProcDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelProjetoIntercProc> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4818383296908183962L;

	/**
	 * Obtem AelProjetoIntercProc através do projeto do paciente, procedimento cirúrgicocom, efetivado e com quantidade disponível
	 * 
	 * @param pjqSeq
	 * @param pacCodigo
	 * @param pciSeq
	 * @return
	 */
	public AelProjetoIntercProc obterProjetoIntercProcProjetoPacienteQuantidadeEfetivado(Integer pjqSeq, Integer pacCodigo, Integer pciSeq) {

		if (pjqSeq == null || pacCodigo == null || pciSeq == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório não informado");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoIntercProc.class);

		criteria.createAlias(AelProjetoIntercProc.Fields.AEL_PROJETO_PACIENTES.toString(), "ppj");
		criteria.createAlias(AelProjetoIntercProc.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "pci");

		criteria.add(Restrictions.gt("ppj.".concat(AelProjetoPacientes.Fields.PJQ_SEQ.toString()), pjqSeq));
		criteria.add(Restrictions.gt("ppj.".concat(AelProjetoPacientes.Fields.PAC_CODIGO.toString()), pacCodigo));
		criteria.add(Restrictions.gt("pci.".concat(MbcProcedimentoCirurgicos.Fields.SEQ.toString()), pciSeq));

		// A quantidade deve ser maior que ZERO
		criteria.add(Restrictions.gt(AelProjetoIntercProc.Fields.QTDE.toString(), Short.valueOf("0")));

		// Deve estar efetivado
		criteria.add(Restrictions.eq(AelProjetoIntercProc.Fields.EFETIVADO.toString(), Boolean.FALSE));

		return null;
	}

}
