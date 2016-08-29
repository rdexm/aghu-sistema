package br.gov.mec.aghu.exames.contratualizacao.business;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.contratualizacao.util.SolicitacaoExame;
import br.gov.mec.aghu.exames.contratualizacao.util.retorno.Detalhes;
import br.gov.mec.aghu.exames.contratualizacao.util.retorno.Header;
import br.gov.mec.aghu.exames.contratualizacao.util.retorno.Item;
import br.gov.mec.aghu.exames.contratualizacao.util.retorno.Itens;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author ebicca
 *
 */
@Stateless
public class GravacaoArquivoXmlRetornoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(GravacaoArquivoXmlRetornoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4799469856214075462L;

	/**
	 */


	protected enum GravacaoArquivoXmlRetornoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_VALOR_PARAMETRO_NAO_ENCONTRADO, MENSAGEM_IMPOSSIVEL_GRAVAR_XML_RETORNO, MENSAGEM_NOME_ARQUIVO_ENTRADA_NULO, MENSAGEM_IMPOSSIVEL_GRAVAR_XML_HISTORICO_ENTRADA
	}

	protected static final String INICIO_ARQUIVO_ENTRADA = "ExamesPrefeitura";
	protected static final String INICIO_ARQUIVO_SAIDA = "RetornoPrefeitura";
	protected static final String SEPARATOR_DIRETORIO = "/";


	public String executarAcoesGravarXMLRetorno(List<Item> listaItens, String nomeArquivoEntradaCaminhoAbsoluto, String nomeArquivoEntrada, int nrRegistrosProcesso) throws ApplicationBusinessException {
		Detalhes detalhes = montarDadosXMLRetorno(listaItens, nrRegistrosProcesso);
		String nomeArquivoRetorno = gravarXMLRetorno(detalhes, nomeArquivoEntrada);
		gravarXMLHistoricoEntrada(nomeArquivoEntradaCaminhoAbsoluto, nomeArquivoEntrada);
		excluirXMLEntrada(nomeArquivoEntrada);
		return nomeArquivoRetorno;
	}

	private void excluirXMLEntrada(String nomeArquivoEntrada) throws ApplicationBusinessException {
		String caminhoArquivoEntrada = buscarCaminhoEntrada();
		try {
			File arquivoOriginal = new File(caminhoArquivoEntrada + SEPARATOR_DIRETORIO + nomeArquivoEntrada);
			if (arquivoOriginal.exists() && arquivoOriginal.canWrite()) {
				arquivoOriginal.delete();
				logInfo("GravacaoArquivoXmlRetornoON.excluirXMLEntrada(). Arquivo de entrada [" + arquivoOriginal.getCanonicalPath() + "] excluído com sucesso.");
			} else {
				logInfo("GravacaoArquivoXmlRetornoON.excluirXMLEntrada(). Arquivo de entrada [" + arquivoOriginal.getCanonicalPath() + "] não existe ou não está acessível.");
			}
		} catch (Exception e) {
			logWarn(super.getResourceBundleValue("MENSAGEM_IMPOSSIVEL_APAGAR_ARQUIVO_ENTRADA"));
		}
	}

	private void gravarXMLHistoricoEntrada(String nomeArquivoEntradaCaminhoAbsoluto, String nomeArquivoEntrada) throws ApplicationBusinessException {
		logDebug("GravacaoArquivoXmlRetornoON.gravarXMLHistoricoEntrada(): nomeArquivoEntradaCaminhoAbsoluto = [" + nomeArquivoEntradaCaminhoAbsoluto + "]");
		logDebug("GravacaoArquivoXmlRetornoON.gravarXMLHistoricoEntrada(): nomeArquivoEntrada = [" + nomeArquivoEntrada + "]");
		try {
			BufferedReader arquivoEntrada = new BufferedReader(new FileReader(nomeArquivoEntradaCaminhoAbsoluto));
			String caminhoArquivoHistoricoEntrada = buscarCaminhoHistoricoEntrada();
			String nomeHistoricoEntrada = caminhoArquivoHistoricoEntrada + SEPARATOR_DIRETORIO + nomeArquivoEntrada;
			File arq = new File(nomeHistoricoEntrada);
			logDebug("GravacaoArquivoXmlRetornoON.gravarXMLHistoricoEntrada(): arquivo = [" + nomeHistoricoEntrada + "]");
			BufferedWriter arquivoSaida = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arq),"UTF-8"));
			String line = null;
			while (( line = arquivoEntrada.readLine()) != null) {
				arquivoSaida.write(line);
				arquivoSaida.newLine();   // Write system dependent end of line.
			}
			arquivoEntrada.close();
			arquivoSaida.close();
			logDebug("GravacaoArquivoXmlRetornoON.gravarXMLHistoricoEntrada(): Arquivo de entrada copiado para a pasta [" + caminhoArquivoHistoricoEntrada + "] com sucesso.");
		} catch (Exception e) {
			logError(e);
			throw new ApplicationBusinessException(GravacaoArquivoXmlRetornoONExceptionCode.MENSAGEM_IMPOSSIVEL_GRAVAR_XML_HISTORICO_ENTRADA);
		} 
	}

	private String gravarXMLRetorno(Detalhes detalhes, String nomeArquivoOriginal) throws ApplicationBusinessException {
		if (StringUtils.isEmpty(nomeArquivoOriginal)) {
			throw new ApplicationBusinessException(GravacaoArquivoXmlRetornoONExceptionCode.MENSAGEM_NOME_ARQUIVO_ENTRADA_NULO);
		}
		logInfo("GravacaoArquivoXmlRetornoON.gravarXMLRetorno(): Arquivo original = [" + nomeArquivoOriginal + "].");
		String caminhoGravacao = buscarCaminhoGravacao();
		logInfo("GravacaoArquivoXmlRetornoON.gravarXMLRetorno(): Diretorio do arquivo de saida = [" + caminhoGravacao + "].");
		String nomeArquivoSaida = montaNomeArquivoSaida(nomeArquivoOriginal);
		logInfo("GravacaoArquivoXmlRetornoON.gravarXMLRetorno(): Nome do arquivo de saida = [" + nomeArquivoSaida + "].");
		String nomeCompletoArquivoSaida = caminhoGravacao + SEPARATOR_DIRETORIO + nomeArquivoSaida;
		logInfo("GravacaoArquivoXmlRetornoON.gravarXMLRetorno(): Arquivo de saida = [" + nomeCompletoArquivoSaida + "].");
		try {
			JAXBContext context = JAXBContext.newInstance(Detalhes.class);
			Marshaller mar = context.createMarshaller();
			mar.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			FileOutputStream fw = new FileOutputStream(nomeCompletoArquivoSaida);
			mar.marshal(detalhes, fw);
			fw.close();
			return nomeCompletoArquivoSaida;
		} catch (JAXBException e) {
			logError(e);
			throw new ApplicationBusinessException(GravacaoArquivoXmlRetornoONExceptionCode.MENSAGEM_IMPOSSIVEL_GRAVAR_XML_RETORNO);
		} catch (IOException e) {
			logError(e);
			throw new ApplicationBusinessException(GravacaoArquivoXmlRetornoONExceptionCode.MENSAGEM_IMPOSSIVEL_GRAVAR_XML_RETORNO);
		}

	}

	private String montaNomeArquivoSaida(String nomeArquivoOriginal) {
		String sufixo = StringUtils.substringAfter(nomeArquivoOriginal, INICIO_ARQUIVO_ENTRADA);
		return INICIO_ARQUIVO_SAIDA + sufixo;
	}

	private String buscarCaminhoGravacao() throws ApplicationBusinessException {
		AghParametros parametro = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_SAIDA);
		if (parametro == null || parametro.getVlrTexto() == null) {
			throw new ApplicationBusinessException(GravacaoArquivoXmlRetornoONExceptionCode.MENSAGEM_VALOR_PARAMETRO_NAO_ENCONTRADO, AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_SAIDA.name());
		}
		return parametro.getVlrTexto(); 
	}

	private String buscarCaminhoHistoricoEntrada() throws ApplicationBusinessException {
		AghParametros parametro = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_ENTRADA_HIST);
		if (parametro == null || parametro.getVlrTexto() == null) {
			throw new ApplicationBusinessException(GravacaoArquivoXmlRetornoONExceptionCode.MENSAGEM_VALOR_PARAMETRO_NAO_ENCONTRADO, AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_SAIDA.name());
		}
		return parametro.getVlrTexto(); 
	}

	private String buscarCaminhoEntrada() throws ApplicationBusinessException {
		AghParametros parametro = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_ENTRADA);
		if (parametro == null || parametro.getVlrTexto() == null) {
			throw new ApplicationBusinessException(GravacaoArquivoXmlRetornoONExceptionCode.MENSAGEM_VALOR_PARAMETRO_NAO_ENCONTRADO, AghuParametrosEnum.P_DIR_EXAMES_CONTRATUALIZACAO_SAIDA.name());
		}
		return parametro.getVlrTexto(); 
	}

	/*
	 * Monta em forma de objeto JAXB o xml que sera gravado no banco 
	 * @param listaItensRetorno - Todos os itens que serao gravados
	 * @param nrRegistrosProcesso - header quantidadeSolicitacoes
	 * @return o objeto a ser parseado para XML
	 */
	private Detalhes montarDadosXMLRetorno(List<Item> listaItensRetorno, int nrRegistrosProcesso) {
		Detalhes detalhes = new Detalhes();
		Header headerRetorno = new Header();
		headerRetorno.setDataHoraGeracao(getDataHoraAtualFormatada());
		headerRetorno.setQuantidadeSolicitacoes(String.valueOf(nrRegistrosProcesso));
		Itens itens = new Itens();
		itens.getItem().addAll(listaItensRetorno);
		detalhes.setHeader(headerRetorno);
		detalhes.setItens(itens);
		return detalhes;
	}

	/**
	 * Executa regras para montagem de itens do retorno do XML.
	 * Deve ser executado para cada solicitação.
	 * Verificar RN2 {@link http://redmine084.mec.gov.br/issues/14603}
	 * @param listaItensVO
	 * @return Lista de itens para uma solicitação
	 */
	public List<Item> montarListaItensXMLRetornoSolicitacao(List<ItemContratualizacaoVO> listaItensVO) {
		List<Item> listaRetorno = new ArrayList<Item>();
		boolean temErroNoItem = false;
		//monta lista de itens de retorno
		for (ItemContratualizacaoVO itemContratualizacaoVO : listaItensVO) {
			if (StringUtils.isNotEmpty(itemContratualizacaoVO.getMensagemErro())) {
				temErroNoItem = true;
			}
			Item item = montarItemRetornoDadosPadrao(itemContratualizacaoVO);
			listaRetorno.add(item);
		}
		logInfo("GravacaoArquivoXmlRetornoON.montarListaItensXMLRetornoSolicitacao(): existe item na solicitação com erro: [" + temErroNoItem + "].");
		if (temErroNoItem) {
			//se tem erro em um item especifico, mantem a mensagem para aquele item e define mensagem padrao para os outros itens
			for (Item item : listaRetorno) {
				if (StringUtils.isEmpty(String.valueOf(item.getMensagemErro()))) {
					item.setMensagemErro(super.getResourceBundleValue("MENSAGEM_ERRO_OUTRO_ITEM_SOLICITACAO"));
				}
			}
		} else {
			//nao ocorreu erro, monta listagem das amostras
			listaRetorno = new ArrayList<Item>();
			for (ItemContratualizacaoVO itemContratualizacaoVO : listaItensVO) {
				List<AelAmostraItemExames> listaAmostraItemExames = itemContratualizacaoVO
				.getItemSolicitacaoExames().getAelAmostraItemExames();
				for (AelAmostraItemExames aelAmostraItemExames : listaAmostraItemExames) {
					Item item = montarItemRetornoDadosPadrao(itemContratualizacaoVO);
					item.setIdAmostra(gerarValorIdAmostra(aelAmostraItemExames.getAelAmostras()));
					item.setNumeroUnico(gerarValorNumeroUnico(aelAmostraItemExames.getAelAmostras()));
					item.setDataNumeroUnico(getDataFormatada(aelAmostraItemExames.getAelAmostras().getDtNumeroUnico()));
					item.setMensagemErro("");
					logDebug("GravacaoArquivoXmlRetornoON.montarListaItensXMLRetornoSolicitacao(): Item a ser gravado no XML = [" + item + "].");
					listaRetorno.add(item);
				}
			}		
		}
		return listaRetorno;
	}

	private String gerarValorIdAmostra(AelAmostras aelAmostras) {
		int numSolicitacaoExames = aelAmostras.getSolicitacaoExame().getSeq();
		int seqp = aelAmostras.getId().getSeqp();
		return String.format("%08d", numSolicitacaoExames) + String.format("%03d", seqp);
	}

	private String gerarValorNumeroUnico(AelAmostras aelAmostras) {
		String numAsString = null;
		Integer numeroUnico = aelAmostras.getNroUnico();
		if (numeroUnico != null) {
			numAsString = String.format("%-6d", numeroUnico);
		}
		String descricao = aelAmostras.getUnidadesFuncionais().getDescricao();
//		O número único pode ser nulo, neste caso vamos enviar apenas o identificador do laboratório executor no campo. 
//		Deixar 6 espaços em branco do número único para não desposicionar  o campo no momento da impressão da etiqueta.
		return StringUtils.defaultIfEmpty(numAsString, "      ") + StringUtils.upperCase(StringUtils.substring(descricao, 0, 4));
	}

	private Item montarItemRetornoDadosPadrao(ItemContratualizacaoVO itemContratualizacaoVO) {
		Item item = new Item();
		item.setIdExterno(itemContratualizacaoVO.getIdExterno());
		if (itemContratualizacaoVO.getItemSolicitacaoExames() != null) {
			item.setMaterialAnalise(itemContratualizacaoVO.getItemSolicitacaoExames().getMaterialAnalise() != null ? String.valueOf(itemContratualizacaoVO.getItemSolicitacaoExames().getMaterialAnalise().getSeq()) : itemContratualizacaoVO.getMaterialAnalise());
			item.setSiglaExame(itemContratualizacaoVO.getItemSolicitacaoExames().getExame() != null ? itemContratualizacaoVO.getItemSolicitacaoExames().getExame().getSigla() : itemContratualizacaoVO.getExame());
			item.setUnidadeExecutora(itemContratualizacaoVO.getItemSolicitacaoExames().getUnidadeFuncional() != null ? String.valueOf(itemContratualizacaoVO.getItemSolicitacaoExames().getUnidadeFuncional().getSeq()) : itemContratualizacaoVO.getUnidadeFuncional());
		} else {
			item.setMaterialAnalise("");
			item.setSiglaExame("");
			item.setUnidadeExecutora("");
		}
		item.setMensagemErro(itemContratualizacaoVO.getMensagemErro());
		item.setDataNumeroUnico("");
		item.setIdAmostra("");
		item.setNumeroUnico("");
		return item;
	}

	private String getDataHoraAtualFormatada() {
		return getDataHoraFormatada(new Date());
	}

	private String getDataHoraFormatada(Date data) {
		String retorno = "";
		if (data != null) {
			DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
			return dateFormat.format(data);
		}
		return retorno;
	}

	private String getDataFormatada(Date data) {
		String retorno = "";
		if (data != null) {
			DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
			retorno = dateFormat.format(data);
		}
		return retorno;
	}

	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public List<Item> invalidarItensSolicitacao(SolicitacaoExame solicitacaoExame, String mensagemErro) {
		List<Item> listaItensInvalidados = new ArrayList<Item>();

		if (solicitacaoExame != null && solicitacaoExame.getItens() != null 
				&& solicitacaoExame.getItens().getItem() != null 
				&& !solicitacaoExame.getItens().getItem().isEmpty()) {
			for (br.gov.mec.aghu.exames.contratualizacao.util.Item item : solicitacaoExame.getItens().getItem()) {
				Item itemRetorno = new Item();

				itemRetorno.setIdExterno(item.getIdExterno());
				itemRetorno.setSiglaExame(item.getSiglaExame());
				itemRetorno.setMaterialAnalise(String.valueOf(item.getMaterialAnalise()));
				itemRetorno.setUnidadeExecutora(String.valueOf(item.getUnidadeExecutora()));
				itemRetorno.setMensagemErro(mensagemErro);
				itemRetorno.setDataNumeroUnico("");
				itemRetorno.setIdAmostra("");
				itemRetorno.setNumeroUnico("");
				logInfo("GravacaoArquivoXmlRetornoON.invalidarItensSolicitacao(): Item a ser gravado no XML = [" + item + "].");
				listaItensInvalidados.add(itemRetorno);
			}
		}

		return listaItensInvalidados;
	}

}
