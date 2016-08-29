package br.gov.mec.aghu.exames.contratualizacao.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.business.scheduler.JobEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.contratualizacao.util.Detalhes;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * @author fhoffmeister
 *
 */
@Stateless
public class ReceberArquivoXmlON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ReceberArquivoXmlON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ISchedulerFacade schedulerFacade;

	/**
	 */
	private static final long serialVersionUID = -580843568867782240L;

	protected enum ReceberArquivoXmlONExceptionCode implements BusinessExceptionCode {
		MSG_ARQUIVO_INVALIDO_NOME,
		MSG_ARQUIVO_INVALIDO_FORMATO,
		MENSAGEM_VALOR_PARAMETRO_NAO_ENCONTRADO, 
		MENSAGEM_NOME_DIRETORIO_VAZIO, 
		MENSAGEM_DIRETORIO_NAO_EXISTE,
		MENSAGEM_CAMINHO_NAO_DIRETORIO, 
		MENSAGEM_SEM_PERMISSAO_LEITURA_DIRETORIO,
		MENSAGEM_SEM_PERMISSAO_ESCRITA_DIRETORIO;
	}
	
	protected static final String INICIO_ARQUIVO = "ExamesPrefeitura";
	protected static final String SEPARATOR = "_";
	protected static final String EXTENSAO = ".xml";
	
	/**
	 * Valida a string passada para o formato "ExamesPrefeitura_ddmmyyyy_hhmmss.xml
	 * 
	 * Se não for válido irá estourar um erro na tela.
	 * @param nomeXml
	 * @return
	 */
	public void verificarFormatoArquivoXml(String nomeXml) throws BaseException {
		Pattern p = Pattern.compile(INICIO_ARQUIVO+SEPARATOR + RegexUtil.DATE_PATTERN + SEPARATOR + RegexUtil.HOUR_PATTERN_HHMMSS +EXTENSAO);
		Matcher m = p.matcher(nomeXml);
		Boolean retorno = m.matches();

		//Se passou no regex, valida se o dia não é maior do que um dia possível em um mês. (Exemplo 30/02)
		if (retorno) {
			retorno = RegexUtil.validarDias(nomeXml.split(SEPARATOR)[1]);
		}

		if (!retorno) {
			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_NOME);
		}
	}

	public Detalhes verificarEstruturaArquivoXml(String caminhoAbsolutoXml) throws BaseException {
		Detalhes detalhes;		
		try {
			JAXBContext context = JAXBContext.newInstance(Detalhes.class);
			Unmarshaller um = context.createUnmarshaller();
			
			
//			Unmarshaller unmarshaller = jc.createUnmarshaller();
			InputStream inputStream = new FileInputStream(caminhoAbsolutoXml);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			try {
				detalhes = (Detalhes) um.unmarshal(reader);
			} finally  {
			    reader.close();
			}

			
			

			//			um.setSchema(SchemaFactory.newInstance(
			//				      XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
			//				      new File("jar/src/main/java/br/gov/mec/aghu/exames/contratualizacao/util/ExamesPrefeitura.xsd")));

//			detalhes = (Detalhes) um.unmarshal(new FileReader(caminhoAbsolutoXml));

			//		} catch (FileNotFoundException e) {
			//			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_FORMATO, e);
			//		} catch (JAXBException e) {
			//			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_FORMATO, e);
			//		} catch (SAXException e) {
			//			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_FORMATO, e);
		} catch (Exception e) {
			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_FORMATO, e);
		}
		return detalhes;
	}

	public Map<String, Object> importarArquivoContratualizacao(Detalhes detalhes, String caminhoAbsoluto, String nomeArquivo, Header headerIntegracao, String nomeMicrocomputador) throws BaseException {
		
		// Abertura de arquivos
		final File arquivo = new File(caminhoAbsoluto);
		final AghSistemas sistema = getAghuFacade().obterAghSistema(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SIGLA_EXAME).getVlrTexto());
		final AghArquivoProcessamento arquivoProcessamento;
		try {
			arquivoProcessamento = criarArquivo(arquivo, sistema, 1, getAghuFacade());
		} catch (IOException e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MSG_ARQUIVO_INVALIDO_NOME, caminhoAbsoluto);
		}
		
		Map<String,Object> parametros = new HashMap<>();
		
		parametros.put("DETALHES", detalhes);
		parametros.put("NOME_ARQUIVO", arquivoProcessamento.getNome());
		parametros.put("NOME_ARQUIVO_ORIGINAL", nomeArquivo);
		parametros.put("HEADER_INTEGRACAO", headerIntegracao);
		parametros.put("CAMINHO_ABSOLUTO", caminhoAbsoluto);
		parametros.put("NOME_MICROCOMPUTADOR", nomeMicrocomputador);
		
		Date dataMais5Seg = DateUtil.adicionaSegundos(new Date(), 5);

		this.schedulerFacade.agendarTarefa(JobEnum.PROCESSADOR_ARQUIVOS_CONTRATUALIZACAO,null
				, dataMais5Seg,
				this.servidorLogadoFacade.obterServidorLogado()
				, parametros);

		final List<AghArquivoProcessamento> arquivos = new ArrayList<AghArquivoProcessamento>(1);
		arquivos.add(arquivoProcessamento);
		Map<String, Object> retorno = new HashMap<String, Object>();
		retorno.put(IExamesFacade.ARQUIVOS_IMPORTACAO_EXAMES, arquivos);
		return retorno;
	}

	private AghArquivoProcessamento criarArquivo(final File arquivo, final AghSistemas sistema, final int ordem, final IAghuFacade aghuFacade) throws IOException, ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final FileInputStream reader = new FileInputStream(arquivo);
		final AghArquivoProcessamento aghArquivo = new AghArquivoProcessamento();
		aghArquivo.setArquivo(new byte[(int) arquivo.length()]);
		reader.read(aghArquivo.getArquivo());
		aghArquivo.setNome(arquivo.getName());
		aghArquivo.setDthrCriadoEm(new Date());
		aghArquivo.setOrdemProcessamento(ordem);
		aghArquivo.setPercentualProcessado(0);
		aghArquivo.setSistema(sistema);
		aghArquivo.setUsuario(servidorLogado);
		return aghuFacade.persistirAghArquivoProcessamento(aghArquivo);
	}
	
	/**
	 * Verifica as permissões de escrita nas pastas definidas em parametros do sistema para a contratualização de exames
	 * Deve possuir permissão de escrita em todas as pastas
	 * @see {@link http://redmine084.mec.gov.br/issues/14594}
	 *  caso falhe em alguma permissao
	 */
	public void verificarPermissaoDeEscritaPastasDestino() throws BaseException {
		logInfo("ReceberArquivoXmlON.verificarPermissaoDeEscritaPastasDestino(): Entrando.");
		// verificar pasta parametro entrada
		verificarPermissaoPastaParametro(AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_ENTRADA);
		// verificar pasta parametro entrada historico
		verificarPermissaoPastaParametro(AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_ENTRADA_HIST);
		// verificar pasta parametro saida
		verificarPermissaoPastaParametro(AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_SAIDA);
		// verificar pasta parametro saida historico
		verificarPermissaoPastaParametro(AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_SAIDA_HIST);
		logInfo("ReceberArquivoXmlON.verificarPermissaoDeEscritaPastasDestino(): Saindo. Permissões de acesso OK.");
	}
	
	protected void verificarPermissaoPastaParametro(AghuParametrosEnum nomeParametro) throws BaseException {
		AghParametros parametro = getParametroFacade().obterAghParametro(nomeParametro);
		if (parametro == null || parametro.getVlrTexto() == null) {
			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MENSAGEM_VALOR_PARAMETRO_NAO_ENCONTRADO, nomeParametro.name());
		} 
		verificarPermissaoPasta(parametro.getVlrTexto());
	}
	
	protected void verificarPermissaoPasta(String caminho) throws ApplicationBusinessException {
		logInfo("ReceberArquivoXmlON.verificarPermissaoPasta(): Caminho = [" + caminho + "].");
		if (StringUtils.isBlank(caminho)) {
			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MENSAGEM_NOME_DIRETORIO_VAZIO);
		}
		File pasta = new File(caminho);
		if (! pasta.exists()) {
			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MENSAGEM_DIRETORIO_NAO_EXISTE, caminho);
		}
		if (! pasta.isDirectory()) {
			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MENSAGEM_CAMINHO_NAO_DIRETORIO, caminho);
		}
		if (! pasta.canRead()) {
			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MENSAGEM_SEM_PERMISSAO_LEITURA_DIRETORIO, caminho);
		}
		if (! pasta.canWrite()) {
			throw new ApplicationBusinessException(ReceberArquivoXmlONExceptionCode.MENSAGEM_SEM_PERMISSAO_ESCRITA_DIRETORIO, caminho);
		}
	}
	
//	private ProcessadorArquivosContratualizacaoScheduller getProcessadorArquivosContratualizacao() {
//		return this.processadorArquivosContratualizacaoScheduller;
//	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
