package br.gov.mec.aghu.internacao.leitos.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinSolicTransfPacientesDAO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.RapServidoresTransPacienteVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SolicitarTransferenciaPacienteON extends BaseBusiness {

	@EJB
	private ExtratoLeitoON extratoLeitoON;
	
	private static final Log LOG = LogFactory.getLog(SolicitarTransferenciaPacienteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3174071022173051141L;

	private enum SolicitarTransferenciaPacienteONExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_SOLICITACAO_TRANS_PAC, ERRO_CANCELAR_SOLICITACAO_TRANS_PAC,
		MENSAGEM_PARAMETRO_MEDICINA_NAO_ENCONTRADO, MENSAGEM_PARAMETRO_ODONTO_NAO_ENCONTRADO,
		ERRO_REMOVER_SOLIC_TRANSF_PACIENTES, ERRO_INFORMAR_PRONTUARIO_LEITO, ERRO_LEITO_SEM_PACIENTE_INTERNADO, 
		ERRO_PACIENTE_NAO_INTERNADO_LEITO, TRANSF_PACIENTE_POSSUI_ALTA_OBITO, ERRO_P_COD_ACOM_LTO_ISOLAMENTO
	}

	
	/**
	 * Método responsável pela persistência de uma solicitação de transferência de paciente.
	 * 
	 * @param solicitacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void persistir(AinSolicTransfPacientes solicitacao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		IRegistroColaboradorFacade registroColaboradorFacade = this.getRegistroColaboradorFacade();
		
		validarPacienteJaPossuiAlta(solicitacao.getInternacao().getAtendimento());
	
		RapServidoresId rapServidoresId = new RapServidoresId();
		rapServidoresId.setMatricula(servidorLogado.getId().getMatricula());
		rapServidoresId.setVinCodigo(servidorLogado.getId().getVinCodigo());

		if(solicitacao.getServidorDigitador() == null) {
			solicitacao.setServidorDigitador(registroColaboradorFacade.obterServidor(rapServidoresId));
		}
		if(solicitacao.getServidorSolicitante() == null) {
			solicitacao.setServidorSolicitante(registroColaboradorFacade.obterServidor(rapServidoresId));
		}
			
		if(DominioSimNao.S.equals(solicitacao.getIndLeitoIsolamento())) {
			AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_ACOM_LTO_ISOLAMENTO);
			if(solicitacao.getAcomodacoes() == null || (solicitacao.getAcomodacoes() != null && !solicitacao.getAcomodacoes().getSeq().equals(parametro.getVlrNumerico().intValue()))) {
				solicitacao.setAcomodacoes(this.getCadastrosBasicosInternacaoFacade().obterAcomodacao(parametro.getVlrNumerico().intValue()));
				if (solicitacao.getAcomodacoes() == null) {
					throw new ApplicationBusinessException(SolicitarTransferenciaPacienteONExceptionCode.ERRO_P_COD_ACOM_LTO_ISOLAMENTO);
				}
			}
		}
			
		if(solicitacao.getEspecialidades() == null)
		{
			solicitacao.setEspecialidades(solicitacao.getInternacao().getEspecialidade());
		}
			
		solicitacao.setCriadoEm(new Date());
			
		AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO = this.getAinSolicTransfPacientesDAO();
		ainSolicTransfPacientesDAO.persistir(solicitacao);
		ainSolicTransfPacientesDAO.flush();
	}
	
	/**
	 * Verifica se o paciente já possui alta
	 * @param atendimento
	 * @throws ApplicationBusinessException
	 */
	public void validarPacienteJaPossuiAlta(AghAtendimentos atendimento) throws ApplicationBusinessException{
		MpmAltaSumario altaSumario = getPrescricaoMedicaFacade().obterAltaSumarioPorAtendimento(atendimento);
		if (altaSumario != null){
			throw new ApplicationBusinessException(SolicitarTransferenciaPacienteONExceptionCode.TRANSF_PACIENTE_POSSUI_ALTA_OBITO);
		}
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return this.prescricaoMedicaFacade;
	}
	
	/**
	 * Método responsável pelo cancekamento de uma solicitação de transferência de paciente.
	 * 
	 * @param solicitacao
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('solicitacaoTransferenciaPaciente','cancelar')}")
	public void cancelar(AinSolicTransfPacientes solicitacao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		IRegistroColaboradorFacade registroColaboradorFacade = this.getRegistroColaboradorFacade();
		
		try {

			RapServidoresId rapServidoresId = new RapServidoresId();
			
			rapServidoresId.setMatricula(servidorLogado.getId().getMatricula());
			rapServidoresId.setVinCodigo(servidorLogado.getId().getVinCodigo());

			if(solicitacao.getServidorCancelador() == null) {
					solicitacao.setServidorCancelador(registroColaboradorFacade.obterServidor(rapServidoresId));
			}
			
			solicitacao.setIndSitSolicLeito(DominioSituacaoSolicitacaoInternacao.C);
			solicitacao.setDthrAtendimentoSolicitacao(new Date());
			
			AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO = this.getAinSolicTransfPacientesDAO();
			ainSolicTransfPacientesDAO.atualizar(solicitacao);
			ainSolicTransfPacientesDAO.flush();
		} catch (Exception e) {
			this.logError("Erro ao cancelar a solicitação de transferência de paciente.", e);
			throw new ApplicationBusinessException(
					SolicitarTransferenciaPacienteONExceptionCode.ERRO_CANCELAR_SOLICITACAO_TRANS_PAC);
		}
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(Object parametro) {
		return getAghuFacade().pesquisarUnidadeFuncional(parametro, 20);
	}

	public AghUnidadesFuncionais unidadeFuncionalInternacao(AinInternacao internacao) {
		AinInternacao internacaoLocal = ainInternacaoDAO.obterPorChavePrimaria(internacao.getSeq());
		
		if (DominioLocalPaciente.Q.equals(internacao.getIndLocalPaciente())) {
			return internacaoLocal.getQuarto() != null ? internacaoLocal.getQuarto().getUnidadeFuncional() : null;
			
		} else if (DominioLocalPaciente.L.equals(internacao.getIndLocalPaciente())) {
			return internacaoLocal.getLeito() != null ? internacaoLocal.getLeito().getUnidadeFuncional() : null;
		}
		
		return getAghuFacade().obterUnidadeFuncionalPorChavePrimaria(internacaoLocal.getUnidadesFuncionais());
	}

	public String mensagemSolicTransPaciente(Integer internacao){		
		List<Object[]> resultadoPesquisa = this.getAinSolicTransfPacientesDAO().mensagemSolicTransPaciente(internacao);
		StringBuffer retorno = new StringBuffer();
		for (Object[] obj : resultadoPesquisa) {
			retorno.append(((DominioSituacaoSolicitacaoInternacao) obj[0]).getDescricao() + ((((Long) obj[1]) > 1) ? "s" : "") + " -> " + obj[1] + " ");
		}
		return retorno.toString();
	}
		
	public List<RapServidores> pesquisarServidores(String paramPesquisa, AghEspecialidades especialidade, FatConvenioSaude convenio)
			throws ApplicationBusinessException {

		// Destinatários
		AghParametros qualificacaoMedicina = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_MEDICINA);
		if (qualificacaoMedicina == null || qualificacaoMedicina.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(SolicitarTransferenciaPacienteONExceptionCode.MENSAGEM_PARAMETRO_MEDICINA_NAO_ENCONTRADO);
		}
		AghParametros qualificacaoOdonto = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_ODONTO);
		if (qualificacaoOdonto == null || qualificacaoOdonto.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(SolicitarTransferenciaPacienteONExceptionCode.MENSAGEM_PARAMETRO_ODONTO_NAO_ENCONTRADO);
		}

		Integer[] tipoQualificacao = { qualificacaoOdonto.getVlrNumerico().intValue(),
				qualificacaoMedicina.getVlrNumerico().intValue() };

		return this.getRegistroColaboradorFacade().pesquisarServidores(paramPesquisa, especialidade, convenio, tipoQualificacao, 20);
	}


	public List<RapServidoresTransPacienteVO> pesquisarServidoresPorNomeOuCRM(String paramPesquisa, AghEspecialidades especialidade,
			FatConvenioSaude convenio) throws ApplicationBusinessException {

		// Destinatários
		AghParametros qualificacaoMedicina = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_MEDICINA);
		if (qualificacaoMedicina == null || qualificacaoMedicina.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(SolicitarTransferenciaPacienteONExceptionCode.MENSAGEM_PARAMETRO_MEDICINA_NAO_ENCONTRADO);
		}
		AghParametros qualificacaoOdonto = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_ODONTO);
		if (qualificacaoOdonto == null || qualificacaoOdonto.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(SolicitarTransferenciaPacienteONExceptionCode.MENSAGEM_PARAMETRO_ODONTO_NAO_ENCONTRADO);
		}

		Integer[] tipoQualificacao = { qualificacaoOdonto.getVlrNumerico().intValue(),
				qualificacaoMedicina.getVlrNumerico().intValue() };

		return this.getRegistroColaboradorFacade().pesquisarServidoresPorNomeOuCRM(paramPesquisa, especialidade, convenio,
				tipoQualificacao, 20);
	}
	
	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaLeito(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer prontuario, String leitoID) throws ApplicationBusinessException {
		
		validarFiltrosPesquisaSolicitacaoTransferenciaLeito(prontuario, leitoID);
		
		List<AinSolicTransfPacientes> listaSolicitacoes = new ArrayList<AinSolicTransfPacientes>(0);
		Integer prontuarioPesquisa = prontuario;
		
		if (StringUtils.isNotBlank(leitoID)) {
			prontuarioPesquisa = obterProntuarioPesquisaSolicitacaoTransferenciaLeito(prontuario, leitoID);
		}
		
		if (prontuarioPesquisa != null) {
			listaSolicitacoes = getAinSolicTransfPacientesDAO().pesquisarSolicitacaoTransferenciaLeito(prontuarioPesquisa, firstResult, maxResults, orderProperty, asc);
		}
		return listaSolicitacoes;
	}

	private void validarFiltrosPesquisaSolicitacaoTransferenciaLeito(
			Integer prontuario, String leitoID) throws ApplicationBusinessException {
		if (prontuario == null && StringUtils.isEmpty(leitoID)) {
			throw new ApplicationBusinessException(SolicitarTransferenciaPacienteONExceptionCode.ERRO_INFORMAR_PRONTUARIO_LEITO);
		}
	}
	
	private Integer obterProntuarioPesquisaSolicitacaoTransferenciaLeito(Integer prontuario, String leitoID)
			throws ApplicationBusinessException {
		List<AghAtendimentos> atendimentos = getAghuFacade().pesquisaAtendimento(prontuario, leitoID);
		if (atendimentos == null || atendimentos.isEmpty()) {
			if (prontuario == null) {
				throw new ApplicationBusinessException(SolicitarTransferenciaPacienteONExceptionCode.ERRO_LEITO_SEM_PACIENTE_INTERNADO);
			} else {
				throw new ApplicationBusinessException(SolicitarTransferenciaPacienteONExceptionCode.ERRO_PACIENTE_NAO_INTERNADO_LEITO);
			}
		}
		return atendimentos.get(0).getProntuario();
	}

	public Long pesquisarSolicitacaoTransferenciaLeitoCount(
			Integer prontuario, String leitoID) {
		Long count = 0l;
		
		try {
			validarFiltrosPesquisaSolicitacaoTransferenciaLeito(prontuario, leitoID);
			
			Integer prontuarioPesquisa = prontuario;
			if (StringUtils.isNotBlank(leitoID)) {
				prontuarioPesquisa = obterProntuarioPesquisaSolicitacaoTransferenciaLeito(prontuario, leitoID);
			}
			
			if (prontuarioPesquisa != null) {
				count = getAinSolicTransfPacientesDAO().pesquisarSolicitacaoTransferenciaLeitoCount(prontuarioPesquisa);		
			}
		} catch (ApplicationBusinessException e) {
			this.logWarn(e);
		}

		return count;
	}
	
	public AinInternacao obterInternacaoPorProntuario(Integer prontuario) {
		return getAinInternacaoDAO().obterInternacaoPorProntuario(prontuario);
	}

	public AinInternacao obterInternacaoPorLeito(String leitoId) {
		return getAinInternacaoDAO().obterInternacaoPorLeito(leitoId);
	}

	public AinSolicTransfPacientes obterSolicTransfPacientePorId(Integer seq) {
		return getAinSolicTransfPacientesDAO().obterPorChavePrimaria(seq);
	}
	
	public AinInternacao obterInternacaoPorId(Integer seq) {
		return getAinInternacaoDAO().obterPorChavePrimaria(seq);
	}

	/**
	 * @ORADB V_RAP_SERVIDOR_CRM
	 * Parâmetros: matrícula e codigo do servidor
	 * @param matricula
	 * @param vinCodigo
	 * @return List<EspCrmVO>, contendo os dados do medico (CRM, nomeMedico, nomeUsual, espSeq, cpf),
	 *  onde cada elemento da lista armazena uma especialidade do medico.
	 */
	public List<EspCrmVO> obterDadosDoMedicoPelaMatriculaEVinCodigo(Integer matricula, Short vinCodigo) {
		return getAghuFacade().obterDadosDoMedicoPelaMatriculaEVinCodigo(matricula, vinCodigo);
	}
		
	/**
	 * Usado na LOV classica para buscar uma lista de RapServidores pela matricula ou nome;
	 * @param responsavel
	 * @return
	 */
	public List<RapServidores> pesquisarSolicitantes(Object responsavel){
		
		return this.getExtratoLeitoON().pesquisarResponsaveis(responsavel);
	}
	
	/**
	 * Método que deleta as solicitações de transferência de uma internação
	 * @param solicitacoesTransf
	 * @throws ApplicationBusinessException
	 */
	public void removerSolicitacoesTransferenciaPaciente(Set<AinSolicTransfPacientes> solicitacoesTransf) throws ApplicationBusinessException{		
		try{
			AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO = this.getAinSolicTransfPacientesDAO();
			
			for (AinSolicTransfPacientes solicitacao: solicitacoesTransf){					
				AinSolicTransfPacientes item = ainSolicTransfPacientesDAO.obterPorChavePrimaria(solicitacao.getSeq());
				ainSolicTransfPacientesDAO.remover(item);
				ainSolicTransfPacientesDAO.flush();
			}
		}
		catch (Exception e) {
			logError("Exceção capturada: ", e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
						SolicitarTransferenciaPacienteONExceptionCode.ERRO_REMOVER_SOLIC_TRANSF_PACIENTES, ((ConstraintViolationException) cause).getConstraintName());
			}
			else{
				throw new ApplicationBusinessException(
						SolicitarTransferenciaPacienteONExceptionCode.ERRO_REMOVER_SOLIC_TRANSF_PACIENTES, "");
			}
		}
	}
	
	protected ExtratoLeitoON getExtratoLeitoON(){
		return extratoLeitoON;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected AinSolicTransfPacientesDAO getAinSolicTransfPacientesDAO(){
		return ainSolicTransfPacientesDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
