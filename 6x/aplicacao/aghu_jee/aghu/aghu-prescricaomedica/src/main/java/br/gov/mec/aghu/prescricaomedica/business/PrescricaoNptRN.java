package br.gov.mec.aghu.prescricaomedica.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNptId;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmItemPrescricaoNptId;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.prescricaomedica.dao.AfaFormulaNptPadraoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.AfaTipoComposicoesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmComposicaoPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialDiversoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmUnidadeMedidaMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaDadosAtendimentoUrgenciaVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaDadosHospitalDiaVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaDadosInternacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmComposicaoPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificaAtendimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificaIndicadoresConvenioInternacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

//@ORADB MPMK_PNP_RN
@Stateless
public class PrescricaoNptRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3503352423981462910L;

	private static final Log LOG = LogFactory.getLog(PrescricaoNptRN.class);

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;

	@EJB
	private ManterPrescricaoDietaRN manterPrescricaoDietaRN;
	
	@Inject
	private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;
	
	@Inject
	private MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO;
	
	@Inject
	private MpmProcedEspecialDiversoDAO mpmProcedEspecialDiversoDAO;

	@Inject
	private AfaFormulaNptPadraoDAO afaFormulaNptPadraoDAO;
	
	@EJB
	private ComposicaoPrescricaoNptRN composicaoPrescricaoNptRN;
	
	@Inject
	private MpmComposicaoPrescricaoNptDAO composicaoPrescricaoNptDAO;
	
	@Inject
	private AfaTipoComposicoesDAO afaTipoComposicoesDAO;
	
	@Inject
	private AfaTipoVelocAdministracoesDAO afaTipoVelocAdministracoesDAO;
	
	@Inject
	private AfaComponenteNptDAO afaComponenteNptDAO;
	
	@Inject
	private AfaFormaDosagemDAO afaFormaDosagemDAO;
	
	@Inject 
	private MpmUnidadeMedidaMedicaDAO  mpmUnidadeMedidaMedicaDAO;
	
	@Inject
	private MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO;
	
	@EJB
	private ItemPrescricaoNptRN itemPrescricaoNptRN;
	
	
		

	protected enum PrescricaoNptExceptionCode implements
	BusinessExceptionCode {
		ERRO_LEITURA_VO, MPM_01120, MPM_00992, MPM_01012, MPM_01025, MPM_NPT_PREENCHER_COMPOSICAO;
	}
	
	public void persistir(MpmPrescricaoNptVO vo, String nomeMicrocomputador, Boolean desconsiderarItensNulos, Boolean isUpdate) throws ApplicationBusinessException {
		persistir(false, vo, nomeMicrocomputador, desconsiderarItensNulos, isUpdate);
	}

	public void persistir(boolean edicaoNValidado, MpmPrescricaoNptVO vo, String nomeMicrocomputador, Boolean desconsiderarItensNulos, Boolean isUpdate) throws ApplicationBusinessException {
		Date dataFimVinculoServidor = servidorLogadoFacade.obterServidorLogado().getDtFimVinculo();
		
		if(isUpdate) {
			MpmPrescricaoNpt prescricao = mpmPrescricaoNptDAO.obterPorChavePrimaria(new MpmPrescricaoNptId(vo.getAtdSeq(), vo.getSeq()));
			try {
				this.parseVOtoEntidade(prescricao, vo, true);
				this.atualizar(prescricao, nomeMicrocomputador, dataFimVinculoServidor);
				this.validarComposicoes(vo);
				this.persistirComposicoes(edicaoNValidado, vo, prescricao, desconsiderarItensNulos, isUpdate);
				} catch (IllegalAccessException | InvocationTargetException e) {
				throw new ApplicationBusinessException(
						PrescricaoNptExceptionCode.ERRO_LEITURA_VO);
			}
		} else {
			MpmPrescricaoNpt prescricao = new MpmPrescricaoNpt();
			try {
				this.parseVOtoEntidade(prescricao, vo, isUpdate);
				this.inserir(prescricao, nomeMicrocomputador, dataFimVinculoServidor);
				this.validarComposicoes(vo);
				this.persistirComposicoes(edicaoNValidado, vo, prescricao, desconsiderarItensNulos, isUpdate);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new ApplicationBusinessException(
						PrescricaoNptExceptionCode.ERRO_LEITURA_VO);
			}
		}
	}
	
	private void validarComposicoes(MpmPrescricaoNptVO vo) throws ApplicationBusinessException {
		boolean nenhumaComposicaoValida = true;
		for (MpmComposicaoPrescricaoNptVO item : vo.getComposicoes()) {
			if(item.getVelocidadeAdministracao() != null) {
				nenhumaComposicaoValida = false;
				break;
			}
		}
		if (nenhumaComposicaoValida) {
			throw new ApplicationBusinessException(PrescricaoNptExceptionCode.MPM_NPT_PREENCHER_COMPOSICAO);
		}
	}
	
	private void persistirComposicoes(boolean edicaoNValidado, MpmPrescricaoNptVO vo, MpmPrescricaoNpt prescricao, Boolean desconsiderarItensNulos, Boolean isUpdate) throws IllegalAccessException, InvocationTargetException, ApplicationBusinessException {
		for (MpmComposicaoPrescricaoNptVO item : vo.getComposicoes()) {
			if(desconsiderarItensNulos) {
				if(item.getVelocidadeAdministracao() == null) {
					// Se for update e não for inclusão de NPT com DominioIndPendenteItemPrescricao.P
					if (isUpdate && !edicaoNValidado) {
						for (MpmItemPrescricaoNptVO componente : item.getComponentes()) {
							MpmItemPrescricaoNptId id = new MpmItemPrescricaoNptId(componente.getCptPnpAtdSeq(), componente.getCptPnpSeq(), componente.getCptSeqp(), componente.getSeqp());
							MpmItemPrescricaoNpt componenteRemover = this.mpmItemPrescricaoNptDAO.obterPorChavePrimaria(id);
							this.mpmItemPrescricaoNptDAO.remover(componenteRemover);
						}
						MpmComposicaoPrescricaoNpt composicaoRemover = composicaoPrescricaoNptDAO.obterPorChavePrimaria(new MpmComposicaoPrescricaoNptId(item.getPnpAtdSeq(), item.getPnpSeq(), item.getSeqp()));
						this.composicaoPrescricaoNptDAO.remover(composicaoRemover);
					}
					continue;
				}
			}
			if(edicaoNValidado || composicaoPrescricaoNptDAO.obterOriginal(new MpmComposicaoPrescricaoNptId(item.getPnpAtdSeq(), item.getPnpSeq(), item.getSeqp())) == null) {
				MpmComposicaoPrescricaoNpt entity = new MpmComposicaoPrescricaoNpt();
				BeanUtils.copyProperties(entity, item);
				Short seqp = composicaoPrescricaoNptDAO.obterUltimoSeqpComposicao(prescricao.getId().getSeq(), prescricao.getId().getAtdSeq());
				entity.setId(new MpmComposicaoPrescricaoNptId(prescricao.getId().getAtdSeq(),prescricao.getId().getSeq(), (short) (seqp + Short.valueOf("1"))));
				entity.setAfaTipoComposicoes(afaTipoComposicoesDAO.obterPorChavePrimaria(item.getTicSeq()));
				entity.setAfaTipoVelocAdministracoes(afaTipoVelocAdministracoesDAO.obterPorChavePrimaria(item.getTvaSeq()));
				entity.setMpmPrescricaoNpts(prescricao);
				this.composicaoPrescricaoNptRN.inserir(entity);
				this.persistirComponentes(edicaoNValidado, item, entity, desconsiderarItensNulos, isUpdate);
			} else {
				MpmComposicaoPrescricaoNpt entity = composicaoPrescricaoNptDAO.obterPorChavePrimaria(new MpmComposicaoPrescricaoNptId(item.getPnpAtdSeq(), item.getPnpSeq(), item.getSeqp()));
				if(entity != null) {
					BeanUtils.copyProperties(entity, item);
					entity.setAfaTipoComposicoes(afaTipoComposicoesDAO.obterPorChavePrimaria(item.getTicSeq()));
					entity.setAfaTipoVelocAdministracoes(afaTipoVelocAdministracoesDAO.obterPorChavePrimaria(item.getTvaSeq()));
					entity.setMpmPrescricaoNpts(prescricao);
					this.composicaoPrescricaoNptRN.atualizar(entity);
					this.persistirComponentes(edicaoNValidado, item, entity, desconsiderarItensNulos, isUpdate);
				}
			}
		}
	}
	
	private void persistirComponentes(boolean edicaoNValidado, MpmComposicaoPrescricaoNptVO vo, MpmComposicaoPrescricaoNpt composicao, Boolean desconsiderarItensNulos, Boolean isUpdate) throws IllegalAccessException, InvocationTargetException, ApplicationBusinessException {
		for (MpmItemPrescricaoNptVO item : vo.getComponentes()) {
			if(desconsiderarItensNulos) {
				if(item.getQtdePrescrita()  == null) {
					// Se for update e não for inclusão de NPT com DominioIndPendenteItemPrescricao.P
					if (isUpdate && !edicaoNValidado) {
						MpmItemPrescricaoNptId id = new MpmItemPrescricaoNptId(item.getCptPnpAtdSeq(), item.getCptPnpSeq(), item.getCptSeqp(), item.getSeqp());
						MpmItemPrescricaoNpt componenteRemover = this.mpmItemPrescricaoNptDAO.obterPorChavePrimaria(id);
						this.mpmItemPrescricaoNptDAO.remover(componenteRemover);
					}
					continue;
				}
			}			
			if(edicaoNValidado || mpmItemPrescricaoNptDAO.obterOriginal(new MpmItemPrescricaoNptId(item.getCptPnpAtdSeq(), item.getCptPnpSeq(), item.getCptSeqp(), item.getSeqp())) == null) {
				MpmItemPrescricaoNpt entity = new MpmItemPrescricaoNpt();
				BeanUtils.copyProperties(entity, item);
				Short seqp = mpmItemPrescricaoNptDAO.obterUltimoSeqpComponente(composicao.getId().getPnpAtdSeq(), composicao.getId().getPnpSeq(), composicao.getId().getSeqp());
				entity.setId(new MpmItemPrescricaoNptId(composicao.getId().getPnpAtdSeq(), composicao.getId().getPnpSeq(), composicao.getId().getSeqp(), (short) (seqp + Short.valueOf("1"))));
				entity.setMpmComposicaoPrescricaoNpts(composicao);
				entity.setAfaComponenteNpts(afaComponenteNptDAO.obterPorChavePrimaria(item.getCnpMedMatCodigo()));
				entity.setAfaFormaDosagens(afaFormaDosagemDAO.obterPorChavePrimaria(item.getFdsSeq()));
				entity.setUnidadeMedidaMedicas(mpmUnidadeMedidaMedicaDAO.obterPorChavePrimaria(item.getUmmSeq()));
				this.itemPrescricaoNptRN.inserir(entity);
			} else {
				MpmItemPrescricaoNptId id = new MpmItemPrescricaoNptId(item.getCptPnpAtdSeq(), item.getCptPnpSeq(), item.getCptSeqp(), item.getSeqp());
				MpmItemPrescricaoNpt entity = this.mpmItemPrescricaoNptDAO.obterPorChavePrimaria(id);
				BeanUtils.copyProperties(entity, item);
				entity.setMpmComposicaoPrescricaoNpts(composicao);
				entity.setAfaComponenteNpts(afaComponenteNptDAO.obterPorChavePrimaria(item.getCnpMedMatCodigo()));
				entity.setAfaFormaDosagens(afaFormaDosagemDAO.obterPorChavePrimaria(item.getFdsSeq()));
				entity.setUnidadeMedidaMedicas(mpmUnidadeMedidaMedicaDAO.obterPorChavePrimaria(item.getUmmSeq()));
				this.itemPrescricaoNptRN.atualizar(entity);
				
			}
		}
	}
	
	private void parseVOtoEntidade(MpmPrescricaoNpt prescricao, MpmPrescricaoNptVO vo, Boolean isUpdate) throws IllegalAccessException, InvocationTargetException, ApplicationBusinessException {		
			BeanUtils.copyProperties(prescricao, vo);
			if(vo.getSegueGotejoPadrao() == null) {
				prescricao.setSegueGotejoPadrao(true);
			}
			if(vo.getBombaInfusao() == null) {
				prescricao.setBombaInfusao(true);
			}
			prescricao.setAfaFormulaNptPadrao(afaFormulaNptPadraoDAO.obterOriginal(vo.getFnpSeq()));
			prescricao.setProcedEspecialDiversos(mpmProcedEspecialDiversoDAO.obterPeloId(vo.getPedSeq()));
			prescricao.setPrescricaoMedica(vo.getPrescricaoMedica());
			if(!isUpdate) {
				prescricao.setId(new MpmPrescricaoNptId(vo.getAtdSeq(), mpmPrescricaoNptDAO.getNextVal(SequenceID.MPM_PNP_SQ1)));
			}			
			if(vo.getPnpSeq() != null && vo.getPnpAtdSeq() != null) {
				MpmPrescricaoNpt origem =  mpmPrescricaoNptDAO.obterPorChavePrimaria(new MpmPrescricaoNptId(vo.getPnpAtdSeq(), vo.getPnpSeq()));
				prescricao.setPrescricaoNpts(origem);
				if(!isUpdate){
					this.verificarSobreposicaoDatas(prescricao, origem);
				}
			}
	}

	protected void inserir(MpmPrescricaoNpt prescricao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		this.preInserir(prescricao, nomeMicrocomputador, dataFimVinculoServidor);
		mpmPrescricaoNptDAO.persistir(prescricao);
	}
	
	
	public void atualizar(MpmPrescricaoNpt prescricao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		this.preAtualizar(prescricao, nomeMicrocomputador, dataFimVinculoServidor);
		mpmPrescricaoNptDAO.atualizar(prescricao);
	}
	
	//MPMT_PNP_BRU
	protected void preAtualizar(MpmPrescricaoNpt prescricao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		
		MpmPrescricaoNpt original = mpmPrescricaoNptDAO.obterOriginal(prescricao.getId());

		VerificaAtendimentoVO atendimento = new VerificaAtendimentoVO();
		FatConvenioSaudePlanoId convenioSaude = new FatConvenioSaudePlanoId();
		
		
		VerificaIndicadoresConvenioInternacaoVO indicadores = new VerificaIndicadoresConvenioInternacaoVO();
		
		Integer phi = null;
		
		/* Verifica se proced. especial diverso est¿tivo */
		if(CoreUtil.modificados(prescricao.getProcedEspecialDiversos(), original.getProcedEspecialDiversos())) {
			rnPnppVerProcDiv(prescricao.getProcedEspecialDiversos());
		}
		
		/* Verifica se f¿rmula npt padr¿est¿tiva */
		if(CoreUtil.modificados(prescricao.getAfaFormulaNptPadrao(), original.getAfaFormulaNptPadrao())) {
			rnPnppVerFormNpt(prescricao.getAfaFormulaNptPadrao());
		}

		/* Verifica vig¿ia do atendimento */
		if(prescricao.getDthrInicio() != null) {
			atendimento = prescricaoMedicaRN.verificaAtendimento(prescricao.getDthrInicio(), prescricao.getDthrFim(), 
					prescricao.getId().getAtdSeq(), null, null, null);
		}
	
		/* Busca  endere¿do conv¿o e primeiro e ¿ltimo evento da interna¿, */
		/* hospital dia ou atend. urg¿ia que originou o atendimento. */
		preencherIdconvenioSaude(convenioSaude, atendimento);
		
		/* Busca SEQ Procedimento hospitalar Interno (PHI) */
		if(prescricao.getProcedEspecialDiversos() != null) {
			phi = prescricaoMedicaRN.verificaProcedimentoHospitalarInterno(null, null, prescricao.getProcedEspecialDiversos().getSeq());
		}

		/* Busca indicadores do conv¿o */
		indicadores = prescricaoMedicaRN.verificaIndicadoresConvenioInternacao(convenioSaude.getCnvCodigo(), convenioSaude.getSeq(), phi);
		
		/* Verificar se o convênio exige justificativa para o procedimento */
		if(prescricao.getJustificativa() == null && indicadores.getIndExigeJustificativa()) {
			if(prescricao.getProcedEspecialDiversos() != null) {
				throw new ApplicationBusinessException(
						PrescricaoNptExceptionCode.MPM_01120);
			}
		}
		
		/* Verifica a exist¿ia de uma prescri¿ m¿ca para esta prescri¿ */
		/* Se ind_pendente = Sim atualizar dthr_mvto_pendente da prescri¿ m¿ca */
		/*  se a data estiver nula  */
		if(CoreUtil.modificados(prescricao.getDthrFim(), original.getDthrFim()) && prescricao.getDthrFim() != null) {
			prescricaoMedicaRN.verificaPrescricaoMedicaUpdate(prescricao.getId().getAtdSeq(), prescricao.getDthrInicio(), prescricao.getDthrFim(), 
					prescricao.getAlteradoEm(), prescricao.getIndPendente(), "U", 
					nomeMicrocomputador, dataFimVinculoServidor);
			
			/*  Quando alterado dthr fim atribuir o servidor correspondente */
			prescricao.setServidorMovimentado(servidorLogadoFacade.obterServidorLogado());
			
			/* esta l¿gica substitui a l¿gica gerado pelo HEADSTART para alterado_em */
			if(!(DominioIndPendenteItemPrescricao.P.equals(prescricao.getIndPendente()) && DominioIndPendenteItemPrescricao.P.equals(original.getIndPendente()))) {
				prescricao.setAlteradoEm(new Date());
			}
		}

	}

	private void preencherIdconvenioSaude(FatConvenioSaudePlanoId convenioSaude, VerificaAtendimentoVO atendimento) throws ApplicationBusinessException {
		BuscaDadosHospitalDiaVO dadosHospDia = new BuscaDadosHospitalDiaVO();
		BuscaDadosInternacaoVO dadosInternacao = new BuscaDadosInternacaoVO();
		BuscaDadosAtendimentoUrgenciaVO dadosAtdUrgencia = new BuscaDadosAtendimentoUrgenciaVO();
		if(atendimento.getSeqHospitalDia() != null) {
			dadosHospDia = prescricaoMedicaRN.buscaDadosHospitalDia(atendimento.getSeqHospitalDia());
			convenioSaude.setCnvCodigo(dadosHospDia.getCodigoConvenioSaude());
			convenioSaude.setSeq(dadosHospDia.getCspSeq());
		} else {
			if(atendimento.getSeqInternacao() != null) {
				dadosInternacao = prescricaoMedicaRN.buscaDadosInternacao(atendimento.getSeqInternacao());
				convenioSaude.setCnvCodigo(dadosInternacao.getCodigoConvenioSaude());
				convenioSaude.setSeq(dadosInternacao.getSeqConvenioSaudePlano());
			} else if (atendimento.getSeqAtendimentoUrgencia() != null){
				dadosAtdUrgencia = prescricaoMedicaRN.buscaDadosAtendimentoUrgencia(atendimento.getSeqAtendimentoUrgencia());
				convenioSaude.setCnvCodigo(dadosAtdUrgencia.getCodigoConvenioSaude());
				convenioSaude.setSeq(dadosAtdUrgencia.getCspSeq());
			}
		}
	}

	//MPMT_PNP_ASU
	protected void verificarSobreposicaoDatas(MpmPrescricaoNpt prescricao, MpmPrescricaoNpt original) throws ApplicationBusinessException{		
		if (DominioIndPendenteItemPrescricao.P.equals(prescricao.getIndPendente())
				|| DominioIndPendenteItemPrescricao.B.equals(prescricao.getIndPendente())) {
			return;
		}

		if (manterPrescricaoDietaRN.verificarSobreposicaoDatas(prescricao.getDthrInicio(),
				prescricao.getDthrFim(), original.getDthrInicio(),
				original.getDthrFim())) {
			throw new ApplicationBusinessException(
					PrescricaoNptExceptionCode.MPM_01025);
		}
	}

	//MPMT_PNP_BRI	
	protected void preInserir(MpmPrescricaoNpt prescricao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		prescricao.setCriadoEm(new Date());
//		prescricao.getId().setSeq(mpmPrescricaoNptDAO.getNextVal(SequenceID.MPM_PNP_SQ1));
		
		VerificaAtendimentoVO atendimento = new VerificaAtendimentoVO();
		FatConvenioSaudePlanoId convenioSaude = new FatConvenioSaudePlanoId();
		
		BuscaDadosHospitalDiaVO dadosHospDia = new BuscaDadosHospitalDiaVO();
		BuscaDadosInternacaoVO dadosInternacao = new BuscaDadosInternacaoVO();
		BuscaDadosAtendimentoUrgenciaVO dadosAtdUrgencia = new BuscaDadosAtendimentoUrgenciaVO();
		VerificaIndicadoresConvenioInternacaoVO indicadores = new VerificaIndicadoresConvenioInternacaoVO();
		
		Integer phi = null;
		
		
		/* Verifica vigência do atendimento */
		if(prescricao.getDthrInicio() != null) {
			atendimento = prescricaoMedicaRN.verificaAtendimento(prescricao.getDthrInicio(), prescricao.getDthrFim(), 
					prescricao.getId().getAtdSeq(), null, null, null);
		}
		
		if(atendimento.getSeqHospitalDia() != null) {
			dadosHospDia = prescricaoMedicaRN.buscaDadosHospitalDia(atendimento.getSeqHospitalDia());
			convenioSaude.setCnvCodigo(dadosHospDia.getCodigoConvenioSaude());
			convenioSaude.setSeq(dadosHospDia.getCspSeq());
		} else {
			if(atendimento.getSeqInternacao() != null) {
				dadosInternacao = prescricaoMedicaRN.buscaDadosInternacao(atendimento.getSeqInternacao());
				convenioSaude.setCnvCodigo(dadosInternacao.getCodigoConvenioSaude());
				convenioSaude.setSeq(dadosInternacao.getSeqConvenioSaudePlano());
			} else if (atendimento.getSeqAtendimentoUrgencia() != null){
				dadosAtdUrgencia = prescricaoMedicaRN.buscaDadosAtendimentoUrgencia(atendimento.getSeqAtendimentoUrgencia());
				convenioSaude.setCnvCodigo(dadosAtdUrgencia.getCodigoConvenioSaude());
				convenioSaude.setSeq(dadosAtdUrgencia.getCspSeq());
			}
		}
		
		/* Busca SEQ Procedimento hospitalar Interno (PHI) */
		if(prescricao.getProcedEspecialDiversos() != null) {
			phi = prescricaoMedicaRN.verificaProcedimentoHospitalarInterno(null, null, prescricao.getProcedEspecialDiversos().getSeq());
		}
		
		/* Busca indicadores do convênio */
		indicadores = prescricaoMedicaRN.verificaIndicadoresConvenioInternacao(convenioSaude.getCnvCodigo(), convenioSaude.getSeq(), phi);

		/* Verificar se o convênio exige justificativa para o procedimento */
		if(prescricao.getJustificativa() == null && indicadores.getIndExigeJustificativa()) {
			if(prescricao.getProcedEspecialDiversos() != null) {
				throw new ApplicationBusinessException(
						PrescricaoNptExceptionCode.MPM_01120);
			}
		}
		
		/* Verifica a existência de uma prescrição medica para esta prescrição */
		/* de procedimentos */
		/* Atualiza a dthr_inicio_mvto_pendente na prescrição médica */
		prescricaoMedicaRN.verificaPrescricaoMedica(prescricao.getId().getAtdSeq(), prescricao.getDthrInicio(), prescricao.getDthrFim(), 
				prescricao.getCriadoEm(), prescricao.getIndPendente(), "I", 
				nomeMicrocomputador, dataFimVinculoServidor);
		
		/* Dados do servidor que digita */
		prescricao.setServidor(servidorLogadoFacade.obterServidorLogado());
		
	}

	/**
	 * ORADB RN_PNPP_VER_PROC_DIV
	 */
	protected void rnPnppVerProcDiv(MpmProcedEspecialDiversos procedimentoEspecial) throws ApplicationBusinessException{
		/* Verifica se Proced. Especial Diverso está ativo*/
		if(procedimentoEspecial != null) {
			procedimentoEspecial = mpmProcedEspecialDiversoDAO.obterPorChavePrimaria(procedimentoEspecial.getSeq());
			if(!DominioSituacao.A.toString().equals(procedimentoEspecial.getIndSituacao())) {
				throw new ApplicationBusinessException(
						PrescricaoNptExceptionCode.MPM_00992);
			}
		}
	}


	/**
	 * ORADB RN_PNPP_VER_FORM_NPT
	 */
	protected void rnPnppVerFormNpt(AfaFormulaNptPadrao formula) throws ApplicationBusinessException{
		/* Verifica se Proced. Especial Diverso está ativo*/
		if(formula != null) {
			formula = afaFormulaNptPadraoDAO.obterPorChavePrimaria(formula.getSeq());
			if(!DominioSituacao.A.toString().equals(formula.getIndSituacao())) {
				throw new ApplicationBusinessException(
						PrescricaoNptExceptionCode.MPM_01012);
			}
		}
	}
	
	
	public void excluir(ItemPrescricaoMedicaVO item, String nomeComputador) throws ApplicationBusinessException{
		MpmPrescricaoNpt prescricaoNpt = mpmPrescricaoNptDAO.obterNutricaoParentaLPeloId(item.getAtendimentoSeq(), item.getItemSeq().intValue());
		excluir(prescricaoNpt, nomeComputador);
	}
	
	/**
	 * Excluir prescricaoNPT conforme RN
	 * @param prescricaoNpt
	 * @throws ApplicationBusinessException
	 */
	public void excluir(MpmPrescricaoNpt prescricaoNpt, String nomeComputador) throws ApplicationBusinessException{
		if (prescricaoNpt.getIndPendente() == DominioIndPendenteItemPrescricao.P && prescricaoNpt.getPrescricaoNpts() == null) {
			excluirṔrescricaoNptPendente(prescricaoNpt);
		}else if (prescricaoNpt.getIndPendente() == DominioIndPendenteItemPrescricao.N) {
			excluirPrescricaoNptValidado(prescricaoNpt, nomeComputador);
		}else if (prescricaoNpt.getIndPendente() == DominioIndPendenteItemPrescricao.P && prescricaoNpt.getPrescricaoNpts() != null) {
			MpmPrescricaoNpt principal = prescricaoNpt.getPrescricaoNpts();
			alterarPrescricaoNpt(principal, DominioIndPendenteItemPrescricao.E, nomeComputador);
			excluirṔrescricaoNptPendente(prescricaoNpt);
		}
	}
	
	
	/**
	 * Excluir Prescricao NPT com situacao Validado - Logicamente
	 * @param prescricaoNpt
	 * @throws ApplicationBusinessException
	 */
	private void excluirPrescricaoNptValidado(MpmPrescricaoNpt prescricaoNpt, String nomeComputador)throws ApplicationBusinessException {
		if (isDtVigente(prescricaoNpt)) {
			prescricaoNpt.setDthrFim(prescricaoNpt.getDthrValidaMovimentacao());
		}else {
			prescricaoNpt.setDthrFim(new Date());
		}
		prescricaoNpt.setAlteradoEm(new Date());
		alterarPrescricaoNpt(prescricaoNpt, DominioIndPendenteItemPrescricao.E, nomeComputador );
	}
	
	
	/**
	 * Excluir Prescricao NPT - Fisicamente
	 * @param prescricaoNpt
	 */
	private void excluirṔrescricaoNptPendente(MpmPrescricaoNpt prescricaoNpt){
		List<MpmComposicaoPrescricaoNpt> listaComposicao = mpmComposicaoPrescricaoNptDAO.listarComposicoesPorPrescricaoNpt(prescricaoNpt);
		for (MpmComposicaoPrescricaoNpt composicao : listaComposicao) {
			List<MpmItemPrescricaoNpt> listaItens = mpmItemPrescricaoNptDAO.listarItensPrescricaoNptPorComposicao(composicao);
			for (MpmItemPrescricaoNpt item : listaItens) {
				mpmItemPrescricaoNptDAO.remover(item);
			}
			
			mpmComposicaoPrescricaoNptDAO.remover(composicao);
		}
		mpmPrescricaoNptDAO.remover(prescricaoNpt);
	}
	
	
	private void alterarPrescricaoNpt(MpmPrescricaoNpt prescricaoNpt, DominioIndPendenteItemPrescricao indPendente, String nomeComputador) throws ApplicationBusinessException{
		prescricaoNpt.setIndPendente(indPendente);
		prescricaoNpt.setServidorMovimentado(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()));
		Date dataFimVinculoServidor = servidorLogadoFacade.obterServidorLogado().getDtFimVinculo();
		atualizar(prescricaoNpt, nomeComputador, dataFimVinculoServidor);
	}
	
	private Boolean isDtVigente(MpmPrescricaoNpt prescricaoNpt) {
		if (prescricaoNpt.getDthrValidaMovimentacao() != null && prescricaoNpt.getDthrInicio() != null && prescricaoNpt.getDthrFim() != null) {
			if (DateUtil.validaDataMaiorIgual(prescricaoNpt.getDthrValidaMovimentacao(),prescricaoNpt.getDthrInicio()) && DateUtil.validaDataMenor(prescricaoNpt.getDthrInicio(), prescricaoNpt.getDthrFim())) {
				return true;
			}

			if (DateUtil.validaDataMenor(prescricaoNpt.getDthrValidaMovimentacao(),prescricaoNpt.getDthrInicio())) {
				return false;
			}
		}

		return false;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
}
