package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFinalizacao;
import br.gov.mec.aghu.dominio.DominioIndConcluidaSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmRespostaConsultoria;
import br.gov.mec.aghu.model.MpmRespostaConsultoriaId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmTipoRespostaConsultoria;
import br.gov.mec.aghu.prescricaomedica.dao.MpmRespostaConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoRespostaConsultoriaDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de #998: Prescrição: Responder consultorias
 * 
 * @author fbraganca
 * 
 */
@Stateless
public class MpmRespostaConsultoriaRN extends BaseBusiness {

	private static final long serialVersionUID = -1783148822250831253L;
	
	private static final Log LOG = LogFactory.getLog(MpmRespostaConsultoriaRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private MpmSolicitacaoConsultoriaRN mpmSolicitacaoConsultoriaRN;
	
	@Inject
	private MpmTipoRespostaConsultoriaDAO mpmTipoRespostaConsultoriaDAO;
	
	@Inject
	private MpmRespostaConsultoriaDAO mpmRespostaConsultoriaDAO;
	
	private enum MpmRespostaConsultoriaRNExceptionCode implements BusinessExceptionCode {
		MPM_00724, MPM_00725, MPM_00728, MPM_00729, MPM_00730, MPM_00731, MPM_00732, MPM_00734, MPM_03944;
	}
	
	
	public void inserir(List<MpmRespostaConsultoria> respostas, DominioFinalizacao indFinalizacao) throws ApplicationBusinessException, BaseListException {
		// RF02
		this.verificarRespostaNaoPreenchida(respostas);
		
		if(respostas.isEmpty()) {
			throw new ApplicationBusinessException(MpmRespostaConsultoriaRNExceptionCode.MPM_00725);
		}
		
		// RF03
		this.validarTipoRepetido(respostas);
		
		// RF04
		if (indFinalizacao == null) {
			throw new ApplicationBusinessException(MpmRespostaConsultoriaRNExceptionCode.MPM_03944);
		}
		
		// RF05
		this.verificarRespostaObrigatoria(respostas);
		
		// RF06 - Chama a Trigger de inserção
		this.preInserir(respostas);
		
		for (MpmRespostaConsultoria mpmRespostaConsultoria : respostas) {
			mpmRespostaConsultoriaDAO.persistir(mpmRespostaConsultoria);
		}
		
	}
	
	/**
	 * RF02 -Validar se há alguma resposta não preenchida ou em branco, 
	 * se houver estes blocos de respostas serão descartados da inserção.
	 * @param respostas
	 * @throws ApplicationBusinessException
	 */
	private void verificarRespostaNaoPreenchida(List<MpmRespostaConsultoria> respostas) throws ApplicationBusinessException {
		
		List<MpmRespostaConsultoria> respostasAux = new ArrayList<MpmRespostaConsultoria>(respostas);
		Integer scnAtdSeq = null;
		Integer scnSeq = null;
		MpmSolicitacaoConsultoria solicitacaoConsultoria = new MpmSolicitacaoConsultoria();
		if(respostas != null && !respostas.isEmpty()){
			scnSeq = respostas.get(0).getId().getScnSeq();
			scnAtdSeq = respostas.get(0).getId().getScnAtdSeq();
			solicitacaoConsultoria = respostas.get(0).getSolicitacaoConsultoria();
			for (MpmRespostaConsultoria mpmRespostaConsultoria : respostasAux) {
				if (mpmRespostaConsultoria.getDescricao() == null) {
					respostas.remove(mpmRespostaConsultoria);
				}
			}
		}else{			
			MpmRespostaConsultoriaId id = new MpmRespostaConsultoriaId();
			id.setScnAtdSeq(scnAtdSeq);
			id.setScnSeq(scnSeq);
			id.setTrcSeq(null);
			id.setCriadoEm(new Date());
			MpmRespostaConsultoria respostaConsultoria = new MpmRespostaConsultoria();
			respostaConsultoria.setId(id);
			respostaConsultoria.setSolicitacaoConsultoria(solicitacaoConsultoria);
			respostas.add(respostaConsultoria);
			
			throw new ApplicationBusinessException(MpmRespostaConsultoriaRNExceptionCode.MPM_00725);
		}
	}
	
	/**
	 * RF03 - Validar se em todos os tipos de resposta adicionados não há repetição.
	 * @param respostas
	 * @throws ApplicationBusinessException
	 */
	private void validarTipoRepetido(List<MpmRespostaConsultoria> respostas) throws ApplicationBusinessException {
		List<MpmRespostaConsultoria> respostasAux = new ArrayList<MpmRespostaConsultoria>(respostas);
		for (MpmRespostaConsultoria mpmRespostaConsultoria : respostas) {
			int count = 0;
			for (MpmRespostaConsultoria mpmRespostaConsultoria2 : respostasAux) {
				if (mpmRespostaConsultoria2.getTipoRespostaConsultoria()
						.equals(mpmRespostaConsultoria.getTipoRespostaConsultoria())) {
					count++;
				}
				if (count > 1) {
					throw new ApplicationBusinessException(MpmRespostaConsultoriaRNExceptionCode.MPM_00734);
				}
			}
		}
	}
		
	/**
	 * Trigger de validações pré-insert para MPM_RESPOSTA_CONSULTORIAS.
	 * 
	 * @ORADB MPMT_REC_BRI
	 * @param mpmRespostaConsultoria
	 * @throws ApplicationBusinessException
	 */
	private void preInserir(List<MpmRespostaConsultoria> respostas) throws ApplicationBusinessException {
		
		for (MpmRespostaConsultoria mpmRespostaConsultoria : respostas) {
			
			mpmRespostaConsultoria.setServidor(servidorLogadoFacade.obterServidorLogado());
			
			// Verificar se tipo resposta consultaria está ativa 
			this.validarTipoRespostaConsultoria(mpmRespostaConsultoria.getTipoRespostaConsultoria());
			
			// Verificar se a solicitação de  consultaria está ativa e não está pendente 
			this.validarSolicitacaoConsultoria(mpmRespostaConsultoria.getSolicitacaoConsultoria());
			
		}
		// Atualizar tabela mpm_solicitacao_consultorias 
		this.mpmSolicitacaoConsultoriaRN.atualizarSolicitacaoConsultoria(respostas.get(0).getSolicitacaoConsultoria(), respostas.get(0));
	}
	
	/**
	 * Procedure que verifica se tipo resposta consultaria está ativa.
	 * 
	 * @ORADB MPMK_REC_RN.RN_RECP_VER_TP_RESP
	 * @param trcSeq
	 * @throws ApplicationBusinessException
	 */
	private void validarTipoRespostaConsultoria(MpmTipoRespostaConsultoria tipoRespostaConsultoria) throws ApplicationBusinessException {
		if (tipoRespostaConsultoria == null) {
			throw new ApplicationBusinessException(MpmRespostaConsultoriaRNExceptionCode.MPM_00728);
		} else {
			if (DominioSituacao.I.equals(tipoRespostaConsultoria.getIndSituacao())) {
				throw new ApplicationBusinessException(MpmRespostaConsultoriaRNExceptionCode.MPM_00729);
			}
		}
	}
	
	/**
	 * Procedure que verifica se a solicitação de consultaria está ativa e não está pendente.
	 * 
	 * @ORADB MPMK_REC_RN.RN_RECP_VER_SLCT_CNS
	 * @param scnAtdSeq
	 * @param scnSeq
	 * @throws ApplicationBusinessException
	 */
	private void validarSolicitacaoConsultoria(MpmSolicitacaoConsultoria solicitacaoConsultoria) throws ApplicationBusinessException {
		if (solicitacaoConsultoria == null) {
			throw new ApplicationBusinessException(MpmRespostaConsultoriaRNExceptionCode.MPM_00730);
		} else {
			if (DominioSituacao.I.equals(solicitacaoConsultoria.getIndSituacao())) {
				throw new ApplicationBusinessException(MpmRespostaConsultoriaRNExceptionCode.MPM_00731);
			}
			if (DominioIndPendenteItemPrescricao.P.equals(solicitacaoConsultoria.getIndPendente())) {
				throw new ApplicationBusinessException(MpmRespostaConsultoriaRNExceptionCode.MPM_00732);
			}
		}
	}
	
	/** 
	 * Busca obrigatoriedade de tipo de respostas conforme a situação da consultoria.
	 * 
	 * @ORADB MPMP_VER_RESP_OBR 
	 * @param mpmRespostaConsultoria
	 * @throws ApplicationBusinessException
	 */
	private void verificarRespostaObrigatoria(List<MpmRespostaConsultoria> listaRespostas) throws BaseListException {
		// Se ind_concluida é igual a N, significa que é a primeira vez que se está  
		// respondendo aquela consultoria, daí vai consistir os tipos obrigatórios na primeira resposta (prim_vez)
		if (DominioIndConcluidaSolicitacaoConsultoria.N.equals(listaRespostas.get(0).getSolicitacaoConsultoria().getIndConcluida())) {
			
			List<MpmTipoRespostaConsultoria> tiposRespPrimVez = mpmTipoRespostaConsultoriaDAO
					.pesquisarTiposRespostasPorSituacaoPrimeiraVezAcompanhamento(DominioSituacao.A, Boolean.TRUE, Boolean.TRUE, null, null);
			
			this.buscarObrigatoriedadeTipoResposta(tiposRespPrimVez, listaRespostas);
		}
		// Se ind_concluida é igual a A ou S, significa que não é a primeira vez que se está  
		// respondendo aquela consultoria, daí vai consistir os tipos obrigatórios 
		// no acompanhamento de uma consultoria (acompanhamento)
		if (DominioIndConcluidaSolicitacaoConsultoria.A.equals(listaRespostas.get(0).getSolicitacaoConsultoria().getIndConcluida()) ||
				DominioIndConcluidaSolicitacaoConsultoria.S.equals(listaRespostas.get(0).getSolicitacaoConsultoria().getIndConcluida())) {
			
			List<MpmTipoRespostaConsultoria> tiposRespAcomp = mpmTipoRespostaConsultoriaDAO
					.pesquisarTiposRespostasPorSituacaoPrimeiraVezAcompanhamento(DominioSituacao.A, null, null, Boolean.TRUE, Boolean.TRUE);
			
			this.buscarObrigatoriedadeTipoResposta(tiposRespAcomp, listaRespostas);
		}	
	}
	
	private void buscarObrigatoriedadeTipoResposta(List<MpmTipoRespostaConsultoria> tiposObrigatorios, 
			List<MpmRespostaConsultoria> respostas) throws BaseListException {
		
		if (tiposObrigatorios != null && !tiposObrigatorios.isEmpty()) {
			BaseListException listaDeErros = new BaseListException();
			for (MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria : tiposObrigatorios) {
				boolean encontrouTipo = false;
				for (MpmRespostaConsultoria mpmRespostaConsultoria : respostas) {
					if (mpmTipoRespostaConsultoria.equals(mpmRespostaConsultoria.getTipoRespostaConsultoria())) {
						encontrouTipo = true;
					}
				}
				if (!encontrouTipo) {
					listaDeErros.add(new ApplicationBusinessException(MpmRespostaConsultoriaRNExceptionCode.MPM_00724, mpmTipoRespostaConsultoria.getDescricao()));
				}
			}
			if (listaDeErros.hasException()) {
				throw listaDeErros;
			}
		}		
	}
		
	public List<MpmTipoRespostaConsultoria> pesquisarTiposRespostasConsultoria(DominioIndConcluidaSolicitacaoConsultoria indConcluida) {
		
		List<MpmTipoRespostaConsultoria> lista = mpmTipoRespostaConsultoriaDAO.pesquisarTiposRespostasConsultoria();
		List<MpmTipoRespostaConsultoria> listaAux = new ArrayList<MpmTipoRespostaConsultoria>(lista);
		
		for (MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria : listaAux) {
			
			if (!((DominioIndConcluidaSolicitacaoConsultoria.N.equals(indConcluida) && Boolean.TRUE.equals(mpmTipoRespostaConsultoria.getIndPrimVez()))
					|| ((DominioIndConcluidaSolicitacaoConsultoria.S.equals(indConcluida)
							|| DominioIndConcluidaSolicitacaoConsultoria.A.equals(indConcluida)) && Boolean.TRUE.equals(mpmTipoRespostaConsultoria.getIndAcompanhamento())))) {
				
				lista.remove(mpmTipoRespostaConsultoria);
				
			}
		}		
		return lista;
	}

}