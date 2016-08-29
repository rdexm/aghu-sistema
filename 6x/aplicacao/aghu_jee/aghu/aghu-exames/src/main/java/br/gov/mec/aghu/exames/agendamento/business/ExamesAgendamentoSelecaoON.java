package br.gov.mec.aghu.exames.agendamento.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.VAelGradeExameDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.exames.solicitacao.business.AgendamentoExameRN;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * 
 * @author gzapalaglio
 *
 */
@Stateless
public class ExamesAgendamentoSelecaoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ExamesAgendamentoSelecaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@EJB
private IInternacaoFacade internacaoFacade;

@Inject
private VAelGradeExameDAO vAelGradeExameDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;

@Inject
private VAelSolicAtendsDAO vAelSolicAtendsDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IExamesFacade examesFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AelMatrizSituacaoDAO aelMatrizSituacaoDAO;

@Inject
private AelAmostrasDAO aelAmostrasDAO;

@EJB
private AgendamentoExameRN agendamentoExameRN;

@Inject
private AghAtendimentoDAO aghAtendimentoDAO;

@Inject 
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;




	/**
	 * 
	 */
	private static final long serialVersionUID = 1831953780160018239L;

	public enum ExamesAgendamentoSelecaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_INFORMAR_PELO_MENOS_UM_CAMPO, MENSAGEM_ERRO_SOLICITACAO_NAO_ENCONTRADA, AEL_00760, AEL_00739, 
		AEL_00740, AEL_00741, MENSAGEM_ERRO_NENHUM_EXAME_A_AGENDAR, MENSAGEM_DADOS_SOLICITACAO_INVALIDOS, 
		MENSAGEM_ERRO_SELECIONAR_EXAME_IMPRIMIR, MENSAGEM_ERRO_SELECIONAR_EXAME_AGENDAR_OU_CANCELAR_AGENDA;
	}	
	
	/**
	 * Valida se pelo um campo foi preenchido para realizar a pesquisa
	 * caso não retorna uma exceção de negócio.
	 * 
	 * @param exameVO, paciente
	 * @throws ApplicationBusinessException
	 */
	public void validarExamesAgendamentoSelecao(VAelSolicAtendsVO exameVO, AipPacientes paciente) throws ApplicationBusinessException{
		if(exameVO==null && paciente==null) {
			throw new ApplicationBusinessException(ExamesAgendamentoSelecaoONExceptionCode.MENSAGEM_DADOS_SOLICITACAO_INVALIDOS);
		}
		if(exameVO.getNumero()==null && exameVO.getNumConsulta()==null && paciente==null){
			throw new ApplicationBusinessException(ExamesAgendamentoSelecaoONExceptionCode.MENSAGEM_ERRO_INFORMAR_PELO_MENOS_UM_CAMPO);
		}
	}
	
	/**
	 * Retorna os dados da solicitação de exames pelo código da solicitação.
	 * 
	 * @param soeSeq
	 * @throws ApplicationBusinessException
	 */
	public VAelSolicAtendsVO obterVAelSolicAtendsPorSoeSeq(Integer soeSeq) throws ApplicationBusinessException {
		Object[] resultadoPesquisa = this.getVAelSolicAtendsDAO().obterVAelSolicAtendsPorSoeSeq(soeSeq);
		VAelSolicAtendsVO exameVO = new VAelSolicAtendsVO();
		
		if(resultadoPesquisa!=null) {
			exameVO.setNumero((Integer)resultadoPesquisa[0]); //soeSeq
			exameVO.setDataSolicitacao((Date)resultadoPesquisa[1]); 
			exameVO.setUnfDescricao((String)resultadoPesquisa[2]); 
			exameVO.setNumConsulta((Integer)resultadoPesquisa[3]);
			exameVO.setAtdSeq((Integer)resultadoPesquisa[4]);
			exameVO.setUnfSeq((Short)resultadoPesquisa[5]);	
			exameVO.setCodPaciente((Integer)resultadoPesquisa[6]);	
			exameVO.setOrigem(DominioOrigemAtendimento.getInstance((String)resultadoPesquisa[7]));
		}
		else {
			throw new ApplicationBusinessException(ExamesAgendamentoSelecaoONExceptionCode.MENSAGEM_ERRO_SOLICITACAO_NAO_ENCONTRADA);
		}
		
		return exameVO;
	}
	
	public List<AgendamentoExameVO> permiteAgendarExames(List<AgendamentoExameVO> exames, String login, String operacao) throws ApplicationBusinessException {
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO);
		List<AelMatrizSituacao> matrizSituacao;
		List<String> codigosOrigemMatriz;
		if (parametro == null) {
			throw new ApplicationBusinessException(ExamesAgendamentoSelecaoONExceptionCode.AEL_00739);
		} else {
			String situacaoPara = parametro.getVlrTexto();
			/*
			 * Verifica se é possível a situação do item exame atual para a
			 * situação AGENDADO.
			 */
			matrizSituacao = this.getAelMatrizSituacaoDAO().listarPorSituacaoItemSolicitacaoParaCodigo(situacaoPara);
			if (matrizSituacao == null || matrizSituacao.size() == 0) {
				throw new ApplicationBusinessException(ExamesAgendamentoSelecaoONExceptionCode.AEL_00740);
			} else {
				codigosOrigemMatriz = new ArrayList<String>();
				for (AelMatrizSituacao matriz : matrizSituacao) {
					codigosOrigemMatriz.add(matriz.getSituacaoItemSolicitacao().getCodigo());
				}
			}
		}
		int examesSelecionados = 0;
		List<AgendamentoExameVO> examesDevemSerAgendados = new ArrayList<AgendamentoExameVO>();

		for (AgendamentoExameVO exame : exames) {
			if (exame.getSelecionado()) {
				examesSelecionados++;
				Boolean existeGradeExame = this.getVAelGradeExameDAO().existeGradeExamePorSiglaMatUnf(
						exame.getItemExame().getExame().getSigla(), exame.getItemExame().getMaterialAnalise().getSeq(),
						exame.getItemExame().getUnidadeFuncional().getSeq());

				/* Verifica a existência de uma grade para o exame */
				if (existeGradeExame == null) {
					throw new ApplicationBusinessException(ExamesAgendamentoSelecaoONExceptionCode.AEL_00760, exame
							.getItemExame().getExame().getDescricaoUsual(), exame.getItemExame().getId().getSeqp());
				}

				String situacaoDe = exame.getItemExame().getSituacaoItemSolicitacao().getCodigo();
				if (!codigosOrigemMatriz.contains(situacaoDe)) {
					throw new ApplicationBusinessException(ExamesAgendamentoSelecaoONExceptionCode.AEL_00740, exame
							.getItemExame().getExame().getDescricaoUsual(), exame.getItemExame().getId().getSeqp());
				}


				if (exame.getListaAmostras() != null && !exame.getListaAmostras().isEmpty()) {
					examesDevemSerAgendados.add(exame);

					for (AgendamentoExameVO itemLista : exames) {
						// Caso o item a ser testado não estiver agendado e não
						// estiver selecionado para agendamento
						// deve-se verificar se este possui itens de amostra em
						// comum com algum exame selecionado
						if (!itemLista.getItemExame().getSituacaoItemSolicitacao().getCodigo().equals(parametro.getVlrTexto())	&& !itemLista.getSelecionado()) {
							if (verificarAmostrasComumEmListasAmostras(exame.getListaAmostras(), itemLista.getListaAmostras())) {
								examesDevemSerAgendados.add(itemLista);
							}
						}
					}
				}
			}
		}
		if (examesSelecionados == 0) {
			throw new ApplicationBusinessException(
					ExamesAgendamentoSelecaoONExceptionCode.MENSAGEM_ERRO_SELECIONAR_EXAME_AGENDAR_OU_CANCELAR_AGENDA,
					operacao);
		}
		
		if (examesDevemSerAgendados != null
				&& examesDevemSerAgendados.size() > examesSelecionados) {
			return examesDevemSerAgendados;
		} else {
			return null;
		}

	}
	
	/**
	 * Pesquisa as solicitações de exame pelos filtros da pesquisa
	 * 
	 * @param filtro
	 * @throws ApplicationBusinessException
	 */
	public List<VAelSolicAtendsVO> obterSolicitacoesExame(VAelSolicAtendsVO filtro) throws ApplicationBusinessException {
		List<Object[]> resultadoPesquisa = this.getVAelSolicAtendsDAO().obterSolicitacoesExame(filtro);
		List<VAelSolicAtendsVO> exames =  new ArrayList<VAelSolicAtendsVO>();
		
		if(resultadoPesquisa !=null && !resultadoPesquisa.isEmpty()){
			for (Object[] record : resultadoPesquisa) {				
				VAelSolicAtendsVO exame = new VAelSolicAtendsVO();
				exame.setNumero((Integer)record[0]); //soeSeq
				exame.setDataSolicitacao((Date)record[1]);
				exame.setProntuario((Integer)record[2]);
				exame.setCodPaciente((Integer)record[3]);
				exame.setOrigem(DominioOrigemAtendimento.getInstance((String)record[4]));
				exame.setNumConsulta((Integer)record[5]);
				exames.add(exame);
			}
		}
		else {
			throw new ApplicationBusinessException(ExamesAgendamentoSelecaoONExceptionCode.MENSAGEM_ERRO_SOLICITACAO_NAO_ENCONTRADA);
		}
		
		Collections.sort(exames, new VAelSolicAtendsVOComparator());
		Collections.reverse(exames); 
		
		return exames;
	}
	
	
	public List<AgendamentoExameVO> obterExamesParaAgendamento(VAelSolicAtendsVO filtro, Short unidadeExecutoraSeq) throws ApplicationBusinessException {
		DominioSimNao unidColetaUnf = DominioSimNao.N;
		
		List<AgendamentoExameVO> examesParaAgendamento;
		
		List<Short> unfHierarquico = this.getAghuFacade().obterUnidadesFuncionaisHierarquicasPorCaract(unidadeExecutoraSeq);
		
		Boolean unidColeta = getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(unidadeExecutoraSeq,
				ConstanteAghCaractUnidFuncionais.UNID_COLETA);
		
		if(unidColeta) {
			unidColetaUnf = DominioSimNao.S;
		}

		List<Object[]> resultadoPesquisa = this.getAelItemSolicitacaoExameDAO().
			listarExamesAgendamentoSelecao(filtro.getNumero(), unidadeExecutoraSeq, filtro.getOrigem(), unfHierarquico, unidColetaUnf.toString());
		
		if(resultadoPesquisa !=null && !resultadoPesquisa.isEmpty()){
			examesParaAgendamento = new ArrayList<AgendamentoExameVO>();
			for (Object[] record : resultadoPesquisa) {
				Integer soeSeq = null;
				Short seqp = null;
				if (getAelItemSolicitacaoExameDAO().isOracle()){ //Como a query é nativa, retorna tipos diferentes de acordo com a base.
					BigDecimal bdSoeSeq = (BigDecimal) record[0];
					BigDecimal bdSeqp  = (BigDecimal) record[1];
					soeSeq = bdSoeSeq.intValue();
					seqp = bdSeqp.shortValue();
				}else {
					soeSeq = (Integer)record[0];
					seqp = (Short)record[1];
				}
				
				AelItemSolicitacaoExames itemExame = this.getAelItemSolicitacaoExameDAO().obterPorId(soeSeq, seqp);

				AelUnfExecutaExames unfExecutoraItemExame = itemExame.getAelUnfExecutaExames();
				AghAtendimentos atendimento = aghAtendimentoDAO.obterAghAtendimentoPorSeq(filtro.getAtdSeq());
				AelSolicitacaoExames solicitacaoExame = aelSolicitacaoExameDAO.obterPorChavePrimaria(filtro.getNumero());
				Short unidadeFuncionalSolicitanteSeq = solicitacaoExame.getUnidadeFuncional().getSeq();				
				Short unidadeFuncionalUsuarioSeq = null;	
				if (filtro.isOrigemSolicitacaoExames()){
					if (solicitacaoExame.getUnidadeFuncionalAreaExecutora() != null){
						unidadeFuncionalUsuarioSeq = solicitacaoExame.getUnidadeFuncionalAreaExecutora().getSeq();
					}
				} else {
					unidadeFuncionalUsuarioSeq = unidadeExecutoraSeq;
				}
				
				List<AelAmostras> listaAmostras = getAelAmostrasDAO().buscaListaAmostrasPorItemExame(itemExame);
						
				if (!filtro.isOrigemSolicitacaoExames() || this.agendamentoExameRN.validaExameAgendaInternacaoOuNaoInternacao(unfExecutoraItemExame, unidadeFuncionalSolicitanteSeq, unidadeFuncionalUsuarioSeq, atendimento)){
					List<Date> dthrAgenda = this.retornaDataAgendadaItemSolicitacao(itemExame);

					AgendamentoExameVO exameVO = new AgendamentoExameVO();
					if(dthrAgenda!=null && !dthrAgenda.isEmpty()) {
						String dthrAgendaString = "";
						String diaSemana = "";
						SimpleDateFormat dataFormatada1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						for(Date dthr : dthrAgenda){
							exameVO.setDthrAgenda(dthrAgenda.get(0));
							if(dthr != null){
								if("".equals(dthrAgendaString)){
									dthrAgendaString = dataFormatada1.format(dthr);
								}else{
									dthrAgendaString = dthrAgendaString.concat(" - ").concat(dataFormatada1.format(dthr));
								}
								if("".equals(diaSemana)){
									diaSemana = CoreUtil.retornaDiaSemana(dthr).getDescricao();
								}else{
									diaSemana = diaSemana.concat(" - ").concat(CoreUtil.retornaDiaSemana(dthr).getDescricao());
								}
							}
						}					
						exameVO.setItemExame(itemExame);
						exameVO.setDthrAgendaString(dthrAgendaString);
						exameVO.setDiaSemanaData(diaSemana);
						exameVO.setSelecionado(false);
					}
					else {
						exameVO.setItemExame(itemExame);
						exameVO.setSelecionado(true);
					}
					if(itemExame.getAelUnfExecutaExames().getDthrReativaTemp()!=null) {
						exameVO.setDthrReativacao(itemExame.getAelUnfExecutaExames().getDthrReativaTemp());
					}
					exameVO.setListaAmostras(listaAmostras);
					
					examesParaAgendamento.add(exameVO);
				} 
			}
		}
		else {
			throw new ApplicationBusinessException(ExamesAgendamentoSelecaoONExceptionCode.MENSAGEM_ERRO_NENHUM_EXAME_A_AGENDAR);
		}
		
		Collections.sort(examesParaAgendamento, new AgendamentoExameVOComparator());
				
		return examesParaAgendamento;
	}
	
	/**
	 * Ordenação das solicitações de exames 
	 * 1. Desativados
	 * 2. SeqP
	 * 
	 */
	static class AgendamentoExameVOComparator implements Comparator<AgendamentoExameVO> {
		@Override
		public int compare(AgendamentoExameVO vo1, AgendamentoExameVO vo2) {
			Integer retorno = 0;
			if(vo1.getDthrReativacao()!=null && vo2.getDthrReativacao()==null) {
				retorno = -1;
			}
			if(vo1.getDthrReativacao()==null && vo2.getDthrReativacao()!=null) {
				retorno = 1;
			}
			if(vo1.getDthrReativacao()==null && vo2.getDthrReativacao()==null || vo1.getDthrReativacao()!=null && vo2.getDthrReativacao()!=null) {
				retorno = vo1.getItemExame().getId().getSeqp().compareTo(vo2.getItemExame().getId().getSeqp());
			}
			return retorno;
		}
	}
	
	/**
	 * Ordenação das solicitações por data
	 * 
	 */
	static class VAelSolicAtendsVOComparator implements Comparator<VAelSolicAtendsVO> {
		@Override
		public int compare(VAelSolicAtendsVO vo1, VAelSolicAtendsVO vo2) {
			return vo1.getDataSolicitacao().compareTo(vo2.getDataSolicitacao());
		}
	}
	
	/**
	 * Esta função retorna a data agendada para um item de solicitação de 
	 * exames que esteja na situação agendada. Caso contrário, retorna NULL. 
	 * 
	 * @return
	 */
	public List<Date> retornaDataAgendadaItemSolicitacao(AelItemSolicitacaoExames itemExame) {
		List<Date> hedDthrAgenda = getAelItemHorarioAgendadoDAO().buscarListHedDthrAgenda(itemExame.getId().getSoeSeq(), itemExame.getId().getSeqp());

		return hedDthrAgenda;	
	}
	
	/**
	 * Retorna a lista de exames selecionados.  
	 * 
	 * @return
	 */
	public List<AgendamentoExameVO> obterExamesSelecionados(List<AgendamentoExameVO> exames) {
		List<AgendamentoExameVO> examesSelecionados = new ArrayList<AgendamentoExameVO>();
		
		for(AgendamentoExameVO exame : exames) {
			if(exame.getSelecionado()) {
				exame.setItemExame(getAelItemSolicitacaoExameDAO().merge(exame.getItemExame()));
				//carregando associações
				if (exame.getItemExame().getSolicitacaoExame().getAtendimento() != null) {
					exame.getItemExame().getSolicitacaoExame().getAtendimento()
							.getPaciente().getNome();
					if (exame.getItemExame().getSolicitacaoExame()
							.getAtendimento().getConsulta() != null) {
						exame.getItemExame().getSolicitacaoExame()
								.getAtendimento().getConsulta().getNumero();
					}
				}					
				
				if(exame.getItemExame().getSolicitacaoExame().getAtendimentoDiverso()!=null) {
					AelAtendimentoDiversos atendimentoDiverso = exame.getItemExame().getSolicitacaoExame().getAtendimentoDiverso();
					atendimentoDiverso.getAipPaciente().getNome();
				}				
				if(exame.getItemExame().getExame()!=null){
					exame.getItemExame().getExame().getDescricaoUsual();
				}
				if(exame.getItemExame().getAelUnfExecutaExames() != null  ){
					exame.getItemExame().getAelUnfExecutaExames().getCriadoEm();
				}				
				if(exame.getItemExame().getMaterialAnalise()!=null){
					exame.getItemExame().getMaterialAnalise().getDescricao();
				}
				if(exame.getItemExame().getUnidadeFuncional()!=null){				
					exame.getItemExame().getUnidadeFuncional().getDescricao();
				}
				if(exame.getItemExame().getSituacaoItemSolicitacao()!=null){
					exame.getItemExame().getSituacaoItemSolicitacao().getDescricao();
				}

                examesSelecionados.add(exame);
			}
		}
		
		return examesSelecionados;
	}
	
	/**
	 * Função que verifica se tem tickets de exame do paciente à imprimir.
	 * 
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public void verificaImpressaoTicketExame(List<AgendamentoExameVO> examesAgendamentoSelecao) throws ApplicationBusinessException {
		Boolean existeTicket = false;
		for(AgendamentoExameVO exame : examesAgendamentoSelecao) {
			if(exame.getSelecionado()) {
				existeTicket = true;
				break;
			}
		}
		if(!existeTicket) {
			throw new ApplicationBusinessException(ExamesAgendamentoSelecaoONExceptionCode.MENSAGEM_ERRO_SELECIONAR_EXAME_IMPRIMIR);
		}
	}
	
	
	/*
	 * Verificar se existem exames da mesma solicitação que possuem amostras em comum
	 */
	public boolean verificarExistenciaAmostrasComum(List<AgendamentoExameVO> listaItensExame){
        for (AgendamentoExameVO itemRef : listaItensExame){
            if (itemRef.getListaAmostras() != null && !itemRef.getListaAmostras().isEmpty()) {
                for (AgendamentoExameVO itemLista : listaItensExame) {
                    if(!itemLista.getItemExame().getId().equals(itemRef.getItemExame().getId())){
                        if (verificarAmostrasComumEmListasAmostras(itemRef.getListaAmostras(), itemLista.getListaAmostras())){
                        	return true;
                        }
                    }
                }
            }
        }
		return false;
	}
	
	/*
	 * Se existem exames da mesma solicitação que possuem amostras em comum e algum deste exames
	 *  não tiver sido selecionado deve-se retornar uma lista de todos os exames com mesma amostra
	 */
    public List<AgendamentoExameVO> verificarExamesNaoSelecComMesmaAmostra(List<AgendamentoExameVO> listaItensExame, AghParametros parametro, String label) throws ApplicationBusinessException{
    	int examesSelecionados = 0;
		List<AgendamentoExameVO> examesDevemSerCanceladosJuntos = new ArrayList<AgendamentoExameVO>();
	        for (AgendamentoExameVO itemSelecionado : listaItensExame){

	            if (itemSelecionado.getSelecionado()){
	            	examesSelecionados++;

	                if (itemSelecionado.getListaAmostras() != null && !itemSelecionado.getListaAmostras().isEmpty()) {
	                	examesDevemSerCanceladosJuntos.add(itemSelecionado);

	                    for (AgendamentoExameVO itemLista : listaItensExame) {
	                    	// Caso o item a ser testado não estiver agendado e não estiver selecionado para agendamento 
	                    	// deve-se verificar se este possui itens de amostra em comum com algum exame selecionado
	                        if(!itemLista.getItemExame().getSituacaoItemSolicitacao().getCodigo().equals(parametro.getVlrTexto())
	                        	&&	!itemLista.getSelecionado()){
	                            if (verificarAmostrasComumEmListasAmostras(itemSelecionado.getListaAmostras(), itemLista.getListaAmostras())){
	                            	examesDevemSerCanceladosJuntos.add(itemLista);
	                            }
	                        }
	                    }
	                }
	            }
	        }
	        if (examesSelecionados == 0) {
				throw new ApplicationBusinessException(
						ExamesAgendamentoSelecaoONExceptionCode.MENSAGEM_ERRO_SELECIONAR_EXAME_AGENDAR_OU_CANCELAR_AGENDA,
						label);
			}
	        if (examesDevemSerCanceladosJuntos != null
					&& examesDevemSerCanceladosJuntos.size() > examesSelecionados) {
				return examesDevemSerCanceladosJuntos;
			} else {
				return null;
			}
    }

    public boolean verificarAmostrasComumEmListasAmostras(List<AelAmostras> listaAmostras, List<AelAmostras> listaAmostrasItemNaoSelecionado){
    	return CollectionUtils.containsAny(listaAmostras, listaAmostrasItemNaoSelecionado);
    }
    
    public List<AelItemSolicitacaoExames> buscarItensSolicitacaoExameNaoAgendados(final Integer soeSeq, final List<Short> listaSeqUnF){
    	List<AelItemSolicitacaoExames> listaItensExame = null;
    	AghParametros parametroAgendado;
		try {
			parametroAgendado = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO);
			listaItensExame = getAelItemSolicitacaoExameDAO().buscarItensSolicitacaoExameNaoAgendados(parametroAgendado.getVlrTexto(), soeSeq, listaSeqUnF);
		} catch (ApplicationBusinessException e) {			
			LOG.error(e.getMessage(), e);
		}
    	
    	return listaItensExame;
    }

    public boolean verificarExistenciaItensSolicitacaoExameNaoAgendados(final Integer soeSeq){
    	boolean existeExameNaoAgendado = false;    	
    	AghParametros parametroAgendado;
    	
		try {
			parametroAgendado = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO);
			existeExameNaoAgendado = getAelItemSolicitacaoExameDAO().verificarExistenciaItensSolicitacaoExameNaoAgendados(parametroAgendado.getVlrTexto(), soeSeq);
		} catch (ApplicationBusinessException e) {			
			LOG.error(e.getMessage(), e);
		}
    	
    	return existeExameNaoAgendado;
    }
    
    protected AelAmostrasDAO getAelAmostrasDAO() {
        return aelAmostrasDAO;
    }

    protected void setAelAmostrasDAO(AelAmostrasDAO aelAmostrasDAO) {
        this.aelAmostrasDAO = aelAmostrasDAO;
    }

		
	protected VAelSolicAtendsDAO getVAelSolicAtendsDAO() {
		return vAelSolicAtendsDAO;
	}
	
	protected VAelGradeExameDAO getVAelGradeExameDAO() {
		return vAelGradeExameDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelMatrizSituacaoDAO getAelMatrizSituacaoDAO() {
		return aelMatrizSituacaoDAO;
	}
	
	protected RapServidores obterServidorLogado(final String login) throws ApplicationBusinessException {
		return getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(login);
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
