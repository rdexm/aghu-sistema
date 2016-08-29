package br.gov.mec.aghu.sicon.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioStatusEnvio;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.sicon.vo.SiconResponseVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class EnvioContratoSiasgSiconController extends ActionController {
	
	private static final long serialVersionUID = 6828045539513447027L;

	private static final String ANDAMENTO_LOADING_MODAL_BOX_WG = "andamentoLoadingModalBoxWG";
	
	private static final String ANDAMENTO_LOADING_MODAL_BOX_REENVIO_WG = "modalReenvioWG";
	
	private static final String PAGE_GERENCIAR_INTEGRACAO_SICON = "gerenciarIntegracaoSicon";

	@EJB
	private ISiconFacade siconFacade;

	@Inject
	private GerarRelatorioEnvioController gerarRelatorioEnvioController;

	private Integer contratoSeq;
	private String autenticacaoSicon;
	private boolean indAf;
	
	private ScoContrato contrato = new ScoContrato();
	
	private boolean confirmarReenvio = false;
	private boolean mostrarModalAndamento;
	private String mensagemBoxAndamento;
	
	private String retornoEnvioContrato;
	private String detalhesEnvioContrato;
	private boolean mostrarDetalhesEnvio;
	
	private SiconResponseVO response = null;
	private DadosEnvioVO dadosEnvioVO = null;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	private static enum Acao {
		INICIAR, VALIDAR, GERAR_XML, ENVIAR, CONFIRMAR_ENVIO
	};
	
	private Acao acao = Acao.INICIAR;
	
	public void iniciar() {
		setRetornoEnvioContrato(null);
		
		if (getContratoSeq() == null) {
			confirmarReenvio = false;
			mostrarModalAndamento = false;
			retornoEnvioContrato = tratarErroValidacao("MENSAGEM_CONTRATO_NAO_ENCONTRADO");  		
		} else {
			try {
				this.contrato = siconFacade.getContrato(getContratoSeq());
				
				//verifica se é um contrato a ser reenviado
				//caso afirmativo, renderiza popup de confirmação
				if (deveConfirmarReenvio()) {
					confirmarReenvio = true;
					mostrarModalAndamento = false;
					this.openDialog(ANDAMENTO_LOADING_MODAL_BOX_REENVIO_WG);
				} else {
					//se não, envia o contrato
					confirmarReenvio = false;
					mostrarModalAndamento = true;
					this.openDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);					
				}
			} catch (BaseException e) {
				confirmarReenvio = false;
				mostrarModalAndamento = false;
				retornoEnvioContrato = tratarErroValidacao(e.getLocalizedMessage());
				apresentarExcecaoNegocio(e);
			}
		}
		
		 acao = Acao.INICIAR;
		 retornoEnvioContrato = null;	
	}

	public String executarEnvioContrato(){

		switch (this.getAcao()) { //passos sequencias, cada um chama o proximo
		case INICIAR:
			setAcao(Acao.VALIDAR);
			return getBundle().getString("MENSAGEM_VALIDANDO_DADOS");
			
		case VALIDAR:
			//chamar validacao, se ok passa para o proximo
			try {
				siconFacade.validarEnvioContrato(contrato.getNrContrato());
				setAcao(Acao.GERAR_XML);				
				return getBundle().getString("MENSAGEM_GERANDO_ARQUIVO_XML");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);								
				retornoEnvioContrato = tratarErroValidacao(e.getLocalizedMessage());
				mostrarModalAndamento = false;
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			}
			break;
			
		case GERAR_XML:
			try {
				dadosEnvioVO = siconFacade.gerarXml(contrato.getSeq(), autenticacaoSicon);
				setAcao(Acao.ENVIAR);				
				return getBundle().getString("MENSAGEM_INICIANDO_INTEGRACAO_SICON");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				mostrarModalAndamento = false;				
				retornoEnvioContrato = tratarErroValidacao(e.getLocalizedMessage());
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			}
			break;
			
		case ENVIAR:
			try {
				int contSeq = contrato.getSeq();
				boolean usarAfReenvio = deveConfirmarReenvio();
				response = siconFacade.integrarSIASG(dadosEnvioVO, contSeq, usarAfReenvio);
				setAcao(Acao.CONFIRMAR_ENVIO);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				mostrarModalAndamento = false;
				retornoEnvioContrato = tratarErroValidacao(e.getLocalizedMessage());
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			} catch (OptimisticLockException e){
				mostrarModalAndamento = false;
				retornoEnvioContrato = tratarErroValidacao(e.getLocalizedMessage());
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			}
			break;
			
		case CONFIRMAR_ENVIO:
			if(response.getStatusEnvio().equals(DominioStatusEnvio.SUCESSO) ||
			   response.getStatusEnvio().equals(DominioStatusEnvio.PENDENTE)){				
				try{
					dadosEnvioVO.setLogEnvioSicon(response.getLogEnvioSicon());
					gerarRelatorioEnvioController.geraRelatorio(dadosEnvioVO);				
				} catch (BaseException e) {
					apresentarExcecaoNegocio(e);
					retornoEnvioContrato = tratarErroValidacao(e.getLocalizedMessage());
				} 
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			}
			
		default:
			if(response != null){
				retornoEnvioContrato = tratarRetornoEnvio(response);
				mostrarModalAndamento = false;
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			}
			
			break;
		}

		
		return null;
	}
	
	private String tratarRetornoEnvio(SiconResponseVO response){
		
		StringBuilder retorno = new StringBuilder(33);
		
		String newLine = System.getProperty("line.separator");
		
		retorno.append(response.getStatusEnvio().getDescricao())
		
		.append(newLine)
		.append("Número Contrato: ")
		.append(contrato.getNrContrato());
		
		
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
				retorno.append("CONTRATO NÃO ENVIADO !");
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
	
	
	private boolean deveConfirmarReenvio() {
		if (DominioOrigemContrato.A.equals(contrato.getIndOrigem()) &&
			DominioSituacaoEnvioContrato.E.equals(contrato.getSituacao()) &&
			isIndAf()) {
			return true;
		}
		return false;
	}
	
	public void executarReenvioContrato() {
		this.confirmarReenvio = false;
		this.mostrarModalAndamento = true;
		this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_REENVIO_WG);
		this.openDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
	}
	
	public String cancelarReenvio() {
		return PAGE_GERENCIAR_INTEGRACAO_SICON;
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
	
	public boolean isConfirmarReenvio() {
		return confirmarReenvio;
	}

	public void setConfirmarReenvio(boolean confirmarReenvio) {
		this.confirmarReenvio = confirmarReenvio;
	}

	public Integer getContratoSeq() {
		return contratoSeq;
	}

	public void setContratoSeq(Integer contratoSeq) {
		this.contratoSeq = contratoSeq;
	}

	public String getAutenticacaoSicon() {
		return autenticacaoSicon;
	}

	public void setAutenticacaoSicon(String autenticacaoSicon) {
		this.autenticacaoSicon = autenticacaoSicon;
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

	public String getRetornoEnvioContrato() {
		return retornoEnvioContrato;
	}

	public void setRetornoEnvioContrato(String retornoEnvioContrato) {
		this.retornoEnvioContrato = retornoEnvioContrato;
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

	public boolean isIndAf() {
		return indAf;
	}

	public void setIndAf(boolean indAf) {
		this.indAf = indAf;
	}

	public DadosEnvioVO getDadosEnvioVO() {
		return dadosEnvioVO;
	}

	public void setDadosEnvioVO(DadosEnvioVO dadosEnvioVO) {
		this.dadosEnvioVO = dadosEnvioVO;
	}

	public SiconResponseVO getResponse() {
		return response;
	}

	public void setResponse(SiconResponseVO response) {
		this.response = response;
	}

				
}
