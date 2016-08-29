package br.gov.mec.aghu.faturamento.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatItemProcHospTransp;
import br.gov.mec.aghu.model.FatItemProcHospTranspId;

public class FatItemProcHospTranspDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatItemProcHospTransp> {

	private static final long serialVersionUID = -2204742502205648403L;
	
	
	/**
	 * #41082
	 * persistência da inclusão
	 * relacionamento entre procedimento hospitalar, item de tabela e item de procedimento com transplante 
	 */
	public void persistirProcedimentoHospitalarComTransplante(
			FatItemProcHospTransp entidade) {
		this.atualizar(entidade);
		
	}

	public FatItemProcHospTransp obterDescricaoTransplantePorProcedHospitalar(FatItemProcHospTranspId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemProcHospTranspId.class);

		criteria.createAlias(FatItemProcHospTransp.Fields.FAT_TIPO_TRANSPLANTE.toString(), "tr");
		
		criteria.add(Restrictions.eq(FatItemProcHospTransp.Fields.ID.toString(), id));

		return (FatItemProcHospTransp) executeCriteriaUniqueResult(criteria);
	}

	public FatItemProcHospTransp pesquisarPorIdETransplante(FatItemProcHospTransp item) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemProcHospTransp.class);
		
		criteria.add(Restrictions.eq(FatItemProcHospTransp.Fields.IPH_PHO_SEQ.toString(), item.getId().getIphPhoSeq()));
		criteria.add(Restrictions.eq(FatItemProcHospTransp.Fields.IPH_SEQ.toString(), item.getId().getIphSeq()));
		//criteria.add(Restrictions.eq(FatItemProcHospTransp.Fields.CODIGO_TRANSPLANTE.toString(), item.getFatTipoTransplante().getCodigo()));
		
		return (FatItemProcHospTransp) executeCriteriaUniqueResult(criteria);
	}

		
}