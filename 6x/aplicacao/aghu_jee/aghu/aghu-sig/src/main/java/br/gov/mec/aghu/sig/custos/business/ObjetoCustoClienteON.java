package br.gov.mec.aghu.sig.custos.business;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoClientesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ObjetoCustoClienteON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ObjetoCustoClienteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@Inject
	private SigObjetoCustoClientesDAO sigObjetoCustoClientesDAO;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;

	private static final long serialVersionUID = 8190164496444499895L;

	public enum ObjetoCustoClienteONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CAMPO_VALOR_ZERO
	}

	public List<SigObjetoCustoClientes> buscaObjetoClienteVersaoAtivo(SigObjetoCustoVersoes sigObjetoCustoVersoes, SigDirecionadores sigDirecionadores,
			Boolean semValor) {
		List<SigObjetoCustoClientes> buscaObjetoClienteVersaoAtivo = this.getSigObjetoCustoClientesDAO().buscaObjetoClienteVersaoAtivo(sigObjetoCustoVersoes,
				sigDirecionadores, semValor);

		Collections.sort(buscaObjetoClienteVersaoAtivo, new Comparator<SigObjetoCustoClientes>() {
			@Override
			public int compare(SigObjetoCustoClientes o1, SigObjetoCustoClientes o2) {
				return ((o1.getCentroCusto() != null) ? o1.getCentroCusto().getDescricao()
						.compareTo((o2.getCentroCusto() != null) ? o2.getCentroCusto().getDescricao() : o2.getCentroProducao().getNome()) : o1
						.getCentroProducao().getNome()
						.compareTo((o2.getCentroCusto() != null) ? o2.getCentroCusto().getDescricao() : o2.getCentroProducao().getNome()));
			}
		});

		return buscaObjetoClienteVersaoAtivo;
	}

	public void atualizarValorCliente(List<SigObjetoCustoClientes> listaClientes) throws ApplicationBusinessException {
		for (SigObjetoCustoClientes sigObjetoCustoClientes : listaClientes) {
			if (sigObjetoCustoClientes.getValor() != null && sigObjetoCustoClientes.getValor().equals(BigDecimal.valueOf(0L))) {
				throw new ApplicationBusinessException(ObjetoCustoClienteONExceptionCode.MENSAGEM_CAMPO_VALOR_ZERO);
			}
		}

		for (SigObjetoCustoClientes sigObjetoCustoClientes : listaClientes) {
			this.getSigObjetoCustoClientesDAO().merge(sigObjetoCustoClientes);
		}
	}

	public void associarCentrosCustoClientes(SigObjetoCustoClientes sigObjetoCustoClientes, RapServidores servidor){
		//inativa linha "ind todos cc"
		sigObjetoCustoClientes.setSituacao(DominioSituacao.I);
		this.getSigObjetoCustoClientesDAO().atualizar(sigObjetoCustoClientes);

		List<FccCentroCustos> listaResultado = this.getCentroCustoFacade().pesquisarCentroCustosPorCentroProdExcluindoGcc(null, null, DominioSituacao.A);

		for (FccCentroCustos centroCusto : listaResultado) {
			SigObjetoCustoClientes objCustoCliente = new SigObjetoCustoClientes();
			objCustoCliente.setCentroCusto(centroCusto);
			objCustoCliente.setDirecionadores(sigObjetoCustoClientes.getDirecionadores());
			objCustoCliente.setObjetoCustoVersoes(sigObjetoCustoClientes.getObjetoCustoVersoes());
			objCustoCliente.setSituacao(DominioSituacao.A);
			objCustoCliente.setCriadoEm(new Date());
			objCustoCliente.setServidor(servidor);

			this.getSigObjetoCustoClientesDAO().persistir(objCustoCliente);
		}
	}

	protected SigObjetoCustoClientesDAO getSigObjetoCustoClientesDAO() {
		return sigObjetoCustoClientesDAO;
	}

	protected ICustosSigFacade getCustosSigFacade() {
		return this.custosSigFacade;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}
}
