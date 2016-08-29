package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTurnosDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgId;
import br.gov.mec.aghu.model.MbcMvtoSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class SalaCirurgicaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(SalaCirurgicaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcMvtoSalaCirurgicaDAO mbcMvtoSalaCirurgicaDAO;

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;

	@Inject
	private MbcTurnosDAO mbcTurnosDAO;

	@EJB 
	private IBlocoCirurgicoCadastroApoioFacade iBlocoCirurgicoCadastroApoioFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = 105920408881874799L;
	protected enum SalaCirurgicaRNExceptionCode implements BusinessExceptionCode {
		MBC_00200,MBC_00308,MBC_00309,MBC_01263;
	}

	/**@throws BaseException 
	 * @ORADB MBCT_SCI_BRI
	 *  1.	Atualiza CRIADO_EM para data atual;
	   2.	(mbck_sci_rn.rn_scip_ver_situacao)
		a.	Se checkbox ativo desmarcado, e Motivo Inativação Vazio, apresentar exceção MBC-00200: “É obrigatório informar o motivo da inativação da sala cirúrgica” 
		b.	Se checkbox ativo marcado, remover texto do motivo inativação, caso esteja preenchido.
		3.	(mbck_sci_rn.rn_scip_ver_unf)
		a.	Se a unidade cirúrgica estiver inativa, apresentar exceção: MBC-00308 - “Unidade funcional deve estar ativa”
		b.	Se a Unidade cirúrgica não tiver característica ‘Unid Executora Cirurgias’, apresentar exceção: MBC-00309 – “Unidade funcional deve ter característica de unidade cirúrgica” 
		4.	Atualiza servidor.
	 */
	private void preInserir(MbcSalaCirurgica salaCirurgica) throws BaseException  {
		salaCirurgica.setCriadoEm(new Date());
		//@TODO nao definido ainda como será 
		salaCirurgica.setEspecial(false);
		rnScipVerSituacao(salaCirurgica);
		rnScipVerUnf(salaCirurgica.getUnidadeFuncional());
		salaCirurgica.setServidor(servidorLogadoFacade.obterServidorLogado());
	}
	
	
	/**
	 * @ORADB MBCT_SCI_BRU
	 * @param salaCirurgica
	 * @param obterLoginUsuarioLogado
	 * @throws BaseException
	 */
	private void preAtualizar(MbcSalaCirurgica salaCirurgica) throws BaseException  {
		rnScipVerSituacao(salaCirurgica);
		AghUnidadesFuncionais unidadeFuncional = getAghuFacade().obterUnidadeFuncional(salaCirurgica.getUnidadeFuncional().getSeq());
		rnScipVerUnf(unidadeFuncional);
		salaCirurgica.setServidor(servidorLogadoFacade.obterServidorLogado());
	}
	
	/**
	 * @throws BaseException 
	 *a.	Se checkbox ativo desmarcado, e Motivo Inativação Vazio, apresentar exceção MBC-00200: “É obrigatório informar o motivo da inativação da sala cirúrgica” 
	 */
	protected void verificarMotivoInativacao(MbcSalaCirurgica salaCirurgica) throws BaseException {
		if (salaCirurgica.getSituacao() == DominioSituacao.I && StringUtils.isEmpty(salaCirurgica.getMotivoInat())) {
			throw new ApplicationBusinessException(SalaCirurgicaRNExceptionCode.MBC_00200);
		}
	}
	
	protected MbcSalaCirurgica verificarRemoverTextoMotivoInatSituacaoAtivo(MbcSalaCirurgica salaCirurgica){
		if(salaCirurgica.getSituacao() == DominioSituacao.A && StringUtils.isNotEmpty(salaCirurgica.getMotivoInat())){
			salaCirurgica.setMotivoInat("");
		}
		return salaCirurgica;
	}
	
	public void inserir(MbcSalaCirurgica salaCirurgica) throws BaseException {
		preInserir(salaCirurgica);
		this.getMbcSalaCirurgicaDAO().persistir(salaCirurgica);
		posInserir(salaCirurgica);
	}
	
	private void posInserir(MbcSalaCirurgica salaCirurgica) throws BaseException {
		atualizarMovimentoSalasCirurgicas(salaCirurgica, false);
		inserirCaracteristica(salaCirurgica);
	}

	/**
	 * @ORADB RN_SCIP_ATU_CARACT
	 * @param salaCirurgica
	 * @param usuarioLogado
	 * @throws BaseException 
	 * @throws ApplicationBusinessException 
	 */
	private void inserirCaracteristica(MbcSalaCirurgica salaCirurgica) throws ApplicationBusinessException, BaseException {
		if (getParametroFacade().verificarExisteAghParametro(AghuParametrosEnum.P_AGHU_SCI_TURNOS_INSERIR_CARACTERISTICA)) {
			AghParametros param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_SCI_TURNOS_INSERIR_CARACTERISTICA);
			String turnosPadrao = param.getVlrTexto();
			String[] turnosPadraoArray = turnosPadrao.split(",");
			for (String turno : turnosPadraoArray) {
				MbcTurnos mbcTurno = getMbcTurnosDAO().obterOriginal(turno);
				if (mbcTurno != null) {
					MbcHorarioTurnoCirgId cirgId = new MbcHorarioTurnoCirgId();
					cirgId.setTurno(mbcTurno.getTurno());
					cirgId.setUnfSeq(salaCirurgica.getUnidadeFuncional().getSeq());
					MbcHorarioTurnoCirg mbcHorarioTurnoCirg = getMbcHorarioTurnoCirgDAO().obterPorChavePrimaria(cirgId);
					//Insere caracteristicas minimas somente se tem horario turno cadastrado para a unidade funcional
					if (mbcHorarioTurnoCirg != null) {
						getBlocoCirurgicoCadastroApoioFacade().gravarMbcCaracteristicaSalaCirg(montarCaracteristica(salaCirurgica, mbcTurno, DominioDiaSemana.SEG, mbcHorarioTurnoCirg));
						getBlocoCirurgicoCadastroApoioFacade().gravarMbcCaracteristicaSalaCirg(montarCaracteristica(salaCirurgica, mbcTurno, DominioDiaSemana.TER, mbcHorarioTurnoCirg));
						getBlocoCirurgicoCadastroApoioFacade().gravarMbcCaracteristicaSalaCirg(montarCaracteristica(salaCirurgica, mbcTurno, DominioDiaSemana.QUA, mbcHorarioTurnoCirg));
						getBlocoCirurgicoCadastroApoioFacade().gravarMbcCaracteristicaSalaCirg(montarCaracteristica(salaCirurgica, mbcTurno, DominioDiaSemana.QUI, mbcHorarioTurnoCirg));
						getBlocoCirurgicoCadastroApoioFacade().gravarMbcCaracteristicaSalaCirg(montarCaracteristica(salaCirurgica, mbcTurno, DominioDiaSemana.SEX, mbcHorarioTurnoCirg));
					}
				}
			}
		}
	}

	private MbcCaracteristicaSalaCirg montarCaracteristica(MbcSalaCirurgica salaCirurgica, MbcTurnos mbcTurno, DominioDiaSemana diaSemana, MbcHorarioTurnoCirg mbcHorarioTurnoCirg) {
		MbcCaracteristicaSalaCirg caracteristicaSalaCirg = new MbcCaracteristicaSalaCirg();
		caracteristicaSalaCirg.setMbcSalaCirurgica(salaCirurgica);
		
		caracteristicaSalaCirg.setMbcHorarioTurnoCirg(mbcHorarioTurnoCirg);
		
		caracteristicaSalaCirg.setDiaSemana(diaSemana);
		caracteristicaSalaCirg.setCirurgiaParticular(Boolean.FALSE);
		caracteristicaSalaCirg.setIndUrgencia(Boolean.FALSE);
		caracteristicaSalaCirg.setIndDisponivel(Boolean.TRUE);
		caracteristicaSalaCirg.setSituacao(DominioSituacao.A);
		
		return caracteristicaSalaCirg;
		
	}


	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	private MbcTurnosDAO getMbcTurnosDAO() {
		return mbcTurnosDAO;
	}
	
	private MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO() {
		return mbcSalaCirurgicaDAO;
	}
	
	private MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO() {
		return mbcHorarioTurnoCirgDAO;
	}

	public void atualizar(MbcSalaCirurgica salaCirurgica) throws BaseException {
		preAtualizar(salaCirurgica);
		this.getMbcSalaCirurgicaDAO().atualizar(salaCirurgica);
		posAtualizar(salaCirurgica);
	}
	
	
	/**
	 * @ORADB MBCK_SCI_RN.RN_SCIP_ATU_MVTO
	 * @param salaCirurgica
	 * @param usuarioLogado
	 * @throws BaseException 
	 */
	protected void atualizarMovimentoSalasCirurgicas(MbcSalaCirurgica salaCirurgica, boolean update) throws BaseException {
		MbcMvtoSalaCirurgica mbcMvtoSalaCirurgica = new MbcMvtoSalaCirurgica();
		if (update) {
			MbcMvtoSalaCirurgica mbcMvtoSalaCirurgicaUpdate = getMbcMvtoSalaCirurgicaDAO().obterUltimoMovimentoPorId(salaCirurgica.getId());
			if (mbcMvtoSalaCirurgicaUpdate == null) {
				throw new ApplicationBusinessException(SalaCirurgicaRNExceptionCode.MBC_01263);
			}
			setDataFimMvto(mbcMvtoSalaCirurgicaUpdate);
			getBlocoCirurgicoCadastroApoioFacade().persistirMbcMvtoSalaCirurgica(mbcMvtoSalaCirurgicaUpdate);
			mbcMvtoSalaCirurgica.setMbcMvtoSalaCirurgica(mbcMvtoSalaCirurgicaUpdate);
		} else {
			mbcMvtoSalaCirurgica.setMbcMvtoSalaCirurgica(null);
		}
		populaMbcvtoSalaCirurgica(mbcMvtoSalaCirurgica, salaCirurgica);
		getBlocoCirurgicoCadastroApoioFacade().persistirMbcMvtoSalaCirurgica(mbcMvtoSalaCirurgica);
	}
	
	private void populaMbcvtoSalaCirurgica(MbcMvtoSalaCirurgica mbcMvtoSalaCirurgica,MbcSalaCirurgica salaCirurgica) {
		mbcMvtoSalaCirurgica.setDtInicioMvto(new Date());
		mbcMvtoSalaCirurgica.setDtFimMvto(null);
		mbcMvtoSalaCirurgica.setMbcSalaCirurgica(salaCirurgica);
		mbcMvtoSalaCirurgica.setNome(salaCirurgica.getNome());
		mbcMvtoSalaCirurgica.setIndEspecial(salaCirurgica.getEspecial());
		mbcMvtoSalaCirurgica.setMotivoInat(salaCirurgica.getMotivoInat());
		mbcMvtoSalaCirurgica.setMbcTipoSala(salaCirurgica.getMbcTipoSala());
		mbcMvtoSalaCirurgica.setVisivelMonitor(salaCirurgica.getVisivelMonitor());
		mbcMvtoSalaCirurgica.setSituacao(salaCirurgica.getSituacao());
	}


	private IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return iBlocoCirurgicoCadastroApoioFacade;
	}


	protected void setDataFimMvto(MbcMvtoSalaCirurgica mbcMvtoSalaCirurgica) {
		Date hoje = new Date();
		if (DateUtil.truncaData(mbcMvtoSalaCirurgica.getDtInicioMvto()).compareTo(DateUtil.truncaData(hoje)) == 0) {
			mbcMvtoSalaCirurgica.setDtFimMvto(mbcMvtoSalaCirurgica.getDtInicioMvto());
		} else {
			mbcMvtoSalaCirurgica.setDtFimMvto(DateUtil.adicionaDias(hoje, -1));
		}
	}


	protected MbcMvtoSalaCirurgicaDAO getMbcMvtoSalaCirurgicaDAO() {
		return mbcMvtoSalaCirurgicaDAO;
	}
	
	
	/**
	 * @ORADB MBCT_SCI_ARU
	 * @param salaCirurgica
	 * @param usuarioLogado
	 * @throws BaseException
	 */
	public void posAtualizar(MbcSalaCirurgica salaCirurgica) throws BaseException {
		MbcSalaCirurgica salaCirurgicaOriginal = getMbcSalaCirurgicaDAO().obterOriginal(salaCirurgica);
		
		if (CoreUtil.modificados(salaCirurgica.getId(),salaCirurgicaOriginal.getId())
				|| CoreUtil.modificados(salaCirurgica.getSituacao(), salaCirurgicaOriginal.getSituacao())
				|| CoreUtil.modificados(salaCirurgica.getEspecial(), salaCirurgicaOriginal.getEspecial())
				|| CoreUtil.modificados(salaCirurgica.getNome(), salaCirurgicaOriginal.getNome())
				|| CoreUtil.modificados(salaCirurgica.getVisivelMonitor(), salaCirurgicaOriginal.getVisivelMonitor()) //Adicionado no AGHU. No AGH há um bug que desconsidera este campo.
				|| CoreUtil.modificados(salaCirurgica.getMbcTipoSala(), salaCirurgicaOriginal.getMbcTipoSala()) //Adicionado no AGHU. No AGH há um bug que desconsidera este campo.
				|| CoreUtil.modificados(salaCirurgica.getMotivoInat(), salaCirurgicaOriginal.getMotivoInat())) {
			atualizarMovimentoSalasCirurgicas(salaCirurgica, true);
			
		}
		
	}
	
	
	
	/**
		a.	Se a unidade cirúrgica estiver inativa, apresentar exceção: MBC-00308 - “Unidade funcional deve estar ativa”
	 * @throws BaseException 
	 */
	protected void verificaUnidadeCirurgicaAtiva(AghUnidadesFuncionais unidadeCirurgica) throws BaseException {
		if (! unidadeCirurgica.isAtivo()) {
			throw new ApplicationBusinessException(SalaCirurgicaRNExceptionCode.MBC_00308);
		}
	}
	
	/**
	 * @ORADB @ORADB mbck_sci_rn.rn_scip_ver_situacao
	 * @param unidadeCirurgica
	 * @throws BaseException 
	 */
	private void rnScipVerSituacao(MbcSalaCirurgica salaCirurgica) throws BaseException {
		verificarMotivoInativacao(salaCirurgica);
		verificarRemoverTextoMotivoInatSituacaoAtivo(salaCirurgica);
	}
	
	/**
	 * @ORADB mbck_sci_rn.rn_scip_ver_unf
	 * @param unidadeCirurgica
	 * @throws BaseException 
	 */
	private void rnScipVerUnf(AghUnidadesFuncionais unidadeCirurgica) throws BaseException {
		verificaUnidadeCirurgicaAtiva(unidadeCirurgica);
		verificarCaracteristicaUnidadeCirurgica(unidadeCirurgica);
	}

	
	protected void verificarCaracteristicaUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) throws BaseException {
		if (!getAghuFacade().verificarCaracteristicaUnidadeFuncional(unidadeCirurgica.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS)) {
			throw new ApplicationBusinessException(SalaCirurgicaRNExceptionCode.MBC_00309);
		}
	}
	
	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}
}
