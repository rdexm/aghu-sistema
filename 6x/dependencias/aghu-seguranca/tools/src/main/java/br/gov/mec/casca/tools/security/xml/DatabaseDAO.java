package br.gov.mec.casca.tools.security.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.PerfisPermissoes;
import br.gov.mec.casca.model.PerfisUsuarios;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.casca.model.PermissaoModulo;
import br.gov.mec.casca.model.PermissoesComponentes;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.casca.tools.updater.HibernateHelper;

/**
 * Classe de acesso ao banco de dados para apoio ao script que popula o banco 
 * com permissões e perfis. Para fins de melhoria de performance foi 
 * implementado nesta classe um recurso, que pode ser ligado ou desligado, para
 * o uso de caches (manuais) para alguns tipos de pesquisa mais chamados via 
 * classe DatabaseUpdater.
 * 
 * @author Vicente Grassi Filho
 * @since 14/08/2014
 */
public class DatabaseDAO {
	
	private Map<String, Object> cacheObjetos = new HashMap<String, Object>();
	private Map<String, Metodo> cacheMetodo = new HashMap<String, Metodo>();
	private Map<String, PerfisPermissoes> cachePerfisPermissoes = new HashMap<String, PerfisPermissoes>();
	private Map<String, PerfisUsuarios> cachePerfisUsuarios = new HashMap<String, PerfisUsuarios>();
	private Map<String, PermissoesComponentes> cachePermissoesComponentes = new HashMap<String, PermissoesComponentes>();
	private Map<String, PermissaoModulo> cachePermissaoModulo = new HashMap<String, PermissaoModulo>();
	
	private Long contadorHitsCacheObjetos = 0L;
	private Long contadorHitsCacheMetodo = 0L;
	private Long contadorHitsCachePerfisPermissoes = 0L;
	private Long contadorHitsCachePerfisUsuarios = 0L;
	private Long contadorHitsCachePermissoesComponentes = 0L;
	private Long contadorHitsCachePermissaoModulo = 0L;
	
	// Flag para (des)ligar o uso da cache manual implementada neste DAO
	private boolean habilitarUsoCache = true;
	
	public DatabaseDAO() {
	}
	
	public DatabaseDAO(boolean usarCache) {
		habilitarUsoCache = usarCache;
	}	
	
	protected void carregarPerfilComPermissoes() throws Exception {
		if (!isCacheHabilitada()) {
			return;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisPermissoes.class);
		criteria.createAlias(PerfisPermissoes.Fields.PERMISSAO.toString(), "permissao");
		criteria.createAlias(PerfisPermissoes.Fields.PERFIL.toString(), "perfil");
			
		@SuppressWarnings("unchecked")
		List<PerfisPermissoes> lpp = criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
		
		for (PerfisPermissoes pp : lpp) {
			cachePerfisPermissoes.put(pp.getPerfil().getNome() + pp.getPermissao().getId(), pp);
			
			if (!cacheObjetos.containsKey(Perfil.class.getName() + "_" + pp.getPerfil().getNome())) {
				cacheObjetos.put(Perfil.class.getName() + "_" + pp.getPerfil().getNome(), pp.getPerfil());
			}
			
			if (!cacheObjetos.containsKey(Permissao.class.getName() + "_" + pp.getPermissao().getNome())) {
				cacheObjetos.put(Permissao.class.getName() + "_" + pp.getPermissao().getNome(), pp.getPermissao());
			}			
		}
	}	
	
	protected void carregarPermissoesComponentes() throws Exception {
		if (!isCacheHabilitada()) {
			return;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PermissoesComponentes.class);
		criteria.createAlias(PermissoesComponentes.Fields.PERMISSAO.toString(), "permissao");
		criteria.createAlias(PermissoesComponentes.Fields.COMPONENTE.toString(), "componente");
		criteria.createAlias(PermissoesComponentes.Fields.METODO.toString(), "metodo");
			
		@SuppressWarnings("unchecked")
		List<PermissoesComponentes> lpc = criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();

		for (PermissoesComponentes pc : lpc) {
			cachePermissoesComponentes.put(pc.getPermissao().getId() + "_" + pc.getComponente().getId() + "_" + pc.getMetodo().getId(), pc);
		}
	}
	
	protected void carregarPermissaoModulo() throws Exception {
		if (!isCacheHabilitada()) {
			return;
		}		
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PermissaoModulo.class);
		criteria.createAlias(PermissaoModulo.Fields.PERMISSAO.toString(), "permissao");		
		criteria.createAlias(PermissaoModulo.Fields.MODULO.toString(), "modulo");		
			
		@SuppressWarnings("unchecked")
		List<PermissaoModulo> lpm = criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
		
		for (PermissaoModulo pm : lpm) {
			cachePermissaoModulo.put(pm.getModulo().getNome() + "_" + pm.getPermissao().getNome(), pm);
		}
	}
	
	protected void carregarComponenteComMetodos() throws Exception {
		if (!isCacheHabilitada()) {
			return;
		}		
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class, "con");
		criteria.createAlias(Metodo.Fields.COMPONENTE_SEM_ID.toString(), "componente");

		@SuppressWarnings("unchecked")
		List<Metodo> lm = criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
		
		for (Metodo metodo : lm) {
			cacheObjetos.put(Componente.class.getName()+"_"+metodo.getComponente().getNome(), metodo.getComponente());
			cacheMetodo.put(metodo.getComponente().getNome() + "_" + metodo.getNome(), metodo);
		}
	}		

	protected PermissoesComponentes obterPermissaoComponente(Permissao permissao, Componente componente, Metodo metodo) throws Exception {
		if (isCacheHabilitada() && cachePermissoesComponentes.containsKey(permissao.getId() + "_" + componente.getId() + "_" + metodo.getId())) {
			contadorHitsCachePermissoesComponentes++;
			return cachePermissoesComponentes.get(permissao.getId() + "_" + componente.getId() + "_" + metodo.getId());
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(PermissoesComponentes.class);
		criteria.add(Restrictions.eq(PermissoesComponentes.Fields.PERMISSAO_ID.toString(), permissao.getId()));
		criteria.add(Restrictions.eq(PermissoesComponentes.Fields.COMPONENTE_ID.toString(), componente.getId()));
		criteria.add(Restrictions.eq(PermissoesComponentes.Fields.METODO_ID.toString(), metodo.getId()));
		
		PermissoesComponentes permComp = null;
		try {
			permComp = (PermissoesComponentes) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
			if (isCacheHabilitada() && permComp != null) {
				cachePermissoesComponentes.put(permComp.getPermissao().getId() + "_" + permComp.getComponente().getId() + "_" + permComp.getMetodo().getId(), permComp);
			}
		} catch (Exception e) {
			throw new Exception("Mais de um registro encontrado no banco para PermissoesComponentes com busca feita por: \n"
							+ permissao.toString() + "\n"
							+ componente.toString() + "\n"
							+ metodo.toString(), e);
		}
		
		return permComp;
	}	

	protected PermissaoModulo obterPermissaoModulo(String nomePermissao, String nomeModulo) {
		if (isCacheHabilitada() && cachePermissaoModulo.containsKey(nomeModulo + "_" + nomePermissao)) {
			contadorHitsCachePermissaoModulo++;
			return cachePermissaoModulo.get(nomeModulo + "_" + nomePermissao);
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PermissaoModulo.class);
		criteria.createAlias(PermissaoModulo.Fields.PERMISSAO.toString(), PermissaoModulo.Fields.PERMISSAO.toString())
			.add(Restrictions.eq(PermissaoModulo.Fields.PERMISSAO_NOME.toString(), nomePermissao));
		criteria.createAlias(PermissaoModulo.Fields.MODULO.toString(), PermissaoModulo.Fields.MODULO.toString())
			.add(Restrictions.eq(PermissaoModulo.Fields.MODULO_NOME.toString(), nomeModulo));
		
		PermissaoModulo pm = (PermissaoModulo) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
		if (isCacheHabilitada() && pm != null) {
			cachePermissaoModulo.put(nomeModulo + "_" + nomePermissao, pm);
		}
		
		return pm;
	}
	
	protected Usuario obterUsuario(String login) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class);

		criteria.add(Restrictions.eq("login", login).ignoreCase());
		
		Object object = null;
		try {
			object = criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
		} catch (Exception e) {
			throw new Exception("Mais de um registro encontrado no banco para o usuário: " + login, e);
		}
		
		return object != null ? (Usuario)object : null;
	}
	
	protected Metodo obterMetodo(String nomeMetodo, String componente) throws Exception {
		if (isCacheHabilitada() && cacheMetodo.containsKey(componente + "_" + nomeMetodo)) {
			contadorHitsCacheMetodo++;
			return cacheMetodo.get(componente + "_" + nomeMetodo);
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Metodo.class);
		criteria.add(Restrictions.eq("nome", nomeMetodo));
		criteria.createCriteria(Metodo.Fields.COMPONENTE_SEM_ID.toString()).add(Restrictions.eq("nome", componente));
		
		Metodo metodo = null;
		try {
			metodo = (Metodo)criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
			if (isCacheHabilitada() && metodo != null) {
				cacheMetodo.put(componente + "_" + nomeMetodo, metodo);
			}
		} catch (Exception e) {
			throw new Exception("Mais de um registro encontrado no banco para o Método (target): " + nomeMetodo + " e Componente (action): " + componente, e);
		}
		
		return metodo;
	}
	
	protected <T> T obter(Class<T> classe, String nome) throws Exception {
		return obter(classe, nome, false);
	}	
	
	@SuppressWarnings("unchecked")
	protected <T> T obter(Class<T> classe, String nome, boolean locked) throws Exception {		
		String chave = classe.getName()+"_"+nome;
		Object objetoRetorno = null;

		if (isCacheHabilitada() && cacheObjetos.containsKey(chave)) {
			contadorHitsCacheObjetos++;
			objetoRetorno = cacheObjetos.get(chave);
		}

		if (objetoRetorno == null) {
			LockMode lockMode = locked ? LockMode.UPGRADE : LockMode.NONE;
			
			DetachedCriteria criteria = DetachedCriteria.forClass(classe);
			criteria.add(Restrictions.eq("nome", nome));
			criteria.setLockMode(lockMode);
			
			try {
				objetoRetorno = criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
				if (isCacheHabilitada() && objetoRetorno != null) {
					cacheObjetos.put(chave, objetoRetorno);
				}
			} catch (Exception e) {
				throw new Exception("Mais de um registro encontrado no banco para a classe " + classe.getSimpleName() + " com 'nome' = " + nome, e);
			}
		}
		
		return (T)objetoRetorno;
	}
	
	protected PerfisUsuarios obterPerfilUsuario(Perfil perfil, Usuario usuario) throws Exception {
		if (isCacheHabilitada() && cachePerfisUsuarios.containsKey(perfil.getNome() + usuario.getLogin())) {
			contadorHitsCachePerfisUsuarios++;
			return cachePerfisUsuarios.get(perfil.getNome() + usuario.getLogin());
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisUsuarios.class);
		criteria.add(Restrictions.eq(PerfisUsuarios.Fields.PERFIL.toString(), perfil));
		criteria.add(Restrictions.eq(PerfisUsuarios.Fields.USUARIO.toString(), usuario));
		
		PerfisUsuarios perfilUsuario = (PerfisUsuarios)criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
		
		if (isCacheHabilitada() && perfilUsuario != null) {
			cachePerfisUsuarios.put(perfil.getNome() + usuario.getLogin(), perfilUsuario);
		}
		
		return perfilUsuario;
	}

	protected PerfisPermissoes obterPerfilPermissao(Perfil perfil, Permissao permissao) throws Exception {
		if (isCacheHabilitada() && cachePerfisPermissoes.containsKey(perfil.getNome() + permissao.getId())) {
			contadorHitsCachePerfisPermissoes++;
			return cachePerfisPermissoes.get(perfil.getNome() + permissao.getId());
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PerfisPermissoes.class);
		criteria.add(Restrictions.eq(PerfisPermissoes.Fields.PERMISSAO.toString(), permissao));
		criteria.add(Restrictions.eq(PerfisPermissoes.Fields.PERFIL.toString(), perfil));
		
		PerfisPermissoes perfilPermissao;
		try {
			perfilPermissao = (PerfisPermissoes)criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
		} catch (Exception e) {
			throw new Exception("Mais de um registro encontrado no banco para PerfisPermissoes com busca feita por: \n"
					+ permissao.toString() + "\n"
					+ perfil.getNome(), e);
		}
		
		if (isCacheHabilitada() && perfilPermissao != null) {
			cachePerfisPermissoes.put(perfil.getNome() + permissao.getId(), perfilPermissao);
		}
		
		return perfilPermissao;
	}

	public Long getContadorHitsCacheObjetos() {
		return contadorHitsCacheObjetos;
	}

	public Long getContadorHitsCacheMetodo() {
		return contadorHitsCacheMetodo;
	}

	public Long getContadorHitsCachePerfisPermissoes() {
		return contadorHitsCachePerfisPermissoes;
	}

	public Long getContadorHitsCachePerfisUsuarios() {
		return contadorHitsCachePerfisUsuarios;
	}

	public Long getContadorHitsCachePermissoesComponentes() {
		return contadorHitsCachePermissoesComponentes;
	}

	public Long getContadorHitsCachePermissaoModulo() {
		return contadorHitsCachePermissaoModulo;
	}

	public boolean isCacheHabilitada() {
		return habilitarUsoCache;
	}
}