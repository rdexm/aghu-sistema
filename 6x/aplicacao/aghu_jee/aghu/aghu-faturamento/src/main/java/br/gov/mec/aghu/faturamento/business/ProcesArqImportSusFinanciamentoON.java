package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSus.ImportarArquivoSusONExceptionCode;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSusFinanciamentoON.ProcessadorArquivosImportacaoSusFinanciamentoONExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCaractFinanciamentoDAO;
import br.gov.mec.aghu.faturamento.vo.FatTabFinanciamentoVO;
import br.gov.mec.aghu.faturamento.vo.FatTabFinanciamentoVO.TipoProcessado;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatCaractFinanciamento;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcesArqImportSusFinanciamentoON  extends BaseBMTBusiness {

	private static final String FINANCIAMENTO_COD = "Financiamento cod:";

	private static final long serialVersionUID = 3284613353510976203L;

	private static final Log LOG = LogFactory.getLog(ProcesArqImportSusFinanciamentoON.class);

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private FatCaractFinanciamentoRN fatCaractFinanciamentoRN;
	
	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@Inject
	private FatCaractFinanciamentoDAO fatCaractFinanciamentoDAO;
		

	/** Forms: carregar_financiamento  */ 
	//
	public Map<Integer, FatTabFinanciamentoVO> carregarFinanciamento( final String sgFaturamento, 
															   final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{

		final AghArquivoProcessamento aghArquivoFinanciamento = aberturaFinanciamentoOK(sgFaturamento);
		
		Map<Integer, FatTabFinanciamentoVO> tabFinanciamento= null;
		
		try{
			if(aghArquivoFinanciamento != null){
				aghArquivoFinanciamento.setDthrInicioProcessamento(controle.getInicio());
				controle.gravarLog("Iniciando carga de tabela de Financiamento em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
				                        .append("Iniciando carga de tabela de Financiamento em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				util.atualizarArquivo(aghArquivoFinanciamento, controle.getInicio(), 0, null, 0, null, controle);
				
				// Carrega em tab de memoria tabela Registro
				tabFinanciamento =  obterCursorFinanciamento(controle);
				
				executaLeituraFinanciamento(aghArquivoFinanciamento, tabFinanciamento, controle);
	
				try {
					ajustaRegistro(aghArquivoFinanciamento, tabFinanciamento, controle);
				} catch (BaseException e) {
					 throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusFinanciamentoONExceptionCode.ERRO_ARQUIVO_FINANCIAMENTO_NAO_ENCONTRADO);
				}
				
			} else {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  		   			.append("Erro na carga de tabela de Financiamento.");
				
		       throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusFinanciamentoONExceptionCode.ERRO_ARQUIVO_FINANCIAMENTO_NAO_ENCONTRADO);
			}

		} finally {
			final Date fim = new Date();
			if (aghArquivoFinanciamento != null) {
				util.atualizarArquivo(aghArquivoFinanciamento, fim, 100, 100, 0, fim, controle);
			}
		}
		
		return tabFinanciamento;
	}

	/** Forms: ajusta_servico 
	 * @throws BaseException */
	@SuppressWarnings("incomplete-switch")
	private void ajustaRegistro( final AghArquivoProcessamento aghArquivoFinanciamento, 
		    					final Map<Integer, FatTabFinanciamentoVO> tabFinanciamento,
		    					final ControleProcessadorArquivosImportacaoSus controle) throws BaseException{
		
		// Segunda parte do processamento
		controle.incrementaNrRegistrosProcesso(tabFinanciamento.keySet().size());
		
		for(Integer seqFinancimaneto : tabFinanciamento.keySet()){
			
			final FatTabFinanciamentoVO fatTabFinanciamentoVO = tabFinanciamento.get(seqFinancimaneto);
			
			controle.gravarLog("Verificando Registro ..."+fatTabFinanciamentoVO.getSeqSus()+ " processado:"+fatTabFinanciamentoVO.getProcessado());
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
			                        .append("Verificando Registro ..."+fatTabFinanciamentoVO.getSeqSus()+ " processado:"+fatTabFinanciamentoVO.getProcessado());
			switch (fatTabFinanciamentoVO.getProcessado()) {
			
				case INCLUI: 
					   incluiFinanciamento(fatTabFinanciamentoVO); 
						controle.gravarLog("Carga em 1 Registro Codigo:"+fatTabFinanciamentoVO.getCodigo()+".");
						controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
                                                .append("Carga em 1 Registro Codigo:"+fatTabFinanciamentoVO.getCodigo()+" .");
						
						break;
					
				case ALTERA: 
					    alteraFinanciamento(fatTabFinanciamentoVO);
						controle.gravarLog("Alterado 1 Registro Codigo:"+fatTabFinanciamentoVO.getCodigo()+".");
						controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
                                                .append("Alterado 1 Registro Codigo:"+fatTabFinanciamentoVO.getCodigo()+" .");
						break;
			}
			
			controle.incrementaNrRegistrosProcessados();
			
			// aqui comita a transacao
			util.atualizarArquivo( aghArquivoFinanciamento, controle.getInicio(), new Date(), 
					  			   ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
					  			   ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
		}
	}

	/** Forms: altera_servico  */
	private void alteraFinanciamento(final FatTabFinanciamentoVO fatTabFinanciamentoVO) throws ApplicationBusinessException {
		
		super.beginTransaction();
		final FatCaractFinanciamento entidade = fatCaractFinanciamentoDAO.obterPorChavePrimaria(fatTabFinanciamentoVO.getSeq());
		entidade.setIndSituacao(DominioSituacao.A);
		this.fatCaractFinanciamentoRN.alterar(entidade);
		super.commitTransaction();
		
	}
	
	/** Forms: inclui_servico  
	 * @throws BaseException */
	private void incluiFinanciamento(final FatTabFinanciamentoVO fatTabFinanciamentoVO) throws BaseException{
		super.beginTransaction();
		final FatCaractFinanciamento entidade = new FatCaractFinanciamento();
		entidade.setSeqSus(fatTabFinanciamentoVO.getSeqSus());
		entidade.setCodigo(fatTabFinanciamentoVO.getSeqSus().toString());
		entidade.setDescricao(fatTabFinanciamentoVO.getDescricao());
		entidade.setIndSituacao(DominioSituacao.A);

		this.fatCaractFinanciamentoRN.persistir(entidade);
		
		super.commitTransaction();
	}
	
	/** Forms: cursor_financiamento  */
	private Map<Integer, FatTabFinanciamentoVO> obterCursorFinanciamento( final ControleProcessadorArquivosImportacaoSus controle) {
		
		final List<FatCaractFinanciamento> financiamentosAtuais;
		financiamentosAtuais = this.fatCaractFinanciamentoDAO.buscaListaFatCaractFinanciamentoAtivos();
		final Map<Integer, FatTabFinanciamentoVO> tabFinanciamento = new TreeMap<Integer, FatTabFinanciamentoVO>();
		
		int wTotRegistro = 0;
		for (FatCaractFinanciamento fatFinanciamento : financiamentosAtuais) {
			if(fatFinanciamento.getSeqSus() != null){
				
				if(tabFinanciamento.containsKey(fatFinanciamento.getSeqSus())){
					controle.gravarLog(FINANCIAMENTO_COD+fatFinanciamento.getSeqSus()+' '+fatFinanciamento.getDescricao()+" duplicado, descricao:"+tabFinanciamento.get(fatFinanciamento.getSeqSus()).getDescricao());
					controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					                        .append(FINANCIAMENTO_COD+fatFinanciamento.getSeqSus()+' '+fatFinanciamento.getDescricao()+" duplicado, descricao:"+tabFinanciamento.get(fatFinanciamento.getSeqSus()).getDescricao());
				} else {
					FatTabFinanciamentoVO fatTabFinanciamentoVO =	new FatTabFinanciamentoVO();

					fatTabFinanciamentoVO.setSeq(fatFinanciamento.getSeq());
					fatTabFinanciamentoVO.setCodigo(fatFinanciamento.getCodigo());
					fatTabFinanciamentoVO.setDescricao(fatFinanciamento.getDescricao());
					fatTabFinanciamentoVO.setSeqSus((fatFinanciamento.getSeqSus()));
					fatTabFinanciamentoVO.setProcessado(TipoProcessado.NAO_PROCESSADO);
					
					tabFinanciamento.put(fatFinanciamento.getSeqSus(), fatTabFinanciamentoVO);
								  
					wTotRegistro++;
					controle.gravarLog(StringUtils.leftPad(String.valueOf(wTotRegistro), 3)+' '+fatTabFinanciamentoVO.getSeqSus()+' '+fatTabFinanciamentoVO.getDescricao());
				}
			}
		}
		
		String msg = "Tabela Financiamento carregada com "+wTotRegistro + " registros:";
		controle.gravarLog(msg);
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(msg);

		return tabFinanciamento;
	}
	
	/** Forms: executa_leitura_financiamento  */
	private void executaLeituraFinanciamento( final AghArquivoProcessamento aghArquivoFinanciamento,
									    final Map<Integer, FatTabFinanciamentoVO> tabFinanciamento,
										final ControleProcessadorArquivosImportacaoSus controle ) throws ApplicationBusinessException{
		BufferedReader br = null;
		try {
			br = util.abrirArquivo(aghArquivoFinanciamento);
			
			String strLine;
			List<String> lines = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}

			if(controle.getNrRegistrosProcessadosGeral() > 0){
				controle.setNrRegistrosProcessados(controle.getNrRegistrosProcessadosGeral() + 1);
				controle.setNrRegistrosProcesso(controle.getNrRegistrosProcessoGeral() + lines.size() +1);
				
			} else {
				controle.setNrRegistrosProcesso(lines.size() + 1);
				controle.setNrRegistrosProcessados(1);
			}
			
			
			int wTotRegistro = 0;
			for (String line : lines) {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
							   			.append("Processando tb_financiamento... "+ ++wTotRegistro);	
				
				montaFinanciamentos(aghArquivoFinanciamento, tabFinanciamento, line, wTotRegistro, controle);
			}
			
			String msg = "Registros lidos: " + lines.size();
			controle.gravarLog(msg);
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(msg);
			
		} catch (final IOException io) {
			LOG.warn("Problemas na leitura dos arquivos.");
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO);
			
		} finally {
			try {
				if (br != null) {
					br.close();
				}

				util.atualizarArquivo(aghArquivoFinanciamento, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			} catch (final IOException e) {
				LOG.error(e.getMessage());
				LOG.error(e.getMessage(), e);
			}
		} 
	}
	
	/** Forms: monta_financiamentos */
	private void montaFinanciamentos( final AghArquivoProcessamento aghArquivofinanciamento,
									   final Map<Integer, FatTabFinanciamentoVO> tabFinanciamento,
									   final String regFinancimento, int wTotRegistro,
									   final ControleProcessadorArquivosImportacaoSus controle ){
		try {
			
			final String  vCoFinanciamento = regFinancimento.substring(0,2).trim();
			final String vNoFinanciamento = regFinancimento.substring(2,101).trim();

            Integer coFinanciamento = Integer.parseInt(vCoFinanciamento);
			if(coFinanciamento != null && coFinanciamento > 0){
				
				if(tabFinanciamento.containsKey(coFinanciamento)){
					final FatTabFinanciamentoVO fatTabFinanciamentoVO = tabFinanciamento.get(coFinanciamento);
					
					final String aux = String.valueOf(CoreUtil.nvl(fatTabFinanciamentoVO.getDescricao(),StringUtils.EMPTY));
					
					if(!StringUtils.equalsIgnoreCase(vNoFinanciamento, aux)){
						controle.gravarLog(FINANCIAMENTO_COD + coFinanciamento + ' ' + fatTabFinanciamentoVO.getDescricao()+ 
								       " descricao alterada  para:"+vNoFinanciamento);
						controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
						                        .append(FINANCIAMENTO_COD + coFinanciamento + ' ' + fatTabFinanciamentoVO.getDescricao()+ 
													       " descricao alterada  para:"+vNoFinanciamento); 
						
						fatTabFinanciamentoVO.setDescricao(vNoFinanciamento);
						fatTabFinanciamentoVO.setProcessado(TipoProcessado.ALTERA);
						
					} else {
						fatTabFinanciamentoVO.setProcessado(TipoProcessado.PROCESSADO);
					}
					
				} else {
					final FatTabFinanciamentoVO vo = new FatTabFinanciamentoVO();
					vo.setSeq(null);
					vo.setSeqSus(coFinanciamento);
					vo.setCodigo(vCoFinanciamento);
					vo.setDescricao(vNoFinanciamento);
					vo.setProcessado(TipoProcessado.INCLUI);
					controle.gravarLog("Novo Registro para financiamento cod:" + vCoFinanciamento + ' ' + vNoFinanciamento);
					controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
                                            .append("Novo Registro para financiamento cod:" + vCoFinanciamento + ' ' + vNoFinanciamento);
					tabFinanciamento.put(coFinanciamento, vo);
				}
			} else {
				controle.gravarLog(FINANCIAMENTO_COD + coFinanciamento + ' ' +vNoFinanciamento + " sem codigo");
			}

			controle.incrementaNrRegistrosProcessados();
			if (controle.getNrRegistrosProcessados() % ProcessadorArquivosImportacaoSusProcedServicoON.QT_MINIMA_ATUALIZAR_ARQUIVO == 0) {
				util.atualizarArquivo(aghArquivofinanciamento, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			}
			
		} catch (final IndexOutOfBoundsException iae) {
			// Tamanho da linha menor q o esperado
			util.tratarExcecaoNaoLancada(iae, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR +
										 "Erro ao ler Registro: Tamanho da linha menor que o esperado." + 
										 ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]", 
										 wTotRegistro, regFinancimento);
		}
	} 
	
	/** Forms: abertura_financiamento */
	private AghArquivoProcessamento aberturaFinanciamentoOK( final String sgFaturamento) throws ApplicationBusinessException{
		
		final String nmArquivoServ = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_FINANCIAMENTO).toLowerCase();
		final AghArquivoProcessamento aghArquivoServico = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArquivoServ);
		return aghArquivoServico;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public FatCaractFinanciamentoRN getFatCaractFinanciamentoRN() {
		return fatCaractFinanciamentoRN;
	}

	public void setFatCaractFinanciamentoRN(FatCaractFinanciamentoRN fatCaractFinanciamentoRN) {
		this.fatCaractFinanciamentoRN = fatCaractFinanciamentoRN;
	}

	public ProcessadorArquivosImportacaoSusUtil getUtil() {
		return util;
	}

	public void setUtil(ProcessadorArquivosImportacaoSusUtil util) {
		this.util = util;
	}

	public FatCaractFinanciamentoDAO getFatCaractFinanciamentoDAO() {
		return fatCaractFinanciamentoDAO;
	}

	public void setFatCaractFinanciamentoDAO(FatCaractFinanciamentoDAO fatCaractFinanciamentoDAO) {
		this.fatCaractFinanciamentoDAO = fatCaractFinanciamentoDAO;
	}
	

}