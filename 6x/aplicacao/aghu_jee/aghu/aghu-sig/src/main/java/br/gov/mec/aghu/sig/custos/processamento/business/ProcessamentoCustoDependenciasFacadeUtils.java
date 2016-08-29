package br.gov.mec.aghu.sig.custos.processamento.business;

import javax.ejb.EJB;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.patrimonio.IPatrimonioService;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;


public class ProcessamentoCustoDependenciasFacadeUtils {

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	@EJB
	private IAdministracaoFacade administracaoFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IPatrimonioService patrimonioService;

	@EJB
	private IEstoqueFacade iEstoqueFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private INutricaoFacade nutricaoFacade;
	
	@EJB
	private IParametroSistemaFacade parametroSistemaFacade;

	
	public IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public ISiconFacade getSiconFacade() {
		return siconFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	public ICadastrosBasicosFacade getCadastrosBasicosFacade() {
		return cadastrosBasicosFacade;
	}
	
	public IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return this.procedimentoTerapeuticoFacade;
	}

	public ICustosSigProcessamentoFacade getCustosSigProcessamentoFacade() {
		return this.custosSigProcessamentoFacade;
	}

	public ICustosSigFacade getCustosSigFacade() {
		return this.custosSigFacade;
	}

	public ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	public IAdministracaoFacade getAdministracaoFacade() {
		return administracaoFacade;
	}

	public IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	public IPatrimonioService getPatrimonioService() {
		return patrimonioService;
	}

	public IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	public IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}

	public ICustosSigCadastrosBasicosFacade getCustosSigCadastrosBasicosFacade() {
		return this.custosSigCadastrosBasicosFacade;
	}

	public IEstoqueFacade getEstoqueFacade() {
		return iEstoqueFacade;
	}

	public INutricaoFacade getNutricaoFacade() {
		return nutricaoFacade;
	}
	
	public IParametroSistemaFacade getParametroSistemaFacade() {
		return parametroSistemaFacade;
	}
}
