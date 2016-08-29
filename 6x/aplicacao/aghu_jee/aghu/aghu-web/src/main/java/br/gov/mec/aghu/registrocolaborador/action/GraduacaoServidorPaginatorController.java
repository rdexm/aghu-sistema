package br.gov.mec.aghu.registrocolaborador.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class GraduacaoServidorPaginatorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = 3280594681192243335L;
	
	private static final String CADASTRAR_GRADUACAO_SERVIDOR = "cadastrarGraduacaoServidor";
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private String voltarPara;
	
	
	@Inject @Paginator
	private DynamicDataModel<RapQualificacao> dataModel;

	private RapServidores servidor;
	
	private RapQualificacao qualificacao;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	
	public void iniciar() {
	 

		if (voltarPara != null){
			servidor = registroColaboradorFacade.obterRapServidor(servidor.getId());
			this.pesquisar();
		}
	
	}
	
	public void pesquisar() {
		if (servidor.getId().getVinCodigo() == null || servidor.getId().getMatricula() == null) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_VINCULO_MATRICULA_OBRIGATORIOS");
			dataModel.limparPesquisa();
			return;
		}

		servidor = registroColaboradorFacade.obterServidor(servidor.getId().getVinCodigo(), servidor.getId().getMatricula());
		
		if (servidor == null) {
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_SERVIDOR_INEXISTENTE");
			dataModel.limparPesquisa();
			return;
		}
		
		dataModel.reiniciarPaginator();
	}
	
	@Override
	public List<RapQualificacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return registroColaboradorFacade.pesquisarGraduacao(null, servidor.getId().getMatricula(), servidor.getId().getVinCodigo(), 
														    firstResult, maxResult, RapQualificacao.Fields.DATA_INICIO.toString(), true);
	}

	@Override
	public Long recuperarCount() {
		return registroColaboradorFacade.pesquisarGraduacaoCount(null, servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		servidor = null;
	}
	
	public String inserir(){
		return CADASTRAR_GRADUACAO_SERVIDOR;
	} 
	
	public String editar(){
		return CADASTRAR_GRADUACAO_SERVIDOR;
	} 
	
	public void excluir() {
		try {
			if (qualificacao != null) {
				registroColaboradorFacade.remover(qualificacao);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_GRADUACAO_SERVIDOR", qualificacao.getId());
				
				dataModel.reiniciarPaginator();
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GRADUACAO_SERVIDOR_INEXISTENTE");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public List<RapServidores> pesquisarServidor(String param){
		return registroColaboradorFacade.pesquisarServidor(param);
	}

	public String voltar(){
		limpar();
		return voltarPara;
	}
	
	
	public DynamicDataModel<RapQualificacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapQualificacao> dataModel) {
		this.dataModel = dataModel;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapQualificacao getQualificacao() {
		return qualificacao;
	}

	public void setQualificacao(RapQualificacao qualificacao) {
		this.qualificacao = qualificacao;
	}


	public String getVoltarPara() {
		return voltarPara;
	}


	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}
