package br.gov.mec.aghu.casca.dao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisPermissoes;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.casca.model.PermissaoModulo;
import br.gov.mec.aghu.casca.model.PermissoesComponentes;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.casca.security.vo.PermissaoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;

public class PermissaoDAO extends BaseDao<Permissao> {
	
	private static final String PERFIL_USUARIO = "perfilUsuario.";

	private static final long serialVersionUID = -7370627135066448378L;
	
	private static final String aliasUsuario = "usuario";
	
	@Inject
	private DataAccessService dataAcess;

	public List<Permissao> pesquisarPermissoes(String nome, String descricao, Modulo modulo, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.createAlias(Permissao.Fields.PERMISSOES_MODULOS.toString(), "pm", JoinType.LEFT_OUTER_JOIN);
		
		if(nome != null){
			criteria.add(Restrictions.ilike(Permissao.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		if(descricao != null){
			criteria.add(Restrictions.ilike(Permissao.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (modulo != null) {
			criteria.add(Restrictions.eq("pm." + PermissaoModulo.Fields.MODULO.toString(), modulo));
		}
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	

	public List<Permissao> pesquisarPermissoesSuggestionBox(String busca) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		if (StringUtils.isNotEmpty(busca)){
			if (StringUtils.isNumeric(busca)){
				criteria.add(Restrictions.eq(Permissao.Fields.ID.toString(), Integer.valueOf(busca)));
			}else{
				criteria.add(Restrictions.or(Restrictions.ilike(Permissao.Fields.NOME.toString(), busca,
						MatchMode.ANYWHERE), Restrictions.ilike(Permissao.Fields.DESCRICAO.toString(),
						busca, MatchMode.ANYWHERE)));
			}	
		}	
		return executeCriteria(criteria, 0, SUGGESTION_MAX_RESULT, Permissao.Fields.NOME.toString(), true);
	}
	
	
	public Long pesquisarPermissoesCountSuggestionBox(String busca) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		if (StringUtils.isNotEmpty(busca)){
			if (StringUtils.isNumeric(busca)){
				criteria.add(Restrictions.eq(Permissao.Fields.ID.toString(), Integer.valueOf(busca)));
			}else{
				criteria.add(Restrictions.or(Restrictions.ilike(Permissao.Fields.NOME.toString(), busca,
					MatchMode.ANYWHERE), Restrictions.ilike(Permissao.Fields.DESCRICAO.toString(),
					busca, MatchMode.ANYWHERE)));
			}	
		}	
		return executeCriteriaCount(criteria);
	}
	

	public List<Permissao> pesquisarTodasPermissoes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		return executeCriteria(criteria);
	}

	public Long pesquisarPermissoesCount(String nome, String descricao, Modulo modulo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		if(nome != null){
			criteria.add(Restrictions.ilike(Permissao.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		if(descricao != null){
			criteria.add(Restrictions.ilike(Permissao.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (modulo != null) {
			criteria.createAlias(Permissao.Fields.PERMISSOES_MODULOS.toString(), "pm");
			criteria.add(Restrictions.eq("pm." + PermissaoModulo.Fields.MODULO.toString(), modulo));
		}
		Long totalRegistros = executeCriteriaCount(criteria);
		return totalRegistros;
	}

	public PermissoesComponentes obterComponentePermissao(Integer idPermissaoComponente) throws ApplicationBusinessException {
		return dataAcess.find(idPermissaoComponente, PermissoesComponentes.class);
	}
	
	@Deprecated
	public List<PermissaoVO> obterPermissoesPrecarregaveis(String aplicacao,
			String targets, String actions, String login) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.eq(Permissao.Fields.ATIVO.toString(), DominioSituacao.A));

		// Target
		DetachedCriteria criteriaPermissoesComponentes = criteria
				.createCriteria(Permissao.Fields.PERMISSOES_COMPONENTES.toString(), "permissaoComponente");

		DetachedCriteria criteriaComponente = criteriaPermissoesComponentes
			.createCriteria(PermissoesComponentes.Fields.COMPONENTE.toString(),"componente");
		
		if (targets != null) {
			criteriaComponente.add(Restrictions.ilike(Componente.Fields.NOME.toString(), targets));
		}
		
		// Aplicacao
		DetachedCriteria criteriaAplicacao = criteriaComponente.createCriteria(
				Componente.Fields.APLICACAO.toString(), "aplicacao");
		if (aplicacao != null) {			
			criteriaAplicacao.add(Restrictions.ilike(
					Aplicacao.Fields.NOME.toString(), aplicacao));
		}

		// Action
		DetachedCriteria criteriaAction = criteriaPermissoesComponentes.createCriteria(
				PermissoesComponentes.Fields.METODO.toString(), "metodo")				
				.add(Restrictions.eq(Metodo.Fields.ATIVO.toString(), true));
		if (actions != null) {
			criteriaAction.add(Restrictions.ilike(Metodo.Fields.NOME.toString(), actions));
		}

		// Usuario		
		criteriaPermissoesComponentes
				.createCriteria(PermissoesComponentes.Fields.PERMISSAO.toString(), "permissao")
				.createCriteria(Permissao.Fields.PERFIS_PERMISSOES.toString(), "perfilPermissao")
				.createCriteria(PerfisPermissoes.Fields.PERFIL.toString(), "perfil")
					.add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(), DominioSituacao.A))
				.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString(), "perfilUsuario")
					.add(Restrictions.or(
							Restrictions.isNull(PERFIL_USUARIO + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString()),
							Restrictions.gt(PERFIL_USUARIO + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString(), new Date())))
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString(), aliasUsuario)										
					.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), Boolean.TRUE));
		
		if (login != null) {
			criteriaPermissoesComponentes
				.add(Restrictions.eq(aliasUsuario+"."+Usuario.Fields.LOGIN.toString(), login).ignoreCase());
		}
		
		// OBS: por questões de performance, manter a ordenação
		// na seguinte ordem: target (componente), action (metodo)		
		criteria.addOrder(Order.asc("componente.nome"));
		criteria.addOrder(Order.asc("metodo.nome"));

		criteria.setProjection(Projections.projectionList()
				.add(Property.forName("componente.nome"), "target")
				.add(Property.forName("metodo.nome"), "action")
				.add(Property.forName("aplicacao.nome"), "aplicacao") 
				.add(Property.forName(aliasUsuario+".login"), "login"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY); 

		criteria.setResultTransformer(Transformers
				.aliasToBean(PermissaoVO.class));

		return executeCriteria(criteria);
	}

	@Deprecated
	public List<PermissaoVO> obterPermissoes(String aplicacao, String target, String action) {		
		return obterPermissoesPrecarregaveis(aplicacao, target, action, null);
	}

	public List<Metodo> recuperaMetodosComponentePermissao(Integer idPermissao,
			Integer idComponente, boolean contem) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class);

		criteria.createCriteria(Metodo.Fields.COMPONENTE_SEM_ID.toString()).add(
				Restrictions.eq(Componente.Fields.ID.toString(), idComponente));

		if (!contem) {
			List<Metodo> metodosQueContem = recuperaMetodosComponentePermissao(idPermissao,
					idComponente, true);
			if (!metodosQueContem.isEmpty()) {
				for (Metodo metodo : metodosQueContem) {
					criteria.add(Restrictions.not(Restrictions.eq(Metodo.Fields.ID.toString(),
							metodo.getId())));
				}
			}
		} else {
			criteria.createCriteria(Metodo.Fields.PERMISSOES_COMPONENTES.toString(),
					Criteria.INNER_JOIN).add(
					Restrictions.eq(PermissoesComponentes.Fields.PERMISSAO_ID.toString(),
							idPermissao));
		}

		return executeCriteria(criteria,true);
	}

	/**
	 * 
	 * @param permissao
	 * @param componente
	 
	public void removePermissaoComponenteMetodos(Permissao permissao, Componente componente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PermissoesComponentes.class);
		criteria.add(Restrictions.eq(PermissoesComponentes.Fields.PERMISSAO_ID.toString(),
				permissao.getId()));
		criteria.add(Restrictions.eq(PermissoesComponentes.Fields.COMPONENTE_ID.toString(),
				componente.getId()));
		List<PermissoesComponentes> lista = executeCriteria(criteria);
		for (PermissoesComponentes permissoesComponentes : lista) {
			getEntityManager().remove(permissoesComponentes);
		}
	}

	/**
	 * 
	 * @param permissao
	 * @param componente
	 * @param listaDeMetodos
	 */
	 
	public void associarPermissaoComponenteMetodos(Permissao permissao, Componente componente,Metodo metodo) {
		PermissoesComponentes permissoesComponentes;
		permissoesComponentes = new PermissoesComponentes();
		permissoesComponentes.setComponente(componente);
		permissoesComponentes.setPermissao(permissao);
		permissoesComponentes.setMetodo(metodo);
		
		dataAcess.persist(permissoesComponentes);
	}

	public Permissao obterPermissao(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.eq(Permissao.Fields.NOME.toString(), nome));
		return (Permissao) executeCriteriaUniqueResult(criteria,true);
	}
	
	
	public boolean verificarNomePermissaoJaExiste(Integer id, String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.eq(Permissao.Fields.NOME.toString(), nome));
		if (id!=null){
			criteria.add(Restrictions.ne(Permissao.Fields.ID.toString(), id));
		}	
		return executeCriteriaUniqueResult(criteria,true)!=null;
	}	
	

	public Permissao obterPermissao(String nome, String login) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.add(Restrictions.eq(Permissao.Fields.NOME.toString(), nome));
		criteria.createCriteria(Permissao.Fields.PERFIS_PERMISSOES.toString())
				.createCriteria(PerfisPermissoes.Fields.PERFIL.toString())
				.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString())
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString())
				.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), login).ignoreCase());
		
		return (Permissao) executeCriteriaUniqueResult(criteria,true);
	}


	public List<PermissoesComponentes> obterPermissao(String login, String componente, String metodo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PermissoesComponentes.class);

		DetachedCriteria criteriaComponente = criteria
				.createCriteria(PermissoesComponentes.Fields.COMPONENTE.toString());
		criteriaComponente.add(Restrictions.eq(Componente.Fields.NOME.toString(), componente));

		criteria.createCriteria(PermissoesComponentes.Fields.METODO.toString(), "metodo")
				.add(Restrictions.eq(Metodo.Fields.NOME.toString(), metodo))
				.add(Restrictions.eq(Metodo.Fields.ATIVO.toString(), true));

		criteria.createCriteria(PermissoesComponentes.Fields.PERMISSAO.toString(), "permissao")
				.add(Restrictions.eq(Permissao.Fields.ATIVO.toString(), DominioSituacao.A))
			.createCriteria(Permissao.Fields.PERFIS_PERMISSOES.toString(), "perfilPermissao")				
			.createCriteria(PerfisPermissoes.Fields.PERFIL.toString(), "perfil")
				.add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(), DominioSituacao.A))
			.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString(), "perfilUsuario")	
				.add(Restrictions.or(
						Restrictions.isNull(PERFIL_USUARIO + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString()),
						Restrictions.gt(PERFIL_USUARIO + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString(), new Date())))
			.createCriteria(PerfisUsuarios.Fields.USUARIO.toString())
				.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), Boolean.TRUE))
				.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), login).ignoreCase());
		
		return executeCriteria(criteria,true);
	}
	
	/**
	 * Retorna true se o usuário possui permissoes para o componente e método fornecido.
	 * 
	 * @param login identifica usuário
	 * @param componente nome do componente
	 * @param metodo nome do método
	 * 
	 * @return
	 */
	public boolean temPermissao(String login, String componente, String metodo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PermissoesComponentes.class);

		DetachedCriteria criteriaComponente = criteria
				.createCriteria(PermissoesComponentes.Fields.COMPONENTE.toString());
		criteriaComponente.add(Restrictions.eq(Componente.Fields.NOME.toString(), componente));

		criteria.createCriteria(PermissoesComponentes.Fields.METODO.toString(), "metodo")
				.add(Restrictions.eq(Metodo.Fields.NOME.toString(), metodo))
				.add(Restrictions.eq(Metodo.Fields.ATIVO.toString(), true));

		DetachedCriteria criteriaPermissao = criteria.createCriteria(PermissoesComponentes.Fields.PERMISSAO.toString());
		this.addCriteriaPermissoesAtivas(criteriaPermissao, login);
		
        Long count = executeCriteriaCount(criteria);
		
		return (count > 0);
	}
	
	
	/**
	 * Adiciona os critérios para retornar permissoes ativas a válidas do usuário.
	 * 
	 * @param criteriaPermissao
	 *            deve ser um criteria de permissao criado normalmente com
	 *            criteria.createCriteria(PermissoesComponentes.Fields.PERMISSAO.toString())
	 *            onde serão adicionadas todas as condições para
	 *            retornar permissoes ativas e validas do usuario fornecido.
	 * @param login identifica usuário
	 */
	private void addCriteriaPermissoesAtivas(
			DetachedCriteria criteriaPermissao, String login) {
		criteriaPermissao
				.add(Restrictions.eq(Permissao.Fields.ATIVO.toString(), DominioSituacao.A))
				.createCriteria(Permissao.Fields.PERFIS_PERMISSOES.toString(), "perfilPermissao")
				.createCriteria(PerfisPermissoes.Fields.PERFIL.toString(), "perfil")
				.add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(), DominioSituacao.A))
				.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString(), "perfilUsuario")
				.add(Restrictions.or(
						Restrictions.isNull(PERFIL_USUARIO + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString()),
						Restrictions.gt(PERFIL_USUARIO + PerfisUsuarios.Fields.DATA_EXPIRACAO.toString(), new Date())))
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString())
				.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), Boolean.TRUE))
				.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), login).ignoreCase());
	}

	
	
	public void excluirPermissoesComponentes(PermissoesComponentes permissoesComponentes) {
		dataAcess.remove(permissoesComponentes);
	}
	
	public List<Permissao> obterPermissoesPorPerfil(Perfil perfil) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Permissao.class);
		criteria.createAlias(Permissao.Fields.PERFIS_PERMISSOES.toString(), "perfis_permissao");
		criteria.add(Restrictions.eq("perfis_permissao." + PerfisPermissoes.Fields.PERFIL.toString(), perfil));
		return executeCriteria(criteria);
	}
}