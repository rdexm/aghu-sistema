package br.gov.mec.aghu.controlepaciente.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ConfigurarListaPacientesEnfermagemController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ConfigurarListaPacientesEnfermagemController.class);
	
	private static final String LISTA_PACIENTES_INTERNADOS = "controlepaciente-listarPacientesInternados";

	private static final long serialVersionUID = 4063050187848965568L;

	@EJB
	private IControlePacienteFacade controlePacienteFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private RapServidores servidor;

	private List<AghUnidadesFuncionais> listaServUnFuncionais;
	private AghUnidadesFuncionais unidadeFuncional, unidadeFuncionalSelecionada;
	
	private static UnidadeFuncionalComparator unidadeComparator = new UnidadeFuncionalComparator();

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

	 

		setServidor(servidorLogadoFacade.obterServidorLogadoSemCache());

		// método para carregar a lista de unidades funcionais
		try {
			listaServUnFuncionais = controlePacienteFacade.buscarListaUnidadesFuncionais(servidor);
			Collections.sort(listaServUnFuncionais, unidadeComparator);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	
	}
	

	public void salvar() {
		try {
			// salva lista de un funcionais
			controlePacienteFacade.salvarListaUnidadesFuncionaisEnfermagem(getListaServUnFuncionais(), getServidor());
			limpar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_LISTA_PACIENTES_CONFIGURADA_SUCESSO");
	}

	/**
	 * Retorna os valores da tela ao conteúdo do banco de dados
	 */
	public void limpar() {
		iniciar();
		setUnidadeFuncional(null);
	}

	/**
	 * Método da suggestion box para pesquisa de unidades funcionais a incluir
	 * na lista Exclui da listagem os itens que já estão na tela Ignora a
	 * pesquisa caso o parametro seja o próprio valor selecionado anteriormente
	 * (contorna falha de pesquisa múltipla na suggestion box)
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String parametro) {
		String paramString = (String) parametro;
		LOG.debug("ConfigurarListaPacientesEnfermagemController.pesquisarUnidadesFuncionais(): parametro = ["+ parametro + "].");
		Set<AghUnidadesFuncionais> result = new HashSet<AghUnidadesFuncionais>();
		if ((unidadeFuncionalSelecionada == null)
				|| !(StringUtils.equalsIgnoreCase(paramString,
						String.valueOf(unidadeFuncionalSelecionada.getSeq())) || StringUtils
						.equalsIgnoreCase(paramString,unidadeFuncionalSelecionada.getLPADAndarAlaDescricao()))) {
			try {
				result = new HashSet<AghUnidadesFuncionais>(controlePacienteFacade.pesquisarListaUnidadesFuncionais(paramString));
				// result.removeAll(getListaServUnFuncionais());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(unidadeFuncionalSelecionada);
		}
		List<AghUnidadesFuncionais> resultReturn = new ArrayList<AghUnidadesFuncionais>(result);
		Collections.sort(resultReturn, unidadeComparator);
		return resultReturn;
	}

	/**
	 * Método que exclui uma unidade funcional da lista em memória Ignora nulos
	 */
	public void excluirUnidadeFuncional(AghUnidadesFuncionais unidade) {
		LOG.debug("ConfigurarListaPacientesEnfermagemController.excluirUnidadeFuncional(): Entrando.");
		if (unidade != null) {
			LOG.debug("ConfigurarListaPacientesEnfermagemController.excluirUnidadeFuncional(): UnidadeFuncional = ["+ (unidade != null ? unidade.getDescricao() : "") + "].");
			listaServUnFuncionais.remove(unidade);
		}
		LOG.debug("ConfigurarListaPacientesEnfermagemController.excluirUnidadeFuncional(): Saindo.");
	}

	/**
	 * Adiciona uma equipe na lista em memória Caso a especialidade já esteja na lista, ou seja nula, ignora
	 */
	public void adicionarUnidadeFuncional() {
		LOG.debug("ConfigurarListaPacientesEnfermagemController.adicionarUnidadeFuncional(): Entrando.");
		if (getUnidadeFuncionalSelecionada() != null) {
			if (!listaServUnFuncionais.contains(getUnidadeFuncionalSelecionada())) {
				LOG.debug("ConfigurarListaPacientesEnfermagemController.adicionarUnidadeFuncional(): UnidadeFuncional = ["+ (getUnidadeFuncionalSelecionada() != null ? getUnidadeFuncionalSelecionada().getDescricao() : "") + "].");
				listaServUnFuncionais.add(getUnidadeFuncionalSelecionada());
				Collections.sort(listaServUnFuncionais, unidadeComparator);
				setUnidadeFuncional(null);
				setUnidadeFuncionalSelecionada(null);
			} else {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONFIG_LISTA_UNIDADE_FUNCIONAL_JA_ADICIONADA");
			}
		}
		LOG.debug("ConfigurarListaPacientesEnfermagemController.adicionarUnidadeFuncional(): Saindo.");
	}

	public void selecionouUnidadeFuncional() {
		setUnidadeFuncionalSelecionada(getUnidadeFuncional());
	}

	public String voltar() {
		limpar();
		return LISTA_PACIENTES_INTERNADOS;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setListaServUnFuncionais(List<AghUnidadesFuncionais> listaServUnFuncionais) {
		this.listaServUnFuncionais = listaServUnFuncionais;
	}

	public List<AghUnidadesFuncionais> getListaServUnFuncionais() {
		return listaServUnFuncionais;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncionalSelecionada(AghUnidadesFuncionais unidadeFuncionalSelecionada) {
		this.unidadeFuncionalSelecionada = unidadeFuncionalSelecionada;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalSelecionada() {
		return unidadeFuncionalSelecionada;
	} 

	private static class UnidadeFuncionalComparator implements Comparator<AghUnidadesFuncionais> {
		@Override
		public int compare(AghUnidadesFuncionais u1, AghUnidadesFuncionais u2) {
			int result = u1.getLPADAndarAlaDescricao().compareToIgnoreCase(u2.getLPADAndarAlaDescricao());
			return result;
		}
	}

}