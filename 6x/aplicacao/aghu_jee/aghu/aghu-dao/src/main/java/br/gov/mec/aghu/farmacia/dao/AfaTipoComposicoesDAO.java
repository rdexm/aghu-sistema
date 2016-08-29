package br.gov.mec.aghu.farmacia.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.AfaCompoGrupoComponenteVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @author marcelo.corati
 *
 */
public class AfaTipoComposicoesDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaTipoComposicoes> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3337925368164950607L;

	public List<AfaTipoComposicoes> obterListaSuggestion(String strPesquisa,List<AfaCompoGrupoComponenteVO> lista) {
		List<Short> ids = new ArrayList<Short>();
		for (AfaCompoGrupoComponenteVO item : lista) {
			ids.add(item.getTicSeq());
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoComposicoes.class);
		String str = strPesquisa;
		
		if (StringUtils.isNotBlank(str)) {
			if (CoreUtil.isNumeroShort(str)) {
				criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.SEQ.toString(), Short.valueOf(str)));			
			} else {
				criteria.add(Restrictions.ilike(AfaTipoComposicoes.Fields.DESCRICAO.toString(), str, MatchMode.ANYWHERE));
			}
		}
		
		if(ids.size() > 0){
			criteria.add(Restrictions.not(Restrictions.in(AfaTipoComposicoes.Fields.SEQ.toString(),ids)));
		}
		
		criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return this.executeCriteria(criteria, 0, 100, null, false);
	}
	
	public AfaTipoComposicoes obterTipoPorSeq(Short seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoComposicoes.class);
		
		criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.SEQ.toString(), seq));
		
		return (AfaTipoComposicoes) this.executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * C05 falta Vo
	 * @param filtro
	 * @return
	 */
	public DetachedCriteria  criaPesquisaAfaTipoComposicoesPorFiltro(Object objPesquisa){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoComposicoes.class);
		String strPesquisa = (String) objPesquisa;
		criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (CoreUtil.isNumeroShort(strPesquisa)) {
			criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AfaTipoComposicoes.Fields.DESCRICAO.toString(), strPesquisa,MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	/** C5
	 * Consultas para o suggestionBox
	 * @param filtro
	 * @return
	 */
	public List<AfaTipoComposicoes> pesquisaAfaTipoComposicoesPorFiltro(Object objPesquisa){
		DetachedCriteria criteria = criaPesquisaAfaTipoComposicoesPorFiltro(objPesquisa);
		return executeCriteria(criteria, 0, 100, "seq", true);
	}
	public long pesquisaAfaTipoComposicoesPorFiltroCount(Object objPesquisa){
		DetachedCriteria criteria = criaPesquisaAfaTipoComposicoesPorFiltro(objPesquisa);
		return executeCriteriaCount(criteria);
	}
	/**
	 * Consulta c8
	 * @param seq
	 * @return
	 */
	public String consultaUsuarioCriadorComposicao(Long seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoComposicoes.class);
		
		criteria.createAlias(AfaTipoComposicoes.Fields.RAP_SERVIDOR.toString(), "rps");
		criteria.createAlias("rps"+RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.add(Restrictions.eq(AfaTipoComposicoes.Fields.SEQ.toString(), seq));
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("pes."+RapPessoasFisicas.Fields.NOME.toString()));
		
		return (String) executeCriteriaUniqueResult(criteria);
	}

}
