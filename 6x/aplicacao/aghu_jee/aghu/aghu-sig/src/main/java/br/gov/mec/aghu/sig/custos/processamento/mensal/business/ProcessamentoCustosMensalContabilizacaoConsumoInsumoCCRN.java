package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.hibernate.ScrollableResults;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.ConsumoInsumoVO;
import br.gov.mec.aghu.sig.dao.SigMvtoContaMensalDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável pela contabilização do consumo de insumos do mês por
 * centro de custo. #16834 - #19741
 * 
 * @author rmalvezzi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContabilizacaoConsumoInsumoCCRN.class)
public class ProcessamentoCustosMensalContabilizacaoConsumoInsumoCCRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = -5811664870339719781L;

	@Override
	public String getTitulo() {
		return "Contabilizacao do consumo de insumos do mes por centro de custo";
	}

	@Override
	public String getNome() {
		return "processamentoMensalContabilizacaoConsumoInsumoCCON - processamentoInsumosEtapa1";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 1;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		this.processamentoInsumosEtapa1(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
	}

	/**
	 * Método principal da Etapa 1 do processamento, é a responsável em disparar
	 * todos os passos dessa etapa.
	 * 
	 * @author rmalvezzi
	 * @param sigProcessamentoCusto
	 *            Processamento Atual.
	 * @param rapServidores
	 *            Servidor Logado.
	 * @param sigProcessamentoPassos
	 *            Passo Atual.
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento.
	 */
	private void processamentoInsumosEtapa1(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		IParametroFacade parametroFacade = this.getProcessamentoCustoUtils().getParametroFacade();
		SigMvtoContaMensalDAO sigMvtoContaMensalDAO = this.getProcessamentoCustoUtils().getSigMvtoContaMensalDAO();
		ICentroCustoFacade centroCustoFacade = this.getProcessamentoCustoUtils().getCentroCustoFacade();
		IComprasFacade comprasFacade = this.getProcessamentoCustoUtils().getComprasFacade();

		// Passo 1
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_CONTABILIZACAO_CONSUMO_INSUMOS");

		BigDecimal parametroMvtoRequisicaoMaterial = null;
		BigDecimal parametroMvtoDevolucaoAlmoxarifado = null;
		BigDecimal parametroCentroCustoAplic = null;

		// Passo 2

		if (parametroFacade.verificarExisteAghParametroValor(AghuParametrosEnum.P_TMV_DOC_RM)) {
			parametroMvtoRequisicaoMaterial = parametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_TMV_DOC_RM.toString());
		}

		if (parametroFacade.verificarExisteAghParametroValor(AghuParametrosEnum.P_TMV_DOC_DA)) {
			parametroMvtoDevolucaoAlmoxarifado = parametroFacade
					.obterValorNumericoAghParametros(AghuParametrosEnum.P_TMV_DOC_DA.toString());
		}

		if (parametroFacade.verificarExisteAghParametroValor(AghuParametrosEnum.CENTRO_CUSTO_APLIC)) {
			parametroCentroCustoAplic = parametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.CENTRO_CUSTO_APLIC.toString());
		}

		if (parametroMvtoRequisicaoMaterial == null || parametroMvtoDevolucaoAlmoxarifado == null || parametroCentroCustoAplic == null) {
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_ERRO_BUSCA_PARAMETROS_CONSUMO_INSUMOS");
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_BUSCA_PARAMETROS_CONSUMO_INSUMOS);
		}

		// Passo 3, 4, e 5
		this.persisteConsumoInsumoMvtoContaMensais(sigMvtoContaMensalDAO, centroCustoFacade, comprasFacade, DominioTipoMovimentoConta.SIP,
				parametroMvtoRequisicaoMaterial, parametroMvtoDevolucaoAlmoxarifado, parametroCentroCustoAplic, rapServidores,
				sigProcessamentoCusto, sigProcessamentoPassos);

		this.persisteConsumoInsumoMvtoContaMensais(sigMvtoContaMensalDAO, centroCustoFacade, comprasFacade, DominioTipoMovimentoConta.SIT,
				parametroMvtoRequisicaoMaterial, parametroMvtoDevolucaoAlmoxarifado, parametroCentroCustoAplic, rapServidores,
				sigProcessamentoCusto, sigProcessamentoPassos);

		// Passo 6
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_GRAVACAO_DADOS_CONSUMO_INSUMOS");
	}

	/**
	 * Método que vai executar os passos 3 (busca), 4 (log) e 5 (insert).
	 * 
	 * @author rmalvezzi
	 * @param tipoMovimentoConta
	 *            SIT -> Fornecedor não HCPA, SIP -> Fornecedor somente HCPA.
	 * @param parametroMvtoRequisicaoMaterial
	 *            Tipo de movimento de estoque que representa o documento de
	 *            requisição de material efetivada (RM).
	 * @param parametroMvtoDevolucaoAlmoxarifado
	 *            Tipo de movimento de estoque que representa o documento de
	 *            devolução ao almoxarifado (DA).
	 * @param rapServidores
	 *            Servidor Logado.
	 * @param processamentoCusto
	 *            Processamento Atual.
	 * @param sigProcessamentoPassos
	 *            Passo Atual.
	 * @throws ApplicationBusinessException
	 *             Exceção lançada se alguma coisa aconteceu na hora do commit
	 *             do processamento.
	 */
	private void persisteConsumoInsumoMvtoContaMensais(SigMvtoContaMensalDAO sigMvtoContaMensalDAO, ICentroCustoFacade centroCustoFacade,
			IComprasFacade comprasFacade, DominioTipoMovimentoConta tipoMovimentoConta, BigDecimal parametroMvtoRequisicaoMaterial,
			BigDecimal parametroMvtoDevolucaoAlmoxarifado, BigDecimal parametroCentroCustoAplic, RapServidores rapServidores,
			SigProcessamentoCusto processamentoCusto, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {

		// Passo 3
		ScrollableResults retorno = this
				.getProcessamentoCustoUtils()
				.getSigProcessamentoCustoDAO()
				.buscarConsumoInsumoMvtoContaMensais(tipoMovimentoConta, parametroMvtoRequisicaoMaterial,
						parametroMvtoDevolucaoAlmoxarifado, processamentoCusto);

		// Passo 4
		this.buscarMensagemEGravarLogProcessamentoSemCommit(processamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_SUCESSO_BUSCA_DADOS_CONSUMO_INSUMOS", tipoMovimentoConta);

		if (retorno != null) {
			while (retorno.next()) {
				ConsumoInsumoVO vo = new ConsumoInsumoVO((Object[]) retorno.get());

				// Passo 4
				SigMvtoContaMensal mvtoContaMensal = new SigMvtoContaMensal();
				mvtoContaMensal.setSigProcessamentoCustos(processamentoCusto);
				mvtoContaMensal.setCriadoEm(new Date());
				mvtoContaMensal.setRapServidores(rapServidores);
				mvtoContaMensal.setTipoMvto(tipoMovimentoConta);
				mvtoContaMensal.setTipoValor(DominioTipoValorConta.DI);

				mvtoContaMensal.setFccCentroCustos(centroCustoFacade.obterCentroCustoPorChavePrimaria(vo.getCentroCustoCodigo()));

				// Verifica se é um centro de custo válido
				if (vo.getCentroCustoRequisitaCodigo() != null && vo.getCentroCustoRequisitaCodigo().intValue() > 0) {
					if (vo.getCentroCustoRequisitaCodigo().intValue() != vo.getCentroCustoCodigo().intValue()) {
						if (vo.getCentroCustoRequisitaCodigo().intValue() != parametroCentroCustoAplic.intValue()) {
							mvtoContaMensal.setFccCentroCustosDebita(centroCustoFacade.obterCentroCustoPorChavePrimaria(vo
									.getCentroCustoRequisitaCodigo()));
						}
					}
				}

				mvtoContaMensal.setScoMaterial(comprasFacade.obterScoMaterialPorChavePrimaria(vo.getMaterialCodigo()));
				mvtoContaMensal.setScoUnidadeMedida(comprasFacade.obterScoUnidadeMedidaPorChavePrimaria(vo.getUnidadeMediaMaterial()));
				mvtoContaMensal.setQtde(vo.getQtdConsumo());
				mvtoContaMensal.setCustoMedio(vo.getCustoMedioMaterial());
				mvtoContaMensal.setValor(vo.getVlrConsumo());

				sigMvtoContaMensalDAO.persistir(mvtoContaMensal);
			}
			retorno.close();
			this.commitProcessamentoCusto();
		}
	}
}
