package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoAutorizacaoFornJnRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoAutorizacaoFornJnRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private ScoAutorizacaoFornJnDAO scoAutorizacaoFornJnDAO;
	

	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
		

	private static final long serialVersionUID = 7276860329724543921L;
		
	public enum ScoAutorizacaoFornJnRNExceptionCode implements BusinessExceptionCode {
		ERRO_INSERIR_AUT_FORNECIMENTO_JN,ERRO_EXCLUIR_AUT_FORNECIMENTO_JN		
	}

	
	
	/**
	 * TODO Implemementar aqui regras associadas a trigger SCOT_AFJN_BRU
	 * @param autorizacaoFornecimentoJn
	 * @param autorizacaoFornecimentoJnOriginal
	 */
	public void atualizar(ScoAutorizacaoFornJn autorizacaoFornecimentoJn, ScoAutorizacaoFornJn autorizacaoFornecimentoJnOriginal) {
		this.getScoAutorizacaoFornJnDAO().atualizar(autorizacaoFornecimentoJn);
	}
	
	/**
	 * Atualiza a journal de uma AF quando não é necessário informar motivo de alteração
	 * @param autorizacaoFornecimento
	 */
	public void atualizarAutorizacaoFornecimentoJnSemMotivoAlteracao(ScoAutorizacaoForn autorizacaoFornecimento) {
		ScoAutorizacaoFornJn autJn = this.getScoAutorizacaoFornJnDAO().buscarUltimaScoAutorizacaoFornJnPorNroAF(autorizacaoFornecimento.getNumero(), null);

		if (autJn != null) {
			if (autorizacaoFornecimento.getDtPrevEntrega() != null) {
				autJn.setDtPrevEntrega(autorizacaoFornecimento.getDtPrevEntrega());
			}
			if (autorizacaoFornecimento.getNroContrato() != null) {
				autJn.setNroContrato(autorizacaoFornecimento.getNroContrato());
			}
			if (autorizacaoFornecimento.getDtVenctoContrato() != null) {
				autJn.setDtVenctoContrato(autorizacaoFornecimento .getDtVenctoContrato());
			}			
			if (autorizacaoFornecimento.getServidorGestor() != null) {
				autJn.setServidorGestor(autorizacaoFornecimento.getServidorGestor());
			}
			if (autorizacaoFornecimento.getObservacao() != null) {
				autJn.setObservacao(autorizacaoFornecimento.getObservacao());
			}
			this.getScoAutorizacaoFornJnDAO().atualizar(autJn);
		}
		
		
	}
	
	private void popularServidoresJn(ScoAutorizacaoForn autorizacaoFornecimento, ScoAutorizacaoFornJn autorizacaoFornecimentoJn, Boolean geracao) {
		if (autorizacaoFornecimento.getServidorGestor() != null) {
			RapServidores servidorGestor = this.getRegistroColaboradorFacade()
					.obterRapServidoresPorChavePrimaria(autorizacaoFornecimento.getServidorGestor().getId());
			autorizacaoFornecimentoJn.setServidorGestor(servidorGestor);
		}
		
		if (!geracao) {
			if (autorizacaoFornecimento.getServidorControlado() != null) {
				autorizacaoFornecimentoJn.setServidorControlado(autorizacaoFornecimento.getServidorControlado());
			}
		}
	}

	public void excluirAutorizacaoFornecimentoJn (
			ScoAutorizacaoFornJn autorizacaoFornecimentoJn) throws ApplicationBusinessException {
		try {
			this.getScoAutorizacaoFornJnDAO().remover(autorizacaoFornecimentoJn);
		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ScoAutorizacaoFornJnRNExceptionCode.ERRO_EXCLUIR_AUT_FORNECIMENTO_JN);
		}
	}

	/**
	 * Cria uma journal da AF
	 * @param autorizacaoFornecimento
	 * @param geracao
	 * @return
	 */
	public ScoAutorizacaoFornJn inserirAutorizacaoFornecimentoJn(
			ScoAutorizacaoForn autorizacaoFornecimento, Boolean geracao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoAutorizacaoFornJn autorizacaoFornecimentoJn = new ScoAutorizacaoFornJn();
		autorizacaoFornecimentoJn.setJnDateTime(new Date());
		autorizacaoFornecimentoJn.setJnOperation(DominioOperacoesJournal.UPD.toString());
		autorizacaoFornecimentoJn.setJnUser(servidorLogado.getUsuario());
		autorizacaoFornecimentoJn.setNumero(autorizacaoFornecimento.getNumero());
		RapServidores servidor = this.getRegistroColaboradorFacade()
				.obterRapServidoresPorChavePrimaria(
						autorizacaoFornecimento.getServidor().getId());
		autorizacaoFornecimentoJn.setServidor(servidor);
		autorizacaoFornecimentoJn.setVerbaGestao(autorizacaoFornecimento.getVerbaGestao());
		autorizacaoFornecimentoJn.setMdaCodigo(autorizacaoFornecimento.getMoeda());
		autorizacaoFornecimentoJn.setPropostaFornecedor(autorizacaoFornecimento.getPropostaFornecedor());
		autorizacaoFornecimentoJn.setNroComplemento(autorizacaoFornecimento.getNroComplemento());
		autorizacaoFornecimentoJn.setDtPrevEntrega(autorizacaoFornecimento.getDtPrevEntrega());
		autorizacaoFornecimentoJn.setValorFrete(autorizacaoFornecimento.getValorFrete());
		autorizacaoFornecimentoJn.setModalidadeEmpenho(autorizacaoFornecimento.getModalidadeEmpenho());
		autorizacaoFornecimentoJn.setIndAprovada(DominioAprovadaAutorizacaoForn.N);
		FsoNaturezaDespesa naturezaDespesa = this.getCadastrosBasicosOrcamentoFacade().obterNaturezaDespesa(autorizacaoFornecimento.getNaturezaDespesa().getId());
		autorizacaoFornecimentoJn.setNaturezaDespesa(naturezaDespesa);
		ScoCondicaoPagamentoPropos condicaoPagamentoPropos = this.getPacFacade().obterCondicaoPagamentoPropostaPorNumero(autorizacaoFornecimento.getCondicaoPagamentoPropos().getNumero());
		autorizacaoFornecimentoJn.setCondicaoPagamentoPropos(condicaoPagamentoPropos);
		autorizacaoFornecimentoJn.setServidorAutorizado(autorizacaoFornecimento.getServidorAutorizado());	
		
		
		this.popularServidoresJn(autorizacaoFornecimento, autorizacaoFornecimentoJn, geracao);
		
		if (geracao) {
			autorizacaoFornecimentoJn.setSequenciaAlteracao(Short.valueOf("0"));
			autorizacaoFornecimentoJn.setIndExclusao(Boolean.FALSE);
			autorizacaoFornecimentoJn.setSituacao(DominioSituacaoAutorizacaoFornecimento.AE);
			autorizacaoFornecimentoJn.setIndGeracao(Boolean.TRUE);
			autorizacaoFornecimentoJn.setDtGeracao(new Date());
		} else {
			autorizacaoFornecimentoJn.setSeq(null);
			autorizacaoFornecimentoJn.setDtAlteracao(new Date());
			autorizacaoFornecimentoJn.setDtAssinaturaChefia(null);
			autorizacaoFornecimentoJn.setDtAssinaturaCoord(null);
			autorizacaoFornecimentoJn.setServidorAssinaCoord(null);
			autorizacaoFornecimentoJn.setSequenciaAlteracao((short) (this.getScoAutorizacaoFornJnDAO().obterMaxSequenciaAlteracaoAfJn(autorizacaoFornecimento.getNumero()) +1));
			autorizacaoFornecimentoJn.setIndGeracao(autorizacaoFornecimento.getGeracao());
			autorizacaoFornecimentoJn.setDtGeracao(autorizacaoFornecimento.getDtGeracao());
			autorizacaoFornecimentoJn.setIndExclusao(autorizacaoFornecimento.getExclusao());
			if (autorizacaoFornecimento.getExclusao()) {
				autorizacaoFornecimentoJn.setSituacao(DominioSituacaoAutorizacaoFornecimento.EX);
				autorizacaoFornecimentoJn.setDtExclusao(autorizacaoFornecimento.getDtExclusao());
				autorizacaoFornecimentoJn.setServidorExcluido(autorizacaoFornecimento.getServidorExcluido());
			} else {
				autorizacaoFornecimentoJn.setSituacao(autorizacaoFornecimento.getSituacao());
				autorizacaoFornecimentoJn.setDtExclusao(null);
				autorizacaoFornecimentoJn.setServidorExcluido(null);
			}						
			
			if (autorizacaoFornecimento.getObservacao() != null) {
				autorizacaoFornecimentoJn.setObservacao(autorizacaoFornecimento.getObservacao());
			}
			
			if (autorizacaoFornecimento.getNroEmpenho() != null) {
				autorizacaoFornecimentoJn.setNroEmpenho(autorizacaoFornecimento.getNroEmpenho());
			}
			
			if (autorizacaoFornecimento.getValorEmpenho() != null) {
				autorizacaoFornecimentoJn.setValorEmpenho(autorizacaoFornecimento.getValorEmpenho().doubleValue());
			}
			
			if (autorizacaoFornecimento.getMotivoAlteracaoAf() != null) {
				autorizacaoFornecimentoJn.setMotivoAlteracaoAf(autorizacaoFornecimento.getMotivoAlteracaoAf());
			}

			if (autorizacaoFornecimento.getConvenioFinanceiro() != null) {
				autorizacaoFornecimentoJn.setCvfCodigo(autorizacaoFornecimento.getConvenioFinanceiro().getCodigo());
			}

			if (autorizacaoFornecimento.getNroContrato() != null) {
				autorizacaoFornecimentoJn.setNroContrato(autorizacaoFornecimento.getNroContrato());
			}
			
			if (autorizacaoFornecimento.getDtVenctoContrato() != null) {
				autorizacaoFornecimentoJn.setDtVenctoContrato(autorizacaoFornecimento.getDtVenctoContrato());
			}			
		}
		return this.inserirAutorizacaoFornecimentoJn(autorizacaoFornecimentoJn);
	}

	public ScoAutorizacaoFornJn inserirAutorizacaoFornecimentoJn(
			ScoAutorizacaoFornJn autorizacaoFornecimentoJn) {
		try {
			this.getScoAutorizacaoFornJnDAO().persistir(
					autorizacaoFornecimentoJn);
		} catch (Exception e) {
			new ApplicationBusinessException(
					ScoAutorizacaoFornJnRNExceptionCode.ERRO_INSERIR_AUT_FORNECIMENTO_JN);
		}
		return autorizacaoFornecimentoJn;
	}
	
	/**
	 * Assina a liberação da AF
	 * @param listaAutJn
	 * @param servidorChefia
	 */
	public void autorizarLiberacaoAutorizacaoFornecimentoJn(List<ScoAutorizacaoFornJn> listaAutJn, RapServidores servidorChefia) {
		for (ScoAutorizacaoFornJn item : listaAutJn) {
			//ScoAutorizacaoFornJn itemUpd = this.getScoAutorizacaoFornJnDAO().obterPorChavePrimaria(item.getSeqJn());
			item.setIndAprovada(DominioAprovadaAutorizacaoForn.C);
			item.setServidorAutorizado(servidorChefia);
			item.setDtAssinaturaChefia(new Date());
			this.getScoAutorizacaoFornJnDAO().atualizar(item);
		}
	}
	
	// Dependências

	protected ScoAutorizacaoFornJnDAO getScoAutorizacaoFornJnDAO() {
		return scoAutorizacaoFornJnDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected IPacFacade getPacFacade() {
		return this.pacFacade;
	}

	protected ICadastrosBasicosOrcamentoFacade getCadastrosBasicosOrcamentoFacade() {
		return this.cadastrosBasicosOrcamentoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
