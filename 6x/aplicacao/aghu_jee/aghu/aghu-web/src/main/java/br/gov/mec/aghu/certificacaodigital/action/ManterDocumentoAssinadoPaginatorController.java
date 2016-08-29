package br.gov.mec.aghu.certificacaodigital.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioIdentificadorDocumentoAssinado;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ManterDocumentoAssinadoPaginatorController extends ActionController implements ActionPaginator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3026122438623445623L;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@Inject @Paginator
	private DynamicDataModel<AghDocumentoCertificado> dataModel;

	//Campos filtro
	private String nome;
	private DominioIdentificadorDocumentoAssinado identificador;
	private DominioTipoDocumento tipo;
	private DominioSituacao indSituacao;
	
	public DominioIdentificadorDocumentoAssinado[] getIdentificadorDocumentoAssinadoItens(){
		return DominioIdentificadorDocumentoAssinado.values();
	}
	
	public DominioTipoDocumento[] getTipoDocumentoItens(){
		return DominioTipoDocumento.values();
	}
	
	public DominioSituacao[] getAtivoInativoItens(){
		return DominioSituacao.values();
	}
	//Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public void iniciar(){
		if(dataModel.getPesquisaAtiva()){
			dataModel.reiniciarPaginator();
		}
	}
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.dataModel.limparPesquisa();

		//Limpa filtro
		nome = null;
		identificador = null;
		tipo = null;
		indSituacao = null;
		exibirBotaoNovo = false;
	}

	@Override
	public Long recuperarCount() {
		//cria objeto com os parâmetros para busca
		AghDocumentoCertificado elemento = new AghDocumentoCertificado(null,nome,null,identificador,tipo,indSituacao);
		
		return certificacaoDigitalFacade.pesquisarDocumentosCertificadosCount(elemento);
	}

	@Override
	public List<AghDocumentoCertificado> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		//cria objeto com os parâmetros de busca
		AghDocumentoCertificado elemento = new AghDocumentoCertificado(null,nome,null,identificador,tipo,indSituacao);
		
		return certificacaoDigitalFacade.pesquisarDocumentosCertificados(firstResult, maxResult, AghDocumentoCertificado.Fields.NOME.toString(), true, elemento);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioIdentificadorDocumentoAssinado getIdentificador() {
		return identificador;
	}

	public void setIdentificador(DominioIdentificadorDocumentoAssinado identificador) {
		this.identificador = identificador;
	}

	public DominioTipoDocumento getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoDocumento tipo) {
		this.tipo = tipo;
	}
	
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public DynamicDataModel<AghDocumentoCertificado> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghDocumentoCertificado> dataModel) {
		this.dataModel = dataModel;
	}

	
}
