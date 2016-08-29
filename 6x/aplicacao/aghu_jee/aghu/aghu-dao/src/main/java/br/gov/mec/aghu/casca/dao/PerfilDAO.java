package br.gov.mec.aghu.casca.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PerfilDAO extends BaseDao<Perfil> {

	private static final long serialVersionUID = 6615528062117497336L;
	
	public List<Perfil> pesquisarPerfis(String nome){
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (nome != null) {
			criteria.add(Restrictions.ilike(Perfil.Fields.NOME.toString(),
					nome, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(Perfil.Fields.NOME.toString()));

		return executeCriteria(criteria);
	}

	public List<Perfil> pesquisarPerfisSuggestionBox(String nome)
			throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.or(Restrictions.ilike(
					Perfil.Fields.NOME.toString(), nome, MatchMode.ANYWHERE),
					Restrictions.ilike(Perfil.Fields.DESCRICAO.toString(),
							nome, MatchMode.ANYWHERE)));
		}
		criteria.addOrder(Order.asc(Perfil.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	public List<Perfil> pesquisarPerfis(List<String> perfis)
			throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (perfis != null && perfis.size() > 0) {
			criteria.add(Restrictions.in(Perfil.Fields.NOME.toString(), perfis));
		}
		criteria.addOrder(Order.asc(Perfil.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}

	public List<Perfil> pesquisarPerfis(String nome, String descricao, Integer firstResult,
			Integer maxResult, boolean asc)  {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (nome != null) {
			criteria.add(Restrictions.ilike(Perfil.Fields.NOME.toString(),
					nome, MatchMode.ANYWHERE));
		}
		
		
		if (descricao != null) {
			Criterion criterionDescricaoResumida =   Restrictions.ilike(Perfil.Fields.DESCRICAO_RESUMIDA.toString(),
					descricao, MatchMode.ANYWHERE);
			
			Criterion criterionDescricao =   Restrictions.ilike(Perfil.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE);			
			
			criteria.add(Restrictions.or(criterionDescricaoResumida, criterionDescricao));			
			
		}
		

		criteria.addOrder(Order.asc(Perfil.Fields.NOME.toString()));
		String orderProperty = Perfil.Fields.NOME.toString();
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	public Long pesquisarPerfisCount(String nome, String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (StringUtils.isNotEmpty(nome)) {
			criteria.add(Restrictions.ilike(Perfil.Fields.NOME.toString(),nome, MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotEmpty(descricao)) {
			Criterion criterionDescricaoResumida =   Restrictions.ilike(Perfil.Fields.DESCRICAO_RESUMIDA.toString(), descricao, MatchMode.ANYWHERE);
			Criterion criterionDescricao =   Restrictions.ilike(Perfil.Fields.DESCRICAO.toString(),	descricao, MatchMode.ANYWHERE);			
			criteria.add(Restrictions.or(criterionDescricaoResumida, criterionDescricao));			
		}
		
		
		return executeCriteriaCount(criteria);
	}

	public List<String> obterNomePerfisPorUsuario(String username) {
		if (username == null) {
			return new ArrayList<String>();
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class)
			.add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString())
				.add(Restrictions.or(
						Restrictions.isNull(PerfisUsuarios.Fields.DATA_EXPIRACAO.toString()),
						Restrictions.gt(PerfisUsuarios.Fields.DATA_EXPIRACAO.toString(), new Date())))
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString())
				.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), Boolean.TRUE))
				.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), username).ignoreCase());
		criteria.setProjection(Projections.property(Perfil.Fields.NOME.toString()));
		
		return executeCriteria(criteria, true);
	}

	public Perfil pesquisarPerfil(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		criteria.add(Restrictions.or(
				Restrictions.eq(Perfil.Fields.NOME.toString(), nome.toUpperCase()),
				Restrictions.eq(Perfil.Fields.NOME.toString(), nome.toLowerCase())
			));		
		return (Perfil) executeCriteriaUniqueResult(criteria, true);
	}
	
	public boolean nomePerfilExiste(String nome, Integer IdDescarta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		criteria.add(Restrictions.or(
				Restrictions.eq(Perfil.Fields.NOME.toString(), nome.toUpperCase()),
				Restrictions.eq(Perfil.Fields.NOME.toString(), nome.toLowerCase())
			));		
		if (IdDescarta!=null){
			criteria.add(Restrictions.ne(Perfil.Fields.ID.toString(), IdDescarta));
		}
		return executeCriteriaCount(criteria) > 0;
	}
	
	public Long pesquisarPerfilCountSuggestionBox(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		if (nome != null) {
			criteria.add(Restrictions.or(Restrictions.ilike(
					Perfil.Fields.NOME.toString(), nome, MatchMode.ANYWHERE),
					Restrictions.ilike(Perfil.Fields.DESCRICAO.toString(),
							nome, MatchMode.ANYWHERE)));
		}
		return executeCriteriaCount(criteria);
	}
	
	public List<Perfil> pesquisarPerfisUsuarioAtivoPorNomeUsuario(String nomeUsuario) {
		if (nomeUsuario == null) {
			return new ArrayList<Perfil>();
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		criteria.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString())
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString())
				.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), Boolean.TRUE))
				.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), nomeUsuario).ignoreCase());
		
		return executeCriteria(criteria, true);
	}
	
	/**
	 * #44276 
	 * C8 - Busca perfil na tabela CSC_PERFIL
	 * @return
	 */
	public Perfil buscarPerfil(String nomePerfil){
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		
		criteria.setProjection(Projections.property(Perfil.Fields.ID.toString()).as(Perfil.Fields.ID.toString()));
		
		criteria.add(Restrictions.eq(Perfil.Fields.NOME.toString(), nomePerfil));
		
		criteria.setResultTransformer(Transformers.aliasToBean(Perfil.class));
		
		return (Perfil)executeCriteriaUniqueResult(criteria); 
	}
	
}
