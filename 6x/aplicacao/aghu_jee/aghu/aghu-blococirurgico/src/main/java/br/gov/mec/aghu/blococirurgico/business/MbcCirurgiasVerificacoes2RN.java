package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.MbcCirurgiasRN.MbcCirurgiasRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProfissionalVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FatcVerCarPhiCnvVO;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe contendo as regras dos pacotes MBCK_CRG_RN e MBCK_CRG_RN2
 * 
 * Obs. Esta classe foi criada para EVITAR VIOLAÇÕES DE PMD em MbcCirurgiasVerificacoesRN e MbcCirurgiasRN
 * 
 * @autor aghu
 * 
 */
@Stateless
public class MbcCirurgiasVerificacoes2RN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcCirurgiasVerificacoes2RN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;


	@EJB
	private IFaturamentoFacade iFaturamentoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private MbcControleEscalaCirurgicaRN mbcControleEscalaCirurgicaRN;

	@EJB
	private IParametroFacade iParametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7485345956788443269L;

	/**
	 * ORADB PROCEDURE mbck_crg_rn.rn_crgp_ver_prof_exe
	 * 
	 * Garantir que tenha um e somente um responsável pela execução cirurgia e que o ind funcao prof seja 'MPF' (professor) ou 'MCO' (contratado) ou 'MAX' (residente/auxiliar)
	 * 
	 * @param crgSeq
	 * @throws ApplicationBusinessException
	 */
	public void verificarProfissionalExecucaoCirugia(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		// Recupera a cirurgia do banco para evitar LazyLoading
		cirurgia = this.getMbcCirurgiasDAO().obterPorChavePrimaria(cirurgia.getSeq());
		
		Set<MbcProfCirurgias> listaResponsaveisCir = cirurgia.getProfCirurgias();
		DominioFuncaoProfissional[] funcoesPermitidas = { DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MAX };
		Integer qtdRealizadoresCir = Integer.valueOf(0);
		Boolean possuiRealizador = Boolean.FALSE;
		
		for (MbcProfCirurgias prof : listaResponsaveisCir) {
			if(prof.getIndRealizou()){
				qtdRealizadoresCir++;
			}
			
			if(ArrayUtils.contains(funcoesPermitidas, prof.getFuncaoProfissional())){
				possuiRealizador = Boolean.TRUE;
			}
		}
		
		if (qtdRealizadoresCir == 0) {
			// Ao menos um profissional deve estar indicado como sendo o que realizou a cirurgia
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00479);
		} else if (qtdRealizadoresCir > 1) {
			// Somente um profissional deve estar indicado como sendo o que realizou a cirurgia
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00480);
		}

		if (!possuiRealizador) {
			// O profissional que realizou a cirurgia deve ser médico contratado ou professor ou auxiliar
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00481);
		}
	}

	/**
	 * ORADB PROCEDURE mbck_crg_rn.rn_crgp_ver_prof_exe
	 * 
	 * Mesma função do metodo de cima, adaptada para a tela de nota de consumo, quando são adicionados profissionais a busca da cirurgia não tráz os profissionais adicionados na nota
	 * então quando é adicionado um profissional e setado ele como executor a regra acima não valida.
	 * 
	 * Garantir que tenha um e somente um responsável pela execução cirurgia e que o ind funcao prof seja 'MPF' (professor) ou 'MCO' (contratado) ou 'MAX' (residente/auxiliar)
	 * 
	 * @param List<CirurgiaTelaProfissionalVO>
	 * @throws AGHUNegocioException
	 */
	public void verificarProfissionalExecucaoCirugia(List<CirurgiaTelaProfissionalVO> listProfissionaisVO) throws ApplicationBusinessException {

		DominioFuncaoProfissional[] funcoesPermitidas = { DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF, DominioFuncaoProfissional.MAX };
		Integer qtdRealizadoresCir = Integer.valueOf(0);
		Boolean possuiRealizador = Boolean.FALSE;
		
		for (CirurgiaTelaProfissionalVO prof : listProfissionaisVO) {
			if (Boolean.TRUE.equals(prof.getIndRealizou())) {
				qtdRealizadoresCir++;
			}
			
			if (ArrayUtils.contains(funcoesPermitidas, prof.getFuncaoProfissional())) {
				possuiRealizador = Boolean.TRUE;
			}
		}
		
		if (qtdRealizadoresCir == 0) {
			// Ao menos um profissional deve estar indicado como sendo o que realizou a cirurgia
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00479);
		} else if (qtdRealizadoresCir > 1) {
			// Somente um profissional deve estar indicado como sendo o que realizou a cirurgia
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00480);
		}

		if (!possuiRealizador) {
			// O profissional que realizou a cirurgia deve ser médico contratado ou professor ou auxiliar
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00481);
		}
	}

	/**
	 * ORADB PROCEDURE mbck_crg_rn.rn_crgp_ver_prof_enf
	 * 
	 * Garantir que tenha pelo menos um profissional de enfermagem vinculado à cirurgia
	 * 
	 * @param crgSeq
	 * @throws ApplicationBusinessException
	 */
	public void verificarProfissionalEnfermagem(Integer crgSeq) throws ApplicationBusinessException {
		List<MbcProfCirurgias> listaProfissionaisEnfermagem = this.getMbcProfCirurgiasDAO().pesquisarProfissionalEnfermagem(crgSeq);
		if (listaProfissionaisEnfermagem.isEmpty()) {
			// Ao menos um profissional da enfermagem deve esta vinculado à cirurgia
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00482);
		}
	}

	/**
	 * ORADB PROCEDURE mbck_crg_rn.rn_crgp_ver_anest
	 * 
	 * Se cirurgia utilizou-se de um tipo de anestesia com indicador de necessidade de anestesista igual a S, obrigatoriamente deve existir um profissional com função e anestesista
	 * na prof cirurgia associado
	 * 
	 * @param crgSeq
	 * @throws ApplicationBusinessException
	 */
	public void verificarProfissionalAnestesista(Integer crgSeq) throws ApplicationBusinessException {

		List<MbcAnestesiaCirurgias> listaAnestesias = this.getMbcAnestesiaCirurgiasDAO().pesquisarAnestesiaNecessidadeAnestesista(crgSeq);

		if (!listaAnestesias.isEmpty()) {

			// Funções de Anestesista: Residente, contratado e professor
			DominioFuncaoProfissional[] funcoes = { DominioFuncaoProfissional.ANR, DominioFuncaoProfissional.ANC, DominioFuncaoProfissional.ANP };

			List<MbcProfCirurgias> listaProfissionaisAnestesistas = this.getMbcProfCirurgiasDAO().pesquisarProfissionalCirurgiaPorFuncao(crgSeq, funcoes);
			if (listaProfissionaisAnestesistas.isEmpty()) {
				// Deve existir pelo menos um anestesista ligado ao procedimento
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00385);
			}

		}
	}
	
	/**
	 * ORADB PROCEDURE MBCK_CRG_RN.RN_CRGP_VER_DIGT_NOT
	 * 
	 * @param cirurgia
	 * @throws BaseException
	 */
	public void verificarDigitoNotaSala(MbcCirurgias cirurgia) throws BaseException {

		if (!cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC) && cirurgia.getDataDigitacaoNotaSala() != null) {

			if (cirurgia.getOrigemPacienteCirurgia().equals(DominioOrigemPacienteCirurgia.I)) {
				
				if (cirurgia.getAtendimento() == null) {
					// Deve haver atendimento de internação associado à cirurgia origem Internação.
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00448);

				} else if (cirurgia.getAtendimento().getInternacao() == null) {
					// Não existe atendimento de internação associado à cirurgia.
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00387);

				} else if (!(CoreUtil.isBetweenDatas(cirurgia.getDataFimCirurgia(), cirurgia.getAtendimento().getDthrInicio(),
						cirurgia.getAtendimento().getDthrFim() != null ? cirurgia.getAtendimento().getDthrFim() : new Date()))) {
					// Datas do atendimento de internação associado não contemplam cirurgia.
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00388);
				}
				// Se o atendimento for de ambulatório (DominioOrigemPacienteCirurgia.A)
			} else if (cirurgia.getAtendimento() == null) {
				// Chama ORADB PROCEDURE mbck_mbc_rn.RN_MBCP_ATU_INS_ATD
				Integer atdSeq = this.getMbcControleEscalaCirurgicaRN().inserirAtendimentoCirurgiaAmbulatorio(cirurgia.getAtendimento(), cirurgia.getPaciente(), 
						cirurgia.getDataPrevisaoInicio(),cirurgia.getUnidadeFuncional(), cirurgia.getEspecialidade(), null);
						cirurgia.setAtendimento(getAghuFacade().obterAtendimentoPeloSeq(atdSeq));
			} else if (!(cirurgia.getAtendimento().getOrigem().equals(DominioOrigemAtendimento.C) || cirurgia.getAtendimento().getOrigem().equals(DominioOrigemAtendimento.U) || cirurgia
					.getAtendimento().getOrigem().equals(DominioOrigemAtendimento.A))) {
				// Atendimento vinculado a cirurgia não é de ambulatório.
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00446);
			}
		}
	}
	
	/**
	 * ORADB FUNCTION MBCK_CRG_RN.RN_CRGC_VER_CERIH
	 * 
	 * @param cirurgia
	 * @return Boolean
	 * @throws ApplicationBusinessException 
	 */
	public Boolean verificarExigenciaCerih(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		Boolean resultado = true;
		List<Object[]> listaCirurgiasComProcedimentosAtivos = 
			getMbcCirurgiasDAO().listarCirurgiasComProcedimentosAtivos(cirurgia.getSeq());
		
		for (Object[] procedimento : listaCirurgiasComProcedimentosAtivos) {
			if ((Byte) procedimento[1] == 1) {
				final Short cspCnvCodigo = (Short) procedimento[0];
				final Byte cspSeq = (Byte) procedimento[1];
				final Integer phi = (Integer) procedimento[2];
				final String pCaracteristica = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_CARACT_CERIH);
				final Short cpgGrcSeq = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
				// Chama FUNCTION ORADB: FATC_VER_CAR_PHI_CNV
				FatcVerCarPhiCnvVO retorno = 
					getFaturamentoFacade().fatcVerCarPhiCnv(cspCnvCodigo, cspSeq, phi, pCaracteristica, cpgGrcSeq);
				// Se FATC_VER_CAR_PHI_CNV = S
				if (retorno.isResult()) {
					List<Integer> listResult = getMbcCirurgiasDAO().pesquisarDiariaAutorizadaComSenha(cirurgia.getSeq());
					// Caso não encontre pelo menos uma senha preenchida, significa que o CERIH não foi informado.
					if (listResult == null || listResult.isEmpty()) {
						resultado = false;
					}
				}
			}
		}
		return resultado;
	}
	
	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	private MbcControleEscalaCirurgicaRN getMbcControleEscalaCirurgicaRN() {
		return mbcControleEscalaCirurgicaRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return  iParametroFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return  iFaturamentoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

}