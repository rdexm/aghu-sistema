package br.gov.mec.aghu.sig.custos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoCctsDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoClientesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoComposicoesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoDirRateiosDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoPhisDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoVersoesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ObjetoCustoComposicoesON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ObjetoCustoComposicoesON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SigObjetoCustoDirRateiosDAO sigObjetoCustoDirRateiosDAO;
	
	@Inject
	private SigObjetoCustoCctsDAO sigObjetoCustoCctsDAO;
	
	@Inject
	private SigObjetoCustoComposicoesDAO sigObjetoCustoComposicoesDAO;
	
	@Inject
	private SigObjetoCustoClientesDAO sigObjetoCustoClientesDAO;
	
	@Inject
	private SigObjetoCustoVersoesDAO sigObjetoCustoVersoesDAO;
	
	@Inject
	private SigObjetoCustoPhisDAO sigObjetoCustoPhisDAO;

	private static final long serialVersionUID = 6141496298935172427L;

	public enum ObjetoCustoComposicoesONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_VALIDACAO_COPIA_DUPLICADA
	}

	public void copiaObjetoCustoComposicoes(SigObjetoCustoVersoes sigObjetoCustoVersoes, List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		getSigObjetoCustoVersoesDAO().refresh(sigObjetoCustoVersoes);

		// validacao
		StringBuilder string = new StringBuilder("");
		for (SigObjetoCustoComposicoes sigObjetoCustoComposicoesCopia : listaObjetoCustoComposicoes) {
			for (SigObjetoCustoComposicoes sigObjetoCustoComposicoes : sigObjetoCustoVersoes.getListObjetoCustoComposicoes()) {
				if ((sigObjetoCustoComposicoesCopia.getSigAtividades() != null && sigObjetoCustoComposicoes.getSigAtividades() != null
						&& sigObjetoCustoComposicoesCopia.getSigAtividades().getSeq() == sigObjetoCustoComposicoes.getSigAtividades().getSeq() && sigObjetoCustoComposicoesCopia
						.getSigDirecionadores().getSeq() == sigObjetoCustoComposicoes.getSigDirecionadores().getSeq())
						|| (sigObjetoCustoComposicoesCopia.getSigObjetoCustoVersoesCompoe() != null
								&& sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe() != null && sigObjetoCustoComposicoesCopia
								.getSigObjetoCustoVersoesCompoe().getSeq() == sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getSeq())
						&& sigObjetoCustoComposicoesCopia.getSigDirecionadores().getSeq() == sigObjetoCustoComposicoes.getSigDirecionadores().getSeq()) {
					if (!string.toString().trim().equals("")) {
						string.append(", ");
					}
					if (sigObjetoCustoComposicoesCopia.getSigAtividades() != null) {
						string.append(sigObjetoCustoComposicoesCopia.getSigAtividades().getNome());
					} else {
						string.append(sigObjetoCustoComposicoesCopia.getSigObjetoCustoVersoesCompoe().getSigObjetoCustos().getNome());
					}
				}
			}
		}

		if (!string.toString().trim().equals("")) {
			throw new ApplicationBusinessException(ObjetoCustoComposicoesONExceptionCode.MENSAGEM_VALIDACAO_COPIA_DUPLICADA, string);
		}


		for (SigObjetoCustoComposicoes aux : listaObjetoCustoComposicoes) {
			SigObjetoCustoComposicoes copia = new SigObjetoCustoComposicoes();

			copia.setSigObjetoCustoVersoes(sigObjetoCustoVersoes);

			copia.setSigAtividades(aux.getSigAtividades());
			copia.setSigObjetoCustoVersoesCompoe(aux.getSigObjetoCustoVersoesCompoe());
			copia.setSigDirecionadores(aux.getSigDirecionadores());
			copia.setIdentificadorPop(aux.getIdentificadorPop());
			copia.setNroExecucoes(aux.getNroExecucoes());

			copia.setRapServidores(servidorLogado);

			copia.setIndSituacao(DominioSituacao.A);
			copia.setCriadoEm(new Date());
			copia.setVersion(0);

			getSigObjetoCustoComposicoesDAO().persistir(copia);
		}

		getSigObjetoCustoComposicoesDAO().flush();

	}

	public SigObjetoCustoVersoes criaNovaVersao(SigObjetoCustoVersoes sigObjetoCustoVersoes) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		sigObjetoCustoVersoes = this.getSigObjetoCustoVersoesDAO().merge(sigObjetoCustoVersoes);
		//SigObjetoCustoVersoes
		SigObjetoCustoVersoes novo = new SigObjetoCustoVersoes();
		novo.setSigObjetoCustos(sigObjetoCustoVersoes.getSigObjetoCustos());
		novo.setCriadoEm(new Date());
		novo.setDataInicio(new Date());
		novo.setVersion(0);
		novo.setRapServidores(servidorLogado);
		novo.setIndSituacao(DominioSituacaoVersoesCustos.E);
		novo.setNroVersao(getSigObjetoCustoVersoesDAO().getNroDaVersaoDoNovoObjetoDeCusto(sigObjetoCustoVersoes));
		
		this.getSigObjetoCustoVersoesDAO().persistir(novo);
		this.getSigObjetoCustoVersoesDAO().flush();
		
		//SigObjetoCustoCcts
		for(SigObjetoCustoCcts aux: sigObjetoCustoVersoes.getListObjetoCustoCcts()){
			if(aux.getIndSituacao().equals(DominioSituacao.A)){
				SigObjetoCustoCcts copia = new SigObjetoCustoCcts();
				copia.setSigObjetoCustoVersoes(novo);
				copia.setIndSituacao(DominioSituacao.A);
				copia.setCriadoEm(new Date());
				copia.setRapServidores(servidorLogado);
				copia.setFccCentroCustos(aux.getFccCentroCustos());
				copia.setIndTipo(aux.getIndTipo());
				copia.setVersion(0);
				
				this.getSigObjetoCustoCctsDAO().persistir(copia);
				this.getSigObjetoCustoCctsDAO().flush();
			}
		}
		
		//SigObjetoCustoPhis
		for (SigObjetoCustoPhis aux : sigObjetoCustoVersoes.getListObjetoCustoPhis()) {
			if(aux.getDominioSituacao().equals(DominioSituacao.A)){
				SigObjetoCustoPhis copia = new SigObjetoCustoPhis();
				copia.setSigObjetoCustoVersoes(novo);
				copia.setDominioSituacao(DominioSituacao.A);
				copia.setCriadoEm(new Date());
				copia.setRapServidores(servidorLogado);
				copia.setFatProcedHospInternos(aux.getFatProcedHospInternos());
				copia.setVersion(0);
	
				this.getSigObjetoCustoPhisDAO().persistir(copia);
				this.getSigObjetoCustoPhisDAO().flush();
			}
		}

		//SigObjetoCustoComposicoes
		for (SigObjetoCustoComposicoes aux : sigObjetoCustoVersoes.getListObjetoCustoComposicoes()) {
			if(aux.getIndSituacao().equals(DominioSituacao.A)){
				SigObjetoCustoComposicoes copia = new SigObjetoCustoComposicoes();
				copia.setSigObjetoCustoVersoes(novo);
				copia.setSigAtividades(aux.getSigAtividades());
				copia.setSigObjetoCustoVersoesCompoe(aux.getSigObjetoCustoVersoesCompoe());
				copia.setSigDirecionadores(aux.getSigDirecionadores());
				copia.setIdentificadorPop(aux.getIdentificadorPop());
				copia.setNroExecucoes(aux.getNroExecucoes());
				copia.setRapServidores(servidorLogado);
				copia.setIndSituacao(DominioSituacao.A);
				copia.setCriadoEm(new Date());
				copia.setVersion(0);
	
				this.getSigObjetoCustoComposicoesDAO().persistir(copia);
				this.getSigObjetoCustoComposicoesDAO().flush();
			}
		}
		
		//SigObjetoCustoDirRateios
		for(SigObjetoCustoDirRateios aux : sigObjetoCustoVersoes.getListObjetoCustoDirRateios()){
			
			if(aux.getSituacao().equals(DominioSituacao.A)){
				SigObjetoCustoDirRateios copia = new SigObjetoCustoDirRateios();
				copia.setObjetoCustoVersoes(novo);
				copia.setServidor(servidorLogado);
				copia.setSituacao(DominioSituacao.A);
				copia.setCriadoEm(new Date());
				copia.setDirecionadores(aux.getDirecionadores());
				copia.setPercentual(aux.getPercentual());
				copia.setVersion(0);
				
				this.getSigObjetoCustoDirRateiosDAO().persistir(copia);
				this.getSigObjetoCustoDirRateiosDAO().flush();
			}
		}
			
		//SigObjetoCustoClientes
		for(SigObjetoCustoClientes aux : sigObjetoCustoVersoes.getListObjetoCustoClientes()){
			if(aux.getSituacao().equals(DominioSituacao.A)){
				SigObjetoCustoClientes copia = new SigObjetoCustoClientes();
				copia.setObjetoCustoVersoes(novo);
				copia.setServidor(servidorLogado);
				copia.setSituacao(DominioSituacao.A);
				copia.setCriadoEm(new Date());
				copia.setCentroProducao(aux.getCentroProducao());
				copia.setCentroCusto(aux.getCentroCusto());
				copia.setDirecionadores(aux.getDirecionadores());
				copia.setIndTodosCct(aux.getIndTodosCct());
				copia.setValor(aux.getValor());			
				copia.setVersion(0);
				
				this.getSigObjetoCustoClientesDAO().persistir(copia);
				this.getSigObjetoCustoClientesDAO().flush();
			}
		}
		

		return novo;
	}
	
	public void persistComposicoesObjetoCusto(SigObjetoCustoComposicoes sigObjetoCustoComposicoes) {
		if (sigObjetoCustoComposicoes.getSeq() != null) {
			this.getSigObjetoCustoComposicoesDAO().atualizar(sigObjetoCustoComposicoes);
		} else {
			this.getSigObjetoCustoComposicoesDAO().persistir(sigObjetoCustoComposicoes);
		}
	}
	
	protected SigObjetoCustoPhisDAO getSigObjetoCustoPhisDAO(){
		return sigObjetoCustoPhisDAO;
	}

	protected SigObjetoCustoComposicoesDAO getSigObjetoCustoComposicoesDAO() {
		return sigObjetoCustoComposicoesDAO;
	}

	protected SigObjetoCustoVersoesDAO getSigObjetoCustoVersoesDAO(){
		return sigObjetoCustoVersoesDAO;
	}
	
	protected SigObjetoCustoCctsDAO getSigObjetoCustoCctsDAO(){
		return sigObjetoCustoCctsDAO;
	}
	
	protected SigObjetoCustoDirRateiosDAO getSigObjetoCustoDirRateiosDAO(){
		return sigObjetoCustoDirRateiosDAO;
	}
	
	protected SigObjetoCustoClientesDAO getSigObjetoCustoClientesDAO(){
		return sigObjetoCustoClientesDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
