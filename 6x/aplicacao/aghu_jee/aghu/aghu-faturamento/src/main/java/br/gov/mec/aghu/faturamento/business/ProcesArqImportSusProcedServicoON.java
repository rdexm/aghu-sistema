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
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSus.ImportarArquivoSusONExceptionCode;
import br.gov.mec.aghu.faturamento.business.ProcessadorArquivosImportacaoSusProcedServicoON.ProcessadorArquivosImportacaoSusProcedServicoONExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatServicosDAO;
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroServicoVO;
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroServicoVO.TipoProcessado;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatServicos;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcesArqImportSusProcedServicoON  extends BaseBMTBusiness {

	private static final String SERVICO_COD = "Servico cod:";

	private static final long serialVersionUID = 3284613353510976203L;

	private static final Log LOG = LogFactory.getLog(ProcesArqImportSusProcedServicoON.class);

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private FatcBuscaServClassRN fatcBuscaServClassRN;

	@EJB
	private FatServicosRN fatServicosRN;
	
	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@Inject
	private FatServicosDAO fatServicosDAO;
	
	/** Forms: carrega_servico  */
	public Map<Short, FatTabRegistroServicoVO> carregaServico( final String sgFaturamento, 
															   final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{

		final AghArquivoProcessamento aghArquivoServico = aberturaServicoOK(sgFaturamento);
		
		Map<Short, FatTabRegistroServicoVO> tabServico = null;
		
		try{
			if(aghArquivoServico != null){
				aghArquivoServico.setDthrInicioProcessamento(controle.getInicio());
				controle.gravarLog("Iniciando carga de tabela de servicos em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));

				util.atualizarArquivo(aghArquivoServico, controle.getInicio(), 0, 100, 0, null, controle);
				
				// Carrega em tab de memoria tabela servicos
				tabServico =  obterCursorServico(controle);
				
				executaLeituraServico(aghArquivoServico, tabServico, controle);
	
				ajustaServico(aghArquivoServico, tabServico, controle);
				
			} else {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  		   			.append("Erro na carga de tabela de servicos.");
				
				throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusProcedServicoONExceptionCode.ERRO_ARQUIVO_SERVICO_NAO_ENCONTRADO);
			}

		} finally {
			final Date fim = new Date();
			if (aghArquivoServico != null) {
				util.atualizarArquivo(aghArquivoServico, fim, 100, 100, 0, fim, controle);
			}
		}
		
		return tabServico;
	}

	/** Forms: ajusta_servico */
	private void ajustaServico( final AghArquivoProcessamento aghArquivoServico, 
		    					final Map<Short, FatTabRegistroServicoVO> tabServico,
		    					final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		// Segunda parte do processamento
		controle.incrementaNrRegistrosProcesso(tabServico.keySet().size());
		
		for(Short seqServico : tabServico.keySet()){
			
			final FatTabRegistroServicoVO fatTabServico = tabServico.get(seqServico);
			
			controle.gravarLog("Verificando Servico ..."+fatTabServico.getCodigo()+ " processado:"+fatTabServico.getProcessado());

			// FORMS: verifica_alteracao_servico;
			switch (fatTabServico.getProcessado()) {
			
				case INCLUI: 
						incluiServico(fatTabServico); 
						break;
					
				case ALTERA: 
						alteraServico(fatTabServico, true);
						controle.gravarLog("Inativados 1 servico seq:"+fatTabServico.getSeq()+" por alteração.");
						break;
					
				case NAO_PROCESSADO: 
						alteraServico(fatTabServico, false);
						controle.gravarLog("Inativados 1 servico seq:"+fatTabServico.getSeq());
						break; 
			}
			
			controle.incrementaNrRegistrosProcessados();
			
			// aqui comita a transacao
			util.atualizarArquivo( aghArquivoServico, controle.getInicio(), new Date(), 
					  			   ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
					  			   ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
		}
	}

	/** Forms: altera_servico inativa_servico */
	private void alteraServico(final FatTabRegistroServicoVO fatTabServico, final boolean incluirRegistro) throws ApplicationBusinessException {
		
		super.beginTransaction();
		final FatServicos servico = fatServicosDAO.obterPorChavePrimaria(fatTabServico.getSeq());
		servico.setIndSituacao(DominioSituacao.I);
		fatServicosRN.persistirFatServicos(servico);
		super.commitTransaction();
		
		if(incluirRegistro){
			incluiServico(fatTabServico);
		}
	}
	
	/** Forms: inclui_servico  */
	private void incluiServico(final FatTabRegistroServicoVO fatTabServico) throws ApplicationBusinessException{
		super.beginTransaction();
		
		final FatServicos servico = new FatServicos();
		servico.setCodigo(fatTabServico.getCodigo());
		servico.setDescricao(fatTabServico.getServico());
		servico.setDtCompetencia(fatTabServico.getDtCompetencia());
		servico.setIndSituacao(DominioSituacao.A);
		
		fatServicosRN.persistirFatServicos(servico);
		
		super.commitTransaction();
	}
	
	/** Forms: cursor_servico  */
	private Map<Short, FatTabRegistroServicoVO> obterCursorServico( final ControleProcessadorArquivosImportacaoSus controle) {
		
		final List<FatServicos> servicosAtuais = fatServicosDAO.buscaListaServicosAtivos();
		final Map<Short, FatTabRegistroServicoVO> tabServico = new TreeMap<Short, FatTabRegistroServicoVO>();
		
		int wTotServico = 0;
		for (FatServicos fatServicos : servicosAtuais) {
			if(fatServicos.getCodigo() != null){
				final Short codigo = Short.valueOf(fatServicos.getCodigo());
				
				if(tabServico.containsKey(codigo)){
					controle.gravarLog(SERVICO_COD+codigo+' '+fatServicos.getDescricao()+" duplicado, descricao:"+tabServico.get(codigo).getServico());
					
				} else {
					tabServico.put(codigo, new FatTabRegistroServicoVO( fatServicos.getSeq(), 
																	    fatServicos.getDescricao(), 
																		fatServicos.getDtCompetencia(), 
																		fatServicos.getIndSituacao(), 
																		TipoProcessado.NAO_PROCESSADO
																	  )
								  );
								  
					wTotServico++;
					controle.gravarLog(StringUtils.leftPad(String.valueOf(wTotServico), 3)+' '+fatServicos.getSeq()+' '+fatServicos.getDescricao());
				}
			}
		}
		
		String msg = "Tabela servico carregada com "+wTotServico + " registros:";
		controle.gravarLog(msg);
		controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(msg);

		return tabServico;
	}
	
	/** Forms: executa_leitura_servico  */
	private void executaLeituraServico( final AghArquivoProcessamento aghArquivoServico,
									    final Map<Short, FatTabRegistroServicoVO> tabServico,
										final ControleProcessadorArquivosImportacaoSus controle ) throws ApplicationBusinessException{
		BufferedReader br = null;
		try {
			br = util.abrirArquivo(aghArquivoServico);
			
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
			
			
			int wTotServico = 0;
			for (String line : lines) {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
							   			.append("Processando rl_procedimento_servico... "+ ++wTotServico);	
				
				montaRegistroServico(aghArquivoServico, tabServico, line, wTotServico, controle);
			}
			
			String msg = "Registros servico lidos: " + lines.size();
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

				util.atualizarArquivo(aghArquivoServico, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			} catch (final IOException e) {
				LOG.error(e.getMessage());
				LOG.error(e.getMessage(), e);
			}
		} 
	}
	
	/** Forms: monta_registro_servico */
	private void montaRegistroServico( final AghArquivoProcessamento aghArquivoServico,
									   final Map<Short, FatTabRegistroServicoVO> tabServico,
									   final String regServico, int wTotServico,
									   final ControleProcessadorArquivosImportacaoSus controle ){
		try {
			
			final Short  vSeq = Short.valueOf(regServico.substring(0,3));
			final String vServico = fatcBuscaServClassRN.aghcRemoveCaracterEspecial(regServico.substring(3,120).trim(), null, 'R').toUpperCase();
			final String vCompetencia = regServico.substring(123,129);
			
			if(vSeq != null && vSeq > 0){
				
				if(tabServico.containsKey(vSeq)){
					final FatTabRegistroServicoVO tabFatServico = tabServico.get(vSeq);
					
					final String aux = String.valueOf(CoreUtil.nvl(tabFatServico.getServico(),StringUtils.EMPTY));
					
					if(!StringUtils.equalsIgnoreCase(vServico, aux)){
						controle.gravarLog(SERVICO_COD + vSeq + ' ' + tabFatServico.getServico() + 
								       " descricao alterada  para:"+vServico);
						
						tabFatServico.setServico(vServico);
						tabFatServico.setProcessado(TipoProcessado.ALTERA);
						tabFatServico.setDtCompetencia(vCompetencia);
						
					} else if(DominioSituacao.I.equals(tabFatServico.getSituacao())){
						tabFatServico.setProcessado(TipoProcessado.ALTERA);
						tabFatServico.setSituacao(DominioSituacao.A);
						tabFatServico.setDtCompetencia(vCompetencia);
						
						controle.gravarLog(SERVICO_COD + vSeq + ' ' + tabFatServico.getServico() + " re-ativado." );
						
					} else {
						tabFatServico.setProcessado(TipoProcessado.PROCESSADO);
					}
					
				} else {
					final FatTabRegistroServicoVO vo = new FatTabRegistroServicoVO();
					vo.setCodigo(vSeq.toString());
					vo.setServico(vServico);
					vo.setProcessado(TipoProcessado.INCLUI);
					vo.setDtCompetencia(vCompetencia);
					controle.gravarLog("Novo servico cod:" + vSeq + ' ' + vServico);
					
					tabServico.put(vSeq, vo);
				}
			} else {
				controle.gravarLog(SERVICO_COD + vSeq + ' ' +vServico + " sem codigo");
			}

			controle.incrementaNrRegistrosProcessados();
			if (controle.getNrRegistrosProcessados() % ProcessadorArquivosImportacaoSusProcedServicoON.QT_MINIMA_ATUALIZAR_ARQUIVO == 0) {
				util.atualizarArquivo(aghArquivoServico, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			}
			
		} catch (final IndexOutOfBoundsException iae) {
			// Tamanho da linha menor q o esperado
			util.tratarExcecaoNaoLancada(iae, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR +
										 "Erro ao ler Procedimento: Tamanho da linha menor que o esperado." + 
										 ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]", 
										 wTotServico, regServico);
		}
	} 
	
	/** Forms: abertura_servico_ok */
	private AghArquivoProcessamento aberturaServicoOK( final String sgFaturamento) throws ApplicationBusinessException{
		
		final String nmArquivoServ = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_SERVICO).toLowerCase();
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

	public FatcBuscaServClassRN getFatcBuscaServClassRN() {
		return fatcBuscaServClassRN;
	}

	public void setFatcBuscaServClassRN(FatcBuscaServClassRN fatcBuscaServClassRN) {
		this.fatcBuscaServClassRN = fatcBuscaServClassRN;
	}

	public FatServicosRN getFatServicosRN() {
		return fatServicosRN;
	}

	public void setFatServicosRN(FatServicosRN fatServicosRN) {
		this.fatServicosRN = fatServicosRN;
	}

	public FatServicosDAO getFatServicosDAO() {
		return fatServicosDAO;
	}

	public void setFatServicosDAO(FatServicosDAO fatServicosDAO) {
		this.fatServicosDAO = fatServicosDAO;
	}
}