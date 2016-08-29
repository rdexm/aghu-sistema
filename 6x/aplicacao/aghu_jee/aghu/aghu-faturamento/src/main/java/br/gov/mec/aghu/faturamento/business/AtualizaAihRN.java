package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.dao.FatAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatLogErrorDAO;
import br.gov.mec.aghu.faturamento.dao.FatRetornaAihDAO;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatAutorizadoCma;
import br.gov.mec.aghu.model.FatAutorizadoCmaId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoAihId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatRetornaAih;
import br.gov.mec.aghu.model.FatRetornaAihId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * @oradb 	PROCEDURE  FATP_ATUALIZA_AIH
 */
@Stateless
public class AtualizaAihRN extends BaseBusiness {

	private static final long serialVersionUID = -7910361764880311629L;
	private static final Log LOG = LogFactory.getLog(AtualizaAihRN.class);
	private static final String IND_AUTORIZADO_SMS_N =  "N";
	private static final String IND_AUTORIZADO_SMS_S =  "S";
	private static final String IND_ENVIADO_SMS = "N";
	private static final String FATP_ATUALIZA_AIH = "FATP_ATUALIZA_AIH";
	private static final String MODULO_AIH = "AIH";
	private Boolean vAtualiza = Boolean.FALSE;
	
	@Inject
	private FatRetornaAihDAO fatRetornaAihDAO;
	
	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@Inject
	private FatAihDAO fatAihDAO;
	
	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;
	
	@Inject
	private FatLogErrorDAO fatLogErrorDAO; 
	
	@EJB
	private FaturamentoRN faturamentoRN;
	
	@EJB
	private FatAutorizadoCmaRN fatAutorizadoCmaRN;
	
	@EJB
	private EspelhoAihPersist espelhoAihPersistRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private FatLogErrorRN fatLogErrorRN; 
	
	@EJB
	private ContaHospitalarON contaHospitalarON;
	
	@EJB
	private FatkCthRN fatkCthRN;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public void  processarAtualizacaoAih(String nomeMicrocomputador) throws ApplicationBusinessException {
		List<FatRetornaAihId> cursorDeContaParaAtualizar = fatRetornaAihDAO.obterCursorDeContaParaAtualizarNativo();
		
		if(cursorDeContaParaAtualizar != null ){
			for (FatRetornaAihId rConta : cursorDeContaParaAtualizar) {
				if(rConta != null) {
					FatRetornaAih retornaAih = new FatRetornaAih(rConta);
					retornaAih.setId(rConta);
					processarContas(nomeMicrocomputador, retornaAih);
				}
			}			
		}
	}

	private void processarContas(String nomeMicrocomputador, FatRetornaAih rConta) throws ApplicationBusinessException {
		
		//Boolean vAtualiza = Boolean.FALSE;
		Boolean vOk = Boolean.FALSE;
		
		if(rConta.getId() != null) {
			
			getLogger().info("conta " + rConta.getId().getCthSeq());
			
			if("S".equals(rConta.getId().getIndTipoLaudo())) {
				procesarRetornoAihContaTipoLaudo(rConta);
			} else { 
				// tipo laudo <> 'S' -- laudo principal
				getLogger().info("tipo laudo diferente de s: " + rConta.getId().getCodSusAut());
				
				// Marina 16/12/2010
				// Sempre grava o cpf de quem autorizou a conta no espelho, não a necessidade de validar
				// se o autorizador tem perfil no AGH, pois, eles não autorizam mais as contas aqui no hospital e sim na SMS.
				gravarCpfAutorizadorContaEspelho(rConta,nomeMicrocomputador);
						
				getLogger().info("c_contas_hcpa");
				
				FatContasHospitalares rContasHcpa = obterCursorContaHCPA(rConta.getId().getCthSeq());
				
				getLogger().info("c_contas_hcpa " + rConta.getId().getCthSeq());
				
				if (rContasHcpa == null) {
					getLogger().info("IF c_contas_hcpa%notfound THEN");
					
					vAtualiza =  Boolean.FALSE;
					persistirFatLogError(rConta, "CONTA NÃO ENCERRADA OU JÁ AUTORIZADA ");
					return;
					
				} else { // if c_contas_hcpa%notfound
					getLogger().info("achou.......");
					
					gerarLogParaContaEncerradasNaoEnviadaSMS(rConta, rContasHcpa);
					
					getLogger().info("verifica a situacao da conta");
					// não tem espelho
					processarContaNaoTemEspelho(rConta, rContasHcpa);
					
					FatAih rAih = obterCursorAih(rConta.getId().getNroAih());
					
					getLogger().info("buscou aih " + rConta.getId().getNroAih() + " sit " + ((rAih != null) ? rAih.getIndSituacao().toString() : ""));
					// verifica se não encontrou aih
					if (rAih == null) {
						getLogger().info("vai inserir aih " + rConta.getId().getNroAih());
						try {
							inserirFatAih(rConta);
							vAtualiza =  Boolean.TRUE;
						} catch (ApplicationBusinessException e) {
							getLogger().info("AIH " + rConta.getId().getNroAih() + " " + e.getMessage());
							vAtualiza =  Boolean.FALSE;
							try {
								persistirFatLogError(rConta, "NAO FOI POSSIVEL INSERIR NA TABELA DE AIHS");
							} catch (Exception ex) {
								return;
							}
						}
					} else if (rAih != null && DominioSituacaoAih.U.equals(rAih.getIndSituacao())) {
						try {
							atualizarFatAih(rConta);
							vAtualiza =  Boolean.TRUE;
						} catch (ApplicationBusinessException e) {
							getLogger().info("AIH " + rConta.getId().getNroAih() + " " + e.getMessage());
							vAtualiza =  Boolean.FALSE;
							try {
								persistirFatLogError(rConta, "NAO FOI atualizar TABELA DE AIHS " + rConta.getId().getNroAih());
							} catch (Exception ex) {
								return;
							}
						}
					// achou aih ocupada	
					} else if (rAih != null && DominioSituacaoAih.A.equals(rAih.getIndSituacao())) {
						
						processarAih(rConta, rAih);
						
					} else { // não é a nem u, aih vencida ou bloqueada
						vAtualiza = Boolean.FALSE;
						try {
							persistirFatLogError(rConta, "AIH INDISPONÍVEL PARA USO");
						} catch (Exception ex) {
							return;
						}
					} // if c_aih%notfound
				}// if c_contas_hcpa%notfound
				
				getLogger().info("chegou aqui");
				
				processarAtualizacao(nomeMicrocomputador, rConta, vOk, rContasHcpa);
				
				verificarexclusaoPorCthModuloDescricaoErro(rConta);
			}// fim else	
		}
	}

	private void processarContaNaoTemEspelho(FatRetornaAih rConta, FatContasHospitalares rContasHcpa) throws ApplicationBusinessException {
		
		if(!CoreUtil.in(rContasHcpa.getIndSituacao(), DominioSituacaoConta.A, DominioSituacaoConta.F)) {
			
			FatEspelhoAih rEspelhosAih = obterCursorEspelhosAih(rConta);
			
			if(rEspelhosAih == null){
				 getLogger().info("não encontrou espelho " + rConta.getId().getCthSeq());
				 
				 vAtualiza =  Boolean.FALSE;
				 persistirFatLogError(rConta, "NAO FOI POSSIVEL LOCALIZAR O ESPELHO DA AIH");
				 return;
				
			} else if (CoreUtil.modificados(rEspelhosAih.getIphCodSusRealiz(), rConta.getId().getCodSusAut())  ) {
				getLogger().info("autorizado <> realizado " + rConta.getId().getCthSeq());
				
				vAtualiza =  Boolean.FALSE;
				persistirFatLogError(rConta, "CODIGO AUTORIZADO DIFERENTE DO REALIZADO ");
				return;
			}
		}
	}

	private void  processarAih(FatRetornaAih rConta,  FatAih rAih) {
		
		String conta = rAih.getContaHospitalar() != null ? rAih.getContaHospitalar().getSeq().toString() : "" ;
		getLogger().info("aih existe " + conta + "null");
		
		// é da mesma conta
		if(rAih.getContaHospitalar() != null && rAih.getContaHospitalar().getSeq().equals(rConta.getId().getCthSeq())) {
			getLogger().info("aih existe e é da mesma conta " + rAih.getNroAih());
			
			try {
				atualizarFatAih(rConta);
				vAtualiza =  Boolean.TRUE;
			} catch (ApplicationBusinessException e) {
				getLogger().info("AIH " + rConta.getId().getNroAih() + " " + e.getMessage());
				vAtualiza =  Boolean.FALSE;
				try {
					persistirFatLogError(rConta, "NÃO CONSEGUIU ATUALIZAR AUDITOR EM CONTA QUE JA POSSUIA A AIH INFORMADA");
				} catch (Exception ex) {
					return;
				}
			}
		} else {
			 processsarAihNaoPertenceConta(rConta, rAih);
		}
	}

	private void processsarAihNaoPertenceConta(FatRetornaAih rConta, FatAih rAih) {

		getLogger().info("verifica se é AIH5 " + rAih.getContaHospitalar() != null ? rAih.getContaHospitalar().getSeq() : "" );
		Boolean vTipoAih5 = obterVTipoAih5(rConta.getId().getCodSusAut());
		
		// IF NVL(v_tipo_aih5,'N') = 'N' THEN
		if(Boolean.FALSE.equals(vTipoAih5 ==  null ? Boolean.FALSE : vTipoAih5 )) {
			getLogger().info("aih existe em outra conta " + rAih.getContaHospitalar() != null ? rAih.getContaHospitalar().getSeq() : "" );
			vAtualiza =  Boolean.FALSE;
			try {
				persistirFatLogError(rConta, "AIH INFORMADA PERTENCE A OUTRA CONTA");
			} catch (Exception ex) {
				return;
			}
		} else {
			// é aih5
			vAtualiza =  Boolean.TRUE;
		}
	}

	private void processarAtualizacao(String nomeMicrocomputador,
			FatRetornaAih rConta,  Boolean vOk,
			FatContasHospitalares rContasHcpa) throws ApplicationBusinessException {
		
		if(vAtualiza) {
			
			FatContasHospitalares fch =  fatContasHospitalaresDAO.obterPorChavePrimaria(rConta.getId().getCthSeq());
			fch.setCpfMedicoAuditor(rConta.getId().getNroCpfAuditor());
			fch.setCnsMedicoAuditor(rConta.getId().getNroCnsAuditor()); 
			fch.setIndAutorizadoSms(IND_AUTORIZADO_SMS_S);
			fch.setCodSusAut(rConta.getId().getCodSusAut());
			fch.setAlteradoEm(new Date());
			
			FatAih fatAih = fatAihDAO.obterPorChavePrimaria(rConta.getId().getNroAih());
			fch.setAih(fatAih);
			
			try {
				atualizarFatContaHospitalar(fch, rConta.getId().getCthSeq(), nomeMicrocomputador);
			} catch (ApplicationBusinessException e) {
				getLogger().info("Erro 1 vai atualizar conta ");
				vAtualiza = Boolean.FALSE;
				try {
					persistirFatLogError(rConta, "AIH INFORMADA PERTENCE A OUTRA CONTA");
				} catch (Exception ex) {
					return;
				}
			}	

			/*Fim não atualiza espelho*/
			if(!CoreUtil.in(rContasHcpa.getIndSituacao(), DominioSituacaoConta.A, DominioSituacaoConta.F)) {
				
				Boolean pPrevia = Boolean.FALSE;
				
				try {
					vOk = fatkCthRN.rnCthcAtuReabre(
							rConta.getId().getCthSeq(), nomeMicrocomputador, servidorLogadoFacade.obterServidorLogado().getDtFimVinculo(), pPrevia);
				} catch (BaseException e) {
					throw new ApplicationBusinessException(e);
				}
				
				if(!vOk){
					vAtualiza = Boolean.FALSE;
					try {
						persistirFatLogError(rConta, "NÃO FOI POSSIVEL REABRIR A CONTA");
					} catch (Exception ex) {
						getLogger().info("deu erro, vai cair no goto SAIDA");
						return;
					}
				}
			} // -- não é conta encerrada
		
			// atualiza aih na conta filha se for aih5
			atualizarAihContaFilhaAih5(nomeMicrocomputador, rConta, fch, obterVTipoAih5(rConta.getId().getCodSusAut())); 
			// Fim atualiza aih na conta filha
			
		} // if v_atualiza
	}
	
	private void gerarLogParaContaEncerradasNaoEnviadaSMS(FatRetornaAih rConta, FatContasHospitalares rContasHcpa) 
			throws ApplicationBusinessException{
		
		if(IND_ENVIADO_SMS.equals(CoreUtil.nvl(rContasHcpa.getIndEnviadoSms(), IND_ENVIADO_SMS))
				&&  DominioSituacaoConta.E.equals(rContasHcpa.getIndSituacao())) {  // 161104
			
			vAtualiza =  Boolean.FALSE;
			persistirFatLogError(rConta, "CONTA NÃO ENVIADA À SMS ");
			return;
			
		} else if (rContasHcpa.getAih() != null) {
			
			if(!CoreUtil.modificados(rConta.getId().getNroAih(), rContasHcpa.getAih())){
				vAtualiza =  Boolean.FALSE;
				persistirFatLogError(rConta, "CONTA POSSUI AIH DIFERENTE DA AUTORIZADA  ");
				return;
			}
		}
	}

	private void gravarCpfAutorizadorContaEspelho(FatRetornaAih rConta, String nomeMicrocomputador) throws ApplicationBusinessException {
		
		if((rConta.getId().getNroCpfAuditor() != null) || rConta.getId().getNroCnsAuditor() != null) {
			
			List<FatEspelhoAih> ListafatEspelhoAih =  fatEspelhoAihDAO.obterPorCthSeq(rConta.getId().getCthSeq());
			
			for (FatEspelhoAih fatEspelhoAih : ListafatEspelhoAih) {
				Long cpf = rConta.getId().getNroCpfAuditor();
				Long cns = rConta.getId().getNroCnsAuditor();
				
				fatEspelhoAih.setCpfMedicoAuditor(null);
				if(cpf != null && cpf != 0){
					fatEspelhoAih.setCpfMedicoAuditor(cpf);
				}
				fatEspelhoAih.setCnsMedicoAuditor(cns);

				atualizarFatEspelhoAih(fatEspelhoAih, nomeMicrocomputador);
				
				getLogger().info("'r_contas.nro_cns_auditor: " + rConta.getId().getNroCnsAuditor());
				
				try {
					FatContasHospitalares fch =  fatContasHospitalaresDAO.obterPorChavePrimaria(rConta.getId().getCthSeq());
					fch.setCpfMedicoAuditor(cpf);
					fch.setCnsMedicoAuditor(cns); 
					
					atualizarFatContaHospitalar(fch, rConta.getId().getCthSeq(), nomeMicrocomputador);
				} catch (ApplicationBusinessException e) {
					return;
				}
			}
			
		} // Fim Marina
	}

	private void atualizarAihContaFilhaAih5(String nomeMicrocomputador, FatRetornaAih rConta, FatContasHospitalares fch, Boolean vTipoAih5) 
			throws ApplicationBusinessException {
		
		if(Boolean.TRUE.equals(vTipoAih5 ==  null ? Boolean.FALSE : vTipoAih5 )) {
			
			for (FatContasHospitalares rContasFilha : obterCursorContaFilha(rConta.getId().getCthSeq())) {
				getLogger().info("filha " + rConta.getId().getCthSeq());

				try {
					FatContasHospitalares contasHospitalares =  fatContasHospitalaresDAO.obterPorChavePrimaria(rContasFilha.getContaHospitalar().getSeq());
					fch.setCpfMedicoAuditor(rConta.getId().getNroCpfAuditor());
					fch.setCnsMedicoAuditor(rConta.getId().getNroCnsAuditor());
					
					FatAih aih = fatAihDAO.obterPorChavePrimaria(rConta.getId().getNroAih());
					fch.setAih(aih);
					
					atualizarFatContaHospitalar(contasHospitalares, rConta.getId().getCthSeq(), nomeMicrocomputador);
				} catch (ApplicationBusinessException e) {
					continue; // GOTO SAIDA
				}
				
				atualizarEspelhoContasFilhaEncerrada(nomeMicrocomputador, rConta, rContasFilha);
			}
		}
	}

	private void procesarRetornoAihContaTipoLaudo(FatRetornaAih rConta) throws ApplicationBusinessException {
		
		if(rConta.getId().getCodSusCma1() != null) {
			
			getLogger().info("Insert");

			FatAutorizadoCmaId id =  new FatAutorizadoCmaId();
			id.setCthSeq(rConta.getId().getCthSeq());
			id.setCodSusCma(rConta.getId().getCodSusCma1());
			id.setCompetencia(rConta.getId().getCodSusCma3());
			
			FatAutorizadoCma fatAutorizadoCma =  new FatAutorizadoCma();
			fatAutorizadoCma.setQtdProc(rConta.getId().getCodSusCma2() != null ? rConta.getId().getCodSusCma2().shortValue() : null);
			fatAutorizadoCma.setId(id);

			fatAutorizadoCmaRN.persistir(fatAutorizadoCma);
			// Incluido ETB 14/05/2008
		}
	}

	private void atualizarEspelhoContasFilhaEncerrada(String nomeMicrocomputador, FatRetornaAih rConta,
			FatContasHospitalares rContasFilha) throws ApplicationBusinessException {
		
		// atualiza espelho da conta filha se estiver encerrada
		if(DominioSituacaoConta.E.equals(rContasFilha.getIndSituacao())){
			
			FatEspelhoAihId id = new FatEspelhoAihId();
			id.setCthSeq(rContasFilha.getContaHospitalar().getSeq());
			FatEspelhoAih fatEspelhoAih =  fatEspelhoAihDAO.obterPorChavePrimaria(id);
			fatEspelhoAih.setCpfMedicoAuditor(rConta.getId().getNroCpfAuditor());
			fatEspelhoAih.setCnsMedicoAuditor(rConta.getId().getNroCnsAuditor());
			
			atualizarFatEspelhoAih(fatEspelhoAih, nomeMicrocomputador);
		}
	}

	private void verificarexclusaoPorCthModuloDescricaoErro(FatRetornaAih rConta) throws ApplicationBusinessException {
		if (vAtualiza) {
			getLogger().info("vai deletar fat_log " + rConta.getId().getCthSeq());
			try {
				// criar param
				fatLogErrorDAO.removerPorCthModuloDescricaoErro(rConta.getId().getCthSeq(), MODULO_AIH, "NUMERO DE AIH NAO FOI INFORMADO."); 
			} catch (Exception e) {
				persistirFatLogError(rConta, "NÃO foi excluiu mensagem de AIH nao informada " + rConta.getId().getNroAih());
			}
		} // if v_atualiza	
	}
	
	private List<FatContasHospitalares> obterCursorContaFilha(Integer conta){
		// 24112004 não atualizar filhas aih1
		// tah_seq = 5 criar parametro
		Byte tahSeq = 5;
		return fatContasHospitalaresDAO.obterCursorContaFilha(conta, tahSeq);
	}
	
	private Boolean obterVTipoAih5(Long codTabela){
		// criar paramentro pho_seq = 12
		Short phoSeq = 12;
		List<FatItensProcedHospitalar> list = fatItensProcedHospitalarDAO.obterCursorAih5(phoSeq, codTabela, DominioSituacao.A);
		return !list.isEmpty() ? list.get(0).getTipoAih5() : null ;
	}
	
	private void atualizarFatAih(FatRetornaAih rContas) throws ApplicationBusinessException{
		
		FatAih oldFatAih =  fatAihDAO.obterOriginal(rContas.getId().getNroAih());
		FatAih fatAih =  fatAihDAO.obterPorChavePrimaria(rContas.getId().getNroAih());
		fatAih.setNroAih(rContas.getId().getNroAih());
		fatAih.setDthrEmissao(rContas.getId().getDtEmissaoAih());
		fatAih.setIndSituacao(DominioSituacaoAih.A);
		
		FatContasHospitalares fatContasHospitalares = fatContasHospitalaresDAO.obterPorChavePrimaria(rContas.getId().getCthSeq());
		fatAih.setContaHospitalar(fatContasHospitalares);
		
		fatAih.setSerMatricula(servidorLogadoFacade.obterServidorLogado().getId().getMatricula());
		fatAih.setSerVinCodigo(servidorLogadoFacade.obterServidorLogado().getId().getVinCodigo());
		
		faturamentoRN.atualizarFatAih(fatAih, oldFatAih);
	}

	private void inserirFatAih(FatRetornaAih rContas) throws ApplicationBusinessException {
		
		FatAih fatAih = new FatAih();
		fatAih.setNroAih(rContas.getId().getNroAih());
		fatAih.setDthrEmissao(rContas.getId().getDtEmissaoAih());
		fatAih.setIndSituacao(DominioSituacaoAih.A);
		
		FatContasHospitalares fatContasHospitalares = fatContasHospitalaresDAO.obterPorChavePrimaria(rContas.getId().getCthSeq());
		fatAih.setContaHospitalar(fatContasHospitalares);
		// usando usuario logado e não o c_servidor
		fatAih.setSerMatricula(servidorLogadoFacade.obterServidorLogado().getId().getMatricula());
		fatAih.setSerVinCodigo(servidorLogadoFacade.obterServidorLogado().getId().getVinCodigo());
		fatAih.setServidor(servidorLogadoFacade.obterServidorLogado());
		faturamentoRN.inserirFatAih(fatAih, false);
	}
	
	private FatAih obterCursorAih(Long vAih) {
		return fatAihDAO.obterPorChavePrimaria(vAih);
	}

	private FatEspelhoAih obterCursorEspelhosAih(FatRetornaAih rContas){
		return fatEspelhoAihDAO.obterPorChavePrimaria(new FatEspelhoAihId(rContas.getId().getCthSeq(), 1));
	}

	private void persistirFatLogError(FatRetornaAih rContas, String msgErro) throws ApplicationBusinessException {
		FatLogError fatLogError = obterFatLogError(rContas.getId().getCthSeq());
		fatLogError.setErro(msgErro);
		
		fatLogErrorRN.persistir(fatLogError);
	}
	
	private FatLogError obterFatLogError(Integer vCthSeq) {
		FatLogError fatLogError =  new FatLogError();
		fatLogError.setModulo(MODULO_AIH);
		fatLogError.setPrograma(FATP_ATUALIZA_AIH);
		fatLogError.setCthSeq(vCthSeq);
		return fatLogError;
	}
	
	private FatContasHospitalares obterCursorContaHCPA(Integer conta){
		// ind_situacao = 'E' -- alterada em 161104 p/ autorizar qquer conta
		return fatContasHospitalaresDAO.obterCursorContaHCPA(conta, IND_AUTORIZADO_SMS_N);
	}
	
	private void atualizarFatContaHospitalar(FatContasHospitalares fatContasHospitalare, Integer seq,
			String nomeMicrocomputador) throws ApplicationBusinessException {
		FatContasHospitalares fctOriginal = fatContasHospitalaresDAO.obterOriginal(seq);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		try {
			contaHospitalarON.persistirContaHospitalar(
					fatContasHospitalare, fctOriginal, false, nomeMicrocomputador, servidorLogado, servidorLogadoFacade.obterServidorLogado().getDtFimVinculo());
		} catch (BaseException e) {
			throw new ApplicationBusinessException(e);
		}
	}

	private void atualizarFatEspelhoAih(FatEspelhoAih fatEspelhoAih, String nomeMicrocomputador) throws ApplicationBusinessException {
		try {
			espelhoAihPersistRN.atualizar(fatEspelhoAih, nomeMicrocomputador, servidorLogadoFacade.obterServidorLogado().getDtFimVinculo());
		} catch (BaseException e) {
			throw new ApplicationBusinessException(e);
		}
	}

	public Boolean getvAtualiza() {
		return vAtualiza;
	}

	public void setvAtualiza(Boolean vAtualiza) {
		this.vAtualiza = vAtualiza;
	}

	
}
