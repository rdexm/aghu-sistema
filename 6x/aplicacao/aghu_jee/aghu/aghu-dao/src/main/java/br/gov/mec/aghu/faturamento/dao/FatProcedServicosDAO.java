package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedServicos;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FatProcedServicosDAO  extends BaseDao<FatProcedServicos> {

	private static final long serialVersionUID = -1317945709783021539L;

	public List<FatProcedServicos> obterFatProcedServicosPorCodTabelaEPhoSeq(final Long codTabela, final Short phoSeq, final String dtCompetencia) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedServicos.class, "FPS");
		
		criteria.createAlias(FatProcedServicos.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), "IPH");
		
		criteria.add(Restrictions.eq("IPH."+FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabela));
		criteria.add(Restrictions.eq("IPH."+FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq));
		criteria.add(Restrictions.eq("FPS."+FatProcedServicos.Fields.DT_COMPETENCIA.toString(), dtCompetencia));
		
		criteria.add(Restrictions.eq(FatProcedServicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
}
