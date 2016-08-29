package br.gov.mec.aghu.internacao.transferir.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class TransferirPacienteON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TransferirPacienteON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AinInternacaoDAO ainInternacaoDAO;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@Inject
	private AinQuartosDAO ainQuartosDAO;

	@Inject
	private AinLeitosDAO ainLeitosDAO;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;

	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8141909619330937224L;

	private enum TransferirPacienteONExceptionCode implements BusinessExceptionCode {
		ERRO, DATA_TRANSEFRENCIA_INVALIDA, SEM_LEITO_QUARTO_UNIDADE_DESTINO, TRANSFERIR_PARA_MAIS_DE_UM_DESTINO, AIN_00316, QUARTO_INEXISTENTE, DATA_POSTERIOR_ALTA_MEDICA, 
		ESPECIALIDADE_EMERGENCIA, AIN_00736, PARAMETRO_NAO_ENCONTRADO, AIN_00822, TRANSF_PACIENTE_POSTERIOR_ALTA_OBITO
	}

	public List<ProfessorCrmInternacaoVO> pesquisarProfessor(String strPesq, AghEspecialidades especialidade, AinInternacao internacao) {
		return getInternacaoFacade().pesquisarProfessoresCrm(especialidade.getSeq(), especialidade.getSigla(),
				internacao.getConvenioSaude().getCodigo(), internacao.getConvenioSaude().getVerificaEscalaProfInt(), strPesq, null, null);
	}

	public AinLeitos pesquisarApenasLeitoConcedido(Object strPesq, Integer intSeq) {
		List<AinLeitos> leitosConcedidos = getAinLeitosDAO().pesquisarAtivosConcedidos(strPesq, intSeq);
		AinLeitos leitoConcedido = null;
		// LEITO CONCEDIDO
		if (leitosConcedidos != null && !leitosConcedidos.isEmpty()) {
			for (AinLeitos ainLeitos : leitosConcedidos) {
				if (ainLeitos.getTipoMovimentoLeito().getGrupoMvtoLeito().equals(DominioMovimentoLeito.L)
						|| ainLeitos.getTipoMovimentoLeito().getGrupoMvtoLeito().equals(DominioMovimentoLeito.R)) {
					leitoConcedido = ainLeitos;
				}
			}
		}
		return leitoConcedido;
	}

	public List<AghEspecialidades> pesquisarEspecialidades(Object strPesquisa, Integer idadePaciente) {
		return getAghuFacade().listarEspecialidadeTransPaciente(strPesquisa, idadePaciente);// internacao.getPaciente().getIdade());
	}

	public ProfessorCrmInternacaoVO obterProfessorCrmInternacaoVO(RapServidores servidorProfessor, AghEspecialidades especialidade,
			Short cnvCodigo) {
		return getInternacaoFacade().obterProfessorCrmInternacaoVO(servidorProfessor, especialidade, cnvCodigo);
	}

	public void atualizarInternacao(AinInternacao internacao, AinInternacao oldInternacao, String nomeMicrocomputador) throws BaseException {
		this.validarEspecialidadeGrupoEmergencia(internacao);
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		getInternacaoFacade().atualizarInternacao(internacao, oldInternacao, nomeMicrocomputador,servidorLogado, new Date(), false);
		flush();
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}	
	/**
	 * AINF_TRANSFERIR_PAC.pll Método When_Validate_Item Método que valida e
	 * impede que uma especialidade pertença a um grupo de emergência
	 * 
	 * @param internacao
	 *            , unfSeq
	 * @return
	 * 
	 */
	private void validarEspecialidadeGrupoEmergencia(final AinInternacao internacao) throws BaseException {
		// Mudança para lançar BaseException gmneto 12/02/2010
		// Obtém unfSeq
		Short unfSeq = null;
		if (internacao.getQuarto() != null) {
			unfSeq = internacao.getQuarto().getUnidadeFuncional().getSeq();
		} else if (internacao.getLeito() != null) {
			unfSeq = internacao.getLeito().getUnidadeFuncional().getSeq();
		} else {
			unfSeq = internacao.getUnidadesFuncionais().getSeq();
		}

		final ConstanteAghCaractUnidFuncionais constanteUnidadeEmergencia = ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA;
		if (!this.getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, constanteUnidadeEmergencia)) {
			final AghuParametrosEnum enumGrupoEspEmerg = AghuParametrosEnum.P_GRUPO_ESPEC_EMERG;
			final AghParametros parametroGrupoEspEmerg = this.getParametroFacade().buscarAghParametro(enumGrupoEspEmerg);
			Hibernate.initialize(internacao.getEspecialidade());
			if (internacao.getEspecialidade().getGreSeq() != null
					&& internacao.getEspecialidade().getGreSeq().intValue() == parametroGrupoEspEmerg.getVlrNumerico().intValue()) {
				// Mudança para lançar exceção sem rollback gmneto 12/02/2010
				throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.AIN_00822);

			}
		}

	}

	public List<AghUnidadesFuncionais> pesquisarUnidades(Object strPesquisa) {
		return getPacienteFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(
				strPesquisa,
				new Object[] { ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA,
						ConstanteAghCaractUnidFuncionais.BLOCO });
	}

	public void validarDestino(AinInternacao internacao) throws ApplicationBusinessException {
		if (internacao.getLeito() == null && internacao.getUnidadesFuncionais() == null && internacao.getQuarto() == null) {
			throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.SEM_LEITO_QUARTO_UNIDADE_DESTINO);
		}

		if ((internacao.getLeito() != null && internacao.getUnidadesFuncionais() != null)
				|| (internacao.getLeito() != null && internacao.getQuarto() != null)
				|| (internacao.getUnidadesFuncionais() != null && internacao.getQuarto() != null)) {
			throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.TRANSFERIR_PARA_MAIS_DE_UM_DESTINO);
		}
	}

	public void validaEspecialidade(AinInternacao internacao) throws ApplicationBusinessException {
		if (!isLocUnidadeEmergencia(internacao.getLeito() != null ? internacao.getLeito().getLeitoID() : null,
				internacao.getQuarto() != null ? internacao.getQuarto().getNumero() : null,
				internacao.getUnidadesFuncionais() != null ? internacao.getUnidadesFuncionais().getSeq() : null)) {
			AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRUPO_ESPEC_EMERG);
			if (internacao.getEspecialidade() != null && internacao.getEspecialidade().getSeq().equals(parametro.getVlrNumerico())) {
				throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.ESPECIALIDADE_EMERGENCIA);
			}
		}
	}

	public void validarDataInternacao(AinInternacao internacao) throws ApplicationBusinessException {
		if (internacao.getDthrUltimoEvento() != null && internacao.getDthrInternacao() != null
				&& internacao.getDthrUltimoEvento().before(internacao.getDthrInternacao())) {
			// AIN-00736 - O último evento deve ser maior ou igual a data da
			// internação
			throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.AIN_00736);
		}
	}

	public void validarDataTransferenciaPosteriorAlta(AinInternacao internacao) throws ApplicationBusinessException {
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		Date dtAlta = getDthrAltaMedica(internacao.getSeq());

		if (dtAlta != null && internacao.getDthrUltimoEvento().after(dtAlta)) {
			throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.DATA_POSTERIOR_ALTA_MEDICA, sdf.format(dtAlta));
		}
	}

	public boolean validarDataTransferencia(Date dataTransf) throws ApplicationBusinessException {
		Date dataAtual = new Date();
		if (dataTransf == null || dataTransf.after(dataAtual)) {
			throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.DATA_TRANSEFRENCIA_INVALIDA);
		}

		return true;
	}

	public boolean consisteClinicaEEspecialidade(AinInternacao internacao) throws ApplicationBusinessException {
		try {
			AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONS_CLIN);
			String consisteClinica;
			boolean pedeConfirmacao = false;

			if (parametro != null && "I".equalsIgnoreCase(parametro.getVlrTexto())) {
				consisteClinica = parametro.getVlrTexto();
			} else {
				consisteClinica = "R";
			}
			
			if(internacao.getLeito() != null) {
				AinQuartos quarto =  getInternacaoFacade().obterAinQuartosPorChavePrimaria(internacao.getLeito().getQuarto().getNumero());			
				if (internacao.getEspecialidade() != null
					&& internacao.getEspecialidade().getClinica() != null
					&& !internacao.getEspecialidade().getClinica().equals(quarto.getClinica())
					&& DominioSimNao.S.equals(quarto.getIndConsClin())) {
					if ("R".equals(consisteClinica)) {
						throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.AIN_00316);
					} else {
						pedeConfirmacao = true;
					}
				}
			}

			return pedeConfirmacao;
			// int.dsp_clc_codigo2 - clínica da esp
			// int.dsp_clc_codigo - clínica do quarto
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.ERRO);
		}
	}

	@SuppressWarnings("unchecked")
	public List<AinLeitos> pesquisarLeitos(Object strPesq, Integer intSeq) {

		List<AinLeitos> leitos = new ArrayList<AinLeitos>();

		AinInternacao internacao = getAinInternacaoDAO().obterPorChavePrimaria(intSeq);

		List<AinLeitos> leitosDesocupados = getAinLeitosDAO().pesquisarLeitosAtivosDesocupados(strPesq, null);
		List<AinLeitos> leitosReservados = getAinLeitosDAO().pesquisarLeitosAtivosReservados(strPesq, null);
		List<AinLeitos> leitosProprioLeito = getAinLeitosDAO().pesquisarLeitosAtivosOcupados(strPesq, null);

		// AinLeitos leitoConcedido =
		// this.pesquisarApenasLeitoConcedido(strPesq, intSeq);

		final BeanComparator ordenarLeito = new BeanComparator(AinLeitos.Fields.LTO_ID.toString(), new NullComparator(false));

		// //Remover o concedido das outras listas
		// leitosDesocupados.remove(leitoConcedido);
		// leitosReservados.remove(leitoConcedido);

		if (leitosDesocupados != null && !leitosDesocupados.isEmpty()) {
			Collections.sort(leitosDesocupados, ordenarLeito);
		}

		if (leitosReservados != null && !leitosReservados.isEmpty()) {
			Collections.sort(leitosReservados, ordenarLeito);
		}

		// Logo em seguida os desocupados
		leitos.addAll(leitosDesocupados);
		// Logo em seguida os reservados
		leitos.addAll(leitosReservados);

		// Por último ele mesmo - Se estiver contemplado na pesquisa
		if (!leitosProprioLeito.isEmpty()) {
			for (AinLeitos ainLeitos : leitosProprioLeito) {
				if (ainLeitos.equals(internacao.getLeito())) {
					leitos.add(ainLeitos);
					break;
				}
			}
		}
		
		if(leitos.size() == 1){
			leitos.get(0).getQuarto().getClinica();
		}
		
		return leitos;

	}

	public List<AinInternacao> pesquisarInternacao(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer prontuario) {
		return getAinInternacaoDAO().pesquisarInternacao(firstResult, maxResults, orderProperty, asc, prontuario);
	}

	public Long pesquisarInternacaoCount(Integer prontuario) {
		return getAinInternacaoDAO().pesquisarInternacaoCount(prontuario);
	}

	public AinQuartos obterQuartoDescricao(String ainQuartosDescricao) {
		AinQuartos retorno = getAinQuartosDAO().obterQuartoDescricao(ainQuartosDescricao);
		return retorno;
	}

	public void validarQuartoInexistente(String ainQuartosDescricao) throws ApplicationBusinessException {
		AinQuartos quarto = obterQuartoDescricao(ainQuartosDescricao);
		if (quarto == null) {
			throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.QUARTO_INEXISTENTE);
		}
	}

	public void validarPacienteJaPossuiAlta(AghAtendimentos atendimento, Date dtTransferencia) throws ApplicationBusinessException {
		MpmAltaSumario altaSumario = getPrescricaoMedicaFacade().obterAltaSumarioPorAtendimento(atendimento);
		if (altaSumario != null) {
			if(altaSumario.getDthrAlta() != null && altaSumario.getDthrAlta().before(dtTransferencia)) {
				throw new ApplicationBusinessException(TransferirPacienteONExceptionCode.TRANSF_PACIENTE_POSTERIOR_ALTA_OBITO);
			}
		}
	}

	public AinInternacao atualizarInternacaoSemFlush(AinInternacao internacao) {
		return getAinInternacaoDAO().atualizar(internacao);
	}

	public AinInternacao obterInternacao(Integer seq) {
		AinInternacao retorno = getAinInternacaoDAO().obterPorChavePrimaria(seq);
		return retorno;
	}

	// @ORADB : AINC_LOC_UNID_EMERG
	public boolean isLocUnidadeEmergencia(String leitoId, Short quartoNum, Short unfSeq) {

		Short seq = null;
		if (leitoId != null) {
			seq = getAinLeitosDAO().buscarMaxSeqUnidadeFuncionalSeqDoLeito(leitoId);
		} else if (quartoNum != null) {
			seq = getAinQuartosDAO().buscarMaxSeqUnidadeFuncionalSeqDoQuarto(quartoNum);
		} else {
			seq = unfSeq;
		}

		return getLeitosInternacaoFacade().verificarCaracteristicaUnidadeFuncional(seq, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);
	}

	// @ORADB : AINC_DTHR_ALTA_MED
	public Date getDthrAltaMedica(Integer intSeq) {
		Date dthr = getPrescricaoMedicaFacade().obterDataAltaPorInternacao(intSeq);
		if (dthr == null) {
			dthr = getPrescricaoMedicaFacade().obterDataAltaInternacaoComMotivoAlta(intSeq);
		}
		return dthr;
	}

	private AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO() {
		return ainMovimentoInternacaoDAO;
	}

	/**
	 * Estória 6950 - Imprimir controles na Transferencia de paciente RN 02 A
	 * Data Inicial que deve ser enviada como parâmetro do relatório é a data
	 * que o paciente ingressou na unidade pela última vez. O ingresso em uma
	 * unidade tanto pode ser uma internação direta como uma transferência de
	 * uma unidade para outra.
	 * 
	 * @param internacaoSeq
	 * @param seq
	 * @return
	 * 
	 */
	public Date buscarDataUltimoIngresso(Integer internacaoSeq, Short unidadeSeq) throws ApplicationBusinessException {
		AghParametros paramInternacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_INTERNACAO);
		AghParametros paramTransfSO = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_TRSF_SO);
		AghParametros paramTransfUnidade = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_TRSF_UNIDADE);
		AghParametros paramTransfClinica = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_TRSF_CLIN);
		List<Integer> listaSeqMotivos = new ArrayList<Integer>();
		listaSeqMotivos.add(paramInternacao.getVlrNumerico().intValue());
		listaSeqMotivos.add(paramTransfSO.getVlrNumerico().intValue());
		listaSeqMotivos.add(paramTransfUnidade.getVlrNumerico().intValue());
		listaSeqMotivos.add(paramTransfClinica.getVlrNumerico().intValue());
		return getAinMovimentoInternacaoDAO().buscarDataUltimoIngresso(internacaoSeq, unidadeSeq, listaSeqMotivos);
	}

	/**
	 * Melhoria #8745 Considerar parâmetro de horas para impressão de Controles
	 * na transferência do paciente
	 * 
	 * @param internacaoSeq
	 * @param unidadeSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Date buscarDataInicioRelatorio(Integer internacaoSeq, Short unidadeSeq) throws ApplicationBusinessException {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date result = null;
		Date dataUltimoIngresso = buscarDataUltimoIngresso(internacaoSeq, unidadeSeq);
		LOG.debug("TransferirPacienteON.buscarDataInicioRelatorio(): data último ingresso = [" + sdf.format(dataUltimoIngresso) + "].");
		AghParametros paramHoras = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HRS_IMP_CONTROLES_TRANSF);
		if (paramHoras == null) {
			result = dataUltimoIngresso;
			LOG.debug("TransferirPacienteON.buscarDataInicioRelatorio(): parametro P_HRS_IMP_CONTROLES_TRANSF nulo.");
		} else {
			Date agora = Calendar.getInstance().getTime();
			LOG.debug("TransferirPacienteON.buscarDataInicioRelatorio() paramhoras = [" + paramHoras.getVlrNumerico().intValue() + "].");
			Date dataAjustadaParametro = DateUtils.addHours(agora, - paramHoras.getVlrNumerico().intValue());
			LOG.debug("TransferirPacienteON.buscarDataInicioRelatorio() data ajustada parametro = [" + sdf.format(dataAjustadaParametro) + "].");
			if (dataUltimoIngresso.after(dataAjustadaParametro)) {
				result = dataUltimoIngresso;
			} else {
				result = dataAjustadaParametro;
			}
		}
		LOG.debug("TransferirPacienteON.buscarDataInicioRelatorio(): result = [" + sdf.format(result) + "].");
		return result;
	}

	private boolean unidadePossuiCaracteristicaPacControle(Short unidadeSeq) {
		return getAghuFacade().verificarCaracteristicaUnidadeFuncional(unidadeSeq, ConstanteAghCaractUnidFuncionais.CONTROLES_PACIENTE_INFORMATIZADO);
	}

	// private AghCaractUnidFuncionaisDAO getAghCaractUnidFuncionaisDAO() {
	// return aghCaractUnidFuncionaisDAO;
	// }

	public boolean deveImprimirControlesPaciente(Short unidadeOrigemSeq, Short unidadeDestinoSeq) {
		if (unidadeOrigemSeq == null || unidadeDestinoSeq == null) {
			return false;
		}
		boolean unidadeOrigemPossuiCaracteristica = unidadePossuiCaracteristicaPacControle(unidadeOrigemSeq);
		if (unidadeOrigemPossuiCaracteristica) {
			// refactor - correção issue #8950
			boolean unidadeDestinoPossuiCaracteristica = unidadePossuiCaracteristicaPacControle(unidadeDestinoSeq);
			if (!unidadeDestinoPossuiCaracteristica) {
				return true;
			}
		}
		return false;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected ILeitosInternacaoFacade getLeitosInternacaoFacade() {
		return this.leitosInternacaoFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected AinLeitosDAO getAinLeitosDAO() {
		return ainLeitosDAO;
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

	protected AinQuartosDAO getAinQuartosDAO() {
		return ainQuartosDAO;
	}

	public String atualizarSolicitacaoTransferencia(Integer prontuario, String leitoID, boolean validarTransferencia)
			throws ApplicationBusinessException {
		StringBuffer retorno = null;
		List<AinSolicTransfPacientes> solicitacoes = getLeitosInternacaoFacade().pesquisarSolicitacaoTransferenciaPorProntuario(prontuario);
		// Existe solicitação de tranferência para o prontuário?
		if (solicitacoes != null && !solicitacoes.isEmpty()) {
			for (AinSolicTransfPacientes solicitacao : solicitacoes) {
				if (DominioSituacaoSolicitacaoInternacao.A.equals(solicitacao.getIndSitSolicLeito()) && solicitacao.getLeito() != null) {
					// Validar leito informado com leito concedido
					if (leitoID.equals(solicitacao.getLeito().getLeitoID()) || validarTransferencia) {
						// Atualizar solicitação para efetuada
						solicitacao.setIndSitSolicLeito(DominioSituacaoSolicitacaoInternacao.E);
						getLeitosInternacaoFacade().persistirAinSolicTransfPacientes(solicitacao);
						if (validarTransferencia) {
							retorno = null;
						} else {
							return null;
						}
					} else {
						// Retornar true para que o usuário confirme a
						// atualização da solicitação de transferência
						if (retorno == null) {
							retorno = new StringBuffer();
						} else {
							retorno.append(", ");
						}
						retorno.append(solicitacao.getLeito().getLeitoID());
					}
				}
			}
		}
		return (retorno != null) ? retorno.toString() : null;
	}

	public List<AinLeitos> buscarLeito(String leitoID) {
		return getAinLeitosDAO().pesquisaLeitoPeloLeitoId(leitoID);
	}
}
