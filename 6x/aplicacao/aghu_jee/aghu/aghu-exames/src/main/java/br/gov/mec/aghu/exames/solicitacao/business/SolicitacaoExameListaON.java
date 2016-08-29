package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelRegiaoAnatomicaDAO;
import br.gov.mec.aghu.exames.dao.AelTmpIntervaloColetaDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameItemVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameResultadoVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelTmpIntervaloColeta;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SolicitacaoExameListaON extends BaseBusiness {

	private static final String MSG_EXCEPTION = "Criterios de Pesquisa incompletos - dever chamar metodo verificarFiltrosPesquisaSolicitacaoExame!";

	private static final Log LOG = LogFactory.getLog(SolicitacaoExameListaON.class);

	@Inject
	private AelTmpIntervaloColetaDAO aelTmpIntervaloColetaDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AelRegiaoAnatomicaDAO aelRegiaoAnatomicaDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private VAelSolicAtendsDAO vAelSolicAtendsDAO;
	
	@EJB
	private IAghuFacade aghuFacade;


	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2072093965621191539L;

	public enum SolicitacaoExameListaONExceptionCode implements BusinessExceptionCode {
		MSG_PREENCHER_ALGUM_FILTRO
		, MSG_PREENCHER_MAIS_ALGUM_FILTRO, MSG_PRONTUARIO_INVALIDO, MSG_CONSULTA_INVALIDA, MSG_QUARTO_INVALIDA, MSG_LEITO_INVALIDA
		;
	}
	
	
	public void verificarFiltrosPesquisaSolicitacaoExame(SolicitacaoExameFilter filter) throws ApplicationBusinessException {
		
		if (!filter.isPreenchido()) {
			throw new ApplicationBusinessException(SolicitacaoExameListaONExceptionCode.MSG_PREENCHER_ALGUM_FILTRO);
		}

		//Inicio Melhoria #12665
		if (filter.getProntuario() != null && !isProntuarioExistente(filter.getProntuario())) {
			throw new ApplicationBusinessException(SolicitacaoExameListaONExceptionCode.MSG_PRONTUARIO_INVALIDO);
		}
		
		if (filter.getNumero() != null && !isConsultaExistente(filter.getNumero())) {		
			throw new ApplicationBusinessException(SolicitacaoExameListaONExceptionCode.MSG_CONSULTA_INVALIDA);
		}
		
		if (StringUtils.isNotBlank(filter.getQuarto()) && !verificarQuartoExistePorDescricao(filter.getQuarto())) {
			throw new ApplicationBusinessException(SolicitacaoExameListaONExceptionCode.MSG_QUARTO_INVALIDA);
		}
		
		if (StringUtils.isNotBlank(filter.getLeito()) && StringUtils.isNotBlank(filter.getLeito()) && !isLeitoExistente(filter.getLeito())) {	
			throw new ApplicationBusinessException(SolicitacaoExameListaONExceptionCode.MSG_LEITO_INVALIDA);
		}
		//Fim Melhoria #12665
	}
	
	public Long pesquisarAtendimentosPacienteTotalRegistros(SolicitacaoExameFilter filter) throws BaseException {
		
		List<String> fonemasPaciente = this.getFonemasPaciente(filter);
		
		return this.getAghuFacade().pesquisarAtendimentoParaSolicitacaoExamesCount(filter, fonemasPaciente);
	}

	public Long pesquisarAtendimentosPacienteInternadoCount(final SolicitacaoExameFilter filter) throws BaseException {

        if ( filter == null || filter.getProntuario() == null) {
            return 0l;
        }

		if ( !filter.isPreenchido() ) {
			throw new IllegalArgumentException(MSG_EXCEPTION);
		}

		if ( !filter.isPreenchidoFiltroMinimo() ) {
			throw new IllegalArgumentException(MSG_EXCEPTION);
		}

		List<String> fonemasPaciente = this.getFonemasPaciente(filter);
		
		return this.getAghuFacade().pesquisarAtendimentosPacienteInternadoCount(filter, fonemasPaciente);
	}
	
	public SolicitacaoExameItemVO pesquisarAtendimentosPacienteUnico(SolicitacaoExameFilter filter) throws BaseException {

      //  if ( filter == null || filter.getProntuario() == null) {
			//  return null;
	  //  }

		List<String> fonemasPaciente = this.getFonemasPaciente(filter);
		
		List<AghAtendimentos> atendimentos = this.getAghuFacade().pesquisarAtendimentoParaSolicitacaoExames(filter, fonemasPaciente, 0, 10, null, true);
		if (atendimentos!= null && atendimentos.size() == 1) {
			return new SolicitacaoExameItemVO(atendimentos.get(0));
		} else {
			throw new IllegalArgumentException("Deveria ter um registro pelo menos!!!!");
		}
	}

	public SolicitacaoExameItemVO pesquisarAtendimentosPacienteInternadoUnico(SolicitacaoExameFilter filter) throws BaseException {

		List<String> fonemasPaciente = this.getFonemasPaciente(filter);
		
		List<AghAtendimentos> atendimentos = this.getAghuFacade().pesquisarAtendimentosPacientesInternados(filter, fonemasPaciente, 0, 10, null, true);
		if (atendimentos!= null && atendimentos.size() == 1) {
			return new SolicitacaoExameItemVO(atendimentos.get(0));
		} else {
			throw new IllegalArgumentException("Deveria ter um registro!!!!");
		}
	}
	
	/**
	 * @return
	 * @throws BaseException 
	 */
	public SolicitacaoExameResultadoVO pesquisarAtendimentosPaciente(SolicitacaoExameFilter filter, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws BaseException {
		
		SolicitacaoExameResultadoVO vo = new SolicitacaoExameResultadoVO();
		
		List<String> fonemasPaciente = this.getFonemasPaciente(filter);
		
		List<AghAtendimentos> atendimentos = this.getAghuFacade().pesquisarAtendimentoParaSolicitacaoExames(filter, fonemasPaciente, firstResult, maxResult, orderProperty, asc);
		List<SolicitacaoExameItemVO> lista = new LinkedList<SolicitacaoExameItemVO>();
		for (AghAtendimentos atd: atendimentos) {
			
			AipPacientes pac =  pacienteFacade.obterAipPacientesPorChavePrimaria(atd.getPaciente().getCodigo());
			atd.setPaciente(pac);
			AghUnidadesFuncionais undFunc = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(atd.getUnidadeFuncional().getSeq());
			atd.setUnidadeFuncional(undFunc);
			
			if (atd.getEspecialidade() != null) {
			    AghEspecialidades esp =  aghuFacade.obterAghEspecialidadesPorChavePrimaria(atd.getEspecialidade().getSeq());
			    atd.setEspecialidade(esp);
			}
			if(atd.getQuarto() != null){			
			  AinQuartos  quarto = internacaoFacade.obterAinQuartosPorChavePrimaria(atd.getQuarto().getNumero());
			  atd.setQuarto(quarto);
			}
			
			if(atd.getLeito() != null){
			   AinLeitos leito = internacaoFacade.obterAinLeitosPorChavePrimaria(atd.getLeito().getLeitoID());
			   atd.setLeito(leito);
			}		
					
			
			lista.add(new SolicitacaoExameItemVO(atd));
		}
		
		Long totalRegistros = this.getAghuFacade().pesquisarAtendimentoParaSolicitacaoExamesCount(filter, fonemasPaciente);
		
		vo.setListaAtendimentosDoPaciente(lista);
		vo.setTotalRegistros(totalRegistros.intValue());
		
		return vo;
	}

	private boolean isLeitoExistente(String leito) {
		AinLeitos leitos = getInternacaoFacade().obterAinLeitosPorChavePrimaria(leito);
		return leitos != null;
	}

	private boolean verificarQuartoExistePorDescricao(String descricao) {
		AinQuartos quartos = getInternacaoFacade().obterQuartoDescricao(descricao);
		return quartos != null;
	}

	private boolean isConsultaExistente(Integer numero) {
		AacConsultas consulta = getAmbulatorioFacade().obterConsulta(numero);
		return consulta != null;
	}

	private boolean isProntuarioExistente(Integer prontuario) {
		AipPacientes paciente = getPacienteFacade().obterPacientePorProntuario(prontuario);
		return paciente != null;
	}

	private List<String> getFonemasPaciente(SolicitacaoExameFilter filter)
			throws ApplicationBusinessException {
		List<String> fonemasPaciente = null;
		if (StringUtils.isNotBlank(filter.getNomePaciente())) {
			fonemasPaciente = this.getPacienteFacade().obterFonemasNaOrdem(filter.getNomePaciente());
		}
		return fonemasPaciente;
	}
	
	/*
	 * Lista de exames de responsabilidade do solicitante ou responsável
	 */
	public SolicitacaoExameResultadoVO listarExamesCancelamentoSolicitante(PesquisaExamesFiltroVO filtro) throws BaseException {
		SolicitacaoExameResultadoVO vo = new SolicitacaoExameResultadoVO();
		List<VAelSolicAtendsVO> lista = new ArrayList<VAelSolicAtendsVO>();

		if ( !filtro.isPreenchidoFiltroMinimo() ) {
			throw new ApplicationBusinessException(SolicitacaoExameListaONExceptionCode.MSG_PREENCHER_MAIS_ALGUM_FILTRO);
		}
		Integer totalRegistros = getVAelSolicAtendsDAO().listarExamesCancSolicTotalRegistros(filtro);

		lista = getVAelSolicAtendsDAO().listarExamesCancelamentoSolicitante(filtro);

		vo.setListaSolicitacaoExames(lista);
		vo.setTotalRegistros(totalRegistros);

		return vo;
	}
	
	
	protected VAelSolicAtendsDAO getVAelSolicAtendsDAO() {
		return vAelSolicAtendsDAO;
	}
	
	/**
	 * Metodo utilizado pela SuggestionBox
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> buscarUnidadeFuncionais(String parametro) {
		return getAghuFacade().pesquisarAghUnidadesFuncionaisPorSequencialOuDescricao(parametro);
	}
	
	public Long buscarUnidadeFuncionaisCount(String parametro) {
		return getAghuFacade().pesquisarAghUnidadesFuncionaisPorSequencialOuDescricaoCount(parametro);
	}	
	
	public List<AelTmpIntervaloColeta> listarPesquisaIntervaloColeta(String seq, AelUnfExecutaExames exame) {
		return getAelTmpIntervaloColetaDAO().listarPesquisaIntervaloColeta(seq, exame);
	}
	
	/**
	 * Retorna uma lista de AelRegiaoAnatomica a partir de <br>
	 * uma consulta da suggestion, por seq ou pela descricao.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<AelRegiaoAnatomica> listarRegiaoAnatomica(String objPesquisa, List<Integer> regioesMama) {
		return getAelRegiaoAnatomicaDAO().listarRegiaoAnatomica(objPesquisa, regioesMama);
	}
	
	/** GET/SET **/
		
	private IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected AelTmpIntervaloColetaDAO getAelTmpIntervaloColetaDAO() {
		return aelTmpIntervaloColetaDAO;
	}
	
	protected AelRegiaoAnatomicaDAO getAelRegiaoAnatomicaDAO() {
		return aelRegiaoAnatomicaDAO;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

}
