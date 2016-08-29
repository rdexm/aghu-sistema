package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoCboDAO;
import br.gov.mec.aghu.faturamento.vo.RnCthcBuscaDadosProfVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcValidaCboRespVO;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedimentoCbo;
import br.gov.mec.aghu.model.FatProcedimentoRegistro;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Refere-se a package FATK_CTH6_RN_UN
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FatkCth6RN extends AbstractFatDebugLogEnableRN {


@EJB
private FatkCthRN fatkCthRN;

private static final Log LOG = LogFactory.getLog(FatkCth6RN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	private static final long serialVersionUID = -8149263606584414870L;

	/**
	 * ORADB Procedure FATK_CTH6_RN_UN.RN_CTHC_BUSCA_DADOS_PROF
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NcssMethodCount"})
	public RnCthcBuscaDadosProfVO rnCthcBuscaDadosProf(Integer pCthSeq, Integer pPhiRealizado, Integer pClinicaRealiz, Short pPhoSeq,
			Integer pIphSeq, Byte pTipoAto, Integer pMatricula, Short pVinculo, Short pVinCodResp, Integer pMatriculaResp, Short pVinCodAnest,
			Integer pMatriculaAnest, final Date dtRealizado, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		final FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();

		String vCbo = null;
		Long vCpfCns = null;
		MbcCirurgias vCirurgia = null;
		String vTipo = null;

		final RnCthcBuscaDadosProfVO retorno = new RnCthcBuscaDadosProfVO();

		// busca cod_procedimento e tipo registro do ato de proc especial ou
		// secundário
		final String valorParametroServEspelho = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SERVICOS_PROFISSIONAIS_ESPELHO); // 03,04,05
		final String[] codigosRegistro = valorParametroServEspelho != null ? valorParametroServEspelho.split(",") : null;
		//#31004
		//dbms_output.put_line(' RN_CTHC_BUSCA_DADOS_PROF');
		logar(" RN_CTHC_BUSCA_DADOS_PROF");
		final FatProcedimentoRegistro fatProcedimentoRegistro = getFatProcedimentoRegistroDAO().buscarPrimeiroPorCodigosRegistroEPorIph(codigosRegistro,
				pPhoSeq, pIphSeq);
		if (fatProcedimentoRegistro == null) {
			retorno.setCpfCns(null);
			retorno.setRetorno(Boolean.TRUE);
			return retorno;
		}
		
		Boolean vProcEsp = Boolean.FALSE;
		final FatItensProcedHospitalar itemProcHospitalar = getFatItensProcedHospitalarDAO().obterItemProcedHospitalar(pPhoSeq, pIphSeq);
		if (itemProcHospitalar != null) {
			vProcEsp = itemProcHospitalar.getProcedimentoEspecial();
		}
		
		
		logar("v_proc_esp  {0} vinc {1} matr {2} ", (vProcEsp == Boolean.TRUE ? "S" : "N"), pVinculo, pMatricula);		
		logar("CBO: Clínica {0} Ato {1} vinc {2} matr {3}", pClinicaRealiz, pTipoAto, pVinculo, pMatricula);

		// Se não for clínica cirurgica não possui equipe e é o CBO do médico da internação

		final Integer valorParamClinicaCirurgica = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_CLINICA_CIRURGICA);

		final Byte valorParamAtoCirurgiao = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_CIRURGIAO);
		final Byte valorParamAtoAnestesista = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_ANESTESISTA);
		final Byte valorParamAtoAnestObste = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_ANEST_OBSTE);

		final String valorParametroCirurgiao = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CBO_CIRURGIAO); // C
		final String valorParametroAnestesista = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CBO_ANESTESISTA); // A

		if ((!CoreUtil.igual(pClinicaRealiz, valorParamClinicaCirurgica))
				|| (CoreUtil.notIn(pTipoAto, valorParamAtoCirurgiao, valorParamAtoAnestesista, valorParamAtoAnestObste))) {

			logar("CBO: Ato {0} vinc {1} matr {2}", pTipoAto, pVinculo, pMatricula);

			// Marina 16/06/2011 - chamado 31989
		
			/*  
			 * eSchweigert 03/09/2012 #20458
			 
			Boolean vProcEsp = Boolean.FALSE;
			FatItensProcedHospitalar itemProcHospitalar = getFatItensProcedHospitalarDAO().obterItemProcedHospitalar(pPhoSeq, pIphSeq);
			if (itemProcHospitalar != null) {
				vProcEsp = itemProcHospitalar.getProcedimentoEspecial();
			}
			*/
			
			logar("v_proc_esp: {0} p_tipo_ato: {1}", vProcEsp, pTipoAto);

			if (CoreUtil.igual(vProcEsp, Boolean.TRUE)) {
				if (CoreUtil.igual(pTipoAto, valorParamAtoCirurgiao)) {
					retorno.setEquipe(Short.valueOf("1"));
				} else if (CoreUtil.igual(pTipoAto, valorParamAtoAnestesista)) {
					retorno.setEquipe(Short.valueOf("6"));
				} else {
					retorno.setEquipe(Short.valueOf("0"));
				}
			} else {
				retorno.setEquipe(Short.valueOf("0"));
			}
			// Marina 16/06/2011

			RnCthcValidaCboRespVO rnCthcValidaCboRespVO = this.rnCthcValidaCboResp(pCthSeq, nvl(pMatriculaResp, pMatricula),
					nvl(pVinCodResp, pVinculo), null, null, null, null, valorParametroCirurgiao, pPhoSeq, pIphSeq, pTipoAto, 
					dtRealizado, nomeMicrocomputador, dataFimVinculoServidor);

			vCbo = rnCthcValidaCboRespVO.getCbo();
			vCpfCns = rnCthcValidaCboRespVO.getCpf();

			retorno.setMatriculaProf(rnCthcValidaCboRespVO.getMatriculaProf());
			retorno.setVinculoProf(rnCthcValidaCboRespVO.getVinculoProf());

			if (!rnCthcValidaCboRespVO.getRetorno()) {
				logar("Aki");
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}

			retorno.setCbo(vCbo);
			retorno.setCpfCns(vCpfCns);
			retorno.setRetorno(Boolean.TRUE);
			return retorno;
		}

		// Fim Se não for clínica cirurgica não possui equipe e é o CBO do médico da internação
		// ETB 01072008 -- Se informado responsável ou anestesista assume o CBO deste

		if (CoreUtil.igual(pTipoAto, valorParamAtoCirurgiao) && pVinCodResp != null) {

			logar("CBO: CIRURGIÃO INFORMADO: {0} vinc {1} matr {2}", pTipoAto, pVinCodResp, pMatriculaResp);
			retorno.setEquipe(Short.valueOf("1"));

			RnCthcValidaCboRespVO rnCthcValidaCboRespVO = this.rnCthcValidaCboResp(pCthSeq, pMatriculaResp, pVinCodResp, null, null, null, null,
					valorParametroCirurgiao, pPhoSeq, pIphSeq, pTipoAto, dtRealizado, nomeMicrocomputador, dataFimVinculoServidor);

			vCbo = rnCthcValidaCboRespVO.getCbo();
			vCpfCns = rnCthcValidaCboRespVO.getCpf();

			retorno.setMatriculaProf(rnCthcValidaCboRespVO.getMatriculaProf());
			retorno.setVinculoProf(rnCthcValidaCboRespVO.getVinculoProf());

			if (!rnCthcValidaCboRespVO.getRetorno()) {
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}

			retorno.setCbo(vCbo);
			retorno.setCpfCns(vCpfCns);
			retorno.setRetorno(Boolean.TRUE);
			return retorno;
		} else if ((CoreUtil.igual(pTipoAto, valorParamAtoAnestesista) || CoreUtil.igual(pTipoAto, valorParamAtoAnestObste)) && pVinCodAnest != null) {
			logar("CBO: ANESTESISTA INFORMADO: {0} vinc {1} matr {2}", pTipoAto, pVinCodAnest, pMatriculaAnest);
			if (CoreUtil.igual(pTipoAto, valorParamAtoAnestesista)) {
				retorno.setEquipe(Short.valueOf("6"));
			} else {
				retorno.setEquipe(Short.valueOf("0"));
			}

			RnCthcValidaCboRespVO rnCthcValidaCboRespVO = this.rnCthcValidaCboResp(pCthSeq, pMatriculaAnest, pVinCodAnest, null, null, null, null,
					valorParametroAnestesista, pPhoSeq, pIphSeq, pTipoAto, dtRealizado, nomeMicrocomputador, dataFimVinculoServidor);

			vCbo = rnCthcValidaCboRespVO.getCbo();
			vCpfCns = rnCthcValidaCboRespVO.getCpf();

			retorno.setMatriculaProf(rnCthcValidaCboRespVO.getMatriculaProf());
			retorno.setVinculoProf(rnCthcValidaCboRespVO.getVinculoProf());

			if (!rnCthcValidaCboRespVO.getRetorno()) {
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}

			retorno.setCbo(vCbo);
			retorno.setCpfCns(vCpfCns);
			retorno.setRetorno(Boolean.TRUE);
			return retorno;
		}

		// Fim ETB 01072008 -- Se informado responsável ou anestesista assume o
		// CBO deste
		// Se for PHI de múltipla busca outros itens BCC da conta
		final Integer valorParamPhiMultipla = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_CIRURG_MULTI);
		if (CoreUtil.igual(pPhiRealizado, valorParamPhiMultipla)) {
			// busca as cirurgias para realizado (phi) de cirurgia multipla
			FatItemContaHospitalar itemContaMult = fatItemContaHospitalarDAO.buscarPrimeiroItemContaCirurgiasNaoCanceladas(pCthSeq,
					valorParamPhiMultipla);
			if (itemContaMult == null) {
				// Nao encontrou as cirurgias para cobrança de cirurgia multipla
				retorno.setCpfCns(null);
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}
			vCirurgia = itemContaMult.getCirurgia();
		} else { // não é multipla. Verifica se existem outros itens bcc para a data
			logar("CBO: Não multipla  Ato {0} equipe {1}", pTipoAto, retorno.getEquipe());
			// busca todas as cirurgias para o horário do realizado da conta
			FatItemContaHospitalar itemContaBcc = fatItemContaHospitalarDAO.buscarPrimeiroItemContaCirurgiasAPVHorarioConta(pCthSeq, pPhiRealizado);
			if (itemContaBcc != null) {
				logar("CBO: DIG c/ BCC  Ato {0} equipe {1}", pTipoAto, retorno.getEquipe());
				vCirurgia = itemContaBcc.getCirurgia();

				if (itemContaBcc.getProcedimentoAmbRealizado() != null) {
					// Busca chave da cirurgia a partir pmr, pois não esta
					// populada na conta
					FatProcedAmbRealizado vProcedAmbRealizado = itemContaBcc.getProcedimentoAmbRealizado();
					logar("v_pmr_seq:  {0}", (vProcedAmbRealizado != null ? vProcedAmbRealizado.getSeq() : null));
					// if(itemContaBcc.getProcedimentoAmbRealizado().getCirurgia()
					// == null){
					if (itemContaBcc.getProcedimentoAmbRealizado().getProcEspPorCirurgia() == null
							|| itemContaBcc.getProcedimentoAmbRealizado().getProcEspPorCirurgia().getCirurgia() == null) {
						retorno.setCpfCns(null);
						retorno.setRetorno(Boolean.FALSE);
						return retorno;
					} else {
						vCirurgia = itemContaBcc.getProcedimentoAmbRealizado().getProcEspPorCirurgia().getCirurgia();
					}
				}

			} else { // if c_itens_conta_bcc%found then

				FatItemContaHospitalar itemConta = fatItemContaHospitalarDAO.buscarPrimeiroItemContaCirurgiasAPV(pCthSeq, pPhiRealizado);
				if (itemConta == null) {
					// Não encontrou a chave da cirurgia nem na conta nem a seq da pmr
					// 27/01/2008 - Def. João Ant.:não é multi, não tem cirurg = médico da alta
					logar("CBO: DIG S/CIRG  Ato {0} phi {1}", pTipoAto, pPhiRealizado);
					
					// Marina 29/06/2012
					if(Boolean.TRUE.equals(vProcEsp)){
						retorno.setEquipe(Short.valueOf("0"));
						
					} else {
						retorno.setEquipe((short) pTipoAto); // ETB 03072008 0;
					}
						
					

					RnCthcValidaCboRespVO rnCthcValidaCboRespVO = this.rnCthcValidaCboResp(pCthSeq, pMatricula, pVinculo, null, null, null, null,
							valorParametroCirurgiao, pPhoSeq, pIphSeq, pTipoAto, dtRealizado, nomeMicrocomputador, dataFimVinculoServidor);

					vCbo = rnCthcValidaCboRespVO.getCbo();
					vCpfCns = rnCthcValidaCboRespVO.getCpf();

					retorno.setMatriculaProf(rnCthcValidaCboRespVO.getMatriculaProf());
					retorno.setVinculoProf(rnCthcValidaCboRespVO.getVinculoProf());

					if (!rnCthcValidaCboRespVO.getRetorno()) {
						retorno.setRetorno(Boolean.FALSE);
						return retorno;
					}

					retorno.setCbo(vCbo);
					retorno.setCpfCns(vCpfCns);
					retorno.setRetorno(Boolean.TRUE);
					return retorno;
				} else { // if c_itens_conta%notfound then
					vCirurgia = itemConta.getCirurgia();

					if (itemConta.getProcedimentoAmbRealizado() != null) {
						// Busca chave da cirurgia a partir pmr, pois não esta
						// populada na conta
						FatProcedAmbRealizado vProcedAmbRealizado = itemConta.getProcedimentoAmbRealizado();
						logar("v_pmr_seq:  {0}", (vProcedAmbRealizado != null ? vProcedAmbRealizado.getSeq() : null));
						if (itemConta.getProcedimentoAmbRealizado().getProcEspPorCirurgia() == null
								|| itemConta.getProcedimentoAmbRealizado().getProcEspPorCirurgia().getCirurgia() == null) {
							retorno.setCpfCns(null);
							retorno.setRetorno(Boolean.FALSE);
							return retorno;
						} else {
							vCirurgia = itemConta.getProcedimentoAmbRealizado().getProcEspPorCirurgia().getCirurgia();
						}
					}
				}
			} // if c_itens_conta_bcc%found then
		}

		logar("Deve passar aqui {0} cirg {1} tipo {2} p_tipo_ato{3}", vCbo, (vCirurgia != null ? vCirurgia.getSeq() : null), vTipo, pTipoAto);

		if (vCbo == null) {
			logar("v_proc_esp: {0} p_tipo_ato: {1}", vProcEsp, pTipoAto);
			
			
			if (pTipoAto != null && pTipoAto.intValue() == 1) {
				logar("1");
				
				// Marina 28/06/2012 - Chamado
				if(Boolean.TRUE.equals(vProcEsp)){
					logar("entroooooooooooo: ");
					retorno.setEquipe(Short.valueOf("0"));
					
				} else {
					retorno.setEquipe((short) pTipoAto);
				}

				vTipo = valorParametroCirurgiao;
			} else {
				if (CoreUtil.igual(pTipoAto, valorParamAtoAnestesista)) {
					logar("2");
					retorno.setEquipe((short) valorParamAtoAnestesista);
				} else {
					logar("3");
					retorno.setEquipe(Short.valueOf("0"));
				}
				vTipo = valorParametroAnestesista;
			}

			RnCthcValidaCboRespVO rnCthcValidaCboRespVO = this.rnCthcValidaCboResp(pCthSeq, null, null, null, null, null,
					(vCirurgia != null ? vCirurgia.getSeq() : null), vTipo, pPhoSeq, pIphSeq, pTipoAto, dtRealizado, 
					nomeMicrocomputador, dataFimVinculoServidor);

			vCbo = rnCthcValidaCboRespVO.getCbo();
			vCpfCns = rnCthcValidaCboRespVO.getCpf();

			retorno.setMatriculaProf(rnCthcValidaCboRespVO.getMatriculaProf());
			retorno.setVinculoProf(rnCthcValidaCboRespVO.getVinculoProf());

			if (!rnCthcValidaCboRespVO.getRetorno()) {
				vCbo = null;
				vCpfCns = null;
			}
			// C CBO cirurgião -- A CBO Anestesista
			logar("Ney CBO Ato  {0} - {1} cirg {2} tipo {3} v_cpfcns={4}", retorno.getEquipe(), vCbo, (vCirurgia != null ? vCirurgia.getSeq() : null), vTipo, vCpfCns);

			retorno.setCbo(vCbo);
			retorno.setCpfCns(vCpfCns);

			// informa dados do cirurgião se não encontra o anestesista ETB
			// 24032008
			// if v_cbo is null and p_equipe = 6 then ETB 11092008

			if (vCbo == null && (CoreUtil.igual(pTipoAto, valorParamAtoAnestesista) || CoreUtil.igual(pTipoAto, valorParamAtoAnestObste))) {
				logar("--------> verifica tipo anestesia {0}", pTipoAto);

				List<MbcTipoAnestesias> tipoAnestesias = getBlocoCirurgicoFacade().listarTipoAnestesias(
						(vCirurgia != null ? vCirurgia.getSeq() : null), Boolean.FALSE);

				if (tipoAnestesias != null && !tipoAnestesias.isEmpty()) {

					rnCthcValidaCboRespVO = this.rnCthcValidaCboResp(pCthSeq, null, null, null, null, null, (vCirurgia != null ? vCirurgia.getSeq()
							: null), valorParametroCirurgiao, pPhoSeq, pIphSeq, pTipoAto, dtRealizado, nomeMicrocomputador, dataFimVinculoServidor);

					vCbo = rnCthcValidaCboRespVO.getCbo();
					vCpfCns = rnCthcValidaCboRespVO.getCpf();

					retorno.setMatriculaProf(rnCthcValidaCboRespVO.getMatriculaProf());
					retorno.setVinculoProf(rnCthcValidaCboRespVO.getVinculoProf());

					if (!rnCthcValidaCboRespVO.getRetorno()) {
						vCbo = null;
						vCpfCns = null;
					}
					logar("CBO Cirurgião no anestesista {0} - {1} cirg {2}", retorno.getEquipe(), vCbo, (vCirurgia != null ? vCirurgia.getSeq()
							: null));

					retorno.setCbo(vCbo);
					retorno.setCpfCns(vCpfCns);
				}
			}

			// fim informa dados do cirurgião se não encontra o anestesista
			if (vCbo == null) {
				final Integer valorParametroPhiAnestesista = this.buscarVlrInteiroAghParametro(AghuParametrosEnum.P_PHI_ANESTESISTA);
				RapServidores anestesista = fatItemContaHospitalarDAO.buscarAnestesistaItensContaCirurgias(pCthSeq, valorParametroPhiAnestesista);
				if (anestesista == null) {
					logar("c_itens_conta_resp%notfound ");
					retorno.setRetorno(Boolean.FALSE);
					return retorno;
				} else {
					logar("chama valida CBO");
					rnCthcValidaCboRespVO = this.rnCthcValidaCboResp(pCthSeq, (anestesista != null ? anestesista.getId().getMatricula() : null),
							(anestesista != null ? anestesista.getId().getVinCodigo() : null), null, null, null, null, vTipo, pPhoSeq, pIphSeq,
							pTipoAto, dtRealizado, nomeMicrocomputador, dataFimVinculoServidor);

					vCbo = rnCthcValidaCboRespVO.getCbo();
					vCpfCns = rnCthcValidaCboRespVO.getCpf();

					retorno.setMatriculaProf(rnCthcValidaCboRespVO.getMatriculaProf());
					retorno.setVinculoProf(rnCthcValidaCboRespVO.getVinculoProf());

					if (!rnCthcValidaCboRespVO.getRetorno()) {
						vCbo = null;
						vCpfCns = null;
					}
					logar("CBO Ato {0} - {1} resp {3} - {4} v_cpfcns={5}", retorno.getEquipe(), vCbo, (anestesista != null ? anestesista.getId().getMatricula()
							: null), (anestesista != null ? anestesista.getId().getVinCodigo() : null), vCpfCns);

					retorno.setCbo(vCbo);
					retorno.setCpfCns(vCpfCns);
				}
			}
			if (vCbo == null) {
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}
			retorno.setCbo(vCbo);

			if (vCpfCns == null) {
				retorno.setRetorno(Boolean.FALSE);
				return retorno;
			}
			retorno.setCpfCns(vCpfCns);

		}

		retorno.setRetorno(Boolean.TRUE);
		return retorno;
	}

	/**
	 * ORADB Procedure FATK_CTH6_RN_UN.RN_CTHC_VALIDA_CBO_RESP
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public RnCthcValidaCboRespVO rnCthcValidaCboResp(Integer pCthSeq, Integer pSerMatricula, Short pSerVinCodigo, Integer pSoeSeq, Short pIseSeqp,
			Integer pConNumero, Integer pCrgSeq, String pTipo, Short pPhoSeq, Integer pIphSeq, Byte pTipoAto
			// -- Ney 26/08/2011 Cbo por competencia
			, final Date dtRealizado, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		final FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();
		final FatProcedimentoCboDAO fatProcedimentoCboDAO = getFatProcedimentoCboDAO();

		String complexidade = null;
		Boolean vIndFaec = null; // -- Marina 25/07/2013

		// Marina 04/02/2010
		String vCboExclusaoCritica = null;

		logar("..............BUSCA CBO COMP..............");
		
		logar("P_SER_MATRICULA: {0}P_SER_VIN_CODIGO: {1}", pSerMatricula, pSerVinCodigo);		

		final RnCthcValidaCboRespVO retorno = new RnCthcValidaCboRespVO();

		RapServidores servidor = null;

		final String valorParametroCirurgiao = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CBO_CIRURGIAO);

		final Byte valorParametroAtoAnestesista = this.buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_ANESTESISTA);

		// Procura o profissional Responsável.
		if (pSerMatricula != null && pSerVinCodigo != null) {
			servidor = getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(new RapServidoresId(pSerMatricula, pSerVinCodigo));
		} else if (pSoeSeq != null && pIseSeqp != null) {
			final String valorParametroSitLiberado = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
			servidor = getRegistroColaboradorFacade().buscarRapServidorDeAelExtratoItemSolicitacao(pSoeSeq, pIseSeqp, valorParametroSitLiberado);
		} else if (pCrgSeq != null) {
			if (CoreUtil.igual(valorParametroCirurgiao, pTipo)) { // CBO Cirurgião
				servidor = getBlocoCirurgicoFacade().buscaRapServidorDeMbcProfCirurgias(pCrgSeq, DominioSimNao.S);
				
			} else { // CBO Anestesista
				// CBO Anestesista da Descricao
				servidor = getBlocoCirurgicoFacade().buscaServidorProfPorCrgSeqETipoAtuacao(pCrgSeq, DominioTipoAtuacao.ANES);

				if (servidor == null) {
					// CBO Anestesista da Nota
					DominioFuncaoProfissional[] funcoes = new DominioFuncaoProfissional[] { DominioFuncaoProfissional.ANP,
							DominioFuncaoProfissional.ANR, DominioFuncaoProfissional.ANC };
					servidor = getBlocoCirurgicoFacade().buscaRapServidorDeMbcProfCirurgias(pCrgSeq, funcoes);

					if (servidor == null) {
						// CBO Anestesista do PDT
						servidor = getBlocoCirurgicoProcDiagTerapFacade().buscaRapServidorDePdtProfissao(pCrgSeq, DominioTipoAtuacao.ANES);
					}
				}
			}
		}
		// dbms_output.put_line('p_ser_matricula: ' || p_ser_matricula);
		// dbms_output.put_line('p_ser_vin_codigo: ' || p_ser_vin_codigo);
		logar("p_ser_matricula: {0}", pSerMatricula);
		logar("p_ser_vin_codigo: {0}", pSerVinCodigo);

		Integer vPesCodigo = (servidor != null && servidor.getPessoaFisica() != null) ? servidor.getPessoaFisica().getCodigo() : null;
		Short vVinCodigo = servidor != null ? servidor.getId().getVinCodigo() : null;
		Integer vMatricula = servidor != null ? servidor.getId().getMatricula() : null;
		Long vCpfCns = (servidor != null && servidor.getPessoaFisica() != null) ? servidor.getPessoaFisica().getCpf() : null;

		retorno.setCpf(vCpfCns);
		retorno.setMatriculaProf(vMatricula);
		retorno.setVinculoProf(vVinCodigo);

		// De posse do profissional responsável - descobrir CBOs vinculados a ele.
		logar("v_pes_codigo: {0} p_dt_realizado={1}", vPesCodigo, DateUtil.obterDataFormatada(dtRealizado, "dd/MM/yyyy")); // -- Ney 26/08/2011 Cbo por competencia
		logar("P_TIPO_ATO: {0}", pTipoAto);

		final Short[] tiiSeqs = buscarVlrShortArrayAghParametro(AghuParametrosEnum.P_TIPO_INFORMACAO_CONTEM_CBO);
		List<RapPessoaTipoInformacoes> pessoasTipoInformacao = getRegistroColaboradorFacade().listarPorPessoaFisicaTipoInformacao(vPesCodigo,
				tiiSeqs, dtRealizado); // -- Ney 26/08/2011 Cbo por competencia

		for (RapPessoaTipoInformacoes rapPessoaTipoInformacoes : pessoasTipoInformacao) {
			logar("r_get_valor_cbo.seq: {0}", rapPessoaTipoInformacoes.getId().getTiiSeq());
			logar("r_get_valor_cbo.valor: {0}", rapPessoaTipoInformacoes.getValor());

			// Marina 04/02/2010
			if (vCboExclusaoCritica == null) {
				vCboExclusaoCritica = rapPessoaTipoInformacoes.getValor();
			}

			final Byte atoAnes = buscarVlrByteAghParametro(AghuParametrosEnum.P_ATO_ANESTESISTA);
			if (!CoreUtil.igual(nvl(pTipoAto, 0), atoAnes)) {

				logar("P_PHO_SEQ: {0}", pPhoSeq);
				logar("P_IPH_SEQ: {0}", pIphSeq);
				logar("r_get_valor_cbo.valor: {0}", rapPessoaTipoInformacoes.getValor());

//				FatProcedimentoCboId idFatProcedimentoCbo = new FatProcedimentoCboId(pPhoSeq, pIphSeq, rapPessoaTipoInformacoes.getValor());
				FatProcedimentoCbo fatProcedimentoCbo = fatProcedimentoCboDAO.obterPorPhoIphSeqCboCompetencia(pPhoSeq, pIphSeq, rapPessoaTipoInformacoes.getValor(), dtRealizado);
				if (fatProcedimentoCbo == null) {
					// Marina 30/12/2009
					// Se ele não achou CBO compatível, e conta é de média
					// complexidade, faz exclusão de critica
					List<FatItensProcedHospitalar> listaItensProcedHospitalar = fatItensProcedHospitalarDAO.buscarComplexidade(pPhoSeq, pIphSeq,
 							DominioSituacao.A);
					if (!listaItensProcedHospitalar.isEmpty()) {
						FatItensProcedHospitalar itemProcedHospitalar = listaItensProcedHospitalar.get(0);
						FatCaractFinanciamento caractFinanciamento = itemProcedHospitalar.getFatCaracteristicaFinanciamento();
						if (caractFinanciamento != null) {
							complexidade = caractFinanciamento.getCodigo();						
						}
						vIndFaec = itemProcedHospitalar.getFaec();
					}
										 
					logar("COMPLEXIDADE: {0}V_IND_FAEC: {1}", complexidade, vIndFaec);
					
					final String valorParametroComplMedia = this
							.buscarVlrTextoAghParametro(AghuParametrosEnum.P_COMPLEX_MDO_FINANCIAMENTO);
					
					//-- Marina 03/04/2013
					if (CoreUtil.igual(valorParametroComplMedia, complexidade) || Boolean.FALSE.equals(vIndFaec)) {
						// -- Marina 23/07/2013 - Verifica a família do CBO, se
						// pertence a 2232,2231,2251,5525,2253, o sistema deverá
						// aceitar qualquer CBO
						Long valor = Long.valueOf(rapPessoaTipoInformacoes.getValor().substring(0, 4));
						logar("CBO: {0}", valor);
						
						final Long[] grupoCbosValidos = buscarVlrLongArrayAghParametro(AghuParametrosEnum.P_AGHU_GRUPO_CBO_VALIDO_ENC_CTA);
					
						if (ArrayUtils.contains(grupoCbosValidos, valor)) {
							logar("entrou na nova regra de liberação do CBO: {0}", rapPessoaTipoInformacoes.getValor());
							// Abribui o cbo principal da conta e d exclusão de crítica
							retorno.setCbo(rapPessoaTipoInformacoes.getValor());
							//fatkCthRN.rnFatpExclusaoCritica(DominioSituacao.I, DominioSituacao.I, 
							//								DominioSituacao.A, DominioSituacao.I, 
							//								DominioSituacao.I, pCthSeq, DominioSituacao.I, 
							//								nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
							retorno.setRetorno(Boolean.TRUE);
							return retorno;
						}
					}
				} else {
					retorno.setCbo(rapPessoaTipoInformacoes.getValor());
					logar("achou");
					logar("p_cbo: {0}", rapPessoaTipoInformacoes.getValor());
					retorno.setRetorno(Boolean.TRUE);
					return retorno;
				}
			} else {
				logar("entrou no caso do anestesista");
				logar("r_get_valor_cbo.seq: {0}", rapPessoaTipoInformacoes.getId().getTiiSeq());
				logar("r_get_valor_cbo.valor: {0}", rapPessoaTipoInformacoes.getValor());
				logar("P_TIPO_ATO: {0}", pTipoAto);
				logar("p_tipo : {0}", pTipo);

				// Verifica se o médico tem CBO de Anestesista
				if (CoreUtil.igual(valorParametroAtoAnestesista, pTipoAto) && CoreUtil.igual(valorParametroCirurgiao, pTipo)) {
					retorno.setCbo(rapPessoaTipoInformacoes.getValor());
					retorno.setRetorno(Boolean.TRUE);
					return retorno;
				} else {
					// -- Ney 26/08/2011 Cbo por competencia
//					final String[] valoresParametroAnestesiologista = new String[] {
//							this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_MEDICO_ANESTESIOLOGISTA),
//							// adicionado novo parâmetro da portaria
//							this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_MEDICO_ANESTESIOLOGISTA_PORTARIA_203_2011) };
					

					final String[] valoresParametroAnestesiologista = new String[] {
							this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_MEDICO_ANESTESIOLOGISTA),
							// adicionado novo parâmetro da portaria
							this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_MEDICO_ANESTESIOLOGISTA_PORTARIA_203_2011)};

					String valorAnestesiologista = getRegistroColaboradorFacade().listarPorPessoaFisicaValorTipoInformacao(vPesCodigo,
							valoresParametroAnestesiologista, dtRealizado); // -- Ney 26/08/2011 Cbo por competencia
					if (valorAnestesiologista != null) {
						retorno.setCbo(valorAnestesiologista);
						retorno.setRetorno(Boolean.TRUE);
						return retorno;
					}
				}
				logar("pCbo: {0}", retorno.getCbo());
			}
		}
		logar("não achou CBO compatível");
		retorno.setRetorno(Boolean.FALSE);
		retorno.setCbo(null);
		retorno.setCpf(null);
		return retorno;
	}

	protected FatkCthRN getFatkCthRN() {
		return fatkCthRN;
	}
}
