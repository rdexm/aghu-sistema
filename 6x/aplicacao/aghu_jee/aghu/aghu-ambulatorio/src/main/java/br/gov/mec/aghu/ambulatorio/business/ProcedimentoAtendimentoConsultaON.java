package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoRealizadoDAO;
import br.gov.mec.aghu.ambulatorio.vo.ProcedimentoAtendimentoConsultaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Regras de negócio (migradas do Forms) 
 * para procedimentos realizados em um atendimento médico.
 * 
 * @author diego.pacheco
 */
@Stateless
public class ProcedimentoAtendimentoConsultaON extends BaseBusiness {

	@EJB
	private ProcedimentoAtendimentoConsultaRN procedimentoAtendimentoConsultaRN;
	
	private static final Log LOG = LogFactory.getLog(ProcedimentoAtendimentoConsultaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private MamProcedimentoRealizadoDAO mamProcedimentoRealizadoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MamProcedimentoDAO mamProcedimentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6221524109478253897L;

	public enum ProcedimentoAtendimentoConsultaONExceptionCode implements
			BusinessExceptionCode {

		MSG_QUANTIDADE_PROCEDIMENTO_ATEND_CONSULTA_INVALIDA, MSG_ERRO_ATUALIZAR_PROCEDIMENTO_REALIZADO, 
		MAM_02279;
	}
	
	
	/**
	 * Altera a quantidade do procedimento realizado.
	 * A alteração de quantidade é sempre realizada
	 * no registro indicado como PENDENTE.
	 * 
	 * ORADB: Procedure ALTERA_QUANTIDADE_PROCED
	 */
	public MamProcedimentoRealizado alterarQuantidadeProcedimento(Integer consultaNumero, Integer prdSeq, Byte quantidade, Boolean carga) 
			throws BaseException, CloneNotSupportedException {
		
		List<MamProcedimentoRealizado> listaProcedimentoValidado = getMamProcedimentoRealizadoDAO()
			.pesquisarProcedimentoPorConsultaProcedimentoValidado(consultaNumero, prdSeq);
		
		// Verifica se já possui um registro 'PENDENTE' para esse proc
		List<MamProcedimentoRealizado> listaProcRealizadoPendente = getMamProcedimentoRealizadoDAO()
			.pesquisarProcedimentoPorConsultaProcedimentoPendente(consultaNumero, prdSeq);
		
		MamProcedimentoRealizado procedimentoRealizado = null;
		
		if (!carga) {
			if (!listaProcedimentoValidado.isEmpty()) {
				MamProcedimentoRealizado procedimentoRealizadoValidado = listaProcedimentoValidado.get(0);
				
				if (procedimentoRealizadoValidado != null) {
					if (listaProcRealizadoPendente.isEmpty()) {
						// Não tem um registro de 'PENDENTE' para o proc, então insere um novo registro 'PENDENTE'
						MamProcedimentoRealizado newProcedimentoRealizado = (MamProcedimentoRealizado) procedimentoRealizadoValidado.clone();
						newProcedimentoRealizado.setSeq(null);
						newProcedimentoRealizado.setMamProcRealizados(null);
						newProcedimentoRealizado.setProcedimentoRealizado(procedimentoRealizadoValidado); // pol_seq
						newProcedimentoRealizado.setQuantidade(quantidade);
						newProcedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.P);
						newProcedimentoRealizado.setDthrCriacao(new Date());
						newProcedimentoRealizado.setDthrValida(null);
						newProcedimentoRealizado.setServidorValida(null);
						procedimentoRealizado = getProcedimentoAtendimentoConsultaRN().inserirProcedimentoRealizado(
								newProcedimentoRealizado, false);
					}
					
					// Atualiza o proc que estava 'VALIDADO' para 'ALTERAÇÃO NÃO VALIDADA'
					atualizarPendenciaProcedimento(procedimentoRealizadoValidado, DominioIndPendenteAmbulatorio.A);
				}
			}
			else if (!listaProcRealizadoPendente.isEmpty()) {
				MamProcedimentoRealizado oldProcRealizadoPendente = (MamProcedimentoRealizado) listaProcRealizadoPendente.get(0).clone();
				MamProcedimentoRealizado newProcRealizadoPendente = (MamProcedimentoRealizado) listaProcRealizadoPendente.get(0).clone();
				newProcRealizadoPendente.setQuantidade(quantidade);
				procedimentoRealizado = getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(
						oldProcRealizadoPendente, newProcRealizadoPendente, false);
			}
			else {
				throw new ApplicationBusinessException(
						ProcedimentoAtendimentoConsultaONExceptionCode.MSG_ERRO_ATUALIZAR_PROCEDIMENTO_REALIZADO);
			}
		}
		
		return procedimentoRealizado;
	}
	

	/**
	 * Valida se a quantidade do procedimento está entre 1 e 99
	 * 
	 * @param quantidade
	 * 
	 */
	public void validarQuantidadeProcedimento(Byte quantidade) throws ApplicationBusinessException {
		if (quantidade.byteValue() >= 1 &&  quantidade.byteValue() <= 99) {
			return;
		}
		else {
			throw new ApplicationBusinessException(
					ProcedimentoAtendimentoConsultaONExceptionCode.MSG_QUANTIDADE_PROCEDIMENTO_ATEND_CONSULTA_INVALIDA);
		}
	}
	
	/**
	 * ORADB: Procedure ALTERA_AUTO_REL_POL
	 * 
	 * Método modificado durante a migração do AGH.
	 * O uso deste método será para alterar a pendencia do procedimento
	 * de Validado (V) para Alteração não validada (A)
	 * 
	 * 
	 * @param procedimentoRealizadoRelacionado
	 * @param newPendente
	 * @throws ApplicationBusinessException
	 * @throws CloneNotSupportedException
	 * @throws ApplicationBusinessException 
	 */
	private MamProcedimentoRealizado atualizarPendenciaProcedimento(MamProcedimentoRealizado procedimentoRealizado, 
			DominioIndPendenteAmbulatorio newPendente) throws ApplicationBusinessException, CloneNotSupportedException, ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (procedimentoRealizado != null 
				&& (procedimentoRealizado.getPendente().equals(DominioIndPendenteAmbulatorio.V) )) {
			
			MamProcedimentoRealizado oldProcedimentoRealizado = (MamProcedimentoRealizado) procedimentoRealizado.clone();
			MamProcedimentoRealizado newProcedimentoRealizado = (MamProcedimentoRealizado) procedimentoRealizado.clone();
			newProcedimentoRealizado.setDthrMovimento(new Date());
			newProcedimentoRealizado.setServidorMovimento(servidorLogado);
			newProcedimentoRealizado.setPendente(newPendente);
			procedimentoRealizado = getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(
					oldProcedimentoRealizado, newProcedimentoRealizado, false);
		}
		else {
			throw new ApplicationBusinessException(
					ProcedimentoAtendimentoConsultaONExceptionCode.MSG_ERRO_ATUALIZAR_PROCEDIMENTO_REALIZADO);
		}
		
		return procedimentoRealizado;
	}
	
	/**
	 * Altera a quantidade do procedimento realizado.
	 * A alteração de CID é sempre realizada
	 * no registro indicado como PENDENTE.
	 * 
	 * ORADB: Procedure P_ALTERA_CID_PROCED
	 * 
	 * @param consultaNumero
	 * @param prdSeq
	 * @param cidSeq
	 * @throws ApplicationBusinessException
	 * @throws CloneNotSupportedException
	 */
	public MamProcedimentoRealizado alterarCidProcedimento(MamProcedimentoRealizado procedimentoRealizado, String cidCodigo, Boolean carga) 
			throws ApplicationBusinessException, CloneNotSupportedException {
		
		if (!carga) {
			if (procedimentoRealizado != null) {
				AghCid cid = getAghuFacade().obterCid(cidCodigo);
				MamProcedimentoRealizado oldProcRealizadoPendente = (MamProcedimentoRealizado) procedimentoRealizado.clone();
				MamProcedimentoRealizado newProcRealizadoPendente = (MamProcedimentoRealizado) procedimentoRealizado.clone();
				newProcRealizadoPendente.setCid(cid);
				procedimentoRealizado = getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(
						oldProcRealizadoPendente, newProcRealizadoPendente, false);
			}
			else {
				throw new ApplicationBusinessException(
						ProcedimentoAtendimentoConsultaONExceptionCode.MSG_ERRO_ATUALIZAR_PROCEDIMENTO_REALIZADO);
			}
		}
		
		return procedimentoRealizado;
	}
	
	/**
	 * ORADB: Procedure CARGA_PROCEDIMENTOS_TEMP
	 * 
	 * @param consultaNumero
	 * @param prdSeq
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<ProcedimentoAtendimentoConsultaVO> listarProcedimentosAtendimento(Integer consultaNumero, Short espSeq, Short paiEspSeq, Boolean inicio) 
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		List<ProcedimentoAtendimentoConsultaVO> listaProcedimentos = new ArrayList<ProcedimentoAtendimentoConsultaVO>();
		ProcedimentoAtendimentoConsultaVO procedimentoNenhum = null;
		Boolean possuiProcRealizado = false;
		
		MamProcedimentoDAO mamProcedimentoDAO = getMamProcedimentoDAO();
		
		BigDecimal seqProcNenhum = this.buscarSeqNenhumProcedimento();
		
		List<Integer> listaSeqsExcluir = new ArrayList<Integer>();
		
		List<Object[]> listaProcedimentosObject1 = mamProcedimentoDAO.pesquisarProcedimentoPorNumeroConsultaUnion1(consultaNumero);		
		for (Object[] procedimento : listaProcedimentosObject1) {
			listaProcedimentos.add(popularProcedimentoAtendimentoConsultaVO(procedimento, seqProcNenhum));
			listaSeqsExcluir.add((Integer) procedimento[0]);
		}
		
		List<Object[]> listaProcedimentosObject2 = mamProcedimentoDAO.pesquisarProcedimentoPorNumeroConsultaUnion2(consultaNumero, espSeq, paiEspSeq, listaSeqsExcluir);
		listaSeqsExcluir.clear();
		for (Object[] procedimento : listaProcedimentosObject2) {
			listaProcedimentos.add(popularProcedimentoAtendimentoConsultaVO(procedimento, seqProcNenhum));
			listaSeqsExcluir.add((Integer) procedimento[0]);
		}
		
		List<Object[]> listaProcedimentosObject3 = mamProcedimentoDAO.pesquisarProcedimentoPorNumeroConsultaUnion3(consultaNumero, espSeq, paiEspSeq, listaSeqsExcluir);
		listaSeqsExcluir.clear();
		for (Object[] procedimento : listaProcedimentosObject3) {
			listaProcedimentos.add(popularProcedimentoAtendimentoConsultaVO(procedimento, seqProcNenhum));
			listaSeqsExcluir.add((Integer) procedimento[0]);
		}
		
		List<Object[]> listaProcedimentosObject4 = mamProcedimentoDAO.pesquisarProcedimentoPorNumeroConsultaUnion4(consultaNumero, espSeq, paiEspSeq, listaSeqsExcluir);
		listaSeqsExcluir.clear();
		for (Object[] procedimento : listaProcedimentosObject4) {
			listaProcedimentos.add(popularProcedimentoAtendimentoConsultaVO(procedimento, seqProcNenhum));
			listaSeqsExcluir.add((Integer) procedimento[0]);
		}
				
		List<Object[]> listaProcedimentosObject5 = mamProcedimentoDAO.pesquisarProcedimentoPorNumeroConsultaUnion5(consultaNumero, espSeq, paiEspSeq, listaSeqsExcluir);
		List<ProcedimentoAtendimentoConsultaVO> listaProcedimentosPadraoConsulta = new ArrayList<ProcedimentoAtendimentoConsultaVO>();
		for (Object[] procedimento : listaProcedimentosObject5) {
			listaProcedimentosPadraoConsulta.add(popularProcedimentoAtendimentoConsultaVO(procedimento, seqProcNenhum));
		}
		
		listaSeqsExcluir = null;			
		
		AacConsultas consulta = getAacConsultasDAO().obterConsulta(consultaNumero);
		
		// Verifica se é o procedimento "nenhum"
		for (ProcedimentoAtendimentoConsultaVO procedimentoVO : listaProcedimentos) {
			if (verificarNenhumProcedimento(procedimentoVO.getSeq(), seqProcNenhum)) {
				procedimentoNenhum = procedimentoVO;
				// Se só existe o nenhum como opção
				// inclui automaticamente nos realizados
				if (procedimentoNenhum != null && listaProcedimentos.size() == 1 
						&& !procedimentoNenhum.getSituacao().isAtivo()) {
					procedimentoNenhum.setRealizado(true);

					MamProcedimento procedimento = getMamProcedimentoDAO().obterPorChavePrimaria(procedimentoNenhum.getSeq());
					
					// Inclui como proc realizado
					MamProcedimentoRealizado procedimentoRealizado = new MamProcedimentoRealizado();
					procedimentoRealizado.setDthrCriacao(new Date());
					procedimentoRealizado.setSituacao(DominioSituacao.A);			
					procedimentoRealizado.setProcedimento(procedimento);
					procedimentoRealizado.setQuantidade((byte)1);
					if (procedimentoVO.getCidCodigo() != null ) {
						AghCid cid = getAghuFacade().obterCid(procedimentoVO.getCidCodigo());
						procedimentoRealizado.setCid(cid);
					}
					procedimentoRealizado.setConsulta(consulta);
					procedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.P);
					procedimentoRealizado.setPadraoConsulta(Boolean.FALSE);
					procedimentoRealizado.setServidor(servidorLogado);
					getProcedimentoAtendimentoConsultaRN().inserirProcedimentoRealizado(procedimentoRealizado, true);
				}				
				break;
			}
		}
		
		for (ProcedimentoAtendimentoConsultaVO procedimentoVO : listaProcedimentos) {
			if (procedimentoVO.getSituacao().isAtivo() && procedimentoNenhum != null 
					&& !(procedimentoVO.getSeq().intValue() == procedimentoNenhum.getSeq().intValue())) {
				possuiProcRealizado = true;	
			}
		}
		
		if (procedimentoNenhum != null && !(procedimentoNenhum.getSituacao().isAtivo()) && !possuiProcRealizado) {
			
			// Verifica os procedimentos padrão da consulta,
			// marcando como realizados quando houver esses procedimentos padrão da consulta
			for (ProcedimentoAtendimentoConsultaVO procedimentoVO : listaProcedimentos) {
				
				for (ProcedimentoAtendimentoConsultaVO procedimentoPadraoConsVO : listaProcedimentosPadraoConsulta) {
					
					if (procedimentoVO.getSeq().intValue() == procedimentoPadraoConsVO.getSeq().intValue()
							&& procedimentoPadraoConsVO.getPadraoConsulta()) {
						
						procedimentoVO.setPadraoConsulta(true);
						
						// Se o proc faz parte da consulta e ainda não foi selecionado
						if(inicio) {
							if (procedimentoVO.getSituacao() == null || !procedimentoVO.getSituacao().isAtivo()) {
						
								procedimentoVO.setRealizado(true);
								procedimentoVO.setQuantidade(procedimentoPadraoConsVO.getQuantidade());
								
								MamProcedimento procedimento = getMamProcedimentoDAO().obterPorChavePrimaria(procedimentoVO.getSeq());
								
								// Inclui como proc realizado
								MamProcedimentoRealizado procedimentoRealizado = new MamProcedimentoRealizado();
								procedimentoRealizado.setDthrCriacao(new Date());
								procedimentoRealizado.setSituacao(DominioSituacao.A);			
								procedimentoRealizado.setProcedimento(procedimento);
								procedimentoRealizado.setQuantidade(procedimentoPadraoConsVO.getQuantidade());
								if (procedimentoPadraoConsVO.getCidSeq() != null && procedimentoPadraoConsVO.getPhiSeq() != null) {
									List<FatProcedHospIntCid> listaProcedHospIntCid = 
										getFaturamentoFacade().
											pesquisarFatProcedHospIntCidAtivosPorPhiSeqCidSeq(
													procedimentoPadraoConsVO.getPhiSeq(),
													procedimentoPadraoConsVO.getCidSeq());
									procedimentoRealizado.setCid(listaProcedHospIntCid.get(0).getCid());
									procedimentoVO.setCidCodigo(listaProcedHospIntCid.get(0).getCid().getCodigo());
								}
								procedimentoRealizado.setConsulta(consulta);
								procedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.P);
								procedimentoRealizado.setPadraoConsulta(Boolean.TRUE);
								procedimentoRealizado.setServidor(servidorLogado);
								getProcedimentoAtendimentoConsultaRN().inserirProcedimentoRealizado(procedimentoRealizado, true);
							}	
						}
					}
				}
			}
		}
		
		// ORDER BY  
		//          -- nenhum
        //  		-- os realizados
        //  		-- alfabética
		if (!listaProcedimentos.isEmpty()) {
			Collections.sort(listaProcedimentos, new ProcedimentoAtendimentoConsultaVOComparator());			
		}		
		
		return listaProcedimentos;
	}
	
	
	private ProcedimentoAtendimentoConsultaVO popularProcedimentoAtendimentoConsultaVO(Object[] procedimento, BigDecimal seqProcNenhum) throws ApplicationBusinessException {
		Integer seq = (Integer) procedimento[0];
		String descricao = (String) procedimento[1];
		Byte quantidade = procedimento[2] != null ? (Byte) procedimento[2] : 1;  // NVL(quantidade,1)
		DominioSituacao situacao = procedimento[3].equals(DominioSituacao.A) ? DominioSituacao.A : DominioSituacao.I;
		Boolean padraoConsulta = procedimento[4] != null ?  procedimento[4].equals("1") ? Boolean.TRUE : (Boolean) procedimento[4] : Boolean.FALSE; 
		Integer phiSeq = (Integer) procedimento[5];
		Integer cidSeq = (Integer) procedimento[6];	
		String cidCodigo = (String) procedimento[7];
		String cidDescricao = (String) procedimento[8];
		Short pedSeq = (Short) procedimento[9];				
		Boolean nenhumProcedimentoRealizado = verificarNenhumProcedimento(seq, seqProcNenhum);
		Boolean realizado = situacao.isAtivo();
		
		ProcedimentoAtendimentoConsultaVO procedimentoAtendConsultaVO = new ProcedimentoAtendimentoConsultaVO();
		procedimentoAtendConsultaVO.setSeq(seq);
		procedimentoAtendConsultaVO.setDescricao(descricao);
		procedimentoAtendConsultaVO.setQuantidade(quantidade);
		procedimentoAtendConsultaVO.setSituacao(situacao);
		procedimentoAtendConsultaVO.setPadraoConsulta(padraoConsulta);
		procedimentoAtendConsultaVO.setPhiSeq(phiSeq);
		procedimentoAtendConsultaVO.setCidSeq(cidSeq);
		procedimentoAtendConsultaVO.setCidCodigo(cidCodigo);
		procedimentoAtendConsultaVO.setPedSeq(pedSeq);
		procedimentoAtendConsultaVO.setNenhumProcedimentoRealizado(nenhumProcedimentoRealizado);
		procedimentoAtendConsultaVO.setRealizado(realizado);
		procedimentoAtendConsultaVO.setCidDescricao(cidDescricao);
		
		return procedimentoAtendConsultaVO;
	}
	
	/**
	 * Comparator para ordenar pesquisa dos procedimentos de atendimento
	 * 
	 * @author diego.pacheco
	 *
	 */
	static class ProcedimentoAtendimentoConsultaVOComparator implements Comparator<ProcedimentoAtendimentoConsultaVO> {
		@Override
		public int compare(ProcedimentoAtendimentoConsultaVO p1, ProcedimentoAtendimentoConsultaVO p2) {
			//#25299
			if (p1.getNenhumProcedimentoRealizado()) {
				return -1;
			} else if (p2.getNenhumProcedimentoRealizado()){
				return 1;
			}
			
			int compSituacao = p1.getSituacao().compareTo(p2.getSituacao());
			int compDescricao = p1.getDescricao().compareTo(p2.getDescricao());

			if (compSituacao != 0) {
				return compSituacao;
			}
			return compDescricao;
		}
	}

	
	/**
	 * ORADB: Function MAMC_GET_PROC_NENHUM
	 * 
	 * @param prdSeq
	 * @return
	 */
	public Boolean verificarNenhumProcedimento(Integer prdSeq, BigDecimal seqProcNenhum) throws ApplicationBusinessException {
		if (seqProcNenhum.intValue() == prdSeq.intValue()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public BigDecimal buscarSeqNenhumProcedimento() throws ApplicationBusinessException {
		BigDecimal seqProcNenhum = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_NENHUM).getVlrNumerico();
		return seqProcNenhum;
	}
	
	/**
	 * Executa operações de delete/insert de acordo com a seleção de procedimentos realizados.
	 * 
	 * ORADB: Procedure WHEN-CHECKBOX-CHANGED
	 * 
	 * @param prdSeq
	 */
	public void executarOperacoesAposSelecionarProcedimento(ProcedimentoAtendimentoConsultaVO procedimentoVO, Integer consultaNumero) 
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
		// Não será necessario verificar condição
		// em que não foi selecionado um procedimento
		
		// Se é o "nenhum"
		BigDecimal seqProcNenhum = this.buscarSeqNenhumProcedimento();
		if (verificarNenhumProcedimento(procedimentoVO.getSeq(), seqProcNenhum)) {
			// e foi marcado
			if (procedimentoVO.getSituacao().isAtivo()) {
				List<MamProcedimentoRealizado> listaProcedimentosRealizados = getMamProcedimentoRealizadoDAO()
					.pesquisarProcedimentoRealizadoPorConsultaExcetoSeqInformado(consultaNumero, procedimentoVO.getSeq());
				if (!listaProcedimentosRealizados.isEmpty()) {
					for (MamProcedimentoRealizado procedimentoRealizado : listaProcedimentosRealizados) {
						// Deleta todos os procedimentos exceto o "nenhum" 
						getProcedimentoAtendimentoConsultaRN().removerProcedimentoRealizado(procedimentoRealizado, false);
					}
				}
			}
		}
		
		// O teste para "Se selecionou outro procedimento e nenhum já estava selecionado = ERRO"
		// foi movido para controller
		
		// Se marcou
		if (procedimentoVO.getSituacao().isAtivo()) {
			// Inclui nos procs realizados
			AacConsultas consulta = getAacConsultasDAO().obterConsulta(consultaNumero);
			MamProcedimento procedimento = getMamProcedimentoDAO().obterPorChavePrimaria(procedimentoVO.getSeq());
						
			MamProcedimentoRealizado procedimentoRealizado = new MamProcedimentoRealizado();
			procedimentoRealizado.setDthrCriacao(new Date());
			procedimentoRealizado.setSituacao(DominioSituacao.A);			
			procedimentoRealizado.setProcedimento(procedimento);
			procedimentoRealizado.setQuantidade(procedimentoVO.getQuantidade());
			if (procedimentoVO.getCidCodigo() != null ) {
				AghCid cid = getAghuFacade().obterCid(procedimentoVO.getCidCodigo());
				procedimentoRealizado.setCid(cid);
			}
			procedimentoRealizado.setPadraoConsulta(procedimentoVO.getPadraoConsulta());
			procedimentoRealizado.setConsulta(consulta);
			procedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.P);
			procedimentoRealizado.setServidor(servidorLogado);
			procedimentoRealizado.setPadraoConsulta(Boolean.FALSE);
			getProcedimentoAtendimentoConsultaRN().inserirProcedimentoRealizado(procedimentoRealizado, false);
		}
		// Se desmarcou
		else if (!procedimentoVO.getSituacao().isAtivo()) {
			MamProcedimentoRealizado procedimentoRealizado = null; 
			List<MamProcedimentoRealizado> listaProcedimentosRealizados = getMamProcedimentoRealizadoDAO()
				.pesquisarProcedimentoPorConsultaProcedimento(consultaNumero, procedimentoVO.getSeq());
				
			if (listaProcedimentosRealizados != null && !listaProcedimentosRealizados.isEmpty()) {
				procedimentoRealizado = listaProcedimentosRealizados.get(0);
			}
				
			if (procedimentoRealizado == null) {
				throw new ApplicationBusinessException(ProcedimentoAtendimentoConsultaONExceptionCode.MAM_02279);
			}
			else {
				for (MamProcedimentoRealizado mamProcedimentoRealizado : listaProcedimentosRealizados) {
					getProcedimentoAtendimentoConsultaRN().removerProcedimentoRealizado(mamProcedimentoRealizado, false);
				}
			}
		}
	}
	
	protected MamProcedimentoRealizadoDAO getMamProcedimentoRealizadoDAO() {
		return mamProcedimentoRealizadoDAO;
	}
	
	protected MamProcedimentoDAO getMamProcedimentoDAO() {
		return mamProcedimentoDAO;
	}
	
	protected ProcedimentoAtendimentoConsultaRN getProcedimentoAtendimentoConsultaRN() {
		return procedimentoAtendimentoConsultaRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
