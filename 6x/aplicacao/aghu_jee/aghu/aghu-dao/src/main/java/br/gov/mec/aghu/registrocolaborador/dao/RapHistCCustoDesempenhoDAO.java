package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RapHistCCustoDesempenho;

/**
 * @modulo registrocolaborador
 */
public class RapHistCCustoDesempenhoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapHistCCustoDesempenho> {

	private static final long serialVersionUID = 5315741999141643447L;

	/**
	 * Obter o histórico do centro de custo de desempenho para posterior atualização
	 * @param codigoVinculo
	 * @param matricula
	 * @param codigoCctDesempenho
	 * @return
	 */
	public RapHistCCustoDesempenho obterHistCCustoDesempenho(
			Short codigoVinculo, Integer matricula, Integer codigoCctDesempenho) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapHistCCustoDesempenho.class);

		criteria.add(Restrictions.eq(
				RapHistCCustoDesempenho.Fields.CODIGO_SERVIDOR.toString(),
				codigoVinculo));
		criteria.add(Restrictions.eq(
				RapHistCCustoDesempenho.Fields.MATRICULA_SERVIDOR.toString(),
				matricula));
		criteria.add(Restrictions.eq(
				RapHistCCustoDesempenho.Fields.CODIGO_CENTRO_CUSTO.toString(),
				codigoCctDesempenho));
		
		List<RapHistCCustoDesempenho> lista = executeCriteria(criteria);

		if(lista != null && !lista.isEmpty()){
			return lista.get(0);
		}else{
			return null;
		}			
	}
}
