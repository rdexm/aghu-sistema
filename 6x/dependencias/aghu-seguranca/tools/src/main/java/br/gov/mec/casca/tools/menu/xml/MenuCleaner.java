package br.gov.mec.casca.tools.menu.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.gov.mec.casca.model.Favorito;
import br.gov.mec.casca.model.Menu;
import br.gov.mec.casca.tools.updater.Cleaner;

class MenuCleaner {

	private Set<Integer> listaMenus;
	
	private Map<String, Set<String>> cacheFavoritosPorUsuario = new HashMap<String, Set<String>>();
	
	private static Logger log = Logger.getLogger(MenuCleaner.class);

	MenuCleaner() {
		listaMenus = new HashSet<Integer>();
	}
	
	Boolean contains(Integer id) {
		return listaMenus.contains(id);
	}	
	
	void addMenu(Integer id) {
		listaMenus.add(id);
	}
	
	protected void cleanup() throws Exception {
		log.info("Limpando registros não mapeados...");
		
		if (!listaMenus.isEmpty()) {
			new Cleaner<Menu>(listaMenus) {
				protected void preDelete(Menu menu) {
					String nomeMenu = menu.getNome();
					String nomeMenuPai = menu.getMenuPai() == null ? null : menu.getMenuPai().getNome();
					
					// O trecho abaixo guarda os menus que foram removidos
					// para possibilitar restauração dos Favoritos do usuário
					if (menu.getUrl() != null && !menu.getUrl().isEmpty()) {
						Set<String> conjuntoURLs = null;
						for (Favorito favoritoPorExcluir : menu.getFavoritos()) {
							favoritoPorExcluir.getUsuario();
							
							if (cacheFavoritosPorUsuario.containsKey(favoritoPorExcluir.getUsuario().getLogin())) {
								cacheFavoritosPorUsuario.get(favoritoPorExcluir.getUsuario().getLogin()).add(menu.getUrl());
							} else {
								conjuntoURLs = new HashSet<String>();
								conjuntoURLs.add(menu.getUrl());
								cacheFavoritosPorUsuario.put(favoritoPorExcluir.getUsuario().getLogin(), conjuntoURLs);
							}
						}
					}

					log.info(String.format("Removendo o menu '%s' do menu pai '%s' (Menu.id = %s)", nomeMenu, nomeMenuPai, menu.getId()));
				}
			}.clean();
		}
	}

	public Map<String, Set<String>> getCacheFavoritosPorUsuario() {
		return cacheFavoritosPorUsuario;
	}	
}
