package br.gov.mec.aghu.ambulatorio.dao;

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
	public List<FatLaudosImpressao> listarLaudos(Integer pacienteCodigo, String tipoSinalizacao, Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatLaudosImpressao.class, "FLI");
		
		criteria.add(Restrictions.eq("FLI." + FatLaudosImpressao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("FLI." + FatLaudosImpressao.Fields.TIPO_SINALIZACAO.toString(), tipoSinalizacao));
		criteria.add(Restrictions.eq("FLI." + FatLaudosImpressao.Fields.PAC_CODIGO.toString(), pacienteCodigo));
		criteria.add(Restrictions.eq("FLI." + FatLaudosImpressao.Fields.CON_NUMERO.toString(), conNumero));
		
		return executeCriteria(criteria);
	}
}
