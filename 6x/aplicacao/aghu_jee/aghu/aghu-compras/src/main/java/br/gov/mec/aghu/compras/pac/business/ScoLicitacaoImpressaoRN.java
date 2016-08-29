package br.gov.mec.aghu.compras.pac.business;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.swing.text.MaskFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLoteLicitacaoDAO;
import br.gov.mec.aghu.compras.pac.vo.AutorizacaoLicitacaoEnviadaVO;
import br.gov.mec.aghu.compras.pac.vo.AutorizacaoLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.EmpresasHomologadasLoteLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.EstadoLoteLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.FasesLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.ItemLicitacaoCabecalhoVO;
import br.gov.mec.aghu.compras.pac.vo.ItensLicitacaoCadastradaVO;
import br.gov.mec.aghu.compras.pac.vo.ItensLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.LotesLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.MensagensSalaDisputaVO;
import br.gov.mec.aghu.compras.pac.vo.PropostaItemLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.PropostaLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.PropostaLoteLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.RegistroInteressadosVO;
import br.gov.mec.aghu.compras.pac.vo.RegistroLancesLoteDisputaPregaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoCadastradaImpressaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoImpressaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoVO;
import br.gov.mec.aghu.compras.pac.vo.SelecaoFornecedorParticipanteVO;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoLicitacaoImpressaoRN extends BaseBusiness {
	
	
	private static final char PONTO = '.';

	private static final String ISO_8859_1 = "ISO-8859-1";

	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	
	private static final String DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
	
	private static final String DD_MM_YYYY_HH_MM_SS_SS = "dd/MM/yyyy HH:mm:ss:SS";

	private static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd-hh.mm.ss";

	private static final String YYYY_MM_DD_HH_MM_SS_SSSSSS = "yyyy-MM-dd-hh.mm.ss.SS";

	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoLoteLicitacaoDAO scoLoteLicitacaoDAO;
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;	
	
	private static final Log LOG = LogFactory.getLog(ScoLicitacaoImpressaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = -5922834917785524834L;
	
	enum ScoLicitacaoImpressaoRNExceptionCode implements BusinessExceptionCode {
		PROBLEMA_ARQUIVO_DIRETORIO, ERRO_NAO_ESPERADO, SEM_ARQUIVO_VINCULADO, SELECIONE_UMA_LICITACAO, LICITACAO_NAO_PUBLICADA, CONS_ENVIADA_LICITACAO_NAO_PUBLICADA, SELECIONE_UMA_LICITACAO_CADASTRADA;
	}	

	/**
	 * Busca o arquivo no diretório e ler.
	 * @return List<ScoLicitacaoImpressaoVO>
	 * @throws ApplicationBusinessException
	 * @throws ParseException
	 */
	public List<ScoLicitacaoImpressaoVO> imprimirHistoricoDetalhadoLicitacaoHomologada(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException {
		
		try {
			String diretorioRetorno = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_PG_BB_DIR_RET_PROC);
			List<ScoLicitacaoImpressaoVO> lista = new ArrayList<ScoLicitacaoImpressaoVO>();

			if (diretorioRetorno != null){
				
				File file = new File(diretorioRetorno+File.separator+nomeArquivoRetorno);
				List<String> linhasArquivo;
				linhasArquivo = FileUtils.readLines(file,ISO_8859_1);
				
				ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO = inicializarVariaveis();
				Map<String, ItemLicitacaoCabecalhoVO> cabecalho05 = new TreeMap<String, ItemLicitacaoCabecalhoVO>();
				
				processarCondicoes(numPac, lista, linhasArquivo,
						scoLicitacaoImpressaoVO, cabecalho05);
			}
			
			return lista;
			
		} catch (ParseException e) {
			throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.ERRO_NAO_ESPERADO);
		} catch (IOException e) {
			throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.PROBLEMA_ARQUIVO_DIRETORIO);
		}
	}
	
	private void processarCondicoes(Integer numPac, List<ScoLicitacaoImpressaoVO> lista, List<String> linhasArquivo, ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, Map<String, ItemLicitacaoCabecalhoVO> cabecalho05)
			throws ParseException {
		boolean passouCabecalho02 = false;
		String numLicitacao = StringUtils.EMPTY;
		Integer header;
		
		for (String linha : linhasArquivo) {
			try {
				if (linha.length() >=2 ) {
					String headerTipo = linha.substring(0, 02);
					if (CoreUtil.isNumeroInteger(headerTipo)) {
						header = Integer.valueOf(headerTipo);
						switch (header) {
						case 0:
							processarCabecalhoTipo0(scoLicitacaoImpressaoVO, linha);
							break;
						case 1:
							if (numPac.equals(Integer.valueOf(linha.substring(301, 307)))) {
								numLicitacao = linha.substring(02, 11);
								processarCabecalhoTipo01(scoLicitacaoImpressaoVO, linha);
							}
							break;
						case 2:
							if (!passouCabecalho02 && StringUtils.isNotBlank(numLicitacao)) {
								processarCabecalhoTipo02(scoLicitacaoImpressaoVO, linha);
							}
							break;
						case 3:
							if (checarLicitacao(numLicitacao, linha)) {
								passouCabecalho02 = true;
								processarCabecalhoTipo03(scoLicitacaoImpressaoVO, linha);
							}
							break;
						case 4:
							if (checarLicitacao(numLicitacao, linha)) {
								processarCabecalhoTipo04(scoLicitacaoImpressaoVO, linha);
							}
							break;
						case 5:
							if (checarLicitacao(numLicitacao, linha)) {
								processarCabecalhoTipo05(cabecalho05, linha);
							}
							break;
						case 6:
							if (checarLicitacao(numLicitacao, linha)) {
								processarCabecalhoTipo06(scoLicitacaoImpressaoVO, linha);
							}
							break;
						default:
							processarOutrasCondicoes(header, linha, numLicitacao, scoLicitacaoImpressaoVO, cabecalho05);
							break;
						}
					}
				}
			} catch (StringIndexOutOfBoundsException outBoundsEx) {
				LOG.warn("Linha incompleta no arquivo | Conteúdo: " + linha);
			}
		}
		scoLicitacaoImpressaoVO.setListaItensLicitacaoCabecalho(new ArrayList<ItemLicitacaoCabecalhoVO>(cabecalho05.values()));
		lista.add(scoLicitacaoImpressaoVO);
	}
	
	private void processarOutrasCondicoes(Integer header, String linha,	String numLicitacao, ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, Map<String, ItemLicitacaoCabecalhoVO> cabecalho05)
			throws ParseException {
		// Condições dividida em dois métodos para viabilizar PMD.

		switch (header) {
		case 7:
			if (checarLicitacao(numLicitacao, linha)) {
				processarCabecalhoTipo07(scoLicitacaoImpressaoVO, linha);
			}
			break;
		case 8:
			if (checarLicitacao(numLicitacao, linha)) {

				processarCabecalhoTipo08(cabecalho05, linha);
			}
			break;
		case 9:
			if (checarLicitacao(numLicitacao, linha)) {
				processarCabecalhoTipo09(cabecalho05, linha);
			}
			break;
		case 10:
			if (checarLicitacao(numLicitacao, linha)) {
				processarCabecalhoTipo10(cabecalho05, linha);
			}
			break;
		case 12:
			if (checarLicitacao(numLicitacao, linha)) {
				processarCabecalhoTipo12(scoLicitacaoImpressaoVO, linha);
			}
			break;
		case 13:
			if (checarLicitacao(numLicitacao, linha)) {
				processarCabecalhoTipo13(scoLicitacaoImpressaoVO, linha);
			}
			break;
		case 15:
			if (checarLicitacao(numLicitacao, linha)) {
				processarCabecalhoTipo15(scoLicitacaoImpressaoVO, linha);
			}
			break;
		default:
			break;
		}

	}

	private ScoLicitacaoImpressaoVO inicializarVariaveis() {
		ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO = new ScoLicitacaoImpressaoVO();	
		scoLicitacaoImpressaoVO.setListaFasesLicitacao(new ArrayList<FasesLicitacaoVO>());
		scoLicitacaoImpressaoVO.setListaEmpresasHomologadasLoteLicitacao(new ArrayList<EmpresasHomologadasLoteLicitacaoVO>());
		scoLicitacaoImpressaoVO.setListaEstadoLoteLicitacao(new ArrayList<EstadoLoteLicitacaoVO>());
		scoLicitacaoImpressaoVO.setListaAutorizacoesLicitacao(new ArrayList<AutorizacaoLicitacaoVO>());
		scoLicitacaoImpressaoVO.setListaPropostaLicitacao(new ArrayList<PropostaLicitacaoVO>());
		scoLicitacaoImpressaoVO.setListaRegistroLancesLoteDisputaPregao(new ArrayList<RegistroLancesLoteDisputaPregaoVO>());
		scoLicitacaoImpressaoVO.setListaMensagensSalaDisputa(new ArrayList<MensagensSalaDisputaVO>());
		scoLicitacaoImpressaoVO.setListaRegistroInteressados(new ArrayList<RegistroInteressadosVO>());
		return scoLicitacaoImpressaoVO;
	}

	private boolean checarLicitacao(String numLicitacao, String linha) {
		return numLicitacao.equals(linha.substring(02, 11));
	}

	private void processarCabecalhoTipo15(ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, String linha) throws ParseException {
		RegistroInteressadosVO cabecalhoTipo15 = new RegistroInteressadosVO();
		cabecalhoTipo15.setCnpjTipo15(linha.substring(20, 34));
		cabecalhoTipo15.setNomeFornecedorTipo15(linha.substring(64, 114));
		cabecalhoTipo15.setRegistroDeInteresse(formatarDataHora(linha.substring(114, 140)));
		cabecalhoTipo15.setTipodeInteresse(concatenarCodigoDescricao(linha.substring(140, 144), linha.substring(144, 194)));	
		scoLicitacaoImpressaoVO.getListaRegistroInteressados().add(cabecalhoTipo15);
	}

	private void processarCabecalhoTipo13(ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, String linha) throws ParseException {
		MensagensSalaDisputaVO cabecalhoTipo13 = new MensagensSalaDisputaVO();
		cabecalhoTipo13.setEnvioMensagem(formatarDataHoraMinMil(linha.substring(11, 37)));
		cabecalhoTipo13.setLoteTipo13(linha.substring(41, 45));
		cabecalhoTipo13.setEmitente(concatenarCodigoDescricao(linha.substring(45, 49), linha.substring(49, 69)));
		cabecalhoTipo13.setNomeFornecedorTipo13(linha.substring(122, 172));
		cabecalhoTipo13.setMensagem(concatenarCodigoDescricao(linha.substring(172, 176), linha.substring(176, 426)));	        		  
		scoLicitacaoImpressaoVO.getListaMensagensSalaDisputa().add(cabecalhoTipo13);
	}

	private void processarCabecalhoTipo12(
			ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, String linha)
			throws ParseException {
		RegistroLancesLoteDisputaPregaoVO cabecalhoTipo12 = new RegistroLancesLoteDisputaPregaoVO();
		cabecalhoTipo12.setLoteTipo12(linha.substring(11, 15));
		cabecalhoTipo12.setNumeroItem(linha.substring(15,19));
		cabecalhoTipo12.setCnpjTipo12(linha.substring(54, 68));
		cabecalhoTipo12.setNomeFornecedorTipo12(linha.substring(98, 148));
		cabecalhoTipo12.setEntregaLance(formatarDataHoraMinMil(linha.substring(19, 45)));
		cabecalhoTipo12.setValor(formatarValor(BigDecimal.valueOf(Long.valueOf(linha.substring(149, 165).trim()),2), 2));
		cabecalhoTipo12.setLanceCancelado(linha.substring(165, 166));
		scoLicitacaoImpressaoVO.getListaRegistroLancesLoteDisputaPregao().add(cabecalhoTipo12);
	}

	private void processarCabecalhoTipo10(Map<String, ItemLicitacaoCabecalhoVO> cabecalho05, String linha) throws ParseException {
		ItemLicitacaoCabecalhoVO itemCabecalho;
		if (!cabecalho05.containsKey(linha.substring(11, 15))) {
			itemCabecalho = new ItemLicitacaoCabecalhoVO();
			itemCabecalho.setLoteTipo5(linha.substring(11, 15));
			cabecalho05.put(linha.substring(11, 15), itemCabecalho);
		} else {
			itemCabecalho = cabecalho05.get(linha.substring(11, 15));
		}
		
		SelecaoFornecedorParticipanteVO cabecalhoTipo10 = new SelecaoFornecedorParticipanteVO();
		cabecalhoTipo10.setCnpjTipo10(linha.substring(24, 38));
		cabecalhoTipo10.setNomeFornecedorTipo10(linha.substring(68, 118));
		cabecalhoTipo10.setAceitacaoPartForn(formatarDataHoraMinMil(linha.substring(122, 148)));
		
		itemCabecalho.getListaTipo10().add(cabecalhoTipo10);
	}

	private void processarCabecalhoTipo09(Map<String, ItemLicitacaoCabecalhoVO> cabecalho05, String linha) {
		ItemLicitacaoCabecalhoVO itemCabecalho;
		if (!cabecalho05.containsKey(linha.substring(11, 15))) {
			itemCabecalho = new ItemLicitacaoCabecalhoVO();
			itemCabecalho.setLoteTipo5(linha.substring(11, 15));
			cabecalho05.put(linha.substring(11, 15), itemCabecalho);
		} else {
			itemCabecalho = cabecalho05.get(linha.substring(11, 15));
		}
		
		PropostaItemLicitacaoVO cabecalhoTipo9 = new PropostaItemLicitacaoVO();
		cabecalhoTipo9.setItemTipo9(linha.substring(15, 19));
		cabecalhoTipo9.setCnpjTipo9(linha.substring(28, 42));
		cabecalhoTipo9.setNomeFornecedorTipo9(linha.substring(72, 122));
		// formata o valor unitário para 4 casas decimais - somente esse campo do arquivo tem essa necessidade
		// DecimalFormat df = new DecimalFormat(",##0.0000");		
		
		//cabecalhoTipo9.setValorUnitario(df.format(BigDecimal.valueOf(Long.valueOf(linha.substring(122, 137).trim()),4)));
		cabecalhoTipo9.setValorUnitario(formatarValor((BigDecimal.valueOf(Long.valueOf(linha.substring(122, 137).trim()),4)), 4));
		
		itemCabecalho.getListaTipo09().add(cabecalhoTipo9);
	}

	private void processarCabecalhoTipo08(Map<String, ItemLicitacaoCabecalhoVO> cabecalho05, String linha) throws ParseException {
		ItemLicitacaoCabecalhoVO itemCabecalho;
		if (!cabecalho05.containsKey(linha.substring(11, 15))) {
			itemCabecalho = new ItemLicitacaoCabecalhoVO();
			itemCabecalho.setLoteTipo5(linha.substring(11, 15));
			cabecalho05.put(linha.substring(11, 15), itemCabecalho);
		} else {
			itemCabecalho = cabecalho05.get(linha.substring(11, 15));
		}
		
		PropostaLoteLicitacaoVO cabecalhoTipo8 = new PropostaLoteLicitacaoVO();
		cabecalhoTipo8.setCnpjTipo8(linha.substring(24, 38));
		cabecalhoTipo8.setNomeFornecedorTipo8(linha.substring(68, 118));
		cabecalhoTipo8.setDataDesclassificacao(formatarDataHoraMinMil(linha.substring(172, 198)));
		cabecalhoTipo8.setMotivoDesclassificacao(concatenarCodigoDescricao(linha.substring(118, 122), linha.substring(122, 172)));
		
		itemCabecalho.getListaTipo08().add(cabecalhoTipo8);
	}

	private void processarCabecalhoTipo07(ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, String linha) throws ParseException {
		PropostaLicitacaoVO cabecalhoTipo7 = new PropostaLicitacaoVO();
		cabecalhoTipo7.setCnpjTipo7(linha.substring(96, 110));
		cabecalhoTipo7.setNomeTipo7(linha.substring(20, 70));
		cabecalhoTipo7.setBairroTipo7(linha.substring(240, 261).trim());
		cabecalhoTipo7.setEntregaDaProposta(conversorData(linha.substring(70,96)));
		String dddContato = removerZeroAEsqueda(linha.substring(551, 555));
		String numeroTelefoneContato = removerZeroAEsqueda(linha.substring(640, 652));
		String telefone = formatadorCampos(dddContato + numeroTelefoneContato);
		cabecalhoTipo7.setDddContato(dddContato);
		cabecalhoTipo7.setNumeroTelefoneContato(numeroTelefoneContato);
		cabecalhoTipo7.setTelefone(telefone);
		cabecalhoTipo7.setContato(linha.substring(501, 551));
		cabecalhoTipo7.setEmail(linha.substring(401, 501));	        		  
		scoLicitacaoImpressaoVO.getListaPropostaLicitacao().add(cabecalhoTipo7);
	}

	private String formatadorCampos(String valor) throws ParseException {
		MaskFormatter mask = new MaskFormatter("(##) ####-####");
		mask.setValueContainsLiteralCharacters(false);
		String telefone = mask.valueToString(valor);
		return telefone;
	}

	private void processarCabecalhoTipo06(ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, String linha) throws ParseException {
		AutorizacaoLicitacaoVO cabecalhoTipo6 = new AutorizacaoLicitacaoVO(); 	        		  
		cabecalhoTipo6.setInicioAutorizacao(formatarDataHoraMinMil(linha.substring(11, 37)));															
		cabecalhoTipo6.setFimAutorizacao(formatarDataHoraMinMil(linha.substring(150, 176)));
		cabecalhoTipo6.setExecutante(concatenarCodigoDescricao(linha.substring(38, 46), linha.substring(46, 96)));
		cabecalhoTipo6.setPerfil(concatenarCodigoDescricao(linha.substring(96, 100), linha.substring(100, 150)));	        		  
		scoLicitacaoImpressaoVO.getListaAutorizacoesLicitacao().add(cabecalhoTipo6);
	}

	private void processarCabecalhoTipo05(Map<String, ItemLicitacaoCabecalhoVO> cabecalho05, String linha) {
		ItemLicitacaoCabecalhoVO itemCabecalho;
		if (!cabecalho05.containsKey(linha.substring(11, 15))) {
			itemCabecalho = new ItemLicitacaoCabecalhoVO();
			itemCabecalho.setLoteTipo5(linha.substring(11, 15));
			cabecalho05.put(linha.substring(11, 15), itemCabecalho);
		} else {
			itemCabecalho = cabecalho05.get(linha.substring(11, 15));
		}
		
		ItensLicitacaoVO cabecalhoTipo5 = new ItensLicitacaoVO();		
		cabecalhoTipo5.setItem(linha.substring(15, 19));
		cabecalhoTipo5.setQuantidade(linha.substring(532, 547));
		cabecalhoTipo5.setMercadoria(concatenarCodigoDescricao(linha.substring(19, 28), linha.substring(28, 278)));
		cabecalhoTipo5.setProduto(concatenarCodigoDescricao(linha.substring(278, 282), linha.substring(282, 532)));
		
		itemCabecalho.getListaTipo05().add(cabecalhoTipo5);
	}

	private void processarCabecalhoTipo04(ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, String linha) throws ParseException {
		EstadoLoteLicitacaoVO cabecalhoTipo4 = new EstadoLoteLicitacaoVO();	        		  
		cabecalhoTipo4.setLoteTipo4(linha.substring(11, 15));
		cabecalhoTipo4.setInicioEstadoPregao(formatarDataHoraMinMil(linha.substring(15, 41)));
		cabecalhoTipo4.setFimEstadoPregao(formatarDataHoraMinMil(linha.substring(95, 121)));
		cabecalhoTipo4.setEstadoLoteDoPregao(concatenarCodigoDescricao(linha.substring(41, 45), linha.substring(45, 95)));	        		  
		scoLicitacaoImpressaoVO.getListaEstadoLoteLicitacao().add(cabecalhoTipo4);
	}

	private void processarCabecalhoTipo03(ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, String linha) {
		EmpresasHomologadasLoteLicitacaoVO cabecalhoTipo3 = new EmpresasHomologadasLoteLicitacaoVO(); 
		cabecalhoTipo3.setLoteTipo3(linha.substring(11, 15));
		cabecalhoTipo3.setValorLote(formatarValor(BigDecimal.valueOf(Long.valueOf(linha.substring(15, 32).trim()),2), 2));
		cabecalhoTipo3.setTempoLance(linha.substring(32, 36));
		cabecalhoTipo3.setTempoDisputa(linha.substring(36, 40));
		cabecalhoTipo3.setCnpjTipo3(linha.substring(49, 63));
		cabecalhoTipo3.setNomeFornecedorTipo3(linha.substring(93, 143));	        		  
		scoLicitacaoImpressaoVO.getListaEmpresasHomologadasLoteLicitacao().add(cabecalhoTipo3);
	}

	private void processarCabecalhoTipo02(ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, String linha) throws ParseException {
		FasesLicitacaoVO cabecalhoTipo2 = new FasesLicitacaoVO(); 
		cabecalhoTipo2.setInicioEstadoLicitacao(conversorData(linha.substring(11,37)));
		cabecalhoTipo2.setFimEstadoLicitacao(conversorData(linha.substring(91,117))); 
		cabecalhoTipo2.setEstadoLicitacao(concatenarCodigoDescricao(linha.substring(37, 41), linha.substring(41, 91)));
		cabecalhoTipo2.setComplemento(linha.substring(117, 367));
		scoLicitacaoImpressaoVO.getListaFasesLicitacao().add(cabecalhoTipo2);
	}
	
	private void processarCabecalhoTipo01 (ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, String linha) throws ParseException {
		scoLicitacaoImpressaoVO.setLicitacao(removerZeroAEsqueda(linha.substring(02, 11)));
		scoLicitacaoImpressaoVO.setProcesso(linha.substring(301, 307));
		scoLicitacaoImpressaoVO.setUnidOrg(concatenarCodigoDescricao(linha.substring(70, 79), linha.substring(79, 129)));
		scoLicitacaoImpressaoVO.setMoeda(concatenarCodigoDescricao(linha.substring(237, 241), linha.substring(241, 291)));
		scoLicitacaoImpressaoVO.setModlLicit(concatenarCodigoDescricao(linha.substring(129, 133), linha.substring(133, 183)));
		scoLicitacaoImpressaoVO.setEdital(linha.substring(291, 301));
		scoLicitacaoImpressaoVO.setTipo(concatenarCodigoDescricao(linha.substring(183, 187), linha.substring(187, 237)));		        		  
		scoLicitacaoImpressaoVO.setDtPubl(new StringBuilder(linha.substring(321,331).replace(".", "/")).toString());
		
		scoLicitacaoImpressaoVO.setDtIniEntgProp(conversorData(linha.substring(331,357)));
		scoLicitacaoImpressaoVO.setDtFimEntgProp(conversorData(linha.substring(357,383)));
		scoLicitacaoImpressaoVO.setDataAbertProp(conversorData(linha.substring(383,409)));
		scoLicitacaoImpressaoVO.setDataAbertProp(conversorData(linha.substring(383,409)));
		scoLicitacaoImpressaoVO.setDataIniPregao(conversorData(linha.substring(409,435)));
		
		scoLicitacaoImpressaoVO.setPrazoRecurso(linha.substring(435, 439));
		scoLicitacaoImpressaoVO.setIdioma(concatenarCodigoDescricao(linha.substring(493, 497), linha.substring(497, 547)));
		scoLicitacaoImpressaoVO.setFormaPartic(concatenarCodigoDescricao(linha.substring(439, 443), linha.substring(443, 493)));
		scoLicitacaoImpressaoVO.setDescricao(linha.substring(547, 1547));
	}

	private void processarCabecalhoTipo0(ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO, String linha) {
		scoLicitacaoImpressaoVO.setDataGeracao(new StringBuilder(linha.substring(19,27)).insert(2, "/").insert(5, "/").toString());
		scoLicitacaoImpressaoVO.setCodigoCliente(linha.substring(29,38));
		scoLicitacaoImpressaoVO.setCodigoIdentificador(removerZeroAEsqueda(linha.substring(40, 49)));
		scoLicitacaoImpressaoVO.setIed(removerZeroAEsqueda(linha.substring(51, 60)));
	}

	private String concatenarCodigoDescricao(String codigo, String descricao) {
		
		if(StringUtils.isNotBlank(codigo) && StringUtils.isBlank(descricao)){
			return removerZeroAEsqueda(codigo.trim());
		}else if(StringUtils.isBlank(codigo) && StringUtils.isNotBlank(descricao)){
			return descricao.trim();
		}

		if(StringUtils.isNotBlank(codigo) && StringUtils.isNotBlank(descricao)){
			return new StringBuilder(removerZeroAEsqueda(codigo)).append(" - ").append(descricao.trim()).toString();
		}
		
		return null; 
	}

	private String conversorData(String linha) throws ParseException {
		if(StringUtils.isNotBlank(linha)){
			StringBuilder resultado = new StringBuilder(linha).delete(linha.lastIndexOf(PONTO), linha.length());
			return new SimpleDateFormat(DD_MM_YYYY).format(DateUtils.parseDate(resultado.toString(), YYYY_MM_DD_HH_MM_SS_SSS));
		}
		return null;
	}	
	
	private String formatarDataHora(String linha) throws ParseException {
		if(StringUtils.isNotBlank(linha)){
			StringBuilder resultado = new StringBuilder(linha).delete(linha.lastIndexOf(PONTO), linha.length());
			return new SimpleDateFormat(DD_MM_YYYY_HH_MM).format(DateUtils.parseDate(resultado.toString(), YYYY_MM_DD_HH_MM_SS_SSS));
			
		}
		return null;
	}
	
	private String formatarDataHoraMinMil(String linha) throws ParseException {
		if(StringUtils.isNotBlank(linha)){
			StringBuilder resultado = new StringBuilder(linha).delete(linha.length()-4, linha.length());
			return new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS_SS).format(DateUtils.parseDate(resultado.toString(), YYYY_MM_DD_HH_MM_SS_SSSSSS));		
		}
		return null;
	}
	
	/**
	 * Formata uma string informando o número de casas decimais. Valor padrão é duas casas.
	 * 
	 * @param valorDecimal Valor que se quer formatar
	 * @param precisao Número de casas decimais
	 * @return Número formatado (String)
	 */
	private String formatarValor(BigDecimal valorDecimal, int precisao) {
		DecimalFormat df = null;
		
		if (precisao == 6) {
			df = new DecimalFormat(",##0.000000");			
		} else if (precisao == 4) {
			df = new DecimalFormat(",##0.0000");
		} else {
			df = new DecimalFormat(",##0.00");
		} 
		
		return df.format(valorDecimal);
	}
	
	private String removerZeroAEsqueda(String valor) {
		Integer valorNumerico = Integer.valueOf(valor.trim());
		return valorNumerico.toString();
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void validarSolicitacaoPregaoEletronico(List<ScoLicitacaoVO> licitacoesSelecionadas) throws ApplicationBusinessException {
		//verifica se foi selecionado mais de um registro ou nenhum registro
		if (licitacoesSelecionadas == null || licitacoesSelecionadas.isEmpty() || licitacoesSelecionadas.size() != 1) {
			throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.SELECIONE_UMA_LICITACAO);
		}else{
			//verifica se existe o número do lote.
			for (ScoLicitacaoVO item : licitacoesSelecionadas) {
				List<Long> loteLicitacaoSelecionada = scoLoteLicitacaoDAO.obterLoteLicitacaoSelecionada(item.getNumeroPAC());
				if (loteLicitacaoSelecionada.isEmpty()){
					 throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.LICITACAO_NAO_PUBLICADA);
				 }
			 }
		 }
		
		//verifica se a coluna Nome Arq. Retorno está vazia
		if (licitacoesSelecionadas != null && !licitacoesSelecionadas.isEmpty() && licitacoesSelecionadas.get(0).getNomeArqRetorno() == null) {
			throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.SEM_ARQUIVO_VINCULADO);
		}
	}
	
	public String validarNomeArqCad(List<ScoLicitacaoVO> licitacoesSelecionadas) throws ApplicationBusinessException {
		//verifica se foi selecionado mais de um registro ou nenhum registro
		if (licitacoesSelecionadas == null || licitacoesSelecionadas.isEmpty() || licitacoesSelecionadas.size() != 1) {
			throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.SELECIONE_UMA_LICITACAO_CADASTRADA);
		}else{
			//verifica se existe o número do lote. EXECUTA C2
			for (ScoLicitacaoVO item : licitacoesSelecionadas) {
				List<Long> loteLicitacaoSelecionada = scoLoteLicitacaoDAO.obterLoteLicitacaoSelecionada(item.getNumeroPAC());
				if (loteLicitacaoSelecionada.isEmpty()){
					 throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.CONS_ENVIADA_LICITACAO_NAO_PUBLICADA);
				 }
			 }
		}
		for (ScoLicitacaoVO item : licitacoesSelecionadas) {
			ScoLicitacao licitacaoSelecionada = scoLicitacaoDAO.obterNomeArqRetorno(item.getNumeroPAC());
			if (licitacaoSelecionada.getNomeArqCad() == null){
				 throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.SEM_ARQUIVO_VINCULADO);
			}
			return licitacaoSelecionada.getNomeArqCad();
		 }
		return null;
	}
	
	public List<ScoLicitacaoCadastradaImpressaoVO> imprimirLicitacaoCadastrada(Integer numPac, String nomeArquivoCad) throws ApplicationBusinessException {
		
		try {
			String diretorioRetorno = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_PG_BB_DIR_RET_PROC);
			List<ScoLicitacaoCadastradaImpressaoVO> lista = new ArrayList<ScoLicitacaoCadastradaImpressaoVO>();

			if (diretorioRetorno != null){
				
				File file = new File(diretorioRetorno+File.separator+nomeArquivoCad);
				List<String> linhasArquivo = FileUtils.readLines(file,ISO_8859_1);
				
				ScoLicitacaoCadastradaImpressaoVO scoLicitacaoCadastradaImpressaoVO = inicializarVariaveisLicitacaoCadastrada();
				Map<String, LotesLicitacaoVO> cabecalho0304 = new TreeMap<String, LotesLicitacaoVO>();
				
				processarCondicoesLicitacaoCadastrada(numPac, lista, linhasArquivo,	scoLicitacaoCadastradaImpressaoVO, cabecalho0304);
			}
			
			return lista;
			
		} catch (ParseException e) {
			throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.ERRO_NAO_ESPERADO);
		} catch (IOException e) {
			throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.PROBLEMA_ARQUIVO_DIRETORIO);
		}
	}
	
	private ScoLicitacaoCadastradaImpressaoVO inicializarVariaveisLicitacaoCadastrada() {
		ScoLicitacaoCadastradaImpressaoVO scoLicitacaoCadastradaImpressaoVO = new ScoLicitacaoCadastradaImpressaoVO();	
		scoLicitacaoCadastradaImpressaoVO.setListaAutorizacoesLicitacao(new ArrayList<AutorizacaoLicitacaoEnviadaVO>());
		scoLicitacaoCadastradaImpressaoVO.setListaLotesLicitacao(new ArrayList<LotesLicitacaoVO>());		
		return scoLicitacaoCadastradaImpressaoVO;
	}
	
	private void processarCondicoesLicitacaoCadastrada(Integer numPac, List<ScoLicitacaoCadastradaImpressaoVO> lista, List<String> linhasArquivo, 
			ScoLicitacaoCadastradaImpressaoVO scoLicitacaoCadastradaImpressaoVO,
			Map<String, LotesLicitacaoVO> cabecalho0304) throws ParseException {
		String numLicitacao = StringUtils.EMPTY;
		String numPacArq = StringUtils.EMPTY;
		Integer header;		
		for (String linha : linhasArquivo) {
			header = Integer.valueOf(linha.substring(0, 02));
			switch (header) {
			case 0:
					processarCabecalhoLicitacaoCadastradaTipo00(scoLicitacaoCadastradaImpressaoVO, linha);
				break;
			case 1:
				if (numPac.equals(Integer.valueOf(linha.substring(51, 71).trim()))) {
					numLicitacao = linha.substring(02, 11);
					numPacArq = linha.substring(51, 71).trim();
					processarCabecalhoLicitacaoCadastradaTipo01(scoLicitacaoCadastradaImpressaoVO, linha);				
				}else{
					numPacArq = linha.substring(51, 71).trim();
				}
				break;
			case 2:
				if (checarLicitacaoCadastrada(numLicitacao, linha, numPac, numPacArq)) {
					processarCabecalhoLicitacaoCadastradaTipo02(scoLicitacaoCadastradaImpressaoVO, linha);
				}
				break;		
			case 7:
				if (checarLicitacaoCadastrada(numLicitacao, linha, numPac, numPacArq)) {
					processarCabecalhoLicitacaoCadastradaTipo07(scoLicitacaoCadastradaImpressaoVO, linha);
				}
				break;
			case 3:
				if (checarLicitacaoCadastrada(numLicitacao, linha, numPac, numPacArq)) {
					processarCabecalhoLicitacaoCadastradaTipo0304(cabecalho0304, linha);
				}
				break;
			case 4:
				if (checarLicitacaoCadastrada(numLicitacao, linha, numPac, numPacArq)) {
					processarCabecalhoLicitacaoCadastradaTipo0304(cabecalho0304, linha);
				}
				break;
			case 5:
				if (checarLicitacaoCadastrada(numLicitacao, linha, numPac, numPacArq)) {
					processarCabecalhoLicitacaoCadastradaTipo0506(cabecalho0304, linha);
				}
				break;
			case 6:
				if (checarLicitacaoCadastrada(numLicitacao, linha, numPac, numPacArq)) {
					processarCabecalhoLicitacaoCadastradaTipo0506(cabecalho0304, linha);
				}
				break;
			default:				
				break;
			}
		}
		scoLicitacaoCadastradaImpressaoVO.setListaLotesLicitacao(new ArrayList<LotesLicitacaoVO>(cabecalho0304.values()));
		lista.add(scoLicitacaoCadastradaImpressaoVO);
	}
	
	private void processarCabecalhoLicitacaoCadastradaTipo00(ScoLicitacaoCadastradaImpressaoVO scoLicitacaoCadastradaImpressaoVO, String linha) {
		scoLicitacaoCadastradaImpressaoVO.setDataGeracao(new StringBuilder(linha.substring(19,27)).insert(2, "/").insert(5, "/").toString());		
		scoLicitacaoCadastradaImpressaoVO.setCodigoCliente(linha.substring(29,38));		
		scoLicitacaoCadastradaImpressaoVO.setCodigoIdentificador(removerZeroAEsqueda(linha.substring(41, 49)));		
		scoLicitacaoCadastradaImpressaoVO.setIed(removerZeroAEsqueda(linha.substring(52, 60)));
	}
	
	private void processarCabecalhoLicitacaoCadastradaTipo01(ScoLicitacaoCadastradaImpressaoVO scoLicitacaoCadastradaImpressaoVO, String linha) throws ParseException {
		scoLicitacaoCadastradaImpressaoVO.setLicitacao(linha.substring(2,11));
		scoLicitacaoCadastradaImpressaoVO.setProcesso(linha.substring(51,71));
		scoLicitacaoCadastradaImpressaoVO.setIdentificadorLicitacaoBB(linha.substring(247,256));
		scoLicitacaoCadastradaImpressaoVO.setDtPubl((linha.substring(71,81)).replace(".", "/")); 
		scoLicitacaoCadastradaImpressaoVO.setDtIniEntgProp(conversorData(linha.substring(81,107)));
		scoLicitacaoCadastradaImpressaoVO.setDtFimEntgProp(conversorData(linha.substring(107,133)));
		scoLicitacaoCadastradaImpressaoVO.setDataAbertProp(conversorData(linha.substring(133,159)));
		scoLicitacaoCadastradaImpressaoVO.setDataIniPregao(conversorData(linha.substring(159,185)));
		scoLicitacaoCadastradaImpressaoVO.setOcorrencia(linha.substring(198,247) + " | ");//complementar com dados do cabeçalho 02.
	}
	
	private void processarCabecalhoLicitacaoCadastradaTipo02(ScoLicitacaoCadastradaImpressaoVO scoLicitacaoCadastradaImpressaoVO, String linha) {
		scoLicitacaoCadastradaImpressaoVO.setDescricao(linha.substring(15,1015));
		scoLicitacaoCadastradaImpressaoVO.setOcorrencia(scoLicitacaoCadastradaImpressaoVO.getOcorrencia() + " " + linha.substring(1016,1065));//ler parte desse dado no cabeçalho 01.		
	}
	
	private void processarCabecalhoLicitacaoCadastradaTipo07(ScoLicitacaoCadastradaImpressaoVO scoLicitacaoCadastradaImpressaoVO, String linha) {
		AutorizacaoLicitacaoEnviadaVO cabecalhotipo7 = new AutorizacaoLicitacaoEnviadaVO();
		cabecalhotipo7.setExecutante(removerZeroAEsqueda(linha.substring(12,20)));
		cabecalhotipo7.setPerfil(removerZeroAEsqueda(linha.substring(21,24)));
		cabecalhotipo7.setOcorrencia(linha.substring(25,74));
		scoLicitacaoCadastradaImpressaoVO.getListaAutorizacoesLicitacao().add(cabecalhotipo7);	
	}
	
	private void processarCabecalhoLicitacaoCadastradaTipo0304(Map<String, LotesLicitacaoVO> cabecalho0304, String linha) {
		LotesLicitacaoVO lotesLicitacaoVO;
		if (!cabecalho0304.containsKey(linha.substring(12, 15))) {
			lotesLicitacaoVO = new LotesLicitacaoVO();
			lotesLicitacaoVO.setLote(removerZeroAEsqueda(linha.substring(12, 15)));
			cabecalho0304.put(linha.substring(12, 15), lotesLicitacaoVO);
		} else {
			lotesLicitacaoVO = cabecalho0304.get(linha.substring(12, 15));
			lotesLicitacaoVO.setDescricao(linha.substring(19,1019).trim());
			lotesLicitacaoVO.setOcorrencia(linha.substring(1020,1069).trim());
		}		
	}
	
	private void processarCabecalhoLicitacaoCadastradaTipo0506(Map<String, LotesLicitacaoVO> cabecalho0304, String linha) {
		LotesLicitacaoVO lotesLicitacaoVO = cabecalho0304.get(linha.substring(12, 15));
		if(Integer.valueOf(linha.substring(0, 02)) == 5){			
			ItensLicitacaoCadastradaVO itensLicitacaoCadastradaVO = new ItensLicitacaoCadastradaVO();
			itensLicitacaoCadastradaVO.setItem(removerZeroAEsqueda(linha.substring(16,19)));
			itensLicitacaoCadastradaVO.setMercadoria(linha.substring(19,28));
			itensLicitacaoCadastradaVO.setProduto(linha.substring(28,32));
			itensLicitacaoCadastradaVO.setQuantidade(removerZeroAEsqueda(linha.substring(33,47)));
			lotesLicitacaoVO.setDescricaoOcorrencia(linha.substring(48,97));
			lotesLicitacaoVO.getListaItemLicitacao().add(itensLicitacaoCadastradaVO);			
		}else if(Integer.valueOf(linha.substring(0, 02)) == 6){
			int sizeLista = lotesLicitacaoVO.getListaItemLicitacao().size();			
			lotesLicitacaoVO.getListaItemLicitacao().get(sizeLista-1).setDescricao(linha.substring(23,1023).trim());
			lotesLicitacaoVO.getListaItemLicitacao().get(sizeLista-1).setOcorrencia(linha.substring(1024,1073).trim());
		}		
	}
	
	public List<ScoLicitacaoImpressaoVO> lerArquivoProcessadoLicitacaoHomologada(Integer numPac, String nomeArquivoProcessado) throws ApplicationBusinessException {
		try {
			String diretorioProcessado = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_PG_BB_DIR_RET_PROC);
			List<ScoLicitacaoImpressaoVO> lista = new ArrayList<ScoLicitacaoImpressaoVO>();
			if (diretorioProcessado != null){
				File file = new File(diretorioProcessado+File.separator+nomeArquivoProcessado);
				List<String> linhasArquivo;
				linhasArquivo = FileUtils.readLines(file,ISO_8859_1);
				
				ScoLicitacaoImpressaoVO scoLicitacaoImpressaoVO = inicializarVariaveis();
				Map<String, ItemLicitacaoCabecalhoVO> cabecalho05 = new TreeMap<String, ItemLicitacaoCabecalhoVO>();
				
				processarCondicoes(numPac, lista, linhasArquivo,
						scoLicitacaoImpressaoVO, cabecalho05);
			}
			return lista;
			
		} catch (ParseException e) {
			throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.ERRO_NAO_ESPERADO);
		} catch (IOException e) {
			throw new ApplicationBusinessException(ScoLicitacaoImpressaoRNExceptionCode.PROBLEMA_ARQUIVO_DIRETORIO);
		}
	}
	
	private boolean checarLicitacaoCadastrada(String numLicitacao, String linha, Integer numPac, String numPacArq) {
		return numLicitacao.equals(linha.substring(02, 11)) && numPac.equals(Integer.valueOf(numPacArq));
	}

}