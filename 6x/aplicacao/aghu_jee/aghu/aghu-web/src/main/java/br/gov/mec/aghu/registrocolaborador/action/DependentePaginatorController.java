package br.gov.mec.aghu.registrocolaborador.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class DependentePaginatorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = -2426172686394877969L;
	
	private static final String CADASTRAR_DEPENDENTE = "cadastrarDependente";
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject @Paginator
	private DynamicDataModel<RapDependentes> dataModel;

	// formulário pesquisa
	private Short   serVinCodigo;
	private RapServidores servidor;
	private boolean pesquisaAutomatica;
	private RapDependentes dependente;
	private RapPessoasFisicas pessoaSuggestion;
	
	private Integer parametroCodigoPessoa;
	private boolean servidorPermiteDependentes;

	private String voltarPara;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() throws ApplicationBusinessException{
	 

		if(voltarPara != null){
			if (this.parametroCodigoPessoa == null) {
				if(servidor != null){
					pesquisaAutomatica = true;
					servidor = registroColaboradorFacade.obterServidor(servidor.getId());
					pessoaSuggestion = servidor.getPessoaFisica();
					serVinCodigo = servidor.getId().getVinCodigo();
					pesquisar();
				}
			} else {
				pesquisaAutomatica = true;
				this.setPessoaSuggestion(registroColaboradorFacade.obterPessoaFisica(parametroCodigoPessoa));
				pesquisar();
			}
		}
	
	}
	
	/**
	 * Realiza a pesquisa.
	 */
	public void pesquisar() {
				
		if (servidor == null && this.pessoaSuggestion == null) {		
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_VINCULO_MATRICULA_PESSOA_OBRIGATORIOS");
			dataModel.limparPesquisa();
			return;
		}
		
		if(servidor != null){
			try {
				setServidorPermiteDependentes(servidor.getVinculo().getIndDependente().isSim());
				
			} catch (Exception e) {
				apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			}
		}
		dataModel.reiniciarPaginator();
	}
	
	public String inserir(){
		if (servidor == null && this.pessoaSuggestion == null) {		
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_VINCULO_MATRICULA_PESSOA_OBRIGATORIOS");
			dataModel.limparPesquisa();
			return null;
		}
		return CADASTRAR_DEPENDENTE;
	}
	
	public String editar(){
		return CADASTRAR_DEPENDENTE;
	}
	
	public String voltar(){
		return voltarPara;
	}
	
	@Override
	public Long recuperarCount()  {
		try {
			Integer pesCodigo = pessoaSuggestion != null ? pessoaSuggestion.getCodigo() : null;
			Integer serMatricula = servidor != null ? servidor.getId().getMatricula() : null;
			
			return registroColaboradorFacade.pesquisarDependenteCount(pesCodigo, null, serMatricula, serVinCodigo);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return 0L;
	}

	@Override
	public List<RapDependentes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
	    try {
			Integer pesCodigo = pessoaSuggestion != null ? pessoaSuggestion.getCodigo() : null;
			Integer serMatricula = servidor != null ? servidor.getId().getMatricula() : null;
			
	        return registroColaboradorFacade.pesquisarDependente(pesCodigo, null, serMatricula, serVinCodigo, firstResult, maxResult, orderProperty,asc );
	        
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	    
		return new ArrayList<RapDependentes>();
	}
	
	/**
	 * Exclui o objeto do banco de dados.
	 */
	public void excluir() {
		try {
			if (dependente != null) {
				registroColaboradorFacade.removerDependente(dependente.getId());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_DEPENDENTE", dependente.getId());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DEPENDENTE_INEXISTENTE");
			}

			dataModel.reiniciarPaginator();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void limpar() {
		servidor = null;
		pessoaSuggestion = null;
		pesquisaAutomatica = false;
		servidorPermiteDependentes = false;
		serVinCodigo = null;
		dataModel.limparPesquisa();
	}
	
	/**
	 * Busca servidor pelo vínculo e matrícula.
	 */
	public List<RapServidores> buscarServidor(String param) {
		return registroColaboradorFacade.pesquisarServidorVinculoAtivoEProgramadoAtual(param);
	}
	
	public void atualizaVinculo(){
		serVinCodigo = null;
		pessoaSuggestion = null;
		
		if(servidor != null){
			pessoaSuggestion = servidor.getPessoaFisica();
			serVinCodigo = servidor.getId().getVinCodigo();
		} else {
			dataModel.limparPesquisa();
		}
	}
	
	public List<RapPessoasFisicas> buscarPessoaFisica(String object) {
		return registroColaboradorFacade.pesquisarPessoaFisica(object);
	}

	public DynamicDataModel<RapDependentes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapDependentes> dataModel) {
		this.dataModel = dataModel;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public boolean isPesquisaAutomatica() {
		return pesquisaAutomatica;
	}

	public void setPesquisaAutomatica(boolean pesquisaAutomatica) {
		this.pesquisaAutomatica = pesquisaAutomatica;
	}

	public RapDependentes getDependente() {
		return dependente;
	}

	public void setDependente(RapDependentes dependente) {
		this.dependente = dependente;
	}

	public RapPessoasFisicas getPessoaSuggestion() {
		return pessoaSuggestion;
	}

	public void setPessoaSuggestion(RapPessoasFisicas pessoaSuggestion) {
		this.pessoaSuggestion = pessoaSuggestion;
	}

	public Integer getParametroCodigoPessoa() {
		return parametroCodigoPessoa;
	}

	public void setParametroCodigoPessoa(Integer parametroCodigoPessoa) {
		this.parametroCodigoPessoa = parametroCodigoPessoa;
	}

	public boolean isServidorPermiteDependentes() {
		return servidorPermiteDependentes;
	}

	public void setServidorPermiteDependentes(boolean servidorPermiteDependentes) {
		this.servidorPermiteDependentes = servidorPermiteDependentes;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
}