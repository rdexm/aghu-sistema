package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.business.ImportarArquivoSusON.ImportarArquivoSusONExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCboDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoCboDAO;
import br.gov.mec.aghu.faturamento.vo.FatProcCboVO;
import br.gov.mec.aghu.faturamento.vo.FatTabOcupacaoCboVO;
import br.gov.mec.aghu.faturamento.vo.FatTabOcupacaoVO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatProcedimentoCbo;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.FileUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusCBOProcON extends BaseBMTBusiness {
	
	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusCBOProcON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@Inject
	private FatProcedimentoCboDAO fatProcedimentoCboDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private FatCboDAO fatCboDAO;

	private static final long serialVersionUID = 3421682147865750031L;

	/**
	 * Principal metodo para atualizar CBO
	 */
	public void atualizarCboProcedimento(final ControleProcessadorArquivosImportacaoSus controle, final List<String> listaNomeArquivos) {
		final Date inicio = new Date();
		controle.iniciarLogRetorno();
		final Boolean houveAlteracaoCbo = Boolean.FALSE;
		final StringBuilder logRetorno = new StringBuilder(200);
		final StringBuilder logRetornoOcupacao = new StringBuilder(200);
		AghArquivoProcessamento aghArquivoOcupacao = null;
		AghArquivoProcessamento aghArquivoProcedimentosCBO = null;
		
		//==================== Forms: inicializa_ok  ====================//
		try { // vai pegar qualquer exception para logar no banco e no log de tela
			
			// Argumentos paramentrizados
			final String nomeArquivoOcupacao = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_OCUPACAO_CBO).toLowerCase(); // "tb_ocupacao.txt";
			final String nomeArquivoProcedimentosCBO = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_CBO).toLowerCase(); // "rl_procedimento_ocupacao.txt";	
			final String parametroFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();

			//==================== Forms: abertura_rl_proced_ocupacao ====================//
			aghArquivoOcupacao = aghuFacade.obterArquivoNaoProcessado(parametroFaturamento, nomeArquivoOcupacao);
			aghArquivoProcedimentosCBO = aghuFacade.obterArquivoNaoProcessado(parametroFaturamento, nomeArquivoProcedimentosCBO);
			//==================== Forms: abertura_rl_proced_ocupacao ====================//
			
			aghArquivoProcedimentosCBO.setDthrInicioProcessamento(new Date(inicio.getTime()));
			aghArquivoOcupacao.setDthrInicioProcessamento(new Date(inicio.getTime()));
			
			controle.setLogRetorno(logRetornoOcupacao);
			util.atualizarArquivo(aghArquivoOcupacao, inicio, 0, 100, 0, null, controle);
			logRetornoOcupacao.setLength(0);
			
			controle.setLogRetorno(logRetorno);
			util.atualizarArquivo(aghArquivoProcedimentosCBO, inicio, 0, 100, 0, null, controle);
			logRetorno.setLength(0);
		
			// realiza atualização das ocupações
			final Map<Long, FatProcCboVO> tabProcCboLidoArq; //armazena dados do arquivo rl_procedimento_ocupacao.txt
			List<FatTabOcupacaoVO> tabOcupacaoArq; //armazena dados do arquivo tb_ocupacao.txt
			//executa_leitura, monta_registro => le os dados dos arquivos
			tabProcCboLidoArq = this.montaRegistroProcedimentoCBO(aghArquivoProcedimentosCBO, logRetornoOcupacao, inicio, controle, listaNomeArquivos);
			tabOcupacaoArq = this.montaRegistroOcupacao(aghArquivoOcupacao, logRetorno, inicio, controle, listaNomeArquivos);
			
			final Date dtInicioComp = tabProcCboLidoArq.get(tabProcCboLidoArq.keySet().iterator().next()).getCompetencia(); //pega competencia primeiro registro do map
			controle.gravarLog("Iniciando ATUALIZA_PROCED_OCUP em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Iniciando ATUALIZA_PROCED_OCUP em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));				
			controle.gravarLog("Competencia do sigtab:" + DateUtil.obterDataFormatada(dtInicioComp, DateConstants.DATE_PATTERN_YYYY_MM));
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Competencia do sigtab: " ).append( DateUtil.obterDataFormatada(dtInicioComp, DateConstants.DATE_PATTERN_YYYY_MM));
			
			//==================== Forms: check_competencia_ok ====================//
			final List<FatCompetencia> competencias = this.getFatCompetenciaDAO().obterListaCompetenciaSemDataFimPorModulo(DominioModuloCompetencia.AMB);
			
			if (competencias == null || competencias.isEmpty()) { 
				logRetornoOcupacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Não foi encontrada uma competencia aberta.").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
				return;
			}
			
			controle.gravarLog("Competencia aberta:" + DateUtil.obterDataFormatada(dtInicioComp, DateConstants.DATE_PATTERN_YYYY_MM));
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Competencia aberta:" ).append( DateUtil.obterDataFormatada(dtInicioComp, DateConstants.DATE_PATTERN_YYYY_MM));
			final Date dtFimComp = DateUtil.adicionaDias(dtInicioComp, -1); //v_dt_encerra
			//==================== Forms: fim check_competencia_ok ====================//
			
			//==================== Forms: check_ultima_ok ====================//
			Date dataMax = getFatProcedimentoCboDAO().obterMaiorCompetenciaSemDataFim();
			if (dataMax != null && dataMax.after(dtInicioComp)) {
				logRetornoOcupacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Competencia do sigtap " + DateUtil.obterDataFormatada(dtInicioComp, DateConstants.DATE_PATTERN_MM_YYYY) + " anterior à ultima competencia atualizada: " +  DateUtil.obterDataFormatada(DateUtil.adicionaMeses(dataMax, 1), DateConstants.DATE_PATTERN_MM_YYYY)).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
				controle.gravarLog("Competencia do sigtap " + DateUtil.obterDataFormatada(dtInicioComp, DateConstants.DATE_PATTERN_MM_YYYY) + " anterior à ultima competencia atualizada: " +  DateUtil.obterDataFormatada(DateUtil.adicionaMeses(dataMax, 1), DateConstants.DATE_PATTERN_MM_YYYY));
				return;
			}
			//==================== Forms: fim check_ultima_ok ====================//
			
			//==================== CONTROLE PRINCIPAL ====================//
			//fetch_registro_PROC_x_CBO => executa_leitura => monta_registro ==> isso já foi feito no AGH le uma linha e depois em loop
			
			//==================== Forms: processa_atualizacao_CBOS ====================//
			//IF abertura_tb_ocupacao THEN
			//abertura_tb_ocupacao já fez abertura arquivos abertura_tb_ocupacao
			
			List<FatTabOcupacaoCboVO> listaOcupacaoBancoHU = carregaTabCboHU(controle); //var tab_cbo_hcpa
			
			//fetch_registro_CBO, executa_leitura_cbo
			verificaTabCboHU(tabOcupacaoArq, listaOcupacaoBancoHU,  houveAlteracaoCbo, dtInicioComp, dtFimComp, controle);
			
			
			ajustaBancoCbo(listaOcupacaoBancoHU, dtInicioComp, dtFimComp, controle);
			
			//==================== Forms: fim processa_atualizacao_CBOS ====================//
			
			//carrega_tab_proc_x_cbo
			Map<Long, FatProcCboVO> listaProcedimentoCboBanco = carregaTabProcCbo(controle);
			
			//ajusta_banco_PROC_x_CBO
			ajustaBancoProcCbo(tabProcCboLidoArq, listaProcedimentoCboBanco, dtInicioComp, dtFimComp, listaOcupacaoBancoHU, controle);
				
			final Date fim = new Date();
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Tabela Procedimento CBO lida e atualizada com sucesso em " + (((fim.getTime() - inicio.getTime())) / 1000L) + " segundos.").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			controle.gravarLog("Tabela Procedimento CBO lida e atualizada com sucesso em " + (((fim.getTime() - inicio.getTime())) / 1000L) + " segundos.");
			
		}  catch (Exception e) { // vai pegar qualquer exception para logar no banco e no log de tela
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			logRetornoOcupacao.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro inesperado " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO)).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" + stringException + "].").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			controle.gravarLog("Erro inesperado " + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + stringException);
			
		} finally {
			final Date fim = new Date();
			//atualiza os logs
			if (aghArquivoOcupacao != null) {
				controle.setLogRetorno(logRetornoOcupacao);
				util.atualizarArquivo(aghArquivoOcupacao, fim, 100, 100, 0, fim, controle);
				logRetornoOcupacao.setLength(0);
			}
			if (aghArquivoProcedimentosCBO != null) {
				controle.setLogRetorno(logRetorno);
				util.atualizarArquivo(aghArquivoProcedimentosCBO, fim, 100, 100, 0, fim, controle);
				logRetorno.setLength(0);
			}
		}
	}

	/**
	 * Forms: ajusta_banco_cbo
	 */
	private void ajustaBancoCbo(List<FatTabOcupacaoCboVO> listaOcupacaoBancoHU, Date dtInicioComp, Date dtFimComp, ControleProcessadorArquivosImportacaoSus controle) {
		//-- esta rotina deve varrer a tabela para garantir que os CBOs não processados sejam inativados
		//-- busca na tabela tb_ocupacao - cbo
		Integer ind = 0;
		for (FatTabOcupacaoCboVO vo : listaOcupacaoBancoHU) {
			if (Boolean.FALSE.equals(vo.getProcessado())) {
				encerraCbo(vo, dtInicioComp, dtFimComp);
				controle.gravarLog("CBO:" + vo.getCbo() + " " + ind + " inativado.");
			}
			++ind;
		}		
	}

	/**
	 * Forms: fetch_registro_CBO, executa_leitura_cbo
	 */
	private void verificaTabCboHU(List<FatTabOcupacaoVO> tabOcupacaoArq,
			List<FatTabOcupacaoCboVO> listaOcupacaoBancoHU,
			Boolean houveAlteracaoCbo, Date dtInicioComp, Date dtFimComp,
			final ControleProcessadorArquivosImportacaoSus controle) {
		
		//Importando CBOs do SIGTAB.... 
		for (FatTabOcupacaoVO arq : tabOcupacaoArq) {
			FatTabOcupacaoCboVO cbo = retornaCbo(listaOcupacaoBancoHU, arq.getCboOcupacao());
			
			if (cbo == null) {
				controle.gravarLog("CBO:" + arq.getCboOcupacao() + " não encontrado na tab hcpa:");
				insereCbo(arq,  dtInicioComp, listaOcupacaoBancoHU);
				
			} else {
				//-- caso encontrada
				if (!cbo.getDescricao().equals(arq.getCboDescricao())) {
					controle.gravarLog("CBO encontrado:" + cbo.getCbo() + " alterada descricao:" + cbo.getDescricao());
					controle.gravarLog("para:" + arq.getDescricao());
				    //-- descricao for diferente inclui na tab_cbo_hcpa e no banco
				    //-- fecha a vigencia antiga e abre uma nova
					encerraCbo(cbo, dtInicioComp, dtFimComp);
					insereCbo(arq, dtInicioComp, listaOcupacaoBancoHU);
					
				} else {
					//-- não houve alteração,  considera como processada para nao desativar no final
					cbo.setProcessado(Boolean.TRUE);
				}
			}
		}
		
	}

	/**
	 * Forms: insere_cbo
	 */
	private void insereCbo(FatTabOcupacaoVO cbo, Date dtInicioComp, List<FatTabOcupacaoCboVO> listaOcupacaoBancoHU) {
		//cria um novo registro
		FatCbos fatCbo = new FatCbos();
		fatCbo.setCodigo(cbo.getCboOcupacao());
		fatCbo.setDescricao(cbo.getDescricao());
		fatCbo.setDtInicio(dtInicioComp);
		
		//persiste no banco
		try {
			super.beginTransaction();
			
			getFatCboDAO().persistir(fatCbo);
			getFatCboDAO().flush();
			super.commitTransaction();
			
		} catch(Exception e) {
			super.rollbackTransaction();
			LOG.info("Erro ao inserir cbo: " + fatCbo.getCodigo() + " erro: " + e.getMessage());
		}
		
		//insere na tabela em memoria
		FatTabOcupacaoCboVO vo = new FatTabOcupacaoCboVO();
		vo.setCbo(cbo.getCboOcupacao());
		vo.setDescricao(cbo.getDescricao());
		vo.setDtInicio(dtInicioComp);
		vo.setProcessado(Boolean.TRUE);
		listaOcupacaoBancoHU.add(vo);
		
	}
	
	/**
	 * Forms: encerra_cbo
	 */
	private void encerraCbo(FatTabOcupacaoCboVO cbo, Date dtInicioComp, Date dtFimComp) {
		super.beginTransaction();
		if (dtInicioComp.after(dtFimComp)) {
			getFatCboDAO().atualizarFatCbo(cbo.getCboSeq(), dtInicioComp);
		} else {
			getFatCboDAO().atualizarFatCbo(cbo.getCboSeq(), dtFimComp);
		}
		super.commitTransaction();
	}

	/**
	 * Retorna CBO da tabela
	 */
	private FatTabOcupacaoCboVO retornaCbo(List<FatTabOcupacaoCboVO> listaOcupacaoBancoHU,
			String cboOcupacao) {
		for (FatTabOcupacaoCboVO vo : listaOcupacaoBancoHU) {
			if (vo.getCbo().equals(cboOcupacao)) {
				return vo;
			}
		}
		
		return null;
	}

	/**
	 * Forms: carrega_tab_cbo_hcpa
	 */
	private List<FatTabOcupacaoCboVO> carregaTabCboHU(final ControleProcessadorArquivosImportacaoSus controle) {
		
		List<FatCbos> listaCbosAtivos = getFatCboDAO().listarCbosSemDataFim();
		List<FatTabOcupacaoCboVO> listaTabCboHU = new ArrayList<FatTabOcupacaoCboVO>();
		
		for (FatCbos fatCbo : listaCbosAtivos) {
			FatTabOcupacaoCboVO vo = new FatTabOcupacaoCboVO();
			
			vo.setCboSeq(fatCbo.getSeq());
			vo.setCbo(StringUtils.rightPad(fatCbo.getCodigo(), 6, ' '));
			vo.setDtInicio(fatCbo.getDtInicio());
			vo.setDtFim(fatCbo.getDtFim());
			vo.setDescricao(fatCbo.getDescricao());
			vo.setProcessado(Boolean.FALSE);
			
			listaTabCboHU.add(vo);
		}

		controle.gravarLog("Tab cbos carregada com:" + listaTabCboHU.size());
		
		return listaTabCboHU;
	}

	/**
	 * Forms: ajusta_banco_PROC_x_CBO
	 */
	private void ajustaBancoProcCbo(Map<Long, FatProcCboVO> tabProcCboLidoArq, Map<Long, FatProcCboVO> listaProcedimentoCboBanco,
									 Date dtInicioComp, Date dtFimComp, List<FatTabOcupacaoCboVO> listaOcupacaoBancoHU,
									 final ControleProcessadorArquivosImportacaoSus controle) throws BaseException {

		Set<Long>  cbos = tabProcCboLidoArq.keySet();
		for (Long cbo : cbos) {			
			verificaAlteracaoCbos(listaProcedimentoCboBanco, tabProcCboLidoArq.get(cbo).getCodTabela(), 
								   tabProcCboLidoArq.get(cbo).getCbosValidos(), dtInicioComp, dtFimComp, 
								   listaOcupacaoBancoHU, controle);
		}

	}

	/**
	 * Forms: verifica_alteracao_cbos
	 */
	private void verificaAlteracaoCbos( Map<Long, FatProcCboVO> listaProcedimentoCboBanco, Long codTabela, String cbosValidos, Date dtInicioComp, 
										Date dtFimComp, List<FatTabOcupacaoCboVO> listaOcupacaoBancoHU,
										final ControleProcessadorArquivosImportacaoSus controle) throws BaseException {
		
		if (listaProcedimentoCboBanco.containsKey(codTabela)) { //se na tabela do banco tem o codTabela lido no arq do sus
			listaProcedimentoCboBanco.get(codTabela).setProcessado(Boolean.TRUE);			
			//ordena em v_cids_ordenados os cids passados no parametros p_cids para permitir comparar com cids_validos de tab_proc_x_cid
			String cbosOrdenados = ffccOrdenaStringCbo(cbosValidos);
			String cbos = ffccOrdenaStringCbo(listaProcedimentoCboBanco.get(codTabela).getCbosValidos());
			if (!cbos.equals(cbosOrdenados)) {
				//-- se houve alteração, atualiza cids em FAT_PROCED_HOSP_INT_CID
				controle.gravarLog("Procedimento:" + codTabela + " com alteração, atualizando cbos:");
				atualizaCbos(codTabela, cbosOrdenados, listaProcedimentoCboBanco.get(codTabela).getTabCbosValidos(), false,
							  dtInicioComp, dtFimComp, listaOcupacaoBancoHU, controle);
			}
		}
		else {
			//INCLUI
			controle.gravarLog("Inclusão do procedimento:" + codTabela + " cbos=" + cbosValidos);
			atualizaCbos(codTabela, cbosValidos, null, true, dtInicioComp, dtFimComp, listaOcupacaoBancoHU, controle);
		}
		
	}

	/**
	 * Forms: atualiza_cbos 
	 */
	private void atualizaCbos(Long codTabela, String cbosValidos, String cbosHU, boolean inclui, Date dtInicioComp, 
								Date dtFimComp, List<FatTabOcupacaoCboVO> listaOcupacaoBancoHU,
								final ControleProcessadorArquivosImportacaoSus controle) throws BaseException {
		
		//Busca na itens procedimentos o pho e iph correspondente
		Short iphPhoSeq = null;
		Integer iphSeq = null;
		FatItensProcedHospitalar iph = getItensProcedHospitalar(codTabela);
		if (iph != null) {
			iphPhoSeq = iph.getId().getPhoSeq();
			iphSeq = iph.getId().getSeq();
		}
		
		if (iphSeq == null) {
			controle.gravarLog("Não foi encontrado item procedimento hospitalar para o codigo:" + codTabela);
		}
		else {
			//monta_tab_cbos_proc
			List<FatTabOcupacaoCboVO> tabCbosProcArq = null;
			if (cbosHU != null) {
				tabCbosProcArq = montaTabCbosProc(cbosHU);
			}
			//-- separa as ocorrencias dos CBOs concatenados passados pelo parametro p_cbos_validos
			Integer pos = 0;
			Integer tam = cbosValidos.length();
			while (pos < tam) {
				String cbo = cbosValidos.substring(pos, pos + 6);
				if (cbo == null || cbo.equals(" ") || cbo.length() < 1) {
					break;
				}
				if (inclui) {
					//-- Procedimento que ainda não possuia CBOs
					incluiCbo(iphPhoSeq, iphSeq, codTabela,cbo, listaOcupacaoBancoHU, dtInicioComp, controle);
				}
				else {
			        //-- Para cada CBO do procedimento lido, valida na tabela de CBOs hcpa:  v_cbos_hcpa
			        //-- a função ver_cbos_lido_na_tab_hcpa atualiza em v_cbos_hcpa o status de cada cbo processado
			        //-- para ao final inativar os não processados (ver_cbos_tab_hcpa)
			        //-- Quando não encontrado, inlcui novo CBO do procedimento
					if (!pesquisaTabCbosProc(tabCbosProcArq, cbo)) {
						
						controle.gravarLog("Incluindo Cbo:" + cbo + " que não foi encontrado para o procedimento:" + codTabela);
						incluiCbo(iphPhoSeq, iphSeq, codTabela, cbo, listaOcupacaoBancoHU, dtInicioComp, controle);
						
					}
					
				}
				pos += 6;
			}
			
			//-- Verifica se ficou algum CBO não processando na na tabela de CBOs hcpa, e caso positivo, inativando
			if (!inclui) {
				verCbosTabHU(tabCbosProcArq, iphPhoSeq, iphSeq, codTabela, dtInicioComp, dtFimComp, controle);
			}
		}
	}

	/**
	 * Forms: ver_cbos_tab_hcpa
	 */
	private void verCbosTabHU(List<FatTabOcupacaoCboVO> tabCbosProcArq,
			Short iphPhoSeq, Integer iphSeq, Long codTabela, Date dtInicioComp, Date dtFimComp, final ControleProcessadorArquivosImportacaoSus controle) {

		for (FatTabOcupacaoCboVO vo : tabCbosProcArq) {
			if (Boolean.FALSE.equals(vo.getProcessado())) {
				inativaCboProcedimento(iphPhoSeq, iphSeq, codTabela, vo.getCboSeq(), vo.getCbo(), dtInicioComp, dtFimComp, controle);
			}
		}
		
	}

	/**
	 * Forms: inativa_cbo_procedimento
	 */
	private void inativaCboProcedimento(Short iphPhoSeq, Integer iphSeq, Long codTabela, Integer cboSeq, String cbo, Date dtInicioComp, Date dtFimComp,
										final ControleProcessadorArquivosImportacaoSus controle) {
		
		Date maiorData = dtInicioComp.after(dtFimComp) ? dtInicioComp : dtFimComp;
		
		controle.gravarLog("Inativando CBO " + cbo + " do procedimento " + codTabela + " ,data fim:" + DateUtil.obterDataFormatada(maiorData, "dd/MM/yyyy"));

		super.beginTransaction();
		Integer rowCount = getFatProcedimentoCboDAO().atualizarFatProcedimentoCbo(cboSeq, iphPhoSeq, iphSeq, maiorData);
		getFatProcedimentoCboDAO().flush();
		super.commitTransaction();
		if (rowCount != 1) {
			controle.gravarLog("Inativando CBO " + cbo + " com possivel erro. Total excluidos:" + rowCount);
		}
	}

	/**
	 * Forms: pesquisa_tab_cbos_proc
	 */
	private boolean pesquisaTabCbosProc(final List<FatTabOcupacaoCboVO> tabCbosProcBanco, String cbo) {
		for (FatTabOcupacaoCboVO vo : tabCbosProcBanco) {
			if (vo.getCbo().equals(cbo)) {
				vo.setProcessado(Boolean.TRUE);
				return true;
			}
		}
		return false;
	}

	/**
	 * Forms: inclui_cbo
	 */
	private void incluiCbo(Short iphPhoSeq, Integer iphSeq, Long codTabela, String cbo, List<FatTabOcupacaoCboVO> listaOcupacaoBancoHU, Date dtInicioComp,
							final ControleProcessadorArquivosImportacaoSus controle) {
		
		Integer seq = retSeqCbo(cbo, listaOcupacaoBancoHU);
		
		if (seq == null) {
			controle.gravarLog("Erro: Não foi possivel determinar seq do CBO:" + cbo);
			
		} else {
			controle.gravarLog("Incluindo cbo " + cbo);
			FatCbos fatCbo = getFatCboDAO().obterPorChavePrimaria(seq);
			
			FatProcedimentoCbo elemento = new FatProcedimentoCbo();
			elemento.setCbo(fatCbo);
			FatItensProcedHospitalar item = getFatItensProcedHospitalarDAO().obterPorChavePrimaria(new FatItensProcedHospitalarId(iphPhoSeq, iphSeq));
			elemento.setItemProcedHosp(item);
			
			elemento.setDtInicio(dtInicioComp);
			
			try {
				
				super.beginTransaction();
				getFatProcedimentoCboDAO().persistir(elemento);
				getFatProcedimentoCboDAO().flush();
				super.commitTransaction();
				
			} catch(PersistenceException e) {
				if (e.getCause() instanceof ConstraintViolationException) {
					LOG.info("Erro, cbo duplicado, iph=" + iphSeq + " cbo= " + cbo);
					controle.gravarLog("Erro, cbo duplicado, iph=" + iphSeq + " cbo=" + cbo);
				}
				
				super.rollbackTransaction();
				
			} catch(Exception e) {
				LOG.info("Não foi possível incluir CBO iph=" + iphSeq + " cbo= " + cbo + " erro:" + e.getMessage());
				super.rollbackTransaction();
			}
		}
	}

	/**
	 * Forms: ret_seq_cbo
	 */
	private Integer retSeqCbo(String cbo, List<FatTabOcupacaoCboVO> listaOcupacaoBancoHU) {
		for (FatTabOcupacaoCboVO vo : listaOcupacaoBancoHU) {
			if (vo.getCbo().equals(cbo)) {
				return vo.getCboSeq();
			}
		}
		return null;
	}

	/**
	 * Forms: monta_tab_cbos_proc
	 */
	private List<FatTabOcupacaoCboVO> montaTabCbosProc(String cbosHU) {
		List<FatTabOcupacaoCboVO> listaFatTabCboVO = new ArrayList<FatTabOcupacaoCboVO>();

		Integer pos = 0;
		Integer tam = cbosHU.length();
		while (pos < tam) {
			String cbo = cbosHU.substring(pos, pos + 6);
			if (cbo == null || cbo.equals(" ") || cbo.length() < 1) {
				break;
			}
			FatTabOcupacaoCboVO vo = new FatTabOcupacaoCboVO();
			vo.setCbo(cbo);
			vo.setCboSeq(Integer.valueOf(cbosHU.substring(pos + 6, pos + 6 + 8)));
			vo.setProcessado(Boolean.FALSE);
			
			listaFatTabCboVO.add(vo);
			
			pos += 15;
		}
		return listaFatTabCboVO;
	}

	/**
	 * Forms: carrega_tab_proc_x_cbo
	 */
	private Map<Long, FatProcCboVO> carregaTabProcCbo(ControleProcessadorArquivosImportacaoSus controle) {
		//--tabela temporária para manter as informações de FAT_PROCEDIMENTOS_CBO classificado por Procedimento e CBOs atuais
		List<FatProcedimentoCbo> listaProcCbo = getFatProcedimentoCboDAO().listarProcedimentosCboAtivosOrdenadoPorCodTabelaECbo();
		Map<Long, FatProcCboVO> listaProcedimentoCbo = new HashMap<Long, FatProcCboVO>();
		
		for (FatProcedimentoCbo procCbo : listaProcCbo) {
			if (listaProcedimentoCbo.get(procCbo.getItemProcedHosp().getCodTabela()) == null) {
				FatProcCboVO vo = new FatProcCboVO();
				vo.setIphPhoSeq(procCbo.getItemProcedHosp().getId().getPhoSeq());
				vo.setIphSeq(procCbo.getItemProcedHosp().getId().getSeq());
				vo.setCbosValidos(StringUtils.rightPad(procCbo.getCbo().getCodigo(), 6, ' '));
				vo.setTabCbosValidos(vo.getCbosValidos() + StringUtils.leftPad(procCbo.getCbo().getSeq().toString(), 8, '0') + "N");
				vo.setQtdCbos(1);
				vo.setProcessado(Boolean.FALSE);
				
				listaProcedimentoCbo.put(procCbo.getItemProcedHosp().getCodTabela(), vo);
			}
			else {
				FatProcCboVO vo = listaProcedimentoCbo.get(procCbo.getItemProcedHosp().getCodTabela());
				vo.setCbosValidos(vo.getCbosValidos() + StringUtils.rightPad(procCbo.getCbo().getCodigo(), 6, ' '));
				vo.setTabCbosValidos(vo.getTabCbosValidos() + StringUtils.rightPad(procCbo.getCbo().getCodigo(), 6, ' ') + StringUtils.leftPad(procCbo.getCbo().getSeq().toString(), 8, '0') + "N");
				vo.setQtdCbos(vo.getQtdCbos() + 1);
			}
		}
		Long key = null;
		Long keyAnterior = null;
		for (FatProcedimentoCbo procCbo : listaProcCbo) { //iterar a lista do banco pra respeitar a mesma ordem do AGH
			key = procCbo.getItemProcedHosp().getCodTabela();
			if (!key.equals(keyAnterior)) {
				controle.gravarLog("Cod tabela:" + key + " cbos="  + listaProcedimentoCbo.get(key).getCbosValidos());
				keyAnterior = key;
			}
		}
		
		return listaProcedimentoCbo;
	}
	
	/**
	 * Forms: monta_registro
	 */
	@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
	private Map<Long, FatProcCboVO> montaRegistroProcedimentoCBO( final AghArquivoProcessamento  arquivoProcedimentoCBO, 
																  final StringBuilder logRetorno, 
																  final Date inicio,
																  final ControleProcessadorArquivosImportacaoSus controle,
																  final List<String> listaNomeArquivos) throws ApplicationBusinessException {
		
		final Map<Long, FatProcCboVO> mapTabProcCid = new TreeMap<Long, FatProcCboVO>();
		
		BufferedReader br = null;
		try { // Ocupação
			br = util.abrirArquivo(arquivoProcedimentoCBO);
			String strLine;
			int linha = 0;
			while ((strLine = br.readLine()) != null) {
				linha++;
				if (StringUtils.isNotEmpty(strLine)) {
					controle.incrementaNrRegistrosProcesso(4);
					try {
						
						Long codProced = Long.valueOf(strLine.substring(0, 10));
						String codOcupacao = strLine.substring(10,16);
						Date competencia = DateUtil.obterData(Integer.valueOf(strLine.substring(16,20)), Integer.valueOf(strLine.substring(20,22)) - 1, 1);
						
						if (codProced > 0) {
							if (mapTabProcCid.get(codProced) != null) {
								mapTabProcCid.get(codProced).setCbosValidos(mapTabProcCid.get(codProced).getCbosValidos() + codOcupacao);
								mapTabProcCid.get(codProced).setQtdCbos(mapTabProcCid.get(codProced).getQtdCbos() + 1);
							}
							else {
								FatProcCboVO vo = new FatProcCboVO();
								vo.setCodTabela(codProced);
								vo.setCbosValidos(codOcupacao);
								vo.setQtdCbos(1);
								vo.setCompetencia(competencia);
								
								mapTabProcCid.put(codProced, vo);
							}
						}
						
					} catch (final IndexOutOfBoundsException iae) {
						// Tamanho da linha menor q o esperado
						util.tratarExcecaoNaoLancada(iae, logRetorno, ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR
								+ "Erro ao ler Procedimento: Tamanho da linha menor que o esperado." + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]",
								linha, strLine);
					}
					if (controle.getNrRegistrosProcesso() % 500 == 0) {
						controle.setLogRetorno(logRetorno);
						util.atualizarArquivo(arquivoProcedimentoCBO, inicio, new Date(), 
												ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
												ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
						logRetorno.setLength(0);
					}
					controle.incrementaNrRegistrosProcessados();
				}
			}
			logRetorno.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Importando CBOs do SIGTAB, total registros: ").append(mapTabProcCid.size());
		} catch (final IOException io) {
			LOG.warn("Problemas na leitura dos arquivos.");
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (final IOException e) {
				LOG.error(e.getMessage());
				LOG.error(e.getMessage(), e);
			}
		}
		return mapTabProcCid;
	}

	/**
	 * Forms: monta_registro_cbo
	 */
	private List<FatTabOcupacaoVO> montaRegistroOcupacao( final AghArquivoProcessamento arquivoOcupacao, 
														  final StringBuilder logRetorno, 
														  final Date inicio, 
														  final ControleProcessadorArquivosImportacaoSus controle,
														  final List<String> listaNomeArquivos) throws ApplicationBusinessException {
		
		final List<FatTabOcupacaoVO> listaTabOcupacaoVO = new ArrayList<FatTabOcupacaoVO>();
		
		String nomeArquivoOcupacao = null;
		try {
			nomeArquivoOcupacao = FileUtil.arquivoExiste(listaNomeArquivos, arquivoOcupacao.getNome());
		} catch (final ApplicationBusinessException e1) {
			throw new ApplicationBusinessException(e1);
		}
		
		BufferedReader br = null;
		try { // Ocupação
			br = util.abrirArquivo(nomeArquivoOcupacao);
			String strLine;
			int linha = 0;
			while ((strLine = br.readLine()) != null) {
				linha++;
				if (StringUtils.isNotEmpty(strLine)) {
					controle.incrementaNrRegistrosProcesso(4);
					try {
						FatTabOcupacaoVO vo = new FatTabOcupacaoVO();
						vo.setCboOcupacao(strLine.substring(0, 6));
						vo.setCboDescricao(strLine.substring(6, 156).trim().toUpperCase());
						vo.setDescricao(vo.getCboDescricao().replace("\\d", "").trim());
						listaTabOcupacaoVO.add(vo);
					} catch (final IndexOutOfBoundsException iae) {
						// Tamanho da linha menor q o esperado
						util.tratarExcecaoNaoLancada(iae, logRetorno, ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR
								+ "Erro ao ler Procedimento: Tamanho da linha menor que o esperado." + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]",
								linha, strLine);
					}
					if (controle.getNrRegistrosProcesso() % 500 == 0) {
						controle.setLogRetorno(logRetorno);
						util.atualizarArquivo(arquivoOcupacao, inicio, new Date(), 
											   ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
											   ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
						logRetorno.setLength(0);
					}
					
					controle.incrementaNrRegistrosProcessados();
				}
			}
		} catch (final IOException io) {
			LOG.warn("Problemas na leitura dos arquivos.");
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (final IOException e) {
				LOG.error(e.getMessage());
				LOG.error(e.getMessage(), e);
			}
		}
		return listaTabOcupacaoVO;
	}
	
	public FatItensProcedHospitalar getItensProcedHospitalar(final Long codTabela) throws BaseException {
		final BigDecimal tabelaFaturPadrao = getParametroFacade().buscarValorNumerico(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
		return getFatItensProcedHospitalarDAO().obterPorCodTabelaPhoSeqAtivos(codTabela, tabelaFaturPadrao.shortValue());
	}

	private String ffccOrdenaStringCbo(final String cbosValidos) {
		// Separa a string a cada 4
		final String [] cbos = util.split(cbosValidos, 6);
		Arrays.sort(cbos);
		return StringUtils.join(cbos);
	}
	
	

	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}

	protected FatProcedimentoCboDAO getFatProcedimentoCboDAO() {
		return fatProcedimentoCboDAO;
	}

	protected FatCboDAO getFatCboDAO() {
		return fatCboDAO;
	}	
	
	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
}