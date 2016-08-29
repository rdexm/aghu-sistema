package br.gov.mec.aghu.faturamento.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.ImportarArquivoSusON.ImportarArquivoSusONExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaCompatibilidDAO;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoCboDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoTransplanteDAO;
import br.gov.mec.aghu.faturamento.dao.FatVlrItemProcedHospCompsDAO;
import br.gov.mec.aghu.faturamento.vo.FatTabCompatibilidadeVO;
import br.gov.mec.aghu.faturamento.vo.FatTabIPHVO;
import br.gov.mec.aghu.faturamento.vo.ProcedimentosXCompatibilidadesVO;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaCompatibilid;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessadorArquivosImportacaoSusCompatibilidadeON extends BaseBMTBusiness {


	@EJB
	private FatCompatExclusItemON fatCompatExclusItemON;
	
	@EJB
	private FatCompetenciaCompatibilidRN fatCompetenciaCompatibilidRN;
	
	private static final Log LOG = LogFactory.getLog(ProcessadorArquivosImportacaoSusCompatibilidadeON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private FatCompetenciaCompatibilidDAO fatCompetenciaCompatibilidDAO;
	
	@Inject
	private FatProcedimentoCboDAO fatProcedimentoCboDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private FatCompetenciaDAO fatCompetenciaDAO;
	
	@Inject
	private FatTipoTransplanteDAO fatTipoTransplanteDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ProcessadorArquivosImportacaoSusUtil util;
	
	@Inject
	private FatVlrItemProcedHospCompsDAO fatVlrItemProcedHospCompsDAO;
	
	protected enum ProcessadorArquivosImportacaoSusCompatibilidadeONExceptionCode implements BusinessExceptionCode {
		MAX_CARACTERS_COMP_EXCEDIDO
	}

	private static final long serialVersionUID = 7439743103164192223L;
		
	private static final String STRING_COM_TIPO_DE_REGISTRO_COMPATIVEL = " com tipo de registro compatível:";
	private static final String STRING_COD_SUS = "Cod SUS:";
	private static final String STRING_TIPO = " tipo:";
	private static final String STRING_INVALIDO = " inválido.";
	private static final String TRES = "03";
	private static final String QUATRO = "04";
	private static final String CINCO = "05";
	private static final String SETE = "07";
	private static final int MAX_CARACTERS_COMP = 32700;
	private static final int QT_MINIMA_ATUALIZAR_ARQUIVO = 500;	

	class Competencia{
		Date dtInicio;
		Date dtFim;
		String anomes;
		boolean vRet = false;
		String mensagem;
	}

	public void atualizarCompatibilidade(final ControleProcessadorArquivosImportacaoSus controle) {
		controle.setPartial(0);
		controle.iniciarLogRetorno();
		AghArquivoProcessamento aghArquivo = null;
		
		try { 
			
			final String nmArqCompatibilidade = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_ARQUIVO_COMPATIBILIDADE).toLowerCase(); 
			final String sgFaturamento = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_SIGLA_FATURAMENTO).toUpperCase();
			
			aghArquivo = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArqCompatibilidade);
			
			//executa_leitura, monta_registro => le os dados dos arquivos
			final Map<Long, FatTabCompatibilidadeVO> tabCompLido = executaLeitura(aghArquivo, controle);
			
			// Forms: abertura_rl_proced_compat 
			aghArquivo = aghuFacade.obterArquivoNaoProcessado(sgFaturamento, nmArqCompatibilidade);
			
			aghArquivo.setDthrInicioProcessamento(controle.getInicio());
			util.atualizarArquivo(aghArquivo, controle.getInicio(), 0, 100, 0, null, controle);
			
			Competencia c = checarCompetencia(tabCompLido, controle);

			if(c.mensagem != null){
				controle.gravarLog("Erro: " + c.mensagem);

			} else {
				
				if(c.vRet){ 	// coloquei 08/2010 para considerar o periodo em que iniciou a validade
					c.dtInicio = DateUtil.obterData(2012, 0, 1); 		// v_dt_inicio  := to_date('012012','mmyyyy');
					c.dtFim = DateUtil.adicionaDias(c.dtInicio, -1);	// v_dt_encerra := to_date('012012','mmyyyy')-1;
				}
	
				controle.gravarLog("Competencia do sigtab:" + DateUtil.obterDataFormatada(c.dtInicio, DateConstants.DATE_PATTERN_YYYY_MM));
				
				final Short phoSeq = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
				
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Iniciando carga comp_agh.");				
				
				final List<FatTabIPHVO> tabIphs = carregaTabIPH(phoSeq, controle);
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Finalizada carga iph.");
				
				final Map<Long, ProcedimentosXCompatibilidadesVO> tabProcXComp = carregaTabProcXCompAgh(phoSeq, aghArquivo, controle);
				
				ajustaBcoProcedimentosCompativeis( c.dtInicio, c.dtFim, tabIphs, tabCompLido, tabProcXComp, aghArquivo, controle);
				
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
							   .append("Tabela de Compatibilidade lida e atualizada com sucesso.");
			}

			controle.gravarLog(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR+"Finalizada em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			
		} catch (Exception e) { // vai pegar qualquer exception para logar no banco e no log de tela
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stringException = sw.toString();
			
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro inesperado " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO)).append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append(" [" + stringException + "].").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			controle.gravarLog("Erro inesperado " + ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + stringException);
			
		} finally {
			final Date fim = new Date();
			if (aghArquivo != null) {
				util.atualizarArquivo(aghArquivo, fim, 100, 100, 0, fim, controle);
			}
		}
	}

	/** Forms: check_competencia_ok */
	private Competencia checarCompetencia(final Map<Long, FatTabCompatibilidadeVO> tabCompLido, final ControleProcessadorArquivosImportacaoSus controle){
		
		// Cursor: c_busca_compt -->> Ano ||Lpad(Mes,2,'0') Ano_Mes,
		final List<FatCompetencia> competencias = this.getFatCompetenciaDAO().obterListaCompetenciaSemDataFimPorModulo(DominioModuloCompetencia.AMB);
		if (competencias == null || competencias.isEmpty()) { 
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Não foi encontrada uma competencia aberta.").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			return null;
		}
		
		Competencia c = new Competencia();
		
		final FatCompetencia fatCompetencia = competencias.get(0);
		c.anomes = fatCompetencia.getId().getAno().toString()+fatCompetencia.getId().getMes().toString();
		
		controle.gravarLog("Competencia aberta:" + c.anomes);
		
		final String inicioComp = tabCompLido.get(tabCompLido.keySet().iterator().next()).getwDtCompetencia(); //pega competencia primeiro registro do map
		
		// Cursor: c_busca_compt -->>  To_Date(:c_anomes_sigtap  ||'01','yyyymmdd') Dt_Inicio,
		c.dtInicio = DateUtil.obterData(Integer.parseInt(StringUtils.substring(inicioComp, 0, 4)), (Integer.parseInt(StringUtils.substring(inicioComp, 4))-1), 1);

		// Cursor: c_busca_compt -->> To_Date(C_Anomes_Sigtap||'01','yyyymmdd')-1 Dt_Encerra
		c.dtFim = DateUtil.adicionaDias(c.dtInicio, -1);
		
		if(checarUltimaCompetenciaOK(c, controle)){
			c.vRet = true;
		}
	
		return c;
	}
	
	
	/** Forms: check_ultima_ok */
	private boolean checarUltimaCompetenciaOK( final Competencia competenciaSigTap, final ControleProcessadorArquivosImportacaoSus controle){
		
		Date dataMax = getFatCompetenciaCompatibilidDAO().obterMaiorCompetenciaSemDataFim();
		
		if (dataMax == null) {
			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Não foi encontrada uma competencia aberta.").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			controle.gravarLog("Não foi encontrada uma competencia aberta.");
			return false;
		}

		Calendar wDtCompetencia = GregorianCalendar.getInstance();
		wDtCompetencia.setTime(competenciaSigTap.dtInicio);
		wDtCompetencia.set(Calendar.DAY_OF_MONTH, 01);
		
		Calendar cDtMax = GregorianCalendar.getInstance();
		cDtMax.setTime(dataMax);
		cDtMax.set(Calendar.DAY_OF_MONTH, 01);
		
		if (wDtCompetencia.getTime().before(cDtMax.getTime())) {
			final StringBuilder sb = new StringBuilder();
			
			sb.append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
		      .append("Competencia do sigtap ")
		      .append(DateUtil.obterDataFormatada(competenciaSigTap.dtInicio, DateConstants.DATE_PATTERN_MM_YYYY) )
		      .append( " anterior à ultima competencia atualizada: " )
		      .append(DateUtil.obterDataFormatada(DateUtil.adicionaMeses(dataMax, 1), DateConstants.DATE_PATTERN_MM_YYYY))
		      .append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
			
			controle.getLogRetorno().append(sb);
			competenciaSigTap.mensagem = sb.toString();
			
			controle.gravarLog("Competencia do sigtap " + DateUtil.obterDataFormatada(competenciaSigTap.dtInicio, DateConstants.DATE_PATTERN_MM_YYYY) + " anterior à ultima competencia atualizada: " +  DateUtil.obterDataFormatada(DateUtil.adicionaMeses(dataMax, 1), DateConstants.DATE_PATTERN_MM_YYYY));
			return false;
		}
		
		return true;
	}
	
	
	/** Forms: AJUSTA_BCO_PROC_X_COMPATIVEIS */
	private void ajustaBcoProcedimentosCompativeis( final Date dtInicioComp, final Date dataEncerra, 
													final List<FatTabIPHVO> tabIphs,
													final Map<Long, FatTabCompatibilidadeVO> tabCompLido,
													final Map<Long, ProcedimentosXCompatibilidadesVO> tabProcXComp, 
													AghArquivoProcessamento aghArquivo,
													final ControleProcessadorArquivosImportacaoSus controle) throws BaseException {
		
		if(controle.getNrRegistrosProcessadosGeral() > 0){
			controle.setNrRegistrosProcessados(controle.getNrRegistrosProcessadosGeral() + 1);
			controle.setNrRegistrosProcesso(controle.getNrRegistrosProcesso() + tabCompLido.keySet().size() +1);
			
		} else {
			controle.setNrRegistrosProcesso(tabCompLido.keySet().size() +1);
			controle.setNrRegistrosProcessados(1);
		}
		
		for(Long codSUS : tabCompLido.keySet()){
			try{
				controle.incrementaNrRegistrosProcessados();
				
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
							.append("Verificando alteração do procedimento ").append(codSUS);
				
				//super.beginTransaction(60*60*24);
				final FatTabCompatibilidadeVO vo = tabCompLido.get(codSUS);
				verificaAlteracaoCompats(dtInicioComp, dataEncerra, vo, codSUS, tabProcXComp, tabIphs, controle);
				//super.commitTransaction();
				util.atualizarArquivo(aghArquivo, controle.getInicio(), null, 100,  
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, null, controle);
				
			}catch (BaseException e) {
//			} catch (Exception e) {  // vai pegar qualquer exception para logar no banco e no log de tela
				super.rollbackTransaction();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				String stringException = sw.toString();
				
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Erro inesperado " )
					      .append(DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO))
					      .append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
					      .append(" [" + stringException + "].").append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR);
				
				controle.gravarLog("Erro ao  verificar procedimento: " + codSUS+ ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + stringException);			
			}
		}
	}

	/** Forms: verifica_alteracao_compats */
	private void verificaAlteracaoCompats( final Date dtInicioComp, final Date dataEncerra, 
										   final FatTabCompatibilidadeVO tabCompLidoVO, final Long codTabela,
										   final Map<Long, ProcedimentosXCompatibilidadesVO> tabProcXComp,
										   final List<FatTabIPHVO> tabIphs, 
										   final ControleProcessadorArquivosImportacaoSus controle) throws BaseException{
		
		controle.gravarLog("verificando procedimento:" + codTabela);	
		
		if(tabProcXComp.get(codTabela) != null){
			tabProcXComp.get(codTabela).setProcessado(true);

			final String compactsAgh = tabProcXComp.get(codTabela).getCompatibilidadeAux().toString();
			
			if(!CoreUtil.igual(tabCompLidoVO.getCompatsAux(), compactsAgh)){
				controle.gravarLog("Procedimento:" +codTabela+" com alteração, atualizando compatibilidades:");
				controle.gravarLog("Procedimento:" +codTabela+" compats_lido:"+tabCompLidoVO.getCompatsAux());
				controle.gravarLog("Procedimento:" +codTabela+" compats_ agh "+compactsAgh);

				atualizaCompats(false, dtInicioComp, dataEncerra, tabCompLidoVO, codTabela, tabProcXComp, tabIphs, controle);
			}  else {
				controle.gravarLog("Sem alteração" );
			}
			
		} else {
			controle.gravarLog("Inclusão do procedimento:" + codTabela);
			atualizaCompats(true, dtInicioComp, dataEncerra, tabCompLidoVO, codTabela, tabProcXComp, tabIphs, controle);
		} 
	}


	/** Forms: ATUALIZA_COMPATS	*/
	private void atualizaCompats( final boolean isInsert, final Date dtInicioComp, final Date dataEncerra,
								  final FatTabCompatibilidadeVO tabCompLido, final Long codSUS,
								  final Map<Long, ProcedimentosXCompatibilidadesVO> tabProcXComp,
								  final List<FatTabIPHVO> tabIphs, final ControleProcessadorArquivosImportacaoSus controle) throws BaseException{
		
		// v_iph_seq  := busca_iph(tab_comp_lido(i).cod_sus,v_pho_seq);
		int index = tabIphs.indexOf(new FatTabIPHVO(codSUS));
		if(index >=0){
			final FatTabIPHVO tabIPH = tabIphs.get(index);
			
			if(tabCompLido.getCpxSeq() == null){ 
				final FatCompetenciaCompatibilid fatComp = abreCompetencia(dtInicioComp, tabIPH.getPhoSeq(), tabIPH.getIphSeq());
				tabCompLido.setCpxSeq(fatComp.getSeq());
			}
			
			if(isInsert){
				incluiLida( tabIphs, tabIPH, tabCompLido, controle);
				
			} else {
				inativaCompatsProcedimento(codSUS, tabCompLido.getCpxSeq(), dataEncerra, controle);
				
				incluiLida( tabIphs, tabIPH, tabCompLido, controle);
			}
		} else {
			controle.gravarLog("Item procedimento(IPH) não encontrado:" + codSUS);
		} 
	}
	
	/** Forms: inativa_compats_procedimento  */
	private void inativaCompatsProcedimento(final Long codTabela, final Long cpxSeq, final Date dataEncerra, final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{

		controle.getLogRetorno().append("Inativando competencia do procedimento "+codTabela);
		
		controle.gravarLog("Inativando compatilidades do procedimento " + codTabela+
					   " ,data fim:"+DateUtil.obterDataFormatada(dataEncerra, DateConstants.DATE_PATTERN_DDMMYYYY));
		
		// Forms: fecha_competencia(p_cpx_seq);
		fatCompetenciaCompatibilidRN.atualizarFatCompetenciaCompatibilid(cpxSeq, dataEncerra);
	}
	
	/** Forms: inclui_lida  */
	private void incluiLida(final List<FatTabIPHVO> tabIphs,
							final FatTabIPHVO tabIPH, final FatTabCompatibilidadeVO tabCompLido,
							final ControleProcessadorArquivosImportacaoSus controle) throws BaseException{

		final List<ProcedimentosXCompatibilidadesVO> vlrsLida = processaStringCompats(tabCompLido.getCompats());
		
		for (final ProcedimentosXCompatibilidadesVO tabSubTabCompLida : vlrsLida) {

			// Forms: gera_nova_compatibilidade 
			int indexIphSeqCompatibiliza = tabIphs.indexOf(new FatTabIPHVO(tabSubTabCompLida.getCodSUS()));
			
			if(indexIphSeqCompatibiliza >=0){
				final FatTabIPHVO tabIPHCompatibiliza = tabIphs.get(indexIphSeqCompatibiliza);
				
				incluiNovaCompatibilidade( tabSubTabCompLida, tabIPH.getPhoSeq(), tabIPH.getIphSeq(), 
									       tabIPHCompatibiliza.getPhoSeq(), tabIPHCompatibiliza.getIphSeq(), 
									       tabCompLido.getCpxSeq(), controle);
			} else {
				controle.gravarLog("Item procedimento compativel(IPH) não encontrado:" + tabSubTabCompLida.getCodSUS() );
			}
		}
	}
	
	/** Forms: inclui */
	private void incluiNovaCompatibilidade( final ProcedimentosXCompatibilidadesVO procCompVO,
											final Short phoSeq, final Integer iphSeq,
											final Short phoSeqCompatibiliza, Integer iphSeqCompatibiliza, 
											final Long vCpxSeq, final ControleProcessadorArquivosImportacaoSus controle) throws BaseException{
		
		controle.gravarLog("Incluindo procedimento compativel(IPH), cod:" + procCompVO.getCodSUS() + " qtd:"+procCompVO.getQuantidade());

		FatCompatExclusItem fcei = new FatCompatExclusItem();

		// v_pho_seq e v_iph_seq
		fcei.setItemProcedHosp(fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(phoSeq, iphSeq)));
		
		// v_pho_seq_COMPaTibiliza e v_iph_seq_COMPaTibiliza
		fcei.setItemProcedHospCompatibiliza(fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(phoSeqCompatibiliza, iphSeqCompatibiliza)));
		
		fcei.setIndComparacao(DominioIndComparacao.valueOf(procCompVO.getTipoComparacao()));
		fcei.setIndCompatExclus(DominioIndCompatExclus.valueOf(procCompVO.getTipoCompatibilidade()));
		fcei.setCompetenciaCompatibilid(getFaturamentoFacade().obterFatCompetenciaCompatibilidPorSeq(vCpxSeq)); 
		fcei.setQuantidadeMaxima(procCompVO.getQuantidade());
		fcei.setIndAmbulatorio(DominioSimNao.S.toString().equalsIgnoreCase(procCompVO.getIndAmbulatorio()));
		fcei.setIndInternacao(DominioSimNao.S.toString().equalsIgnoreCase(procCompVO.getIndInternacao()));
		
		// eSchweigert 09/10/2013: No código migrado em carrega_lida o campo: 
		// tab_sub_tab_comp_lida(ind_sub).ttr_codigo, não é populado em nenhum momento
		if(procCompVO.getTtrCodigo() != null){
			fcei.setTipoTransplante(fatTipoTransplanteDAO.obterPorChavePrimaria(procCompVO.getTtrCodigo()));
		}
		
		fatCompatExclusItemON.persistirFatCompatExclusItem(fcei);
	}
	
	/** Forms: carrega_lida, carrega */
	private List<ProcedimentosXCompatibilidadesVO> processaStringCompats(String pCompats) {
		// inicializa sub-tabela
		
		int tam =  pCompats != null ? pCompats.length() : 0;
		int pos = 0;
		
		final List<ProcedimentosXCompatibilidadesVO> fatTabCompatibilidadeVOs = new ArrayList<ProcedimentosXCompatibilidadesVO>();
		while( pos < tam){
			
			final String codTabela = StringUtils.substring(pCompats, pos, pos+10);
			
			if(codTabela == null){
				return null; //  EXIT;
			} else {
				final ProcedimentosXCompatibilidadesVO vo = new ProcedimentosXCompatibilidadesVO();
				
				vo.setCodSUS(Long.valueOf(codTabela)); 							 	 // v_cod_tabela;
				vo.setTipoComparacao(pCompats.substring(pos+10, pos+11)); 			 // SUBSTR(p_compats,pos+10,1);
				vo.setTipoCompatibilidade(pCompats.substring(pos+11, pos+14)); 		 // SUBSTR(p_compats,pos+11,3);
				vo.setIndAmbulatorio(pCompats.substring(pos+14, pos+15));			 // SUBSTR(p_compats,pos+14,1);
				vo.setIndInternacao(pCompats.substring(pos+15, pos+16));			 // SUBSTR(p_compats,pos+15,1);
				vo.setQuantidade(Short.valueOf(pCompats.substring(pos+16, pos+21))); // SUBSTR(p_compats,pos+16,5);
		
				fatTabCompatibilidadeVOs.add(vo);
			}
			pos += 21;
		}
		
		return fatTabCompatibilidadeVOs;
	}
	

	/** Forms: abre_competencia	 */
	private FatCompetenciaCompatibilid abreCompetencia( final Date dtInicioComp, final Short phoSeq, final Integer iphSeq) throws ApplicationBusinessException{
		
		final FatCompetenciaCompatibilid comp = new FatCompetenciaCompatibilid();
		comp.setDtInicioValidade(dtInicioComp);
		comp.setFatItensProcedHospitalar(fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(phoSeq, iphSeq)));
		fatCompetenciaCompatibilidRN.persistirFatCompetenciaCompatibilid(comp);
		
		return comp;
	}
	
	/** Forms: carrega_tab_proc_x_comp_agh	 */
	private Map<Long, ProcedimentosXCompatibilidadesVO> carregaTabProcXCompAgh(final Short phoSeq, AghArquivoProcessamento aghArquivo,
										 									   final ControleProcessadorArquivosImportacaoSus controle) throws ApplicationBusinessException{
		
		final List<ProcedimentosXCompatibilidadesVO> rProcXComps = getFatCompetenciaCompatibilidDAO().obterCursorCProcXCcomp(phoSeq);
		
		final Map<Long, ProcedimentosXCompatibilidadesVO> mapProcXCompVO = new TreeMap<Long, ProcedimentosXCompatibilidadesVO>();
		
		final StringBuilder vCompatibilidade = new StringBuilder();
		final StringBuilder vCompatibilidadeAux = new StringBuilder();
		final StringBuilder vCompatibilidadeTTR = new StringBuilder();
		
		int ind=0;
		for (ProcedimentosXCompatibilidadesVO rProcXComp : rProcXComps) {
			++ind;

			controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR)
						   .append("Carregando tabela de Compat do AGH ..."+ind);	
			
			if(rProcXComp.getCodSUS() != null){
				
				montaVCompatibilidades(vCompatibilidade, vCompatibilidadeAux, vCompatibilidadeTTR, rProcXComp);
				
				if(mapProcXCompVO.get(rProcXComp.getCodSUS()) != null){
					
					// Limitei a tabela em 32700 caracteres, acredito que não vai estourar
					if(mapProcXCompVO.get(rProcXComp.getCodSUS()).getCompatibilidade().length() < MAX_CARACTERS_COMP){
						mapProcXCompVO.get(rProcXComp.getCodSUS()).appendCompatibilidade(vCompatibilidade);
						mapProcXCompVO.get(rProcXComp.getCodSUS()).appendCompatibilidadeAux(vCompatibilidadeAux);
						mapProcXCompVO.get(rProcXComp.getCodSUS()).appendCompatibilidadeTTR(vCompatibilidadeTTR);

						
					// Bom, se estourar, ainda da para aumentar para 32.767
					} else {
						throw new ApplicationBusinessException(ProcessadorArquivosImportacaoSusCompatibilidadeONExceptionCode.MAX_CARACTERS_COMP_EXCEDIDO, rProcXComp.getCodSUS());
					}
				} else {
					ProcedimentosXCompatibilidadesVO vo = new ProcedimentosXCompatibilidadesVO();
					vo.inicializaVars();
					vo.setCodSUS(rProcXComp.getCodSUS());
					vo.setItem(rProcXComp.getItem());
					vo.setCpxSeq(rProcXComp.getCpxSeq());
					vo.appendCompatibilidade(vCompatibilidade);
					vo.appendCompatibilidadeAux(vCompatibilidadeAux);
					vo.appendCompatibilidadeTTR(vCompatibilidadeTTR);
					vo.setProcessado(false);
					
					mapProcXCompVO.put(rProcXComp.getCodSUS(), vo);
				}
			}
			

			if (ind % QT_MINIMA_ATUALIZAR_ARQUIVO == 0) {
				util.atualizarArquivo(aghArquivo, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			}
		}
		
		return mapProcXCompVO;
	}

	private void montaVCompatibilidades(final StringBuilder vCompatibilidade,
										final StringBuilder vCompatibilidadeAux,
										final StringBuilder vCompatibilidadeTTR,
										final ProcedimentosXCompatibilidadesVO rProcXComp) {
		vCompatibilidade.setLength(0);
		vCompatibilidadeAux.setLength(0);
		vCompatibilidadeTTR.setLength(0);
		
		final String qtde = StringUtils.leftPad(rProcXComp.getQuantidade().toString(), 5, BigDecimal.ZERO.toString());
		final String codSusComp = StringUtils.leftPad(rProcXComp.getCodSUSComp().toString(), 10, BigDecimal.ZERO.toString());
		
		vCompatibilidade.append(StringUtils.leftPad(rProcXComp.getCodSUS().toString(), 10, BigDecimal.ZERO.toString()))
				        .append(rProcXComp.getComparacao())
				        .append(rProcXComp.getTipo())
				        .append(rProcXComp.getIndAmbulatorio())
				        .append(rProcXComp.getIndInternacao())
				        .append(qtde);

		vCompatibilidadeAux.append(codSusComp)
						   .append(StringUtils.leftPad(StringUtils.EMPTY, 4))
				 		   .append(rProcXComp.getIndAmbulatorio())
				 		   .append(rProcXComp.getIndInternacao())
				 		   .append(qtde);
		
		vCompatibilidadeTTR.append(codSusComp)
						   .append(StringUtils.rightPad(rProcXComp.getTtrCodigo(), 10, StringUtils.EMPTY));
	}
	
	/** Forms: carrega_tab_iph */
	private List<FatTabIPHVO> carregaTabIPH(final Short phoSeq, final ControleProcessadorArquivosImportacaoSus controle) {
		final List<FatTabIPHVO> tabIphs = getFatVlrItemProcedHospCompsDAO().obterValoresCompatibilidade(phoSeq );
		controle.gravarLog("Tab cbos carregada com:" + tabIphs.size());
		return tabIphs;
	}

	
	/** Forms: executa_leitura	 */
	private Map<Long, FatTabCompatibilidadeVO> executaLeitura( final AghArquivoProcessamento aghArquivo,
															   final ControleProcessadorArquivosImportacaoSus controle ) throws ApplicationBusinessException {
		
		final Map<Long, FatTabCompatibilidadeVO> tabCompLido = new TreeMap<Long, FatTabCompatibilidadeVO>();
		
		BufferedReader br = null;
		try {
			// Forms: abertura_rl_proced_compat
			br = util.abrirArquivo(aghArquivo);

			controle.gravarLog("Iniciando ATUALIZA_COMPATIBILIDADE em " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			
			String strLine;
			List<String> lines = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}

			if(controle.getNrRegistrosProcessadosGeral() > 0){
				controle.setNrRegistrosProcessados(controle.getNrRegistrosProcessadosGeral() + 1);
				controle.setNrRegistrosProcesso(controle.getNrRegistrosProcessoGeral() + lines.size() +1);
				
			} else {
				controle.setNrRegistrosProcesso(lines.size() +1);
				controle.setNrRegistrosProcessados(1);
			}
			
			
			int linha = 0;
			for (String line : lines) {
				controle.getLogRetorno().append(ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR).append("Processando rl_compatibilidade... "+linha);		
				montaRegistro(aghArquivo, tabCompLido, line, ++linha, controle);
			}
			
		} catch (final IOException io) {
			LOG.warn("Problemas na leitura dos arquivos.");
			throw new ApplicationBusinessException(ImportarArquivoSusONExceptionCode.ARQUIVO_NAO_ENCONTRADO);
		} finally {
			try {
				if (br != null) {
					br.close();
				}

				util.atualizarArquivo(aghArquivo, controle.getInicio(), new Date(), 
									  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,
									  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
			} catch (final IOException e) {
				LOG.error(e.getMessage());
				LOG.error(e.getMessage(), e);
			}
		}
		
		return tabCompLido;
	}

	/** Forms: monta_registro	 */
	private void montaRegistro( final AghArquivoProcessamento aghArquivo,
								final Map<Long, FatTabCompatibilidadeVO> tabCompLido,
								final String strLine, int linha, final ControleProcessadorArquivosImportacaoSus controle) {
		
		FatTabCompatibilidadeVO vo = lerLinhaArquivoCompatibilidade(strLine, linha, controle);
		
		if(CoreUtil.igual(vo.getwTpCompatibilidade(), "3")){
			controle.gravarLog(STRING_COD_SUS +vo.getwProcedimentoPrincipal() + 
							STRING_COM_TIPO_DE_REGISTRO_COMPATIVEL +vo.getwRegistroCompativel()+
							STRING_TIPO+vo.getwTpCompatibilidade()+ " desprezado." );
			vo.setwRetorno(false);
		}
		
		if(vo.iswRetorno()){
			
			validarRegistroPrincipal(vo, controle);
			
			validarCompatibilidade(vo);

			if(vo.getIndComparacao() == null ||
					vo.getIndCompatExclus() == null ||
						vo.getIndAmbulatorio() == null ||
							vo.getIndInternacao() == null ){
				
				vo.setwRetorno(false);
				controle.gravarLog("Reg invalido procedimento cod:" +vo.getwProcedimentoPrincipal() + 
					   	       " REGISTRO:" +vo.getwRegistroPrincipal()+
						   	   " COMPATIVEL:"+vo.getwProcedimentoCompativel()+
						   	   " REGISTRO:" +vo.getwRegistroCompativel()+
						   	   " COMPATIBILIDADE:"+vo.getwTpCompatibilidade()+
						   	   " QT:"+vo.getwQtPermitida()+
						   	   " ind_amb:"+vo.getIndAmbulatorio()+
						   	   " ind_int:"+vo.getIndInternacao()+
						   	   " w_DT_COMPETENCIA:"+vo.getwDtCompetencia());
			}
		}

		if(vo.iswRetorno()){
			final Long wProcedimentoPrincipal = Long.parseLong(vo.getwProcedimentoPrincipal());
			
			if(wProcedimentoPrincipal.compareTo(Long.valueOf(0)) > 0){
				if (tabCompLido.get(wProcedimentoPrincipal) != null) {

					// Limitei a tabela em 32700 caracteres, acredito que não vai estourar
					if(tabCompLido.get(wProcedimentoPrincipal).getCompats().length() < 32700){
						
						concatena(tabCompLido.get(wProcedimentoPrincipal).getCompats(), vo, controle);
						
						// eSchweigert Altera no VO do tabCompLido os valores RE-GERADOS de compats e compatsAux
						tabCompLido.get(wProcedimentoPrincipal).setCompats(vo.getCompats());
						tabCompLido.get(wProcedimentoPrincipal).setCompatsAux(vo.getCompatsAux());
						
					} else {	// Bom, se estourar, ainda da para aumentar para 32.767
						LOG.info("Excedida capacidade da Tabela de compatibiliades lidas do procedimento "+vo.getwProcedimentoPrincipal()+ "- 32700 carateres");									
					}
					
				} else {
					concatena(null, vo, controle);	
					tabCompLido.put(wProcedimentoPrincipal, vo);
				}
			}
		}
		
		controle.incrementaNrRegistrosProcessados();
		if (controle.getNrRegistrosProcessados() % QT_MINIMA_ATUALIZAR_ARQUIVO == 0) {
			util.atualizarArquivo(aghArquivo, controle.getInicio(), new Date(), 
								  ProcessadorArquivosImportacaoSusUtil.REFRESH, 100,
								  ProcessadorArquivosImportacaoSusUtil.MAX_TENTATIVAS, controle);
		}
	}
	
	/** Forms: concatena */
	private void concatena(final String pCompatibilidades, final FatTabCompatibilidadeVO vo, final ControleProcessadorArquivosImportacaoSus controle){

		List<ProcedimentosXCompatibilidadesVO> tabSubTabCompHcpa = processaStringCompats(pCompatibilidades);

		avaliaExistencia(vo, tabSubTabCompHcpa, controle);

		final StringBuilder vCompatibilidades = new StringBuilder();
		final StringBuilder vCompatibilidadesAux = new StringBuilder();
		concatenaTab(tabSubTabCompHcpa, vCompatibilidades, vCompatibilidadesAux);
		
		vo.setCompats(vCompatibilidades.toString());
		vo.setCompatsAux(vCompatibilidadesAux.toString());
	}

	/** Forms: avalia_existencia */
	private void avaliaExistencia( final FatTabCompatibilidadeVO vo, final List<ProcedimentosXCompatibilidadesVO> tabSubTabCompHcpa,
								   final ControleProcessadorArquivosImportacaoSus controle) {
		
		int index = tabSubTabCompHcpa.indexOf(new ProcedimentosXCompatibilidadesVO(
														Long.valueOf(vo.getwProcedimentoCompativel())
																				   )
											 );
		if(index >= 0){
			controle.gravarLog("Procedimento "+vo.getwProcedimentoCompativel()+" com mesmo procedimento compatível:");

			// ja existe, verifica indicadores para atualização
			controle.gravarLog(vo.getwProcedimentoPrincipal()+" com mesmo procedimento compatível:"); // p_cod_tabela
			
			final ProcedimentosXCompatibilidadesVO pvo = tabSubTabCompHcpa.get(index);
			
			if(DominioSimNao.N.toString().equalsIgnoreCase(pvo.getIndInternacao()) &&
					Boolean.TRUE.equals(vo.getIndInternacao()) ){
				
				pvo.setIndInternacao(DominioSimNao.S.toString());
				controle.gravarLog( "Procedimento compatível:"+vo.getwProcedimentoCompativel()+
								" ind_ambulatorio:"+pvo.getIndAmbulatorio()+
								" ativando ind_internacao.");
				
			} else if(DominioSimNao.N.toString().equalsIgnoreCase(pvo.getIndAmbulatorio()) &&
					Boolean.TRUE.equals(vo.getIndAmbulatorio()) ){
				
				pvo.setIndAmbulatorio(DominioSimNao.S.toString());
				controle.gravarLog( "Procedimento compatível:"+vo.getwProcedimentoCompativel()+
						" ind_internacao:"+pvo.getIndInternacao()+
						" ativando ind_ambulatorio.");
			}
			
			if(pvo.getQuantidade().intValue() != vo.getwQtPermitida().intValue()){
				controle.gravarLog( "Procedimento compatível:"+vo.getwProcedimentoCompativel()+
							    " quantidade:"+pvo.getQuantidade() +
							    " nova quantidade:"+vo.getwQtPermitida() );
				
				pvo.setQuantidade(vo.getwQtPermitida().shortValue());
			}

		} else {
			final ProcedimentosXCompatibilidadesVO pvo = new ProcedimentosXCompatibilidadesVO();
			
			pvo.setCodSUS(Long.valueOf(vo.getwProcedimentoPrincipal()));
			pvo.setTipoComparacao(vo.getIndComparacao().toString());
			pvo.setTipoCompatibilidade(vo.getIndCompatExclus().toString());
			pvo.setIndInternacao(Boolean.TRUE.equals(vo.getIndInternacao()) ? DominioSimNao.S.toString() : DominioSimNao.N.toString());
			pvo.setIndAmbulatorio(Boolean.TRUE.equals(vo.getIndAmbulatorio()) ? DominioSimNao.S.toString() : DominioSimNao.N.toString());
			pvo.setQuantidade(vo.getwQtPermitida().shortValue());
			
			tabSubTabCompHcpa.add(pvo);
		}
	}
	
	private void validarCompatibilidade(FatTabCompatibilidadeVO vo) {
		switch (Integer.parseInt(vo.getwTpCompatibilidade())) {
			case 1: // 1 Compatível
				vo.setIndCompatExclus(DominioIndCompatExclus.ICP);				// Item cobrado pelo procedimento
				
				if(Boolean.TRUE.equals(vo.getIndInternacao())){
					vo.setIndComparacao(DominioIndComparacao.R); 				// Comparação entre procedimento realizado e item
				} else if(Boolean.TRUE.equals(vo.getIndAmbulatorio())){
					vo.setIndComparacao(DominioIndComparacao.I); 				// Comparação entre itens
				}
			break;
			
			case 2:
				if(Boolean.TRUE.equals(vo.getIndInternacao())){
					// AIH (Proc. Especial)
					if(StringUtils.equals(QUATRO, vo.getwRegistroPrincipal()) && 
							StringUtils.equals(QUATRO, vo.getwRegistroCompativel())){
						vo.setIndComparacao(DominioIndComparacao.I); 			// Comparação entre itens
						vo.setIndCompatExclus(DominioIndCompatExclus.INP);		// Item não cobrado pelo procedimento
						
					} else{
						vo.setIndComparacao(DominioIndComparacao.R); 			// Comparação entre procedimento realizado e item
						vo.setIndCompatExclus(DominioIndCompatExclus.PNI); 		// Procedimento realizado não cobra esse item
					}
				} else if(Boolean.TRUE.equals(vo.getIndAmbulatorio())){
					vo.setIndCompatExclus(DominioIndCompatExclus.INP); 			// Item não cobrado pelo procedimento
					vo.setIndComparacao(DominioIndComparacao.I); 				// Comparação entre itens
				}
			break;
		}
	}

	private void validarRegistroPrincipal(FatTabCompatibilidadeVO vo, final ControleProcessadorArquivosImportacaoSus controle) {
		switch (Integer.parseInt(vo.getwRegistroPrincipal())) {
			
			case 3:	// AIH (Proc. Principal)
				vo.setIndAmbulatorio(Boolean.FALSE);
				vo.setIndInternacao(Boolean.TRUE);
				
				// AIH (Proc. Especial) 05 AIH (Proc. Secundário)
				if(CoreUtil.notIn(vo.getwRegistroCompativel(), TRES, QUATRO, CINCO)){ 
					controle.gravarLog(STRING_COD_SUS +vo.getwProcedimentoPrincipal() + 
								   STRING_COM_TIPO_DE_REGISTRO_COMPATIVEL +vo.getwRegistroCompativel()+
								   STRING_INVALIDO );
				}
			break;
			
			case 4:	// 04 AIH (Proc. Especial)
				if(StringUtils.equals(QUATRO, vo.getwRegistroCompativel())){
					vo.setIndAmbulatorio(Boolean.FALSE);
					vo.setIndInternacao(Boolean.TRUE);
				}
			break;
			
			case 6: // 06 APAC (Proc. Principal)
				vo.setIndAmbulatorio(Boolean.TRUE);
				vo.setIndInternacao(Boolean.FALSE);
				
				if(!StringUtils.equals(SETE, vo.getwRegistroCompativel())){	// APAC (Proc. Secundário)
					controle.gravarLog(STRING_COD_SUS +vo.getwProcedimentoPrincipal() + 
							       STRING_COM_TIPO_DE_REGISTRO_COMPATIVEL +vo.getwRegistroCompativel()+
							       STRING_INVALIDO );
				}
			break;

			case 7:	// 07 APAC (Proc. Secundário)
				if(StringUtils.equals(SETE, vo.getwRegistroCompativel())){
					vo.setIndAmbulatorio(Boolean.TRUE);
					vo.setIndInternacao(Boolean.FALSE);
				}
			break;
			
			default:
				controle.gravarLog(STRING_COD_SUS +vo.getwProcedimentoPrincipal() + 
						   	   " com tipo de registro:" +vo.getwRegistroPrincipal()+
						   	   STRING_TIPO+vo.getwTpCompatibilidade()+
						   	   STRING_INVALIDO );
				break;
		}
	}

	private FatTabCompatibilidadeVO lerLinhaArquivoCompatibilidade(String strLine, int linha, final ControleProcessadorArquivosImportacaoSus controle) {
		
		final FatTabCompatibilidadeVO vo = new FatTabCompatibilidadeVO();
		
		try {
			vo.setwRetorno(true);
			vo.setwProcedimentoPrincipal(strLine.substring(0,10));			// SUBSTR(reg_importacao,1,10);
			vo.setwRegistroPrincipal(strLine.substring(10,12)); 			// SUBSTR(reg_importacao,11,2); 
			vo.setwProcedimentoCompativel(strLine.substring(12,22));		// SUBSTR(reg_importacao,13,10);
			vo.setwRegistroCompativel(strLine.substring(22, 24)); 			// SUBSTR(reg_importacao,23,2); 
			vo.setwTpCompatibilidade(strLine.substring(24,25)); 			// SUBSTR(reg_importacao,25,1); 
			vo.setwQtPermitida(Integer.valueOf(strLine.substring(25,29)));	// TO_NUMBER(SUBSTR(reg_importacao,26,4));
			vo.setwDtCompetencia(strLine.substring(29,35));					// SUBSTR(reg_importacao,30,6); 

		} catch (final IndexOutOfBoundsException iae) {
			// Tamanho da linha menor q o esperado
			util.tratarExcecaoNaoLancada(iae, controle.getLogRetorno(), ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR
					+ "Erro ao ler Procedimento: Tamanho da linha menor que o esperado." + 
					ControleProcessadorArquivosImportacaoSus.LINE_SEPARATOR + "Linha: [{0}]:[{1}]",
					linha, strLine);
		}
		return vo;
	}
	
	/** Forms: concatena_tab */
	private void concatenaTab(final List<ProcedimentosXCompatibilidadesVO> tabSubTabCompHcpa,
							  final StringBuilder vCompatibilidades,
							  final StringBuilder vCompatibilidadesAux ) {
		
		for (ProcedimentosXCompatibilidadesVO vo : tabSubTabCompHcpa) {
			
			final String qtde = StringUtils.leftPad(vo.getQuantidade().toString(), 5, BigDecimal.ZERO.toString());
			final String codTabela = StringUtils.leftPad(vo.getCodSUS().toString(), 10, BigDecimal.ZERO.toString());
			
			vCompatibilidades.append(codTabela)
					        .append(vo.getTipoComparacao())
					        .append(vo.getTipoCompatibilidade())
					        .append(vo.getIndAmbulatorio())
					        .append(vo.getIndInternacao())
					        .append(qtde);

			vCompatibilidadesAux.append(codTabela)
							   .append(StringUtils.leftPad(StringUtils.EMPTY, 4))
					 		   .append(vo.getIndAmbulatorio())
					 		   .append(vo.getIndInternacao())
					 		   .append(qtde);
		}
	}
	
	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}
	
	protected FatProcedimentoCboDAO getFatProcedimentoCboDAO() {
		return fatProcedimentoCboDAO;
	}
	
	protected FatCompetenciaCompatibilidDAO getFatCompetenciaCompatibilidDAO() {
		return fatCompetenciaCompatibilidDAO;
	}
	
	protected FatVlrItemProcedHospCompsDAO getFatVlrItemProcedHospCompsDAO() {
		return fatVlrItemProcedHospCompsDAO;
	}

	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}

	protected FatTipoTransplanteDAO getFatTipoTransplanteDAO() {
		return fatTipoTransplanteDAO;
	}
	
	protected FatCompetenciaCompatibilidRN getFatCompetenciaCompatibilidRN() {
		return fatCompetenciaCompatibilidRN;
	}

	protected FatCompatExclusItemON getFatCompatExclusItemON() {
		return fatCompatExclusItemON;
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