package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatLaudosImpressao;

public class FatLaudosImpressaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatLaudosImpressao> {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3973806543412985688L;

	/**
	 * 42011
	 * @param codigoPaciente
	 * @return
	 */
	public List<FatLaudosImpressao> listarLaudos(Integer pacienteCodigo, String tipoSinalizacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatLaudosImpressao.class);
		
		criteria.add(Restrictions.eq(FatLaudosImpressao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(FatLaudosImpressao.Fields.TIPO_SINALIZACAO.toString(), tipoSinalizacao));
		criteria.add(Restrictions.eq(FatLaudosImpressao.Fields.PAC_CODIGO.toString(), pacienteCodigo));
		
		return executeCriteria(criteria);
	}
}
