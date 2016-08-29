package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaAnestesiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaEquipeAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaPacienteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaProcedimentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaTipoAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcOcorrenciaFichaEventosDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOcorrenciaFichaEvento;
import br.gov.mec.aghu.dominio.DominioSituacaoExame;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgiasId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaEquipeAnestesia;
import br.gov.mec.aghu.model.MbcFichaPaciente;
import br.gov.mec.aghu.model.MbcFichaProcedimento;
import br.gov.mec.aghu.model.MbcFichaTipoAnestesia;
import br.gov.mec.aghu.model.MbcOcorrenciaFichaEvento;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * 
 * @author aghu
 * 
 */
@Stateless
public class MbcFichaAnestesiasRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcFichaAnestesiasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcFichaAnestesiasDAO mbcFichaAnestesiasDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcFichaTipoAnestesiaDAO mbcFichaTipoAnestesiaDAO;

	@Inject
	private MbcFichaPacienteDAO mbcFichaPacienteDAO;

	@Inject
	private MbcFichaProcedimentoDAO mbcFichaProcedimentoDAO;

	@Inject
	private MbcFichaEquipeAnestesiaDAO mbcFichaEquipeAnestesiaDAO;

	@Inject
	private MbcOcorrenciaFichaEventosDAO mbcOcorrenciaFichaEventosDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;


	@EJB
	private MbcAnestesiaCirurgiasRN mbcAnestesiaCirurgiasRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private MbcCirurgiasRN mbcCirurgiasRN;

	@EJB
	private MbcProfCirurgiasRN mbcProfCirurgiasRN;

	private static final long serialVersionUID = 245519597105542761L;

	/*
	 * Métodos para PERSISTIR
	 */
	public void persistir(MbcFichaAnestesias fichaAnestesia) throws BaseException {
		if (fichaAnestesia.getSeq() == null) { // Inserir
			this.inserirMbcFichaAnestesias(fichaAnestesia);
		} else { // Atualizar
			this.atualizarMbcFichaAnestesias(fichaAnestesia);
		}
	}

	private void inserirMbcFichaAnestesias(MbcFichaAnestesias fichaAnestesia) {
		this.preInserir(fichaAnestesia);
		this.getMbcFichaAnestesiasDAO().persistir(fichaAnestesia);
		this.getMbcFichaAnestesiasDAO().flush();

	}

	/**
	 * ORADB TRIGGER "AGH".MBCT_FIC_BRI
	 * @param fichaAnestesia
	 * @param servidorLogado
	 */
	private void preInserir(MbcFichaAnestesias fichaAnestesia) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		/* SERVIDOR */
		fichaAnestesia.setServidor(servidorLogado);

	}

	private void atualizarMbcFichaAnestesias(MbcFichaAnestesias fichaAnestesia) throws BaseException {
		this.preAtualizar(fichaAnestesia);
		this.getMbcFichaAnestesiasDAO().atualizar(fichaAnestesia);
		this.getMbcFichaAnestesiasDAO().flush();
	}

	/**
	 * ORADB TRIGGER AGH.MBCT_FIC_BRU
	 * @param fichaAnestesia
	 * @param servidorLogado
	 * @throws BaseException 
	 */
	private void preAtualizar(MbcFichaAnestesias fichaAnestesia) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		/*Se a ficha não estiver vinculada a uma cirurgia, procura vincular na conclusão */

		MbcFichaAnestesias old = this.getMbcFichaAnestesiasDAO().obterOriginal(fichaAnestesia.getSeq());
		if(CoreUtil.modificados(old.getPendente(), fichaAnestesia.getPendente())){

			//if(fichaAnestesia.getPendente().equals(DominioIndPendenteAmbulatorio.V) && fichaAnestesia.getCirurgia() == null){
			/* Na conclusão de uma ficha do Centro obstétrico, procura Vincular a uma cirurgia  */
			//  mbck_fic_rn.rn_ficp_vincula_crg(:new.seq,:new.pac_codigo, :new.unf_seq,:new.data,:new.crg_seq);
			//}

			if(fichaAnestesia.getPendente().equals(DominioIndPendenteAmbulatorio.V) && fichaAnestesia.getCirurgia() != null){



				// mbck_fic_rn.rn_ficp_atu_cirurgia(:new.crg_seq,:new.seq);

				AghParametros paramEventoAnestesia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EVENTO_ANESTESIA);

				Short v_evt_anest = paramEventoAnestesia.getVlrNumerico().shortValue();

				/* verifica se a nota da cirurgia já foi digitada, só atualiza dados se não foi */
				if(fichaAnestesia.getCirurgia()!=null && !fichaAnestesia.getCirurgia().getDigitaNotaSala()){

					/* Busca o tipo de anestesia da ficha */
					List<MbcFichaTipoAnestesia> fichasTipoAnestesia = this.getMbcFichaTipoAnestesiaDAO().pesquisarMbcFichasTipoAnestesias(fichaAnestesia.getSeq());

					if(fichasTipoAnestesia!=null && !fichasTipoAnestesia.isEmpty()){ //c_anest

						List<MbcAnestesiaCirurgias> listAnestesiaCirurgias = getMbcAnestesiaCirurgiasDAO().listarTipoAnestesiasPorCrgSeq(fichaAnestesia.getCirurgia().getSeq());

						if(listAnestesiaCirurgias == null){ //c_cir_anest

							MbcAnestesiaCirurgias anestesiaCirurgias = new MbcAnestesiaCirurgias();
							MbcAnestesiaCirurgiasId id = new MbcAnestesiaCirurgiasId();
							id.setTanSeq(fichasTipoAnestesia.get(0).getTipoAnestesia().getSeq());
							id.setCrgSeq(fichaAnestesia.getCirurgia().getSeq());
							anestesiaCirurgias.setId(id);
							anestesiaCirurgias.setMbcTipoAnestesias(fichasTipoAnestesia.get(0).getTipoAnestesia());

							/**
							 * INSERT INTO  MBC_ANESTESIA_CIRURGIAS
							 */
							this.getMbcAnestesiaCirurgiasRN().inserirMbcAnestesiaCirurgias(anestesiaCirurgias);
						}else{

							MbcAnestesiaCirurgias anestesiaCirurgias = listAnestesiaCirurgias.get(0);
							anestesiaCirurgias.getId().setTanSeq(fichasTipoAnestesia.get(0).getTipoAnestesia().getSeq());
							/**
							 * UPDATE MBC_ANESTESIA_CIRURGIAS
							 */
							this.getMbcAnestesiaCirurgiasRN().atualizarMbcAnestesiaCirurgias(anestesiaCirurgias);
						}
					}

					/* Busca data início da anestesia */

					MbcOcorrenciaFichaEvento ocorrenciaI = this.getMbcOcorrenciaFichaEventosDAO().obterOcorrenciaPorFicSeqEvpSeqTipoOcorrencia(fichaAnestesia.getSeq(), v_evt_anest, DominioOcorrenciaFichaEvento.I);
					Date v_inicio_anest = null;
					if(ocorrenciaI!=null){
						v_inicio_anest = ocorrenciaI.getDthrOcorrencia();
					}

					/*  Busca data fim da anestesia */
					MbcOcorrenciaFichaEvento ocorrenciaF = this.getMbcOcorrenciaFichaEventosDAO().obterOcorrenciaPorFicSeqEvpSeqTipoOcorrencia(fichaAnestesia.getSeq(), v_evt_anest, DominioOcorrenciaFichaEvento.I);
					Date v_fim_anest = null;

					if(ocorrenciaF != null){
						v_fim_anest = ocorrenciaF.getDthrOcorrencia();
					}

					List<MbcFichaPaciente> listFichaPaciente = this.getMbcFichaPacienteDAO().pesquisarMbcFichasPacientes(fichaAnestesia.getSeq());

					if(listFichaPaciente!=null && !listFichaPaciente.isEmpty()){

						MbcCirurgias cirurgia = fichaAnestesia.getCirurgia();

						cirurgia.setAsa(listFichaPaciente.get(0).getAsa());
						cirurgia.setDataInicioAnestesia(v_inicio_anest);
						cirurgia.setDataFimAnestesia(v_fim_anest);

						/**
						 * UPDATE MBC_CIRURGIAS
						 */
						this.getMbcCirurgiasRN().persistirCirurgia(cirurgia, null, servidorLogado.getDtFimVinculo());

					}


					/**
					 *  Carrega os profissionais de anestesia da ficha para cirurgia inclui os anestesistas em  mbc_prof_cirurgias
					 */
					List<MbcFichaEquipeAnestesia> listFichaEquipeAnestesia = getMbcFichaEquipeAnestesiaDAO().pesquisarMbcFichaEquipeAnestesiasByFichaAnestesia(fichaAnestesia.getSeq(), null);

					if(listFichaEquipeAnestesia!=null && !listFichaEquipeAnestesia.isEmpty()){

						for(MbcFichaEquipeAnestesia fichaEquipeAnestesia : listFichaEquipeAnestesia){

							List<MbcProfAtuaUnidCirgs> listProfAtuaUnidCir = this.getMbcProfAtuaUnidCirgsDAO().pesquisarProfissionaisUnidCirgUnfSeqFea(fichaAnestesia.getCirurgia().getUnidadeFuncional().getSeq(), fichaEquipeAnestesia);

							if(listProfAtuaUnidCir!=null && !listProfAtuaUnidCir.isEmpty()){

								for(MbcProfAtuaUnidCirgs puc : listProfAtuaUnidCir){

									MbcProfCirurgias profCirurgias = new MbcProfCirurgias();
									popularMbcProfCirurgias(fichaAnestesia,puc, profCirurgias);

									/**
									 * INSERT INTO  mbc_prof_cirurgias
									 */
									this.getMbcProfCirurgiasRN().persistirProfissionalCirurgias(profCirurgias);
								}
							}
						}
					}
				}
			}

		}

	}

	private void popularMbcProfCirurgias(MbcFichaAnestesias fichaAnestesia, MbcProfAtuaUnidCirgs puc, MbcProfCirurgias profCirurgias) {
		MbcProfCirurgiasId id = new MbcProfCirurgiasId();

		id.setCrgSeq(fichaAnestesia.getCirurgia().getSeq());
		id.setPucIndFuncaoProf(puc.getId().getIndFuncaoProf());
		id.setPucSerMatricula(puc.getId().getSerMatricula());
		id.setPucSerVinCodigo(puc.getId().getSerVinCodigo());
		id.setPucUnfSeq(puc.getId().getUnfSeq());
		profCirurgias.setId(id);

		profCirurgias.setIndResponsavel(false);
		profCirurgias.setIndRealizou(Boolean.FALSE);
		profCirurgias.setIndInclEscala(Boolean.FALSE);
		profCirurgias.setMbcProfAtuaUnidCirgs(puc);
		profCirurgias.setCirurgia(fichaAnestesia.getCirurgia());
		profCirurgias.setUnidadeFuncional(puc.getUnidadeFuncional());
		profCirurgias.setServidor(puc.getRapServidores());
		profCirurgias.setFuncaoProfissional(puc.getId().getIndFuncaoProf());
	}

	/** ORADB PROCEDURE MBCP_VINCULA_CIR_FIC
	 * 	Verifica se a cirurgia que se está agendando já não tem ficha de anestesia vai associar 
	 *  a cirurgia com a ficha se o procedimento da ficha for igual ao procedimento principal da cirurgia.
	 * @throws BaseException 
	 */
	@SuppressWarnings("ucd")
	public void vincularCirurgiaFicha(Integer crgSeq, Integer pacCodigo, Integer pciSeq, Date data, Short unfSeq) throws BaseException{

		MbcFichaAnestesias ficha = null;

		List<MbcFichaProcedimento> listFichaProcedimento = this.getMbcFichaProcedimentoDAO().pesquisarFichaAnestesia(pacCodigo, unfSeq, data, pciSeq);

		if (listFichaProcedimento != null && !listFichaProcedimento.isEmpty()) {
			
			ficha = listFichaProcedimento.get(0).getMbcFichaAnestesias();
			
		}

		/**
		 * Vai associar a ficha com a cirurgia
		 */
		if (ficha != null) {
			
			MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterOriginal(crgSeq);
			ficha.setCirurgia(cirurgia);
			this.atualizarMbcFichaAnestesias(ficha);
			
		}
		
	}


	/**
	 * ORADB PROCEDURE RN_FICP_VINCULA_CRG
	 * Na conclusão de uma ficha do Centro obstétrico, procura Vincular a uma cirurgia
	 * @param cirurgia
	 * @param pciSeq
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("ucd")
	public Integer vincularFicPCrg(MbcCirurgias cirurgia,Long P_FIC_SEQ  ) throws ApplicationBusinessException{

		AghParametros paramUnidadeCentroObst = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIDADE_CO);
		Integer p_crg_seq = null;

		Integer v_unid_co = paramUnidadeCentroObst.getVlrNumerico()!=null?paramUnidadeCentroObst.getVlrNumerico().intValue():null;

		if(cirurgia.getUnidadeFuncional().getSeq().equals(v_unid_co)){
			/**
			 *  Para cada procedimento realizado na ficha procura se tem cirurgia sem ficha
			 */
			List<MbcFichaProcedimento> listFichaProcedimento = this.getMbcFichaProcedimentoDAO().obterFichaProcedimentoComProcedimentoCirurgicoByFichaAnestesia(P_FIC_SEQ, DominioSituacaoExame.R);

			for(MbcFichaProcedimento fpo: listFichaProcedimento){

				List<MbcProcEspPorCirurgias> listProcEspPorCirurgias = this.getMbcProcEspPorCirurgiasDAO().obterProcedimentoEspVinculaFicha(cirurgia, fpo.getMbcProcedimentoCirurgicos().getSeq());

				if(listProcEspPorCirurgias!=null && !listProcEspPorCirurgias.isEmpty() && listProcEspPorCirurgias.get(0).getCirurgia()!=null){

					p_crg_seq = listProcEspPorCirurgias.get(0).getCirurgia().getSeq(); //v_crg_seq

				}
			}


		}
		return p_crg_seq;
	}

	protected MbcFichaAnestesiasDAO getMbcFichaAnestesiasDAO() {
		return mbcFichaAnestesiasDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected MbcFichaTipoAnestesiaDAO getMbcFichaTipoAnestesiaDAO() {
		return mbcFichaTipoAnestesiaDAO;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	protected MbcAnestesiaCirurgiasRN getMbcAnestesiaCirurgiasRN() {
		return mbcAnestesiaCirurgiasRN;
	}

	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}

	protected MbcOcorrenciaFichaEventosDAO getMbcOcorrenciaFichaEventosDAO() {
		return mbcOcorrenciaFichaEventosDAO;
	}

	protected MbcFichaPacienteDAO getMbcFichaPacienteDAO() {
		return mbcFichaPacienteDAO;
	}

	protected MbcCirurgiasRN getMbcCirurgiasRN() {
		return mbcCirurgiasRN;
	}

	protected MbcFichaEquipeAnestesiaDAO getMbcFichaEquipeAnestesiaDAO() {
		return mbcFichaEquipeAnestesiaDAO;
	}

	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}

	protected MbcProfCirurgiasRN getMbcProfCirurgiasRN() {
		return mbcProfCirurgiasRN;
	}

	protected MbcFichaProcedimentoDAO getMbcFichaProcedimentoDAO() {
		return mbcFichaProcedimentoDAO;
	}

	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}



}
