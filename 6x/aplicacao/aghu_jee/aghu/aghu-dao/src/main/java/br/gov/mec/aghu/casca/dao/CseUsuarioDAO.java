package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.ambulatorio.dao.AghCseUsuarioParametroVOQueryBuilder;
import br.gov.mec.aghu.model.CseProcessos;
import br.gov.mec.aghu.model.CseUsuario;
import br.gov.mec.aghu.model.RapServidores;

public class CseUsuarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<CseUsuario>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8605111018337057067L;

	/**
	 * @ORADB P_CHAMA_ATESTADOS - CURSOR c_tem_dado_cons
	 */
	public List<CseProcessos> obterParametroProcedure(RapServidores usuarioLogado, Integer codConsulta){
		AghCseUsuarioParametroVOQueryBuilder builder = new AghCseUsuarioParametroVOQueryBuilder();
		DetachedCriteria criteria = builder.build(usuarioLogado.getUsuario(), codConsulta);
		
		
		if (criteria == null) {
            return null;
		}
		
		return executeCriteria(criteria);
	}
	
}
