package br.gov.mec.aghu.exames.action;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por pendências de assinatura digital em resultados de notas adicionais Estória #14256
 */
public class ConsultaResultadosNotaAdicionalGerarPendenciaAssinaturaController extends ActionReport {
	
	private static final Log LOG = LogFactory.getLog(ConsultaResultadosNotaAdicionalGerarPendenciaAssinaturaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -5617100837006532252L;

	private static final String PARAMETRO_CAMINHO_RELATORIO = "PARAMETRO_CAMINHO_RELATORIO";

	private static final String TIPO_DOCUMENTO = "TIPO_DOCUMENTO";

	private static final String BLOQUEAR_GERACAO_PENDENCIA = "BLOQUEAR_GERACAO_PENDENCIA";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private AghAtendimentos atendimento; // Atendimento utilizada no retorno da entidade pai
	private AelAtendimentoDiversos atendimentoDiverso; // Atendimento Diverso utilizada no retorno da entidade pai
	private String caminhoArquivo; // Caminho do arquivo de origem para geração de pendências na certificação digital

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private String nomeRelatorio;

	@Override
	public String recuperarArquivoRelatorio() {
		return this.caminhoArquivo;
	}

	@Override
	protected BaseBean getEntidadePai() {
		/*
		 * TODO: Posteriormente será definada a ENTIDADE PAI da assinatura digital. 
		 * Vide algumas alternativas: 
		 * - Para uma pesquisa de solicitação de exames por prontuário utilizar o próprio. 
		 * - Para uma pesquisa de solicitação de exames por número da solicitação utilizar a própria.
		 */
		BaseBean retorno = null;
		// Atualmente a entidade pai é baseada no ATENDIMENTO ou ATENDIMENTO DIVERSO da solicitação de exames.
		if (this.atendimento != null) {
			retorno = this.atendimento;
		} else if (this.atendimentoDiverso != null) {
			retorno = this.atendimentoDiverso;
		}
		return retorno;
	}

	/**
	 * Geração da pendência de assinatura digital
	 * @throws ApplicationBusinessException 
	 * 
	 * @throws MECBaseException
	 */
	public void gerarPendenciaAssinaturaDigital(ByteArrayOutputStream baos) throws ApplicationBusinessException  {
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
		if (this.certificacaoDigitalFacade.verificarServidorHabilitadoCertificacaoDigital(servidorLogado)) {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put(PARAMETRO_CAMINHO_RELATORIO, this.recuperarArquivoRelatorio());
			parametros.put(TIPO_DOCUMENTO, DominioTipoDocumento.LE); // Importante determinar o tipo de documento
			this.executarPosGeracaoRelatorio(parametros, baos);
		} else {
			LOG.warn("O usuário não está habilitado para receber pendencias de assinatura digital");
		}
	}

	/**
	 * Método que executa após a geração de um relatório. Na implementação padrão dos relatórios do AGHU, verifica e gera uma pendência de assinatura.
	 * @throws ApplicationBusinessException 
	 */
	protected void executarPosGeracaoRelatorio(Map<String, Object> parametros,
			ByteArrayOutputStream baos) throws ApplicationBusinessException {

		this.nomeRelatorio = (String) parametros.get(PARAMETRO_CAMINHO_RELATORIO);

		Boolean bloquearGeracaoPendencia = false;
		if (parametros.containsKey(BLOQUEAR_GERACAO_PENDENCIA)) {
			bloquearGeracaoPendencia = (Boolean) parametros.get(BLOQUEAR_GERACAO_PENDENCIA);
		}

		DominioTipoDocumento tipoDocumento = null;
		if (parametros.containsKey(TIPO_DOCUMENTO)) {
			tipoDocumento = (DominioTipoDocumento) parametros.get(TIPO_DOCUMENTO);
		}

		Boolean certificacaoDigital = false;
		String valorParametroCertificacaoDigital = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL).getVlrTexto();

		if (valorParametroCertificacaoDigital != null) {
			certificacaoDigital = DominioSimNao.valueOf(valorParametroCertificacaoDigital).isSim();
		}

		if (certificacaoDigital) {
			AghDocumentoCertificado documentoCertificado = this.verificarRelatorioNecessitaAssinatura(tipoDocumento);

			if (documentoCertificado != null && !bloquearGeracaoPendencia) {
//				entidadePai = this.getEntidadePai();
				if (tipoDocumento != null) {
					documentoCertificado.setTipo(tipoDocumento);
				}
				this.gerarPendenciaAssinatura(baos);
			} else if (documentoCertificado == null && bloquearGeracaoPendencia) {
				LOG.error("Erro ao gerar relatório: parâmetros inconsistentes. Não é possível bloquear a geração de pendencia para um relatório não cadastrado na tabela AGH_DOCUMENTOS_CERTIFICADOS");
				throw new IllegalArgumentException("O relatório " + nomeRelatorio
						+ " Não está registrado para geração de pendencia de assinatura, de forma que é impossível bloquear esta geração");
			}
		} else {
			LOG.info("Esse hospital não está habilitado para certificação digital, consequentemente a geração de pendências de assinatura não será executada");
		}
	}
	
	/**
	 * Método que identifica se a geração de um relatório implica na geração de
	 * uma pendencia de assinatura. A implementação padrão consulta a tabela
	 * Agh_Documento_Certificado e o parâmetro p_aghu_certificacao_digital.
	 * 
	 * @param nomeRelatorio
	 * @return
	 * @throws ApplicationBusinessException 
	 * @throws MECBaseException
	 */
	protected AghDocumentoCertificado verificarRelatorioNecessitaAssinatura(
			DominioTipoDocumento tipoDocumento) throws ApplicationBusinessException {
		LOG.info("verificarRelatorioNecessitaAssinatura");
		return this.certificacaoDigitalFacade
				.verificarRelatorioNecessitaAssinatura(nomeRelatorio,
						tipoDocumento);
	}

	/**
	 * Método responsável por gerar uma pendência de assinatura. A implementação padrão gera um pendência com o tipo de arquivo equivalente a tabela Agh_Documento_Certificado para
	 * o usuário logado.
	 * 
	 * 
	 * @throws MECBaseException
	 */
	protected void gerarPendenciaAssinatura(ByteArrayOutputStream baos)  {
		// TODO ver se geracao de pendencia foi migrada
//		byte[] byteArray = null;
//		try {
//			byteArray = baos.toByteArray();
//		} catch (Exception e) {
//			LOG.error("Erro na geração de pendencia de assinatura digital", e);
//			super.enviarEmailErroCriarMensagem(e.getMessage());
//			return;
//		}
//
//		RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
//		
//		MensagemPendenciaAssinaturaDTO mensagem = new MensagemPendenciaAssinaturaDTO();
//		mensagem.setArquivoGerado(byteArray);
//		mensagem.setEntidadePai(getEntidadePai());
//		mensagem.setDocumentoCertificado(getDocumentoCertificado());
//		mensagem.setServidorLogado(servidorLogado);
//
//		try {
//			pendenciaAssinaturaQueueSender.send(queueSession.createObjectMessage(mensagem));
//		} catch (Exception e) {
//			LOG.error(e);
//			super.enviarEmailErroCriarMensagem(e.getMessage());
//		}
	}

	/*
	 * Getters e setters
	 */

	public String getCaminhoArquivo() {
		return caminhoArquivo;
	}

	public void setCaminhoArquivo(String caminhoArquivo) {
		this.caminhoArquivo = caminhoArquivo;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}
	
	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}
	
	public AelAtendimentoDiversos getAtendimentoDiverso() {
		return atendimentoDiverso;
	}
	
	public void setAtendimentoDiverso(AelAtendimentoDiversos atendimentoDiverso) {
		this.atendimentoDiverso = atendimentoDiverso;
	}

	@Override
	protected Collection<?> recuperarColecao()
			throws ApplicationBusinessException {
		// TODO Auto-generated method stub
		return null;
	}

}
