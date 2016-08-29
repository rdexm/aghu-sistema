package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipBairrosCepLogradouroId;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCepLogradourosId;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.model.AipTituloLogradouros;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe controladora na tela de cadastro/edição de logradouro.
 * 
 * @author Marcelo Tocchetto
 */

public class LogradouroController extends ActionController {

	private static final long serialVersionUID = 2391697780191673431L;
	private static final String OUTCOME_JSF_RULE_SALVAR = "salvar";
	private static final String OUTCOME_JSF_RULE_CANCELAR = "cancelar";
	private static final String REDIRECT_PESQUISAR_LOGRADOURO = "logradouroList";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@Inject
	private LogradouroPaginatorController logradouroPaginatorController;

	private AipLogradouros aipLogradouro = new AipLogradouros();

	private AipCepLogradouros aipCepLogradouro = new AipCepLogradouros();
	
	private AipCepLogradouros aipCLSelecionado = new AipCepLogradouros();
	
	private Set<AipCepLogradouros> aipCepLogradouros = new HashSet<AipCepLogradouros>();

	private AipBairros aipBairro;

	private boolean exibirTabelaResultados = false;

	private Integer codBairro;
	
	
	// busca pela pk
	private Integer codigo;
	
	// search lista de valores
	private String descricao;
	
	private String origemModal;
	private boolean exibirModalAlteracoesPendentes;
	
	//Armazena os bairros existentes ao iniciar a edição dos bairros de um CEP
	private List<AipBairrosCepLogradouro> bairroCepLogradourosOriginais;
	
	public void inicio(){
		Integer codigo = aipLogradouro.getCodigo();
		if(codigo != null){
			aipLogradouro = this.cadastrosBasicosPacienteFacade.obterLogradouroPorCodigo(codigo);
	
			// Carrega os bairros associados ao logradouro.
			aipCepLogradouros = aipLogradouro.getAipCepLogradouros();
		}
		novoAipCepLogradouro();
		exibirTabelaResultados = false;
	}

	public String salvar() {
		origemModal = OUTCOME_JSF_RULE_SALVAR;
		boolean todosCepLogAdicionados = false;
		for (AipCepLogradouros cepLogradouro : aipCepLogradouros) {
			if (NumberUtil.equals(cepLogradouro.getId().getCep(), aipCepLogradouro.getId().getCep())
					&& cepLogradouro.getNroInicial().equalsIgnoreCase(aipCepLogradouro
							.getNroInicial())
					&& cepLogradouro.getNroFinal().equalsIgnoreCase(aipCepLogradouro
							.getNroFinal())) {
				todosCepLogAdicionados = true;
			}
		}
		if (todosCepLogAdicionados
				|| (aipCepLogradouro.getId() != null && (aipCepLogradouro
						.getId().getCep() != null
						|| aipCepLogradouro.getNroInicial() != null || aipCepLogradouro
						.getNroInicial() != null))) {
			exibirModalAlteracoesPendentes = true;
			return null;
		}
		return salvarLogradouros();
	}

	public String confirmarModal() {
		if (origemModal.equals(OUTCOME_JSF_RULE_CANCELAR)) {
			return REDIRECT_PESQUISAR_LOGRADOURO;
		} else {
			exibirModalAlteracoesPendentes = false;
			return salvarLogradouros();
		}
	}

	public void cancelarModal() {
		exibirModalAlteracoesPendentes = false;
	}

	public String salvarLogradouros() {
		try {
			if (aipBairro != null) {
				this.apresentarMsgNegocio(Severity.ERROR,
						"MENSAGEM_ADICIONAR_BAIRRO");
				return null;
			}

			for (AipCepLogradouros aipCL : aipCepLogradouros) {
				if (aipCL.getBairroCepLogradouros().isEmpty()) {
					this.apresentarMsgNegocio(
							Severity.ERROR, "MENSAGEM_BAIRRO_OBRIGATORIO");
					return null;
				}
			}
			
			final String mensagemSucesso = aipLogradouro.getCodigo() == null ? "MENSAGEM_SUCESSO_PERSISTIR_LOGRADOURO"
					: "MENSAGEM_SUCESSO_ALTERAR_LOGRADOURO";
			
			aipLogradouro.setAipCepLogradouros(new HashSet<AipCepLogradouros>(aipCepLogradouros));
			cadastrosBasicosPacienteFacade.persistirLogradouro(aipLogradouro);

			this.apresentarMsgNegocio(Severity.INFO, mensagemSucesso, aipLogradouro.getNome());
			
			clear(); 
			this.logradouroPaginatorController.reiniciarPaginator();

			return REDIRECT_PESQUISAR_LOGRADOURO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String cancelar() {
		//A solução adotada (provavelmente temporária), foi de fazer um refresh no objeto em questão, ao cancelar a edição/inclusão
		//pois estava ocorrendo um erro ao icluir um novo logradouro, após cancelar a edição de um outro. Issue 1449 - redmine.
		cadastrosBasicosPacienteFacade.cancelaEdicaoOuInclusaoLogradouro(aipLogradouro);
		clear();
		this.logradouroPaginatorController.reiniciarPaginator();
		return REDIRECT_PESQUISAR_LOGRADOURO;
	}

	public void adicionarCepLogradouro() {
		if (aipCepLogradouro.getId().getCep() != null) {
			aipCepLogradouro.getId().setLgrCodigo(
					aipLogradouro.getCodigo() == null ? 0 : aipLogradouro
							.getCodigo());
			if (aipCepLogradouro.getId() != null
					&& aipCepLogradouro.getId().getCep() != 0) {
				if (aipCepLogradouros.contains(aipCepLogradouro)) {
					for (AipCepLogradouros aipCL : aipCepLogradouros) {
						if (aipCL.equals(aipCepLogradouro)) {
							aipCL.setNroInicial(aipCepLogradouro
									.getNroInicial());
							aipCL.setNroFinal(aipCepLogradouro.getNroFinal());
							aipCL.setLado(aipCepLogradouro.getLado());
							break;
						}
					}
				} else {
					aipCepLogradouro.setLogradouro(aipLogradouro);
					aipCepLogradouros.add(aipCepLogradouro);
				}
			}
			novoAipCepLogradouro();
		} else {
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_CEP_OBRIGATORIO");
		}
	}
	
	public void removerCepLogradouro() {
		aipCepLogradouros.remove(aipCLSelecionado);
		aipCLSelecionado = null;
		novoAipCepLogradouro();
	}

	public void novoAipCepLogradouro() {
		AipCepLogradourosId id = new AipCepLogradourosId();
		this.aipCepLogradouro = new AipCepLogradouros();
		this.aipCepLogradouro.setId(id);
		this.aipCepLogradouro.setLogradouro(aipLogradouro);
	}

	public void adicionarBairro() {
		Set<AipBairrosCepLogradouro> bairroCepLogradouros = aipCLSelecionado
				.getBairroCepLogradouros();

		if (aipBairro != null && aipBairro.getCodigo() != null) {
			if (!hasAipBairro(bairroCepLogradouros, aipBairro)) {
				final Integer lgrCodigo = aipCLSelecionado.getLogradouro()
						.getCodigo();
				AipBairrosCepLogradouroId aipBCLId = new AipBairrosCepLogradouroId();
				aipBCLId.setCloCep(aipCLSelecionado.getId().getCep());
				aipBCLId.setBaiCodigo(aipBairro.getCodigo());
				aipBCLId.setCloLgrCodigo(lgrCodigo == null ? 0 : lgrCodigo);

				AipBairrosCepLogradouro aipBCL = new AipBairrosCepLogradouro(
						aipBCLId, aipBairro);
				
				//Verificação se o bairro adicionado já não existe no objeto de banco (trata problema com hibernate e objetos "duplicados")
				if(bairroCepLogradourosOriginais != null && !bairroCepLogradourosOriginais.isEmpty()) {
					int pos = bairroCepLogradourosOriginais.indexOf(aipBCL);
					if(pos != -1) {
						bairroCepLogradouros.add(bairroCepLogradourosOriginais.get(pos));
					} else {
						bairroCepLogradouros.add(aipBCL);
					}
				} else {
					bairroCepLogradouros.add(aipBCL);
				}
			}

			aipBairro = null;
			exibirTabelaResultados = true;
		}
	}
	
	public void removerBairro() {
		Set<AipBairrosCepLogradouro> bairroCepLogradouros = aipCLSelecionado
				.getBairroCepLogradouros();
		List<AipBairrosCepLogradouro> bairrosCepRemove = new ArrayList<AipBairrosCepLogradouro>(
				1);
		for (AipBairrosCepLogradouro cep : bairroCepLogradouros) {
			if (cep.getAipBairro().getCodigo().equals(codBairro)) {
				bairrosCepRemove.add(cep);
				break;
			}
		}

		if (bairroCepLogradouros.removeAll(bairrosCepRemove)) {
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_EXCLUSAO_BAIRRO");
		}
		exibirTabelaResultados = !bairroCepLogradouros.isEmpty();
	}
	
	public List<AipCepLogradouros> listarAipCepLogradouros(){
		return new ArrayList<AipCepLogradouros>(this.aipCepLogradouros);
	}
	
	public List<AipBairrosCepLogradouro> listarBairroCepLogradouros(){
		return new ArrayList<AipBairrosCepLogradouro>(aipCLSelecionado.getBairroCepLogradouros());
	}

	public List<AipTipoLogradouros> pesquisarTipoLogradouro(String descricao) {
		return cadastrosBasicosPacienteFacade.pesquisarTipoLogradouro(descricao);
	}

	public List<AipTituloLogradouros> pesquisarTituloLogradouro(String descricao) {
		return cadastrosBasicosPacienteFacade.pesquisarTituloLogradouro(descricao);
	}

	public List<AipCidades> pesquisarCidade(String descricao) {
		return cadastrosBasicosPacienteFacade.pesquisarCidadesParaLogradouro(descricao);
	}

	public List<AipBairros> pesquisarBairro(String descricao) {
		return cadastrosBasicosPacienteFacade.pesquisarBairro(descricao);
	}

	public boolean hasAipBairro(
			Set<AipBairrosCepLogradouro> bairroCepLogradouros,
			AipBairros aipBairro) {
		boolean result = false;
		for (AipBairrosCepLogradouro cep : bairroCepLogradouros) {
			if (cep.getAipBairro().getCodigo().equals(aipBairro.getCodigo())) {
				result = true;
				break;
			}
		}

		return result;
	}

	private void clear() {
		aipLogradouro = new AipLogradouros();
		aipLogradouro.setAipCidade(new AipCidades());

		aipBairro = null;
		aipCepLogradouros = new HashSet<AipCepLogradouros>();
		novoAipCepLogradouro();
		setAipCLSelecionado(new AipCepLogradouros());

		exibirTabelaResultados = false;
	}

	public void editarBairros(AipCepLogradouros aipCepLogradouro) {
		aipBairro = null;
		aipCLSelecionado = aipCepLogradouro;
		bairroCepLogradourosOriginais = new ArrayList<AipBairrosCepLogradouro>(aipCLSelecionado.getBairroCepLogradouros());
		exibirTabelaResultados = !aipCLSelecionado.getBairroCepLogradouros().isEmpty();
	}

	public void limparCidade() {
		aipLogradouro.setAipCidade(new AipCidades());
	}

	public boolean isMostrarLinkExcluirCidade() {
		return aipLogradouro != null && this.aipLogradouro.getCodigo() == null
				&& this.aipLogradouro.getAipCidade().getCodigo() != null;
	}

	public void limparTituloLogradouro() {
		this.aipLogradouro.setAipTituloLogradouro(null);
	}

	public boolean isMostrarLinkExcluirTituloLogradouro() {
		return this.aipLogradouro != null
				&& this.aipLogradouro.getAipTituloLogradouro() != null
				&& this.aipLogradouro.getAipTituloLogradouro().getCodigo() != null;
	}

	public boolean isMostrarLinkExcluirBairro() {
		return this.aipBairro != null && this.aipBairro.getCodigo() != null;
	}

	public void limparTipoLogradouro() {
		this.aipLogradouro.setAipTipoLogradouro(null);
	}

	public void limparBairro() {
		this.aipBairro = null;
	}

	public boolean isMostrarLinkExcluirTipoLogradouro() {
		return this.aipLogradouro != null
				&& this.aipLogradouro.getAipTipoLogradouro() != null
				&& this.aipLogradouro.getAipTipoLogradouro().getCodigo() != null;
	}

	// GET's e SET's

	public void setAipLogradouro(AipLogradouros aipLogradouro) {
		this.aipLogradouro = aipLogradouro;
	}

	public AipLogradouros getAipLogradouro() {
		return aipLogradouro;
	}

	public AipBairros getAipBairro() {
		return aipBairro;
	}

	public void setAipBairro(AipBairros aipBairro) {
		this.aipBairro = aipBairro;
	}

	public boolean isExibirTabelaResultados() {
		return exibirTabelaResultados;
	}

	public void setExibirTabelaResultados(boolean exibirTabelaResultados) {
		this.exibirTabelaResultados = exibirTabelaResultados;
	}

	public AipCepLogradouros getAipCepLogradouro() {
		return aipCepLogradouro;
	}

	public void setAipCepLogradouro(AipCepLogradouros aipCepLogradouro) {
		this.aipCepLogradouro = aipCepLogradouro;
	}

	public void setAipCLSelecionado(AipCepLogradouros aipCLSelecionado) {
		this.aipCLSelecionado = aipCLSelecionado;
	}

	public AipCepLogradouros getAipCLSelecionado() {
		return aipCLSelecionado;
	}

	public Set<AipCepLogradouros> getAipCepLogradouros() {
		return aipCepLogradouros;
	}

	public void setAipCepLogradouros(Set<AipCepLogradouros> aipCepLogradouros) {
		this.aipCepLogradouros = aipCepLogradouros;
	}

	public Integer getCodBairro() {
		return codBairro;
	}

	public void setCodBairro(Integer codBairro) {
		this.codBairro = codBairro;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public boolean exibirModalAlteracoesPendentes() {
		return exibirModalAlteracoesPendentes;
	}

	public void setExibirModalAlteracoesPendentes(
			boolean exibirModalAlteracoesPendentes) {
		this.exibirModalAlteracoesPendentes = exibirModalAlteracoesPendentes;
	}

}