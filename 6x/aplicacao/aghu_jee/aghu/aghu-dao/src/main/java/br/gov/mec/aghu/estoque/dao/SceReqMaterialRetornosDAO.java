package br.gov.mec.aghu.estoque.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.model.SceReqMaterialRetornos;

public class SceReqMaterialRetornosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceReqMaterialRetornos> {
	
	
	private static final long serialVersionUID = -5949355424630114347L;

	/**
	 * Retorna a quantidade de requisições de materiais de retornos dependentes do pacote de materiais
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @return
	 */
	public Long obterQuantidadeRequisicaoMateriaisRetornoPorCentrosCustosNumeroPacoteMaterial(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer numeroPacote){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceReqMaterialRetornos.class, "RMR");
		
		criteria.add(Restrictions.eq("RMR." + SceReqMaterialRetornos.Fields.PACOTE_MATERIAL.toString() + "." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO,
				codigoCentroCustoProprietario));
		
		criteria.add(Restrictions.eq("RMR." + SceReqMaterialRetornos.Fields.PACOTE_MATERIAL.toString() + "." + ScePacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO,
				codigoCentroCustoAplicacao));
		
		criteria.add(Restrictions.eq("RMR." + SceReqMaterialRetornos.Fields.PACOTE_MATERIAL.toString() + "." + ScePacoteMateriais.Fields.NUMERO,
				numeroPacote));
		
		return executeCriteriaCount(criteria);
		
	}
	
}

