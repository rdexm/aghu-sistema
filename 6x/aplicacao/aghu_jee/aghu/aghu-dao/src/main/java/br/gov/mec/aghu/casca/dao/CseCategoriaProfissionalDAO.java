package br.gov.mec.aghu.casca.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CseCategoriaPerfil;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * Classe de acesso a base de dados responsáveis pelas operações relativas a
 * tabela MAM_DIAGNOSTICOS.
 * 
 */
public class CseCategoriaProfissionalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<CseCategoriaProfissional> {

	private static final long serialVersionUID = -8906285528945564832L;
	
	@Inject
	private PerfilDAO perfilDAO;

	/**
	 * Retorna a criteria da categoria profissional do usuário logado.
	 * 
	 * @param servidor
	 * ORA-DB MAMC_GET_CAT_PROFIS
	 * @return
	 */
	public DetachedCriteria criteriaPesquisarCategoriaProfissional (Collection<String> perfisUsuario) {
		
		String[] perfis = perfisUsuario.toArray(new String[perfisUsuario.size()]);

		DetachedCriteria criteria = DetachedCriteria.forClass(CseCategoriaPerfil.class).setProjection(Property.forName(CseCategoriaPerfil.Fields.CSE_CATEGORIA_PROFISSIONAL.toString()));
		DetachedCriteria categoriaCriteria = criteria.createCriteria(CseCategoriaPerfil.Fields.CSE_CATEGORIA_PROFISSIONAL.toString());
		categoriaCriteria.add(Restrictions.eq(CseCategoriaProfissional.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(CseCategoriaPerfil.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in(CseCategoriaPerfil.Fields.PERFIL.toString(), perfis));
		
		return criteria;
	}
	
	/**
	 * Retorna a categoria profissional do usuário logado.
	 * 
	 * @param servidor
	 * ORA-DB MAMC_GET_CAT_PROFIS
	 * @return
	 */
	public List<CseCategoriaProfissional> pesquisarCategoriaProfissional (RapServidores servidor) {
		if (servidor == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		
		List<String> perfisUsuario = obterNomePerfisPorUsuario(servidor.getUsuario());
		if (perfisUsuario == null || perfisUsuario.isEmpty()) {
			return new ArrayList<CseCategoriaProfissional>();
		}
		
		DetachedCriteria criteria = this.criteriaPesquisarCategoriaProfissional(perfisUsuario);
		
		return this.executeCriteria(criteria, true);
	}
	

	public CseCategoriaProfissional primeiraCategoriaProfissional(RapServidores servidor) {
		if (servidor == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		
		List<String> perfisUsuario = obterNomePerfisPorUsuario(servidor.getUsuario());
		if (perfisUsuario == null || perfisUsuario.isEmpty()) {
			return null;
		}
	
		DetachedCriteria criteria = this.criteriaPesquisarCategoriaProfissional(perfisUsuario);
		
		List<CseCategoriaProfissional> list = this.executeCriteria(criteria, 0, 1, null, true);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	private List<String> obterNomePerfisPorUsuario(String login)  {
		List<String> perfisUsuario = perfilDAO.obterNomePerfisPorUsuario(login);
		if (perfisUsuario == null || perfisUsuario.isEmpty()) {
			return null;
		}
		
		return perfisUsuario;	
	}	
	
	public List<CseCategoriaProfissional> pesquisarListaCseCategoriaProfissional(Object filtro) {
		
		DetachedCriteria criteria = createPesquisaCseCategoriaProfissionalCriteria((String) filtro);
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarListaCseCategoriaProfissionalCount(Object filtro) {
		
		DetachedCriteria criteria = createPesquisaCseCategoriaProfissionalCriteria((String) filtro);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria createPesquisaCseCategoriaProfissionalCriteria(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CseCategoriaProfissional.class);
		
		if (StringUtils.isNotBlank(filtro)) {
			Criterion descricaoCriterion = Restrictions.ilike(CseCategoriaProfissional.Fields.NOME.toString(), filtro, MatchMode.ANYWHERE);
			
			if (CoreUtil.isNumeroShort(filtro)) {
				criteria.add(Restrictions.or(Restrictions.eq(CseCategoriaProfissional.Fields.SEQ.toString(), Integer.valueOf(filtro)), descricaoCriterion));
			} else {
				criteria.add(descricaoCriterion);
			}
		}
		
		return criteria;
	}
}
