package br.gov.mec.aghu.casca.aplicacao.action;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.casca.vo.ModuloVO;
import br.gov.mec.aghu.core.commons.Parametro;

@ApplicationScoped
@Named
public class IntegracaoModulosStartUp implements Serializable {

	private static final long serialVersionUID = -3846504634049528961L;
	
	@Inject @Parametro("modulos_ativos")
	private String modulosAtivosParametrizadosPorServidorAplicacao;
	
	private Set<String> modulosAtivos;
	
	private boolean modulosAtivosDefinidosPorBancoDeDados = true;
	
	private static final Log LOG = LogFactory.getLog(IntegracaoModulosStartUp.class);
	
	@EJB
	private ICascaFacade cascaFacade;

	@PostConstruct
	public void inicializarMapaModulosAtivos() {
		LOG.info("Carregando modulos ativos do sistema...");
		
		if (modulosAtivosParametrizadosPorServidorAplicacao != null && !modulosAtivosParametrizadosPorServidorAplicacao.isEmpty()) {
			modulosAtivos = new HashSet<String>();
			for (String moduloAtivo : modulosAtivosParametrizadosPorServidorAplicacao.split(",")) {
				modulosAtivos.add(moduloAtivo);
			}
		}
		
		if (modulosAtivos == null || modulosAtivos.isEmpty()) {
			modulosAtivosDefinidosPorBancoDeDados = true;
			modulosAtivos = new HashSet<String>();
		
			//chamada direta via dao p/ não passar pelas checagens de modulos ativos, já que esta informação ainda não está disponível.
			List<Modulo> cscModulosAtivos =  cascaFacade.listarModulosAtivos();
			
			for (Modulo modulo : cscModulosAtivos){
				modulosAtivos.add(modulo.getNome());
			}
		} else {
			modulosAtivosDefinidosPorBancoDeDados = false;
			LOG.warn("ATENÇÃO: módulos ativos carregados via arquivo components.properties e não via configuração de banco de dados");
			for (String nomeModuloReadOnly : ModuloVO.getNomeModulosReadOnly()) {
				if (modulosAtivos.contains(nomeModuloReadOnly)) {
					continue;
				}
				
				if (cascaFacade.listarModulosPorNome(nomeModuloReadOnly).size() > 0) {
					modulosAtivos.add(nomeModuloReadOnly);
				} else {
					LOG.warn("ATENÇÃO: Módulo %s não reconhecido pelo sistema." + nomeModuloReadOnly);
				}
			}
		}

		LOG.info("Modulos ativos carregados: " + modulosAtivos);
	}

	public Set<String> getModulosAtivos() {
		return modulosAtivos;
	}
	
	public void ativarModulo(String nomeModulo) {
		if (!modulosAtivos.contains(nomeModulo)) {
			modulosAtivos.add(nomeModulo);
		}
	}
	
	public void desativarModulo(String nomeModulo) {
		if (modulosAtivos.contains(nomeModulo)) {
			modulosAtivos.remove(nomeModulo);
		}
	}

	/**
	 * @return true caso a definição dos módulos ativos tenha sido
	 * carregado via banco de dados e false caso tenha sido carregado
	 * via configuração particular da instância do servidor de aplicação
	 */
	public boolean isModulosAtivosDefinidosPorBancoDeDados() {
		return modulosAtivosDefinidosPorBancoDeDados;
	}
}
