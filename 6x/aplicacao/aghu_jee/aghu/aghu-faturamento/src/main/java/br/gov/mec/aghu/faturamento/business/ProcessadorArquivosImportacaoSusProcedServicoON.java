package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSus.ImportarArquivoSusONExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedServicosDAO;
import br.gov.mec.aghu.faturamento.dao.FatServClassificacoesDAO;
import br.gov.mec.aghu.faturamento.dao.FatServicosDAO;
import br.gov.mec.aghu.faturamento.vo.FatProcedServVO;
import br.gov.mec.aghu.faturamento.vo.FatServClassificacoesVO;
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroServicoVO;
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroServicoVO.TipoProcessado;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedServicos;
import br.gov.mec.aghu.model.FatServClassificacoes;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusProcedServicoON  extends BaseBMTBusiness {
	
	@EJB
	private ProcesArqImportSusProcedServicoServClassON procesArqImportSusProcedServicoServClassON;

	private static final long serialVersionUID = -5611858458159466681L;

	@EJB
	private FatcBuscaServClassRN fatcBuscaServClassRN;

	@EJB
	private FatServClassificacoesRN fatServClassificacoesRN;
	
	@Inject
	private FatServicosDAO fatServicosDAO;

	@EJB
	private FatServicosRN fatServicosRN;
	@EJB
	private FatProcedServicosRN fatProcedServicosRN;

	@EJB
	private ProcesArqImportSusProcedServicoON procesArqImportSusProcedServicoON;

	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@Inject
	private FatServClassificacoesDAO fatServClassificacoesDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@Inject
	private FatProcedServicosDAO fatProcedServicosDAO;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IFaturamentoFacade iFaturamentoFacade;
	
	private static final String msg9 ="Incluindo servico ";
	private static final String msg10 = "Inativando servico/classificacao do procedimento ";
	protected static final int QT_MINIMA_ATUALIZAR_ARQUIVO = 500;

	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusProcedServicoON.class);	
	
	protected enum ProcessadorArquivosImportacaoSusProcedServicoONExceptionCode implements BusinessExceptionCode {
		MAX_CARACTERS_TABELA_SERVICO_PROC_EXCEDIDA, ERRO_ARQUIVO_SERVICO_NAO_ENCONTRADO, ERRO_ARQUIVO_SERV_CLASS_NAO_ENCONTRADO, MAX_CARACTERS_TABELA_PROC_X_SERVICO_EXCEDIDA
	}

	public void atualizarProcedServico(final ControleProcessadorArquivosImportacaoSus controle) {
		final StringBuilder logRetornoArquivoImportacao = new StringBuilder(200);
		
		controle.iniciarLogRetorno();
		controle.setPartial(0);
		
		AghArquivoProcessamento arquivoImportacao = null;
		try { 
		    
			controle.setInicio(new Date());
			
			final String sgFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();
			arquivoImportacao = aberturaOK(sgFaturamento);
			
			if(arquivoImportacao != null){
				
				final Short phoSeq = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
				arquivoImportacao.setDthrInicioProcessamento(controle.getInicio());

				String msg = "Iniciando execução de atualiza_proced_int_servico em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
				controle.gravarLog(msg);
				logRetornoArquivoImportacao.append(msg);
				controle.setLogRetorno(logRetornoArquivoImportacao);
				util.atualizarArquivo(arquivoImportacao, controle.getInicio(), 0, 100, 0, null, controle);
				logRetornoArquivoImportacao.setLength(0);
				
				final Map<Short, FatTabRegistroServicoVO> tabServico = procesArqImportSusProcedServicoON.carregaServico( sgFaturamento, controle);
				final Map<Integer, FatServClassificacoesVO> tabServClass = 
										procesArqImportSusProcedServicoServClassON.carregaSerClass(sgFaturamento, tabServico, controle);
				controle.gravarLog("Iniciando em carrega_tab_proc_x_servico em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
	
				// Carrega em tab de memoria tabela de procedimentos x servico
				final Map<Long, FatProcedServVO> tabProcxServico = carregaTabProcServico(sgFaturamento, tabServico, tabServClass, logRetornoArquivoImportacao, controle);
	
				controle.gravarLog("fetch_registro em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
	
				// Carrega em tab de memoria arquivo de servico validos dos procedimentos
				final Map<Long, FatProcedServVO> tabLido = executaLeitura(arquivoImportacao, logRetornoArquivoImportacao, controle);
				
				controle.gravarLog("ajusta_banco em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
	
				// Varre tab de memoria arquivo de servico e processa atualização da tab de memoria procedimentos x servico
				ajustaBanco( phoSeq, tabLido, tabServClass, tabProcxServico, arquivoImportacao, logRetornoArquivoImportacao, controle);
	
				controle.gravarLog("Inativa procedimentos x servico não precessados em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				
				// Inativa procedimentos x servico a partir da tab de memoria procedimentos x servico não precessados
				verificaTabProcXServico(phoSeq, tabProcxServico, arquivoImportacao, logRetornoArquivoImportacao, controle);

				controle.setLogRetorno(logRetornoArquivoImportacao);
				util.atualizarArquivo(arquivoImportacao, controle.getInicio(), 0, 100, 0, null, controle);
				logRetornoArquivoImportacao.setLength(0);
				
				controle.gravarLog("Finalizada verifica_tab_proc_x_servico em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				
				logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
							   			   .append("Tabela FAT_PROCED_SERVICOS lida e atualizada com sucesso.");
	
				controle.gravarLog("Finalizada em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));

				controle.setLogRetorno(logRetornoArquivoImportacao);
				util.atualizarArquivo(arquivoImportacao,  controle.getInicio(), 0, 100, 0, null, controle);
				logRetornoArquivoImportacao.setLength(0);
				
			} else {
				logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
							   			   .append("Erro na atualização da Tabela FAT_PROCED_SERVICOS. Arquivo Importação não encontrado.");
				controle.gravarLog("Erro na atualização da Tabela FAT_PROCED_SERVICOS. Arquivo Importação não encontrado.");
			}
			
		} catch (Exception e) { // vai pegar qualquer exception para logar no banco e no log de tela
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro inesperado " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO)).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" + stringException + "].").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			controle.gravarLog("Erro inesperado " + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + stringException);
			
		} finally {
			final Date fim = new Date();
			if (arquivoImportacao != null) {
				controle.setLogRetorno(logRetornoArquivoImportacao);
				util.atualizarArquivo(arquivoImportacao, fim, 100, 100, 0, fim, controle);
				logRetornoArquivoImportacao.setLength(0);
			}
		}
	}
	
	/** Forms: verifica_tab_proc_x_servico */
	private void verificaTabProcXServico( final Short phoSeq, final Map<Long, FatProcedServVO> tabProcxServico,
										  final AghArquivoProcessamento arquivoImportacao,
										  final StringBuilder logRetornoArquivoImportacao, 
										  final ControleProcessadorArquivosImportacaoSus controle ) throws ApplicationBusinessException {
		      							  	
		controle.incrementaNrRegistrosProcesso(tabProcxServico.keySet().size());

		// Varre toda a tabela tab_proc_x_servico e exclui servico dos procedimentos não processados
		for(Long codTabela : tabProcxServico.keySet()){
			FatProcedServVO vo = tabProcxServico.get(codTabela);
			
			String msg = "Verificando cod "+ vo.getCodTabela() + " processado="+vo.getProcessado();
			logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(msg);
			controle.gravarLog(msg);
			
			if(TipoProcessado.NAO_PROCESSADO.equals(vo.getProcessado())){
				controle.gravarLog(msg+" excluido.");
				
				final List<FatProcedServicos> proceds = 
								fatProcedServicosDAO.obterFatProcedServicosPorCodTabelaEPhoSeq(codTabela, phoSeq, 
																								vo.getDtCompetencia());
				//Inativa servico
				inativaProcedServico(codTabela, phoSeq, vo.getDtCompetencia(), proceds, logRetornoArquivoImportacao, controle);

				controle.setLogRetorno(logRetornoArquivoImportacao);
				util.atualizarArquivo(arquivoImportacao, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				logRetornoArquivoImportacao.setLength(0);
			}
		}
	}

	/** Forms: ajusta_banco */
	private void ajustaBanco( final Short phoSeq, final Map<Long, FatProcedServVO> tabLido,
						      final Map<Integer, FatServClassificacoesVO> tabServClass, 
						      final Map<Long, FatProcedServVO> tabProcxServico,
						      final AghArquivoProcessamento arquivoImportacao,
						      final StringBuilder logRetornoArquivoImportacao, 
							  final ControleProcessadorArquivosImportacaoSus controle ) throws ApplicationBusinessException {


		controle.incrementaNrRegistrosProcesso(tabLido.keySet().size());
		
		// Varre tabela do arquivo lido classificado por cod_tabela
		// Carrega em variavel todos os servico validos de cada cod_tabela
		for(Long codTabela : tabLido.keySet()){
			final FatProcedServVO vo = tabLido.get(codTabela);
			final String servicosValidos = vo.getSbServicosValidos().toString();
			
			logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
						   .append("Verificando servico de ...").append(codTabela);
			
			controle.gravarLog("cod_tabela:"+codTabela+" servico_validos="+servicosValidos);
			
			verificaAlteracaoPServico( codTabela, servicosValidos, phoSeq, tabProcxServico, tabServClass, 
									   arquivoImportacao, logRetornoArquivoImportacao, controle);
		}
	}
	
	/** Forms: verifica_alteracao_p_servico */
	private void verificaAlteracaoPServico( final Long codTabela, final String pServicos, final Short phoSeq, 
										    final Map<Long, FatProcedServVO> tabProcxServico,
										    final Map<Integer, FatServClassificacoesVO> tabServClass,
										    final AghArquivoProcessamento arquivoImportacao,
										    final StringBuilder logRetornoArquivoImportacao,
										    final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		controle.gravarLog("verificando cod:"+codTabela);
		final String sCodTabela = StringUtils.leftPad(codTabela.toString(), 9, StringUtils.EMPTY);
		Set<String> set = new TreeSet<String>();
		set.add(pServicos);
		
		if(tabProcxServico.containsKey(codTabela)){
			final FatProcedServVO fatProcedServVO = tabProcxServico.get(codTabela);
			controle.incrementaNrRegistrosProcessados();
			
			// Só processa codigos que não devem ser desprezados
			if(TipoProcessado.DESPREZADO.equals(fatProcedServVO.getProcessado())){
				controle.gravarLog("Proc desprezado por solicitação:"+codTabela);
				
			} else if(TipoProcessado.PROCESSADO.equals(fatProcedServVO.getProcessado())){
				controle.gravarLog("Segunda execução para o Cod:"+sCodTabela+" servico="+pServicos);
				atualizaProcedServico( codTabela, set, fatProcedServVO.getDtCompetencia(), true, 
			   			   			   phoSeq, tabServClass, logRetornoArquivoImportacao, controle);

				controle.setLogRetorno(logRetornoArquivoImportacao);
				util.atualizarArquivo(arquivoImportacao, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				logRetornoArquivoImportacao.setLength(0);
				
			} else {
				// marca em tab_proc_x_servico que o phi foi processado, para posteriormente deletar os não processados
				fatProcedServVO.setProcessado(TipoProcessado.PROCESSADO);
				
				// ordena em v_servico_ordenados os servico passados no parametros p_servicos para permitir comparar com
				// servico_validos de tab_proc_x_servico

				controle.gravarLog("servico do cod :"+sCodTabela+ ' '+fatProcedServVO.getServicosValidos()+ ", ind_alteracao:"+(fatProcedServVO.isIndAlteracao()? 'S' : 'N'));
				controle.gravarLog("servico lidos            :"+pServicos);
				
				// (ordena strings de 6 posicoes concatenados)
				Set<String> servicosOrdenados = util.ordenarStringCBO(pServicos, 6);
				
				if(!CoreUtil.igual(fatProcedServVO.getServicosValidos(), StringUtils.join( servicosOrdenados.toArray() )) ||
						fatProcedServVO.isIndAlteracao()){
					
					// se houve alteração, atualiza servico em FAT_PROCED_HOSP_INT_servico
					controle.gravarLog("houve alteração, atualiza servico - ind_alteracao: S");

					atualizaProcedServico( codTabela, servicosOrdenados, fatProcedServVO.getDtCompetencia(), false, 
										   phoSeq, tabServClass, logRetornoArquivoImportacao, controle);	


					controle.setLogRetorno(logRetornoArquivoImportacao);
					util.atualizarArquivo(arquivoImportacao, controle.getInicio(), new Date(), 
										  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
										  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
					logRetornoArquivoImportacao.setLength(0);
					
				} else {
					controle.gravarLog("Sem alteração");
				}
			}
		} else {
			controle.gravarLog("Inclusão do cod:"+sCodTabela + " servicos="+pServicos);
			atualizaProcedServico( codTabela, set, null, true, phoSeq, tabServClass, logRetornoArquivoImportacao, controle);	

			controle.setLogRetorno(logRetornoArquivoImportacao);
			util.atualizarArquivo(arquivoImportacao, controle.getInicio(), new Date(), 
								  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,
								  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			logRetornoArquivoImportacao.setLength(0);
		}
	}
	
	/** Forms: atualiza_proced_servico  */
	private void atualizaProcedServico( final Long codTabela, final Set<String> servicosValidos, final String dtCompetencia,
									    final boolean isInsert, final Short phoSeq,
									    final Map<Integer, FatServClassificacoesVO> tabServClass,
									    final StringBuilder logRetornoArquivoImportacao, 
									    final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		// Alterei consulta pois em inativa_proced_servico estava inativando todos os registros com os 
		// PRMS: p_iph_seq, p_pho_seq e p_comptencia
		final List<FatProcedServicos> proceds = fatProcedServicosDAO.obterFatProcedServicosPorCodTabelaEPhoSeq(codTabela, phoSeq, dtCompetencia);

		if(!isInsert){
			inativaProcedServico(codTabela, phoSeq, dtCompetencia, proceds, logRetornoArquivoImportacao, controle);
		}
		
		if(!proceds.isEmpty()){
			FatItensProcedHospitalar iph = proceds.get(0).getItemProcedimentoHospitalar();
			
			for(String servico: servicosValidos){
				if(servico == null || servico.length() < 1 || StringUtils.EMPTY.equals(servico)){
					break;
				} else {
					int intServico = Integer.valueOf(servico);
					if(tabServClass.containsKey(intServico) && 
							tabServClass.get(intServico).getFatServclassificacao() != null){
						
						incluiProcedServico(servico, dtCompetencia, 
											tabServClass.get(intServico).getFatServclassificacao(), 
											iph, logRetornoArquivoImportacao, controle);
					} else {
						controle.gravarLog("Erro, não possivel determinar a seq do serv/class, cod:"+codTabela+" servico:"+servico);
					}
				}
			}
		}
	}
	
	/** Forms: inclui_proced_servico  */
	private void incluiProcedServico( final String servico, final String dtCompetencia,
									  final FatServClassificacoes fatServClassificacoes, 
									  final FatItensProcedHospitalar iph,
									  final StringBuilder logRetornoArquivoImportacao, 
									  final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		final FatProcedServicos proced = new FatProcedServicos();
		logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(msg9).append(servico);
		controle.gravarLog(msg9+servico);
		
		proced.setFatServClassificacoes(fatServClassificacoes);
		proced.setItemProcedimentoHospitalar(iph);
		proced.setDtCompetencia(dtCompetencia);
		proced.setIndSituacao(DominioSituacao.A);

		fatProcedServicosRN.persistirFatProcedServicos(proced);
	}
	
	/** Forms: inativa_proced_servico */
	private void inativaProcedServico( final Long codTabela, final Short phoSeq, final String dtCompetencia,
									   final List<FatProcedServicos> proceds,
									   final StringBuilder logRetornoArquivoImportacao,
									   final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(msg10).append(codTabela);
		
		controle.gravarLog(msg10+codTabela+" iph:xxx pho:"+phoSeq+" comp:"+dtCompetencia );
		
		for (FatProcedServicos fatProcedServicos : proceds) {
			fatProcedServicos.setIndSituacao(DominioSituacao.I);
			fatProcedServicosRN.persistirFatProcedServicos(fatProcedServicos);
		}
		
		controle.gravarLog("Inativados "+proceds.size()+" servico/classificacao do procedimento "+codTabela);
	}
	
	/** Forms: fetch_registro executa_leitura   */
	@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
	private Map<Long, FatProcedServVO> executaLeitura( final AghArquivoProcessamento arquivoImportacao,
													   final StringBuilder logRetornoArquivoImportacao,
													   final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		final Map<Long, FatProcedServVO> tabLido = new TreeMap<Long, FatProcedServVO>();

		controle.setLogRetorno(logRetornoArquivoImportacao);
		util.atualizarArquivo(arquivoImportacao, controle.getInicio(), 0, 100, 0, null, controle);
		logRetornoArquivoImportacao.setLength(0);
		
		BufferedReader br = null;
		try {
			br = util.abrirArquivo(arquivoImportacao);

			String strLine;
			List<String> lines = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}
			
			controle.incrementaNrRegistrosProcesso(lines.size());
			
			int wTotServico = 0;
			for (String line : lines) {
				logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
							   .append("Processando rl_procedimento_servico... ").append(++wTotServico);
				
				montaRegistro(arquivoImportacao, logRetornoArquivoImportacao, tabLido, line, controle);
			} 
			
		} catch (final IOException io) {
			LOG.warn("Problemas na leitura dos arquivos.");
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO);
			
		} finally {
			try {
				if (br != null) {
					br.close();
				}

				controle.setLogRetorno(logRetornoArquivoImportacao);
				util.atualizarArquivo(arquivoImportacao, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				logRetornoArquivoImportacao.setLength(0);
				
			} catch (final IOException e) {
				LOG.error(e.getMessage(), e);
			}
		} 
		
		return tabLido;
	}
	
	/** Forms: monta_registro_servico */
	private void montaRegistro( final AghArquivoProcessamento arquivoImportacao, final StringBuilder logRetornoArquivoImportacao,
								final Map<Long, FatProcedServVO> tabLido, final String regImportacao,
								final ControleProcessadorArquivosImportacaoSus controle ) throws ApplicationBusinessException{
		
		//  Só processa servico com indicador de principal
		final Long wTpCodTabela = Long.valueOf(regImportacao.substring(0,10));
		final String sbServicosValidos = regImportacao.substring(10,16);
		final String dtCompetencia = regImportacao.substring(16,22);
		
		if(wTpCodTabela.intValue() > 0){
			if(tabLido.containsKey(wTpCodTabela)){
				
				// Limitei a tabela em 30000 caracteres, acredito que não vai estourar
				if(tabLido.get(wTpCodTabela).getSbServicosValidos().length() < 30000){
					tabLido.get(wTpCodTabela).appendSbServicosValidos(sbServicosValidos);
					
				} else {	// Bom, se estourar, ainda da para aumentar para 32.767
					throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusProcedServicoONExceptionCode.MAX_CARACTERS_TABELA_SERVICO_PROC_EXCEDIDA);
				}
				
			} else {
				tabLido.put(wTpCodTabela, new FatProcedServVO(wTpCodTabela, new StringBuilder(sbServicosValidos), dtCompetencia));
			}
		}
		
		controle.incrementaNrRegistrosProcessados();
		if (controle.getNrRegistrosProcessados() % QT_MINIMA_ATUALIZAR_ARQUIVO == 0) {
			controle.setLogRetorno(logRetornoArquivoImportacao);
			util.atualizarArquivo(arquivoImportacao, controle.getInicio(), new Date(), 
								  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
								  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			logRetornoArquivoImportacao.setLength(0);
		}
	}
	
	

	/* *********************** P R O C   S E R V **********************************/

	/** Forms: carrega_tab_proc_x_servico  */
	private Map<Long, FatProcedServVO> carregaTabProcServico( final String sgFaturamento, 
															  final Map<Short, FatTabRegistroServicoVO> tabServico,
															  final Map<Integer, FatServClassificacoesVO> tabServClass,
															  final StringBuilder logRetornoArquivoImportacao, 
															  final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{

	    List<FatProcedServVO> rProcxServicos = getFatItensProcedHospitalarDAO().obterFatProcedServVO();
	    Map<Long, FatProcedServVO> tabProcxServico = new TreeMap<Long, FatProcedServVO>();
	    	
	    StringBuilder vServicosValidos = new StringBuilder(30000);
	    Long vCod = null;
	    boolean vIndAlteracao = false;
	    String vDtCompetencia = null;
	    
	    for (FatProcedServVO rProcxServico : rProcxServicos) {
	    	
	    	// Leitura de proc_x_servico classificado por cod_tabela e servico
	    	// Carrega em um unico registro de memoria todos os servico validos de cada cod_tabela, na quebra de cod_tabela
	    	if(!CoreUtil.igual(vCod, rProcxServico.getCodTabela())){
	    		if(vCod != null){
	    			logRetornoArquivoImportacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
	    						   .append("Carregando servico do cod ...").append(vCod);
	    			
	    			tabProcxServico.put(vCod, new FatProcedServVO(vCod, vServicosValidos.toString(), vDtCompetencia, vIndAlteracao, TipoProcessado.NAO_PROCESSADO));
	    			controle.gravarLog("Cod:"+vCod+" servicos="+vServicosValidos.toString()+" ind_alteracao="+(vIndAlteracao ? 'S':'N'));
	    		}
	    		
	    		vServicosValidos.setLength(0);	// limpa
	    		
	    		vCod = rProcxServico.getCodTabela();
	    		vServicosValidos.append(rProcxServico.getServClass());
	    		vDtCompetencia = rProcxServico.getDtCompetencia();
				vIndAlteracao = verAlteracaoServclas(rProcxServico, tabServico, tabServClass);
	    		
    		// Enquanto não quebra o cod tabela, concatena em v_servico_validos cada servico valido para o phi
	    	} else {
	    		// Limitei a tabela em 30000 caracteres, acredito que não vai estourar
	    		if(vServicosValidos.length() < 29996){ // 30000 -4
	    			vServicosValidos.append(rProcxServico.getServClass());
	    			
	    			if(!vIndAlteracao){
	    				vIndAlteracao = verAlteracaoServclas(rProcxServico, tabServico, tabServClass);
	    			}
	    			
	    		} else {	// Bom, se estourar, ainda da para aumentar para 32.767	
	    			throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusProcedServicoONExceptionCode.MAX_CARACTERS_TABELA_PROC_X_SERVICO_EXCEDIDA);
	    		}
	    	}
		}
	
	    if(vCod != null){
			tabProcxServico.put(vCod, new FatProcedServVO(vCod, vServicosValidos.toString(), vDtCompetencia, vIndAlteracao, TipoProcessado.NAO_PROCESSADO));
			controle.gravarLog("Cod:"+vCod+" servicos="+vServicosValidos.toString()+" ind_alteracao="+(vIndAlteracao ? 'S':'N'));
	    } 
	    
	    return tabProcxServico;
	}
	

	/** Forms: ver_alteracao_servclas */
	private boolean verAlteracaoServclas( final FatProcedServVO rProcxServico, 
										  final Map<Short, FatTabRegistroServicoVO> tabServico,
										  final Map<Integer, FatServClassificacoesVO> tabServClass){
		boolean indAlteracao = false;
		
		if(rProcxServico.getFseCodigo() != null ){
			short cod = Short.parseShort(rProcxServico.getFseCodigo());
			if(cod > 0 && tabServico.containsKey(cod)){
				indAlteracao = TipoProcessado.ALTERA.equals(tabServico.get(cod).getProcessado()); 
			}
		}
		
		if(!indAlteracao && rProcxServico.getFcsCodigo() != null){
			Integer cod = Integer.valueOf(rProcxServico.getFcsCodigo());
			if(cod > 0 && tabServClass.containsKey(cod)){
				indAlteracao = TipoProcessado.ALTERA.equals(tabServClass.get(cod).getProcessado()); 
			}
		}
		
		return indAlteracao;
	} 
	
	/** Forms: abertura_OK  */
	private AghArquivoProcessamento aberturaOK( final String sgFaturamento) throws ApplicationBusinessException{
		
		final String nmArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_PROCED_SERVICO).toLowerCase(); 
		AghArquivoProcessamento aghArquivo = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArquivo);
		
		return aghArquivo;
	}
		
	
	
	
	
	protected FatcBuscaServClassRN getFatcBuscaServClassRN(){
		return fatcBuscaServClassRN;
	}
	
	protected FatServClassificacoesRN getFatServClassificacoesRN() {
		return fatServClassificacoesRN;
	}
	
	protected FatServicosRN getFatServicosRN() {
		return  fatServicosRN;
	}
	

	
	protected FatProcedServicosRN getFatProcedServicosRN() {
		return fatProcedServicosRN;
	}

	
	protected ProcesArqImportSusProcedServicoON getProcesArqImportSusProcedServicoON() {
		return procesArqImportSusProcedServicoON;
	}

	
	protected FatProcedServicosDAO getFatProcedServicosDAO() {
		return fatProcedServicosDAO;
	}

	
	
	protected ProcesArqImportSusProcedServicoServClassON getProcesArqImportSusProcedServicoServClassON() {
		return procesArqImportSusProcedServicoServClassON;
	}
	
	
	protected FatServicosDAO getFatServicosDAO() {
		return fatServicosDAO;
	}

	protected FatServClassificacoesDAO getFatServClassificacoesDAO() {
		return fatServClassificacoesDAO;
	}

	
	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}

	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	

	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}