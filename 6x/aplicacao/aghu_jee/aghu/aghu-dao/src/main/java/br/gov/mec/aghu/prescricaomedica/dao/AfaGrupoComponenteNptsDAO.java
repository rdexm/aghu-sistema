package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * @author thiago.cortes
 *
 */
public class AfaGrupoComponenteNptsDAO  extends BaseDao<AfaComponenteNpt> {

	/**#3506
	 * Preencher suggestionBox
	 * @param pesquisa
	 * @return 
	 */
	public List<AfaGrupoComponenteNpt> pesquisarGrupoComponenteAtivo(final Object pesquisa){
		String aliasGcn = "GCN";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaGrupoComponenteNpt.class,aliasGcn);
		criteria.add(Restrictions.eq(aliasGcn+ponto+AfaGrupoComponenteNpt.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		String strParametro = (String)pesquisa;
		Short seq = null;

		if (CoreUtil.isNumeroShort(strParametro)) {
			seq = Short.valueOf(strParametro);  
		}
		if (seq != null) {
			criteria.add(Restrictions.eq(aliasGcn+ponto+AfaGrupoComponenteNpt.Fields.SEQ.toString(), seq));
		}else {
			criteria.add(Restrictions.ilike(aliasGcn+ponto+AfaGrupoComponenteNpt.Fields.DESCRICAO.toString(),strParametro, MatchMode.ANYWHERE));
		}
		return executeCriteria(criteria);
	}
	public AfaGrupoComponenteNpt obterGrupoComponente(final Short seq){
		String aliasGcn = "GCN";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaGrupoComponenteNpt.class,aliasGcn);
		criteria.add(Restrictions.eq(aliasGcn+ponto+AfaGrupoComponenteNpt.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		if (seq != null) {
			criteria.add(Restrictions.eq(aliasGcn+ponto+AfaGrupoComponenteNpt.Fields.SEQ.toString(), seq));
		}
		return (AfaGrupoComponenteNpt) executeCriteriaUniqueResult(criteria);
	}

}
