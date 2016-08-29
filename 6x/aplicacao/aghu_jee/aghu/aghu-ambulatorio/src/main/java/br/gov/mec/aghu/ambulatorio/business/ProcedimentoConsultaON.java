package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultaProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalarId;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio relacionadas
 * com procedimentos de consulta
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class ProcedimentoConsultaON extends BaseBusiness {


@EJB
private ProcedimentoConsultaRN procedimentoConsultaRN;

private static final Log LOG = LogFactory.getLog(ProcedimentoConsultaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

@EJB
private IFaturamentoFacade faturamentoFacade;

@Inject
private AacConsultaProcedHospitalarDAO aacConsultaProcedHospitalarDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7893287022124978538L;

	public enum ProcedimentoConsultaONExceptionCode implements
			BusinessExceptionCode {

		MSG_QUANTIDADE_PROCEDIMENTO_CONSULTA_INVALIDA, MSG_PROCEDIMENTO_CONSULTA_JA_EXISTENTE, 
		ERRO_CLONE_CONSULTA_PROCED_HOSPITALAR, FAT_01096, FAT_01097;
	}
	
	protected AacConsultaProcedHospitalarDAO getAacConsultaProcedHospitalarDAO() {
		return aacConsultaProcedHospitalarDAO;
	}
	
	protected ProcedimentoConsultaRN getProcedimentoConsultaRN() {
		return procedimentoConsultaRN;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	/**
	 *
	 * Insere um novo procedimento para a consulta
	 * 
	 * @param consultaSelecionada
	 * @param procedimentoConsulta
	 * @param cid
	 * @param procedimento
	 * @param quantidade
	 * 
	 */
	public void adicionarProcedimentoConsulta(AacConsultas consultaSelecionada, AacConsultaProcedHospitalar procedimentoConsulta, 
			AghCid cid, Integer phiSeq, Short espSeq, Byte quantidade, List<AacConsultaProcedHospitalar> listaProcedimentosHospConsulta,
			String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean aack_prh_rn_v_apac_diaria, 
			final Boolean aack_aaa_rn_v_protese_auditiva, 
			final Boolean fatk_cap_rn_v_cap_encerramento) 
			throws BaseException {
		validarQuantidadeProcedimento(quantidade);
		AacConsultaProcedHospitalarId consProcedHospid = new AacConsultaProcedHospitalarId(consultaSelecionada.getNumero(), 
				phiSeq);
		
		FatProcedHospInternos procedHospInterno = getFaturamentoFacade().obterProcedimentoHospitalarInterno(phiSeq);

		if (cid != null) {
			procedimentoConsulta.setCid(cid);	
		}
		procedimentoConsulta.setConsulta(false); // Todos os novos procedimentos adicionados são considerados como "não-padrão"
		procedimentoConsulta.setConsultas(consultaSelecionada);
		procedimentoConsulta.setProcedHospInterno(procedHospInterno);
		procedimentoConsulta.setQuantidade(quantidade);
		procedimentoConsulta.setId(consProcedHospid);
		for (AacConsultaProcedHospitalar consultaProcedHospitalar : listaProcedimentosHospConsulta) {
			if (consultaProcedHospitalar.equals(procedimentoConsulta)) {
				throw new ApplicationBusinessException(ProcedimentoConsultaONExceptionCode.MSG_PROCEDIMENTO_CONSULTA_JA_EXISTENTE);
			}
		}
		executarEventoPreInsert(procedHospInterno, cid, quantidade);
		getProcedimentoConsultaRN().inserirProcedimentoConsulta(procedimentoConsulta, true, nomeMicrocomputador, dataFimVinculoServidor, aack_prh_rn_v_apac_diaria, aack_aaa_rn_v_protese_auditiva, fatk_cap_rn_v_cap_encerramento);
	}
	
	/**
	 * Remove um procedimento da consulta
	 * 
	 * @param procedimentoConsulta
	 * @throws BaseException 
	 */
	public void removerProcedimentoConsulta(
			AacConsultaProcedHospitalar procedimentoConsulta,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		procedimentoConsulta = getAacConsultaProcedHospitalarDAO().obterPorChavePrimaria(procedimentoConsulta.getId());
		// Evento pre-delete exclui da FatProcedAmbRealizado
		executarEventoPreDelete(
				procedimentoConsulta.getConsultas().getNumero(),
				procedimentoConsulta.getProcedHospInterno().getSeq(),
				nomeMicrocomputador, dataFimVinculoServidor);
		AacConsultaProcedHospitalarDAO consultaProcedHospitalarDAO = getAacConsultaProcedHospitalarDAO();
		consultaProcedHospitalarDAO.remover(procedimentoConsulta);
	}
	
	/**
	 * Valida se a quantidade do procedimento está entre -99 e 99
	 * 
	 * @return
	 */
	public void validarQuantidadeProcedimento(Byte quantidade) throws ApplicationBusinessException {
		if (quantidade >= -99 &&  quantidade <= 99) {
			return;
		}
		else {
			throw new ApplicationBusinessException(
					ProcedimentoConsultaONExceptionCode.MSG_QUANTIDADE_PROCEDIMENTO_CONSULTA_INVALIDA);
		}
	}
	
	/**
	 * Verifica se é necessário informar o CID para um determinado procedimento.
	 * Retorna um numero indicando a quantidade.
	 * 
	 * ORADB: Procedure P_TEM_CID_INFORMAR
	 * 
	 * @param procedimento
	 * @param cid
	 */
	public Integer verificarCidObrigatorio(FatProcedHospInternos procedHospInterno, AghCid cid) {
		Integer qtd = 0;
		boolean primeiraVez = true;
		
		List<FatProcedHospIntCid> procedimentosCid = getFaturamentoFacade()
				.pesquisarFatProcedHospIntCidAtivosPorPhiSeq(
						procedHospInterno.getSeq());

		if (!procedimentosCid.isEmpty()) {
			for (FatProcedHospIntCid fatProcedHospIntCid : procedimentosCid) {
				qtd++;
				if (primeiraVez) {
					primeiraVez = false;
					cid = fatProcedHospIntCid.getCid();
				}
			}			
		}
		
		return qtd;
	}
	
	/**
	 * ORADB: Procedure EVT_PRE_INSERT
	 * 
	 * @param procedimento
	 * @param cid
	 * @param quantidade
	 */
	public void executarEventoPreInsert(FatProcedHospInternos procedHospInterno, AghCid cid, Byte quantidade) throws ApplicationBusinessException {
		Integer qtdeCidInformar = 0;

		if (procedHospInterno != null) {
			qtdeCidInformar = verificarCidObrigatorio(procedHospInterno, cid);

			// Não tem cid para informar e foi informado
			if (qtdeCidInformar == 0 && cid != null) {
				throw new ApplicationBusinessException(
						ProcedimentoConsultaONExceptionCode.FAT_01096);
			}

			if (qtdeCidInformar > 0 && cid == null) {
				throw new ApplicationBusinessException(
						ProcedimentoConsultaONExceptionCode.FAT_01097);
			}

			// Não é necessário verificar se CID é compatível,
			// pois suggestion da tela pesquisará somente
			// por CIDs compatíveis com o procedimento informado
		}
	}
	
	/**
	 * 
	 * @param numeroConsulta
	 * @param phiSeq 
	 * @throws BaseException 
	 */
	public void executarEventoPreDelete(Integer numeroConsulta, Integer phiSeq,
			String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		List<FatProcedAmbRealizado> listaFatProcedAmbRealizado = getFaturamentoFacade()
				.buscarPorNumeroConsultaEProcedHospInternos(numeroConsulta, phiSeq);
		
		IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		
		if (!listaFatProcedAmbRealizado.isEmpty()) {
			FatProcedAmbRealizado procedAmbRealizado = listaFatProcedAmbRealizado.get(0);
			faturamentoFacade.excluirProcedimentoAmbulatorialRealizado(procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
		}
	}
	

	public AacConsultaProcedHospitalar clonarConsultaProcedHospitalar(AacConsultaProcedHospitalar consProcedHosp) 
			throws ApplicationBusinessException {
		AacConsultaProcedHospitalar cloneConsultaProcedHospitalar = new AacConsultaProcedHospitalar();
		
		try {
			cloneConsultaProcedHospitalar = (AacConsultaProcedHospitalar) BeanUtils.cloneBean(consProcedHosp);
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(ProcedimentoConsultaONExceptionCode.ERRO_CLONE_CONSULTA_PROCED_HOSPITALAR);
		}
		
		if (consProcedHosp.getCid() != null) {
			AghCid cid = new AghCid();
			cid.setSeq(consProcedHosp.getCid().getSeq());
			cloneConsultaProcedHospitalar.setCid(cid);
		}
		
		if (consProcedHosp.getConsultas() != null) {
			AacConsultas consulta = new AacConsultas();
			consulta.setNumero(consProcedHosp.getConsultas().getNumero());
			cloneConsultaProcedHospitalar.setConsultas(consulta);
		}
		
		if (consProcedHosp.getProcedHospInterno() != null) {
			FatProcedHospInternos procedHospInterno = new FatProcedHospInternos();
			procedHospInterno.setSeq(consProcedHosp.getProcedHospInterno().getSeq());
			cloneConsultaProcedHospitalar.setProcedHospInterno(procedHospInterno);			
		}
		
		return cloneConsultaProcedHospitalar;
	}
	
}
