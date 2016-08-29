package br.gov.mec.aghu.blococirurgico.business;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEscalaProfUnidCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioMomentoAgendamento;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcEscalaProfUnidCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.util.AghuEnumUtils;


@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class MbcControleEscalaCirurgicaRN extends BaseBMTBusiness {
	
	private static final long serialVersionUID = 3222500847490874728L;
	
	private static final Log LOG = LogFactory.getLog(MbcControleEscalaCirurgicaRN.class);
	
	public final static int TRANSACTION_TIMEOUT_24_HORAS = 60 * 60 * 24;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcEscalaProfUnidCirgDAO mbcEscalaProfUnidCirgDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private IParametroFacade iParametroFacade;
	
	@EJB
	private MbcAtualizacaoProfissionalCirurgiaRN mbcAtualizacaoProfissionalCirurgiaRN;
	
	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private MbcCirurgiasRN mbcCirurgiasRN;

	@EJB
	private MbcProfCirurgiasRN mbcProfCirurgiasRN;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	

	public enum MbcControleEscalaCirurgicaRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_REGISTRO_ESCALA_EXISTENTE, MBC_00419,AGH_00163,AIP_00013,AGH_00182,MBC_00429,MBC_00430,MBC_00417, MBC_00418,
		ERRO_INSERIR_MBC_CONTROLE_ESCALA_CIRURGICA,
		ERRO_EXCLUIR_ESCALA_CIRURGIA,
		ERRO_ATUALIZAR_ESCALA_CIRURGIA
		;
	}


	/**
	 * ORADB MBCT_CEC_BRI (INSERT) – ANTES DE INSERIR O OBJETO
	 * 
	 * @param escalaCirurgica
	 * @throws BaseException
	 */
	private void preInserir(MbcControleEscalaCirurgica escalaCirurgica, String nomeMicrocomputador) throws BaseException {
		this.verificarExistenciaRegistro(escalaCirurgica); 
		escalaCirurgica.setDthrGeracaoEscala(new Date());
		this.verificarHorarioEscalaDefinitiva(escalaCirurgica);
		this.realizarFechamentoAtendimentosCirurgicos(escalaCirurgica,nomeMicrocomputador );
		this.manterVolumeRegistros(escalaCirurgica);//RN6
		this.atualizarAtendimentoMbcCirurgia(escalaCirurgica, nomeMicrocomputador);//RN7
		this.atualizarProfCirurgico(escalaCirurgica);

	}

	/**
	 * ORADB mbck_cec_rn.RN_CECP_ATU_PRF_CIRG
	 * @param escalaCirurgica
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	private void atualizarProfCirurgico(MbcControleEscalaCirurgica escalaCirurgica)	throws ApplicationBusinessException, BaseException {
		String turno = null;  
		//Boolean v_ness_anest = Boolean.FALSE;

		/* cirurgias marcadas para data da escala */
		List<MbcCirurgias> listCur = getMbcCirurgiasDAO().pesquisarCirurgiasDataMarcada(escalaCirurgica.getId().getUnfSeq(), escalaCirurgica.getId().getDtEscala());

		for(MbcCirurgias curCrg : listCur ){

			List<MbcHorarioTurnoCirg>  listHorarioTurnoCirg = this.getMbcHorarioTurnoCirgDAO().buscarTurnosPorUnidadeFuncional(escalaCirurgica.getId().getUnfSeq());	

			/* Busca os turnos da unidade */
			for(MbcHorarioTurnoCirg curHtc : listHorarioTurnoCirg){

				//to_number(to_char(nvl(crg.dthr_prev_inicio,crg.dthr_inicio_ordem),'hh24mi')) hrio_inic
				Date hrioInic = curCrg.getDataPrevisaoInicio()!=null?curCrg.getDataPrevisaoInicio():curCrg.getDataInicioOrdem();

				if (DateUtil.validaHoraMaior(curHtc.getHorarioInicial(), curHtc.getHorarioFinal())) {

					if(DateUtil.validaHoraMenorIgual(curHtc.getHorarioInicial(), hrioInic) && DateUtil.validaHoraMaior(hrioInic, curHtc.getHorarioFinal()) ||
							(DateUtil.validaHoraMaior(curHtc.getHorarioInicial(), hrioInic) && DateUtil.validaHoraMenorIgual(
									hrioInic, curHtc.getHorarioFinal()))) {	
						
						turno = curHtc.getId().getTurno();
						break;
					}

				} else if (DateUtil.validaHoraMaior(curHtc.getHorarioFinal(), curHtc.getHorarioInicial())) {

					if(DateUtil.validaHoraMenorIgual(curHtc.getHorarioInicial(), hrioInic) && DateUtil.validaHoraMaior(curHtc.getHorarioFinal(), hrioInic)){ 

						turno = curHtc.getId().getTurno();
						break;
					}else{
						turno = "E";
					}

				}else{
					/* Turno Inválido */
					throw new ApplicationBusinessException(MbcControleEscalaCirurgicaRNExceptionCode.MBC_00429);

				}

			}//end LOOP

			if("E".equals(turno)){
				/* Cirurgia não se enquadra em nenhum Turno Cadastrado */
				throw new ApplicationBusinessException(MbcControleEscalaCirurgicaRNExceptionCode.MBC_00430);
			}
			/* Verifica necessidade de anestesista */
			//			List<MbcTipoAnestesias> listTipoAnestesia = getMbcAnestesiaCirurgiasDAO().listarTipoAnestesias(curCrg.getSeq(), Boolean.TRUE);
			//			if(listTipoAnestesia !=null && !listTipoAnestesia.isEmpty()){
			//				v_ness_anest = Boolean.TRUE;
			//			}

			/* Pesquisa para deletar profissionais incluidos pela escala menos anestesistas professor e contratado*/
			List<MbcProfCirurgias> listaProfCirurgias = this.getMbcProfCirurgiasDAO().listarMbcProfCirurgiasControleEscala(curCrg.getSeq());
			if(listaProfCirurgias!=null && !listaProfCirurgias.isEmpty()){
				for(MbcProfCirurgias profCir : listaProfCirurgias){
					this.getMbcProfCirurgiasRN().removerMbcProfCirurgias(profCir, Boolean.FALSE);
				}
			}

			DominioDiaSemana diaSemana =   AghuEnumUtils.retornaDiaSemanaAghu(curCrg.getData()); //.retornaDiaSemana(curCrg.getData());
			/* Busca escala de profissionais - menos anestesistas professor e contratado */
			List<MbcEscalaProfUnidCirg> listaEscalaProfUnidCirg = this.getMbcEscalaProfUnidCirgDAO().pesquisarEscalaProfissionais(curCrg.getSalaCirurgica().getId().getUnfSeq(), curCrg.getSalaCirurgica().getId().getSeqp(), diaSemana, turno);

			/* atualizar profissionais da cirurgia por função*/
			this.atualizarProfCirurgiaPorFuncao(escalaCirurgica.getRapServidores(), curCrg, listaEscalaProfUnidCirg);
			/* atualizar profissionais anestesistas */
			this.atualizarProfAnest(escalaCirurgica.getRapServidores(), turno, curCrg, diaSemana);


		}
	}

	public void atualizarProfAnest(RapServidores servidor,
			String turno, MbcCirurgias curCrg, DominioDiaSemana diaSemana)
					throws BaseException {
		mbcAtualizacaoProfissionalCirurgiaRN.atualizarProfAnest(servidor, turno, curCrg, diaSemana);
	}

	public void atualizarProfCirurgiaPorFuncao(RapServidores servidor, MbcCirurgias curCrg,	List<MbcEscalaProfUnidCirg> listaEscalaProfUnidCirg)
			throws BaseException {
		mbcAtualizacaoProfissionalCirurgiaRN.atualizarProfCirurgiaPorFuncao(servidor, curCrg, listaEscalaProfUnidCirg);
	}

	public MbcProfCirurgias populaMbcProfCirurgias(MbcCirurgias curCrg,
			MbcEscalaProfUnidCirg escalaProfUnidCirg) {
		MbcProfCirurgias profCirurgia = new MbcProfCirurgias();
		MbcProfCirurgiasId id = new MbcProfCirurgiasId();
		id.setCrgSeq(curCrg.getSeq());
		id.setPucSerMatricula(escalaProfUnidCirg.getId().getPucSerMatricula());
		id.setPucSerVinCodigo(escalaProfUnidCirg.getId().getPucSerVinCodigo());
		id.setPucUnfSeq(escalaProfUnidCirg.getId().getPucUnfSeq());
		id.setPucIndFuncaoProf(escalaProfUnidCirg.getId().getPucIndFuncaoProf());
		profCirurgia.setId(id);
		profCirurgia.setCirurgia(curCrg);
		profCirurgia.setUnidadeFuncional(curCrg.getUnidadeFuncional());
		profCirurgia.setMbcProfAtuaUnidCirgs(escalaProfUnidCirg.getMbcProfAtuaUnidCirgs());
		profCirurgia.setIndResponsavel(false);
		profCirurgia.setIndRealizou(Boolean.FALSE);
		profCirurgia.setIndInclEscala(Boolean.TRUE);
		return profCirurgia;
	}

	/** Insere um atendimento Cirurgico para ambulatório e atualiza MbcCirurgias
	 * ORADB mbck_cec_rn.RN_CECP_ATU_ATEND
	 * @param escalaCirurgica
	 * @throws BaseException 
	 */
	private void atualizarAtendimentoMbcCirurgia(MbcControleEscalaCirurgica escalaCirurgica, String nomeMicrocomputador) throws BaseException {

		if(escalaCirurgica.getTipoEscala().equals(DominioTipoEscala.D)){

			List<MbcCirurgias> listCirurgias = this.getMbcCirurgiasDAO().pesquisarMbcCirurgiaControleEscala(escalaCirurgica.getId().getDtEscala(), escalaCirurgica.getAghUnidadesFuncionais());

			for(MbcCirurgias cirurgia: listCirurgias){
				
				Date dataInicioOuOrdem = cirurgia.getDataPrevisaoInicio() != null ? cirurgia.getDataPrevisaoInicio() : cirurgia.getDataInicioOrdem(); 

				if(cirurgia.getOrigemPacienteCirurgia().equals(DominioOrigemPacienteCirurgia.I)){ //origem = internação

					AghAtendimentos atendimento = getAghuFacade().obterAtendimentoContrEscCirurg(cirurgia.getPaciente().getCodigo());

					if(atendimento != null ){

						cirurgia.setAtendimento(atendimento);
						cirurgia.setMomentoAgenda(DominioMomentoAgendamento.DEF);
						this.getMbcCirurgiasRN().persistirCirurgia(cirurgia, nomeMicrocomputador, escalaCirurgica.getRapServidores().getDtFimVinculo());

					}else{
						cirurgia.setMomentoAgenda(DominioMomentoAgendamento.DEF);
						this.getMbcCirurgiasRN().persistirCirurgia(cirurgia, nomeMicrocomputador, escalaCirurgica.getRapServidores().getDtFimVinculo());
					}

				}else if(cirurgia.getOrigemPacienteCirurgia().equals(DominioOrigemPacienteCirurgia.A)){ //origem = ambulatorio

					AghAtendimentos atendimento = getAghuFacade().obterAtendimentoContrEscCirurg(cirurgia.getPaciente().getCodigo());

					if(atendimento != null ){
						cirurgia.setAtendimento(atendimento);
						cirurgia.setMomentoAgenda(DominioMomentoAgendamento.DEF);
						cirurgia.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.I);
						this.getMbcCirurgiasRN().persistirCirurgia(cirurgia, nomeMicrocomputador, escalaCirurgica.getRapServidores().getDtFimVinculo());
					}else{
						MbcProfCirurgias profCirurgia = this.getMbcProfCirurgiasDAO().obterEquipePorCirurgia(cirurgia.getSeq());
						RapServidores servidorPuc = null;
						if(profCirurgia != null && profCirurgia.getServidorPuc()!=null){
							// v_serv_resp = null;
							// v_vin_resp = null;
							servidorPuc = profCirurgia.getServidorPuc();
						}

						/**
						 * Inserir atendimento cirúrgico para cirurgias de ambulatório
						 */
						Integer atdSeq = this.inserirAtendimentoCirurgiaAmbulatorio(atendimento, cirurgia.getPaciente(), dataInicioOuOrdem, cirurgia.getUnidadeFuncional(), cirurgia.getEspecialidade(), servidorPuc);
						if (atdSeq != null) {
							this.getMbcCirurgiasDAO().flush();
							AghAtendimentos novoAtendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
							
							/**
							 * Atualizar MbcCirurgias
							 */
							cirurgia.setAtendimento(novoAtendimento);
							cirurgia.setMomentoAgenda(DominioMomentoAgendamento.DEF);
							this.getMbcCirurgiasRN().persistirCirurgia(cirurgia, nomeMicrocomputador, escalaCirurgica.getRapServidores().getDtFimVinculo());	
						}
					}

				}
			}
		}
	}

	/** 
	 * ORADB PROCEDURE mbck_mbc_rn.RN_MBCP_ATU_INS_ATD
	 * @param escalaCirurgica
	 * @throws BaseException
	 */
	public Integer inserirAtendimentoCirurgiaAmbulatorio(AghAtendimentos atendimento, AipPacientes paciente, Date dataInicioCirurgia, AghUnidadesFuncionais unidadeFuncional, AghEspecialidades especialidade, RapServidores servidorMovimento) throws BaseException {
		return mbcAtualizacaoProfissionalCirurgiaRN.inserirAtendimentoCirurgiaAmbulatorio(atendimento, paciente, dataInicioCirurgia, unidadeFuncional, especialidade, servidorMovimento);
	}


	/**
	 * ORADB mbck_cec_rn.rn_cecp_man_volume
	 * @param escalaCirurgica
	 * @throws BaseException
	 */
	private void manterVolumeRegistros(MbcControleEscalaCirurgica escalaCirurgica) throws BaseException {

		List<MbcControleEscalaCirurgica> list = this.getMbcControleEscalaCirurgicaDAO().pesquisarEscalasCirurgicasPorUnf(escalaCirurgica.getAghUnidadesFuncionais());
		AghParametros paramQuantRegCec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_QTD_REG_CEC);

		if(list!=null && list.size() > paramQuantRegCec.getVlrNumerico().intValue()){  
			/**
			 * Deleta o registro mais antigo, não pode ter mais que 30 registros para a unidade executora consultada
			 */
			this.iBlocoCirurgicoFacade.removerEscalaCirurgica(list.get(0));
		}
	}

	/**
	 * ORADB mbck_cec_rn.rn_cecp_fecha_atd_c
	 * @throws BaseException 
	 */
	private void realizarFechamentoAtendimentosCirurgicos(MbcControleEscalaCirurgica escalaCirurgica, String nomeMicrocomputador)
	throws BaseException {

		AghParametros paramDiasRetroaFech = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_RETROA_FECH_ATD_C);

		Calendar dataRetroFech = Calendar.getInstance();
		Integer dias = paramDiasRetroaFech.getVlrNumerico().intValue();
		dataRetroFech.add(Calendar.DAY_OF_MONTH, - dias);

		/**
		 * Lista todos atendimentos com ind_pac_atendimento = 'S' e origem = "C" para realizar o Fechamento dos atendimentos cirúrgicos
		 */
		List<AghAtendimentos> listAtendimentos = getAghuFacade().pesquisarAtendimentos(null, null, DominioPacAtendimento.S, Arrays.asList(DominioOrigemAtendimento.C));

		for(AghAtendimentos atendimento :listAtendimentos){
			if(atendimento.getDthrInicio().before(dataRetroFech.getTime())){

				atendimento.setDthrFim(new Date());
				atendimento.setIndPacAtendimento(DominioPacAtendimento.N);

				atualizarAghAtendimento(escalaCirurgica, nomeMicrocomputador, atendimento);
			}
		}
	}

	private void atualizarAghAtendimento(MbcControleEscalaCirurgica escalaCirurgica, String nomeMicrocomputador, AghAtendimentos atendimento)throws ApplicationBusinessException, BaseException {
		
		AghAtendimentos atendimentoOld = getPacienteFacade().clonarAtendimento(atendimento);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		getPacienteFacade().atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, escalaCirurgica.getRapServidores().getDtFimVinculo());
		
	}

	/**
	 * ORADB mbck_cec_rn.rn_cecp_ver_dthr_ger
	 * @param escalaCirurgica
	 * @throws ApplicationBusinessException
	 */
	public void verificarHorarioEscalaDefinitiva(MbcControleEscalaCirurgica escalaCirurgica) throws ApplicationBusinessException {

		AghParametros paramGeracaoEscala = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HORARIO_GER_ESCALA);

		Calendar dt = Calendar.getInstance();
		String valorHora = paramGeracaoEscala.getVlrTexto().replace("0","");
		dt.set(Calendar.HOUR_OF_DAY, Integer.valueOf(valorHora));  
		dt.set(Calendar.MINUTE, 0);
		dt.set(Calendar.SECOND, 0);

		if(escalaCirurgica.getTipoEscala().equals(DominioTipoEscala.D) && 
				DateUtil.validaHoraMaior(dt.getTime(), new Date())){
			throw new ApplicationBusinessException(MbcControleEscalaCirurgicaRNExceptionCode.MBC_00419);
			/*A Escala Definitiva esta sendo rodada antes do horário combinado.	Verifique!*/
		}
	}

	/**
	 * RN1: Verifica se já existe registro com os mesmos atributos
	 * 
	 * @param escalaCirurgica
	 * @throws BaseException
	 */
	private void verificarExistenciaRegistro(MbcControleEscalaCirurgica escalaCirurgica) throws BaseException {

		final Boolean existeEscalaCirurgica = this.getMbcControleEscalaCirurgicaDAO().verificaExistenciaPeviaDefinitivaPorUNFData(escalaCirurgica.getAghUnidadesFuncionais().getSeq(), escalaCirurgica.getId().getDtEscala(), null);

		if (existeEscalaCirurgica) {
			throw new ApplicationBusinessException(MbcControleEscalaCirurgicaRNExceptionCode.MENSAGEM_ERRO_REGISTRO_ESCALA_EXISTENTE);
		}

	}


	/**
	 * Inserir MbcControleEscalaCirurgica
	 * 
	 * @param escalaCirurgica
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserir(MbcControleEscalaCirurgica escalaCirurgica, String nomeMicrocomputador) throws BaseException {
	
		LOG.info("Inicio o insert MbcControleEscalaCirurgica");

		try {
			this.beginTransaction(TRANSACTION_TIMEOUT_24_HORAS);
			
			this.preInserir(escalaCirurgica, nomeMicrocomputador);
			this.getMbcControleEscalaCirurgicaDAO().persistir(escalaCirurgica);
			
		}catch(ApplicationBusinessException e) {
			throw new ApplicationBusinessException(e);
		}catch(Exception e) {
			this.rollbackTransaction();
		} finally {
			this.commitTransaction();
		}
		
		LOG.info("Fim o insert MbcControleEscalaCirurgica");
	}


	/**
	 * 
	 * @param controleEscalaCirurgica
	 * @throws BaseException
	 */
	public void verificarDadosModificados(MbcControleEscalaCirurgica controleEscalaCirurgica) throws BaseException {
		MbcControleEscalaCirurgica original = getMbcControleEscalaCirurgicaDAO().obterOriginal(controleEscalaCirurgica);

		if (CoreUtil.modificados(controleEscalaCirurgica.getId().getDtEscala(),original.getId().getDtEscala())) {
			/* Não é permitido alterar a data da escala. */
			throw new ApplicationBusinessException(MbcControleEscalaCirurgicaRNExceptionCode.MBC_00417);
		}

		if (CoreUtil.modificados(controleEscalaCirurgica.getTipoEscala(),original.getTipoEscala()) && controleEscalaCirurgica.getTipoEscala().equals(DominioTipoEscala.P)
				&& original.getTipoEscala().equals(DominioTipoEscala.D)){
			/* Não é permitido rodar uma escala Prévia após ter executado uma escala Definitiva. */
			throw new ApplicationBusinessException(MbcControleEscalaCirurgicaRNExceptionCode.MBC_00418);
		}


	}

	/**
	 * Melhoria #42042
	 * @param pacCodigo
	 * @param cirurgia
	 * @throws BaseException 
	 */
	public void inserirAtendimentoPreparoCirurgia(AipPacientes paciente, MbcCirurgias cirurgia, String nomeMicroComputador) throws BaseException {
		if (cirurgia.getAtendimento() == null) {
			MbcProfCirurgias profCirurgia = this.getMbcProfCirurgiasDAO().obterEquipePorCirurgia(cirurgia.getSeq());
			RapServidores servidorPuc = null;
			if(profCirurgia != null && profCirurgia.getServidorPuc()!=null){
				// v_serv_resp = null;
				// v_vin_resp = null;
				servidorPuc = profCirurgia.getServidorPuc();
				profCirurgia.setCirurgia(cirurgia);
			}

			/**
			 * Inserir atendimento cirúrgico para cirurgias de ambulatório
			 */
			
			// Código ajustado para resolver incidente #50864
			Date dataInicioOuOrdem = null;
			if (cirurgia.getDataPrevisaoInicio() != null) {
				
				dataInicioOuOrdem = DateUtil.validaDataMaior(cirurgia.getDataPrevisaoInicio(), new Date()) ?
						new Date() : cirurgia.getDataPrevisaoInicio();			
			} else {
				if (cirurgia.getDataInicioOrdem() != null) {
					dataInicioOuOrdem = DateUtil.validaDataMaior(cirurgia.getDataInicioOrdem(), new Date()) ?
							new Date() : cirurgia.getDataInicioOrdem();
				}
			}
			AghAtendimentos atendimento = getAghuFacade().obterAtendimentoContrEscCirurg(cirurgia.getPaciente().getCodigo());
			
			Integer atdSeq = this.inserirAtendimentoCirurgiaAmbulatorio(atendimento, cirurgia.getPaciente(), dataInicioOuOrdem, cirurgia.getUnidadeFuncional(), cirurgia.getEspecialidade(), servidorPuc);
			if (atdSeq != null) {
				AghAtendimentos novoAtendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
				/**
				 * Atualizar MbcCirurgias
				 */
				cirurgia.setAtendimento(novoAtendimento);
				this.getMbcCirurgiasRN().persistirCirurgia(cirurgia, nomeMicroComputador, null);	
			}
		}
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	protected MbcCirurgiasRN getMbcCirurgiasRN() {
		return mbcCirurgiasRN;
	}

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO() {
		return mbcHorarioTurnoCirgDAO;
	}

	protected MbcProfCirurgiasRN getMbcProfCirurgiasRN() {
		return mbcProfCirurgiasRN;
	}

	protected MbcEscalaProfUnidCirgDAO getMbcEscalaProfUnidCirgDAO() {
		return mbcEscalaProfUnidCirgDAO;
	}

}
