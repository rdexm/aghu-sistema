package br.gov.mec.aghu.certificacaodigital.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.model.AghCertificadoDigital;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class GerirCertificadoDigitalPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AghCertificadoDigital> dataModel;



	/**
	 * 
	 */
	private static final long serialVersionUID = -8646689217161170425L;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private RapServidores responsavel;
	private Integer vinCodigo;

	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;

	@Override
	public Long recuperarCount() {
		// cria objeto com os parâmetros para busca
		AghCertificadoDigital aghCertificadoDigital = new AghCertificadoDigital();
		aghCertificadoDigital.setServidorResp(this.getResponsavel());

		return this.certificacaoDigitalFacade
				.pesquisarCertificadoDigitalCount(aghCertificadoDigital);
	}

	@Override
	public List<AghCertificadoDigital> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		// cria objeto com os parâmetros para busca
		AghCertificadoDigital aghCertificadoDigital = new AghCertificadoDigital();
		aghCertificadoDigital.setServidorResp(this.getResponsavel());

		List<AghCertificadoDigital> list = this.certificacaoDigitalFacade
				.pesquisarCertificadoDigital(
						firstResult,
						maxResult,
						RapServidores.Fields.NOME_PESSOA_FISICA.toString(),
						true,
						aghCertificadoDigital);

		return list;
	}

	public void iniciar() {
	 

	 

		if (this.getResponsavel() != null) {
			if (recuperarCount() == 0) {
				setExibirBotaoNovo(true);
			} else {
				setExibirBotaoNovo(false);
			}
		}
	
	}
	

	public void pesquisar() {
		if (recuperarCount() == 0 && this.responsavel != null && 
				this.certificacaoDigitalFacade.verificarServidorPossuiPermissaoAssinaturaDigital(this.responsavel.getUsuario())) {
			setExibirBotaoNovo(true);
		} else {
			setExibirBotaoNovo(false);
		}
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Método da suggestion box para pesquisa todos os servidores cadastrados no
	 * sistema
	 * 
	 */
	public List<RapServidores> pesquisarServidores(String parametro)
			throws BaseException {
		return this.returnSGWithCount(this.registroColaboradorFacade.pesquisarServidor(parametro),
				this.pesquisarServidoresCount(parametro));
	}

	private Long pesquisarServidoresCount(String parametro) {
		return this.registroColaboradorFacade.pesquisarServidoresCount(parametro);
	}

	/**
	 * 	Metodo da suggestion executado após ser selecionado algum servidor, 
	 *  se ocorrer a Exception IndexOutOfBoundsException é porque o usuário já possui assinatura digital. 	
	 */
	public void selecionouServidor() {
		this.setVinCodigo(this.responsavel.getVinculo().getCodigo().intValue());
		if (this.responsavel != null) {
			Object nroMatricula = this.responsavel.getId().getMatricula().toString();
			if (this.certificacaoDigitalFacade.verificarServidorPossuiPermissaoAssinaturaDigital(this.responsavel.getUsuario())) {
				List<RapServidores> retorno = this.certificacaoDigitalFacade.pesquisarServidorComCertificacaoDigital(nroMatricula);
				if(retorno == null || retorno.size() == 0){
					setExibirBotaoNovo(true);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVIDOR_COM_PERMISSAO_ASSINATURA_DIGITAL");
				}else {
					boolean valida = false;
					for (RapServidores rapServidores : retorno) {
						if(rapServidores.getId().getVinCodigo().intValue() == this.responsavel.getId().getVinCodigo().intValue()){
							valida = true;
							break;
						}
					}
					if(valida){
						setExibirBotaoNovo(false);
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVIDOR_JA_GEROU_CERTIFICADO_DIGITAL");
					}else{
						setExibirBotaoNovo(true);
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVIDOR_COM_PERMISSAO_ASSINATURA_DIGITAL");
					}
				}
			} else {
				List<RapServidores> retorno = this.certificacaoDigitalFacade.pesquisarServidorComCertificacaoDigital(nroMatricula);
				if(retorno == null || retorno.size() == 0){
					setExibirBotaoNovo(false);
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVIDOR_SEM_PERMISSAO_ASSINATURA_DIGITAL");
				}else {
					boolean valida = false;
					for (RapServidores rapServidores : retorno) {
						if(rapServidores.getId().getVinCodigo().intValue() == this.responsavel.getId().getVinCodigo().intValue()){
							valida = true;
							break;
						}
					}
					if(valida){
						setExibirBotaoNovo(false);
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVIDOR_JA_GEROU_CERTIFICADO_DIGITAL");
					}else{
						setExibirBotaoNovo(false);
						apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVIDOR_SEM_PERMISSAO_ASSINATURA_DIGITAL");
					}
				}
			}
		}
		
	}

	public void limparDadosServidor() {
		this.setVinCodigo(null);
		setExibirBotaoNovo(false);
		// Apaga resultados da exibição
		this.dataModel.setPesquisaAtiva(false);
	}

	public String limparPesquisa() {
		this.setResponsavel(null);
		this.setVinCodigo(null);
		// Apaga resultados da exibição
		this.dataModel.setPesquisaAtiva(false);
		this.setExibirBotaoNovo(false);

		return null;
	}

	// getters e setters
	public RapServidores getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(RapServidores responsavel) {
		this.responsavel = responsavel;
	}

	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public boolean verificarSituacaoServidor(RapServidores rap) {
		boolean retorno = false;
		Date today = new Date();
		String situacao = "";
		if (rap.getIndSituacao() != null) {
			situacao = rap.getIndSituacao().toString();
		}
		if (rap.getIndSituacao().toString().equalsIgnoreCase("A")) {
			retorno = true;
		} else {
			if (situacao.equalsIgnoreCase("P") && rap.getDtFimVinculo() != null
					&& rap.getDtFimVinculo().after(today)) {
				retorno = true;
			}
		}
		return retorno;
	}
 


	public DynamicDataModel<AghCertificadoDigital> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghCertificadoDigital> dataModel) {
	 this.dataModel = dataModel;
	}
}
