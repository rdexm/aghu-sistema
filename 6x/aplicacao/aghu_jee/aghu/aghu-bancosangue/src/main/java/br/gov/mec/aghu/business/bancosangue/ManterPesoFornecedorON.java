package br.gov.mec.aghu.business.bancosangue;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsComponenteMovimentadoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsComponentePesoFornecedorDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsComponentePesoFornecedorJnDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsEstoqueComponentesDAO;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedor;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedorId;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedorJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterPesoFornecedorON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterPesoFornecedorON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AbsEstoqueComponentesDAO absEstoqueComponentesDAO;
	
	@Inject
	private AbsComponentePesoFornecedorJnDAO absComponentePesoFornecedorJnDAO;
	
	@Inject
	private AbsComponenteMovimentadoDAO absComponenteMovimentadoDAO;
	
	@Inject
	private AbsComponentePesoFornecedorDAO absComponentePesoFornecedorDAO;

	private static final long serialVersionUID = 1061239213249749561L;

	public enum ManterPesoFornecedorONExceptionCode implements	BusinessExceptionCode {
		DEPENDENCIA_COMPONENTES_MOVIMENTADOS,
		DEPENDENCIA_ESTOQUE_COMPONENTES,
		JA_EXISTE_SUGESTAO,
		PESO_MAXIMO_DESCONTO_FORNECEDOR
	}
	
	protected AbsComponentePesoFornecedorJnDAO getAbsComponentePesoFornecedorJnDAO() {
		return absComponentePesoFornecedorJnDAO; 
	}
	
	protected AbsComponentePesoFornecedorDAO getAbsComponentePesoFornecedorDAO() {
		return absComponentePesoFornecedorDAO; 
	}
	
	protected AbsComponenteMovimentadoDAO getAbsComponenteMovimentadoDAO() {
		return absComponenteMovimentadoDAO; 
	}
	
	protected AbsEstoqueComponentesDAO getAbsEstoqueComponentesDAO() {
		return absEstoqueComponentesDAO; 
	}
	
	public Boolean existeSugestaoParaComponenteSanguineo(String csaCodigo) {
		return getAbsComponentePesoFornecedorDAO().existeSugestaoParaComponenteSanguineo(csaCodigo);
	}
	
	public void atualizarAbsComponentePesoFornecedor(AbsComponentePesoFornecedor absComponentePesoFornecedor) throws ApplicationBusinessException {
		if(absComponentePesoFornecedor.getPeso() > 9999){
			throw new ApplicationBusinessException(ManterPesoFornecedorONExceptionCode.PESO_MAXIMO_DESCONTO_FORNECEDOR);
		}else{
			if (absComponentePesoFornecedor.getIndSugestao() && existeSugestaoParaComponenteSanguineo(absComponentePesoFornecedor.getId().getCsaCodigo())) {
				throw new ApplicationBusinessException(ManterPesoFornecedorONExceptionCode.JA_EXISTE_SUGESTAO);
			} else {
				getAbsComponentePesoFornecedorDAO().merge(absComponentePesoFornecedor);
				inserirAbsComponentePesoFornecedorJournal(absComponentePesoFornecedor,DominioOperacoesJournal.UPD);
			}
		}
	}

	protected void inserirAbsComponentePesoFornecedorJournal(AbsComponentePesoFornecedor absComponentePesoFornecedor, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AbsComponentePesoFornecedor original = getAbsComponentePesoFornecedorDAO().obterOriginal(absComponentePesoFornecedor);
		AbsComponentePesoFornecedorJn journal = BaseJournalFactory.getBaseJournal(operacao,AbsComponentePesoFornecedorJn.class, servidorLogado.getUsuario());
		journal.setCsaCodigo(original.getId().getCsaCodigo());
		journal.setFboSeq(original.getId().getFboSeq()); 
		journal.setSeqp(original.getId().getSeqp());
		journal.setPeso(original.getPeso()); 
		journal.setIndSugestao(original.getIndSugestao()); 
		journal.setCriadoEm(original.getCriadoEm());
		journal.setServidor(original.getServidor());
		getAbsComponentePesoFornecedorJnDAO().persistir(journal);
	}

	public void inserirAbsComponentePesoFornecedor(AbsComponentePesoFornecedor absComponentePesoFornecedor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(absComponentePesoFornecedor.getPeso() > 9999){
			throw new ApplicationBusinessException(ManterPesoFornecedorONExceptionCode.PESO_MAXIMO_DESCONTO_FORNECEDOR);
		}else{
			if (absComponentePesoFornecedor.getIndSugestao() && existeSugestaoParaComponenteSanguineo(absComponentePesoFornecedor.getId().getCsaCodigo())) {
				throw new ApplicationBusinessException(ManterPesoFornecedorONExceptionCode.JA_EXISTE_SUGESTAO);
				
			} else {
				Short seqp = absComponentePesoFornecedorDAO.pesquisarMaxSeqp(absComponentePesoFornecedor.getId().getCsaCodigo(), absComponentePesoFornecedor.getId().getFboSeq());
				
				if(seqp == null) {
					seqp = 0;
				}

				seqp = (short) (seqp + Short.valueOf("1"));
				absComponentePesoFornecedor.getId().setSeqp(seqp);
				absComponentePesoFornecedor.setCriadoEm(new Date());
				absComponentePesoFornecedor.setServidor(servidorLogado);
				absComponentePesoFornecedorDAO.persistir(absComponentePesoFornecedor); 
			}
		}
	}
	
	public void excluirAbsComponentePesoFornecedor(AbsComponentePesoFornecedorId id) throws ApplicationBusinessException {
		final AbsComponentePesoFornecedor absComponentePesoFornecedor = getAbsComponentePesoFornecedorDAO().obterPorChavePrimaria(id);
		
		if (absComponentePesoFornecedor == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		if (getAbsComponenteMovimentadoDAO().existeComponentePesoFornecedor(absComponentePesoFornecedor)) {
			throw new ApplicationBusinessException(ManterPesoFornecedorONExceptionCode.DEPENDENCIA_COMPONENTES_MOVIMENTADOS);
			
		} else if (getAbsEstoqueComponentesDAO().existeComponentePesoFornecedor(absComponentePesoFornecedor)) {
			throw new ApplicationBusinessException(ManterPesoFornecedorONExceptionCode.DEPENDENCIA_ESTOQUE_COMPONENTES);
			
		} else {
			getAbsComponentePesoFornecedorDAO().remover(absComponentePesoFornecedor); 
			inserirAbsComponentePesoFornecedorJournal(absComponentePesoFornecedor,DominioOperacoesJournal.DEL);
		}
	}

	public void alterarIndSugestao(AbsComponentePesoFornecedor absComponentePesoFornecedor) throws ApplicationBusinessException{
		if(absComponentePesoFornecedor.getIndSugestao() != true){
			String csaCodigo = absComponentePesoFornecedor.getComponenteSanguineo().getCodigo();
			AbsComponentePesoFornecedor componenteSituacaoNova = getAbsComponentePesoFornecedorDAO().obterSugestaoParaComponenteSanguineo(csaCodigo);
			AbsComponentePesoFornecedor componenteSituacaoAntiga = getAbsComponentePesoFornecedorDAO().obterSugestaoParaComponenteSanguineo(csaCodigo);
			if (componenteSituacaoAntiga != null) {
				componenteSituacaoNova.setIndSugestao(Boolean.TRUE);
				componenteSituacaoAntiga.setIndSugestao(Boolean.FALSE);
				getAbsComponentePesoFornecedorDAO().atualizar(componenteSituacaoAntiga);
				getAbsComponentePesoFornecedorDAO().atualizar(componenteSituacaoNova);
			}
			if(componenteSituacaoNova != null){
				componenteSituacaoNova.setIndSugestao(false);
				atualizarAbsComponentePesoFornecedor(componenteSituacaoNova);
			}
			absComponentePesoFornecedor.setIndSugestao(true); 
			atualizarAbsComponentePesoFornecedor(absComponentePesoFornecedor);
		}
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
