package br.gov.mec.aghu.registrocolaborador.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;



public class PessoaFisicaPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<RapPessoasFisicas> dataModel;

	private static final String PESQUISAR_DEPENDENTE = "pesquisarDependente";
	private static final String CADASTRAR_PESSOA_FISICA = "cadastrarPessoaFisica";
	private static final String PESQUISAR_SERVIDOR = "pesquisarServidor";
	
	private static final long serialVersionUID = 8658739937915669526L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private RapPessoasFisicas rapPessoaFisica = new RapPessoasFisicas();

	private Integer codigo;

	private String nome;

	private Long cpf;
	
	private Boolean exibirNovo;
	
	private boolean cadastrouNovaPessoa = false;
	
	private Integer codigoNovaPessoa;
	
	private Integer codigoPessoaFisica;

	@Override
	public Long recuperarCount() {

		return registroColaboradorFacade.pesquisarPessoaFisicaCount(codigo, nome, cpf);

	}

	@Override
	public List<RapPessoasFisicas> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		List<RapPessoasFisicas> listaPessoas = registroColaboradorFacade
				.pesquisarPessoaFisica(codigo, nome, cpf, firstResult, maxResult, 
						RapPessoasFisicas.Fields.NOME.toString(), asc);
		for (RapPessoasFisicas pf : listaPessoas) {
			pf.setNumeroDependentes(registroColaboradorFacade.obterNumeroDependentes(pf));
			try {
				pf.setPermiteDependentes(registroColaboradorFacade.existeVinculoDependentesPessoa(pf));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		return listaPessoas;
	}

	public void pesquisar() {

		// zerar todo resultset do paginator
		this.dataModel.reiniciarPaginator();
		
		// seta os valores para enviar ao paginator
		setCodigo(rapPessoaFisica.getCodigo());
		setNome(rapPessoaFisica.getNome());
		setCpf(rapPessoaFisica.getCpf());

		// Ativa o uso de paguinação
		this.dataModel.setPesquisaAtiva(true);
		exibirNovo = true;
	}
	
	public void carregarNovo() {
		this.rapPessoaFisica = new RapPessoasFisicas();
		setCodigo(codigoNovaPessoa);
		setNome(null);
		setCpf(null);
		// zerar todo resultset do paginator
		this.dataModel.reiniciarPaginator();
		
		this.dataModel.setPesquisaAtiva(true);
		exibirNovo = true;
		cadastrouNovaPessoa = false;
	}
	
	public String navegarServidor(Integer codigoPessoa) {
		setCodigoPessoaFisica(codigoPessoa);
		return PESQUISAR_SERVIDOR;
	}
	
	public String navegarDependente() {
		return PESQUISAR_DEPENDENTE;
	}
	
	public String editarPessoa(Integer codigoPessoa) {
		setCodigoPessoaFisica(codigoPessoa);
		return CADASTRAR_PESSOA_FISICA;
	}
	
	public String incluirPessoa() {
		return CADASTRAR_PESSOA_FISICA;
	}
	
	
	public void limpar(){
		this.dataModel.reiniciarPaginator();
		exibirNovo = false;
		this.dataModel.setPesquisaAtiva(false);
		this.rapPessoaFisica = new RapPessoasFisicas();
	}

	/**
	 * Método utilizado para verificar se é permitido a um determinado servidor
	 * adicionar dependentes. Utilizado para renderizar o ícone de dependentes.
	 * 
	 * @return {@code true}, para permitido e {@code false}, para caso
	 *         contrário.
	 */
	public Boolean permiteAdicionarDependente(RapPessoasFisicas _pessoa) {

		RapServidores servidor = registroColaboradorFacade.buscarServidorPesCodigo(_pessoa.getCodigo());
		
		if (servidor == null) {
			return false;
		}
		
		if (_pessoa.temDependentes() || servidor.getVinculo().getIndDependente() == DominioSimNao.S) {

			if (servidor.getIndSituacao() == DominioSituacaoVinculo.P) {
				if (servidor.getDtFimVinculo()
						.after(new Date())) {
					return true;
				}
				return false;
			} 
			if (servidor.getIndSituacao() == DominioSituacaoVinculo.I) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	public RapPessoasFisicas getRapPessoaFisica() {
		return rapPessoaFisica;
	}

	public void setRapPessoaFisica(RapPessoasFisicas rapPessoaFisica) {
		this.rapPessoaFisica = rapPessoaFisica;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Boolean getExibirNovo() {
		return exibirNovo;
	}

	public void setExibirNovo(Boolean exibirNovo) {
		this.exibirNovo = exibirNovo;
	}

	public void setCodigoNovaPessoa(Integer codigoNovaPessoa) {
		this.codigoNovaPessoa = codigoNovaPessoa;
	}

	public Integer getCodigoNovaPessoa() {
		return codigoNovaPessoa;
	}

	public void setCadastrouNovaPessoa(boolean cadastrouNovaPessoa) {
		this.cadastrouNovaPessoa = cadastrouNovaPessoa;
	}

	public boolean isCadastrouNovaPessoa() {
		return cadastrouNovaPessoa;
	}

	public void setCodigoPessoaFisica(Integer codigoPessoaFisica) {
		this.codigoPessoaFisica = codigoPessoaFisica;
	}

	public Integer getCodigoPessoaFisica() {
		return codigoPessoaFisica;
	}
	
	//@Out(value="nomePesquisaPessoaFisica", required=false)
	public String getNomePesquisaPaciente() {
		return rapPessoaFisica.getNome();
	}
 


	public DynamicDataModel<RapPessoasFisicas> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapPessoasFisicas> dataModel) {
	 this.dataModel = dataModel;
	}
}
