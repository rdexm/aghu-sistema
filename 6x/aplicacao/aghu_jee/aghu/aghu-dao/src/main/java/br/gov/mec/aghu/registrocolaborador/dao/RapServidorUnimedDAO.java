package br.gov.mec.aghu.registrocolaborador.dao;

import org.hibernate.SQLQuery;

import br.gov.mec.aghu.model.RapServidorUnimed;

public class RapServidorUnimedDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapServidorUnimed>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8963581991987884404L;



	public String obterCarteiraUnimed(Integer codStarh){
		StringBuilder query = new StringBuilder(300);
		query.append("SELECT nvl(tit.num_carteira, 0) "
		+ "FROM rhfp0283 tit "
		+ "WHERE tit.cod_contrato = :valorConsulta "
		+ "AND tit.cod_plano_saude in (1, 3)"
		+ "  AND sysdate between tit.data_inicio"
		+ "  AND nvl(tit.data_fim, sysdate)");
		
		
		final SQLQuery consulta = createSQLQuery(query.toString());
		
		if(codStarh != null){
			consulta.setParameter("valorConsulta", codStarh);
		}
		String carteira = (String) consulta.uniqueResult();
		
		return carteira;		
	}
}
