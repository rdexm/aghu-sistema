package br.gov.mec.aghu.sicon.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioStatusEnvio;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.sicon.vo.SiconResponseVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class EnvioRescisaoSiconController extends ActionController {
	
	private static final long serialVersionUID = 3351695743836758421L;

	private static final String ANDAMENTO_LOADING_MODAL_BOX_WG = "andamentoLoadingModalBoxWG";
	
	private static final String PAGE_GERENCIAR_INTEGRACAO_SICON = "gerenciarIntegracaoSicon";

	@EJB
	private ISiconFacade siconFacade;


	@Inject
	private GerarRelatorioEnvioRescisaoController gerarRelatorioEnvioRescisaoController;

	private Integer contratoSeq;
	private String autenticacaoSicon;

	private ScoResContrato rescisao;
	private ScoContrato contrato;

	private String mensagemBoxAndamento;
	private boolean mostrarModalAndamento;

	private String retornoEnvioRescisao;

	private DadosEnvioVO dadosEnvioVO = null;
	private SiconResponseVO response = null;

	private static enum Acao {
		INICIAR, VALIDAR, GERAR_XML, ENVIAR, CONFIRMAR_ENVIO
	};

	private Acao acao = Acao.INICIAR;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		setRetornoEnvioRescisao(null);

		if (getContratoSeq() == null) {
			mostrarModalAndamento = false;
			retornoEnvioRescisao = tratarErroValidacao(getBundle().getString("MSG_RESCISAO_NAO_ENCONTRADA"));

		} else {
			try {
				contrato = siconFacade.getContrato(getContratoSeq());
				rescisao = contrato.getRescicao();
				setMostrarModalAndamento(true);
				this.openDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			} catch (BaseException e) {
				setMostrarModalAndamento(false);
				apresentarExcecaoNegocio(e);
			}
		}
		
		 acao = Acao.INICIAR;
		 retornoEnvioRescisao = null;
	}

	public String executarEnvioRescisao() {

		switch (this.getAcao()) { // passos sequencias, cada um chama o proximo
		case INICIAR:
			setAcao(Acao.VALIDAR);
			return getBundle().getString("MENSAGEM_VALIDANDO_DADOS_RESCISAO");			
		case VALIDAR:
			try {
				siconFacade.validarEnvioRescisao(rescisao);
				setAcao(Acao.GERAR_XML);
				return getBundle().getString("MENSAGEM_GERANDO_ARQUIVO_XML");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				setMostrarModalAndamento(false);				
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
				retornoEnvioRescisao = tratarErroValidacao(getBundle().getString(e.getLocalizedMessage()));
			}
			break;
		case GERAR_XML:
			try {
				dadosEnvioVO = siconFacade.gerarXmlRescisaoContrato(contrato.getSeq(), autenticacaoSicon);
				setAcao(Acao.ENVIAR);
				return getBundle().getString("MENSAGEM_INICIANDO_INTEGRACAO_SICON");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				setMostrarModalAndamento(false);
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
				retornoEnvioRescisao = tratarErroValidacao(getBundle().getString(e.getLocalizedMessage()));
			}
			break;
		case ENVIAR:
			try {
				setResponse(siconFacade.integrarRescisaoSIASG(dadosEnvioVO, rescisao));
				setAcao(Acao.CONFIRMAR_ENVIO);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				setMostrarModalAndamento(false);
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
				retornoEnvioRescisao = tratarErroValidacao(getBundle().getString(e.getLocalizedMessage()));
			}
			break;
		case CONFIRMAR_ENVIO:
			if (response.getStatusEnvio().equals(DominioStatusEnvio.SUCESSO)
					|| response.getStatusEnvio().equals(DominioStatusEnvio.PENDENTE)) {
				try {
					dadosEnvioVO.setLogEnvioSicon(response.getLogEnvioSicon());
					gerarRelatorioEnvioRescisaoController.geraRelatorio(dadosEnvioVO);
				} catch (BaseException e) {
					apresentarExcecaoNegocio(e);
				}
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			}

		default:
			if (response != null) {
				retornoEnvioRescisao = tratarRetornoEnvio(response);
				mostrarModalAndamento = false;
				this.closeDialog(ANDAMENTO_LOADING_MODAL_BOX_WG);
			}

			break;
		}

		return null;
	}

	private String tratarRetornoEnvio(SiconResponseVO response) {

		StringBuilder retorno = new StringBuilder(47);

		String newLine = System.getProperty("line.separator");

		retorno.append(response.getStatusEnvio().getDescricao())

		.append(newLine)
		.append("Número Contrato: ")
		.append(contrato.getNrContrato())
		.append(newLine)
		.append("Seq Rescisão: ")
		.append(rescisao.getSeq());

		if (response.getStatusEnvio().equals(DominioStatusEnvio.ERRO)) {
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

	private String tratarErroValidacao(String erro) {

		StringBuilder retorno = new StringBuilder(37);

		String newLine = System.getProperty("line.separator");

		switch (this.getAcao()) { // passos sequencias, cada um chama o proximo

		case VALIDAR:
			retorno.append("RESCISÃO NÃO ENVIADA !");
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
		.append(erro);

		return retorno.toString();
	}

	public String voltar() {
		return PAGE_GERENCIAR_INTEGRACAO_SICON;
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

	public ScoResContrato getRescisao() {
		return rescisao;
	}

	public void setRescisao(ScoResContrato rescisao) {
		this.rescisao = rescisao;
	}

	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
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

	public boolean isMostrarModalAndamento() {
		return mostrarModalAndamento;
	}

	public void setMostrarModalAndamento(boolean mostrarModalAndamento) {
		this.mostrarModalAndamento = mostrarModalAndamento;
	}

	public String getRetornoEnvioRescisao() {
		return retornoEnvioRescisao;
	}

	public void setRetornoEnvioRescisao(String retornoEnvioRescisao) {
		this.retornoEnvioRescisao = retornoEnvioRescisao;
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