package br.gov.mec.aghu.sicon.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioStatusEnvio;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAditContratoId;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.sicon.vo.SiconResponseVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class EnvioAditivoSiconController extends ActionController {

	private static final long serialVersionUID = -947542843151948517L;

	private static final String ANDAMENTO_LOADING_MODAL_BOX_WG = "andamentoLoadingModalBoxWG";
	
	private static final String PAGE_GERENCIAR_INTEGRACAO_SICON = "gerenciarIntegracaoSicon";

	@EJB
	private ISiconFacade siconFacade;

	@Inject
	private GerarRelatorioEnvioAditivoController gerarRelatorioEnvioAditivoController;


	//Parâmetros do Aditivo Selecionado
	private Integer seqContrato;
	private Integer seqAditivo;
	private String autenticacao;

	private ScoContrato contrato = new ScoContrato();
	private ScoAditContrato aditivoContrato = new ScoAditContrato();

	private boolean mostrarModalAndamento;
	private String mensagemBoxAndamento;

	private String retornoEnvioAditivo;
	private String detalhesEnvioContrato;
	private boolean mostrarDetalhesEnvio;

	private DadosEnvioVO dadosEnvioVO = null;
	private SiconResponseVO response = null;
	
	private static enum Acao {
		INICIAR, VALIDAR, GERAR_XML, ENVIAR, CONFIRMAR_ENVIO
	};

	private Acao acao = Acao.INICIAR;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	public void iniciar() {
		setRetornoEnvioAditivo(null);

		if (getSeqContrato() == null || getSeqAditivo() == null) {
			mostrarModalAndamento = false;
			apresentarMsgNegocio(Severity.ERROR,
					"MENSAGEM_ADITIVO_NAO_ENCONTRADO");

		} else {
			ScoAditContratoId idAditivo = new ScoAditContratoId(seqAditivo,
					seqContrato);
			this.aditivoContrato = siconFacade.obterAfContratoComContrato(idAditivo);
			this.contrato = aditivoContrato.getCont();
			this.openDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			if (aditivoContrato == null || contrato == null) {
				apresentarMsgNegocio(Severity.ERROR,
						"MENSAGEM_ADITIVO_NAO_ENCONTRADO");
				mostrarModalAndamento = false;
			} else {
				mostrarModalAndamento = true;
			}
		}	
	}

	public String executarEnvioAditivo() {

		switch (this.getAcao()) { // passos sequencias, cada um chama o proximo
		case INICIAR:
			setAcao(Acao.VALIDAR);
			return getBundle().getString("MENSAGEM_VALIDANDO_DADOS_ADITIVO");
		

		case VALIDAR:
			// chamar validacao, se ok passa para o proximo
			try {

				siconFacade.validarEnvioAditivo(this.aditivoContrato);
				setAcao(Acao.GERAR_XML);
				return getBundle().getString("MENSAGEM_GERANDO_ARQUIVO_XML");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				mostrarModalAndamento = false;
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
				retornoEnvioAditivo = tratarErroValidacao(e.getLocalizedMessage());
			}
			break;

		case GERAR_XML:
			try {

				dadosEnvioVO = siconFacade.gerarXmlAditivo(
						this.aditivoContrato, this.autenticacao);
				setAcao(Acao.ENVIAR);
				return getBundle().getString("MENSAGEM_INICIANDO_INTEGRACAO_SICON");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);								
				retornoEnvioAditivo = tratarErroValidacao(e.getLocalizedMessage());
				mostrarModalAndamento = false;
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			}
			break;

		case ENVIAR:
			try {
				response = siconFacade.enviarAditivo(dadosEnvioVO,aditivoContrato);
				setAcao(Acao.CONFIRMAR_ENVIO);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				retornoEnvioAditivo = tratarErroValidacao(e.getLocalizedMessage());
				mostrarModalAndamento = false;
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);				
			}
			break;

		case CONFIRMAR_ENVIO:
			if (response.getStatusEnvio().equals(DominioStatusEnvio.SUCESSO) ||
				response.getStatusEnvio().equals(DominioStatusEnvio.PENDENTE)) {				
				try {
					dadosEnvioVO.setLogEnvioSicon(response.getLogEnvioSicon());
					gerarRelatorioEnvioAditivoController.geraRelatorio(dadosEnvioVO, aditivoContrato);
				
				} catch (BaseException e) {
					apresentarExcecaoNegocio(e);
					retornoEnvioAditivo = tratarErroValidacao(e.getLocalizedMessage());
					mostrarModalAndamento = false;
					this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);					
				}
			}
			
		default:			
			if(response != null){
				retornoEnvioAditivo = tratarRetornoEnvio(response);
				mostrarModalAndamento = false;
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			}

			break;
		}
		
		return null;
	}
	
	private String tratarRetornoEnvio(SiconResponseVO response){
		
		StringBuilder retorno = new StringBuilder(46);
		
		String newLine = System.getProperty("line.separator");
		
		retorno.append(response.getStatusEnvio().getDescricao())
		
		.append(newLine)
		.append("Número Contrato: ")
		.append(contrato.getNrContrato())
		.append(newLine)
		.append("Seq Aditivo: ")
		.append(aditivoContrato.getId().getSeq());
		
		if(response.getStatusEnvio().equals(DominioStatusEnvio.ERRO)){
			retorno.append(newLine)
			.append("Classificação Erro: ")
			.append(response.getClassificaoErroXML().getDescricao())
			
			.append(newLine)
			.append("Código Erro: ")
			.append(response.getCodigoErro())
							
			.append(newLine)
			.append("Descrição Erro: ")
			.append(response.getDescricaoErro());
		}
		
		return retorno.toString();
	}
	
	private String tratarErroValidacao(String erro){
		
		StringBuilder retorno = new StringBuilder(37);
		
		String newLine = System.getProperty("line.separator");
		
		switch (this.getAcao()) { //passos sequencias, cada um chama o proximo
		
			case VALIDAR:
				retorno.append("ADITIVO NÃO ENVIADO !");
				break;
			
			case GERAR_XML:
				retorno.append("ERRO NA GERAÇÃO ARQUIVO !");
				break;
			
			case ENVIAR:
				retorno.append("ERRO NO ENVIO DO ARQUIVO !");
				break;
	
			default:
				retorno.append("ERRO INTEGRAÇÃO SICON");
				break;
		}
		
		retorno.append(newLine)
		.append("Descrição Erro: ")
		.append(getBundle().getString(erro));
		
		return retorno.toString();
	}
	
	public String voltar() {
		setAcao(Acao.INICIAR);
		return PAGE_GERENCIAR_INTEGRACAO_SICON;
	}

	public boolean isAcaoValidandoDados() {
		return Acao.VALIDAR.equals(acao);
	}

	public boolean isAcaoGerandoXML() {
		return Acao.GERAR_XML.equals(acao);
	}

	public boolean isAcaoEnviando() {
		return Acao.ENVIAR.equals(acao);
	}

	public boolean isMostrarModalAndamento() {
		return mostrarModalAndamento;
	}

	public void setMostrarModalAndamento(boolean mostrarModalAndamento) {
		this.mostrarModalAndamento = mostrarModalAndamento;
	}

	public Acao getAcao() {
		return acao;
	}

	public void setAcao(Acao acao) {
		this.acao = acao;
	}

	public String getMensagemBoxAndamento() {
		return mensagemBoxAndamento;
	}

	public void setMensagemBoxAndamento(String mensagemBoxAndamento) {
		this.mensagemBoxAndamento = mensagemBoxAndamento;
	}

	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}

	public String getRetornoEnvioAditivo() {
		return retornoEnvioAditivo;
	}

	public void setRetornoEnvioAditivo(String retornoEnvioAditivo) {
		this.retornoEnvioAditivo = retornoEnvioAditivo;
	}

	public String getDetalhesEnvioContrato() {
		return detalhesEnvioContrato;
	}

	public void setDetalhesEnvioContrato(String detalhesEnvioContrato) {
		this.detalhesEnvioContrato = detalhesEnvioContrato;
	}

	public boolean isMostrarDetalhesEnvio() {
		return mostrarDetalhesEnvio;
	}

	public void setMostrarDetalhesEnvio(boolean mostrarDetalhesEnvio) {
		this.mostrarDetalhesEnvio = mostrarDetalhesEnvio;
	}

	public Integer getSeqContrato() {
		return seqContrato;
	}

	public void setSeqContrato(Integer seqContrato) {
		this.seqContrato = seqContrato;
	}

	public Integer getSeqAditivo() {
		return seqAditivo;
	}

	public void setSeqAditivo(Integer seqAditivo) {
		this.seqAditivo = seqAditivo;
	}

	public String getAutenticacao() {
		return autenticacao;
	}

	public void setAutenticacao(String autenticacao) {
		this.autenticacao = autenticacao;
	}

	public ScoAditContrato getAditivoContrato() {
		return aditivoContrato;
	}

	public void setAditivoContrato(ScoAditContrato aditivoContrato) {
		this.aditivoContrato = aditivoContrato;
	}

}
