package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.exames.business.EmitirRelatorioSumarioExamesAltaRN.VarMampAltaSumExames;
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
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamPcSumExameMasc;
import br.gov.mec.aghu.model.MamPcSumExameTab;
import br.gov.mec.aghu.model.MamPcSumLegenda;
import br.gov.mec.aghu.model.MamPcSumMascCampoEdit;
import br.gov.mec.aghu.model.MamPcSumMascLinha;
import br.gov.mec.aghu.model.MamPcSumObservacao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * Utilizado estoria #28379 - POL - Gerar Dados para Relatório Sumario Exames Alta
 * 
 * @author Eduardo Giovany Schweigert
 *
 */
@Stateless
public class EmitirRelatorioSumarioExamesAltaON extends BaseBusiness {


@Inject
private AelMaterialAnaliseDAO aelMaterialAnaliseDAO;

@Inject
private AelExamesDAO aelExamesDAO;


@EJB
private EmitirRelatorioSumarioExamesAltaRN emitirRelatorioSumarioExamesAltaRN;

private static final Log LOG = LogFactory.getLog(EmitirRelatorioSumarioExamesAltaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPacienteFacade pacienteFacade;

@EJB
private IAghuFacade aghuFacade;
	
	private static final long serialVersionUID = 970631511259439158L;

	public RelatorioExamesPacienteVO montarRelatorio(Integer asuApaAtdSeq, Integer asuApaSeq, Short apeSeqp) throws BaseException {

		final RelatorioExamesPacienteVO vo = new RelatorioExamesPacienteVO();

		final VarMampAltaSumExames var = getEmitirRelatorioSumarioExamesAltaRN().geraInformacoesSumarioAlta(asuApaAtdSeq, asuApaSeq, apeSeqp);
		
		if(var.listaMamPcSumExameTab.isEmpty()){
			return null;
		}

		vo.setProntuario(var.mamPcPaciente.getPaciente().getProntuario().toString());

		executarQSueQSue2(vo, var, var.mamPcPaciente.getPaciente().getProntuario(), var.mamPcPaciente.getPaciente().getCodigo());
		executarQLegenda(vo, var);
		executarQObservacao(vo, var);
		executarQSem(vo, var);
		executarQMascLinha(vo, var);
		executarQMascCampoEdit(vo, var);
		
		List<RelatorioExamesPacienteExamesVO> listExamesPacienteExamesVO = vo.getExames();
		for(RelatorioExamesPacienteExamesVO i: listExamesPacienteExamesVO){
			i.setRodape(refazerRodape(i.getTipoExame()));
		}
			
		for(MamPcSumObservacao mamPcSumObservacao : var.listaMamPcSumObservacao) {
			
			//percorrendo lista QSUE2
			for(int i = 0; i < listExamesPacienteExamesVO.size(); i++) {
				
				if(listExamesPacienteExamesVO.get(i).getListExamesPacientesListaObservacaoVO() == null){
					listExamesPacienteExamesVO.get(i).setListExamesPacientesListaObservacaoVO(new ArrayList<RelatorioExamesPacientesListaObservacaoVO>());
				}

				String dataEventoAux = "";
				for(int j = 0; j < listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().size();j++){
					final RelatorioExamesPacienteExamesDetalhesVO relatorioExamesPacienteExamesDetalhesVO = listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j);
					
					if(relatorioExamesPacienteExamesDetalhesVO != null) {
						if( CoreUtil.igual(toStringSeNaoNull(DateUtil.obterDataFormatada(mamPcSumObservacao.getDthrEvento(), "dd/MM/yy")+" "+DateUtil.obterDataFormatada(mamPcSumObservacao.getDthrEvento(), "HH:mm")),
										   listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j).getDataEvento()) &&
														
						   CoreUtil.igual(toStringSeNaoNull(mamPcSumObservacao.getPertenceSumario()), 
								          toStringSeNaoNull(relatorioExamesPacienteExamesDetalhesVO.getRodape())) &&
						   					
						   CoreUtil.igual(toStringSeNaoNull(mamPcSumObservacao.getProntuario()),
								   		  toStringSeNaoNull(relatorioExamesPacienteExamesDetalhesVO.getProntuarioObs())) &&
								   			
						   CoreUtil.igual(toStringSeNaoNull(mamPcSumObservacao.getRecemNascido()),
								          toStringSeNaoNull(relatorioExamesPacienteExamesDetalhesVO.getRecemNascidoObs()))){

							if(mamPcSumObservacao.getCodigoMensagem() != null ){
								String dataEvento = relatorioExamesPacienteExamesDetalhesVO.getDataEvento();
								StringBuffer buffer = new StringBuffer(dataEvento);
								buffer.append(" (").append(mamPcSumObservacao.getCodigoMensagem()).append(')');					
								
								if(!dataEventoAux.equals(dataEvento)){
									StringBuffer buffer2 = new StringBuffer();
									buffer2.append('(').append(mamPcSumObservacao.getCodigoMensagem()).append(')');
									RelatorioExamesPacientesListaObservacaoVO voObservacao = new RelatorioExamesPacientesListaObservacaoVO();
									voObservacao.setCodigoObservacao(buffer2.toString());
									voObservacao.setDescricaoObservacao(mamPcSumObservacao.getDescricao());
									listExamesPacienteExamesVO.get(i).getListExamesPacientesListaObservacaoVO().add(voObservacao);	
								}
								dataEventoAux = dataEvento;
								relatorioExamesPacienteExamesDetalhesVO.setDataEvento(buffer.toString());
							}
						}
					}
				}
			}
		}
		
		//List<AelPolSumLegenda> listaAelPolSumLegenda = (List<AelPolSumLegenda>)parametros.get("listaAelPolSumLegenda");
		configurarIdentificacao(var.mamPcPaciente.getPaciente().getProntuario(), asuApaAtdSeq, vo);
		
		// ##### parametros para o relatorio #####
		//p_data: '01'||'mmyyyy'	
		Date data = definirDataParametro();
		if(data != null) {
			data = null;
		}
		
		return vo;
	}
	
	private void executarQSueQSue2(final RelatorioExamesPacienteVO vo, final VarMampAltaSumExames var,
									 final Integer prontuario, final Integer pacCodigo) {
									 
		List<AipPacientes> listaAipPacientesConsulta = getPacienteFacade().executarCursorPac(pacCodigo);
		List<RelatorioExamesPacienteExamesDetalhesVO> listPacienteExamesDetalhes = new ArrayList<RelatorioExamesPacienteExamesDetalhesVO>();
		String tipoExame = "";
		
		if(listaAipPacientesConsulta.size() > 0) {
			
			//verifica se listaMamPcSumExameTab tem registro, caso não tenha, não tem join. :(
			if(!var.listaMamPcSumExameTab.isEmpty()) {
				//pac.codigo = sue.pac_codigo
				for(AipPacientes aipPacientes : listaAipPacientesConsulta) {
					if(aipPacientes.getCodigo().equals(pacCodigo) ) {
						for(MamPcSumExameTab mamPcSumExameTab : var.listaMamPcSumExameTab) {
							vo.setProntuario(toStringSeNaoNull(mamPcSumExameTab.getProntuario())); //prontuario
							vo.setNome(formataNomeQSUE(mamPcSumExameTab, aipPacientes)); //nome
							vo.setRecemNascido(mamPcSumExameTab.getRecemNascido());//recem nascido
							vo.setLtoLtoId(decode(mamPcSumExameTab.getLtoLtoId(), "XXXXX")); //ltoltoid
							vo.setDthrFim(decode(mamPcSumExameTab.getDthrFim(),DateUtil.obterData(1900, 1, 1, 00, 00)).toString()); //dhtrfim
							
							tipoExame = decodeSumarioExame(mamPcSumExameTab.getPertenceSumario(), mamPcSumExameTab.getIndImprime());
							
							//MONTAR OBJETO INTERNO
							RelatorioExamesPacienteExamesDetalhesVO examesPacienteExamesVO = new RelatorioExamesPacienteExamesDetalhesVO();
							
							//pertence-sumario
							examesPacienteExamesVO.setPertenceSumario(tipoExame);
							
							examesPacienteExamesVO.setRodape(toStringSeNaoNull(mamPcSumExameTab.getPertenceSumario()));//pertence_sumario_rodape
							examesPacienteExamesVO.setNome(toStringSeNaoNull(mamPcSumExameTab.getCalNomeSumario()));//cal_nome
							examesPacienteExamesVO.setValor(formataReeValor(mamPcSumExameTab.getReeValor())); //ree_valor
							examesPacienteExamesVO.setProntuarioObs(toStringSeNaoNull(mamPcSumExameTab.getProntuario())); //prontuario_obs
							examesPacienteExamesVO.setRecemNascidoObs(mamPcSumExameTab.getRecemNascido()); //recem_nascido_obs
							examesPacienteExamesVO.setDataEvento(DateUtil.obterDataFormatada(mamPcSumExameTab.getDthrEventoAreaExec(), "dd/MM/yy")+" "+DateUtil.obterDataFormatada(mamPcSumExameTab.getDthrEventoAreaExec(), "HH:mm")); // data_evento
							examesPacienteExamesVO.setDataHora(mamPcSumExameTab.getDthrEventoAreaExec());
							examesPacienteExamesVO.setHoraEvento(DateUtil.obterDataFormatada(mamPcSumExameTab.getDthrEventoAreaExec(), "HH:mm")); // hora_evento
							examesPacienteExamesVO.setLtoLtoId2(decode(mamPcSumExameTab.getLtoLtoId(), "XXXXX")); // Lto_Lto_Id2
							examesPacienteExamesVO.setDthrFim2(decode(mamPcSumExameTab.getDthrFim(),DateUtil.obterData(1900, 1, 1, 00, 00)).toString()); // dthr_fim2
							examesPacienteExamesVO.setOrdem(mamPcSumExameTab.getOrdem()); // CAL_ORDEM
							examesPacienteExamesVO.setPpePleSeq(mamPcSumExameTab.getPpePleSeq());
							examesPacienteExamesVO.setPpeSeqp(mamPcSumExameTab.getPpeSeqp());
												
							//ADICIONAR NA LISTA DE OBJETO
							listPacienteExamesDetalhes.add(examesPacienteExamesVO);
						}
					}
				}
			}
		}
		
		vo.setExames(preencherListasSumarioPertence(listPacienteExamesDetalhes));		
	}
	
	/**
	 * Q_LEGENDA
	 * @param vo
	 */
	private void executarQLegenda(final RelatorioExamesPacienteVO vo, final VarMampAltaSumExames var) {
		List<RelatorioExamesPacienteExamesVO> listExamesPacienteExamesVO = vo.getExames();
	
		for(MamPcSumLegenda mamPcSumLegenda : var.listaMamPcSumLegenda) {
		
			//percorrendo lista QSUE2
			if(listExamesPacienteExamesVO.size() > 0) {
				for(int i = 0; i < listExamesPacienteExamesVO.size(); i++) {
					
					if(listExamesPacienteExamesVO.get(i) != null) {
						
						if (listExamesPacienteExamesVO.get(i).getListExamesPacientesListaLegendaVO() == null) {
							listExamesPacienteExamesVO.get(i).setListExamesPacientesListaLegendaVO(new ArrayList<RelatorioExamesPacientesListaLegendaVO>());
						}
					
						for(int j = 0; j < listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().size();j++){

							
							if(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j) != null) {
							
								final RelatorioExamesPacienteExamesDetalhesVO relatorioExamesPacienteExamesDetalhesVO = listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j);
								
								final String dtTemp = String.valueOf(CoreUtil.nvl(mamPcSumLegenda.getId().getDthrFim(), DateUtil.obterData(1900, 1, 1, 00, 00)));
								if(  CoreUtil.igual(dtTemp,(relatorioExamesPacienteExamesDetalhesVO.getDthrFim2())) &&
										
								     CoreUtil.igual(toStringSeNaoNull(mamPcSumLegenda.getId().getLtoLtoId()),
								    		        toStringSeNaoNull(relatorioExamesPacienteExamesDetalhesVO.getLtoLtoId2())) &&
												    
								     CoreUtil.igual(toStringSeNaoNull(mamPcSumLegenda.getId().getPertenceSumario()),
								    		 		toStringSeNaoNull(relatorioExamesPacienteExamesDetalhesVO.getRodape())) &&
											
									 CoreUtil.igual(toStringSeNaoNull(mamPcSumLegenda.getId().getProntuario()),
											 	    relatorioExamesPacienteExamesDetalhesVO.getProntuarioObs()) &&
											
									 CoreUtil.igual(toStringSeNaoNull(mamPcSumLegenda.getId().getRecemNascido()),
											 	    toStringSeNaoNull(relatorioExamesPacienteExamesDetalhesVO.getRecemNascidoObs()))
								) {
									RelatorioExamesPacientesListaLegendaVO voLegenda = new RelatorioExamesPacientesListaLegendaVO();
									
									voLegenda.setNumeroLegenda(toStringSeNaoNull(mamPcSumLegenda.getId().getNumeroLegenda()));
									voLegenda.setDescricaoLegenda(mamPcSumLegenda.getId().getDescricao());
									voLegenda.setGrupoLegenda(toStringSeNaoNull(mamPcSumLegenda.getId().getGrupoLegenda()));
									listExamesPacienteExamesVO.get(i).getListExamesPacientesListaLegendaVO().add(voLegenda);
									break;
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
	private void executarQObservacao(final RelatorioExamesPacienteVO vo, final VarMampAltaSumExames var) {
		
		List<RelatorioExamesPacienteExamesVO> listExamesPacienteExamesVO = vo.getExames();
		
		for(MamPcSumObservacao mamPcSumObservacao : var.listaMamPcSumObservacao) {
			
			//percorrendo lista QSUE2
			for(int i = 0; i < listExamesPacienteExamesVO.size(); i++) {

				for(int j = 0; j < listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().size();j++){
					
					if(listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j) != null) {
						final RelatorioExamesPacienteExamesDetalhesVO relatorioExamesPacienteExamesDetalhesVO = listExamesPacienteExamesVO.get(i).getListPacienteExamesDetalhesVO().get(j);

						if(  CoreUtil.igual(toStringSeNaoNull(mamPcSumObservacao.getDthrFim()),
											(relatorioExamesPacienteExamesDetalhesVO.getDthrFim2())) &&
								
						     CoreUtil.igual(toStringSeNaoNull(mamPcSumObservacao.getLtoLtoId()),
						    		        toStringSeNaoNull(relatorioExamesPacienteExamesDetalhesVO.getLtoLtoId2())) &&
										    
						     CoreUtil.igual(toStringSeNaoNull(mamPcSumObservacao.getPertenceSumario()),
						    		 		toStringSeNaoNull(relatorioExamesPacienteExamesDetalhesVO.getRodape())) &&
									
							 CoreUtil.igual(toStringSeNaoNull(mamPcSumObservacao.getProntuario()),
									 	    relatorioExamesPacienteExamesDetalhesVO.getProntuarioObs()) &&
									
							 CoreUtil.igual(toStringSeNaoNull(mamPcSumObservacao.getRecemNascido()),
									 	    toStringSeNaoNull(relatorioExamesPacienteExamesDetalhesVO.getRecemNascidoObs()))
						) {						
							relatorioExamesPacienteExamesDetalhesVO.setCodigo(toStringSeNaoNull(mamPcSumObservacao.getCodigoMensagem()));
							relatorioExamesPacienteExamesDetalhesVO.setDescricaoObservacao(mamPcSumObservacao.getDescricao());
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
	private void executarQSem(final RelatorioExamesPacienteVO vo, final VarMampAltaSumExames var) {
		
		List<RelatorioExamesPacienteDetalhesVO> listExamesPacienteDetalhesVO = vo.getExamesDetalhes();
		if(listExamesPacienteDetalhesVO == null){
			listExamesPacienteDetalhesVO = new ArrayList<RelatorioExamesPacienteDetalhesVO>();
		}
		
		final AelExamesDAO aelExamesDAO = getAelExamesDAO();
		final AelMaterialAnaliseDAO aelMaterialAnaliseDAO = getAelMaterialAnaliseDAO();
		final IAghuFacade aghuFacade = getAghuFacade();
		
		for(MamPcSumExameMasc mamPcSumExameMasc : var.listaMamPcSumExameMasc) {
			AelExames aelExames = null;
			AelMateriaisAnalises aelMateriaisAnalises = null;
			AghUnidadesFuncionais aghUnidadesFuncionais = null;
			
			//executa ael_exames
			if(mamPcSumExameMasc.getUfeEmaExaSigla() != null) {
				aelExames = aelExamesDAO.obterPeloId(mamPcSumExameMasc.getUfeEmaExaSigla());
			}
			
			if(mamPcSumExameMasc.getUfeEmaManSeq() != null) {
				aelMateriaisAnalises = aelMaterialAnaliseDAO.obterOriginal(mamPcSumExameMasc.getUfeEmaManSeq());
			}
			
			if(mamPcSumExameMasc.getUfeUnfSeq() != null) {
				aghUnidadesFuncionais = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(mamPcSumExameMasc.getUfeUnfSeq());
			}
				
			if( aelExames != null && 
					aelMateriaisAnalises != null &&
						aghUnidadesFuncionais != null ) {
				
				//sempre será apenas um registro
				RelatorioExamesPacienteDetalhesVO examesPacienteDetalhesVO = new RelatorioExamesPacienteDetalhesVO();
				examesPacienteDetalhesVO.setOrdemRelatorio(mamPcSumExameMasc.getOrdemRelatorio());
				examesPacienteDetalhesVO.setOrdemAgrupamento(mamPcSumExameMasc.getOrdemAgrupamento());
				examesPacienteDetalhesVO.setExaDescricao(aelExames.getDescricao());
				examesPacienteDetalhesVO.setManDescricao(aelMateriaisAnalises.getDescricao());
				examesPacienteDetalhesVO.setUnfDescricao(aghUnidadesFuncionais.getDescricao());
				examesPacienteDetalhesVO.setDataHoraEvento(DateUtil.obterDataFormatada(mamPcSumExameMasc.getDthrEventoLib(), "dd/MM/yyyy"));
				
				listExamesPacienteDetalhesVO.add(examesPacienteDetalhesVO);
			}
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
	private void executarQMascLinha(final RelatorioExamesPacienteVO vo, final VarMampAltaSumExames var) {
		
		List<RelatorioExamesPacienteDetalhesVO> listExamesPacienteDetalhesVO = vo.getExamesDetalhes();

		if(listExamesPacienteDetalhesVO != null) {
			//percorrendo lista QSEM
			for(int i = 0; i < listExamesPacienteDetalhesVO.size(); i++) {
				listExamesPacienteDetalhesVO.get(i).setListLinhas(new ArrayList<RelatorioExamesPacienteDetalhesLinhaVO>());
				for(MamPcSumMascLinha mamPcSumMascLinha : var.listaMamPcSumMascLinha) {			
					
					if(CoreUtil.igual(mamPcSumMascLinha.getOrdemRelatorio(),(listExamesPacienteDetalhesVO.get(i).getOrdemRelatorio()))){
						RelatorioExamesPacienteDetalhesLinhaVO voLinha = new RelatorioExamesPacienteDetalhesLinhaVO();
						voLinha.setDescricaoMascLinha(mamPcSumMascLinha.getDescricaoClob());
						voLinha.setNroLinha(mamPcSumMascLinha.getNroLinha());
						voLinha.setOrdemRelatorio(mamPcSumMascLinha.getOrdemRelatorio());
						listExamesPacienteDetalhesVO.get(i).getListLinhas().add(voLinha);
					}
				}
				
			}
		}
	}
	
	private void executarQMascCampoEdit(final RelatorioExamesPacienteVO vo, final VarMampAltaSumExames var) {
		
		List<RelatorioExamesPacienteDetalhesVO> listExamesPacienteDetalhesVO = vo.getExamesDetalhes();

		//percorrendo lista QMASCLINHA
		if(listExamesPacienteDetalhesVO != null) {
			for(int i = 0; i < listExamesPacienteDetalhesVO.size(); i++) {
//				for(RelatorioExamesPacienteDetalhesLinhaVO linha : listExamesPacienteDetalhesVO.get(i).getListLinhas()){
				
				listExamesPacienteDetalhesVO.get(i).setListCamposEdit(new ArrayList<RelatorioExamesPacienteDetalhesCampoEditVO>());
				for(MamPcSumMascCampoEdit campoEdit : var.listaMamPcSumMascCampoEdit) {			
					
					if(CoreUtil.igual(toStringSeNaoNull(campoEdit.getNroLinha()),(listExamesPacienteDetalhesVO.get(i).getNroLinha())) &&
						 CoreUtil.igual(toStringSeNaoNull(campoEdit.getOrdemRelatorio()),(listExamesPacienteDetalhesVO.get(i).getOrdemRelatorio()))) {
						
						RelatorioExamesPacienteDetalhesCampoEditVO voLinha = new RelatorioExamesPacienteDetalhesCampoEditVO();
						voLinha.setDescricaoMascCampoEdit(campoEdit.getDescricao());
						listExamesPacienteDetalhesVO.get(i).getListCamposEdit().add(voLinha);
					}
				}
			}
		}
	}
	
	private String formataNomeQSUE(MamPcSumExameTab mamPcSumExameTab, AipPacientes aipPacientes) {
		
		String nome = "";
		if(mamPcSumExameTab.getProntuario() != null && mamPcSumExameTab.getProntuario() > 90000000) {
			nome = aipPacientes.getNome();
		}else {
			if(Boolean.TRUE.equals(mamPcSumExameTab.getRecemNascido())){
				nome = "RN de " + aipPacientes.getNome();
			}else {
				nome = aipPacientes.getNome();
			}
		}
		
		return nome;
	}
	

	private String decodeSumarioExame(DominioSumarioExame sumario, Boolean indImprime) {
		String retorno = null;
		if(sumario != null) {
			switch (sumario) {
				case B: case E:				
				case G: case H:
					retorno = sumario.getDescricao();	
				break;			

			default:
				if(Boolean.FALSE.equals(indImprime)) {
					retorno = "Nenhum exame de tabela realizado.";
				}
				break;
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
	
	public String formataReeValor(Double reeValor) {
		String retorno = String.valueOf(reeValor);  
		retorno = retorno.replace(".", ","); 
		if( (retorno.contains(",0") && retorno.indexOf(",0") != (retorno.length()-3)) || retorno.contains(",00")){
			return retorno.substring(0, retorno.indexOf(','));
		}else{
			return retorno;
		}
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
		
		
		for (RelatorioExamesPacienteExamesDetalhesVO examesDetalhesVO : listDetalhesVO) {
			
			if (DominioSumarioExame.B.getDescricao().equals(examesDetalhesVO.getPertenceSumario())) {
				listaBioquimica.add(examesDetalhesVO);
				
			} else if (DominioSumarioExame.E.getDescricao().equals(examesDetalhesVO.getPertenceSumario())) {
				listaGasometria.add(examesDetalhesVO);
				
			} else  if(DominioSumarioExame.G.getDescricao().equals(examesDetalhesVO.getPertenceSumario())) {
				listaEQU.add(examesDetalhesVO);
				
			} else  if(DominioSumarioExame.H.getDescricao().equals(examesDetalhesVO.getPertenceSumario())) {
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
	
                                              
	private String refazerRodape(String pertenceSumario) {
		if(pertenceSumario.equals("Bioquímica")){
			return "Unidades: Enzimas = U/L   ** Na,K,Cl,CO2 = mEq/L   ** Bioquímica = mg/dL";
		}else if(pertenceSumario.equals("Hematologia")){
			return "* Os resultados do leucograma diferencial são dados em %";
		}else{
			return "";
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
	/** verifica se atributo não é null e retorna o valor toString()	 */
	private String toStringSeNaoNull(Object obj) {
		if(obj != null) {
			return obj.toString().toUpperCase();
		} else {
			return null;
		}
	}

	private Date definirDataParametro() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected EmitirRelatorioSumarioExamesAltaRN getEmitirRelatorioSumarioExamesAltaRN() {
		return emitirRelatorioSumarioExamesAltaRN;
	}
	
	protected AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}
	
	protected AelMaterialAnaliseDAO getAelMaterialAnaliseDAO() {
		return aelMaterialAnaliseDAO;
	}	
}