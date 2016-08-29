package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipDistritoSanitarios;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CidadeController extends ActionController{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4751711980297610396L;
	
	

	
	private static final String REDIRECT_CIDADE_LIST = "cidadeList";
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	private AipCidades aipCidades = new AipCidades();
	
	//Atributo para seleção da combo de Distritos Sanitários na tela de cadastro de cidades
	private AipDistritoSanitarios aipDistritoSanitario;
	
	//Atributo para armazenar a lista de Distrito Sanitário
	private List<AipDistritoSanitarios> distritoSanitarioList = new ArrayList<AipDistritoSanitarios>(); 
	
	private DominioSimNao cadastraLogradouro = DominioSimNao.N;
	
	/**
	 * Comparator usado entre as listas de Distritos Sanitários da cidade e da tela.
	 */
	private static final Comparator<AipDistritoSanitarios> COMPARATOR_DISTRITOS_SANITARIOS_DESCRICAO = new Comparator<AipDistritoSanitarios>() {
		@Override
		public int compare(AipDistritoSanitarios o1, AipDistritoSanitarios o2) {
			return o1.getDescricao().compareTo(o2.getDescricao());
		}
	};
	
	/**
	 * Método chamado ao carregar tela para identificar se a mesma está sendo chamada para edição ou
	 * inclusão de um registro. Caso seja para inclusão a mesma vem em branco, caso contrário os campos
	 * são carregados com os valores do objeto.
	 */
	public String iniciar() {
	 

		if (this.aipCidades == null || this.aipCidades.getCodigo() == null) {
			this.aipCidades = new AipCidades();
			this.aipCidades.setIndSituacao(DominioSituacao.A);
			this.aipCidades.setIndLogradouro(false);
			this.aipDistritoSanitario = null;
			this.distritoSanitarioList = new ArrayList<AipDistritoSanitarios>();
			
		} else {
			this.aipCidades = this.cadastrosBasicosPacienteFacade.obterCidadePorCodigo(aipCidades.getCodigo(), true);

			if(aipCidades == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			distritoSanitarioList = new ArrayList<AipDistritoSanitarios>(
					this.cadastrosBasicosPacienteFacade.pesquisarDistritoSanitarioPorCidadeCodigo(aipCidades.getCodigo()));
			
			Collections.sort(this.distritoSanitarioList, COMPARATOR_DISTRITOS_SANITARIOS_DESCRICAO);
		}
		
		return null;
	
	}
	
	/**
	 * Método chamado na tela para inserir/editar Cidade quando usuário clicar no botão Salvar.
	 */
	public String salvar() {
		try {

			boolean isInsert = aipCidades.getCodigo() != null;
			
			if(cadastraLogradouro != null) {
				this.aipCidades.setIndLogradouro(cadastraLogradouro.isSim());	
			} else {
				this.aipCidades.setIndLogradouro(false);
			}

			this.cadastrosBasicosPacienteFacade.persistirCidade(this.aipCidades, distritoSanitarioList);
			
			if (isInsert) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_CIDADE", this.aipCidades.getNome());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CIDADE", this.aipCidades.getNome());
			}
			
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Método chamado pelo usuário para cancelar a inserção/edição do registro e retornar a tela de pesquisa de cidades
	 */
	public String cancelar() {
		this.limpar();		
		return REDIRECT_CIDADE_LIST;
	}
	
	/**
	 * Método para limpar conteúdo das variáveis da tela.
	 */
	private void limpar() {
		this.aipCidades = new AipCidades();
		this.aipCidades.setIndSituacao(DominioSituacao.A);
		this.aipCidades.setIndLogradouro(false);
		this.aipDistritoSanitario = null;
		this.cadastraLogradouro = DominioSimNao.N;
		this.distritoSanitarioList = new ArrayList<AipDistritoSanitarios>();
	}

	
	/**
	 * Método que retorna uma coleção de UFs p/ preencher a suggestion box, de acordo com filtro informado.
	 */	
	public List<AipUfs> pesquisarUFs(String parametro) {
		return this.cadastrosBasicosPacienteFacade.pesquisarPorSiglaNomePermiteCidades(parametro);
	}
	
	public List<AipDistritoSanitarios> pesquisarPorCodigoDescricao(String param) {
		return this.cadastrosBasicosPacienteFacade.pesquisarDistritoSanitariosPorCodigoDescricao(param);
	}
	
	/**
	 * Método para excluir o Distrito Sanitário relacionado a cidade.
	 */
	public void excluirDistritoSanitarioDeCidade(Short distritoSanitarioCodigo) {
		
		for (AipDistritoSanitarios distritoSanitario : this.distritoSanitarioList) {
			if (distritoSanitario.getCodigo().equals(distritoSanitarioCodigo)) {
				this.distritoSanitarioList.remove(distritoSanitario);
				break;
			}
		}
	}
	
	public void adicionarDistritoSanitario() {
		if (this.aipDistritoSanitario != null && !this.distritoSanitarioList.contains(this.aipDistritoSanitario)) {
			this.distritoSanitarioList.add(this.aipDistritoSanitario);
			Collections.sort(this.distritoSanitarioList, COMPARATOR_DISTRITOS_SANITARIOS_DESCRICAO);
		}
		limparDistritoSanitario();
	}
	
	public void limparDistritoSanitario() {
		this.aipDistritoSanitario = null;
	}

	public boolean isMostrarLinkExcluirDistritoSanitario() {
		return this.aipDistritoSanitario != null && this.aipDistritoSanitario.getCodigo() != null;
	}
	
	
	//GETTERS e SETTERS
	public AipCidades getAipCidades() {
		return this.aipCidades;
	}
	
	public void setAipCidades(AipCidades aipCidades) {
		this.aipCidades = aipCidades;
	}

	public AipDistritoSanitarios getAipDistritoSanitario() {
		return this.aipDistritoSanitario;
	}

	public void setAipDistritoSanitario(AipDistritoSanitarios aipDistritoSanitario) {
		this.aipDistritoSanitario = aipDistritoSanitario;
	}

	public List<AipDistritoSanitarios> getDistritoSanitarioList() {
		return this.distritoSanitarioList;
	}

	public void setDistritoSanitarioList(
			List<AipDistritoSanitarios> distritoSanitarioList) {
		this.distritoSanitarioList = distritoSanitarioList;
	}

	public String getNomeUFSelecionada()	{
		if (this.aipCidades.getAipUf() == null || StringUtils.isBlank(this.aipCidades.getAipUf().getNome())){
			return "";
		}
		return this.aipCidades.getAipUf().getNome();
	}

	public DominioSimNao getCadastraLogradouro() {
		return cadastraLogradouro;
	}

	public void setCadastraLogradouro(DominioSimNao cadastraLogradouro) {
		this.cadastraLogradouro = cadastraLogradouro;
	}
	
	

}
