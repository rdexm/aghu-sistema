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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSus.ImportarArquivoSusONExceptionCode;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSusProcedServicoON.ProcessadorArquivosImportacaoSusProcedServicoONExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoRegistroDAO;
import br.gov.mec.aghu.faturamento.dao.FatRegistrosDAO;
import br.gov.mec.aghu.faturamento.vo.FatTabProcedRegistroVO;
import br.gov.mec.aghu.faturamento.vo.FatTabProcedRegistroVO.TipoProcessado;
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroVO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentoRegistro;
import br.gov.mec.aghu.model.FatProcedimentoRegistroId;
import br.gov.mec.aghu.model.FatRegistro;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcesArqImportSusRegistroProcedON  extends BaseBMTBusiness{

	private static final String PROCED_REGISTRO_COD = "procedimento_registro cod:";
    private static final String INSTRUMENTO_REGISTRO_EMPTY = "Instrumento de registro não encontrado para código registro: ";
	private static final String PROCEDIMENTO_EMPTY = "Procedimento não encontrado para código procedimento: ";
    private static final long serialVersionUID = 7695698187574264162L;

	private static final Log LOG = LogFactory.getLog(ProcesArqImportSusRegistroProcedON.class);
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@Inject
	private FatProcedimentoRegistroDAO fatProcedimentoRegistroDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@Inject
	private FatRegistrosDAO fatRegistrosDAO;
	
	/** Forms: carrega_serv_class  */
	public Map<String, FatTabProcedRegistroVO> carregaRegistroProced( final String sgFaturamento, 
			  					  								     final Map<String, FatTabRegistroVO> tabRegistro,
			  					  								     final ControleProcessadorArquivosImportacaoSus controle) 
			  					  								     throws ApplicationBusinessException{
		
		final AghArquivoProcessamento arquivoRegistroProced = aberturaRegistroProcedOK(sgFaturamento, parametroFacade, aghuFacade);
		
		try{
			if(arquivoRegistroProced != null){
				arquivoRegistroProced.setDthrInicioProcessamento(controle.getInicio());
				controle.gravarLog("Iniciando carga de tabela de procedimento_registro em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
				                        .append("Iniciando carga de tabela de procedimento_registro em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				util.atualizarArquivo(arquivoRegistroProced, controle.getInicio(), 0, 100, 0, null, controle);
				
				final Map<String, FatTabProcedRegistroVO> tabRegistroProced = new TreeMap<String, FatTabProcedRegistroVO>();
				
				controle.gravarLog("Iniciando limpeza da tabela FAT_PROCEDIMENTOS_REGISTRO...");
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
                .append("Iniciando limpeza da tabela FAT_PROCEDIMENTOS_REGISTRO...");
				
				int totalProcedRegisExcluidos = 0;
				totalProcedRegisExcluidos = this.limparFatProcedimentoRegistro();
				
				controle.gravarLog("Total de registro descontinuados: "+totalProcedRegisExcluidos+" registros.");
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
                                        .append("Total de registro descontinuados: "+totalProcedRegisExcluidos+" registros.");
				executaLeituraProcedRegistro(arquivoRegistroProced, tabRegistroProced, controle);
				
				ajustaProcedimentoRegistro(tabRegistro, tabRegistroProced, arquivoRegistroProced, controle);
				
				return tabRegistroProced;
				
			} else {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro na carga de tabela de serv_classs.");
				throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusProcedServicoONExceptionCode.ERRO_ARQUIVO_SERV_CLASS_NAO_ENCONTRADO);
			}
		} finally {
			final Date fim = new Date();
			if (arquivoRegistroProced != null) {
				util.atualizarArquivo(arquivoRegistroProced, fim, 100, 100, 0, fim, controle);
			}
		}
	}
	
	/** Forms: executa_leitura_serv_class  */
	private void executaLeituraProcedRegistro( final AghArquivoProcessamento arquivoProcedRegistro,
									      final Map<String, FatTabProcedRegistroVO> tabProcedRegistro,
										  final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		BufferedReader br = null;
		try {
			br = util.abrirArquivo(arquivoProcedRegistro);

			String strLine;
			List<String> lines = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}
			
			controle.incrementaNrRegistrosProcesso(lines.size() +1);
			
			int wTotProcedRegis = 0;
			for (String line : lines) {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  	       			.append("Processando rl_procedimento_registro... ").append(++wTotProcedRegis);
				
				montaProcedRegistro(arquivoProcedRegistro, tabProcedRegistro, line, wTotProcedRegis, controle);
			} 
			
			String msg = "Tabela procedimento_registro carregada com " + lines.size()+" registros";
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

				util.atualizarArquivo(arquivoProcedRegistro, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			} catch (final IOException e) {
				LOG.error(e.getMessage(), e);
			}
		} 
	}

	/** Forms: ajusta_Procedimento_Registro */
	@SuppressWarnings("incomplete-switch")
	private void ajustaProcedimentoRegistro( final Map<String, FatTabRegistroVO> tabRegistro,
		    					  final Map<String, FatTabProcedRegistroVO> tabProcedRegistro,
		    					  final AghArquivoProcessamento arquivoProcedRegistro,
		    					  final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		

		controle.incrementaNrRegistrosProcesso(tabProcedRegistro.keySet().size());
		int totalIncluidos = 0;
		int totalAlterados = 0;
		Long codProcedimento = null;
		for(String seqProcedRegistro : tabProcedRegistro.keySet()){
			beginTransaction();
			final FatTabProcedRegistroVO fatTabProcedRegistroVO = tabProcedRegistro.get(seqProcedRegistro);
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
						   			.append("Verificando procedimento_registro ...").append(fatTabProcedRegistroVO.getCodigoProcedimento()).append(" - ").append(fatTabProcedRegistroVO.getCodigoRegistro());
			switch (fatTabProcedRegistroVO.getProcessado()) {
			case INCLUI:
				codProcedimento = incluiProcedimentoRegistro(fatTabProcedRegistroVO, controle);
				if(codProcedimento != null){
					totalIncluidos++;					
				}
				break;

			case ALTERA:
				codProcedimento = alteraProcedimentoRegistro(fatTabProcedRegistroVO, controle);
				if(codProcedimento != null){
					totalAlterados++;					
				}
				break;
			}
			
			controle.incrementaNrRegistrosProcessados();
			
			fatProcedimentoRegistroDAO.flush();
			commitTransaction();
			util.atualizarArquivo(arquivoProcedRegistro, controle.getInicio(), new Date(), 
							      ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
							      ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
		}
  		controle.gravarLog("Total de registros incluidos: "+totalIncluidos);
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
		                        .append("Total de registros incluidos: "+totalIncluidos);
  		controle.gravarLog("Total de registros alterados: "+totalAlterados);
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
		                        .append("Total de registros alterados: "+totalAlterados);
	}
	
	/** Forms: incluir_procedimento_registro  */
	private Long incluiProcedimentoRegistro( final FatTabProcedRegistroVO tabProcedRegistro,
							                 final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{

		if(tabProcedRegistro != null &&
		   tabProcedRegistro.getCodigoProcedimento() != null &&
		   tabProcedRegistro.getCodigoRegistro() != null &&
		   tabProcedRegistro.getCodigoProcedimento() > 0 &&
		   tabProcedRegistro.getCodigoRegistro().trim().length() > 0){
			
			FatRegistro fatRegistro = fatRegistrosDAO.obterPorChavePrimaria(tabProcedRegistro.getCodigoRegistro());
			FatItensProcedHospitalar item = fatItensProcedHospitalarDAO.buscarFatItensProcedHospitalarPorCodProcedimento(tabProcedRegistro.getCodigoProcedimento());
            if(fatRegistro != null && item != null){
            	FatProcedimentoRegistro fatProdRegis = new FatProcedimentoRegistro();			
            	fatProdRegis.setCodProcedimento(tabProcedRegistro.getCodigoProcedimento());
            	fatProdRegis.setFatRegistro(fatRegistro);
            	FatProcedimentoRegistroId id = new FatProcedimentoRegistroId(item.getPhoSeq(),item.getSeq(),tabProcedRegistro.getCodigoRegistro());
            	fatProdRegis.setId(id);
            	fatProcedimentoRegistroDAO.persistir(fatProdRegis);
            	return fatProdRegis.getCodProcedimento();
            }else{
            	if(fatRegistro == null){
            		controle.gravarLog(INSTRUMENTO_REGISTRO_EMPTY+ tabProcedRegistro.getCodigoRegistro()+".");
            		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
            		                        .append(INSTRUMENTO_REGISTRO_EMPTY+ tabProcedRegistro.getCodigoRegistro()+".");
            	}
            	if(item == null){
            		controle.gravarLog(PROCEDIMENTO_EMPTY+tabProcedRegistro.getCodigoProcedimento()+".");
            		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
                                            .append(PROCEDIMENTO_EMPTY+tabProcedRegistro.getCodigoProcedimento()+".");
            	}
            }
		} else {
		controle.gravarLog("Problema na determinação procedimento_registro, cod:"+tabProcedRegistro.getCodigoProcedimento()+" - "+tabProcedRegistro.getCodigoRegistro());
		}
		return null;
	}
	
	/** Forms: incluir_procedimento_registro  
	 * @throws ApplicationBusinessException */
	private int limparFatProcedimentoRegistro(){
		List<FatProcedimentoRegistro> lista = this.fatProcedimentoRegistroDAO.buscarFatProcedimentoRegistro();
		if(lista != null && !lista.isEmpty()){	
			this.beginTransaction();
			this.fatProcedimentoRegistroDAO.limparFatProcedimentoRegistro();
			this.commitTransaction();
			return lista.size();
		}
		return 0;
	}
	private Long alteraProcedimentoRegistro( final FatTabProcedRegistroVO tabProcedRegistro,
							                 final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{

		if(tabProcedRegistro != null &&
		   tabProcedRegistro.getCodigoProcedimento() != null &&
		   tabProcedRegistro.getCodigoRegistro() != null &&
		   tabProcedRegistro.getCodigoProcedimento() > 0 &&
		   tabProcedRegistro.getCodigoRegistro().trim().length() > 0){
			FatRegistro fatRegistro = fatRegistrosDAO.obterPorChavePrimaria(tabProcedRegistro.getCodigoRegistro());
			FatItensProcedHospitalar item = fatItensProcedHospitalarDAO.buscarFatItensProcedHospitalarPorCodProcedimento(tabProcedRegistro.getCodigoProcedimento());
            if(fatRegistro != null && item != null){
            	FatProcedimentoRegistro fatProdRegis = new FatProcedimentoRegistro();			
            	fatProdRegis.setCodProcedimento(tabProcedRegistro.getCodigoProcedimento());
            	fatProdRegis.setFatRegistro(fatRegistro);
            	FatProcedimentoRegistroId id = new FatProcedimentoRegistroId(item.getPhoSeq(),item.getSeq(),tabProcedRegistro.getCodigoRegistro());
            	fatProdRegis.setId(id);
            	fatProcedimentoRegistroDAO.merge(fatProdRegis);  
            	return fatProdRegis.getCodProcedimento();
            }else{
            	if(fatRegistro != null){
            		controle.gravarLog("Instrumento de registro não encontrado para código registro: "+ tabProcedRegistro.getCodigoRegistro()+".");
            		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
            		                        .append("Instrumento de registro não encontrado para código registro: "+ tabProcedRegistro.getCodigoRegistro()+".");
            	}
            	if(item != null){
            		controle.gravarLog("Procedimento não encontrado para código procedimento: "+tabProcedRegistro.getCodigoProcedimento()+".");
            		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
                                            .append("Procedimento não encontrado para código procedimento: "+tabProcedRegistro.getCodigoProcedimento()+".");
            	}
            }
		} else {
		controle.gravarLog("Problema na determinação procedimento_registro, cod:"+tabProcedRegistro.getCodigoProcedimento()+" - "+tabProcedRegistro.getCodigoRegistro());
		}
		return null;
	}
	/** Forms: monta_proced_registro */
	private void montaProcedRegistro( final AghArquivoProcessamento arquivoProcedRegistro,
										 final Map<String, FatTabProcedRegistroVO> tabProcedRegistro,
										 final String regProcedRegistro, int wTotProcedRegistro,
										 final ControleProcessadorArquivosImportacaoSus controle){
		try {
			final Long vCoProcedimento = Long.parseLong(regProcedRegistro.substring(0,10).trim());
			final String vCoRegistro = regProcedRegistro.substring(10,12).trim();
			final String vKey = String.valueOf(vCoProcedimento).concat(vCoRegistro);
			if(vCoProcedimento != null && vCoProcedimento > 0){
			    FatTabProcedRegistroVO vo = new FatTabProcedRegistroVO();
				if(!tabProcedRegistro.containsKey(vKey)){			
					vo.setCodigoProcedimento(vCoProcedimento);
					vo.setCodigoRegistro(vCoRegistro);
					vo.setKeyProcedRegistro(vKey);
					vo.setProcessado(TipoProcessado.INCLUI);
				}else{
					vo = tabProcedRegistro.get(vCoProcedimento);
					vo.setCodigoRegistro(vCoRegistro);
					vo.setKeyProcedRegistro(vKey);
					vo.setProcessado(TipoProcessado.ALTERA);
				}
				tabProcedRegistro.put(vo.getKeyProcedRegistro(), vo);
			} else {
				controle.gravarLog(PROCED_REGISTRO_COD + vCoProcedimento + ' ' +vCoRegistro + " sem codigo");
			}
			
			controle.incrementaNrRegistrosProcessados();
			if (controle.getNrRegistrosProcessados() % ProcessadorArquivosImportacaoSusProcedServicoON.QT_MINIMA_ATUALIZAR_ARQUIVO == 0) {
				util.atualizarArquivo(arquivoProcedRegistro, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			}
			
		} catch (final IndexOutOfBoundsException iae) {
			// Tamanho da linha menor q o esperado
			util.tratarExcecaoNaoLancada(iae, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR +
										 "Erro ao ler Procedimento: Tamanho da linha menor que o esperado." + 
										 ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]", 
										 wTotProcedRegistro, regProcedRegistro);
		}
	}
	
	/** Forms: cursor_procedimento_registro  */
/*	private Map<Long, FatTabProcedRegistroVO> obterCursorRegistroProced(final ControleProcessadorArquivosImportacaoSus controle) {

		final List<FatProcedimentoRegistro> procedimentosRegistros = fatProcedimentoRegistroDAO.buscarFatProcedimentoRegistro();
	    Map<Long, FatTabProcedRegistroVO> tabRegistroProced = new TreeMap<Long, FatTabProcedRegistroVO>();
				
		if(procedimentosRegistros != null && procedimentosRegistros.size() > 0){	
		
			for (FatProcedimentoRegistro procedregistro : procedimentosRegistros) {
				
				final FatTabProcedRegistroVO vo = new FatTabProcedRegistroVO();
				vo.setCodigoProcedimento(procedregistro.getCodProcedimento());
				vo.setCodigoRegistro(procedregistro.getId().getCodRegistro());
				vo.setKeyProcedRegistro(String.valueOf(procedregistro.getCodProcedimento()).concat(vo.getCodigoRegistro().trim()));
				if(vo.getCodigoProcedimento() != null){
					if(vo.getCodigoProcedimento() > 0){
						if(tabRegistroProced.containsKey(vo.getCodigoProcedimento())){
							controle.gravarLog(PROCED_REGISTRO_COD+vo.getCodigoProcedimento()+ ' '+vo.getCodigoRegistro()+" duplicado.");
							fatProcedimentoRegistroDAO.remover(procedregistro);						
							controle.gravarLog(PROCED_REGISTRO_COD+vo.getCodigoProcedimento()+ ' '+vo.getCodigoRegistro()+" Removido para registro duplicado.");
						} else {
							vo.setProcessado(br.gov.mec.aghu.faturamento.vo.FatTabProcedRegistroVO.TipoProcessado.NAO_PROCESSADO);
							tabRegistroProced.put(vo.getCodigoProcedimento(), vo);
						}
					}
				}
			}
		}
		return tabRegistroProced;
	}*/
	
	/** Forms: abertura_procedimento_registro ok */
	private AghArquivoProcessamento aberturaRegistroProcedOK( final String sgFaturamento,   final IParametroFacade parametroFacade, final IAghuFacade aghuFacade) 
															throws ApplicationBusinessException{
		final String nmArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_PROCEDIMENTO_REGISTRO).toLowerCase(); 
	    AghArquivoProcessamento aghArquivo = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArquivo);
		return aghArquivo;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public ProcessadorArquivosImportacaoSusUtil getUtil() {
		return util;
	}

	public void setUtil(ProcessadorArquivosImportacaoSusUtil util) {
		this.util = util;
	}

	public FatProcedimentoRegistroDAO getFatProcedimentoRegistroDAO() {
		return fatProcedimentoRegistroDAO;
	}

	public void setFatProcedimentoRegistroDAO(FatProcedimentoRegistroDAO fatProcedimentoRegistroDAO) {
		this.fatProcedimentoRegistroDAO = fatProcedimentoRegistroDAO;
	}

	public FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}

	public void setFatItensProcedHospitalarDAO(FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO) {
		this.fatItensProcedHospitalarDAO = fatItensProcedHospitalarDAO;
	}

	public FatRegistrosDAO getFatRegistrosDAO() {
		return fatRegistrosDAO;
	}

	public void setFatRegistrosDAO(FatRegistrosDAO fatRegistrosDAO) {
		this.fatRegistrosDAO = fatRegistrosDAO;
	}


	
}