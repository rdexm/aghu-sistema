package br.gov.mec.casca.tools.security.xml;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.casca.model.PerfisPermissoes;
import br.gov.mec.casca.model.PerfisUsuarios;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.casca.model.PermissaoModulo;
import br.gov.mec.casca.model.PermissoesComponentes;
import br.gov.mec.casca.tools.updater.Cleaner;
import br.gov.mec.casca.tools.updater.HibernateHelper;

class DatabaseCleaner {
	
	private Set<Integer> listaComponentes;
	
	private Set<Integer> listaMetodos;
	
	private Set<Integer> listaAplicacoes;
	
	private Set<Integer> listaPermissoes;

	private Set<Integer> listaPermissoesComponentes;
	
	private Set<Integer> listaPerfis;
	
	private Set<Integer> listaPerfisPermissoes;
	
	private Set<Integer> listaUsuarios;
	
	private Set<Integer> listaUsuariosPerfis;
	
	private Set<Integer> listaModulos;
	
	private Set<Integer> listaPermissoesModulo;
	
	private static Logger getLogger() {
		return Logger.getLogger(DatabaseCleaner.class);
	}
	
	DatabaseCleaner() {
		listaComponentes = new HashSet<Integer>();
		listaMetodos = new HashSet<Integer>();
		listaAplicacoes = new HashSet<Integer>();
		listaPermissoes = new HashSet<Integer>();
		listaPermissoesComponentes = new HashSet<Integer>();
		listaPerfis = new HashSet<Integer>();
		listaPerfisPermissoes = new HashSet<Integer>();
		listaUsuarios = new HashSet<Integer>();
		listaUsuariosPerfis = new HashSet<Integer>();
		listaModulos = new HashSet<Integer>();
		listaPermissoesModulo = new HashSet<Integer>();
	}
	
	void addComponente(Integer id) {
		listaComponentes.add(id);
	}
	
	void addMetodo(Integer id) {
		listaMetodos.add(id);
	}
	
	void addAplicacao(Integer id) {
		listaAplicacoes.add(id);
	}
	
	void addPermissao(Integer id) {
		listaPermissoes.add(id);
	}
	
	void addPermissaoComponente(Integer id) {
		listaPermissoesComponentes.add(id);
	}
	
	void addPerfilPermissao(Integer id) {
		listaPerfisPermissoes.add(id);
	}
	
	void addUsuarioPerfil(Integer id) {
		listaUsuariosPerfis.add(id);
	}
	
	void addUsuario(Integer id) {
		listaUsuarios.add(id);
	}
	
	void addPerfil(Integer id) {
		listaPerfis.add(id);
	}
	
	void addModulo(Integer id) {
		listaModulos.add(id);
	}
	
	void addPermissaoModulo(Integer id) {
		listaPermissoesModulo.add(id);
	}
	
	protected void cleanBeforeExecute() throws Exception {
			HibernateHelper.getSingleton().getSession().createQuery("delete from "+PermissoesComponentes.class.getName()).executeUpdate();
			HibernateHelper.getSingleton().getSession().createQuery("delete from "+Metodo.class.getName()).executeUpdate();
			HibernateHelper.getSingleton().getSession().createQuery("delete from "+Componente.class.getName()).executeUpdate();
			HibernateHelper.getSingleton().getSession().createQuery("delete from "+PermissaoModulo.class.getName()).executeUpdate();
			getLogger().warn("Excluiu todos os registros associados às entidades persitentes: PermissoesComponentes, Metodo, Componente, PermissaoModulo");
	} 			
	
	protected void cleanup() throws Exception {
		getLogger().info("Limpando registros não mapeados...");
		
		getLogger().info("OBS: Usuários e perfis existentes no banco de dados serão mantidos.");
		
		if (!listaUsuarios.isEmpty()) {
			new Cleaner<PerfisUsuarios>(listaUsuariosPerfis) {
				protected void preDelete(PerfisUsuarios perfUser) {
					String nomePerfil = perfUser.getPerfil().getNome();
					String nomeUsuario = perfUser.getUsuario().getNome();

					getLogger().info("Removendo o perfil '"+nomePerfil+"' do usuario '"+nomeUsuario+"' (PerfisUsuarios.id = "+perfUser.getId()+")");
				}

				protected void manageCriteria(DetachedCriteria criteria) {
					criteria.add(Restrictions.in("usuario.id", listaUsuarios));
				}
			}.clean();
		}
		
		if (!listaPerfis.isEmpty()) {
			new Cleaner<PerfisPermissoes>(listaPerfisPermissoes) {
				protected void preDelete(PerfisPermissoes perfPerm) {
					String nomePerfil = perfPerm.getPerfil().getNome();
					String nomePermissao = perfPerm.getPermissao().getNome();

					getLogger().info("Removendo a permissao '"+nomePermissao+"' do perfil '"+nomePerfil+"' (PerfisPermissoes.id = "+perfPerm.getId()+")");
				}

				protected void manageCriteria(DetachedCriteria criteria) {
					criteria.add(Restrictions.in("perfil.id", listaPerfis));
				}
			}.clean();
		}
		
		if (!listaPermissoesModulo.isEmpty()) {
			new Cleaner<PermissaoModulo>(listaPermissoesModulo) {
				protected void preDelete(PermissaoModulo permissaoModulo) {
					String nomePermissao = permissaoModulo.getPermissao().getNome();
					String nomeModulo = permissaoModulo.getModulo().getNome();
					
					getLogger().info("Removendo permissao '"+nomePermissao+"' do modulo '" +nomeModulo+ "' (PermissoesComponentesModulo.id = "+permissaoModulo.getId()+")");
				}
			}.clean();
		}
		
		if (!listaPermissoesComponentes.isEmpty()) {
			new Cleaner<PermissoesComponentes>(listaPermissoesComponentes) {
				protected void preDelete(PermissoesComponentes permComp) {
					String nomePermissao = permComp.getPermissao().getNome();
					String nomeMetodo = permComp.getMetodo().getNome();
					String nomeComponente = permComp.getComponente().getNome();
	
					getLogger().info("Removendo target '"+nomeComponente+"' e action '"+nomeMetodo+"' da permissao '"+nomePermissao+"' (PermissoesComponentes.id = "+permComp.getId()+")");
				}
			}.clean();
		}
		
		if (!listaPermissoes.isEmpty()) {
			new Cleaner<Permissao>(listaPermissoes) {
				protected void preDelete(Permissao permissao) {
					getLogger().info("Removendo permissão '"+permissao.getNome()+"' (id='"+permissao.getId()+"')");
				}
			}.clean();
		}
		
		if (!listaMetodos.isEmpty()) {
			new Cleaner<Metodo>(listaMetodos) {
				protected void preDelete(Metodo metodo) {
					getLogger().info("Removendo action '"+metodo.getNome()+"'(id='"+metodo.getId()+"')");
				}
			}.clean();
		}

		if (!listaAplicacoes.isEmpty()) {
			new Cleaner<Aplicacao>(listaAplicacoes) {
				protected void preDelete(Aplicacao aplicacao) {
					getLogger().info("Removendo aplicação '"+aplicacao.getNome()+"'(id='"+aplicacao.getId()+"')");
				}
			}.clean();
		}

		if (!listaComponentes.isEmpty()) {
			new Cleaner<Componente>(listaComponentes) {
				protected void preDelete(Componente componente) {
					getLogger().info("Removendo target '"+componente.getNome()+"'(id='"+componente.getId()+"')");
				}
			}.clean();
		}
		
	}
	
	protected void cleanAll() throws Exception {
		getLogger().info("Limpando todos os registros...");
		getLogger().info("OBS: Usuários e perfis existentes no banco de dados serão mantidos.");
		
		if (!listaUsuarios.isEmpty()) {
			new Cleaner<PerfisUsuarios>(listaUsuariosPerfis) {
				protected void preDelete(PerfisUsuarios perfUser) {
					String nomePerfil = perfUser.getPerfil().getNome();
					String nomeUsuario = perfUser.getUsuario().getNome();

					getLogger().info("Removendo o perfil '"+nomePerfil+"' do usuario '"+nomeUsuario+"' (PerfisUsuarios.id = "+perfUser.getId()+")");
				}

				protected void manageCriteria(DetachedCriteria criteria) {
					criteria.add(Restrictions.in("usuario.id", listaUsuarios));
				}
			}.clean();
		}		
	}

}
