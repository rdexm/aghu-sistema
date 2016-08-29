package br.gov.mec.aghu.exames.business;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.dao.AelDocResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * #16838 - Anexar Documento ao Laudo Automaticamente
 * @author aghu
 * 
 */
@Stateless
public class AnexoDocumentoLaudoAutomaticoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AnexoDocumentoLaudoAutomaticoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private AelDocResultadoExameDAO aelDocResultadoExameDAO;

@EJB
private IExamesBeanFacade examesBeanFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4462251888798351761L;

	// A extensão padrão é PDF e até então nenhum outro formato é permitido
	private final static String EXTENSAO_PADRAO_ANEXO = ".pdf";

	public enum AnexoDocumentoLaudoAutomaticoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ANEXO_AUTOMATICO_TAMANHO_ARQUIVO_REJEITADO;
	}

	/**
	 * Anexa documento ao laudo automaticamente
	 */
	public boolean anexarDocumentoLaudoAutomatico(AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {
		return this.controlarAnexoAutomatico(itemSolicitacaoExame);
	}

	/**
	 * Function TFormDigitacaoExame.ControleAnexoAutomatico Verifica se a unidade funcional do item de solicitação de exame permite anexar documentos automaticamente
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public boolean controlarAnexoAutomatico(AelItemSolicitacaoExames itemSolicitacaoExame) throws BaseException {
		final boolean indAnexaDocAutomatico = this.getAelItemSolicitacaoExameDAO().verificarUnidadeFuncionalItemSolicitacaoExamePermiteAnexo(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());
		if (Boolean.TRUE.equals(indAnexaDocAutomatico)) {
			final String localSelecao = this.atribuirLocalSelecao(itemSolicitacaoExame); // Parte da function TFormDigitacaoExame.InicioDoProcessoAnexarDoc
			if (!this.verificarExistenciaDocumentoAnexo(itemSolicitacaoExame)) { // Parte da function TFormDigitacaoExame.InicioDoProcessoAnexarDoc
				return this.verificarPastaDocumentoAnexo(itemSolicitacaoExame, localSelecao);
			}
		}
		return false;
	}

	/**
	 * Function TFormDigitacaoExame.AtribuirLocalSelecao
	 */
	public String atribuirLocalSelecao(AelItemSolicitacaoExames itemSolicitacaoExame) {
		return this.getAelItemSolicitacaoExameDAO().obterLocalDocumentoAnexoUnidadeFuncionalItemSolicitacaoExame(itemSolicitacaoExame.getId().getSoeSeq(),
				itemSolicitacaoExame.getId().getSeqp());
	}

	/**
	 * Function TFormDigitacaoExame.ExisteDocumentoAnexo
	 */
	public boolean verificarExistenciaDocumentoAnexo(AelItemSolicitacaoExames itemSolicitacaoExame) {
		return this.getAelDocResultadoExameDAO().existeDocumentoAnexado(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());
	}

	/**
	 * Procedure TFormDigitacaoExame.VerificaPastaDocumentoAnexo
	 */
	public boolean verificarPastaDocumentoAnexo(AelItemSolicitacaoExames itemSolicitacaoExame, final String localSelecao) throws BaseException {
		
		File diretorio = new File(localSelecao);

		// Verifica se o caminho existe e é uma pasta
		if (diretorio.exists() && diretorio.isDirectory()) {

			// Cria filtro para arquivos *.PDF (extensão de anexo padrão)
			FilenameFilter filtroPdfs = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.trim().toLowerCase().endsWith(EXTENSAO_PADRAO_ANEXO);
				}
			};

			// Obtém todos arquivos da pasta
			File[] arquivos = diretorio.listFiles(filtroPdfs);

			// Obtém o nome do arquivo em anexo: SOE_SEQ + SEQP com 3 zeros à esquerda + extensão
			final String nomeDoArquivo = this.extrairNomeExtensaoDocumentoLaudoAnexo(itemSolicitacaoExame);

			// Percorre todos arquivos da pasta
			for (File anexo : arquivos) {
				final String nomeAnexo = anexo.getName();
				if (nomeAnexo.equalsIgnoreCase(nomeDoArquivo)) {
					// RETORNA verdadeiro somente se o arquivo de anexo foi encontrado e será anexado
					return this.anexarDocumento(itemSolicitacaoExame, anexo);
				}
			}

		}

		return false;

	}
	


	/**
	 * Obtém o nome do arquivo em anexo: SOE_SEQ + SEQP (com 3 zeros à esquerda) + extensão padrão
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public String extrairNomeExtensaoDocumentoLaudoAnexo(AelItemSolicitacaoExames itemSolicitacaoExame) {
		final Integer soeSeq = itemSolicitacaoExame.getId().getSoeSeq();
		final Short seqp = itemSolicitacaoExame.getId().getSeqp();
		// Acrescenta 3 zeros a esquerda no seqp
		String seqpTresZerosEsquerda = StringUtil.adicionaZerosAEsquerda(seqp, 3);
		return soeSeq + seqpTresZerosEsquerda + EXTENSAO_PADRAO_ANEXO;
	}

	/**
	 * Function TFormDigitacaoExame.AnexarDocumento(const pSolicitacao, pItem: Integer; const pPathNomeArquivo: String): Boolean; Anexa o arquivo
	 * 
	 * @return
	 */
	public boolean anexarDocumento(AelItemSolicitacaoExames itemSolicitacaoExame, final File arquivo) throws BaseException {

		// Valida o tamanho do arquivo enviado
		if (arquivo != null) {
			
			final Integer tamanhoMaximoArquivo = this.obterTamanhoMaximoBytesUploadLaudo();

			// Valida o tamanho máximo do arquivo de upload
			if (arquivo.length() > tamanhoMaximoArquivo) {
				throw new ApplicationBusinessException(AnexoDocumentoLaudoAutomaticoONExceptionCode.MENSAGEM_ANEXO_AUTOMATICO_TAMANHO_ARQUIVO_REJEITADO, arquivo.getName(), tamanhoMaximoArquivo);
			}

			// Instancia um documento de resultado de exames
			AelDocResultadoExame doc = new AelDocResultadoExame();
			
			// Seta anulação como FALSA
			doc.setIndAnulacaoDoc(false);

			// Seta item de solicitacao de exame
			doc.setItemSolicitacaoExame(itemSolicitacaoExame);

			// Converte arquivo em um array de Bytes
			byte[] blobDocumentoLaudo = CoreUtil.serializarArquivo(CoreUtil.converterFileToArrayBytes(arquivo));

			// Seta BLOB no registro de documento de resultado de exames
			doc.setDocumento(blobDocumentoLaudo);

			// Chamada para as RNs de AEL_DOC_RESULTADO_EXAMES. Vide: modulo.qInseriAEL_DOC_RESULTADO_EXAMES
			this.getExamesBeanFacade().anexarDocumentoLaudo(doc, itemSolicitacaoExame.getUnidadeFuncional());

			return true;

		}

		return false;

	}
	
	
	/**
	 * Obtém o tamanho o tamanho máximo para o upload de laudo anexo
	 * Obs. Implementação do AGHU
	 * @return
	 * @throws BaseException
	 */
	public Integer obterTamanhoMaximoBytesUploadLaudo()  {
		
		Integer tamanhoMaximoBytesUploadLaudo = null; 
		final Integer valorPadrao = 1048576; // O valor padrão é 1MB;
		
		AghParametros parametroTamanhoMaximoBytesUploadLaudo;
		try {
			parametroTamanhoMaximoBytesUploadLaudo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_TAMANHO_MAXIMO_BYTES_UPLOAD_LAUDO);
			
			if(parametroTamanhoMaximoBytesUploadLaudo != null && (parametroTamanhoMaximoBytesUploadLaudo.getVlrNumerico() != null)){
				BigDecimal valorNumericoParametro = parametroTamanhoMaximoBytesUploadLaudo.getVlrNumerico();
				BigDecimal valorNumericoPadraoParametro = parametroTamanhoMaximoBytesUploadLaudo.getVlrNumericoPadrao();
				
				tamanhoMaximoBytesUploadLaudo = valorNumericoParametro != null ? valorNumericoParametro.intValue() : valorNumericoPadraoParametro != null ? valorNumericoPadraoParametro.intValue() : valorPadrao;
			}
			
		} catch (ApplicationBusinessException e) {
			// Retorna o valor padrão quando o parâmetro está indisponível
			return valorPadrao;
		}	

		return tamanhoMaximoBytesUploadLaudo;
		
	}

	/*
	 * Facades, ONs, RNs e DAOs
	 */
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	

	protected IExamesBeanFacade getExamesBeanFacade() {
		return this.examesBeanFacade;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected AelDocResultadoExameDAO getAelDocResultadoExameDAO() {
		return aelDocResultadoExameDAO;
	}

}
