package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendentePrescricoesCuidados;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeCuidadoDiagnosticoId;
import br.gov.mec.aghu.model.EpePrescCuidDiagnostico;
import br.gov.mec.aghu.model.EpePrescCuidDiagnosticoId;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.EpePrescricoesCuidadosId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricaoEnfermagemDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ElaboracaoPrescricaoEnfermagemON extends BaseBusiness {

	@EJB
	private ElaboracaoPrescricaoEnfermagemRN elaboracaoPrescricaoEnfermagemRN;
	
	@EJB
	private ManutencaoPrescricaoCuidadoON manutencaoPrescricaoCuidadoON;
	
	private static final Log LOG = LogFactory.getLog(ElaboracaoPrescricaoEnfermagemON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO;
	
	@Inject
	private EpePrescCuidDiagnosticoDAO epePrescCuidDiagnosticoDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 357334283065598864L;
	
	public enum ElaboracaoPrescricaoEnfermagemONExceptionCode implements BusinessExceptionCode {
		ERRO_CLONAR_PRESCRICAO_ENFERMAGEM, 
		MAIS_DE_UM_ATENDIMENTO_ENCONTRADO, NENHUM_ATENDIMENTO_ENCONTRADO, 
		OUTRA_PRESCRICAO_EM_USO, ATENDIMENTO_NAO_ESTA_EM_ANDAMENTO, 
		ERRO_PRESCRICAO_ENFERMAGEM_TIPO_ATENDIMENTO_NAO_PERMITE_PRESCRICAO,
		ERRO_PRESCRICAO_ENFERMAGEM_PRESCRICAO_NAO_INFORMATIZADA, PRESCRICAO_NO_PASSADO,
		NAO_EH_PERMITIDO_ADIANTAR_PRESCRICAO, NAO_EH_POSSIVEL_ADIANTAR_PRESCRICAO, PRESCRICOES_SOBREPOSTAS,
		ERRO_PRESCRICAO_ENFERMAGEM_PRESCRICAO_CONSECUTIVA_NAO_ENCONTRADA,
		ATENDIMENTO_NAO_VIGENTE_PARA_PERIODO, ERRO_PRESCRICAO_ENFERMAGEM_ATENDIMENTO_PRESCRICAO_NULO,
		ERRO_PRESCRICAO_ENFERMAGEM_DT_REFERENCIA_PRESCRICAO_NULA, ERRO_PRESCRICAO_ENFERMAGEM_DT_INICIO_PRESCRICAO_NULA,
		ERRO_PRESCRICAO_ENFERMAGEM_COPIAR_PRESCRICAO_CUIDADOS, PRESCRICAO_ATUAL_EM_USO,ERRO_PRESCRICAO_ENFERMAGEM_DATA_PRESCRICAO_JA_EXISTENTE;;
	}
	
	
	private IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	
	public EpePrescricaoEnfermagem clonarPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) throws ApplicationBusinessException{
		getEpePrescricaoEnfermagemDAO().refresh(prescricaoEnfermagem);
		
		EpePrescricaoEnfermagem clonePrescricaoEnfermagem = null;
		try{
			clonePrescricaoEnfermagem = (EpePrescricaoEnfermagem) BeanUtils.cloneBean(prescricaoEnfermagem);
		} catch(Exception e){
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_CLONAR_PRESCRICAO_ENFERMAGEM);
		}
		
		
		/* #47082 e 46759 
		 if(prescricaoEnfermagem.getServidor() != null) {
			RapServidores servidor = new RapServidores();
			RapServidoresId id = new RapServidoresId();
			id.setMatricula(prescricaoEnfermagem.getServidor().getId().getMatricula());
			id.setVinCodigo(prescricaoEnfermagem.getServidor().getId().getVinCodigo());
			servidor.setId(id);
			clonePrescricaoEnfermagem.setServidor(servidor); 
		}
		
		if(prescricaoEnfermagem.getServidorAtualizada() != null) {
			RapServidores servidor = new RapServidores();
			RapServidoresId id = new RapServidoresId();
			id.setMatricula(prescricaoEnfermagem.getServidorAtualizada().getId().getMatricula());
			id.setVinCodigo(prescricaoEnfermagem.getServidorAtualizada().getId().getVinCodigo());
			servidor.setId(id);
			clonePrescricaoEnfermagem.setServidorAtualizada(servidor); 
		}
		
		if(prescricaoEnfermagem.getServidorValida() != null) {
			RapServidores servidor = new RapServidores();
			RapServidoresId id = new RapServidoresId();
			id.setMatricula(prescricaoEnfermagem.getServidorValida().getId().getMatricula());
			id.setVinCodigo(prescricaoEnfermagem.getServidorValida().getId().getVinCodigo());
			servidor.setId(id);
			clonePrescricaoEnfermagem.setServidorValida(servidor); 
		}
		
		if(prescricaoEnfermagem.getAtendimento() != null) {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq(prescricaoEnfermagem.getAtendimento().getSeq());
			clonePrescricaoEnfermagem.setAtendimento(atendimento); 
		}*/
	
		return clonePrescricaoEnfermagem;
	}
	
	/**
	 * Busca atendimento atual do paciente através do número do prontuário
	 * 
	 * @param prontuario
	 * @return atendimento
	 */
	public List<AghAtendimentos> obterAtendimentoAtualPorProntuario(final Integer prontuario) {
		AipPacientes paciente = getPacienteFacade().obterPacientePorProntuario(prontuario);
		return getAghuFacade().pesquisarAtendimentoVigentePrescricaoEnfermagem(paciente);
	}
	
	/**
	 * Busca atendimento atual do paciente através do id do leito em que está
	 * internado. No caso de inconsistência do banco, mais de um registro 
	 * serão retornados.
	 * 
	 * @param leitoId
	 * @return atendimento
	 * @throws ApplicationBusinessException 
	 */
	public AghAtendimentos obterAtendimentoAtualPorLeitoId(final String leitoId) throws ApplicationBusinessException {
		AinLeitos leito = getCadastrosBasicosInternacaoFacade().obterLeitoPorId(leitoId);
		List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisarAtendimentoVigentePrescricaoEnfermagemPorLeito(leito);
		if (listaAtendimentos.size() == 1){
			return listaAtendimentos.get(0);
		}
		
		if (listaAtendimentos.size() > 1){
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.MAIS_DE_UM_ATENDIMENTO_ENCONTRADO, leitoId);
		}
		
		throw new ApplicationBusinessException(
				ElaboracaoPrescricaoEnfermagemONExceptionCode.NENHUM_ATENDIMENTO_ENCONTRADO, leitoId);		
	}
	
	/**
	 * Busca prescrições médicas de um atendimento ainda não encerradas (data e
	 * hora fim maior que data e hora atual)
	 * 
	 * @param atdSeq
	 * @param dataAtual
	 * @return lista de prescrições médicas
	 */
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoEnfermagemNaoEncerradaPorAtendimento(
			final Integer atdSeq, final Date dataAtual) {
		List<EpePrescricaoEnfermagem> temp = getEpePrescricaoEnfermagemDAO().pesquisarPrescricaoAtendimentoDataFim(atdSeq, dataAtual);
		for(EpePrescricaoEnfermagem t : temp){
			getEpePrescricaoEnfermagemDAO().refresh(t);
		}
		return temp;
	}
	
	/**
	 * Obtém data de referência para a próxima prescrição de enfermagem
	 * 
	 * @param atdSeq
	 * @param dataAtual
	 * @throws ApplicationBusinessException
	 * 
	 */
	public Date obterDataReferenciaProximaPrescricao(final AghAtendimentos atendimento, final Date dataAtual) throws ApplicationBusinessException {
		if (atendimento == null) {
			throw new IllegalArgumentException("Atendimento não informado.");
		}
		
		if (dataAtual == null) {
			throw new IllegalArgumentException("Data não informada.");
		}
		
		boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.PEN_INFORMATIZADA);
		if (!possuiCaracteristica) {
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_PRESCRICAO_NAO_INFORMATIZADA);
		}
		
		Date dataReferencia;
		List<EpePrescricaoEnfermagem> prescricoes;
		prescricoes = getEpePrescricaoEnfermagemDAO()
			.pesquisarPrescricaoAtendimentoDataFim(atendimento.getSeq(), dataAtual);
		if (prescricoes.size() > 0) {
			dataReferencia = prescricoes.get(prescricoes.size()-1).getDthrFim();
		} else {
			Date horaValidadePen = atendimento.getUnidadeFuncional().getHrioValidadePen();
			Date dataTrocaPrescricao = DateUtil.comporDiaHora(dataAtual, horaValidadePen);
			if (dataAtual.compareTo(dataTrocaPrescricao) < 0) {
				dataReferencia = DateUtil.adicionaDias(dataAtual, -1);
			} else {
				dataReferencia = dataAtual;
			}
		}
		return DateUtils.truncate(dataReferencia, Calendar.DAY_OF_MONTH);
	}
	
	protected EpePrescricaoEnfermagemDAO getEpePrescricaoEnfermagemDAO() {
		return epePrescricaoEnfermagemDAO;
	}
	
	private Date getDataAtual() {
		return new Date();
	}
	
	public void verificarCriarPrescricao(
			final AghAtendimentos atendimento
		) throws ApplicationBusinessException {
		verificarCriarPrescricao(atendimento, getDataAtual());
	}
	
	
	private void verificarCriarPrescricao (
		final AghAtendimentos atendimento,
		Date dataAtual
	) throws ApplicationBusinessException {
		// TODO: trigger de presc enf. ?
		verificarAtendimento(atendimento);
		
		// Prescricao informatizada
		boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.PEN_INFORMATIZADA);
		if (!possuiCaracteristica) {
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_PRESCRICAO_NAO_INFORMATIZADA);
		}
		
		validarOutraPrescricaoEmUso(atendimento, null, dataAtual);
	}	
	
	/**
	 * Verifica se o atendimento está em andamento e permite internação
	 * 
	 * @param atendimento
	 * @throws ApplicationBusinessException
	 *             ATENDIMENTO_NAO_ESTA_EM_ANDAMENTO,
	 *             TIPO_ATENDIMENTO_NAO_PERMITE_PRESCRICAO
	 */
	public void verificarAtendimento(AghAtendimentos atendimento)
			throws ApplicationBusinessException {
		if (!DominioPacAtendimento.S.equals(atendimento.getIndPacAtendimento())) {
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.ATENDIMENTO_NAO_ESTA_EM_ANDAMENTO);
		}
		if (!atendimento.getOrigem().permitePrescricao()) {
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_TIPO_ATENDIMENTO_NAO_PERMITE_PRESCRICAO);
		}
	}	
	
	/**
	 * Valida se não há uma outra prescrição em uso para o mesmo atendimento, o
	 * que inviabiliza criação ou edição de outras prescrições.
	 * 
	 * @param atendimento
	 * @param prescricaoDesconsiderar
	 * @param dataAtual
	 * @throws ApplicationBusinessException
	 */
	private void validarOutraPrescricaoEmUso(final AghAtendimentos atendimento,
			EpePrescricaoEnfermagem prescricaoDesconsiderar, Date dataAtual)
			throws ApplicationBusinessException {
		EpePrescricaoEnfermagem prescricaoEmUso = obterPrescricaoEmUso(atendimento,
				prescricaoDesconsiderar, dataAtual);
		if (prescricaoEmUso != null) {
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.OUTRA_PRESCRICAO_EM_USO
			);
		}
	}
	
	/**
	 * Verifica se existe alguma prescrição com situação "em uso"
	 * 
	 * @param atendimento
	 * @param prescricaoDesconsiderar
	 * @param dataAtual
	 */
	private EpePrescricaoEnfermagem obterPrescricaoEmUso(
			final AghAtendimentos atendimento,
			EpePrescricaoEnfermagem prescricaoDesconsiderar, Date dataAtual) {
		
		List<EpePrescricaoEnfermagem> listaPrescricao;
		listaPrescricao = getEpePrescricaoEnfermagemDAO().pesquisarPrescricaoAtendimentoDataFim(
				atendimento.getSeq(), dataAtual);
		
		for (EpePrescricaoEnfermagem prescricao : listaPrescricao) {
			if (prescricao.equals(prescricaoDesconsiderar)) {
				continue;
			}
			if (DominioSituacaoPrescricao.U.equals(prescricao.getSituacao())) {
				return prescricao;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * Cria uma nova prescrição de enfermagem para um atendimento
	 * para a data informada (opcional)
	 * 
	 * @param atendimento
	 * @param data de referência
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public EpePrescricaoEnfermagem criarPrescricao(
			final AghAtendimentos atendimento, Date dataReferencia)	throws BaseException {

		if (atendimento == null) {
			throw new IllegalArgumentException("Atendimento não informado.");
		}

		// Serializa criação de prescrições sobre o mesmo atendimento
		getAghuFacade().refreshAndLock(atendimento);
		
		AghUnidadesFuncionais unidadeFuncional = atendimento.getUnidadeFuncional();
		getAghuFacade().refreshUnidadesFuncionais(unidadeFuncional);

		// Utiliza a mesma data durante toda a transação
		Date dataAtual = new Date();
		
		// TODO: tem uma chamada para verificarAtendimento - Trigger
		verificarCriarPrescricao(atendimento, dataAtual);

		// Calcula data de início e data de fim
		if (dataReferencia == null) {
			dataReferencia = obterDataReferenciaProximaPrescricao(atendimento,
					dataAtual);
		}

		Date horaValidadePen = unidadeFuncional.getHrioValidadePen();

		Date dthrFim = DateUtil.comporDiaHora(DateUtil.adicionaDias(dataReferencia, +1), horaValidadePen);

		Date dthrInicio = (Date) ObjectUtils.max(DateUtil.adicionaDias(dthrFim,-1), dataAtual);
		
		List<EpePrescricaoEnfermagem> listaPrescricao;
		listaPrescricao = getEpePrescricaoEnfermagemDAO()
			.pesquisarPrescricaoAtendimentoDataFim(atendimento.getSeq(), dataAtual);
		
		EpePrescricaoEnfermagem ultimaPrescricaoDia = obterUltimaPrescricaoDoDia(
				listaPrescricao, dataReferencia
		);
		
		if (ultimaPrescricaoDia != null && ultimaPrescricaoDia.getDthrFim().compareTo(dthrInicio) < 0) {
			// Isto ocorre quando há alteração na hora de validade da prescição (geralmente por troca de unidade funcional)
			// Neste caso, deve-se criar uma prescrição para preencher o horário entre o fim da prescrição anterior e o que 
			// seria o início da nova prescrição.
			dthrFim = dthrInicio;
			dthrInicio = ultimaPrescricaoDia.getDthrFim();
		}
		
		if (dthrFim.compareTo(dataAtual) < 0) {
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.PRESCRICAO_NO_PASSADO);
		}

		// TODO: deixar conforme trigger de presc enf. ?
		validarAdiantamento(unidadeFuncional, dataAtual, dthrInicio);

		// TODO: deixar conforme trigger de presc enf. ?
		// Inicio
		// Validar se já existe prescrição neste período
		for (EpePrescricaoEnfermagem prescricao : listaPrescricao) {
			if (prescricao.getDthrFim().compareTo(dthrFim) < 0
					&& prescricao.getDthrFim().compareTo(dthrInicio) > 0) {
				dthrInicio = prescricao.getDthrFim();
			}
			if (prescricao.getDthrInicio().compareTo(dthrFim) < 0
					&& prescricao.getDthrInicio().compareTo(dthrInicio) > 0) {
				dthrFim = prescricao.getDthrInicio();
			}
			if (prescricao.getDthrInicio().compareTo(dthrInicio) >= 0
					&& prescricao.getDthrFim().compareTo(dthrFim) <= 0) {
				throw new ApplicationBusinessException(
						ElaboracaoPrescricaoEnfermagemONExceptionCode.PRESCRICOES_SOBREPOSTAS);
			}
		}
		if (dthrInicio.compareTo(dthrFim) >= 0) {
			throw new ApplicationBusinessException(ElaboracaoPrescricaoEnfermagemONExceptionCode.PRESCRICOES_SOBREPOSTAS);
		}
		
		// Verificar Prescrição Consecutiva
		boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.PEN_CONSECUTIVA);
		if (possuiCaracteristica && ultimaPrescricaoDia == null && dthrInicio.compareTo(dataAtual) > 0) {
				throw new ApplicationBusinessException(ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_PRESCRICAO_CONSECUTIVA_NAO_ENCONTRADA);
		}		
		// Fim
		

		// TODO: deixar conforme trigger de presc enf. ?
		verificarVigenciaAtendimento(atendimento, dthrFim, dthrInicio);

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Tudo ok. Cria nova prescrição.
		EpePrescricaoEnfermagem novaPrescricao = new EpePrescricaoEnfermagem();
		novaPrescricao.setId(new EpePrescricaoEnfermagemId(atendimento.getSeq(), 
				getEpePrescricaoEnfermagemDAO().obterValorSequencialId()));

		novaPrescricao.setCriadoEm(dataAtual);
		novaPrescricao.setDthrMovimento(dataAtual);
		novaPrescricao.setDthrInicioMvtoPendente(dataAtual);
		novaPrescricao.setSituacao(DominioSituacaoPrescricao.U);
		novaPrescricao.setServidor(servidorLogado);
		novaPrescricao.setServidorAtualizada(servidorLogado);
		novaPrescricao.setDthrInicio(dthrInicio);
		novaPrescricao.setDthrFim(dthrFim);
		novaPrescricao.setDtReferencia(dataReferencia);
		novaPrescricao.setAtendimento(atendimento);
		
		EpePrescricaoEnfermagem prescricaoEnfAnterior = obterUltimaPrescricaoAtendimento(
				novaPrescricao.getId().getAtdSeq(), novaPrescricao.getDtReferencia(), novaPrescricao.getDthrInicio());

		this.inserirPrescricaoEnfermagem(novaPrescricao, true);
		
		

		// Copiar itens da prescrição anterior
		copiarPrescricao(prescricaoEnfAnterior, novaPrescricao);

		super.flush();

		gerarLogPrescricaoEmUso(novaPrescricao, "Inclusão");
		
		// TODO: descomentar linha abaixo quando for implementado o interfaceamento com o módulo de Faturamento
//		getPrescricaoMedicaRN().atualizarFaturamento(novaPrescricao, null, DominioOperacaoBanco.INS);

		return novaPrescricao;
	}
	
	/**
	 * Obtem a última prescrição que encerra no dia informado
	 * @param prescricoes é a lista de prescrições retornadas por  
	 *        getPrescricaoMedicaDAO().prescricoesAtendimentoDataFim
	 *        Considera-se que a lista esteja ordenada por dataFim
	 * @param data considera apenas o dia, ignorando a hora
	 * @return prescricao com maior dataFim dentro do dia informado  
	 *         null caso não exista prescrição encerrando neste dia
	 */
	private EpePrescricaoEnfermagem obterUltimaPrescricaoDoDia(
			List<EpePrescricaoEnfermagem> listaPrescricao, Date data
	) {
		EpePrescricaoEnfermagem ultimaPrescricao = null;
		for (EpePrescricaoEnfermagem prescricao : listaPrescricao) {
			if (DateValidator.validarMesmoDia(prescricao.getDthrFim(), data)) {
				ultimaPrescricao = prescricao;
			}
		}
		return ultimaPrescricao;
	}
	
	/**
	 * Valida adiantamento da prescrição. A prescrição não pode ser adiantada
	 * mais do que o informado no parâmetro em sua unidade funcional. 
	 * 
	 * @param unidadeFuncional
	 * @param dataAtual
	 * @param dthrInicio
	 * @throws ApplicationBusinessException
	 */
	private void validarAdiantamento(AghUnidadesFuncionais unidadeFuncional,
			Date dataAtual, Date dthrInicio) throws ApplicationBusinessException {
		Short quantidade = unidadeFuncional.getNroUnidTempoPenAdiantadas();
		if (quantidade == null || quantidade == 0) {
			if (dthrInicio.compareTo(dataAtual) > 0) {
				throw new ApplicationBusinessException(
						ElaboracaoPrescricaoEnfermagemONExceptionCode.NAO_EH_PERMITIDO_ADIANTAR_PRESCRICAO);
			}
		} else {
			Calendar dataInicioMaxima = Calendar.getInstance();
			dataInicioMaxima.setTime(dataAtual);
			int field = 0;
			switch (unidadeFuncional.getIndUnidTempoPenAdiantada()) {
			case D:
				field = Calendar.DAY_OF_MONTH;
				break;
			case H:
				field = Calendar.HOUR_OF_DAY;
				break;
			}
			dataInicioMaxima.add(field, quantidade);
			if (dthrInicio.compareTo(dataInicioMaxima.getTime()) > 0) {
				throw new ApplicationBusinessException(
						ElaboracaoPrescricaoEnfermagemONExceptionCode.NAO_EH_POSSIVEL_ADIANTAR_PRESCRICAO,
						quantidade, field == Calendar.DAY_OF_MONTH ? "dias"
								: "horas");
			}
		}
	}	
	
	/**
	 * Verifica se atendimento é vigente para o período informado
	 * 
	 * @param atendimento
	 * @param dthrFim
	 * @param dthrInicio
	 * @throws ApplicationBusinessException
	 *             ATENDIMENTO_NAO_VIGENTE_PARA_PERIODO
	 */
	public void verificarVigenciaAtendimento(
			final AghAtendimentos atendimento, Date dthrFim, Date dthrInicio)
			throws ApplicationBusinessException {
		if (dthrInicio.compareTo(atendimento.getDthrInicio()) < 0
				|| dthrFim.compareTo(atendimento.getDthrInicio()) < 0
				|| atendimento.getDthrFim() != null
				&& (dthrInicio.compareTo(atendimento.getDthrFim()) > 0 || dthrFim
						.compareTo(atendimento.getDthrFim()) > 0)) {
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.ATENDIMENTO_NAO_VIGENTE_PARA_PERIODO);
		}
	}
	
	/**
	 * Método que retorna a prescrição mais recente de um atendimento
	 * @param atdSeq
	 * @param dtReferencia
	 * @param dataInicio
	 * @return
	 */
	public EpePrescricaoEnfermagem obterUltimaPrescricaoAtendimento(Integer atdSeq, Date dtReferencia, Date dataInicio){
		return getEpePrescricaoEnfermagemDAO().obterUltimaPrescricaoAtendimento(atdSeq, dtReferencia, dataInicio);
	}	
	
	/**
	 * Método que atualiza a prescrição recém criada com as informações da
	 * prescrição anterior mais recente
	 * 
	 * @param atdSeq
	 * @param dtReferencia
	 * @param dataInicio
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	public void copiarPrescricao(EpePrescricaoEnfermagem prescricaoEnfAnterior, EpePrescricaoEnfermagem prescricaoEnfNova) throws BaseException {
		// Obtém dados da prescrição nova (ESTES ATRIBUTOS PRECISAM ESTAR
		// SETADOS NO OBJETO)
		Integer penAtdSeq = prescricaoEnfNova.getId().getAtdSeq();
		Date dtReferencia = prescricaoEnfNova.getDtReferencia();
		Date dataInicio = prescricaoEnfNova.getDthrInicio();
		Date dataFim = prescricaoEnfNova.getDthrFim();

		if (penAtdSeq == null) {
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_ATENDIMENTO_PRESCRICAO_NULO);
		}
		if (dtReferencia == null) {
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_DT_REFERENCIA_PRESCRICAO_NULA);
		}
		if (dataInicio == null) {
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_DT_INICIO_PRESCRICAO_NULA);
		}

		Long nroDias = Long.valueOf(0);

		if (prescricaoEnfAnterior != null && prescricaoEnfAnterior.getDthrInicio() != null) {
			Date dtInicioLida = prescricaoEnfAnterior.getDthrInicio();
			if (dataInicio.compareTo(dtInicioLida) != 0) {
				nroDias = (dtReferencia.getTime() - dtInicioLida.getTime()) / 86400000;
				if (nroDias == 0) {
					nroDias = Long.valueOf(1);
				}
				this.copiarItensPrescricao(prescricaoEnfNova,
						prescricaoEnfAnterior, dataInicio, dataFim,
						nroDias);
			}
		}
	}
	
	/**
	 * Método que copia e atualiza a lista de cuidados da prescrição
	 * 
	 * @param dataFim
	 * @param dtInicio
	 * @param dtFimLida
	 * @param pmeAtdSeq
	 * @param prescricaoEnfNova
	 * @throws BaseException
	 * 
	 */
	protected void copiarCuidadosPrescricao(Date dataFim, Date dtInicio,
			EpePrescricaoEnfermagem prescricaoEnfNova, EpePrescricaoEnfermagem prescricaoEnfAnterior)
			throws BaseException {
		EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO = getEpePrescricoesCuidadosDAO();
		EpePrescCuidDiagnosticoDAO epePrescCuidDiagnosticoDAO = getEpePrescCuidDiagnosticoDAO();
		List<EpePrescricoesCuidados> listaCuidados = epePrescricoesCuidadosDAO
			.pesquisarCuidadosUltimaPrescricao(prescricaoEnfAnterior);
		
		try{
			for (EpePrescricoesCuidados prescCuidado : listaCuidados){
				// Insere um novo cuidado de prescrição (cópia do cuidado da prescrição anterior)
				EpePrescricoesCuidados novoPrescCuidado = 
						this.inserirPrescricaoCuidado(prescricaoEnfNova, prescCuidado, dtInicio, dataFim);
				// Insere a cópia do diagnostico para o cuidado
				List<EpePrescCuidDiagnostico> listaPrescCuidDiagnostico = 
						epePrescCuidDiagnosticoDAO.listarPrescCuidDiagnosticoPorPrescricaoCuidado(prescCuidado);
				if (!listaPrescCuidDiagnostico.isEmpty()) {
					EpePrescCuidDiagnostico prescCuidDiagnostico = (EpePrescCuidDiagnostico) listaPrescCuidDiagnostico.get(0);
					EpePrescCuidDiagnostico novoPrescCuidDiagnostico = clonarPrescCuidDiagnostico(prescCuidDiagnostico);
					EpePrescCuidDiagnosticoId prescCuidDiagnosticoId = novoPrescCuidDiagnostico.getId();
					prescCuidDiagnosticoId.setPrcSeq(novoPrescCuidado.getId().getSeq());
					prescCuidDiagnosticoId.setPrcAtdSeq(prescricaoEnfNova.getAtendimento().getSeq());
					novoPrescCuidDiagnostico.setId(prescCuidDiagnosticoId);
					novoPrescCuidDiagnostico.setCuidadoDiagnostico(prescCuidDiagnostico.getCuidadoDiagnostico());
					novoPrescCuidDiagnostico.setPrescricaoCuidado(novoPrescCuidado);
					epePrescCuidDiagnosticoDAO.desatachar(prescCuidDiagnostico);
					getEpePrescCuidDiagnosticoDAO().persistir(novoPrescCuidDiagnostico);
				}
			}
		} catch (BaseException e) {
			logError("Exceção BaseException capturada, lançada para cima.");
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_COPIAR_PRESCRICAO_CUIDADOS, e);
		}
	}
	
	/**
	 * Metodo para clonar uma entidade da classe EpePrescCuidDiagnostico 
	 * @param EpePrescCuidDiagnostico
	 * @return EpePrescCuidDiagnostico clonado.
	 *  
	 * @throws Exception
	 */
	public EpePrescCuidDiagnostico clonarPrescCuidDiagnostico(EpePrescCuidDiagnostico prescCuidDiagnostico) 
			throws ApplicationBusinessException{
		EpePrescCuidDiagnostico clonePrescCuidDiagnostico = null;
		try{
			clonePrescCuidDiagnostico = (EpePrescCuidDiagnostico) BeanUtils.cloneBean(prescCuidDiagnostico);
		} catch(Exception e){
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_COPIAR_PRESCRICAO_CUIDADOS);
		}
		
		if (prescCuidDiagnostico.getId() !=  null) {
			EpePrescCuidDiagnosticoId id = new EpePrescCuidDiagnosticoId();
			id = new EpePrescCuidDiagnosticoId(
					prescCuidDiagnostico.getId().getPrcAtdSeq(),
					prescCuidDiagnostico.getId().getPrcSeq(),
					prescCuidDiagnostico.getId().getCdgFdgDgnSnbGnbSeq(),
					prescCuidDiagnostico.getId().getCdgFdgDgnSnbSequencia(),
					prescCuidDiagnostico.getId().getCdgFdgDgnSequencia(),
					prescCuidDiagnostico.getId().getCdgFdgFreSeq(),
					prescCuidDiagnostico.getId().getCdgCuiSeq());
			
			clonePrescCuidDiagnostico.setId(id); 
		}
		
		if (prescCuidDiagnostico.getCuidadoDiagnostico() != null) {
			EpeCuidadoDiagnostico cuidadoDiagnostico = new EpeCuidadoDiagnostico();
			cuidadoDiagnostico.setId(new EpeCuidadoDiagnosticoId(
					prescCuidDiagnostico.getCuidadoDiagnostico().getId().getFdgDgnSnbGnbSeq(), 
					prescCuidDiagnostico.getCuidadoDiagnostico().getId().getFdgDgnSnbSequencia(),
					prescCuidDiagnostico.getCuidadoDiagnostico().getId().getFdgDgnSequencia(),
					prescCuidDiagnostico.getCuidadoDiagnostico().getId().getFdgFreSeq(),
					prescCuidDiagnostico.getCuidadoDiagnostico().getId().getCuiSeq()));
					
			clonePrescCuidDiagnostico.setCuidadoDiagnostico(cuidadoDiagnostico); 
		}
		
		if (prescCuidDiagnostico.getPrescricaoCuidado() != null) {
			EpePrescricoesCuidados prescricaoCuidado = new EpePrescricoesCuidados();
			prescricaoCuidado.setId(new EpePrescricoesCuidadosId(
					prescCuidDiagnostico.getPrescricaoCuidado().getPrescricaoEnfermagem().getAtendimento().getSeq(),
					prescCuidDiagnostico.getPrescricaoCuidado().getId().getSeq()));
			
			clonePrescCuidDiagnostico.setPrescricaoCuidado(prescricaoCuidado); 
		}
		
		clonePrescCuidDiagnostico.setVersion(0);
		
		return clonePrescCuidDiagnostico;
	}	
	
	/**
	 * Método que atualiza os itens da prescrição
	 * 
	 * @param dtFimLida
	 * @param nroDias
	 * @param dtInicioLida
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	public void copiarItensPrescricao(EpePrescricaoEnfermagem prescricaoEnfNova,
			EpePrescricaoEnfermagem prescricaoEnfAnterior, Date dataInicio,
			Date dataFim, Long nroDias) throws BaseException {
		
		// Copia os cuidados da prescrição
		this.copiarCuidadosPrescricao(dataFim, dataInicio, prescricaoEnfNova, prescricaoEnfAnterior);
	}
	
	/**
	 * Método que insere cuidados em uma nova prescrição
	 * 
	 * @param prescricaoEnfermagem
	 * @param cuidadoAntigo
	 * @param dtInicio
	 * @param dataFim
	 * @param pcuSeqNovo
	 * @throws BaseException 
	 */
	@SuppressWarnings("deprecation")
	private EpePrescricoesCuidados inserirPrescricaoCuidado(
			EpePrescricaoEnfermagem prescricaoEnfermagem,
			EpePrescricoesCuidados cuidadoAntigo, Date dtInicio, Date dataFim) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Obtém o próximo sequencial
		Integer seqNovo = getEpePrescricoesCuidadosDAO().obterValorSequencialId();
		
		/*----- Monta o novo cuidado de prescrição -----*/
		EpePrescricoesCuidados prescricaoCuidadoNovo = new EpePrescricoesCuidados();
		// Monta id
		EpePrescricoesCuidadosId epePrescricaoCuidadosId = new EpePrescricoesCuidadosId();
		epePrescricaoCuidadosId.setAtdSeq(prescricaoEnfermagem.getId().getAtdSeq());
		epePrescricaoCuidadosId.setSeq(seqNovo);
		// Seta o id
		prescricaoCuidadoNovo.setId(epePrescricaoCuidadosId);
		// Seta a referência para a Prescrição de Enfermagem criada
		prescricaoCuidadoNovo.setPrescricaoEnfermagem(prescricaoEnfermagem);
		// Seta os demais campos do cuidado anterior
		prescricaoCuidadoNovo.setTipoFrequenciaAprazamento(cuidadoAntigo.getTipoFrequenciaAprazamento());
		prescricaoCuidadoNovo.setPendente(DominioIndPendentePrescricoesCuidados.P);
		prescricaoCuidadoNovo.setDthrInicio(dtInicio);
		prescricaoCuidadoNovo.setFrequencia(cuidadoAntigo.getFrequencia());
		prescricaoCuidadoNovo.setDescricao(cuidadoAntigo.getDescricao());
		prescricaoCuidadoNovo.setDthrFim(dataFim);
		prescricaoCuidadoNovo.setServidor(servidorLogado);
		prescricaoCuidadoNovo.setCriadoEm(new Date());
		prescricaoCuidadoNovo.setCuidado(cuidadoAntigo.getCuidado());
		
		// Persiste a prescrição de cuidado
		return getManutencaoPrescricaoCuidadoON().inserirPrescricaoCuidado(prescricaoCuidadoNovo, "");
	}
	
	/**
	 * Verifica se é possível editar a prescrição de enfermagem. Não pode haver outra
	 * prescrição em uso para o mesmo atendimento. Marca a prescrição como em
	 * uso para o usuário atual.
	 * 
	 * @param prescricaoEnfermagemNew
	 * @param cienteEmUso
	 *            indica se o usuário já está ciente no caso da prescrição está
	 *            em uso.
	 * @throws ApplicationBusinessException 
	 * @exception OUTRA_PRESCRICAO_EM_USO
	 */
	public void editarPrescricao(final EpePrescricaoEnfermagem prescricaoEnfermagemOld, 
			final EpePrescricaoEnfermagem prescricaoEnfermagemNew, final boolean cienteEmUso) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghAtendimentos atendimento = prescricaoEnfermagemNew.getAtendimento();

		getAghuFacade().refreshAndLock(atendimento);
		
		getEpePrescricaoEnfermagemDAO().refreshAndLock(prescricaoEnfermagemNew);		

		// utiliza a mesma data durante toda a transação
		Date dataAtual = getDataAtual();
		
		if (!DominioSituacaoPrescricao.U.equals(prescricaoEnfermagemNew.getSituacao())){
			validarOutraPrescricaoEmUso(atendimento, prescricaoEnfermagemNew, dataAtual);			
		}

		if (!cienteEmUso && !DominioSituacaoPrescricao.L.equals(prescricaoEnfermagemOld.getSituacao())) {
			validarPrescricaoEmUso(prescricaoEnfermagemNew);
		}

		// Se está 'L'ivre e vai para em 'U'so, atualiza o movimento
		if (DominioSituacaoPrescricao.L.equals(prescricaoEnfermagemNew.getSituacao())) {
			prescricaoEnfermagemNew.setDthrMovimento(dataAtual);
		}
		prescricaoEnfermagemNew.setSituacao(DominioSituacaoPrescricao.U);
		
		prescricaoEnfermagemNew.setServidorAtualizada(servidorLogado);

		super.flush();
		
		gerarLogPrescricaoEmUso(prescricaoEnfermagemNew, "Edição");
		
		this.atualizarPrescricaoEnfermagem(prescricaoEnfermagemOld, prescricaoEnfermagemNew, true);
	}	
	
	/**
	 * Verifica se a prescrição está em uso. Se for o caso, lança uma exceção de
	 * forma a alertar o usuário. Após o usuários tomar ciencia da condição da
	 * prescrição, esta validação não é mais realizada.
	 * 
	 * @param prescricao
	 * @exception PRESCRICAO_ATUAL_EM_USO
	 *            
	 */
	private void validarPrescricaoEmUso(final EpePrescricaoEnfermagem prescricao)
			throws BaseException {
		if (DominioSituacaoPrescricao.U.equals(prescricao.getSituacao())) {
			RapServidores servidor;
			if (prescricao.getServidorAtualizada() == null) {
				servidor = prescricao.getServidor(); 
			} else {
				servidor = prescricao.getServidorAtualizada();
			}
			throw new ApplicationBusinessException(ElaboracaoPrescricaoEnfermagemONExceptionCode.PRESCRICAO_ATUAL_EM_USO, 
					new SimpleDateFormat("dd/MM/yyyy HH:mm").format(
							prescricao.getCriadoEm()
					),
					servidor.getPessoaFisica().getNome() 
			);
		}
	}	
	
	/**
	 * Imprime no log da aplicação as informações da
	 * prescrição e qual operação está sendo realizada sobre a mesma.
	 * 
	 * @param prescricaoEnfermagem
	 * @param operacao
	 */
	private void gerarLogPrescricaoEmUso(EpePrescricaoEnfermagem prescricaoEnfermagem, String operacao) {
		if (prescricaoEnfermagem == null || operacao == null || operacao.isEmpty()) {
			throw new IllegalArgumentException("Parâmetro 'prescricaoEnfermagem' ou 'operacao' não informado.");
		}
		
		StringBuilder msgLog = new StringBuilder(operacao);
		msgLog.append(" da Prescrição de Enfermagem");
		if (prescricaoEnfermagem.getId() != null) {
			msgLog.append(" [SEQ = ").append(prescricaoEnfermagem.getId().getSeq()).append(']')
			.append(" [ATD_SEQ = ").append(prescricaoEnfermagem.getId().getAtdSeq()).append(']');
		}
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");        
		msgLog.append(" [CRIADO_EM = ").append(df.format(prescricaoEnfermagem.getCriadoEm())).append(']')	
		.append(" [DT_REFERENCIA = ").append(df.format(prescricaoEnfermagem.getDtReferencia())).append(']')
		.append(" [IND_SITUACAO = ").append(prescricaoEnfermagem.getSituacao()).append(']')
		.append(" [SER_MATRICULA = ").append(prescricaoEnfermagem.getServidor().getId().getMatricula()).append(']')
		.append(" [SER_VIN_CODIGO = ").append(prescricaoEnfermagem.getServidor().getId().getVinCodigo()).append(']');
		
		logInfo(msgLog);		
	}	

	public void inserirPrescricaoEnfermagem(
			EpePrescricaoEnfermagem prescricaoEnfermagem, boolean flush) throws BaseException {
		this.getElaboracaoPrescricaoEnfermagemRN().inserirPrescricaoEnfermagem(
				prescricaoEnfermagem, flush);
	}

	public EpePrescricaoEnfermagem atualizarPrescricaoEnfermagem(
			EpePrescricaoEnfermagem prescricaoEnfermagemOld,
			EpePrescricaoEnfermagem prescricaoEnfermagemNew, boolean flush) throws BaseException {
		return this.getElaboracaoPrescricaoEnfermagemRN()
				.atualizarPrescricaoEnfermagem(prescricaoEnfermagemOld,
						prescricaoEnfermagemNew, flush);
	}
	
	protected ElaboracaoPrescricaoEnfermagemRN getElaboracaoPrescricaoEnfermagemRN(){
		return elaboracaoPrescricaoEnfermagemRN;
	}
	
	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO() {
		return epePrescricoesCuidadosDAO;
	}
	
	protected ManutencaoPrescricaoCuidadoON getManutencaoPrescricaoCuidadoON() {
		return manutencaoPrescricaoCuidadoON;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected EpePrescCuidDiagnosticoDAO getEpePrescCuidDiagnosticoDAO() {
		return epePrescCuidDiagnosticoDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	

	public void validarDataPrescricao(Date dtPrescricao, List<EpePrescricaoEnfermagem> listaPrescricaoEnfermagem) throws ApplicationBusinessException  {
		
		if(!listaPrescricaoEnfermagem.isEmpty()){
			Date dataUltimaPrescricao = listaPrescricaoEnfermagem.get(listaPrescricaoEnfermagem.size()-1).getDthrInicio();
			
			DateUtil.obterDataFormatada(dataUltimaPrescricao, "dd/MM/yyyy");
			DateUtil.obterDataFormatada(dtPrescricao, "dd/MM/yyyy");
			
			Date dataReferencia = listaPrescricaoEnfermagem.get(listaPrescricaoEnfermagem.size()-1).getDtReferencia();
			
		if(dtPrescricao.before(dataReferencia)){
			throw new ApplicationBusinessException(
						ElaboracaoPrescricaoEnfermagemONExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_DATA_PRESCRICAO_JA_EXISTENTE);
			}
		
		}		
		
	}	
		
}
