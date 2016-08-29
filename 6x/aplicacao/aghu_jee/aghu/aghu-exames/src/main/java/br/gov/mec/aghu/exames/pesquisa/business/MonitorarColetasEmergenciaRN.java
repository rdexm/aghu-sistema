package br.gov.mec.aghu.exames.pesquisa.business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemanaFeriado;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.exames.dao.AelHorarioRotinaColetasDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.exames.solicitacao.business.SolicitacaoExameON;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelHorarioRotinaColetas;
import br.gov.mec.aghu.model.AelHorarioRotinaColetasId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.IAelSolicitacaoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MonitorarColetasEmergenciaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(MonitorarColetasEmergenciaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelHorarioRotinaColetasDAO aelHorarioRotinaColetasDAO;

@Inject
private VAelSolicAtendsDAO vAelSolicAtendsDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private SolicitacaoExameON solicitacaoExameON;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5250397255440892894L;
	public final static String V_URGENTE = "Urgente";
	public final static String V_PROGRAMADA = "Programada";
	
	public enum MonitorarColetasEmergenciaRNExceptionCode implements BusinessExceptionCode {
		ERRO_P_AVISO_COLETA_PROGRAMADA, ERRO_P_SITUACAO_A_COLETAR;
	}
	
	/**
	 * ORADB funcao aelc_ver_horario_col
	 * @param AelSolicitacaoExame
	 * @return
	 */
	private boolean restricaoHorarioColeta(AghUnidadesFuncionais unidadeExecutora, List<AelItemSolicitacaoExames> listaSolicitacaoExame){

		for (AelItemSolicitacaoExames item : listaSolicitacaoExame) {
			if(item.getTipoColeta().equals(DominioTipoColeta.U)){
				return true;
			}
		}

		for (AelItemSolicitacaoExames item : listaSolicitacaoExame) {

			final Date soeCriadoEm = item.getSolicitacaoExame().getCriadoEm();
			final Date iseDthrProgramada = item.getDthrProgramada();

			if(soeCriadoEm.before(iseDthrProgramada) && this.validaHorarioColeta(unidadeExecutora, item)){
				return true;
			}

		}

		return false;
	}
	
	/**
	 * ORADB funcao aelc_laudo_orig_pac
	 * @param AelSolicitacaoExame
	 * @return
	 */
	private boolean restricaoLaudoOrigemPaciente(List<AelItemSolicitacaoExames> listaSolicitacaoExame){

		for (AelItemSolicitacaoExames item : listaSolicitacaoExame) {

			final String emaExaSigla = item.getAelUnfExecutaExames().getId().getEmaExaSigla();
			final Integer emaManSeq = item.getAelUnfExecutaExames().getId().getEmaManSeq();
			final AelSolicitacaoExames solicitacaoExame = item.getSolicitacaoExame();

			List<AelTipoAmostraExame> listaTipoAmostraExame = getAelTipoAmostraExameDAO().buscarListaAelTipoAmostraExameColetadorSolicitante(emaExaSigla,emaManSeq);

			for (AelTipoAmostraExame tipoAmostraExame : listaTipoAmostraExame) {
				if(tipoAmostraExame.getOrigemAtendimento().equals(DominioOrigemAtendimento.T)
						|| tipoAmostraExame.getOrigemAtendimento().equals(this.validaLaudoOrigemPaciente(solicitacaoExame))) {
					return true;
				}
			}	

		}

		return false;
	}
	
	/**
	 * 
	 * Pesquisa para VO de solicitacoes de exame para o monitoramento de coletas de emergencia realizado tudo em query.
	 * No método abaixo "pesquisaMonitoramentoColetasEmergencia" a lógica era feita em código realizando muitos acessos a base e estourando timeout
	 * @param unidadeExecutora
	 * @return
	 * @throws BaseException
	 */
	public List<VAelSolicAtendsVO> pesquisaMonitoramentoColetasEmergenciaAgregado(AghUnidadesFuncionais unidadeExecutora) throws BaseException  {
		Date dataCalculadaAparecimentoSolicitacao = this.obterDataCalculadaAparecimentoSolicitacao();
		String sitCodigo = this.obterSituacaoExameColetado();
		
		return this.getVAelSolicAtendsDAO().pesquisarVPLAgregado(unidadeExecutora, dataCalculadaAparecimentoSolicitacao, sitCodigo);
	}
	
	/**
	 * Pesquisa para VO de solicitacoes de exame para o monitoramento de coletas de emergencia
	 * @param unidadeExecutora
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public List<VAelSolicAtendsVO> pesquisaMonitoramentoColetasEmergencia(AghUnidadesFuncionais unidadeExecutora) throws BaseException  {
		
		Date dataCalculadaAparecimentoSolicitacao = this.obterDataCalculadaAparecimentoSolicitacao();
		String sitCodigo = this.obterSituacaoExameColetado();
		
		List<VAelSolicAtendsVO> resultadoPesquisaVPL = this.getVAelSolicAtendsDAO().pesquisarVPL(unidadeExecutora, dataCalculadaAparecimentoSolicitacao, sitCodigo);
		
		// Populando a VO de VAelSolicAtends
		List<VAelSolicAtendsVO> retornoListaVAelSolicAtendsVO = new LinkedList<VAelSolicAtendsVO>();

		for (VAelSolicAtendsVO vo : resultadoPesquisaVPL) {

			// Realiza validacoes de VO
			List<AelItemSolicitacaoExames> listaSolicitacaoExame = getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExameMonitoramento(vo.getNumero(),unidadeExecutora, dataCalculadaAparecimentoSolicitacao, sitCodigo);

			Boolean isItemVoValido = this.restricaoHorarioColeta(unidadeExecutora, listaSolicitacaoExame);
			if(!isItemVoValido){
				continue;
			}
			isItemVoValido = this.restricaoLaudoOrigemPaciente(listaSolicitacaoExame);
			if(!isItemVoValido){
				continue;
			}
			
			vo.setPacienteGMR(solicitacaoExameON.isPacienteGMR(vo.getNumero()));

			// Verifica a solicitacao e urgente ou programada
			boolean isUrgente = false;
			boolean isProgramada = false;
			forSolicitacoesUrgentes:
				for (AelItemSolicitacaoExames item : listaSolicitacaoExame) {	

					// Caso ambas condicoes forem encontradas: salva as demais pesquisas
					if(isUrgente && isProgramada){
						break forSolicitacoesUrgentes;
					}

					// Testa urgente
					if (!isUrgente){
						final String retorno = this.pesquisarSolicitacoesExamesUrgentes(item.getSolicitacaoExame(), unidadeExecutora);
						if (retorno != null){
							isUrgente = true;
							vo.setPrioridade(retorno);
						}
					}
					
					// Testa programada
					if (!isProgramada){
						final String retorno = this.pesquisarSolicitacoesExamesProgramadas(item.getSolicitacaoExame(), unidadeExecutora);
						if (retorno != null){
							isProgramada = true;
							vo.setProgramada(retorno);
						}
					}

				}

			// Acrescenta item de solicitacao de coelta na lista de VOs
			retornoListaVAelSolicAtendsVO.add(vo);
		}
		resultadoPesquisaVPL.clear(); // Limpeza necessária para menor consumo de memória
		return retornoListaVAelSolicAtendsVO;
	}
	
	/**
	 * Pesquisa para itens de solicitacoes de exame para o monitoramento de coletas de emergencia
	 * @param unidadeExecutora
	 * @return
	 * @throws BaseException
	 */
	public List<AelItemSolicitacaoExames> pesquisaMonitoramentoColetasEmergenciaItensProgramados(AghUnidadesFuncionais unidadeExecutora, VAelSolicAtendsVO vo) throws BaseException  {
		
		List<AelItemSolicitacaoExames>  listaOrigem = this.getAelItemSolicitacaoExameDAO().pesquisaMonitoramentoColetasEmergenciaItensProgramados(unidadeExecutora, vo, this.obterSituacaoExameColetado());
		List<AelItemSolicitacaoExames>  listaDestino = new LinkedList<AelItemSolicitacaoExames>();
		
		// Aplica regras adicionais na lista de origem e separa itens validos na lista de destino (retorno)
		if(listaOrigem != null){
			for (AelItemSolicitacaoExames item : listaOrigem) {
				
				// Resgata o tipo de amostra de exame do item de solicitacao de coleta
				List<AelTipoAmostraExame> listaTae = this.getAelTipoAmostraExameDAO().buscarListaAelTipoAmostraExameColetadorSolicitante(item.getExame().getSigla(), item.getMaterialAnalise().getSeq());
				
				for (AelTipoAmostraExame tae : listaTae) {
					// Teste a origem do atendimento. Somente todas as origens ou origens retornadas no laudo do paciente sao validas.
					if(tae.getOrigemAtendimento().equals(DominioOrigemAtendimento.T) 
							|| tae.getOrigemAtendimento().equals(this.validaLaudoOrigemPaciente(item.getSolicitacaoExame()))){
						listaDestino.add(item);
					}
				}	
				
			}
		}
		
		return listaDestino;
	}
	
	/**
	 * Funcoes
	 */
	
	/**
	 * ORADB funcao aelc_ver_horario_col
	 * @param AelSolicitacaoExame
	 * @return
	 */
	public final boolean validaHorarioColeta(AghUnidadesFuncionais unidadeExecutora, AelItemSolicitacaoExames itemSolicitacaoExame){
		return this.getAelHorarioRotinaColetasDAO().obterAelHorarioRotinaColetasDAO(unidadeExecutora,itemSolicitacaoExame);
	}
	
	public final DominioOrigemAtendimento validaLaudoOrigemPaciente(IAelSolicitacaoExames solicitacaoExame) {
		if (solicitacaoExame instanceof AelSolicitacaoExames) {
			return validaLaudoOrigemPaciente((AelSolicitacaoExames) solicitacaoExame);
		}
		else if (solicitacaoExame instanceof AelSolicitacaoExamesHist) {
			return validaLaudoOrigemPacienteHist((AelSolicitacaoExamesHist) solicitacaoExame);
		}
		
		return null;
	}
	
	/**
	 * ORADB funcao aelc_laudo_orig_pac
	 * @param AelSolicitacaoExame
	 * @return origem do atendimento da solicitacao de exame
	 */
	public final DominioOrigemAtendimento validaLaudoOrigemPaciente(AelSolicitacaoExames solicitacaoExame){
			
		// Recupera o atendimento da solicitacao de exame
		AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
			
			if(atendimento != null){
				// Se o atendimento existir retorna: A origem do mesmo
				return atendimento.getOrigem();
			} else{ 
				// Se o atendimento NAO existir: Consulta em atendimento diversos
				AelAtendimentoDiversos atendimentoDiversos = solicitacaoExame.getAtendimentoDiverso();
				if(atendimentoDiversos == null){
					// Se o atendimento diverso NAO existir: Retorna nulo
					return null;
				} else if(atendimentoDiversos.getAbsCandidatosDoadores() == null){
					// Se o atendimento diverso existir e NÃO existir cadastro: Retorna nulo
					return null;
				} else{
					// Para nenhuma das condicoes a origem do atendimento sera: Doacao de sangue
					return DominioOrigemAtendimento.D;
				}
			}
	}	
	
	/**
	 * ORADB funcao aelc_laudo_orig_pac
	 * @param AelSolicitacaoExame
	 * @return origem do atendimento da solicitacao de exame
	 */
	public final DominioOrigemAtendimento validaLaudoOrigemPacienteHist(AelSolicitacaoExamesHist solicitacaoExame){
			
		// Recupera o atendimento da solicitacao de exame
		AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
			
			if(atendimento != null){
				// Se o atendimento existir retorna: A origem do mesmo
				return atendimento.getOrigem();
			} else{ 
				// Se o atendimento NAO existir: Consulta em atendimento diversos
				AelAtendimentoDiversos atendimentoDiversos = solicitacaoExame.getAtendimentoDiverso();
				if(atendimentoDiversos == null){
					// Se o atendimento diverso NAO existir: Retorna nulo
					return null;
				} else if(atendimentoDiversos.getAbsCandidatosDoadores() == null){
					// Se o atendimento diverso existir e NÃO existir cadastro: Retorna nulo
					return null;
				} else{
					// Para nenhuma das condicoes a origem do atendimento sera: Doacao de sangue
					return DominioOrigemAtendimento.D;
				}
			}
	}	

	/*
	 * Procedures...
	 */
	
	/** 
	 * ORADB EVT_WHEN_NEW_FORM_INSTANCE - Calcula data programada para ocorrencias de solicitacao programada
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 */
	  protected Date obterDataCalculadaAparecimentoSolicitacao() throws BaseException {
	    AghParametros aghParametro;
	    Date valorDataCalculada = null;
	    aghParametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AVISO_COLETA_PROGRAMADA);
	    if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
	      valorDataCalculada = DateUtil.adicionaMinutos(new Date(), aghParametro.getVlrNumerico().intValue());
	    } else {
	      throw new ApplicationBusinessException(MonitorarColetasEmergenciaRNExceptionCode.ERRO_P_AVISO_COLETA_PROGRAMADA);
	    }
	    return valorDataCalculada;
	  }

	/** 
	 * ORADB EVT_WHEN_NEW_FORM_INSTANCE - Obter situacao do exame a coletar
	 * @param tipoAmostraExame
	 * @throws ApplicationBusinessException
	 *  
	 */
	protected String obterSituacaoExameColetado() throws BaseException {
		AghParametros aghParametro = null;
		String situacao = null;
		aghParametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
		if (aghParametro != null && aghParametro.getVlrTexto() != null) {
			situacao = aghParametro.getVlrTexto();
		} else {
			throw new ApplicationBusinessException(MonitorarColetasEmergenciaRNExceptionCode.ERRO_P_SITUACAO_A_COLETAR);
		}
		return situacao;
	}
	
	/**
	 * ORADB AELC_BUSCA_PROGRAMADA
	 * @param AelSolicitacaoExame
	 * @return
	 */
	public String pesquisarSolicitacoesExamesProgramadas(AelSolicitacaoExames solicitacaoExame, AghUnidadesFuncionais unidadeExecutora) throws BaseException {

		List<AelItemSolicitacaoExames> listaSoeUrgentes = this.getAelItemSolicitacaoExameDAO().pesquisaItemSolicitacaoExameBuscaProgramada(solicitacaoExame, this.obterSituacaoExameColetado(), unidadeExecutora, obterDataCalculadaAparecimentoSolicitacao());

		List<AelItemSolicitacaoExames> listaSoeAtendimentos = new LinkedList<AelItemSolicitacaoExames>();
		
		for (AelItemSolicitacaoExames itemSoeUrgentes : listaSoeUrgentes) {

			List<AelTipoAmostraExame> listaTae = this.getAelTipoAmostraExameDAO().buscarListaAelTipoAmostraExameColetador(itemSoeUrgentes.getExame().getSigla(), itemSoeUrgentes.getMaterialAnalise().getSeq());
			
			for (AelTipoAmostraExame tae : listaTae) {
				// Teste a origem do atendimento. Somente todas as origens ou origens retornadas no laudo do paciente sao validas.
				if(tae.getOrigemAtendimento().equals(DominioOrigemAtendimento.T) 
						|| tae.getOrigemAtendimento().equals(this.validaLaudoOrigemPaciente(itemSoeUrgentes.getSolicitacaoExame()))){
					listaSoeAtendimentos.add(itemSoeUrgentes);
				}
			}
		}
		
		String vProgramada = null;
		
		for (AelItemSolicitacaoExames itemSoeAtendimento : listaSoeAtendimentos) {

			AelHorarioRotinaColetasId horarioRotinaColetasId = new AelHorarioRotinaColetasId();
			horarioRotinaColetasId.setUnfSeq(unidadeExecutora.getSeq());
			horarioRotinaColetasId.setUnfSeqSolicitante(unidadeExecutora);
			horarioRotinaColetasId.setDia(DominioDiaSemanaFeriado.getInstance(CoreUtil.retornaDiaSemana(itemSoeAtendimento.getDthrProgramada()).toString().substring(0, 3).toUpperCase()));
			horarioRotinaColetasId.setHorario(itemSoeAtendimento.getDthrProgramada());
			
			AelHorarioRotinaColetas horarioRotinaColetas  = this.getAelHorarioRotinaColetasDAO().obterPorChavePrimaria(horarioRotinaColetasId);
			
			// Caso exista um horario de rotina de coleta a solicitacao de exame nao sera considerada programada
			if(horarioRotinaColetas == null){
				vProgramada = V_PROGRAMADA;
			}
			
		}
	
		return vProgramada;
	}	
	
	/**
	 * ORADB AELC_BUSCA_URGENTE
	 * @param soeSeq
	 * @param unfExecutoraSeq
	 * @return
	 * @throws BaseException
	 */
	public String pesquisarSolicitacoesExamesUrgentes(AelSolicitacaoExames solicitacaoExame, AghUnidadesFuncionais unidadeExecutora) throws BaseException {

		List<AelItemSolicitacaoExames> listaSoeUrgentes = this.getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExameBuscaUrgente(solicitacaoExame, this.obterSituacaoExameColetado(), unidadeExecutora);
		
		for (AelItemSolicitacaoExames item : listaSoeUrgentes) {
			
			List<AelTipoAmostraExame> listaTae = this.getAelTipoAmostraExameDAO().buscarListaAelTipoAmostraExameColetador(item.getExame().getSigla(), item.getMaterialAnalise().getSeq());
			for (AelTipoAmostraExame tae : listaTae) {
				// Teste a origem do atendimento. Somente todas as origens ou origens retornadas no laudo do paciente sao validas.
				if(tae.getOrigemAtendimento().equals(DominioOrigemAtendimento.T) 
						|| tae.getOrigemAtendimento().equals(this.validaLaudoOrigemPaciente(item.getSolicitacaoExame()))){
					return V_URGENTE;
				}
			}
			
		}
		
		return null;
	}	
	
	/*
	 * Getters...
	 */	
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected VAelSolicAtendsDAO getVAelSolicAtendsDAO() {
		return vAelSolicAtendsDAO;
	}
	
	protected AelHorarioRotinaColetasDAO getAelHorarioRotinaColetasDAO() {
		return aelHorarioRotinaColetasDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}

	public SolicitacaoExameON getSolicitacaoExameON() {
		return solicitacaoExameON;
	}

	public void setSolicitacaoExameON(SolicitacaoExameON solicitacaoExameON) {
		this.solicitacaoExameON = solicitacaoExameON;
	}
	

}
