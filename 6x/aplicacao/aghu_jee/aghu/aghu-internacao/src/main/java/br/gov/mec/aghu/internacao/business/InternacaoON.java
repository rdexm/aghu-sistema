package br.gov.mec.aghu.internacao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.dao.AinAcomodacoesDAO;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinObservacoesPacAltaDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinObservacoesPacAlta;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.MpmControlPrevAltas;
import br.gov.mec.aghu.model.PacIntdConv;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class InternacaoON extends BaseBusiness {

	private static final long serialVersionUID = -3705745695369914878L;

	private static final Log LOG = LogFactory.getLog(InternacaoON.class);
	
	
	@EJB
	private InternacaoRN internacaoRN;
			
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	
	
	@Inject
	private AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;

	@Inject
	private AinObservacoesPacAltaDAO ainObservacoesPacAltaDAO;
	
	@Inject
	private AinAcomodacoesDAO ainAcomodacoesDAO;
	

	private enum InternacaoONExceptionCode implements BusinessExceptionCode {
		ERRO_REMOVER_ATENDIMENTO_URGENCIA, ERRO_ATUALIZAR_OBSERVACAO_ALTA, ERRO_PREV_ALTA_MENOR, ERRO_ATUALIZAR_PREV_ALTA, DATA_PREVISAO_ALTA_OBRIGATORIO
		// Um valor é obrigatório para o campo Previsão Alta
	}

	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Método que persiste (insert/update) objetos PacIntdConv. O mesmo se
	 * encontra nesta ON pois não faz parte do schema AGH, não existindo módulo
	 * no sistema para ele. Foi escolhida esta ON pois é no módulo de Internação
	 * que o mesmo é utilizado.
	 * 
	 * @dbtables PacIntdConv insert,update
	 * 
	 * @param pacIntdConv
	 */
	@Secure("#{s:hasPermission('internacao','alterar')}")
	public void persistirInternacaoConvenio(PacIntdConv pacIntdConv) {
		this.getPacienteFacade().atualizarPacIntdConv(pacIntdConv, true);
	}

	/**
	 * 
	 * @dbtables AinAtendimentosUrgencia insert,update
	 * 
	 * @param atendimentoUrgencia
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('atendimentoUrgencia','alterar')}")
	public void persistirAtendimentoUrgencia(
			AinAtendimentosUrgencia atendimentoUrgencia)
			throws ApplicationBusinessException {
		if (atendimentoUrgencia.getSeq() == null) {
			this.incluirAtendimentoUrgencia(atendimentoUrgencia);
		} else {
			this.alterarAtendimentoUrgencia(atendimentoUrgencia);
		}
	}

	private void incluirAtendimentoUrgencia(
			AinAtendimentosUrgencia atendimentoUrgencia)
			throws ApplicationBusinessException {
		// internacaoRN.validarAtendimentoUrgenciaAntesInserir(atendimentoUrgencia);

		AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO = this.getAinAtendimentosUrgenciaDAO();
		ainAtendimentosUrgenciaDAO.persistir(atendimentoUrgencia);
		ainAtendimentosUrgenciaDAO.flush();
		
		// FIXME: Retirar no método AtualizaInternacaoRN.atualizaAtendimentos, a
		// chamada
		// atualizarLeitoAtendimentoUrgencia (que existe dentro do for) quando
		// for implementada
		// a enforce de AIN_ATENDIMENTOS_URGENCIA

		// internacaoRN.processarEnforceAtendimentoUrgencia(atendimentoUrgencia,
		// null, TipoOperacaoEnum.INSERT);
	}

	private void alterarAtendimentoUrgencia(
			AinAtendimentosUrgencia atendimentoUrgencia)
			throws ApplicationBusinessException {
		// AinAtendimentosUrgencia atendimentoUrgenciaOriginal =
		// this.entityManager
		// .find(AinAtendimentosUrgencia.class,
		// atendimentoUrgencia.getSeq());
		// this.getSession().evict(atendimentoUrgenciaOriginal);
		//		
		// internacaoRN.validarAtendimentoUrgenciaAntesAtualizar(atendimentoUrgencia,
		// atendimentoUrgenciaOriginal);

		AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO = this.getAinAtendimentosUrgenciaDAO();
		ainAtendimentosUrgenciaDAO.atualizar(atendimentoUrgencia);
		ainAtendimentosUrgenciaDAO.flush();
		
		// internacaoRN.validarAtendimentoUrgenciaAposAtualizar(atendimentoUrgencia,
		// atendimentoUrgenciaOriginal);
		//		
		// internacaoRN.processarEnforceAtendimentoUrgencia(atendimentoUrgencia,
		// atendimentoUrgenciaOriginal, TipoOperacaoEnum.DELETE);
	}

	/**
	 * 
	 * @dbtables AinInternacao select
	 * 
	 * @param intSeq
	 * @return
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacaoPorSeq(Integer intSeq, Enum... inners) {
		return this.getAinInternacaoDAO().obterPorChavePrimaria(intSeq, inners);
	}

	/**
	 * Obter internações dados do professor.
	 * 
	 * @dbtables AinInternacao select
	 * 
	 * @param intSeq
	 * @return
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> obterInternacao(Boolean indPacienteInternado,
			Integer matriculaProf, Short vinCodigoProf, Short codConvenio,
			Short espSeq) {
		return this.getAinInternacaoDAO().obterInternacao(indPacienteInternado, matriculaProf, vinCodigoProf, codConvenio, espSeq);
	}

	/**
	 * 
	 * @param seqInternacao
	 * @param codigoObservacaoPacAlta
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('alta','alterar')}")
	public void associarObservacaoPacAlta(Integer seqInternacao,
			Integer codigoObservacaoPacAlta, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		try {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			AinInternacao internacao = this.getAinInternacaoDAO().obterPorChavePrimaria(seqInternacao);
			AinObservacoesPacAlta observacaoPacAlta = null;

			if (codigoObservacaoPacAlta != null) {
				observacaoPacAlta = this.getAinObservacoesPacAltaDAO().obterPorChavePrimaria(codigoObservacaoPacAlta);
			}

			internacao.setObservacaoPacienteAlta(observacaoPacAlta);
			this.getInternacaoRN().atualizarInternacao(internacao, nomeMicrocomputador, dataFimVinculoServidor, false, servidorLogado, false);

			this.getAinInternacaoDAO().flush();
		} catch (BaseException e) {
			throw new ApplicationBusinessException(
					InternacaoONExceptionCode.ERRO_ATUALIZAR_OBSERVACAO_ALTA, e);
		}
	}

	/**
	 * Método para retornar um objeto de acomodação através do seu ID.
	 * 
	 * @param seq
	 * @return
	 */
	@Secure("#{s:hasPermission('acomodacao','pesquisar')}")
	public AinAcomodacoes obterAcomodacao(Integer seq) {
		return this.getAinAcomodacoesDAO().obterPorChavePrimaria(seq);
	}

	/**
	 * Método para buscar a acomodação da internação através da conta de
	 * internação e da conta hospitalar
	 * 
	 * @param seqInternacao
	 * @param cspCnvCodigo
	 * @return
	 */
	@Secure("#{s:hasPermission('acomodacao','pesquisar')}")
	public AinAcomodacoes obterAcomodacaoContaHospitalar(Integer seqInternacao,
			Short cspCnvCodigo) {

		FatContasInternacao contaInternacao = this.getFaturamentoFacade().obtePrimeiraContaHospitalar(seqInternacao, cspCnvCodigo);
		if (contaInternacao != null && contaInternacao.getContaHospitalar() != null) {
			return contaInternacao.getContaHospitalar().getAcomodacao();
		}
		return null;
	}

	/**
	 * Obtém a última internação (NÃO VIGENTE) do paciente internado
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterUltimaInternacaoPaciente(Integer codigoPaciente) {
		return this.getAinInternacaoDAO().obterUltimaInternacaoNaoVigentePaciente(codigoPaciente);
	}

	/**
	 * Atualiza a previão de alta da internação passada por parâmetro.
	 * 
	 * @param internacao
	 * @param dtPrevAlta
	 * @throws BaseException
	 */
	public void atualizaPrevisaoAltaInternacao(AinInternacao internacao,
			Date dtPrevAlta, Boolean confirmacaoPelaModalPrevPlanoAlta, 
			Boolean considerarValorModalPlanoPrevAlta, Boolean bloquearData) throws BaseException {
		
		AghParametros param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PADRAO_CONTROL_PREV_ALTA);
		Integer qtdDiasPadrao = param != null ? param.getVlrNumerico().intValue() : 3; //Valor padrão para dias de prev alta, caso parâmetro não informado é igual a 3 dias 

		
		AinInternacaoDAO ainInternacaoDAO = this.getAinInternacaoDAO();
		
		internacao = this.getAinInternacaoDAO().obterPorChavePrimaria(internacao.getSeq());
		
		if(bloquearData && dtPrevAlta == null){
			dtPrevAlta = DateUtil.adicionaDias(new Date(),qtdDiasPadrao);
		}

		// #51433 - bloquearData verifica se a unidade possui característica de "Controla Previsão Alta",
		// caso afirmativo, não lança a exceção "Data da Previsão de alta deve ser maior ou igual a data atual".
		if (bloquearData || DateValidator.validaDataTruncadaMaiorIgual(dtPrevAlta, new Date())) {
			
			internacao.setDtPrevAlta(dtPrevAlta);
			
			if(confirmacaoPelaModalPrevPlanoAlta){
				
				MpmControlPrevAltas controlPrevAltas = new MpmControlPrevAltas();
				
				if(considerarValorModalPlanoPrevAlta){
					controlPrevAltas.setResposta(DominioSimNao.S.toString());
					controlPrevAltas.setDtInicio(new Date());
					controlPrevAltas.setDtFim(internacao.getDtPrevAlta());
				}else{
					internacao.setDtPrevAlta(DateUtil.adicionaDias(new Date(),qtdDiasPadrao));
					controlPrevAltas.setResposta(DominioSimNao.N.toString());
					controlPrevAltas.setDtInicio(null);
					controlPrevAltas.setDtFim(null);
				}
				
				Short qtdDiasFat = internacao.getItemProcedimentoHospitalar().getQuantDiasFaturamento();
				
				if(qtdDiasFat == null || qtdDiasFat == 0){
					internacao.setDtPrevAlta(DateUtil.adicionaDias(new Date(),qtdDiasPadrao));
				}else{
					Date dtFat = DateUtil.adicionaDias(internacao.getDthrInternacao(),qtdDiasFat.intValue());
					Date dtAtual = new Date();
					if(DateUtil.validaDataMaior(dtFat,dtAtual)){
						internacao.setDtPrevAlta(DateUtil.adicionaDias(new Date(),qtdDiasPadrao));
					}
				}				
				
				
				controlPrevAltas.setAtendimento(internacao.getAtendimento());
				controlPrevAltas.setServidor(this.getServidorLogadoFacade().obterServidorLogado());
			
				this.prescricaoMedicaFacade.persistirMpmControlPrevAltas(controlPrevAltas);
				
			}						
			
			//TODO: não precisa chamar triggers de internação para esta operação
			//this.getInternacaoRN().atualizarInternacao(internacao);
			
			ainInternacaoDAO.atualizar(internacao);
			ainInternacaoDAO.flush();
		} else {
			throw new ApplicationBusinessException(
					InternacaoONExceptionCode.ERRO_PREV_ALTA_MENOR);
		}

	}
	
	public Boolean exibirModalPlanoPrevAlta(AinInternacao internacao) throws ApplicationBusinessException {
		
		String parametro = this.getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_MPM_CARACTERISTICA_PREV_ALTA);
		if (	DominioOrigemAtendimento.I.equals(internacao.getAtendimento().getOrigem())
				&& ainInternacaoDAO.verificarUnidadeFuncionalControleAlta(internacao, parametro)
				&& permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "finalizarPrescricao", "executar")
				&& !prescricaoMedicaFacade.verificaPrevisaoAltaProxima(internacao.getAtendimento())
				) {
			return true;
		}
		return false;
	}
	
	protected InternacaoRN getInternacaoRN() {
		return internacaoRN;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected AinAtendimentosUrgenciaDAO getAinAtendimentosUrgenciaDAO(){
		return ainAtendimentosUrgenciaDAO;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}
	
	protected AinObservacoesPacAltaDAO getAinObservacoesPacAltaDAO(){
		return ainObservacoesPacAltaDAO;
	}
	
	protected AinAcomodacoesDAO getAinAcomodacoesDAO(){
		return ainAcomodacoesDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
}
