package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcGrupoProcedCirurgico;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupo;

public class MbcProcedimentoPorGrupoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcProcedimentoPorGrupo> {

	private static final long serialVersionUID = -5564804244558404117L;

	public List<MbcProcedimentoPorGrupo> listarMbcProcedimentoPorGrupoPorMbcGrupoProcedCirurgico(final MbcGrupoProcedCirurgico mbcGrupoProcedCirurgico) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoPorGrupo.class);

		criteria.add(Restrictions.eq(MbcProcedimentoPorGrupo.Fields.GPC_SEQ.toString(), mbcGrupoProcedCirurgico.getSeq()));

		criteria.createAlias(MbcProcedimentoPorGrupo.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "PROCED");
		criteria.addOrder(Order.asc("PROCED." + MbcGrupoProcedCirurgico.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}

	public List<Short> listarGrupoSeqPorProcedimento(Integer pciSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoPorGrupo.class);

		criteria.setProjection(Projections.property(MbcProcedimentoPorGrupo.Fields.GPC_SEQ.toString()));

		criteria.add(Restrictions.eq(MbcProcedimentoPorGrupo.Fields.PCI_SEQ.toString(), pciSeq));

		return executeCriteria(criteria);
	}

	public List<MbcProcedimentoPorGrupo> pesquisarGrupoPorProcedimento(Integer pciSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoPorGrupo.class);
		criteria.add(Restrictions.eq(MbcProcedimentoPorGrupo.Fields.PCI_SEQ.toString(), pciSeq));
		return executeCriteria(criteria);
	}

	/**
	 * Primeira parte do cursores CUR_PPC e CUR_CIRURGIAS em MBCK_PPC.RN_PPCP_VER_UTLZ_EQU
	 * 
	 * @param grupoPpc
	 * @return
	 */
	public List<Integer> pesquisarVerificarUtilizacaoEquipamentoPorGrupo(Short grupoPpc) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcedimentoPorGrupo.class);

		criteria.createAlias(MbcProcedimentoPorGrupo.Fields.MBC_GRUPO_PROCED_CIRURGICOS.toString(), "gpc", DetachedCriteria.INNER_JOIN);
		criteria.createAlias(MbcProcedimentoPorGrupo.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "pci", DetachedCriteria.INNER_JOIN);

		criteria.setProjection(Projections.property(MbcProcedimentoPorGrupo.Fields.PCI_SEQ.toString()));

		criteria.add(Restrictions.eq("gpc.".concat(MbcGrupoProcedCirurgico.Fields.SEQ.toString()), grupoPpc));

		return executeCriteria(criteria);

	}
}