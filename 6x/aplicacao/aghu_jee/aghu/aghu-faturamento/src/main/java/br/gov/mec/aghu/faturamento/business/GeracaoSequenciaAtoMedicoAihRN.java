package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatExcecaoPercentualDAO;
import br.gov.mec.aghu.faturamento.vo.CursorAtosMedicosVO;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatAtoMedicoAihId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatExcecaoPercentual;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimentoId;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;


/**
 * <p>
 * ORADB: <code>FATP_GERA_SEQUENCIA_ATOS</code>
 * </p>
 * @author eschweigert
 *
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class GeracaoSequenciaAtoMedicoAihRN extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(GeracaoSequenciaAtoMedicoAihRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private FaturamentoInternacaoRN faturamentoInternacaoRN;
	
	@Inject
	private FatExcecaoPercentualDAO fatExcecaoPercentualDAO;
	
	@Inject
	private FatAtoMedicoAihDAO fatAtoMedicoAihDAO;

	@EJB
	private AtoMedicoAihPersist atoMedicoAihPersist;

	private static final long serialVersionUID = 1481287001224763928L;

	protected FatExcecaoPercentual obterFatExcecoesPercentual(final byte sequencia, final Integer cthSeq, final Integer seqp) {
		return this.getFatExcecaoPercentualDAO().buscarExcecoesPercentuais(sequencia, cthSeq, seqp);		
	}

	protected BigDecimal obterFator(final byte sequencia, final Integer cthSeq) {
		
		BigDecimal retorno = BigDecimal.ZERO;
		
		final FatExcecaoPercentual excecao = this.obterFatExcecoesPercentual(sequencia, cthSeq, 1);	
		
		if (excecao != null) {
			retorno = BigDecimal.valueOf(excecao.getPercentual().longValue()).divide(BigDecimal.valueOf(100l));
		}
	
		return retorno;
	}
	
	
	/**
	 * <p>
	 * ORADB: <code>FATP_GERA_SEQUENCIA_ATOS</code>
	 * ORADB: <code>FATC_BUSCA_PROCED_PR_CTA</code>
	 * ORADB: <code>FATC_BUSCA_INSTR_REG</code>
	 * </p>
	 * @param cthSeq
	 * @throws BaseException 
	 * @throws IllegalStateException 
	 */
	public String gerarSequenciaAtoMedicaAihPorCthSeq(final Integer cthSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		// Marina 13/06/2012
		BigDecimal vValorProcedimentoCta = BigDecimal.ZERO;
		BigDecimal vValorSadtCta = BigDecimal.ZERO;
		BigDecimal vValorServHospCta = BigDecimal.ZERO;
		BigDecimal vValorServProfCta = BigDecimal.ZERO;
		Byte cspSeq = null;
		Short cspCnvCodigo = null;
		String vRealizado = null;
		
		final String codRegistro = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_AGHU_REG_AIH_PROC_PRINC);
		
//		Marina 25/10/2014
		Boolean pSeqOnco = this.cSeqOnco(cthSeq, nomeMicrocomputador, dataFimVinculoServidor);

		final List<CursorAtosMedicosVO> list =  getFatAtoMedicoAihDAO().obterCursorAtosMedicosVOs( cthSeq, 
																								   CursorAtosMedicosVO.IP_PHO_SEQ_6, 
																								   CursorAtosMedicosVO.IND_SITUACAO,
																								   codRegistro,
																								   pSeqOnco
																								  );
		logar("v_seq_onco: ", pSeqOnco);
		
		if(list != null && !list.isEmpty()){
			int vSeq = 0;
			int vSeqPrinc = 0;
			BigDecimal vFator = null;
			
			vFator = this.obterFator((byte) 1, cthSeq);
			
			for (CursorAtosMedicosVO vo : list) {
				
				final FatAtoMedicoAih ama = obterFatAtoMedicoAih(vo);
				
				BigDecimal vValorServHosp 	  = BigDecimal.ZERO;
				BigDecimal vValorServProf 	  = BigDecimal.ZERO;
				BigDecimal vValorSadt 		  = BigDecimal.ZERO;
				BigDecimal vValorProcedimento = BigDecimal.ZERO;
				
				BigDecimal valorServHospAux 	  = BigDecimal.ZERO;
				BigDecimal valorServProfAux 	  = BigDecimal.ZERO;
				BigDecimal valorSadtAux 		  = BigDecimal.ZERO;
				BigDecimal valorProcedimentoAux = BigDecimal.ZERO;
				
				vSeq++;
				ama.setSeqArqSus(Short.valueOf((short)vSeq));
				
				if( (CursorAtosMedicosVO.Registro.PRINCIPAL1.getDescricao().equalsIgnoreCase(vo.getRegistro()) ) &&
						CoreUtil.igual(Integer.valueOf(4), vo.getTivSeq()) && CoreUtil.igual(Integer.valueOf(1), vo.getTaoSeq())){
					vSeqPrinc++;
											
					if (vFator.compareTo(BigDecimal.ZERO) > 0) {
						
						if (vSeqPrinc > 1) {
							vFator = this.obterFator((byte) vSeqPrinc, cthSeq);
						}
						
						if(ama.getValorServHosp() != null){
							valorServHospAux = ama.getValorServHosp();
							vValorServHosp = ama.getValorServHosp().multiply(vFator);
							ama.setValorServHosp(vValorServHosp);
						}
						
						if(ama.getValorServProf() != null){
							valorServProfAux = ama.getValorServProf();
							vValorServProf = ama.getValorServProf().multiply(vFator);
							ama.setValorServProf(vValorServProf);
						}
						
						if(ama.getValorSadt() != null){
							valorSadtAux = ama.getValorSadt();
							vValorSadt = ama.getValorSadt().multiply(vFator);
							ama.setValorSadt(vValorSadt);
						}
						
						if(ama.getValorProcedimento() != null){
							valorProcedimentoAux = ama.getValorProcedimento();
							vValorProcedimento = ama.getValorProcedimento().multiply(vFator);
							ama.setValorProcedimento(vValorProcedimento);
						}
						
						this.getAtoMedicoAihPersist().atualizar(ama, nomeMicrocomputador, dataFimVinculoServidor);
						
					} else {
						this.getAtoMedicoAihPersist().atualizar(ama, nomeMicrocomputador, dataFimVinculoServidor);
					}
					
					// Marina 13/06/2012
					vValorProcedimentoCta = vValorProcedimentoCta.add(((BigDecimal) nvl(valorProcedimentoAux, BigDecimal.ZERO) ).subtract(vValorProcedimento) );
					vValorSadtCta 		  = vValorSadtCta.add(((BigDecimal) nvl(valorSadtAux, BigDecimal.ZERO) ).subtract(vValorSadt) );
					vValorServHospCta     = vValorServHospCta.add(((BigDecimal) nvl(valorServHospAux, BigDecimal.ZERO) ).subtract(vValorServHosp) );
					vValorServProfCta 	  = vValorServProfCta.add(((BigDecimal) nvl(valorServProfAux, BigDecimal.ZERO) ).subtract(vValorServProf) );
					// Marina 16/04/2013
					vRealizado = StringUtils.substring(faturamentoInternacaoRN.buscaSSM(cthSeq, cspCnvCodigo,cspSeq, DominioSituacaoSSM.R), 0, 9);
					
				} else {
					this.getAtoMedicoAihPersist().atualizar(ama, nomeMicrocomputador, dataFimVinculoServidor);
				}

				cspSeq = vo.getCspSeq();
				cspCnvCodigo = vo.getCspCnvCodigo();
			}
		
			logar("V_REALIZADO_CONTA: "+(vRealizado == null ? '0' : vRealizado));
			
			// Marina 13/06/2012 -- atualiza a conta após geração da nova sequencia- chamado 72340
			fatpAtualizaCta(cthSeq, vValorProcedimentoCta, vValorSadtCta, vValorServHospCta, vValorServProfCta, nomeMicrocomputador, dataFimVinculoServidor);
		}
		
		return vRealizado;
	}
	
	/**
	 * ORADB: FATP_ATUALIZA_CTA
	 * 
	 * Marina 13/06/2012
	 * @throws BaseException 
	 */
	private void fatpAtualizaCta(final Integer cthSeq,
								 final BigDecimal pValorProcedimentoCta, final BigDecimal pValorSadtCta, 
							     final BigDecimal pValorServHospCta, final BigDecimal pValorServProfCta, 
							     String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		
		logar("********* AJUSTA VALORES NA CONTA **************");
		logar("p_VALOR_PROCEDIMENTO_CTA: {0}", pValorProcedimentoCta);
		logar("p_VALOR_SADT_CTA: {0}", pValorSadtCta);
		logar("p_VALOR_SERV_HOSP_CTA: {0}", pValorServHospCta);
		logar("p_VALOR_SERV_PROF_CTA: {0}", pValorServProfCta);
		
		// cursor c_busca_valores_cta is
		final FatContasHospitalares cth = getFatContasHospitalaresDAO().obterPorChavePrimaria(cthSeq);
		BigDecimal vSh   = (BigDecimal) CoreUtil.nvl(cth.getValorSh(),   BigDecimal.ZERO);
		BigDecimal vSp   = (BigDecimal) CoreUtil.nvl(cth.getValorSp(),   BigDecimal.ZERO);
		BigDecimal vSadt = (BigDecimal) CoreUtil.nvl(cth.getValorSadt(), BigDecimal.ZERO);
		BigDecimal vProc = (BigDecimal) CoreUtil.nvl(cth.getValorProcedimento(), BigDecimal.ZERO);
		

		logar("v_sh: {0}", vSh);
		logar("v_sp: {0}", vSp);
		logar("v_sadt: {0}", vSadt);
		logar("v_proc: {0}", vProc);

		if(!CoreUtil.igual(vSh, BigDecimal.ZERO)){
			vSh = vSh.subtract( (BigDecimal) CoreUtil.nvl(pValorServHospCta, BigDecimal.ZERO));
		}
		
		if(!CoreUtil.igual(vSp, BigDecimal.ZERO)){
			vSp = vSp.subtract( (BigDecimal) CoreUtil.nvl(pValorServProfCta, BigDecimal.ZERO));
		}
		
		if(!CoreUtil.igual(vSadt, BigDecimal.ZERO)){
			vSadt = vSadt.subtract( (BigDecimal) CoreUtil.nvl(pValorSadtCta, BigDecimal.ZERO));
		}
		
		if(!CoreUtil.igual(vProc, BigDecimal.ZERO)){
			vProc = vProc.subtract( (BigDecimal) CoreUtil.nvl(pValorProcedimentoCta, BigDecimal.ZERO));
		}
		
		logar("**** DIFERENÇA");
		logar("v_sh: {0}", vSh);
		logar("v_sp: {0}", vSp);
		logar("v_sadt: {0}", vSadt);
		logar("v_proc: {0}", vProc);
		
		cth.setValorSh(vSh);
		cth.setValorSp(vSp);
		cth.setValorSadt(vSadt);
		
		final FatContasHospitalares oldCTH = getFatContasHospitalaresDAO().obterOriginal(cthSeq);
		getContaHospitalarON().atualizarContaHospitalar(cth, oldCTH, false, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	public Boolean cSeqOnco(Integer cthSeq, String nomeMicrocomputador, Date dtFimVincServ)
			throws IllegalStateException, BaseException {
		
		VFatAssociacaoProcedimentoId vFat = getFatContasHospitalaresDAO().obterIphPhoPorPhiRealizado(cthSeq, Short.valueOf((short) 12));
		
		Boolean vSeqOnco = this.getFaturamentoRN().verificarCaracteristicaExame(
						vFat.getIphSeq(),
						vFat.getIphPhoSeq(),
						DominioFatTipoCaractItem.GERA_SEQUENCIA_POR_COMPATIBILIDADE_DO_PRINCIPAL);
		return vSeqOnco;
	}
	
	/**
	 * Obtem os demais campos da tabela FatAtoMedicoAih necessários para o processamento
	 */
	private FatAtoMedicoAih obterFatAtoMedicoAih(final CursorAtosMedicosVO vo){
		return getFatAtoMedicoAihDAO().obterPorChavePrimaria(new FatAtoMedicoAihId(vo.getEaiSeq(), vo.getEaiCthSeq(), vo.getSeqp()));
	}
	
	protected FatExcecaoPercentualDAO getFatExcecaoPercentualDAO() {
		return fatExcecaoPercentualDAO;
	}
	
	protected FatAtoMedicoAihDAO getFatAtoMedicoAihDAO() {
		return fatAtoMedicoAihDAO;
	}
	
	protected AtoMedicoAihPersist getAtoMedicoAihPersist() {
		return atoMedicoAihPersist;
	}
	
}