package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioTipoNutricaoParenteral;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.vo.ItemAlteracaoNptVO;
import br.gov.mec.aghu.faturamento.vo.RnCthcBuscaContaVO;
import br.gov.mec.aghu.faturamento.vo.TipoPrescricaoFaturamentoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * @author lalegre
 *
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class FatkInterfacePrescricaoRN extends AbstractFatDebugLogEnableRN {


private static final String PNP = "PNP";

@EJB
private ItemContaHospitalarON itemContaHospitalarON;

	private static final Log LOG = LogFactory.getLog(FatkInterfacePrescricaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private FatkCth4RN fatkCth4RN;
	
	@EJB		
	private ItemContaHospitalarRN itemContaHospitalarRN;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2711923318591704214L;
	
	/**
	 * ORADB FATK_INTERFACE_MPM.RN_FATP_ATU_INTERFACE
	 * 
	 * TODO: Essa package nao foi completamente implementada!
	 * Inclusive na versao original a assinatura do metodo nao possuia todos os parametros necessarios, vide javadoc 
	 * 
	 * @param atdSeq
	 * @param prescricaoMedicaSeq
	 * @param dataInicio
	 * @param dataFim
	 * @param operacao
	 * @throws BaseException 
	 */
	public void atualizaContaHospitalar (Integer atdSeq, Integer prescricaoMedicaSeq, Date dataInicio, Date dataFim, Date dataInicioMovimentoPendente, DominioOperacaoBanco operacao, final Date dataFimVinculoServidor) throws BaseException {
		
		AghAtendimentos atendimento = getAghuFacade().obterAtendimentoPeloSeq(atdSeq);
		
		if (atendimento != null && atendimento.getInternacao() != null && 
				atendimento.getInternacao().getConvenioSaude().getGrupoConvenio().equals(DominioGrupoConvenio.S)) {
			
			Date dataMovimento = new Date();
			RnCthcBuscaContaVO rnCthcBuscaContaVO = new RnCthcBuscaContaVO();
			Integer pacCodigo = atendimento.getPaciente().getCodigo();
			
			if (dataInicio.before(dataMovimento)) {
				
				dataMovimento = dataInicio;
				
			}
			
			rnCthcBuscaContaVO = getFatkCth4RN().rnCthcBuscaConta(pacCodigo, dataMovimento, null);
			
			if (!rnCthcBuscaContaVO.getRetorno()) {
				
				logDebug("Nao encontrou conta. p_atd_seq = " + atdSeq.toString() + " v_pac_codigo = " + pacCodigo.toString() + " p_pme_dthr_ini = " + dataMovimento.toString());
				
			} else {
				
				if (rnCthcBuscaContaVO.getTipoMensagem() != null) {
					
					logDebug("Conta encerrada ou apresentada cth = " + rnCthcBuscaContaVO.getCthSeq().toString());
					
				}
				
				if (operacao.equals(DominioOperacaoBanco.INS)) {
					
					inserirConta(rnCthcBuscaContaVO, atdSeq, prescricaoMedicaSeq, dataInicio, dataFim, dataFimVinculoServidor);
					
				} else {
					//TODO: FATK_INTERFACE_MPM.RN_FATP_ATU_INTERFACE procedure P_ALTERACAO
					alterarConta(rnCthcBuscaContaVO, atdSeq, prescricaoMedicaSeq, dataInicio, dataFim , dataInicioMovimentoPendente, dataFimVinculoServidor);
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * ORADB PROCEDURE P_INCLUSAO
	 * @param atdSeq
	 * @param prescricaoMedicaSeq
	 * @param dataInicio
	 * @param dataFim
	 * @throws BaseException 
	 */
	private void inserirConta(RnCthcBuscaContaVO rnCthcBuscaContaVO, Integer atdSeq, Integer prescricaoMedicaSeq, Date dataInicio, Date dataFim, final Date dataFimVinculoServidor) throws BaseException {
		
		List<TipoPrescricaoFaturamentoVO> itens = new ArrayList<TipoPrescricaoFaturamentoVO>();
		List<MpmPrescricaoNpt> itensNpt = getPrescricaoMedicaFacade().obterItensPrescricaoNpt(atdSeq, prescricaoMedicaSeq, dataInicio, dataFim);
		List<MpmPrescricaoProcedimento> itensProcedimentos = getPrescricaoMedicaFacade().obterItensPrescricaoProcedimento(atdSeq, prescricaoMedicaSeq, dataInicio, dataFim);
		Integer phi;
		
		popularVO(itens, itensNpt, itensProcedimentos);
		
		for (TipoPrescricaoFaturamentoVO vo : itens) {
			
			phi = obterPHI(vo, rnCthcBuscaContaVO);
			incluirICH(vo, rnCthcBuscaContaVO, phi, dataFimVinculoServidor);
			
		}
		
	}
	
	/**
	 * ORADB PROCEDURE FATK_INTERFACE_MPM.RN_FATP_ATU_INTERFACE.P_ALTERACAO
	 * 
	 * @param rnCthcBuscaContaVO
	 * @param atdSeq
	 * @param prescricaoMedicaSeq
	 * @param dataInicio
	 * @param dataFim
	 * @throws BaseException 
	 */
	private void alterarConta(RnCthcBuscaContaVO rnCthcBuscaContaVO, Integer atdSeq, Integer prescricaoMedicaSeq, Date dataInicio, Date dataFim, Date dataInicioMovimentoPendente, final Date dataFimVinculoServidor) throws BaseException {
		//Nutricao Parenteral
		logar(" vai procurar alteracoes npt ");
		List<ItemAlteracaoNptVO> regNptsList = this.getPrescricaoMedicaFacade().pesquisarAlteracoesNpt(atdSeq, prescricaoMedicaSeq, dataInicioMovimentoPendente);
		
		TipoPrescricaoFaturamentoVO vo = null;
		Integer phi = null;
		
		if(!regNptsList.isEmpty()) {
			logar(" encontrou alteracoes npt ");
		
			for (ItemAlteracaoNptVO regNpts : regNptsList) {
				TipoOperacaoEnum operacao = this.getFarmaciaFacade().getMpmcOperMvto(
						regNpts.getAlteradoEm(), regNpts.getPnpAtdSeq(), (regNpts.getPnpSeq() != null ? regNpts.getPnpSeq().longValue() : null));
							
				vo = new TipoPrescricaoFaturamentoVO();
				vo.setAtdSeq(regNpts.getAtdSeq());
				vo.setDataInicio(regNpts.getDtHrInicio());
				
				vo.setTipo(PNP);
				
				if(operacao == TipoOperacaoEnum.INSERT) {
					vo.setPacCodigo(regNpts.getPacCodigo());
					vo.setPedSeq((Short) CoreUtil.nvl(regNpts.getPedSeq(), 0));
					vo.setPrescricaoSeq(regNpts.getSeq());
					vo.setServidorValida(this.getRegistroColaboradorFacade().buscaServidor(
							new RapServidoresId(regNpts.getMatricula(), regNpts.getVinCodigo())));
					
					phi = obterPHI(vo, rnCthcBuscaContaVO);
					
					vo.setMatCodigo(regNpts.getMatCodigo());
					vo.setPciSeq(regNpts.getPciSeq());
					
					incluirICH(vo, rnCthcBuscaContaVO, phi, dataFimVinculoServidor);
				}
				
				if(operacao == TipoOperacaoEnum.DELETE) {
					vo.setPnpSeq(regNpts.getSeq());
					vo.setPprSeq(regNpts.getPprSeq());
					cancelarICH(vo, dataFimVinculoServidor);
				}
			}
		}
		
		//procedimentos especiais diversos
		logar(" vai procurar alteracoes proc ");
		List<ItemAlteracaoNptVO> regProcList = this.getPrescricaoMedicaFacade().pesquisarProcedimentosEspeciaisDiversos(atdSeq, prescricaoMedicaSeq, dataInicioMovimentoPendente);
		
		if(!regProcList.isEmpty()) {
			logar(" encontrou alteracoes proc ");
			
			for (ItemAlteracaoNptVO regProc : regProcList) {
				TipoOperacaoEnum operacao = this.getFarmaciaFacade().getMpmcOperMvto(
						regProc.getAlteradoEm(), regProc.getPprAtdSeq(), regProc.getPprPprSeq());
							
				vo = new TipoPrescricaoFaturamentoVO();
				vo.setAtdSeq(regProc.getAtdSeq());
				vo.setDataInicio(regProc.getDtHrInicio());
				
				vo.setTipo("PPR");
				
				if(operacao == TipoOperacaoEnum.INSERT) {
					vo.setPacCodigo(regProc.getPacCodigo());
					vo.setPedSeq((Short) CoreUtil.nvl(regProc.getPedSeq(), 0));
					vo.setPrescricaoSeq(regProc.getPprSeq().intValue());
					vo.setServidorValida(this.getRegistroColaboradorFacade().buscaServidor(
							new RapServidoresId(regProc.getMatricula(), regProc.getVinCodigo())));
					
					vo.setMatCodigo((Integer) CoreUtil.nvl(regProc.getMatCodigo(), 0));
					vo.setPciSeq((Integer) CoreUtil.nvl(regProc.getPciSeq(), 0));
					
					phi = obterPHI(vo, rnCthcBuscaContaVO);
					
					incluirICH(vo, rnCthcBuscaContaVO, phi, dataFimVinculoServidor);
				}
				
				if(operacao == TipoOperacaoEnum.DELETE) {
					vo.setPnpSeq(regProc.getSeq());
					vo.setPprSeq(regProc.getPprSeq());
					cancelarICH(vo, dataFimVinculoServidor);
				}
			}
		}
		
	}
	
	private void popularVO(List<TipoPrescricaoFaturamentoVO> itens, List<MpmPrescricaoNpt> itensNpt, List<MpmPrescricaoProcedimento> itensProcedimentos) {
		
		TipoPrescricaoFaturamentoVO vo;
		final Short defaultValue = 0;
		
		for (MpmPrescricaoNpt prescricaoNpt : itensNpt) {
			
			vo = new TipoPrescricaoFaturamentoVO();
			vo.setAtdSeq(prescricaoNpt.getPrescricaoMedica().getAtendimento().getSeq());
			vo.setDataFim(prescricaoNpt.getDthrFim());
			vo.setDataInicio(prescricaoNpt.getDthrInicio());
			vo.setMatCodigo(0);
			vo.setPacCodigo(prescricaoNpt.getPrescricaoMedica().getAtendimento().getPaciente().getCodigo());
			vo.setPciSeq(0);
			
			if (prescricaoNpt.getProcedEspecialDiversos() != null) {
				
				vo.setPedSeq(prescricaoNpt.getProcedEspecialDiversos().getSeq());
				
			} else {
				
				vo.setPedSeq(defaultValue);
				
			}
			
			vo.setPrescricaoSeq(prescricaoNpt.getId().getSeq());
			vo.setServidorValida(prescricaoNpt.getServidorValidacao());
			vo.setTipo(PNP);
			
			itens.add(vo);
		
		}
		
		for (MpmPrescricaoProcedimento prescricaoProcedimento : itensProcedimentos) {
			
			vo = new TipoPrescricaoFaturamentoVO();
			vo.setAtdSeq(prescricaoProcedimento.getPrescricaoMedica().getAtendimento().getSeq());
			vo.setDataFim(prescricaoProcedimento.getDthrFim());
			vo.setDataInicio(prescricaoProcedimento.getDthrInicio());
			vo.setPacCodigo(prescricaoProcedimento.getPrescricaoMedica().getAtendimento().getPaciente().getCodigo());
			
			if (prescricaoProcedimento.getProcedimentoEspecialDiverso() != null) {
				
				vo.setPedSeq(prescricaoProcedimento.getProcedimentoEspecialDiverso().getSeq());
				
			} else {
				
				vo.setPedSeq(defaultValue);
				
			}
			
			if (prescricaoProcedimento.getMatCodigo() != null) {
				
				vo.setMatCodigo(prescricaoProcedimento.getMatCodigo().getCodigo());
				
			} else {
				
				vo.setMatCodigo(0);
				
			}
			
			if (prescricaoProcedimento.getProcedimentoCirurgico() != null) {
				
				vo.setPciSeq(prescricaoProcedimento.getProcedimentoCirurgico().getSeq());
				
			} else {
				
				vo.setPciSeq(0);
				
			}
			
			vo.setPrescricaoSeq(prescricaoProcedimento.getId().getSeq().intValue());
			vo.setServidorValida(prescricaoProcedimento.getServidorValidacao());
			vo.setTipo("PPR");
			
			itens.add(vo);
			
		}
		
	}
	
	/**
	 * ORADB FUNCTION  P_BUSCA_PHI
	 * @param vo
	 * @param rnCthcBuscaContaVO
	 * @return
	 */
	private Integer obterPHI(TipoPrescricaoFaturamentoVO vo, RnCthcBuscaContaVO rnCthcBuscaContaVO) {
		
		Integer phi = null;
		FatProcedHospInternos procedHospInternos = null;
		
		if (vo.getTipo().equals(PNP)) {
			
			Integer idade = getItemContaHospitalarRN().fatcBuscaIdadePac(rnCthcBuscaContaVO.getCthSeq());
			DominioTipoNutricaoParenteral tipo;
			
			if (idade == 0) {
				
				tipo = DominioTipoNutricaoParenteral.N;
				
			} else if (idade < 13) {
				
				tipo = DominioTipoNutricaoParenteral.P;
				
			} else {
				
				tipo = DominioTipoNutricaoParenteral.A;
				
			}
			
			procedHospInternos = getFatProcedHospInternosDAO().obterProcedHospInternoPorTipoNutricaoEnteral(tipo);
			
		} else {
			
			if (vo.getPedSeq() > 0) {
				
				procedHospInternos = getFatProcedHospInternosDAO().obterFatProcedHospInternosPorProcedEspecialDiversos(vo.getPedSeq());
				
			} else if (vo.getMatCodigo() > 0) {
				
				procedHospInternos = getFatProcedHospInternosDAO().obterFatProcedHospInternosPorMaterial(vo.getMatCodigo());
				
			} else if (vo.getPciSeq() > 0) {
				
				procedHospInternos = getFatProcedHospInternosDAO().obterFatProcedHospInternosPorProcedimentoCirurgicos(vo.getPciSeq());
				
			}
			
		}
		
		if (procedHospInternos != null) {
			
			if (procedHospInternos.getIndOrigemPresc() == null || (procedHospInternos.getIndOrigemPresc() != null && !procedHospInternos.getIndOrigemPresc())) {
				
				phi = 0;
				
			} else {
				
				phi = procedHospInternos.getSeq();
				
			}
			
		}
		
		return phi;
		
	}
	
	/**
	 * 
	 * @param vo
	 * @param rnCthcBuscaContaVO
	 * @param phi
	 * @throws BaseException 
	 */
	private void incluirICH(TipoPrescricaoFaturamentoVO vo, RnCthcBuscaContaVO rnCthcBuscaContaVO, Integer phi, final Date dataFimVinculoServidor) throws BaseException {
		
		final Short quantidadeRealizada = 1;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		if (phi == null) {
			
			logDebug("Nao encontrou phi " + vo.getAtdSeq().toString() + vo.getPacCodigo().toString());
			
		}
		
		if (phi != 0) {
			
			FatItemContaHospitalarId id = new FatItemContaHospitalarId(rnCthcBuscaContaVO.getCthSeq(), null);
			FatItemContaHospitalar itemContaHospitalar = new FatItemContaHospitalar();
			itemContaHospitalar.setId(id);
			
			itemContaHospitalar.setIchType(DominioItemConsultoriaSumarios.ICH);
			itemContaHospitalar.setProcedimentoHospitalarInterno(getFatProcedHospInternosDAO().obterPorChavePrimaria(phi));
			itemContaHospitalar.setValor(BigDecimal.ZERO);
			itemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.A);
			itemContaHospitalar.setQuantidadeRealizada(quantidadeRealizada);
			itemContaHospitalar.setIndOrigem(DominioIndOrigemItemContaHospitalar.MPM);
			itemContaHospitalar.setLocalCobranca(DominioLocalCobranca.I);
			itemContaHospitalar.setDthrRealizado(vo.getDataInicio());
			itemContaHospitalar.setUnidadesFuncional(null);
			itemContaHospitalar.setServidor(vo.getServidorValida());
			
			if (vo.getTipo().equals(PNP)) {
				
				itemContaHospitalar.setPrescricaoNpt(getPrescricaoMedicaFacade().obterNutricaoParentalTotalPeloId(vo.getAtdSeq(), vo.getPrescricaoSeq()));
				
			} else {
				
				itemContaHospitalar.setPrescricaoProcedimento(getPrescricaoMedicaFacade().obterPrescricaoProcedimentoPorId(vo.getPrescricaoSeq().longValue(), vo.getAtdSeq()));
				
			}
			
			getItemContaHospitalarON().persistirItemContaHospitalar(itemContaHospitalar, null, true, servidorLogado, dataFimVinculoServidor);
			
		}
		
	}
	
	/** 
	 * ORADB FUNCTION p_cancel_ich
	 * 
	 * @param vo
	 * @param rnCthcBuscaContaVO
	 * @param phi
	 * @throws BaseException
	 */
	private void cancelarICH(TipoPrescricaoFaturamentoVO vo, final Date dataFimVinculoServidor) throws BaseException {
		FatItemContaHospitalar itemContaHospitalar = this.getFatItemContaHospitalarDAO()
			.obterItemContaHospitalarPorCancelamentoICH(vo.getAtdSeq(), vo.getPnpSeq(), vo.getPprSeq(), vo.getDataInicio(), vo.getTipo());		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if(itemContaHospitalar != null) {
			itemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.C);
			getItemContaHospitalarON().atualizarItemContaHospitalarSemValidacoesForms(itemContaHospitalar, servidorLogado, null, dataFimVinculoServidor);
		}
	}
	
	private IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}	
	protected FatkCth4RN getFatkCth4RN() {
		return fatkCth4RN;
	}
		
	protected ItemContaHospitalarRN getItemContaHospitalarRN() {
		return itemContaHospitalarRN;
	}
	
	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}
	
	protected ItemContaHospitalarON getItemContaHospitalarON() {
		return itemContaHospitalarON;
	}
	
	protected FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}
}
