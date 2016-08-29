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
import br.gov.mec.aghu.faturamento.dao.FatServClassificacoesDAO;
import br.gov.mec.aghu.faturamento.dao.FatServicosDAO;
import br.gov.mec.aghu.faturamento.vo.FatServClassificacoesVO;
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroServicoVO;
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroServicoVO.TipoProcessado;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatServClassificacoes;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcesArqImportSusProcedServicoServClassON  extends BaseBMTBusiness{

	private static final String SERV_CLASS_COD = "serv_class cod:";

	private static final long serialVersionUID = 7695698187574264162L;

	private static final Log LOG = LogFactory.getLog(ProcesArqImportSusProcedServicoServClassON.class);
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private FatcBuscaServClassRN fatcBuscaServClassRN;
	
	@EJB
	private FatServClassificacoesRN fatServClassRN;
	
	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@Inject
	private FatServClassificacoesDAO fatServClassDAO;
	
	@Inject
	private FatServicosDAO fatTabServicoDAO;
	     
	
	/** Forms: carrega_serv_class  */
	public Map<Integer, FatServClassificacoesVO> carregaSerClass( final String sgFaturamento, 
			  					  								     final Map<Short, FatTabRegistroServicoVO> tabServico,
			  					  								     final ControleProcessadorArquivosImportacaoSus controle) 
			  					  								     throws ApplicationBusinessException{
		
		final AghArquivoProcessamento arquivoServClass = aberturaServClassOK(sgFaturamento, parametroFacade, aghuFacade);
		
		try{
			if(arquivoServClass != null){
				arquivoServClass.setDthrInicioProcessamento(controle.getInicio());
				controle.gravarLog("Iniciando carga de tabela de serv_classs em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));

				util.atualizarArquivo(arquivoServClass, controle.getInicio(), 0, 100, 0, null, controle);
				
				final Map<Integer, FatServClassificacoesVO> tabServClass = obterCursorServClass(controle);
				
				executaLeituraServClass(arquivoServClass, tabServClass, controle);
				
				ajustaServClass(tabServico, tabServClass, arquivoServClass, controle);
				
				return tabServClass;
				
			} else {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro na carga de tabela de serv_classs.");
				throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusProcedServicoONExceptionCode.ERRO_ARQUIVO_SERV_CLASS_NAO_ENCONTRADO);
			}
		} finally {
			final Date fim = new Date();
			if (arquivoServClass != null) {
				util.atualizarArquivo(arquivoServClass, fim, 100, 100, 0, fim, controle);
			}
		}
	}
	
	/** Forms: executa_leitura_serv_class  */
	private void executaLeituraServClass( final AghArquivoProcessamento arquivoServClass,
									      final Map<Integer, FatServClassificacoesVO> tabServClass,
										  final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		BufferedReader br = null;
		try {
			br = util.abrirArquivo(arquivoServClass);

			String strLine;
			List<String> lines = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}
			
			controle.incrementaNrRegistrosProcesso(lines.size() +1);
			
			int wTotServClass = 0;
			for (String line : lines) {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					  	       			.append("Processando rl_procedimento_serv_class... ").append(++wTotServClass);
				
				montaRegistroServClass(arquivoServClass, tabServClass, line, wTotServClass, controle);
			} 
			
			String msg = "Tabela serv_class carregada com " + lines.size()+" registros";
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

				util.atualizarArquivo(arquivoServClass, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,  
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			} catch (final IOException e) {
				LOG.error(e.getMessage(), e);
			}
		} 
	}

	/** Forms: ajusta_serv_class  */
	private void ajustaServClass( final Map<Short, FatTabRegistroServicoVO> tabServico,
		    					  final Map<Integer, FatServClassificacoesVO> tabServClass,
		    					  final AghArquivoProcessamento arquivoServClass,
		    					  final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		

		controle.incrementaNrRegistrosProcesso(tabServClass.keySet().size());
		
		// Varre tabela do arquivo lido classificado por cod_tabela
		// Carrega em variavel todos os servico validos de cada cod_tabela
		for(Integer seqClassificacao : tabServClass.keySet()){
			beginTransaction();
			final FatServClassificacoesVO fatTabClassificacao = tabServClass.get(seqClassificacao);
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
						   			.append("Verificando serv_class ...").append(fatTabClassificacao.getServclas());
			
			// FORMS: verifica_alteracao_serv_class;
			switch (fatTabClassificacao.getProcessado()) {

				case INCLUI: 
						incluiServClass(fatTabClassificacao, tabServico, controle);
						break;
					
				case ALTERA:
						alteraServClass(fatTabClassificacao, true, tabServico, controle);
						break;
					
				case NAO_PROCESSADO: 
						alteraServClass(fatTabClassificacao, false, tabServico, controle);
						break;
						
				default:
					// NVL(tab_serv_class(i).ind_alteracao,'N') = 'S'
					if(fatTabClassificacao.isIndAlteracao()){
						alteraServClass(fatTabClassificacao, true, tabServico, controle);
					}
					break;
			}
			
			controle.incrementaNrRegistrosProcessados();
			
			fatServClassDAO.flush();
			commitTransaction();
			util.atualizarArquivo( arquivoServClass, controle.getInicio(), new Date(), 
							       ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
							       ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
		}
	}
	
	
	/** Forms: altera_serv_class inativa_serv_class */
	private void alteraServClass( final FatServClassificacoesVO tabServClass, final boolean incluirRegistro, 
		      					  final Map<Short, FatTabRegistroServicoVO> tabServico, 
		      					  final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException {
		
		final FatServClassificacoes fatClass = fatServClassDAO.obterPorChavePrimaria(tabServClass.getSeq());
		fatClass.setIndSituacao(DominioSituacao.I);
		fatServClassRN.persistirFatServClassificacoes(fatClass);
		
		if(incluirRegistro){
			controle.gravarLog("Inativados 1 servico/classificacao:"+tabServClass.getSeq()+" por alteração,"+(tabServClass.isIndAlteracao() ? 'S' : 'N'));
			incluiServClass(tabServClass, tabServico, controle);
		} else {
			controle.gravarLog("Inativados 1 servico/classificacao:"+tabServClass.getSeq()+' '+fatClass.getSeq());
		}
	}

	/** Forms: inclui_serv_class  */
	private void incluiServClass( final FatServClassificacoesVO tabServClass,
							      final Map<Short, FatTabRegistroServicoVO> tabServico,
							      final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		Short vInd = Short.valueOf(tabServClass.getServclas().substring(0,3));
		Short vFseSeq = null;
		if(vInd.intValue() > 0 && tabServico.containsKey(vInd)){
			vFseSeq = tabServico.get(vInd).getSeq(); 
		}

		if(vFseSeq != null){
			FatServClassificacoes fatClass = new FatServClassificacoes();
			
			fatClass.setServico(fatTabServicoDAO.obterPorChavePrimaria(vFseSeq));
			fatClass.setCodigo(tabServClass.getServclas().substring(3,6));
			fatClass.setDescricao(tabServClass.getClassificacao());
			fatClass.setIndSituacao(DominioSituacao.A);

			fatServClassRN.persistirFatServClassificacoes(fatClass);
			tabServClass.setFatServclassificacao(fatClass);
			
		} else {
			controle.gravarLog("Problema na determinação fse_seq, cod:"+tabServClass.getServclas()+' '+tabServClass.getClassificacao());
		}
	}
	
	/** Forms: monta_registro_serv_class */
	private void montaRegistroServClass( final AghArquivoProcessamento arquivoServClass,
										 final Map<Integer, FatServClassificacoesVO> tabServClass,
										 final String regServClass, int wTotServClass,
										 final ControleProcessadorArquivosImportacaoSus controle){
		try {
			final Integer vSeq = Integer.valueOf(regServClass.substring(0,6).trim());
			final String vClassificacao = fatcBuscaServClassRN.aghcRemoveCaracterEspecial(regServClass.substring(6,150).trim(), null, 'R').toUpperCase();
			final String vCompetencia = regServClass.substring(156,162);
			
			if(vSeq != null && vSeq > 0){
				
				if(tabServClass.containsKey(vSeq)){
					final FatServClassificacoesVO tabFatServClass = tabServClass.get(vSeq);
					
					final String aux = String.valueOf(CoreUtil.nvl(tabFatServClass.getClassificacao(),StringUtils.EMPTY));
					
					if(!StringUtils.equalsIgnoreCase(vClassificacao, aux)){
						controle.gravarLog(SERV_CLASS_COD + vSeq + ' ' + tabFatServClass.getClassificacao() + 
								       " descricao alterada  para:"+vClassificacao);
						
						tabFatServClass.setClassificacao(vClassificacao);
						tabFatServClass.setProcessado(TipoProcessado.ALTERA);
						tabFatServClass.setDtCompetencia(vCompetencia);
						
					} else if(DominioSituacao.I.equals(tabFatServClass.getSituacao())){
						controle.gravarLog(SERV_CLASS_COD+vSeq+' '+tabFatServClass.getClassificacao()+" re-ativado.");
						tabFatServClass.setProcessado(TipoProcessado.ALTERA);
						tabFatServClass.setSituacao(DominioSituacao.A);
						tabFatServClass.setDtCompetencia(vCompetencia);
						
					} else {
						tabFatServClass.setProcessado(TipoProcessado.PROCESSADO);
					}
					
				} else {
					final FatServClassificacoesVO vo = new FatServClassificacoesVO();
					vo.setServclas(vSeq.toString());
					vo.setClassificacao(vClassificacao);
					vo.setProcessado(TipoProcessado.INCLUI);
					vo.setDtCompetencia(vCompetencia);
					
					tabServClass.put(vSeq, vo);
				}
			} else {
				controle.gravarLog(SERV_CLASS_COD + vSeq + ' ' +regServClass.substring(3,120) + " sem codigo");
			}
			
			controle.incrementaNrRegistrosProcessados();
			if (controle.getNrRegistrosProcessados() % ProcessadorArquivosImportacaoSusProcedServicoON.QT_MINIMA_ATUALIZAR_ARQUIVO == 0) {
				util.atualizarArquivo(arquivoServClass, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100, 
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			}
			
		} catch (final IndexOutOfBoundsException iae) {
			// Tamanho da linha menor q o esperado
			util.tratarExcecaoNaoLancada(iae, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR +
										 "Erro ao ler Procedimento: Tamanho da linha menor que o esperado." + 
										 ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]", 
										 wTotServClass, regServClass);
		}
	}
	
	/** Forms: cursor_serv_class  */
	private Map<Integer, FatServClassificacoesVO> obterCursorServClass(final ControleProcessadorArquivosImportacaoSus controle) {

		final List<FatServClassificacoes> classificacoes = fatServClassDAO.buscarFatServClassificacoesAtivos();
		final Map<Integer, FatServClassificacoesVO> tabServClass = new TreeMap<Integer, FatServClassificacoesVO>(); 
		
		for (FatServClassificacoes classificacao : classificacoes) {
			
			final FatServClassificacoesVO cc = new FatServClassificacoesVO();
			cc.setSeq(classificacao.getSeq());
			
			//lpad(fse.codigo,3,'0')||lpad(fcs.codigo,3,'0') servclas
			cc.setServclas( StringUtils.leftPad(classificacao.getServico().getCodigo(), 3, '0') + 
						    StringUtils.leftPad(classificacao.getCodigo(), 3, '0') );
			
			cc.setClassificacao(fatcBuscaServClassRN.aghcRemoveCaracterEspecial(classificacao.getDescricao(), null, 'R'));
			cc.setSituacao(classificacao.getIndSituacao());
			cc.setDtCompetencia(classificacao.getDtCompetencia());
			cc.setFatServclassificacao(classificacao);
			
			if(cc.getServclas() != null){
				int servClass = Integer.parseInt(cc.getServclas());
				if(servClass > 0){
					if(tabServClass.containsKey(servClass)){
						controle.gravarLog(SERV_CLASS_COD+servClass+ ' '+ cc.getClassificacao()+" duplicado, descricao:"+cc.getClassificacao());
						
					} else {
						cc.setProcessado(TipoProcessado.NAO_PROCESSADO);
						cc.setIndAlteracao(verAlteracaoServico(classificacao, tabServClass));
						
						tabServClass.put(servClass, cc);
					}
				}
			}
		}
		return tabServClass;
	}
	
	/** Forms: ver_alteracao_servico */
	private boolean verAlteracaoServico( final FatServClassificacoes classificacao, 
										 final Map<Integer, FatServClassificacoesVO> tabServClass){
		
		// lpad(FAT_SERVICOS.codigo,3,'0')||lpad(FAT_SERV_CLASSIFICACOES.codigo,3,'0') servclas,
		
		//  v_servico     NUMBER      := to_number(SUBSTR(p_servclas,1,3));
		//  v_clas        NUMBER      := to_number(SUBSTR(p_servclas,4,3));
		if(classificacao.getServico().getCodigo() != null){
			int cod = Integer.parseInt(classificacao.getServico().getCodigo());
			if(tabServClass.containsKey(cod) && 
					TipoProcessado.ALTERA.equals(tabServClass.get(cod).getProcessado())){
				return true;
			}
		}
		
		return false;
	}
	
	/** Forms: abertura_serv_class_ok */
	private AghArquivoProcessamento aberturaServClassOK( final String sgFaturamento,   final IParametroFacade parametroFacade, final IAghuFacade aghuFacade) 
															throws ApplicationBusinessException{
		final String nmArquivo = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_SERVICO_CLASSIFICACAO).toLowerCase(); 
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

	public FatcBuscaServClassRN getFatcBuscaServClassRN() {
		return fatcBuscaServClassRN;
	}

	public void setFatcBuscaServClassRN(FatcBuscaServClassRN fatcBuscaServClassRN) {
		this.fatcBuscaServClassRN = fatcBuscaServClassRN;
	}

	public FatServClassificacoesRN getFatServClassRN() {
		return fatServClassRN;
	}

	public void setFatServClassRN(FatServClassificacoesRN fatServClassRN) {
		this.fatServClassRN = fatServClassRN;
	}

	public FatServClassificacoesDAO getFatServClassDAO() {
		return fatServClassDAO;
	}

	public void setFatServClassDAO(FatServClassificacoesDAO fatServClassDAO) {
		this.fatServClassDAO = fatServClassDAO;
	}

	public FatServicosDAO getFatTabServicoDAO() {
		return fatTabServicoDAO;
	}

	public void setFatTabServicoDAO(FatServicosDAO fatTabServicoDAO) {
		this.fatTabServicoDAO = fatTabServicoDAO;
	}
	
}