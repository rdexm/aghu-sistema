package br.gov.mec.aghu.sig.custos.business;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCustoCcts;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoHistoricos;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.custos.business.ManterObjetosCustoON.ManterObjetosCustoONExceptionCode;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoPesoClienteVO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoCctsDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoClientesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoComposicoesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoDirRateiosDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoHistoricosDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoPhisDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoVersoesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustosDAO;

@Stateless
public class ObjetosCustoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ObjetosCustoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SigObjetoCustoHistoricosDAO sigObjetoCustoHistoricosDAO;

@Inject
private SigObjetoCustoDirRateiosDAO sigObjetoCustoDirRateiosDAO;

@Inject
private SigObjetoCustoCctsDAO sigObjetoCustoCctsDAO;

@Inject
private SigObjetoCustosDAO sigObjetoCustosDAO;

@Inject
private SigObjetoCustoComposicoesDAO sigObjetoCustoComposicoesDAO;

@Inject
private SigObjetoCustoClientesDAO sigObjetoCustoClientesDAO;

@Inject
private SigObjetoCustoVersoesDAO sigObjetoCustoVersoesDAO;

@Inject
private SigObjetoCustoPhisDAO sigObjetoCustoPhisDAO;

	private static final long serialVersionUID = -3372484498232566933L;

	public void excluirVersaoObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {
		if (objetoCustoVersao == null) {
			throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_VERSAO_OBJETO_CUSTO_INVALIDA);
		}

		this.verificarAssociacaoVersoesObjetoCusto(objetoCustoVersao);

		this.verificarObjetoCustoAtivoMaisUmMes(objetoCustoVersao);

		this.excluirVersaoObjetoCustoEAssociacoes(objetoCustoVersao);

		this.excluirObjetoCusto(objetoCustoVersao);
	}

	public boolean validarExclusaoObjetoCustoAtivoMaisUmMes(SigObjetoCustoVersoes objetoCustoVersao) {
		try {
			this.verificarObjetoCustoAtivoMaisUmMes(objetoCustoVersao);
		} catch (ApplicationBusinessException ex) {
			return true;
		}
		return false;
	}

	private void verificarAssociacaoVersoesObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {
		List<SigObjetoCustoComposicoes> list = getSigObjetoCustoComposicoesDAO().pesquisarAssociacaoVersoesObjetoCusto(objetoCustoVersao);

		if (list != null && !list.isEmpty()) {
			throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_EXCLUSAO_NAO_PERMITIDA_OBJETO_CUSTO_ASSOCIADO);
		}
	}

	private void verificarObjetoCustoAtivoMaisUmMes(SigObjetoCustoVersoes objetoCustoVersao) throws ApplicationBusinessException {

		if (objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)) {

			Calendar dtIniVig = Calendar.getInstance();
			dtIniVig.setTime(objetoCustoVersao.getDataInicio());
			dtIniVig.set(Calendar.DAY_OF_MONTH, 1);
			dtIniVig.set(Calendar.HOUR_OF_DAY, 0);
			dtIniVig.set(Calendar.MINUTE, 0);
			dtIniVig.set(Calendar.SECOND, 0);
			dtIniVig.set(Calendar.MILLISECOND, 0);

			Calendar dataAtual = Calendar.getInstance();
			dataAtual.setTime(new Date());
			dataAtual.set(Calendar.DAY_OF_MONTH, 1);
			dataAtual.set(Calendar.HOUR_OF_DAY, 0);
			dataAtual.set(Calendar.MINUTE, 0);
			dataAtual.set(Calendar.SECOND, 0);
			dataAtual.set(Calendar.MILLISECOND, 0);

			if (dtIniVig.getTime().before(dataAtual.getTime())) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MENSAGEM_EXCLUSAO_NAO_PERMITIDA_OBJETO_CUSTO_ATIVO);
			}
		}
	}

	private void excluirVersaoObjetoCustoEAssociacoes(SigObjetoCustoVersoes objetoCustoVersao) {
		SigObjetoCustoPhisDAO sigObjetoCustoPhisDAO = getSigObjetoCustoPhisDAO();

		List<SigObjetoCustoPhis> listPhi = sigObjetoCustoPhisDAO.pesquisarPhiPorObjetoCustoVersao(objetoCustoVersao.getSeq());

		for (SigObjetoCustoPhis phi : listPhi) {
			sigObjetoCustoPhisDAO.remover(phi);
		}

		SigObjetoCustoCctsDAO sigObjetoCustoCctsDAO = this.getSigObjetoCustoCctsDAO();

		List<SigObjetoCustoCcts> listCcts = sigObjetoCustoCctsDAO.pesquisarObjetosCustoCentroCusto(objetoCustoVersao);

		for (SigObjetoCustoCcts ccts : listCcts) {
			sigObjetoCustoCctsDAO.remover(ccts);
		}

		SigObjetoCustoHistoricosDAO sigObjetoCustoHistoricosDAO = this.getSigObjetoCustoHistoricosDAO();

		List<SigObjetoCustoHistoricos> listHistorico = sigObjetoCustoHistoricosDAO.pesquisarObjetoCustoHistoricos(objetoCustoVersao);

		for (SigObjetoCustoHistoricos historico : listHistorico) {
			sigObjetoCustoHistoricosDAO.remover(historico);
		}

		SigObjetoCustoComposicoesDAO sigObjetoCustoComposicoesDAO = this.getSigObjetoCustoComposicoesDAO();

		List<SigObjetoCustoComposicoes> listComposicao = sigObjetoCustoComposicoesDAO.pesquisarObjetoCustoComposicao(objetoCustoVersao);

		for (SigObjetoCustoComposicoes composicao : listComposicao) {
			sigObjetoCustoComposicoesDAO.remover(composicao);
		}

		// Excluir sig_objeto_custo_dir_rateios
		SigObjetoCustoDirRateiosDAO sigObjetoCustoDirRateiosDAO = getSigObjetoCustoDirRateiosDAO();

		List<SigObjetoCustoDirRateios> listDirecRateios = sigObjetoCustoDirRateiosDAO.pesquisarObjetoCustoDirRateios(objetoCustoVersao);

		for (SigObjetoCustoDirRateios direcRateio : listDirecRateios) {
			sigObjetoCustoDirRateiosDAO.remover(direcRateio);
		}

		// Excluir sig_objeto_custo_clientes
		SigObjetoCustoClientesDAO sigObjetoCustoClientesDAO = getSigObjetoCustoClientesDAO();

		List<SigObjetoCustoClientes> listObjCustoClientes = sigObjetoCustoClientesDAO.pesquisarObjetoCustoClientes(objetoCustoVersao);

		for (SigObjetoCustoClientes objCustoCliente : listObjCustoClientes) {
			sigObjetoCustoClientesDAO.remover(objCustoCliente);
		}

		getSigObjetoCustoVersoesDAO().removerPorId(objetoCustoVersao.getSeq());

	}

	public void persistirSigObjetoCustoVersoe(SigObjetoCustoVersoes objetoCustoVersao, List<SigObjetoCustoClientes> listaClientes,
			List<SigObjetoCustoDirRateios> listaDirecionadoresRateio) throws ApplicationBusinessException {
		this.validaSigObjetoCustoVersoe(objetoCustoVersao, listaClientes, listaDirecionadoresRateio);
		this.getSigObjetoCustoVersoesDAO().persistir(objetoCustoVersao);
	}

	public void atualizarSigObjetoCustoVersoe(SigObjetoCustoVersoes objetoCustoVersao, List<SigObjetoCustoClientes> listaClientes,
			List<SigObjetoCustoDirRateios> listaDirecionadoresRateio) throws ApplicationBusinessException {
		this.validaSigObjetoCustoVersoe(objetoCustoVersao, listaClientes, listaDirecionadoresRateio);
		this.getSigObjetoCustoVersoesDAO().atualizar(objetoCustoVersao);
	}

	private void validaSigObjetoCustoVersoe(SigObjetoCustoVersoes objetoCustoVersao, List<SigObjetoCustoClientes> listaClientes,
			List<SigObjetoCustoDirRateios> listaDirecionadoresRateio) throws ApplicationBusinessException {
		if (objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)) {
			if (objetoCustoVersao.getSigObjetoCustos().getIndTipo().equals(DominioTipoObjetoCusto.AP)) {
				this.validaObjetoCustoSemClienteDirecionador(listaDirecionadoresRateio, listaClientes);
				this.validaObjetoCustoSemClienteParaDirecionador(listaDirecionadoresRateio, listaClientes);
			}
		}
	}

	private void validaObjetoCustoSemClienteParaDirecionador(List<SigObjetoCustoDirRateios> listaDirecionadoresRateio,
			List<SigObjetoCustoClientes> listaClientes) throws ApplicationBusinessException {
		if (listaClientes == null || listaClientes.isEmpty() || listaDirecionadoresRateio == null || listaDirecionadoresRateio.isEmpty()) {
			throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MESSAGEM_OBJETO_CUSTO_NAO_DIRECIONADOR_CLIENTE);
		} else {
			boolean aux = true;
			for (SigObjetoCustoClientes sigObjetoCustoClientes : listaClientes) {
				if (sigObjetoCustoClientes.getSituacao() == DominioSituacao.A) {
					aux = false;
				}
			}
			for (SigObjetoCustoDirRateios sigDirecionadores : listaDirecionadoresRateio) {
				if (sigDirecionadores.getSituacao() == DominioSituacao.A) {
					aux = false;
				}
			}
			if (aux) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MESSAGEM_OBJETO_CUSTO_NAO_DIRECIONADOR_CLIENTE);
			}
		}
	}

	private void validaObjetoCustoSemClienteDirecionador(List<SigObjetoCustoDirRateios> listaDirecionadoresRateio, List<SigObjetoCustoClientes> listaClientes)
			throws ApplicationBusinessException {
		HashMap<SigDirecionadores, Boolean> map = new HashMap<SigDirecionadores, Boolean>();
		for (SigObjetoCustoDirRateios sigDirecionadores : listaDirecionadoresRateio) {
			if (sigDirecionadores.getSituacao() == DominioSituacao.A) {
				map.put(sigDirecionadores.getDirecionadores(), false);
			}
		}
		for (SigObjetoCustoClientes sigObjetoCustoClientes : listaClientes) {
			if (sigObjetoCustoClientes.getSituacao() == DominioSituacao.A) {
				map.put(sigObjetoCustoClientes.getDirecionadores(), true);
			}
		}
		for (Boolean b : map.values()) {
			if (!b) {
				throw new ApplicationBusinessException(ManterObjetosCustoONExceptionCode.MESSAGEM_OBJETO_CUSTO_NAO_CLIENTES);
			}
		}
	}

	private void excluirObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) {

		List<SigObjetoCustoVersoes> listVersoes = getSigObjetoCustoVersoesDAO().pesquisarObjetoCustoVersoes(objetoCustoVersao.getSigObjetoCustos(), null);

		if (listVersoes.isEmpty()) {
			getSigObjetoCustosDAO().removerPorId(objetoCustoVersao.getSigObjetoCustos().getSeq());
		}
	}

	public void gravarObjetoCustoCentroCusto(SigObjetoCustoCcts objetoCustoCentroCusto) {
		objetoCustoCentroCusto.setCriadoEm(new Date());
		objetoCustoCentroCusto.setIndSituacao(DominioSituacao.A);
		objetoCustoCentroCusto.setIndTipo(DominioTipoObjetoCustoCcts.P);
		this.getSigObjetoCustoCctsDAO().persistir(objetoCustoCentroCusto);
	}

	public boolean validarExclusaoAssociacaoVersoesObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) {
		try {
			this.verificarAssociacaoVersoesObjetoCusto(objetoCustoVersao);
		} catch (ApplicationBusinessException ex) {
			return true;
		}
		return false;
	}
	
	public List<ObjetoCustoPesoClienteVO> pesquisarObjetoCustoPesoCliente(Integer firstResult, Integer maxResult, String orderProperty,
										boolean asc, FccCentroCustos centroCusto,SigDirecionadores direcionador, String nome, DominioSituacaoVersoesCustos situacao) {
		
		List<ObjetoCustoPesoClienteVO> listResult = this.getSigObjetoCustoVersoesDAO().pesquisarObjetoCustoPesoCliente(firstResult, maxResult, orderProperty, asc, centroCusto, direcionador, nome, situacao);
		
		for( ObjetoCustoPesoClienteVO vo : listResult){
			Hibernate.initialize(vo.getDirecionador());
			List<SigObjetoCustoComposicoes> composicoes = this.getSigObjetoCustoComposicoesDAO().pesquisarComposicoesPorObjetoCustoVersaoAtivo(vo.getOcvSeq());
			if(composicoes != null && !composicoes.isEmpty()){
				vo.setPossuiComposicao(DominioSimNao.S);
			}else {
				vo.setPossuiComposicao(DominioSimNao.N);
			}
		}
		return listResult;
	}
	
	public SigObjetoCustoVersoes obterObjetoCustoVersoes(Integer seq) {
		SigObjetoCustoVersoes objetoCustoVersao =  getSigObjetoCustoVersoesDAO().obterPorChavePrimaria(seq, true, SigObjetoCustoVersoes.Fields.OBJETO_CUSTO, SigObjetoCustoVersoes.Fields.OBJETO_CUSTO_CCTS);
		
		if(objetoCustoVersao!= null){
			for(SigObjetoCustoCcts objetoCustoCcts : objetoCustoVersao.getListObjetoCustoCcts()){
				 if(objetoCustoCcts != null && objetoCustoCcts.getFccCentroCustos() != null){
					 objetoCustoCcts.getFccCentroCustos().getDescricao();
				}
			}
		}
		return objetoCustoVersao;
	}

	// DAOs E ONs

	protected SigObjetoCustoVersoesDAO getSigObjetoCustoVersoesDAO() {
		return sigObjetoCustoVersoesDAO;
	}

	protected SigObjetoCustoDirRateiosDAO getSigObjetoCustoDirRateiosDAO() {
		return sigObjetoCustoDirRateiosDAO;
	}

	protected SigObjetoCustoCctsDAO getSigObjetoCustoCctsDAO() {
		return sigObjetoCustoCctsDAO;
	}

	protected SigObjetoCustoClientesDAO getSigObjetoCustoClientesDAO() {
		return sigObjetoCustoClientesDAO;
	}

	protected SigObjetoCustosDAO getSigObjetoCustosDAO() {
		return sigObjetoCustosDAO;
	}

	protected SigObjetoCustoComposicoesDAO getSigObjetoCustoComposicoesDAO() {
		return sigObjetoCustoComposicoesDAO;
	}

	protected SigObjetoCustoHistoricosDAO getSigObjetoCustoHistoricosDAO() {
		return sigObjetoCustoHistoricosDAO;
	}

	protected SigObjetoCustoPhisDAO getSigObjetoCustoPhisDAO() {
		return sigObjetoCustoPhisDAO;
	}

	
}
