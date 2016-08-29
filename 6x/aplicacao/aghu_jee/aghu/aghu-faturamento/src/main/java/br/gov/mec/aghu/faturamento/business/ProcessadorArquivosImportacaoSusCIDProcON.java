package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.FileUtil;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.dominio.DominioIndProcessadoRecProcCid;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.business.ImportarArquivoSusON.ImportarArquivoSusONExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospIntCidDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;
import br.gov.mec.aghu.faturamento.vo.FatTabLidoVO;
import br.gov.mec.aghu.faturamento.vo.FatTabProcCidVO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.FatProcedHospIntCidId;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusCIDProcON extends BaseBMTBusiness {

	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusCIDProcON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private DataAccessService dataAcessService;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private FatProcedHospIntCidDAO fatProcedHospIntCidDAO;
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@Inject
	private VFatAssociacaoProcedimentoDAO vFatAssociacaoProcedimentoDAO;

	private static final long serialVersionUID = -5823277580174797077L;

	private static final String STRING_1 = "Verificando CIDs de ...";
	private static final String STRING_2 = " cids_validos=";
	private static final String STRING_3 = "cod_tabela:";
	private static final String STRING_4 = "Iniciando execução de atualiza_proced_int_cid em ";
	private static final String STRING_5 = "Iniciando em carrega_tab_proc_x_cid em ";
	private static final String STRING_6 = "fetch_registro em ";
	private static final String STRING_7 = "ajusta_banco em ";
	private static final String STRING_8 = "verifica_tab_proc_x_cid em ";
	private static final Object STRING_9 = "Tabela FAT_PROCED_HOSP_INT_CID lida e atualizada com sucesso.";
	private static final String STRING_10 = "Finalizada verifica_tab_proc_x_cid em ";
	private static final String STRING_11 = "Carregando CIDs do phi ... ";
	private static final String STRING_12 = "Problemas na leitura dos arquivos.";

	private static final String ERRO_INESPERADO = "Erro inesperado ";
	private static final String PHI = "Phi:";
	private static final String CIDS = " cids=";

	private static final String VERIFICANDO_PHI = "Verificando phi ";
	private static final String PROCESSANDO = " processado=";
	private static final String EXCLUIDO = " excluido.";

	private static final String PHI_DESPREZADO_SOLICITACAO = "Phi desprezado por solicitação:";
	private static final String STRING_13 = "Segunda execução para o phi:";
	private static final String STRING_14 = "cids do phi:";
	private static final String STRING_15 = "cids lidos          :";
	private static final String STRING_16 = "houve alteração, atualiza cids";
	private static final String STRING_17 = "Sem alteração";
	private static final String STRING_18 = "Inclusão do phi:";

	
	public void atualizarCidProcedimento(final ControleProcessadorArquivosImportacaoSus controle,  final List<String> listaNomeArquivos) {
		final Date inicio = new Date();
		controle.iniciarLogRetorno();
		
		final Map<Integer, DominioTipoPlano> tabModalidade;
		AghArquivoProcessamento aghArquivoModalidade = null;
		AghArquivoProcessamento aghArquivo = null;
		
		try {
			final String nomeArquivoCid = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_CID).toLowerCase();
			final String nomeArquivoModalidade = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_PROCEDIMENTOS_MODALIDADE).toLowerCase(); //"rl_procedimento_modalidade.txt";
			final String parametroFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();
			final String seqs = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_PHI_SEQ_IMPORTACAO_CID);
			final Short grupo = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
			final Short convenio = parametroFacade.buscarValorShort(AghuParametrosEnum.P_CONVENIO_SUS);
			final Short tabela = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			final String planosSus = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_IMPORTACAO_CID_PLANOS_SUS);
			
			final String[] valores = planosSus.split("\\,");
			final Byte[] planosConvenioSus = new Byte[valores.length];
			for (int i = 0; i < valores.length; i++) {
				final Byte byte1 = Byte.parseByte(valores[i]);
				planosConvenioSus[i] = byte1;
			}
			
			aghArquivoModalidade = aghuFacade.obterArquivoNaoProcessado(parametroFaturamento, nomeArquivoModalidade);
			aghArquivo = aghuFacade.obterArquivoNaoProcessado(parametroFaturamento, nomeArquivoCid);
			
			aghArquivo.setDthrInicioProcessamento(new Date(inicio.getTime()));
			final String[] phiSeqs = seqs.split("\\,");

			tabModalidade = carregaModalidade(aghArquivoModalidade, listaNomeArquivos);

			controle.gravarLog(STRING_4 + DateUtil.dataToString(inicio, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			
			controle.gravarLog(STRING_5 + DateUtil.dataToString(inicio, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			
			//-- Carrega em tab de memoria arquivo de cids validos dos procedimentos
			controle.gravarLog(STRING_6 + DateUtil.dataToString(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			util.atualizarArquivo(aghArquivo, new Date(), null, 100, 0, null, controle);
			final Map<Integer, FatTabLidoVO> tabLido = this.fetchRegistro(aghArquivo,listaNomeArquivos, inicio, tabModalidade, controle);
	
			// -- Carrega em tab de memoria tabela de procedimentos x cid
			// carrega_tab_proc_x_cid -- Carrega em tab de memoria tabela de procedimentos x cid
			final Map<Integer, FatTabProcCidVO> tabProcCid = carregaTabProcCid(aghArquivo, phiSeqs, controle);
			
			// logRetorno.append("\najusta_banco em " + DateUtil.dataToString(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			controle.gravarLog(STRING_7 + DateUtil.dataToString(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			util.atualizarArquivo(aghArquivo, new Date(), null, 100, 0, null, controle);
			
			//  -- Varre tab de memoria arquivo de cids e processa atualização da tab de memoria procedimentos x cid
			for (final Integer tabLidoKey : tabLido.keySet()) {
				// -- Varre tabela do arquivo lido classificado por cod_tabela 
				// -- Carrega em variavel todos os Cids validos de cada cod_tabela
				final FatTabLidoVO fatTabLidoVO = tabLido.get(tabLidoKey);
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_1).append(tabLidoKey);
				controle.gravarLog(STRING_3 + fatTabLidoVO.getCodTabela() + STRING_2 + fatTabLidoVO.getCidsValidos());
				
				verificaAlteracaoCids(fatTabLidoVO, tabProcCid, grupo, convenio, tabela, planosConvenioSus, phiSeqs, controle);
				
				if (controle.getNrRegistrosProcessados() % 200 == 0) {
					util.atualizarArquivo(aghArquivo, new Date(), null, 100, 0, null, controle);
				}
				controle.incrementaNrRegistrosProcessados(2);
			}
	
			controle.gravarLog(STRING_8 + DateUtil.dataToString(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			util.atualizarArquivo(aghArquivo, new Date(), null, 100, 0, null, controle);
			
			//-- Exclui procedimentos x cid a partir da tab de memoria procedimentos x cid não precessados
			verificaTabProcCid(tabProcCid, phiSeqs, controle);

			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_9);
			controle.gravarLog(STRING_10 + DateUtil.dataToString(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			
		} catch (final Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
									.append(ERRO_INESPERADO + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
									.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" + stringException + "].")
									.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
									
			controle.gravarLog(ERRO_INESPERADO + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + stringException);
			
		} finally {
			//atualiza os logs
			if (aghArquivo != null) {
				util.atualizarArquivo(aghArquivo, new Date(), 100, 100, 0, new Date(), controle);
			}
			if (aghArquivoModalidade != null) {
				util.atualizarArquivo(aghArquivoModalidade, new Date(), 100, 100, 0, new Date(), controle);
			}
		}
	}
	
	private Map<Integer, FatTabProcCidVO> carregaTabProcCid(final AghArquivoProcessamento aghArquivo,  final String[] phiSeqs, 
															final ControleProcessadorArquivosImportacaoSus controle) {
		Integer vPhiSeq = 0;
		StringBuffer vCidsValidos = new StringBuffer();
		
		final Map<Integer, FatTabProcCidVO> retorno = new HashMap<Integer, FatTabProcCidVO>();
		final List<FatTabProcCidVO> fatTabProcCidVOs = fatProcedHospIntCidDAO.buscarProcCid();
		
		controle.incrementaNrRegistrosProcesso(fatTabProcCidVOs.size() + 1);
		controle.incrementaNrRegistrosProcessados();
		
		for (final FatTabProcCidVO fatTabProcCidVO : fatTabProcCidVOs) {
			//-- Leitura de proc_x_cid classificado por phi_Seq e cids
			//-- Carrega em um unico registro de memoria todos os Cids validos de cada phi, na quebra de PHI
			if(vPhiSeq.equals(fatTabProcCidVO.getPhiSeq())) {
				// -- Enquanto não quebra o Phi, concatena em v_cids_validos cada Cid valido para o phi
				vCidsValidos.append(fatTabProcCidVO.getCid());
			} else { // if r_proc_x_cid.phi_seq <> v_phi_seq then
				if (vPhiSeq != 0) { // if v_phi_seq <> 0 then
					controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(STRING_11).append(vPhiSeq);
					if (retorno.size() % 500 == 0) {
						util.atualizarArquivo(aghArquivo, new Date(), null, 100, 0, null, controle);
					}
					retorno.put(vPhiSeq, new FatTabProcCidVO(vPhiSeq, DominioIndProcessadoRecProcCid.N, vCidsValidos.toString()));
					controle.incrementaNrRegistrosProcesso(2);
				}
				vPhiSeq = fatTabProcCidVO.getPhiSeq();
				vCidsValidos = new StringBuffer(fatTabProcCidVO.getCid());
			}
			controle.incrementaNrRegistrosProcessados();
		}
		if (vPhiSeq != 0) { // if v_phi_seq <> 0 then
			retorno.put(vPhiSeq, new FatTabProcCidVO(vPhiSeq, null, vCidsValidos.toString()));
		}
		
		for (final Integer phoSeqKey : retorno.keySet()) {
			// if tab_proc_x_cid(ind).phi_seq IN (9466, 10207,9513,9514,29691,9841,6716,6732,7845,7858,28715) then
			final FatTabProcCidVO tabProcCidI = retorno.get(phoSeqKey);
			if (estaNaLista(phoSeqKey, phiSeqs)) {
				tabProcCidI.setProcessado(DominioIndProcessadoRecProcCid.D);
			}
			// logRetorno.append("\nPhi:").append(tabProcCidI.getPhiSeq()).append(" cids=").append(tabProcCidI.getCidsValidos());
			controle.gravarLog(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + PHI + tabProcCidI.getPhiSeq() + CIDS + tabProcCidI.getCidsValidos());
			controle.incrementaNrRegistrosProcessados();
		}
		util.atualizarArquivo(aghArquivo, new Date(), null, 100, 0, null, controle);
		return retorno;
	}

	private Map<Integer, DominioTipoPlano> carregaModalidade( final AghArquivoProcessamento aghArquivo, final List<String> listaNomeArquivos) throws ApplicationBusinessException {

		List<String> lista = processaRegistrosModalidade(aghArquivo, listaNomeArquivos);
		Map<Integer, DominioTipoPlano> mapModalidade = new HashMap<Integer, DominioTipoPlano>();
		for (String item : lista) {
			Integer vSeq = Integer.valueOf(item.subSequence(0, 10).toString());
			DominioTipoPlano modalidade = null;
			if (item.substring(11,12).equals("1") || item.substring(11,12).equals("5")) {
				modalidade = DominioTipoPlano.A;
			}
			else {
				modalidade = DominioTipoPlano.I;
			}
			
			
			if (vSeq > 0) {
				if (mapModalidade.containsKey(vSeq)) {
					if (!CoreUtil.igual(modalidade, mapModalidade.get(vSeq))) {
						mapModalidade.put(vSeq, null);
					}
					else {
						if (mapModalidade.get(vSeq) != null) {
							mapModalidade.put(vSeq, modalidade);
						}
					}
				}
				else {
					mapModalidade.put(vSeq, modalidade);
				}
			}
		}
		
		return mapModalidade;
		
	}
	
	public List<String> processaRegistrosModalidade(final AghArquivoProcessamento arquivo,final List<String> listaNomeArquivos) throws ApplicationBusinessException {
		
		String nomeArquivoModalidades = null;
		try {
			nomeArquivoModalidades = FileUtil.arquivoExiste(listaNomeArquivos, arquivo.getNome());
		} catch (final ApplicationBusinessException e1) {
			throw new ApplicationBusinessException(e1);
		}
		
		
		
		BufferedReader br = null;
		final List<String> lista = new ArrayList<String>();
		try { // Modalidade
			br = util.abrirArquivo(nomeArquivoModalidades);
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (StringUtils.isNotEmpty(strLine)) {
					lista.add(strLine);
				}
			}
		} catch (final IOException io) {
			LOG.warn(STRING_12);
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
		return lista;
	}

	private void verificaTabProcCid( final Map<Integer, FatTabProcCidVO> tabProcCid, final String[] phiSeqs, 
									 final ControleProcessadorArquivosImportacaoSus controle) {
		
		// -- Varre toda a tabela tab_proc_x_cid e exclui cids dos phis não processados
		for (final Integer phiSeqKey : tabProcCid.keySet()) {
			final FatTabProcCidVO fatTabProcCidVO = tabProcCid.get(phiSeqKey);
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  				.append(VERIFICANDO_PHI).append(fatTabProcCidVO.getPhiSeq())
					  				.append(PROCESSANDO).append(fatTabProcCidVO.getProcessado());
			
			controle.gravarLog(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + 
							VERIFICANDO_PHI + fatTabProcCidVO.getPhiSeq()   + 
							PROCESSANDO + fatTabProcCidVO.getProcessado());
			
			if (DominioIndProcessadoRecProcCid.N.equals(fatTabProcCidVO.getProcessado())) {
				controle.gravarLog(VERIFICANDO_PHI + fatTabProcCidVO.getPhiSeq() + PROCESSANDO + fatTabProcCidVO.getProcessado() + EXCLUIDO);
				excluiCids(fatTabProcCidVO.getPhiSeq(), phiSeqs, controle);
			}
			controle.incrementaNrRegistrosProcessados();
		}
	}

	private void verificaAlteracaoCids(final FatTabLidoVO fatTabLidoVO, final Map<Integer, FatTabProcCidVO> tabProcCid,
									   final Short grupo, final Short convenio, final Short tabela, 
									   final Byte[] planosConvenioSus, final String[] phiSeqs,
									   final ControleProcessadorArquivosImportacaoSus controle) {
		
		final Integer codTabela = fatTabLidoVO.getCodTabela();
		final String cidsValidos = fatTabLidoVO.getCidsValidos();
		final DominioTipoPlano modalidade = fatTabLidoVO.getModalidade();
		
		final List<Integer> listaPhiSeq = getVFatAssociacaoProcedimentoDAO().getPHIs( codTabela.longValue(), 
																					  grupo, convenio, tabela, planosConvenioSus);
		
		// -- para cada phi relacionado com o cod_tabela em verificação, compara is cids validos
		for (final Integer phiSeq : listaPhiSeq) {
			
			controle.gravarLog(VERIFICANDO_PHI + phiSeq);
			
			if (tabProcCid.containsKey(phiSeq)) {
				final FatTabProcCidVO fatTabProcCidVO = tabProcCid.get(phiSeq);
				
				// -- Só processa phis que não devem ser desprezados
				if (DominioIndProcessadoRecProcCid.D.equals(fatTabProcCidVO.getProcessado())) {
					
					controle.gravarLog(PHI_DESPREZADO_SOLICITACAO + phiSeq);
					
				} else if (DominioIndProcessadoRecProcCid.S.equals(fatTabProcCidVO.getProcessado())) {
					
					controle.gravarLog(STRING_13 + StringUtils.rightPad(phiSeq.toString(), 8, ' ') + CIDS + cidsValidos);
					atualizaCids(phiSeq, cidsValidos, ProcessadorArquivosImportacaoSusUtil.INCLUI, phiSeqs, modalidade, controle);
					
				} else {
					// -- marca em tab_proc_x_cid que o phi foi processado, para posteriormente deletar os não processados
					fatTabProcCidVO.setProcessado(DominioIndProcessadoRecProcCid.S);
					
					// -- ordena em v_cids_ordenados os cids passados no parametros p_cids para permitir comparar com
					// -- cids_validos de tab_proc_x_cid
					controle.gravarLog(STRING_14 + StringUtils.rightPad(phiSeq.toString(), 8, ' ') + " " + fatTabProcCidVO.getCidsValidos());
					controle.gravarLog(STRING_15 + cidsValidos);
					final String vCidsOrdenados = this.ffccOrdenaString(cidsValidos);
					
					if (!vCidsOrdenados.equals(fatTabProcCidVO.getCidsValidos())) {
						// -- se houve alteração, atualiza cids em FAT_PROCED_HOSP_INT_CID
						controle.gravarLog(STRING_16);
						atualizaCids(phiSeq, vCidsOrdenados, ProcessadorArquivosImportacaoSusUtil.ALTERA, phiSeqs, modalidade, controle);
					} else {
						controle.gravarLog(STRING_17);
					}
				}
			} else {
				controle.gravarLog(STRING_18 + StringUtils.rightPad(phiSeq.toString(), 8, ' ') + CIDS + cidsValidos);
				atualizaCids( phiSeq, cidsValidos, ProcessadorArquivosImportacaoSusUtil.INCLUI, phiSeqs, modalidade, controle);
			}
		}
	}

	private void atualizaCids( final Integer phiSeq, final String pCidsValidos, final String pTipo, final String[] phiSeqs,
							   final DominioTipoPlano modalidade, final ControleProcessadorArquivosImportacaoSus controle) {
		
		if (ProcessadorArquivosImportacaoSusUtil.ALTERA.equals(pTipo)) {
			excluiCids(phiSeq, phiSeqs, controle);
		}
		
		final String[] cids = util.split(pCidsValidos, 4);
		for (final String cid : cids) {
			
			String cidProcessado = StringUtil.rightTrim(cid);
			if (cidProcessado.length() > 3) {
				cidProcessado = cidProcessado.substring(0, 3) + "." + cidProcessado.substring(3, cidProcessado.length());
			}
			
			final Integer cidSeq = getAghuFacade().buscaCidPorCodigoComPonto(cidProcessado);
			if (cidSeq != null) {
				try {
					super.beginTransaction();
					
					controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Incluindo cid ").append(cidSeq);
					controle.gravarLog("Incluindo cid " + cidSeq);
					
					FatProcedHospIntCidId id = new FatProcedHospIntCidId(phiSeq, cidSeq);
					FatProcedHospIntCid hospIntCid = new FatProcedHospIntCid(id, modalidade, true);
					
					dataAcessService.persist(hospIntCid);
					dataAcessService.flush();
					super.commitTransaction();
					
				} catch (final Exception e) {
					super.rollbackTransaction();
					controle.gravarLog("Erro, cid duplicado, phi:" + phiSeq + " cid:" + cidSeq);
				}
			}
		}
	}

	private void excluiCids(final Integer phiSeq, final String[] phiSeqs, final ControleProcessadorArquivosImportacaoSus controle) {
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Excluindo cid do phi ").append(phiSeq);
		controle.gravarLog("Excluindo cid do phi " + phiSeq);
		super.beginTransaction();
		getFatProcedHospIntCidDAO().deleteByPhiSeqs(phiSeq, phiSeqs);
		super.commitTransaction();
	}

	private String ffccOrdenaString(final String cidsValidos) {
		// Separa a string a cada 4
		final String [] cids = util.split(cidsValidos, 4);
		Arrays.sort(cids);
		return StringUtils.join(cids);
	}
	
	private SortedMap<Integer, FatTabLidoVO> fetchRegistro( final AghArquivoProcessamento aghArquivo, final List<String> listaNomeArquivos, 
														    final Date inicio, 
															final Map<Integer, DominioTipoPlano> tabModalidade, 
															final ControleProcessadorArquivosImportacaoSus controle) {
		List<String> arq = null;
		try {
			arq = util.processaRegistrosProcedimento(aghArquivo, listaNomeArquivos, inicio, controle);
		} catch (final Exception e) {
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Problemas na leitura do arquivo: ").append(aghArquivo.getNome());
			LOG.error(e.getMessage(), e);
			util.atualizarArquivo(aghArquivo,  new Date(), null, 0, 0, new Date(), controle);
		}
		
		controle.incrementaNrRegistrosProcesso((arq.size() * 2) + 1);
		controle.incrementaNrRegistrosProcessados();
		
		final SortedMap<Integer, FatTabLidoVO> retorno = new TreeMap<Integer, FatTabLidoVO>();
		if (arq != null) {
			Integer cont = 0;
			for (final String strLine : arq) {
				if (strLine.length() < 16) {
					LOG.error(String.format(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Erro ao ler Procedimento: Tamanho da linha menor que o esperado." +
										   ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [%s]:[%s]" + 
										   ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR, cont, strLine));
				} else {
					final String codTabelaStr = strLine.substring(0, 10).trim();
					final String codCid = StringUtils.rightPad(strLine.substring(10, 14).trim(), 4, ' ');
					final String stPrincipal = strLine.substring(14, 15);
					final DominioTipoPlano modalidade = determinaModalidade(Integer.valueOf(codTabelaStr), tabModalidade);
					// -- Só processa CIDs com indicador de principal
					if ("S".equalsIgnoreCase(stPrincipal)) {
						cont ++;
						controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Processando rl_procedimento_cid ...").append(cont);
						Integer codTabela = 0;
						try {
							codTabela = Integer.valueOf(codTabelaStr);
						} catch (final NumberFormatException e) {
							LOG.error(String.format(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "codTabela inválido: %s", codTabelaStr));
						}
						// if w_tpc_cod_tabela > 0 then
						if (codTabela > 0) {
							if (retorno.containsKey(codTabela)) {
								final FatTabLidoVO tabLidoVO = retorno.get(codTabela);
								StringBuffer sb = new StringBuffer(tabLidoVO.getCidsValidos());
								sb.append(codCid);
								tabLidoVO.setCidsValidos(sb.toString());
							} else {
								retorno.put(codTabela, new FatTabLidoVO(codTabela, stPrincipal, codCid, modalidade));
							}
						}
						if(cont % 2000 == 0){
							util.atualizarArquivo(aghArquivo, new Date(), null, 100, 0, null, controle);
						}
					}
				}
				controle.incrementaNrRegistrosProcessados();
			}
		}
		
		return retorno;
	}
	

	private DominioTipoPlano determinaModalidade(Integer codTabela, final Map<Integer, DominioTipoPlano> tabModalidade) {
		
		if (codTabela > 0) {
			if (tabModalidade.containsKey(codTabela)) {
				return tabModalidade.get(codTabela);
			}
		}
		return null;
	}

	protected boolean estaNaLista(final Integer param, final String[] phiSeqs) {
		for (int x = 0; x < phiSeqs.length; x++) {
			if (phiSeqs[x].equals(param.toString())) {
				return true;
			}
		}
		return false;
	}

	
	protected VFatAssociacaoProcedimentoDAO getVFatAssociacaoProcedimentoDAO() {
		return vFatAssociacaoProcedimentoDAO;
	}

	protected FatProcedHospIntCidDAO getFatProcedHospIntCidDAO() {
		return fatProcedHospIntCidDAO;
	}	

	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
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
