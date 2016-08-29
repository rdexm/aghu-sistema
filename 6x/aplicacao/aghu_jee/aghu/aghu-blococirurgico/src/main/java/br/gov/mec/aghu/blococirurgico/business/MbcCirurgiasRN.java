package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.MbcControleEscalaCirurgicaRN.MbcControleEscalaCirurgicaRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEscalaProfUnidCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioMomentoAgendamento;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEscalaProfUnidCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSolicitacaoCirurgiaPosEscala;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.util.AghuEnumUtils;

/**
 * Classe responsável por prover os métodos de negócio de MbcCirurgias.
 * 
 * @autor fwinck
 * 
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength" })
@Stateless
public class MbcCirurgiasRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcCirurgiasRN.class);

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcEscalaProfUnidCirgDAO mbcEscalaProfUnidCirgDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;


	@EJB
	private IInternacaoFacade iInternacaoFacade;

	@EJB
	private MbcCirurgiasAjusteValoresRN mbcCirurgiasAjusteValoresRN;

	@EJB
	private ISolicitacaoExameFacade iSolicitacaoExameFacade;
	
	@EJB
	private MbcAtualizacaoProfissionalCirurgiaRN mbcAtualizacaoProfissionalCirurgiaRN;

	@EJB
	private MbcSolicitacaoCirurgiaPosEscalaRN mbcSolicitacaoCirurgiaPosEscalaRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private FatPaTuTrCnVmBcRN fatPaTuTrCnVmBcRN;

	@EJB
	private MbcCirurgiasVerificacoesRN mbcCirurgiasVerificacoesRN;

	@EJB
	private MbcCirurgiasJnRN mbcCirurgiasJnRN;

	private static final long serialVersionUID = -9001235819703398140L;

	public enum MbcCirurgiasRNExceptionCode implements BusinessExceptionCode {
		MBC_00438, MBC_00278, MBC_00279, MBC_00280, MBC_00281, MBC_00283, MBC_00284, MBC_00285, MBC_00393, MBC_00287, MBC_00318, MBC_00510, MBC_00328, MBC_00329, MBC_00330, MBC_00331, MBC_00347, MBC_00348, MBC_01326, MBC_00367, MBC_00368, MBC_00219, MBC_00220, MBC_01349, MBC_00221_2, MBC_00222, MBC_00223, MBC_00232, MBC_00242, MBC_00394, MBC_00464, MBC_00520, MBC_00947, MBC_00976, MBC_00977, MBC_01101, MBC_01104, MBC_01369, MBC_01826, MBC_00235, MBC_00236, FAT_00100, MBC_00403, MBC_00515, MBC_00288, MBC_00289, MBC_00297, MBC_00317, MBC_00319, MBC_00320, MBC_00321, MBC_01086, MBC_00349, MBC_00343, MBC_00538, MBC_00344, MBC_00376, MBC_00365, MBC_00379, MBC_00378, MBC_00370, MBC_00371, MBC_00437, MBC_00435, MBC_00436, MBC_00429, MBC_00391, MBC_CRG_CK10, MBC_CRG_CK11, MBC_CRG_CK12, MBC_CRG_CK13, MBC_CRG_CK14, MBC_CRG_CK15, MBC_CRG_CK16, MBC_CRG_CK17, MBC_00479, MBC_00480, MBC_00481, MBC_00482, MBC_00385, MBC_00448, MBC_00387, MBC_00388, MBC_00446, MBC_00504;
	}

	public MbcCirurgias persistirCirurgia(MbcCirurgias cirurgia, final String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		//Utilizar o ObterOriginal porque manten o objeto antigo do banco de dados após ser feito omerge na sequencia.
		MbcCirurgias cirurgiaOld = this.getMbcCirurgiasDAO().obterOriginal(cirurgia.getSeq());
		
		MbcCirurgias cirurgiaRetorno = cirurgia;
		
		if (cirurgiaOld == null) { // Insere
			this.preInserir(cirurgiaRetorno);
			this.getMbcCirurgiasVerificacoesRN().verificarRestricoesMbcCirurgias(cirurgiaRetorno);
			this.getMbcCirurgiasDAO().persistir(cirurgiaRetorno);
			this.posInserir(cirurgiaRetorno);

		}// FIM Insere 
		else { // Altera
			executarAntesAtualizar(cirurgiaRetorno, cirurgiaOld, nomeMicrocomputador, dataFimVinculoServidor);
			this.getMbcCirurgiasVerificacoesRN().verificarRestricoesMbcCirurgias(cirurgiaRetorno);
			cirurgiaRetorno = this.mbcCirurgiasDAO.merge(cirurgiaRetorno);
			this.getMbcCirurgiasDAO().atualizar(cirurgiaRetorno);
			this.getMbcCirurgiasJnRN().posAtualizar(cirurgiaRetorno, cirurgiaOld);
			this.getFatPaTuTrCnVmBcRN().posAtualizar(cirurgiaRetorno, cirurgiaOld);
			cirurgia = this.getMbcCirurgiasDAO().obterPorChavePrimaria(cirurgiaRetorno.getSeq());
		}// FIM Altera
		
		return cirurgiaRetorno;
	}

	private void posInserir(MbcCirurgias cirurgia) throws BaseException {
		if (cirurgia.getSolicitacaoCirurgiaPosEscala() != null) {
			this.atualizarSolicitacaoCirurgia(cirurgia);
		}
	}

	/**
	 * ORADB PROCEDURE MBCP_ATU_SOLIC_CIRG
	 * 
	 * @param cirurgia
	 */
	public void atualizarSolicitacaoCirurgia(MbcCirurgias cirurgia) throws BaseException {
		MbcSolicitacaoCirurgiaPosEscala solicitacaoCirurgiaPosEscala = cirurgia.getSolicitacaoCirurgiaPosEscala();
		if(solicitacaoCirurgiaPosEscala != null){
			solicitacaoCirurgiaPosEscala.setSolicitacaoAtendida(Boolean.TRUE);
			getMbcSolicitacaoCirurgiaPosEscalaRN().atualizar(solicitacaoCirurgiaPosEscala);
		}
	}

	private void preInserir(MbcCirurgias cirurgia) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		if(cirurgia.getAplicaListaCirurgiaSegura() == null){
			cirurgia.setAplicaListaCirurgiaSegura(false);
		}
		if(cirurgia.getIndPrc() == null){
			cirurgia.setIndPrc(false);
		}
		// Boolean consisteHorario = true; //consiste_horario varchar2(1) := 'S';
		cirurgia.setCriadoEm(new Date());
		cirurgia.setServidor(servidorLogado); // mbck_mbc_rn.rn_mbcp_atu_servidor
		this.verificarDataLimite(cirurgia); // mbck_crg_rn.rn_crgp_ver_dt_limit
		this.verificarDataAgendamentoNaoEletiva(cirurgia); // rn_crgp_ver_dt_ag_np
		this.atualizarMomentoAgendamento(cirurgia); // rn_crgp_atu_mto_agnd
		/* verifica datas se cirurgia eletiva */
		this.verificarDataCirurgiaEletiva(cirurgia);
		/* se eletiva, so pode agendar situação agendada */
		this.verificarInclusaoCirurgia(cirurgia);
		/* se eletiva, obrigatoriamente ter campos */
		this.verificarNaturezaEletiva(cirurgia);
		// this.atualizarDtHrInicioCirurgia(cirurgia);
		getMbcCirurgiasAjusteValoresRN().verificarDataPrevisaoInicio(cirurgia);
		/* calcula fim previsto da cirurgia */
		getMbcCirurgiasAjusteValoresRN().verificarDataPrevisaoFim(cirurgia);
		this.verificarDataPrevisaoCirurgia(cirurgia);
		this.verificarConvenioParticular(cirurgia);
		/* verificar perfil para marcar cirurgia não prevista */
		AghParametros paramUnidadeCo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIDADE_CO);
		Integer vlrNumerico = paramUnidadeCo.getVlrNumerico().intValue();
		if (DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda()) && DominioMomentoAgendamento.POS.equals(cirurgia.getMomentoAgenda())
				&& !cirurgia.getUnidadeFuncional().getSeq().equals(vlrNumerico)) {
			this.validarPerfilCirurgiaNaoPrev();
		}
		if (!DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda()) && !cirurgia.getUnidadeFuncional().getSeq().equals(vlrNumerico)) {
			this.validarPerfilCirurgiaNaoPrev();
		}
		/* garante que a sala seja informada em certa situação */
		this.getMbcCirurgiasVerificacoesRN().verificarSalaCirurgica(cirurgia);
		/* Garante que a sala informada esteja ativa */
		this.getMbcCirurgiasVerificacoesRN().verificarSalaCirurgicaAtiva(cirurgia);
		/* o tempo previsto da cirurgia não pode ser maior que o padrão da unidade */
		this.getMbcCirurgiasVerificacoesRN().verificarTempoCirurgia(cirurgia);
		/* convênio/plano devem estar ativos */
		this.getMbcCirurgiasVerificacoesRN().verificarConvenio(cirurgia);
		/* motivo demora sr deve estar ativo */
		this.getMbcCirurgiasVerificacoesRN().verificarDemoraSR(cirurgia);
		/* motivo atraso deve estar ativo */
		this.getMbcCirurgiasVerificacoesRN().verificarAtraso(cirurgia);
		/* motivo cancelamento deve estar ativo */
		this.getMbcCirurgiasVerificacoesRN().verificarMotivoCancelamento(cirurgia);
		/* destino paciente deve estar ativo */
		this.getMbcCirurgiasVerificacoesRN().verificarDestino(cirurgia);
		/* ver caracteristicas da sala e perfil de quem agenda */
		this.verificarCaracteristicaSalaPerfilAgendamento(cirurgia);
		/* ver se o tempo da prevista não excede o turno */
		this.verificarTurno(cirurgia);
		/* apropria atendimento */
		this.atualizarAtendimentoCirurgia(cirurgia);
		/* Atualiza o número da agenda */
		this.atualizarNroAgenda(cirurgia);
		/* Atualiza convenio da cirurgia conforme convenio da internação */
		this.getMbcCirurgiasAjusteValoresRN().atualizarConvenio(cirurgia, null);// 25
		
		/* Verifica convênio qdo projeto de pesquisa é informado */
		// ORADB mbck_crg_rn2.rn_crgp_ver_cnv_proj
		if (cirurgia.getProjetoPesquisa() != null) {
			getMbcCirurgiasVerificacoesRN().verificarProjetoPesquisa(cirurgia);
		}

	}

	/**
	 * ORADB RN_CRGP_ATU_NRO_AGEN
	 * 
	 * @param cirurgia
	 */
	private void atualizarNroAgenda(MbcCirurgias cirurgia) {
		Short numeroAgenda = this.getMbcCirurgiasDAO().obterUltimoNumeroAgenda(cirurgia);
		if (numeroAgenda == null) {
			cirurgia.setNumeroAgenda(Short.valueOf("1"));
		} else {
			Integer nro = numeroAgenda.intValue() + 1;
			cirurgia.setNumeroAgenda(nro.shortValue());

		}
	}

	/**
	 * ORADB PROCEDURE RN_CRGP_ATU_ATEND
	 * 
	 * @param cirurgia
	 * @throws BaseException
	 */
	private void atualizarAtendimentoCirurgia(MbcCirurgias cirurgia) throws BaseException {
		if ((DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda()) && DominioMomentoAgendamento.POS.equals(cirurgia.getMomentoAgenda()))
				|| !DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda())) {

			if (DominioOrigemPacienteCirurgia.I.equals(cirurgia.getOrigemPacienteCirurgia())) {

				AghAtendimentos atendi = getAghuFacade().obterAtendimentoPorPacienteEOrigem(cirurgia.getPaciente().getCodigo());

				if (atendi == null) {

					AghAtendimentos atendf = getAghuFacade().obterAtendimentoPorPacienteEDthr(cirurgia.getPaciente().getCodigo(), cirurgia.getData());

					if (atendf != null) {
						cirurgia.setAtendimento(atendf);
					}

				} else {
					cirurgia.setAtendimento(atendi);
				}

			} else {

				/* Atendiamento Urgencia */
				AghAtendimentos atendu = getAghuFacade().obterAtendimentoPorPacienteEOrigemDthrFim(cirurgia.getPaciente().getCodigo(), DominioOrigemAtendimento.U, null);

				if (atendu == null) {

					/* c_atendi_amb */
					AghAtendimentos atendiAmb = getAghuFacade().obterAtendimentoPorPacienteEOrigemDthrFim(cirurgia.getPaciente().getCodigo(), DominioOrigemAtendimento.I,
							cirurgia.getDataFimCirurgia());

					/* Atendiamento Internação */
					if (atendiAmb == null) {

						AghAtendimentos atdAmbUrg = getAghuFacade().obterAtendimentoPorPacienteEOrigemDthrFim(cirurgia.getPaciente().getCodigo(), DominioOrigemAtendimento.A, null);

						if (atdAmbUrg != null
								&& getAghuFacade().validarCaracteristicaDaUnidadeFuncional(cirurgia.getUnidadeFuncional().getSeq(),
										ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)
										&& !getAghuFacade().validarCaracteristicaDaUnidadeFuncional(cirurgia.getUnidadeFuncional().getSeq(),
										ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS)
										&& cirurgia.getDataInicioCirurgia() != null &&
										cirurgia.getDataInicioCirurgia().after(atdAmbUrg.getDthrInicio())
										&& (CoreUtil.diferencaEntreDatasEmDias(cirurgia.getDataInicioCirurgia(), atdAmbUrg.getDthrInicio()) < 2)) {

							cirurgia.setAtendimento(atdAmbUrg);

						} else {
							/**
							 * Insere em AghAtendimentos
							 */
							Integer atdSeq = mbcAtualizacaoProfissionalCirurgiaRN.inserirAtendimentoCirurgiaAmbulatorio(cirurgia.getAtendimento(), cirurgia.getPaciente(), cirurgia.getDataPrevisaoInicio(), cirurgia.getUnidadeFuncional(), cirurgia.getEspecialidade(), null);
							this.getMbcCirurgiasDAO().flush();
							AghAtendimentos novoAtendimento = getAghuFacade().obterAtendimentoOriginal(atdSeq);
							cirurgia.setAtendimento(novoAtendimento);
							
						}

					} else {

						cirurgia.setAtendimento(atendiAmb);
						cirurgia.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.I);

					}

				} else {
					cirurgia.setAtendimento(atendu);
				}

			}

		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_turno
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	private void verificarTurno(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (!cirurgia.getOverbooking()) {

			if (DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda())
					&& getAghuFacade().validarCaracteristicaDaUnidadeFuncional(cirurgia.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.BLOCO)) {

				MbcHorarioTurnoCirg htc = this.getMbcHorarioTurnoCirgDAO().obterHorarioInicioTurnoCirurg(cirurgia);
				MbcHorarioTurnoCirg htcNoite = this.getMbcHorarioTurnoCirgDAO().obterHorarioNoiteTurnoCirurg(cirurgia);

				if (htc == null) {
					/* Horário de previsão do início da cirurgia não pertence a nenhum turno */
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00370);
				} else {

					List<MbcCaracteristicaSalaCirg> listParticularTurno = getMbcCaracteristicaSalaCirgDAO().pesquisarTurnoCirurgiaParticular(cirurgia, htc.getId().getUnfSeq(),
							htc.getId().getTurno());
					List<MbcCaracteristicaSalaCirg> listParticularNoite = getMbcCaracteristicaSalaCirgDAO()
					.pesquisarTurnoCirurgiaParticular(cirurgia, htc.getId().getUnfSeq(), "N");
					/**
					 * se os 2 turnos são para atendimento particular ou convênio NÃO CRITICA
					 */
					Boolean particularTurno = false;
					Boolean particularNoite = false;
					if (listParticularTurno != null && !listParticularTurno.isEmpty()) {
						particularTurno = listParticularTurno.get(0).getCirurgiaParticular();
					}

					if (listParticularNoite != null && !listParticularNoite.isEmpty()) {
						particularNoite = listParticularNoite.get(0).getCirurgiaParticular();
					}

					// IF NVL(v_particular_turno, 'N') = 'S' AND
					// NVL(v_particular_noite, 'N') = 'S'
					// THEN
					// NULL;
					// ELSE

					if (!particularTurno && !particularNoite) {

						Calendar horaFimDia = Calendar.getInstance();
						horaFimDia.set(Calendar.HOUR_OF_DAY, 24);
						horaFimDia.set(Calendar.MINUTE, 00);
						Calendar horaInicioDia = Calendar.getInstance();
						horaInicioDia.set(Calendar.HOUR_OF_DAY, 00);
						horaInicioDia.set(Calendar.MINUTE, 00);

						if (htcNoite != null && htc.getHorarioInicial().before(htc.getHorarioFinal())) {
							if (DateUtil.validaHoraMaior(cirurgia.getDataPrevisaoFim(), htcNoite.getHorarioInicial()) &&
								DateUtil.validaHoraMenorIgual(cirurgia.getDataPrevisaoFim(), htcNoite.getHorarioFinal()) && 
								htc.getId().getTurno().equals("N")) {
								/* Horário de previsão de fim da cirurgia excede o fim do turno */
								throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00371);
							}
						} else if (
								DateUtil.validaHoraMaior(cirurgia.getDataPrevisaoFim(), htc.getHorarioInicial()) &&
								DateUtil.validaHoraMenorIgual(cirurgia.getDataPrevisaoFim(), horaFimDia.getTime()) 
								|| CoreUtil.isBetweenDatas(cirurgia.getDataPrevisaoFim(), horaInicioDia.getTime(), htc.getHorarioFinal())
								&& htc.getId().getTurno().equals("N")) {
							/* Horário de previsão de fim da cirurgia excede o fim do turno */
							throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00371);// MBC_00372
						}
					}
				}
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_carac_sa
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	private void verificarCaracteristicaSalaPerfilAgendamento(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		boolean perfilAgendarCirgNaoPrev = this.getPermissaoPerfil("agendarCirurgiaNaoPrevista");

		if (!perfilAgendarCirgNaoPrev) {
			if (cirurgia.getConvenioSaude() == null) {
				/* Convênio saúde não cadastrado */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00219);// MBC-00366
			}

			MbcCaracteristicaSalaCirg c_sala_esp = getMbcCaracteristicaSalaCirgDAO().buscarCaracteristicaSalaEspera(cirurgia);

			if (c_sala_esp == null) {
				/**
				 * Não há sala cadastrada na unidade para o turno e dia da semana
				 */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00376);
			}

			Boolean v_sala_hcpa_cedida = null;
			if (c_sala_esp.getMbcCedenciaSalaHcpas() == null) {

				v_sala_hcpa_cedida = Boolean.FALSE;
			}

			if (DominioGrupoConvenio.S.equals(cirurgia.getConvenioSaude().getGrupoConvenio()) && c_sala_esp.getCirurgiaParticular().equals(Boolean.TRUE)) {
				if (Boolean.FALSE.equals(v_sala_hcpa_cedida)) {
					/* Não é permitido agendar cirurgia SUS em sala com característica particular */
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00365);
				}
			}

			if (!c_sala_esp.getIndDisponivel()) {
				/* Sala não está disponivel para este dia da semana e turno */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00379);

			}

			if (c_sala_esp.getIndUrgencia() && DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda())) {
				if (Boolean.FALSE.equals(v_sala_hcpa_cedida)) {
					/* Sala reservada para procedimentos de urgencia */
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00378);
				}
			}

		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_nao_prev
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void validarPerfilCirurgiaNaoPrev() throws ApplicationBusinessException {

		boolean perfilAgendarCirgNaoPrev = this.getPermissaoPerfil("agendarCirurgiaNaoPrevista");

		if (!perfilAgendarCirgNaoPrev) {
			/**
			 * Não é permitido agendar cirurgia não prevista para este usuário. Entre em contato com a unid. cirúrgica
			 */
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00344);
		}
	}

	/**
	 * ORADB RN_CRGP_VER_CNV_PARTS
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	private void verificarConvenioParticular(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		boolean perfilAgendarCirgConvPart = this.getPermissaoPerfil("agendarCirurgiaConvParticular");

		if (cirurgia.getConvenioSaude() == null) {
			/* Convênio saúde não cadastrado */
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00219);

		} else if (DominioGrupoConvenio.S.equals(cirurgia.getConvenioSaude().getGrupoConvenio())) {
			// Perfil : AGENDAR CIRURGIA CONV PARTICUL

			if (!perfilAgendarCirgConvPart) {
				/* Não é permitido agendar cirurgia de convênio/particular. Entre em contato com a admissão. */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00343);
			}
		}

		AinInternacao internacao = getIInternacaoFacade().obterInternacaoPorAtendimentoPacCodigo(cirurgia.getPaciente().getCodigo());

		if (internacao != null) {

			if (!internacao.getConvenioSaude().getCodigo().equals(cirurgia.getConvenioSaude().getCodigo())) {

				if (!DominioGrupoConvenio.S.equals(cirurgia.getConvenioSaude().getGrupoConvenio())) {

					if (!perfilAgendarCirgConvPart) {
						/**
						 * Paciente está internado no convênio XXXXX. Não é permitido agendar cirurgia SUS.Entre em contato com a admissão
						 */
						throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00538, cirurgia.getConvenioSaude().getDescricao());
					}
				}
			}
		}
	}

	/**
	 * ORADB RN_CRGP_VER_INTERSEC
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	private void verificarDataPrevisaoCirurgia(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		// if mbck_crg_rn.consiste_horario = 'N' then
		// return;
		// end if;

//		Conforme acordado com Lisiane e Fabricio, TODAS cirurgias eletivas irão ser colididas com outras cir agendadas
		if (DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda()) ) {

			if (cirurgia.getSalaCirurgica() != null && cirurgia.getSalaCirurgica().getId().getSeqp() != null && cirurgia.getDataPrevisaoInicio() != null
					&& cirurgia.getDataPrevisaoFim() != null) {

				List<MbcCirurgias> qtdColisoes = getMbcCirurgiasDAO().listarDataPrvInicioFim(cirurgia);

				if (!qtdColisoes.isEmpty()) {
					/* O horário solicitado não está disponível na sala informada */
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00349);
				}
			}
		}
	}

	/**
	 * Atualiza data prevista de início da cirurgia concatenando data da agenda com o horário da dthr_prev_inicio. ORADB mbck_crg_rn.rn_crgp_atu_dt_inic
	 * 
	 * @param cirurgia
	 */
	@SuppressWarnings({ "unused", "deprecation" })
	private void atualizarDtHrInicioCirurgia(MbcCirurgias cirurgia) {
		if (cirurgia.getDataInicioCirurgia() != null && cirurgia.getData() != null) {
			Calendar data = Calendar.getInstance();
			data.setTime(cirurgia.getData());
			data.set(Calendar.HOUR_OF_DAY, cirurgia.getDataInicioCirurgia().getHours());
			data.set(Calendar.MINUTE, cirurgia.getDataInicioCirurgia().getMinutes());
			cirurgia.setDataInicioCirurgia(data.getTime());
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_dt_cirg
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	private void verificarDataCirurgiaEletiva(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda()) && !DominioMomentoAgendamento.POS.equals(cirurgia.getMomentoAgenda())) {

			Date dataAtual = new Date();
			if (cirurgia.getData().before(dataAtual)) {
				/* Data da cirurgia eletiva não pode ser anterior ao dia de hoje */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00288);
			}

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(cirurgia.getData());
			int dia = calendar.get(GregorianCalendar.DAY_OF_WEEK);
			if (dia == 1) {
				/* Data da cirurgia eletiva não pode ser domingo */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00289);
			}

			Boolean controleEscala = getMbcControleEscalaCirurgicaDAO().verificaExistenciaPeviaDefinitivaPorUNFData(cirurgia.getUnidadeFuncional().getSeq(), cirurgia.getData(),
					DominioTipoEscala.D);
			if (controleEscala) {
				boolean cirurNaoPrevista = this.getPermissaoPerfil("agendarCirurgiaNaoPrevista");
				if (!cirurNaoPrevista) {
					/* Já saiu a escala definitiva de cirurgias para esta data.Entre em contato com a unid. cirúrgica para agendar eletiva após rodar escala. */
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_01086);
				}
			}

			AghFeriados feriado = this.getAghuFacade().obterFeriadoSemTurno(cirurgia.getData());
			if (feriado != null) {
				/* Data da cirurgia eletiva não pode ser feriado */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00297);
			}
		}
	}

	/**
	 * ORADB PROCEDURE RN_CRGP_VER_ELETIVA
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	private void verificarNaturezaEletiva(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda())) {

			if (cirurgia.getDataPrevisaoInicio() == null && !cirurgia.getOverbooking()) {
				/* Informe hora prevista de início para cirurgia eletiva */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00319);
			}

			if ((cirurgia.getTempoPrevistoHoras() == null || cirurgia.getTempoPrevistoHoras() == 0)
					&& (cirurgia.getTempoPrevistoMinutos() == null || cirurgia.getTempoPrevistoMinutos() == 0)) {
				/* Informe tempo previsto de duração da cirurgia */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00320);
			}

			if (cirurgia.getSalaCirurgica() == null || cirurgia.getSalaCirurgica().getId().getSeqp() == null) {
				/* Informe a sala para agendar cirurgia eletiva */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00321);
			}
		}
	}

	/**
	 * ORADB PROCEDURE RN_CRGP_VER_INCLUSAO
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	private void verificarInclusaoCirurgia(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (!DominioSituacaoCirurgia.AGND.equals(cirurgia.getSituacao()) && DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda())) {
			/* Só pode incluir cirurgia na situação Agendada */
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00317);
		}
	}

	/**
	 * ORADB RN_CRGP_ATU_MTO_AGND
	 * 
	 * @param cirurgia
	 */
	private void atualizarMomentoAgendamento(MbcCirurgias cirurgia) {

		DominioMomentoAgendamento mtoAgndRes; // OUT

		if (DominioMomentoAgendamento.AGD.equals(cirurgia.getMomentoAgenda())) {

			mtoAgndRes = DominioMomentoAgendamento.AGD;

		} else {

			if (DominioNaturezaFichaAnestesia.EMG.equals(cirurgia.getNaturezaAgenda()) || DominioNaturezaFichaAnestesia.URG.equals(cirurgia.getNaturezaAgenda())) {

				mtoAgndRes = DominioMomentoAgendamento.POS;

			} else {

				Date data = new Date();
				if (cirurgia.getData().equals(data) || cirurgia.getData().before(data)) {

					mtoAgndRes = DominioMomentoAgendamento.POS;
				} else {
					Boolean controleEscala = getMbcControleEscalaCirurgicaDAO().verificaExistenciaPeviaDefinitivaPorUNFData(cirurgia.getUnidadeFuncional().getSeq(),
							cirurgia.getData(), DominioTipoEscala.D);

					if (controleEscala != null && controleEscala){
						mtoAgndRes = DominioMomentoAgendamento.POS;
					} else {
						mtoAgndRes = DominioMomentoAgendamento.PRV;
					}
				}
			}
		}

		cirurgia.setMomentoAgenda(mtoAgndRes);// out
	}

	/**
	 * Verifica se data de agendamento de não eletiva não antecede a data mínima para retroação de agendamento ORADB rn_crgp_ver_dt_ag_np
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	private void verificarDataAgendamentoNaoEletiva(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda()) && !DominioMomentoAgendamento.POS.equals(cirurgia.getMomentoAgenda())) {

			/* Rotina para buscar valor da tabela de parâmetros que define o número */
			/* de dias para agendamento retroativo de cirurgia eletiva */
			AghParametros paramDiasAgRetroa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_AG_RETROA_NPREV);
			if (paramDiasAgRetroa.getVlrNumerico() != null) {
				Calendar dataRetro = Calendar.getInstance();
				dataRetro.add(Calendar.DAY_OF_MONTH, -paramDiasAgRetroa.getVlrNumerico().intValue());

				if (cirurgia.getData().before(dataRetro.getTime())) {

					/* Data do agendamento menor que a data mínima permitida para agendamento retroativo */
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00515);
				}

			}

		}
	}

	/**
	 * ORADBB PROCEDURE mbck_crg_rn.rn_crgp_ver_dt_limit
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	private void verificarDataLimite(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (cirurgia.getUnidadeFuncional() == null) {
			/* Unidade funcional não cadastrada */
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00235);
		} else if (!DominioSituacao.A.equals(cirurgia.getUnidadeFuncional().getIndSitUnidFunc())) {
			/* Unidade funcional está inativa */
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00236);
		}

		if (cirurgia.getConvenioSaude() == null) {

			/* Convênio não cadastrado */
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.FAT_00100);
		}

		Short diasLimite = cirurgia.getUnidadeFuncional().getQtdDiasLimiteCirg();
		Short diasLimiteConv = cirurgia.getUnidadeFuncional().getQtdDiasLimiteCirgConvenio();
		Date dataLimite;

		if (!DominioGrupoConvenio.S.equals(cirurgia.getConvenioSaude().getGrupoConvenio())) {
			diasLimite = diasLimiteConv;
		}

		if (CoreUtil.maior(diasLimite, (short) 0)) {

			Calendar dataAtual = Calendar.getInstance();

			dataAtual.add(Calendar.DAY_OF_MONTH, diasLimite);
			dataLimite = dataAtual.getTime();

			if (cirurgia.getData().after(dataLimite)) {
				/* Data da cirurgia fora do limite máximo permitido para agendar */
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00403);
			}

		}
	}

	/**
	 * ORADB Trigger MBCT_CRG_BRU ORADB mbck_cec_rn.v_veio_gera_escala_d ORADB MBCK_CRG_RN.valida_regra_bru
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void executarAntesAtualizar(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		Boolean V_VEIO_GERA_ESCALA_D = Boolean.FALSE; // ORADB mbck_cec_rn.v_veio_gera_escala_d
		Boolean VALIDA_REGRA_BRU = Boolean.TRUE; // ORADB MBCK_CRG_RN.valida_regra_bru

		MbcCirurgiasVerificacoesRN verificacoes = getMbcCirurgiasVerificacoesRN();
		MbcCirurgiasAjusteValoresRN ajusteValores = getMbcCirurgiasAjusteValoresRN();

		// Devido a problemas de pmd foi, dividido em 3 métodos
		executarAntesAtualizarParte1(cirurgia, cirurgiaOld, nomeMicrocomputador, dataFimVinculoServidor, verificacoes, ajusteValores, V_VEIO_GERA_ESCALA_D,
				VALIDA_REGRA_BRU);
		executarAntesAtualizarParte2(cirurgia, cirurgiaOld, nomeMicrocomputador, dataFimVinculoServidor, verificacoes, ajusteValores, V_VEIO_GERA_ESCALA_D,
				VALIDA_REGRA_BRU);
		executarAntesAtualizarParte3(cirurgia, cirurgiaOld, nomeMicrocomputador, dataFimVinculoServidor, verificacoes, ajusteValores, V_VEIO_GERA_ESCALA_D,
				VALIDA_REGRA_BRU);		
	}

	private void executarAntesAtualizarParte1(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor, MbcCirurgiasVerificacoesRN verificacoes, MbcCirurgiasAjusteValoresRN ajusteValores, Boolean V_VEIO_GERA_ESCALA_D,
			Boolean VALIDA_REGRA_BRU) throws ApplicationBusinessException {
		// ORADB mbck_crg_rn2.rn_crgp_atu_digt_ns
		ajusteValores.atualizarDigitoNotaSala(cirurgia, cirurgiaOld);// 1
		// ORADB mbck_crg_rn.rn_crgp_ver_alt_elet
		verificacoes.verificarAlteracaoEletiva(cirurgia, V_VEIO_GERA_ESCALA_D);// 2

		// ORADB mbck_crg_rn.rn_crgp_ver_dtprev_i
		// IF aghk_util.modificados(:old.dthr_prev_inicio,:new.dthr_prev_inicio)
		if (CoreUtil.modificados(cirurgia.getDataPrevisaoInicio(), cirurgiaOld.getDataPrevisaoInicio())) {
			ajusteValores.verificarDataPrevisaoInicio(cirurgia);// 3
		}

		// ORADB mbck_crg_rn.rn_crgp_atu_prev_fim
		/*
		 * IF aghk_util.modificados(:old.tempo_prev_hrs,:new.tempo_prev_hrs) OR aghk_util.modificados(:old.tempo_prev_min,:new.tempo_prev_min) OR
		 * aghk_util.modificados(:old.dthr_prev_inicio,:new.dthr_prev_inicio)
		 */
		if (CoreUtil.modificados(cirurgia.getTempoPrevistoHoras(), cirurgiaOld.getTempoPrevistoHoras())
				|| CoreUtil.modificados(cirurgia.getTempoPrevistoMinutos(), cirurgiaOld.getTempoPrevistoMinutos())
				|| CoreUtil.modificados(cirurgia.getDataPrevisaoInicio(), cirurgiaOld.getDataPrevisaoInicio())) {
			ajusteValores.verificarDataPrevisaoFim(cirurgia);// 4
		}

		// ORADB mbck_crg_rn.rn_crgp_ver_obrig_ns
		verificacoes.verificarObrigacaoNotaSala(cirurgia);// 5

		// ORADB mbck_crg_rn.rn_crgp_atu_ultdg_ns
		ajusteValores.atualizarUltDigitacaoNotaSala(cirurgia);// 6
		// Verifica campos alterados
		verificacoes.verificarCamposNaoAlteraveis(cirurgia, cirurgiaOld);// 7,8,9,10

		// ORADB mbck_crg_rn.rn_crgp_ver_sit_canc
		ajusteValores.verificarSituacaoCancelada(cirurgia);// 11

		// ORADB mbck_crg_rn.rn_crgp_ver_cancelam
		verificacoes.varificaCancelamento(cirurgia, cirurgiaOld);// 12

		// ORADB mbck_crg_rn2.rn_crgp_atu_info_sit
		ajusteValores.atualizarInformacaoSituacao(cirurgia, cirurgiaOld);// 13
		// ORADB mbck_crg_rn.rn_crgp_atu_tempo_o2
		ajusteValores.atualizarTempoO2(cirurgia);// 14
		// ORADB mbck_crg_rn.rn_crgp_atu_tempo_az
		ajusteValores.atualizarTempoAZ(cirurgia);// 15
		// ORADB mbck_crg_rn.rn_crgp_ver_sala
		verificacoes.verificarSalaCirurgica(cirurgia);// 16

		// ORADB mbck_crg_rn2.rn_crgp_ver_sl_ativa
		// IF aghk_util.modificados(:old.sci_unf_seq,:new.sci_unf_seq) OR aghk_util.modificados(:old.sci_seqp,:new.sci_seqp)
		if (CoreUtil.modificados(cirurgia.getSalaCirurgica().getId().getUnfSeq(), cirurgiaOld.getSalaCirurgica().getId().getUnfSeq())
				&& CoreUtil.modificados(cirurgia.getSalaCirurgica().getId().getSeqp(), cirurgiaOld.getSalaCirurgica().getId().getSeqp())) {
			verificacoes.verificarSalaCirurgicaAtiva(cirurgia);// 17
		}

		// ORADB mbck_crg_rn.rn_crgp_ver_tempos
		verificacoes.verificarTempoCirurgia(cirurgia);// 18

		// ORADB mbck_crg_rn.rn_crgp_ver_convenio
		// IF aghk_util.modificados(:old.csp_cnv_codigo,:new.csp_cnv_codigo) OR aghk_util.modificados(:old.csp_seq,:new.csp_seq)
		if (CoreUtil.modificados(cirurgia.getConvenioSaudePlano().getId().getCnvCodigo(), cirurgiaOld.getConvenioSaudePlano().getId().getCnvCodigo())) {
			verificacoes.verificarConvenio(cirurgia);// 19
		}

		// ORADB mbck_crg_rn.rn_crgp_ver_demora
		verificacoes.verificarDemoraSR(cirurgia);// 20

		// ORADB mbck_crg_rn.rn_crgp_ver_atraso
		verificacoes.verificarAtraso(cirurgia);// 21

		// ORADB mbck_crg_rn.rn_crgp_ver_mot_canc
		// IF aghk_util.modificados(:old.mtc_seq,:new.mtc_seq)
		validarMotivoCancelamento(cirurgia, cirurgiaOld, verificacoes);

		// ORADB mbck_crg_rn.rn_crgp_ver_destino
		verificacoes.verificarDestino(cirurgia);// 23

		// ORADB mbck_crg_rn.rn_crgp_ver_dest_pac
		verificacoes.verificarDestinoPaciente(cirurgia, cirurgiaOld);// 24

		// ORADB mbck_crg_rn.rn_crgp_atu_convenio
		ajusteValores.atualizarConvenio(cirurgia, cirurgiaOld);// 25

		// ORADB mbck_crg_rn.rn_crgp_atu_sltc_exm
		// IF ( aghk_util.modificados(:old.csp_cnv_codigo,:new.csp_cnv_codigo) OR aghk_util.modificados(:old.csp_seq,:new.csp_seq) )
		if (CoreUtil.modificados(cirurgia.getConvenioSaudePlano().getId().getCnvCodigo(), cirurgiaOld.getConvenioSaudePlano().getId().getCnvCodigo())) {
			verificacoes.atualizarSolicitacoesExames(cirurgiaOld, nomeMicrocomputador);// 26
		}

		// ORADB mbck_crg_rn.rn_crgp_atu_proc_pac
		ajusteValores.atualizarProcPaciente(cirurgia, cirurgiaOld);// 27

		// ORADB mbck_crg_rn2.rn_crgp_canc_quimio
		ajusteValores.cancelarQuimio(cirurgia, cirurgiaOld);// 28

		// ORADB mbck_crg_rn.rn_crgp_atu_cancela
		ajusteValores.atualizarCancelamento(cirurgia);// 29

		// ORADB mbck_crg_rn.rn_crgp_atu_atd_exme
		// IF aghk_util.modificados(:old.atd_seq,:new.atd_seq) AND :old.atd_seq IS NOT NULL THEN
		
		Integer seqCirgAtd = null;
		if(cirurgia.getAtendimento() != null) {
			seqCirgAtd = cirurgia.getAtendimento().getSeq();
		}
		
		if (cirurgiaOld.getAtendimento() != null && CoreUtil.modificados(seqCirgAtd, 
				cirurgiaOld.getAtendimento().getSeq())) {
			ajusteValores.atualizarAtendimentoSolicitacaoExames(cirurgia, cirurgiaOld, nomeMicrocomputador);// 30
		}

	}

	private void validarMotivoCancelamento(MbcCirurgias cirurgia,
			MbcCirurgias cirurgiaOld, MbcCirurgiasVerificacoesRN verificacoes)
	throws ApplicationBusinessException {
		if (cirurgia.getMotivoCancelamento() != null 
				&& cirurgiaOld.getMotivoCancelamento() != null 
				&& cirurgia.getMotivoCancelamento().equals(cirurgiaOld.getMotivoCancelamento())) {
			verificacoes.verificarMotivoCancelamento(cirurgia);// 22
		}
	}

	private void executarAntesAtualizarParte2(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor, MbcCirurgiasVerificacoesRN verificacoes, MbcCirurgiasAjusteValoresRN ajusteValores, Boolean V_VEIO_GERA_ESCALA_D,
			Boolean VALIDA_REGRA_BRU) throws BaseException {

		// ORADB mbck_crg_rn.mbcp_atu_agenda_canc
		// IF :old.mtc_seq IS NULL AND :new.mtc_seq IS NOT NULL THEN
		if (cirurgiaOld.getMotivoCancelamento() == null && cirurgia.getMotivoCancelamento() != null) {
			ajusteValores.atualizarAgendaCancelamento(cirurgia);// 31
		}

		// ORADB mbck_crg_rn.mbcp_atu_agenda_banc
		/*
		 * IF aghk_util.modificados(:old.unf_seq,:new.unf_seq) OR aghk_util.modificados(:old.pac_codigo,:new.pac_codigo) OR
		 * aghk_util.modificados(:old.csp_cnv_codigo,:new.csp_cnv_codigo) OR aghk_util.modificados(:old.csp_seq,:new.csp_seq) OR
		 * aghk_util.modificados(:old.sci_unf_seq,:new.sci_unf_seq) OR aghk_util.modificados(:old.sci_seqp,:new.sci_seqp) OR
		 * aghk_util.modificados(:old.origem_pac_cirg,:new.origem_pac_cirg) THEN
		 */
		if (CoreUtil.modificados(cirurgia.getUnidadeFuncional().getSeq(), cirurgiaOld.getUnidadeFuncional().getSeq()) || CoreUtil.modificados(cirurgia.getPaciente().getCodigo(), cirurgiaOld.getPaciente().getCodigo())
				|| CoreUtil.modificados(cirurgia.getConvenioSaudePlano().getId(), cirurgiaOld.getConvenioSaudePlano().getId())
				|| CoreUtil.modificados(cirurgia.getSalaCirurgica().getId(), cirurgiaOld.getSalaCirurgica().getId())
				|| CoreUtil.modificados(cirurgia.getOrigemPacienteCirurgia(), cirurgiaOld.getOrigemPacienteCirurgia())) {
			ajusteValores.atualizarAgendaBanc(cirurgia, cirurgiaOld, VALIDA_REGRA_BRU);// 32
		}

		// ORADB mbck_crg_rn.rn_crgp_atu_util_sl
		/*
		 * IF aghk_util.modificados(:old.sci_unf_seq,:new.sci_unf_seq) OR aghk_util.modificados(:old.sci_seqp,:new.sci_seqp) OR
		 * (aghk_util.modificados(:old.DTHR_INICIO_CIRG,:new.DTHR_INICIO_CIRG) AND :new.DTHR_INICIO_CIRG IS NOT NULL) THEN
		 */
		if (CoreUtil.modificados(cirurgia.getSalaCirurgica().getId(), cirurgiaOld.getSalaCirurgica().getId())
				|| (CoreUtil.modificados(cirurgia.getDataInicioCirurgia(), cirurgiaOld.getDataInicioCirurgia()) && cirurgia.getDataInicioCirurgia() != null)) {
			ajusteValores.verificarUtilizacaoSala(cirurgia, cirurgiaOld);// 33
		}

		// ORADB mbck_crg_rn2.rn_crgp_ver_alt_unid
		// IF aghk_util.modificados(:old.unf_seq,:new.unf_seq) THEN
		if (CoreUtil.modificados(cirurgia.getUnidadeFuncional().getSeq(), cirurgiaOld.getUnidadeFuncional().getSeq())) {
			verificacoes.verificarAlteracaoUnidadeCirurgica(cirurgia);// 34
		}

		// ORADB mbck_crg_rn2.rn_crgp_atu_unid_prf
		// IF aghk_util.modificados(:old.unf_seq,:new.unf_seq) THEN
		if (CoreUtil.modificados(cirurgia.getUnidadeFuncional().getSeq(), cirurgiaOld.getUnidadeFuncional().getSeq())) {
			ajusteValores.atualizarUnidadeProfissional(cirurgia, cirurgiaOld, V_VEIO_GERA_ESCALA_D);// 35
		}
	}

	private void executarAntesAtualizarParte3(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld, final String nomeMicrocomputador,
			final Date dataFimVinculoServidor, MbcCirurgiasVerificacoesRN verificacoes, MbcCirurgiasAjusteValoresRN ajusteValores, Boolean V_VEIO_GERA_ESCALA_D,
			Boolean VALIDA_REGRA_BRU) throws ApplicationBusinessException {
		// ORADB mbck_crg_rn2.rn_crgp_ver_cnv_proj
		/*
		 * IF (aghk_util.modificados(:old.pjq_seq,:new.pjq_seq) AND :new.pjq_seq IS NOT NULL) OR (aghk_util.modificados(:old.csp_cnv_codigo,:new.csp_cnv_codigo)) THEN
		 */
		if (cirurgia.getProjetoPesquisa() != null && cirurgiaOld.getProjetoPesquisa() != null && 
				(  CoreUtil.modificados(cirurgia.getProjetoPesquisa().getSeq(), cirurgiaOld.getProjetoPesquisa().getSeq()))
				|| CoreUtil.modificados(cirurgia.getConvenioSaudePlano().getId(), cirurgiaOld.getConvenioSaudePlano().getId())) {
			verificacoes.verificarProjetoPesquisa(cirurgia);// 36
		}

		// ORADB mbck_crg_rn2.rn_crgp_atu_cota_pjq
		// IF (aghk_util.modificados(:old.situacao,:new.situacao) AND :new.situacao = 'CANC') THEN
		if (CoreUtil.modificados(cirurgia.getSituacao(), cirurgiaOld.getSituacao()) && DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao())) {
			verificacoes.atualizarCotaProcedimentos(cirurgia);// 37
		}

		// ORADB rn_crgp_atu_agfa
		// IF aghk_util.modificados(:old.pac_codigo,:new.pac_codigo) THEN
		if (CoreUtil.modificados(cirurgia.getPaciente().getCodigo(), cirurgiaOld.getPaciente().getCodigo())) {
			verificacoes.atualizarSistemaImagensAGFA(cirurgia, cirurgiaOld);// 38
		}
		// ORADB mbck_crg_rn2.rn_crgp_cirg_regime
		// IF aghk_util.modificados(:old.origem_pac_cirg,:new.origem_pac_cirg) AND :new.situacao = 'AGND'AND :new.dthr_entrada_sala IS NULL THEN
		if (CoreUtil.modificados(cirurgia.getOrigemPacienteCirurgia(), cirurgiaOld.getOrigemPacienteCirurgia()) && DominioSituacaoCirurgia.AGND.equals(cirurgia.getSituacao())
				&& cirurgia.getDataEntradaSala() == null) {
			verificacoes.verificarCirurgiaRegime(cirurgia);// 39
		}
	}	

	/**
	 * ORADB PROCEDURE RN_CRGP_ATU_PRF_NELT
	 * 
	 * @param cirurgia
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void atualizaProfNelt(Integer crgSeq, DominioNaturezaFichaAnestesia naturezaAgenda, AghUnidadesFuncionais unidadeFuncional, MbcSalaCirurgica salaCirurgica) throws BaseException {

		DominioDiaSemana v_dia_sem = null;
		String v_turno = null;
		//Boolean erroTurno = Boolean.FALSE;

		if (!DominioNaturezaFichaAnestesia.ELE.equals(naturezaAgenda)) {
			
			MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterOriginal(crgSeq);

			/* Busca dia da semana e hrio inicial da cirurgia */
			v_dia_sem = AghuEnumUtils.retornaDiaSemanaAghu(cirurgia.getData());

			/* Busca os turnos da unidade */
			List<MbcHorarioTurnoCirg> listHorariosTurnoCirg = this.getMbcHorarioTurnoCirgDAO().buscarTurnosPorUnidadeFuncional(cirurgia.getUnidadeFuncional().getSeq());

			for (MbcHorarioTurnoCirg fet_htc : listHorariosTurnoCirg) {

				if (DateUtil.validaHoraMaior(fet_htc.getHorarioInicial(), fet_htc.getHorarioFinal())) {

					if ((DateUtil.validaHoraMenorIgual(fet_htc.getHorarioInicial(), cirurgia.getDataInicioCirurgia()) && DateUtil.validaHoraMaior(
							cirurgia.getDataInicioCirurgia(), fet_htc.getHorarioFinal()))
							|| (DateUtil.validaHoraMaior(fet_htc.getHorarioInicial(), cirurgia.getDataInicioCirurgia()) && DateUtil.validaHoraMenorIgual(
									cirurgia.getDataInicioCirurgia(), fet_htc.getHorarioFinal()))) {
						v_turno = fet_htc.getMbcTurnos().getTurno();
						break;
					}

				} else if (DateUtil.validaHoraMaior(fet_htc.getHorarioFinal(), fet_htc.getHorarioInicial())) {

					if ((DateUtil.validaHoraMenorIgual(fet_htc.getHorarioInicial(), cirurgia.getDataInicioCirurgia()) && DateUtil.validaHoraMenorIgual(
							cirurgia.getDataInicioCirurgia(), fet_htc.getHorarioFinal()))) {
						v_turno = fet_htc.getMbcTurnos().getTurno();
						break;
					} else {
						v_turno = "E";
					}

				} else {
					/* Turno Inválido */
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00429);
				}

			}// fim loop

			if ("E".equals(v_turno)) {
				/* Cirurgia não se enquadra em nenhum Turno Cadastrado */
				throw new ApplicationBusinessException(MbcControleEscalaCirurgicaRNExceptionCode.MBC_00430);
			}

			/* Busca escala de profissionais - menos anestesistas professor e contratado */
			List<MbcEscalaProfUnidCirg> listaEscalaProfUnidCirg = this.getMbcEscalaProfUnidCirgDAO().pesquisarEscalaProfissionais(
					cirurgia.getSalaCirurgica().getId().getUnfSeq(), cirurgia.getSalaCirurgica().getId().getSeqp(), v_dia_sem, v_turno);

			/* atualizar profissionais da cirurgia por função */
			mbcAtualizacaoProfissionalCirurgiaRN.atualizarProfCirurgiaPorFuncao(cirurgia.getServidor(), cirurgia, listaEscalaProfUnidCirg);

			/* atualizar profissionais anestesistas */
			mbcAtualizacaoProfissionalCirurgiaRN.atualizarProfAnest(cirurgia.getServidor(), v_turno, cirurgia, v_dia_sem);	

		}
	}


	/**
	 * Estória do Usuário #40230
	 * ORADB Trigger MBCT_CRG_ASU
	 * @author marcelo.deus
	 * @throws BaseException 
	 * 
	 */
	public void posAtualizarMbcCirurgias(MbcCirurgias newMbcCirurgias, MbcCirurgias oldMbcCirurgias) throws BaseException{
		getFatPaTuTrCnVmBcRN().posAtualizar(newMbcCirurgias, oldMbcCirurgias);
	}

	/**
	 * Verifica se o usuário tem uma permissão associada ao perfil
	 * 
	 * @param permissao
	 * @return
	 */
	private Boolean getPermissaoPerfil(String permissao) {
		return true; // TODO REMOVER APENAS TESTES
		// TODO DESCOMENTAR ABAIXO PARA O TESTE REAL
		// return getICascaFacade().usuarioTemPermissao(getIdentity().getPrincipal().getName(), permissao);
	}
	public MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	private MbcCirurgiasVerificacoesRN getMbcCirurgiasVerificacoesRN() {
		return mbcCirurgiasVerificacoesRN;
	}
	private MbcCirurgiasAjusteValoresRN getMbcCirurgiasAjusteValoresRN() {
		return mbcCirurgiasAjusteValoresRN;
	}
	public IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	public MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}
	public IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}
	public IInternacaoFacade getIInternacaoFacade() {
		return iInternacaoFacade;
	}	
	public ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return iSolicitacaoExameFacade;
	}
	public MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	public MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO() {
		return mbcHorarioTurnoCirgDAO;
	}
	private MbcSolicitacaoCirurgiaPosEscalaRN getMbcSolicitacaoCirurgiaPosEscalaRN() {
		return mbcSolicitacaoCirurgiaPosEscalaRN;
	}
	private FatPaTuTrCnVmBcRN getFatPaTuTrCnVmBcRN() {
		return fatPaTuTrCnVmBcRN;
	}
	private MbcCirurgiasJnRN getMbcCirurgiasJnRN() {
		return mbcCirurgiasJnRN;
	}
	public MbcEscalaProfUnidCirgDAO getMbcEscalaProfUnidCirgDAO() {
		return mbcEscalaProfUnidCirgDAO;
	}
	protected AgendaProcedimentosON getAgendaProcedimentosON() {
		return new AgendaProcedimentosON();
	}

}