package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSus.ImportarArquivoSusONExceptionCode;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSusFinanciamentoON.ProcessadorArquivosImportacaoSusFinanciamentoONExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCaractFinanciamentoDAO;
import br.gov.mec.aghu.faturamento.dao.FatFormaOrganizacaoDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.vo.FatTabProcedHospitalarVO;
import br.gov.mec.aghu.faturamento.vo.FatTabProcedHospitalarVO.TipoProcessado;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatFormaOrganizacaoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcesArqImportSusProcedHospitalarON  extends BaseBMTBusiness {

	private static final String PROCED_COD = "Procedimento cod:";
	private static final long serialVersionUID = 3284613353510976203L;

	private static final Log LOG = LogFactory.getLog(ProcesArqImportSusProcedHospitalarON.class);

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
		
	@Inject
	private FatFormaOrganizacaoDAO fatFormaOrganizacaoDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@Inject
	private FatCaractFinanciamentoDAO fatCaractFinanciamentoDAO;
	
	public Map<Long, FatTabProcedHospitalarVO> carregarProcedimento( final String sgFaturamento, 
															   final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{

		final AghArquivoProcessamento aghArquivoProcedimento = aberturaProcedimentoOK(sgFaturamento);
		
		Map<Long, FatTabProcedHospitalarVO> tabProcedimento= new TreeMap<Long, FatTabProcedHospitalarVO>();
		
		try{
			if(aghArquivoProcedimento != null){
				aghArquivoProcedimento.setDthrInicioProcessamento(controle.getInicio());
				controle.gravarLog("Iniciando atualização de tabela de procedimento em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
                                        .append("Iniciando atualização de tabela de procedimento em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				
                util.atualizarArquivo(aghArquivoProcedimento, controle.getInicio(), 0, null, 0, null, controle);
				
			
				executaLeituraProcedHospitalar(aghArquivoProcedimento, tabProcedimento, controle);
	
				try {
					ajustaRegistro(aghArquivoProcedimento, tabProcedimento, controle);
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
			if (aghArquivoProcedimento != null) {
				util.atualizarArquivo(aghArquivoProcedimento, fim, 100, 100, 0, fim, controle);
			}
		}
		
		return tabProcedimento;
	}

	@SuppressWarnings("incomplete-switch")
	private void ajustaRegistro( final AghArquivoProcessamento aghArquivoProcedimento, 
		    					final Map<Long, FatTabProcedHospitalarVO> tabProcedimento,
		    					final ControleProcessadorArquivosImportacaoSus controle) throws BaseException{
		
		// Segunda parte do processamento
		controle.incrementaNrRegistrosProcesso(tabProcedimento.keySet().size());
		
		for(Long codTabelaProced : tabProcedimento.keySet()){
			
			final FatTabProcedHospitalarVO fatTabProcedHospitalarVO = tabProcedimento.get(codTabelaProced);
			
			controle.gravarLog("Verificando Registro ..."+fatTabProcedHospitalarVO.getCodTabela()+ " processado:"+fatTabProcedHospitalarVO.getTipoProcessado());
            controle.getLogRetorno().append("Verificando Registro ..."+fatTabProcedHospitalarVO.getCodTabela()+ " processado:"+fatTabProcedHospitalarVO.getTipoProcessado());
			switch (fatTabProcedHospitalarVO.getTipoProcessado()) {			
				case ALTERA: 
					     alteraProcedimento(fatTabProcedHospitalarVO);
						controle.gravarLog("Alterado 1 Registro Codigo:"+fatTabProcedHospitalarVO.getCodTabela()+".");
						break;
			}
			
			controle.incrementaNrRegistrosProcessados();
			
			// aqui comita a transacao
			util.atualizarArquivo( aghArquivoProcedimento, controle.getInicio(), new Date(), 
					  			   ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
					  			   ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
		}
	}

	private void alteraProcedimento(final FatTabProcedHospitalarVO fatTabProcedHospitalarVO) throws ApplicationBusinessException {
		
		super.beginTransaction();
		FatItensProcedHospitalar entidade = this.fatItensProcedHospitalarDAO.buscarFatItensProcedHospitalarPorCodProcedimentoFinan(fatTabProcedHospitalarVO.getCodTabela());
		FatCaractFinanciamento financiamento = fatCaractFinanciamentoDAO.obterPorCodigoSus(fatTabProcedHospitalarVO.getSeqSusFinanciamento());
		entidade.setFatCaracteristicaFinanciamento(financiamento);
		this.fatItensProcedHospitalarDAO.atualizar(entidade);
		super.commitTransaction();
		
	}
	
	private void executaLeituraProcedHospitalar( final AghArquivoProcessamento aghArquivoProcedHospitalar,
									    final Map<Long, FatTabProcedHospitalarVO> tabProcedHospitalar,
										final ControleProcessadorArquivosImportacaoSus controle ) throws ApplicationBusinessException{
			List<String> lines = this.obterRegistroArquivoProcedimento(aghArquivoProcedHospitalar);
			if(controle.getNrRegistrosProcessadosGeral() > 0){
				controle.setNrRegistrosProcessados(controle.getNrRegistrosProcessadosGeral() + 1);
				controle.setNrRegistrosProcesso(controle.getNrRegistrosProcessoGeral() + lines.size() +1);
				
			} else {
				controle.setNrRegistrosProcesso(lines.size() + 1);
				controle.setNrRegistrosProcessados(1);
			}
			
			
			montaProcedHospitalarFinan(aghArquivoProcedHospitalar, tabProcedHospitalar, controle, lines);
			String msg = "Registros lidos: " + lines.size();
			controle.gravarLog(msg);
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(msg);
				util.atualizarArquivo(aghArquivoProcedHospitalar, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
	}
	
	private void montaProcedHospitalarFinan(final AghArquivoProcessamento aghArquivoProcedHospitalar,
			                           final Map<Long, FatTabProcedHospitalarVO> tabProcedHospitalar,
			                           final ControleProcessadorArquivosImportacaoSus controle,
			                           final List<String> lines){
		int wTotRegistro = 0;
		for (String line : lines) {
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
						   			.append("Processando tb_procedimento... "+ ++wTotRegistro);	
			
			montaProcedHospitalarFinan(aghArquivoProcedHospitalar, tabProcedHospitalar, line, wTotRegistro, controle);
		}     
    } 
	
	private void montaProcedHospitalarFinan( final AghArquivoProcessamento aghArquivoProcedHospitalar,
									   final Map<Long, FatTabProcedHospitalarVO> tabProcedHospitalar,
									   final String regProcedHospitalar, int wTotRegistro,
									   final ControleProcessadorArquivosImportacaoSus controle ){
		try {
			
			FatTabProcedHospitalarVO vo = lerLinhaArquivo(regProcedHospitalar);

            Long codTabela = vo.getCodTabela();
			if(codTabela != null && codTabela > 0){
					
				if(!tabProcedHospitalar.containsKey(codTabela)){
						FatItensProcedHospitalar itensProcedHospitalar = this.fatItensProcedHospitalarDAO.buscarFatItensProcedHospitalarPorCodProcedimentoFinan(codTabela);
						if(itensProcedHospitalar != null){
							if(itensProcedHospitalar.getCodTabela() != null && itensProcedHospitalar.getCodTabela().equals(codTabela)){
								if(itensProcedHospitalar.getFatCaracteristicaFinanciamento() == null ||
								   itensProcedHospitalar.getFatCaracteristicaFinanciamento().getSeqSus() == null ||
								   !itensProcedHospitalar.getFatCaracteristicaFinanciamento().getSeqSus().equals(vo.getSeqSusFinanciamento())){ 
									controle.gravarLog(PROCED_COD + codTabela + ' ' + vo.getDescricao()+ 
										               "Financiamento alterada  para Procedimento:"+codTabela);
									controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
			                                                .append(PROCED_COD + codTabela + ' ' + vo.getDescricao()+ 
										                            " Financiamento alterada  para Procedimento:"+codTabela);
								    vo.setTipoProcessado(TipoProcessado.ALTERA);
							    }else{
							    	controle.gravarLog(PROCED_COD + codTabela + ' ' + vo.getDescricao()+ 
										       " Financiamento sem alteração  para Procedimento:"+codTabela);
							    	controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
			                                                .append(PROCED_COD + codTabela + ' ' + vo.getDescricao()+ 
										                           "Financiamento sem alteração  para Procedimento:"+codTabela);
							    	vo.setTipoProcessado(TipoProcessado.PROCESSADO);
							    }
								
							} else {
								vo.setTipoProcessado(TipoProcessado.NAO_PROCESSADO);
							}		
							tabProcedHospitalar.put(vo.getCodTabela(), vo);
					    }
				} else {
					controle.gravarLog(PROCED_COD + codTabela + ' ' +vo.getDescricao() + ", já processado.");
					controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + PROCED_COD + codTabela + ' ' +vo.getDescricao() + ", já processado.");
				}
	
				controle.incrementaNrRegistrosProcessados();
				if (controle.getNrRegistrosProcessados() % ProcessadorArquivosImportacaoSusProcedServicoON.QT_MINIMA_ATUALIZAR_ARQUIVO == 0) {
					util.atualizarArquivo(aghArquivoProcedHospitalar, controle.getInicio(), new Date(), 
										  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
										  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
				}
			}
		} catch (final IndexOutOfBoundsException iae) {
			// Tamanho da linha menor q o esperado
			util.tratarExcecaoNaoLancada(iae, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR +
										 "Erro ao ler Registro: Tamanho da linha menor que o esperado." + 
										 ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]", 
										 wTotRegistro, wTotRegistro);
		}
	} 
	
	/* Processar linha de acordo com layout*/
	private FatTabProcedHospitalarVO lerLinhaArquivo(final String strLine) {

		final FatTabProcedHospitalarVO vo = new FatTabProcedHospitalarVO();
		
		vo.setCodTabela(Long.valueOf(strLine.substring(0, 10).trim()));
		vo.setGrupoId(strLine.substring(0, 2).trim());
		vo.setSubGrupoId(strLine.substring(2, 4).trim());
		vo.setFormaOrgId(strLine.substring(4, 6).trim());

		vo.setFatFormaOrganizacao(fatFormaOrganizacaoDAO.obterPorChavePrimaria(
																			   new FatFormaOrganizacaoId(Short.valueOf(vo.getGrupoId()), 
																					   				     Byte.valueOf(vo.getSubGrupoId()), 
																					   				     Byte.valueOf(vo.getFormaOrgId())
																					   				    )
																			  ));
		
		vo.setDescricao(strLine.substring(10, 250).trim().toUpperCase());
		vo.setSexoStr(strLine.substring(261, 262).trim().toUpperCase());
		vo.setValorShStr(strLine.substring(282, 292).trim());
		vo.setValorProcedStr(strLine.substring(292, 302).trim());
		vo.setValorSpStr(strLine.substring(302, 312).trim());
		vo.setIdadeMinStr(strLine.substring(274, 278).trim());
		vo.setIdadeMaxStr(strLine.substring(278, 282).trim());
		vo.setValorSh(new BigDecimal(vo.getValorShStr()).divide(new BigDecimal(100)));
		vo.setValorProced( new BigDecimal(vo.getValorProcedStr()).divide(new BigDecimal(100)));
		vo.setValorSp(new BigDecimal(vo.getValorSpStr()).divide(new BigDecimal(100)));

		vo.setIdadeMin(Integer.valueOf(vo.getIdadeMinStr()));
		vo.setIdadeMax(Integer.valueOf(vo.getIdadeMaxStr()));
		
		String seqSusString = strLine.substring(312, 314).trim();
		vo.setSeqSusFinanciamento(Integer.valueOf(seqSusString));
		
		String seqSusString2 = strLine.substring(260, 261).trim();
		vo.setSeqSusComplexidade(Integer.valueOf(seqSusString2));
		
		try {
			vo.setvQtMaximaExecucao(Short.valueOf(strLine.substring(262, 266).trim()));
		} catch (final NumberFormatException e) {
			vo.setvQtMaximaExecucao(null);
		}
		
		if ("I".equals(vo.getSexoStr()) || "N".equals(vo.getSexoStr())) {
			vo.setSexo(DominioSexoDeterminante.valueOf("Q"));
		} else {
			vo.setSexo(DominioSexoDeterminante.valueOf(vo.getSexoStr()));
		}

		if (vo.getIdadeMin() == 9999) {
			vo.setIdadeMin(0);
		} else {
			vo.setIdadeMin(vo.getIdadeMin() / 12);
		}

		vo.setIdadeMax(vo.getIdadeMax() / 12);
		
		return vo;
	}
	
	private AghArquivoProcessamento aberturaProcedimentoOK( final String sgFaturamento) throws ApplicationBusinessException{
		
		final String nmArquivoServ = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NOME_ARQUIVO_ITENS_PROCED_HOSP).toLowerCase();
		final AghArquivoProcessamento aghArquivoServico = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArquivoServ);
		return aghArquivoServico;
	}
    private List<String> obterRegistroArquivoProcedimento(final AghArquivoProcessamento aghArquivoProcedHospitalar) throws ApplicationBusinessException{
    	BufferedReader br = util.abrirArquivo(aghArquivoProcedHospitalar);
		
		String strLine;
		List<String> lines = new ArrayList<String>();
		try {
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}
			br.close();
		} catch (IOException e) {
			LOG.warn("Problemas na leitura dos arquivos.");
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO);			
		}
		
		return lines;
    }
	@Override
	protected Log getLogger() {
		return LOG;
	}
//Getters and Setters

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	protected ProcessadorArquivosImportacaoSusUtil getUtil() {
		return util;
	}

	protected void setUtil(ProcessadorArquivosImportacaoSusUtil util) {
		this.util = util;
	}

	protected FatFormaOrganizacaoDAO getFatFormaOrganizacaoDAO() {
		return fatFormaOrganizacaoDAO;
	}

	protected void setFatFormaOrganizacaoDAO(FatFormaOrganizacaoDAO fatFormaOrganizacaoDAO) {
		this.fatFormaOrganizacaoDAO = fatFormaOrganizacaoDAO;
	}

	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}

	protected void setFatItensProcedHospitalarDAO(FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO) {
		this.fatItensProcedHospitalarDAO = fatItensProcedHospitalarDAO;
	}

	protected FatCaractFinanciamentoDAO getFatCaractFinanciamentoDAO() {
		return fatCaractFinanciamentoDAO;
	}

	protected void setFatCaractFinanciamentoDAO(FatCaractFinanciamentoDAO fatCaractFinanciamentoDAO) {
		this.fatCaractFinanciamentoDAO = fatCaractFinanciamentoDAO;
	}
	
}