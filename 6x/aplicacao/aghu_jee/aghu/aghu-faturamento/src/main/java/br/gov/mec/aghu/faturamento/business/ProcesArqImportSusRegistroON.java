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
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSus.ImportarArquivoSusONExceptionCode;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSusProcedRegistroON.ProcessadorArquivosImportacaoSusProcedRegistroONExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatRegistrosDAO;
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroVO;
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroVO.TipoProcessado;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatRegistro;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcesArqImportSusRegistroON  extends BaseBMTBusiness {

	private static final String REGISTRO_COD = "Registro cod:";

	private static final long serialVersionUID = 3284613353510976203L;

	private static final Log LOG = LogFactory.getLog(ProcesArqImportSusRegistroON.class);

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private FatRegistrosRN fatRegistrosRN;
	
	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@Inject
	private FatRegistrosDAO fatRegistrosDAO;
		

	/** Forms: carrega_servico  */ 
	//
	public Map<String, FatTabRegistroVO> carregaRegistro( final String sgFaturamento, 
															   final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{

		final AghArquivoProcessamento aghArquivoRegistro = aberturaRegistroOK(sgFaturamento);
		
		Map<String, FatTabRegistroVO> tabRegistro = null;
		
		try{
			if(aghArquivoRegistro != null){
				aghArquivoRegistro.setDthrInicioProcessamento(controle.getInicio());
				controle.gravarLog("Iniciando carga de tabela de Registro em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
		   			.append("Iniciando carga de tabela de Registro em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				util.atualizarArquivo(aghArquivoRegistro, controle.getInicio(), 0, null, 0, null, controle);
				
				// Carrega em tab de memoria tabela Registro
				tabRegistro =  obterCursorRegistro(controle);
				
				executaLeituraRegsitro(aghArquivoRegistro, tabRegistro, controle);
	
				ajustaRegistro(aghArquivoRegistro, tabRegistro, controle);
				
			} else {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  		   			.append("Erro na carga de tabela de registros.");
				
				//throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusProcedRegistroONExceptionCode.ERRO_ARQUIVO_SERVICO_NAO_ENCONTRADO);
				throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusProcedRegistroONExceptionCode.ERRO_ARQUIVO_REGISTRO_NAO_ENCONTRADO);
			}

		} finally {
			final Date fim = new Date();
			if (aghArquivoRegistro != null) {
				util.atualizarArquivo(aghArquivoRegistro, fim, 100, 100, 0, fim, controle);
			}
		}
		
		return tabRegistro;
	}

	/** Forms: ajusta_servico */
	@SuppressWarnings("incomplete-switch")
	private void ajustaRegistro( final AghArquivoProcessamento aghArquivoRegistro, 
		    					final Map<String, FatTabRegistroVO> tabRegistro,
		    					final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		// Segunda parte do processamento
		controle.incrementaNrRegistrosProcesso(tabRegistro.keySet().size());
		
		for(String seqRegistro : tabRegistro.keySet()){
			
			final FatTabRegistroVO fatTabRegistro = tabRegistro.get(seqRegistro);
			
			controle.gravarLog("Verificando Registro ..."+fatTabRegistro.getCodigo()+ " processado:"+fatTabRegistro.getProcessado());

			// FORMS: verifica_alteracao_servico;
			switch (fatTabRegistro.getProcessado()) {
			
				case INCLUI: 
						incluiRegistro(fatTabRegistro); 
						break;
					
				case ALTERA: 
					    alteraRegistro(fatTabRegistro, true);
						controle.gravarLog("Inativados 1 Registro Codigo:"+fatTabRegistro.getCodigo()+" por alteração.");
						break;
					
				case NAO_PROCESSADO: 
					    alteraRegistro(fatTabRegistro, false);
						controle.gravarLog("Inativados 1 Registro Codigo:"+fatTabRegistro.getCodigo());
						break; 
			}
			
			controle.incrementaNrRegistrosProcessados();
			
			// aqui comita a transacao
			util.atualizarArquivo( aghArquivoRegistro, controle.getInicio(), new Date(), 
					  			   ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
					  			   ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
		}
	}

	/** Forms: altera_servico inativa_servico */
	private void alteraRegistro(final FatTabRegistroVO fatTabRegistro, final boolean incluirRegistro) throws ApplicationBusinessException {
		
		super.beginTransaction();
		final FatRegistro registro = fatRegistrosDAO.obterPorChavePrimaria(fatTabRegistro.getCodigo());
		registro.setIndSituacao(DominioSituacao.I);
		fatRegistrosRN.persistirFatRegistros(registro);
		super.commitTransaction();
		
		if(incluirRegistro){
			incluiRegistro(fatTabRegistro);
		}
	}
	
	/** Forms: inclui_servico  */
	private void incluiRegistro(final FatTabRegistroVO fatTabRegistro){
		super.beginTransaction();
		
		final FatRegistro registro = new FatRegistro();
		registro.setCodigo(fatTabRegistro.getCodigo());
		registro.setDescricao(fatTabRegistro.getDescricao());
		registro.setIndSituacao(DominioSituacao.A);
		
		fatRegistrosRN.persistirFatRegistros(registro);
		
		super.commitTransaction();
	}
	
	/** Forms: cursor_servico  */
	private Map<String, FatTabRegistroVO> obterCursorRegistro( final ControleProcessadorArquivosImportacaoSus controle) {
		
		final List<FatRegistro> registrosAtuais;
		registrosAtuais = this.fatRegistrosDAO.buscaListaRegistrosAtivos();
		final Map<String, FatTabRegistroVO> tabRegistro = new TreeMap<String, FatTabRegistroVO>();
		
		int wTotRegistro = 0;
		for (FatRegistro fatRegistro: registrosAtuais) {
			if(fatRegistro.getCodigo() != null){
				
				if(tabRegistro.containsKey(fatRegistro.getCodigo())){
					controle.gravarLog(REGISTRO_COD+fatRegistro.getCodigo()+' '+fatRegistro.getDescricao()+" duplicado, descricao:"+tabRegistro.get(fatRegistro.getCodigo()).getDescricao());
					
				} else {
					FatTabRegistroVO fatTabRegistroVO =	new FatTabRegistroVO();
					fatTabRegistroVO.setCodigo(fatRegistro.getCodigo());
					fatTabRegistroVO.setDescricao(fatRegistro.getDescricao());
					fatTabRegistroVO.setProcessado(TipoProcessado.NAO_PROCESSADO);
					fatTabRegistroVO.setSituacao(DominioSituacao.getInstance(DominioSituacao.A.equals(fatRegistro.getIndSituacao())));
					
					tabRegistro.put(fatRegistro.getCodigo(), fatTabRegistroVO);
								  
					wTotRegistro++;
					controle.gravarLog(StringUtils.leftPad(String.valueOf(wTotRegistro), 3)+' '+fatRegistro.getCodigo()+' '+fatRegistro.getDescricao());
				}
			}
		}
		
		String msg = "Tabela Registro carregada com "+wTotRegistro + " registros:";
		controle.gravarLog(msg);
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(msg);

		return tabRegistro;
	}
	
	/** Forms: executa_leitura_servico  */
	private void executaLeituraRegsitro( final AghArquivoProcessamento aghArquivoRegistro,
									    final Map<String, FatTabRegistroVO> tabRegistro,
										final ControleProcessadorArquivosImportacaoSus controle ) throws ApplicationBusinessException{
		BufferedReader br = null;
		try {
			br = util.abrirArquivo(aghArquivoRegistro);
			
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
							   			.append("Processando tb_registro... "+ ++wTotRegistro);	
				
				montaRegistros(aghArquivoRegistro, tabRegistro, line, wTotRegistro, controle);
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

				util.atualizarArquivo(aghArquivoRegistro, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			} catch (final IOException e) {
				LOG.error(e.getMessage());
				LOG.error(e.getMessage(), e);
			}
		} 
	}
	
	/** Forms: monta_registro_servico */
	private void montaRegistros( final AghArquivoProcessamento aghArquivoRegistro,
									   final Map<String, FatTabRegistroVO> tabRegistro,
									   final String regRegsitro, int wTotRegistro,
									   final ControleProcessadorArquivosImportacaoSus controle ){
		try {
			
			final String  vSeq = regRegsitro.substring(0,2).trim();
			final String vDescricaoRegistro = regRegsitro.substring(2,51).trim();

			//	final String vServico = fatcBuscaServClassRN.aghcRemoveCaracterEspecial(regRegsitro.substring(3,120).trim(), null, 'R').toUpperCase();
            Integer coRegistro = Integer.parseInt(vSeq);
			if(coRegistro != null && coRegistro > 0){
				
				if(tabRegistro.containsKey(vSeq)){
					final FatTabRegistroVO tabRegistroVO = tabRegistro.get(vSeq);
					
					final String aux = String.valueOf(CoreUtil.nvl(tabRegistroVO.getDescricao(),StringUtils.EMPTY));
					
					if(!StringUtils.equalsIgnoreCase(vDescricaoRegistro, aux)){
						controle.gravarLog(REGISTRO_COD + vSeq + ' ' + tabRegistroVO.getDescricao()+ 
								       " descricao alterada  para:"+vDescricaoRegistro);
						controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
			   			.append(REGISTRO_COD + vSeq + ' ' + tabRegistroVO.getDescricao()+ 
							       " descricao alterada  para:"+vDescricaoRegistro);
						tabRegistroVO.setDescricao(vDescricaoRegistro);
						tabRegistroVO.setProcessado(TipoProcessado.ALTERA);
						
					} else if(DominioSituacao.I.equals(tabRegistroVO.getSituacao())){
						tabRegistroVO.setProcessado(TipoProcessado.ALTERA);
						tabRegistroVO.setSituacao(DominioSituacao.A);
						
						controle.gravarLog(REGISTRO_COD + vSeq + ' ' + tabRegistroVO.getDescricao() + " re-ativado." );
						
					} else {
						tabRegistroVO.setProcessado(TipoProcessado.PROCESSADO);
					}
					
				} else {
					final FatTabRegistroVO vo = new FatTabRegistroVO();
					vo.setCodigo(vSeq);
					vo.setDescricao(vDescricaoRegistro);
					vo.setProcessado(TipoProcessado.INCLUI);
					controle.gravarLog("Novo Registro cod:" + vSeq + ' ' + vDescricaoRegistro);
					controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
		   			.append("Novo Registro cod:" + vSeq + ' ' + vDescricaoRegistro);
					tabRegistro.put(vSeq, vo);
				}
			} else {
				controle.gravarLog(REGISTRO_COD + vSeq + ' ' +vDescricaoRegistro + " sem codigo");
			}

			controle.incrementaNrRegistrosProcessados();
			if (controle.getNrRegistrosProcessados() % ProcessadorArquivosImportacaoSusProcedServicoON.QT_MINIMA_ATUALIZAR_ARQUIVO == 0) {
				util.atualizarArquivo(aghArquivoRegistro, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			}
			
		} catch (final IndexOutOfBoundsException iae) {
			// Tamanho da linha menor q o esperado
			util.tratarExcecaoNaoLancada(iae, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR +
										 "Erro ao ler Registro: Tamanho da linha menor que o esperado." + 
										 ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]", 
										 wTotRegistro, regRegsitro);
		}
	} 
	
	/** Forms: abertura_servico_ok */
	private AghArquivoProcessamento aberturaRegistroOK( final String sgFaturamento) throws ApplicationBusinessException{
		
		final String nmArquivoServ = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_REGISTRO).toLowerCase();
		final AghArquivoProcessamento aghArquivoServico = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArquivoServ);
		return aghArquivoServico;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

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

	protected FatRegistrosRN getFatRegistrosRN() {
		return fatRegistrosRN;
	}

	protected void setFatRegistrosRN(FatRegistrosRN fatRegistrosRN) {
		this.fatRegistrosRN = fatRegistrosRN;
	}

	protected ProcessadorArquivosImportacaoSusUtil getUtil() {
		return util;
	}

	protected void setUtil(ProcessadorArquivosImportacaoSusUtil util) {
		this.util = util;
	}

	protected FatRegistrosDAO getFatRegistrosDAO() {
		return fatRegistrosDAO;
	}

	protected void setFatRegistrosDAO(FatRegistrosDAO fatRegistrosDAO) {
		this.fatRegistrosDAO = fatRegistrosDAO;
	}


}