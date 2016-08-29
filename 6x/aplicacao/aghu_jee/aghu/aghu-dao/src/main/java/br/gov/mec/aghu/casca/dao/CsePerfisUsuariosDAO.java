package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.ambulatorio.dao.AghPerfilProcessoParametroVOQueryBuilder;
import br.gov.mec.aghu.ambulatorio.vo.ParametrosAghPerfilProcessoVO;
import br.gov.mec.aghu.model.CsePerfisUsuarios;
import br.gov.mec.aghu.model.RapServidores;

public class CsePerfisUsuariosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<CsePerfisUsuarios> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5468065647644984059L;
	
	
	/**
	 * @ORADB P_CHAMA_ATESTADOS - CURSOR cur_modo
	 */
	public List<ParametrosAghPerfilProcessoVO> obterParametroProcedure(RapServidores usuarioLogado){
		AghPerfilProcessoParametroVOQueryBuilder builder = new AghPerfilProcessoParametroVOQueryBuilder();
		DetachedCriteria criteria = builder.build(usuarioLogado.getUsuario());
		
		
		if (criteria == null) {
            return null;
		}
		
		return executeCriteria(criteria);
	}

}
