package br.gov.mec.aghu.faturamento.business;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.vo.RnIchcAtuRegrasVO;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * <p>
 * Linhas: 591 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 0 <br/>
 * Consultas: 7 tabelas <br/>
 * Alteracoes: 15 tabelas <br/>
 * Metodos: 5 <br/>
 * Metodos externos: 0 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_ICH_RN</code>
 * </p>
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.CyclomaticComplexity","PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FaturamentoFatkIchRN extends AbstractFatDebugLogEnableRN {


private static final String INT = "INT";

private static final String RN_ICHC_ATU_REGRAS = "RN_ICHC_ATU_REGRAS";

@EJB
private VerificacaoItemProcedimentoHospitalarRN verificacaoItemProcedimentoHospitalarRN;

@EJB
private FaturamentoRN faturamentoRN;

@EJB
private FaturamentoON faturamentoON;

private static final Log LOG = LogFactory.getLog(FaturamentoFatkIchRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatContasHospitalaresDAO fatContasHospitalaresDAO;

@EJB
private IFaturamentoFacade faturamentoFacade;

@Inject
private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;

@EJB
private IAghuFacade aghuFacade;

@Inject
private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;

@Inject
private FatEspelhoItemContaHospDAO fatEspelhoItemContaHospDAO;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;
	private static final long serialVersionUID = 3133535121334840000L;

	/**
	 * <p>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>OK</b><br/>
	 * Linhas: 483 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 4 tabelas <br/>
	 * Alteracoes: 11 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_ICHC_ATU_REGRAS</code>
	 * </p>
	 * <p>
	 * <b>INSERT:</b> {@link FatLogError} <br/>
	 * </p>
	 * @param pCthSeq 
	 * @param pDataPrevia 
	 * @param pPacCodigo 
	 * @param pPacProntuario 
	 * @param pPhiRealiz 
	 * @param pPhoRealiz 
	 * @param pCodSusRealiz 
	 * @param pPhiSeq 
	 * @param pIphPhoSeq 
	 * @param pIphSeq 
	 * @param pIphCodSus 
	 * @param pModoCobranca 
	 * @param pVlrSh 
	 * @param pVlrSadt 
	 * @param pVlrProc 
	 * @param pVlrAnest 
	 * @param pVlrSp 
	 * @param pPacDtNasc 
	 * @param pCodExclusaoCritica 
	 * @param dthrRealizado --> Ney 15/07/2011 Portaria 203 Fase 2
	 * @return 
	 * @throws BaseException 
	 * @see FatItemContaHospitalar
	 * @see FatItensProcedHospitalar
	 * @see FatLogError
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength"})
	public RnIchcAtuRegrasVO rnIchcAtuRegras(Integer pCthSeq, Date pDataPrevia, Integer pPacCodigo, Integer pPacProntuario,
			Integer pPhiRealiz, Short pPhoRealiz, Integer pIphRealiz, Long pCodSusRealiz, Integer pPhiSeq, Short pIphPhoSeq,
			Integer pIphSeq, Long pIphCodSus, DominioModoCobranca pModoCobranca, BigDecimal pVlrSh, BigDecimal pVlrSadt,
			BigDecimal pVlrProc, BigDecimal pVlrAnest, BigDecimal pVlrSp, Date pPacDtNasc, Integer pCodExclusaoCritica, Date pDtHrRealizado) throws BaseException {
		
		FaturamentoON faturamentoON = getFaturamentoON();

		VerificacaoItemProcedimentoHospitalarRN verificacaoItemProcedimentoHospitalarRN = getVerificacaoItemProcedimentoHospitalarRN();
		FatItemContaHospitalarDAO fatItemContaHospitalarDAO = getFatItemContaHospitalarDAO();
		FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO = getFatItensProcedHospitalarDAO();

		RnIchcAtuRegrasVO rnIchcAtuRegrasVO = new RnIchcAtuRegrasVO();

		rnIchcAtuRegrasVO.setModoCobranca(pModoCobranca);
		rnIchcAtuRegrasVO.setVlrSh(pVlrSh);
		rnIchcAtuRegrasVO.setVlrSadt(pVlrSadt);
		rnIchcAtuRegrasVO.setVlrProc(pVlrProc);
		rnIchcAtuRegrasVO.setVlrAnest(pVlrAnest);
		rnIchcAtuRegrasVO.setVlrSp(pVlrSp);
		rnIchcAtuRegrasVO.setCodExclusaoCritica(pCodExclusaoCritica);

		Boolean exigeValor = false;
		Boolean procedTemValor = false;
		Integer vIdadePac = null;
		Integer vIdadeLimite = null;
		Integer vClinicaPediatrica = null;
		Integer vClinicaMedica = null;
		Integer vClc = null;
		Integer vidmin = null;
		Integer vidmax = null;
		FatItemContaHospitalar regIchPhi = null;
		Boolean internacao = null;
		Boolean vCirmultPolitr = null;
		Boolean vAids = null;
		Boolean vBuscaDoador = null;

		Short result = 1;

		// Verifica se o hospital esta habilitado a executar o procedimento
		Boolean hospCadastrado = verificacaoItemProcedimentoHospitalarRN.getHcpaCadastrado(pIphPhoSeq, pIphSeq);
		if (!hospCadastrado) {
			logar("HOSPITAL NAO CADASTRADO");

			List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO.listarPorCthPhiSituacao(pCthSeq, pPhiSeq,
					DominioSituacaoItenConta.A);
			if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
				for (int i = 0; i < itensContaHospitalar.size(); i++) {
					regIchPhi = itensContaHospitalar.get(i);

					faturamentoON.criarFatLogErrors("HOSPITAL NAO CADASTRADO PARA EXECUTAR O PROCEDIMENTO.", INT, pCthSeq, null, null,
							pIphCodSus, null, pDataPrevia, null, regIchPhi.getId().getSeq(), null, null, null, null, pPacCodigo, null,
							null, null, pPacProntuario, null, null, pIphPhoSeq, null, pIphSeq, null, pPhiSeq, null,
							RN_ICHC_ATU_REGRAS, null, null,new FatMensagemLog(91));
				}
			}

			result = 0; // NAO PODE COBRAR O ITEM
		}

		// Verifica se o procedimento e' cobravel em internacao
		String vCobrancaConta = verificacaoItemProcedimentoHospitalarRN.verificarPossibilidadeCobranca(pIphPhoSeq, pIphSeq, "I");
		Boolean permiteCobrConta = vCobrancaConta != null;
		if (!permiteCobrConta) {
			logar("NAO ACHOU LOCAL COBRANCA");
			List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO.listarPorCthPhiSituacao(pCthSeq, pPhiSeq,
					DominioSituacaoItenConta.A);
			if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
				for (int i = 0; i < itensContaHospitalar.size(); i++) {
					regIchPhi = itensContaHospitalar.get(i);
					faturamentoON.criarFatLogErrors("NAO FOI POSSIVEL IDENTIFICAR PERMISSAO DE COBRANCA DO PROCEDIMENTO.", INT,
							pCthSeq, null, null, pIphCodSus, null, pDataPrevia, null, regIchPhi.getId().getSeq(), null, null, null,
							null, pPacCodigo, null, null, null, pPacProntuario, null, null, pIphPhoSeq, null, pIphSeq, null, pPhiSeq,
							null, RN_ICHC_ATU_REGRAS, null, null,new FatMensagemLog(177));
				}
			}
			result = 0; // NAO PODE COBRAR O ITEM
		} else {
			if ("N".equals(vCobrancaConta)) {
				logar("ITEM NAO PODE SER COBRADO EM AIH");
				List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO.listarPorCthPhiSituacao(pCthSeq,
						pPhiSeq, DominioSituacaoItenConta.A);
				if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
					for (int i = 0; i < itensContaHospitalar.size(); i++) {
						regIchPhi = itensContaHospitalar.get(i);
						faturamentoON.criarFatLogErrors("PROCEDIMENTO NAO PODE SER COBRADO EM AIH.", INT, pCthSeq, null, null,
								pIphCodSus, null, pDataPrevia, null, regIchPhi.getId().getSeq(), null, null, null, null, pPacCodigo,
								null, null, null, pPacProntuario, null, null, pIphPhoSeq, null, pIphSeq, null, pPhiSeq, null,
								RN_ICHC_ATU_REGRAS, null, null,new FatMensagemLog(209));
					}
				}
				result = 0; // NAO PODE COBRAR O ITEM
			}
		}

		// Verifica se sexo do paciente e' compativel c/ sexo do procedimento
		Boolean sexoCompativel = verificacaoItemProcedimentoHospitalarRN.verificarCompatibilidadeSexoPacienteSexoProcedimento(pIphPhoSeq,
				pIphSeq, pPacCodigo);
		if (!sexoCompativel) {
			logar("SEXO DO PACIENTE INCOMPATIVEL");
			List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO.listarPorCthPhiSituacao(pCthSeq, pPhiSeq,
					DominioSituacaoItenConta.A);
			if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
				for (int i = 0; i < itensContaHospitalar.size(); i++) {
					regIchPhi = itensContaHospitalar.get(i);
					faturamentoON.criarFatLogErrors("SEXO DO PACIENTE INCOMPATIVEL COM O DO PROCEDIMENTO.", INT, pCthSeq, null,
							null, pIphCodSus, null, pDataPrevia, null, regIchPhi.getId().getSeq(), null, null, null, null, pPacCodigo,
							null, null, null, pPacProntuario, null, null, pIphPhoSeq, null, pIphSeq, null, pPhiSeq, null,
							RN_ICHC_ATU_REGRAS, null, null,new FatMensagemLog(248));
				}
			}
			result = 0; // NAO PODE COBRAR O ITEM
		}

		// Verifica se o procedimento e' nosologia (SSM)
		FatItensProcedHospitalar itemProcedHospitalar = fatItensProcedHospitalarDAO
				.obterPorChavePrimaria(new FatItensProcedHospitalarId(pIphPhoSeq, pIphSeq));
		if (itemProcedHospitalar != null) {
			internacao = itemProcedHospitalar.getInternacao();
		}
		internacao = internacao != null ? internacao : false;

		// Busca indicadores de procedimento realizado de cirurgia multipla ou
		// de AIDS/politraumatizado/busca ativa doador
		itemProcedHospitalar = fatItensProcedHospitalarDAO
				.obterPorChavePrimaria(new FatItensProcedHospitalarId(pPhoRealiz, pIphRealiz));
		if (itemProcedHospitalar != null) {
			vCirmultPolitr = itemProcedHospitalar.getCirurgiaMultipla();
			vAids = itemProcedHospitalar.getAidsPolitraumatizado();
			vBuscaDoador = itemProcedHospitalar.getBuscaDoador();
		}

		vCirmultPolitr = vCirmultPolitr != null ? vCirmultPolitr : false;
		vAids = vAids != null ? vAids : false;
		vBuscaDoador = vBuscaDoador != null ? vBuscaDoador : false;

		if (((pPhiSeq == null && pPhiRealiz != null) || (pPhiSeq != null && pPhiRealiz == null) || (pPhiSeq != null && !pPhiSeq
				.equals(pPhiRealiz)))
				&& ((pIphCodSus == null && pCodSusRealiz != null) || (pIphCodSus != null && pCodSusRealiz == null) || (pIphCodSus != null && !pIphCodSus
						.equals(pCodSusRealiz)))) {
			logar("nao eh o realiz");
			if (internacao && (vCirmultPolitr || vAids || vBuscaDoador)) {
				logar("proc eh SSM E realiz de cirmult/politr/aids/buscativ");

				// Verifica exclusao critica de idade para o proced
				// Busca limites de idade para o procedimento
				itemProcedHospitalar = fatItensProcedHospitalarDAO.obterPorChavePrimaria(new FatItensProcedHospitalarId(pIphPhoSeq,
						pIphSeq));
				if (itemProcedHospitalar != null) {
					vidmin = itemProcedHospitalar.getIdadeMin();
					vidmax = itemProcedHospitalar.getIdadeMax();
					vClc = itemProcedHospitalar.getClinica() != null ? itemProcedHospitalar.getClinica().getCodigo() : null;
				}

				vidmin = vidmin != null ? vidmin : 0;
				vidmax = vidmax != null ? vidmax : 0;

				// Verifica idade do paciente
				vIdadePac = CoreUtil.calculaIdade(pPacDtNasc);

				// Verifica limite de idade entre paciente pediatrico e adulto
				vIdadeLimite = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_LIMITE_IDADE_PEDIATR_ADULTO);

				// Verifica codigo de clinica pediatrica
				vClinicaPediatrica = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_CLINICA_PEDIATRICA);

				// Verifica codigo de clinica medica
				vClinicaMedica = buscarVlrInteiroAghParametro(AghuParametrosEnum.P_COD_CLINICA_MEDICA);

				if (vClc == null) {
					logar("proced nao tem clc valida");

					// Verifica clinica conforme idade do paciente
					if (vIdadePac <= vIdadeLimite) {
						vClc = vClinicaPediatrica;
						logar("clc ped (pela idade pac)");
					} else if (vIdadePac > vIdadeLimite) {
						vClc = vClinicaMedica;
						logar("clc med (pela idade pac)");
					}
				}

				logar("idmin:{0} idmax:{1} clc:{2} idpac:{3} idlim:{4} clcped:{5} clcmed:{6}", vidmin, vidmax, vClc, vIdadePac, vIdadeLimite, vClinicaPediatrica, vClinicaMedica);

				// Verifica se idade paciente e' compativel c/idade permitida
				// p/proced
				if (vIdadePac < vidmin || vIdadePac > vidmax) {
					logar("idade pac fora da faixa do proced");

					// Verifica compatibilidade da clinica do proced c/idade do
					// paciente:
					if (vIdadePac < vIdadeLimite && CoreUtil.igual(vClc, vClinicaMedica)) {
						logar("pac pediatrico e clc medica");
						List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO.listarPorCthPhiSituacao(
								pCthSeq, pPhiSeq, DominioSituacaoItenConta.A);
						if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
							for (int i = 0; i < itensContaHospitalar.size(); i++) {
								regIchPhi = itensContaHospitalar.get(i);
								faturamentoON.criarFatLogErrors("PROCEDIMENTO DE CLINICA MEDICA, INCOMPATIVEL COM IDADE DO PACIENTE.",
										INT, pCthSeq, null, null, pIphCodSus, null, pDataPrevia, null, regIchPhi.getId().getSeq(),
										null, null, null, null, pPacCodigo, null, null, null, pPacProntuario, null, null, pIphPhoSeq,
										null, pIphSeq, null, pPhiSeq, null, RN_ICHC_ATU_REGRAS, null, null,new FatMensagemLog(201));
							}
						}
						result = 0; // NAO PODE COBRAR O ITEM
					} else if (vIdadePac >= vIdadeLimite
							&& ((vClc == null && vClinicaPediatrica == null) || (vClc != null && vClc.equals(vClinicaPediatrica)))) {
						logar("pac adulto e clc pediatrica");
						List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO.listarPorCthPhiSituacao(
								pCthSeq, pPhiSeq, DominioSituacaoItenConta.A);
						if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
							for (int i = 0; i < itensContaHospitalar.size(); i++) {
								regIchPhi = itensContaHospitalar.get(i);
								faturamentoON.criarFatLogErrors(
										"PROCEDIMENTO DE CLINICA PEDIATRICA, INCOMPATIVEL COM IDADE DO PACIENTE.", INT, pCthSeq,
										null, null, pIphCodSus, null, pDataPrevia, null, regIchPhi.getId().getSeq(), null, null, null,
										null, pPacCodigo, null, null, null, pPacProntuario, null, null, pIphPhoSeq, null, pIphSeq,
										null, pPhiSeq, null, RN_ICHC_ATU_REGRAS, null, null,new FatMensagemLog(202));
							}
						}
						result = 0; // NAO PODE COBRAR O ITEM
					} else {
						// Verifica exclusao critica tipo 2 e 3:
						if (vIdadePac < vidmin) {
							logar("idade menor");

							if (rnIchcAtuRegrasVO.getCodExclusaoCritica() == null
									|| rnIchcAtuRegrasVO.getCodExclusaoCritica().equals(2)) {
								logar("nao tinha outra exclusao critica");
								rnIchcAtuRegrasVO.setCodExclusaoCritica(2);
							} else {
								logar("ja tinha outra exclusao critica");

								List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO
										.listarPorCthPhiSituacao(pCthSeq, pPhiSeq, DominioSituacaoItenConta.A);
								if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
									for (int i = 0; i < itensContaHospitalar.size(); i++) {
										regIchPhi = itensContaHospitalar.get(i);
										faturamentoON.criarFatLogErrors("CONTA POSSUI MAIS DE UMA EXCLUSAO DE CRITICA: 2 e "
												+ rnIchcAtuRegrasVO.getCodExclusaoCritica(), INT, pCthSeq, null, null, pIphCodSus,
												null, pDataPrevia, null, regIchPhi.getId().getSeq(), null, null, null, null,
												pPacCodigo, null, null, null, pPacProntuario, null, null, pIphPhoSeq, null, pIphSeq,
												null, pPhiSeq, null, RN_ICHC_ATU_REGRAS, null, null, null);
									}
								}
								result = -1; // NAO PODE ENCERRAR A CONTA
							}
						}

						if (vIdadePac > vidmax) {
							logar("idade maior");
							if (rnIchcAtuRegrasVO.getCodExclusaoCritica() == null
									|| rnIchcAtuRegrasVO.getCodExclusaoCritica().equals(3)) {
								logar("nao tinha outra exclusao critica");
								rnIchcAtuRegrasVO.setCodExclusaoCritica(3);
							} else {
								logar("ja tinha outra exclusao critica");

								List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO
										.listarPorCthPhiSituacao(pCthSeq, pPhiSeq, DominioSituacaoItenConta.A);
								if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
									for (int i = 0; i < itensContaHospitalar.size(); i++) {
										regIchPhi = itensContaHospitalar.get(i);
										faturamentoON.criarFatLogErrors("CONTA POSSUI MAIS DE UMA EXCLUSAO DE CRITICA: 3 e "
												+ rnIchcAtuRegrasVO.getCodExclusaoCritica(), INT, pCthSeq, null, null, pIphCodSus,
												null, pDataPrevia, null, regIchPhi.getId().getSeq(), null, null, null, null,
												pPacCodigo, null, null, null, pPacProntuario, null, null, pIphPhoSeq, null, pIphSeq,
												null, pPhiSeq, null, RN_ICHC_ATU_REGRAS, null, null, null);
									}
								}
								result = -1; // NAO PODE ENCERRAR A CONTA
							}
						}
					}
				}
			} else {
				logar("proc nao eh SSM ou realiz nao eh de nenhum dos 4 tipos");

				// Verifica se a idade do paciente e' compativel com a idade
				// permitida para o procedimento
				Boolean idadeCompativel = verificacaoItemProcedimentoHospitalarRN.verificarFaixaIdadePorCodPaciente(pPacCodigo, pIphPhoSeq,
						pIphSeq);
				if (!idadeCompativel) {
					logar("IDADE DO PACIENTE INCOMPATIVEL");
					List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO.listarPorCthPhiSituacao(pCthSeq,
							pPhiSeq, DominioSituacaoItenConta.A);
					if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
						for (int i = 0; i < itensContaHospitalar.size(); i++) {
							regIchPhi = itensContaHospitalar.get(i);
							faturamentoON.criarFatLogErrors(
									"IDADE DO PACIENTE INCOMPATIVEL COM AS IDADES MINIMA E MAXIMA PERMITIDAS PARA O PROCEDIMENTO.",
									INT, pCthSeq, null, null, pIphCodSus, null, pDataPrevia, null, regIchPhi.getId().getSeq(), null,
									null, null, null, pPacCodigo, null, null, null, pPacProntuario, null, null, pIphPhoSeq, null,
									pIphSeq, null, pPhiSeq, null, RN_ICHC_ATU_REGRAS, null, null,new FatMensagemLog(96));
						}
					}
					result = 0; // NAO PODE COBRAR O ITEM
				}
			}
		}

		// Verifica se o procedimento e' especial
		Boolean especial = verificacaoItemProcedimentoHospitalarRN.getProcedimentoEspecial(pIphPhoSeq, pIphSeq);
		if (especial || internacao || DominioModoCobranca.V.equals(rnIchcAtuRegrasVO.getModoCobranca())) {
			rnIchcAtuRegrasVO.setModoCobranca(DominioModoCobranca.V);

			// Verifica se o procedimento possui valor cadastrado p/a competencia
			FatVlrItemProcedHospComps fatVlrItemProcedHospComps = verificacaoItemProcedimentoHospitalarRN
					.obterValoresItemProcHospPorModuloCompetencia(pIphPhoSeq, pIphSeq, DominioModuloCompetencia.INT,
							// Ney 15/07/2011 Portaria 203 Fase 2 
							pDtHrRealizado);

			if (fatVlrItemProcedHospComps != null) {
				procedTemValor = true;
				rnIchcAtuRegrasVO.setVlrSh((BigDecimal)CoreUtil.nvl(fatVlrItemProcedHospComps.getVlrServHospitalar(), BigDecimal.ZERO));
				rnIchcAtuRegrasVO.setVlrSadt((BigDecimal)CoreUtil.nvl(fatVlrItemProcedHospComps.getVlrSadt(), BigDecimal.ZERO));
				rnIchcAtuRegrasVO.setVlrProc((BigDecimal)CoreUtil.nvl(fatVlrItemProcedHospComps.getVlrProcedimento(), BigDecimal.ZERO));
				rnIchcAtuRegrasVO.setVlrAnest((BigDecimal)CoreUtil.nvl(fatVlrItemProcedHospComps.getVlrAnestesista(), BigDecimal.ZERO));
				rnIchcAtuRegrasVO.setVlrSp((BigDecimal)CoreUtil.nvl(fatVlrItemProcedHospComps.getVlrServProfissional(), BigDecimal.ZERO));
			} else {
				procedTemValor = false;
			}

			if (!procedTemValor) {
				// Verifica se o procedimento exige que haja valores cadastrados
				// e, neste caso, se existem valores cadastrados
				exigeValor = verificacaoItemProcedimentoHospitalarRN.getExigeValor(pIphPhoSeq, pIphSeq);
				if (exigeValor) {
					logar("ITEM EXIGE VALOR MAS VALORES NAO ENCONTRADOS");
					List<FatItemContaHospitalar> itensContaHospitalar = fatItemContaHospitalarDAO.listarPorCthPhiSituacao(pCthSeq,
							pPhiSeq, DominioSituacaoItenConta.A);
					if (itensContaHospitalar != null && !itensContaHospitalar.isEmpty()) {
						for (int i = 0; i < itensContaHospitalar.size(); i++) {
							regIchPhi = itensContaHospitalar.get(i);
							faturamentoON.criarFatLogErrors("VALORES NAO ENCONTRADOS PARA O PROCEDIMENTO.", INT, pCthSeq, null,
									null, pIphCodSus, null, pDataPrevia, null, regIchPhi.getId().getSeq(), null, null, null, null,
									pPacCodigo, null, null, null, pPacProntuario, null, null, pIphPhoSeq, null, pIphSeq, null,
									pPhiSeq, null, RN_ICHC_ATU_REGRAS, null, null,new FatMensagemLog(260));
						}
					}
					result = 0; // NAO PODE COBRAR O ITEM
				}

				rnIchcAtuRegrasVO.setVlrSh(BigDecimal.ZERO);
				rnIchcAtuRegrasVO.setVlrSadt(BigDecimal.ZERO);
				rnIchcAtuRegrasVO.setVlrProc(BigDecimal.ZERO);
				rnIchcAtuRegrasVO.setVlrAnest(BigDecimal.ZERO);
				rnIchcAtuRegrasVO.setVlrSp(BigDecimal.ZERO);
			}
		} else {
			if (rnIchcAtuRegrasVO.getModoCobranca() == null || !DominioModoCobranca.V.equals(rnIchcAtuRegrasVO.getModoCobranca())) {
				rnIchcAtuRegrasVO.setModoCobranca(DominioModoCobranca.P);
			}
			rnIchcAtuRegrasVO.setVlrSh(BigDecimal.ZERO);
			rnIchcAtuRegrasVO.setVlrSadt(BigDecimal.ZERO);
			rnIchcAtuRegrasVO.setVlrProc(BigDecimal.ZERO);
			rnIchcAtuRegrasVO.setVlrAnest(BigDecimal.ZERO);
			rnIchcAtuRegrasVO.setVlrSp(BigDecimal.ZERO);
		}

		rnIchcAtuRegrasVO.setRetorno(result);
		return rnIchcAtuRegrasVO;
	}

	/**
	 * <p>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>OK</b><br/>
	 * Linhas: 25 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 0 tabelas <br/>
	 * Alteracoes: 4 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_ICHP_ATU_SITUACAO</code>
	 * </p>
	 * <p>
	 * <b>UPDATE:</b> {@link FatItemContaHospitalar} <br/>
	 * </p>
	 * <p>
	 * <b>DELETE:</b> {@link FatEspelhoItemContaHosp} <br/>
	 * </p>
	 * @param pPrevia 
	 * @param pSituacao 
	 * @param pCthSeq 
	 * @param pIchSeq 
	 * @return 
	 * @throws BaseException 
	 * @see FatItemContaHospitalar
	 * @see FatEspelhoItemContaHosp
	 */
	public void rnIchpAtuSituacao(Boolean pPrevia, DominioSituacaoItenConta pSituacao, Integer pCthSeq, Short pIchSeq, final Date dataFimVinculoServidor) throws BaseException {
		FaturamentoRN faturamentoRN = getFaturamentoRN();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		if (!pPrevia) {
			FatItemContaHospitalar fatItensContaHospitalar = getFatItemContaHospitalarDAO().obterPorChavePrimaria(
					new FatItemContaHospitalarId(pCthSeq, pIchSeq));

			if (fatItensContaHospitalar != null && DominioSituacaoItenConta.A.equals(fatItensContaHospitalar.getIndSituacao())) {
				FatItemContaHospitalar itemContaHospitalarOld = null;
				try{
					itemContaHospitalarOld = getFaturamentoFacade().clonarItemContaHospitalar(fatItensContaHospitalar);
				}catch (Exception e) {
					logError("Exceção capturada: ", e);
					throw new BaseException(FaturamentoExceptionCode.ERRO_AO_ATUALIZAR);
				}
				fatItensContaHospitalar.setIndSituacao(pSituacao);
				getItemContaHospitalarON().atualizarItemContaHospitalarSemValidacoesForms( fatItensContaHospitalar, itemContaHospitalarOld, 
																						   false, servidorLogado, dataFimVinculoServidor, pPrevia, null);
			}
		}

		if (DominioSituacaoItenConta.N.equals(pSituacao)) {
			List<FatEspelhoItemContaHosp> auxLista = getFatEspelhoItemContaHospDAO().listarEspelhosItensContaHospitalar(pCthSeq,
					pIchSeq);

			if (auxLista != null && !auxLista.isEmpty()) {
				for (FatEspelhoItemContaHosp fatEspelhoItemContaHosp : auxLista) {
					faturamentoRN.excluirFatEspelhoItemContaHosp(fatEspelhoItemContaHosp, true);
					//getFaturamentoFacade().evict(fatEspelhoItemContaHosp);
				}
			}
		}
	}

	private IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}
	
	/**
	 * ORADB FATK_ICH_RN.RN_ICHP_VER_DTREALIZ
	 * 
	 * @param pCthSeq
	 * @param pDthrRealizado
	 * @param pIndOrigem
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void rnIchpVerDtrealiz(Integer pCthSeq, Date pDthrRealizado, DominioIndOrigemItemContaHospitalar pIndOrigem) throws ApplicationBusinessException {
		
		FatContasHospitalaresDAO dao = getFatContasHospitalaresDAO();
		FatContasHospitalares fatCntHosp = dao.obterPorChavePrimaria(pCthSeq);
		
		logar("v_dt_alta: {0}", fatCntHosp.getDtAltaAdministrativa());
		logar("p_dthr_realizado: {0}", pDthrRealizado);
		logar("v_dt_intern: {0}", fatCntHosp.getDataInternacaoAdministrativa());
		
		if (pDthrRealizado.before(fatCntHosp.getDataInternacaoAdministrativa())){
			 throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00586);
		}
		
		Date vDtAlta = fatCntHosp.getDtAltaAdministrativa();
		if (vDtAlta == null) {
			vDtAlta = new Date();
		}
		
		if (pDthrRealizado.after(vDtAlta) && CoreUtil.notIn(pIndOrigem, 
												DominioIndOrigemItemContaHospitalar.AEL, 
												DominioIndOrigemItemContaHospitalar.MPM,
												DominioIndOrigemItemContaHospitalar.ABS,
												DominioIndOrigemItemContaHospitalar.MPT,
												DominioIndOrigemItemContaHospitalar.BCC)) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00585);
		}
	}

	/**
	 * ORADB FATK_ICH_RN.RN_ICHP_VER_PHI_REAL
	 * 
	 * @param pCthSeq
	 * @param pIchPhiSeq
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void rnIchpVerPhiReal(Integer pCthSeq, Integer pIchPhiSeq) 
		throws ApplicationBusinessException {
		
		FatContasHospitalaresDAO dao = getFatContasHospitalaresDAO();
		FatContasHospitalares fatCntHosp = dao.obterPorChavePrimaria(pCthSeq);

		if (fatCntHosp.getProcedimentoHospitalarInternoRealizado() != null && (fatCntHosp.getProcedimentoHospitalarInternoRealizado().getSeq().equals(pIchPhiSeq))) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00588);
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected FaturamentoON getFaturamentoON() {
		return faturamentoON;
	}

	protected FaturamentoRN getFaturamentoRN() {
		return faturamentoRN;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected VerificacaoItemProcedimentoHospitalarRN getVerificacaoItemProcedimentoHospitalarRN() {
		return verificacaoItemProcedimentoHospitalarRN;
	}
	
	protected FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}
	
	protected FatEspelhoItemContaHospDAO getFatEspelhoItemContaHospDAO() {
		return fatEspelhoItemContaHospDAO;
	}
	
	protected FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}
	
	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}
	
}
