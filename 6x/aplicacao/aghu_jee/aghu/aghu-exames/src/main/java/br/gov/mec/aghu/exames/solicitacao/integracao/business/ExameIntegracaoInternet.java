/**
 * 
 */
package br.gov.mec.aghu.exames.solicitacao.integracao.business;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import br.gov.mec.aghu.exames.solicitacao.util.ConselhoMedico;
import br.gov.mec.aghu.exames.solicitacao.util.ConselhoUf;
import br.gov.mec.aghu.exames.solicitacao.util.GrupoExame;
import br.gov.mec.aghu.exames.solicitacao.util.IdentificacaoLaudo;
import br.gov.mec.aghu.exames.solicitacao.util.Laudo;
import br.gov.mec.aghu.exames.solicitacao.util.ObjectFactory;
import br.gov.mec.aghu.exames.solicitacao.util.Opcao;
import br.gov.mec.aghu.exames.solicitacao.util.Paciente;
import br.gov.mec.aghu.exames.solicitacao.util.RecebeLaudo;
import br.gov.mec.aghu.exames.solicitacao.util.retorno.Erro;
import br.gov.mec.aghu.exames.solicitacao.util.retorno.RetornoLaudo;
import br.gov.mec.aghu.exames.solicitacao.vo.DadosEnvioExameInternetVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DadosRetornoExameInternetVO;
import br.gov.mec.aghu.core.business.BaseBusiness;


/**
 * Classe responsável por implementar todas a integração dos exames com o Portal
 * 
 * @author dcastro
 * 
 */
@Stateless
public class ExameIntegracaoInternet extends BaseBusiness implements IExameIntegracaoInternet {
	
	@Inject
	private CloseableHttpClient httpClient;

	/**
	 * 
	 */
	private static final long serialVersionUID = 111167461453782587L;
	private static final Log LOG = LogFactory.getLog(ExameIntegracaoInternet.class);
	private static final String NOME_ARQUIVO = "laudo";
	private static final String EXTENSAO_PDF = ".pdf";
	private static final String EXTENSAO_XML = ".xml";
	private static final int NUM_ARQUIVOS_TEMP = 1;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String ERRO_GENERICO_WEBSERVICE = "Erro genérico ao comunicar com o WEBSERVICE";

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Método responsável por gerar o xml de envio dos resultados
	 * 
	 * @param dadosEnvioExameInternetVO
	 * @return
	 */
	@Override
	public String gerarXmlEnvio(DadosEnvioExameInternetVO dadosEnvioExameInternetVO)
			 {

		if (dadosEnvioExameInternetVO == null) {
			throw new IllegalArgumentException(
					"Parâmetro dadosEnvioExameInternetVO não informado.");
		}

		Marshaller marshaller = null;

		try {
			JAXBContext context = JAXBContext
					.newInstance("br.gov.mec.aghu.exames.solicitacao.util");
			marshaller = context.createMarshaller();

		} catch (JAXBException e) {
			LOG.error(e.getMessage(), e);
		}

		Laudo laudoExame = new ObjectFactory().createLaudo();
		
		GrupoExame grupoExame = new ObjectFactory().createGrupoExame();
		grupoExame.setCodigo(dadosEnvioExameInternetVO.getSeqGrupo());
		grupoExame.setDescricao(dadosEnvioExameInternetVO.getDescricaoGrupo());

		IdentificacaoLaudo identificacaoLaudo = new ObjectFactory()
				.createIdentificacaoLaudo();
		identificacaoLaudo
				.setSolicitacao(dadosEnvioExameInternetVO.getSoeSeq());
		identificacaoLaudo.setGrupoExame(grupoExame);
		identificacaoLaudo.setLocalizador(dadosEnvioExameInternetVO
				.getLocalizador());

		Paciente pacienteExame = new ObjectFactory().createPaciente();
		pacienteExame.setCodigo(dadosEnvioExameInternetVO.getCodigoPaciente());
		pacienteExame.setDataNascimento(dadosEnvioExameInternetVO
				.getDataNascimentoPaciente());
		pacienteExame.setNome(dadosEnvioExameInternetVO.getNomePaciente());

		ConselhoMedico medicoExame = new ObjectFactory().createConselhoMedico();
		medicoExame.setConselhoNumero(dadosEnvioExameInternetVO
				.getConselhoProfissionalMedico().getRegConselhoMedico());
		
		medicoExame.setConselhoTipo(
				dadosEnvioExameInternetVO
				.getConselhoProfissionalMedico()
				.getSiglaConselhoMedico());
		
		medicoExame
				.setConselhoUf(ConselhoUf.fromValue(dadosEnvioExameInternetVO
						.getConselhoProfissionalMedico().getUfConselhoMedico()));		 

		if(dadosEnvioExameInternetVO.getConselhoProfissionalResponsavel()!=null){
			ConselhoMedico responsavelExame = new ObjectFactory().createConselhoMedico();
			responsavelExame.setConselhoNumero(dadosEnvioExameInternetVO
					.getConselhoProfissionalResponsavel()
					.getRegConselhoMedico());
			
			responsavelExame.setConselhoTipo(
					dadosEnvioExameInternetVO
					.getConselhoProfissionalResponsavel()
					.getSiglaConselhoMedico());
			
			responsavelExame.setConselhoUf(ConselhoUf
					.fromValue(dadosEnvioExameInternetVO
							.getConselhoProfissionalResponsavel()
							.getUfConselhoMedico()));
			
			laudoExame.setResponsavel(responsavelExame);
		}
		
		laudoExame.setIdentificacao(identificacaoLaudo);
		laudoExame.setConvenio(dadosEnvioExameInternetVO.getConvenio());
		laudoExame.setDataLiberacao(dadosEnvioExameInternetVO
				.getDataLiberacaoExame());
		laudoExame.setDataExame(dadosEnvioExameInternetVO.getDataExame());

		laudoExame.setPaciente(pacienteExame);
		laudoExame.setMedico(medicoExame);
		laudoExame
				.setNotaAdicional(dadosEnvioExameInternetVO.isNotaAdicional() ? Opcao.S
						: Opcao.N);
		laudoExame.setInstituicao(dadosEnvioExameInternetVO
				.getCnpjInstituicao());

		RecebeLaudo recebeLaudo = new ObjectFactory().createRecebeLaudo();
		recebeLaudo.setLaudo(laudoExame);

		Writer xmlEnvio = new StringWriter();

		try {
			marshaller.marshal(recebeLaudo, xmlEnvio);
		} catch (JAXBException e) {
			LOG.error(e.getMessage(), e);
		}

		return xmlEnvio.toString();
	}


	/**
	 * Compactar o relatório do laudo e o xml de envio
	 */
	public File gerarArquivoCompactado(byte[] arquivoLaudo, String xmlEnvio) {

		File zipFile = null;
		ZipOutputStream zipFileOS = null;

		try {

			LOG.info("Iniciada a compactacao dos arquivos de laudos");

			zipFile = criarArquivoTemporario(NUM_ARQUIVOS_TEMP);
			
			zipFileOS = new ZipOutputStream(new FileOutputStream(zipFile));

			zipFileOS.putNextEntry(new ZipEntry(NOME_ARQUIVO + EXTENSAO_PDF));

			zipFileOS.write(arquivoLaudo);

			LOG.info("Compactação concluída laudo.pdf");

			zipFileOS.putNextEntry(new ZipEntry(NOME_ARQUIVO + EXTENSAO_XML));
			
			zipFileOS.write(xmlEnvio.getBytes());

			LOG.info("Compactação concluída laudo.xml");
            
			return zipFile;

		} catch (Exception e) {
			LOG.error("Ocorreu um erro ao gerar o arquivo zip: ", e);
			return null;
		} finally {
			if (zipFileOS != null) {
				IOUtils.closeQuietly(zipFileOS);
			}
		}
	}
	
	public ByteArrayOutputStream gerarArquivoCompactadoEmMemoria(byte[] arquivoLaudo, String xmlEnvio) {

		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			LOG.info("Iniciada a compactacao dos arquivos de laudos");

			ZipOutputStream zipFileOS = new ZipOutputStream(os);

			zipFileOS.putNextEntry(new ZipEntry(NOME_ARQUIVO + EXTENSAO_PDF));

			zipFileOS.write(arquivoLaudo);

			LOG.info("Compactação concluída laudo.pdf");

			zipFileOS.putNextEntry(new ZipEntry(NOME_ARQUIVO + EXTENSAO_XML));
			
			zipFileOS.write(xmlEnvio.getBytes());

			LOG.info("Compactação concluída laudo.xml");
            
			return os;

		} catch (Exception e) {
			LOG.error("Ocorreu um erro ao gerar o arquivo zip: ", e);
			return null;
		} finally {
			if (os != null) {
				IOUtils.closeQuietly(os);
			}
		}
	}
	

	/**
	 * Criar arquivos temporários para compactação dos mesmos
	 * @param numArquivos
	 * @return
	 * @throws Exception
	 */
	private File criarArquivoTemporario(int numArquivos) {
		File file = null;		
		int aux = 0;
		try {
			do {
				file = new File(numArquivos++ + "_" + System.nanoTime() + ".tmp");
			} while (aux++ < 10 && !file.createNewFile());
		} catch (IOException e) {
			LOG.error("Ocorreu um erro ao gerar o arquivo temporário ", e);
		}

		if (!file.exists()) {
			LOG.error("Ocorreu um erro ao gerar o arquivo temporário ");
		}

		return file;
	}

	/**
	 * OBS.: Método comentando para não configurar o certificado no AGHU e deixar
	 * isso como configuração do servidor
	 * 
	 * @throws AGHUNegocioException
	 *             se exception ao buscar parâmetro
	 * @throws AGHUNegocioException
	 *             se o arquivo imformado no parâmetro não existir no servidor
	 */
	/*private void configuraTrustStore(String trustStorePath) {

		LOG.info("------// Path keystore : " + trustStorePath);

		// verifica existencia do arquivo
		File trustStore = new File(trustStorePath);
		if (!trustStore.exists() || !trustStore.isFile()) {
			LOG.error("ERRO - Operação de envio não suportada, "
					+ "falta o arquivo de certificados para a requisição HTTPS. ");
			LOG.error("O arquivo " + trustStore.getAbsolutePath()
					+ " não existe neste servidor.");
		}

		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", "12345678");
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
	}*/

	/**
	 * Realizar o envio do laudo para o portal
	 */
	public DadosRetornoExameInternetVO enviarLaudoPortal(final Integer soeSeq, Integer grupoSeq, String localizador, File arquivoLaudo, String urlBase, String senha ) {

		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		DadosRetornoExameInternetVO retornoExamesInternetVO = new DadosRetornoExameInternetVO();
		String url = urlBase + this.geraHash(senha + localizador) + "/" + localizador;
		final HttpPost post = new HttpPost(url);
		HttpClientContext context = HttpClientContext.create();
		CloseableHttpResponse retorno = null;
		try {
			LOG.info("Tentando enviar laudo para o portal pela URL: " + url);
			
			post.addHeader("Connection", "close");
			post.addHeader("connection", "close");
			post.addHeader("content-type", "application/binary");
			post.addHeader("enctype", "multipart/form-data");
			
			HttpEntity entity = new FileEntity(arquivoLaudo,ContentType.MULTIPART_FORM_DATA);
			post.setEntity(entity);

			int hardTimeout = 15; // seconds
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					if (post != null) {
						post.abort();
						post.releaseConnection();
						LOG.info("Abortando a conexão HTTP no envio do ZIP após 15 segundos : [SOE_SEQ:"
								+ soeSeq + "]");
					}
				}
			};

			new Timer(true).schedule(task, hardTimeout * 1000);
			retorno = httpClient.execute(post,context);

			int returncode = retorno.getStatusLine().getStatusCode();
			
			if (returncode == HttpStatus.SC_OK) {

				br = new BufferedReader(new InputStreamReader(
						retorno.getEntity().getContent(), Charset.forName("ISO-8859-1")));
				String readLine;

				while (((readLine = br.readLine()) != null)) {
					sb.append(readLine);
				}

				sb.append(LINE_SEPARATOR);
				retornoExamesInternetVO = tratarXmlRetorno(sb.toString());			

			}
			return retornoExamesInternetVO;
		} catch (IOException e) {
			LOG.error("------// Erro no HttpClient : [SOE_SEQ:"+soeSeq+"]", e);
			
			List<String> erro = new ArrayList<String>();
			erro.add(ERRO_GENERICO_WEBSERVICE + " msg: " + e.getLocalizedMessage() + " causa: " + e.getCause());
			retornoExamesInternetVO.setDescricaoErro(erro);

			return retornoExamesInternetVO;
		} catch (Exception e) {
			LOG.error("------// Erro no envio para o portal : [SOE_SEQ:"+soeSeq+"]", e);
			
			List<String> erro = new ArrayList<String>();
			erro.add(ERRO_GENERICO_WEBSERVICE + " msg: " + e.getLocalizedMessage() + " causa: " + e.getCause());
			retornoExamesInternetVO.setDescricaoErro(erro);

			return retornoExamesInternetVO;
		}finally {
			if (arquivoLaudo != null && arquivoLaudo.exists()) {
				arquivoLaudo.delete();
			}
			if (retorno != null) {
				try {
					retorno.close();
				} catch (IOException e) {
					LOG.warn("Erro ao fechar response.",e);
				}
			}
			post.releaseConnection();
		}
	}
	
	/**
	 * carregar o VO com os erros apresentados na comunicação com o webservice
	 * @param xmlRetorno
	 * @return
	 */
	private static DadosRetornoExameInternetVO tratarXmlRetorno(String xmlRetorno) {	
		InputStream is = new ByteArrayInputStream(xmlRetorno.getBytes());
		RetornoLaudo retorno = new br.gov.mec.aghu.exames.solicitacao.util.retorno.ObjectFactory().createRetornoLaudo();
		try {
			JAXBContext context = JAXBContext
					.newInstance("br.gov.mec.aghu.exames.solicitacao.util.retorno");

			Unmarshaller unmarshaller = context.createUnmarshaller();

			retorno = unmarshaller.unmarshal(new StreamSource(is),
					RetornoLaudo.class).getValue();

		} catch (JAXBException e) {
			LOG.error("Exceção capturada: ", e);
		}
		
		DadosRetornoExameInternetVO retornoLaudoVO = new DadosRetornoExameInternetVO();
		
		if (retorno != null && retorno.getIdentificacao() != null) {
			retornoLaudoVO.setSoeSeq(retorno.getIdentificacao().getSolicitacao());
			retornoLaudoVO.setSeqGrupo(retorno.getIdentificacao().getGrupoExame().getCodigo());
			retornoLaudoVO.setLocalizador(retorno.getIdentificacao().getLocalizador());
		}
		
		if (retorno.getErros() != null && retorno.getErros().getErro() != null && !retorno.getErros().getErro().isEmpty()) {
			retornoLaudoVO.getDescricaoErro().add("ERROS XML RETORNO DO PORTAL: ");			
			
			for(Erro item : retorno.getErros().getErro()){
				retornoLaudoVO.getDescricaoErro().add(item.getDescricao());	
			}
			
		}
		
		return retornoLaudoVO;
	}
	
	private String geraHash(String entrada) {
		StringBuilder hash = new StringBuilder(entrada);
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(hash.toString().getBytes());
			hash = stringHexa(md.digest());
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
		}
		return hash.toString();
	}

	private StringBuilder stringHexa(byte[] bytes) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
			int parteBaixa = bytes[i] & 0xf;
			if (parteAlta == 0){
				s.append('0');
			}
			s.append(Integer.toHexString(parteAlta | parteBaixa));
		}
		return s;
	}

}