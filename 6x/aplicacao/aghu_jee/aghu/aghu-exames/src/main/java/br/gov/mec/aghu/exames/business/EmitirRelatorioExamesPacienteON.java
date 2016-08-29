package br.gov.mec.aghu.exames.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteDetalhesCampoEditVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteDetalhesLinhaVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteDetalhesVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteExamesDetalhesVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteExamesVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacientesListaLegendaVO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacientesListaObservacaoVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelPolSumExameMasc;
import br.gov.mec.aghu.model.AelPolSumExameTab;
import br.gov.mec.aghu.model.AelPolSumLegenda;
import br.gov.mec.aghu.model.AelPolSumMascCampoEdit;
import br.gov.mec.aghu.model.AelPolSumMascLinha;
import br.gov.mec.aghu.model.AelPolSumObservacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * Utilizado estoria #5945 - POL - Emitir Relatório de Exames do Paciente
 * 
 * @author Guilherme Finotti Carvalho
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class EmitirRelatorioExamesPacienteON extends BaseBMTBusiness {
	

	private static final int TEMPO_12_HORAS = (60 * 60 * 12); // 12 horas
	
	@EJB
	private EmitirRelatorioExamesPacienteRN emitirRelatorioExamesPacienteRN;
	
	private static final Log LOG = LogFactory.getLog(EmitirRelatorioExamesPacienteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelMaterialAnaliseDAO aelMaterialAnaliseDAO;
	
	@Inject
	private AelExamesDAO aelExamesDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	public enum EmitirRelatorioExamesPacienteONExceptionCode implements BusinessExceptionCode {
		AEL_01516;
	}

	private static final long serialVersionUID = 6444745957951758755L;

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	/**
	 * Acesso ao modulo aghuFacade
	 * @return
	 */
	private IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	/**
	 * Retorna RN responsavel pela execucao da procedure AELK_POL_SUM_EXAMES
	 * @return
	 */
	private EmitirRelatorioExamesPacienteRN getEmitirRelatorioExamesPacienteRN() {
		return emitirRelatorioExamesPacienteRN;
	}
	

	protected AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}
	
	protected AelMaterialAnaliseDAO getAelMaterialAnaliseDAO() {
		return aelMaterialAnaliseDAO;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public RelatorioExamesPacienteVO montarRelatorio(Integer prontuario, Integer atdSeq, DominioSumarioExame pertenceSumario, Date dthrEvento, Boolean recemNascido, String pertenceSumarioRodape, Integer pacCodigo) throws BaseException {

		beginTransaction(TEMPO_12_HORAS);
		
		RelatorioExamesPacienteVO vo = new RelatorioExamesPacienteVO();
		Map<String,Object> parametros = new HashMap<String, Object>();
		// ================ Previnir erro PMD =================
		
		parametros = getEmitirRelatorioExamesPacienteRN().executarProcedure(prontuario, atdSeq, pertenceSumario, dthrEvento, recemNascido, pertenceSumarioRodape);

		List<AelPolSumExameTab> listaPolExamesTab = (List<AelPolSumExameTab>)parametros.get("listaAelPolSumExameTab");
		if(listaPolExamesTab != null && listaPolExamesTab.isEmpty()){
			commitTransaction();
			throw new ApplicationBusinessException(EmitirRelatorioExamesPacienteONExceptionCode.AEL_01516);
		}
		
		//Collections.sort(listaPolExamesTab);
		
		vo.setProntuario(prontuario.toString());
		
		//preencher VO
		//List<AelPolSumExameTab> listaAelPolSumExameTabQSue = executarQSue(vo, prontuario, parametros, pacCodigo);
		
		//executarQSue2(vo, prontuario, parametros,listaAelPolSumExameTabQSue);
		
		executarQSueQSue2(vo, prontuario, parametros, pacCodigo);
		executarQLegenda(vo,parametros);
		executarQObservacao(vo,parametros);
		executarQSem(vo,parametros);
		executarQMascLinha(vo,parametros);
		executarQMascCampoEdit(vo,parametros);
		
		List<RelatorioExamesPacienteExamesVO> listExamesPacienteExamesVO = vo.getExames();
		for(RelatorioExamesPacienteExamesVO i: listExamesPacienteExamesVO){
			i.setRodape(refazerRodape(i.getTipoExame()));
		}
			
		List<AelPolSumObservacao> listaPolExamesObservacao = (List<AelPolSumObservacao>)parametros.get("listaAelPolSumObservacao");
		
		processaPolSumObservacao(listExamesPacienteExamesVO, listaPolExamesObservacao);
		
		//List<AelPolSumLegenda> listaAelPolSumLegenda = (List<AelPolSumLegenda>)parametros.get("listaAelPolSumLegenda");
		configurarIdentificacao(prontuario, atdSeq, vo);
		
		// ##### parametros para o relatorio #####
		//p_data: '01'||'mmyyyy'	
		Date data = definirDataParametro();
		if(data != null) {
			data = null;
		}

		commitTransaction();
		return vo;
	}

	private void processaPolSumObservacao(List<RelatorioExamesPacienteExamesVO> listExamesPacienteExamesVO, List<AelPolSumObservacao> listaPolExamesObservacao) {
		
		for(AelPolSumObservacao aelPolSumObservacao : listaPolExamesObservacao) {
			
			//percorrendo lista QSUE2
			for(int i = 0; i < listExamesPacienteExamesVO.size(); i++) {
				
				if(listExamesPacienteExamesVO.get(i).getListExamesPacientesListaObservacaoVO() == null){
					listExamesPacienteExamesVO.get(i).setListExamesPacientesListaObservacaoVO(new ArrayList<RelatorioExamesPacientesListaObservacaoVO>());
				}

				String dataEventoAux = "";
				for(int j = 0; j < listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().size();j++){
					
					if(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j) != null) {
						if(validaCondicaoIf(validarSeAtributoNaoEhNull(DateUtil.obterDataFormatada(aelPolSumObservacao.getId().getDthrEvento(), "dd/MM/yy")+" "+DateUtil.obterDataFormatada(aelPolSumObservacao.getId().getDthrEvento(), "HH:mm")),
								(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getDataEvento())) &&			
						   validaCondicaoIf(aelPolSumObservacao.getId().getPertenceSumario(),
								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getRodape())) &&
						   validaCondicaoIf(validarSeAtributoNaoEhNull(aelPolSumObservacao.getId().getProntuario()),
								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getProntuarioObs())) &&
						   validaCondicaoIf(aelPolSumObservacao.getId().getRecemNascido(),
								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getRecemNascidoObs()))){
							if(aelPolSumObservacao.getId().getCodigoMensagem() != null ){
								String dataEvento = listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getDataEvento();
								StringBuffer buffer = new StringBuffer(dataEvento);
								buffer.append(" (").append(aelPolSumObservacao.getId().getCodigoMensagem()).append(')');					
								
								if(!dataEventoAux.equals(dataEvento)){
									StringBuffer buffer2 = new StringBuffer();
									buffer2.append('(').append(aelPolSumObservacao.getId().getCodigoMensagem()).append(')');
									RelatorioExamesPacientesListaObservacaoVO voObservacao = new RelatorioExamesPacientesListaObservacaoVO();
									voObservacao.setCodigoObservacao(buffer2.toString());
									voObservacao.setDescricaoObservacao(aelPolSumObservacao.getId().getDescricao());
									listExamesPacienteExamesVO.get(i).getListExamesPacientesListaObservacaoVO().add(voObservacao);	
								}
								dataEventoAux = dataEvento;
								listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).setDataEvento(buffer.toString());
							}
						}
					}
				}
			}
		}
	}

	private void configurarIdentificacao(Integer prontuario, Integer atdSeq,
			RelatorioExamesPacienteVO vo) {
		AghAtendimentos atendimento = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
		StringBuilder linhaProntuarios = new StringBuilder();
		if(atendimento.getOrigem().equals(DominioOrigemAtendimento.N) && atendimento.getGsoPacCodigo() != null){
			linhaProntuarios.append(CoreUtil.formataProntuario(prontuario)).append(" MAE: ").append(atendimento.getGsoPacCodigo());
		}else {
			linhaProntuarios.append(CoreUtil.formataProntuario(prontuario));
		}
		vo.setIdentificacao(linhaProntuarios.toString());
	}
	
	
	/**
	 * Pega a data atual e altera o dia do mes para 1
	 * @return
	 */
	private Date definirDataParametro() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}
	
	private void executarQSueQSue2(RelatorioExamesPacienteVO vo, Integer prontuario, Map map, Integer pacCodigo) {
		List<AipPacientes> listaAipPacientesConsulta = getPacienteFacade().executarCursorPac(pacCodigo);
		List<RelatorioExamesPacienteExamesDetalhesVO> listPacienteExamesDetalhes = new ArrayList<RelatorioExamesPacienteExamesDetalhesVO>();
		String tipoExame = "";
		
		if(listaAipPacientesConsulta.size() > 0) {
			List<AelPolSumExameTab> listaPolExamesTab = (List<AelPolSumExameTab>)map.get("listaAelPolSumExameTab");
			//verifica se listaPolExamesTab tem registro, caso não tenha, não tem join. :(
			if(listaPolExamesTab != null) {
				if(listaPolExamesTab.size() > 0) {
					//:p_prontuario = pac.prontuario and pac.prontuario = sue.prontuario 
					for(AipPacientes aipPacientes : (List<AipPacientes>)listaAipPacientesConsulta) {
						if(aipPacientes.getProntuario().equals(prontuario) ) {
							for(AelPolSumExameTab aelPolSumExameTab : listaPolExamesTab) {
								vo.setProntuario(validarSeAtributoNaoEhNull(aelPolSumExameTab.getId().getProntuario())); //prontuario
								vo.setNome(formataNomeQSUE(aelPolSumExameTab, aipPacientes)); //nome
								vo.setRecemNascido(aelPolSumExameTab.getId().getRecemNascido());//recem nascido
								vo.setLtoLtoId(decode(aelPolSumExameTab.getId().getLtoLtoId(), "XXXXX")); //ltoltoid
								vo.setDthrFim(decode(aelPolSumExameTab.getId().getDthrFim(),DateUtil.obterData(1900, 1, 1, 00, 00)).toString()); //dhtrfim
								
								tipoExame = decodeSumarioExame(aelPolSumExameTab.getId().getPertenceSumario(), 
										aelPolSumExameTab.getId().getIndImprime());
								
								//MONTAR OBJETO INTERNO
								RelatorioExamesPacienteExamesDetalhesVO examesPacienteExamesVO = new RelatorioExamesPacienteExamesDetalhesVO();
								
								//pertence-sumario
								examesPacienteExamesVO.setPertenceSumario(tipoExame);
								
								examesPacienteExamesVO.setRodape(validarSeAtributoNaoEhNull(aelPolSumExameTab.getId().getPertenceSumario()));//pertence_sumario_rodape
								examesPacienteExamesVO.setNome(validarSeAtributoNaoEhNull(aelPolSumExameTab.getId().getCalNomeSumario()));//cal_nome
								examesPacienteExamesVO.setValor(formataReeValor(aelPolSumExameTab.getId().getReeValor())); //ree_valor
								examesPacienteExamesVO.setProntuarioObs(validarSeAtributoNaoEhNull(aelPolSumExameTab.getId().getProntuario())); //prontuario_obs
								examesPacienteExamesVO.setRecemNascidoObs(aelPolSumExameTab.getId().getRecemNascido()); //recem_nascido_obs
								examesPacienteExamesVO.setDataEvento(DateUtil.obterDataFormatada(aelPolSumExameTab.getId().getDthrEventoAreaExec(), "dd/MM/yy")+" "+DateUtil.obterDataFormatada(aelPolSumExameTab.getId().getDthrEventoAreaExec(), "HH:mm"));
								examesPacienteExamesVO.setDataHora(aelPolSumExameTab.getId().getDthrEventoAreaExec());
								examesPacienteExamesVO.setHoraEvento(DateUtil.obterDataFormatada(aelPolSumExameTab.getId().getDthrEventoAreaExec(), "HH:mm"));
								examesPacienteExamesVO.setLtoLtoId2(decode(aelPolSumExameTab.getId().getLtoLtoId(), "XXXXX"));
								examesPacienteExamesVO.setDthrFim2(decode(aelPolSumExameTab.getId().getDthrFim(),DateUtil.obterData(1900, 1, 1, 00, 00)).toString());
								examesPacienteExamesVO.setOrdem(aelPolSumExameTab.getId().getOrdem());
													
								//ADICIONAR NA LISTA DE OBJETO
								listPacienteExamesDetalhes.add(examesPacienteExamesVO);
							}
							
						}
					}
				}
			}
					
		}
		vo.setExames(preencherListasSumarioPertence(listPacienteExamesDetalhes));		
		
	}
	                                              
	
	private String refazerRodape(String pertenceSumario) {
		if(pertenceSumario.equals("Bioquímica")){
			return "Unidades: Enzimas = U/L   ** Na,K,Cl,CO2 = mEq/L   ** Bioquímica = mg/dL";
		}else if(pertenceSumario.equals("Hematologia")){
			return "* Os resultados do leucograma diferencial são dados em %";
		}else{
			return "";
		}
	}

	/**
	 * Q_LEGENDA
	 * @param vo
	 */
	private void executarQLegenda(RelatorioExamesPacienteVO vo,Map map) {
		
		List<AelPolSumLegenda> listaPolExamesTab = (List<AelPolSumLegenda>)map.get("listaAelPolSumLegenda");
		List<RelatorioExamesPacienteExamesVO> listExamesPacienteExamesVO = vo.getExames();
		
		for(AelPolSumLegenda aelPolSumLegenda : listaPolExamesTab) {
		
			//percorrendo lista QSUE2
			if(listExamesPacienteExamesVO.size() > 0) {
				for(int i = 0; i < listExamesPacienteExamesVO.size(); i++) {
					
					if(listExamesPacienteExamesVO.get(i) != null) {
						
						if (listExamesPacienteExamesVO.get(i).getListExamesPacientesListaLegendaVO() == null) {
							listExamesPacienteExamesVO.get(i).setListExamesPacientesListaLegendaVO(new ArrayList<RelatorioExamesPacientesListaLegendaVO>());
						}
					
						for(int j = 0; j < listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().size();j++){

							
							if(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j) != null) {
								
								if(validaCondicaoIf(validarSeAtributoNaoEhNull(aelPolSumLegenda.getId().getDthrFim()),
										(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getDthrFim2())) &&
								   validaCondicaoIf(aelPolSumLegenda.getId().getLtoLtoId(),
										(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getLtoLtoId2())) &&
								   validaCondicaoIf(aelPolSumLegenda.getId().getPertenceSumario(),
										(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getRodape())) &&
								   validaCondicaoIf(validarSeAtributoNaoEhNull(aelPolSumLegenda.getId().getProntuario()),
										(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getProntuarioObs())) &&
								   validaCondicaoIf(aelPolSumLegenda.getId().getRecemNascido(),
										(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getRecemNascidoObs()))) {
									
									RelatorioExamesPacientesListaLegendaVO voLegenda = new RelatorioExamesPacientesListaLegendaVO();
									voLegenda.setNumeroLegenda(validarSeAtributoNaoEhNull(aelPolSumLegenda.getId().getNumeroLegenda()));
									voLegenda.setDescricaoLegenda(aelPolSumLegenda.getId().getDescricao());
									voLegenda.setGrupoLegenda(validarSeAtributoNaoEhNull(aelPolSumLegenda.getId().getGrupoLegenda()));
									listExamesPacienteExamesVO.get(i).getListExamesPacientesListaLegendaVO().add(voLegenda);
									break;
//									listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).setNumero(validarSeAtributoNaoEhNull(aelPolSumLegenda.getId().getNumeroLegenda()));
//									listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).setDescricaoLegenda(aelPolSumLegenda.getId().getDescricao());
//									listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).setGrupo(validarSeAtributoNaoEhNull(aelPolSumLegenda.getId().getGrupoLegenda()));
									
								}
							}
						}
						if(!listExamesPacienteExamesVO.get(i).getListExamesPacientesListaLegendaVO().isEmpty()){
							Collections.sort(listExamesPacienteExamesVO.get(i).getListExamesPacientesListaLegendaVO());
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Q_OBSERVACAO
	 * @param vo
	 * @param map
	 */
	private void executarQObservacao(RelatorioExamesPacienteVO vo,Map map) {
		
		List<AelPolSumObservacao> listaPolExamesObservacao = (List<AelPolSumObservacao>)map.get("listaAelPolSumObservacao");
		List<RelatorioExamesPacienteExamesVO> listExamesPacienteExamesVO = vo.getExames();
		
		for(AelPolSumObservacao aelPolSumObservacao : listaPolExamesObservacao) {
			
			//percorrendo lista QSUE2
			for(int i = 0; i < listExamesPacienteExamesVO.size(); i++) {

				for(int j = 0; j < listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().size();j++){
					
					if(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j) != null) {
						
						if(validaCondicaoIf(validarSeAtributoNaoEhNull(aelPolSumObservacao.getId().getDthrFim()),
								(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getDthrFim2())) &&
						   validaCondicaoIf(aelPolSumObservacao.getId().getLtoLtoId(),
								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getLtoLtoId2())) &&
						   validaCondicaoIf(aelPolSumObservacao.getId().getPertenceSumario(),
								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getRodape())) &&
						   validaCondicaoIf(validarSeAtributoNaoEhNull(aelPolSumObservacao.getId().getProntuario()),
								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getProntuarioObs())) &&
						   validaCondicaoIf(aelPolSumObservacao.getId().getRecemNascido(),
								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getRecemNascidoObs()))){
							
							listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).setCodigo(validarSeAtributoNaoEhNull(aelPolSumObservacao.getId().getCodigoMensagem()));
							listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).setDescricaoObservacao(aelPolSumObservacao.getId().getDescricao());
						}
					}
				}
			}	
		}
	}

	
	/**
	 * Q_SEM
	 * @param vo
	 * @param map
	 */
	private void executarQSem(RelatorioExamesPacienteVO vo,Map map) {
		
		List<AelPolSumExameMasc> listAelPolSumExameMasc = (List<AelPolSumExameMasc>)map.get("listaAelPolSumExameMasc");
		//List<RelatorioExamesPacienteExamesVO> listExamesPacienteExamesVO = vo.getExames();
		List<RelatorioExamesPacienteDetalhesVO> listExamesPacienteDetalhesVO = vo.getExamesDetalhes();
		if(listExamesPacienteDetalhesVO == null){
			listExamesPacienteDetalhesVO = new ArrayList<RelatorioExamesPacienteDetalhesVO>();
		}
		AelExames aelExames = null;
		AelMateriaisAnalises aelMateriaisAnalises = null;
		AghUnidadesFuncionais aghUnidadesFuncionais = null;
		
		for(AelPolSumExameMasc aelPolSumExameMasc : listAelPolSumExameMasc) {
			
//			//percorrendo lista QSUE
//			for(int i = 0; i < listExamesPacienteExamesVO.size(); i++) {
				
				//executa ael_exames
				if(aelPolSumExameMasc.getId().getUfeEmaExaSigla() != null) {
					aelExames = getAelExamesDAO().obterPeloId(validarSeAtributoNaoEhNull(aelPolSumExameMasc.getId().getUfeEmaExaSigla()));
				}
				if(aelPolSumExameMasc.getId().getUfeEmaManSeq() != null) {
					aelMateriaisAnalises = getAelMaterialAnaliseDAO().obterOriginal(aelPolSumExameMasc.getId().getUfeEmaManSeq());
				}
				if(aelPolSumExameMasc.getId().getUfeUnfSeq() != null) {
					aghUnidadesFuncionais = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(aelPolSumExameMasc.getId().getUfeUnfSeq());
				}
				
//				for(int j = 0; j < listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().size();j++){
//					
//					if(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j) != null) {
				
						if(
//								validaCondicaoIf(validarSeAtributoNaoEhNull(aelPolSumExameMasc.getId().getDthrFim()),
//								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getDthrFim2())) &&
//						   validaCondicaoIf(aelPolSumExameMasc.getId().getLtoLtoId(),
//								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getLtoLtoId2())) &&
//						   validaCondicaoIf(validarSeAtributoNaoEhNull(aelPolSumExameMasc.getId().getProntuario()),
//								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getProntuarioObs())) &&
//						   validaCondicaoIf(aelPolSumExameMasc.getId().getRecemNascido(),
//								   (listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getRecemNascidoObs())) &&
						   aelExames != null &&
						   aelMateriaisAnalises != null &&
						   aghUnidadesFuncionais != null
						   ) {
							
							//sempre será apenas um registro
							RelatorioExamesPacienteDetalhesVO examesPacienteDetalhesVO = new RelatorioExamesPacienteDetalhesVO();
							examesPacienteDetalhesVO.setOrdemRelatorio(aelPolSumExameMasc.getId().getOrdemRelatorio());
							examesPacienteDetalhesVO.setOrdemAgrupamento(aelPolSumExameMasc.getId().getOrdemAgrupamento());
							examesPacienteDetalhesVO.setExaDescricao(aelExames.getDescricao());
							examesPacienteDetalhesVO.setManDescricao(aelMateriaisAnalises.getDescricao());
							examesPacienteDetalhesVO.setUnfDescricao(aghUnidadesFuncionais.getDescricao());
							examesPacienteDetalhesVO.setDataHoraEvento(DateUtil.obterDataFormatada(aelPolSumExameMasc.getId().getDthrEventoLib(), "dd/MM/yyyy"));
							
							listExamesPacienteDetalhesVO.add(examesPacienteDetalhesVO);
							//break;
						}
					//}
				//}
			//}
		}
		
		Collections.sort(listExamesPacienteDetalhesVO,  new Comparator<RelatorioExamesPacienteDetalhesVO>() {
			@Override
			public int compare(RelatorioExamesPacienteDetalhesVO o1, RelatorioExamesPacienteDetalhesVO o2) {
				int compare = o1.getOrdemAgrupamento().compareTo(o2.getOrdemAgrupamento());
				if (compare==0){
					return o1.getOrdemRelatorio().compareTo(o2.getOrdemRelatorio());
				}
				return compare;
			}
		});
		vo.setExamesDetalhes(listExamesPacienteDetalhesVO);
	}
	
	/**
	 * Q_MASC_LINHA
	 * @param vo
	 * @param map
	 */
	private void executarQMascLinha(RelatorioExamesPacienteVO vo,Map map) {
		
		List<AelPolSumMascLinha> listAelPolSumMascLinha = (List<AelPolSumMascLinha>)map.get("listaAelPolSumMascLinha");
		List<RelatorioExamesPacienteDetalhesVO> listExamesPacienteDetalhesVO = vo.getExamesDetalhes();
//		List<RelatorioExamesPacienteDetalhesVO> listExamesPacienteDetalhesVOAux = new ArrayList<RelatorioExamesPacienteDetalhesVO>();

		if(listExamesPacienteDetalhesVO != null) {
			//percorrendo lista QSEM
			for(int i = 0; i < listExamesPacienteDetalhesVO.size(); i++) {
				listExamesPacienteDetalhesVO.get(i).setListLinhas(new ArrayList<RelatorioExamesPacienteDetalhesLinhaVO>());
				for(AelPolSumMascLinha aelPolSumMascLinha : listAelPolSumMascLinha) {			
					
					if(validaCondicaoIf(aelPolSumMascLinha.getId().getOrdemRelatorio(),(listExamesPacienteDetalhesVO.get(i).getOrdemRelatorio()))){
//						listExamesPacienteDetalhesVO.get(i).setDescricaoMascLinha(aelPolSumMascLinha.getId().getDescricao());
//						RelatorioExamesPacienteDetalhesVO voDetalhes = new RelatorioExamesPacienteDetalhesVO();
//						voDetalhes.setDataHoraEvento(listExamesPacienteDetalhesVO.get(i).getDataHoraEvento());
//						voDetalhes.setDescricaoMascCampoEdit(listExamesPacienteDetalhesVO.get(i).getDescricaoMascCampoEdit());
//						voDetalhes.setDescricaoMascLinha(listExamesPacienteDetalhesVO.get(i).getDescricaoMascLinha());
//						voDetalhes.setExaDescricao(listExamesPacienteDetalhesVO.get(i).getExaDescricao());
//						voDetalhes.setManDescricao(listExamesPacienteDetalhesVO.get(i).getManDescricao());
//						voDetalhes.setNroLinha(listExamesPacienteDetalhesVO.get(i).getNroLinha());
//						voDetalhes.setOrdemAgrupamento(listExamesPacienteDetalhesVO.get(i).getOrdemAgrupamento());
//						voDetalhes.setOrdemRelatorio(listExamesPacienteDetalhesVO.get(i).getOrdemRelatorio());
//						voDetalhes.setUnfDescricao(listExamesPacienteDetalhesVO.get(i).getUnfDescricao());
//						listExamesPacienteDetalhesVOAux.add(voDetalhes);
//						listExamesPacienteDetalhesVO.get(i).setOrdemRelatorio(aelPolSumMascLinha.getId().getOrdemRelatorio());
//						listExamesPacienteDetalhesVO.get(i).setNroLinha(aelPolSumMascLinha.getId().getNroLinha());
						RelatorioExamesPacienteDetalhesLinhaVO voLinha = new RelatorioExamesPacienteDetalhesLinhaVO();
						voLinha.setDescricaoMascLinha(aelPolSumMascLinha.getId().getDescricao());
						voLinha.setNroLinha(aelPolSumMascLinha.getId().getNroLinha());
						voLinha.setOrdemRelatorio(aelPolSumMascLinha.getId().getOrdemRelatorio());
						listExamesPacienteDetalhesVO.get(i).getListLinhas().add(voLinha);
					}
				}
				
			}
		}
	}

	/**
	 * Q_MASC_CAMPO_EDIT
	 * @param vo
	 * @param map
	 */
	private void executarQMascCampoEdit(RelatorioExamesPacienteVO vo,Map map) {
		
		List<AelPolSumMascCampoEdit> listAelPolSumMascCampoEdit = (List<AelPolSumMascCampoEdit>)map.get("listaAelPolSumMascCampoEdit");
		List<RelatorioExamesPacienteDetalhesVO> listExamesPacienteDetalhesVO = vo.getExamesDetalhes();

		//percorrendo lista QMASCLINHA
		if(listExamesPacienteDetalhesVO != null) {
			for(int i = 0; i < listExamesPacienteDetalhesVO.size(); i++) {
				
				listExamesPacienteDetalhesVO.get(i).setListCamposEdit(new ArrayList<RelatorioExamesPacienteDetalhesCampoEditVO>());
				for(AelPolSumMascCampoEdit campoEdit : listAelPolSumMascCampoEdit) {			
					
					if(validaCondicaoIf(validarSeAtributoNaoEhNull(campoEdit.getId().getNroLinha()),(listExamesPacienteDetalhesVO.get(i).getNroLinha())) &&
					 validaCondicaoIf(validarSeAtributoNaoEhNull(campoEdit.getId().getOrdemRelatorio()),(listExamesPacienteDetalhesVO.get(i).getOrdemRelatorio()))) {
						
						RelatorioExamesPacienteDetalhesCampoEditVO voLinha = new RelatorioExamesPacienteDetalhesCampoEditVO();
						voLinha.setDescricaoMascCampoEdit(campoEdit.getId().getDescricao());
						listExamesPacienteDetalhesVO.get(i).getListCamposEdit().add(voLinha);
					}
				}
			}
		}
	}
	
	
	
	
	private String formataReeValor(Double reeValor) {
		
		String retorno = String.valueOf(reeValor);  
		retorno = retorno.replace(".", ","); 
		if( (retorno.contains(",0") && retorno.indexOf(",0") != (retorno.length()-3)) || retorno.contains(",00")){
			return retorno.substring(0, retorno.indexOf(','));
		}else{
			return retorno;
		}
//		
////		if(reeValor != null) {
////			if(reeValor == NumberUtil.truncate(reeValor, 0)) {
////				retorno = reeValor.toString();
////			}else {
////				retorno = reeValor.toString().concat(",");
////			}
////		}
//		return retorno;
	}

	private String decodeSumarioExame(DominioSumarioExame sumario, String indImprime) {
		
		String retorno = "";
		
		if(sumario != null && !StringUtils.isBlank(indImprime)) {
		
			if(sumario.equals(DominioSumarioExame.B)) {
				retorno = DominioSumarioExame.B.getDescricao();
			}else if(sumario.equals(DominioSumarioExame.E)) {
				retorno = DominioSumarioExame.E.getDescricao();
			}else if(sumario.equals(DominioSumarioExame.G)) {
				retorno = DominioSumarioExame.G.getDescricao();
			}else if(sumario.equals(DominioSumarioExame.H)) {
				retorno = DominioSumarioExame.H.getDescricao();
			}else if(sumario.equals(DominioSumarioExame.E)) {
				retorno = DominioSumarioExame.E.getDescricao();
			}else {
				if(StringUtils.isNotBlank(indImprime) && indImprime.equals("N")) {
					retorno = "Nenhum exame de tabela realizado.";
				}else if(!indImprime.equals("N")) {
					retorno = null;
				}
			}
		}
		
		return retorno;
	}
	
	private String decode(String value, String ret) {  
		
		String retorno = "";
		if(StringUtils.isBlank(value)) {
			retorno = ret; 
		} else {
			retorno = value;
		}
		
		return retorno;  
	}  

	private Date decode(Date value, Date ret) {  
		
		Date retorno;
		if(value ==  null) {
			retorno = ret; 
		} else {
			retorno = value;
		}
		
		return retorno;  
	} 
	
	private String formataNomeQSUE(AelPolSumExameTab aelPolSumExameTab, AipPacientes aipPacientes) {
		
		String nome = "";
		if(aelPolSumExameTab.getId().getProntuario() > VALOR_MAXIMO_PRONTUARIO) {
			nome = aipPacientes.getNome();
		}else {
			if(Boolean.TRUE.equals(aelPolSumExameTab.getId().getRecemNascido())) {
				nome = "RN de " + aipPacientes.getNome();
			}else {
				nome = aipPacientes.getNome();
			}
		}
		
		return nome;
	}
	/**
	 * axilia a tirar a complexidade dos metodos (PMD)
	 *
	 * verifica se condicao tem algum comparador null:
	 * caso sim: verifica o valor do outro, se null, return true
	 * caso não: verifica o valor do outro, se not null, return false
	 * 
	 * @param object, obeject
	 * @return boolean
	 */
	private boolean validaCondicaoIf(Object param1, Object param2) {
		boolean retorno = false;
		
		if(param1 == null && param2 == null) {
			retorno = true;
		} 
		else if(param1 != null && param2 != null) {
			retorno = param1.equals(param2) || param1.toString().equals(param2.toString());
		}
		
		return retorno;
	}
	
	/**
	 * verifica se atributo não é null e retorna 
	 * o valor toString()
	 */
	private String validarSeAtributoNaoEhNull(Object obj) {
		
		String retorno = "";
		if(obj != null) {
			retorno = obj.toString().toUpperCase();
		} else {
			retorno = null;
		}
		
		return retorno;
	}

	
	/**
	 * Metodo que cria as listas de dominiosumariopertence.
	 * A criação das listas está estática.
	 * 
	 */
	private List<RelatorioExamesPacienteExamesVO> preencherListasSumarioPertence(List<RelatorioExamesPacienteExamesDetalhesVO> listDetalhesVO ){

		List<RelatorioExamesPacienteExamesDetalhesVO> listaBioquimica = new ArrayList<RelatorioExamesPacienteExamesDetalhesVO>();
		List<RelatorioExamesPacienteExamesDetalhesVO> listaGasometria = new ArrayList<RelatorioExamesPacienteExamesDetalhesVO>();
		List<RelatorioExamesPacienteExamesDetalhesVO> listaEQU = new ArrayList<RelatorioExamesPacienteExamesDetalhesVO>();
		List<RelatorioExamesPacienteExamesDetalhesVO> listaHematologia = new ArrayList<RelatorioExamesPacienteExamesDetalhesVO>();
		
		List<RelatorioExamesPacienteExamesVO> listExamesPacienteExamesVO = new ArrayList<RelatorioExamesPacienteExamesVO>();  
		
		RelatorioExamesPacienteExamesVO examesPacienteBioquimica = new RelatorioExamesPacienteExamesVO();
		RelatorioExamesPacienteExamesVO examesPacienteGasometria = new RelatorioExamesPacienteExamesVO();
		RelatorioExamesPacienteExamesVO examesPacienteEQU = new RelatorioExamesPacienteExamesVO();
		RelatorioExamesPacienteExamesVO examesPacienteHematologia = new RelatorioExamesPacienteExamesVO();
		
		
		for(RelatorioExamesPacienteExamesDetalhesVO examesDetalhesVO : listDetalhesVO) {
			
			if(examesDetalhesVO.getPertenceSumario().equals(DominioSumarioExame.B.getDescricao())) {
				listaBioquimica.add(examesDetalhesVO);
			}
			if(examesDetalhesVO.getPertenceSumario().equals(DominioSumarioExame.E.getDescricao())) {
				listaGasometria.add(examesDetalhesVO);
			}
			if(examesDetalhesVO.getPertenceSumario().equals(DominioSumarioExame.G.getDescricao())) {
				listaEQU.add(examesDetalhesVO);
			}
			if(examesDetalhesVO.getPertenceSumario().equals(DominioSumarioExame.H.getDescricao())) {
				listaHematologia.add(examesDetalhesVO);
			}
			
		}
		
		if(listaBioquimica.size() > 0) {
			examesPacienteBioquimica.setTipoExame(DominioSumarioExame.B.getDescricao());
			examesPacienteBioquimica.setListPacienteExamesDetalhesVO(listaBioquimica);
			listExamesPacienteExamesVO.add(examesPacienteBioquimica);
		}
		if(listaGasometria.size() > 0) {
			examesPacienteGasometria.setTipoExame(DominioSumarioExame.E.getDescricao());
			examesPacienteGasometria.setListPacienteExamesDetalhesVO(listaGasometria);
			listExamesPacienteExamesVO.add(examesPacienteGasometria);
		}
		if(listaEQU.size() > 0) {
			examesPacienteEQU.setTipoExame(DominioSumarioExame.G.getDescricao());
			examesPacienteEQU.setListPacienteExamesDetalhesVO(listaEQU);
			listExamesPacienteExamesVO.add(examesPacienteEQU);
		}
		if(listaHematologia.size() > 0) {
			examesPacienteHematologia.setTipoExame(DominioSumarioExame.H.getDescricao());
			examesPacienteHematologia.setListPacienteExamesDetalhesVO(listaHematologia);
			listExamesPacienteExamesVO.add(examesPacienteHematologia);
		}
		
		return listExamesPacienteExamesVO;
	}
	
}