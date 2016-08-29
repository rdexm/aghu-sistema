package br.gov.mec.casca.tools.security.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.casca.model.Modulo;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.PerfisPermissoes;
import br.gov.mec.casca.model.PerfisUsuarios;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.casca.model.PermissaoModulo;
import br.gov.mec.casca.model.PermissoesComponentes;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.casca.tools.updater.HibernateHelper;

public class XMLWriter {

	public final static String ARQUIVO_PRINCIPAL = "mapeamento-seguranca";
	public final static String ARQUIVO_PERFIS = "mapeamento-seguranca-perfis";
	public final static String ARQUIVO_PERMISSOES = "mapeamento-permissoes";
	
	public final static String SEPARADOR= "-";
	public final static String EXTENSAO = ".xml";
	
	private boolean simulacao = false;
	
	private String local;

	private String diretorio;
	
	private XMLWriter() {
	}
	
	public static void main(String[] args) throws Exception {
		
		XMLWriter xmlWriter = new XMLWriter();
		if(args != null && args.length > 0) {
			if(args[0] != null) {
				xmlWriter.setDiretorio(args[0]);
			}
			if(args.length >= 2 && args[1] != null) {
				boolean simulacao = Boolean.parseBoolean(args[1]);
				xmlWriter.setSimular(simulacao);
			}
			if(args.length >= 3 && !args[2].isEmpty()) {
				xmlWriter.setLocal(args[2]);
			}
			
			xmlWriter.execute();
		} else {
			System.err.println("Forma de utilização: XMLWriter <diretorio> [simular] [local]");
		}
	}
	
	private void execute() throws FileNotFoundException {

		Session session = HibernateHelper.getSingleton().getSession();

		System.out.printf("Usando dir %s%n", this.diretorio);
		System.out.printf("Modo de simulação: %s%n", this.simulacao ? "ligado" : "desligado");
		System.out.printf("Local usado: %s%n", this.local);
		
		geraComponentes(session);

		geraPermissoes(session);

		geraPerfisUsuarios(session);
		
		session.close();
	}

	private void geraComponentes(Session session) throws FileNotFoundException {
		
		PrintWriter writer = verificaGeracaoArquivo(ARQUIVO_PRINCIPAL);
		
		iniciaWriter(writer);

		// Aplicacao
		geraAplicacoes(session, writer);
		
		geraModulos(session, writer);
		
		geraTargets(session, writer);
		
		encerraWriter(writer);
	}

	@SuppressWarnings("unchecked")
	public void geraAplicacoes(Session session, PrintWriter writer) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Aplicacao.class);
		criteria.addOrder(Order.asc(Aplicacao.Fields.NOME.toString()));
		
		List<Aplicacao> aplicacoes = criteria.getExecutableCriteria(session).list();
		
		writer.printf("%4s<aplicacoes>%n", " ");
		
		for (Aplicacao aplicacao : aplicacoes) {
			writer.printf("%8s<aplicacao nome=\"%s\"", " ", aplicacao.getNome());
			
			if (StringUtils.isBlank(aplicacao.getServidor())) {
			//if (aplicacao.getServidor() != null || aplicacao.getServidor().isEmpty()) {
				writer.printf(" servidor=\"%s\"", aplicacao.getServidor());
			}
			
			if (aplicacao.getPorta() != null) {
				writer.printf(" porta=\"%s\"", aplicacao.getPorta());
			}
			
			// FIXME Protocolo é String! Corrigir na model
			if (aplicacao.getProtocolo() != null) {
				writer.printf(" protocolo=\"%s\"", aplicacao.getProtocolo());
			}
			
			if (StringUtils.isBlank(aplicacao.getContexto())) {
			//if (aplicacao.getContexto() != null || aplicacao.getContexto().isEmpty()) {
				writer.printf(" contexto=\"%s\"", aplicacao.getContexto());
			}
			
			if (StringUtils.isBlank(aplicacao.getDescricao())) {
			//if (aplicacao.getDescricao() != null || aplicacao.getDescricao().isEmpty()) {
				writer.printf(" descricao=\"%s\"", aplicacao.getDescricao());
			}
			
			if (aplicacao.getExterno() != null) {
				writer.printf(" externo=\"%s\"", aplicacao.getExterno());
			}
			
			writer.println("/>");
		}
		
		writer.printf("%4s</aplicacoes>%n", " ");
	}

	@SuppressWarnings("unchecked")
	private void geraModulos(Session session, PrintWriter writer) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Modulo.class);
		List<Modulo> modulos= criteria.getExecutableCriteria(session).list();
		
		if(modulos != null && !modulos.isEmpty()) {
			writer.printf("%4s<modulos>%n", " ");
			for(Modulo modulo : modulos) {
				writer.printf("%8s<modulo nome=\"%s\" descricao=\"%s\" aplicacao=\"%s\">%n", " ", modulo.getNome(), modulo.getDescricao(), modulo.getAplicacao().getNome());
			}
			writer.printf("%4s</modulos>%n", " ");
		}
	}

	@SuppressWarnings("unchecked")
	public void geraTargets(Session session, PrintWriter writer) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Componente.class);
		criteria.addOrder(Order.asc(Componente.Fields.NOME.toString()));
		List<Componente> targets = criteria.getExecutableCriteria(session).list();

		writer.printf("%4s<targets>%n", " ");
		for (Componente target : targets) {
			writer.printf("%8s<target nome=\"%s\" aplicacao=\"%s\">%n", " ", target.getNome(), target.getAplicacao().getNome());

			for (Metodo metodo : target.getMetodos()) {
				writer.printf(String.format("%12s<action nome=\"%s\" descricao=\"%s\"/>%n", " ", metodo.getNome(), metodo.getDescriao()));
			}
			
			writer.printf("%8s</target>%n", " ");
		}
		writer.printf("%4s</targets>%n", " ");
	}

	@SuppressWarnings("unchecked")
	private void geraPermissoes(Session session) throws FileNotFoundException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Modulo.class);
		List<Modulo> modulos = criteria.getExecutableCriteria(session).list();
		
		for(Modulo modulo : modulos) {

			String nomeArquivo = ARQUIVO_PERMISSOES + SEPARADOR + modulo.getNome().trim();
			PrintWriter writer = verificaGeracaoArquivo(nomeArquivo);

			iniciaWriter(writer);
			
			//buscar as permissoes do modulo
			criteria = DetachedCriteria.forClass(PermissaoModulo.class);
			criteria.add(Restrictions.eq(PermissaoModulo.Fields.MODULO_ID.toString(), modulo.getId()))
				.setProjection(Projections.distinct(Projections.property(PermissaoModulo.Fields.PERMISSAO.toString())));
			
			List<Permissao> permissoes = criteria.getExecutableCriteria(session).list();
			
			writer.printf("%4s<permissoes>%n", " ");
			for(Permissao permissao : permissoes) {
				writer.printf("%8s<permissao nome=\"%s\" descricao=\"%s\" modulo=\"%s\">%n" , " ", permissao.getNome(), permissao.getDescricao(), modulo.getNome());
				
				criteria = DetachedCriteria.forClass(PermissoesComponentes.class);
				criteria.add(Restrictions.eq(PermissoesComponentes.Fields.PERMISSAO_ID.toString(), permissao.getId()));
				criteria.createCriteria(PermissoesComponentes.Fields.COMPONENTE.toString()).addOrder(Order.asc(Componente.Fields.NOME.toString()));
				criteria.createCriteria(PermissoesComponentes.Fields.METODO.toString()).addOrder(Order.asc(Metodo.Fields.NOME.toString()));
				
				List<PermissoesComponentes> permissoesComponentes = criteria.getExecutableCriteria(session).list();
				for (PermissoesComponentes permissaoComponente : permissoesComponentes) {
					writer.printf(String.format("%12s<permissao_target target=\"%s\" action=\"%s\"/>%n", " ", permissaoComponente.getComponente().getNome(), permissaoComponente.getMetodo().getNome()));
				}
				writer.printf("%8s</permissao>%n", " ");
			}
			writer.printf("%4s</permissoes>%n", " ");
			encerraWriter(writer);
		}
	}

	/**
	 * @param geraArquivo
	 * @param session
	 * @param local 
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	private void geraPerfisUsuarios(Session session) throws FileNotFoundException {

		PrintWriter writer = verificaGeracaoArquivo(ARQUIVO_PERFIS);
		
		iniciaWriter(writer);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Perfil.class);
		List<Perfil> perfis = criteria.getExecutableCriteria(session).list();
		
		if(perfis != null && !perfis.isEmpty()) {
			
			writer.printf("%4s<perfis>%n", " ");
			for(Perfil perfil : perfis) {
				writer.printf("%8s<perfil nome=\"%s\" descricao=\"%s\">%n" , " ", perfil.getNome(), perfil.getDescricao());
				
				criteria = DetachedCriteria.forClass(PerfisPermissoes.class);
				criteria.add(Restrictions.eq(PerfisPermissoes.Fields.PERFIL_ID.toString(), perfil.getId()));
				criteria.setProjection(Projections.distinct(Projections.property(PerfisPermissoes.Fields.PERMISSAO.toString())));
				List<Permissao> permissoes = criteria.getExecutableCriteria(session).list();
				
				for(Permissao permissao : permissoes) {
					writer.printf(String.format("%12s<perfil_permissao permissao=\"%s\"/>%n", " ", permissao.getNome()));
				}
			}
			writer.printf("%4s</perfis>%n", " ");
		}
		
		criteria = DetachedCriteria.forClass(Usuario.class);
		List<Usuario> usuarios = criteria.getExecutableCriteria(session).list();
		if(usuarios != null && !usuarios.isEmpty()) {
			writer.printf("%4s<usuarios>%n", " ");
			
			for(Usuario usuario : usuarios) {
				writer.printf("%8s<usuario nome=\"%s\">%n" , " ", usuario.getNome());
				
				criteria = DetachedCriteria.forClass(PerfisUsuarios.class);
				criteria.add(Restrictions.eq(PerfisUsuarios.Fields.USUARIO_ID.toString(), usuario.getId()));
				criteria.setProjection(Projections.distinct(Projections.property(PerfisUsuarios.Fields.PERFIL.toString())));
				
				List<Perfil> perfisUsuario = criteria.getExecutableCriteria(session).list();
				for(Perfil perfil : perfisUsuario) {
					writer.printf(String.format("%12s<usuario_perfil perfil=\"%s\"/>%n", " ", perfil.getNome()));
				}
					
			}
			writer.printf("%4s</usuarios>%n", " ");
		}
		encerraWriter(writer);
	}

	/**
	 * @param geraArquivo
	 * @param local 
	 * @return
	 * @throws FileNotFoundException
	 */
	public PrintWriter verificaGeracaoArquivo(String nomeArquivo) throws FileNotFoundException {
		
		OutputStream out = System.out;

		if(!this.simulacao) {
			if(this.local != null && !this.local.isEmpty()) {
				nomeArquivo = nomeArquivo + SEPARADOR + this.local;
			}
			if(this.diretorio != null && !this.diretorio.isEmpty()) {
				nomeArquivo = this.diretorio + File.separator + nomeArquivo;
			}
			out = new java.io.FileOutputStream(nomeArquivo + EXTENSAO);
		}

		PrintWriter writer = new PrintWriter(out);
		return writer;
	}

	private void setSimular(boolean simulacao) {
		this.simulacao = simulacao;
	}

	private void setLocal(String local) {
		this.local = local;
	}
	
	private void setDiretorio(String diretorio) {
		this.diretorio = diretorio;
	}
	
	public void iniciaWriter(PrintWriter writer) {
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.println("<seguranca>");
	}

	public void encerraWriter(PrintWriter writer) {
		writer.print("</seguranca>");
		writer.flush();
		writer.close();
	}

}